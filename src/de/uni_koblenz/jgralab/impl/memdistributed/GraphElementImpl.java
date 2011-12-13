/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

package de.uni_koblenz.jgralab.impl.memdistributed;

import java.util.Comparator;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * Implementation of all methods of the interface {@link GraphElement} which are
 * independent of the fields of a specific GraphElementImpl.
 * 
 * @author ist@uni-koblenz.de
 * 
 * @param <OwnType>
 *            This parameter must be either {@link Vertex} in case of a vertex
 *            or {@link Edge} in case of an edge.
 * @param <DualType>
 *            If <code>&lt;OwnType&gt;</code> is {@link Vertex} this parameter
 *            must be {@link Edge}. Otherwise it has to be {@link Vertex}.
 * 
 */

// TODO: Implement methods getLocalGraph, getGraph and getContainingGraph
// getCompleteGraph returns the complete raph
// get local graph the complete one or a local partial one
// getContainingGraph the graph that directly contains the element
public abstract class GraphElementImpl<OwnTypeClass extends GraphElementClass<OwnTypeClass, OwnType>, OwnType extends GraphElement<OwnTypeClass, OwnType, DualType>, DualType extends GraphElement<?, DualType, OwnType>>
		implements GraphElement<OwnTypeClass, OwnType, DualType> {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1245169302974416890L;

	/**
	 * The id of this {@link GraphElement}.
	 */
	protected long id;

	/**
	 * Holds the version of the {@link Incidence} structure, for every
	 * modification of the structure (e.g. adding or deleting an
	 * {@link Incidence} or changing the incidence sequence) this version number
	 * is increased by one. It is set to 0 when the {@link GraphElement} is
	 * created or the graph is loaded.
	 */
	private long incidenceListVersion = 0;
	
	protected long firstIncidenceId;
	
	protected long lastIncidenceId;

	/**
	 * The kappa value, which represents the highest level in which this
	 * {@link GraphElement} is visible.
	 */
	private int kappa = DEFAULT_KAPPA_VALUE;

	// TODO determine default value
	private static final int DEFAULT_KAPPA_VALUE = Integer.MAX_VALUE;


	/**
	 * The {@link Graph} to which this {@link GraphElement} belongs.
	 */
	protected CompleteGraphImpl graph;
	

	protected GraphDatabaseBaseImpl graphDb;
	

	/**
	 * The subordinate graph nested in this graph‚
	 */
	protected long subOrdinateGraphId;

	
	protected long nextElementId;
	
	protected long previousElementId;
	
	private long sigmaId;

	
	
	/**
	 * Creates a new {@link GraphElement} which belongs to <code>graph</code>.
	 * 
	 * @param graph
	 *            {@link Graph}
	 */
	protected GraphElementImpl(long id, de.uni_koblenz.jgralab.impl.memdistributed.GraphDatabaseBaseImpl graphDatabase) {
		this.graphDb = graphDatabase;
		this.id = id;
	}


	@Override
	public final GraphElement<?, ?, ?> getSigma() {
		return (sigmaId > 0) ? graphDb.getVertexObject(sigmaId) : graphDb.getEdgeObject(-sigmaId);
	}

	@Override
	public final int getKappa() {
		return kappa;
	}

	@Override
	public final boolean isVisible(int kappa) {
		return this.kappa >= kappa;
	}

	/**
	 * @param incidenceClass
	 * @param elemToConnect
	 * @param incidenceId
	 *            the id of the created incidence
	 * @return
	 */
	public abstract Incidence connect(IncidenceClass incidenceClass, DualType elemToConnect,
			int incidenceId);

	

	
	@Override
	public final boolean containsElement(GraphElement<?, ?, ?> element) {
		for (GraphElement<?, ?, ?> el = element; el.getSigma() != null
				&& getKappa() > el.getKappa(); el = el.getSigma()) {
			if (el.getSigma() == this) {
				return true;
			}
		}
		return false;
	}

	@Override
	public final Graph getGraph() {
		return graph;
	}

	@Override
	public final GraphClass getGraphClass() {
		return graph.getType();
	}

	@Override
	public final Schema getSchema() {
		return graph.getSchema();
	}

	/**
	 * Changes the graph version of the graph this element belongs to. Should be
	 * called whenever the graph is changed, all changes like adding, creating
	 * and reordering of edges and vertices or changes of attributes of the
	 * graph, an edge or a vertex are treated as a change.
	 */
	public final void graphModified() {
		graph.graphModified();
	}

	@Override
	public final long getGlobalId() {
		return id;
	}

	@Override
	public final int getLocalId() {
		return (int) id;
	}

	/**
	 * Sets {@link GraphElement#partialGraphId}.
	 * 
	 * @param id
	 *            int an id
	 */
	protected final void setId(int id) {
		assert id >= 0;
		this.id = id;
	}

	@Override
	public final void initializeAttributesWithDefaultValues() {
		for (Attribute attr : getType().getAttributeList()) {
			if (attr.getDefaultValueAsString() == null) {
				continue;
			}
			try {
				internalSetDefaultValue(attr);
			} catch (GraphIOException e) {
				e.printStackTrace();
			}
		}
	}

	protected final void internalSetDefaultValue(Attribute attr)
			throws GraphIOException {
		attr.setDefaultValue(this);
	}

	/**
	 * Must be called by all methods which manipulate the incidence list of this
	 * {@link GraphElement}.
	 */
	public final void incidenceListModified() {
		assert isValid();
		setIncidenceListVersion(getIncidenceListVersion() + 1);
	}

	/**
	 * Checks if the list of {@link Incidence}s has changed with respect to the
	 * given <code>incidenceListVersion</code>.
	 * 
	 * @param incidenceListVersion
	 *            long
	 * @return boolean true if <code>incidenceListVersion</code> differs from
	 *         {@link #incidenceListVersion}
	 */
	public final boolean isIncidenceListModified(long incidenceListVersion) {
		assert isValid();
		return (this.getIncidenceListVersion() != incidenceListVersion);
	}

	/**
	 * Sets {@link #incidenceListVersion} to <code>incidentListVersion</code>.
	 * 
	 * @param incidenceListVersion
	 *            long
	 */
	protected final void setIncidenceListVersion(long incidenceListVersion) {
		this.incidenceListVersion = incidenceListVersion;
	}

	/**
	 * @return long the internal incidence list version
	 * @see #isIncidenceListModified(long)
	 */
	public final long getIncidenceListVersion() {
		assert isValid();
		return incidenceListVersion;
	}

	@Override
	public final Incidence getFirstIncidence(IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graph.getTraversalContext(),
				anIncidenceClass.getM1Class(), null, false);
	}

	@Override
	public final <T extends Incidence> T getFirstIncidence(
			Class<T> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graph.getTraversalContext(), anIncidenceClass,
				null, false);
	}

	@Override
	public final Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			Direction direction) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graph.getTraversalContext(),
				anIncidenceClass.getM1Class(), direction, false);
	}

	@Override
	public final <T extends Incidence> T getFirstIncidence(
			Class<T> anIncidenceClass, Direction direction) {
		return getFirstIncidence(graph.getTraversalContext(), anIncidenceClass,
				direction, false);
	}

	@Override
	public final Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graph.getTraversalContext(),
				anIncidenceClass.getM1Class(), null, noSubclasses);
	}

	@Override
	public final <T extends Incidence> T getFirstIncidence(
			Class<T> anIncidenceClass, boolean noSubclasses) {
		return getFirstIncidence(graph.getTraversalContext(), anIncidenceClass,
				null, noSubclasses);
	}

	@Override
	public final Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graph.getTraversalContext(),
				anIncidenceClass.getM1Class(), direction, noSubclasses);
	}

	@Override
	public final Incidence getFirstIncidence(Graph traversalContext,
			IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext,
				anIncidenceClass.getM1Class(), null, false);
	}

	@Override
	public final <T extends Incidence> T getFirstIncidence(
			Graph traversalContext, Class<T> anIncidenceClass) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext, anIncidenceClass, null,
				false);
	}

	@Override
	public final Incidence getFirstIncidence(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext,
				anIncidenceClass.getM1Class(), direction, false);
	}

	@Override
	public final <T extends Incidence> T getFirstIncidence(
			Graph traversalContext, Class<T> anIncidenceClass,
			Direction direction) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext, anIncidenceClass, direction,
				false);
	}

	@Override
	public final Incidence getFirstIncidence(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext,
				anIncidenceClass.getM1Class(), null, noSubclasses);
	}

	@Override
	public final <T extends Incidence> T getFirstIncidence(
			Graph traversalContext, Class<T> anIncidenceClass,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext, anIncidenceClass, null,
				noSubclasses);
	}

	@Override
	public final Incidence getFirstIncidence(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext,
				anIncidenceClass.getM1Class(), direction, noSubclasses);
	}

	@Override
	public final int getDegree(IncidenceClass ic) {
		assert ic != null;
		assert isValid();
		return getDegree(graph.getTraversalContext(), ic, false);
	}

	@Override
	public final int getDegree(Class<? extends Incidence> ic) {
		assert ic != null;
		assert isValid();
		return getDegree(graph.getTraversalContext(), ic, false);
	}

	@Override
	public final int getDegree(IncidenceClass ic, Direction direction) {
		assert ic != null;
		assert isValid();
		return getDegree(graph.getTraversalContext(), ic, direction, false);
	}

	@Override
	public final int getDegree(Class<? extends Incidence> ic,
			Direction direction) {
		assert ic != null;
		assert isValid();
		return getDegree(graph.getTraversalContext(), ic, direction, false);
	}

	@Override
	public final int getDegree(Graph traversalContext, IncidenceClass ic) {
		assert ic != null;
		assert isValid();
		return getDegree(ic, false);
	}

	@Override
	public final int getDegree(Graph traversalContext,
			Class<? extends Incidence> ic) {
		assert ic != null;
		assert isValid();
		return getDegree(traversalContext, ic, false);
	}

	@Override
	public final int getDegree(Graph traversalContext, IncidenceClass ic,
			Direction direction) {
		assert ic != null;
		assert isValid();
		return getDegree(traversalContext, ic, direction, false);
	}

	@Override
	public final int getDegree(Graph traversalContext,
			Class<? extends Incidence> ic, Direction direction) {
		assert ic != null;
		assert isValid();
		return getDegree(traversalContext, ic, direction, false);
	}

	/**
	 * Removes <code>moved</code> from the sequence of {@link Incidence}s at
	 * this {@link GraphElement} and puts it after <code>target</code>.
	 * 
	 * @param target
	 *            {@link IncidenceImpl} after which <code>moved</code> should be
	 *            put
	 * @param moved
	 *            {@link IncidenceImpl} which should be moved to a new position @
	 */
	protected abstract void putIncidenceAfter(IncidenceImpl target,
			IncidenceImpl moved);

	/**
	 * Removes <code>moved</code> from the sequence of {@link Incidence}s at
	 * this {@link GraphElement} and puts it before <code>target</code>.
	 * 
	 * @param target
	 *            {@link IncidenceImpl} before which <code>moved</code> should
	 *            be put
	 * @param moved
	 *            {@link IncidenceImpl} which should be moved to a new position @
	 */
	protected abstract void putIncidenceBefore(IncidenceImpl target,
			IncidenceImpl moved);

	/**
	 * Appends <code>i</code> to the sequence of {@link Incidence}s at this
	 * {@link GraphElement}. {@link IncidenceImpl#nextIncidenceAtVertex} will be
	 * set to <code>null</code>.
	 * 
	 * @param i
	 *            {@link IncidenceImpl} @
	 */
	protected abstract void appendIncidenceToLambdaSeq(IncidenceImpl i);

	/**
	 * Removes <code>i</code> from the sequence of {@link Incidence}s at this
	 * {@link GraphElement}. <code>i</code> must be part of the sequence.
	 * 
	 * @param i
	 *            {@link IncidenceImpl} @
	 */
	protected abstract void removeIncidenceFromLambdaSeq(IncidenceImpl i);

	/**
	 * Sets the first {@link Incidence} of this {@link GraphElement} to
	 * <code>firstIncidence</code>.
	 * 
	 * @param firstIncidence
	 *            {@link IncidenceImpl}
	 */
	public abstract void setFirstIncidence(IncidenceImpl firstIncidence);

	/**
	 * Sets the last {@link Incidence} of this {@link GraphElement} to
	 * <code>lastIncidence</code>.
	 * 
	 * @param lastIncidence
	 *            {@link IncidenceImpl}
	 */
	public abstract void setLastIncidence(IncidenceImpl lastIncidence);

	/**
	 * Sorts the incidence sequence according to the given {@link Comparator} in
	 * ascending order.
	 * 
	 * @param comp
	 *            {@link Comparator} that defines the desired {@link Incidence}
	 *            order. @
	 */
	public abstract void sortIncidences(Comparator<Incidence> comp);

	@Override
	public final void addSubordinateElement(Vertex appendix) {
		// TODO: Das gefällt mir noch nicht, dass hier schon der Graph gebaut
		// wird
//		if (getSubordinateGraph().getLastVertex() != null) {
//			appendix.putAfter(getSubordinateGraph().getLastVertex());
//		} else {
//			// TODO: In this case it is also necessary to set first and last of
//			// subordinate graph
//			addFirstSubordinateVertex(appendix);
//		}
//		((GraphElementImpl<?, ?, ?>) appendix).setAllKappas(getKappa() - 1);
//		((GraphElementImpl<?, ?, ?>) appendix).setSigma(this);
	}

	/**
	 * If this is a {@link Vertex} <code>appendix</code> is put behind this in
	 * the sequence of vertices. Otherwise nothing is done.
	 * 
	 * @param appendix
	 *            {@link Vertex} @
	 */
	protected abstract void addFirstSubordinateVertex(Vertex appendix);

	@Override
	public final void addSubordinateElement(Edge appendix) {
		if (getSubordinateGraph().getLastEdge() != null) {
			appendix.putAfter(getSubordinateGraph().getLastEdge());
		} else {
			addFirstSubordinateEdge(appendix);
		}
		((GraphElementImpl<?, ?, ?>) appendix).setAllKappas(getKappa() - 1);
		((GraphElementImpl<?, ?, ?>) appendix).setSigma(this);
	}

	/**
	 * If this is a {@link Edge} <code>appendix</code> is put behind this in the
	 * sequence of edges. Otherwise nothing is done.
	 * 
	 * @param appendix
	 *            {@link Edge}
	 */
	protected abstract void addFirstSubordinateEdge(Edge appendix);

	/**
	 * Sets {@link #sigma} to <code>parent</code>.
	 * 
	 * @param newSigma
	 *            {@link GraphElementImpl}
	 */
	public final void setSigma(GraphElement<?, ?, ?> newSigma) {
		assert newSigma != null;
		assert getType().getAllowedSigmaClasses().contains(newSigma.getType());
		this.sigmaId = (newSigma instanceof Vertex) ? newSigma.getGlobalId() : -newSigma.getGlobalId();
	}

	/**
	 * Sets {@link #kappa} only of this {@link GraphElement} to
	 * <code>kappa</code>.
	 * 
	 * @param kappa
	 *            <b>int</b>
	 */
	@Override
	public final void setKappa(int kappa) {
		assert getType().getAllowedMaxKappa() >= kappa
				&& getType().getAllowedMinKappa() <= kappa;
		this.kappa = kappa;
	}

	/**
	 * Sets the {@link #kappa} value to <code>kappa</code> and adapts the kappa
	 * values of all child {@link GraphElement}s of this {@link GraphElement}.
	 * 
	 * @param kappa
	 *            <b>int</b>
	 */
	private final void setAllKappas(int kappa) {
		int kappaDifference = getKappa() - kappa;
		setKappa(kappa);
		for (Vertex v : getSubordinateGraph().getVertices()) {
			((GraphElementImpl<?, ?, ?>) v).setKappa(v.getKappa()
					- kappaDifference);
		}
		for (Edge e : getSubordinateGraph().getEdges()) {
			((GraphElementImpl<?, ?, ?>) e).setKappa(e.getKappa()
					- kappaDifference);
		}
	}

	/**
	 * @return <code>true</code> if <code>{@link #subOrdinateGraph}==null</code>
	 */
	final boolean isSubordinateGraphObjectAlreadyCreated() {
		return subOrdinateGraphId != 0;
	}

	/**
	 * @param parent
	 *            {@link GraphElement}
	 * @return <code>true</code> if this GraphElement is a direct or indirect
	 *         child of <code>parent</code>.
	 */
	public final boolean isChildOf(GraphElement<?, ?, ?> parent) {
		if (getSigma() == null || getKappa() >= parent.getKappa()) {
			return false;
		} else if (getSigma() == parent) {
			return true;
		} else {
			return ((GraphElementImpl<?, ?, ?>) getSigma()).isChildOf(parent);
		}
	}

	@Override
	public final Graph getLocalGraph() {
		return getGraph();
	}

	@Override
	public final Graph getContainingGraph() {
		return graph;
	}

}
