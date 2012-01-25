package de.uni_koblenz.jgralab.algolib.universalsearch;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

/** Interface of the satellite algorithms running on the remote stations */
public interface SatelliteAlgorithm {

	/** handles root vertex <code>root</code> on the station it is stored */
	public void handleRoot(Vertex root) throws RemoteException;

	/** handles vertex <code>vertex</code> on the station it is stored */
	public void handleVertex(Vertex vertex) throws RemoteException;

	/** handles vertex <code>vertex</code> on the station it is stored */
	public void handleEdge(Edge edge) throws RemoteException;

	/**
	 * handles tree incidence <code>incidence</code> on the station it is stored
	 */
	public void handleTreeIncidence(Incidence incidence) throws RemoteException;

	/**
	 * handles cross incidence <code>incidence</code> on the station it is
	 * stored
	 */
	public void handleCrossIncidence(Incidence incidence)
			throws RemoteException;
}