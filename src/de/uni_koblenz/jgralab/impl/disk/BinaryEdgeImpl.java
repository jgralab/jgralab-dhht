package de.uni_koblenz.jgralab.impl.disk;

import java.io.IOException;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

public abstract class BinaryEdgeImpl extends EdgeImpl implements BinaryEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8155387019211417812L;

	protected BinaryEdgeImpl(int anId, GraphDatabaseBaseImpl graphDatabase)
			throws IOException {
		super(anId, graphDatabase);
	}

	protected BinaryEdgeImpl(int anId, GraphDatabaseBaseImpl graphDatabase,
			EdgeContainer container) throws IOException {
		super(anId, graphDatabase, container);
	}

	@Override
	public Vertex getAlpha() {
		if (localGraphDatabase.getIncidenceObject(
				container.firstIncidenceId[getIdInStorage(elementId)])
				.getDirection() == Direction.VERTEX_TO_EDGE) {
			return localGraphDatabase.getIncidenceObject(
					container.firstIncidenceId[getIdInStorage(elementId)])
					.getVertex();
		} else {
			return localGraphDatabase.getIncidenceObject(
					container.lastIncidenceId[getIdInStorage(elementId)])
					.getVertex();
		}
	}

	@Override
	public void setAlpha(Vertex vertex) {
		Incidence i = localGraphDatabase
				.getIncidenceObject(container.firstIncidenceId[getIdInStorage(elementId)]);
		if (i.getDirection() != Direction.VERTEX_TO_EDGE) {
			i = localGraphDatabase
					.getIncidenceObject(container.lastIncidenceId[getIdInStorage(elementId)]);
		}
		Vertex v = i.getVertex();
		storingGraphDatabase.connect(i.getType().getId(), v.getGlobalId(),
				elementId);
		storingGraphDatabase.deleteIncidence(i.getGlobalId());
	}

	@Override
	public Vertex getOmega() {
		if (localGraphDatabase.getIncidenceObject(
				container.firstIncidenceId[getIdInStorage(elementId)])
				.getDirection() == Direction.EDGE_TO_VERTEX) {
			return localGraphDatabase.getIncidenceObject(
					container.firstIncidenceId[getIdInStorage(elementId)])
					.getVertex();
		} else {
			return localGraphDatabase.getIncidenceObject(
					container.lastIncidenceId[getIdInStorage(elementId)])
					.getVertex();
		}
	}

	@Override
	public void setOmega(Vertex vertex) {
		Incidence i = localGraphDatabase
				.getIncidenceObject(container.firstIncidenceId[getIdInStorage(elementId)]);
		if (i.getDirection() != Direction.EDGE_TO_VERTEX) {
			i = localGraphDatabase
					.getIncidenceObject(container.lastIncidenceId[getIdInStorage(elementId)]);
		}
		Vertex v = i.getVertex();
		storingGraphDatabase.connect(i.getType().getId(), v.getGlobalId(),
				elementId);
		storingGraphDatabase.deleteIncidence(i.getGlobalId());
	}

	@Override
	public boolean isBinary() {
		return true;
	}
}
