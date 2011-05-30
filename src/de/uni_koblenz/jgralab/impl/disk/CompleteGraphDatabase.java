package de.uni_koblenz.jgralab.impl.disk;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.RemoteJGraLabServer;
import de.uni_koblenz.jgralab.impl.mem.CompleteGraphImpl;
import de.uni_koblenz.jgralab.schema.GraphClass;
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
	public void registerPartialGraph(int id, String hostname) {
		if (hostnames[id] == null) {
			hostnames[id] = hostname;
		} else {
			throw new RuntimeException("There is already a graph with the id "
					+ id + " registered");
		}
	}

	public Graph createPartialGraph(GraphClass gc, String hostname) {
		int partialGraphId = getFreePartialGraphId();
		RemoteJGraLabServer remoteServer = localJGraLabServer.getRemoteInstance(hostname);
		RemoteGraphDatabaseAccess p = remoteServer.getGraphDatabase(uniqueGraphId);
		partialGraphDatabases.put(partialGraphId, (RemoteGraphDatabaseAccessWithInternalMethods) p); 
		return getGraphObject(convertToGlobalId(1));
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
	public long getIncidenceListVersion(long elementId) {
		int partialGraphId = getPartialGraphId(elementId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getIncidenceListVersion(convertToLocalId(elementId));
	}

	@Override
	public void increaseIncidenceListVersion(long elementId,
			long incidenceListVersion) {
		int partialGraphId = getPartialGraphId(elementId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		diskStore.increaseIncidenceListVersion(convertToLocalId(elementId));
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
	public long getFirstIncidenceId(long elementId) {
		int partialGraphId = getPartialGraphId(elementId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getFirstIncidenceId(convertToLocalId(elementId));
	}

	@Override
	public long getLastIncidenceId(long elementId) {
		int partialGraphId = getPartialGraphId(elementId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getLastIncidenceId(convertToLocalId(elementId));
	}

	@Override
	public long getNextIncidenceIdAtVertexId(long incId) {
		int partialGraphId = getPartialGraphId(incId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getNextIncidenceIdAtVertexId(convertToLocalId(incId));
	}

	@Override
	public long getPreviousIncidenceIdAtVertexId(long incId) {
		int partialGraphId = getPartialGraphId(incId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getPreviousIncidenceIdAtVertexId(convertToLocalId(incId));
	}

	@Override
	public long getNextIncidenceIdAtEdgeId(long incId) {
		int partialGraphId = getPartialGraphId(incId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getNextIncidenceIdAtEdgeId(convertToLocalId(incId));
	}

	@Override
	public long getPreviousIncidenceIdAtEdgeId(long incId) {
		int partialGraphId = getPartialGraphId(incId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getPreviousIncidenceIdAtEdgeId(convertToLocalId(incId));
	}

	@Override
	public long connect(Integer incidenceClassId, long vertexId, long edgeId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDirection(long incId, Direction dir) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteIncidence(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> JGraLabList<T> createList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> JGraLabList<T> createList(Collection<? extends T> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> JGraLabList<T> createList(int initialCapacity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> JGraLabList<T> createList(int initialCapacity, float loadFactor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(int initialCapacity,
			float loadFactor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> JGraLabMap<K, V> createMap(Map<? extends K, ? extends V> map) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGraphVersion(long graphVersion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void incidenceListModified(long elementId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIncidentEdgeId(long incId, long edgeId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIncidentVertexId(long incId, long vertexId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerRemotePartialGraph(int id, String hostname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int createPartialGraph(Class<? extends Graph> graphClass,
			String hostname) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setIncidenceListVersion(long elementId,
			long incidenceListVersion) {
		// TODO Auto-generated method stub
		
	}


}
