package de.uni_koblenz.jgralab.impl.std;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;

public abstract class BinaryEdgeImpl extends EdgeImpl implements BinaryEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8155387019211417812L;

	protected BinaryEdgeImpl(int anId, Graph graph) throws RemoteException {
		super(anId, graph);
	}
	

	@Override
	public Vertex getAlpha() {
		if (firstIncidenceAtEdge.getDirection() == Direction.VERTEX_TO_EDGE) {
			return firstIncidenceAtEdge.getVertex();
		} else {
			return lastIncidenceAtEdge.getVertex();
		}
	}

	@Override
	public Vertex getOmega() {
		if (firstIncidenceAtEdge.getDirection() == Direction.EDGE_TO_VERTEX) {
			return firstIncidenceAtEdge.getVertex();
		} else {
			return lastIncidenceAtEdge.getVertex();
		}
	}

	@Override
	public boolean isBinary() {
		return true;
	}
}
