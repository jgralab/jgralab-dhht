package de.uni_koblenz.jgralab.impl;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ProgressFunction;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * Implements a partial Graph as member of a distributed complete one
 * 
 * TODO: - Implement add and delete edge/vertex operations with correct linking
 * on next/previous vertex in global graph -
 * 
 * @author dbildh
 * 
 */
public abstract class PartialGraphImpl extends CompleteOrPartialGraphImpl {

	/* holds the graph this partial graph belongs to */
	protected GraphBaseImpl completeGraph;

	boolean loading = false;

	protected PartialGraphImpl(GraphClass cls, GraphBaseImpl completeGraph)
			throws RemoteException {
		super(cls);
		this.completeGraph = completeGraph;
		id = ((CompleteGraphImpl) completeGraph.getCompleteGraph())
				.allocateFreePartialGraphId();
	}

	@Override
	public GraphBaseImpl getCompleteGraph() {
		return completeGraph;
	}

	/**
	 * Changes this graph's version. graphModified() is called whenever the
	 * graph is changed, all changes like adding, creating and reordering of
	 * edges and vertices or changes of attributes of the graph, an edge or a
	 * vertex are treated as a change.
	 */
	@Override
	public void graphModified() throws RemoteException {
		graphVersion++;
		completeGraph.graphModified();
	}

	/**
	 * Changes the vertex sequence version of this graph. Should be called
	 * whenever the vertex list of this graph is changed, for instance by
	 * creation and deletion or reordering of vertices.
	 */
	@Override
	protected void vertexListModified() throws RemoteException {
		vertexListVersion++;
		graphVersion++;
		completeGraph.vertexListModified();
	}

	/**
	 * Changes the vertex sequence version of this graph. Should be called
	 * whenever the vertex list of this graph is changed, for instance by
	 * creation and deletion or reordering of vertices.
	 */
	@Override
	protected void edgeListModified() throws RemoteException {
		edgeListVersion++;
		graphVersion++;
		completeGraph.edgeListModified();
	}

	/*
	 * TODO: Should return true, if e is part of this graph or any of its
	 * partial graphs
	 */
	@Override
	public boolean containsEdge(Edge e) throws RemoteException {
		return (e != null) && (e.getGraph() == this)
				&& containsEdgeId(((EdgeImpl) e).id);
	}

	/*
	 * TODO: Should return true, if v is part of this graph or any of its
	 * partial graphs
	 */
	@Override
	public boolean containsVertex(Vertex v) throws RemoteException {
		VertexImpl[] vertex = getVertexArray();
		return (v != null) && (v.getGraph() == this)
				&& containsVertexId(((VertexImpl) v).id)
				&& (vertex[((VertexImpl) v).id] == v);
	}

	@Override
	public void deleteVertex(Vertex v) {
		// TODO
	}

	@Override
	public void deleteEdge(Edge e) throws RemoteException {
		assert (e != null) && e.isValid() && containsEdge(e);
		internalDeleteEdge(e);
		edgeListModified();
	}

	private void internalDeleteEdge(Edge e) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUid() throws RemoteException {
		return getCompleteGraph().getUid();
	}

	@Override
	public Schema getSchema() {
		return schema;
	}

	@Override
	public int compareTo(Graph o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void saveGraph(String filename, ProgressFunction pf,
			BooleanGraphMarker subGraph) throws GraphIOException {
		if (subGraph == null) {
			GraphIO.saveGraphToFile(filename, this, pf);
		} else {
			GraphIO.saveGraphToFile(filename, subGraph, pf);
		}
	}

}
