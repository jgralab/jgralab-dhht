package de.uni_koblenz.jgralab.impl.disk;

import java.io.FileNotFoundException;
import java.lang.ref.Reference;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.JGraLabServerImpl;
import de.uni_koblenz.jgralab.impl.JGraLabSetImpl;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.Schema;



/**
 * A GraphDatabase stores one local complete or partial graph and provides an uniform access to 
 * all partial graphs of the distributed graph the store belongs to and all its elements based
 * on the ids
 * 
 */

public abstract class GraphDatabaseBaseImpl extends GraphDatabaseElementaryMethods implements RemoteGraphDatabaseAccess {
	

	public long createSubordinateGraph(long containingGraphElementId) {
		//get m1 class and free id
		Class<? extends Graph> m1Class = schema.getGraphClass().getM1Class();
		
		GraphData data = new GraphData();
		data.globalSubgraphId = convertToGlobalId(localSubgraphData.size()); 
		localSubgraphData.add(data);
		data.containingElementId = containingGraphElementId;

		Graph subordinateGraph = graphFactory.createSubordinateGraphDiskBasedStorage(data.globalSubgraphId);

		data.typeId = schema.getClassId(m1Class);
		data.vertexCount = 0;
		data.edgeCount = 0;
		return data.globalSubgraphId;
	}

	
	
	/**
	 * Creates a new graph database to store all local subgraphs of the 
	 * complete graph identified by the given <code>uniqueGraphId</code>.
	 * All those local subgraphs have the same <code>partialGraphId</code>
	 * and belong to the specified <code>schema</code>.
	 * @param schema the schema of the graph whose subgraphs are represented 
	 *        by this graph database 
	 * @param uniqueGraphId the unique id of the graph
	 * @param partialGraphId the common partial graph id of all local subgraphs
	 */
	protected GraphDatabaseBaseImpl(Schema schema, String uniqueGraphId, long parentDistributedGraphId, int partialGraphId) {
		super(schema, uniqueGraphId, parentDistributedGraphId, partialGraphId);
	}
	
	
	
	/**
	 * Deletes  the partial graph identified by its id
	 * @param partialGraphId
	 */	
	public abstract void deletePartialGraph(int partialGraphId);
	
	
	/**
	 * Registers the partial graph with the given id <code>id</code> which is stored on the
	 * host with the name <code>hostname</code>
	 * @param id
	 * @param hostname
	 */
	public abstract void registerRemotePartialGraph(int id, String hostname);
	
	/**
	 * Creates a new partial graph on the given hostname and returns the globalSubgraphId of that
	 * partial graph
	 */
	public abstract int createPartialGraph(Class<? extends Graph> graphClass, String hostname);
	
	
	public abstract void edgeListModified();
	
	/* (non-Javadoc)
	 * @see de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBasicMethods#vertexListModified()
	 */
	@Override
	public abstract void vertexListModified();
	
	public abstract void graphModified();
	

	

	/* **************************************************************************
	 * Methods to access traversal context will be implemented by the two subclasses
	 * CompleteGraphDatabase and LocalGraphDatabase
	 * **************************************************************************/
	
	public abstract Graph getTraversalContext();
	
	public abstract void releaseTraversalContext();
	
