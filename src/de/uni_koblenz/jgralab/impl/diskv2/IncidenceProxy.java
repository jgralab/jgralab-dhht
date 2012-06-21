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

package de.uni_koblenz.jgralab.impl.diskv2;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.RemoteGraphDatabaseAccess;
import de.uni_koblenz.jgralab.impl.RemoteStorageAccess;
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
public abstract class IncidenceProxy implements Incidence {

	private final GraphDatabaseBaseImpl localGraphDatabase;

	private final RemoteGraphDatabaseAccess storingGraphDatabase;

	private final long id;

	protected IncidenceProxy(long globalId,
			GraphDatabaseBaseImpl localGraphDatabase,
			RemoteGraphDatabaseAccess storingGraphDatabase) {
		this.localGraphDatabase = localGraphDatabase;
		this.storingGraphDatabase = storingGraphDatabase;
		this.id = globalId;
	}


	void setNextIncidenceAtVertex(Incidence nextIncidenceAtVertex) {
		((GraphDatabaseBaseImpl) storingGraphDatabase)
				.setNextIncidenceIdAtVertex(id,
						nextIncidenceAtVertex.getGlobalId());
	}

	void setPreviousIncidenceAtVertex(IncidenceProxy previousIncidenceAtVertex) {
		((GraphDatabaseBaseImpl) storingGraphDatabase)
				.setPreviousIncidenceIdAtVertex(id,
						previousIncidenceAtVertex.getGlobalId());
	}

	void setNextIncidenceAtEdge(IncidenceProxy nextIncidenceAtEdge) {
		((GraphDatabaseBaseImpl) storingGraphDatabase)
				.setNextIncidenceIdAtEdge(id,
						nextIncidenceAtEdge.getGlobalId());
	}

	void setPreviousIncidenceAtEdge(IncidenceProxy previousIncidenceAtEdge) {
		((GraphDatabaseBaseImpl) storingGraphDatabase)
				.setPreviousIncidenceIdAtEdge(id,
						previousIncidenceAtEdge.getGlobalId());
	}

	@Override
	public Graph getGraph() {
		// an Incidence belongs to the same partial graph as the incident edge
		return getEdge().getGraph();
	}

