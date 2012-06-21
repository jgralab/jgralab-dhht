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
