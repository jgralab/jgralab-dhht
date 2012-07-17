package de.uni_koblenz.jgralab.impl.diskv2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * This class is used to detect and store the attributes of generated vertex and edge
 * classes. It stores the type of every generated attribute and offers 
 * ways to invoke the get- and set-methods for every attribute.
 * For every non-abstract vertex and edge class, there must be exactly one profile.
 * A profile is valid for all Edges and Vertices of the same class. 
 *  
 * @author aheld
 *
 */
public class GraphElementProfile {
	
	private static GraphElementProfile[] profiles;
		
	/**
	 * The size that the profiled GraphElement needs on the disk.
	 * This includes variables, like kappa and firstIncidenceId,
	 * as well as all generated attributes that are of primitive types.
	 * 
	 * For Strings and Lists, 8 Bytes are needed to store the position
	 * where the actual String or List can be found. These are also
	 * added to the size.
	 */
	private int size;
	
	/**
	 * Array holding the type IDs of the generated attributes
	 * Each ID corresponds to a type. The table is:
	 * 0 -> boolean
	 * 1 -> integer
	 * 2 -> long
	 * 3 -> double
	 * 4 -> String
	 * 5 -> List
	 */
	private byte[] attrTypeIDs;
	
	/**
	 * Array holding the get methods for every generated attribute
	 */
	private Method[] getters;
	
	/**
	 * Array holding the set method for every generated attribute
	 */
	private Method[] setters;
	
	//constraints for the three arrays listed above:
	//- all four arrays must have the same length.
	//- attrTypeIDs[n], getters[n] and setters[n] refer to the type, the get method and the
	//  set method for the same attribute.
	
	/**
	 * Creates a new profile for a given class, which must be a subclass of
	 * either VertexImpl or EdgeImpl.
	 * 
	 * @param cls - The Edge or Vertex class to be profiled
	 */
	public GraphElementProfile(Class<? extends GraphElement<?,?,?,?>> cls, int typeId){
		String[] attrNames = detectAttributes(cls);
		detectGetters(cls, attrNames);
		detectSetters(cls, attrNames);
		detectSize();
	}
	
	/**
	 * Creates a new profile for the given vertex class.
	 * 
	 * @param cls
	 * 		The vertex class to be profiled
	 * @param typeId
	 * 		The internal ID of the given vertex class
	 * @param graphdb
	 * 		The GraphDatabase that we work with
	 */
	//TODO: Find a better way to implement this
	public static void createProfile(VertexClass cls, int typeId, GraphDatabaseBaseImpl graphdb){
		Class<? extends Vertex> m1Class = cls.getM1Class();
		GraphFactory factory = graphdb.getGraphFactory();
		Vertex dummy = factory.createVertex_Diskv2BasedStorage(m1Class, 0, graphdb);
		GraphElementProfile profile = new GraphElementProfile(dummy.getClass(), typeId);
		profiles[typeId] = profile;
	}
	
	/**
	 * Creates a new profile for the given edge class.
	 * 
	 * @param cls
	 * 		The edge class to be profiled
	 * @param typeId
	 * 		The internal ID of the given edge class
	 * @param graphdb
	 * 		The GraphDatabase that we work with
	 */
	//TODO: Find a better way to implement this
	public static void createProfile(EdgeClass cls, int typeId, GraphDatabaseBaseImpl graphdb){
		Class<? extends Edge> m1Class = cls.getM1Class();
		GraphFactory factory = graphdb.getGraphFactory();
		Edge dummy = factory.createEdge_Diskv2BasedStorage(m1Class, 0, graphdb);
		GraphElementProfile profile = new GraphElementProfile(dummy.getClass(), typeId);
		profiles[typeId] = profile;
	}
	
	/**
	 * Get a profile for the vertex or edge class which has the given internal ID.
	 * 
	 * @param typeId
	 * 		The internal ID of the vertex or edge class
	 * @return
	 * 		A profile for the specified vertex or edge class
	 */
	public static GraphElementProfile getProfile(int typeId){
		return profiles[typeId];
	}
	
