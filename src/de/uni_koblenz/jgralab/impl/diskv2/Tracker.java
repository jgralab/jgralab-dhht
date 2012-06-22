package de.uni_koblenz.jgralab.impl.diskv2;

import java.nio.ByteBuffer;

/**
 * Abstract class used to track a graph object. Every tracker tracks
 * exactly one graph object. A tracker for a graph object is instantiated
 * if the object is newly created, or if the object has been restored from the
 * disk and one of its attributes has been changed. 
 * 
 * @author aheld
 */
public abstract class Tracker {

	/**
	 * A buffer used to track the attributes of a graph object. This is used to 
	 * track the attributes that are the same for every Incidence or every 
	 * GraphElement, i.e. the attributes that are not declared in the generated code.
	 * 
	 * If the tracked object is an Incidence, this Buffer holds six values of type
	 * long and its size is 48 Bytes. If the tracked object is a GraphElement, it holds
	 * seven values of type long and one value of type int, which means its size 
	 * is 60 Bytes.
	 * 
	 * Every index in the buffer is reserved for a specific attribute.
	 * 
	 * For an Incidence, the indexes are:
	 *  0 - nextIncidenceIdAtEdge
	 *  8 - nextIncidenceIdAtVertex
	 * 16 - previousIncidenceIdAtEdge
	 * 24 - previousIncidenceIdAtVertex
	 * 32 - incidentEdgeId
	 * 40 - incidentVertexId
	 * 
	 * For a GraphElement, the indexes are:
	 *  0 - nextElementId
	 *  8 - previousElementId
	 * 16 - firstIncidenceId
	 * 24 - lastIncidenceId
	 * 32 - incidenceListVersion
	 * 40 - SigmaId
	 * 48 - subOrdianteGraphId
	 * 56 - kappa
	 */
	protected ByteBuffer changedAttributes;
	
	/**
	 * Creates a new Tracker.
	 * 
	 * @param size - The accumulated size of all the attributes that are be tracked.
	 */
	protected Tracker(int size){
		changedAttributes = ByteBuffer.allocate(size);
	}
	
	/**
	 * Puts an attribute into the buffer.
	 * 
	 * @param attribute - the attribute to be tracked
	 * @param index - the position at which the tracked attribute is stored
	 */
	public void putAttribute(long attribute, int index){
		changedAttributes.putLong(index, attribute);
	}
	
	/**
	 * Method to access the ByteBuffer.
	 */
	public ByteBuffer getChangedAttributes(){
		return changedAttributes;
	}
}
