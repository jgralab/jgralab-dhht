package de.uni_koblenz.jgralab.impl.disk;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.RemoteJGraLabServer;
import de.uni_koblenz.jgralab.schema.Schema;

public class CompleteGraphDatabase extends GraphDatabaseBaseImpl {

	private static final int MAX_NUMBER_OF_PARTIAL_GRAPHS = 500;
 
	/*
	 * Stores the hostnames of the partial graphs
	 */
	private final String[] hostnames;

	private final List<Integer> freePartialGraphIds;

	public CompleteGraphDatabase(Schema schema, String uniqueGraphId, String hostname) {
		super(schema, uniqueGraphId, 0, 1); 
		hostnames = new String[MAX_NUMBER_OF_PARTIAL_GRAPHS];
		hostnames[0] = hostname;
		freePartialGraphIds = new LinkedList<Integer>();
		for (int i = 0; i < MAX_NUMBER_OF_PARTIAL_GRAPHS; i++) {
			freePartialGraphIds.add(i);
		}
	}

	@Override
	public String getHostname(int id) {
		return hostnames[id];
	}

	@Override
	public int getFreePartialGraphId() {
		if (freePartialGraphIds.size() > 0) {
			return freePartialGraphIds.remove(0);
		} else {
			throw new RuntimeException("There is no free partial graph id");
		}
	}


	public void releasePartialGraphId(int partialGraphId) {
		freePartialGraphIds.add(partialGraphId);
	}

	/**
	 * Registers the partial graph with the given id <code>id</code> which is
	 * stored on the host with the name <code>hostname</code>
	 * 
	 * @param id
	 * @param hostname
	 */
	@Override
	public void registerPartialGraph(int id, String hostname) {
		if (hostnames[id] == null) {
			hostnames[id] = hostname;
		} else {
			throw new RuntimeException("There is already a graph with the id "
					+ id + " registered");
		}
	}
	

	
	@Override
	public long createPartialGraph(Class<? extends Graph> gc, String hostname) {
		int partialGraphId = getFreePartialGraphId();
		RemoteJGraLabServer remoteServer = localJGraLabServer.getRemoteInstance(hostname);
		RemoteGraphDatabaseAccess p = remoteServer.getGraphDatabase(uniqueGraphId);
		partialGraphDatabases.put(partialGraphId, (RemoteGraphDatabaseAccessWithInternalMethods) p); 
		return getGraphObject(convertToGlobalId(1)).getGlobalSubgraphId();
	}

	@Override
	public void deletePartialGraph(int partialGraphId) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public void edgeListModified() {
		edgeListVersion++;
		graphModified();
	}

	@Override
	public void vertexListModified() {
		vertexListVersion++;
		graphModified();
	}

	@Override
	public void graphModified() {
		graphVersion++;
	}
	
	@Override
	public void incidenceListOfVertexModified(long elementId) {
		int partialGraphId = getPartialGraphId(elementId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		diskStore.increaseIncidenceListVersionOfVertexId(convertToLocalId(elementId));
	}
	
	@Override
	public void incidenceListOfEdgeModified(long elementId) {
		int partialGraphId = getPartialGraphId(elementId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		diskStore.increaseIncidenceListVersionOfEdgeId(convertToLocalId(elementId));
	}

	/* **************************************************************************
	 * Methods to access traversal context
	 * *************************************************************************
	 */

	/**
	 * Stores the traversal context of each {@link Thread} working on this
	 * {@link Graph}.
	 */
	private HashMap<Thread, Stack<Graph>> traversalContextMap;

	@Override
	public Graph getTraversalContext() {
		if (traversalContextMap == null) {
			return null;
		}
		Stack<Graph> stack = traversalContextMap.get(Thread.currentThread());
		if (stack == null || stack.isEmpty()) {
			return getGraphObject(GraphDatabaseBaseImpl.GLOBAL_GRAPH_ID);
		} else {
			return stack.peek();
		}
	}



	@Override
	public void releaseTraversalContext() {
		if (traversalContextMap == null) {
			return;
		}
		Stack<Graph> stack = this.traversalContextMap.get(Thread
				.currentThread());
		if (stack != null) {
			stack.pop();
			if (stack.isEmpty()) {
				traversalContextMap.remove(Thread.currentThread());
				if (traversalContextMap.isEmpty()) {
					traversalContextMap = null;
				}
			}
		}
	}

	@Override
	public void setTraversalContext(Graph traversalContext) {
		if (this.traversalContextMap == null) {
			this.traversalContextMap = new HashMap<Thread, Stack<Graph>>();
		}
		Stack<Graph> stack = this.traversalContextMap.get(Thread
				.currentThread());
		if (stack == null) {
			stack = new Stack<Graph>();
			this.traversalContextMap.put(Thread.currentThread(), stack);
		}
		stack.add(traversalContext);

	}

	@Override
	public void graphModified(int graphId) {
		graphVersion++;
	}


	@Override
	public int getGraphTypeId(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getGraphTypeId(subgraphId);
		}
		return getGraphData(convertToLocalId(subgraphId)).typeId;
 	}

	@Override
	public long getContainingElementId(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getGraphTypeId(subgraphId);
		}
		return getGraphData(convertToLocalId(subgraphId)).containingElementId;
	}



	@Override
	public void setVCount(long subgraphId, long count) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).setVCount(subgraphId, count);
		}
		getGraphData(convertToLocalId(subgraphId)).vertexCount = count;
	}

	@Override
	public long getECount(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getECount(subgraphId);
		}
		return getGraphData(convertToLocalId(subgraphId)).edgeCount;
	}

	@Override
	public void setECount(long subgraphId, long count) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).setECount(subgraphId, count);
		}
		getGraphData(convertToLocalId(subgraphId)).edgeCount = count;
	}

	@Override
	public long getMaxECount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public long getICount(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getICount(subgraphId);
		}
		return getGraphData(convertToLocalId(subgraphId)).incidenceCount;
	}

	
	@Override
	public long getIncidenceListVersionOfVertexId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getIncidenceListVersionOfEdgeId(convertToLocalId(vertexId));
	}

	@Override
	public long getIncidenceListVersionOfEdgeId(long edgeId) {
		int partialGraphId = getPartialGraphId(edgeId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getIncidenceListVersionOfVertexId(convertToLocalId(edgeId));
	}
	
	@Override
	public long getFirstIncidenceIdAtVertexId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getFirstIncidenceIdAtVertexId(convertToLocalId(vertexId));
	}
	
	@Override
	public long getFirstIncidenceIdAtEdgeId(long edgeId) {
		int partialGraphId = getPartialGraphId(edgeId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getFirstIncidenceIdAtEdgeId(convertToLocalId(edgeId));
	}

	@Override
	public long getLastIncidenceIdAtVertexId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getLastIncidenceIdAtVertexId(convertToLocalId(vertexId));
	}
	
	@Override
	public long getLastIncidenceIdAtEdgeId(long edgeId) {
		int partialGraphId = getPartialGraphId(edgeId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getLastIncidenceIdAtEdgeId(convertToLocalId(edgeId));
	}
	
	@Override
	public void deleteIncidence(long id) {
		removeIncidenceFromLambdaSeqOfEdge(id);
		removeIncidenceFromLambdaSeqOfVertex(id);
	}

	
	@Override
	public void setGraphVersion(long graphVersion) {
		this.graphVersion = graphVersion;
	}













}
