package de.uni_koblenz.jgralab.impl.disk;

import java.util.Collection;
import java.util.Map;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.JGraLabList;
import de.uni_koblenz.jgralab.JGraLabMap;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;

public class PartialGraphDatabase extends GraphDatabaseBaseImpl {

	private final CompleteGraphDatabase completeGraphDatabase;

	public PartialGraphDatabase(Schema schema, String uniqueGraphId, String hostnameOfCompleteGraph, int localPartialGraphId) {
		super(schema, uniqueGraphId, 1, localPartialGraphId); 
		completeGraphDatabase = (CompleteGraphDatabase) localJGraLabServer.getRemoteInstance(hostnameOfCompleteGraph).getGraphDatabase(uniqueGraphId);
	}

	@Override
	public String getHostname(int id) {
		return completeGraphDatabase.getHostname(id);
	}

	@Override
	public int getFreePartialGraphId() {
		return completeGraphDatabase.getFreePartialGraphId();
	}

	@Override
	public void registerPartialGraph(int id, String hostname) {
		completeGraphDatabase.registerPartialGraph(id, hostname);
	}

	public void releasePartialGraphId(int partialGraphId) {
		completeGraphDatabase.releasePartialGraphId(partialGraphId);
	}

	public Graph createPartialGraph(GraphClass gc, String hostname) {
		return completeGraphDatabase.createPartialGraph(gc, hostname);
	}

	@Override
	public void deletePartialGraph(int partialGraphId) {
		completeGraphDatabase.deletePartialGraph(partialGraphId);
	}

	@Override
	public void edgeListModified() {
		edgeListVersion++;
		completeGraphDatabase.edgeListModified();
	}

	@Override
	public void vertexListModified() {
		vertexListVersion++;
		completeGraphDatabase.vertexListModified();
	}

	@Override
	public void graphModified() {
		graphVersion++;
		completeGraphDatabase.graphModified();
	}

	/* **************************************************************************
	 * Methods to access traversal context
	 * *************************************************************************
	 */

	@Override
	public Graph getTraversalContext() {
		return completeGraphDatabase.getTraversalContext();
	}

	@Override
	public void releaseTraversalContext() {
		completeGraphDatabase.releaseTraversalContext();
	}

	@Override
	public void setTraversalContext(Graph traversalContext) {
		completeGraphDatabase.setTraversalContext(traversalContext);
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
	public int getGraphTypeId(int subgraphId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getContainingElementId(long globalSubgraphId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getIncidenceListVersion(long elementId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setIncidenceListVersion(long elementId,
			long incidenceListVersion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVCount(long count, long count2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getECount(long globalSubgraphId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setECount(long globalSubgraphId, long count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxECount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getICount(long globalSubgraphId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getFirstIncidenceId(long elemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLastIncidenceId(long elemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNextIncidenceIdAtVertexId(long incId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPreviousIncidenceIdAtVertexId(long globalIncidenceId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNextIncidenceIdAtEdgeId(long incId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPreviousIncidenceIdAtEdgeId(long incId) {
		// TODO Auto-generated method stub
		return 0;
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

}
