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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.disk.EdgeContainer;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabase;
import de.uni_koblenz.jgralab.impl.disk.IncidenceContainer;
import de.uni_koblenz.jgralab.impl.disk.VertexContainer;
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

	/* maps for in-memory storage */
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphMapForMemBasedImpl;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> viewGraphMapForMemBasedImpl;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForEdgeMapForMemBasedImpl;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForVertexMapForMemBasedImpl;
	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeMapForMemBasedImpl;
	protected HashMap<Class<? extends BinaryEdge>, Constructor<? extends BinaryEdge>> binaryEdgeMapForMemBasedImpl;
	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexMapForMemBasedImpl;
	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceMapForMemBasedImpl;

	/* maps for disk-based storage */
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphMapForDiskBasedImpl;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphProxyMapForDiskBasedImpl;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> viewGraphMapForDiskBasedImpl;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForEdgeMapForDiskBasedImpl;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForVertexMapForDiskBasedImpl;
	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeMapForDiskBasedImpl;
	protected HashMap<Class<? extends BinaryEdge>, Constructor<? extends BinaryEdge>> binaryEdgeMapForDiskBasedImpl;
	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexMapForDiskBasedImpl;
	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceMapForDiskBasedImpl;

	/* maps for proxy elements */
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphMapForProxies;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> viewGraphMapForProxies;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForEdgeMapForProxies;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForVertexMapForProxies;
	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeMapForProxies;
	protected HashMap<Class<? extends BinaryEdge>, Constructor<? extends BinaryEdge>> binaryEdgeMapForProxies;
	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexMapForProxies;
	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceMapForProxies;

	protected HashMap<Class<? extends Record>, Constructor<? extends Record>> recordMap;

	/*
	 * maps elements to their constructors needed to reload the element from the
	 * disk
	 */
	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeMapForDiskStorageReloading;
	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexMapForDiskStorageReloading;
	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceMapForDiskStorageReloading;

	/**
	 * Creates and initializes a new <code>GraphFactoryImpl</code>.
	 */
	protected GraphFactoryImpl() {
		this.createMapsForStandardSupport();
	}

	private void createMapsForStandardSupport() {
		graphMapForMemBasedImpl = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		viewGraphMapForMemBasedImpl = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForVertexMapForMemBasedImpl = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForEdgeMapForMemBasedImpl = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		edgeMapForMemBasedImpl = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
		binaryEdgeMapForMemBasedImpl = new HashMap<Class<? extends BinaryEdge>, Constructor<? extends BinaryEdge>>();
		vertexMapForMemBasedImpl = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
		incidenceMapForMemBasedImpl = new HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>>();

		graphMapForDiskBasedImpl = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		graphProxyMapForDiskBasedImpl = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		viewGraphMapForDiskBasedImpl = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForVertexMapForDiskBasedImpl = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForEdgeMapForDiskBasedImpl = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		edgeMapForDiskBasedImpl = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
		binaryEdgeMapForDiskBasedImpl = new HashMap<Class<? extends BinaryEdge>, Constructor<? extends BinaryEdge>>();
		vertexMapForDiskBasedImpl = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
		incidenceMapForDiskBasedImpl = new HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>>();

		graphMapForProxies = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		viewGraphMapForProxies = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForVertexMapForProxies = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForEdgeMapForProxies = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
//		edgeMapForProxies = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
//		binaryEdgeMapForProxies = new HashMap<Class<? extends BinaryEdge>, Constructor<? extends BinaryEdge>>();
//		vertexMapForProxies = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
//		incidenceMapForProxies = new HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>>();

		edgeMapForDiskStorageReloading = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
		vertexMapForDiskStorageReloading = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
		incidenceMapForDiskStorageReloading = new HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>>();
		recordMap = new HashMap<Class<? extends Record>, Constructor<? extends Record>>();
	}

	@Override
	public Edge createEdge(Class<? extends Edge> edgeClass, int id, Graph g,
			Vertex alpha, Vertex omega) {
		try {
			Edge e = binaryEdgeMapForMemBasedImpl.get(edgeClass).newInstance(
					id, g, alpha, omega);
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
	public Edge createEdgeDiskBasedStorage(Class<? extends Edge> edgeClass,
			int id, Graph g, Vertex alpha, Vertex omega) {
		try {
			Edge e = binaryEdgeMapForDiskBasedImpl.get(edgeClass).newInstance(
					id, g, alpha, omega);
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
	public Edge reloadEdge(Class<? extends Edge> edgeClass, int id, Graph g,
			EdgeContainer container) {
		try {
			Edge e = edgeMapForDiskStorageReloading.get(edgeClass).newInstance(
					id, container, g);
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

		// CompleteGraphImpl cg = (CompleteGraphImpl) g;
		// cg.backgroundStorage.freeMem();
		try {
			Edge e = edgeMapForMemBasedImpl.get(edgeClass).newInstance(id, g);
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
	public Edge createEdgeProxy(Class<? extends Edge> edgeClass, int id, Graph g) {

		// CompleteGraphImpl cg = (CompleteGraphImpl) g;
		// cg.backgroundStorage.freeMem();
		try {
			Edge e = edgeMapForMemBasedImpl.get(edgeClass).newInstance(id, g);
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
	public Edge createEdgeDiskBasedStorage(Class<? extends Edge> edgeClass,
			int id, Graph g) {

		// CompleteGraphImpl cg = (CompleteGraphImpl) g;
		// cg.backgroundStorage.freeMem();
		try {
			Edge e = edgeMapForDiskBasedImpl.get(edgeClass).newInstance(id, g);
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
			Graph g = graphMapForMemBasedImpl.get(graphClass).newInstance(id,
					vMax, eMax);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}

	public Graph createGraphDiskBasedStorage(Class<? extends Graph> graphClass,
			String id, int vMax, int eMax) {
		try {
			Graph g = graphMapForDiskBasedImpl.get(graphClass).newInstance(id,
					vMax, eMax);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}

	public Graph createGraph(Class<? extends Graph> graphClass, String id) {
		try {
			Graph g = graphMapForMemBasedImpl.get(graphClass).newInstance(id,
					1000, 1000);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}

	public Graph createGraphDiskBasedStorage(Class<? extends Graph> graphClass,
			String id) {
		try {
			Graph g = graphMapForDiskBasedImpl.get(graphClass).newInstance(id,
					1000, 1000);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}
	
	public Graph createGraphProxy(Class<? extends Graph> graphClass, String uid, int partialGraphId, GraphDatabase database) {
		try {
			Graph g = graphProxyMapForDiskBasedImpl.get(graphClass).newInstance(uid, partialGraphId, database);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException("Cannot create graph proxy of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}

	@Override
	public <T extends Incidence> T createIncidence(Class<T> incidenceClass,
			int id, Vertex v, Edge e) {
		try {
			@SuppressWarnings("unchecked")
			T i = (T) incidenceMapForMemBasedImpl.get(incidenceClass)
					.newInstance(id, v, e);
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

	@Override
	public <T extends Incidence> T createIncidenceDiskBasedStorage(
			Class<T> incidenceClass, int id, Vertex v, Edge e) {
		try {
			@SuppressWarnings("unchecked")
			T i = (T) incidenceMapForDiskBasedImpl.get(incidenceClass)
					.newInstance(id, v, e);
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

	@Override
	public Incidence reloadIncidence(Class<? extends Incidence> incidenceClass,
			int id, IncidenceContainer container) {
		try {
			Incidence i = incidenceMapForDiskStorageReloading.get(
					incidenceClass).newInstance(id, container);
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
		// CompleteGraphImpl cg = (CompleteGraphImpl) g;
		// cg.backgroundStorage.freeMem();
		try {
			Vertex v = vertexMapForMemBasedImpl.get(vertexClass).newInstance(
					id, g);
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

	public Vertex createVertexDiskBasedStorage(
			Class<? extends Vertex> vertexClass, int id, Graph g) {
		// CompleteGraphImpl cg = (CompleteGraphImpl) g;
		// cg.backgroundStorage.freeMem();
		try {
			Vertex v = vertexMapForDiskBasedImpl.get(vertexClass).newInstance(
					id, g);
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

	@Override
	public Vertex reloadVertex(Class<? extends Vertex> vertexClass, int id,
			Graph g, VertexContainer container) {
		try {
			Vertex v = vertexMapForDiskStorageReloading.get(vertexClass)
					.newInstance(id, container, g);
			return v;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new M1ClassAccessException("Cannot reload vertex of class "
					+ vertexClass.getCanonicalName(), ex);
		}
	}

	@Override
	public void setGraphImplementationClass(
			Class<? extends Graph> originalClass,
			Class<? extends Graph> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { String.class, int.class, int.class };
				graphMapForMemBasedImpl.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	@Override
	public void setGraphImplementationClassForDiskBasedStorage(
			Class<? extends Graph> originalClass,
			Class<? extends Graph> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { String.class, int.class, int.class };
				graphMapForMemBasedImpl.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	@Override
	public void setVertexImplementationClass(
			Class<? extends Vertex> originalClass,
			Class<? extends Vertex> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { int.class, Graph.class };
				vertexMapForMemBasedImpl.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for vertexclass"
								+ implementationClass, ex);
			}
		}
	}

	@Override
	public void setVertexImplementationClassForDiskBasedStorage(
			Class<? extends Vertex> originalClass,
			Class<? extends Vertex> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { int.class, Graph.class };
				vertexMapForDiskBasedImpl.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramsDisk = { int.class, VertexContainer.class,
						Graph.class };
				vertexMapForDiskStorageReloading.put(originalClass,
						implementationClass.getConstructor(paramsDisk));
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
				incidenceMapForMemBasedImpl.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for incidenceclass"
								+ implementationClass, ex);
			}
		}
	}

	public void setIncidenceImplementationClassForDiskBasedStorage(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { int.class, Vertex.class, Edge.class };
				incidenceMapForDiskBasedImpl.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramsDisk = { int.class, IncidenceContainer.class };
				incidenceMapForDiskStorageReloading.put(originalClass,
						implementationClass.getConstructor(paramsDisk));
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
				edgeMapForMemBasedImpl.put(originalClass,
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
					binaryEdgeMapForMemBasedImpl.put(
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

	@SuppressWarnings("unchecked")
	public void setEdgeImplementationClassForDiskBasedStorage(
			Class<? extends Edge> originalClass,
			Class<? extends Edge> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { int.class, Graph.class };
				edgeMapForDiskBasedImpl.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramsDisk = { int.class, EdgeContainer.class,
						Graph.class };
				edgeMapForDiskStorageReloading.put(originalClass,
						implementationClass.getConstructor(paramsDisk));

				List<Class<?>> interfaces = new ArrayList<Class<?>>();
				for (Class<?> c : originalClass.getInterfaces()) {
					interfaces.add(c);
				}
				if (interfaces.contains(BinaryEdge.class)) {
					Class<?>[] binaryParams = { int.class, Graph.class,
							Vertex.class, Vertex.class };
					Constructor<BinaryEdge> binaryConstructor = (Constructor<BinaryEdge>) implementationClass
							.getConstructor(binaryParams);
					binaryEdgeMapForMemBasedImpl.put(
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
	public de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl createViewGraph(
			Graph viewGraph, int level)  {
		try {
			Class<? extends Graph> graphClass = viewGraph.getM1Class();
			de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl g = (de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl) viewGraphMapForMemBasedImpl
					.get(graphClass).newInstance(viewGraph, level);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException(
					"Cannot create view graph for graph of class "
							+ viewGraph.getGraphClass().getQualifiedName(), ex);
		}
	}

	@Override
	public de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl createViewGraphDiskBasedStorage(
			Graph viewGraph, int level)  {
		try {
			Class<? extends Graph> graphClass = viewGraph.getM1Class();
			de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl g = (de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl) viewGraphMapForMemBasedImpl
					.get(graphClass).newInstance(viewGraph, level);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException(
					"Cannot create view graph for graph of class "
							+ viewGraph.getGraphClass().getQualifiedName(), ex);
		}
	}

	@Override
	public de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl createSubordinateGraph(
			Vertex vertex)  {
		try {
			Class<? extends Graph> graphClass = vertex.getGraph().getM1Class();
			de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl g = (de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl) subordinateGraphForVertexMapForMemBasedImpl
					.get(graphClass).newInstance(vertex);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException(
					"Cannot create subordinate graph for elem of class "
							+ vertex.getType().getQualifiedName(), ex);
		}
	}

	@Override
	public de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl createSubordinateGraphDiskBasedStorage(
			Vertex vertex)  {
		try {
			Class<? extends Graph> graphClass = vertex.getGraph().getM1Class();
			de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl g = (de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl) subordinateGraphForVertexMapForMemBasedImpl
					.get(graphClass).newInstance(vertex);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException(
					"Cannot create subordinate graph for elem of class "
							+ vertex.getType().getQualifiedName(), ex);
		}
	}

	@Override
	public de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl createSubordinateGraph(
			Edge vertex)  {
		try {
			Class<? extends Graph> graphClass = vertex.getGraph().getM1Class();
			de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl g = (de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl) subordinateGraphForEdgeMapForMemBasedImpl
					.get(graphClass).newInstance(vertex);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException(
					"Cannot create subordinate graph for elem of class "
							+ vertex.getType().getQualifiedName(), ex);
		}
	}

	@Override
	public de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl createSubordinateGraphDiskBasedStorage(
			Edge vertex)  {
		try {
			Class<? extends Graph> graphClass = vertex.getGraph().getM1Class();
			de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl g = (de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl) subordinateGraphForEdgeMapForMemBasedImpl
					.get(graphClass).newInstance(vertex);
			return g;
		} catch (Exception ex) {
			throw new M1ClassAccessException(
					"Cannot create subordinate graph for elem of class "
							+ vertex.getType().getQualifiedName(), ex);
		}
	}



	@Override
	public void setSubordinateGraphImplementationClass(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { Vertex.class };
				subordinateGraphForVertexMapForMemBasedImpl.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramse = { Edge.class };
				subordinateGraphForEdgeMapForMemBasedImpl.put(originalClass,
						implementationClass.getConstructor(paramse));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	@Override
	public void setSubordinateGraphImplementationClassForDiskBasedStorage(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { Vertex.class };
				subordinateGraphForVertexMapForDiskBasedImpl.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramse = { Edge.class };
				subordinateGraphForEdgeMapForDiskBasedImpl.put(originalClass,
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
			Class<? extends de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { originalClass, int.class };
				viewGraphMapForMemBasedImpl.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	@Override
	public void setViewGraphImplementationClassForDiskBasedStorage(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { originalClass, int.class };
				viewGraphMapForMemBasedImpl.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new M1ClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}
	

}
