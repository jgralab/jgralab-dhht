package de.uni_koblenz.jgralab.impl.disk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.lang.ref.ReferenceQueue;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.BitSet;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * This class realizes the storage of vertices, edges and incidences on the disk. All
 * methods may be used only with local objects and local ids.
 * @author dbildh
 *
 */
public final class DiskStorageManager {
	
	
	/* Switches to toggle behaviour */
	
	private final static int MAX_REUSE_QUEUE_SIZE = 80;
	
	private final static int MAX_LRU_QUEUE_SIZE = 200;
	
	private final static int CLEANUP_THREAD_WAITING_TIME = 50;
		
	private final static int BITS_FOR_ELEMENT_MASK = 14;
	
	private static final boolean USE_LRU_QUEUE = true;

	
	/* Values that are calculated on the basis of MAX_NUMER_OF_ELEMENT and BITS_FOR_ELEMENT_MASK  */
	
	static final int CONTAINER_MASK = Integer.MAX_VALUE >> (32-(BITS_FOR_ELEMENT_MASK+1)); //= 00000011 11111111 in binary, 10 bit = 1024 elements per container
	
	static final int ELEMENT_CONTAINER_COUNT = Integer.MAX_VALUE >> (BITS_FOR_ELEMENT_MASK+2);
	
	static final int INCIDENCE_CONTAINER_COUNT = Integer.MAX_VALUE >> BITS_FOR_ELEMENT_MASK;
	
	static final int CONTAINER_SIZE = CONTAINER_MASK + 1;
		
	
	/* Threads to control the disk buffering */
		
	private Thread vertexCleanupThread;
	
	private Thread edgeCleanupThread;
	
	private Thread incidenceCleanupThread;
	
	
	/* maps that store proxies for remote elements */
	

	
	
	/* names of files to store element data */
	
	private static final String vertexFileName = "dhht_disk_storage_vertices";
	
	private static final String vertexAttributeFileName = "dhht_disk_storage_vertex_attributes";
	
	private static final String edgeFileName = "dhht_disk_storage_edges";
	
	private static final String edgeAttributeFileName =  "dhht_disk_storage_edge_attributes";
	
	private static final String incidenceFileName =   "dhht_disk_storage_incidences";
	
	
	private Schema schema;
//	
//	Graph graph;
//	
	private GraphFactory factory;
	
	private GraphDatabase graphDatabase;
	
	private int vertexStorageCount = 0;
	
	private int edgeStorageCount = 0;
	
	private int incidenceStorageCount = 0;

	private String randomId; 

	private FileChannel[] vertexFiles;
	
	private FileChannel[] vertexAttributeFiles;
	
	private FileChannel[] edgeFiles;
	
	private FileChannel[] edgeAttributeFiles;
	
	private FileChannel[] incidenceFiles;
	
	private VertexContainerReference[] vertexStorages;
	
	private EdgeContainerReference[] edgeStorages;
	
	private IncidenceContainerReference[] incidenceStorages;
	
	private BitSet vertexStorageSaved;
	
	private BitSet edgeStorageSaved;
	
	private BitSet incidenceStorageSaved;
	
	private ReferenceQueue<VertexContainer> vertexQueue;
	
	private ReferenceQueue<EdgeContainer> edgeQueue;
	
	private ReferenceQueue<IncidenceContainer> incidenceQueue;
	
	/* oldest container in LRU queue, will be unreferenced if queue is full
	 * and may be collected by the garbage collector as soon as memory is
	 * needed. Up to gc-clearance, the element is still reachable by a 
	 * soft reference and may be enqueued at a later point in time
	 */
	private StorageContainer firstInLRUQueue; 
	
	private StorageContainer lastInLRUQueue;
		
	private int lruQueueSize = 0;
	
	private VertexContainerReference firstInVertexReuseQueue;
	
	int vertexReuseQueueSize = 0;
	
	private EdgeContainerReference firstInEdgeReuseQueue;
	
	private int edgeReuseQueueSize = 0;
	
	private IncidenceContainerReference firstInIncidenceReuseQueue;
	
