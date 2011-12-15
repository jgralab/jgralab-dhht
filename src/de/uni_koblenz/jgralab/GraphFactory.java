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

package de.uni_koblenz.jgralab;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.impl.RemoteGraphDatabaseAccess;

/**
 * Creates instances of graphs, edges and vertices. By changing factory it is
 * possible to extend Graph, Vertex, and Edge classes used in a graph.
 * 
 * @author ist@uni-koblenz.de
 */
public interface GraphFactory {

	/**
	 * creates a Graph-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified graphClass.
	 */
	public Graph createGraph_InMemoryStorage(Class<? extends Graph> graphClass, String id, int vMax, int eMax);
	
	/**
	 * creates a Graph-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified graphClass.
	 */
	public Graph createGraph_InMemoryStorage(Class<? extends Graph> graphClass, String id);
	
	
	/**
	 * creates a Graph-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified graphClass.
	 */
	public Graph createGraph_DistributedStorage(Class<? extends Graph> graphClass, String uniqueGraphId, long subgraphId, de.uni_koblenz.jgralab.impl.memdistributed.GraphDatabaseBaseImpl graphDatabase, RemoteGraphDatabaseAccess storingGraphDatabase);
		
	/**
	 * creates a Graph-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified graphClass.
	 * @param graphDatabase 
	 */
	public Graph createGraph_DiskBasedStorage(Class<? extends Graph> graphClass, String uniqueGraphId, long subgraphId, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, RemoteGraphDatabaseAccess storingGraphDatabase);
	
	/**
	 * creates a View-Graph object for the specified class. The returned object
	 * may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param viewGraph
	 * @param level
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl createViewGraph_InMemoryStorage(Graph viewGraph, int level);
	
	/**
	 * creates a View-Graph object for the specified class. The returned object
	 * may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param viewGraph
	 * @param level
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.memdistributed.ViewGraphImpl createViewGraph_DistributedStorage(Graph viewGraph, int level);
	
	/**
	 * creates a View-Graph object for the specified class. The returned object
	 * may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param viewGraph
	 * @param level
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl createViewGraph_DiskBasedStorage(Graph viewGraph, int level);


	/**
	 * creates a Subordinate-Graph object for the specified class. The returned
	 * object may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl createSubordinateGraphInVertex_InMemoryStorage(Vertex vertex);
	
	/**
	 * creates a Subordinate-Graph object for the specified class. The returned
	 * object may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl createSubordinateGraphInEdge_InMemoryStorage(Edge edge);
	
	
	/**
	 * creates a Subordinate-Graph object for the specified class. The returned
	 * object may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.memdistributed.SubordinateGraphImpl createSubordinateGraphInVertex_DistributedStorage(de.uni_koblenz.jgralab.impl.memdistributed.GraphDatabaseBaseImpl graphDatabase, long vertexId);
	

	/**
	 * creates a Subordinate-Graph object for the specified class. The returned
	 * object may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.memdistributed.SubordinateGraphImpl createSubordinateGraphInEdge_DistributedStorage(de.uni_koblenz.jgralab.impl.memdistributed.GraphDatabaseBaseImpl graphDatabase, long edgeId);

	
	/**
	 * creates a Subordinate-Graph object for the specified class. The returned
	 * object may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl createSubordinateGraphInVertex_DiskBasedStorage(de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, long vertexId);
	

	/**
	 * creates a Subordinate-Graph object for the specified class. The returned
	 * object may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl createSubordinateGraphInEdge_DiskBasedStorage(de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, long edgeId);

	
	
	
	/**
	 * creates a Vertex-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified vertexClass.
	 */
	public Vertex createVertex_InMemoryStorage(Class<? extends Vertex> vertexClass, int id,	Graph g);
	
	/**
	 * creates a Vertex-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified vertexClass.
	 */
	public Vertex createVertex_DistributedStorage(Class<? extends Vertex> vertexClass, long id,	de.uni_koblenz.jgralab.impl.memdistributed.GraphDatabaseBaseImpl graphDatabase);
	
	/**
	 * creates a Vertex-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified vertexClass.
	 */
	public Vertex createVertex_DiskBasedStorage(Class<? extends Vertex> vc, long id, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl localGraphDatabase);

	/**
	 * creates a Vertex-proxy for the specified class, acting as local API for the 
	 * respective remote vertex identified by its global id. The returned object may
	 * be an instance of a subclass of the specified vertexClass.
	 */
	public Vertex createVertexProxy_DistributedStorage(Class<? extends Vertex> vertexClass, long id, de.uni_koblenz.jgralab.impl.memdistributed.GraphDatabaseBaseImpl graphDatabase, RemoteGraphDatabaseAccess storingGraphDatabase);
	
