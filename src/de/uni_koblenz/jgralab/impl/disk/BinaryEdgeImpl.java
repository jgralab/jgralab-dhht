package de.uni_koblenz.jgralab.impl.disk;

import java.io.IOException;
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

	protected BinaryEdgeImpl(int anId, Graph graph) throws IOException {
		super(anId, graph);
	}
	
	protected BinaryEdgeImpl(int anId, de.uni_koblenz.jgralab.impl.disk.EdgeContainer storage, Graph graph) throws IOException {
		super(anId, storage, graph);
	}
	

	@Override
	public Vertex getAlpha() throws RemoteException {
		if (storage.backgroundStorage.getIncidenceObject(storage.firstIncidenceId[id]).getDirection() == Direction.VERTEX_TO_EDGE) {
			return storage.backgroundStorage.getIncidenceObject(storage.firstIncidenceId[id]).getVertex();
		} else {
			return storage.backgroundStorage.getIncidenceObject(storage.lastIncidenceId[id]).getVertex();
		}
	}

	@Override
	public Vertex getOmega() throws RemoteException {
		if (storage.backgroundStorage.getIncidenceObject(storage.firstIncidenceId[id]).getDirection() == Direction.EDGE_TO_VERTEX) {
			return storage.backgroundStorage.getIncidenceObject(storage.firstIncidenceId[id]).getVertex();
		} else {
			return storage.backgroundStorage.getIncidenceObject(storage.lastIncidenceId[id]).getVertex();
		}
	}

	@Override
	public boolean isBinary() {
		return true;
	}
}
