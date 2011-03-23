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
package de.uni_koblenz.jgralab.impl.std;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.EdgeBaseImpl;
import de.uni_koblenz.jgralab.impl.FreeIndexList;
import de.uni_koblenz.jgralab.impl.VertexBaseImpl;
import de.uni_koblenz.jgralab.schema.GraphClass;

/**
 * The implementation of a <code>SubordninateGraph</code> accessing attributes
 * without versioning.
 * 
 * @author ist@uni-koblenz.de
 */
public class SubordinateGraphImpl extends
		de.uni_koblenz.jgralab.impl.GraphBaseImpl {
	private VertexBaseImpl[] vertex;
	private int vCount;
	private EdgeBaseImpl[] edge;
	private int eCount;
	private VertexBaseImpl firstVertex;
	private VertexBaseImpl lastVertex;
	private EdgeBaseImpl firstEdge;
	private EdgeBaseImpl lastEdge;
	private final GraphElement<?, ?, ?> containingElement;

	/**
	 * Holds the version of the vertex sequence. For every modification (e.g.
	 * adding/deleting a vertex or changing the vertex sequence) this version
	 * number is increased by 1. It is set to 0 when the graph is loaded.
	 */
	private long vertexListVersion;

	/**
	 * Holds the version of the edge sequence. For every modification (e.g.
	 * adding/deleting an edge or changing the edge sequence) this version
	 * number is increased by 1. It is set to 0 when the graph is loaded.
	 */
	private long edgeListVersion;

	/**
	 * List of vertices to be deleted by a cascading delete caused by deletion
	 * of a composition "parent".
	 */
	private List<VertexBaseImpl> deleteVertexList;

	@Override
	public GraphElement<?, ?, ?> getContainingElement() {
		return containingElement;
	}

	@Override
	protected VertexBaseImpl[] getVertexArray() {
		// TODO
		return vertex;
	}

	@Override
	public int getVCount() {
		// TODO
		return vCount;
	}

	@Override
	protected EdgeBaseImpl[] getEdgeArray() {
		// TODO
		return edge;
	}

	@Override
	public int getECount() {
		// TODO
		return eCount;
	}

	@Override
	public Vertex getFirstVertex() {
		// TODO
		return firstVertex;
	}

	@Override
	public Vertex getLastVertex() {
		// TODO
		return lastVertex;
	}

	@Override
	public Edge getFirstEdge() {
		// TODO
		return firstEdge;
	}

	@Override
	public Edge getLastEdge() {
		// TODO
		return lastEdge;
	}

	@Override
	protected FreeIndexList getFreeVertexList() {
		// TODO
		return freeVertexList;
	}

	@Override
	protected FreeIndexList getFreeEdgeList() {
		// TODO
		return freeEdgeList;
	}

	@Override
	protected void setVertex(VertexBaseImpl[] vertex) {
		// TODO
		this.vertex = vertex;
	}

	@Override
	protected void setVCount(int count) {
		// TODO
		vCount = count;
	}

	@Override
	protected void setEdge(EdgeBaseImpl[] edge) {
		// TODO
		this.edge = edge;
	}

	@Override
	protected void setECount(int count) {
		// TODO
		eCount = count;
	}

	@Override
	protected void setFirstVertex(VertexBaseImpl firstVertex) {
		// TODO
		this.firstVertex = firstVertex;
	}

	@Override
	protected void setLastVertex(VertexBaseImpl lastVertex) {
		// TODO
		this.lastVertex = lastVertex;
	}

	@Override
	protected void setFirstEdge(EdgeBaseImpl firstEdge) {
		// TODO
		this.firstEdge = firstEdge;
	}

	@Override
	protected void setLastEdge(EdgeBaseImpl lastEdge) {
		// TODO
		this.lastEdge = lastEdge;
	}

	@Override
	protected List<VertexBaseImpl> getDeleteVertexList() {
		// TODO
		return deleteVertexList;
	}

	@Override
	protected void setDeleteVertexList(List<VertexBaseImpl> deleteVertexList) {
		// TODO
		this.deleteVertexList = deleteVertexList;
	}

	@Override
	protected void setVertexListVersion(long vertexListVersion) {
	}

	@Override
	public long getVertexListVersion() {
		return containingElement.getGraph().getVertexListVersion();
	}

	@Override
	protected void setEdgeListVersion(long edgeListVersion) {
	}

	@Override
	public long getEdgeListVersion() {
		return containingElement.getGraph().getEdgeListVersion();
	}

	/**
	 * TODO GraphClass = containingElement.getType()
	 * 
	 * @param containingElement
	 *            {@link GraphElement} which contains this subordinate graph
	 */
	protected SubordinateGraphImpl(GraphElement<?, ?, ?> containingElement) {
		super(containingElement.getGraph().getId(), containingElement
				.getGraph().getType());
		this.containingElement = containingElement;
	}

	// @Override
	// public void abort() {
	// throw new UnsupportedOperationException(
	// "Abort is not supported for this graph.");
	// }
	//
	// @Override
	// public void commit() {
	// throw new UnsupportedOperationException(
	// "Commit is not supported for this graph.");
	// }
	//
	// @Override
	// public Transaction newReadOnlyTransaction() {
	// throw new UnsupportedOperationException(
	// "Creation of read-only-transactions is not supported for this graph.");
	// }
	//
	// @Override
	// public Transaction newTransaction() {
	// throw new UnsupportedOperationException(
	// "Creation of read-write-transactions is not supported for this graph.");
	// }
	//
	// @Override
	// public Savepoint defineSavepoint() {
	// throw new UnsupportedOperationException(
	// "Definition of save-points is not supported for this graph.");
	// }
	//
	// @Override
	// public Transaction getCurrentTransaction() {
	// throw new UnsupportedOperationException(
	// "Transactions are not supported for this graph.");
	// }
	//
	// @Override
	// public void restoreSavepoint(Savepoint savepoint) {
	// throw new UnsupportedOperationException(
	// "Definition of save-points is not supported for this graph.");
	// }
	//
	// @Override
	// public void setCurrentTransaction(Transaction transaction) {
	// throw new UnsupportedOperationException(
	// "Transactions are not supported for this graph.");
	// }
	//
	// @Override
	// public boolean isInConflict() {
	// throw new UnsupportedOperationException(
	// "Transactions are not supported for this graph.");
	// }

	@Override
	public Graph getCompleteGraph() {
		return containingElement.getGraph();
	}

	@Override
	protected int allocateVertexIndex(int currentId) {
		// TODO
		int vId = freeVertexList.allocateIndex();
		if (vId == 0) {
			expandVertexArray(getExpandedVertexCount());
			vId = freeVertexList.allocateIndex();
		}
		return vId;
	}

	@Override
	protected int allocateEdgeIndex(int currentId) {
		// TODO
		int eId = freeEdgeList.allocateIndex();
		if (eId == 0) {
			expandEdgeArray(getExpandedEdgeCount());
			eId = freeEdgeList.allocateIndex();
		}
		return eId;
	}

	/*
	 * @Override protected void freeIndex(FreeIndexList freeIndexList, int
	 * index) { freeIndexList.freeIndex(index); }
	 */

	@Override
	protected void freeEdgeIndex(int index) {
		// TODO
		freeEdgeList.freeIndex(index);
	}

	@Override
	protected void freeVertexIndex(int index) {
		// TODO
		freeVertexList.freeIndex(index);
	}

	@Override
	protected void vertexAfterDeleted(Vertex vertexToBeDeleted) {
		// TODO
	}

	@Override
	protected void edgeAfterDeleted(Edge edgeToBeDeleted) {
		// TODO
	}

	@Override
	public final boolean hasStandardSupport() {
		return containingElement.getGraph().hasStandardSupport();
	}

	@Override
	public final boolean hasTransactionSupport() {
		return containingElement.getGraph().hasTransactionSupport();
	}

	@Override
	public final boolean hasSavememSupport() {
		return containingElement.getGraph().hasSavememSupport();
	}

	@Override
	public final boolean hasDatabaseSupport() {
		return containingElement.getGraph().hasDatabaseSupport();
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
	protected void addVertex(Vertex newVertex) {
		// TODO Auto-generated method stub
		super.addVertex(newVertex);
	}

	@Override
	protected void addEdge(Edge newEdge) {
		// TODO Auto-generated method stub
		super.addEdge(newEdge);
	}

	@Override
	public Graph getView(int kappa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readAttributeValueFromString(String attributeName, String value)
			throws GraphIOException, NoSuchAttributeException {
		readAttributeValueFromString(attributeName, value);
	}

	@Override
	public String writeAttributeValueToString(String attributeName)
			throws IOException, GraphIOException, NoSuchAttributeException {
		return containingElement.getGraph().writeAttributeValueToString(
				attributeName);
	}

	@Override
	public void writeAttributeValues(GraphIO io) throws IOException,
			GraphIOException {
		containingElement.getGraph().writeAttributeValues(io);
	}

	@Override
	public void readAttributeValues(GraphIO io) throws GraphIOException {
		containingElement.getGraph().readAttributeValues(io);
	}

	@Override
	public Object getAttribute(String name) throws NoSuchAttributeException {
		return containingElement.getGraph().getAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object data)
			throws NoSuchAttributeException {
		containingElement.getGraph().setAttribute(name, data);
	}

	@Override
	public Class<? extends Graph> getM1Class() {
		return containingElement.getGraph().getM1Class();
	}

	@Override
	public GraphClass getType() {
		return containingElement.getGraph().getType();
	}

}
