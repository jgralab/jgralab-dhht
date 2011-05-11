package de.uni_koblenz.jgralab.impl.disk;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
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
	protected Graph completeGraph;

	boolean loading = false;

	protected PartialGraphImpl(GraphClass cls, String uidOfCompleteGraph, String hostnameOfCompleteGraph) throws RemoteException {
		super(cls);
		//create local graph database
		graphDatabase = new PartialGraphDatabase(this, hostnameOfCompleteGraph);
		id = graphDatabase.getLocalGraphId();
		this.completeGraph = graphDatabase.getGraphObject(0);
		id = ((CompleteGraphImpl) completeGraph.getCompleteGraph())
				.allocateFreePartialGraphId();
	}

	@Override
	public Graph getCompleteGraph() {
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
//TODO
		return false;
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
	public String getCompleteGraphUid() throws RemoteException {
		return getCompleteGraph().getCompleteGraphUid();
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

}
