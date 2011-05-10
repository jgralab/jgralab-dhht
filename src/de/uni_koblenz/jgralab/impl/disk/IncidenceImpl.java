/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

package de.uni_koblenz.jgralab.impl.disk;

import java.io.IOException;
import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * Implementation of all methods of the interface {@link Incidence} which are
 * independent of the fields of a specific IncidenceImpl.
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class IncidenceImpl implements Incidence {

	IncidenceContainer storage;
	
	private final Incidence getIncidenceFromBg(int id) {
		return storage.backgroundStorage.getIncidence(id);
	}
	
	private final Vertex getVertexFromBg(int id) {
		return storage.backgroundStorage.getVertex(id);
	}
	
	private final Edge getEdgeFromBg(int id) {
		return storage.backgroundStorage.getEdge(id);
	}
	
	private final int getIdInStorage(int id) {
		return id & BackgroundStorage.CONTAINER_MASK; //BackgroundStorage.getElementIdInStorage(id);
	}
	
	protected IncidenceImpl(int id, IncidenceContainer storage) {
		this.storage = storage;
		this.id = id;
	}
	
	
	/**
	 * Creates a new instance of IncidenceImpl and appends it to the lambda
	 * sequences of <code>v</code> and <code>e</code>.
	 * 
	 * @param id  the id of this incidence 
	 * 
	 * @param v
	 *            {@link Vertex}
	 * @param e
	 *            {@link Edge}
	 * @throws IOException 
	 */
	protected IncidenceImpl(int id, VertexImpl v, EdgeImpl e, Direction dir) throws IOException {
		this.id = id;
		((CompleteGraphImpl) v.getGraph()).addIncidence(this);
		id = getId();
		this.storage = v.storage.backgroundStorage.getIncidenceStorage(id);
		setIncidentEdge(e);
		setIncidentVertex(v);
		setDirection(dir);

		// add this incidence to the sequence of incidences of v
		if (v.getFirstIncidence() == null) {
			// v has no incidences
			v.setFirstIncidence(this);
			v.setLastIncidence(this);
		} else {
			((IncidenceImpl) v.getLastIncidence()).setNextIncidenceAtVertex(this);
			setPreviousIncidenceAtVertex((IncidenceImpl) v.getLastIncidence());
			v.setLastIncidence(this);
		}

		v.incidenceListModified();

		// add this incidence to the sequence of incidences of e
		if (e.getFirstIncidence() == null) {
			// v has no incidences
			e.setFirstIncidence(this);
			e.setLastIncidence(this);
		} else {
			((IncidenceImpl) e.getLastIncidence()).setNextIncidenceAtEdge(this);
			setPreviousIncidenceAtEdge((IncidenceImpl) e.getLastIncidence());
			e.setLastIncidence(this);
		}
//		if (getNextIncidenceAtEdge() != null)
//			throw new RemoteException();
		if (getNextIncidenceAtVertex() != null)
			throw new RuntimeException("id: " + id + " next id:" + getNextIncidenceAtVertex().getId() );
		e.incidenceListModified();
	}


	void setIncidentVertex(VertexImpl incidentVertex) {
		storage.vertexId[getIdInStorage(id)] = incidentVertex.getId();
	}

	void setNextIncidenceAtVertex(IncidenceImpl nextIncidenceAtVertex) {
		storage.nextIncidenceAtVertexId[getIdInStorage(id)] = nextIncidenceAtVertex.getId();
	}

	void setPreviousIncidenceAtVertex(IncidenceImpl previousIncidenceAtVertex) {
		storage.previousIncidenceAtVertexId[getIdInStorage(id)] = previousIncidenceAtVertex.getId();
	}

	void setIncidentEdge(EdgeImpl edgeImpl) {
		storage.edgeId[getIdInStorage(id)] = edgeImpl.getId();
	}

	void setNextIncidenceAtEdge(IncidenceImpl nextIncidenceAtEdge) {
		storage.nextIncidenceAtEdgeId[getIdInStorage(id)] = nextIncidenceAtEdge.getId();
	}

	void setPreviousIncidenceAtEdge(IncidenceImpl previousIncidenceAtEdge) {
		storage.previousIncidenceAtEdgeId[getIdInStorage(id)] = previousIncidenceAtEdge.getId();
	}

	void setDirection(Direction direction) {
		assert direction != null;
		assert direction != Direction.BOTH;
		storage.direction[getIdInStorage(id)] = (direction == Direction.EDGE_TO_VERTEX);
	}

	@Override
	public Direction getDirection() {
		return storage.direction[getIdInStorage(id)] ? Direction.EDGE_TO_VERTEX : Direction.VERTEX_TO_EDGE;
	}

	@Override
	public Graph getGraph() {
		// an Incidence belongs to the same partial graph as the incident edge
		return storage.backgroundStorage.graph;
	}

	@Override
	public Edge getEdge() {
		return getEdgeFromBg(storage.edgeId[getIdInStorage(id)]);
	}

	@Override
	public Vertex getVertex() {
		return getVertexFromBg(storage.vertexId[getIdInStorage(id)]);
	}

	@Override
	public final Incidence getNextIncidenceAtEdge(Graph traversalContext) throws RemoteException {
		Incidence currentIncidence = getIncidenceFromBg(storage.nextIncidenceAtEdgeId[getIdInStorage(id)]);
		while ((traversalContext != null) && (currentIncidence != null) && (!traversalContext.containsVertex(currentIncidence.getVertex())))
			currentIncidence = getIncidenceFromBg(((IncidenceImpl)currentIncidence).storage.nextIncidenceAtEdgeId[getIdInStorage(((IncidenceImpl)currentIncidence).id)]);
		return currentIncidence;
	}
	
	@Override
	public final Incidence getNextIncidenceAtVertex(Graph traversalContext) throws RemoteException {
		Incidence currentIncidence = getIncidenceFromBg(storage.nextIncidenceAtVertexId[getIdInStorage(id)]);
		while ((traversalContext != null) && (currentIncidence != null) && (!traversalContext.containsEdge(currentIncidence.getEdge())))
			currentIncidence = getIncidenceFromBg(((IncidenceImpl)currentIncidence).storage.nextIncidenceAtVertexId[getIdInStorage(((IncidenceImpl)currentIncidence).id)]);
		return currentIncidence;
	}


	@Override
	public Incidence getPreviousIncidenceAtEdge(Graph traversalContext) throws RemoteException {
		Incidence currentIncidence = getIncidenceFromBg(storage.previousIncidenceAtEdgeId[getIdInStorage(id)]);
		while ((traversalContext != null) && (currentIncidence != null) && (!traversalContext.containsVertex(currentIncidence.getVertex())))
			currentIncidence = getIncidenceFromBg(((IncidenceImpl)currentIncidence).storage.previousIncidenceAtEdgeId[getIdInStorage(((IncidenceImpl)currentIncidence).id)]);
		return currentIncidence;
	}

	@Override
	public Incidence getPreviousIncidenceAtVertex(Graph traversalContext) throws RemoteException {
		Incidence currentIncidence = getIncidenceFromBg(storage.previousIncidenceAtVertexId[getIdInStorage(id)]);
		while ((traversalContext != null) && (currentIncidence != null) && (!traversalContext.containsEdge(currentIncidence.getEdge())))
			currentIncidence = getIncidenceFromBg(((IncidenceImpl)currentIncidence).storage.previousIncidenceAtVertexId[getIdInStorage(((IncidenceImpl)currentIncidence).id)]);
		return currentIncidence;
	}

	@Override
	public Iterable<Edge> getTheseEdges(Graph traversalContext) throws RemoteException {
		assert getGraph().getTraversalContext().containsVertex(getVertexFromBg(storage.vertexId[getIdInStorage(id)]));
		return getVertexFromBg(storage.vertexId[getIdInStorage(id)]).getIncidentEdges(traversalContext, getDirection());
	}

	@Override
	public Iterable<Edge> getThoseEdges(Graph traversalContext) throws RemoteException {
		assert getGraph().getTraversalContext().containsVertex(getVertexFromBg(storage.vertexId[getIdInStorage(id)]));
		return getVertexFromBg(storage.vertexId[getIdInStorage(id)]).getIncidentEdges(traversalContext, getDirection().getOppositeDirection());
	}

	@Override
	public Vertex getThis(Graph traversalContext) throws RemoteException {
		if (!getEdgeFromBg(storage.edgeId[getIdInStorage(id)]).isBinary()) {
			throw new UnsupportedOperationException(
					"This method is only supported by binary Edges.");
		} else if (getGraph().getTraversalContext().containsVertex(getVertexFromBg(storage.vertexId[getIdInStorage(id)]))) {
			return getVertexFromBg(storage.vertexId[getIdInStorage(id)]);
		} else {
			return null;
		}
	}

	@Override
	public Iterable<Vertex> getTheseVertices(Graph traversalContext) throws RemoteException {
		assert getGraph().getTraversalContext().containsEdge(getEdgeFromBg(storage.edgeId[getIdInStorage(id)]));
		return getEdgeFromBg(storage.edgeId[getIdInStorage(id)]).getIncidentVertices(traversalContext, getDirection());
	}

	@Override
	public Vertex getThat(Graph traversalContext) throws RemoteException {
		Edge incidentEdge = getEdgeFromBg(storage.edgeId[getIdInStorage(id)]);
		if (!incidentEdge.isBinary()) {
			throw new UnsupportedOperationException(
					"This method is only supported by binary Edges.");
		}
		Vertex vertex = null;
		
		if (getDirection() == Direction.EDGE_TO_VERTEX) {
			vertex = ((BinaryEdge) incidentEdge).getOmega();
		} else {
			vertex = ((BinaryEdge) incidentEdge).getAlpha();
		}
		if (getGraph().getTraversalContext().containsVertex(vertex)) {
			return vertex;
		} else {
			return null;
		}
	}

	@Override
	public Iterable<Vertex> getThoseVertices(Graph traversalContext) throws RemoteException {
		assert getGraph().getTraversalContext().containsEdge(getEdgeFromBg(storage.edgeId[getIdInStorage(id)]));
		return getEdgeFromBg(storage.edgeId[getIdInStorage(id)]).getIncidentVertices(traversalContext, getDirection().getOppositeDirection());
	}

	@Override
	public void putBeforeAtVertex(Incidence i) throws RemoteException {
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
			((VertexImpl) getVertex())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtVertex());
				((IncidenceImpl) getNextIncidenceAtVertex())
						.setPreviousIncidenceAtVertex(null);
		} else if (this == getVertex().getLastIncidence()) {
			((VertexImpl) getVertex())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtVertex());
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex((IncidenceImpl) getNextIncidenceAtVertex());
				((IncidenceImpl) getNextIncidenceAtVertex())
						.setPreviousIncidenceAtVertex((IncidenceImpl) getPreviousIncidenceAtVertex());
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately before i
		if (i == getVertex().getFirstIncidence()) {
			((VertexImpl) getVertex()).setFirstIncidence(this);
			setPreviousIncidenceAtVertex(null);
		} else {
			IncidenceImpl previousIncidence = (IncidenceImpl) i
					.getPreviousIncidenceAtVertex();
			previousIncidence.setNextIncidenceAtVertex(this);
			setPreviousIncidenceAtVertex(previousIncidence);
		}
		setNextIncidenceAtVertex((IncidenceImpl) i);
		((IncidenceImpl) i).setPreviousIncidenceAtVertex(this);

		((VertexImpl) getVertex()).incidenceListModified();
	}

	@Override
	public void putAfterAtVertex(Incidence i) throws RemoteException {
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
			((VertexImpl) getVertex())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtVertex());
				((IncidenceImpl) getNextIncidenceAtVertex())
						.setPreviousIncidenceAtVertex(null);
		} else if (this == getVertex().getLastIncidence()) {
			((VertexImpl) getVertex())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtVertex());
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtVertex())
					.setNextIncidenceAtVertex((IncidenceImpl) getNextIncidenceAtVertex());
				((IncidenceImpl) getNextIncidenceAtVertex())
						.setPreviousIncidenceAtVertex((IncidenceImpl) getPreviousIncidenceAtVertex());
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately after i
		if (i == getVertex().getLastIncidence()) {
			((VertexImpl) getVertex()).setLastIncidence(this);
			setNextIncidenceAtVertex(null);
		} else {
			IncidenceImpl nxtIncidence = (IncidenceImpl) i
					.getNextIncidenceAtVertex();
			setNextIncidenceAtVertex(nxtIncidence);
			nxtIncidence.setPreviousIncidenceAtVertex(this);
		}
		((IncidenceImpl) i).setNextIncidenceAtVertex(this);
		setPreviousIncidenceAtVertex((IncidenceImpl) i);

		((VertexImpl) getVertex()).incidenceListModified();
	}

	@Override
	public void putBeforeAtEdge(Incidence i) throws RemoteException {
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
			((EdgeImpl) getEdge())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtEdge());
			((IncidenceImpl) getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge(null);
		} else if (this == getEdge().getLastIncidence()) {
			((EdgeImpl) getEdge())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtEdge());
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge((IncidenceImpl) getNextIncidenceAtEdge());
			((IncidenceImpl) getNextIncidenceAtEdge())
					.setPreviousIncidenceAtEdge((IncidenceImpl) getPreviousIncidenceAtEdge());
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately before i
		if (i == getEdge().getFirstIncidence()) {
			((EdgeImpl) getEdge()).setFirstIncidence(this);
			setPreviousIncidenceAtEdge(null);
		} else {
			IncidenceImpl previousIncidence = (IncidenceImpl) i
					.getPreviousIncidenceAtEdge();
			previousIncidence.setNextIncidenceAtEdge(this);
			setPreviousIncidenceAtEdge(previousIncidence);
		}
		setNextIncidenceAtEdge((IncidenceImpl) i);
		((IncidenceImpl) i).setPreviousIncidenceAtEdge(this);
		((EdgeImpl) getEdge()).incidenceListModified();
	}

	@Override
	public void putAfterAtEdge(Incidence i) throws RemoteException {
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
			((EdgeImpl) getEdge())
					.setFirstIncidence((IncidenceImpl) getNextIncidenceAtEdge());
			((IncidenceImpl) getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge(null);
		} else if (this == getEdge().getLastIncidence()) {
			((EdgeImpl) getEdge())
					.setLastIncidence((IncidenceImpl) getPreviousIncidenceAtEdge());
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge(null);
		} else {
			((IncidenceImpl) getPreviousIncidenceAtEdge())
					.setNextIncidenceAtEdge((IncidenceImpl) getNextIncidenceAtEdge());
			((IncidenceImpl) getNextIncidenceAtEdge())
						.setPreviousIncidenceAtEdge((IncidenceImpl) getPreviousIncidenceAtEdge());
		}

		// insert moved incidence in the sequence of incidences at the vertex
		// immediately after i
		if (i == getEdge().getLastIncidence()) {
			((EdgeImpl) getEdge()).setLastIncidence(this);
			setNextIncidenceAtEdge(null);
		} else {
			IncidenceImpl nxtIncidence = (IncidenceImpl) i
					.getNextIncidenceAtEdge();
			setNextIncidenceAtEdge(nxtIncidence);
			nxtIncidence.setPreviousIncidenceAtEdge(this);
		}
		((IncidenceImpl) i).setNextIncidenceAtEdge(this);
		setPreviousIncidenceAtEdge((IncidenceImpl) i);

		((EdgeImpl) getEdge()).incidenceListModified();
	}
	
	

	
	protected int id;
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public int compareTo(Incidence i) {
		try {
			assert getGraph() == i.getGraph();
			return getId() - i.getId();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public Incidence getNextIncidenceAtEdge() throws RemoteException {
		if (getGraph().getTraversalContext() == null) {
			return getIncidenceFromBg(storage.nextIncidenceAtEdgeId[getIdInStorage(id)]);
		} else {
			return getNextIncidenceAtEdge(getGraph().getTraversalContext());
		}	
	}

	@Override
	public final Incidence getNextIncidenceAtEdge(Direction direction) throws RemoteException {
		if (getGraph().getTraversalContext() == null) {
			Incidence i = getIncidenceFromBg(storage.nextIncidenceAtEdgeId[getIdInStorage(id)]);
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null) && (direction != i.getDirection()))
					i = getIncidenceFromBg(((IncidenceImpl)i).storage.nextIncidenceAtEdgeId[getIdInStorage(((IncidenceImpl)i).id)]);;	
			} 
			return i;
		} else {
			return getNextIncidenceAtEdge(getGraph().getTraversalContext(),	direction);
		}	
	}
	

	@Override
	public Incidence getNextIncidenceAtEdge(boolean thisIncidence,
			IncidenceType... incidenceTypes) throws RemoteException {
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				thisIncidence, incidenceTypes);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			Direction direction) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, direction);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, Direction direction) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, direction);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, direction, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, direction, noSubclasses);
	}

