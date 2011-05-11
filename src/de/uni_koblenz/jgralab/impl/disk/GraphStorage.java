package de.uni_koblenz.jgralab.impl.disk;

public class GraphStorage {
	
	/* Switches that toggle number of elements in a local partial graph
	 * and number of partial graphs
	 */
	private final static int BITS_FOR_PARTIAL_GRAPH_MASK = 5;
	
	/* Values that are calculated on the basis of BITS_FOR_PARTIAL_GRAPH_MASK */
	
	public final static int MAX_NUMBER_OF_LOCAL_ELEMENTS = Integer.MAX_VALUE >> BITS_FOR_PARTIAL_GRAPH_MASK;
	
	public final static int MAX_NUMBER_OF_LOCAL_INCIDENCES = Integer.MAX_VALUE >> BITS_FOR_PARTIAL_GRAPH_MASK;
	
	
	

	public static final int getPartialGraphId(int elementId) {
		return elementId >> (32-BITS_FOR_PARTIAL_GRAPH_MASK);
	}
	
	public static final int getElementIdInPartialGraph(int elementId) {
		return elementId & (MAX_NUMBER_OF_LOCAL_ELEMENTS);
	}
	
}
