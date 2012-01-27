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
import java.util.Random;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.disk.EdgeContainer;
import de.uni_koblenz.jgralab.impl.disk.IncidenceContainer;
import de.uni_koblenz.jgralab.impl.disk.VertexContainer;
import de.uni_koblenz.jgralab.schema.exception.SchemaClassAccessException;

/**
 * Default implementation for GraphFactory. Per default every create-method
 * creates an instance of exactly the specified class. To change this use
 * <code>setImplementationClass</code>-methods. Class is abstract because only
 * factories which are specific for their schema should be used.
 * 
 * @author ist@uni-koblenz.de
 */
public class GraphFactoryImpl implements GraphFactory {

	/* maps for in-memory storage */
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphMap_MemBasedStorage;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> viewGraphMap_MemBasedStorage;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForEdgeMap_MemBasedStorage;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForVertexMap_MemBasedStorage;
	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeMap_MemBasedStorage;
	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexMap_MemBasedStorage;
	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceMap_MemBasedStorage;

	/* maps for disk-based storage */
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphMap_DiskBasedStorage;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphProxyMap_DiskBasedStorage;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> viewGraphMap_DiskBasedStorage;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForEdgeMap_DiskBasedStorage;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForVertexMap_DiskBasedStorage;
	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeMap_DiskBasedStorage;
	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexMap_DiskBasedStorage;
	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceMap_DiskBasedStorage;
	
	/* maps for distributed storage */
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphMap_DistributedStorage;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphProxyMap_DistributedStorage;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> viewGraphMap_DistributedStorage;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForEdgeMap_DistributedStorage;
	protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> subordinateGraphForVertexMap_DistributedStorage;
	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeMap_DistributedStorage;
	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexMap_DistributedStorage;
	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceMap_DistributedStorage;

	/* maps for proxy elements */
	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeProxyMap_DistributedStorage;
	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexProxyMap_DistributedStorage;
	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceProxyMap_DistributedStorage;
	
	protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeProxyMap_DiskBasedStorage;
	protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexProxyMap_DiskBasedStorage;
	protected HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>> incidenceProxyMap_DiskBasedStorage;

	protected HashMap<Class<? extends Record>, Constructor<? extends Record>> recordMap_InMemoryStorage;
	protected HashMap<Class<? extends Record>, Constructor<? extends Record>> recordMap_DistributedStorage;
	protected HashMap<Class<? extends Record>, Constructor<? extends Record>> recordMap_DiskBasedStorage;

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

	public static String generateUniqueGraphId() {
		long uidPart = System.currentTimeMillis();
		Random r = new Random();
		return uidPart + "-" + r.nextLong() + "-" + r.nextLong() + "-"
				+ r.nextLong();
	}

	private void createMapsForStandardSupport() {
		graphMap_MemBasedStorage = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		viewGraphMap_MemBasedStorage = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForVertexMap_MemBasedStorage = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForEdgeMap_MemBasedStorage = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		edgeMap_MemBasedStorage = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
		vertexMap_MemBasedStorage = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
		incidenceMap_MemBasedStorage = new HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>>();

		graphMap_DiskBasedStorage = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		graphProxyMap_DiskBasedStorage = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		viewGraphMap_DiskBasedStorage = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForVertexMap_DiskBasedStorage = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		subordinateGraphForEdgeMap_DiskBasedStorage = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
		edgeMap_DiskBasedStorage = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
		vertexMap_DiskBasedStorage = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
		incidenceMap_DiskBasedStorage = new HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>>();

		edgeProxyMap_DistributedStorage = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
		vertexProxyMap_DistributedStorage = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
		incidenceProxyMap_DistributedStorage = new HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>>();
		
		edgeProxyMap_DiskBasedStorage = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
		vertexProxyMap_DiskBasedStorage = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
		incidenceProxyMap_DiskBasedStorage = new HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>>();

		edgeMapForDiskStorageReloading = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
		vertexMapForDiskStorageReloading = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
		incidenceMapForDiskStorageReloading = new HashMap<Class<? extends Incidence>, Constructor<? extends Incidence>>();
		recordMap_InMemoryStorage = new HashMap<Class<? extends Record>, Constructor<? extends Record>>();
		recordMap_DistributedStorage= new HashMap<Class<? extends Record>, Constructor<? extends Record>>();
		recordMap_DiskBasedStorage = new HashMap<Class<? extends Record>, Constructor<? extends Record>>();
	}

