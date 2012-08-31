package de.uni_koblenz.jgralab.impl.diskv2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class DiskStorageManager {
		
	/**
	 * The GraphDatabase that this DiskStorageManager works for
	 */
	private GraphDatabaseBaseImpl graphdb;
	
	/**
	 * FileAccess objects to all the files used by this manager
	 */
	private FileAccess[] files;
	
	private FileAccess incidences;
	
	private FileAccess vertexDict;
	private FileAccess edgeDict;
	
	private FileAccess strings;
	private FileAccess lists;
	
	private long stringsPointer;
	private long listsPointer;
	
	public DiskStorageManager(GraphDatabaseBaseImpl graphdb){
		this.graphdb = graphdb;

		setupFilesAndProfiles();
	}
	
	/**
	 * For every non-abstract Vertex and Edge class, this method creates
	 * a GraphElementProfile and a FileAccess.
	 */
	private void setupFilesAndProfiles(){
		Schema s = graphdb.getSchema();
		
		//get the amount of classes
		int amountOfClasses = s.getNumberOfTypedElementClasses();
		//tell GraphElementProfile to instantiate the Array in which profiles are stored
		GraphElementProfile.setup(amountOfClasses);
		//make room for FileAccess objects, plus three more for incidences and the two dicts
		files = new FileAccess[amountOfClasses];
		
		//get lists of all vertex and edge classes
		List<VertexClass> vClasses = s.getVertexClassesInTopologicalOrder();
		List<EdgeClass> eClasses = s.getEdgeClassesInTopologicalOrder();
		
		//make FileAccess objects for incidences, the two dicts, strings and lists
		incidences = FileAccess.createFileAccess("incidences");
		vertexDict = FileAccess.createFileAccess("vertexDict");
		edgeDict = FileAccess.createFileAccess("edgeDict");
		strings = FileAccess.createFileAccess("strings");
		lists = FileAccess.createFileAccess("lists");
		
		stringsPointer = 0;
		listsPointer = 0;
		
		//create a profile and a FileAccess for all non-abstract vertex classes
		for (VertexClass vClass: vClasses){
			if (!vClass.isAbstract()){
				int typeId = vClass.getId();
				GraphElementProfile.createProfile(vClass, typeId, graphdb);
				files[typeId] = FileAccess.createFileAccess(typeId + "_vertices");
			}
		}
		
		//create a profile and a FileAccess for all non-abstract edge classes
		for (EdgeClass eClass: eClasses){
			if (!eClass.isAbstract()){
				int typeId = eClass.getId();
				GraphElementProfile.createProfile(eClass, typeId, graphdb);
				files[typeId] = FileAccess.createFileAccess(typeId + "_edges");
			}
		}
	}
	
	/**
	 * Writes a Graph Element to the disk if it has been newly created, or if it has
	 * been changed since the last time it was loaded from the disk.
	 * 
	 * @param geRef - The Reference to the GraphElement that is written out.
	 */
	public void writeGraphElementToDisk(CacheEntry<? extends GraphElementImpl<?,?,?,?>> vRef){
		Tracker tracker = vRef.getTracker();
		
		if (tracker == null) {
			//element is neither new nor has it been changed since its last reload
			return;
		}
		ByteBuffer attributes = tracker.getVariables();
		String[] strings = tracker.getStrings();
		List[] lists = tracker.getLists();
		
		//detect the type of the vertex or edge we want to write out
		int typeId = attributes.getInt(0) - 1;
		
		//fetch the profile of this type
		GraphElementProfile profile = GraphElementProfile.getProfile(typeId);
		
		//get access to the file in which elements of this type are stored
		FileAccess file = files[typeId];
		
		//determine the size of the element we want to store
		int byteSize = profile.getSize();
		long baseLocation = byteSize * vRef.getKey();
		
		//write the primitive attributes to a file
		file.write(attributes, baseLocation);
		
		//write all Strings to a file, and store their location on the disk
		if (strings != null){
			int numElems = profile.getNumStrings();
			ByteBuffer locations = ByteBuffer.allocate(numElems * 8);
			
			for (int i = 0; i < numElems; i++){
				long location = writeStringToDisk(strings[i]);
				locations.putLong(location);
			}
			
			locations.position(0);
			file.write(locations, baseLocation + profile.getStartOfStrings());
		}
		
		//write all Lists to a file, and store their location on the disk
		if (lists != null){
			int numElems = profile.getNumLists();
			ByteBuffer locations = ByteBuffer.allocate(numElems * 8);
				
			for (int i = 0; i < numElems; i++){
				locations.putLong(writeListToDisk(lists[i]));
			}

			file.write(locations, baseLocation + profile.getStartOfLists());
		}
	}
	
	/**
	 * Reads a vertex from the disk and restores it.
	 *  
	 * @param key
	 *        The local id of the vertex
	 * @return
	 *        A soft reference to the restored vertex
	 */
	public VertexImpl readVertexFromDisk(int key){
		//determine which vertex class we have to use
		int typeId = vertexDict.read(4, key*4).getInt(0);
		
		//read the data from the disk
		ByteBuffer buf = readGraphElementFromDisk(key, typeId);
		
		//create a vertex that is identical to the vertex we deleted earlier
		VertexImpl ver = restoreVertex(buf, key);
		
		//return a CacheEntry for that new vertex so we can put it back in the cache
		return ver;
	}
	
	/**
	 * Reads an edge from the disk and restores it.
	 *  
	 * @param key
	 *        The local id of the vertex
	 * @return
	 *        A soft reference to the restored vertex
	 */
	public EdgeImpl readEdgeFromDisk(int key){
		//determine which edge class we have to use
		int typeId = edgeDict.read(4, key*4).getInt(0);
		
		//read the data from the disk
		ByteBuffer buf = readGraphElementFromDisk(key, typeId);
		
		//create a vertex that is identical to the vertex we deleted earlier
		EdgeImpl edge = restoreEdge(buf, key);
		
		//return a CacheEntry for that new vertex so we can put it back in the cache
		return edge;
	}
	
	/**
	 * Helper method to avoid duplicate code in readVertexFromDisk 
	 * and readEdgeFromDisk
	 */
	private ByteBuffer readGraphElementFromDisk(int key, int typeId){
		//determine the size of the GraphElement we want to restore
		int byteSize = GraphElementProfile.getProfile(typeId).getSize();
		
		FileAccess file = files[typeId];
		ByteBuffer buf = file.read(byteSize, key * byteSize);
		
		buf.position(0);
		
		return buf;
	}
	
	/**
	 * Writes an Incidence to the disk if it has been newly created, or if it has
	 * been changed since the last time it was loaded from the disk.
	 * 
	 * @param incidenceRef - The Reference to the Incidence that is written out.
	 */
	public void writeIncidenceToDisk(CacheEntry<IncidenceImpl> incidenceRef){
		Tracker tracker = incidenceRef.getTracker();
		
		if (tracker == null) {
			//incidence is neither new nor has it been changed since its last reload
			return;
		}

		ByteBuffer attributes = tracker.getVariables();
		
		//incidences always need 52 bytes because they have no attributes
		incidences.write(attributes, incidenceRef.getKey() * 52);
	}
	
	/**
	 * Reads an incidence from the disk and restores it.
	 *  
	 * @param key
	 *        The local id of the incidence
	 * @return
	 *        A soft reference to the restored incidence
	 */
	public IncidenceImpl readIncidenceFromDisk(int key){
		//read 52 bytes from the file which stores the Incidences
		ByteBuffer buf = incidences.read(52, key * 52);
		
		buf.position(0);
		
		//create an Incidence that is identical to the Incidence we deleted earlier
		IncidenceImpl inc = restoreIncidence(buf, key);
		
		//return a CacheEntry for that new incidence so we can put it back in the cache
		return inc;
	}
	
	public long writeStringToDisk(String s){
		if (s == null) return -1;
		
		long currentPosition = stringsPointer;
		
		byte[] bytes = s.getBytes();
		int length = bytes.length;
		
		ByteBuffer buf = ByteBuffer.allocate(4 + length);
		buf.putInt(length);
		buf.put(bytes);
		
		strings.write(buf, stringsPointer);
		
		stringsPointer += (4 + length);

		return currentPosition;
	}
	
	public String readStringFromDisk(long position){
		if (position == -1) return null;
		
		ByteBuffer buf = strings.read(4, position);
		int length = buf.getInt(0);
		
		String res = new String(strings.read(length, position + 4).array());

		return res;
	}
	
	public long writeListToDisk(List<?> l){
		if (l == null) return -1;
		
		long currentPosition = listsPointer;
		
		byte[] bytes = serializeList(l);
		int length = bytes.length;
		
		ByteBuffer buf = ByteBuffer.allocate(4 + length);
		buf.putInt(length);
		buf.put(bytes);
		
		lists.write(buf, listsPointer);
		
		listsPointer += (4 + length);
		
		return currentPosition;
	}
	
	public List readListFromDisk(long position){
		if (position == -1) return null;
		
		ByteBuffer buf = lists.read(4, position);
		int length = buf.getInt(0);
		
		byte[] readBytes = lists.read(length, position + 4).array();
		
		return restoreList(readBytes);
	}

	/**
	 * Restores a Vertex using the data provided by a ByteBuffer.
	 * 
	 * @param buf
	 *        The ByteBuffer holding the Vertex' variables
	 * @param key
	 *        The Vertex' id
	 * @return
	 *        The restored Vertex
	 */
	private VertexImpl restoreVertex(ByteBuffer buf, int key){
		int typeId = buf.getInt(0) - 1;
		
		Schema schema = graphdb.getSchema();
		
		//get the vertex class for the typeId we read from the disk
		VertexClass verClass = (VertexClass) schema
				.getTypeForId(typeId);
		Class<? extends Vertex> m1Class = verClass.getM1Class();
		
		//convert the local ID (the key) to the global ID
		long longId = graphdb.convertToGlobalId(key);
		
		GraphFactory factory = graphdb.getGraphFactory();
		
		//create a new Vertex of the given vertex class with the given ID
		VertexImpl ver = (VertexImpl) factory
				.createVertex_Diskv2BasedStorage(m1Class, longId, graphdb);
		
		//restore the vertex' variables and restore it
		return (VertexImpl) restoreGraphElement(ver, buf, typeId);
	}
	
	/**
	 * Restores a Vertex using the data provided by a ByteBuffer.
	 * 
	 * @param buf
	 *        The ByteBuffer holding the Vertex' variables
	 * @param key
	 *        The Vertex' id
	 * @return
	 *        The restored Vertex
	 */
	private EdgeImpl restoreEdge(ByteBuffer buf, int key){
		int typeId = buf.getInt(0) - 1;
		
		Schema schema = graphdb.getSchema();
		
		//get the edge class for the typeId we read from the disk
		EdgeClass edgeClass = (EdgeClass) schema
				.getTypeForId(typeId);
		Class<? extends Edge> m1Class = edgeClass.getM1Class();
		
		//convert the local ID (the key) to the global ID
		long longId = graphdb.convertToGlobalId(key);
		
		//create a new Edge of the given edge class with the given ID
		GraphFactory factory = graphdb.getGraphFactory();
		EdgeImpl edge = (EdgeImpl) factory
				.createEdge_Diskv2BasedStorage(m1Class, longId, graphdb);
		
		//restore the edge's variables and restore it
		return (EdgeImpl) restoreGraphElement(edge, buf, typeId);
	}
	
	/**
	 * Helper method to avoid duplicate code in restoreVertex and restoreEdge
	 */
	private GraphElementImpl<?,?,?,?> restoreGraphElement(GraphElementImpl<?,?,?,?> ge, ByteBuffer buf, int typeId){
		//restore the non-generated variables of this GraphElement
		ge.restoreNextElementId(buf.getLong(4));
		ge.restorePreviousElementId(buf.getLong(12));
		ge.restoreFirstIncidenceId(buf.getLong(20));
		ge.restoreLastIncidenceId(buf.getLong(28));
		ge.restoreIncidenceListVersion(buf.getLong(36));
		ge.restoreSigmaId(buf.getLong(44));
		ge.restoreSubOrdianteGraphId(buf.getLong(52));
		ge.restoreKappa(buf.getInt(60));
		
		//restore the generated attributes of this GraphElement
		buf.position(64);
		GraphElementProfile prof = GraphElementProfile.getProfile(typeId);
		prof.restoreAttributesOfElement(ge, buf);
		
		//restore the Strings of this GraphElement
		long position;
		buf.position(prof.getStartOfStrings());
		
		int numElems = prof.getNumStrings();
		String[] strings = new String[numElems];
		
		for (int i = 0; i < numElems; i++){
			position = buf.getLong();
			strings[i] = readStringFromDisk(position);
		}
		
		prof.restoreStringsOfElement(ge, strings);
		
		//restore the Lists of this GraphElement
		numElems = prof.getNumLists();
		List[] lists = new List[numElems];
				
		for (int i = 0; i < numElems; i++){
			position = buf.getLong();
			lists[i] = readListFromDisk(position);
		}
				
		prof.restoreListsOfElement(ge, lists);

		return ge;
	}
	
	/**
	 * Restores an Incidence using the data provided by a ByteBuffer.
	 * 
	 * @param buf
	 *        The ByteBuffer holding the Incidence's variables
	 * @param key
	 *        The Incidence's id
	 * @return
	 *        The restored Incidence
	 */
	private IncidenceImpl restoreIncidence(ByteBuffer buf, int key){
		int typeId = buf.getInt(0) - 1;
		
		Schema schema = graphdb.getSchema();
		
		//get the incidence class for the typeId we read from the disk
		IncidenceClass incClass = (IncidenceClass) schema
				.getTypeForId(typeId);
		Class<? extends Incidence> m1Class = incClass.getM1Class();
		
		//read the attributes that we need for the factory method
		long longId = key;
		long eId = buf.getLong(36);
		long vId = buf.getLong(44);
		
		//create a new Incidence of the given incidence class with the given ID
		//(Incidences only have local IDs, so we don't need to convert the id)
		GraphFactory factory = graphdb.getGraphFactory();
		IncidenceImpl inc = (IncidenceImpl) factory
				.createIncidence_Diskv2BasedStorage(m1Class, longId, vId,
						eId, graphdb);
		
		//restore the variables that aren't required by the constructor
		inc.restoreNextIncidenceIdAtEdge(buf.getLong(4));
		inc.restoreNextIncidenceIdAtVertex(buf.getLong(12));
		inc.restorePreviousIncidenceIdAtEdge(buf.getLong(20));
		inc.restorePreviousIncidenceIdAtVertex(buf.getLong(28));

		return inc;
	}
	
	/**
	 * Converts a List to a ByteArray using the Java Serialization API.
	 * 
	 * @param
	 * 		The List to be serialized
	 * 
	 * @return
	 * 		The List as a ByteArray
	 */
	public byte[] serializeList(List<?> l){
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objStream = new ObjectOutputStream(outStream);
			objStream.writeObject(l);
		} catch (IOException e) {
			throw new RuntimeException("Unable to serialize list");
		}
		return outStream.toByteArray();
	}
	
	public List restoreList(byte[] readBytes){
		ByteArrayInputStream inStream = new ByteArrayInputStream(readBytes);
		try {
			ObjectInputStream objReader = new ObjectInputStream(inStream);
			return (List) objReader.readObject();
		} catch (IOException e) {
			throw new RuntimeException("Unable to create ObjectInputStream");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to restore list");
		}
	}
	
	/**
	 * Gets a file in which the vertex type ID for every vertex is stored
	 */
	public FileAccess getVertexDict(){
		return vertexDict;
	}
	
	/**
	 * Gets a file in which the edge type ID for every edge is stored
	 */
	public FileAccess getEdgeDict(){
		return edgeDict;
	}
	
	//-------------------------------------------------------------------
	//Methods and Variables to enforce a maximum size of the disk storage
	//-------------------------------------------------------------------
	
	private static long diskStorageSize;
	private static long maxDiskStorageSize = -1;
	
	public static void setMaxDiskStorageSize(long size){
		if (size < 1){
			throw new IllegalArgumentException("Maximum Disk Storage size must be bigger than zero");
		}
		maxDiskStorageSize = size;
		checkDiskStorage();
	}
	
	public static void increaseDiskStorageSize(long increment){
		diskStorageSize += increment;
		checkDiskStorage();
	}
	
	private static void checkDiskStorage(){
		if (maxDiskStorageSize > 0 && diskStorageSize > maxDiskStorageSize){
			throw new RuntimeException("Maximum Disk Storage size exceeded");
		}
	}
}