	/**
	 * Instantiates the Array that stores all profiles.
	 * 
	 * @param size
	 * 		The maximum amount of profiles we will need to store
	 */
	public static void setup(int size){
		profiles = new GraphElementProfile[size];
	}
	
	/**
	 * Returns the size of the vertex or edge class
	 * @return
	 * 		How many bytes an object of the given class needs on the disk 
	 */
	public int getSize(){
		return size;
	}
	
	/**
	 * Returns a ByteBuffer containing the primitive attributes of a GraphElement
	 * as well as information where its Strings and Lists are stored.
	 * 
	 * @param ge 
	 * 		The GraphElement whose attributes are written to the buffer
	 * 
	 * @return 
	 * 		A byte array containing the given GraphElement's attributes
	 */
	public ByteBuffer getAttributesForElement(GraphElement<?,?,?,?> ge){
		//make enough room to store all attributes
		ByteBuffer buf = ByteBuffer.allocate(size);
		
		//iterate over every attribute
		for (int i = 0; i < attrTypeIDs.length; i++){
			switch (attrTypeIDs[i]){
				case 0: //attribute is a Boolean, invoke its get method and store it
					if (invokeGetBoolean(ge, i))
						buf.put((byte) 1);
					else buf.put((byte) 0);
					break;
				case 1: //attribute is an Integer, invoke its get method and store it
					buf.putInt(invokeGetInteger(ge, i));
					break;
				case 2: //attribute is a Long, invoke its get method and store it
					buf.putLong(invokeGetLong(ge, i));
					break;
				case 3: //attribute is an Double, invoke its get method and store it
					buf.putDouble(invokeGetDouble(ge, i));
					break;
			}
		}
		
		return buf;
	}
	
	public void restoreAttributesOfElement(GraphElement<?,?,?,?> ge, ByteBuffer buf){
		for (int i = 0; i < attrTypeIDs.length; i++){
			
			//iterate over every attribute
			switch (attrTypeIDs[i]){
				case 0: //attribute is a Boolean, invoke its set method and restore it
					if (buf.get() == 1)
						invokeSetBoolean(ge, true, i);
					else invokeSetBoolean(ge, false, i);
					break;
				case 1: //attribute is an Integer, invoke its set method and restore it
					invokeSetInteger(ge, buf.getInt(), i);
					break;
				case 2: //attribute is a Long, invoke its set method and restore it
					invokeSetLong(ge, buf.getLong(), i);
					break;
				case 3: //attribute is an Double, invoke its set method and restore it
					invokeSetDouble(ge, buf.getDouble(), i);
					break;
			}
		}
	}
	
	/**
	 * Initializes all four arrays with the same length
	 *  
	 * @param length - The length of the arrays
	 */
	private void initArrays(int length){
		attrTypeIDs = new byte[length];
		getters = new Method[length];
		setters = new Method[length];
	}
	
	/**
	 * Uses the Java Reflection API to detect all generated attributes of a Vertex
	 * or Edge class. Their type IDs are stored in the array attrTypeIDs.
	 * 
	 * @param cls - The Edge or Vertex class to be profiled
	 */
	private String[] detectAttributes(Class<?> cls){
		//get an array containg all fields of this class, i.e. all attributes of 
		//the vertex or edge class
		Field[] fields = cls.getDeclaredFields();
		int numAttr = fields.length;
		
		initArrays(numAttr);
		String[] attrNames = new String[numAttr];
		
		//write the names and types of each attribute in arrays 
		for (int i = 0; i < fields.length; i++){
			Field current = fields[i];
			attrNames[i] = current.getName();
			attrTypeIDs[i] = getTypeID(current.getType());
		}
		
		//return the array containing the names so we can use it to find the getters
		//and setters
		return attrNames;
	}

