package de.uni_koblenz.jgralab.impl.diskv2;

import java.nio.ByteBuffer;
import java.util.List;

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
	 * The amount of memory in bytes an incidence uses on the disk
	 */
	protected static final int INCIDENCE_SIZE = 52;
	
	/**
	 * The amount of memory in bytes a GraphElement, sans its attributes,
	 * needs on the disk
	 */
	protected static final int GRAPHELEMENT_BASE_SIZE = 64;

	/**
	 * A buffer used to track the variables of a GraphElement or Incidence
	 * It is used to track the attributes that are the same for every Incidence or
	 * every GraphElement, i.e. the variables that are not declared in the 
	 * generated code. The ClassId of each object is also stored here.
	 * 
	 * If the tracked object is an Incidence, this Buffer holds six values of type
	 * long and its size is 52 Bytes. If the tracked object is a GraphElement, it holds
	 * seven values of type long and one value of type int, which means its size 
	 * is 64 Bytes.
	 * 
	 * Every index in the buffer is reserved for a specific attribute.
	 * 
	 * For an Incidence, the indexes are:
	 *  0 - incidenceClassId
	 *  4 - nextIncidenceIdAtEdge
	 * 12 - nextIncidenceIdAtVertex
	 * 20 - previousIncidenceIdAtEdge
	 * 28 - previousIncidenceIdAtVertex
	 * 36 - incidentEdgeId
	 * 44 - incidentVertexId
	 * 
	 * For a GraphElement, the indexes are:
	 *  0 - VertexClassId or EdgeClassId
	 *  4 - nextElementId
	 * 12 - previousElementId
	 * 20 - firstIncidenceId
	 * 28 - lastIncidenceId
	 * 36 - incidenceListVersion
	 * 44 - SigmaId
	 * 52 - subOrdianteGraphId
	 * 60 - kappa
	 */
	protected ByteBuffer variables;
	
	/**
	 * Creates a new Tracker.
	 * 
	 * @param size - The accumulated size of all the attributes that are be tracked.
	 */
	protected Tracker(int size){
		variables = ByteBuffer.allocate(size);
	}
	
	/**
	 * Puts an attribute into the buffer.
	 * 
	 * @param attribute - the attribute to be tracked
	 * @param index - the position at which the tracked attribute is stored
	 */
	public void putVariable(int index, long variable){
		variables.putLong(index, variable);
	}
	
	/**
	 * Method to access the ByteBuffer.
	 */
	public abstract ByteBuffer getVariables();
	
	/**
	 * Method to access the Strings.
	 */
	public abstract String[] getStrings();
	
	/**
	 * Method to access the Lists.
	 */
	public abstract List[] getLists();
}
