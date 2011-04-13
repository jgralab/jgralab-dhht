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
package de.uni_koblenz.jgralab.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
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
 * @author ist@uni-koblenz.de
 */
public abstract class SubordinateGraphImpl extends
		de.uni_koblenz.jgralab.impl.GraphBaseImpl {

	private GraphElement<?, ?, ?> containingElement;

	// TODO: Check if the respective methods are really
	// needed in the graph interface and how to ensure, that
	// the variables reflect the number of elements in the subgraphs
	/*
	 * If a vertex or edge is added to this subordinate graph, these values are
	 * updated automatically. If an Incidence is created iCount is updated
	 * automatically.
	 */
	private int vCount;

	private int eCount;

	private int iCount;

	// those variables should not be kept in each subgraph but only in the
	// partial ones and the toplevel

	/**
	 * Holds the version of the vertex sequence. For every modification (e.g.
	 * adding/deleting a vertex or changing the vertex sequence) this version
	 * number is increased by 1. It is set to 0 when the graph is loaded.
	 */
	// private long vertexListVersion;

	/**
	 * Holds the version of the edge sequence. For every modification (e.g.
	 * adding/deleting an edge or changing the edge sequence) this version
	 * number is increased by 1. It is set to 0 when the graph is loaded.
	 */
	// private long edgeListVersion;

	@Override
	public GraphElement<?, ?, ?> getContainingElement() {
		return containingElement;
	}

	@Override
	public int getVCount() {
		return vCount;
	}

	@Override
	public int getECount() {
		return eCount;
	}

	@Override
	protected void setVCount(int count) {
		vCount = count;
	}

	@Override
	protected void setECount(int count) {
		eCount = count;
	}

	@Override
	protected void setICount(int count) {
		iCount = count;
	}

	@Override
	public long getVertexListVersion() {
		return getSuperordinateGraph().getVertexListVersion();
	}

	@Override
	public long getEdgeListVersion() {
		return getSuperordinateGraph().getEdgeListVersion();
	}

	/**
	 * TODO GraphClass == containingElement.getType()?
	 * 
	 * @param containingVertex
	 *            {@link Vertex} which contains this subordinate graph
	 */
	protected SubordinateGraphImpl(Vertex containingVertex) {
		super(containingVertex.getGraph()
				.getType());
		initializeCommonFields(containingVertex);

		// initialize vertices
		for (Vertex current = containingVertex.getNextVertex(); current != null
				&& ((GraphElementImpl<?, ?, ?>) current)
						.isChildOf(containingElement); current.getNextVertex()) {
			if (getFirstVertex() == null) {
				setFirstVertex((VertexImpl) current);
			}
			setLastVertex((VertexImpl) current);
			setVCount(getVCount() + 1);
		}

		// initialize edges
		Edge current = containingVertex.getGraph().getFirstEdge();
		while (current != null
				&& !((GraphElementImpl<?, ?, ?>) current)
						.isChildOf(containingElement)) {
			current = current.getNextEdge();
		}
		if (current != null) {
			setFirstEdge((EdgeImpl) current);
			do {
				setLastEdge((EdgeImpl) current);
				setECount(getECount() + 1);
				setICount(getICount() + current.getDegree());
				current = current.getNextEdge();
			} while (current != null
					&& ((GraphElementImpl<?, ?, ?>) current)
							.isChildOf(containingElement));
		}
	}

	/**
	 * TODO GraphClass == containingElement.getType()?
	 * 
	 * @param containingEdge
	 *            {@link Edge} which contains this subordinate graph
	 */
	protected SubordinateGraphImpl(Edge containingEdge) {
		super(containingEdge.getGraph()
				.getType());
		initializeCommonFields(containingEdge);

		// initialize edges
		for (Edge current = containingEdge.getNextEdge(); current != null
				&& ((GraphElementImpl<?, ?, ?>) current)
						.isChildOf(containingElement); current.getNextEdge()) {
			if (getFirstEdge() == null) {
				setFirstEdge((EdgeImpl) current);
			}
			setLastEdge((EdgeImpl) current);
			setECount(getECount() + 1);
			setICount(getICount() + current.getDegree());
		}

		// initialize vertices
		Vertex current = containingEdge.getGraph().getFirstVertex();
		while (current != null
				&& !((GraphElementImpl<?, ?, ?>) current)
						.isChildOf(containingElement)) {
			current = current.getNextVertex();
		}
		if (current != null) {
			setFirstVertex((VertexImpl) current);
			do {
				setLastVertex((VertexImpl) current);
				setVCount(getVCount() + 1);
				current = current.getNextVertex();
			} while (current != null
					&& ((GraphElementImpl<?, ?, ?>) current)
							.isChildOf(containingElement));
		}
	}

	private void initializeCommonFields(GraphElement<?, ?, ?> containingElement) {
		this.containingElement = containingElement;
	}

	@Override
	public <T> JGraLabList<T> createList() {
		return containingElement.getGraph().createList();
	}

	@Override
	public <T> JGraLabList<T> createList(Collection<? extends T> collection) {
		return containingElement.getGraph().createList(collection);
	}

	@Override
	public <T> JGraLabList<T> createList(int initialCapacity) {
		return containingElement.getGraph().createList(initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet() {
		return containingElement.getGraph().createSet();
	}

	@Override
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection) {
		return containingElement.getGraph().createSet(collection);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity) {
		return containingElement.getGraph().createSet(initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor) {
		return containingElement.getGraph().createSet(initialCapacity,
				loadFactor);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap() {
		return containingElement.getGraph().createMap();
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map) {
		return containingElement.getGraph().createMap(map);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity) {
		return containingElement.getGraph().createMap(initialCapacity);
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) {
		return containingElement.getGraph().createMap(initialCapacity,
				loadFactor);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) {
		return containingElement.getGraph().createRecord(recordClass, io);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) {
		return containingElement.getGraph().createRecord(recordClass, fields);
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Object... components) {
		return containingElement.getGraph().createRecord(recordClass,
				components);
	}

	@Override
	public Graph getView(int kappa) {
		// TODO check if this is correct
		return containingElement.getGraph().getGraphFactory()
				.createViewGraph(containingElement.getGraph(), kappa);
	}

	@Override
	public void readAttributeValueFromString(String attributeName, String value)
			throws GraphIOException, NoSuchAttributeException {
		readAttributeValueFromString(attributeName, value);
	}

	@Override
	public String writeAttributeValueToString(String attributeName)
			throws IOException, GraphIOException, NoSuchAttributeException {
		return containingElement.writeAttributeValueToString(attributeName);
	}

	@Override
	public void writeAttributeValues(GraphIO io) throws IOException,
			GraphIOException {
		containingElement.writeAttributeValues(io);
	}

	@Override
	public void readAttributeValues(GraphIO io) throws GraphIOException {
		containingElement.readAttributeValues(io);
	}

	@Override
	public Object getAttribute(String name) throws NoSuchAttributeException {
		return containingElement.getAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object data)
			throws NoSuchAttributeException {
		containingElement.setAttribute(name, data);
	}

	@Override
	public boolean isLoading() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void loadingCompleted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsVertex(Vertex v) {
		return ((GraphElementImpl<?, ?, ?>) v)
				.isChildOf(getContainingElement());
	}

	@Override
	public boolean containsEdge(Edge e) {
		return ((GraphElementImpl<?, ?, ?>) e)
				.isChildOf(getContainingElement());
	}

	@Override
	public void deleteVertex(Vertex v) {
		if (containsVertex(v)) {
			containingElement.getGraph().deleteVertex(v);
		} else {
			throw new GraphException("The subordinate graph of "
					+ getContainingElement().getId()
					+ " does not contain vertex " + v.getId() + ".");
		}
	}

	@Override
	public void deleteEdge(Edge e) {
		if (containsEdge(e)) {
			containingElement.getGraph().deleteEdge(e);
		} else {
			throw new GraphException("The subordinate graph of "
					+ getContainingElement().getId()
					+ " does not contain edge " + e.getId() + ".");
		}
	}

	@Override
	public Vertex getVertex(int id) {
		Vertex v = containingElement.getGraph().getVertex(id);
		if (((GraphElementImpl<?, ?, ?>) v).isChildOf(getContainingElement())) {
			return v;
		} else {
			return null;
		}
	}

	@Override
	public Edge getEdge(int id) {
		Edge e = containingElement.getGraph().getEdge(id);
		if (((GraphElementImpl<?, ?, ?>) e).isChildOf(getContainingElement())) {
			return e;
		} else {
			return null;
		}
	}

	@Override
	public int getMaxVCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMaxECount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getUid() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void defragment() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Schema getSchema() {
		return containingElement.getGraph().getSchema();
	}

	@Override
	public <T extends BinaryEdge> T createEdge(Class<T> cls, Vertex alpha,
			Vertex omega) {
		T edge = super.createEdge(cls, alpha, omega);
		containingElement.addSubordinateElement(edge);
		return edge;
	}

	@Override
	public <T extends Edge> T createEdge(Class<T> cls) {
		T edge = super.createEdge(cls);
		containingElement.addSubordinateElement(edge);
		return edge;
	}

	@Override
	public <T extends Vertex> T createVertex(Class<T> cls) {
		T vertex = super.createVertex(cls);
		containingElement.addSubordinateElement(vertex);
		return vertex;
	}

	@Override
	public int getICount() {
		return iCount;
	}

	@Override
	public GraphFactory getGraphFactory() {
		return getCompleteGraph().getGraphFactory();
	}

	@Override
	public void graphModified() {
		getSuperordinateGraph().graphModified();
	}

	@Override
	public void vertexListModified() {
		getSuperordinateGraph().vertexListModified();
	}

	@Override
	public void edgeListModified() {
		getSuperordinateGraph().edgeListModified();
	}

	@Override
	public GraphBaseImpl getParentDistributedGraph() {
		return this;
	}

	@Override
	public GraphBaseImpl getSuperordinateGraph() {
		return (GraphBaseImpl) containingElement.getGraph();
	}

	@Override
	public GraphBaseImpl getCompleteGraph() {
		return getSuperordinateGraph().getCompleteGraph();
	}

	@Override
	public Graph getViewedGraph() {
		return this;
	}

	@Override
	public long getGraphVersion() {
		return getSuperordinateGraph().getGraphVersion();
	}

	@Override
	public boolean isPartOfGraph(Graph other) {
		return other == getSuperordinateGraph()
				|| getSuperordinateGraph().isPartOfGraph(other);
	}

	@Override
	public boolean containsVertexLocally(Vertex v) {
		return getSuperordinateGraph().containsVertexLocally(v);
	}

	@Override
	public boolean containsEdgeLocally(Edge e) {
		return getSuperordinateGraph().containsEdgeLocally(e);
	}

	// TODO: Check if these methods should return the type of the vertex or edge
	// the subordinate graph is embedded in
	@Override
	public GraphClass getGraphClass() {
		return getSuperordinateGraph().getGraphClass();
	}

	public Class<? extends Graph> getM1Class() {
		return getSuperordinateGraph().getM1Class();
	}

	public void update(Vertex newLastVertex) {
		if (getFirstVertex() == null) {
			setFirstVertex((VertexImpl) newLastVertex);
		}
		setLastVertex((VertexImpl) newLastVertex);
		setVCount(getVCount() + 1);
	}

	public void update(Edge newLastEdge) {
		if (getFirstEdge() == null) {
			setFirstEdge((EdgeImpl) newLastEdge);
		}
		setLastEdge((EdgeImpl) newLastEdge);
		setECount(getECount() + 1);
		setICount(getICount() + newLastEdge.getDegree());
	}

	public void update(Incidence newIncidence) {
		setICount(getICount() + 1);
	}
}