	public abstract void setTraversalContext(Graph traversalContext);

	
	/* **************************************************************************
	 * Methods to access graph properties
	 * **************************************************************************/
	
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
		return getVertexObject(vertex.getId()) == vertex;
	}
	
	public long createVertex(int vertexClassId) {
		return createVertex(vertexClassId, 0);
	}	
	


	public long createVertex(int vertexClassId, long vertexId) {
		//set id
		if (isLoading()) {
			if (vertexId == 0) {
				throw new GraphException("Cannot add a vertex without a predefined id during graph loading");
			} 
		} else {
			if (vertexId == 0) {
				vertexId =  convertToGlobalId(allocateVertexIndex());
			} else {
				throw new GraphException("Cannot add a vertex with a predefined id outside the graph loading");
			}
		}

		//instantiate object
		VertexImpl v = (VertexImpl) graphFactory.createVertexDiskBasedStorage((Class<? extends Vertex>) schema.getM1ClassForId(vertexClassId), vertexId, this);
		localDiskStorage.storeVertex(v);

		long toplevelSubgraphId = convertToGlobalId(1);
		
		getGraphData(0).vertexCount++;
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
	private void deleteVerticesInDeleteList()  {
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
							if ((omega != v) && containsVertex(omega)
									&& !deleteVertexList.contains(omega.getId())) {
								deleteVertexList.add(omega.getId());
								notifyEdgeDeleted(bedge.getId());
								removeEdgeFromESeq(bedge.getId());
								deleteEdge = true;
							}
						}
					} else if (bedge.getOmega() == v) {
						if (bedge.getAlphaSemantics() == IncidenceType.COMPOSITION) {
							VertexImpl alpha = (VertexImpl) bedge.getAlpha();
							if ((alpha != v) && containsVertex(alpha)
									&& !deleteVertexList.contains(alpha.getId())) {
								deleteVertexList.add(alpha.getId());
								notifyEdgeDeleted(bedge.getId());
								removeEdgeFromESeq(bedge.getId());
								deleteEdge = true;
							}
						}
					}
				}
				edgeHasBeenDeleted |= deleteEdge;
				if (!deleteEdge) {
					edges.add(edge);
					edge.removeIncidenceFromLambdaSeq((IncidenceImpl) inc);
				}
				inc = v.getFirstIncidence();
			}
			for (EdgeImpl edge : edges) {
				edge.incidenceListModified();
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
		
		//TODO: instead of the toplevel graph, the lowest subgraph the vertex is 
		//      contained in needs to be determined. Because of the restrictions to 
		//      the ordering v may be only the first vertex of the lowest graph 
		//      it is contained in
		long toplevelGraphId = convertToGlobalId(1);
		
		//if current vertex is the first or last one in the local graph,
		//the respecitive values need to be set to its next or previous vertex 
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

		
		//next and previous pointer of previous and next vertex need to be set
		//in any case (only exception: its the globally first or last vertex)
		if (prevV != 0)
			setNextVertexId(prevV, nextV);
		if (nextV != 0)
			setPreviousVertexId(prevV, nextV);
	
		
		//remove vertex from storage
		freeVertexIndex(convertToLocalId(vertexId));
		setPreviousVertexId(vertexId, 0);
		setNextVertexId(vertexId, 0);
		setVCount(toplevelGraphId, getVCount(toplevelGraphId) - 1);
		getLocalDiskStorage().removeVertexFromDiskStorage(convertToLocalId(vertexId));
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
	protected void putVertexBefore(VertexImpl targetVertex,	VertexImpl movedVertex) {
		assert (targetVertex != null) && targetVertex.isValid()
				&& containsVertex(targetVertex);
		assert (movedVertex != null) && movedVertex.isValid()
				&& containsVertex(movedVertex);
		assert targetVertex != movedVertex;
		putVertexBefore(targetVertex.getId(), movedVertex.getId());
		
	}

	/**
	 * Global methods, changes Vseq so that the vertex identified by movedVertexId is 
	 * directly before the vertex identified by targetVertexId
	 * @param targetVertexId global id of the target vertex
	 * @param movedVertexId global id of the vertex to be moved
	 */
	public void putVertexBefore(long targetVertexId, long movedVertexId) {
		long prevVertexId = getPreviousVertexId(targetVertexId);

		if ((targetVertexId == movedVertexId) || (prevVertexId == movedVertexId)) {
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
	protected void putVertexAfter(VertexImpl targetVertex, VertexImpl movedVertex) {
		assert (targetVertex != null) && targetVertex.isValid()	&& containsVertex(targetVertex);
		assert (movedVertex != null) && movedVertex.isValid() && containsVertex(movedVertex);
		assert targetVertex != movedVertex;

	}	
	
	/**
	 * Global methods, changes Vseq so that the vertex identified by movedVertexId is 
	 * directly after the vertex identified by targetVertexId
	 * @param targetVertexId global id of the target vertex
	 * @param movedVertexId global id of the vertex to be moved
	 */
	@Override
	public void putVertexAfter(long targetVertexId, long movedVertexId)  {
		assert (targetVertexId != 0) && (containsVertexId(targetVertexId));
		assert (targetVertexId != 0) && (containsVertexId(targetVertexId));
		
	
		long prevVertexId = getPreviousVertexId(targetVertexId);

		if ((targetVertexId == movedVertexId) || (prevVertexId == movedVertexId)) {
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
		return getEdgeObject(edge.getId()) == edge;
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
	public long createEdge(int edgeClassId) {
		return createEdge(edgeClassId, 0);
	}
	
	public long createEdge(int edgeClassId, long edgeId) {
		//set id
		if (isLoading()) {
			if (edgeId == 0) {
				throw new GraphException("Cannot add a edge without a predefined id during graph loading");
			} 
		} else {
			if (edgeId == 0) {
				edgeId =  convertToGlobalId(allocateEdgeIndex());
			} else {
				throw new GraphException("Cannot add a edge with a predefined id outside the graph loading");
			}
		}

		//instantiate object
		EdgeImpl v = (EdgeImpl) graphFactory.createEdgeDiskBasedStorage((Class<? extends Edge>) schema.getM1ClassForId(edgeClassId), edgeId, this);
		localDiskStorage.storeEdge(v);

		long toplevelSubgraphId = convertToGlobalId(1);
		
		getGraphData(0).vertexCount++;
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
			((VertexImpl) inc.getVertex())
					.removeIncidenceFromLambdaSeq((IncidenceImpl) inc);
			inc = e.getFirstIncidence();
		}
		for (Vertex vertex : vertices) {
			((VertexImpl) vertex).incidenceListModified();
		}

		removeEdgeFromESeq(edgeId);

	}
	


	
	/**
	 * Removes the edge v from the global edge sequence of this graph.
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
		
		//TODO: instead of the toplevel graph, the lowest subgraph the edge is 
		//      contained in needs to be determined. Because of the restrictions to 
		//      the ordering v may be only the first edge of the lowest graph 
		//      it is contained in
		long toplevelGraphId = convertToGlobalId(1);
		
		//if current edge is the first or last one in the local graph,
		//the respecitive values need to be set to its next or previous edge 
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

		
		//next and previous pointer of previous and next edge need to be set
		//in any case (only exception: its the globally first or last edge)
		if (prevV != 0)
			setNextEdgeId(prevV, nextV);
		if (nextV != 0)
			setPreviousEdgeId(prevV, nextV);
	
		
		//remove edge from storage
		freeEdgeIndex(convertToLocalId(edgeId));
		setPreviousEdgeId(edgeId, 0);
		setNextEdgeId(edgeId, 0);
		setVCount(toplevelGraphId, getVCount(toplevelGraphId) - 1);
		getLocalDiskStorage().removeEdgeFromDiskStorage(convertToLocalId(edgeId));
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
	protected void putEdgeBefore(EdgeImpl targetEdge,	EdgeImpl movedEdge) {
		assert (targetEdge != null) && targetEdge.isValid()
				&& containsEdge(targetEdge);
		assert (movedEdge != null) && movedEdge.isValid()
				&& containsEdge(movedEdge);
		assert targetEdge != movedEdge;
		putEdgeBefore(targetEdge.getId(), movedEdge.getId());
		
	}

	/**
	 * Global methods, changes Vseq so that the edge identified by movedEdgeId is 
	 * directly before the edge identified by targetEdgeId
	 * @param targetEdgeId global id of the target edge
	 * @param movedEdgeId global id of the edge to be moved
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
		assert (targetEdge != null) && targetEdge.isValid()	&& containsEdge(targetEdge);
		assert (movedEdge != null) && movedEdge.isValid() && containsEdge(movedEdge);
		assert targetEdge != movedEdge;

	}	
	
	/**
	 * Global methods, changes Vseq so that the edge identified by movedEdgeId is 
	 * directly after the edge identified by targetEdgeId
	 * @param targetEdgeId global id of the target edge
	 * @param movedEdgeId global id of the edge to be moved
	 */
	public void putEdgeAfter(long targetEdgeId, long movedEdgeId)  {
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
	 * **************************************************************************/

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

	
	public void incidenceListModified(int elemId) {
		int partialGraphId = getPartialGraphId(elemId);
		if (partialGraphId == localPartialGraphId) {
			localDiskStorage.incidenceListModified(elemId);
		} else {
			getGraphDatabase(partialGraphId).incidenceListModified(elemId);
		}
	}
	
	/**
	 * Connects the specified vertex <code>v</code> to the speficied edge <code>e</code> by an
	 * incidence of class <code>cls</code> and sets the incidence's id to the next locally 
	 * available incidence id
	 * @param cls
	 * @param vertex
	 * @param edge
	 */
	public long connect(Class<? extends Incidence> cls, long vertexId, long edgeId) {
		return connect(cls, vertexId, edgeId, 0);
	}
	
	/**
	 * Connects the specified vertex <code>v</code> to the speficied edge <code>e</code> by an
	 * incidence of class <code>cls</code> and sets the incidence's id to <code>id</code>
	 * @param cls
	 * @param vertex
	 * @param edge
	 * @param id
	 */
	@Override
	public long connect(Class<? extends Incidence> cls, long vertexId, long edgeId, long incId) {	
		IncidenceClass incClass = (IncidenceClass) schema.getTypeForId(schema.getClassId(cls));
		Direction dir = incClass.getDirection();
		
		//check id 
		if (incId != 0) {
			if (!isLoading()) {
				throw new GraphException("Incidences with a defined id may only be created during graph loading");
			}
		} else {
			incId = convertToGlobalId(allocateIncidenceIndex());
		}	
	    //call graph factory to create object
		IncidenceImpl newInc = (IncidenceImpl) graphFactory.createIncidenceDiskBasedStorage(cls, incId, this);
		
		//set incident edge and vertex ids of incidence 
		setIncidentEdgeId(incId, edgeId);
		setIncidentVertexId(incId, vertexId);
		setDirection(incId, dir);
		
		//append created incidence to lambda sequences of vertex and edge
		
		// add this incidence to the sequence of incidences of v
		if (getFirstIncidenceId(vertexId) == 0) {
			// v has no incidences
			setFirstIncidenceId(vertexId, incId);
			setLastIncidenceId(vertexId, incId);
		} else {
			long lastIncId = getLastIncidenceId(vertexId);
			setNextIncidenceIdAtVertexId(lastIncId, incId);
			setPreviousIncidenceIdAtVertexId(incId, lastIncId);
			setLastIncidenceId(vertexId, incId);
		}

		incidenceListModified(vertexId);

		if (getFirstIncidenceId(-edgeId) == 0) {
			// v has no incidences
			setFirstIncidenceId(-edgeId, incId);
			setLastIncidenceId(-edgeId, incId);
		} else {
			long lastIncId = getLastIncidenceId(-edgeId);
			setNextIncidenceIdAtEdgeId(lastIncId, incId);
			setPreviousIncidenceIdAtEdgeId(incId, lastIncId);
			setLastIncidenceId(-edgeId, incId);
		}

		incidenceListModified(vertexId);
		
		
		localDiskStorage.storeIncidence(newInc);
		if (!isLoading()) {
			notifyIncidenceAdded(incId);
		}
		return incId;
	}



	



	




	protected void notifyEdgeAdded(long edgeId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
			gdb.internalNotifyEdgeAdded(edgeId);
		}	
	}
	
	protected void notifyEdgeDeleted(long edgeId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
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
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
			gdb.internalNotifyVertexAdded(vertexId);
		}	
	}
	
	protected void notifyVertexDeleted(long vertexId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
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
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
			gdb.internalNotifyIncidenceAdded(incidenceId);
		}	
	}
	
	protected void notifyIncidenceDeleted(long incidenceId) {
		for (RemoteGraphDatabaseAccessWithInternalMethods gdb : partialGraphDatabases.values()) {
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
	public <T extends Record> T createRecord(Class<T> recordClass, GraphIO io) {
		T record = graphFactory.createRecord(recordClass, getGraphObject(convertToGlobalId(1)));
		try {
			record.readComponentValues(io);
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass,Map<String, Object> fields) {
		T record = graphFactory.createRecord(recordClass, getGraphObject(convertToGlobalId(1)));
		record.setComponentValues(fields);
		return record;
	}

	@Override
	public <T extends Record> T createRecord(Class<T> recordClass, Object... components) {
		T record = graphFactory.createRecord(recordClass, getGraphObject(convertToGlobalId(1)));
		record.setComponentValues(components);
		return record;
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
		// TODO Auto-generated method stub
		return null;
	}

	





}