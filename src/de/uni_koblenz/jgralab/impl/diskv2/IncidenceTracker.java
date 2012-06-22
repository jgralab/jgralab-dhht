package de.uni_koblenz.jgralab.impl.diskv2;

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
		super(48);
	}
	
	/**
	 * Stores all attributes of an Incidence in the ByteBuffer.
	 * 
	 * @param inc - The Incidence to be tracked
	 */
	public void fill(IncidenceImpl inc){
		putAttribute(inc.getNextIncidenceIdAtEdge(), 0);
		putAttribute(inc.getNextIncidenceIdAtVertex(), 8);
		putAttribute(inc.getPreviousIncidenceIdAtEdge(), 16);
		putAttribute(inc.getPreviousIncidenceIdAtVertex(), 24);
		putAttribute(inc.getIncidentEdgeId(), 32);
		putAttribute(inc.getIncidentVertexId(), 40);
	}
}
