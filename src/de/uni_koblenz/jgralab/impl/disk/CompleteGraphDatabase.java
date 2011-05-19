package de.uni_koblenz.jgralab.impl.disk;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.JGraLabServerRemoteInterface;
import de.uni_koblenz.jgralab.schema.GraphClass;

public class CompleteGraphDatabase extends GraphDatabase {

	/*
	 * Stores the hostnames of the partial graphs
	 */
	private String[] hostnames;
	
	private List<Integer> freePartialGraphIds;
	
	
	
	
	protected CompleteGraphDatabase(CompleteOrPartialGraphImpl localGraph, String hostname) {
		super(localGraph);
		hostnames = new String[MAX_NUMBER_OF_PARTIAL_GRAPHS];
		hostnames[0] = hostname;
		freePartialGraphIds = new LinkedList<Integer>();
		for (int i=0; i<MAX_NUMBER_OF_PARTIAL_GRAPHS; i++) {
			freePartialGraphIds.add(i);
		}
	}
	
	protected String getHostname(int id) {
		return hostnames[id];
	}


	@Override
	public int getFreePartialGraphId() {
		if (freePartialGraphIds.size() > 0)
			return freePartialGraphIds.remove(0);
		else
			throw new RuntimeException("There is no free partial graph id");
	}
	
	@Override
	public void releasePartialGraphId(int partialGraphId) {
		freePartialGraphIds.add(partialGraphId);
	}

	/**
	 * Registers the partial graph with the given id <code>id</code> which is stored on the
	 * host with the name <code>hostname</code>
	 * @param id
	 * @param hostname
	 */
	public void registerPartialGraph(int id, String hostname) {
		if (hostnames[id] == null) {
			hostnames[id] = hostname;
		} else {
			throw new RuntimeException("There is already a graph with the id " + id + " registered");
		}
	}
	
	

	public Graph createPartialGraph(GraphClass gc, String hostname) {
		int partialGraphId = getFreePartialGraphId();
		JGraLabServerRemoteInterface remoteServer = server.getRemoteInstance(hostname);
		GraphDatabase p = remoteServer.createGraph(gc.getId(), localGraph.getCompleteGraphUid(), partialGraphId, this.hostnames[0]);
		partialGraphDatabases[partialGraphId] = p;
		return p.getGraphObject(partialGraphId); 
	}

	@Override
	public void deletePartialGraph(int partialGraphId) {
		throw new RuntimeException("Not yet implemented");
	}
	

	public void edgeListModified() {
		edgeListVersion++;
		graphModified();
	}
	
	public void vertexListModified() {
		vertexListVersion++;
		graphModified();
	}
	
	
	public void graphModified() {
		graphVersion++;
	}
	
	
	/* **************************************************************************
	 * Methods to access traversal context
	 * **************************************************************************/
	
	/**
	 * Stores the traversal context of each {@link Thread} working on this
	 * {@link Graph}.
	 */
	private HashMap<Thread, Stack<Graph>> traversalContextMap;

	public Graph getTraversalContext() {
		if (traversalContextMap == null)
			return null;
		Stack<Graph> stack = traversalContextMap.get(Thread.currentThread());
		if (stack == null || stack.isEmpty()) {
			return getCompleteGraphObject();
		} else {
			return stack.peek();
		}
	}
	
	public void releaseTraversalContext() {
		if (traversalContextMap == null)
			return;
		Stack<Graph> stack = this.traversalContextMap.get(Thread.currentThread());
		if (stack != null) {
			stack.pop();
			if (stack.isEmpty()) {
				traversalContextMap.remove(Thread.currentThread());
				if (traversalContextMap.isEmpty())
					traversalContextMap = null;
			}
		}	
	}
	
	public void setTraversalContext(Graph traversalContext) {
		if (this.traversalContextMap == null)
			this.traversalContextMap = new HashMap<Thread, Stack<Graph>>();
		Stack<Graph> stack = this.traversalContextMap.get(Thread.currentThread());
		if (stack == null) {
			stack = new Stack<Graph>();
			this.traversalContextMap.put(Thread.currentThread(), stack);
		}
		stack.add(traversalContext);
		
	}




	public void graphModified(int graphId) {
		graphVersion++;
	}
	
	
	@Override
	public Graph createPartialGraph(String hostname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph loadRemotePartialGraph(String hostname, int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
