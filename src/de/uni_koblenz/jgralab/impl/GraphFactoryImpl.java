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
import java.util.HashMap;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
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

	// FIXME Remove redundancies! Why is this all in one class anyway? Because
	// it seems to be a factory pattern!?!

	// Maps for standard support.
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphMap;
	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeMap;
	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexMap;
	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceMap;
	protected HashMap<Class<? extends Record>, Constructor<? extends Record>> recordMap;

//	// Maps for database support.
//	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphDatabaseMap;
//	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeDatabaseMap;
//	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexDatabaseMap;
//	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceDatabaseMap;
//	protected HashMap<Class<? extends Record>, Constructor<? extends Record>> recordDatabaseMap;
//
//	// Maps for transaction support.
//	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphTransactionMap;
//	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeTransactionMap;
//	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexTransactionMap;
//	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceTransactionMap;
//	protected HashMap<Class<? extends Record>, Constructor<? extends Record>> recordTransactionMap;
//
//	// Maps for savemem support.
//	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphSavememMap;
//	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeSavememMap;
//	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexSavememMap;
//	protected HashMap<Class<? extends Incidence>, Constructor<? extends  Incidence>> incidenceSavememMap;
//	protected HashMap<Class<? extends Record>, Constructor<? extends Record>> recordSavememMap;

	/**
	 * Creates and initializes a new <code>GraphFactoryImpl</code>.
	 */
	protected GraphFactoryImpl() {
		this.createMapsForStandardSupport();
//		this.createMapsForDatabaseSupport();
//		this.createMapsForTransactionSupport();
//		this.createMapsForSaveMemSupport();
	}

	private void createMapsForStandardSupport() {
		graphMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		edgeMap = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
		vertexMap = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
		recordMap = new HashMap<Class<? extends Record>, Constructor<? extends Record>>();
	}

