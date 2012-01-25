package de.uni_koblenz.jgralab.algolib.universalsearch;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Graph;

public class MyCentralAlgorithm extends CentralAlgorithmImpl {

	public MyCentralAlgorithm(Graph partialGraph, boolean dfs) {
		super(partialGraph, dfs);
	}

	public long getVertexCount() {
		long count = 0;
		for (SatelliteAlgorithmRemoteAccess remoteAlgorithm : remoteAlgorithms.values()) {
			try {
				count += remoteAlgorithm.getVCount();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return count;
	}
	
	public long getEdgeCount() {
		long count = 0;
		for (SatelliteAlgorithmRemoteAccess remoteAlgorithm : remoteAlgorithms.values()) {
			try {
				count += remoteAlgorithm.getECount();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return count;
	}

}
