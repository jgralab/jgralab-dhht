package de.uni_koblenz.jgralab.impl.std;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.EdgeBaseImpl;
import de.uni_koblenz.jgralab.impl.IncidenceBaseImpl;
import de.uni_koblenz.jgralab.impl.VertexBaseImpl;

/**
 * Implements the interface {@link Incidence}.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class IncidenceImpl extends IncidenceBaseImpl {

	/**
	 * Creates a new instance of IncidenceImpl and appends it to the lambda
	 * sequences of <code>v</code> and <code>e</code>.
	 * 
	 * @param v
	 *            {@link Vertex}
	 * @param e
	 *            {@link Edge}
	 */
	protected IncidenceImpl(VertexImpl v, EdgeImpl e) {
		super(v, e);
		setIncidentEdge(e);
		setIncidentVertex(v);

		// add this incidence to the sequence of incidences of v
		if (v.getFirstIncidence() == null) {
			// v has no incidences
			v.setFirstIncidence(this);
			v.setLastIncidence(this);
		} else {
			((IncidenceImpl) v.getLastIncidence())
					.setNextIncidenceAtVertex(this);
			if (!getGraph().hasSavememSupport()) {
				setPreviousIncidenceAtVertex((IncidenceImpl) v
						.getLastIncidence());
			}
			v.setLastIncidence(this);
		}

		v.incidenceListModified();

		// add this incidence to the sequence of incidences of e
		if (e.getFirstIncidence() == null) {
			// v has no incidences
			e.setFirstIncidence(this);
			e.setLastIncidence(this);
		} else {
			((IncidenceImpl) e.getLastIncidence())
					.setNextIncidenceAtVertex(this);
			if (!getGraph().hasSavememSupport()) {
				setPreviousIncidenceAtVertex((IncidenceImpl) e
						.getLastIncidence());
			}
			e.setLastIncidence(this);
		}

		e.incidenceListModified();
	}

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

	public void setIncidentVertex(VertexImpl incidentVertex) {
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

	@Override
	public void putBeforeAtVertex(Incidence i) {
		assert i != null;
		assert i != this;
		assert getGraph() == i.getGraph();

		Incidence prevIncidence = i.getPreviousIncidenceAtVertex();
		if ((i == this) || (prevIncidence == this)) {
			return;
		}

		assert i.getVertex().getFirstIncidence() != i.getVertex()
				.getLastIncidence();

		// remove this incidence from the sequence of incidences at the vertex
		if (this == getVertex().getFirstIncidence()) {
			((VertexBaseImpl) getVertex())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtVertex());
			if (!getGraph().hasSavememSupport()) {
				((IncidenceImpl) getNextIncidenceAtVertex())
						.setPreviousIncidenceAtVertex(null);
			}
		} else if (this == getVertex().getLastIncidence()) {
			((VertexBaseImpl) getVertex())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtVertex());
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex((IncidenceImpl) getNextIncidenceAtVertex());
			if (!getGraph().hasSavememSupport()) {
				((IncidenceImpl) getNextIncidenceAtVertex())
						.setPreviousIncidenceAtVertex((IncidenceImpl) getPreviousIncidenceAtVertex());
			}
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately before i
		if (i == getVertex().getFirstIncidence()) {
			((VertexBaseImpl) getVertex()).setFirstIncidence(this);
			if (!getGraph().hasSavememSupport()) {
				setPreviousIncidenceAtVertex(null);
			}
		} else {
			IncidenceImpl previousIncidence = (IncidenceImpl) i
					.getPreviousIncidenceAtVertex();
			previousIncidence.setNextIncidenceAtVertex(this);
			if (!getGraph().hasSavememSupport()) {
				setPreviousIncidenceAtVertex(previousIncidence);
			}
		}
		setNextIncidenceAtVertex((IncidenceImpl) i);
		if (!getGraph().hasSavememSupport()) {
			((IncidenceImpl) i).setPreviousIncidenceAtVertex(this);
		}

		((VertexImpl) getVertex()).incidenceListModified();
	}

	@Override
	public void putAfterAtVertex(Incidence i) {
		assert i != null;
		assert i != this;
		assert getGraph() == i.getGraph();

		Incidence nextIncidence = i.getNextIncidenceAtVertex();
		if ((i == this) || (nextIncidence == this)) {
			return;
		}

		assert i.getVertex().getLastIncidence() != i.getVertex()
				.getFirstIncidence();

		// remove this incidence from the sequence of incidences at the vertex
		if (this == getVertex().getFirstIncidence()) {
			((VertexBaseImpl) getVertex())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtVertex());
			if (!getGraph().hasSavememSupport()) {
				((IncidenceImpl) getNextIncidenceAtVertex())
						.setPreviousIncidenceAtVertex(null);
			}
		} else if (this == getVertex().getLastIncidence()) {
			((VertexBaseImpl) getVertex())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtVertex());
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex((IncidenceImpl) getNextIncidenceAtVertex());
			if (!getGraph().hasSavememSupport()) {
				((IncidenceImpl) getNextIncidenceAtVertex())
						.setPreviousIncidenceAtVertex((IncidenceImpl) getPreviousIncidenceAtVertex());
			}
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately after i
		if (i == getVertex().getLastIncidence()) {
			((VertexBaseImpl) getVertex()).setLastIncidence(this);
			setNextIncidenceAtVertex(null);
		} else {
			IncidenceImpl nxtIncidence = (IncidenceImpl) i
					.getNextIncidenceAtVertex();
			setNextIncidenceAtVertex(nxtIncidence);
			if (!getGraph().hasSavememSupport()) {
				nxtIncidence.setPreviousIncidenceAtVertex(this);
			}
		}
		((IncidenceImpl) i).setNextIncidenceAtVertex(this);
		if (!getGraph().hasSavememSupport()) {
			setPreviousIncidenceAtVertex((IncidenceImpl) i);
		}

		((VertexImpl) getVertex()).incidenceListModified();
	}

	@Override
	public void putBeforeAtEdge(Incidence i) {
		assert i != null;
		assert i != this;
		assert getGraph() == i.getGraph();

		Incidence prevIncidence = i.getPreviousIncidenceAtEdge();
		if ((i == this) || (prevIncidence == this)) {
			return;
		}

		assert i.getEdge().getFirstIncidence() != i.getEdge()
				.getLastIncidence();

		// remove this incidence from the sequence of incidences at the vertex
		if (this == getEdge().getFirstIncidence()) {
			((EdgeBaseImpl) getEdge())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtEdge());
			if (!getGraph().hasSavememSupport()) {
				((IncidenceImpl) getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge(null);
			}
		} else if (this == getEdge().getLastIncidence()) {
			((EdgeBaseImpl) getEdge())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtEdge());
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge((IncidenceImpl) getNextIncidenceAtEdge());
			if (!getGraph().hasSavememSupport()) {
				((IncidenceImpl) getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge((IncidenceImpl) getPreviousIncidenceAtEdge());
			}
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately before i
		if (i == getEdge().getFirstIncidence()) {
			((EdgeBaseImpl) getEdge()).setFirstIncidence(this);
			if (!getGraph().hasSavememSupport()) {
				setPreviousIncidenceAtEdge(null);
			}
		} else {
			IncidenceImpl previousIncidence = (IncidenceImpl) i
					.getPreviousIncidenceAtEdge();
			previousIncidence.setNextIncidenceAtEdge(this);
			if (!getGraph().hasSavememSupport()) {
				setPreviousIncidenceAtEdge(previousIncidence);
			}
		}
		setNextIncidenceAtEdge((IncidenceImpl) i);
		if (!getGraph().hasSavememSupport()) {
			((IncidenceImpl) i).setPreviousIncidenceAtEdge(this);
		}

		((EdgeImpl) getEdge()).incidenceListModified();
	}

	@Override
	public void putAfterAtEdge(Incidence i) {
		assert i != null;
		assert i != this;
		assert getGraph() == i.getGraph();

		Incidence nextIncidence = i.getNextIncidenceAtEdge();
		if ((i == this) || (nextIncidence == this)) {
			return;
		}

		assert i.getEdge().getLastIncidence() != i.getEdge()
				.getFirstIncidence();

		// remove this incidence from the sequence of incidences at the vertex
		if (this == getEdge().getFirstIncidence()) {
			((EdgeBaseImpl) getEdge())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtEdge());
			if (!getGraph().hasSavememSupport()) {
				((IncidenceImpl) getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge(null);
			}
		} else if (this == getEdge().getLastIncidence()) {
			((EdgeBaseImpl) getEdge())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtEdge());
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge((IncidenceImpl) getNextIncidenceAtEdge());
			if (!getGraph().hasSavememSupport()) {
				((IncidenceImpl) getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge((IncidenceImpl) getPreviousIncidenceAtEdge());
			}
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately after i
		if (i == getEdge().getLastIncidence()) {
			((EdgeBaseImpl) getEdge()).setLastIncidence(this);
			setNextIncidenceAtEdge(null);
		} else {
			IncidenceImpl nxtIncidence = (IncidenceImpl) i
					.getNextIncidenceAtEdge();
			setNextIncidenceAtEdge(nxtIncidence);
			if (!getGraph().hasSavememSupport()) {
				nxtIncidence.setPreviousIncidenceAtEdge(this);
			}
		}
		((IncidenceImpl) i).setNextIncidenceAtEdge(this);
		if (!getGraph().hasSavememSupport()) {
			setPreviousIncidenceAtEdge((IncidenceImpl) i);
		}

		((EdgeImpl) getEdge()).incidenceListModified();
	}

}
