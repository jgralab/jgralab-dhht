package de.uni_koblenz.jgralab.impl.memdistributed;

import java.io.IOException;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

public abstract class BinaryEdgeImpl extends EdgeImpl implements BinaryEdge {


	protected BinaryEdgeImpl(long anId, GraphDatabaseBaseImpl graphDatabase)
			throws IOException {
		super(anId, graphDatabase);
	}

	@Override
	public Vertex getAlpha() {
		Incidence firstIncidence = getFirstIncidence((Graph) null);
		if (firstIncidence.getDirection() == Direction.VERTEX_TO_EDGE) {
			return firstIncidence.getVertex();
		} else {
			return firstIncidence.getNextIncidenceAtEdge((Graph)null).getVertex();
		}
	}

	@Override
	public void setAlpha(Vertex vertex) {
		Incidence incidence = getFirstIncidence((Graph) null);
		if (incidence.getDirection() == Direction.VERTEX_TO_EDGE)
			incidence = incidence.getNextIncidenceAtEdge((Graph)null);
		((VertexImpl) incidence.getVertex()).removeIncidenceFromLambdaSeq((IncidenceImpl) incidence);
		((IncidenceImpl) incidence).setIncidentVertex((VertexImpl) vertex);
		((VertexImpl) vertex).appendIncidenceToLambdaSeq((IncidenceImpl) incidence);
	}

	@Override
	public Vertex getOmega() {
		Incidence firstIncidence = getFirstIncidence((Graph) null);
		if (firstIncidence.getDirection() == Direction.EDGE_TO_VERTEX) {
			return firstIncidence.getVertex();
		} else {
			return firstIncidence.getNextIncidenceAtEdge((Graph)null).getVertex();
		}
	}

	@Override
	public void setOmega(Vertex vertex) {
		Incidence incidence = getFirstIncidence((Graph) null);
		if (incidence.getDirection() == Direction.EDGE_TO_VERTEX)
			incidence = incidence.getNextIncidenceAtEdge((Graph)null);
		((VertexImpl) incidence.getVertex()).removeIncidenceFromLambdaSeq((IncidenceImpl) incidence);
		((IncidenceImpl) incidence).setIncidentVertex((VertexImpl) vertex);
		((VertexImpl) vertex).appendIncidenceToLambdaSeq((IncidenceImpl) incidence);
	}

	@Override
	public boolean isBinary() {
		return true;
	}
}
