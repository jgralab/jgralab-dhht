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

package de.uni_koblenz.jgralab.impl.diskv2;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.RemoteJGraLabServer;
import de.uni_koblenz.jgralab.impl.ParentEntityKind;
import de.uni_koblenz.jgralab.impl.RemoteGraphDatabaseAccess;
import de.uni_koblenz.jgralab.impl.RemoteGraphDatabaseAccessWithInternalMethods;
import de.uni_koblenz.jgralab.schema.Schema;

public class CompleteGraphDatabaseImpl extends GraphDatabaseBaseImpl implements
		RemoteGraphDatabaseAccessWithInternalMethods {

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
		super(schema, uniqueGraphId, 0, TOPLEVEL_PARTIAL_GRAPH_ID);
		hostnames = new String[MAX_NUMBER_OF_PARTIAL_GRAPHS];
		hostnames[GraphDatabaseElementaryMethods.TOPLEVEL_PARTIAL_GRAPH_ID] = hostname;
		freePartialGraphIds = new LinkedList<Integer>();
		for (int i = GraphDatabaseElementaryMethods.TOPLEVEL_PARTIAL_GRAPH_ID + 1; i < MAX_NUMBER_OF_PARTIAL_GRAPHS; i++) {
			freePartialGraphIds.add(i);
		}
		completeGraphAttributes = new HashMap<String, Object>();

		// creates toplevel graph
		GraphData data = new GraphData();
		data.globalSubgraphId = convertToGlobalId(GraphDatabaseElementaryMethods.TOPLEVEL_LOCAL_SUBGRAPH_ID);
		data.typeId = schema.getClassId(schema.getGraphClass());
		localSubgraphData.add(data);
		getGraphObject(data.globalSubgraphId);
	}

	public ParentEntityKind getParentEntityKind(long globalSubgraphId) {
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			try {
				return getGraphDatabase(partialGraphId).getParentEntityKind(
						globalSubgraphId);
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
		}
		int localSubgraphId = convertToLocalId(globalSubgraphId);
		// for the local toplevel graph, the value is stored in the
		// kindOfParentElement flag
		// of the partial graph database
		if (localSubgraphId == TOPLEVEL_LOCAL_SUBGRAPH_ID) {
			return null;
		}
		// a non-toplevel graph is implemented by subordinate graph
		// and thus nested either in a vertex or a edge, which is
		// encoded by the sign of its sigma value

		return getGraphData(localSubgraphId).containingElementId < 0 ? ParentEntityKind.EDGE
				: ParentEntityKind.VERTEX;
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
	public long createPartialGraphInEdge(long parentGlobalEntityId,
			String remoteHostname) {
		return getToplevelGraphForPartialGraphId(internalCreatePartialGraphInEntity(
				remoteHostname, parentGlobalEntityId, ParentEntityKind.EDGE));
	}

	@Override
	public long createPartialGraphInVertex(long parentGlobalEntityId,
			String remoteHostname) {
		return getToplevelGraphForPartialGraphId(internalCreatePartialGraphInEntity(
				remoteHostname, parentGlobalEntityId, ParentEntityKind.VERTEX));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.impl.disk.CompleteGraphDatabaseRemoteAccess#
	 * internalCreatePartialGraphInEntity(long, java.lang.String,
	 * de.uni_koblenz.jgralab.impl.disk.PartialGraphDatabase.ParentEntity)
	 */
	@Override
	public int internalCreatePartialGraphInEntity(String remoteHostname,
			long parentGlobalEntityId, ParentEntityKind entityKind) {
		RemoteJGraLabServer remoteServer = localJGraLabServer
				.getRemoteInstance(remoteHostname);
		String localHostname = getHostname(getLocalPartialGraphId());
		int partialGraphId = allocatePartialGraphId();
		registerPartialGraph(partialGraphId, remoteHostname);
		RemoteGraphDatabaseAccess p;
		try {
			p = remoteServer.createPartialGraphDatabase(
					schema.getQualifiedName(), uniqueGraphId, localHostname,
					parentGlobalEntityId, entityKind, partialGraphId, ImplementationType.DISK);
		} catch (Exception e) {
			throw new RuntimeException(
					"Cannot create remote graph database of host "
							+ remoteHostname, e);
		}

		partialGraphDatabases.put(partialGraphId,
				(RemoteGraphDatabaseAccessWithInternalMethods) p);
		return partialGraphId;
	}

	// for central graph database
	public int loadPartialGraph(String hostname) {
		RemoteJGraLabServer remoteServer = localJGraLabServer
				.getRemoteInstance(hostname);
		RemoteGraphDatabaseAccess p;
		try {
			p = remoteServer.getGraphDatabase(uniqueGraphId, ImplementationType.DISK);
			int partialGraphId = p.getLocalPartialGraphId();
			partialGraphDatabases.put(partialGraphId,
					(RemoteGraphDatabaseAccessWithInternalMethods) p);
			return partialGraphId;
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	// private void releasePartialGraphId(int partialGraphId) {
	// freePartialGraphIds.add(partialGraphId);
	// }

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

	//inherited from GraphDatabaseBaseImpl
	//public void incidenceListOfVertexModified(long elementId)

	
	//inherited from GraphDatabaseBaseImpl
	//public void incidenceListOfEdgeModified(long elementId)


	/* **************************************************************************
	 * Methods to access traversal context
	 * *************************************************************************
	 */

	/**
	 * Stores the traversal context of each {@link Thread} working on this
	 * {@link Graph}.
	 */
	private HashMap<Thread, Stack<Graph>> traversalContextMap;

	public long getTraversalContextSubgraphId() {
		if (getTraversalContext() == null)
			return GLOBAL_GRAPH_ID;
		return getTraversalContext().getGlobalId();
	}

	@Override
	public Graph getTraversalContext() {
		if (traversalContextMap == null) {
			return getGraphObject(GraphDatabaseElementaryMethods.GLOBAL_GRAPH_ID);
		}
		Stack<Graph> stack = traversalContextMap.get(Thread.currentThread());
		if (stack == null || stack.isEmpty()) {
			return getGraphObject(GraphDatabaseElementaryMethods.GLOBAL_GRAPH_ID);
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


	//inherited from GraphDatabaseBaseImpl
	//public long getFirstIncidenceIdAtVertexId(long vertexId) 

	//inherited from GraphDatabaseBaseImpl
	//public long getFirstIncidenceIdAtEdgeId(long edgeId) 


	//inherited from GraphDatabaseBaseImpl
	//public long getLastIncidenceIdAtVertexId(long vertexId) 


	//inherited from GraphDatabaseBaseImpl
	//public long getLastIncidenceIdAtEdgeId(long edgeId) 
	

	@Override
	public void setGraphVersion(long graphVersion) {
		this.graphVersion = graphVersion;
	}

	public Object getGraphAttribute(String attributeName) {
		// check if the graph has such an attribute
		return completeGraphAttributes.get(attributeName);
	}

	@Override
	public void setGraphAttribute(String attributeName, Object data) {
		// check if the graph has such an attribute
		completeGraphAttributes.put(attributeName, data);
	}

	public void setTraversalContext(long globalSubgraphId) {
		setTraversalContext(getGraphObject(globalSubgraphId));
	}





}
