package de.uni_koblenz.jgralab.impl.disk;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;

/**
 * This interface defines all methods needed to access properties of the graph and its elements
 * such as the first or last vertex of a graph, the incidence sequence of vertices and edges
 * and so on. The interface is implemented by GraphDatabase, the dispatcher class managing 
 * the connections between the single partial graphs of a distributed one and by DiskStorageManager,
 * controlling the local storage of single elements on the disk and in memory
 * @author dbildh
 *
 */

public interface GraphPropertyAccess {

	

	
	/**
	 * Sets the first {@link Incidence} of this {@link GraphElement} to
	 * <code>firstIncidence</code>.
	 * 
	 * @param firstIncidence
	 *            {@link IncidenceImpl}
	 */
	public void setFirstIncidence(int elemId, int incidenceId);
	
	
	public void setLastIncidence(int elemId, int incidenceId);


	public void incidenceListModified(int elemId);


	public int getSigma(int elemId);

	
	public void setSigma(int elementId, int sigmaId);
	
	
	public int getKappa(int elementId); 
	
	public void setKappa(int elementId, int kappa);
	
	public void setIncidenceListVersion(int elementId, long incidenceListVersion);

	public long getIncidenceListVersion(int elementId);
	
	
}
