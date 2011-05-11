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

package de.uni_koblenz.jgralab;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Stack;

import de.uni_koblenz.jgralab.impl.disk.DiskStorageManager;
import de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * The interface Graph is the base of all JGraLab graphs. It provides access to
 * global graph properties and to the Vertex and Edge sequence. Creation and
 * removal of vertices and edges, as well as validity checks, are provided.
 * 
 * Additionally, convenient methods for traversal, either based on separate
 * calls (getFirst/getNext) or on Iterables, can be used to traverse the graph.
 * 
 * @author ist@uni-koblenz.de
 */
public interface Graph extends AttributedElement<GraphClass, Graph> {

	/**
	 * @return {@link GraphElement} which contains this {@link Graph} or
	 *         <code>null</code> if it is the complete {@link Graph} or a
	 *         {@link ViewGraphImpl}
	 */
	public GraphElement<?, ?, ?> getContainingElement() throws RemoteException;

	/**
	 * @return {@link Graph} the distributed {@link Graph} this partial graph is
	 *         a member of or this graph itself, it is not directly a member of
	 *         a distributed graph
	 */
	public abstract Graph getParentDistributedGraph() throws RemoteException;

	/**
	 * @return {@link Graph} the superordinate {@link Graph} containing the
	 *         element this graph is contained in as subordinate one or this
	 *         graph, if it is not a subordinate one
	 */
	public abstract Graph getSuperordinateGraph() throws RemoteException;

	/**
	 * @return {@link Graph} the complete, top-level {@link Graph}
	 */
	public abstract Graph getCompleteGraph() throws RemoteException;

	/**
	 * 
	 * @return {@link Graph} the graph viewed by this viewgraph or the graph
	 *         itself if it is not a view
	 */
	public abstract Graph getViewedGraph() throws RemoteException;

	/**
	 * @see GraphElement#isVisible(int)
	 * @param kappa
	 *            <b>int</b>
	 * @return {@link Graph} which contains all {@link GraphElement}s
	 *         <code>ge</code> where <code>ge.isVisible(kappa)==true</code>.
	 */
	public Graph getView(int kappa) throws RemoteException;

	/**
	 * @return {@link Graph} the current traversal context. It is not removed
	 *         from the {@link Stack}.
	 */
	public Graph getTraversalContext() throws RemoteException;

	/**
	 * Sets this {@link Graph} as the traversal context.
	 */
	public void useAsTraversalContext() throws RemoteException;

	/**
	 * Removes the current traversal context from the {@link Stack}.
	 */
	public void releaseTraversalContext() throws RemoteException;

	/**
	 * Creates a vertex the specified class <code>cls</code> and adds the new
	 * vertex to this Graph.
	 */
	public <T extends Vertex> T createVertex(Class<T> cls) throws RemoteException;

	/**
	 * Creates an edge of the specified class <code>cls</code> and adds the new
	 * edge to this Graph.
	 */
	public <T extends Edge> T createEdge(Class<T> cls) throws RemoteException;

	/**
	 * Connects the given vertex and the given edge by an incidence of class
	 * cls. The direction of the connection is automatically determined by cls
	 * 
	 * @return the incidence created
	 */
	public <T extends Incidence> T connect(Class<T> cls, Vertex vertex,
			Edge edge) throws RemoteException;

	/**
	 * Creates an binary edge of the specified class <code>cls</code> and adds
	 * the new edge to this Graph.
	 */
	public <T extends BinaryEdge> T createEdge(Class<T> cls, Vertex alpha,
			Vertex omega) throws RemoteException;

	/**
	 * Checks whether this graph is currently being loaded.
	 * 
	 * @return true if the graph is currently being loaded
	 */
	public boolean isLoading() throws RemoteException;

	/**
	 * Callback method: Called immediately after loading of this graph is
	 * completed. Overwrite this method to perform user defined operations after
	 * loading a graph.
	 */
	public void loadingCompleted() throws RemoteException;

