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
		attributes.putInt(0, (typeId + 1));
		
		putAttribute(inc.getNextIncidenceIdAtEdge(), 4);
		putAttribute(inc.getNextIncidenceIdAtVertex(), 12);
		putAttribute(inc.getPreviousIncidenceIdAtEdge(), 20);
		putAttribute(inc.getPreviousIncidenceIdAtVertex(), 28);
		putAttribute(inc.getIncidentEdgeId(), 36);
		putAttribute(inc.getIncidentVertexId(), 44);
	}
}
