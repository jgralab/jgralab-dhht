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

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.ProgressFunction;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.LocalBooleanGraphMarker;
import de.uni_koblenz.jgralab.impl.JGraLabMapImpl;
import de.uni_koblenz.jgralab.impl.RemoteGraphDatabaseAccess;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * Implementation of a Graph object representing the global or a partial graph
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class CompleteGraphImpl extends GraphBaseImpl {

	/**
	 * Creates a graph
	 * 
	 * @param id
	 *            this Graph's id
	 */
	protected CompleteGraphImpl(String graphId, long partialGraphId,
			GraphDatabaseBaseImpl localDatabase,
			RemoteGraphDatabaseAccess graphData) {
		super(partialGraphId, localDatabase, graphData);
	}

	// ==============================================================
	// Methods to access schema and type
	// ==============================================================

	@Override
	public Schema getSchema() {
		return localGraphDatabase.getSchema();
	}

	@Override
	public abstract Class<? extends Graph> getM1Class();

	@Override
	public abstract GraphClass getType();

	// @Override
	// public int compareTo(Graph a) {
	// int compVal = getUniqueGraphId().compareTo(a.getUniqueGraphId());
	// if (compVal == 0) {
	// return a.getPartialGraphId() - getPartialGraphId();
	// } else {
	// return compVal;
	// }
	// }

	public void saveGraph(String filename, ProgressFunction pf,
			LocalBooleanGraphMarker subGraph) throws GraphIOException {
		// if (subGraph != null) {
		GraphIO.saveGraphToFile(filename, this, pf);
		// } else {
		// GraphIO.saveGraphToFile(filename, subGraph, pf);
		// }
	}

	// ==============================================================
	// Methods to access traversal context
	// ==============================================================

	@Override
	public void useAsTraversalContext() {
		localGraphDatabase.setTraversalContext(this);
	}

	@Override
	public void releaseTraversalContext() {
		localGraphDatabase.releaseTraversalContext();
	}

	@Override
	public Graph getTraversalContext() {
		return localGraphDatabase.getTraversalContext();
	}

	// ============================================================================
	// Methods to access hierarchy and distribution
	//
	// - General methods
	// - Nesting hierarchy
	// - Visibility layering
	// - Distribution
	// - Graph IDs
	// ============================================================================

	@Override
	public Graph getCompleteGraph() {
		return localGraphDatabase
				.getGraphObject(GraphDatabaseElementaryMethods.GLOBAL_GRAPH_ID);
	}

	@Override
	public Graph getLocalPartialGraph() {
		int localGraphId = GraphDatabaseElementaryMethods.TOPLEVEL_LOCAL_SUBGRAPH_ID;
		long globalId = getGraphDatabase().convertToGlobalId(localGraphId);
		return localGraphDatabase
				.getGraphObject(globalId);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public AttributedElement getParentGraphOrElement() {
		if (globalSubgraphId == GraphDatabaseElementaryMethods.GLOBAL_GRAPH_ID)
			return null;
		try {
			return localGraphDatabase
					.getGraphElementObject(storingGraphDatabase
							.getContainingElementId(globalSubgraphId));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Graph getParentGraph() {
		if (globalSubgraphId == GraphDatabaseElementaryMethods.GLOBAL_GRAPH_ID)
			return null;
		AttributedElement elem = getParentGraphOrElement();
		if (elem instanceof Graph)
			return (Graph) elem;
		return ((GraphElement) elem).getGraph();
	}

	@Override
	public boolean isPartOfGraph(Graph other) {
		Graph parentGraph = getParentGraph();
		return ((parentGraph == other) || (parentGraph.isPartOfGraph(other)));
	}

	@Override
	public Graph getView(int kappa) {
		return localGraphDatabase.getGraphFactory()
				.createViewGraph_InMemoryStorage(this, kappa);
	}

	@Override
	public Graph getViewedGraph() {
		return this;
	}

	// inherited from GraphBaseImpl
	// public Graph createPartialGraphInGraph(String hostnameOfPartialGraph)

	@Override
	public Graph getPartialGraph(int partialGraphId) {
		long globalId = (partialGraphId << 32) + 1;
		return localGraphDatabase.getGraphObject(globalId);
	}

	// Inherited from GraphBaseImpl
	// @Deprecated
	// public void savePartialGraphs(GraphIO graphIO)

	// Inherited from GraphBaseImpl
	// public String getUniqueGraphId() {}

	// Inherited from GraphBaseImpl
	// public long getGlobalId() {}

	// Inherited from GraphBaseImpl
	// public int getLocalId() {}

	// Inherited from GraphBaseImpl
	// public int getPartialGraphId() {}

	// Inherited from GraphBaseImpl
	// public int isLocalElementId(long id) {}

	// ============================================================================
	// Methods to access vertices and edges of the graph
	// ============================================================================

	// Inherited from GraphBaseImpl
	// public <T extends Vertex> T createVertex(Class<T> cls) {}

	// Inherited from GraphBaseImpl
	// public <T extends Edge> T createEdge(Class<T> cls)

	// Inherited from GraphBaseImpl
	// public <T extends BinaryEdge> T createEdge(Class<T> cls, Vertex alpha,
	// Vertex omega)

	@Override
	public <T extends Incidence> T connect(Class<T> cls, Vertex vertex,
			Edge edge) {
		return vertex.connect(cls, edge);
	}

	@Override
	public boolean containsVertex(Vertex v) {
		return (v != null) && (v.getGlobalId() > 0)
				&& (localGraphDatabase.getVertexObject(v.getGlobalId()) == v);
	}

	@Override
	public boolean containsVertexLocally(Vertex v) {
		return (v != null) && (v.getGraph() == this)
				&& (localGraphDatabase.getVertexObject(v.getGlobalId()) == v);
	}

	@Override
	public boolean containsEdge(Edge e) {
		return (e != null) && (e.getGlobalId() > 0)
				&& (localGraphDatabase.getEdgeObject(e.getGlobalId()) == e);
	}

	@Override
	public boolean containsEdgeLocally(Edge e) {
		return (e != null) && (e.getGraph() == this)
				&& (localGraphDatabase.getEdgeObject(e.getGlobalId()) == e);
	}

	// Inherited from GraphBaseImpl
	// public boolean containsElement(GraphElement elem);

	// Inherited from GraphBaseImpl
	// public boolean deleteVertex(Vertex v);

	// Inherited from GraphBaseImpl
	// public boolean deleteEdge(Edge e);

	// Inherited from GraphBaseImpl
	// public Vertex getFirstVertex()

	// Inherited from GraphBaseImpl
	// public Vertex getLastVertex()

	// Inherited from GraphBaseImpl
	// public Edge getFirstEdge()

	// Inherited from GraphBaseImpl
	// public Edge getLastEdge()

	// Inherited from GraphBaseImpl
	// public Vertex getVertex(long id)

	// Inherited from GraphBaseImpl
	// public Edge getEdge(long id)

	// Inherited from GraphBaseImpl
	// public long getMaxVCount()

	// Inherited from GraphBaseImpl
	// public long getMaxECount()

	// Inherited from GraphBaseImpl
	// public long getVCount()

	// Inherited from GraphBaseImpl
	// public long getECount()

	// Inherited from GraphBaseImpl
	// public long getICount()

	// Inherited from GraphBaseImpl
	// public Iterable<Vertex> getVertices()

	// Inherited from GraphBaseImpl
	// public Iterable<Edge> getEdges()

	// Inherited from GraphBaseImpl
	// public void sortVertices(Comparator<Vertex> comp)

	// Inherited from GraphBaseImpl
	// public void sortEdges(Comparator<Edge> comp)

	@Override
	public void defragment() {

	}

	// ============================================================================
	// Methods to access vertex and edge order
	// ============================================================================

	// /**
	// * Modifies eSeq such that the movedEdge is immediately after the
	// * targetEdge.
	// *
	// * @param targetEdge
	// * an edge
	// * @param movedEdge
	// * the edge to be moved
	// */
	// protected void putEdgeAfterInGraph(EdgeImpl targetEdge, EdgeImpl
	// movedEdge) {
	// storingGraphDatabase
	// .putEdgeAfter(targetEdge.getGlobalId(), movedEdge.getGlobalId());
	// }
	//
	// /**
	// * Modifies eSeq such that the movedEdge is immediately before the
	// * targetEdge.
	// *
	// * @param targetEdge
	// * an edge
	// * @param movedEdge
	// * the edge to be moved
	// */
	// protected void putEdgeBeforeInGraph(EdgeImpl targetEdge, EdgeImpl
	// movedEdge) {
	// storingGraphDatabase.putEdgeBefore(targetEdge.getGlobalId(),
	// movedEdge.getGlobalId());
	// }
	//
	// /**
	// * Modifies vSeq such that the movedVertex is immediately after the
	// * targetVertex.
	// *
	// * @param targetVertex
	// * an edge
	// * @param movedVertex
	// * the edge to be moved
	// */
	// protected void putVertexAfterInGraph(VertexImpl targetVertex,
	// VertexImpl movedVertex) {
	// storingGraphDatabase.putVertexAfter(targetVertex.getGlobalId(),
	// movedVertex.getGlobalId());
	// }
	//
	// /**
	// * Modifies eSeq such that the movedVertex is immediately before the
	// * targetVertex.
	// *
	// * @param targetVertex
	// * an edge
	// * @param movedVertex
	// * the edge to be moved
	// */
	// protected void putVertexBeforeInGraph(VertexImpl targetVertex,
	// VertexImpl movedVertex) {
	// storingGraphDatabase.putVertexBefore(targetVertex.getGlobalId(),
	// movedVertex.getGlobalId());
	// }

	// ============================================================================
	// Listener methods inherited from GraphBaseImpl
	// ============================================================================

	// ============================================================================
	// Methods to access graph state and version (loading etc.)
	// ============================================================================

	@Override
	public boolean isLoading() {
		try {
			return storingGraphDatabase.isLoading();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets the loading flag.
	 * 
	 * @param isLoading
	 */
	public void setLoading(boolean isLoading) {
		try {
			storingGraphDatabase.setLoading(isLoading);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long getGraphVersion() {
		try {
			return storingGraphDatabase.getGraphVersion();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets the version counter of this graph. Should only be called immediately
	 * after loading and in graphModified.
	 * 
	 * @param graphVersion
	 *            new version value
	 */
	public void setGraphVersion(long graphVersion) {
		try {
			storingGraphDatabase.setGraphVersion(graphVersion);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Changes this graph's version. graphModified() is called whenever the
	 * graph is changed, all changes like adding, creating and reordering of
	 * edges and vertices or changes of attributes of the graph, an edge or a
	 * vertex are treated as a change.
	 */
	@Override
	public void graphModified() {
		try {
			storingGraphDatabase.graphModified();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long getVertexListVersion() {
		try {
			return storingGraphDatabase.getVertexListVersion();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long getEdgeListVersion() {
		try {
			return storingGraphDatabase.getEdgeListVersion();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void edgeListModified() {
		try {
			storingGraphDatabase.edgeListModified();
			storingGraphDatabase.graphModified();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Changes the vertex sequence version of this graph. Should be called
	 * whenever the vertex list of this graph is changed, for instance by
	 * creation and deletion or reordering of vertices.
	 */
	@Override
	protected void vertexListModified() {
		try {
			storingGraphDatabase.vertexListModified();
			storingGraphDatabase.graphModified();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public final GraphFactory getGraphFactory() {
		return localGraphDatabase.getGraphFactory();
	}

	@Override
	public GraphDatabaseBaseImpl getGraphDatabase() {
		return localGraphDatabase;
	}

	// ====================================================
	// Methods to create domain objects
	// ====================================================

	@Override
	public <T> JGraLabList<T> createList() {
		try {
			return storingGraphDatabase.createList();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> JGraLabList<T> createList(Collection<? extends T> collection) {
		try {
			return storingGraphDatabase.createList(collection);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> JGraLabList<T> createList(int initialCapacity) {
		try {
			return storingGraphDatabase.createList(initialCapacity);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap() {
		try {
			return storingGraphDatabase.createMap();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity) {
		try {
			return storingGraphDatabase.createMap(initialCapacity);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) {
		try {
			return storingGraphDatabase.createMap(initialCapacity, loadFactor);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map) {
		return new JGraLabMapImpl<K, V>(map);
	}

	@Override
	public <T> JGraLabSet<T> createSet() {
		try {
			return storingGraphDatabase.createSet();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection) {
		try {
			return storingGraphDatabase.createSet(collection);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity) {
		try {
			return storingGraphDatabase.createSet(initialCapacity);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor) {
		try {
			return storingGraphDatabase.createSet(initialCapacity, loadFactor);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) {
		try {
			return storingGraphDatabase.createRecord(recordClass, io);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) {
		try {
			return storingGraphDatabase.createRecord(recordClass, fields);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Object... components) {
		try {
			return storingGraphDatabase.createRecord(recordClass, components);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

}
