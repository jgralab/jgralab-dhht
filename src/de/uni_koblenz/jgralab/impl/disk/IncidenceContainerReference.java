package de.uni_koblenz.jgralab.impl.disk;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import de.uni_koblenz.jgralab.Incidence;



public class IncidenceContainerReference extends ContainerReference<IncidenceContainer> {
	

	long[] vertexId;
	
	long[] edgeId;
	
	long[] nextIncidenceAtVertexId;
	
	long[] previousIncidenceAtVertexId;

	long[] nextIncidenceAtEdgeId;
	
	long[] previousIncidenceAtEdgeId;

	
	
	public IncidenceContainerReference(IncidenceContainer container, ReferenceQueue<IncidenceContainer> queue) {
		super(container, queue);
		backgroundStorage = container.backgroundStorage;
		id = container.id;
		types = container.types;
		edgeId = container.edgeId;
		vertexId = container.vertexId;
		nextIncidenceAtEdgeId = container.nextIncidenceAtEdgeId;
		nextIncidenceAtVertexId = container.nextIncidenceAtVertexId;
		previousIncidenceAtEdgeId = container.previousIncidenceAtEdgeId;
		previousIncidenceAtVertexId = container.previousIncidenceAtVertexId;
	}
	

	public IncidenceContainerReference(IncidenceContainer container,
			FileChannel input, ReferenceQueue<IncidenceContainer> queue) throws IOException {
		super(container, queue);
		backgroundStorage = container.backgroundStorage;
		id = container.id;

		container.types = types = new int[DiskStorageManager.CONTAINER_SIZE];
   		container.edgeId = edgeId = new long[DiskStorageManager.CONTAINER_SIZE];
		container.vertexId = vertexId = new long[DiskStorageManager.CONTAINER_SIZE];
   		container.nextIncidenceAtEdgeId = nextIncidenceAtEdgeId = new long[DiskStorageManager.CONTAINER_SIZE];
  		container.nextIncidenceAtEdgeId = nextIncidenceAtEdgeId = new long[DiskStorageManager.CONTAINER_SIZE];
   		container.nextIncidenceAtVertexId = nextIncidenceAtVertexId = new long[DiskStorageManager.CONTAINER_SIZE];
   		container.previousIncidenceAtEdgeId = previousIncidenceAtEdgeId= new long[DiskStorageManager.CONTAINER_SIZE];
   		container.previousIncidenceAtVertexId = previousIncidenceAtVertexId= new long[DiskStorageManager.CONTAINER_SIZE];
   		container.incidences = new Incidence[DiskStorageManager.CONTAINER_SIZE];
   		read(input);
	}
	
	public IncidenceContainerReference(IncidenceContainer container, FileChannel input, IncidenceContainerReference oldRef, ReferenceQueue<IncidenceContainer> queue) throws IOException {
		super(container, queue);
		backgroundStorage = container.backgroundStorage;
		id = container.id;
		container.types = types = oldRef.types;
   		container.edgeId = edgeId = oldRef.edgeId;
		container.vertexId = vertexId = oldRef.vertexId;
  		container.nextIncidenceAtEdgeId = nextIncidenceAtEdgeId = oldRef.nextIncidenceAtEdgeId;
   		container.nextIncidenceAtVertexId = nextIncidenceAtVertexId = oldRef.nextIncidenceAtVertexId;
   		container.previousIncidenceAtEdgeId = previousIncidenceAtEdgeId = oldRef.previousIncidenceAtEdgeId;
   		container.previousIncidenceAtVertexId = previousIncidenceAtVertexId = oldRef.previousIncidenceAtVertexId;
   		container.incidences = new Incidence[DiskStorageManager.CONTAINER_SIZE];
   		read(input);
	}
		
	/**
	 * Constructor that copies the old data to the new reference and container to be reused 
	 * @param storage
	 * @param firstInIncidenceReuseQueue
	 * @param incidenceQueue
	 */
	public IncidenceContainerReference(IncidenceContainer container,
			IncidenceContainerReference oldRef,
			ReferenceQueue<? extends IncidenceContainer> queue) {
		super(container, queue);
		backgroundStorage = container.backgroundStorage;
		id = container.id;
		container.types = types = oldRef.types;
   		container.edgeId = edgeId = oldRef.edgeId;
		container.vertexId = vertexId = oldRef.vertexId;
  		container.nextIncidenceAtEdgeId = nextIncidenceAtEdgeId = oldRef.nextIncidenceAtEdgeId;
   		container.nextIncidenceAtVertexId = nextIncidenceAtVertexId = oldRef.nextIncidenceAtVertexId;
   		container.previousIncidenceAtEdgeId = previousIncidenceAtEdgeId = oldRef.previousIncidenceAtEdgeId;
   		container.previousIncidenceAtVertexId = previousIncidenceAtVertexId = oldRef.previousIncidenceAtVertexId;
   		container.incidences = new Incidence[DiskStorageManager.CONTAINER_SIZE];
	}

