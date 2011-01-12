package de.uni_koblenz.jgralab.impl.std;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.IncidenceBaseImpl;

/**
 * Implements the interface {@link Incidence}.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class IncidenceImpl extends IncidenceBaseImpl {

	/**
	 * The incident {@link VertexImpl}.
	 */
	private VertexImpl incidentVertex;

	/**
	 * The next {@link Incidence} in the lambda-sequence of
	 * {@link IncidenceImpl#incidentVertex}.
	 */
	private IncidenceImpl nextIncidenceAtVertex;

	/**
	 * The previous {@link Incidence} in the lambda-sequence of
	 * {@link IncidenceImpl#incidentVertex}.
	 */
	private IncidenceImpl previousIncidenceAtVertex;

	/**
	 * The incident {@link EdgeImpl}.
	 */
	private EdgeImpl incidentEdge;

	/**
	 * The next {@link Incidence} in the lambda-sequence of
	 * {@link IncidenceImpl#incidentEdge}.
	 */
	private IncidenceImpl nextIncidenceAtEdge;

	/**
	 * The previous {@link Incidence} in the lambda-sequence of
	 * {@link IncidenceImpl#incidentEdge}.
	 */
	private IncidenceImpl previousIncidenceAtEdge;

	/**
	 * The direction of this {@link Incidence}.
	 */
	private Direction direction;

	protected void setIncidentVertex(VertexImpl incidentVertex) {
		this.incidentVertex = incidentVertex;
	}

	public void setNextIncidenceAtVertex(IncidenceImpl nextIncidenceAtVertex) {
		this.nextIncidenceAtVertex = nextIncidenceAtVertex;
	}

	public void setPreviousIncidenceAtVertex(
			IncidenceImpl previousIncidenceAtVertex) {
		this.previousIncidenceAtVertex = previousIncidenceAtVertex;
	}

	protected void setIncidentEdge(EdgeImpl incidentEdge) {
		this.incidentEdge = incidentEdge;
	}

	protected void setNextIncidenceAtEdge(IncidenceImpl nextIncidenceAtEdge) {
		this.nextIncidenceAtEdge = nextIncidenceAtEdge;
	}

	protected void setPreviousIncidenceAtEdge(
			IncidenceImpl previousIncidenceAtEdge) {
		this.previousIncidenceAtEdge = previousIncidenceAtEdge;
	}

	protected void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public Direction getDirection() {
		return direction;
	}

	@Override
	public Graph getGraph() {
		// an Incidence belongs to the same partial graph as the incident edge
		return incidentEdge.getGraph();
	}

	@Override
	public Edge getEdge() {
		return incidentEdge;
	}

	@Override
	public Vertex getVertex() {
		return incidentVertex;
	}

	@Override
	public Incidence getNextIncidenceAtEdge() {
		return nextIncidenceAtEdge;
	}

	@Override
	public Incidence getNextIncidenceAtVertex() {
		return nextIncidenceAtVertex;
	}

	@Override
	public Incidence getPreviousIncidenceAtEdge() {
		return previousIncidenceAtEdge;
	}

	@Override
	public Incidence getPreviousIncidenceAtVertex() {
		return previousIncidenceAtVertex;
	}

	@Override
	public Iterable<Edge> getTheseEdges() {
		return incidentVertex.getIncidentEdges(direction);
	}

	@Override
	public Iterable<Edge> getThoseEdges() {
		return incidentVertex
				.getIncidentEdges(direction == Direction.EDGE_TO_VERTEX ? Direction.VERTEX_TO_EDGE
						: Direction.EDGE_TO_VERTEX);
	}

	@Override
	public Vertex getThis() {
		if (incidentEdge.isBinaryEdge()) {
			throw new UnsupportedOperationException(
					"This method is only supported by binary Edges.");
		}
		return incidentVertex;
	}

	@Override
	public Iterable<Vertex> getTheseVertices() {
		return incidentEdge.getIncidentVertices(direction);
	}

	@Override
	public Vertex getThat() {
		if (incidentEdge.isBinaryEdge()) {
			throw new UnsupportedOperationException(
					"This method is only supported by binary Edges.");
		}
		return incidentEdge.getAlpha() == this ? incidentEdge.getOmega()
				: incidentEdge.getAlpha();
	}

	@Override
	public Iterable<Vertex> getThoseVertices() {
		return incidentEdge
				.getIncidentVertices(direction == Direction.EDGE_TO_VERTEX ? Direction.VERTEX_TO_EDGE
						: Direction.EDGE_TO_VERTEX);
	}

}