	private int incidenceReuseQueueSize = 0;
	
	
	public DiskStorageManager(GraphDatabase database) throws FileNotFoundException {
		schema = database.getSchema();
		this.graphDatabase = database;
		this.factory = database.getGraphFactory();
		randomId = Long.toString(System.currentTimeMillis());
		vertexFiles = new FileChannel[ELEMENT_CONTAINER_COUNT];
		edgeFiles = new FileChannel[ELEMENT_CONTAINER_COUNT];
		incidenceFiles = new FileChannel[INCIDENCE_CONTAINER_COUNT];
		vertexAttributeFiles = new FileChannel[ELEMENT_CONTAINER_COUNT];
		edgeAttributeFiles = new FileChannel[ELEMENT_CONTAINER_COUNT];
		vertexStorages = new VertexContainerReference[ELEMENT_CONTAINER_COUNT];
		edgeStorages = new EdgeContainerReference[ELEMENT_CONTAINER_COUNT];
		incidenceStorages = new IncidenceContainerReference[INCIDENCE_CONTAINER_COUNT];
		vertexStorageSaved = new BitSet(ELEMENT_CONTAINER_COUNT);
		edgeStorageSaved = new BitSet(ELEMENT_CONTAINER_COUNT);
		incidenceStorageSaved = new BitSet(INCIDENCE_CONTAINER_COUNT);
		

		
		vertexQueue = new ReferenceQueue<VertexContainer>();
		edgeQueue = new ReferenceQueue<EdgeContainer>();
		incidenceQueue = new ReferenceQueue<IncidenceContainer>();
		
		createFreeMemThread();
	}
	
