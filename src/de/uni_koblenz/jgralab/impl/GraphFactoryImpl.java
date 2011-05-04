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

package de.uni_koblenz.jgralab.impl;

import java.lang.reflect.Constructor;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.exception.M1ClassAccessException;

/**
 * Default implementation for GraphFactory. Per default every create-method
 * creates an instance of exactly the specified class. To change this use
 * <code>setImplementationClass</code>-methods. Class is abstract because only
 * factories which are specific for their schema should be used.
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class GraphFactoryImpl implements GraphFactory {

	// Maps for standard support.
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphMap;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> viewGraphMap;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForEdgeMap;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForVertexMap;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> partialGraphMap;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> partialSubordinateGraphMap;
	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeMap;
	protected HashMap<Class<? extends BinaryEdge>, Constructor<? extends BinaryEdge>> binaryEdgeMap;
	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexMap;
	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceMap;
	protected HashMap<Class<? extends Record>, Constructor<? extends Record>> recordMap;

	/**
	 * Creates and initializes a new <code>GraphFactoryImpl</code>.
	 */
	protected GraphFactoryImpl() {
		this.createMapsForStandardSupport();
	}

	private void createMapsForStandardSupport() {
		graphMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		viewGraphMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForVertexMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForEdgeMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		partialGraphMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		partialSubordinateGraphMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		edgeMap = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
		binaryEdgeMap = new HashMap<Class<? extends BinaryEdge>, Constructor<? extends BinaryEdge>>();
		vertexMap = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
		incidenceMap = new HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>>();
		recordMap = new HashMap<Class<? extends Record>, Constructor<? extends Record>>();
	}

	@Override
	public Edge createEdge(Class<? extends Edge> edgeClass, int id, Graph g,
			Vertex alpha, Vertex omega) {
		try {
			Edge e = binaryEdgeMap.get(edgeClass).newInstance(id, g, alpha,
					omega);
			return e;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new M1ClassAccessException("Cannot create edge of class "
					+ edgeClass.getCanonicalName(), ex);
		}
	}

	@Override
	public Edge createEdge(Class<? extends Edge> edgeClass, int id, Graph g) {
		try {
			Edge e = edgeMap.get(edgeClass).newInstance(id, g);
			return e;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new M1ClassAccessException("Cannot create edge of class "
					+ edgeClass.getCanonicalName(), ex);
		}
	}

	public Graph createGraph(Class<? extends Graph> graphClass, String id,
			int vMax, int eMax) {
		try {
			Graph g = graphMap.get(graphClass).newInstance(id, vMax, eMax);
			JGraLabServerImpl.getLocalInstance().putGraph(id, g);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}

	public Graph createGraph(Class<? extends Graph> graphClass, String id) {
		try {
			Graph g = graphMap.get(graphClass).newInstance(id, 1000, 1000);
			JGraLabServerImpl.getLocalInstance().putGraph(id, g);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}

	@Override
	public <T extends Incidence> T createIncidence(Class<T> incidenceClass,
			int id, Vertex v, Edge e) {
		try {
			@SuppressWarnings("unchecked")
			T i = (T) incidenceMap.get(incidenceClass).newInstance(id, v, e);
			return i;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new M1ClassAccessException(
					"Cannot create incidence of class "
							+ incidenceClass.getCanonicalName(), ex);
		}
	}

	public Vertex createVertex(Class<? extends Vertex> vertexClass, int id,
			Graph g) {
		try {
			Vertex v = vertexMap.get(vertexClass).newInstance(id, g);
			return v;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new M1ClassAccessException("Cannot create vertex of class "
					+ vertexClass.getCanonicalName(), ex);
		}
	}

	public void setGraphImplementationClass(
			Class<? extends Graph> originalClass,
			Class<? extends Graph> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { String.class, int.class, int.class };
				graphMap.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	public void setVertexImplementationClass(
			Class<? extends Vertex> originalClass,
			Class<? extends Vertex> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { int.class, Graph.class };
				vertexMap.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for vertexclass"
								+ implementationClass, ex);
			}
		}
	}

	public void setIncidenceImplementationClass(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { int.class, Vertex.class, Edge.class };
				incidenceMap.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for incidenceclass"
								+ implementationClass, ex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setEdgeImplementationClass(Class<? extends Edge> originalClass,
			Class<? extends Edge> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { int.class, Graph.class };
				edgeMap.put(originalClass,
						implementationClass.getConstructor(params));

				List<Class<?>> interfaces = new ArrayList<Class<?>>();
				for (Class<?> c : originalClass.getInterfaces()) {
					interfaces.add(c);
				}
				if (interfaces.contains(BinaryEdge.class)) {
					Class<?>[] binaryParams = { int.class, Graph.class,
							Vertex.class, Vertex.class };
					Constructor<BinaryEdge> binaryConstructor = (Constructor<BinaryEdge>) implementationClass
							.getConstructor(binaryParams);
					binaryEdgeMap.put(
							(Class<? extends BinaryEdge>) originalClass,
							binaryConstructor);
				}

			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for edgeclass"
								+ implementationClass, ex);
			}
		}
	}

	public void setRecordImplementationClass(Class<? extends Record> m1Class,
			Class<? extends Record> implementationClass) {
		if (isSuperclassOrEqual(m1Class, implementationClass)) {
			try {
				Class<?>[] params = { Graph.class };
				recordMap.put(m1Class,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for record"
								+ implementationClass, ex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Record> T createRecord(Class<T> recordDomain, Graph g) {
		try {
			T r = (T) recordMap.get(recordDomain).newInstance(g);
			return r;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new M1ClassAccessException("Cannot create record of class "
					+ recordDomain.getCanonicalName(), ex);
		}
	}

	// -------------------------------------------------------------------------
	// Helper methods.
	// -------------------------------------------------------------------------

	/**
	 * tests if a is a superclass of b or the same class than b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	protected boolean isSuperclassOrEqual(Class<?> a, Class<?> b) {
		if (a == b) {
			return true;
		}
		if (implementsInterface(b, a)) {
			return true;
		}
		while (b.getSuperclass() != null) {
			if (b.getSuperclass() == a) {
				return true;
			}
			if (implementsInterface(b, a)) {
				return true;
			}
			b = b.getSuperclass();
		}
		return false;
	}

	/**
	 * tests if class a implements the interface b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	protected boolean implementsInterface(Class<?> a, Class<?> b) {
		Class<?>[] list = a.getInterfaces();
		for (Class<?> c : list) {
			if (c == b) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ViewGraphImpl createViewGraph(Graph viewGraph, int level)
			throws RemoteException {
		try {
			Class<? extends Graph> graphClass = viewGraph.getM1Class();
			ViewGraphImpl g = (ViewGraphImpl) viewGraphMap.get(graphClass)
					.newInstance(viewGraph, level);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException(
					"Cannot create view graph for graph of class "
							+ viewGraph.getGraphClass().getQualifiedName(), ex);
		}
	}

	@Override
	public SubordinateGraphImpl createSubordinateGraph(Vertex vertex)
			throws RemoteException {
		try {
			Class<? extends Graph> graphClass = vertex.getGraph().getM1Class();
			SubordinateGraphImpl g = (SubordinateGraphImpl) subordinateGraphForVertexMap
					.get(graphClass).newInstance(vertex);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException(
					"Cannot create subordinate graph for elem of class "
							+ vertex.getType().getQualifiedName(), ex);
		}
	}

	@Override
	public SubordinateGraphImpl createSubordinateGraph(Edge vertex)
			throws RemoteException {
		try {
			Class<? extends Graph> graphClass = vertex.getGraph().getM1Class();
			SubordinateGraphImpl g = (SubordinateGraphImpl) subordinateGraphForEdgeMap
					.get(graphClass).newInstance(vertex);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException(
					"Cannot create subordinate graph for elem of class "
							+ vertex.getType().getQualifiedName(), ex);
		}
	}

	@Override
	public PartialGraphImpl createPartialGraph(Graph completeGraph)
			throws RemoteException {
		try {
			Class<? extends Graph> graphClass = completeGraph.getM1Class();
			PartialGraphImpl g = (PartialGraphImpl) partialGraphMap.get(
					graphClass).newInstance(completeGraph);
			JGraLabServerImpl.getLocalInstance().putGraph(
					Integer.toString(g.getId()), g);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException(
					"Cannot create view graph for graph of class "
							+ completeGraph.getGraphClass().getUniqueName(), ex);
		}
	}

	@Override
	public PartialSubordinateGraphImpl createPartialSubordinateGraph(
			GraphElement<?, ?, ?> elem) throws RemoteException {
		try {
			Class<? extends Graph> graphClass = elem.getGraph().getM1Class();
			PartialSubordinateGraphImpl g = (PartialSubordinateGraphImpl) partialSubordinateGraphMap
					.get(graphClass).newInstance(elem);
			JGraLabServerImpl.getLocalInstance().putGraph(
					Integer.toString(g.getId()), g);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException(
					"Cannot create subordinate graph for elem of class "
							+ elem.getType().getQualifiedName(), ex);
		}
	}

	@Override
	public void setSubordinateGraphImplementationClass(
			Class<? extends Graph> originalClass,
			Class<? extends SubordinateGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { Vertex.class };
				subordinateGraphForVertexMap.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramse = { Edge.class };
				subordinateGraphForEdgeMap.put(originalClass,
						implementationClass.getConstructor(paramse));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	@Override
	public void setViewGraphImplementationClass(
			Class<? extends Graph> originalClass,
			Class<? extends ViewGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { originalClass, int.class };
				viewGraphMap.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	@Override
	public void setPartialGraphImplementationClass(
			Class<? extends Graph> originalClass,
			Class<? extends ViewGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { Graph.class, int.class };
				partialGraphMap.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	@Override
	public void setPartialSubordinateGraphImplementationClass(
			Class<? extends Graph> originalClass,
			Class<? extends ViewGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { GraphElement.class, int.class };
				partialSubordinateGraphMap.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

}