	@Override
	public Graph createGraph_InMemoryStorage(Class<? extends Graph> graphClass,
			String id, int vMax, int eMax) {
		try {
			Graph g = graphMap_MemBasedStorage.get(graphClass).newInstance(id,
					vMax, eMax);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}

	public Graph createGraph_InMemoryStorage(Class<? extends Graph> graphClass,
			String id) {
		try {
			Graph g = graphMap_MemBasedStorage.get(graphClass).newInstance(id,
					1000, 1000);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}
	
	
	@Override
	public Graph createGraph_DistributedStorage(Class<? extends Graph> graphClass,
			String uniqueGraphId, long subgraphId,
			de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl graphDatabase,
			RemoteGraphDatabaseAccess storingGraphDatabase) {
		try {
			Graph g = graphMap_DiskBasedStorage.get(graphClass).newInstance(
					uniqueGraphId, subgraphId, graphDatabase,
					storingGraphDatabase);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}


	@Override
	public Graph createGraph_DiskBasedStorage(Class<? extends Graph> graphClass,
			String uniqueGraphId, long subgraphId,
			de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase,
			RemoteGraphDatabaseAccess storingGraphDatabase) {
		try {
			Graph g = graphMap_DiskBasedStorage.get(graphClass).newInstance(
					uniqueGraphId, subgraphId, graphDatabase,
					storingGraphDatabase);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException("Cannot create graph of class "
					+ graphClass.getCanonicalName(), ex);
		}
	}


	@Override
	public Edge createEdge_InMemoryStorage(Class<? extends Edge> edgeClass, int id, Graph g) {
		try {
			Edge e = edgeMap_MemBasedStorage.get(edgeClass).newInstance(id, g);
			return e;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create edge of class "
					+ edgeClass.getCanonicalName(), ex);
		}
	}

	@Override
	public Edge createEdge_DistributedStorage(Class<? extends Edge> edgeClass,
			long id, de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl graphDatabase) {
		try {
			Edge e = edgeMap_DistributedStorage.get(edgeClass).newInstance(id,
					graphDatabase);
			return e;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create edge of class "
					+ edgeClass.getCanonicalName(), ex);
		}
	}
	
	@Override
	public Edge createEdge_DiskBasedStorage(Class<? extends Edge> edgeClass,
			long id, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase) {
		try {
			Edge e = edgeMap_DiskBasedStorage.get(edgeClass).newInstance(id,
					graphDatabase);
			return e;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create edge of class "
					+ edgeClass.getCanonicalName(), ex);
		}
	}

