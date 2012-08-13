package de.uni_koblenz.jgralab.impl.diskv2;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

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
	 * Stores all variables of an Incidence in the ByteBuffer.
	 * 
	 * @param inc - The Incidence to be tracked
	 */
	public void fill(IncidenceImpl inc){
		int typeId = inc.getType().getId();
		variables.putInt(0, (typeId + 1));
		
		putVariable(4, inc.getNextIncidenceIdAtEdge());
		putVariable(12, inc.getNextIncidenceIdAtVertex());
		putVariable(20, inc.getPreviousIncidenceIdAtEdge());
		putVariable(28, inc.getPreviousIncidenceIdAtVertex());
		putVariable(36, inc.getIncidentEdgeId());
		putVariable(44, inc.getIncidentVertexId());
	}
	
	public ByteBuffer getVariables(){
		return variables;
	}

	@Override
	public String[] getStrings() {
		throw new UnsupportedOperationException("Tried to access strings[] for an IncidenceTracker");
	}

	@Override
	public List[] getLists() {
		throw new UnsupportedOperationException("Tried to access lists[] for an IncidenceTracker");
	}
}
