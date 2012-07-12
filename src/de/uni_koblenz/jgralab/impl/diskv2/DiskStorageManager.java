package de.uni_koblenz.jgralab.impl.diskv2;

import java.nio.ByteBuffer;
import java.util.HashMap;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class DiskStorageManager {
	
	//TODO: For testing purposes, delete eventually
	boolean LOG = true;
	int deletedVertices;
	int restoredVertices;
	int deletedEdges;
	int restoredEdges;
	int deletedIncidences;
	int restoredIncidences;
	
	private GraphDatabaseBaseImpl graphdb;
	
	//TODO: Make an array, so we can ditch getOrCreateFileAccess
	private FileAccess vertexDict;
	private FileAccess edgeDict;
	
	private int[] byteSizes;
	
	public DiskStorageManager(GraphDatabaseBaseImpl graphdb){
		this.graphdb = graphdb;
		this.vertexDict = FileAccess.getOrCreateFileAccess("vertexDict");
		this.edgeDict = FileAccess.getOrCreateFileAccess("edgeDict");
		byteSizes = new int[1024];
		
		//TODO: For testing purposes, delete eventually
		deletedVertices = 0;
		restoredVertices = 0;
		deletedEdges = 0;
		restoredEdges = 0;
		deletedIncidences = 0;
		restoredIncidences = 0;
	}
	
	//TODO: testing
	public void printStats(){
		System.out.println("Deleted  Vertices  : " + deletedVertices);
		System.out.println("Restored Vertices  : " + restoredVertices);
		System.out.println("Deleted  Edges     : " + deletedEdges);
		System.out.println("Restored Edges     : " + restoredEdges);
		System.out.println("Deleted  Incidences: " + deletedIncidences);
		System.out.println("Restored Incidences: " + restoredIncidences);
	}
	
	/**
	 * Writes a Graph Element to the disk if it has been newly created, or if it has
	 * been changed since the last time it was loaded from the disk.
	 * 
	 * @param geRef - The Reference to the GraphElement that is written out.
	 */
	public void writeVertexToDisk(CacheEntry<VertexImpl> vRef){
		Tracker tracker = vRef.getTracker();
		if (LOG) deletedVertices++;
		
		if (tracker == null) {
			//incidence is neither new nor has it been changed since its last reload
			return;
		}

		ByteBuffer attributes = tracker.getVariables();
		
		int typeID = attributes.getInt(0) - 1;
		String fileName = typeID + "_vertices";
		FileAccess file = FileAccess.getOrCreateFileAccess(fileName);
		
		if (byteSizes[typeID] == 0){
			detectByteSizeVertex(typeID);
		}
		int byteSize = byteSizes[typeID] + 64;
		
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
		if (LOG) restoredVertices++;
		
		int typeID = vertexDict.read(4, key*4).getInt(0);
		String fileName = typeID + "_vertices";
		
		int byteSize = byteSizes[typeID] + 64;
		
		FileAccess file = FileAccess.getOrCreateFileAccess(fileName);
		ByteBuffer buf = file.read(byteSize, key * byteSize);
		
		buf.position(0);
		
		VertexImpl ver = restoreVertex(buf, key);
		
		return new CacheEntry<VertexImpl>(ver);
	}
	
	/**
	 * Writes a Graph Element to the disk if it has been newly created, or if it has
	 * been changed since the last time it was loaded from the disk.
	 * 
	 * @param geRef - The Reference to the GraphElement that is written out.
	 */
	public void writeEdgeToDisk(CacheEntry<EdgeImpl> eRef){
		Tracker tracker = eRef.getTracker();
		if (LOG) deletedEdges++;
		
		if (tracker == null) {
			//incidence is neither new nor has it been changed since its last reload
			return;
		}

		ByteBuffer attributes = tracker.getVariables();
		
		int typeID = attributes.getInt(0) - 1;
		String fileName = typeID + "_edges";
		FileAccess file = FileAccess.getOrCreateFileAccess(fileName);
		
		if (byteSizes[typeID] == 0){
			detectByteSizeEdge(typeID);
		}
		int byteSize = byteSizes[typeID] + 64;
		
		file.write(attributes, eRef.getKey() * byteSize);
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
		if (LOG) restoredEdges++;
		
		int typeID = edgeDict.read(4, key*4).getInt(0);
		String fileName = typeID + "_edges";
		
		int byteSize = byteSizes[typeID] + 64;
		
		FileAccess file = FileAccess.getOrCreateFileAccess(fileName);
		ByteBuffer buf = file.read(byteSize, key * byteSize);
		
		buf.position(0);
		
		EdgeImpl edge = restoreEdge(buf, key);
		
		return new CacheEntry<EdgeImpl>(edge);
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
		
		FileAccess file = FileAccess.getOrCreateFileAccess("incidences");
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
		
		FileAccess file = FileAccess.getOrCreateFileAccess("incidences");
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
		
		ver.restoreNextElementId(buf.getLong(4));
		ver.restorePreviousElementId(buf.getLong(12));
		ver.restoreFirstIncidenceId(buf.getLong(20));
		ver.restoreLastIncidenceId(buf.getLong(28));
		ver.restoreIncidenceListVersion(buf.getLong(36));
		ver.restoreSigmaId(buf.getLong(44));
		ver.restoreSubOrdianteGraphId(buf.getLong(52));
		ver.restoreKappa(buf.getInt(60));
		
		buf.position(64);
		GraphElementProfile prof = GraphElementProfile
				.getOrCreateProfile(ver); 
		prof.restoreAttributesOfElement(ver, buf);

		return ver;
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
		
		edge.restoreNextElementId(buf.getLong(4));
		edge.restorePreviousElementId(buf.getLong(12));
		edge.restoreFirstIncidenceId(buf.getLong(20));
		edge.restoreLastIncidenceId(buf.getLong(28));
		edge.restoreIncidenceListVersion(buf.getLong(36));
		edge.restoreSigmaId(buf.getLong(44));
		edge.restoreSubOrdianteGraphId(buf.getLong(52));
		edge.restoreKappa(buf.getInt(60));
		
		buf.position(64);
		GraphElementProfile prof = GraphElementProfile
				.getOrCreateProfile(edge); 
		prof.restoreAttributesOfElement(edge, buf);

		return edge;
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
	
	//Helper method to detect the size of a vertex from its typeID.
	//TODO: This method is a kludge
	private void detectByteSizeVertex(int typeID){
		Schema schema = graphdb.getSchema();
		
		VertexClass verClass = (VertexClass) schema
				.getTypeForId(typeID);
		Class<? extends Vertex> m1Class = verClass.getM1Class();
		
		GraphFactory factory = graphdb.getGraphFactory();
		
		VertexImpl ver = (VertexImpl) factory
				.createVertex_Diskv2BasedStorage(m1Class, 1, graphdb);
		GraphElementProfile prof = 
				GraphElementProfile.getOrCreateProfile(ver);
		int byteSize = prof.getSize();
		
		byteSizes[typeID] = byteSize;
	}
	
	//TODO: This method is a kludge
	private void detectByteSizeEdge(int typeID){
		Schema schema = graphdb.getSchema();
		
		EdgeClass edgeClass = (EdgeClass) schema
				.getTypeForId(typeID);
		Class<? extends Edge> m1Class = edgeClass.getM1Class();
		
		GraphFactory factory = graphdb.getGraphFactory();
		
		EdgeImpl edge = (EdgeImpl) factory
			.createEdge_Diskv2BasedStorage(m1Class, 1, graphdb);
		GraphElementProfile prof = 
			GraphElementProfile.getOrCreateProfile(edge);
		int byteSize = prof.getSize();
		
		byteSizes[typeID] = byteSize;
	}

}
