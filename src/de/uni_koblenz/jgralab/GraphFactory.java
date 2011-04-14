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

import de.uni_koblenz.jgralab.impl.PartialGraphImpl;
import de.uni_koblenz.jgralab.impl.PartialSubordinateGraphImpl;
import de.uni_koblenz.jgralab.impl.SubordinateGraphImpl;
import de.uni_koblenz.jgralab.impl.ViewGraphImpl;

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
	public Graph createGraph(Class<? extends Graph> graphClass, String id,
			int vMax, int eMax);

	/**
	 * creates a Graph-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified graphClass.
	 */
	public Graph createGraph(Class<? extends Graph> graphClass, String id);

	/**
	 * creates a View-Graph object for the specified class. The returned object
	 * may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param viewGraph
	 * @param level
	 * @return
	 * @throws RemoteException 
	 */
	public ViewGraphImpl createViewGraph(Graph viewGraph, int level) throws RemoteException;

	/**
	 * creates a Subordinate-Graph object for the specified class. The returned
	 * object may be an instance of a subclass of the specified graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public SubordinateGraphImpl createSubordinateGraph(
			GraphElement<?, ?, ?> elem) throws RemoteException;

	/**
	 * creates a PartialSubordinate-Graph object for the specified class. The
	 * returned object may be an instance of a subclass of the specified
	 * graphClass.
	 * 
	 * @param elem
	 * @return
	 * @throws RemoteException 
	 */
	public PartialSubordinateGraphImpl createPartialSubordinateGraph(
			GraphElement<?, ?, ?> elem) throws RemoteException;

	/**
	 * creates a Partial-Graph object for the specified remote graph. The
	 * returned object may be an instance of a subclass of the specified
	 * graphClass.
	 * 
	 * @param compelteGraph
	 * @return
	 */
	public PartialGraphImpl createPartialGraph(Graph completeGraph) throws RemoteException;

	/**
	 * creates a Vertex-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified vertexClass.
	 */
	public Vertex createVertex(Class<? extends Vertex> vertexClass, int id,
			Graph g);

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
	public <T extends Incidence> T createIncidence(Class<T> incidenceClass,
			int id, Vertex v, Edge e);

	/**
	 * creates a Edge-object for the specified class. The returned object may be
	 * an instance of a subclass of the specified edgeClass.
	 */
	public Edge createEdge(Class<? extends Edge> edgeClass, int id, Graph g);

	/**
	 * creates a Edge-object for the specified class. The returned object may be
	 * an instance of a subclass of the specified edgeClass.
	 */
	public Edge createEdge(Class<? extends Edge> edgeClass, int id, Graph g,
			Vertex alpha, Vertex omega);

	public void setGraphImplementationClass(
			Class<? extends Graph> graphM1Class,
			Class<? extends Graph> implementationClass);

	public void setSubordinateGraphImplementationClass(
			Class<? extends Graph> graphM1Class,
			Class<? extends SubordinateGraphImpl> implementationClass);

	public void setViewGraphImplementationClass(
			Class<? extends Graph> graphM1Class,
			Class<? extends ViewGraphImpl> implementationClass);

	void setPartialGraphImplementationClass(
			Class<? extends Graph> originalClass,
			Class<? extends ViewGraphImpl> implementationClass);

	void setPartialSubordinateGraphImplementationClass(
			Class<? extends Graph> originalClass,
			Class<? extends ViewGraphImpl> implementationClass);

	public void setVertexImplementationClass(
			Class<? extends Vertex> vertexM1Class,
			Class<? extends Vertex> implementationClass);

	public void setEdgeImplementationClass(Class<? extends Edge> edgeM1Class,
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
