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
import java.util.Collection;
import java.util.Map;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * The implementation of a <code>SubordninateGraph</code> accessing attributes
 * without versioning.
 * 
 * TODO: firstEdge and firstVertex, respectively, are not needed but may be
 * determined by the containing element
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class SubordinateGraphImpl extends GraphBaseImpl implements
		GraphStructureChangedListener {

	public SubordinateGraphImpl(long globalSubgraphId,
			GraphDatabaseBaseImpl localGraphDatabase,
			RemoteGraphDatabaseAccess storingGraphDatabase) {
		super(globalSubgraphId, localGraphDatabase, storingGraphDatabase);
	}
	
	
	// ============================================================================
	// Methods to access schema 
	// ============================================================================
	
	@Override
	public Schema getSchema() {
		return localGraphDatabase.getSchema();
	}
	
	// TODO: Check if these methods should return the type of the vertex or edge
	// the subordinate graph is embedded in
	@Override
	public GraphClass getGraphClass() {
		return getParentGraph().getGraphClass();
	}

	public Class<? extends Graph> getM1Class() {
		return getParentGraph().getM1Class();
	}



	
	
	
	// ============================================================================
	// Methods to manage the current traversal context inherited from GraphBaseImpl
	// ============================================================================

	
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
		return localGraphDatabase.getGraphObject(GraphDatabaseElementaryMethods.GLOBAL_GRAPH_ID); 
	}

	public Graph getLocalPartialGraph() {
		return getParentGraph().getLocalPartialGraph();
	}


	@Override
	public Graph getPartialGraph(int partialGraphId) {
		return getGraphDatabase().getGraphObject(GraphDatabaseBaseImpl.getToplevelGraphForPartialGraphId(partialGraphId));
	}

	
	
	@SuppressWarnings("rawtypes")
	@Override
	public AttributedElement getParentGraphOrElement() {
		return localGraphDatabase.getGraphElementObject(storingGraphDatabase
				.getContainingElementId(globalSubgraphId));
	}
	
	
	@Override
	public Graph getParentGraph() {
		long containingElementId = storingGraphDatabase.getContainingElementId(globalSubgraphId);
		long superordinateGraphId = 0;
		if (containingElementId < 0)
			superordinateGraphId = localGraphDatabase.getEdgeObject(-containingElementId).getGraph().getGlobalId();
		else 
			superordinateGraphId = localGraphDatabase.getVertexObject(containingElementId).getGraph().getGlobalId();
		return localGraphDatabase.getGraphObject(superordinateGraphId);
	}

	
	@Override
	public boolean isPartOfGraph(Graph other) {
		return other == getParentGraph() || getParentGraph().isPartOfGraph(other);
	}
	
	
	@Override
	public Graph getView(int kappa) {
		return localGraphDatabase.createViewGraph(this, kappa);
	}
	

	@Override
	public Graph getViewedGraph() {
		return this;
	}
	
	
	//inherited from GraphBaseImpl
	//public Graph createPartialGraphInGraph(String hostnameOfPartialGraph)
	
	//inherited from GraphBaseImpl
	//public List<? extends Graph> getPartialGraphs();
	

	//inherited from GraphBaseImpl
	//public Graph getPartialGraph(int partialGraphId);
	
	//inherited from GraphBaseImpl
	//@Deprecated
	//public void savePartialGraphs(GraphIO graphIO);
	
	
	
	// ============================================================================
	// Methods to access ids inherited from GraphBaseImpl
	// ============================================================================

	
	// ============================================================================
	// Methods to access vertices and edges of the graph
	// ============================================================================

	@SuppressWarnings("rawtypes")
	@Override
	public <T extends Vertex> T createVertex(Class<T> cls) {
		T vertex = super.createVertex(cls);
		((GraphElement)getParentGraphOrElement()).addSubordinateElement(vertex);
		return vertex;
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public <T extends Edge> T createEdge(Class<T> cls) {
		T edge = super.createEdge(cls);
		((GraphElement)getParentGraphOrElement()).addSubordinateElement(edge);
		return edge;
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public <T extends BinaryEdge> T createEdge(Class<T> cls, Vertex alpha,
			Vertex omega) {
		T edge = super.createEdge(cls, alpha, omega);
		((GraphElement)getParentGraphOrElement()).addSubordinateElement(edge);
		return edge;
	}


	//inherited from GraphBaseImpl
	//public <T extends Incidence> T connect(Class<T> cls, Vertex vertex, Edge edge)

	
	@Override
	public boolean containsVertex(Vertex v) {
		return ((GraphElementImpl<?, ?, ?>) v)
				.isChildOf((GraphElement<?, ?, ?>) getParentGraphOrElement());
	}
	
	@Override
	public boolean containsVertexLocally(Vertex v) {
		return isLocalElementId(v.getGlobalId()) && 
		  (getVertexObjectForId(GraphDatabaseElementaryMethods.convertToLocalId(v.getGlobalId())) == v); 
	}

	@Override
	public boolean containsEdge(Edge e) {
		return ((GraphElementImpl<?, ?, ?>) e)
				.isChildOf((GraphElement<?, ?, ?>) getParentGraphOrElement());
	}
	
	@Override
	public boolean containsEdgeLocally(Edge e) {
		return isLocalElementId(e.getGlobalId()) && 
		  (getEdgeObjectForId(GraphDatabaseElementaryMethods.convertToLocalId(e.getGlobalId())) == e); 

	}
	
	//inherited by GraphBaseImpl
	//public boolean containsElement(@SuppressWarnings("rawtypes") GraphElement elem);

	
	
	@Override
	public void deleteVertex(Vertex v) {
		if (containsVertex(v)) {
			storingGraphDatabase.deleteVertex(v.getGlobalId());
		} else {
			throw new GraphException("The subordinate graph of "
					+ getParentGraphOrElement().getGlobalId()
					+ " does not contain vertex " + v.getGlobalId() + ".");
		}
	}

	@Override
	public void deleteEdge(Edge e) {
		if (containsEdge(e)) {
			storingGraphDatabase.deleteEdge(e.getGlobalId());
		} else {
			throw new GraphException("The subordinate graph of "
					+ getParentGraphOrElement().getGlobalId()
					+ " does not contain edge " + e.getGlobalId() + ".");
		}
	}
	
	
	// ============================================================================
	// getFirstVertex and getFirstEdge methods are inherited from GraphBaseImpl
	// ============================================================================
	
	
	@Override
	public Vertex getVertex(long id) {
		Vertex v = localGraphDatabase.getVertexObject(id);
		if (((GraphElementImpl<?, ?, ?>) v).isChildOf((GraphElement<?, ?, ?>) getParentGraphOrElement())) {
			return v;
		} else {
			return null;
		}
	}

	@Override
	public Edge getEdge(long id) {
		Edge e = localGraphDatabase.getEdgeObject(id);
		if (((GraphElementImpl<?, ?, ?>) e).isChildOf((GraphElement<?, ?, ?>) getParentGraphOrElement())) {
			return e;
		} else {
			return null;
		}
	}

	@Override
	public long getMaxVCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getMaxECount() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public long getMaxICount() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public long getVCount() {
		return storingGraphDatabase.getVCount(globalSubgraphId);
	}

	@Override
	public long getECount() {
		return storingGraphDatabase.getECount(globalSubgraphId);
	}
	
	@Override
	public long getICount() {
		return storingGraphDatabase.getICount(globalSubgraphId);
	}
	
	
	// ============================================================================
	// getVertices() and getEdges() methods are inherited from GraphBaseImpl
	// ============================================================================

	
	// ============================================================================
	// sortVertices() and sortEdges() methods are inherited from GraphBaseImpl
	// ============================================================================
	
	@Override
	public void defragment() {
		throw new UnsupportedOperationException();
	}
	
	
	
	
	// ============================================================================
	// Methods to handle graph listeners are inherited from GraphBaseImpl
	// ============================================================================
	
	
	// ============================================================================
	// Methods to access graph state and version (loading etc.)
	// ============================================================================

	
	@Override
	public boolean isLoading() {
		return getCompleteGraph().isLoading();
	}
	
	
	@Override
	public void graphModified() {
		((GraphBaseImpl) getParentGraph()).graphModified();
	}
	

	@Override
	public long getGraphVersion() {
		return getParentGraph().getGraphVersion();
	}
	
	
	//Inherited from GraphBaseImpl
	//public boolean isVertexListModified(long vertexListVersion);
	
	
	@Override
	public long getVertexListVersion() {
		return getParentGraph().getVertexListVersion();
	}
	

	@Override
	public void vertexListModified() {
		((GraphBaseImpl) getParentGraph()).vertexListModified();
	}
	
	
	//Inherited from GraphBaseImpl
	//public boolean isEdgeListModified(long edgeListVersion);

	
	@Override
	public long getEdgeListVersion() {
		return getParentGraph().getEdgeListVersion();
	}

	
	@Override
	public void edgeListModified() {
		((GraphBaseImpl) getParentGraph()).edgeListModified();
	}


	//Inherited from GraphBaseImpl
	//public GraphFactory getGraphFactory();


	//Inherited from GraphBaseImpl
	//public GraphDatabaseBaseImpl getGraphDatabase();


	

	@Override
	public int getPartialGraphId() {
		return getCompleteGraph().getPartialGraphId();
	}
	
	
	
	
	
	// ============================================================================
	// Methods to create complex values such as lists and maps
	// ============================================================================
	
	
	
	
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
	public <K, V> JGraLabMap<K, V> createMap() {
		return storingGraphDatabase.createMap();
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map) {
		return storingGraphDatabase.createMap(map);
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



	
	
	// ============================================================================
	// Methods which are created by the code generator for the complete graph
	// ============================================================================

	
	
	@Override
	public void readAttributeValueFromString(String attributeName, String value)
			throws GraphIOException, NoSuchAttributeException {
		readAttributeValueFromString(attributeName, value);
	}

	@Override
	public String writeAttributeValueToString(String attributeName)
			throws IOException, GraphIOException, NoSuchAttributeException {
		throw new UnsupportedOperationException(
				"writeAttributeValues may not be called on a SubordinateGraph");
	}

	@Override
	public void writeAttributeValues(GraphIO io) throws IOException,
			GraphIOException {
		throw new UnsupportedOperationException(
				"writeAttributeValues may not be called on a SubordinateGraph");
	}

	@Override
	public void readAttributeValues(GraphIO io) throws GraphIOException {
		throw new UnsupportedOperationException(
				"writeAttributeValues may not be called on a SubordinateGraph");
	}

	@Override
	public Object getAttribute(String name) throws NoSuchAttributeException {
		return getParentGraphOrElement().getAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object data)
			throws NoSuchAttributeException {
		getParentGraphOrElement().setAttribute(name, data);
	}
	
	
	
	
	
	
	// ============================================================================
	// Methods of SubordinateGraph as GraphListener on the complete graph
	// ============================================================================
	
	
	
	
	
	@Override
	public void vertexAdded(Vertex v) {
		if (containsVertex(v)) {
			storingGraphDatabase.increaseVCount(globalSubgraphId);
			if (v.getPreviousVertex() == getParentGraphOrElement()) {
				// this is a new first vertex
				storingGraphDatabase.setFirstVertexId(globalSubgraphId, v.getGlobalId());
				if (getLastVertex() == null) {
					storingGraphDatabase.setLastVertexId(globalSubgraphId, v.getGlobalId());
				}
			} else if (v.getPreviousVertex() == getLastVertex()) {
				// this is a new last vertex
				storingGraphDatabase.setLastVertexId(globalSubgraphId, v.getGlobalId());
			}
		}
	}

	@Override
	public void vertexDeleted(Vertex v) {
		if (containsVertex(v)) {
			storingGraphDatabase.decreaseVCount(globalSubgraphId);
			if (getLastVertex() == getFirstVertex() && getFirstVertex() == v) {
				// this was the last vertex
				storingGraphDatabase.setLastVertexId(globalSubgraphId, 0);
				storingGraphDatabase.setFirstVertexId(globalSubgraphId, 0);
			} else {
				if (getLastVertex() == v) {
					storingGraphDatabase.setLastVertexId(globalSubgraphId, v.getPreviousVertex().getGlobalId());
				}
				if (getFirstVertex() == v) {
					storingGraphDatabase.setFirstVertexId(globalSubgraphId, v.getNextVertex().getGlobalId());
				}
			}
		}
	}

	@Override
	public void edgeAdded(Edge e) {
		if (containsEdge(e)) {
			storingGraphDatabase.increaseECount(globalSubgraphId);
			if (e.getPreviousEdge() == getParentGraphOrElement()) {
				// this is a new first edge
				storingGraphDatabase.setFirstEdgeId(globalSubgraphId, e.getGlobalId());
				if (getLastEdge() == null) {
					storingGraphDatabase.setLastEdgeId(globalSubgraphId, e.getGlobalId());
				} else if (e.getPreviousEdge() == getLastEdge()) {
					// this is a new last edge
					storingGraphDatabase.setLastEdgeId(globalSubgraphId, e.getGlobalId());
				}
			}
		}
	}

	@Override
	public void edgeDeleted(Edge e) {
		if (containsEdge(e)) {
			storingGraphDatabase.decreaseECount(globalSubgraphId);
			if (getLastEdge() == getFirstEdge() && getFirstEdge() == e) {
				// this was the last edge
				storingGraphDatabase.setLastEdgeId(globalSubgraphId, 0);
				storingGraphDatabase.setFirstEdgeId(globalSubgraphId, 0);
			} else {
				if (getLastEdge() == e) {
					storingGraphDatabase.setLastEdgeId(globalSubgraphId, e.getPreviousEdge().getGlobalId());
				}
				if (getFirstEdge() == e) {
					storingGraphDatabase.setFirstEdgeId(globalSubgraphId, e.getNextEdge().getGlobalId());
				}
			}
		}
	}

	@Override
	public void maxVertexCountIncreased(int newValue) {
	}

	@Override
	public void maxEdgeCountIncreased(int newValue) {
	}

	@Override
	public void maxIncidenceCountIncreased(int newValue) {
	}

	@Override
	public void incidenceAdded(Incidence i) {
		if (containsEdge(i.getEdge())) {
			storingGraphDatabase.increaseICount(globalSubgraphId);
		}
	}

	@Override
	public void incidenceDeleted(Incidence i) {
		if (containsEdge(i.getEdge())) {
			storingGraphDatabase.decreaseICount(globalSubgraphId);
		}
	}
	
}
