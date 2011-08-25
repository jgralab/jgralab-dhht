package de.uni_koblenz.jgralab.impl.disk;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.RemoteJGraLabServer;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.JGraLabSetImpl;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * A GraphDatabase stores one local complete or partial graph and provides an
 * uniform access to all partial graphs of the distributed graph the store
 * belongs to and all its elements based on the ids
 * 
 */

public abstract class GraphDatabaseBaseImpl extends
		GraphDatabaseElementaryMethods implements RemoteGraphDatabaseAccess {


	
	/**
	 * Creates a new graph database to store all local subgraphs of the complete
	 * graph identified by the given <code>uniqueGraphId</code>. All those local
	 * subgraphs have the same <code>partialGraphId</code> and belong to the
	 * specified <code>schema</code>.
	 * 
	 * @param schema
	 *            the schema of the graph whose subgraphs are represented by
	 *            this graph database
	 * @param uniqueGraphId
	 *            the unique id of the graph
	 * @param partialGraphId
	 *            the common partial graph id of all local subgraphs
	 */
	protected GraphDatabaseBaseImpl(Schema schema, String uniqueGraphId,
			long parentDistributedGraphId, int partialGraphId) {
		super(schema, uniqueGraphId, parentDistributedGraphId, partialGraphId);
	}
	
	
	
	
	@Override
	public long createLocalSubordinateGraphInVertex(long containingVertexId) {
		// get m1 class and free id
		Class<? extends Graph> m1Class = schema.getGraphClass().getM1Class();

		GraphData data = new GraphData();
		data.globalSubgraphId = convertToGlobalId(localSubgraphData.size());
		localSubgraphData.add(data);
		data.containingElementId = containingVertexId;

		// Graph subordinateGraph =
		// graphFactory.createSubordinateGraphDiskBasedStorage(data.globalSubgraphId);

		data.typeId = schema.getClassId(m1Class);
		data.vertexCount = 0;
		data.edgeCount = 0;
		return data.globalSubgraphId;
	}

	@Override
	public long createLocalSubordinateGraphInEdge(long containingEdged) {
		// get m1 class and free id
		Class<? extends Graph> m1Class = schema.getGraphClass().getM1Class();

		GraphData data = new GraphData();
		data.globalSubgraphId = convertToGlobalId(localSubgraphData.size());
		localSubgraphData.add(data);
		data.containingElementId = -containingEdged;

		// Graph subordinateGraph =
		// graphFactory.createSubordinateGraphDiskBasedStorage(data.globalSubgraphId);

		data.typeId = schema.getClassId(m1Class);
		data.vertexCount = 0;
		data.edgeCount = 0;
		return data.globalSubgraphId;
	}
	
		
	//for partial graph database
	@Override
	public int loadPartialGraph(String hostname) {
		RemoteGraphDatabaseAccessWithInternalMethods compDatabase = getGraphDatabase(GraphDatabaseElementaryMethods.TOPLEVEL_PARTIAL_GRAPH_ID);
		int partialGraphId = compDatabase.loadPartialGraph(hostname);
		RemoteJGraLabServer remoteServer = localJGraLabServer.getRemoteInstance(hostname);
		RemoteGraphDatabaseAccess p;
		try {
			p = remoteServer.getGraphDatabase(uniqueGraphId);
			partialGraphDatabases.put(partialGraphId, (RemoteGraphDatabaseAccessWithInternalMethods) p);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return partialGraphId;
	}
	
	

	
	@Override
	public long createPartialGraphInGraph(long parentGlobalEntityId, String remoteHostname) {
		return internalCreatePartialGraphInEntity(remoteHostname, parentGlobalEntityId, ParentEntityKind.GRAPH);
	}
	
	@Override
	public long createPartialGraphInEdge(long parentGlobalEntityId, String remoteHostname) {
		return internalCreatePartialGraphInEntity(remoteHostname, parentGlobalEntityId, ParentEntityKind.EDGE);
	}
	
	@Override
	public long createPartialGraphInVertex(long parentGlobalEntityId, String remoteHostname) {
		return internalCreatePartialGraphInEntity(remoteHostname, parentGlobalEntityId, ParentEntityKind.VERTEX);
	}
		
	
	
	
	
	
	/** returns the list of all partial graph ids directly or indirectly 
	 *  contained in the graph identified by the given globalSubgraphId
	 * @param globalSubgraphId
	 * @return
	 */
	@Override
	public List<Integer> getPartialGraphIds(long globalSubgraphId) {
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getPartialGraphIds(globalSubgraphId);
		}
		int localSubgraphId = convertToLocalId(globalSubgraphId);
		GraphData data = getGraphData(localSubgraphId);
		if (data.partialGraphs == null)
			return null;
		List<Integer> value = new LinkedList<Integer>();
		for (Integer pgId : data.partialGraphs) {
			value.add(pgId);
			value.addAll(getPartialGraphIds(GraphDatabaseElementaryMethods.getToplevelGraphForPartialGraphId(pgId)));
		}
		return value;		
	}
	
	@Override
	public void addPartialGraphId(long globalSubgraphId, int newPartialGraphId) {
		int partialGraphId = getPartialGraphId(globalSubgraphId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).addPartialGraphId(globalSubgraphId, newPartialGraphId);
		}
		int localSubgraphId = convertToLocalId(globalSubgraphId);
		GraphData data = getGraphData(localSubgraphId);
		if (data.partialGraphs == null) {
			data.partialGraphs = new LinkedList<Integer>();
		}
		data.partialGraphs.add(newPartialGraphId);
	}
	

	/**
	 * Deletes the partial graph identified by its id
	 * 
	 * @param partialGraphId
	 */
	public abstract void deletePartialGraph(int partialGraphId);

	public abstract void edgeListModified();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBasicMethods#vertexListModified
	 * ()
	 */
	@Override
	public abstract void vertexListModified();

	public abstract void graphModified();

	/* **************************************************************************
	 * Methods to access traversal context will be implemented by the two
	 * subclasses CompleteGraphDatabase and LocalGraphDatabase
	 * *************************************************************************
	 */

	public abstract Graph getTraversalContext();

	public abstract void releaseTraversalContext();

	public abstract void setTraversalContext(Graph traversalContext);

	/* **************************************************************************
	 * Methods to access graph properties
	 * *************************************************************************
	 */

	public long getVertexListVersion() {
		return vertexListVersion;
	}

	/**
	 * Use to free a <code>Vertex</code>-index.
	 * 
	 * @param index
	 */
	protected void freeVertexIndex(int index) {
		freeVertexList.freeIndex(index);
	}

	protected FreeIndexList getFreeVertexList() {
		return freeVertexList;
	}

	/**
	 * Use to allocate a <code>Vertex</code>-index.
	 */
	protected int allocateVertexIndex() {
		int vId = freeVertexList.allocateIndex();
		if (vId == 0) {
			vId = freeVertexList.allocateIndex();
		}
		return vId;
	}

	public boolean containsVertexId(long vId) {
		return getVertexObject(vId) != null;
	}

	public boolean containsVertex(Vertex vertex) {
		return getVertexObject(vertex.getGlobalId()) == vertex;
	}

	@Override
	public long createVertex(int m1ClassId) {
		return createVertex(m1ClassId, 0);
	}

	@Override
	public long createVertex(int m1ClassId, long vertexId) {
		// set id
		if (isLoading()) {
			if (vertexId == 0) {
				throw new GraphException(
						"Cannot add a vertex without a predefined id during graph loading");
			}
		} else {
			if (vertexId == 0) {
				vertexId = convertToGlobalId(allocateVertexIndex());
			} else {
				throw new GraphException(
						"Cannot add a vertex with a predefined id outside the graph loading");
			}
		}
		System.out.println("Creating vertex with id " + vertexId);
		// instantiate object
		Class<? extends Vertex> m1Class = (Class<? extends Vertex>) schema.getM1ClassForId(m1ClassId);
		VertexImpl v = (VertexImpl) graphFactory
				.createVertexDiskBasedStorage(m1Class, vertexId, this);
		localDiskStorage.storeVertex(v);

		long toplevelSubgraphId = convertToGlobalId(1);

		getGraphData(TOPLEVEL_LOCAL_SUBGRAPH_ID).vertexCount++;
		if (getFirstVertexId(toplevelSubgraphId) == 0) {
			setFirstVertexId(toplevelSubgraphId, vertexId);
		}
		if (getLastVertexId(toplevelSubgraphId) != 0) {
			setNextVertexId(getLastVertexId(toplevelSubgraphId), vertexId);
			setPreviousVertexId(vertexId, getLastVertexId(toplevelSubgraphId));
		}
		setLastVertexId(toplevelSubgraphId, vertexId);

		if (!isLoading()) {
			vertexListModified();
			notifyVertexAdded(vertexId);
		}
		return vertexId;
	}

	public void deleteVertex(long vertexId) {
		deleteVertexList.add(vertexId);
		deleteVerticesInDeleteList();
	}

	/**
	 * Deletes all vertices in deleteVertexList from the internal structures of
	 * this graph. Possibly, cascading deletes of child vertices occur when
	 * parent vertices of Composition classes are deleted.
	 */
	private void deleteVerticesInDeleteList() {
		boolean edgeHasBeenDeleted = false;
		while (deleteVertexList.isEmpty()) {
			long vertexId = deleteVertexList.remove(0);
			assert (vertexId != 0) && containsVertexId(vertexId);
			notifyVertexDeleted(vertexId);
			// delete all incident edges including incidence objects
			Vertex v = getVertexObject(vertexId);
			Incidence inc = v.getFirstIncidence();

			Set<EdgeImpl> edges = new HashSet<EdgeImpl>();
			while (inc != null) {
				EdgeImpl edge = (EdgeImpl) inc.getEdge();
				boolean deleteEdge = false;
				if (edge.isBinary()) {
					BinaryEdge bedge = (BinaryEdge) edge;
					if (bedge.getAlpha() == v) {
						if (bedge.getOmegaSemantics() == IncidenceType.COMPOSITION) {
							VertexImpl omega = (VertexImpl) bedge.getOmega();
							if ((omega != v)
									&& containsVertex(omega)
									&& !deleteVertexList
											.contains(omega.getGlobalId())) {
								deleteVertexList.add(omega.getGlobalId());
								notifyEdgeDeleted(bedge.getGlobalId());
								removeEdgeFromESeq(bedge.getGlobalId());
								deleteEdge = true;
							}
						}
					} else if (bedge.getOmega() == v) {
						if (bedge.getAlphaSemantics() == IncidenceType.COMPOSITION) {
							VertexImpl alpha = (VertexImpl) bedge.getAlpha();
							if ((alpha != v)
									&& containsVertex(alpha)
									&& !deleteVertexList
											.contains(alpha.getGlobalId())) {
								deleteVertexList.add(alpha.getGlobalId());
								notifyEdgeDeleted(bedge.getGlobalId());
								removeEdgeFromESeq(bedge.getGlobalId());
								deleteEdge = true;
							}
						}
					}
				}
				edgeHasBeenDeleted |= deleteEdge;
				if (!deleteEdge) {
					edges.add(edge);
					removeIncidenceFromLambdaSeqOfEdge(inc.getGlobalId());
				}
				inc = v.getFirstIncidence();
			}
			for (EdgeImpl edge : edges) {
				incidenceListOfEdgeModified(edge.getGlobalId());
			}
			removeVertexFromVSeq(vertexId);
		}
		vertexListModified();
		if (edgeHasBeenDeleted) {
			edgeListModified();
		}
	}

	/**
	 * Removes the vertex v from the global vertex sequence of this graph.
	 * 
	 * @param vertexId
	 *            a vertex
	 */
	public void removeVertexFromVSeq(long vertexId) {
		assert vertexId != 0;
		int partialGraphId = getPartialGraphId(vertexId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).removeVertexFromVSeq(vertexId);
			return;
		}

		// TODO: instead of the toplevel graph, the lowest subgraph the vertex
		// is
		// contained in needs to be determined. Because of the restrictions to
		// the ordering v may be only the first vertex of the lowest graph
		// it is contained in
		long toplevelGraphId = convertToGlobalId(1);

		// if current vertex is the first or last one in the local graph,
		// the respecitive values need to be set to its next or previous vertex
		long firstV = getFirstVertexId(toplevelGraphId);
		long lastV = getLastVertexId(toplevelGraphId);
		long nextV = getNextVertexId(vertexId);
		long prevV = getPreviousVertexId(vertexId);

		if (firstV == vertexId) {
			setFirstVertexId(toplevelGraphId, nextV);
		}
		if (lastV == vertexId) {
			setLastVertexId(toplevelGraphId, prevV);
		}

		// next and previous pointer of previous and next vertex need to be set
		// in any case (only exception: its the globally first or last vertex)
		if (prevV != 0) {
			setNextVertexId(prevV, nextV);
		}
		if (nextV != 0) {
			setPreviousVertexId(prevV, nextV);
		}

		// remove vertex from storage
		freeVertexIndex(convertToLocalId(vertexId));
		setPreviousVertexId(vertexId, 0);
		setNextVertexId(vertexId, 0);
		setVCount(toplevelGraphId, getVCount(toplevelGraphId) - 1);
		getLocalDiskStorage().removeVertexFromDiskStorage(
				convertToLocalId(vertexId));
		notifyVertexDeleted(vertexId);
	}

	/**
	 * Modifies vSeq such that the movedVertex is immediately before the
	 * targetVertex.
	 * 
	 * GlobalOperation
	 * 
	 * @param targetVertex
	 *            a vertex
	 * @param movedVertex
	 *            the vertex to be moved
	 */
	protected void putVertexBefore(VertexImpl targetVertex,
			VertexImpl movedVertex) {
		assert (targetVertex != null) && targetVertex.isValid()
				&& containsVertex(targetVertex);
		assert (movedVertex != null) && movedVertex.isValid()
				&& containsVertex(movedVertex);
		assert targetVertex != movedVertex;
		putVertexBefore(targetVertex.getGlobalId(), movedVertex.getGlobalId());

	}

	/**
	 * Global methods, changes Vseq so that the vertex identified by
	 * movedVertexId is directly before the vertex identified by targetVertexId
	 * 
	 * @param targetVertexId
	 *            global id of the target vertex
	 * @param movedVertexId
	 *            global id of the vertex to be moved
	 */
	public void putVertexBefore(long targetVertexId, long movedVertexId) {
		long prevVertexId = getPreviousVertexId(targetVertexId);

		if ((targetVertexId == movedVertexId)
				|| (prevVertexId == movedVertexId)) {
			return;
		}

		long toplevelGraphId = convertToGlobalId(1);

		assert getFirstVertexId(toplevelGraphId) != getLastVertexId(toplevelGraphId);

		long firstV = getFirstVertexId(toplevelGraphId);
		long lastV = getLastVertexId(toplevelGraphId);
		long mvdNextV = getNextVertexId(movedVertexId);
		long mvdPrevV = getPreviousVertexId(movedVertexId);

		// remove moved vertex from vSeq
		if (movedVertexId == firstV) {
			setFirstVertexId(toplevelGraphId, mvdNextV);
		}
		if (movedVertexId == lastV) {
			setLastVertexId(toplevelGraphId, mvdPrevV);
		}
		if (mvdPrevV != 0) {
			setNextVertexId(mvdPrevV, mvdNextV);
		}
		if (mvdNextV != 0) {
			setPreviousVertexId(mvdNextV, mvdPrevV);
		}

		// insert moved vertex in vSeq immediately before target
		else if (targetVertexId == firstV) {
			setFirstVertexId(toplevelGraphId, movedVertexId);
		}

		long tgtPrevV = getPreviousVertexId(targetVertexId);

		setPreviousVertexId(movedVertexId, tgtPrevV);
		setNextVertexId(movedVertexId, targetVertexId);
		setPreviousVertexId(targetVertexId, movedVertexId);
		if (tgtPrevV != 0) {
			setNextVertexId(tgtPrevV, movedVertexId);
		}

		vertexListModified();

	}

	/**
	 * Modifies vSeq such that the movedVertex is immediately after the
	 * targetVertex.
	 * 
	 * @param targetVertex
	 *            a vertex
	 * @param movedVertex
	 *            the vertex to be moved
	 */
	protected void putVertexAfter(VertexImpl targetVertex,
			VertexImpl movedVertex) {
		assert (targetVertex != null) && targetVertex.isValid()
				&& containsVertex(targetVertex);
		assert (movedVertex != null) && movedVertex.isValid()
				&& containsVertex(movedVertex);
		assert targetVertex != movedVertex;

	}

	/**
	 * Global methods, changes Vseq so that the vertex identified by
	 * movedVertexId is directly after the vertex identified by targetVertexId
	 * 
	 * @param targetVertexId
	 *            global id of the target vertex
	 * @param movedVertexId
	 *            global id of the vertex to be moved
	 */
	@Override
	public void putVertexAfter(long targetVertexId, long movedVertexId) {
		assert (targetVertexId != 0) && (containsVertexId(targetVertexId));
		assert (targetVertexId != 0) && (containsVertexId(targetVertexId));

		long prevVertexId = getPreviousVertexId(targetVertexId);

		if ((targetVertexId == movedVertexId)
				|| (prevVertexId == movedVertexId)) {
			return;
		}

		long toplevelGraphId = convertToGlobalId(1);

		assert getFirstVertexId(toplevelGraphId) != getLastVertexId(toplevelGraphId);

		long firstV = getFirstVertexId(toplevelGraphId);
		long lastV = getLastVertexId(toplevelGraphId);
		long mvdNextV = getNextVertexId(movedVertexId);
		long mvdPrevV = getPreviousVertexId(movedVertexId);

		// remove moved vertex from vSeq
		if (movedVertexId == firstV) {
			setFirstVertexId(toplevelGraphId, mvdNextV);
		}
		if (movedVertexId == lastV) {
			setLastVertexId(toplevelGraphId, mvdPrevV);
		}
		if (mvdPrevV != 0) {
			setNextVertexId(mvdPrevV, mvdNextV);
		}
		if (mvdNextV != 0) {
			setPreviousVertexId(mvdNextV, mvdPrevV);
		}

		// insert moved vertex in vSeq immediately after target
		else if (targetVertexId == lastV) {
			setLastVertexId(toplevelGraphId, movedVertexId);
		}

		long tgtNextV = getNextVertexId(targetVertexId);

		setNextVertexId(movedVertexId, tgtNextV);
		setPreviousVertexId(movedVertexId, targetVertexId);
		setNextVertexId(targetVertexId, movedVertexId);
		if (tgtNextV != 0) {
			setPreviousVertexId(tgtNextV, movedVertexId);
		}

		vertexListModified();

	}

	public long getEdgeListVersion() {
		return edgeListVersion;
	}

	public boolean containsEdgeId(long eId) {
		return getEdgeObject(eId) != null;
	}

	boolean containsEdge(Edge edge) {
		return getEdgeObject(edge.getGlobalId()) == edge;
	}

	/**
	 * Use to free an <code>Edge</code>-index
	 * 
	 * @param index
	 */
	protected void freeEdgeIndex(int index) {
		freeEdgeList.freeIndex(index);
	}

	protected FreeIndexList getFreeEdgeList() {
		return freeEdgeList;
	}

	@Override
	public long getMaxECount() {
		return Integer.MAX_VALUE;
	}

	/**
	 * Use to allocate a <code>Edge</code>-index.
	 */
	protected int allocateEdgeIndex() {
		int eId = freeEdgeList.allocateIndex();
		if (eId == 0) {
			eId = freeEdgeList.allocateIndex();
		}
		return eId;
	}

	@Override
	public long createEdge(int  m1ClassId) {
		return createEdge(m1ClassId, 0);
	}

	@Override
	public long createEdge(int m1ClassId, long edgeId) {
		// set id
		if (isLoading()) {
			if (edgeId == 0) {
				throw new GraphException(
						"Cannot add a edge without a predefined id during graph loading");
			}
		} else {
			if (edgeId == 0) {
				edgeId = convertToGlobalId(allocateEdgeIndex());
			} else {
				throw new GraphException(
						"Cannot add a edge with a predefined id outside the graph loading");
			}
		}

		// instantiate object
		@SuppressWarnings("unchecked")
		Class<? extends Edge> m1Class = (Class<? extends Edge>) schema.getM1ClassForId(m1ClassId);
		EdgeImpl v = (EdgeImpl) graphFactory.createEdgeDiskBasedStorage(m1Class, edgeId, this);
		localDiskStorage.storeEdge(v);

		long toplevelSubgraphId = convertToGlobalId(1);

		getGraphData(TOPLEVEL_LOCAL_SUBGRAPH_ID).vertexCount++;
		if (getFirstEdgeId(toplevelSubgraphId) == 0) {
			setFirstEdgeId(toplevelSubgraphId, edgeId);
		}
		if (getLastEdgeId(toplevelSubgraphId) != 0) {
			setNextEdgeId(getLastEdgeId(toplevelSubgraphId), edgeId);
			setPreviousEdgeId(edgeId, getLastEdgeId(toplevelSubgraphId));
		}
		setLastEdgeId(toplevelSubgraphId, edgeId);

		if (!isLoading()) {
			edgeListModified();
			notifyEdgeAdded(edgeId);
		}
		return edgeId;
	}

	/**
	 * Deletes the edge from the internal structures of this graph.
	 * 
	 * @param edge
	 *            an edge
	 */
	public void deleteEdge(long edgeId) {
		assert (edgeId != 0) && containsEdgeId(edgeId);

		Edge e = getEdgeObject(edgeId);

		Incidence inc = e.getFirstIncidence();
		Set<Vertex> vertices = new HashSet<Vertex>();
		while (inc != null) {
			vertices.add(inc.getVertex());
			removeIncidenceFromLambdaSeqOfVertex(inc.getGlobalId());
			inc = e.getFirstIncidence();
		}
		for (Vertex vertex : vertices) {
			incidenceListOfVertexModified(vertex.getGlobalId());
		}

		removeEdgeFromESeq(edgeId);

	}

	/**
	 * Removes the edge v from the global edge sequence of this graph.
	 * 
	 * @param edgeId
	 *            a edge
	 */
	public void removeEdgeFromESeq(long edgeId) {
		assert edgeId != 0;
		int partialGraphId = getPartialGraphId(edgeId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).removeEdgeFromESeq(edgeId);
			return;
		}

		// TODO: instead of the toplevel graph, the lowest subgraph the edge is
		// contained in needs to be determined. Because of the restrictions to
		// the ordering v may be only the first edge of the lowest graph
		// it is contained in
		long toplevelGraphId = convertToGlobalId(1);

		// if current edge is the first or last one in the local graph,
		// the respecitive values need to be set to its next or previous edge
		long firstV = getFirstEdgeId(toplevelGraphId);
		long lastV = getLastEdgeId(toplevelGraphId);
		long nextV = getNextEdgeId(edgeId);
		long prevV = getPreviousEdgeId(edgeId);

		if (firstV == edgeId) {
			setFirstEdgeId(toplevelGraphId, nextV);
		}
		if (lastV == edgeId) {
			setLastEdgeId(toplevelGraphId, prevV);
		}

		// next and previous pointer of previous and next edge need to be set
		// in any case (only exception: its the globally first or last edge)
		if (prevV != 0) {
			setNextEdgeId(prevV, nextV);
		}
		if (nextV != 0) {
			setPreviousEdgeId(prevV, nextV);
		}

		// remove edge from storage
		freeEdgeIndex(convertToLocalId(edgeId));
		setPreviousEdgeId(edgeId, 0);
		setNextEdgeId(edgeId, 0);
		setVCount(toplevelGraphId, getVCount(toplevelGraphId) - 1);
		getLocalDiskStorage().removeEdgeFromDiskStorage(
				convertToLocalId(edgeId));
		notifyEdgeDeleted(edgeId);
	}

	/**
	 * Modifies vSeq such that the movedEdge is immediately before the
	 * targetEdge.
	 * 
	 * GlobalOperation
	 * 
	 * @param targetEdge
	 *            a edge
	 * @param movedEdge
	 *            the edge to be moved
	 */
	protected void putEdgeBefore(EdgeImpl targetEdge, EdgeImpl movedEdge) {
		assert (targetEdge != null) && targetEdge.isValid()
				&& containsEdge(targetEdge);
		assert (movedEdge != null) && movedEdge.isValid()
				&& containsEdge(movedEdge);
		assert targetEdge != movedEdge;
		putEdgeBefore(targetEdge.getGlobalId(), movedEdge.getGlobalId());

	}

	/**
	 * Global methods, changes Vseq so that the edge identified by movedEdgeId
	 * is directly before the edge identified by targetEdgeId
	 * 
	 * @param targetEdgeId
	 *            global id of the target edge
	 * @param movedEdgeId
	 *            global id of the edge to be moved
	 */
	public void putEdgeBefore(long targetEdgeId, long movedEdgeId) {
		long prevEdgeId = getPreviousEdgeId(targetEdgeId);

		if ((targetEdgeId == movedEdgeId) || (prevEdgeId == movedEdgeId)) {
			return;
		}

		int toplevelGraphId = convertToLocalId(1);

		assert getFirstEdgeId(toplevelGraphId) != getLastEdgeId(toplevelGraphId);

		long firstV = getFirstEdgeId(toplevelGraphId);
		long lastV = getLastEdgeId(toplevelGraphId);
		long mvdNextV = getNextEdgeId(movedEdgeId);
		long mvdPrevV = getPreviousEdgeId(movedEdgeId);

		// remove moved edge from vSeq
		if (movedEdgeId == firstV) {
			setFirstEdgeId(toplevelGraphId, mvdNextV);
		}
		if (movedEdgeId == lastV) {
			setLastEdgeId(toplevelGraphId, mvdPrevV);
		}
		if (mvdPrevV != 0) {
			setNextEdgeId(mvdPrevV, mvdNextV);
		}
		if (mvdNextV != 0) {
			setPreviousEdgeId(mvdNextV, mvdPrevV);
		}

		// insert moved edge in vSeq immediately before target
		else if (targetEdgeId == firstV) {
			setFirstEdgeId(toplevelGraphId, movedEdgeId);
		}

		long tgtPrevV = getPreviousEdgeId(targetEdgeId);

		setPreviousEdgeId(movedEdgeId, tgtPrevV);
		setNextEdgeId(movedEdgeId, targetEdgeId);
		setPreviousEdgeId(targetEdgeId, movedEdgeId);
		if (tgtPrevV != 0) {
			setNextEdgeId(tgtPrevV, movedEdgeId);
		}

		edgeListModified();

	}

	/**
	 * Modifies vSeq such that the movedEdge is immediately after the
	 * targetEdge.
	 * 
	 * @param targetEdge
	 *            a edge
	 * @param movedEdge
	 *            the edge to be moved
	 */
	protected void putEdgeAfter(EdgeImpl targetEdge, EdgeImpl movedEdge) {
		assert (targetEdge != null) && targetEdge.isValid()
				&& containsEdge(targetEdge);
		assert (movedEdge != null) && movedEdge.isValid()
				&& containsEdge(movedEdge);
		assert targetEdge != movedEdge;

	}

	/**
	 * Global methods, changes Vseq so that the edge identified by movedEdgeId
	 * is directly after the edge identified by targetEdgeId
	 * 
	 * @param targetEdgeId
	 *            global id of the target edge
	 * @param movedEdgeId
	 *            global id of the edge to be moved
	 */
	public void putEdgeAfter(long targetEdgeId, long movedEdgeId) {
		assert (targetEdgeId != 0) && (containsEdgeId(targetEdgeId));
		assert (targetEdgeId != 0) && (containsEdgeId(targetEdgeId));

		long prevEdgeId = getPreviousEdgeId(targetEdgeId);

		if ((targetEdgeId == movedEdgeId) || (prevEdgeId == movedEdgeId)) {
			return;
		}

		int toplevelGraphId = convertToLocalId(1);

		assert getFirstEdgeId(toplevelGraphId) != getLastEdgeId(toplevelGraphId);

		long firstV = getFirstEdgeId(toplevelGraphId);
		long lastV = getLastEdgeId(toplevelGraphId);
		long mvdNextV = getNextEdgeId(movedEdgeId);
		long mvdPrevV = getPreviousEdgeId(movedEdgeId);

		// remove moved edge from vSeq
		if (movedEdgeId == firstV) {
			setFirstEdgeId(toplevelGraphId, mvdNextV);
		}
		if (movedEdgeId == lastV) {
			setLastEdgeId(toplevelGraphId, mvdPrevV);
		}
		if (mvdPrevV != 0) {
			setNextEdgeId(mvdPrevV, mvdNextV);
		}
		if (mvdNextV != 0) {
			setPreviousEdgeId(mvdNextV, mvdPrevV);
		}

		// insert moved edge in vSeq immediately after target
		else if (targetEdgeId == lastV) {
			setLastEdgeId(toplevelGraphId, movedEdgeId);
		}

		long tgtNextE = getNextEdgeId(targetEdgeId);

		setNextEdgeId(movedEdgeId, tgtNextE);
		setPreviousEdgeId(movedEdgeId, targetEdgeId);
		setNextEdgeId(targetEdgeId, movedEdgeId);
		if (tgtNextE != 0) {
			setPreviousEdgeId(tgtNextE, movedEdgeId);
		}

		edgeListModified();

	}

	/* **************************************************************************
	 * Methods to access Lambda sequences
	 * *************************************************************************
	 */

	protected FreeIndexList getFreeIncidenceList() {
		return freeIncidenceList;
	}

	/**
	 * Use to allocate a <code>Incidence</code>-index.
	 */
	protected int allocateIncidenceIndex() {
		int iId = freeIncidenceList.allocateIndex();
		return iId;
	}

	@Override
	public void incidenceListOfVertexModified(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		if (partialGraphId == localPartialGraphId) {
			localDiskStorage.incidenceListOfVertexModified(vertexId);
		} else {
			getGraphDatabase(partialGraphId).incidenceListOfVertexModified(
					vertexId);
		}
	}

	//TODO: increase/decrease e and v count of parent graphs in necessary?
	
	@Override
	public void increaseVCount(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).increaseVCount(subgraphId);
		} else {
			getGraphData(convertToLocalId(subgraphId)).vertexCount++;
		}
	}

	@Override
	public void decreaseVCount(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).decreaseVCount(subgraphId);
		} else {
			getGraphData(convertToLocalId(subgraphId)).vertexCount--;
		}
	}

	@Override
	public void increaseECount(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).decreaseECount(subgraphId);
		} else {
			getGraphData(convertToLocalId(subgraphId)).edgeCount++;
		}
	}

	@Override
	public void decreaseECount(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).increaseECount(subgraphId);
		} else {
			getGraphData(convertToLocalId(subgraphId)).edgeCount--;
		}
	}
	
	@Override
	public void increaseICount(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).increaseICount(subgraphId);
		} else {
			getGraphData(convertToLocalId(subgraphId)).incidenceCount++;
		}
	}

	@Override
	public void decreaseICount(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).decreaseICount(subgraphId);
		} else {
			getGraphData(convertToLocalId(subgraphId)).incidenceCount--;
		}
	}

	/**
	 * Connects the specified vertex <code>v</code> to the speficied edge
	 * <code>e</code> by an incidence of class <code>cls</code> and sets the
	 * incidence's id to the next locally available incidence id
	 * 
	 * @param cls
	 * @param vertex
	 * @param edge
	 */
	@Override
	public long connect(int incidenceClassId, long vertexId, long edgeId) {
		return connect(incidenceClassId, vertexId, edgeId, 0);
	}

	/**
	 * Connects the specified vertex <code>v</code> to the speficied edge
	 * <code>e</code> by an incidence of class <code>cls</code> and sets the
	 * incidence's id to <code>id</code>
	 * 
	 * @param cls
	 * @param vertex
	 * @param edge
	 * @param id
	 */
	@Override
	public long connect(int incidenceClassId, long vertexId, long edgeId,
			long incId) {
		IncidenceClass incClass = (IncidenceClass) schema
				.getTypeForId(incidenceClassId);
		Class<? extends Incidence> m1Class = incClass.getM1Class();
		// Direction dir = incClass.getDirection();

		// check id
		if (incId != 0) {
			if (!isLoading()) {
				throw new GraphException(
						"Incidences with a defined id may only be created during graph loading");
			}
		} else {
			incId = convertToGlobalId(allocateIncidenceIndex());
		}
		// call graph factory to create object
		IncidenceImpl newInc = (IncidenceImpl) graphFactory
				.createIncidenceDiskBasedStorage(m1Class, incId, vertexId,
						edgeId, this);

		// append created incidence to lambda sequences of vertex and edge

		// add this incidence to the sequence of incidences of v
		if (getFirstIncidenceIdAtVertexId(vertexId) == 0) {
			// v has no incidences
			setFirstIncidenceIdAtVertexId(vertexId, incId);
			setLastIncidenceIdAtVertexId(vertexId, incId);
		} else {
			long lastIncId = getLastIncidenceIdAtEdgeId(vertexId);
			setNextIncidenceIdAtVertexId(lastIncId, incId);
			setPreviousIncidenceIdAtVertexId(incId, lastIncId);
			setLastIncidenceIdAtVertexId(vertexId, incId);
		}

		incidenceListOfVertexModified(vertexId);

		if (getFirstIncidenceIdAtEdgeId(-edgeId) == 0) {
			// v has no incidences
			setFirstIncidenceIdAtEdgeId(-edgeId, incId);
			setLastIncidenceIdAtEdgeId(-edgeId, incId);
		} else {
			long lastIncId = getLastIncidenceIdAtEdgeId(-edgeId);
			setNextIncidenceIdAtEdgeId(lastIncId, incId);
			setPreviousIncidenceIdAtEdgeId(incId, lastIncId);
			setLastIncidenceIdAtEdgeId(-edgeId, incId);
		}

		incidenceListOfEdgeModified(edgeId);

		localDiskStorage.storeIncidence(newInc);
		if (!isLoading()) {
			notifyIncidenceAdded(incId);
		}
		return incId;
	}

	@Override
	public void deleteIncidence(long id) {
		removeIncidenceFromLambdaSeqOfEdge(id);
		removeIncidenceFromLambdaSeqOfVertex(id);
	}

	@Override
	public long getIncidenceListVersionOfVertexId(long vertexId) {
		int partialGraphId = getPartialGraphId(vertexId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore
				.getIncidenceListVersionOfEdgeId(convertToLocalId(vertexId));
	}

	@Override
	public long getIncidenceListVersionOfEdgeId(long edgeId) {
		int partialGraphId = getPartialGraphId(edgeId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore
				.getIncidenceListVersionOfVertexId(convertToLocalId(edgeId));
	}

	@Override
	public long getEdgeIdAtIncidenceId(long id) {
		int partialGraphId = getPartialGraphId(id);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getConnectedEdgeId(convertToLocalId(id));
	}

	@Override
	public long getVertexIdAtIncidenceId(long id) {
		int partialGraphId = getPartialGraphId(id);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getConnectedVertexId(convertToLocalId(id));
	}

	@Override
	public void putIncidenceIdAfterAtVertexId(long targetId, long movedId) {
		assert (targetId != 0) && (movedId != 0);
		// assert target.getThis() == moved.getThis();

		if ((targetId == movedId)
				|| (getNextIncidenceIdAtVertexId(targetId) == movedId)) {
			return;
		}

		long vertexId = getConnectedVertexId(targetId);

		long previousId = getPreviousIncidenceIdAtVertexId(movedId);
		long nextId = getNextIncidenceIdAtVertexId(movedId);

		// remove moved incidence from lambdaSeq
		if (movedId == getFirstIncidenceIdAtVertexId(vertexId)) {
			setFirstIncidenceIdAtVertexId(vertexId, nextId);
			setPreviousIncidenceIdAtVertexId(nextId, 0);
		} else if (movedId == getLastIncidenceIdAtVertexId(vertexId)) {
			setLastIncidenceIdAtVertexId(vertexId, previousId);
			setNextIncidenceIdAtVertexId(previousId, 0);
		} else {
			setNextIncidenceIdAtVertexId(previousId, nextId);
			setPreviousIncidenceIdAtVertexId(nextId, previousId);
		}

		long tgtNextId = getNextIncidenceIdAtVertexId(targetId);
		// insert moved incidence in lambdaSeq immediately after target
		if (targetId == getLastIncidenceIdAtVertexId(vertexId)) {
			setLastIncidenceIdAtVertexId(vertexId, movedId);
		} else {
			setPreviousIncidenceIdAtVertexId(tgtNextId, movedId);
		}
		setPreviousIncidenceIdAtVertexId(movedId, targetId);
		setNextIncidenceIdAtVertexId(movedId, tgtNextId);
		setNextIncidenceIdAtVertexId(targetId, movedId);
		incidenceListOfVertexModified(vertexId);
	}

	@Override
	public void putIncidenceIdBeforeAtVertexId(long targetId, long movedId) {
		assert (targetId != 0) && (movedId != 0);
		// assert target.getThis() == moved.getThis();

		if ((targetId == movedId)
				|| (getNextIncidenceIdAtVertexId(targetId) == movedId)) {
			return;
		}

		long vertexId = getConnectedVertexId(targetId);

		long previousId = getPreviousIncidenceIdAtVertexId(movedId);
		long nextId = getNextIncidenceIdAtVertexId(movedId);

		// remove moved incidence from lambdaSeq
		if (movedId == getFirstIncidenceIdAtVertexId(vertexId)) {
			setFirstIncidenceIdAtVertexId(vertexId, nextId);
			setPreviousIncidenceIdAtVertexId(nextId, 0);
		} else if (movedId == getLastIncidenceIdAtVertexId(vertexId)) {
			setLastIncidenceIdAtVertexId(vertexId, previousId);
			setNextIncidenceIdAtVertexId(previousId, 0);
		} else {
			setNextIncidenceIdAtVertexId(previousId, nextId);
			setPreviousIncidenceIdAtVertexId(nextId, previousId);
		}

		long tgtPreviousId = getPreviousIncidenceIdAtVertexId(targetId);
		// insert moved incidence in lambdaSeq immediately after target
		if (targetId == getFirstIncidenceIdAtVertexId(vertexId)) {
			setFirstIncidenceIdAtVertexId(vertexId, movedId);
		} else {
			setNextIncidenceIdAtVertexId(tgtPreviousId, movedId);
		}
		setNextIncidenceIdAtVertexId(movedId, targetId);
		setPreviousIncidenceIdAtVertexId(movedId, tgtPreviousId);
		setPreviousIncidenceIdAtVertexId(targetId, movedId);
		incidenceListOfVertexModified(vertexId);
	}

	private long getConnectedVertexId(long incidenceId) {
		int partialGraphId = getPartialGraphId(incidenceId);
		return getDiskStorageForPartialGraph(partialGraphId)
				.getConnectedVertexId(convertToLocalId(incidenceId));
	}

	private long getConnectedEdgeId(long incidenceId) {
		int partialGraphId = getPartialGraphId(incidenceId);
		return getDiskStorageForPartialGraph(partialGraphId)
				.getConnectedEdgeId(convertToLocalId(incidenceId));
	}

	@Override
	public long getFirstIncidenceIdAtVertexId(long elemId) {
		int partialGraphId = getPartialGraphId(elemId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore
				.getFirstIncidenceIdAtVertexId(convertToLocalId(elemId));
	}

	@Override
	public long getLastIncidenceIdAtVertexId(long elemId) {
		int partialGraphId = getPartialGraphId(elemId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getLastIncidenceIdAtVertexId(convertToLocalId(elemId));
	}

	@Override
	public long getFirstIncidenceIdAtEdgeId(long elemId) {
		int partialGraphId = getPartialGraphId(elemId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getFirstIncidenceIdAtEdgeId(convertToLocalId(elemId));
	}

	@Override
	public long getLastIncidenceIdAtEdgeId(long elemId) {
		int partialGraphId = getPartialGraphId(elemId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getLastIncidenceIdAtEdgeId(convertToLocalId(elemId));
	}

	@Override
	public long getNextIncidenceIdAtVertexId(long incId) {
		int partialGraphId = getPartialGraphId(incId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore.getNextIncidenceIdAtVertexId(convertToLocalId(incId));
	}

	@Override
	public long getPreviousIncidenceIdAtVertexId(long globalIncidenceId) {
		int partialGraphId = getPartialGraphId(globalIncidenceId);
		RemoteDiskStorageAccess diskStore = getDiskStorageForPartialGraph(partialGraphId);
		return diskStore
				.getPreviousIncidenceIdAtVertexId(convertToLocalId(globalIncidenceId));
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
		return diskStore
				.getPreviousIncidenceIdAtEdgeId(convertToLocalId(incId));
	}

	@Override
	public void appendIncidenceToLambdaSeqOfEdge(long edgeId, long incidenceId) {
		assert incidenceId != 0;
		setNextIncidenceIdAtEdgeId(incidenceId, 0);
		if (getFirstIncidenceIdAtEdgeId(edgeId) == 0) {
			setFirstIncidenceIdAtEdgeId(edgeId, incidenceId);
		}
		long lastIncidenceId = getLastIncidenceIdAtEdgeId(edgeId);
		if (lastIncidenceId != 0) {
			setNextIncidenceIdAtEdgeId(lastIncidenceId, incidenceId);
			setPreviousIncidenceIdAtEdgeId(incidenceId, lastIncidenceId);
		}
		setLastIncidenceIdAtEdgeId(edgeId, incidenceId);
		incidenceListOfEdgeModified(edgeId);
	}

	@Override
	public void appendIncidenceToLambdaSeqOfVertex(long vertexId,
			long incidenceId) {
		assert incidenceId != 0;
		setNextIncidenceIdAtVertexId(incidenceId, 0);
		if (getFirstIncidenceIdAtVertexId(vertexId) == 0) {
			setFirstIncidenceIdAtVertexId(vertexId, incidenceId);
		}
		long lastIncidenceId = getLastIncidenceIdAtVertexId(vertexId);
		if (lastIncidenceId != 0) {
			setNextIncidenceIdAtVertexId(lastIncidenceId, incidenceId);
			setPreviousIncidenceIdAtVertexId(incidenceId, lastIncidenceId);
		}
		setLastIncidenceIdAtVertexId(vertexId, incidenceId);
		incidenceListOfVertexModified(vertexId);
	}

	public void removeIncidenceFromLambdaSeqOfEdge(long incidenceId) {
		long edgeId = getConnectedEdgeId(incidenceId);
		int partialGraphId = getPartialGraphId(edgeId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId)
					.removeIncidenceFromLambdaSeqOfEdge(incidenceId);
			return;
		} else {
			long previousId = getPreviousIncidenceIdAtEdgeId(incidenceId);
			long nextId = getNextIncidenceIdAtEdgeId(incidenceId);
			if (incidenceId == getFirstIncidenceIdAtEdgeId(edgeId)) {
				setFirstIncidenceIdAtEdgeId(edgeId, nextId);
				setPreviousIncidenceIdAtEdgeId(nextId, 0);
			} else if (incidenceId == getLastIncidenceIdAtEdgeId(edgeId)) {
				setLastIncidenceIdAtEdgeId(edgeId, previousId);
				setNextIncidenceIdAtEdgeId(previousId, 0);
			} else {
				setNextIncidenceIdAtEdgeId(previousId, nextId);
				setPreviousIncidenceIdAtEdgeId(nextId, previousId);
			}
		}
		// delete incidence
		setNextIncidenceIdAtEdgeId(incidenceId, 0);
		setPreviousIncidenceIdAtEdgeId(incidenceId, 0);
		incidenceListOfVertexModified(edgeId);
	}

	public void removeIncidenceFromLambdaSeqOfVertex(long incidenceId) {
		long vertexId = getConnectedVertexId(incidenceId);
		int partialGraphId = getPartialGraphId(vertexId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId)
					.removeIncidenceFromLambdaSeqOfVertex(incidenceId);
			return;
		} else {
			long previousId = getPreviousIncidenceIdAtVertexId(incidenceId);
			long nextId = getNextIncidenceIdAtVertexId(incidenceId);
			if (incidenceId == getFirstIncidenceIdAtVertexId(vertexId)) {
				setFirstIncidenceIdAtVertexId(vertexId, nextId);
				setPreviousIncidenceIdAtEdgeId(nextId, 0);
			} else if (incidenceId == getLastIncidenceIdAtVertexId(vertexId)) {
				setLastIncidenceIdAtVertexId(vertexId, previousId);
				setNextIncidenceIdAtVertexId(previousId, 0);
			} else {
				setNextIncidenceIdAtVertexId(previousId, nextId);
				setPreviousIncidenceIdAtVertexId(nextId, previousId);
			}
		}
		// delete incidence
		setNextIncidenceIdAtVertexId(incidenceId, 0);
		setPreviousIncidenceIdAtVertexId(incidenceId, 0);
		incidenceListOfVertexModified(vertexId);
	}

	@Override
	public void putIncidenceIdAfterAtEdgeId(long targetId, long movedId) {
		assert (targetId != 0) && (movedId != 0);
		// assert target.getThis() == moved.getThis();

		if ((targetId == movedId)
				|| (getNextIncidenceIdAtEdgeId(targetId) == movedId)) {
			return;
		}

		long edgeId = -getConnectedEdgeId(targetId);

		long previousId = getPreviousIncidenceIdAtEdgeId(movedId);
		long nextId = getNextIncidenceIdAtEdgeId(movedId);

		// remove moved incidence from lambdaSeq
		if (movedId == getFirstIncidenceIdAtEdgeId(edgeId)) {
			setFirstIncidenceIdAtEdgeId(edgeId, nextId);
			setPreviousIncidenceIdAtEdgeId(nextId, 0);
		} else if (movedId == getLastIncidenceIdAtEdgeId(edgeId)) {
			setLastIncidenceIdAtEdgeId(edgeId, previousId);
			setNextIncidenceIdAtEdgeId(previousId, 0);
		} else {
			setNextIncidenceIdAtEdgeId(previousId, nextId);
			setPreviousIncidenceIdAtEdgeId(nextId, previousId);
		}

		long tgtNextId = getNextIncidenceIdAtEdgeId(targetId);
		// insert moved incidence in lambdaSeq immediately after target
		if (targetId == getLastIncidenceIdAtEdgeId(edgeId)) {
			setLastIncidenceIdAtEdgeId(edgeId, movedId);
		} else {
			setPreviousIncidenceIdAtEdgeId(tgtNextId, movedId);
		}
		setPreviousIncidenceIdAtEdgeId(movedId, targetId);
		setNextIncidenceIdAtEdgeId(movedId, tgtNextId);
		setNextIncidenceIdAtEdgeId(targetId, movedId);
		incidenceListOfVertexModified(edgeId);
	}

	@Override
	public void putIncidenceIdBeforeAtEdgeId(long targetId, long movedId) {
		assert (targetId != 0) && (movedId != 0);
		// assert target.getThis() == moved.getThis();

		if ((targetId == movedId)
				|| (getNextIncidenceIdAtEdgeId(targetId) == movedId)) {
			return;
		}

		long edgeId = -getConnectedEdgeId(targetId);

		long previousId = getPreviousIncidenceIdAtEdgeId(movedId);
		long nextId = getNextIncidenceIdAtEdgeId(movedId);

		// remove moved incidence from lambdaSeq
		if (movedId == getFirstIncidenceIdAtEdgeId(edgeId)) {
			setFirstIncidenceIdAtEdgeId(-edgeId, nextId);
			setPreviousIncidenceIdAtEdgeId(nextId, 0);
		} else if (movedId == getLastIncidenceIdAtEdgeId(edgeId)) {
			setLastIncidenceIdAtEdgeId(edgeId, previousId);
			setNextIncidenceIdAtEdgeId(previousId, 0);
		} else {
			setNextIncidenceIdAtEdgeId(previousId, nextId);
			setPreviousIncidenceIdAtEdgeId(nextId, previousId);
		}

		long tgtPreviousId = getPreviousIncidenceIdAtEdgeId(targetId);
		// insert moved incidence in lambdaSeq immediately after target
		if (targetId == getFirstIncidenceIdAtEdgeId(edgeId)) {
			setFirstIncidenceIdAtEdgeId(edgeId, movedId);
		} else {
			setNextIncidenceIdAtEdgeId(tgtPreviousId, movedId);
		}
		setNextIncidenceIdAtEdgeId(movedId, targetId);
		setPreviousIncidenceIdAtEdgeId(movedId, tgtPreviousId);
		setPreviousIncidenceIdAtEdgeId(targetId, movedId);
		incidenceListOfEdgeModified(edgeId);
	}

	@Override
	public long getContainingElementId(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getContainingElementId(
					subgraphId);
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
	public long getICount(long subgraphId) {
		int partialGraphId = getPartialGraphId(subgraphId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getICount(subgraphId);
		}
		return getGraphData(convertToLocalId(subgraphId)).incidenceCount;
	}

	protected void notifyEdgeAdded(long edgeId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases
				.values()) {
			gdb.internalNotifyEdgeAdded(edgeId);
		}
	}

	protected void notifyEdgeDeleted(long edgeId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases
				.values()) {
			gdb.internalNotifyEdgeDeleted(edgeId);
		}
	}

	public void internalNotifyEdgeAdded(long edgeId) {
		Edge o = getEdgeObject(edgeId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.edgeAdded(o);
		}
	}

	public void internalNotifyEdgeDeleted(long edgeId) {
		Edge o = getEdgeObject(edgeId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.edgeDeleted(o);
		}
	}

	protected void notifyVertexAdded(long vertexId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases
				.values()) {
			gdb.internalNotifyVertexAdded(vertexId);
		}
	}

	protected void notifyVertexDeleted(long vertexId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases
				.values()) {
			gdb.internalNotifyVertexDeleted(vertexId);
		}
	}

	public void internalNotifyVertexAdded(long vertexId) {
		Vertex o = getVertexObject(vertexId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.vertexAdded(o);
		}
	}

	public void internalNotifyVertexDeleted(long vertexId) {
		Vertex o = getVertexObject(vertexId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.vertexDeleted(o);
		}
	}

	protected void notifyIncidenceAdded(long incidenceId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases
				.values()) {
			gdb.internalNotifyIncidenceAdded(incidenceId);
		}
	}

	protected void notifyIncidenceDeleted(long incidenceId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases
				.values()) {
			gdb.internalNotifyIncidenceDeleted(incidenceId);
		}
	}

	public void internalNotifyIncidenceAdded(long incidenceId) {
		Incidence o = getIncidenceObject(incidenceId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.incidenceAdded(o);
		}
	}

	public void internalNotifyIncidenceDeleted(long incidenceId) {
		Incidence o = getIncidenceObject(incidenceId);
		for (GraphStructureChangedListener l : graphStructureChangedListeners) {
			l.incidenceDeleted(o);
		}
	}
	
	
	
	@Override
	public void setVertexAttribute(long elementId, String attributeName, Object data) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).setVertexAttribute(elementId, attributeName, data);
		} else {
			getVertexObject(elementId).setAttribute(attributeName, data);
		}
	}

	@Override
	public void setEdgeAttribute(long elementId, String attributeName,
			Object data) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId != localPartialGraphId) {
			getGraphDatabase(partialGraphId).setEdgeAttribute(elementId, attributeName, data);
		} else {
			getEdgeObject(elementId).setAttribute(attributeName, data);
		}
	}

	@Override
	public Object getVertexAttribute(long elementId, String attributeName) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getVertexAttribute(elementId, attributeName);
		} else {
			return getVertexObject(elementId).getAttribute(attributeName);
		}
	}

	@Override
	public Object getEdgeAttribute(long elementId, String attributeName) {
		int partialGraphId = getPartialGraphId(elementId);
		if (partialGraphId != localPartialGraphId) {
			return getGraphDatabase(partialGraphId).getEdgeAttribute(elementId, attributeName);
		} else {
			return getEdgeObject(elementId).getAttribute(attributeName);
		}
	}
	

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) {
		T record = graphFactory.createRecord(recordClass,
				getGraphObject(convertToGlobalId(1)));
		try {
			record.readComponentValues(io);
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, Object... components) {
		T record = graphFactory.createRecord(recordClass,
				getGraphObject(convertToGlobalId(1)));
		record.setComponentValues(components);
		return record;
	}
	
	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,
			Map<String, Object> fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> JGraLabSet<T> createSet() {
		return new JGraLabSetImpl<T>();
	}

	@Override
	public <T> JGraLabSet<T> createSet(Collection<? extends T> collection) {
		return new JGraLabSetImpl<T>(collection);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity) {
		return new JGraLabSetImpl<T>(initialCapacity);
	}

	@Override
	public <T> JGraLabSet<T> createSet(int initialCapacity, float loadFactor) {
		return new JGraLabSetImpl<T>(initialCapacity, loadFactor);
	}

	public Graph createViewGraph(Graph g, int kappa) {
		return graphFactory.createViewGraphDiskBasedStorage(g, kappa);
	}

	public Graph loadRemotePartialGraph(String hostname, int id) {
		// TODO Auto-generated method stub
		return null;
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


}