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

package de.uni_koblenz.jgralab.impl.diskv2;

import java.util.Comparator;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.RemoteStorageAccess;
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


public abstract class GraphElementImpl
<OwnTypeClass extends GraphElementClass<OwnTypeClass, OwnType, DualTypeClass, DualType>, 
OwnType extends GraphElement<OwnTypeClass,OwnType,DualTypeClass,DualType>,
DualTypeClass extends GraphElementClass<DualTypeClass, DualType, OwnTypeClass, OwnType>,
DualType extends GraphElement<DualTypeClass, DualType, OwnTypeClass, OwnType>>

implements GraphElement<OwnTypeClass, OwnType, DualTypeClass, DualType> {

	/**
	 * The id of this {@link GraphElement}.
	 */
	protected long id;
	
	/**
	 * Get the Tracker for this GraphElement
	 * 
	 * @return The Tracker that tracks this GraphElement
	 */
	public abstract GraphElementTracker getTracker();
	
	/**
	 * Called whenever a primitive attribute of this GraphElement changed so the
	 * new attribute value is stored in the Tracker.
	 */
	public void attributeChanged() {
		GraphElementTracker tracker = getTracker();
		if (tracker != null)
			tracker.storeAttributes(this);
	}
	
	/**
	 * Called whenever a String of this GraphElement changed so the
	 * new String is stored in the Tracker.
	 */
	public void stringChanged() {
		GraphElementTracker tracker = getTracker();
		if (tracker != null)
			tracker.storeStrings(this);
	}
	
	/**
	 * Called whenever a List of this GraphElement changed so the
	 * new list value is stored in the Tracker.
	 */
	public void listChanged() {
		GraphElementTracker tracker = getTracker();
		if (tracker != null)
			tracker.storeLists(this);
	}

	/**
	 * Holds the version of the {@link Incidence} structure, for every
	 * modification of the structure (e.g. adding or deleting an
	 * {@link Incidence} or changing the incidence sequence) this version number
	 * is increased by one. It is set to 0 when the {@link GraphElement} is
	 * created or the graph is loaded.
	 */
	private long incidenceListVersion = 0;
	
	public void increaseIncidenceListVersion() {
		incidenceListVersion++;
		getTracker().putVariable(36, incidenceListVersion);
	}
	
	protected long firstIncidenceId;
	
	protected long lastIncidenceId;
	
	public void setFirstIncidenceId(long incidenceId) {
		this.firstIncidenceId = incidenceId;
		getTracker().putVariable(20, incidenceId);
	}
	
	public void restoreFirstIncidenceId(long incidenceId) {
		this.firstIncidenceId = incidenceId;
	}
	
	public long getFirstIncidenceId() {
		return firstIncidenceId;
	}
	
	public void setLastIncidenceId(long incidenceId) {
		this.lastIncidenceId = incidenceId;
		getTracker().putVariable(28, incidenceId);
	}
	
	public void restoreLastIncidenceId(long incidenceId) {
		this.lastIncidenceId = incidenceId;
	}
	
	public long getLastIncidenceId() {
		return lastIncidenceId;
	}

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
//	protected CompleteGraphImpl graph;
	

	protected GraphDatabaseBaseImpl graphDb;
	

	/**
	 * The subordinate graph nested in this graph 
	 */
	protected long subOrdinateGraphId;
	
	public long getSubOrdinateGraphId(){
		return subOrdinateGraphId;
	}
	
	public final void restoreSubOrdianteGraphId(long graphId){
		this.subOrdinateGraphId = graphId;
	}
	
	protected long nextElementId;
	
	public void setNextElementId(long nextElemId) {
		this.nextElementId = nextElemId;
		getTracker().putVariable(4, nextElementId);
	}
	
	public void restoreNextElementId(long nextElemId) {
		this.nextElementId = nextElemId;
	}
	
	public long getNextElementId() {
		return nextElementId;
	}
	
	protected long previousElementId;
	
	public void setPreviousElementId(long previousElemId) {
		this.previousElementId = previousElemId;
		getTracker().putVariable(12, previousElementId);
	}
	
	public void restorePreviousElementId(long previousElemId) {
		this.previousElementId = previousElemId;
	}
	
	public long getPreviousElementId() {
		return previousElementId;
	}
	
	private long sigmaId;

	
	
	/**
	 * Creates a new {@link GraphElement} which belongs to <code>graph</code>.
	 * 
	 * @param graph
	 *            {@link Graph}
	 */
	protected GraphElementImpl(long id, de.uni_koblenz.jgralab.impl.diskv2.GraphDatabaseBaseImpl graphDatabase) {
		this.graphDb = graphDatabase;
		this.id = id;
	}


	@SuppressWarnings("rawtypes")
	@Override
	public final GraphElement getSigma() {
		if (sigmaId > 0) {
			Vertex v = graphDb.getVertexObject(sigmaId);
			return v;
		} else {
			Edge e = graphDb.getEdgeObject(-sigmaId);
			return e;
		}
	}
	
	public long getSigmaId() {
		return sigmaId;
	}
	
	public void setSigmaId(long newSigmaId) {
		sigmaId = newSigmaId;
		getTracker().putVariable(44, newSigmaId);
	}
	
	public void restoreSigmaId(long newSigmaId) {
		sigmaId = newSigmaId;
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
	public abstract <T extends Incidence> T connect(IncidenceClass incidenceClass, DualType elemToConnect,
			int incidenceId);

	

	
	@SuppressWarnings("rawtypes")
	@Override
	public final boolean containsElement(GraphElement element) {
		for (GraphElement el = element; el.getSigma() != null
				&& getKappa() > el.getKappa(); el = el.getSigma()) {
			if (el.getSigma() == this) {
				return true;
			}
		}
		return false;
	}

	@Override
	public final Graph getGraph() {
		return graphDb.getTraversalContext();
	}

	@Override
	public final GraphClass getGraphClass() {
		return graphDb.getTraversalContext().getType();
	}

	@Override
	public final Schema getSchema() {
		return graphDb.getSchema();
	}

	/**
	 * Changes the graph version of the graph this element belongs to. Should be
	 * called whenever the graph is changed, all changes like adding, creating
	 * and reordering of edges and vertices or changes of attributes of the
	 * graph, an edge or a vertex are treated as a change.
	 */
	public final void graphModified() {
		graphDb.graphModified();
	}

	@Override
	public final long getGlobalId() {
		return id;
	}

	@Override
	public final int getLocalId() {
		return (int) id;
	}
	
	@Override
	public final int hashCode(){
		return getLocalId();
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
		getTracker().putVariable(36, incidenceListVersion);
	}
	
	protected final void restoreIncidenceListVersion(long incidenceListVersion) {
		this.incidenceListVersion = incidenceListVersion;
	}

	/**
	 * @return long the internal incidence list version
	 * @see #isIncidenceListModified(long)
	 */
	public final long getIncidenceListVersion() {
		//assert isValid();
		return incidenceListVersion;
	}

	@Override
	public final Incidence getFirstIncidence(IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graphDb.getTraversalContext(),
				anIncidenceClass.getM1Class(), false);
	}

	@Override
	public final <T extends Incidence> T getFirstIncidence(
			Class<T> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graphDb.getTraversalContext(), anIncidenceClass,
				 false);
	}


	@Override
	public final Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getFirstIncidence(graphDb.getTraversalContext(),
				anIncidenceClass.getM1Class(), noSubclasses);
	}

	@Override
	public final <T extends Incidence> T getFirstIncidence(
			Class<T> anIncidenceClass, boolean noSubclasses) {
		return getFirstIncidence(graphDb.getTraversalContext(), anIncidenceClass,
				noSubclasses);
	}

	@Override
	public final Incidence getFirstIncidence(Graph traversalContext,
			IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext,
				anIncidenceClass.getM1Class(), false);
	}

	@Override
	public final <T extends Incidence> T getFirstIncidence(
			Graph traversalContext, Class<T> anIncidenceClass) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(traversalContext, anIncidenceClass, false);
	}


	@Override
	public final int getDegree(IncidenceClass ic) {
		assert ic != null;
		assert isValid();
		return getDegree(graphDb.getTraversalContext(), ic, false);
	}

	@Override
	public final int getDegree(Class<? extends Incidence> ic) {
		assert ic != null;
		assert isValid();
		return getDegree(graphDb.getTraversalContext(), ic, false);
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

	@SuppressWarnings("rawtypes")
	@Override
	public final void addSubordinateElement(Edge appendix) {
		if (getSubordinateGraph().getLastEdge() != null) {
			appendix.putAfter(getSubordinateGraph().getLastEdge());
		} else {
			addFirstSubordinateEdge(appendix);
		}
		((GraphElementImpl) appendix).setAllKappas(getKappa() - 1);
		((GraphElementImpl) appendix).setSigma(this);
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
	@SuppressWarnings("rawtypes")
	public final void setSigma(GraphElement newSigma) {
		long newSigmaId;
		if (newSigma == null)
			newSigmaId = 0;
		else
			newSigmaId = (newSigma instanceof Vertex) ? newSigma.getGlobalId() : -newSigma.getGlobalId();
		setSigmaId(newSigmaId);
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
		getTracker().putKappa(kappa);
	}
	
	public final void restoreKappa(int kappa){
		this.kappa = kappa;
	}

	/**
	 * Sets the {@link #kappa} value to <code>kappa</code> and adapts the kappa
	 * values of all child {@link GraphElement}s of this {@link GraphElement}.
	 * 
	 * @param kappa
	 *            <b>int</b>
	 */
	final void setAllKappas(int kappa) {
		int kappaDifference = getKappa() - kappa;
		setKappa(kappa);
		for (Vertex v : getSubordinateGraph().getVertices()) {
			((GraphElementImpl<?, ?, ?,?>) v).setKappa(v.getKappa()
					- kappaDifference);
		}
		for (Edge e : getSubordinateGraph().getEdges()) {
			((GraphElementImpl<?, ?, ?,?>) e).setKappa(e.getKappa()
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
	@SuppressWarnings("rawtypes")
	public final boolean isChildOf(GraphElement parent) {
		if (getSigma() == null || getKappa() >= parent.getKappa()) {
			return false;
		} else if (getSigma() == parent) {
			return true;
		} else {
			return ((GraphElementImpl) getSigma()).isChildOf(parent);
		}
	}

	@Override
	public final Graph getLocalGraph() {
		return getGraph();
		//return graphDb.getGraphObject(graphDb.getLocalPartialGraphId());
	}

	@Override
	public final Graph getContainingGraph() {
//		if (sigmaId != 0) {
//			//nested in a subordinate graph
//			if (graphDb.getPartialGraphId(sigmaId) != graphDb.getPartialGraphId(id)) {
//				//the subordinate graph is either distributed itself or also a partial one 
//				return graphDb.getLocalGraph();
//			}
//		}
//		return graphDb.;
		return getGraph();
	}
	
	@Override
	public RemoteStorageAccess getStorage() {
		return graphDb.getLocalStorage();
	}

}
