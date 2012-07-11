package de.uni_koblenz.jgralab.impl.diskv2;

import java.nio.ByteBuffer;
import java.util.HashMap;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
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
	
	public DiskStorageManager(GraphDatabaseBaseImpl graphdb){
		this.graphdb = graphdb;
		
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
		
		FileAccess file = FileAccess.getOrCreateFileAccess("vertices");
		file.write(attributes, vRef.getKey() * 101);
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
		
		FileAccess file = FileAccess.getOrCreateFileAccess("vertices");
		ByteBuffer buf = file.read(101, key * 101);
		
		buf.position(0);
		
		VertexImpl ver = restoreVertex(buf, key);
		
		return new CacheEntry<VertexImpl>(ver);
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
	public VertexImpl restoreVertex(ByteBuffer buf, int key){
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

		return ver;
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
	public IncidenceImpl restoreIncidence(ByteBuffer buf, int key){
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

}