	final void updateLRUStatus(StorageContainer container) {
		//remove from queue and enqueue
		if (lastInLRUQueue != container) {
			if (firstInLRUQueue == null) {
				//createFreeMemThread();
				firstInLRUQueue = container;
				lastInLRUQueue = container;
				container.nextInQueue = null;
				container.previousInQueue = null;
				lruQueueSize = 1;
				return;
			}
			// check if container is already part of the queue
			// and remove it
			if (container.nextInQueue != null) {
				if (container == firstInLRUQueue) {
					firstInLRUQueue = container.nextInQueue;
				}
				container.nextInQueue.previousInQueue = container.previousInQueue;
				if (container.previousInQueue != null) {
					container.previousInQueue.nextInQueue = container.nextInQueue;
					container.previousInQueue = null;
				}	
				container.nextInQueue = null;
			} else {
				//otherwise, increase queue size
				lruQueueSize++;
			}
			//enqueue element to the end of the queue
			container.previousInQueue = lastInLRUQueue;
			lastInLRUQueue.nextInQueue = container;
			lastInLRUQueue = container;
			if (lruQueueSize > MAX_LRU_QUEUE_SIZE) {
				//if queue is full, first element is dequeued
				StorageContainer oldFirst = firstInLRUQueue;
				firstInLRUQueue = firstInLRUQueue.nextInQueue;
				firstInLRUQueue.previousInQueue = null;
				oldFirst.nextInQueue = null;
				oldFirst.previousInQueue = null;
				lruQueueSize--;
			}
		}	
	}
	
	

	
	private final void writeStorage(FileChannel[] fileArray, ContainerReference storage, String baseName)  {
		try {
			FileChannel channel = getChannel(fileArray, storage.id, baseName);
			storage.write(channel);
			channel.force(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private final void writeAttributes(FileChannel[] fileArray, GraphElementContainerReference storage, String baseName)  {
		try {
			FileChannel channel = getChannel(fileArray, storage.id, baseName);
			storage.writeAttributes(channel);
			channel.force(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	
	private final FileChannel getChannel(FileChannel[] channelArray, int id, String fileBaseName) throws IOException {
		FileChannel channel = channelArray[id];
		if (channel == null) {
			RandomAccessFile file = new RandomAccessFile(File.createTempFile(fileBaseName + "_" + randomId + "_" + Integer.toString(id), "tmp"), "rw");
			channel = file.getChannel();
			channelArray[id] = channel;
		}
		return channel;
	}
	
	
	
	
	
	private static final int getContainerId(int elementId) {
		return elementId >> BITS_FOR_ELEMENT_MASK & Integer.MAX_VALUE;
	}
	
	private static final int getElementIdInContainer(long l) {
		return ((int)l) & CONTAINER_MASK & Integer.MAX_VALUE;
	}
	
	
	
	
	
	private final int clearUnusedVertexContainers() {
		int count = 0;
		VertexContainerReference ref = (VertexContainerReference) vertexQueue.poll();
		while (ref != null) {
			if (ref.isReused()) {
				ref = null;
			} else {
				writeStorage(vertexFiles, ref, vertexFileName);
				if (ref.attributes != null) {
					writeAttributes(vertexAttributeFiles, ref, vertexAttributeFileName);
				}
				vertexStorages[ref.id] = null;
				count++;
				if (vertexReuseQueueSize < MAX_REUSE_QUEUE_SIZE) {
					ref.nextInReuseQueue = (ContainerReference<?>) firstInVertexReuseQueue;
					firstInVertexReuseQueue = (VertexContainerReference) ref;
					vertexReuseQueueSize++;
				}
				setVertexStorageSaved(ref.id);
			}
			ref = (VertexContainerReference) vertexQueue.poll();
		} 
		return count;
	}
	
	
	private final int clearUnusedEdgeContainers() {
		int count = 0;
		EdgeContainerReference ref = (EdgeContainerReference) edgeQueue.poll();
		while (ref != null) {
			if (ref.isReused()) {
				ref = null;
			} else {
				writeStorage(edgeFiles, ref, edgeFileName);
				edgeStorages[ref.id] = null;
				count++;
				if (edgeReuseQueueSize < MAX_REUSE_QUEUE_SIZE) {
					ref.nextInReuseQueue = (ContainerReference<?>) firstInEdgeReuseQueue;
					firstInEdgeReuseQueue = (EdgeContainerReference) ref;
					edgeReuseQueueSize++;
				}
				setEdgeStorageSaved(ref.id);
			}
			 ref = (EdgeContainerReference) edgeQueue.poll();
		} 
		return count;
	}
	
	
	private final int clearUnusedIncidenceContainers() {
		int count = 0;
		IncidenceContainerReference ref = (IncidenceContainerReference) incidenceQueue.poll();
		while (ref != null) {
				if (ref.isReused()) {
					ref = null;
				} else {
					writeStorage(incidenceFiles, ref, incidenceFileName);
					incidenceStorages[ref.id] = null;
					count++;
					if (incidenceReuseQueueSize < MAX_REUSE_QUEUE_SIZE) {
						ref.nextInReuseQueue = (ContainerReference<?>) firstInIncidenceReuseQueue;
						firstInIncidenceReuseQueue = (IncidenceContainerReference) ref;
						incidenceReuseQueueSize++;
					}
					setIncidenceStorageSaved(ref.id);
				}
			ref = (IncidenceContainerReference) incidenceQueue.poll();
		} 
		return count;
	}
	
	

	private final void createFreeMemThread() {
		vertexCleanupThread = new Thread() {
			public void run() {
				do {
					synchronized (vertexStorages) {
						try {
							clearUnusedVertexContainers();
							vertexStorages.notify();
							vertexStorages.wait(CLEANUP_THREAD_WAITING_TIME);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
				} while (true);
			}
		};

		edgeCleanupThread = new Thread() {
			public void run() {
				do {
					synchronized (edgeStorages) {
						try {
							clearUnusedEdgeContainers();
							edgeStorages.notify();
							edgeStorages.wait(CLEANUP_THREAD_WAITING_TIME);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
				} while (true);
			}
		};

		incidenceCleanupThread = new Thread() {
			public void run() {
				do {
					synchronized (incidenceStorages) {
						try {
							clearUnusedIncidenceContainers();
							incidenceStorages.notify();
							incidenceStorages.wait(CLEANUP_THREAD_WAITING_TIME);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}

				} while (true);
			}
		};
		startCleanupThreads();
	}
	
	
	private void startCleanupThreads() {
		vertexCleanupThread.start();
		edgeCleanupThread.start();
		incidenceCleanupThread.start();
	}
	
	
	
	public void setVertexStorageSaved(int id) {
		vertexStorageSaved.set(id);		
	}
	
	public  boolean isVertexStorageSaved(int id) {
		boolean b = vertexStorageSaved.get(id);
		vertexStorageSaved.set(id, false);
		return b;		
	}
	
	public void setEdgeStorageSaved(int id) {
		edgeStorageSaved.set(id);		
	}
	
	public boolean isEdgeStorageSaved(int id) {
		boolean b = edgeStorageSaved.get(id);
		edgeStorageSaved.set(id, false);
		return b;		
	}

	public void setIncidenceStorageSaved(int id) {
		incidenceStorageSaved.set(id);		
	}
	
	public  boolean isIncidenceStorageSaved(int id) {
		boolean b = incidenceStorageSaved.get(id);
		incidenceStorageSaved.set(id, false);
		return b;		
	}
	
	private final GraphElementContainer getElementContainer(int id) {
		if (id >0)
			return getVertexContainer(id);
		else
			return getEdgeContainer(id);
	}
	
	
	
	private final VertexContainer reloadVertexStorage(int storageId) {
		while (!isVertexStorageSaved(storageId)) {
			try {
				vertexStorages.notifyAll();
				vertexStorages.wait(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			FileChannel channel = getChannel(vertexFiles, storageId, vertexFileName);
			VertexContainer storage = new VertexContainer(storageId, this);
			VertexContainerReference reference = null;		
			if (vertexReuseQueueSize > 0) {				
				vertexReuseQueueSize--;
				reference = new VertexContainerReference(storage, channel, firstInVertexReuseQueue, vertexQueue);
				firstInVertexReuseQueue = (VertexContainerReference) firstInVertexReuseQueue.nextInReuseQueue;
			} else {
				reference = new VertexContainerReference(storage, channel, vertexQueue);
			}	
			vertexStorages[storageId] = reference;
			return storage;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	final VertexContainer getVertexContainer(int vertexId) {
		int storageId = getContainerId(vertexId);
		VertexContainer storage = null;
		VertexContainerReference reference = null;
		if (storageId < vertexStorageCount) {
			synchronized (vertexStorages) {
				reference = vertexStorages[storageId];
				if (reference != null) {
					storage = reference.get();
					if (storage == null) {
						//reactivate storage
						reference.setReused();
						//create new container
						storage = new VertexContainer(storageId, this);
						reference = new VertexContainerReference(storage, reference, vertexQueue);
						vertexStorages[storageId] = reference;
					} 
				} else {
					//reload storage from disk
					storage = reloadVertexStorage(storageId);
				}
			}
		} else {
			storage = new VertexContainer(storageId, CONTAINER_SIZE, this);
			reference= new VertexContainerReference(storage, vertexQueue);
			vertexStorageCount++;
			vertexStorages[storageId] = reference;
		}
		if (USE_LRU_QUEUE)
			updateLRUStatus(storage);
		return storage;
	}	
	
	
	
	public final Vertex getVertexObject(int id) {
		VertexContainer container = getVertexContainer(id);
		int idInStorage = getElementIdInContainer(id);
		int type = container.types[idInStorage]; 
		if (type != 0) {
			//element is typed, so return either the existing vertex or create a new one
			Vertex v = container.vertices[idInStorage];
			if (v == null) {
				Class<? extends Vertex> c = (Class<? extends Vertex>) schema.getM1ClassForId(type);
				v = factory.reloadLocalVertex(c, id, graphDatabase, container);
				container.vertices[idInStorage] = v;
			}	
			return v;
		} else {
			return null;
		}	
	}

	
	public AttributeContainer[] getVertexAttributeContainerArray(int id) {
		VertexContainer container = getVertexContainer(id);
		if (container.attributes == null) {
			try {
					
				FileChannel channel = getChannel(vertexAttributeFiles, container.id, vertexAttributeFileName);
				if (channel.size() != 0) {
					ObjectInputStream input = new ObjectInputStream(Channels.newInputStream(channel));
					container.attributes = (AttributeContainer[]) input.readObject();
					input.close();
				} else {
					container.attributes = new AttributeContainer[CONTAINER_SIZE];
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return container.attributes;
	}
	
	
	public AttributeContainer getVertexAttributeContainer(int id) {
		AttributeContainer[] containerArray = getVertexAttributeContainerArray(id);
		int idInStorage = getElementIdInContainer(id);
		AttributeContainer container = containerArray[idInStorage];
		return container;
	}
	
	
	
	
	private final EdgeContainer reloadEdgeStorage(int storageId) {
		while (!isEdgeStorageSaved(storageId)) {
			try {
				edgeStorages.notify();
				edgeStorages.wait(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			FileChannel channel = getChannel(edgeFiles, storageId,edgeFileName);
			EdgeContainer storage = new EdgeContainer(storageId, this);
			EdgeContainerReference reference = null;		
			if (edgeReuseQueueSize > 0) {				
				edgeReuseQueueSize--;
				reference = new EdgeContainerReference(storage, channel, firstInEdgeReuseQueue, edgeQueue);
				firstInEdgeReuseQueue = (EdgeContainerReference) firstInEdgeReuseQueue.nextInReuseQueue;
			} else {
				reference = new EdgeContainerReference(storage, channel, edgeQueue);
			}	
			edgeStorages[storageId] = reference;
			return storage;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	final EdgeContainer getEdgeContainer(int edgeId) {
		int storageId = getContainerId(edgeId);
		EdgeContainer storage = null;
		EdgeContainerReference reference = null;
		if (storageId < edgeStorageCount) {
			synchronized (edgeStorages) {
				reference = edgeStorages[storageId];
				if (reference != null) {
					storage = reference.get();
					if (storage == null) {
						//reactivate storage
						reference.setReused();
						//create new container
						storage = new EdgeContainer(storageId, this);
						reference = new EdgeContainerReference(storage, reference, edgeQueue);
						edgeStorages[storageId] = reference;
					} 
				} else {
					//reload storage from disk
					storage = reloadEdgeStorage(storageId);
				}
			}
		} else {
			storage = new EdgeContainer(storageId, CONTAINER_SIZE, this);
			reference= new EdgeContainerReference(storage, edgeQueue);
			edgeStorageCount++;
			edgeStorages[storageId] = reference;
		}
		if (USE_LRU_QUEUE)
			updateLRUStatus(storage);
		return storage;
	}	
	
	

	public final Edge getEdgeObject(int id) {
			EdgeContainer container = getEdgeContainer(id);
			int idInStorage = getElementIdInContainer(id);
			int type = container.types[idInStorage]; 
			if (type != 0) {
				//element is typed, so return either the existing vertex or create a new one
				Edge e = container.edges[idInStorage];
				if (e == null) {
					e = factory.reloadLocalEdge((Class<? extends Edge>) schema.getM1ClassForId(type), id, graphDatabase, container);
					container.edges[idInStorage] = e;
				}
				return e;
			} else {
				return null;
			}	
		
	}
	
	public AttributeContainer[] getEdgeAttributeContainerArray(int id) {
		EdgeContainer container = getEdgeContainer(id);
		if (container.attributes == null) {
			try {
				FileChannel channel = getChannel(edgeAttributeFiles, container.id, edgeAttributeFileName);
				//channel mappen
				if (channel.size() != 0) {
					ObjectInputStream input = new ObjectInputStream(Channels.newInputStream(channel));
					container.attributes = (AttributeContainer[]) input.readObject();
					input.close();
				} else {
					container.attributes = new AttributeContainer[CONTAINER_SIZE];
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return container.attributes;
	}
	
	
	public AttributeContainer getEdgeAttributeContainer(int id) {
		AttributeContainer[] containerArray = getEdgeAttributeContainerArray(id);
		int idInStorage = getElementIdInContainer(id);
		AttributeContainer container = containerArray[idInStorage];
		return container;
	}
	
	private final IncidenceContainer reloadIncidenceStorage(int storageId) {
		while (!isIncidenceStorageSaved(storageId)) {
			try {
				incidenceStorages.notify();
				incidenceStorages.wait(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			FileChannel channel = getChannel(incidenceFiles, storageId,incidenceFileName);
			IncidenceContainer storage = new IncidenceContainer(storageId, this);
			IncidenceContainerReference reference = null;		
			if (incidenceReuseQueueSize > 0) {				
				incidenceReuseQueueSize--;
				reference = new IncidenceContainerReference(storage, channel, firstInIncidenceReuseQueue, incidenceQueue);
				firstInIncidenceReuseQueue = (IncidenceContainerReference) firstInIncidenceReuseQueue.nextInReuseQueue;
			} else {
				reference = new IncidenceContainerReference(storage, channel, incidenceQueue);
			}	
			incidenceStorages[storageId] = reference;
			return storage;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	final IncidenceContainer getIncidenceContainer(int incidenceId) {
		int storageId = getContainerId(incidenceId);
		IncidenceContainer storage = null;
		IncidenceContainerReference reference = null;
		if (storageId < incidenceStorageCount) {
			synchronized (incidenceStorages) {
				reference = incidenceStorages[storageId];
				if (reference != null) {
					storage = reference.get();
					if (storage == null) {
						//reactivate storage
						reference.setReused();
						//create new container
						storage = new IncidenceContainer(storageId, this);
						reference = new IncidenceContainerReference(storage, reference, incidenceQueue);
						incidenceStorages[storageId] = reference;
					} 
				} else {
					//reload storage from disk
					storage = reloadIncidenceStorage(storageId);
				}
			}
		} else {
			storage = new IncidenceContainer(storageId, CONTAINER_SIZE, this);
			reference= new IncidenceContainerReference(storage, incidenceQueue);
			incidenceStorageCount++;
			incidenceStorages[storageId] = reference;
		}
		if (USE_LRU_QUEUE)
			updateLRUStatus(storage);
		return storage;
	}	
			

	
	public final Incidence getIncidenceObject(int id) {
			if (id == 0)
				return null;
			IncidenceContainer container = getIncidenceContainer(id);
			int idInStorage = getElementIdInContainer(id);
			int type = container.types[idInStorage]; 
			if (type != 0) {
				//element is typed, so return either the existing vertex or create a new one
				Incidence i = container.incidences[idInStorage];
				if (i == null) {
					Class<?> c = graphDatabase.getSchema().getM1ClassForId(type);
					i = factory.reloadLocalIncidence((Class<? extends Incidence>) schema.getM1ClassForId(type), id, graphDatabase, container);
					container.incidences[idInStorage] = i;
				}
				return i;
			} else {
				return null;
			}	
	}

	
	
	/**
	 * Store and remove vertices
	 * @param v
	 */
	
	private int getLocalId(long id) {
		return GraphDatabase.getLocalElementId(id);
	}
	

	public void storeVertex(VertexImpl v) {
		int vId = getLocalId(v.getId());
		VertexContainer storage = getVertexContainer(vId);
		int id = getElementIdInContainer(vId);
		storage.vertices[id] = v;
		v.container = storage;
		storage.types[id] = graphDatabase.getSchema().getClassId(v.getType());
		AttributeContainer[] containerArray = getVertexAttributeContainerArray(getContainerId(vId));
		containerArray[id] = v.getAttributeContainer();
	}

	public void storeEdge(EdgeImpl e) {
		int eId = getLocalId(e.getId());
		EdgeContainer storage = getEdgeContainer(eId);
		int id = getElementIdInContainer(eId);
		storage.edges[id] = e;
		e.container = storage;
		storage.types[id] = graphDatabase.getSchema().getClassId(e.getType());
		AttributeContainer[] containerArray = getEdgeAttributeContainerArray(getContainerId(eId));
		containerArray[id] = e.getAttributeContainer();
	}

	public void storeIncidence(IncidenceImpl i) {
		int iId = getLocalId(i.getId());
		IncidenceContainer storage = getIncidenceContainer(iId);
		int id = getElementIdInContainer(i.getId());
		storage.incidences[id] = i;
		i.container = storage;
		storage.types[id] = graphDatabase.getSchema().getClassId(i.getType());
	}
	

	public void removeEdgeFromDiskStorage(long edgeId) {
		int eId = getLocalId(edgeId);
		EdgeContainer storage = getEdgeContainer(eId);
		int id = getElementIdInContainer(eId);
		storage.edges[id] = null;
		storage.types[id] = 0;
	}


	public void removeVertexFromDiskStorage(long vertexId) {
		int vId = getLocalId(vertexId);
		VertexContainer storage = getVertexContainer(vId);
		int id = getElementIdInContainer(vId);
		storage.vertices[id] = null;
		storage.types[id] = 0;
	}
	
	
	public void removeIncidenceFromDiskStorage(int incId) {
		IncidenceContainer storage = getIncidenceContainer(incId);
		int id = getElementIdInContainer(incId);
		storage.incidences[id] = null;
		storage.types[id] = 0;
	}



	
	
	
	
	/*
	 * Methods to access and modify Vseq, Eseq and Iseq
	 */
	
	
	// VSeq
	
	public void setNextVertexId(int vId, long nextVId) {
		getVertexContainer(getContainerId(vId)).nextElementInGraphId[getElementIdInContainer(vId)] = nextVId;
	}
	
	public long getNextVertexId(int vId) {
		return getVertexContainer(getContainerId(vId)).nextElementInGraphId[getElementIdInContainer(vId)];
	}
	
	public void setPreviousVertexId(int vId, long nextVId) {
		getVertexContainer(getContainerId(vId)).nextElementInGraphId[getElementIdInContainer(vId)] = nextVId;
	}
	
	public long getPreviousVertexId(int vId) {
		return getVertexContainer(getContainerId(vId)).nextElementInGraphId[getElementIdInContainer(vId)];
	}
	
	//Eseq
	
	public void setNextEdgeId(int eId, long nextEId) {
		getEdgeContainer(getContainerId(eId)).nextElementInGraphId[getElementIdInContainer(eId)] = nextEId;
	}
	
	public long getNextEdgeId(int eId) {
		return getEdgeContainer(getContainerId(eId)).nextElementInGraphId[getElementIdInContainer(eId)];
	}
	
	public void setPreviousEdgeId(int eId, long nextEId) {
		getEdgeContainer(getContainerId(eId)).nextElementInGraphId[getElementIdInContainer(eId)] = nextEId;
	}
	
	public long getPreviousEdgeId(int eId) {
		return getEdgeContainer(getContainerId(eId)).nextElementInGraphId[getElementIdInContainer(eId)];
	}
	
	
	//Iseq	
	
	public void setFirstIncidenceId(int elemId, long incidenceId) {
		getElementContainer(getContainerId(elemId)).firstIncidenceId[getElementIdInContainer(elemId)] = incidenceId;
	}
	
	public void setLastIncidenceId(int elemId, long incidenceId) {
		getElementContainer(getContainerId(elemId)).lastIncidenceId[getElementIdInContainer(elemId)] = incidenceId;
	}

	public long getFirstIncidenceId(int elemId) {
		return getElementContainer(getContainerId(elemId)).firstIncidenceId[getElementIdInContainer(elemId)];
	}
	
	public long  getLastIncidenceId(int elemId) {
		return getElementContainer(getContainerId(elemId)).lastIncidenceId[getElementIdInContainer(elemId)];
	}
	
	
	public void incidenceListModified(int elemId) {
		getElementContainer(getContainerId(elemId)).lastIncidenceId[getElementIdInContainer(elemId)]++;
	}
	
	// hierarchy

	public long getSigmaId(int localElemId) {
		return getElementContainer(getContainerId(localElemId)).sigmaId[getElementIdInContainer(localElemId)];
	}

	public void setSigmaId(int localElemId, long sigmaId) {
		getElementContainer(getContainerId(localElemId)).sigmaId[getElementIdInContainer(localElemId)] = sigmaId;
	}
	
	public int getKappa(int localElemId) {
		return getElementContainer(getContainerId(localElemId)).kappa[getElementIdInContainer(localElemId)];
	}

	public void setKappa(int localElemId, int kappa) {
		getElementContainer(getContainerId(-localElemId)).sigmaId[getElementIdInContainer(localElemId)] = kappa;
	}
	
	
	// distribution
	
	

	
	

	
	
}
