package de.uni_koblenz.jgralab.impl.diskv2;

import java.util.HashMap;

import de.uni_koblenz.jgralab.schema.IncidenceClass;

/**
 * Tracks the changed attributes of an Incidence.
 * 
 * @author aheld
 *
 */
public class IncidenceTracker extends Tracker{
	
	/**
	 * Create a new tracker with a ByteBuffer size of 48 Bytes.
	 */
	public IncidenceTracker(){
		super(52);
	}
	
	/**
	 * Stores all attributes of an Incidence in the ByteBuffer.
	 * 
	 * @param inc - The Incidence to be tracked
	 */
	public void fill(IncidenceImpl inc){
		int typeId = inc.getType().getId();
		variables.putInt(0, (typeId + 1));
		
		putVariable(inc.getNextIncidenceIdAtEdge(), 4);
		putVariable(inc.getNextIncidenceIdAtVertex(), 12);
		putVariable(inc.getPreviousIncidenceIdAtEdge(), 20);
		putVariable(inc.getPreviousIncidenceIdAtVertex(), 28);
		putVariable(inc.getIncidentEdgeId(), 36);
		putVariable(inc.getIncidentVertexId(), 44);
	}
}
