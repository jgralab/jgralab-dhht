package de.uni_koblenz.jgralab.impl.disk;

import java.util.Map;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Schema;

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


	public Vertex getVertexObject(int id);


	public Edge getEdgeObject(int id);


	public Schema getSchema();
	
	public abstract void removeEdgeFromDatabase(EdgeImpl e);

	public abstract void removeVertexFromDatabase(VertexImpl v);


	public <T extends Record> T createRecord(Class<T> recordClass, Map<String, Object> fields);

	
	
}