//	private void createMapsForDatabaseSupport() {
//		this.graphDatabaseMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
//		this.edgeDatabaseMap = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
//		this.vertexDatabaseMap = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
//		this.recordDatabaseMap = new HashMap<Class<? extends Record>, Constructor<? extends Record>>();
//	}
//
//	private void createMapsForTransactionSupport() {
//		graphTransactionMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
//		edgeTransactionMap = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
//		vertexTransactionMap = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
//		recordTransactionMap = new HashMap<Class<? extends Record>, Constructor<? extends Record>>();
//	}
//
//	private void createMapsForSaveMemSupport() {
//		graphSavememMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
//		edgeSavememMap = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
//		vertexSavememMap = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
//		recordSavememMap = new HashMap<Class<? extends Record>, Constructor<? extends Record>>();
//	}

	// --- Methods for option STDIMPL
	// ---------------------------------------------------

	public Edge createEdge(Class<? extends Edge> edgeClass, int id, Graph g,
			Vertex alpha, Vertex omega) {
		try {
		//	Edge e = binaryEdgeMap.get(edgeClass).newInstance(id, g, alpha, omega);
		//	return e;
			return null;
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
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}

	public Graph createGraph(Class<? extends Graph> graphClass, String id) {
		try {
			Graph g = graphMap.get(graphClass).newInstance(id, 1000, 1000);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}

	@Override
	public <T extends Incidence> T createIncidence(Class<T> incidenceClass,
			Vertex v, Edge e) {
		try {
			@SuppressWarnings("unchecked")
			T i = (T) incidenceMap.get(incidenceClass).newInstance(v, e);
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

	public void setEdgeImplementationClass(Class<? extends Edge> originalClass,
			Class<? extends Edge> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { int.class, Graph.class, Vertex.class,
						Vertex.class };
				edgeMap.put(originalClass,
						implementationClass.getConstructor(params));
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

//	// -------------------------------------------------------------------------
//	// Methods for the TRANSIMPL option.
//	// -------------------------------------------------------------------------
//
//	@Override
//	public Graph createGraphWithDatabaseSupport(
//			Class<? extends Graph> graphClass, GraphDatabase graphDatabase,
//			String id) {
//		try {
//			return graphDatabaseMap.get(graphClass).newInstance(id, 1000, 1000,
//					graphDatabase);
//		} catch (Exception exception) {
//			throw new M1ClassAccessException("Cannot create graph of class "
//					+ graphClass.getCanonicalName(), exception);
//		}
//	}
//
//	@Override
//	public Graph createGraphWithDatabaseSupport(
//			Class<? extends Graph> graphClass, GraphDatabase graphDatabase,
//			String id, int vMax, int eMax) {
//		try {
//			return graphDatabaseMap.get(graphClass).newInstance(id, vMax, eMax,
//					graphDatabase);
//		} catch (Exception exception) {
//			throw new M1ClassAccessException("Cannot create graph of class "
//					+ graphClass.getCanonicalName(), exception);
//		}
//	}
//
//	@Override
//	public Edge createEdgeWithDatabaseSupport(Class<? extends Edge> edgeClass,
//			int id, Graph graph, Vertex alpha, Vertex omega) {
//		try {
//			return edgeDatabaseMap.get(edgeClass).newInstance(id, graph, alpha,
//					omega);
//		} catch (Exception exception) {
//			if (exception.getCause() instanceof GraphException) {
//				throw new GraphException(exception.getCause()
//						.getLocalizedMessage());
//			} else {
//				throw new M1ClassAccessException("Cannot create edge of class "
//						+ edgeClass.getCanonicalName(), exception);
//			}
//		}
//	}
//
//	@Override
//	public Vertex createVertexWithDatabaseSupport(
//			Class<? extends Vertex> vertexClass, int id, Graph graph) {
//		try {
//			Constructor<? extends Vertex> constructor = vertexDatabaseMap
//					.get(vertexClass);
//			return constructor.newInstance(id, graph);
//		} catch (Exception exception) {
//			if (exception.getCause() instanceof GraphException) {
//				throw new GraphException(exception.getCause()
//						.getLocalizedMessage());
//			} else {
//				throw new M1ClassAccessException(
//						"Cannot create vertex of class "
//								+ vertexClass.getCanonicalName(), exception);
//			}
//		}
//	}
//
//	@Override
//	public void setGraphDatabaseImplementationClass(
//			Class<? extends Graph> originalClass,
//			Class<? extends Graph> implementationClass) {
//		if (isSuperclassOrEqual(originalClass, implementationClass)) {
//			try {
//				Class<?>[] params = { String.class, int.class, int.class,
//						GraphDatabase.class };
//				graphDatabaseMap.put(originalClass,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException exception) {
//				throw new M1ClassAccessException(
//						"Unable to locate default constructor for graphclass "
//								+ implementationClass.getName(), exception);
//			}
//		}
//	}
//
//	@Override
//	public void setVertexDatabaseImplementationClass(
//			Class<? extends Vertex> originalClass,
//			Class<? extends Vertex> implementationClass) {
//		if (isSuperclassOrEqual(originalClass, implementationClass)) {
//			try {
//				Class<?>[] params = { int.class, Graph.class };
//				vertexDatabaseMap.put(originalClass,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException exception) {
//				throw new M1ClassAccessException(
//						"Unable to locate default constructor for vertex class"
//								+ implementationClass, exception);
//			}
//		}
//	}
//
//	@Override
//	public void setEdgeDatabaseImplementationClass(
//			Class<? extends Edge> originalClass,
//			Class<? extends Edge> implementationClass) {
//		if (isSuperclassOrEqual(originalClass, implementationClass)) {
//			try {
//				Class<?>[] params = { int.class, Graph.class, Vertex.class,
//						Vertex.class };
//				edgeDatabaseMap.put(originalClass,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException exception) {
//				throw new M1ClassAccessException(
//						"Unable to locate default constructor for edge class"
//								+ implementationClass, exception);
//			}
//		}
//	}
//
//	public void setRecordDatabaseImplementationClass(
//			Class<? extends Record> m1Class,
//			Class<? extends Record> implementationClass) {
//		if (isSuperclassOrEqual(m1Class, implementationClass)) {
//			try {
//				Class<?>[] params = { Graph.class };
//				recordMap.put(m1Class,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException ex) {
//				throw new M1ClassAccessException(
//						"Unable to locate default constructor for record"
//								+ implementationClass, ex);
//			}
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public <T extends Record> T createRecordWithDatabaseSupport(
//			Class<T> recordDomain, Graph g) {
//		try {
//			T r = (T) recordMap.get(recordDomain).newInstance(g);
//			return r;
//		} catch (Exception ex) {
//			if (ex.getCause() instanceof GraphException) {
//				throw new GraphException(ex.getCause().getLocalizedMessage(),
//						ex);
//			}
//			throw new M1ClassAccessException("Cannot create record of class "
//					+ recordDomain.getCanonicalName(), ex);
//		}
//	}
//
//	// --- Methods for option TRANSIMPL
//	// -------------------------------------------------
//
//	public Edge createEdgeWithTransactionSupport(
//			Class<? extends Edge> edgeClass, int id, Graph g, Vertex alpha,
//			Vertex omega) {
//		try {
//			Edge e = edgeTransactionMap.get(edgeClass).newInstance(id, g,
//					alpha, omega);
//			e.initializeAttributesWithDefaultValues();
//			return e;
//		} catch (Exception ex) {
//			if (ex.getCause() instanceof GraphException) {
//				throw new GraphException(ex.getCause().getLocalizedMessage(),
//						ex);
//			}
//			throw new M1ClassAccessException("Cannot create edge of class "
//					+ edgeClass.getCanonicalName(), ex);
//		}
//	}
//
//	public Graph createGraphWithTransactionSupport(
//			Class<? extends Graph> graphClass, String id, int vMax, int eMax) {
//		try {
//			Graph g = graphTransactionMap.get(graphClass).newInstance(id, vMax,
//					eMax);
//			return g;
//		} catch (Exception ex) {
//			if (ex.getCause() instanceof GraphException) {
//				throw new GraphException(ex.getCause().getLocalizedMessage(),
//						ex);
//			}
//			throw new M1ClassAccessException("Cannot create graph of class "
//					+ graphClass.getCanonicalName(), ex);
//		}
//	}
//
//	public Graph createGraphWithTransactionSupport(
//			Class<? extends Graph> graphClass, String id) {
//		try {
//			Graph g = graphTransactionMap.get(graphClass).newInstance(id, 1000,
//					1000);
//			return g;
//		} catch (Exception ex) {
//			if (ex.getCause() instanceof GraphException) {
//				throw new GraphException(ex.getCause().getLocalizedMessage(),
//						ex);
//			}
//			throw new M1ClassAccessException("Cannot create graph of class "
//					+ graphClass.getCanonicalName(), ex);
//		}
//	}
//
//	public Vertex createVertexWithTransactionSupport(
//			Class<? extends Vertex> vertexClass, int id, Graph g) {
//		try {
//			Vertex v = vertexTransactionMap.get(vertexClass).newInstance(id, g);
//			return v;
//		} catch (Exception ex) {
//			if (ex.getCause() instanceof GraphException) {
//				throw new GraphException(ex.getCause().getLocalizedMessage(),
//						ex);
//			}
//			throw new M1ClassAccessException("Cannot create vertex of class "
//					+ vertexClass.getCanonicalName(), ex);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public <T extends Record> T createRecordWithTransactionSupport(
//			Class<T> recordDomain, Graph g) {
//		try {
//			T r = (T) recordTransactionMap.get(recordDomain).newInstance(g);
//			return r;
//		} catch (Exception ex) {
//			if (ex.getCause() instanceof GraphException) {
//				throw new GraphException(ex.getCause().getLocalizedMessage(),
//						ex);
//			}
//			throw new M1ClassAccessException("Cannot create vertex of class "
//					+ recordDomain.getCanonicalName(), ex);
//		}
//	}
//
//	public void setGraphTransactionImplementationClass(
//			Class<? extends Graph> originalClass,
//			Class<? extends Graph> implementationClass) {
//		if (isSuperclassOrEqual(originalClass, implementationClass)) {
//			try {
//				Class<?>[] params = { String.class, int.class, int.class };
//				graphTransactionMap.put(originalClass,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException ex) {
//				throw new M1ClassAccessException(
//						"Unable to locate transaction constructor for graphclass "
//								+ implementationClass.getName(), ex);
//			}
//		}
//	}
//
//	public void setVertexTransactionImplementationClass(
//			Class<? extends Vertex> originalClass,
//			Class<? extends Vertex> implementationClass) {
//		if (isSuperclassOrEqual(originalClass, implementationClass)) {
//			try {
//				Class<?>[] params = { int.class, Graph.class };
//				vertexTransactionMap.put(originalClass,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException ex) {
//				throw new M1ClassAccessException(
//						"Unable to locate transaction constructor for vertexclass"
//								+ implementationClass, ex);
//			}
//		}
//	}
//
//	public void setEdgeTransactionImplementationClass(
//			Class<? extends Edge> originalClass,
//			Class<? extends Edge> implementationClass) {
//		if (isSuperclassOrEqual(originalClass, implementationClass)) {
//			try {
//				Class<?>[] params = { int.class, Graph.class, Vertex.class,
//						Vertex.class };
//				edgeTransactionMap.put(originalClass,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException ex) {
//				throw new M1ClassAccessException(
//						"Unable to locate transaction constructor for edgeclass"
//								+ implementationClass, ex);
//			}
//		}
//	}
//
//	public void setRecordTransactionImplementationClass(
//			Class<? extends Record> m1Class,
//			Class<? extends Record> implementationClass) {
//		if (isSuperclassOrEqual(m1Class, implementationClass)) {
//			try {
//				Class<?>[] params = { Graph.class };
//				recordTransactionMap.put(m1Class,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException ex) {
//				throw new M1ClassAccessException(
//						"Unable to locate default constructor for record"
//								+ implementationClass, ex);
//			}
//		}
//	}
//
//	// -------------------------------------------------------------------------
//	// Methods for the SAVEMEMIMPL option.
//	// FIXME This is currently a clone STDIMPL methods with changed maps.
//
//	public Edge createEdgeWithSavememSupport(Class<? extends Edge> edgeClass,
//			int id, Graph g, Vertex alpha, Vertex omega) {
//		try {
//			return edgeSavememMap.get(edgeClass).newInstance(id, g, alpha,
//					omega);
//		} catch (Exception ex) {
//			if (ex.getCause() instanceof GraphException) {
//				throw new GraphException(ex.getCause().getLocalizedMessage(),
//						ex);
//			} else {
//				throw new M1ClassAccessException("Cannot create edge of class "
//						+ edgeClass.getCanonicalName(), ex);
//			}
//		}
//	}
//
//	public Graph createGraphWithSavememSupport(
//			Class<? extends Graph> graphClass, String id, int vMax, int eMax) {
//		try {
//			Graph g = graphSavememMap.get(graphClass).newInstance(id, vMax,
//					eMax);
//			return g;
//		} catch (Exception ex) {
//			throw new M1ClassAccessException("Cannot create graph of class "
//					+ graphClass.getCanonicalName(), ex);
//		}
//	}
//
//	public Graph createGraphWithSavememSupport(
//			Class<? extends Graph> graphClass, String id) {
//		try {
//			Graph g = graphSavememMap.get(graphClass).newInstance(id, 1000,
//					1000);
//			return g;
//		} catch (Exception ex) {
//			throw new M1ClassAccessException("Cannot create graph of class "
//					+ graphClass.getCanonicalName(), ex);
//		}
//	}
//
//	public Vertex createVertexWithSavememSupport(
//			Class<? extends Vertex> vertexClass, int id, Graph g) {
//		try {
//			Vertex v = vertexSavememMap.get(vertexClass).newInstance(id, g);
//			return v;
//		} catch (Exception ex) {
//			if (ex.getCause() instanceof GraphException) {
//				throw new GraphException(ex.getCause().getLocalizedMessage(),
//						ex);
//			}
//			throw new M1ClassAccessException("Cannot create vertex of class "
//					+ vertexClass.getCanonicalName(), ex);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public <T extends Record> T createRecordWithSavememSupport(
//			Class<T> recordDomain, Graph g) {
//		try {
//			T r = (T) recordSavememMap.get(recordDomain).newInstance(g);
//			return r;
//		} catch (Exception ex) {
//			if (ex.getCause() instanceof GraphException) {
//				throw new GraphException(ex.getCause().getLocalizedMessage(),
//						ex);
//			}
//			throw new M1ClassAccessException("Cannot create vertex of class "
//					+ recordDomain.getCanonicalName(), ex);
//		}
//	}
//
//	public void setGraphSavememImplementationClass(
//			Class<? extends Graph> originalClass,
//			Class<? extends Graph> implementationClass) {
//		if (isSuperclassOrEqual(originalClass, implementationClass)) {
//			try {
//				Class<?>[] params = { String.class, int.class, int.class };
//				graphSavememMap.put(originalClass,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException ex) {
//				throw new M1ClassAccessException(
//						"Unable to locate default constructor for graphclass "
//								+ implementationClass.getName(), ex);
//			}
//		}
//	}
//
//	public void setVertexSavememImplementationClass(
//			Class<? extends Vertex> originalClass,
//			Class<? extends Vertex> implementationClass) {
//		if (isSuperclassOrEqual(originalClass, implementationClass)) {
//			try {
//				Class<?>[] params = { int.class, Graph.class };
//				vertexSavememMap.put(originalClass,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException ex) {
//				throw new M1ClassAccessException(
//						"Unable to locate default constructor for vertexclass"
//								+ implementationClass, ex);
//			}
//		}
//	}
//
//	public void setEdgeSavememImplementationClass(
//			Class<? extends Edge> originalClass,
//			Class<? extends Edge> implementationClass) {
//		if (isSuperclassOrEqual(originalClass, implementationClass)) {
//			try {
//				Class<?>[] params = { int.class, Graph.class, Vertex.class,
//						Vertex.class };
//				edgeSavememMap.put(originalClass,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException ex) {
//				throw new M1ClassAccessException(
//						"Unable to locate default constructor for edgeclass"
//								+ implementationClass, ex);
//			}
//		}
//	}
//
//	public void setRecordSavememImplementationClass(
//			Class<? extends Record> m1Class,
//			Class<? extends Record> implementationClass) {
//		if (isSuperclassOrEqual(m1Class, implementationClass)) {
//			try {
//				Class<?>[] params = { Graph.class };
//				recordSavememMap.put(m1Class,
//						implementationClass.getConstructor(params));
//			} catch (NoSuchMethodException ex) {
//				throw new M1ClassAccessException(
//						"Unable to locate default constructor for record"
//								+ implementationClass, ex);
//			}
//		}
//	}

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

}