	@Override
	public Edge reloadLocalEdge(Class<? extends Edge> edgeClass, long id,
			de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, EdgeContainer container) {
		try {
			Edge e = edgeMapForDiskStorageReloading.get(edgeClass).newInstance(
					id, graphDatabase, container);
			return e;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create edge of class "
					+ edgeClass.getCanonicalName(), ex);
		}
	}
	
	@Override
	public Edge createEdgeProxy_DistributedStorage(Class<? extends Edge> edgeClass, long id,
			de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl graphDatabase,
			RemoteGraphDatabaseAccess remoteDatabase) {
		try {
			Edge e = edgeProxyMap_DistributedStorage.get(edgeClass).newInstance(id,
					graphDatabase, remoteDatabase);
			return e;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create edge of class "
					+ edgeClass.getCanonicalName(), ex);
		}
	}

	@Override
	public Edge createEdgeProxy_DiskBasedStorage(Class<? extends Edge> edgeClass, long id,
			de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase,
			RemoteGraphDatabaseAccess remoteDatabase) {
		try {
			Edge e = edgeProxyMap_DiskBasedStorage.get(edgeClass).newInstance(id,
					graphDatabase, remoteDatabase);
			return e;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create edge of class "
					+ edgeClass.getCanonicalName(), ex);
		}
	}

	@Override
	public <T extends Incidence> T createIncidence_InMemoryStorage(Class<T> incidenceClass,
			int id, Vertex v, Edge e) {
		try {
			@SuppressWarnings("unchecked")
			T i = (T) incidenceMap_MemBasedStorage.get(incidenceClass)
					.newInstance(id, v, e);
			return i;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException(
					"Cannot create incidence of class "
							+ incidenceClass.getCanonicalName(), ex);
		}
	}

	@Override
	public <T extends Incidence> T createIncidence_DistributedStorage(
			Class<? extends T> incidenceClass, long incidenceId, long vertexId,
			long edgeId, de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl graphDatabase) {
		try {
			@SuppressWarnings("unchecked")
			T i = (T) incidenceMap_DistributedStorage.get(incidenceClass)
					.newInstance(incidenceId, graphDatabase, vertexId, edgeId);
			return i;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException(
					"Cannot create incidence of class "
							+ incidenceClass.getCanonicalName(), ex);
		}
	}
	
	@Override
	public <T extends Incidence> T createIncidence_DiskBasedStorage(
			Class<? extends T> incidenceClass, long incidenceId, long vertexId,
			long edgeId, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase) {
		try {
			@SuppressWarnings("unchecked")
			T i = (T) incidenceMap_DiskBasedStorage.get(incidenceClass)
					.newInstance(incidenceId, graphDatabase, vertexId, edgeId);
			return i;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException(
					"Cannot create incidence of class "
							+ incidenceClass.getCanonicalName(), ex);
		}
	}


	@Override
	public <T extends Incidence> T createIncidenceProxy_DistributedStorage(
			Class<? extends T> incidenceClass, long id,
			de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl graphDatabase,
			RemoteGraphDatabaseAccess remoteDatabase) {
		try {
			@SuppressWarnings("unchecked")
			T i = (T) incidenceProxyMap_DistributedStorage.get(incidenceClass).newInstance(
					id, graphDatabase, remoteDatabase);
			return i;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException(
					"Cannot create incidence proxy of class "
							+ incidenceClass.getCanonicalName(), ex);
		}
	}
	

	@Override
	public <T extends Incidence> T createIncidenceProxy_DiskBasedStorage(
			Class<? extends T> incidenceClass, long id,
			de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase,
			RemoteGraphDatabaseAccess remoteDatabase) {
		try {
			@SuppressWarnings("unchecked")
			T i = (T) incidenceProxyMap_DiskBasedStorage.get(incidenceClass).newInstance(
					id, graphDatabase, remoteDatabase);
			return i;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException(
					"Cannot create incidence proxy of class "
							+ incidenceClass.getCanonicalName(), ex);
		}
	}
	
	@Override
	public <T extends Incidence> T reloadLocalIncidence(
			Class<? extends T> incidenceClass, long id,
			de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, IncidenceContainer container) {
		try {
			@SuppressWarnings("unchecked")
			T i = (T) incidenceMapForDiskStorageReloading.get(incidenceClass)
					.newInstance(id, graphDatabase, container);
			return i;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException(
					"Cannot create incidence of class "
							+ incidenceClass.getCanonicalName(), ex);
		}
	}
	

	@Override
	public Vertex createVertex_InMemoryStorage(Class<? extends Vertex> vertexClass, int id,
			Graph g) {
		try {
			Vertex v = vertexMap_MemBasedStorage.get(vertexClass).newInstance(
					id, g);
			return v;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create vertex of class "
					+ vertexClass.getCanonicalName(), ex);
		}
	}

	@Override
	public Vertex createVertex_DistributedStorage(
			Class<? extends Vertex> vertexClass, long id,
			de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl localGraphDatabase) {
		try {
			Vertex v = vertexMap_DistributedStorage.get(vertexClass).newInstance(
					id, localGraphDatabase);
			return v;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create vertex of class "
					+ vertexClass.getCanonicalName(), ex);
		}
	}
	
	@Override
	public Vertex createVertex_DiskBasedStorage(
			Class<? extends Vertex> vertexClass, long id,
			de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl localGraphDatabase) {
		try {
			Vertex v = vertexMap_DiskBasedStorage.get(vertexClass).newInstance(
					id, localGraphDatabase);
			return v;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create vertex of class "
					+ vertexClass.getCanonicalName(), ex);
		}
	}
	
	@Override
	public Vertex createVertexProxy_DistributedStorage(Class<? extends Vertex> vertexClass,
			long id, de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl graphDatabase,
			RemoteGraphDatabaseAccess storingGraphDatabase) {
		try {
			Vertex v = vertexProxyMap_DistributedStorage.get(vertexClass).newInstance(id,
					graphDatabase, storingGraphDatabase);
			return v;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create vertex of class "
					+ vertexClass.getCanonicalName(), ex);
		}
	}
	

	@Override
	public Vertex createVertexProxy_DiskBasedStorage(Class<? extends Vertex> vertexClass,
			long id, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase,
			RemoteGraphDatabaseAccess storingGraphDatabase) {
		try {
			Vertex v = vertexProxyMap_DiskBasedStorage.get(vertexClass).newInstance(id,
					graphDatabase, storingGraphDatabase);
			return v;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create vertex of class "
					+ vertexClass.getCanonicalName(), ex);
		}
	}

	@Override
	public Vertex reloadLocalVertex(Class<? extends Vertex> vertexClass,
			long id, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase,
			VertexContainer container) {
		try {
			Vertex v = vertexMapForDiskStorageReloading.get(vertexClass)
					.newInstance(id, graphDatabase, container);
			return v;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot reload vertex of class "
					+ vertexClass.getCanonicalName(), ex);
		}
	}

	@Override
	public void setGraphImplementationClass_InMemoryStorage(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.mem.GraphBaseImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { String.class, int.class, int.class };
				graphMap_MemBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}
	
	@Override
	public void setGraphImplementationClass_DistributedStorage(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.distributed.GraphBaseImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { String.class, long.class,
						de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				graphMap_DistributedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	@Override
	public void setGraphImplementationClass_DiskBasedStorage(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.disk.GraphBaseImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { String.class, long.class,
						de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				graphMap_DiskBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}


	@Override
	public void setEdgeImplementationClass_InMemoryStorage(
			Class<? extends Edge> originalClass,
			Class<? extends Edge> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, Graph.class };
				edgeMap_MemBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for edgeclass"
								+ implementationClass, ex);
			}
		}
	}

	@Override
	public void setEdgeImplementationClass_DiskBasedStorage(
			Class<? extends Edge> originalClass,
			Class<? extends Edge> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class };
				edgeMap_DiskBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramsDisk = { long.class,
						de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class, EdgeContainer.class };
				edgeMapForDiskStorageReloading.put(originalClass,
						implementationClass.getConstructor(paramsDisk));

			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for edgeclass"
								+ implementationClass, ex);
			}
		}
	}
	
	@Override
	public void setEdgeImplementationClass_DistributedStorage(
			Class<? extends Edge> originalClass,
			Class<? extends Edge> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl.class };
				edgeMap_DistributedStorage.put(originalClass,
						implementationClass.getConstructor(params));

			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for edgeclass"
								+ implementationClass, ex);
			}
		}
	}

	@Override
	public void setEdgeProxyImplementationClass_DiskBasedStorage(
			Class<? extends Edge> edgeM1Class,
			Class<? extends Edge> implementationClass) {
		if (isSuperclassOrEqual(edgeM1Class, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				edgeProxyMap_DiskBasedStorage.put(edgeM1Class,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for edge proxy"
								+ implementationClass, ex);
			}
		}
	}
	
	@Override
	public void setEdgeProxyImplementationClass_DistributedStorage(
			Class<? extends Edge> edgeM1Class,
			Class<? extends Edge> implementationClass) {
		if (isSuperclassOrEqual(edgeM1Class, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				edgeProxyMap_DistributedStorage.put(edgeM1Class,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for edge proxy"
								+ implementationClass, ex);
			}
		}
	}

	@Override
	public void setVertexImplementationClass_InMemoryStorage(
			Class<? extends Vertex> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.mem.VertexImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, Graph.class };
				vertexMap_MemBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for vertexclass"
								+ implementationClass, ex);
			}
		}
	}

	@Override
	public void setVertexImplementationClass_DiskBasedStorage(
			Class<? extends Vertex> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.disk.VertexImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class };
				vertexMap_DiskBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramsDisk = { long.class,
						de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class, VertexContainer.class };
				vertexMapForDiskStorageReloading.put(originalClass,
						implementationClass.getConstructor(paramsDisk));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for vertexclass"
								+ implementationClass, ex);
			}
		}
	}
	
	@Override
	public void setVertexImplementationClass_DistributedStorage(
			Class<? extends Vertex> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.distributed.VertexImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl.class };
				vertexMap_DistributedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for vertexclass"
								+ implementationClass, ex);
			}
		}
	}

	@Override
	public void setVertexProxyImplementationClass_DistributedStorage(
			Class<? extends Vertex> vertexM1Class,
			Class<? extends Vertex> implementationClass) {
		if (isSuperclassOrEqual(vertexM1Class, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				vertexProxyMap_DistributedStorage.put(vertexM1Class,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for vertex proxy"
								+ implementationClass, ex);
			}
		}
	}
	
	@Override
	public void setVertexProxyImplementationClass_DiskBasedStorage(
			Class<? extends Vertex> vertexM1Class,
			Class<? extends Vertex> implementationClass) {
		if (isSuperclassOrEqual(vertexM1Class, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				vertexProxyMap_DiskBasedStorage.put(vertexM1Class,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for vertex proxy"
								+ implementationClass, ex);
			}
		}
	}

	@Override
	public void setIncidenceImplementationClass_InMemoryStorage(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, Vertex.class, Edge.class };
				incidenceMap_MemBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for incidenceclass"
								+ implementationClass, ex);
			}
		}
	}
	
	@Override
	public void setIncidenceImplementationClass_DistributedStorage(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl.class,
						long.class, long.class };
				incidenceMap_DistributedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for incidenceclass"
								+ implementationClass, ex);
			}
		}
	}

	@Override
	public void setIncidenceImplementationClass_DiskBasedStorage(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class,
						long.class, long.class };
				incidenceMap_DiskBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramsDisk = { long.class,
						de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class, IncidenceContainer.class };
				incidenceMapForDiskStorageReloading.put(originalClass,
						implementationClass.getConstructor(paramsDisk));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for incidenceclass"
								+ implementationClass, ex);
			}
		}
	}
	
	@Override
	public void setIncidenceProxyImplementationClass_DistributedStorage(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				incidenceProxyMap_DistributedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for incidence proxy"
								+ implementationClass, ex);
			}
		}
	}

	@Override
	public void setIncidenceProxyImplementationClass_DiskBasedStorage(
			Class<? extends Incidence> originalClass,
			Class<? extends Incidence> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				incidenceProxyMap_DiskBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for incidence proxy"
								+ implementationClass, ex);
			}
		}
	}
	
	@Override
	public void setRecordImplementationClass_InMemoryStorage(Class<? extends Record> m1Class,
			Class<? extends Record> implementationClass) {
		if (isSuperclassOrEqual(m1Class, implementationClass)) {
			try {
				Class<?>[] params = { Graph.class };
				recordMap_InMemoryStorage.put(m1Class,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for record"
								+ implementationClass, ex);
			}
		}
	}
	
	@Override
	public void setRecordImplementationClass_DistributedStorage(Class<? extends Record> m1Class,
			Class<? extends Record> implementationClass) {
		if (isSuperclassOrEqual(m1Class, implementationClass)) {
			try {
				Class<?>[] params = { Graph.class };
				recordMap_DiskBasedStorage.put(m1Class,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for record"
								+ implementationClass, ex);
			}
		}
	}
	
	public void setRecordImplementationClass_DiskBasedStorage(Class<? extends Record> m1Class,
			Class<? extends Record> implementationClass) {
		if (isSuperclassOrEqual(m1Class, implementationClass)) {
			try {
				Class<?>[] params = { Graph.class };
				recordMap_DiskBasedStorage.put(m1Class,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for record"
								+ implementationClass, ex);
			}
		}
	}

	
	@SuppressWarnings("unchecked")
	public <T extends Record> T createRecord_InMemoryStorage(Class<T> recordDomain, Graph g) {
		try {
			T r = (T) recordMap_InMemoryStorage.get(recordDomain).newInstance(g);
			return r;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create record of class "
					+ recordDomain.getCanonicalName(), ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Record> T createRecord_DistributedStorage(Class<T> recordDomain, Graph g) {
		try {
			T r = (T) recordMap_DistributedStorage.get(recordDomain).newInstance(g);
			return r;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create record of class "
					+ recordDomain.getCanonicalName(), ex);
		}
	}
	

	
	@SuppressWarnings("unchecked")
	public <T extends Record> T createRecord_DiskBasedStorage(Class<T> recordDomain, Graph g) {
		try {
			T r = (T) recordMap_DiskBasedStorage.get(recordDomain).newInstance(g);
			return r;
		} catch (Exception ex) {
			if (ex.getCause() instanceof GraphException) {
				throw new GraphException(ex.getCause().getLocalizedMessage(),
						ex);
			}
			throw new SchemaClassAccessException("Cannot create record of class "
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
	public de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl createViewGraph_InMemoryStorage(
			Graph viewGraph, int level) {
		try {
			Class<? extends Graph> graphClass = viewGraph.getM1Class();
			de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl g = (de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl) viewGraphMap_MemBasedStorage
					.get(graphClass).newInstance(viewGraph, level);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException(
					"Cannot create view graph for graph of class "
							+ viewGraph.getGraphClass().getQualifiedName(), ex);
		}
	}

	@Override
	public de.uni_koblenz.jgralab.impl.distributed.ViewGraphImpl createViewGraph_DistributedStorage(
			Graph viewGraph, int level) {
		try {
			Class<? extends Graph> graphClass = viewGraph.getM1Class();
			de.uni_koblenz.jgralab.impl.distributed.ViewGraphImpl g = (de.uni_koblenz.jgralab.impl.distributed.ViewGraphImpl) viewGraphMap_DiskBasedStorage
					.get(graphClass).newInstance(viewGraph, level);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException(
					"Cannot create view graph for graph of class "
							+ viewGraph.getGraphClass().getQualifiedName(), ex);
		}
	}
	
	@Override
	public de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl createViewGraph_DiskBasedStorage(
			Graph viewGraph, int level) {
		try {
			Class<? extends Graph> graphClass = viewGraph.getM1Class();
			de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl g = (de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl) viewGraphMap_DiskBasedStorage
					.get(graphClass).newInstance(viewGraph, level);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException(
					"Cannot create view graph for graph of class "
							+ viewGraph.getGraphClass().getQualifiedName(), ex);
		}
	}

	@Override
	public de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl createSubordinateGraphInVertex_InMemoryStorage(
			Vertex vertex) {
		try {
			Class<? extends Graph> graphClass = vertex.getGraph().getM1Class();
			System.out.println("Subordinate graph impl class: "
					+ subordinateGraphForVertexMap_MemBasedStorage
							.get(graphClass));
			de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl g = (de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl) subordinateGraphForVertexMap_MemBasedStorage
					.get(graphClass).newInstance(vertex);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException(
					"Cannot create subordinate graph for elem of class "
							+ vertex.getType().getQualifiedName(), ex);
		}
	}
	
	@Override
	public de.uni_koblenz.jgralab.impl.distributed.SubordinateGraphImpl createSubordinateGraphInVertex_DistributedStorage(
			de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl graphDatabase, long vertexId) {
		try {
			Vertex vertex = graphDatabase.getVertexObject(vertexId);
			Class<? extends Graph> graphClass = vertex.getGraph().getM1Class();
			de.uni_koblenz.jgralab.impl.distributed.SubordinateGraphImpl g = (de.uni_koblenz.jgralab.impl.distributed.SubordinateGraphImpl) subordinateGraphForVertexMap_DistributedStorage
					.get(graphClass).newInstance(vertex);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException(
					"Cannot create subordinate graph for vertex " + vertexId,
					ex);
		}
	}

	@Override
	public de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl createSubordinateGraphInVertex_DiskBasedStorage(
			de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, long vertexId) {
		try {
			Vertex vertex = graphDatabase.getVertexObject(vertexId);
			Class<? extends Graph> graphClass = vertex.getGraph().getM1Class();
			de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl g = (de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl) subordinateGraphForVertexMap_DiskBasedStorage
					.get(graphClass).newInstance(vertex);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException(
					"Cannot create subordinate graph for vertex " + vertexId,
					ex);
		}
	}

	@Override
	public de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl createSubordinateGraphInEdge_InMemoryStorage(
			Edge vertex) {
		try {
			Class<? extends Graph> graphClass = vertex.getGraph().getM1Class();
			de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl g = (de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl) subordinateGraphForEdgeMap_MemBasedStorage
					.get(graphClass).newInstance(vertex);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException(
					"Cannot create subordinate graph for elem of class "
							+ vertex.getType().getQualifiedName(), ex);
		}
	}
	
	@Override
	public de.uni_koblenz.jgralab.impl.distributed.SubordinateGraphImpl createSubordinateGraphInEdge_DistributedStorage(
			de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl graphDatabase, long edgeId) {
		try {
			Edge edge = graphDatabase.getEdgeObject(edgeId);
			Class<? extends Graph> graphClass = edge.getGraph().getM1Class();
			de.uni_koblenz.jgralab.impl.distributed.SubordinateGraphImpl g = (de.uni_koblenz.jgralab.impl.distributed.SubordinateGraphImpl) subordinateGraphForEdgeMap_DistributedStorage
					.get(graphClass).newInstance(edge);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException(
					"Cannot create subordinate graph for edge " + edgeId, ex);
		}
	}

	@Override
	public de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl createSubordinateGraphInEdge_DiskBasedStorage(
			de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl graphDatabase, long edgeId) {
		try {
			Edge edge = graphDatabase.getEdgeObject(edgeId);
			Class<? extends Graph> graphClass = edge.getGraph().getM1Class();
			de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl g = (de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl) subordinateGraphForEdgeMap_DiskBasedStorage
					.get(graphClass).newInstance(edge);
			return g;
		} catch (Exception ex) {
			throw new SchemaClassAccessException(
					"Cannot create subordinate graph for edge " + edgeId, ex);
		}
	}

	@Override
	public void setSubordinateGraphImplementationClass_InMemoryStorage(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.mem.SubordinateGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { Vertex.class };
				subordinateGraphForVertexMap_MemBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramse = { Edge.class };
				subordinateGraphForEdgeMap_MemBasedStorage.put(originalClass,
						implementationClass.getConstructor(paramse));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}
	
	@Override
	public void setSubordinateGraphImplementationClass_DistributedStorage(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.distributed.SubordinateGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				subordinateGraphForVertexMap_DistributedStorage.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramse = { long.class, de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				subordinateGraphForEdgeMap_DistributedStorage.put(originalClass,
						implementationClass.getConstructor(paramse));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	@Override
	public void setSubordinateGraphImplementationClass_DiskBasedStorage(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.disk.SubordinateGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { long.class, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				subordinateGraphForVertexMap_DiskBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
				Class<?>[] paramse = { long.class, de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl.class,
						RemoteGraphDatabaseAccess.class };
				subordinateGraphForEdgeMap_DiskBasedStorage.put(originalClass,
						implementationClass.getConstructor(paramse));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	@Override
	public void setViewGraphImplementationClass_InMemoryStorage(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.mem.ViewGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { originalClass, int.class };
				viewGraphMap_MemBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}

	@Override
	public void setViewGraphImplementationClass_DiskBasedStorage(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.disk.ViewGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { originalClass, int.class };
				viewGraphMap_DiskBasedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}
	
	@Override
	public void setViewGraphImplementationClass_DistributedStorage(
			Class<? extends Graph> originalClass,
			Class<? extends de.uni_koblenz.jgralab.impl.distributed.ViewGraphImpl> implementationClass) {
		if (isSuperclassOrEqual(originalClass, implementationClass)) {
			try {
				Class<?>[] params = { originalClass, int.class };
				viewGraphMap_DistributedStorage.put(originalClass,
						implementationClass.getConstructor(params));
			} catch (NoSuchMethodException ex) {
				throw new SchemaClassAccessException(
						"Unable to locate default constructor for graphclass "
								+ implementationClass.getName(), ex);
			}
		}
	}





	
	


}
