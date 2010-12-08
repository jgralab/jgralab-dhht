package de.uni_koblenz.jgralab.impl.std;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.IncidenceBaseImpl;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class IncidenceImpl extends IncidenceBaseImpl {

	private VertexImpl incidentVertex;

	private IncidenceImpl nextIncidenceAtVertex;

	private IncidenceImpl previousIncidenceAtVertex;

	private EdgeImpl incidentEdge;

	private IncidenceImpl nextIncidenceAtEdge;

	private IncidenceImpl previousIncidenceAtEdge;

	private Direction direction;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Edge> getThoseEdges() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

}
