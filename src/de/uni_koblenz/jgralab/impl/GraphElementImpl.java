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

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class GraphElementImpl implements GraphElement {

	protected int id;

	/**
	 * Holds the version of the {@link Incidence} structure, for every
	 * modification of the structure (e.g. adding or deleting an
	 * {@link Incidence} or changing the incidence sequence) this version number
	 * is increased by one. It is set to 0 when the {@link GraphElement} is
	 * created or the graph is loaded.
	 */
	private long incidenceListVersion = 0;

	protected GraphElementImpl(Graph graph) {
		assert graph != null;
		this.graph = (GraphBaseImpl) graph;
	}

	protected GraphBaseImpl graph;

	@Override
	public Graph getGraph() {
		return graph;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.AttributedElement#getGraphClass()
	 */
	public GraphClass getGraphClass() {
		return (GraphClass) graph.getAttributedElementClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jgralab.AttributedElement#getSchema()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.GraphElement#getId()
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * sets the id field of this graph element
	 * 
	 * @param id
	 *            an id
	 */
	protected abstract void setId(int id);

	@Override
	public void initializeAttributesWithDefaultValues() {
		for (Attribute attr : getAttributedElementClass().getAttributeList()) {
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

	/**
	 * 
	 * @param attr
	 * @throws GraphIOException
	 */
	protected void internalSetDefaultValue(Attribute attr)
			throws GraphIOException {
		attr.setDefaultValue(this);
	}

	/**
	 * Must be called by all methods which manipulate the incidence list of this
	 * GraphElement.
	 */
	protected void incidenceListModified() {
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
	public Incidence getFirstIncidence(Direction direction) {
		assert isValid();
		IncidenceBaseImpl i = (IncidenceBaseImpl) getFirstIncidence();
		switch (direction) {
		case EDGE_TO_VERTEX:
			while ((i != null) && i.getDirection() != Direction.EDGE_TO_VERTEX) {
				i = i.getNextIncidence(this);
			}
			return i;
		case VERTEX_TO_EDGE:
			while ((i != null) && i.getDirection() != Direction.VERTEX_TO_EDGE) {
				i = i.getNextIncidence(this);
			}
			return i;
		default:
			throw new RuntimeException("FIXME!");
		}
	}

	@Override
	public Incidence getFirstIncidence(boolean thisIncidence,
			IncidenceType... incidentTypes) {
		assert isValid();
		IncidenceBaseImpl i = (IncidenceBaseImpl) getFirstIncidence();
		if (incidentTypes.length == 0) {
			return i;
		}
		while (i != null) {
			for (IncidenceType element : incidentTypes) {
				if ((thisIncidence ? i.getThisSemantics() : i
						.getThatSemantics()) == element) {
					return i;
				}
			}
			i = i.getNextIncidence(this);
		}
		return null;
	}

	@Override
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(anIncidenceClass.getM1Class(), null, false);
	}

	@Override
	public Incidence getFirstIncidence(
			Class<? extends Incidence> anIncidenceClass) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(anIncidenceClass, null, false);
	}

	@Override
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			Direction direction) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(anIncidenceClass.getM1Class(), direction,
				false);
	}

	@Override
	public Incidence getFirstIncidence(
			Class<? extends Incidence> anIncidenceClass, Direction direction) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(anIncidenceClass, direction, false);
	}

	@Override
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(anIncidenceClass.getM1Class(), null,
				noSubclasses);
	}

	@Override
	public Incidence getFirstIncidence(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(anIncidenceClass, null, noSubclasses);
	}

	@Override
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		return getFirstIncidence(anIncidenceClass.getM1Class(), direction,
				noSubclasses);
	}

	@Override
	public Incidence getFirstIncidence(
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		assert isValid();
		IncidenceBaseImpl currentIncidence = (IncidenceBaseImpl) getFirstIncidence(direction);
		while (currentIncidence != null) {
			if (noSubclasses) {
				if (anIncidenceClass == currentIncidence.getM1Class()) {
					return currentIncidence;
				}
			} else {
				if (anIncidenceClass.isInstance(currentIncidence)) {
					return currentIncidence;
				}
			}
			currentIncidence = currentIncidence.getNextIncidence(this/*
																	 * TODO,
																	 * direction
																	 */);
		}
		return null;
	}

}
