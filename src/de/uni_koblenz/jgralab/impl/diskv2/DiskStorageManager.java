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
	int restoredGEs;
	int deletedIncidences;
	int restoredIncidences;
	
	private GraphDatabaseBaseImpl graphdb;
	
	//TODO: Make an array, so we can ditch getOrCreateFileAccess
	private FileAccess[] files;
	
	public DiskStorageManager(GraphDatabaseBaseImpl graphdb){
		this.graphdb = graphdb;

		setupFilesAndProfiles();
		
		//TODO: For testing purposes, delete eventually
		deletedGEs = 0;
		restoredGEs = 0;
		deletedIncidences = 0;
		restoredIncidences = 0;
	}
	
	private void setupFilesAndProfiles(){
		Schema s = graphdb.getSchema();
		
		int amountOfClasses = s.getNumberOfTypedElementClasses();
		GraphElementProfile.setup(amountOfClasses);
		files = new FileAccess[amountOfClasses + 3];
		
		List<VertexClass> vClasses = s.getVertexClassesInTopologicalOrder();
		List<EdgeClass> eClasses = s.getEdgeClassesInTopologicalOrder();
		
		files[amountOfClasses] = FileAccess.createFileAccess("incidences");
		files[amountOfClasses + 1] = FileAccess.createFileAccess("vertexDict");
		files[amountOfClasses + 2] = FileAccess.createFileAccess("edgeDict");
		
		for (VertexClass vClass: vClasses){
			if (!vClass.isAbstract()){
				int typeId = vClass.getId();
				GraphElementProfile.createProfile(vClass, typeId, graphdb);
				files[typeId] = FileAccess.createFileAccess(typeId + "_vertices");
			}
		}
		
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
		System.out.println("Restored Elements  : " + restoredGEs);
		System.out.println("Deleted  Incidences: " + deletedIncidences);
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
			//incidence is neither new nor has it been changed since its last reload
			return;
		}
		ByteBuffer attributes = tracker.getVariables();
		
		int typeId = attributes.getInt(0) - 1;
		FileAccess file = files[typeId];
		
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
		int typeId = getVertexDict().read(4, key*4).getInt(0);
		
		ByteBuffer buf = readGraphElementFromDisk(key, typeId);
		
		VertexImpl ver = restoreVertex(buf, key);
		
		return new CacheEntry<VertexImpl>(ver);
	}
	
	/**
	 * Reads a vertex from the disk and restores it.
	 *  
	 * @param key
	 *        The local id of the vertex
	 * @return
	 *        A soft reference to the restored vertex
	 */
	public CacheEntry<EdgeImpl> readEdgeFromDisk(int key){
		int typeId = getEdgeDict().read(4, key*4).getInt(0);
		
		ByteBuffer buf = readGraphElementFromDisk(key, typeId);
		
		EdgeImpl edge = restoreEdge(buf, key);
		
		return new CacheEntry<EdgeImpl>(edge);
	}
	
	/**
	 * Helper method to avoid duplicate code in readVertexFromDisk 
	 * and readEdgeFromDisk
	 */
	private ByteBuffer readGraphElementFromDisk(int key, int typeId){
		if (LOG) restoredGEs++;
		
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

		ByteBuffer attributes = tracker.getVariables();
		
		FileAccess file = files[files.length - 3];
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
		
		FileAccess file = files[files.length - 3];
		ByteBuffer buf = file.read(52, key * 52);
		
		buf.position(0);
		
		IncidenceImpl inc = restoreIncidence(buf, key);
		
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
		
		VertexClass verClass = (VertexClass) schema
				.getTypeForId(typeId);
		Class<? extends Vertex> m1Class = verClass.getM1Class();
		
		long longId = graphdb.convertToGlobalId(key);
		
		GraphFactory factory = graphdb.getGraphFactory();
		
		VertexImpl ver = (VertexImpl) factory
				.createVertex_Diskv2BasedStorage(m1Class, longId, graphdb);
		
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
		
		EdgeClass edgeClass = (EdgeClass) schema
				.getTypeForId(typeId);
		Class<? extends Edge> m1Class = edgeClass.getM1Class();
		
		long longId = graphdb.convertToGlobalId(key);
		
		GraphFactory factory = graphdb.getGraphFactory();
		
		EdgeImpl edge = (EdgeImpl) factory
				.createEdge_Diskv2BasedStorage(m1Class, longId, graphdb);
		
		return (EdgeImpl) restoreGraphElement(edge, buf, typeId);
	}
	
	/**
	 * Helper method to avoid duplicate code in restoreVertex and restoreEdge
	 */
	private GraphElementImpl<?,?,?,?> restoreGraphElement(GraphElementImpl<?,?,?,?> ge, ByteBuffer buf, int typeId){
		ge.restoreNextElementId(buf.getLong(4));
		ge.restorePreviousElementId(buf.getLong(12));
		ge.restoreFirstIncidenceId(buf.getLong(20));
		ge.restoreLastIncidenceId(buf.getLong(28));
		ge.restoreIncidenceListVersion(buf.getLong(36));
		ge.restoreSigmaId(buf.getLong(44));
		ge.restoreSubOrdianteGraphId(buf.getLong(52));
		ge.restoreKappa(buf.getInt(60));
		
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
		
		IncidenceClass incClass = (IncidenceClass) schema
				.getTypeForId(typeId);
		Class<? extends Incidence> m1Class = incClass.getM1Class();
		
		long longId = key;
		long eId = buf.getLong(36);
		long vId = buf.getLong(44);
		
		GraphFactory factory = graphdb.getGraphFactory();
		
		IncidenceImpl inc = (IncidenceImpl) factory
				.createIncidence_Diskv2BasedStorage(m1Class, longId, vId,
						eId, graphdb);
		
		inc.restoreNextIncidenceIdAtEdge(buf.getLong(4));
		inc.restoreNextIncidenceIdAtVertex(buf.getLong(12));
		inc.restorePreviousIncidenceIdAtEdge(buf.getLong(20));
		inc.restorePreviousIncidenceIdAtVertex(buf.getLong(28));

		return inc;
	}
	
	public FileAccess getVertexDict(){
		return files[files.length - 2];
	}
	
	public FileAccess getEdgeDict(){
		return files[files.length - 1];
	}
}
