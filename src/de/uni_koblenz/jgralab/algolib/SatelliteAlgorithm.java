package de.uni_koblenz.jgralab.algolib;

import java.rmi.Remote;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

/** Interface of the satellite algorithms running on the remote stations */
public interface SatelliteAlgorithm extends Remote { 
	  
	/** process vertex given by its id <code>vertexId</code> and 
	  * belonging to the local partial graph. 
	  * @implements $\mathbf{Communication~ point~ 1}$ and $\mathbf{Communication~ point~ 3}$ 
	  */   
	public void processVertex(Long vertexId);   
	
	/** handles root vertex <code>root</code> on the station it is stored */	                               
	public void handleRoot(Vertex root);                
	
	/** handles vertex <code>vertex</code> on the station it is stored */
	public void handleVertex(Vertex vertex); 
	  
	/** handles vertex <code>vertex</code> on the station it is stored */   
	public void handleEdge(Edge edge);                              
	                                        
	/** handles tree incidence <code>incidence</code>
	  * on the station it is stored */ 
	public void handleTreeIncidence(Incidence incidence);
        
	/** handles cross incidence <code>incidence</code>
	  * on the station it is stored */ 
	public void handleCrossIncidence(Incidence incidence);
} 