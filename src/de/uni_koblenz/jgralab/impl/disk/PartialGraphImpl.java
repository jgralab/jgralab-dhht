package de.uni_koblenz.jgralab.impl.disk;


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
 * TODO: Check if CompleteGraph and PartialGraph may be combined to onw class, since 
 * all aspects related to distribution are kept in the databases
 * 
 * @author dbildh
 * 
 */
public abstract class PartialGraphImpl extends CompleteOrPartialGraphImpl {

	boolean loading = false;

	protected PartialGraphImpl(GraphClass cls, GraphDatabase graphDatabase) {
		super(cls);
		//create local graph database
		this.graphDatabase = graphDatabase;
		id = graphDatabase.getLocalGraphId();
	}

	@Override
	public Graph getCompleteGraph() {
		return graphDatabase.getGraphObject(0);
	}

	/**
	 * Changes this graph's version. graphModified() is called whenever the
	 * graph is changed, all changes like adding, creating and reordering of
	 * edges and vertices or changes of attributes of the graph, an edge or a
	 * vertex are treated as a change.
	 */
	@Override
	public void graphModified() {
		graphDatabase.graphModified();
	}

	/**
	 * Changes the vertex sequence version of this graph. Should be called
	 * whenever the vertex list of this graph is changed, for instance by
	 * creation and deletion or reordering of vertices.
	 */
	@Override
	protected void vertexListModified() {
		graphDatabase.vertexListModified();
		graphDatabase.graphModified();
	}

	/**
	 * Changes the vertex sequence version of this graph. Should be called
	 * whenever the vertex list of this graph is changed, for instance by
	 * creation and deletion or reordering of vertices.
	 */
	@Override
	protected void edgeListModified() {
		graphDatabase.edgeListModified();
		graphDatabase.graphModified();
	}

	/*
	 * TODO: Should return true, if e is part of this graph or any of its
	 * partial graphs
	 */
	@Override
	public boolean containsEdge(Edge e) {
		return (e != null) && (e.getGraph() == this)
				&& containsEdgeId(((EdgeImpl) e).id);
	}

	/*
	 * TODO: Should return true, if v is part of this graph or any of its
	 * partial graphs
	 */
	@Override
	public boolean containsVertex(Vertex v) {
//TODO
		return false;
	}

	@Override
	public void deleteVertex(Vertex v) {
		// TODO
	}

	@Override
	public void deleteEdge(Edge e) {
		assert (e != null) && e.isValid() && containsEdge(e);
		internalDeleteEdge(e);
		edgeListModified();
	}

	private void internalDeleteEdge(Edge e) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCompleteGraphUid() {
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
