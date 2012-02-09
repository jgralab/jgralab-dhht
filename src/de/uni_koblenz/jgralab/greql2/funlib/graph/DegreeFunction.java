package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.types.Path;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

public abstract class DegreeFunction extends Function {

	private Direction direction;

	public DegreeFunction(String description, Direction direction) {
		super(description, 10, 1, 1, Category.GRAPH);
		this.direction = direction;
	}

	public Integer evaluate(Vertex v) {
		return v.getDegree(direction);
	}

	public Integer evaluate(Vertex v, TypeCollection c) {
		int degree = 0;
		for (Incidence i = v.getFirstIncidence(); i != null; i=i
				.getNextIncidenceAtVertex()) {
			if (c.acceptsType(i.getEdge().getType())) {
				switch (direction) {
				case BOTH:
					++degree;
					break;
				case VERTEX_TO_EDGE:
				case EDGE_TO_VERTEX:
					if (i.getDirection() == direction) {
						++degree;
					}
					break;
				}
			}
		}
		return degree;
	}
	
	public Integer evaluate(Edge e) {
		return e.getDegree(direction);
	}

	public Integer evaluate(Edge e, TypeCollection c) {
		int degree = 0;
		for (Incidence i = e.getFirstIncidence(); i != null; i=i
				.getNextIncidenceAtEdge()) {
			if (c.acceptsType(i.getVertex().getType())) {
				switch (direction) {
				case BOTH:
					++degree;
					break;
				case VERTEX_TO_EDGE:
				case EDGE_TO_VERTEX:
					if (i.getDirection() != direction) {
						++degree;
					}
					break;
				}
			}
		}
		return degree;
	}

	public Integer evaluate(Vertex v, Path p) {
		return p.degree(v, direction);
	}

}
