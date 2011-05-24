package de.uni_koblenz.jgralab.impl.disk;

import java.io.IOException;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Vertex;

public abstract class BinaryEdgeImpl extends EdgeImpl implements BinaryEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8155387019211417812L;

	protected BinaryEdgeImpl(int anId, GraphDatabase graphDatabase) throws IOException {
		super(anId, graphDatabase);
	}
	
	protected BinaryEdgeImpl(int anId, GraphDatabase graphDatabase, EdgeContainer container) throws IOException {
		super(anId, graphDatabase, container);
	}
	

	@Override
	public Vertex getAlpha() {
		if (localGraphDatabase.getIncidenceObject(container.firstIncidenceId[getIdInStorage(elementId)]).getDirection() == Direction.VERTEX_TO_EDGE) {
			return localGraphDatabase.getIncidenceObject(container.firstIncidenceId[getIdInStorage(elementId)]).getVertex();
		} else {
			return localGraphDatabase.getIncidenceObject(container.lastIncidenceId[getIdInStorage(elementId)]).getVertex();
		}
	}

	@Override
	public Vertex getOmega() {
		if (localGraphDatabase.getIncidenceObject(container.firstIncidenceId[getIdInStorage(elementId)]).getDirection() == Direction.EDGE_TO_VERTEX) {
			return localGraphDatabase.getIncidenceObject(container.firstIncidenceId[getIdInStorage(elementId)]).getVertex();
		} else {
			return localGraphDatabase.getIncidenceObject(container.lastIncidenceId[getIdInStorage(elementId)]).getVertex();
		}
	}

	@Override
	public boolean isBinary() {
		return true;
	}
}
