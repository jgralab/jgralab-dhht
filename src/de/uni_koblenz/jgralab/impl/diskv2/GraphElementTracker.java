package de.uni_koblenz.jgralab.impl.diskv2;

import java.nio.ByteBuffer;

import de.uni_koblenz.jgralab.GraphElement;

public class GraphElementTracker extends Tracker{
	
	/**
	 * Stores the attributes of a GraphElement.
	 */
	private ByteBuffer attributes;
	
	/**
	 * Create a new tracker with a ByteBuffer size of 64 Bytes.
	 */
	public GraphElementTracker(){
		super(64);
	}
	
	/**
	 * Stores the non-generated variables of a GraphElement in the ByteBuffer.
	 * 
	 * @param ge - The GraphElement to be tracked
	 */
	public void fill(GraphElementImpl<?,?,?,?> ge){
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
		int typeId = ge.getType().getId();
		GraphElementProfile profile = GraphElementProfile.getProfile(typeId);
		attributes = profile.getAttributesForElement(ge);
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

}
