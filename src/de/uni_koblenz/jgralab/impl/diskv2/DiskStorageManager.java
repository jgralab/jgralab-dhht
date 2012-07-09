package de.uni_koblenz.jgralab.impl.diskv2;

import java.nio.ByteBuffer;
import java.util.HashMap;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Schema;

public class DiskStorageManager {
	
	//TODO: For debugging purposes
	boolean LOG = true;
		
	private HashMap<String, GraphElementProfile> profiles;
	
	private GraphDatabaseBaseImpl graphdb;
	
	public DiskStorageManager(GraphDatabaseBaseImpl graphdb){
		profiles = new HashMap<String, GraphElementProfile>();
		this.graphdb = graphdb;
	}
	
	/**
	 * Writes an Incidence to the disk if it has been newly created, or if it has
	 * been changed since the last time it was loaded from the disk.
	 * 
	 * @param incidenceRef - The Reference to the Incidence that is written out.
	 */
	public void writeIncidenceToDisk(CacheEntry<IncidenceImpl> incidenceRef){
		if (LOG) System.out.print("Request to externalize Incidence #" + incidenceRef.getKey() + " - ");
		Tracker tracker = incidenceRef.getTracker();
		
		if (tracker == null) {
			if (LOG) System.out.println("Nothing to do");
			//incidence is neither new nor has it been changed since its last reload
			return;
		}
		
		if (LOG) System.out.println("Writing it to the disk");
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
		if (LOG) System.out.println("Request to read Incidence #" + key);
		
		FileAccess file = FileAccess.getOrCreateFileAccess("incidences");
		ByteBuffer buf = file.read(52, key * 52);
		
		buf.position(0);
		
		IncidenceImpl inc = restoreIncidence(buf, key);
		
		return new CacheEntry<IncidenceImpl>(inc);
	}
	
	/**
	 * Restores an Incidence using the data provided by a ByteBuffer.
	 * 
	 * @param buf
	 *        The ByteBuffer holding the Incidence's attributes
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
	
	public GraphElementProfile getGraphElementProfile(GraphElement<?,?,?,?> ge){
		String className = ge.getClass().getName();
		GraphElementProfile profile = profiles.get(className);
		
		if (profile == null){
			profile = new GraphElementProfile(ge.getClass());
			profiles.put(className, profile);
		}
		
		return profile;
	}

}
