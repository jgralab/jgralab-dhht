package de.uni_koblenz.jgralab.greql2.types;

import java.util.HashSet;

import org.pcollections.PVector;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;

@SuppressWarnings("rawtypes")
public class HyperPath {

	private PVector<GraphElement> elements;
	private PVector<Incidence> incidences;

	private HyperPath(PVector<GraphElement> es, PVector<Incidence> is) {
		this.elements = es;
		this.incidences = is;
	}

	public static HyperPath start(GraphElement e) {
		if (e == null || !e.isValid()) {
			throw new IllegalArgumentException(
					"The element must be != null and valid");
		}
		PVector<GraphElement> es = JGraLab.vector();
		PVector<Incidence> is = JGraLab.vector();
		return new HyperPath(es.plus(e), is);
	}

	public HyperPath reverse() {
		PVector<GraphElement> es = JGraLab.vector();
		PVector<Incidence> is = JGraLab.vector();
		for (int i = es.size() - 1; i >= 0; --i) {
			es = es.plus(elements.get(i));
		}
		for (int i = is.size() - 1; i >= 0; --i) {
			// TODO jtheegarten Create new incidence with reversed direction
			is = is.plus(incidences.get(i));
		}
		return new HyperPath(es, is);
	}

	public HyperPath append(Incidence i) {
		if (i.getThis() != getEndElement()) {
			throw new IllegalArgumentException("Can't append " + i
					+ " to this Path (e.getThis() !=" + getEndElement() + ")");
		}
		return new HyperPath(elements.plus(i.getThat()), incidences.plus(i));
	}

	public GraphElement getStartElement() {
		return elements.get(0);
	}

	public GraphElement getEndElement() {
		return elements.get(elements.size() - 1);
	}

	public int getLength() {
		return incidences.size();
	}

	public GraphElement getElementAt(int i) {
		return elements.get(i);
	}

	public Incidence getIncidenceAt(int i) {
		return incidences.get(i);
	}

	public boolean isTrail() {
		HashSet<GraphElement> h = new HashSet<GraphElement>();
		h.add(getStartElement());
		for (Incidence i : incidences) {
			if (h.contains(i.getThat())) {
				return false;
			}
			h.add(i.getThat());
		}
		return true;
	}

	public PVector<Incidence> getIncidenceTrace() {
		return incidences;
	}

	public PVector<GraphElement> getElementTrace() {
		return elements;
	}

	public PVector<Edge> getEdgeTrace() {
		PVector<Edge> resultVector = JGraLab.vector();
		for (GraphElement<?, ?, ?, ?> elem : elements) {
			if (elem instanceof Edge) {
				resultVector = resultVector.plus((Edge) elem);
			}
		}
		return resultVector;
	}

	public PVector<Vertex> getVertexTrace() {
		PVector<Vertex> resultVector = JGraLab.vector();
		for (GraphElement<?, ?, ?, ?> elem : elements) {
			if (elem instanceof Vertex) {
				resultVector = resultVector.plus((Vertex) elem);
			}
		}
		return resultVector;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof HyperPath)) {
			return false;
		}
		HyperPath p = (HyperPath) o;
		return elements.equals(p.elements) && incidences.equals(p.incidences);
	}

	@Override
	public int hashCode() {
		return elements.hashCode() + incidences.hashCode();
	}

	public int degree(GraphElement element, Direction dir) {
		int degree = 0;
		switch (dir) {
		case EDGE_TO_VERTEX:
			for (Incidence i : incidences) {
				if (i.getVertex() == element) {
					degree++;
				}
			}
			return degree;
		case VERTEX_TO_EDGE:
			for (Incidence i : incidences) {
				if (i.getEdge() == element) {
					degree++;
				}
			}
			return degree;
		case BOTH:
			for (Incidence i : incidences) {
				if (i.getVertex() == element) {
					degree++;
				} else if (i.getEdge() == element) {
					degree++;
				}
			}
			return degree;
		default:
			throw new RuntimeException("FIXME: Unhandled Direction " + dir);
		}
	}

	public boolean contains(GraphElement el) {
		return elements.contains(el);
	}

	public boolean containsVertex(Vertex v) {
		return elements.contains(v);
	}

	public boolean containsEdge(Edge e) {
		return elements.contains(e);
	}

	public boolean containsIncidence(Incidence i) {
		return incidences.contains(i);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Path: ");
		if (elements.isEmpty()) {
			sb.append("empty");
		} else {
			sb.append(elements.get(0));
			for (Incidence i : incidences) {
				sb.append(" ").append(i).append(" ").append(i.getThat());
			}
		}
		return sb.toString();
	}
}