	@Override
	public Edge getEdge() {
		try {
			return localGraphDatabase.getEdgeObject(storingGraphDatabase
					.getEdgeIdAtIncidenceId(id));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Vertex getVertex() {
		try {
			return localGraphDatabase.getVertexObject(storingGraphDatabase
					.getVertexIdAtIncidenceId(id));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public final Incidence getNextIncidenceAtEdge(Graph traversalContext) {
		Incidence currentIncidence;
		try {
			currentIncidence = localGraphDatabase
					.getIncidenceObject(storingGraphDatabase
							.getNextIncidenceIdAtEdgeId(storingGraphDatabase
									.getEdgeIdAtIncidenceId(id)));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		while ((traversalContext != null)
				&& (currentIncidence != null)
				&& (!traversalContext.containsVertex(currentIncidence
						.getVertex()))) {
			currentIncidence = currentIncidence.getNextIncidenceAtEdge();
		}
		return currentIncidence;
	}

	@Override
	public final Incidence getNextIncidenceAtVertex(Graph traversalContext) {
		Incidence currentIncidence;
		try {
			long nextIncId = storingGraphDatabase
					.getNextIncidenceIdAtVertexId(id);
			currentIncidence = localGraphDatabase.getIncidenceObject(nextIncId);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		while ((traversalContext != null) && (currentIncidence != null)
				&& (!traversalContext.containsEdge(currentIncidence.getEdge()))) {
			currentIncidence = currentIncidence.getNextIncidenceAtVertex();
		}
		return currentIncidence;
	}

	@Override
	public Incidence getPreviousIncidenceAtEdge(Graph traversalContext) {
		Incidence currentIncidence;
		try {
			currentIncidence = localGraphDatabase
					.getIncidenceObject(storingGraphDatabase
							.getPreviousIncidenceIdAtEdgeId(storingGraphDatabase
									.getEdgeIdAtIncidenceId(id)));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		while ((traversalContext != null)
				&& (currentIncidence != null)
				&& (!traversalContext.containsVertex(currentIncidence
						.getVertex()))) {
			currentIncidence = currentIncidence.getPreviousIncidenceAtEdge();
		}
		return currentIncidence;
	}

	@Override
	public Incidence getPreviousIncidenceAtVertex(Graph traversalContext) {
		Incidence currentIncidence;
		try {
			currentIncidence = localGraphDatabase
					.getIncidenceObject(storingGraphDatabase
							.getPreviousIncidenceIdAtVertexId(storingGraphDatabase
									.getVertexIdAtIncidenceId(id)));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		while ((traversalContext != null) && (currentIncidence != null)
				&& (!traversalContext.containsEdge(currentIncidence.getEdge()))) {
			currentIncidence = currentIncidence.getPreviousIncidenceAtVertex();
		}
		return currentIncidence;
	}

	@Override
	public Iterable<Edge> getTheseEdges(Graph traversalContext) {
		Vertex vertex;
		try {
			vertex = localGraphDatabase.getVertexObject(storingGraphDatabase
					.getVertexIdAtIncidenceId(id));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		assert getGraph().getTraversalContext() == null
				|| getGraph().getTraversalContext().containsVertex(vertex);
		return vertex.getIncidentEdges(traversalContext, getDirection());
	}

	@Override
	public Iterable<Edge> getThoseEdges(Graph traversalContext) {
		Vertex vertex;
		try {
			vertex = localGraphDatabase.getVertexObject(storingGraphDatabase
					.getVertexIdAtIncidenceId(id));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		assert getGraph().getTraversalContext() == null
				|| getGraph().getTraversalContext().containsVertex(vertex);
		return vertex.getIncidentEdges(traversalContext, getDirection()
				.getOppositeDirection());
	}

	@Override
	public Vertex getThis(Graph traversalContext) {
		try {
			if (!localGraphDatabase.getEdgeObject(
					storingGraphDatabase.getEdgeIdAtIncidenceId(id)).isBinary()) {
				throw new UnsupportedOperationException(
						"This method is only supported by binary Edges.");
			} else {
				Vertex vertex = localGraphDatabase
						.getVertexObject(storingGraphDatabase
								.getVertexIdAtIncidenceId(id));
				if (getGraph().getTraversalContext() == null
						|| getGraph().getTraversalContext().containsVertex(
								vertex)) {
					return vertex;
				} else {
					return null;
				}
			}
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Iterable<Vertex> getTheseVertices(Graph traversalContext) {
		Edge edge;
		try {
			edge = localGraphDatabase.getEdgeObject(storingGraphDatabase
					.getEdgeIdAtIncidenceId(id));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		assert getGraph().getTraversalContext() == null
				|| getGraph().getTraversalContext().containsEdge(edge);
		return edge.getIncidentVertices(traversalContext, getDirection());
	}

	@Override
	public Vertex getThat(Graph traversalContext) {
		Edge incidentEdge;
		try {
			incidentEdge = localGraphDatabase
					.getEdgeObject(storingGraphDatabase
							.getEdgeIdAtIncidenceId(id));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		if (!incidentEdge.isBinary()) {
			throw new UnsupportedOperationException(
					"This method is only supported by binary Edges.");
		}
		Vertex vertex = null;

		if (getDirection() == Direction.VERTEX_TO_EDGE) {
			vertex = ((BinaryEdge) incidentEdge).getOmega();
		} else {
			vertex = ((BinaryEdge) incidentEdge).getAlpha();
		}
		if (getGraph().getTraversalContext() == null
				|| getGraph().getTraversalContext().containsVertex(vertex)) {
			return vertex;
		} else {
			return null;
		}
	}

	@Override
	public Iterable<Vertex> getThoseVertices(Graph traversalContext) {
		Edge edge;
		try {
			edge = localGraphDatabase.getEdgeObject(storingGraphDatabase
					.getEdgeIdAtIncidenceId(id));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		assert getGraph().getTraversalContext() == null
				|| getGraph().getTraversalContext().containsEdge(edge);
		return edge.getIncidentVertices(traversalContext, getDirection()
				.getOppositeDirection());
	}

	@Override
	public void putAfterAtVertex(Incidence i) {
		try {
			storingGraphDatabase.putIncidenceIdAfterAtVertexId(id,
					i.getGlobalId());
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void putBeforeAtVertex(Incidence i) {
		try {
			storingGraphDatabase.putIncidenceIdBeforeAtVertexId(id,
					i.getGlobalId());
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void putAfterAtEdge(Incidence i) {
		try {
			storingGraphDatabase.putIncidenceIdAfterAtEdgeId(id,
					i.getGlobalId());
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void putBeforeAtEdge(Incidence i) {
		try {
			storingGraphDatabase.putIncidenceIdBeforeAtEdgeId(id,
					i.getGlobalId());
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long getGlobalId() {
		return id;
	}

	@Override
	public int getLocalId() {
		return (int) id;
	}

	@Override
	public int compareTo(Incidence i) {
		assert getGraph() == i.getGraph();
		return (int) (getGlobalId() - i.getGlobalId());
	}

	@Override
	public Incidence getNextIncidenceAtEdge() {
		if (getGraph().getTraversalContext() == null) {
			try {
				return localGraphDatabase
						.getIncidenceObject(storingGraphDatabase
								.getNextIncidenceIdAtEdgeId(id));
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
		} else {
			return getNextIncidenceAtEdge(getGraph().getTraversalContext());
		}
	}

	@Override
	public final Incidence getNextIncidenceAtEdge(Direction direction) {
		if (getGraph().getTraversalContext() == null) {
			Incidence i;
			try {
				i = localGraphDatabase.getIncidenceObject(storingGraphDatabase
						.getNextIncidenceIdAtEdgeId(id));
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null) && (direction != i.getDirection())) {
					i.getNextIncidenceAtEdge();
				}
			}
			return i;
		} else {
			return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
					direction);
		}
	}

	@Override
	public Incidence getNextIncidenceAtEdge(boolean thisIncidence,
			IncidenceType... incidenceTypes) {
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				thisIncidence, incidenceTypes);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}


	// @Override
	// public Incidence getNextIncidenceAtEdge(Graph traversalContext, Direction
	// direction) {
	// Incidence i = getNextIncidenceAtEdge(traversalContext);
	// while ((i != null) && (direction != null) && (direction !=
	// Direction.BOTH) && (direction != i.getDirection()))
	// i = i.getNextIncidenceAtEdge(traversalContext);
	// return i;
	// }

	@Override
	public final Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Direction direction) {
		Incidence i;
		try {
			i = localGraphDatabase.getIncidenceObject(storingGraphDatabase
					.getNextIncidenceIdAtEdgeId(id));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		if (traversalContext == null) {
			while (((i != null) && (direction != null)
					&& (direction != Direction.BOTH) && (direction != i
					.getDirection()))) {
				i = i.getNextIncidenceAtEdge();
			}
		} else {
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null)
						&& ((!traversalContext.containsVertex(i.getVertex())) || (direction != i
								.getDirection()))) {
					i = i.getNextIncidenceAtEdge();
				}
			} else {
				while ((i != null)
						&& (!traversalContext.containsVertex(i.getVertex()))) {
					i = i.getNextIncidenceAtEdge();
				}
			}

		}
		return i;
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			boolean thisIncidence, IncidenceType... incidenceTypes) {
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
			IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, false);
	}



	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(traversalContext, anIncidenceClass, 
				noSubclasses);
	}



	@Override
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		Incidence currentIncidence = getNextIncidenceAtEdge(traversalContext);
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
					traversalContext);
		}
		return null;
	}

	@Override
	public Incidence getPreviousIncidenceAtEdge() {
		return getPreviousIncidenceAtEdge(getGraph().getTraversalContext());
	}

	@Override
	public Incidence getNextIncidenceAtVertex() {
		return getNextIncidenceAtVertex(getGraph().getTraversalContext());
	}

	@Override
	public final Incidence getNextIncidenceAtVertex(Direction direction) {
		if (getGraph().getTraversalContext() == null) {
			Incidence i;
			try {
				i = localGraphDatabase.getIncidenceObject(storingGraphDatabase
						.getNextIncidenceIdAtVertexId(id));
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null) && (direction != i.getDirection())) {
					i = i.getNextIncidenceAtVertex();
				}
			}
			return i;
		} else {
			return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
					direction);
		}
	}

	@Override
	public Incidence getNextIncidenceAtVertex(boolean thisIncidence,
			IncidenceType... incidenceTypes) {
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				thisIncidence, incidenceTypes);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass);
	}


	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(getGraph().getTraversalContext(),
				anIncidenceClass, noSubclasses);
	}



	@Override
	public final Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Direction direction) {
		Incidence i;
		try {
			i = localGraphDatabase.getIncidenceObject(storingGraphDatabase
					.getNextIncidenceIdAtVertexId(id));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		if (traversalContext == null) {
			while (((i != null) && (direction != null)
					&& (direction != Direction.BOTH) && (direction != i
					.getDirection()))) {
				i = i.getNextIncidenceAtVertex();
			}
		} else {
			if ((direction != null) && (direction != Direction.BOTH)) {
				while ((i != null)
						&& ((!traversalContext.containsEdge(i.getEdge())) || (direction != i
								.getDirection()))) {
					i = i.getNextIncidenceAtVertex();
				}
			} else {
				while ((i != null)
						&& (!traversalContext.containsEdge(i.getEdge()))) {
					i = i.getNextIncidenceAtVertex();
				}
			}

		}
		return i;
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			boolean thisIncidence, IncidenceType... incidenceTypes) {
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
			IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				 false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				 false);
	}


	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(traversalContext, anIncidenceClass,
				noSubclasses);
	}



	@Override
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		Incidence currentIncidence = getNextIncidenceAtVertex(traversalContext);
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
					traversalContext);
		}
		return null;
	}

	@Override
	public Incidence getPreviousIncidenceAtVertex() {
		return getPreviousIncidenceAtVertex(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Edge> getThoseEdges() {
		return getThoseEdges(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Edge> getTheseEdges() {
		return getTheseEdges(getGraph().getTraversalContext());
	}

	@Override
	public Vertex getThis() {
		return getThis(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Vertex> getTheseVertices() {
		return getTheseVertices(getGraph().getTraversalContext());
	}

	@Override
	public Vertex getThat() {
		return getThat(getGraph().getTraversalContext());
	}

	@Override
	public Iterable<Vertex> getThoseVertices() {
		return getThoseVertices(getGraph().getTraversalContext());
	}

	@Override
	public boolean isBeforeAtVertex(Incidence i) {
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
	public boolean isAfterAtVertex(Incidence i) {
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
	public boolean isBeforeAtEdge(Incidence i) {
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
	public boolean isAfterAtEdge(Incidence i) {
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
	public GraphClass getGraphClass() {
		return getVertex().getGraphClass();
	}

	@Override 
	public RemoteStorageAccess getStorage(){
		return localGraphDatabase.getLocalStorage();
	}
	
	@Override
	public Schema getSchema() {
		return getVertex().getSchema();
	}

	@Override
	public IncidenceType getThisSemantics() {
		if (getEdge().isBinary()) {
			return this.getType().getIncidenceType();
		} else {
			throw new UnsupportedOperationException(
					"getThisSemantics() may be called only for incidences of binary edges");
		}
	}

	@Override
	public IncidenceType getThatSemantics() {
		if (getEdge().isBinary()) {
			return getThatIncidence().getType().getIncidenceType();
		} else {
			throw new UnsupportedOperationException(
					"getThatSemantics() may be called only for incidences of binary edges");
		}
	}

	public Incidence getThatIncidence() {
		if (getEdge().isBinary()) {
			return getEdge().getFirstIncidence() == this ? getEdge()
					.getFirstIncidence() : getEdge().getLastIncidence();
		} else {
			throw new UnsupportedOperationException(
					"getThatIncidence() may be called only for incidences of binary edges");
		}
	}

	//
	// @Override
	// public Graph getCompleteGraph() {
	// return
	// localGraphDatabase.getGraphObject(GraphDatabaseBaseImpl.GLOBAL_GRAPH_ID);
	// }

	@Override
	public void delete() {
		localGraphDatabase.deleteIncidence(id);
	}

}
