package de.uni_koblenz.jgralab.impl.diskv2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * This class is used to detect and store the attributes of generated vertex and edge
 * classes. It stores the names and type of every generated attribute and offers 
 * ways to invoke the get- and set-methods for every attribute.
 * It is implied that every profile corresponds to exactly one vertex or edge class.
 * A profile is valid for all Edges and Vertices of the same class. 
 *  
 * @author aheld
 *
 */
public class GraphElementProfile {
	
	/**
	 * Array holding the names of the generated attributes
	 */
	private String[] attrNames;
	
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
	
	//constraints for the four arrays listed above:
	//- all four arrays must have the same length.
	//- attrTypeIDs[n], getters[n] and setters[n] refer to the type, the get method and the
	//  set method for the attribute named at attrNames[n], respectively.
	
	/**
	 * Creates a new profile for a given class, which must be a subclass of
	 * either VertexImpl or EdgeImpl.
	 * 
	 * @param cls - The Edge or Vertex class to be profiled
	 */
	public GraphElementProfile(Class<?> cls){
		detectAttributes(cls);
		detectGetters(cls);
		detectSetters(cls);
	}
	
	/**
	 * Initializes all four arrays with the same length
	 *  
	 * @param length - The length of the arrays
	 */
	private void initArrays(int length){
		attrNames = new String[length];
		attrTypeIDs = new byte[length];
		getters = new Method[length];
		setters = new Method[length];
	}
	
	/**
	 * Uses the Java Reflection API to detect all generated attributes of a Vertex or Edge
	 * class. The names of the attributes are stored in the array attrNames and their type IDs
	 * are stored at the respective position in the array attrTypeIDs.
	 * 
	 * @param cls - The Edge or Vertex class to be profiled
	 */
	private void detectAttributes(Class<?> cls){
		Field[] fields = cls.getDeclaredFields();
		int numAttr = fields.length;
		
		initArrays(numAttr);
		
		for (int i = 0; i < fields.length; i++){
			Field current = fields[i];
			attrNames[i] = current.getName();
			attrTypeIDs[i] = getTypeID(current.getType());
		}
	}

	/**
	 * Uses the Java Reflection API to obtain all get methods for the generated attributes
	 * specified by the array attrNames. These are stored in the array getters so they
	 * can be invoked later without having to use reflection again.
	 * 
	 * @param cls - The Edge or Vertex class to be profiled
	 */
	private void detectGetters(Class<?> cls){
		for (int i = 0; i < attrNames.length; i++){
			String methodName;
			if (attrTypeIDs[i] == 0){
				methodName = "is" + attrNames[i];
			}
			else {
				methodName = "get" + attrNames[i];
			}
			Method m;
			try {
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
	private void detectSetters(Class<?> cls){
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
	 * Helper method to obtain a set method for an attribute of type boolean.
	 */
	private Method detectBooleanSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = Boolean.TYPE;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to create Method: " + methodName + "(Boolean)");
		}
	}
	
	/**
	 * Helper method to obtain a set method for an attribute of type integer.
	 */
	private Method detectIntegerSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = Integer.TYPE;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to create Method: " + methodName + "(Integer)");
		}
	}
	
	/**
	 * Helper method to obtain a set method for an attribute of type long.
	 */
	private Method detectLongSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = Long.TYPE;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to create Method: " + methodName + "(Long)");
		}
	}
	
	/**
	 * Helper method to obtain a set method for an attribute of type double.
	 */
	private Method detectDoubleSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = Double.TYPE;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to create Method: " + methodName + "(Double)");
		}
	}
	
	/**
	 * Helper method to obtain a set method for an attribute of type String.
	 */
	private Method detectStringSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = String.class;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to create Method: " + methodName + "(String)");
		}
	}
	
	/**
	 * Helper method to obtain a set method for a List.
	 */
	private Method detectListSetter(String methodName, Class<?> cls, Class<?>[] parameter){
		parameter[0] = java.util.List.class;
		try {
			return cls.getMethod(methodName, parameter);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to create Method: " + methodName + "(List)");
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
	 * Helper method to returns the name of a type for a given type ID. Used to
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
		String output = "";
		for (int i = 0; i < attrNames.length; i++){
			output += getTypeName(attrTypeIDs[i]) + " ";
			output += attrNames[i] + "\n";
			output += "Getter: " + getters[i].getName() + "\n";
			output += "Setter: " + setters[i].getName() + "\n\n";
		}
		return output;
	}
}