//	@Override
//	public Incidence getNextIncidenceAtEdge(Graph traversalContext,	Direction direction) throws RemoteException {
//		Incidence i = getNextIncidenceAtEdge(traversalContext);
//		while ((i != null) && (direction != null) && (direction != Direction.BOTH) && (direction != i.getDirection()))
//			i = i.getNextIncidenceAtEdge(traversalContext);
//		return i;
//	}


	@Override
	public final Incidence getNextIncidenceAtEdge(Graph traversalContext, Direction direction) throws RemoteException {
		Incidence i = getIncidenceFromBg(storage.nextIncidenceAtEdgeId[getIdInStorage(id)]);
		if (traversalContext==null) {
			while (((i != null) && (direction != null) && (direction != Direction.BOTH) && (direction != i.getDirection()))) { 
					i = getIncidenceFromBg(((IncidenceImpl)i).storage.nextIncidenceAtEdgeId[getIdInStorage(((IncidenceImpl)i).id)]);
			}		
		} else {
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null) && ((!traversalContext.containsVertex(i.getVertex())) || (direction != i.getDirection())))
					i = getIncidenceFromBg(((IncidenceImpl)i).storage.nextIncidenceAtEdgeId[getIdInStorage(((IncidenceImpl)i).id)]);
			} else {
				while ((i != null) && (!traversalContext.containsVertex(i.getVertex())))
					i = getIncidenceFromBg(((IncidenceImpl)i).storage.nextIncidenceAtEdgeId[getIdInStorage(((IncidenceImpl)i).id)]);
			}
			
		}
		return i;
	}
	
	
	

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			boolean thisIncidence, IncidenceType... incidenceTypes) throws RemoteException {
		Incidence i = getNextIncidenceAtEdge(traversalContext);
		if (incidenceTypes.length == 0) {
			return i;
		}
		while (i != null) {
			for (IncidenceType element : incidenceTypes) {
				if ((thisIncidence ? i.getThisSemantics() : i
						.getThatSemantics()) == element) {
					return i;
				}
			}
			i = i.getNextIncidenceAtEdge(traversalContext);
		}
		return null;
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, null,
				false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, null,
				false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, null,
				noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, null,
				noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction,
			boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext,
				anIncidenceClass.getM1Class(), direction, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		Incidence currentIncidence = getNextIncidenceAtEdge(traversalContext,
				direction);
		while (currentIncidence != null) {
			if (noSubclasses) {
				if (anIncidenceClass == currentIncidence.getM1Class()) {
					return currentIncidence;
				}
			} else {
				if (anIncidenceClass.isInstance(currentIncidence)) {
					return currentIncidence;
				}
			}
			currentIncidence = currentIncidence.getNextIncidenceAtEdge(
					traversalContext, direction);
		}
		return null;
	}

	@Override
	public Incidence getPreviousIncidenceAtEdge() throws RemoteException {
		return getPreviousIncidenceAtEdge(getGraph().getTraversalContext());
	}

	@Override
	public Incidence getNextIncidenceAtVertex() throws RemoteException {
		return getNextIncidenceAtVertex(getGraph().getTraversalContext());
	}

	@Override
	public final Incidence getNextIncidenceAtVertex(Direction direction) throws RemoteException {
		if (getGraph().getTraversalContext() == null) {
			Incidence i = getIncidenceFromBg(storage.nextIncidenceAtVertexId[getIdInStorage(id)]);
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null) && (direction != i.getDirection())) {
					i = getIncidenceFromBg(((IncidenceImpl)i).storage.nextIncidenceAtVertexId[getIdInStorage(((IncidenceImpl)i).id)]);
				}	
			} 
			return i;
		} else {
			return getNextIncidenceAtVertex(getGraph().getTraversalContext(),	direction);
		}	
	}
	
	

	@Override
	public Incidence getNextIncidenceAtVertex(boolean thisIncidence,
			IncidenceType... incidenceTypes) throws RemoteException {
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				thisIncidence, incidenceTypes);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			Direction direction) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, direction);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, Direction direction) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, direction);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, direction, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, direction, noSubclasses);
	}

	

	@Override
	public final Incidence getNextIncidenceAtVertex(Graph traversalContext, Direction direction) throws RemoteException {
		Incidence i = getIncidenceFromBg(storage.nextIncidenceAtVertexId[getIdInStorage(id)]);
		if (traversalContext==null) {
			while (((i != null) && (direction != null) && (direction != Direction.BOTH) && (direction != i.getDirection()))) { 
					i = getIncidenceFromBg(((IncidenceImpl)i).storage.nextIncidenceAtVertexId[getIdInStorage(((IncidenceImpl)i).id)]);
			}		
		} else {
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null) && ((!traversalContext.containsEdge(i.getEdge())) || (direction != i.getDirection())))
					i = getIncidenceFromBg(((IncidenceImpl)i).storage.nextIncidenceAtVertexId[getIdInStorage(((IncidenceImpl)i).id)]);
			} else {
				while ((i != null) && (!traversalContext.containsEdge(i.getEdge())))
					i = getIncidenceFromBg(((IncidenceImpl)i).storage.nextIncidenceAtVertexId[getIdInStorage(((IncidenceImpl)i).id)]);
			}
			
		}
		return i;
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			boolean thisIncidence, IncidenceType... incidenceTypes) throws RemoteException {
		Incidence i = getNextIncidenceAtVertex(traversalContext);
		if (incidenceTypes.length == 0) {
			return i;
		}
		while (i != null) {
			for (IncidenceType element : incidenceTypes) {
				if ((thisIncidence ? i.getThisSemantics() : i
						.getThatSemantics()) == element) {
					return i;
				}
			}
			i = i.getNextIncidenceAtVertex(traversalContext);
		}
		return null;
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				null, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				null, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				null, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				null, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction,
			boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext,
				anIncidenceClass.getM1Class(), direction, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) throws RemoteException {
		assert anIncidenceClass != null;
		Incidence currentIncidence = getNextIncidenceAtVertex(traversalContext,
				direction);
		while (currentIncidence != null) {
			if (noSubclasses) {
				if (anIncidenceClass == currentIncidence.getM1Class()) {
					return currentIncidence;
				}
			} else {
				if (anIncidenceClass.isInstance(currentIncidence)) {
					return currentIncidence;
				}
			}
			currentIncidence = currentIncidence.getNextIncidenceAtVertex(
					traversalContext, direction);
		}
		return null;
	}

	@Override
	public Incidence getPreviousIncidenceAtVertex() throws RemoteException {
		return getPreviousIncidenceAtVertex(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Edge> getThoseEdges() throws RemoteException {
		return getThoseEdges(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Edge> getTheseEdges() throws RemoteException {
		return getTheseEdges(getGraph().getTraversalContext());
	}

	@Override
	public Vertex getThis() throws RemoteException {
		return getThis(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Vertex> getTheseVertices() throws RemoteException {
		return getTheseVertices(getGraph().getTraversalContext());
	}

	@Override
	public Vertex getThat() throws RemoteException {
		return getThat(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Vertex> getThoseVertices() throws RemoteException {
		return getThoseVertices(getGraph().getTraversalContext());
	}

	@Override
	public boolean isBeforeAtVertex(Incidence i) throws RemoteException {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence prev = i.getPreviousIncidenceAtVertex();
		while ((prev != null) && (prev != this)) {
			prev = i.getPreviousIncidenceAtVertex();
		}
		return prev != null;
	}

	@Override
	public boolean isAfterAtVertex(Incidence i) throws RemoteException {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence next = i.getNextIncidenceAtVertex();
		while ((next != null) && (next != this)) {
			next = i.getNextIncidenceAtVertex();
		}
		return next != null;
	}

	@Override
	public boolean isBeforeAtEdge(Incidence i) throws RemoteException {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence prev = i.getPreviousIncidenceAtEdge();
		while ((prev != null) && (prev != this)) {
			prev = i.getPreviousIncidenceAtEdge();
		}
		return prev != null;
	}

	@Override
	public boolean isAfterAtEdge(Incidence i) throws RemoteException {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence next = i.getNextIncidenceAtEdge();
		while ((next != null) && (next != this)) {
			next = i.getNextIncidenceAtEdge();
		}
		return next != null;
	}

	@Override
	public GraphClass getGraphClass() throws RemoteException {
		return getVertex().getGraphClass();
	}

	@Override
	public Schema getSchema() throws RemoteException {
		return getVertex().getSchema();
	}

	@Override
	public IncidenceType getThisSemantics() throws RemoteException {
		if (getEdge().isBinary()) {
			return this.getType().getIncidenceType();
		} else {
			throw new UnsupportedOperationException(
					"getThisSemantics() may be called only for incidences of binary edges");
		}
	}

	@Override
	public IncidenceType getThatSemantics() throws RemoteException {
		if (getEdge().isBinary()) {
			return getThatIncidence().getType().getIncidenceType();
		} else {
			throw new UnsupportedOperationException(
					"getThatSemantics() may be called only for incidences of binary edges");
		}
	}

	public Incidence getThatIncidence() throws RemoteException {
		if (getEdge().isBinary()) {
			return getEdge().getFirstIncidence() == this ? getEdge()
					.getFirstIncidence() : getEdge().getLastIncidence();
		} else {
			throw new UnsupportedOperationException(
					"getThatIncidence() may be called only for incidences of binary edges");
		}
	}

	public void setId(int iId) {
		this.id = iId;
//		try {
//		//	storage = storage.backgroundStorage.getIncidenceStorage(iId);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}


	@Override
	public Graph getLocalGraph() throws RemoteException {
		return getEdge().getLocalGraph();
	}
	
}