	/**
	 * Uses the Java Reflection API to obtain all get methods for the generated attributes
	 * specified by the array attrNames. These are stored in the array getters so they
	 * can be invoked later without having to use reflection again.
	 * 
	 * @param cls - The Edge or Vertex class to be profiled
	 */
	private void detectGetters(Class<?> cls, String[] attrNames){
		for (int i = 0; i < attrNames.length; i++){
			String methodName;
			if (attrTypeIDs[i] == 0){ //case 1: Boolean, method name starts with 'is'
				methodName = "is" + attrNames[i];
			}
			else { //case 2: not Boolean, method name starts with 'get'
				methodName = "get" + attrNames[i];
			}
			Method m;
			try {
				//fetch the get method and store it in the array
				m = cls.getMethod(methodName, (Class<?>[]) null);
				getters[i] = m;
			} catch (SecurityException e) {
				throw new IllegalArgumentException("Method " + methodName + " is not public");
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("Unknown method " + methodName);
			}
		}
	}
	
	/**
	 * Uses the Java Reflection API to obtain all set methods for the generated attributes
	 * specified by the array attrNames. These are stored in the array setters so they
	 * can be invoked later without having to use reflection again.
	 * 
	 * @param cls - The Edge or Vertex class to be profiled
	 */
	private void detectSetters(Class<?> cls, String[] attrNames){
		for (int i = 0; i < attrNames.length; i++){
			String methodName = "set" + attrNames[i];
			Method m;
			Class<?>[] parameter = new Class[1];
			switch (attrTypeIDs[i]){
				case 0:
					m = detectBooleanSetter(methodName, cls, parameter);
					break;
				case 1:
					m = detectIntegerSetter(methodName, cls, parameter);
					break;
				case 2:
					m = detectLongSetter(methodName, cls, parameter);
					break;
				case 3:
					m = detectDoubleSetter(methodName, cls, parameter);
					break;
				case 4:
					m = detectStringSetter(methodName, cls, parameter);
					break;
				case 5:
					m = detectListSetter(methodName, cls, parameter);
					break;
				default:
					throw new IllegalArgumentException("Invalid attribute ID: " + attrTypeIDs[i]);
			}
			setters[i] = m;
		}
	}
	
	/**
	 * Detects the indexes at which the attributes need to be stored in the
	 * Tracker.
	 */
	private void detectSize(){
		int currentIndex = 64; //64 bytes for non-generated variables and the type ID
		for (int i = 0; i < attrTypeIDs.length; i++){
			currentIndex++; //we need at least one byte
			if (attrTypeIDs[i] > 0){
				currentIndex += 3; //attribute is not a Boolean, we need three more bytes
				if (attrTypeIDs[i] > 1){
					currentIndex += 4; //attribute is not an Integer, either
					                   //so we need four more bytes
				}
			}
		}
		size = currentIndex;
	}
	