	/**
	 * Checks whether this graph has changed with respect to the given
	 * <code>previousVersion</code>. Every change in the graph, e.g. adding,
	 * creating and reordering of edges and vertices or changes of attributes of
	 * the graph, an edge or a vertex are treated as a change.
	 * 
	 * @param previousVersion
	 *            The version to check against
	 * @return <code>true</code> if the internal graph version of the graph is
	 *         different from the <code>previousVersion</code>.
	 */
	public boolean isGraphModified(long previousVersion) throws RemoteException;

	/**
	 * Returns the version counter of this graph.
	 * 
	 * @return the graph version
	 * @see #isGraphModified(long)
	 */
	public long getGraphVersion() throws RemoteException;

	/**
	 * Checks if the vertex sequence of this has changed with respect to the
	 * given <code>previousVersion</code>. Changes in the vertex sequence are
	 * creation and deletion as well as reordering of vertices, but not changes
	 * of attribute values.
	 * 
	 * @return <code>true</code> if the vertex list version of this graph is
	 *         different from <code>previousVersion</code>.
	 */
	public boolean isVertexListModified(long previousVersion) throws RemoteException;

	/**
	 * Returns the version counter of the vertex sequence of this graph.
	 * 
	 * @return the vertex sequence version
	 * @see #isVertexListModified(long)
	 */
	public long getVertexListVersion() throws RemoteException;

	/**
	 * Checks if the edge sequence of this has changed with respect to the given
	 * <code>previousVersion</code>. Changes in the edge sequence are creation
	 * and deletion as well as reordering of edges, but not changes of attribute
	 * values.
	 * 
	 * @return <code>true</code> if the edge list version of this graph is
	 *         different from <code>previousVersion</code>.
	 */
	public boolean isEdgeListModified(long edgeListVersion) throws RemoteException;

	/**
	 * Returns the version counter of the edge sequence of this graph.
	 * 
	 * @return the edge sequence version
	 * @see #isEdgeListModified(long)
	 */
	public long getEdgeListVersion() throws RemoteException;

	/**
	 * @return true if this graph contains the given vertex <code>v</code>.
	 */
	public boolean containsVertex(Vertex v) throws RemoteException;

	/**
	 * @return true if this graph contains the given edge <code>e</code>.
	 */
	boolean containsEdge(Edge e) throws RemoteException;

	/**
	 * Removes the vertex <code>v</code> from the vertex sequence of this graph.
	 * Also, any edges incident to vertex <code>v</code> are deleted. If
	 * <code>v</code> is the parent of a composition, all child vertices are
	 * also deleted.
	 * 
	 * Preconditions: v.isValid()
	 * 
	 * Postconditions: !v.isValid() && !containsVertex(v) &&
	 * getVertex(v.getId()) == null
	 * 
	 * @param v
	 *            the Vertex to be deleted
	 */
	public void deleteVertex(Vertex v) throws RemoteException;

	/**
	 * Removes the edge <code>e</code> from the edge sequence of this graph.
	 * This implies changes to the incidence lists of the alpha and omega vertex
	 * of <code>e</code>.
	 * 
	 * Preconditions: e.isValid()
	 * 
	 * Postconditions: !e.isValid() && !containsEdge(e) && getEdge(e.getId()) ==
	 * null
	 * 
	 * @param e
	 *            the Edge to be deleted
	 */
	public void deleteEdge(Edge e) throws RemoteException;

	/**
	 * Returns the first {@link Vertex} in the vertex sequence of this
	 * {@link Graph}. If it does not contain any vertices <code>null</code> is
	 * returned.
	 * 
	 * @return {@link Vertex}
	 */
	public Vertex getFirstVertex() throws RemoteException;

	/**
	 * Returns the last {@link Vertex} in the vertex sequence of this
	 * {@link Graph}. If it does not contain any vertices <code>null</code> is
	 * returned.
	 * 
	 * @return {@link Vertex}
	 */
	public Vertex getLastVertex() throws RemoteException;

	/**
	 * Returns the first Vertex of the specified <code>vertexClass</code>
	 * (including subclasses) in the vertex sequence of this Graph.
	 * 
	 * @param vertexClass
	 *            a VertexClass (i.e. an instance of schema.VertexClass)
	 * 
	 * @return the first Vertex, or null if this graph contains no vertices of
	 *         the specified <code>vertexClass</code>.
	 */
	public Vertex getFirstVertex(VertexClass vertexClass) throws RemoteException;

