/**
 * 
 */
package de.uni_koblenz.jgralab;

import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * Represents a binary {@link Edge}. This means that its {@link EdgeClass} is
 * connected to to {@link VertexClass}es with multiplicity 1. One of the two
 * {@link IncidenceClass}es must have the direction
 * {@link Direction#EDGE_TO_VERTEX} and the other
 * {@link Direction#VERTEX_TO_EDGE}.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface BinaryEdge extends Edge {

	/**
	 * Returns the start {@link Vertex} of this {@link BinaryEdge}.
	 * 
	 * @return {@link Vertex} the alpha {@link Vertex} of this {@link Edge}
	 * @throws UnsupportedOpperationException
	 *             if this {@link Edge} is not binary.
	 */
	public Vertex getAlpha();

	/**
	 * Returns the end {@link Vertex} of this {@link BinaryEdge}.
	 * 
	 * @return {@link Vertex} the omega {@link Vertex} of this {@link Edge}
	 * @throws UnsupportedOpperationException
	 *             if this {@link Edge} is not binary.
	 */
	public Vertex getOmega();

	/**
	 * The semanctics of this {@link Edge}, e.g. {@link IncidenceType#EDGE},
	 * {@link IncidenceType#AGGREGATION} or {@link IncidenceType#COMPOSITION}.
	 * 
	 * @return {@link IncidenceType}
	 */
	public IncidenceType getSemantics();

	/**
	 * The semanctics of the alpha {@link Incidence} of this {@link Edge}, e.g.
	 * {@link IncidenceType#EDGE}, {@link IncidenceType#AGGREGATION} or
	 * {@link IncidenceType#COMPOSITION}.
	 * 
	 * @return {@link IncidenceType}
	 */
	public IncidenceType getAlphaSemantics();

	/**
	 * The semanctics of the omega {@link Incidence} of this {@link Edge}, e.g.
	 * {@link IncidenceType#EDGE}, {@link IncidenceType#AGGREGATION} or
	 * {@link IncidenceType#COMPOSITION}.
	 * 
	 * @return {@link IncidenceType}
	 */
	public IncidenceType getOmegaSemantics();

}
