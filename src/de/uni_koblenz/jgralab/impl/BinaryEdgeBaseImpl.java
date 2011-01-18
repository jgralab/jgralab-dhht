package de.uni_koblenz.jgralab.impl;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

public abstract class BinaryEdgeBaseImpl extends EdgeBaseImpl implements BinaryEdge {

	protected BinaryEdgeBaseImpl(int anId, Graph graph) {
		super(anId, graph);
	}

	
	public Vertex getAlpha() {
		Incidence inc = getFirstIncidence(Direction.VERTEX_TO_EDGE);
		return inc.getVertex();
	}
	
	public Vertex getOmega() {
		Incidence inc = getFirstIncidence(Direction.EDGE_TO_VERTEX);
		return inc.getVertex();
	}

	public void setAlpha(Vertex alpha) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	

	public void setOmega(Vertex alpha) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}


}
