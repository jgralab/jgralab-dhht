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

import de.uni_koblenz.jgralab.impl.disk.EdgeContainer;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl;
import de.uni_koblenz.jgralab.impl.disk.IncidenceContainer;
import de.uni_koblenz.jgralab.impl.disk.RemoteGraphDatabaseAccess;
import de.uni_koblenz.jgralab.impl.disk.VertexContainer;
import de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl;
import de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl;

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
	public Graph createGraph(Class<? extends Graph> graphClass, String id, int vMax, int eMax);
	
	/**
	 * creates a Graph-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified graphClass.
	 */
	public Graph createGraph(Class<? extends Graph> graphClass, String id);
	
	/**
	 * creates a Graph-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified graphClass.
	 * @param graphDatabase 
	 */
	public Graph createGraphDiskBasedStorage(Class<? extends Graph> graphClass, GraphDatabaseBaseImpl graphDatabase);
	
	/**
	 * creates a local graph proxy object for an existing remote partial or global graph.
	 * of the specified class. The returned object may be an instance of a subclass of the 
	 * specified graphClass.
	 */
	public Graph createGraphProxy(Class<? extends Graph> graphClass, String uid, int id, GraphDatabaseBaseImpl localDatabase);

	/**
	 * creates a View-Graph object for the specified class. The returned object
	 * may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param viewGraph
	 * @param level
	 * @return
	 * @throws RemoteException 
	 */
	public ViewGraphImpl createViewGraph(Graph viewGraph, int level);
	
	/**
	 * creates a View-Graph object for the specified class. The returned object
	 * may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param viewGraph
	 * @param level
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl createViewGraphDiskBasedStorage(Graph viewGraph, int level);


	/**
	 * creates a Subordinate-Graph object for the specified class. The returned
	 * object may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public SubordinateGraphImpl createSubordinateGraph(Vertex vertex);
	
	/**
	 * creates a Subordinate-Graph object for the specified class. The returned
	 * object may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public SubordinateGraphImpl createSubordinateGraph(Edge edge);
	
	
	/**
	 * creates a Subordinate-Graph object for the specified class. The returned
	 * object may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl createSubordinateGraphDiskBasedStorageInVertex(GraphDatabaseBaseImpl graphDatabase, long vertexId);
	

	/**
	 * creates a Subordinate-Graph object for the specified class. The returned
	 * object may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl createSubordinateGraphDiskBasedStorageInEdge(GraphDatabaseBaseImpl graphDatabase, long edgeId);
	/**
	 * creates a Vertex-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified vertexClass.
	 */
	public Vertex createVertex(Class<? extends Vertex> vertexClass, int id,	Graph g);
	
	/**
	 * creates a Vertex-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified vertexClass.
	 */
	public Vertex createVertexDiskBasedStorage(Class<? extends Vertex> vc, long id, GraphDatabaseBaseImpl localGraphDatabase);

	public Vertex createVertexProxy(Class<? extends Vertex> vertexClass, long id, GraphDatabaseBaseImpl graphDatabase, RemoteGraphDatabaseAccess storingGraphDatabase);
	
	public Vertex reloadLocalVertex(Class<? extends Vertex> vertexClass, long id, GraphDatabaseBaseImpl localGraphDatabase, VertexContainer container);
	
	
	
	/**
	 * creates a Edge-object for the specified class. The returned object may be
	 * an instance of a subclass of the specified edgeClass.
	 */
	public Edge createEdge(Class<? extends Edge> edgeClass, int id, Graph g);
	
	/**
	 * creates a Edge-object for the specified class. The returned object may be
	 * an instance of a subclass of the specified edgeClass.
	 */
	public Edge createEdge(Class<? extends Edge> edgeClass, int id, Graph g, Vertex alpha, Vertex omega);
	
	
	/**
	 * creates a Edge-object for the specified class. The returned object may be
	 * an instance of a subclass of the specified edgeClass.
	 */
	public Edge createEdgeDiskBasedStorage(Class<? extends Edge> edgeClass, long id, GraphDatabaseBaseImpl graphDatabase);

	
	/**
	 * creates an local proxy for the remote edge-object identified by its id <code>remoteEdgeId</code>. The returned object is 
	 * an instance of the class defined by <code>setEdgeImplementationClass</code> for the interface defined by <code>edgeClass</code>
	 */
	public Edge createEdgeProxy(Class<? extends Edge> edgeClass, long id, GraphDatabaseBaseImpl graphDatabase, RemoteGraphDatabaseAccess remoteDatabase);

	
	
	/**
	 * reloads an local edge from disk storage and initializes the implementation class specified for
	 * that edge class 
	 * @param edgeClass
	 * @param id
	 * @param graphDatabase
	 * @return
	 */
	public Edge reloadLocalEdge(Class<? extends Edge> edgeClass, long id, GraphDatabaseBaseImpl graphDatabase, EdgeContainer container);


	
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
	public <T extends Incidence> T createIncidence(Class<T> incidenceClass,	int id, Vertex v, Edge e);
	
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
	public <T extends Incidence> T createIncidenceDiskBasedStorage(Class<? extends T> ic, long incidenceId, long vertexId, long edgeId, GraphDatabaseBaseImpl graphDatabase);

	public <T extends Incidence> T createIncidenceProxy(Class<? extends T> ic, long id, GraphDatabaseBaseImpl graphDatabase, RemoteGraphDatabaseAccess remoteDatabase);

	public <T extends Incidence> T  reloadLocalIncidence(Class<? extends T> incidenceClass, long id, GraphDatabaseBaseImpl graphDatabase, IncidenceContainer container);
	
	
	
	
	
	
	public void setGraphImplementationClass(
			Class<? extends Graph> graphM1Class,
			Class<? extends Graph> implementationClass);
	
	public void setGraphImplementationClassForDiskBasedStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends Graph> implementationClass);

	public void setSubordinateGraphImplementationClass(
			Class<? extends Graph> graphM1Class,
			Class<? extends SubordinateGraphImpl> implementationClass);
	
	public void setSubordinateGraphImplementationClassForDiskBasedStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl> implementationClass);

	public void setViewGraphImplementationClass(
			Class<? extends Graph> graphM1Class,
			Class<? extends ViewGraphImpl> implementationClass);
	
	public void setViewGraphImplementationClassForDiskBasedStorage(
			Class<? extends Graph> graphM1Class,
			Class<? extends de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl> implementationClass);
		
	public void setVertexImplementationClass(
			Class<? extends Vertex> vertexM1Class,
			Class<? extends Vertex> implementationClass);
	
	public void setVertexImplementationClassForDiskBasedStorage(
			Class<? extends Vertex> vertexM1Class,
			Class<? extends Vertex> implementationClass);

	public void setEdgeImplementationClass(Class<? extends Edge> edgeM1Class,
			Class<? extends Edge> implementationClass);

	public void setEdgeImplementationClassForDiskBasedStorage(Class<? extends Edge> edgeM1Class,
			Class<? extends Edge> implementationClass);
	
	/**
	 * Creates an record of class <code>recordDomain</code> in the graph g
	 */
	public <T extends Record> T createRecord(Class<T> recordDomain, Graph g);

	/**
	 * Assigns an implementation class with transaction support for a
	 * <code>Record</code>.
	 * 
	 * @param record
	 * @param implementationClass
	 */
	public void setRecordImplementationClass(Class<? extends Record> record,
			Class<? extends Record> implementationClass);





}