	/**
	 * Returns the first Vertex of the specified <code>vertexClass</code>,
	 * including subclasses only if <code>noSubclasses</code> is set to false,
	 * in the vertex sequence of this Graph.
	 * 
	 * @param vertexClass
	 *            a VertexClass (i.e. an instance of schema.VertexClass)
	 * 
	 * @param noSubclasses
	 *            if set to true, only vertices with the exact class are taken
	 *            into account, false means that also subclasses are valid
	 * 
	 * @return the first Vertex, or null if this graph contains no vertices of
	 *         the specified <code>vertexClass</code>.
	 */
	public Vertex getFirstVertex(VertexClass vertexClass, boolean noSubclasses) throws RemoteException;

	/**
	 * Returns the first Vertex of the specified <code>vertexClass</code>
	 * (including subclasses) in the vertex sequence of this Graph.
	 * 
	 * @param vertexClass
	 *            a VertexClass (i.e. an M1 interface extending Vertex)
	 * 
	 * @return the first Vertex, or null if this graph contains no vertices of
	 *         the specified <code>vertexClass</code>.
	 */
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass) throws RemoteException;

	/**
	 * Returns the first Vertex of the specified <code>vertexClass</code>,
	 * including subclasses only if <code>noSubclasses</code> is set to false,
	 * in the vertex sequence of this Graph.
	 * 
	 * @param vertexClass
	 *            a VertexClass (i.e. an M1 interface extending Vertex)
	 * 
	 * @param noSubclasses
	 *            if set to true, only vertices with the exact class are taken
	 *            into account, false means that also subclasses are valid
	 * 
	 * @return the first Vertex, or null if this graph contains no vertices of
	 *         the specified <code>vertexClass</code>.
	 */
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass,
			boolean noSubclasses) throws RemoteException;

	/**
	 * Returns the first {@link Edge} in the edge sequence of this {@link Graph}
	 * . If it does not contain any {@link Edge}s <code>null</code> is returned.
	 * 
	 * @return {@link Edge}
	 */
	public Edge getFirstEdge() throws RemoteException;

	/**
	 * Returns the last {@link Edge} in the edge sequence of this {@link Graph}.
	 * If it does not contain any {@link Edge}s <code>null</code> is returned.
	 * 
	 * @return {@link Edge}
	 */
	public Edge getLastEdge() throws RemoteException;

	/**
	 * Returns the first Edge of the specified <code>edgeClass</code> (including
	 * subclasses) in the edge sequence of this Graph.
	 * 
	 * @param edgeClass
	 *            an EdgeClass (i.e. an instance of schema.EdgeClass)
	 * 
	 * @return the first Edge, or null if this graph contains no edges of the
	 *         specified <code>edgeClass</code>.
	 */
	public Edge getFirstEdge(EdgeClass edgeClass) throws RemoteException;

	/**
	 * Returns the first Edge of the specified <code>edgeClass</code>, including
	 * subclasses only if <code>noSubclasses</code> is set to false, in the edge
	 * sequence of this Graph.
	 * 
	 * @param edgeClass
	 *            an EdgeClass (i.e. an instance of schema.EdgeClass)
	 * 
	 * @param noSubclasses
	 *            if set to true, only edges with the exact class are taken into
	 *            account, false means that also subclasses are valid
	 * 
	 * @return the first Edge, or null if this graph contains no edges of the
	 *         specified <code>edgeClass</code>.
	 */
	public Edge getFirstEdge(EdgeClass edgeClass, boolean noSubclasses) throws RemoteException;

	/**
	 * Returns the first Edge of the specified <code>edgeClass</code> (including
	 * subclasses) in the edge sequence of this Graph.
	 * 
	 * @param edgeClass
	 *            an EdgeClass (i.e. an M1 interface extending Edge)
	 * 
	 * @return the first Edge, or null if this graph contains no edges of the
	 *         specified <code>edgeClass</code>.
	 */
	public Edge getFirstEdge(Class<? extends Edge> edgeClass) throws RemoteException;

	/**
	 * Returns the first Edge of the specified <code>edgeClass</code>, including
	 * subclasses only if <code>noSubclasses</code> is set to false, in the edge
	 * sequence of this Graph.
	 * 
	 * @param edgeClass
	 *            an EdgeClass (i.e. an M1 interface extending Edge)
	 * 
	 * @param noSubclasses
	 *            if set to true, only edges with the exact class are taken into
	 *            account, false means that also subclasses are valid
	 * 
	 * @return the first Edge, or null if this graph contains no edges of the
	 *         specified <code>edgeClass</code>.
	 * @throws RemoteException 
	 */
	public Edge getFirstEdge(Class<? extends Edge> edgeClass,
			boolean noSubclasses) throws RemoteException;

	/**
	 * Returns the Vertex with the specified <code>id</code> if such a vertex
	 * exists in this Graph.
	 * 
	 * @param id
	 *            the id of the vertex (must be > 0)
	 * @return the Vertex, or null if no such vertex exists
	 */
	public Vertex getVertex(int id) throws RemoteException;

	/**
	 * Returns the oriented Edge with the specified <code>id</code> if such an
	 * edge exists in this Graph. If <code>id</code> is positive, the normal
	 * edge is returned, otherwise, the reversed Edge is returned.
	 * 
	 * @param id
	 *            the id of the edge (must be != 0)
	 * @return the Edge, or null if no such edge exists
	 */
	public Edge getEdge(int id) throws RemoteException;

	/**
	 * The maximum number of vertices that can be stored in the graph before the
	 * internal array structures are expanded.
	 * 
	 * @return the maximum number of vertices
	 */
	public int getMaxVCount() throws RemoteException;

	/**
	 * The maximum number of edges that can be stored in the graph before the
	 * internal array structures are expanded.
	 * 
	 * @return the maximum number of edges
	 */
	public int getMaxECount() throws RemoteException;

	/**
	 * Returns the number of vertices in this Graph.
	 * 
	 * @return the number of vertices
	 */
	public int getVCount() throws RemoteException;

	/**
	 * Returns the number of edges in this Graph.
	 * 
	 * @return the number of edges
	 */
	public int getECount() throws RemoteException;

	/**
	 * @return the number of incidences in this graph
	 */
	public int getICount() throws RemoteException;

	/**
	 * Returns the <code>id</code> of this Graph. JGraLab assigns a 128 bit
	 * random id to all Graphs upon creation. This initial id is most likely
	 * (but not guaranteed) unique.
	 * 
	 * @return the id of this graph
	 */
	public String getCompleteGraphUid() throws RemoteException;

	/**
	 * Sets the <code>id</code> of this Graph.
	 * 
	 * Precondition: id != null && id.equals(id.trim()) && !id.equals("")
	 * 
	 * @param id
	 *            the new id
	 */
	// Method should be used internally only
	// public void setId(String id);

	/**
	 * Returns an {@link Iterable} which iterates over all {@link Edge}s of this
	 * {@link Graph} in the order determined by the edge sequence.
	 * 
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getEdges() throws RemoteException;

	/**
	 * Returns an Iterable which iterates over all edges of this Graph which
	 * have the specified <code>edgeClass</code> (including subclasses), in the
	 * order determined by the edge sequence.
	 * 
	 * @param edgeClass
	 *            an EdgeClass (i.e. instance of schema.EdgeClass)
	 * 
	 * @return an Iterable for all edges of the specified <code>edgeClass</code>
	 */
	public Iterable<Edge> getEdges(EdgeClass edgeClass) throws RemoteException;

	/**
	 * Returns an Iterable which iterates over all edges of this Graph which
	 * have the specified <code>edgeClass</code> (including subclasses), in the
	 * order determined by the edge sequence.
	 * 
	 * @param edgeClass
	 *            an EdgeClass (i.e. an M1 interface extending Edge)
	 * 
	 * @return an Iterable for all edges of the specified <code>edgeClass</code>
	 */
	public Iterable<Edge> getEdges(Class<? extends Edge> edgeClass) throws RemoteException;

	/**
	 * Returns the list of reachable vertices.
	 * 
	 * @param startVertex
	 *            a start vertex
	 * @param pathDescription
	 *            a GReQL path description
	 * @param vertexType
	 *            the type of the reachable vertices (acts as implicit
	 *            GoalRestriction)
	 * @return a List of all vertices of type <code>vertexType</code> reachable
	 *         from <code>startVertex</code> using the given
	 *         <code>pathDescription</code>
	 */
	// public <T extends Vertex> List<T> reachableVertices(Vertex startVertex,
	// String pathDescription, Class<T> vertexType);// old

	/**
	 * Returns an {@link Iterable} which iterates over all vertices of this
	 * {@link Graph} in the order determined by the vertex sequence.
	 * 
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getVertices() throws RemoteException;

	/**
	 * Returns an Iterable which iterates over all vertices of this Graph which
	 * have the specified <code>vertexClass</code> (including subclasses), in
	 * the order determined by the vertex sequence.
	 * 
	 * @param vertexclass
	 *            a VertexClass (i.e. instance of schema.VertexClass)
	 * 
	 * @return an Iterable for all vertices of the specified
	 *         <code>vertexClass</code>
	 */
	public Iterable<Vertex> getVertices(VertexClass vertexclass) throws RemoteException;

	/**
	 * Returns an Iterable which iterates over all vertices of this Graph which
	 * have the specified <code>vertexClass</code> (including subclasses), in
	 * the order determined by the vertex sequence.
	 * 
	 * @param vertexClass
	 *            a VertexClass (i.e. an M1 interface extending Vertex)
	 * 
	 * @return a iterable for all vertices of the specified
	 *         <code>vertexClass</code>
	 */
	public Iterable<Vertex> getVertices(Class<? extends Vertex> vertexClass) throws RemoteException;

	/**
	 * Optimizes edge and vertex ids such that after defragmentation
	 * getMaxECount() == getECount() and getMaxVCount() == getVCount(). That
	 * means that gaps in the vertex and edge IDs are deleted (defragmented) and
	 * that the internal arrays are shortened such that they hold exactly the
	 * required number of vertices/edges.
	 * 
	 * <b>Attention:</b> defragment() possibly changes vertex and edge IDs! *
	 * <b>Attention:</b> Not supported within when using transactions!
	 * @throws RemoteException 
	 */
	public void defragment() throws RemoteException;

	/**
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cls
	 *            the class for the generic type of the list
	 * @return
	 */
	public <T> JGraLabList<T> createList() throws RemoteException;

	/**
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cls
	 *            the class for the generic type of the list
	 * @param collection
	 * @return
	 */
	public <T> JGraLabList<T> createList(Collection<? extends T> collection) throws RemoteException;

	/**
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cls
	 *            the class for the generic type of the list
	 * @param initialCapacity
	 * @return
	 */
	public <T> JGraLabList<T> createList(int initialCapacity) throws RemoteException;

	/**
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cls
	 *            the class for the generic type of the set
	 * @return
	 */
	public <T> JGraLabSet<T> createSet() throws RemoteException;

	/**
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cls
	 *            the class for the generic type of the set
	 * @param collection
	 * @return
	 */
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection) throws RemoteException;

	/**
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cls
	 *            the class for the generic type of the set
	 * @param initialCapacity
	 * @return
	 */
	public <T> JGraLabSet<T> createSet(int initialCapacity) throws RemoteException;

	/**
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cls
	 *            the class for the generic type of the set
	 * @param initialCapacity
	 * @param loadFactor
	 * @return
	 */
	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor) throws RemoteException;

	/**
	 * 
	 * @param <K>
	 *            the generic type for the key
	 * @param <V>
	 *            the generic type for the value
	 * @param key
	 *            the class for the generic type of the key
	 * @param value
	 *            the class for the generic type of the value
	 * @return
	 */
	public <K, V> JGraLabMap<K, V> createMap() throws RemoteException;

	/**
	 * 
	 * @param <K>
	 *            the generic type for the key
	 * @param <V>
	 *            the generic type for the value
	 * @param key
	 *            the class for the generic type of the key
	 * @param value
	 *            the class for the generic type of the value
	 * @param map
	 * @return
	 */
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map) throws RemoteException;

	/**
	 * 
	 * @param key
	 *            the class for the generic type of the key
	 * @param value
	 *            the class for the generic type of the value
	 * @param initialCapacity
	 * @return
	 */
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity) throws RemoteException;

	/**
	 * 
	 * @param <K>
	 *            the generic type for the key
	 * @param <V>
	 *            the generic type for the value
	 * @param key
	 *            the class for the generic type of the key
	 * @param value
	 *            the class for the generic type of the value
	 * @param initialCapacity
	 * @param loadFactor
	 * @return
	 */
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) throws RemoteException;

	/**
	 * Generic creation of records.
	 * 
	 * @param <T>
	 *            *
	 * @param recordClass
	 * @param io
	 * @return
	 */
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) throws RemoteException;

	/**
	 * 
	 * @param <T>
	 * @param recordClass
	 * @param io
	 * @return
	 */
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) throws RemoteException;

	/**
	 * 
	 * @param <T>
	 * @param recordClass
	 * @param io
	 * @return
	 * @throws RemoteException 
	 */
	public <T extends Record> T createRecord(Class<T> recordClass,
			Object... components) throws RemoteException;

	/**
	 * Sorts the vertex sequence according to the given comparator in ascending
	 * order.
	 * 
	 * @param comp
	 *            the comparator defining the desired vertex order.
	 */
	public void sortVertices(Comparator<Vertex> comp) throws RemoteException;

	/**
	 * Sorts the edge sequence according to the given comparator in ascending
	 * order.
	 * 
	 * @param comp
	 *            the comparator defining the desired edge order.
	 */
	public void sortEdges(Comparator<Edge> comp) throws RemoteException;

	/**
	 * Registers the given <code>newListener</code> to the internal listener
	 * list.
	 * 
	 * @param newListener
	 *            the new <code>GraphStructureChangedListener</code> to
	 *            register.
	 */
	public void addGraphStructureChangedListener(
			GraphStructureChangedListener newListener) throws RemoteException;

	/**
	 * Removes the given <code>listener</code> from the internal listener list.
	 * 
	 * @param listener
	 *            the <code>GraphStructureChangedListener</code> to be removed.
	 */
	public void removeGraphStructureChangedListener(
			GraphStructureChangedListener listener) throws RemoteException;

	/**
	 * Removes all <code>GraphStructureChangedListener</code> from the internal
	 * listener list.
	 */
	public void removeAllGraphStructureChangedListeners() throws RemoteException;

	/**
	 * Returns the amount of registered
	 * <code>GraphStructureChangedListener</code>s.
	 * 
	 * @return the amount of registered
	 *         <code>GraphStructureChangedListener</code>s
	 */
	public int getGraphStructureChangedListenerCount() throws RemoteException;

	public GraphFactory getGraphFactory() throws RemoteException;

	/**
	 * @return true if this graph is a part of <code>other</code> either
	 *         directly as a subordinate or partial one or indirectly by its
	 *         parent
	 */
	public boolean isPartOfGraph(Graph other) throws RemoteException;

	/**
	 * @returns the ID of this partial or complete graph.
	 */
	public int getPartialGraphId() throws RemoteException;

	/**
	 * Adds a partial graph on the given host to the sequence of partial graphs
	 * and returns a local proxy
	 * 
	 * @param hostname
	 *            name of the host running the remote JGraLab instance
	 * @return a local proxy object for the created partial graph
	 * @throws RemoteException 
	 */
	public Graph createPartialGraph(String hostname) throws RemoteException;

	public void writePartialGraphs(GraphIO graphIO);



	public DiskStorageManager getDiskStorage();
	
	
	/**
	 * Checks if the given id <code>id</code> is the id of an local
	 * element or a remote one
	 */
	public boolean isIdOfLocalElement(int id);

	/**
	 * Retrieves the partial graph with the given id partialGraphId
	 */
	public Graph getPartialGraph(int partialGraphId);

	public Collection<? extends Graph> getPartialGraphs();

	
}