	/**
	 * creates a Vertex-proxy for the specified class, acting as local API for the 
	 * respective remote vertex identified by its global id. The returned object may
	 * be an instance of a subclass of the specified vertexClass.
	 */
	public Vertex createVertexProxy_DiskBasedStorage(Class<? extends Vertex> vertexClass, long id, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, RemoteGraphDatabaseAccess storingGraphDatabase);
	
	
	public Vertex reloadLocalVertex(Class<? extends Vertex> vertexClass, long id, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl localGraphDatabase, de.uni_koblenz.jgralab.impl.disk.VertexContainer container);
	
	
	
	/**
	 * creates a Edge-object for the specified class. The returned object may be
	 * an instance of a subclass of the specified edgeClass.
	 */
	public Edge createEdge_InMemoryStorage(Class<? extends Edge> edgeClass, int id, Graph g);

	/**
	 * creates a Edge-object for the specified class. The returned object may be
	 * an instance of a subclass of the specified edgeClass.
	 */
	public Edge createEdge_DistributedStorage(Class<? extends Edge> edgeClass, long id, de.uni_koblenz.jgralab.impl.memdistributed.GraphDatabaseBaseImpl graphDatabase);
	
	
	/**
	 * creates a Edge-object for the specified class. The returned object may be
	 * an instance of a subclass of the specified edgeClass.
	 */
	public Edge createEdge_DiskBasedStorage(Class<? extends Edge> edgeClass, long id, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase);

	
	/**
	 * creates an local proxy for the remote edge-object identified by its id <code>remoteEdgeId</code>. The returned object is 
	 * an instance of the class defined by <code>setEdgeImplementationClass</code> for the interface defined by <code>edgeClass</code>
	 */
	public Edge createEdgeProxy_DistributedStorage(Class<? extends Edge> edgeClass, long id, de.uni_koblenz.jgralab.impl.memdistributed.GraphDatabaseBaseImpl graphDatabase, RemoteGraphDatabaseAccess remoteDatabase);
	
	
	/**
	 * creates an local proxy for the remote edge-object identified by its id <code>remoteEdgeId</code>. The returned object is 
	 * an instance of the class defined by <code>setEdgeImplementationClass</code> for the interface defined by <code>edgeClass</code>
	 */
	public Edge createEdgeProxy_DiskBasedStorage(Class<? extends Edge> edgeClass, long id, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, RemoteGraphDatabaseAccess remoteDatabase);

	
	
	/**
	 * reloads an local edge from disk storage and initializes the implementation class specified for
	 * that edge class 
	 * @param edgeClass
	 * @param id
	 * @param graphDatabase
	 * @return
	 */
	public Edge reloadLocalEdge(Class<? extends Edge> edgeClass, long id, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, de.uni_koblenz.jgralab.impl.disk.EdgeContainer container);


	
	/**
	 * Creates a {@link Incidence}-object for the specified class. The returned
	 * object may be an instance of a subclass of the specified
	 * <code>incidenceClass</code>.
	 * 
	 * @param incidenceClass
	 *            {@link Class}
	 * @param id
	 *            the id of this incidence
	 * @param v
	 *            {@link Vertex} to which the created {@link Incidence} is
	 *            connected
	 * @param e
	 *            {@link Edge} to which the created {@link Incidence} is
	 *            connected
	 * @return {@link Incidence}
	 */
	public <T extends Incidence> T createIncidence_InMemoryStorage(Class<T> incidenceClass,	int id, Vertex v, Edge e);
	
	public <T extends Incidence> T createIncidence_DistributedStorage(Class<? extends T> ic, long incidenceId, long vertexId, long edgeId, de.uni_koblenz.jgralab.impl.memdistributed.GraphDatabaseBaseImpl graphDatabase);
	
	/**
	 * Creates a {@link Incidence}-object for the specified class. The returned
	 * object may be an instance of a subclass of the specified
	 * <code>incidenceClass</code>.
	 * 
	 * @param incidenceClass
	 *            {@link Class}
	 * @param id
	 *            the id of this incidence
	 * @param v
	 *            {@link Vertex} to which the created {@link Incidence} is
	 *            connected
	 * @param e
	 *            {@link Edge} to which the created {@link Incidence} is
	 *            connected
	 * @return {@link Incidence}
	 */
	public <T extends Incidence> T createIncidence_DiskBasedStorage(Class<? extends T> ic, long incidenceId, long vertexId, long edgeId, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase);
	
	
	public <T extends Incidence> T createIncidenceProxy_DistributedStorage(Class<? extends T> ic, long id, de.uni_koblenz.jgralab.impl.memdistributed.GraphDatabaseBaseImpl graphDatabase, RemoteGraphDatabaseAccess remoteDatabase);


	public <T extends Incidence> T createIncidenceProxy_DiskBasedStorage(Class<? extends T> ic, long id, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, RemoteGraphDatabaseAccess remoteDatabase);
	


