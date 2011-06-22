package de.uni_koblenz.jgralab.impl.disk;

public class DiskImplementationBasicMethods {

	public static long getToplevelGraphForPartialGraphId(int partialGraphId) {
		long val = (DiskImplementationBasicMethods.TOPLEVEL_LOCAL_SUBGRAPH_ID << 32) + partialGraphId;
		return val;
	}

	// the global subgraph id of the toplevel dhhtgraph
	public static final long GLOBAL_GRAPH_ID = 0x0000000100000001l;
	// the local subgraph id of the toplevel graph if a partial one
	public static final long TOPLEVEL_LOCAL_SUBGRAPH_ID = 1;
	// the partial graph id of the toplevel graph, the lowest bit of the 
	// high int is set
	public static final int TOPLEVEL_PARTIAL_GRAPH_ID = 1;

}
