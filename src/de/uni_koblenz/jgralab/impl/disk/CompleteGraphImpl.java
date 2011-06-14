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

import java.util.Collection;
import java.util.Map;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.ProgressFunction;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.LocalBooleanGraphMarker;
import de.uni_koblenz.jgralab.impl.JGraLabMapImpl;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * Implementation of a Graph object representing the global or a partial graph
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class CompleteGraphImpl extends GraphBaseImpl {

	/**
	 * the unique id of the complete graph this object represents or belogns to
	 */
	private final String uid;

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
		this.uid = graphId;
	}

	// ==============================================================
	// Methods to access basic graph properties
	// ==============================================================

	@Override
	public String getUniqueGraphId() {
		return uid;
	}

	@Override
	public int compareTo(Graph a) {
		int compVal = getUniqueGraphId().compareTo(a.getUniqueGraphId());
		if (compVal == 0) {
			return a.getPartialGraphId() - getPartialGraphId();
		} else {
			return compVal;
		}
	}

	@Override
	public long getGraphVersion() {
		return storingGraphDatabase.getGraphVersion();
	}

	@Override
	public boolean isLoading() {
		return storingGraphDatabase.isLoading();
	}

	@Override
	public GraphDatabaseBaseImpl getGraphDatabase() {
		return localGraphDatabase;
	}

	@Override
	@Deprecated
	public GraphFactory getGraphFactory() {
		return localGraphDatabase.getGraphFactory();
	}

	@Override
	public Schema getSchema() {
		return localGraphDatabase.getSchema();
	}

	@Override
	public Graph getSuperordinateGraph() {
		if (globalSubgraphId == 1) {
			return this;
		} else {
			return getParentDistributedGraph();
		}
	}

	@Override
	public Graph createPartialGraph(String hostname) {
		long pgId = storingGraphDatabase.createPartialGraph(this
				.getGraphClass().getM1Class(), hostname);
		return localGraphDatabase.getGraphObject(pgId);
	}

	@Override
	public Graph getParentDistributedGraph() {
		return localGraphDatabase.getGraphObject(storingGraphDatabase
				.getIdOfParentDistributedGraph());
	}

	@Override
	public Graph getCompleteGraph() {
		return localGraphDatabase.getGraphObject(0);
	}

	@Override
	public Graph getView(int kappa) {
		return localGraphDatabase.getGraphFactory()
				.createViewGraph(this, kappa);
	}

	public Graph getViewedGraph() {
		return this;
	}

	@Override
	public boolean isPartOfGraph(Graph other) {
		Graph parentDistributedGraph = getParentDistributedGraph();
		return ((parentDistributedGraph == other) || (parentDistributedGraph
				.isPartOfGraph(other)));
	}

	@Override
	public <T extends Incidence> T connect(Class<T> cls, Vertex vertex,
			Edge edge) {
		return vertex.connect(cls, edge);
	}

	@Override
	public boolean containsEdge(Edge e) {
		return (e != null) && (e.getId() > 0)
				&& (localGraphDatabase.getEdgeObject(e.getId()) == e);
	}

	@Override
	public boolean containsEdgeLocally(Edge e) {
		return (e != null) && (e.getGraph() == this)
				&& (localGraphDatabase.getEdgeObject(e.getId()) == e);
	}

	@Override
	public boolean containsVertex(Vertex v) {
		return (v != null) && (v.getId() > 0)
				&& (localGraphDatabase.getVertexObject(v.getId()) == v);
	}

	@Override
	public boolean containsVertexLocally(Vertex v) {
		return (v != null) && (v.getGraph() == this)
				&& (localGraphDatabase.getVertexObject(v.getId()) == v);
	}

	@Override
	public void deleteEdge(Edge e) {
		assert (e != null) && e.isValid() && containsEdge(e);
		storingGraphDatabase.deleteEdge(e.getId());
	}

	@Override
	public void deleteVertex(Vertex v) {
		assert (v != null) && v.isValid() && containsVertex(v);
		storingGraphDatabase.deleteVertex(v.getId());
	}

	@Override
	public long getECount() {
		return storingGraphDatabase.getECount(globalSubgraphId);
	}

	@Override
	public long getMaxECount() {
		return storingGraphDatabase.getMaxECount();
	}

	@Override
	public Edge getEdge(long eId) {
		assert eId != 0 : "The edge id must be != 0, given was " + eId;
		return localGraphDatabase.getEdgeObject(eId);
	}

	@Override
	public long getEdgeListVersion() {
		return storingGraphDatabase.getEdgeListVersion();
	}

	@Override
	public long getVCount() {
		return storingGraphDatabase.getVCount(globalSubgraphId);
	}

	@Override
	public long getMaxVCount() {
		return storingGraphDatabase.getMaxVCount();
	}

	@Override
	public Vertex getVertex(long vId) {
		assert (vId > 0) : "The vertex id must be > 0, given was " + vId;
		return localGraphDatabase.getVertexObject(vId);
	}

	@Override
	public long getVertexListVersion() {
		return storingGraphDatabase.getVertexListVersion();
	}

	@Override
	public long getICount() {
		return localGraphDatabase.getICount(globalSubgraphId);
	}

	/**
	 * Modifies eSeq such that the movedEdge is immediately after the
	 * targetEdge.
	 * 
	 * @param targetEdge
	 *            an edge
	 * @param movedEdge
	 *            the edge to be moved
	 */
	protected void putEdgeAfterInGraph(EdgeImpl targetEdge, EdgeImpl movedEdge) {
		storingGraphDatabase
				.putEdgeAfter(targetEdge.getId(), movedEdge.getId());
	}

	/**
	 * Modifies eSeq such that the movedEdge is immediately before the
	 * targetEdge.
	 * 
	 * @param targetEdge
	 *            an edge
	 * @param movedEdge
	 *            the edge to be moved
	 */
	protected void putEdgeBeforeInGraph(EdgeImpl targetEdge, EdgeImpl movedEdge) {
		storingGraphDatabase.putEdgeBefore(targetEdge.getId(),
				movedEdge.getId());
	}

	/**
	 * Modifies vSeq such that the movedVertex is immediately after the
	 * targetVertex.
	 * 
	 * @param targetVertex
	 *            an edge
	 * @param movedVertex
	 *            the edge to be moved
	 */
	protected void putVertexAfterInGraph(VertexImpl targetVertex,
			VertexImpl movedVertex) {
		storingGraphDatabase.putVertexAfter(targetVertex.getId(),
				movedVertex.getId());
	}

	/**
	 * Modifies eSeq such that the movedVertex is immediately before the
	 * targetVertex.
	 * 
	 * @param targetVertex
	 *            an edge
	 * @param movedVertex
	 *            the edge to be moved
	 */
	protected void putVertexBeforeInGraph(VertexImpl targetVertex,
			VertexImpl movedVertex) {
		storingGraphDatabase.putVertexBefore(targetVertex.getId(),
				movedVertex.getId());
	}

	// ==============================================================
	// Methods to access traversal context
	// ==============================================================

	@Override
	public void setTraversalContext(Graph traversalContext) {
		localGraphDatabase.setTraversalContext(traversalContext);
	}

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

	// ====================================================
	// Methods to create domain objects
	// ====================================================

	@Override
	public <T> JGraLabList<T> createList() {
		return storingGraphDatabase.createList();
	}

	@Override
	public <T> JGraLabList<T> createList(Collection<? extends T> collection) {
		return storingGraphDatabase.createList(collection);
	}

	@Override
	public <T> JGraLabList<T> createList(int initialCapacity) {
		return storingGraphDatabase.createList(initialCapacity);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap() {
		return storingGraphDatabase.createMap();
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity) {
		return storingGraphDatabase.createMap(initialCapacity);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) {
		return storingGraphDatabase.createMap(initialCapacity, loadFactor);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map) {
		return new JGraLabMapImpl<K, V>(map);
	}

	@Override
	public <T> JGraLabSet<T> createSet() {
		return storingGraphDatabase.createSet();
	}

	@Override
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection) {
		return storingGraphDatabase.createSet(collection);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity) {
		return storingGraphDatabase.createSet(initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor) {
		return storingGraphDatabase.createSet(initialCapacity, loadFactor);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) {
		return storingGraphDatabase.createRecord(recordClass, io);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) {
		return storingGraphDatabase.createRecord(recordClass, fields);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Object... components) {
		return storingGraphDatabase.createRecord(recordClass, components);
	}

	// ==============================================================
	// Methods to be used only internal
	// ==============================================================

	@Override
	protected void setVCount(int count) {
		storingGraphDatabase.setVCount(globalSubgraphId, count);
	}

	/**
	 * Sets the loading flag.
	 * 
	 * @param isLoading
	 */
	public void setLoading(boolean isLoading) {
		storingGraphDatabase.setLoading(isLoading);
	}

	/**
	 * Sets the version counter of this graph. Should only be called immediately
	 * after loading and in graphModified.
	 * 
	 * @param graphVersion
	 *            new version value
	 */
	public void setGraphVersion(long graphVersion) {
		storingGraphDatabase.setGraphVersion(graphVersion);
	}

	@Override
	public void defragment() {

	}

	/**
	 * Changes this graph's version. graphModified() is called whenever the
	 * graph is changed, all changes like adding, creating and reordering of
	 * edges and vertices or changes of attributes of the graph, an edge or a
	 * vertex are treated as a change.
	 */
	@Override
	public void graphModified() {
		storingGraphDatabase.graphModified();
	}

	@Override
	protected void edgeListModified() {
		storingGraphDatabase.edgeListModified();
		storingGraphDatabase.graphModified();
	}

	/**
	 * Changes the vertex sequence version of this graph. Should be called
	 * whenever the vertex list of this graph is changed, for instance by
	 * creation and deletion or reordering of vertices.
	 */
	@Override
	protected void vertexListModified() {
		storingGraphDatabase.vertexListModified();
		storingGraphDatabase.graphModified();
	}

	public void saveGraph(String filename, ProgressFunction pf, LocalBooleanGraphMarker subGraph) throws GraphIOException {
	//	if (subGraph != null) {
			GraphIO.saveGraphToFile(filename, this, pf);
//		} else {
//			GraphIO.saveGraphToFile(filename, subGraph, pf);
//		}
	}




}
