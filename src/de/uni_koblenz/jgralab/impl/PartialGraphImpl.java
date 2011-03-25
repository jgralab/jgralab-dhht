package de.uni_koblenz.jgralab.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;

public abstract class PartialGraphImpl extends CompleteOrPartialGraphImpl {
	
	/* holds the graph this partial graph belongs to */
	protected GraphBaseImpl completeGraph;
	

	boolean loading = false;

	protected PartialGraphImpl(String id, GraphClass cls, GraphBaseImpl completeGraph) {
		super(id, cls);
		this.completeGraph = completeGraph;
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
	public void graphModified() {
		graphVersion++;
		completeGraph.graphModified();
	}
	
	
	/**
	 * Changes the vertex sequence version of this graph. Should be called
	 * whenever the vertex list of this graph is changed, for instance by
	 * creation and deletion or reordering of vertices.
	 */
	protected void vertexListModified() {
		vertexListVersion++;
		graphVersion++;
		completeGraph.vertexListModified();
	}
	
	/**
	 * Changes the vertex sequence version of this graph. Should be called
	 * whenever the vertex list of this graph is changed, for instance by
	 * creation and deletion or reordering of vertices.
	 */
	protected void edgeListModified() {
		edgeListVersion++;
		graphVersion++;
		completeGraph.edgeListModified();
	}

	

	/*
	 *TODO: Should return true, if e is part of this graph or
	 * any of its partial graphs
	 */
	@Override
	public boolean containsEdge(Edge e) {
		return (e != null) && (e.getGraph() == this)
				&& containsEdgeId(((EdgeImpl) e).id);
	}



	/*
	 * TODO: Should return true, if v is part of this graph or
	 * any of its partial graphs
	 */
	@Override
	public boolean containsVertex(Vertex v) {
		VertexImpl[] vertex = getVertexArray();
		return (v != null) && (v.getGraph() == this)
				&& containsVertexId(((VertexImpl) v).id)
				&& (vertex[((VertexImpl) v).id] == v);
	}


	@Override
	public void deleteVertex(Vertex v) {
		
	}

	@Override
	public void deleteEdge(Edge e) {
		assert (e != null) && e.isValid() && containsEdge(e);
		internalDeleteEdge(e);
		edgeListModified();
	}



	@Override
	public String getUid() {
		return getCompleteGraph().getUid();
	}


	@Override
	public Schema getSchema() {
		// TODO Auto-generated method stub
		return null;
	}


}
