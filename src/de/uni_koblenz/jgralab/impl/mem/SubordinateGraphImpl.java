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
package de.uni_koblenz.jgralab.impl.mem;

import java.io.IOException;
import java.util.List;

import org.pcollections.PMap;
import org.pcollections.PSet;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl;
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
public abstract class SubordinateGraphImpl extends
		de.uni_koblenz.jgralab.impl.mem.GraphBaseImpl implements
		GraphStructureChangedListener {

	@SuppressWarnings("rawtypes")
	private GraphElement  containingElement;

	// TODO: Check if the respective methods are really
	// needed in the graph interface and how to ensure, that
	// the variables reflect the number of elements in the subgraphs
	// implemented methods of GraphStructureChangeListener to react on
	// adding/deletion of vertices

	private int vCount;

	private int eCount;

	private int iCount;
	
	private int subgraphId;

	@SuppressWarnings("rawtypes")
	@Override
	public AttributedElement getParentGraphOrElement() {
		return containingElement;
	}

	@Override
	public long getVCount() {
		return vCount;
	}

	@Override
	public long getECount() {
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
		return getParentGraph().getVertexListVersion();
	}

	@Override
	public long getEdgeListVersion() {
		return getParentGraph().getEdgeListVersion();
	}

	/**
	 * TODO GraphClass == containingElement.getType()?
	 * 
	 * @param containingVertex
	 *            {@link Vertex} which contains this subordinate graph
	 */
	@SuppressWarnings({ "rawtypes" })
	protected SubordinateGraphImpl(Vertex containingVertex) {
		super(containingVertex.getGraph().getType());
	//	System.out.println("Creating subordinate graph in vertex " + containingVertex.getLocalId());
		initializeCommonFields(containingVertex);
		subgraphId = (int) containingVertex.getGlobalId();
		// System.out.println("Initializing subordinate graph " + this);
		
		Vertex currentV = containingVertex.getNextVertex((Graph) null);
		//System.out.println("Sigma of next v : " + currentV.getSigma().getLocalId());
		//System.out.println("Contains: " + ((GraphElementImpl<?, ?, ?>) currentV).isChildOf(containingElement));
		while (currentV != null && ((GraphElementImpl) currentV).isChildOf(containingElement)) {
		//	System.out.println("Adding vertex: " + currentV);
			if (firstVertex == null) {
				firstVertex = (VertexImpl) currentV;
			}
			// System.out.println("  Iterating vertex " + current);
			lastVertex = (VertexImpl) currentV;
			vCount++;
			currentV = currentV.getNextVertex((Graph) null);
		}
		// System.out.println("Iterating edges");
		// initialize edges
		Edge currentE = containingVertex.getGraph().getFirstEdge();
		while (currentE != null
				&& !((GraphElementImpl) currentE)
						.isChildOf(containingElement)) {
			currentE = currentE.getNextEdge();
		}
		if (currentE != null) {
			firstEdge = (EdgeImpl) currentE;
			do {
			//	System.out.println("Adding edge: " + currentE.getLocalId());
				lastEdge = (EdgeImpl) currentE;
				eCount++;
				iCount += currentE.getDegree();
				currentE = currentE.getNextEdge();
			} while (currentE != null
					&& ((GraphElementImpl) currentE)
							.isChildOf(containingElement));
		}
		getCompleteGraph().addGraphStructureChangedListener(this);
	}

	/**
	 * TODO GraphClass == containingElement.getType()?
	 * 
	 * @param containingEdge
	 *            {@link Edge} which contains this subordinate graph
	 */
	@SuppressWarnings("rawtypes")
	protected SubordinateGraphImpl(Edge containingEdge) {
		super(containingEdge.getGraph().getType());
		subgraphId = (int) (Integer.MAX_VALUE - containingEdge.getGlobalId());
		initializeCommonFields(containingEdge);

		// initialize edges
		for (Edge current = containingEdge.getNextEdge(); current != null
				&& ((GraphElementImpl) current)
						.isChildOf(containingElement); current.getNextEdge()) {
			if (getFirstEdge() == null) {
				setFirstEdge((EdgeImpl) current);
			}
			setLastEdge((EdgeImpl) current);
			eCount++;
			iCount += current.getDegree();
		}

		// initialize vertices
		Vertex current = containingEdge.getGraph().getFirstVertex();
		while (current != null
				&& !((GraphElementImpl) current)
						.isChildOf(containingElement)) {
			current = current.getNextVertex();
		}
		if (current != null) {
			setFirstVertex((VertexImpl) current);
			do {
				setLastVertex((VertexImpl) current);
				vCount++;
				current = current.getNextVertex();
			} while (current != null
					&& ((GraphElementImpl) current)
							.isChildOf(containingElement));
		}
		getCompleteGraph().addGraphStructureChangedListener(this);
	}

	@SuppressWarnings("rawtypes")
	private void initializeCommonFields(GraphElement containingElement) {
		this.containingElement = containingElement;
	}

	@Override
	public <T> PVector<T> createList() {
		return containingElement.getGraph().createList();
	}


	@Override
	public <T> PSet<T> createSet() {
		return containingElement.getGraph().createSet();
	}


	@Override
	public <K, V> PMap<K, V> createMap() {
		return containingElement.getGraph().createMap();
	}



	@Override
	public Graph getView(int kappa) {
		return containingElement.getGraph().getGraphFactory()
				.createViewGraph_InMemoryStorage(this, kappa);
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
		return containingElement.getAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object data)
			throws NoSuchAttributeException {
		containingElement.setAttribute(name, data);
	}

	@Override
	public boolean isLoading() {
		return false;
	}


	@SuppressWarnings("rawtypes")
	@Override
	public boolean containsVertex(Vertex v) {
		return ((GraphElementImpl) v)
				.isChildOf((GraphElement) getParentGraphOrElement());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean containsEdge(Edge e) {
		return ((GraphElementImpl) e)
				.isChildOf((GraphElement) getParentGraphOrElement());
	}

	@Override
	public void deleteVertex(Vertex v) {
		if (containsVertex(v)) {
			containingElement.getGraph().deleteVertex(v);
		} else {
			throw new GraphException("The subordinate graph of "
					+ getParentGraphOrElement().getGlobalId()
					+ " does not contain vertex " + v.getGlobalId() + ".");
		}
	}

	@Override
	public void deleteEdge(Edge e) {
		if (containsEdge(e)) {
			containingElement.getGraph().deleteEdge(e);
		} else {
			throw new GraphException("The subordinate graph of "
					+ getParentGraphOrElement().getGlobalId()
					+ " does not contain edge " + e.getGlobalId() + ".");
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Vertex getVertex(long id) {
		Vertex v = containingElement.getGraph().getVertex(id);
		if (((GraphElementImpl) v).isChildOf((GraphElement) getParentGraphOrElement())) {
			return v;
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Edge getEdge(long id) {
		Edge e = containingElement.getGraph().getEdge(id);
		if (((GraphElementImpl) e).isChildOf((GraphElement) getParentGraphOrElement())) {
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
	public String getUniqueGraphId() {
		return getCompleteGraph().getUniqueGraphId();
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
	public long getICount() {
		return iCount;
	}

	@Override
	public GraphFactory getGraphFactory() {
		return getCompleteGraph().getGraphFactory();
	}

	@Override
	public void graphModified() {
		getParentGraph().graphModified();
	}

	@Override
	public void vertexListModified() {
		getParentGraph().vertexListModified();
	}

	@Override
	public void edgeListModified() {
		getParentGraph().edgeListModified();
	}


	@Override
	public GraphBaseImpl getCompleteGraph() {
		return getParentGraph().getCompleteGraph();
	}
	

	public GraphBaseImpl getParentGraph() {
		return (GraphBaseImpl) containingElement.getGraph();
	}

	@Override
	public Graph getViewedGraph() {
		return this;
	}

	@Override
	public long getGraphVersion() {
		return getParentGraph().getGraphVersion();
	}

	@Override
	public boolean isPartOfGraph(Graph other) {
		return other == getParentGraph()
				|| getParentGraph().isPartOfGraph(other);
	}

	@Override
	public boolean containsVertexLocally(Vertex v) {
		return getParentGraph().containsVertexLocally(v);
	}

	@Override
	public boolean containsEdgeLocally(Edge e) {
		return getParentGraph().containsEdgeLocally(e);
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

	@Override
	public void vertexAdded(Vertex v) {
		if (containsVertex(v)) {
			vCount++;
			if (v.getPreviousVertex() == getParentGraphOrElement()) {
				// this is a new first vertex
				setFirstVertex((VertexImpl) v);
				if (getLastVertex() == null) {
					setLastVertex((VertexImpl) v);
				}
			} else if (v.getPreviousVertex() == getLastVertex()) {
				// this is a new last vertex
				setLastVertex((VertexImpl) v);
			}
		}
	}

	@Override
	public void vertexDeleted(Vertex v) {
		if (containsVertex(v)) {
			vCount--;
			if (getLastVertex() == getFirstVertex() && getFirstVertex() == v) {
				// this was the last vertex
				setLastVertex(null);
				setFirstVertex(null);
			} else {
				if (getLastVertex() == v) {
					setLastVertex((VertexImpl) v.getPreviousVertex());
				}
				if (getFirstVertex() == v) {
					setFirstVertex((VertexImpl) v.getNextVertex());
				}
			}
		}
	}

	@Override
	public void edgeAdded(Edge e) {
		if (containsEdge(e)) {
			eCount++;
			if (e.getPreviousEdge() == getParentGraphOrElement()) {
				// this is a new first edge
				setFirstEdge((EdgeImpl) e);
				if (getLastEdge() == null) {
					setLastEdge((EdgeImpl) e);
				} else if (e.getPreviousEdge() == getLastEdge()) {
					// this is a new last edge
					setLastEdge((EdgeImpl) e);
				}
			}
		}
	}

	@Override
	public void edgeDeleted(Edge e) {
		if (containsEdge(e)) {
			eCount--;
			if (getLastEdge() == getFirstEdge() && getFirstEdge() == e) {
				// this was the last edge
				setLastEdge(null);
				setFirstEdge(null);
			} else {
				if (getLastEdge() == e) {
					setLastEdge((EdgeImpl) e.getPreviousEdge());
				}
				if (getFirstEdge() == e) {
					setFirstEdge((EdgeImpl) e.getNextEdge());
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
			iCount++;
		}
	}

	@Override
	public void incidenceDeleted(Incidence i) {
		if (containsEdge(i.getEdge())) {
			iCount--;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int compareTo(Graph arg0) {
		if (getCompleteGraph() == arg0) {
			// each graph is smaller than the complete graph
			return -1;
		} else if (arg0.getParentGraphOrElement() != null) {
			// this is a SubordinateGraphImpl
			GraphElement ce = (GraphElement) arg0.getParentGraphOrElement();
			boolean isArg0Vertex = ce instanceof Vertex;
			boolean isThisVertex = getParentGraphOrElement() instanceof Vertex;
			if (isArg0Vertex && isThisVertex) {
				// both are vertices
				return ((Vertex) getParentGraphOrElement()).compareTo((Vertex) ce);
			} else if (!isArg0Vertex && !isThisVertex) {
				// both are edges
				return ((Edge) getParentGraphOrElement()).compareTo((Edge) ce);
			} else {
				// the subordinate graph of a vertex is greater
				return isThisVertex ? 1 : -1;
			}
		} else {
			// this is a ViewGraphImpl or PartialGraphImpl
			return -arg0.compareTo(this);
		}
	}

	@Override
	public int getPartialGraphId() {
		return getCompleteGraph().getPartialGraphId();
	}

	@Override
	public List<? extends Graph> getPartialGraphs() {
		throw new UnsupportedOperationException();
	}


	@Override
	public Graph getPartialGraph(int partialGraphId) {
		throw new UnsupportedOperationException();
	}


	@Override
	public long getGlobalId() {
		return getLocalId();
	}


	@Override
	public int getLocalId() {
		return subgraphId;
	}

	@Override
	public boolean isLocalElementId(long id) {
		return getCompleteGraph().isLocalElementId(id);
	}


	@Override
	public GraphDatabaseBaseImpl getGraphDatabase() {
		throw new UnsupportedOperationException();
	}


}
