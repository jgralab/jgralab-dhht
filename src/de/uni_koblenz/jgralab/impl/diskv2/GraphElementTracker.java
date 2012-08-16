package de.uni_koblenz.jgralab.impl.diskv2;

import java.nio.ByteBuffer;
import java.util.List;

import de.uni_koblenz.jgralab.GraphElement;

public class GraphElementTracker extends Tracker{
	
	/**
	 * Store the attributes, Strings and Lists of a GraphElement.
	 */
	private ByteBuffer attributes;
	private String[] strings;
	private List[] lists;
	
	/**
	 * Create a new tracker with a ByteBuffer size of 64 Bytes.
	 */
	public GraphElementTracker(){
		super(64);
	}
	
	/**
	 * Stores the variables of a GraphElement in the ByteBuffer.
	 * 
	 * @param ge - The GraphElement to be tracked
	 */
	public void fill(GraphElementImpl<?,?,?,?> ge){
		storeAttributes(ge);
		storeStrings(ge);
		storeLists(ge);
	}
	
	private void storeVariables(GraphElementImpl<?,?,?,?> ge){
		//TODO Don't put typeID in the buffer, but declare it as a normal member
		int typeId = ge.getType().getId();
		variables.putInt(0, (typeId + 1));
				
		putVariable(4, ge.getNextElementId());
		putVariable(12, ge.getPreviousElementId());
		putVariable(20, ge.getFirstIncidenceId());
		putVariable(28, ge.getLastIncidenceId());
		putVariable(36, ge.getIncidenceListVersion());
		putVariable(44, ge.getSigmaId());
		putVariable(52, ge.getSubOrdinateGraphId());
		putKappa(ge.getKappa());
	}
	
	public void putKappa(int kappa){
		variables.putInt(60, kappa);
	}
	
	/**
	 * Stores the attributes of a GraphElement in the ByteBuffer.
	 * 
	 * @param ge - The GraphElement to be tracked
	 */
	public void storeAttributes(GraphElementImpl<?,?,?,?> ge){
		storeVariables(ge);
		int typeId = ge.getType().getId();
		GraphElementProfile profile = GraphElementProfile.getProfile(typeId);
		attributes = profile.getAttributesForElement(ge);
	}
	
	/**
	 * Stores the attributes of a GraphElement in the ByteBuffer.
	 * 
	 * @param ge - The GraphElement to be tracked
	 */
	public void storeStrings(GraphElementImpl<?,?,?,?> ge){
		//TODO avoid doing this
		if (attributes == null) storeAttributes(ge);
		int typeId = ge.getType().getId();
		GraphElementProfile profile = GraphElementProfile.getProfile(typeId);
		strings = profile.getStringsForElement(ge);
	}
	
	/**
	 * Stores the attributes of a GraphElement in the ByteBuffer.
	 * 
	 * @param ge - The GraphElement to be tracked
	 */
	public void storeLists(GraphElementImpl<?,?,?,?> ge){
		//TODO avoid doing this
		if (attributes == null) storeAttributes(ge);
		int typeId = ge.getType().getId();
		GraphElementProfile profile = GraphElementProfile.getProfile(typeId);
		lists = profile.getListsForElement(ge);
	}
	
	public ByteBuffer getVariables(){
		if (attributes == null)
			return variables;
		
		int totalSize = variables.capacity() + attributes.capacity();
		ByteBuffer buf = ByteBuffer.allocate(totalSize);
		buf.put(variables.array());
		buf.put(attributes.array());
		return buf;
	}
	
	public String[] getStrings(){
		return strings;
	}
	
	public List[] getLists(){
		return lists;
	}

}
