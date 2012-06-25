package de.uni_koblenz.jgralab.impl.diskv2;

import java.util.HashMap;

import de.uni_koblenz.jgralab.GraphElement;

public class DiskStorageManager {
	
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
		Tracker tracker = incidenceRef.getTracker();
		if (tracker == null) {
			return;
		}
		
		IncidenceTracker iTracker = (IncidenceTracker) tracker;
		
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
