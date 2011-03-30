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

package de.uni_koblenz.jgralab.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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
public abstract class GraphElementImpl<OwnTypeClass extends GraphElementClass<OwnTypeClass, OwnType>, OwnType extends GraphElement<OwnTypeClass, OwnType, DualType>, DualType extends GraphElement<?, DualType, OwnType>>
		extends UnicastRemoteObject implements
		GraphElement<OwnTypeClass, OwnType, DualType> {

	/**
	 * Generated Serual Version UID
	 */
	private static final long serialVersionUID = 1245169302974416890L;

	/**
	 * The id of this {@link GraphElement}.
	 */
	protected int id;

	/**
	 * Holds the version of the {@link Incidence} structure, for every
	 * modification of the structure (e.g. adding or deleting an
	 * {@link Incidence} or changing the incidence sequence) this version number
	 * is increased by one. It is set to 0 when the {@link GraphElement} is
	 * created or the graph is loaded.
	 */
	private long incidenceListVersion = 0;

	/**
	 * The kappa value, which represents the highest level in which this
	 * {@link GraphElement} is visible.
	 */
	private int kappa = DEFAULT_KAPPA_VALUE;

	// TODO determine default value
	private static final int DEFAULT_KAPPA_VALUE = Integer.MAX_VALUE;

	private GraphElementImpl<?, ?, ?> parent;

	/**
	 * The {@link Graph} to which this {@link GraphElement} belongs.
	 */
	protected CompleteGraphImpl graph;

	protected Graph subOrdinateGraph;

	/**
	 * Creates a new {@link GraphElement} which belongs to <code>graph</code>.
	 * 
	 * @param graph
	 *            {@link Graph}
	 */
	protected GraphElementImpl(Graph graph) throws RemoteException {
		assert graph != null;
		this.graph = (CompleteGraphImpl) graph;
	}

	@Override
	public GraphElement<?, ?, ?> getParent() {
		return parent;
	}

	@Override
	public int getKappa() {
		return kappa;
	}

	@Override
	public boolean isVisible(int kappa) {
		return this.kappa >= kappa;
	}

	@Override
	public boolean containsElement(GraphElement<?, ?, ?> element) {
		for (GraphElement<?, ?, ?> el = element; el.getParent() != null
				&& getKappa() > el.getKappa(); el = el.getParent()) {
			if (el.getParent() == this) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	@Override
	public GraphClass getGraphClass() {
		return graph.getType();
	}

	@Override
	public Schema getSchema() {
		return graph.getSchema();
	}

	/**
	 * Changes the graph version of the graph this element belongs to. Should be
	 * called whenever the graph is changed, all changes like adding, creating
	 * and reordering of edges and vertices or changes of attributes of the
	 * graph, an edge or a vertex are treated as a change.
	 */
	public void graphModified() {
		graph.graphModified();
	}

	@Override
	public int getId() {
		return id;
	}

	/**
	 * Sets {@link GraphElement#id}.
	 * 
	 * @param id
	 *            int an id
	 */
	protected abstract void setId(int id);

	@Override
	public void initializeAttributesWithDefaultValues() {
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

	protected void internalSetDefaultValue(Attribute attr)
			throws GraphIOException {
		attr.setDefaultValue(this);
	}

	/**
	 * Must be called by all methods which manipulate the incidence list of this
	 * {@link GraphElement}.
	 */
	public void incidenceListModified() {
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
	boolean isIncidenceListModified(long incidenceListVersion) {
		assert isValid();
		return (this.getIncidenceListVersion() != incidenceListVersion);
	}

	/**
	 * Sets {@link #incidenceListVersion} to <code>incidentListVersion</code>.
	 * 
	 * @param incidenceListVersion
	 *            long
	 */
	protected void setIncidenceListVersion(long incidenceListVersion) {
		this.incidenceListVersion = incidenceListVersion;
	}

	/**
	 * @return long the internal incidence list version
	 * @see #isIncidenceListModified(long)
	 */
	long getIncidenceListVersion() {
		assert isValid();
		return incidenceListVersion;
	}

	@Override
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graph.getTraversalContext(),
				anIncidenceClass.getM1Class(), null, false);
	}

	@Override
	public <T extends Incidence> T getFirstIncidence(Class<T> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graph.getTraversalContext(), anIncidenceClass,
				null, false);
	}

	@Override
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			Direction direction) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graph.getTraversalContext(),
				anIncidenceClass.getM1Class(), direction, false);
	}

	@Override
	public <T extends Incidence> T getFirstIncidence(Class<T> anIncidenceClass,
			Direction direction) {
		return getFirstIncidence(graph.getTraversalContext(), anIncidenceClass,
				direction, false);
	}

	@Override
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graph.getTraversalContext(),
				anIncidenceClass.getM1Class(), null, noSubclasses);
	}

	@Override
	public <T extends Incidence> T getFirstIncidence(Class<T> anIncidenceClass,
			boolean noSubclasses) {
		return getFirstIncidence(graph.getTraversalContext(), anIncidenceClass,
				null, noSubclasses);
	}

	@Override
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graph.getTraversalContext(),
				anIncidenceClass.getM1Class(), direction, noSubclasses);
	}

	@Override
	public Incidence getFirstIncidence(Graph traversalContext,
			IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext,
				anIncidenceClass.getM1Class(), null, false);
	}

	@Override
	public <T extends Incidence> T getFirstIncidence(Graph traversalContext,
			Class<T> anIncidenceClass) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext, anIncidenceClass, null,
				false);
	}

	@Override
	public Incidence getFirstIncidence(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext,
				anIncidenceClass.getM1Class(), direction, false);
	}

	@Override
	public <T extends Incidence> T getFirstIncidence(Graph traversalContext,
			Class<T> anIncidenceClass, Direction direction) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext, anIncidenceClass, direction,
				false);
	}

	@Override
	public Incidence getFirstIncidence(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext,
				anIncidenceClass.getM1Class(), null, noSubclasses);
	}

	@Override
	public <T extends Incidence> T getFirstIncidence(Graph traversalContext,
			Class<T> anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext, anIncidenceClass, null,
				noSubclasses);
	}

	@Override
	public Incidence getFirstIncidence(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext,
				anIncidenceClass.getM1Class(), direction, noSubclasses);
	}

	@Override
	public int getDegree(IncidenceClass ic) {
		assert ic != null;
		assert isValid();
		return getDegree(graph.getTraversalContext(), ic, false);
	}

	@Override
	public int getDegree(Class<? extends Incidence> ic) {
		assert ic != null;
		assert isValid();
		return getDegree(graph.getTraversalContext(), ic, false);
	}

	@Override
	public int getDegree(IncidenceClass ic, Direction direction) {
		assert ic != null;
		assert isValid();
		return getDegree(graph.getTraversalContext(), ic, direction, false);
	}

	@Override
	public int getDegree(Class<? extends Incidence> ic, Direction direction) {
		assert ic != null;
		assert isValid();
		return getDegree(graph.getTraversalContext(), ic, direction, false);
	}

	@Override
	public int getDegree(Graph traversalContext, IncidenceClass ic) {
		assert ic != null;
		assert isValid();
		return getDegree(ic, false);
	}

	@Override
	public int getDegree(Graph traversalContext, Class<? extends Incidence> ic) {
		assert ic != null;
		assert isValid();
		return getDegree(traversalContext, ic, false);
	}

	@Override
	public int getDegree(Graph traversalContext, IncidenceClass ic,
			Direction direction) {
		assert ic != null;
		assert isValid();
		return getDegree(traversalContext, ic, direction, false);
	}

	@Override
	public int getDegree(Graph traversalContext, Class<? extends Incidence> ic,
			Direction direction) {
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
	 *            {@link IncidenceImpl} which should be moved to a new position
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
	 *            {@link IncidenceImpl} which should be moved to a new position
	 */
	protected abstract void putIncidenceBefore(IncidenceImpl target,
			IncidenceImpl moved);

	/**
	 * Appends <code>i</code> to the sequence of {@link Incidence}s at this
	 * {@link GraphElement}. {@link IncidenceImpl#nextIncidenceAtVertex} will be
	 * set to <code>null</code>.
	 * 
	 * @param i
	 *            {@link IncidenceImpl}
	 */
	protected abstract void appendIncidenceToLambdaSeq(IncidenceImpl i);

	/**
	 * Removes <code>i</code> from the sequence of {@link Incidence}s at this
	 * {@link GraphElement}. <code>i</code> must be part of the sequence.
	 * 
	 * @param i
	 *            {@link IncidenceImpl}
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
	 *            order.
	 */
	public abstract void sortIncidences(Comparator<Incidence> comp);

	@Override
	public void addSubordinateElement(Vertex appendix) {
		// TODO use moveto.....
		appendix.putAfter(getSubordinateGraph().getLastVertex());
		((GraphElementImpl<?, ?, ?>) appendix).setAllKappas(getKappa() - 1);
		((GraphElementImpl<?, ?, ?>) appendix).setParent(this);
	}

	@Override
	public void addSubordinateElement(Edge appendix) {
		// TODO use moveto.....
		appendix.putAfter(getSubordinateGraph().getLastEdge());
		((GraphElementImpl<?, ?, ?>) appendix).setAllKappas(getKappa() - 1);
		((GraphElementImpl<?, ?, ?>) appendix).setParent(this);
	}

	/**
	 * Sets {@link #parent} to <code>parent</code>.
	 * 
	 * @param parent
	 *            {@link GraphElementImpl}
	 */
	private void setParent(GraphElementImpl<?, ?, ?> parent) {
		assert parent != null;
		assert getType().getAllowedSigmaClasses().contains(parent.getType());
		this.parent = parent;
	}

	/**
	 * Sets {@link #kappa} only of this {@link GraphElement} to
	 * <code>kappa</code>.
	 * 
	 * @param kappa
	 *            <b>int</b>
	 */
	public void setKappa(int kappa) {
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
	private void setAllKappas(int kappa) {
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
	boolean isSubordinateGraphObjectAlreadyCreated() {
		return subOrdinateGraph != null;
	}

	/**
	 * @param parent
	 *            {@link GraphElement}
	 * @return <code>true</code> if this GraphElement is a direct or indirect
	 *         child of <code>parent</code>.
	 */
	public boolean isChildOf(GraphElement<?, ?, ?> parent) {
		if (getParent() == null || getKappa() >= parent.getKappa()) {
			return false;
		} else if (getParent() == parent) {
			return true;
		} else {
			return ((GraphElementImpl<?, ?, ?>) getParent()).isChildOf(parent);
		}
	}

	@Override
	public int getGlobalId() {
		return GraphBaseImpl.getGlobalId(getLocalGraph().getId(), id);
	}

	@Override
	public Graph getLocalGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph getContainingGraph() {
		// TODO Auto-generated method stub
		return null;
	}

}