	public <T extends Incidence> T reloadLocalIncidence(Class<? extends T> incidenceClass, long id, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, de.uni_koblenz.jgralab.impl.disk.IncidenceContainer container);
	
	
	
	
	
	
	public void setGraphImplementationClass_InMemoryStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.mem.GraphBaseImpl> implementationClass);
	
	
	public void setGraphImplementationClass_DistributedStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.memdistributed.GraphBaseImpl> implementationClass);
	
	
	public void setGraphImplementationClass_DiskBasedStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.disk.GraphBaseImpl> implementationClass);
	

	public void setSubordinateGraphImplementationClass_InMemoryStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl> implementationClass);
	
	public void setSubordinateGraphImplementationClass_DistributedStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.memdistributed.SubordinateGraphImpl> implementationClass);
	
	public void setSubordinateGraphImplementationClass_DiskBasedStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl> implementationClass);

	public void setViewGraphImplementationClass_InMemoryStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl> implementationClass);
	
	public void setViewGraphImplementationClass_DistributedStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.memdistributed.ViewGraphImpl> implementationClass);
	
	public void setViewGraphImplementationClass_DiskBasedStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl> implementationClass);
		
	
	
	/**
	 * Sets the implementation class to be used for all vertices of the given M1 Class vertexM1Class if a graph
	 * is created using the in-memory storage facility
	 * @param vertexM1Class
	 * @param implementationClass
	 */
	public void setVertexImplementationClass_InMemoryStorage(
			Class<? extends Vertex> vertexM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.mem.VertexImpl> implementationClass);
	
	public void setVertexImplementationClass_DistributedStorage(
			Class<? extends Vertex> vertexM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.memdistributed.VertexImpl> implementationClass);
	
	public void setVertexImplementationClass_DiskBasedStorage(
			Class<? extends Vertex> vertexM1Class,
			Class<? extends  de.uni_koblenz.jgralab.impl.disk.VertexImpl> implementationClass);
	

	public void setVertexProxyImplementationClass_DistributedStorage(
			Class<? extends Vertex> vertexM1Class,
			Class<? extends de.uni_koblenz.jgralab.Vertex> implementationClass);
	
	public void setVertexProxyImplementationClass_DiskBasedStorage(
			Class<? extends Vertex> vertexM1Class,
			Class<? extends  de.uni_koblenz.jgralab.Vertex> implementationClass);
	
	
	
	public void setEdgeImplementationClass_InMemoryStorage(Class<? extends Edge> edgeM1Class,
			Class<? extends Edge> implementationClass);
	
	public void setEdgeImplementationClass_DistributedStorage(Class<? extends Edge> edgeM1Class,
			Class<? extends Edge> implementationClass);

	public void setEdgeImplementationClass_DiskBasedStorage(Class<? extends Edge> edgeM1Class,
			Class<? extends Edge> implementationClass);
	
	public void setEdgeProxyImplementationClass_DistributedStorage(Class<? extends Edge> edgeM1Class,
			Class<? extends Edge> implementationClass);
	
	public void setEdgeProxyImplementationClass_DiskBasedStorage(Class<? extends Edge> edgeM1Class,
			Class<? extends Edge> implementationClass);
	
	
	
	
	
	public void setIncidenceImplementationClass_InMemoryStorage(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass);
	
	public void setIncidenceImplementationClass_DistributedStorage(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass);
	
	public void setIncidenceImplementationClass_DiskBasedStorage(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass);

	public void setIncidenceProxyImplementationClass_DistributedStorage(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass);
	
	public void setIncidenceProxyImplementationClass_DiskBasedStorage(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass);

	
	
	/**
	 * Creates an record of class <code>recordDomain</code> in the graph g
	 */
	public <T extends Record> T createRecord_InMemoryStorage(Class<T> recordDomain, Graph g);
	
	/**
	 * Creates an record of class <code>recordDomain</code> in the graph g
	 */
	public <T extends Record> T createRecord_DistributedStorage(Class<T> recordDomain, Graph g);
	
	/**
	 * Creates an record of class <code>recordDomain</code> in the graph g
	 */
	public <T extends Record> T createRecord_DiskBasedStorage(Class<T> recordDomain, Graph g);

	/**
	 * Assigns an implementation class with transaction support for a
	 * <code>Record</code>.
	 * 
	 * @param record
	 * @param implementationClass
	 */
	public void setRecordImplementationClass_InMemoryStorage(Class<? extends Record> record,
			Class<? extends Record> implementationClass);
	
	public void setRecordImplementationClass_DistributedStorage(Class<? extends Record> record,
			Class<? extends Record> implementationClass);
	
	public void setRecordImplementationClass_DiskBasedStorage(Class<? extends Record> record,
			Class<? extends Record> implementationClass);






}
