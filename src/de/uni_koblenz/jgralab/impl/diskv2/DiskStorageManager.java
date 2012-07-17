package de.uni_koblenz.jgralab.impl.diskv2;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class DiskStorageManager {
	
	//TODO: For testing purposes, delete eventually
	boolean LOG = true;
	int deletedGEs;
	int writtenGEs;
	int restoredGEs;
	int deletedIncidences;
	int writtenIncidences;
	int restoredIncidences;
	
	/**
	 * The GraphDatabase that this DiskStorageManager works for
	 */
	private GraphDatabaseBaseImpl graphdb;
	
	/**
	 * FileAccess objects to all the files used by this manager
	 */
	private FileAccess[] files;
	
	public DiskStorageManager(GraphDatabaseBaseImpl graphdb){
		this.graphdb = graphdb;

		setupFilesAndProfiles();
		
		//TODO: For testing purposes, delete eventually
		deletedGEs = 0;
		writtenGEs = 0;
		restoredGEs = 0;
		deletedIncidences = 0;
		writtenIncidences = 0;
		restoredIncidences = 0;
	}
	
	/**
	 * For every non-abstract Vertex and Edge class, this method creates
	 * a GraphElementProfile and a FileAccess.
	 */
	private void setupFilesAndProfiles(){
		Schema s = graphdb.getSchema();
		
		//get the amount of classes
		int amountOfClasses = s.getNumberOfTypedElementClasses();
		//tell GraphElementProfile to instantiate the Array in which profiles are stored
		GraphElementProfile.setup(amountOfClasses);
		//make room for FileAccess objects, plus three more for incidences and the two dicts
		files = new FileAccess[amountOfClasses + 3];
		
		//get lists of all vertex and edge classes
		List<VertexClass> vClasses = s.getVertexClassesInTopologicalOrder();
		List<EdgeClass> eClasses = s.getEdgeClassesInTopologicalOrder();
		
		//make FileAccess objects for incidences and the two dicts
		files[amountOfClasses] = FileAccess.createFileAccess("incidences");
		files[amountOfClasses + 1] = FileAccess.createFileAccess("vertexDict");
		files[amountOfClasses + 2] = FileAccess.createFileAccess("edgeDict");
		
		//create a profile and a FileAccess for all non-abstract vertex classes
		for (VertexClass vClass: vClasses){
			if (!vClass.isAbstract()){
				int typeId = vClass.getId();
				GraphElementProfile.createProfile(vClass, typeId, graphdb);
				files[typeId] = FileAccess.createFileAccess(typeId + "_vertices");
			}
		}
		
		//create a profile and a FileAccess for all non-abstract edge classes
		for (EdgeClass eClass: eClasses){
			if (!eClass.isAbstract()){
				int typeId = eClass.getId();
				GraphElementProfile.createProfile(eClass, typeId, graphdb);
				files[typeId] = FileAccess.createFileAccess(typeId + "_edges");
			}
		}
	}
	
	//TODO: testing
	public void printStats(){
		System.out.println("Deleted  Elements  : " + deletedGEs);
		System.out.println("Written to Disk    : " + writtenGEs);
		System.out.println("Restored Elements  : " + restoredGEs);
		System.out.println("Deleted  Incidences: " + deletedIncidences);
		System.out.println("Written to Disk    : " + writtenIncidences);
		System.out.println("Restored Incidences: " + restoredIncidences);
	}
	
	/**
	 * Writes a Graph Element to the disk if it has been newly created, or if it has
	 * been changed since the last time it was loaded from the disk.
	 * 
	 * @param geRef - The Reference to the GraphElement that is written out.
	 */
	public void writeGraphElementToDisk(CacheEntry<? extends GraphElementImpl<?,?,?,?>> vRef){
		Tracker tracker = vRef.getTracker();
		if (LOG) deletedGEs++;
		
		if (tracker == null) {
			//element is neither new nor has it been changed since its last reload
			return;
		}
		if (LOG) writtenGEs++;
		ByteBuffer attributes = tracker.getVariables();
		
		//detect the type of the vertex or edge we want to write out
		int typeId = attributes.getInt(0) - 1;
		
		//get access to the file in which elements of this type are stored
		FileAccess file = files[typeId];
		
		//determine the size of the element we want to store, pun intended
		int byteSize = GraphElementProfile.getProfile(typeId).getSize();
		
		file.write(attributes, vRef.getKey() * byteSize);
	}
	
	/**
	 * Reads a vertex from the disk and restores it.
	 *  
	 * @param key
	 *        The local id of the vertex
	 * @return
	 *        A soft reference to the restored vertex
	 */
	public CacheEntry<VertexImpl> readVertexFromDisk(int key){
		//determine which vertex class we have to use
		int typeId = getVertexDict().read(4, key*4).getInt(0);
		
		//read the data from the disk
		ByteBuffer buf = readGraphElementFromDisk(key, typeId);
		
		//create a vertex that is identical to the vertex we deleted earlier
		VertexImpl ver = restoreVertex(buf, key);
		
		//return a CacheEntry for that new vertex so we can put it back in the cache
		return new CacheEntry<VertexImpl>(ver);
	}
	
	/**
	 * Reads an edge from the disk and restores it.
	 *  
	 * @param key
	 *        The local id of the vertex
	 * @return
	 *        A soft reference to the restored vertex
	 */
	public CacheEntry<EdgeImpl> readEdgeFromDisk(int key){
		//determine which edge class we have to use
		int typeId = getEdgeDict().read(4, key*4).getInt(0);
		
		//read the data from the disk
		ByteBuffer buf = readGraphElementFromDisk(key, typeId);
		
		//create a vertex that is identical to the vertex we deleted earlier
		EdgeImpl edge = restoreEdge(buf, key);
		
		//return a CacheEntry for that new vertex so we can put it back in the cache
		return new CacheEntry<EdgeImpl>(edge);
	}
	
	/**
	 * Helper method to avoid duplicate code in readVertexFromDisk 
	 * and readEdgeFromDisk
	 */
	private ByteBuffer readGraphElementFromDisk(int key, int typeId){
		if (LOG) restoredGEs++;
		
		//determine the size of the GraphElement we want to restore
		int byteSize = GraphElementProfile.getProfile(typeId).getSize();
		
		FileAccess file = files[typeId];
		ByteBuffer buf = file.read(byteSize, key * byteSize);
		
		buf.position(0);
		
		return buf;
	}
	
	/**
	 * Writes an Incidence to the disk if it has been newly created, or if it has
	 * been changed since the last time it was loaded from the disk.
	 * 
	 * @param incidenceRef - The Reference to the Incidence that is written out.
	 */
	public void writeIncidenceToDisk(CacheEntry<IncidenceImpl> incidenceRef){
		Tracker tracker = incidenceRef.getTracker();
		if (LOG) deletedIncidences++;
		
		if (tracker == null) {
			//incidence is neither new nor has it been changed since its last reload
			return;
		}
		if (LOG) writtenIncidences++;

		ByteBuffer attributes = tracker.getVariables();
		
		//get the file in which incidences are stored
		FileAccess file = files[files.length - 3];
		
		//incidences always need 52 bytes because they have no attributes
		file.write(attributes, incidenceRef.getKey() * 52);
	}
	
	/**
	 * Reads an incidence from the disk and restores it.
	 *  
	 * @param key
	 *        The local id of the incidence
	 * @return
	 *        A soft reference to the restored incidence
	 */
	public CacheEntry<IncidenceImpl> readIncidenceFromDisk(int key){
		if (LOG) restoredIncidences++;
		
		//read 52 bytes from the file which stores the Incidences
		FileAccess file = files[files.length - 3];
		ByteBuffer buf = file.read(52, key * 52);
		
		buf.position(0);
		
		//create an Incidence that is identical to the Incidence we deleted earlier
		IncidenceImpl inc = restoreIncidence(buf, key);
		
		//return a CacheEntry for that new incidence so we can put it back in the cache
		return new CacheEntry<IncidenceImpl>(inc);
	}

	/**
	 * Restores a Vertex using the data provided by a ByteBuffer.
	 * 
	 * @param buf
	 *        The ByteBuffer holding the Vertex' variables
	 * @param key
	 *        The Vertex' id
	 * @return
	 *        The restored Vertex
	 */
	private VertexImpl restoreVertex(ByteBuffer buf, int key){
		int typeId = buf.getInt(0) - 1;
		
		Schema schema = graphdb.getSchema();
		
		//get the vertex class for the typeId we read from the disk
		VertexClass verClass = (VertexClass) schema
				.getTypeForId(typeId);
		Class<? extends Vertex> m1Class = verClass.getM1Class();
		
		//convert the local ID (the key) to the global ID
		long longId = graphdb.convertToGlobalId(key);
		
		GraphFactory factory = graphdb.getGraphFactory();
		
		//create a new Vertex of the given vertex class with the given ID
		VertexImpl ver = (VertexImpl) factory
				.createVertex_Diskv2BasedStorage(m1Class, longId, graphdb);
		
		//restore the vertex' variables and restore it
		return (VertexImpl) restoreGraphElement(ver, buf, typeId);
	}
	
	/**
	 * Restores a Vertex using the data provided by a ByteBuffer.
	 * 
	 * @param buf
	 *        The ByteBuffer holding the Vertex' variables
	 * @param key
	 *        The Vertex' id
	 * @return
	 *        The restored Vertex
	 */
	private EdgeImpl restoreEdge(ByteBuffer buf, int key){
		int typeId = buf.getInt(0) - 1;
		
		Schema schema = graphdb.getSchema();
		
		//get the edge class for the typeId we read from the disk
		EdgeClass edgeClass = (EdgeClass) schema
				.getTypeForId(typeId);
		Class<? extends Edge> m1Class = edgeClass.getM1Class();
		
		//convert the local ID (the key) to the global ID
		long longId = graphdb.convertToGlobalId(key);
		
		//create a new Edge of the given edge class with the given ID
		GraphFactory factory = graphdb.getGraphFactory();
		EdgeImpl edge = (EdgeImpl) factory
				.createEdge_Diskv2BasedStorage(m1Class, longId, graphdb);
		
		//restore the edge's variables and restore it
		return (EdgeImpl) restoreGraphElement(edge, buf, typeId);
	}
	
	/**
	 * Helper method to avoid duplicate code in restoreVertex and restoreEdge
	 */
	private GraphElementImpl<?,?,?,?> restoreGraphElement(GraphElementImpl<?,?,?,?> ge, ByteBuffer buf, int typeId){
		//restore the non-generated variables of this GraphElement
		ge.restoreNextElementId(buf.getLong(4));
		ge.restorePreviousElementId(buf.getLong(12));
		ge.restoreFirstIncidenceId(buf.getLong(20));
		ge.restoreLastIncidenceId(buf.getLong(28));
		ge.restoreIncidenceListVersion(buf.getLong(36));
		ge.restoreSigmaId(buf.getLong(44));
		ge.restoreSubOrdianteGraphId(buf.getLong(52));
		ge.restoreKappa(buf.getInt(60));
		
		//restore the generated attributes of this GraphElement
		buf.position(64);
		GraphElementProfile prof = GraphElementProfile.getProfile(typeId);
		prof.restoreAttributesOfElement(ge, buf);

		return ge;
	}
	
	/**
	 * Restores an Incidence using the data provided by a ByteBuffer.
	 * 
	 * @param buf
	 *        The ByteBuffer holding the Incidence's variables
	 * @param key
	 *        The Incidence's id
	 * @return
	 *        The restored Incidence
	 */
	private IncidenceImpl restoreIncidence(ByteBuffer buf, int key){
		int typeId = buf.getInt(0) - 1;
		
		Schema schema = graphdb.getSchema();
		
		//get the incidence class for the typeId we read from the disk
		IncidenceClass incClass = (IncidenceClass) schema
				.getTypeForId(typeId);
		Class<? extends Incidence> m1Class = incClass.getM1Class();
		
		//read the attributes that we need for the factory method
		long longId = key;
		long eId = buf.getLong(36);
		long vId = buf.getLong(44);
		
		//create a new Incidence of the given incidence class with the given ID
		//(Incidences only have local IDs, so we don't need to convert the id)
		GraphFactory factory = graphdb.getGraphFactory();
		IncidenceImpl inc = (IncidenceImpl) factory
				.createIncidence_Diskv2BasedStorage(m1Class, longId, vId,
						eId, graphdb);
		
		//restore the variables that aren't required by the constructor
		inc.restoreNextIncidenceIdAtEdge(buf.getLong(4));
		inc.restoreNextIncidenceIdAtVertex(buf.getLong(12));
		inc.restorePreviousIncidenceIdAtEdge(buf.getLong(20));
		inc.restorePreviousIncidenceIdAtVertex(buf.getLong(28));

		return inc;
	}
	
	/**
	 * Gets a file in which the vertex type ID for every vertex is stored
	 */
	public FileAccess getVertexDict(){
		return files[files.length - 2];
	}
	
	/**
	 * Gets a file in which the edge type ID for every edge is stored
	 */
	public FileAccess getEdgeDict(){
		return files[files.length - 1];
	}
}
