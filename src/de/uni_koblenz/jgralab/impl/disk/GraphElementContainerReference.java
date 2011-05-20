package de.uni_koblenz.jgralab.impl.disk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public abstract class GraphElementContainerReference<T extends GraphElementContainer> extends ContainerReference<T> {
	
	long[] incidenceListVersion = null;
	
	int[] kappa;
	
	long[] sigmaId;
	
	long[] nextElementInGraphId;
	
	long[] previousElementInGraphId;
	
	long[] firstIncidenceId;
	
	long[] lastIncidenceId;
	
	AttributeContainer[] attributes;
	
	
	public GraphElementContainerReference(T container,ReferenceQueue<? super StorageContainer> queue) {
		super(container, queue);
		backgroundStorage = container.backgroundStorage;
		types = container.types;
		id = container.id;
		incidenceListVersion = container.incidenceListVersion;
		kappa = container.kappa;
		sigmaId = container.sigmaId;
		nextElementInGraphId = container.nextElementInGraphId;
		previousElementInGraphId = container.previousElementInGraphId;
		firstIncidenceId = container.firstIncidenceId;
		lastIncidenceId = container.lastIncidenceId;
	}
	
	public GraphElementContainerReference(T container,
			FileChannel input, ReferenceQueue queue) throws IOException, ClassNotFoundException {
		super(container, queue);
		backgroundStorage = container.backgroundStorage;
		id = container.id;

		container.types = types = new int[DiskStorageManager.CONTAINER_SIZE];
   		container.incidenceListVersion = incidenceListVersion= new long[DiskStorageManager.CONTAINER_SIZE];
   		container.kappa = kappa= new int[DiskStorageManager.CONTAINER_SIZE];
  		container.sigmaId = sigmaId= new long[DiskStorageManager.CONTAINER_SIZE];
   		container.nextElementInGraphId = nextElementInGraphId= new long[DiskStorageManager.CONTAINER_SIZE];
   		container.previousElementInGraphId = previousElementInGraphId= new long[DiskStorageManager.CONTAINER_SIZE];
   		container.firstIncidenceId = firstIncidenceId= new long[DiskStorageManager.CONTAINER_SIZE];
   		container.lastIncidenceId = lastIncidenceId= new long[DiskStorageManager.CONTAINER_SIZE];
   		read(input);
	}
	
	public GraphElementContainerReference(T container, FileChannel input, GraphElementContainerReference<T> oldRef, ReferenceQueue queue) throws IOException {
		super(container, queue);
		backgroundStorage = container.backgroundStorage;
		id = container.id;
		container.types = types = oldRef.types;
   		container.incidenceListVersion = incidenceListVersion = oldRef.incidenceListVersion;
   		container.kappa = kappa = oldRef.kappa;
  		container.sigmaId = sigmaId = oldRef.sigmaId;
   		container.nextElementInGraphId = nextElementInGraphId = oldRef.nextElementInGraphId;
   		container.previousElementInGraphId = previousElementInGraphId = oldRef.previousElementInGraphId;
   		container.firstIncidenceId = firstIncidenceId = oldRef.firstIncidenceId;
   		container.lastIncidenceId = lastIncidenceId = oldRef.lastIncidenceId;
   		read(input);
	}
	
	public GraphElementContainerReference(T container, GraphElementContainerReference<T> oldRef, ReferenceQueue queue) {
		super(container, queue);
		backgroundStorage = container.backgroundStorage;
		id = container.id;
		container.types = types = oldRef.types;
   		container.incidenceListVersion = incidenceListVersion = oldRef.incidenceListVersion;
   		container.kappa = kappa = oldRef.kappa;
  		container.sigmaId = sigmaId = oldRef.sigmaId;
   		container.nextElementInGraphId = nextElementInGraphId = oldRef.nextElementInGraphId;
   		container.previousElementInGraphId = previousElementInGraphId = oldRef.previousElementInGraphId;
   		container.firstIncidenceId = firstIncidenceId = oldRef.firstIncidenceId;
   		container.lastIncidenceId = lastIncidenceId = oldRef.lastIncidenceId;
	}



	void writeAttributes(FileChannel channel) throws IOException {
		MappedByteBuffer bb = channel.map(MapMode.READ_WRITE, 0, DiskStorageManager.CONTAINER_SIZE * 4 * 9);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(bao);
		output.writeObject(attributes);
		output.close();
		bb.put(bao.toByteArray());
		bb.force();
	}


	
	void read(FileChannel channel) throws IOException {
		MappedByteBuffer bb = channel.map(MapMode.READ_ONLY, 0, DiskStorageManager.CONTAINER_SIZE * 4 * 9);
		if (types == null)
			throw new RuntimeException("Types is null");
		IntBuffer ib = bb.asIntBuffer();
		ib.get(types);
		ib.get(kappa);
		
		LongBuffer lb = bb.asLongBuffer(); 
		lb.get(sigmaId);
		lb.get(nextElementInGraphId);
		lb.get(previousElementInGraphId);
		lb.get(firstIncidenceId);
		lb.get(lastIncidenceId);
		lb.get(incidenceListVersion);
	}	
	

	void write(FileChannel channel) throws IOException {
		MappedByteBuffer bb = channel.map(MapMode.READ_WRITE, 0, DiskStorageManager.CONTAINER_SIZE * 4 * 9);
		IntBuffer ib = bb.asIntBuffer();
		ib.clear();
		ib.put(types);
		ib.put(kappa);
		
		LongBuffer lb = bb.asLongBuffer(); 
		lb.put(sigmaId);
		lb.put(nextElementInGraphId);
		lb.put(previousElementInGraphId);
		lb.put(firstIncidenceId);
		lb.put(lastIncidenceId);
		lb.put(incidenceListVersion);

	}
}
