package de.uni_koblenz.jgralab.impl.disk;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;

public interface GraphDatabaseInternalMethods {

	/**
	 * Sets the sigma value of the element identified by <code>elementId</code>
	 * to the value <code>sigmaId</code>. Both values are signed, negative values
	 * identify edges while positve ones identify vertices as in all other methods
	 * of this class 
	 */
	public abstract void setSigma(long globalElementId, int sigmaId);

	/**
	 * Sets the kappa value of the global element identified by the given id
	 * @param elementId
	 * @param kappa
	 */
	public abstract void setKappa(long globalElementId, int kappa);

	/**
	 * Sets the vertexListModified-flag of the graph
	 */
	public abstract void vertexListModified();

	/**
	 * @return true iff the graph is loading
	 */
	public abstract boolean isLoading();

	public abstract void setLoading(boolean isLoading);

	public abstract void setVCount(int localSubgraphId, int count);

	public abstract void setFirstVertexId(int subgraphId, long edgeId);

	public abstract void setLastVertexId(int subgraphId, long edgeId);

	public abstract void setFirstEdgeId(int subgraphId, long edgeId);

	public abstract void setLastEdgeId(int subgraphId, long edgeId);

	/**
	 * Sets the first {@link Incidence} of this {@link GraphElement} to
	 * <code>firstIncidence</code>.
	 * 
	 * @param firstIncidence
	 *            {@link IncidenceImpl}
	 */
	public abstract void setFirstIncidenceId(long globalElementId, long incidenceId);

	public abstract void setLastIncidenceId(long globalElementId, long incidenceId);

	public abstract void setNextIncidenceIdAtVertexId(long globalIncidenceId,
			long nextIncidenceId);

	public abstract void setPreviousIncidenceIdAtVertexId(
			long globalIncidenceId, long nextIncidenceId);

	public abstract void setNextIncidenceAtEdge(long globalIncidenceId,
			long nextIncidenceId);

	public abstract void setPreviousIncidenceAtEdge(long globalIncidenceId,
			long nextIncidenceId);

}