	/**
	 * Helper method to obtain a set method for an attribute of type boolean.
	 */
	private Method detectBooleanSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = Boolean.TYPE;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Method " + methodName + " is not public");
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Unknown method " + methodName);
		}
	}
	
	/**
	 * Helper method to obtain a set method for an attribute of type integer.
	 */
	private Method detectIntegerSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = Integer.TYPE;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Method " + methodName + " is not public");
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Unknown method " + methodName);
		}
	}
	
	/**
	 * Helper method to obtain a set method for an attribute of type long.
	 */
	private Method detectLongSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = Long.TYPE;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Method " + methodName + " is not public");
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Unknown method " + methodName);
		}
	}
	
	/**
	 * Helper method to obtain a set method for an attribute of type double.
	 */
	private Method detectDoubleSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = Double.TYPE;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Method " + methodName + " is not public");
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Unknown method " + methodName);
		}
	}
	
	/**
	 * Helper method to obtain a set method for an attribute of type String.
	 */
	private Method detectStringSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = String.class;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Method " + methodName + " is not public");
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Unknown method " + methodName);
		}
	}
	
	/**
	 * Helper method to obtain a set method for a List.
	 */
	private Method detectListSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = java.util.List.class;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Method " + methodName + " is not public");
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Unknown method " + methodName);
		}
	}
	
	/**
	 * Method to invoke the set method for an attribute of type boolean. 
	 * 
	 * @param ge - The GraphElement whose set method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param argument - The argument that is passed to the set method.
	 * @param position - The position at which the set method invoked by this method is stored in the
	 *                   array 'setters'. The type of this method's argument must be the same as the 
	 *                   type of the argument passed to this method.
	 */
	private void invokeSetBoolean(GraphElement<?,?,?,?> ge, boolean argument, int position){
		try {
			setters[position].invoke(ge, argument);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + setters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + setters[position].getName());
		}
	}
	
	/**
	 * Method to invoke the set method for an attribute of type int.
	 * 
	 * @param ge - The GraphElement whose set method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param argument - The argument that is passed to the set method.
	 * @param position - The position at which the set method invoked by this method is stored in the
	 *                   array 'setters'. The type of this method's argument must be the same as the 
	 *                   type of the argument passed to this method.
	 */
	private void invokeSetInteger(GraphElement<?,?,?,?> ge, int argument, int position){
		try {
			setters[position].invoke(ge, argument);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + setters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + setters[position].getName());
		}
	}
	
	/**
	 * Method to invoke the set method for an attribute of type long. 
	 * 
	 * @param ge - The GraphElement whose set method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param argument - The argument that is passed to the set method.
	 * @param position - The position at which the set method invoked by this method is stored in the
	 *                   array 'setters'. The type of this method's argument must be the same as the 
	 *                   type of the argument passed to this method.
	 */
	private void invokeSetLong(GraphElement<?,?,?,?> ge, long argument, int position){
		try {
			setters[position].invoke(ge, argument);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + setters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + setters[position].getName());
		}
	}
	
	/**
	 * Method to invoke the set method for an attribute of type double.
	 * 
	 * @param ge - The GraphElement whose set method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param argument - The argument that is passed to the set method.
	 * @param position - The position at which the set method invoked by this method is stored in the
	 *                   array 'setters'. The type of this method's argument must be the same as the 
	 *                   type of the argument passed to this method.
	 */
	private void invokeSetDouble(GraphElement<?,?,?,?> ge, double argument, int position){
		try {
			setters[position].invoke(ge, argument);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + setters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + setters[position].getName());
		}
	}
	
	/**
	 * Method to invoke the set method for an attribute of type String. 
	 * 
	 * @param ge - The GraphElement whose set method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param argument - The argument that is passed to the set method.
	 * @param position - The position at which the set method invoked by this method is stored in the
	 *                   array 'setters'. The type of this method's argument must be the same as the 
	 *                   type of the argument passed to this method.
	 */
	private void invokeSetString(GraphElement<?,?,?,?> ge, String argument, int position){
		try {
			setters[position].invoke(ge, argument);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + setters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + setters[position].getName());
		}
	}
	
	/**
	 * Method to invoke the set method for an attribute of type List.
	 * 
	 * @param ge - The GraphElement whose set method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param argument - The argument that is passed to the set method.
	 * @param position - The position at which the set method invoked by this method is stored in the
	 *                   array 'setters'. The type of this method's argument must be the same as the 
	 *                   type of the argument passed to this method.
	 */
	private void invokeSetList(GraphElement<?,?,?,?> ge, List<?> argument, int position){
		try {
			setters[position].invoke(ge, argument);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + setters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + setters[position].getName());
		}
	}
	
	/**
	 * Method to invoke the get method for an attribute of type Boolean.
	 * 
	 * @param ge - The GraphElement whose get method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param position - The position at which the get method invoked by this method is stored in the
	 *                   array 'getters'. The invoked method's return type must be the same as the 
	 *                   return type of this method.
	 */
	private boolean invokeGetBoolean(GraphElement<?,?,?,?> ge, int position){
		try {
			return (Boolean) getters[position].invoke(ge, (Object[]) null);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + getters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + getters[position].getName());
		}
	}
	
	/**
	 * Method to invoke the get method for an attribute of type Integer.
	 * 
	 * @param ge - The GraphElement whose get method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param position - The position at which the get method invoked by this method is stored in the
	 *                   array 'getters'. The invoked method's return type must be the same as the 
	 *                   return type of this method.
	 */
	private int invokeGetInteger(GraphElement<?,?,?,?> ge, int position){
		try {
			return (Integer) getters[position].invoke(ge, (Object[]) null);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + getters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + getters[position].getName());
		}
	}
	
	/**
	 * Method to invoke the get method for an attribute of type Long.
	 * 
	 * @param ge - The GraphElement whose get method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param position - The position at which the get method invoked by this method is stored in the
	 *                   array 'getters'. The invoked method's return type must be the same as the 
	 *                   return type of this method.
	 */
	private long invokeGetLong(GraphElement<?,?,?,?> ge, int position){
		try {
			return (Long) getters[position].invoke(ge, (Object[]) null);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + getters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + getters[position].getName());
		}
	}
	
	/**
	 * Method to invoke the get method for an attribute of type Double.
	 * 
	 * @param ge - The GraphElement whose get method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param position - The position at which the get method invoked by this method is stored in the
	 *                   array 'getters'. The invoked method's return type must be the same as the 
	 *                   return type of this method.
	 */
	private double invokeGetDouble(GraphElement<?,?,?,?> ge, int position){
		try {
			return (Double) getters[position].invoke(ge, (Object[]) null);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + getters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + getters[position].getName());
		}
	}
	
	/**
	 * Method to invoke the get method for an attribute of type String.
	 * 
	 * @param ge - The GraphElement whose get method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param position - The position at which the get method invoked by this method is stored in the
	 *                   array 'getters'. The invoked method's return type must be the same as the 
	 *                   return type of this method.
	 */
	private String invokeGetString(GraphElement<?,?,?,?> ge, int position){
		try {
			return (String) getters[position].invoke(ge, (Object[]) null);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + getters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + getters[position].getName());
		}
	}
	
	/**
	 * Method to invoke the get method for an attribute of type List.
	 * 
	 * @param ge - The GraphElement whose get method shall be invoked. Its class must be the same class
	 *             that was passed to the Constructor of the GraphElementProfile object for which
	 *             this method is called. 
	 * @param position - The position at which the get method invoked by this method is stored in the
	 *                   array 'getters'. The invoked method's return type must be the same as the 
	 *                   return type of this method.
	 */
	private List<?> invokeGetList(GraphElement<?,?,?,?> ge, int position){
		try {
			return (List<?>) getters[position].invoke(ge, (Object[]) null);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Method " + getters[position].getName() + " is not public");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown by invoked method " + getters[position].getName());
		}
	}
	
	/**
	 * Helper method that returns the ID for a given type. For the full table, 
	 * see documentation of attrTypeIDs.
	 */
	private byte getTypeID(Class<?> type){
		String typeName = type.getName();
		
		if (typeName.equals("boolean")) return 0;
		if (typeName.equals("int")) return 1;
		if (typeName.equals("long")) return 2;
		if (typeName.equals("double")) return 3;
		if (typeName.equals("java.lang.String")) return 4;
		if (typeName.equals("java.util.List")) return 5;
		
		throw new IllegalArgumentException("Unknown attribute type: " + type);
	}
	
	/**
	 * Helper method to return the name of the
	 *  type for a given type ID. Used to
	 * output a profile in a human-readable form.
	 */
	private String getTypeName(byte typeID){
		switch (typeID){
			case 0: return "Boolean";
			case 1: return "Integer";
			case 2: return "Long";
			case 3: return "Double";
			case 4: return "String";
			case 5: return "List";
			default: throw new IllegalArgumentException ("Unknown type ID: " + typeID);
		}
	}
	
	@Override
	public String toString(){
		String output = "Size: " + Integer.toString(size) + "Bytes\n";
		for (int i = 0; i < attrTypeIDs.length; i++){
			output += "Type:   " + getTypeName(attrTypeIDs[i]) + "\n";
			output += "Getter: " + getters[i].getName() + "\n";
			output += "Setter: " + setters[i].getName() + "\n";
		}
		return output;
	}
}