	void read(FileChannel channel) throws IOException {
		MappedByteBuffer bb = channel.map(MapMode.READ_ONLY, 0, DiskStorageManager.CONTAINER_SIZE * 4 * 8);
		LongBuffer lb = bb.asLongBuffer();
		lb.get(edgeId);
		lb.get(vertexId);
		lb.get(nextIncidenceAtEdgeId);
		lb.get(nextIncidenceAtVertexId);
		lb.get(previousIncidenceAtEdgeId);
		lb.get(previousIncidenceAtVertexId);
		IntBuffer ib = bb.asIntBuffer();
		ib.get(types);
		lb.clear();
	}	
	
	public String toString() {
		return "IncidenceStorage " + id;
	}
	


	void write(FileChannel channel) throws IOException {
		MappedByteBuffer bb = channel.map(MapMode.READ_WRITE, 0, DiskStorageManager.CONTAINER_SIZE * 4 * 8);
		LongBuffer lb = bb.asLongBuffer();
		lb.put(edgeId);
		lb.put(vertexId);
		lb.put(nextIncidenceAtEdgeId);
		lb.put(nextIncidenceAtVertexId);
		lb.put(previousIncidenceAtEdgeId);
		lb.put(previousIncidenceAtVertexId);
		IntBuffer ib =bb.asIntBuffer();
		ib.put(types);
	}
	
	void nullify() {
		types = null;
		edgeId = null;
		vertexId = null;
		nextIncidenceAtEdgeId = null;
		previousIncidenceAtEdgeId = null;
		nextIncidenceAtVertexId = null;
		previousIncidenceAtVertexId = null;
	}
	
//	
//	private static void assrt(int exp, int cur) {
//		if (exp != cur)
//			throw new RuntimeException("Expected " + exp + " but was " + cur);
//	}
//	
//	private static void assrt(boolean exp, boolean cur, int id) {
//		if (exp != cur)
//			throw new RuntimeException("Expected " + exp + " but was " + cur + " at positiopn " + id);
//	}
//
//
//	public static void main(String[] args) {
//		try {
//		BackgroundStorage b = new BackgroundStorage(null);
//		IncidenceStorage storage = new IncidenceStorage(1, BackgroundStorage.CONTAINER_SIZE, b);
//		ReferenceQueue refQueue = new ReferenceQueue();
//		IncidenceContainerReference ref = new IncidenceContainerReference(storage, refQueue);
//		
//		for (int i = 0; i<BackgroundStorage.CONTAINER_SIZE; i++) {
//			storage.types[i] = i+7;
//			storage.edgeId[i] = i+1;
//			storage.vertexId[i] = i+2;
//			storage.direction[i] = (i % 2 == 0);
//			storage.nextIncidenceAtEdgeId[i] = i+3;
//			storage.nextIncidenceAtVertexId[i] = i+4;
//			storage.previousIncidenceAtEdgeId[i] = i+5;
//			storage.previousIncidenceAtVertexId[i] = i+6;
//		}
//		for (int i = 0; i<BackgroundStorage.CONTAINER_SIZE; i++) 
//			assrt(ref.get().direction[i], ref.direction[i], 1);	
//		for (int i = 0; i<BackgroundStorage.CONTAINER_SIZE; i++) 
//			assrt(storage.direction[i], ref.direction[i], 1);		
//		RandomAccessFile f = new RandomAccessFile("/tmp/testfile", "rw");
//		FileChannel out = f.getChannel();
//		ref.write(out);
//		out.close();
//		for (int i = 0; i<BackgroundStorage.CONTAINER_SIZE; i++) {
//			assrt(storage.direction[i], ref.direction[i], 77);
//		}
//		f = null;
//		IncidenceStorage storage2 = new IncidenceStorage(1, BackgroundStorage.CONTAINER_SIZE, b);
//		IncidenceContainerReference ref2 = new IncidenceContainerReference(storage2, refQueue);
//		f = new RandomAccessFile("/tmp/testfile", "rw");
//		FileChannel in = f.getChannel();
//		ref2.read(in);
//		for (int i =0; i<BackgroundStorage.CONTAINER_SIZE; i++) {
//			assrt(storage.types[i], ref2.types[i]);
//			assrt(storage.edgeId[i], ref2.edgeId[i]);
//			assrt(storage.vertexId[i], ref2.vertexId[i]);
//			assrt(storage.direction[i], ref.direction[i], i);
//
//			assrt(storage2.direction[i], ref2.direction[i], i);
//			assrt(storage.direction[i], storage2.direction[i], i);
//			assrt(storage.nextIncidenceAtEdgeId[i], ref2.nextIncidenceAtEdgeId[i]);
//			assrt(storage.nextIncidenceAtVertexId[i], ref2.nextIncidenceAtVertexId[i]);
//			assrt(storage.previousIncidenceAtEdgeId[i], ref2.previousIncidenceAtEdgeId[i]);
//			assrt(storage.previousIncidenceAtVertexId[i], ref2.previousIncidenceAtVertexId[i]);
//		}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//		System.out.println("Successfull");
//		
//	}
	
	
	
	
}
