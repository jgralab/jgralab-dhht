package de.uni_koblenz.jgralab.impl.disk;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.RemoteJGraLabServer;
import de.uni_koblenz.jgralab.impl.disk.PartialGraphDatabase.ParentEntity;
import de.uni_koblenz.jgralab.schema.Schema;

public class CompleteGraphDatabaseImpl extends GraphDatabaseBaseImpl {

	private static final int MAX_NUMBER_OF_PARTIAL_GRAPHS = 500;

	/*
	 * Stores the hostnames of the partial graphs
	 */
	private final String[] hostnames;

	private final List<Integer> freePartialGraphIds;
	
	/**
	 * Stores the attributes of the complete graph
	 */
	private Map<String, Object> completeGraphAttributes;

	public CompleteGraphDatabaseImpl(Schema schema, String uniqueGraphId,
			String hostname) {
		super(schema, uniqueGraphId, 0, 1);
		hostnames = new String[MAX_NUMBER_OF_PARTIAL_GRAPHS];
		hostnames[0] = hostname;
		freePartialGraphIds = new LinkedList<Integer>();
		for (int i = 0; i < MAX_NUMBER_OF_PARTIAL_GRAPHS; i++) {
			freePartialGraphIds.add(i);
		}
		completeGraphAttributes = new HashMap<String, Object>();
	}
	


	@Override
	public String getHostname(int id) {
		return hostnames[id];
	}

	private int allocatePartialGraphId() {
		if (freePartialGraphIds.size() > 0) {
			return freePartialGraphIds.remove(0);
		} else {
			throw new RuntimeException("There is no free partial graph id");
		}
	}
	
	@Override
	public long createPartialGraphInGraph(long parentGlobalEntityId, String remoteHostname) {
		return internalCreatePartialGraphInEntity(remoteHostname, parentGlobalEntityId, ParentEntity.GRAPH);
	}
	
	@Override
	public long createPartialGraphInEdge(long parentGlobalEntityId, String remoteHostname) {
		return internalCreatePartialGraphInEntity(remoteHostname, parentGlobalEntityId, ParentEntity.EDGE);
	}
	
	@Override
	public long createPartialGraphInVertex(long parentGlobalEntityId, String remoteHostname) {
		return internalCreatePartialGraphInEntity(remoteHostname, parentGlobalEntityId, ParentEntity.VERTEX);
	}
		
	/* (non-Javadoc)
	 * @see de.uni_koblenz.jgralab.impl.disk.CompleteGraphDatabaseRemoteAccess#internalCreatePartialGraphInEntity(long, java.lang.String, de.uni_koblenz.jgralab.impl.disk.PartialGraphDatabase.ParentEntity)
	 */
	@Override
	public int internalCreatePartialGraphInEntity(String remoteHostname, long parentGlobalEntityId, ParentEntity entityKind) {
		RemoteJGraLabServer remoteServer = localJGraLabServer.getRemoteInstance(remoteHostname);
		String localHostname =  getHostname(getLocalPartialGraphId());
		int partialGraphId = allocatePartialGraphId();
		RemoteGraphDatabaseAccess p;
		try {
			p = remoteServer.createPartialGraphDatabase(
											schema.getQualifiedName(), uniqueGraphId, localHostname, parentGlobalEntityId, entityKind, partialGraphId);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot create remote graph database of host " + remoteHostname,e );
		}

		partialGraphDatabases.put(partialGraphId, (RemoteGraphDatabaseAccessWithInternalMethods) p);
		return partialGraphId;
	}
	
	//for central graph database
	public int loadPartialGraph(String hostname) {
		RemoteJGraLabServer remoteServer = localJGraLabServer.getRemoteInstance(hostname);
		RemoteGraphDatabaseAccess p = remoteServer.getGraphDatabase(uniqueGraphId);
		int partialGraphId = p.getLocalPartialGraphId();
		partialGraphDatabases.put(partialGraphId, (RemoteGraphDatabaseAccessWithInternalMethods) p);
		return partialGraphId;
	}
	
	private void releasePartialGraphId(int partialGraphId) {
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
		diskStore
				.increaseIncidenceListVersionOfVertexId(convertToLocalId(elementId));
	}

	@Override
	public void incidenceListOfEdgeModified(long elementId) {
		int partialGraphId = getPartialGraphId(elementId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		diskStore
				.increaseIncidenceListVersionOfEdgeId(convertToLocalId(elementId));
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
	public long getFirstIncidenceIdAtVertexId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore
				.getFirstIncidenceIdAtVertexId(convertToLocalId(vertexId));
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
		return diskStore
				.getLastIncidenceIdAtVertexId(convertToLocalId(vertexId));
	}

	@Override
	public long getLastIncidenceIdAtEdgeId(long edgeId) {
		int partialGraphId = getPartialGraphId(edgeId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getLastIncidenceIdAtEdgeId(convertToLocalId(edgeId));
	}

	@Override
	public void setGraphVersion(long graphVersion) {
		this.graphVersion = graphVersion;
	}





	public Object getGraphAttribute(String attributeName) {
		//check if the graph has such an attribute
		return completeGraphAttributes.get(attributeName);
	}
	@Override
	public void setGraphAttribute(String attributeName, Object data) {
		// check if the graph has such an attribute
		completeGraphAttributes.put(attributeName, data);
	}



			

}
