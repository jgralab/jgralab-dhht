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

package de.uni_koblenz.jgralab;

import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;

/**
 * Aggregates vertices and edges.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface GraphElement extends AttributedElement {

	/**
	 * Returns the id of this {@link GraphElement}.
	 * 
	 * @return int the id of this {@link GraphElement}.
	 */
	public int getId();// old

	/**
	 * Returns <code>true</code> if this {@link GraphElement} is still present
	 * in the {@link Graph} (i.e. not deleted). This check is equivalent to
	 * <code>getGraph().containsVertex(this)</code> or
	 * <code>getGraph().containsEdge(this)</code>.
	 * 
	 * @return boolean
	 */
	public boolean isValid();// old

	/**
	 * Returns the graph containing this {@link GraphElement}.
	 * 
	 * @return {@link Graph} containing this {@link GraphElement}
	 */
	public Graph getGraph();

	/**
	 * Returns the first {@link Incidence} of this {@link GraphElement}. If such
	 * an {@link Incidence} does not exist, <code>null</code> is returned.
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence();

	/**
	 * Returns the first {@link Incidence} of this {@link GraphElement} with
	 * direction <code>direction</code>. If such an {@link Incidence} does not
	 * exist, <code>null</code> is returned.
	 * 
	 * @param direction
	 *            {@link Direction} of connected {@link Incidence}s. If set to
	 *            <code>null</code> {@link Incidence}s of both directions are
	 *            returned.
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence(Direction direction);

	/**
	 * Get the first {@link Incidence} which has one of the aggregation
	 * semantics given by <code>incidentTypes</code> at either this
	 * {@link Incidence} (<code>thisIncidence == true</code>) or that
	 * {@link Incidence} (<code>thisIncidence == false</code>). If there are no
	 * <code>incidentTypes</code> given, it simply returns the first
	 * {@link Incidence}. If such an {@link Incidence} does not exist,
	 * <code>null</code> is returned.<br/>
	 * <br/>
	 * For example, this returns the first {@link Incidence} to a parent
	 * {@link GraphElement} in the containment hierarchy.
	 * 
	 * <pre>
	 * ge.getFirstIncidence(true, IncidenceType.AGGREGATION, IncidenceType.COMPOSITION)
	 * </pre>
	 * 
	 * And this returns the first {@link Incidence} to a child
	 * {@link GraphElement} in the containment hierarchy.
	 * 
	 * <pre>
	 * v.getFirstIncidence(false, IncidenceType.AGGREGATION, IncidenceType.COMPOSITION)
	 * </pre>
	 * 
	 * @param thisIncidence
	 *            boolean if true, <code>incidentTypes</code> has to match the
	 *            {@link Incidence} at this {@link GraphElement}, else it has to
	 *            match the opposite {@link Incidence}
	 * @param incidentTypes
	 *            {@link IncidenceType}...
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence(boolean thisIncidence,
			IncidenceType... incidentTypes);

	/**
	 * Returns the first incidence in lambda-seq where the corresponding
	 * {@link IncidenceClass} is of class <code>anIncidenceClass</code>. If such
	 * an {@link Incidence} does not exist, <code>null</code> is returned.
	 * 
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} to search for
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass);

	/**
	 * Returns the first {@link Incidence} in lambda-seq where the corresponding
	 * {@link Incidence} is of class <code>anIncidenceClass</code>. If such an
	 * {@link Incidence} does not exist, <code>null</code> is returned.
	 * 
	 * @param anIncidenceClass
	 *            {@link Class} the {@link Incidence} class to search for
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence(
			Class<? extends Incidence> anIncidenceClass);

	/**
	 * Returns the first {@link Incidence} in lambda-seq where the corresponding
	 * {@link Incidence} is of class <code>anIncidenceClass</code> and the
	 * direction is <code>direction</code>. If such an {@link Incidence} does
	 * not exist, <code>null</code> is returned.
	 * 
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} to search for
	 * @param direction
	 *            {@link Direction} of the {@link Incidence}s. If set to
	 *            <code>null</code> {@link Incidence}s of both directions are
	 *            returned.
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			Direction direction);

	/**
	 * Returns the first {@link Incidence} in lambda-seq where the corresponding
	 * {@link Incidence} is of class <code>anIncidenceClass</code> and the
	 * direction is <code>direction</code>. If such an {@link Incidence} does
	 * not exist, <code>null</code> is returned.
	 * 
	 * @param anIncidenceClass
	 *            {@link Class} to search for
	 * @param direction
	 *            {@link Direction} of the {@link Incidence}s. If set to
	 *            <code>null</code> {@link Incidence}s of both directions are
	 *            returned.
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence(
			Class<? extends Incidence> anIncidenceClass, Direction direction);

	/**
	 * Returns the first {@link Incidence} in lambda-seq where the corresponding
	 * {@link Incidence} is of class <code>anIncidenceClass</code>. If
	 * <code>noSubclasses</code> is set to <code>true</code> the
	 * {@link Incidence} must not be an instance of a subclass of
	 * <code>anIncidenceClass</code>. If such an {@link Incidence} does not
	 * exist, <code>null</code> is returned.
	 * 
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} to search for
	 * @param noSubclasses
	 *            boolean
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			boolean noSubclasses);

	/**
	 * Returns the first {@link Incidence} in lambda-seq where the corresponding
	 * {@link Incidence} is of class <code>anIncidenceClass</code>. If
	 * <code>noSubclasses</code> is set to <code>true</code> the
	 * {@link Incidence} must not be an instance of a subclass of
	 * <code>anIncidenceClass</code>. If such an {@link Incidence} does not
	 * exist, <code>null</code> is returned.
	 * 
	 * @param anIncidenceClass
	 *            {@link Class} to search for
	 * @param noSubclasses
	 *            boolean
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses);

	/**
	 * Returns the first {@link Incidence} in lambda-seq where the corresponding
	 * {@link Incidence} is of class <code>anIncidenceClass</code>. If
	 * <code>noSubclasses</code> is set to <code>true</code> the
	 * {@link Incidence} must not be an instance of a subclass of
	 * <code>anIncidenceClass</code>. The {@link Incidence} must have the
	 * direction <code>direction</code>. If such an {@link Incidence} does not
	 * exist, <code>null</code> is returned.
	 * 
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} to search for
	 * @param direction
	 *            {@link Direction} of the {@link Incidence}s. If set to
	 *            <code>null</code> {@link Incidence}s of both directions are
	 *            returned.
	 * @param noSubclasses
	 *            boolean
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses);

	/**
	 * Returns the first {@link Incidence} in lambda-seq where the corresponding
	 * {@link Incidence} is of class <code>anIncidenceClass</code>. If
	 * <code>noSubclasses</code> is set to <code>true</code> the
	 * {@link Incidence} must not be an instance of a subclass of
	 * <code>anIncidenceClass</code>. The {@link Incidence} must have the
	 * direction <code>direction</code>. If such an {@link Incidence} does not
	 * exist, <code>null</code> is returned.
	 * 
	 * @param anIncidenceClass
	 *            {@link Class} to search for
	 * @param direction
	 *            {@link Direction} of the {@link Incidence}s. If set to
	 *            <code>null</code> {@link Incidence}s of both directions are
	 *            returned.
	 * @param noSubclasses
	 *            boolean
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence(
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses);

	/**
	 * Returns the last {@link Incidence} of this {@link GraphElement}.
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getLastIncidence();

	/**
	 * Returns an {@link Iterable} over all {@link Incidence}s at this
	 * {@link GraphElement}.
	 * 
	 * @return {@link Iterable}&lt;{@link Incidence}&gt;
	 */
	public Iterable<Incidence> getIncidences();

	/**
	 * Returns an {@link Iterable} over all {@link Incidence}s at this
	 * {@link GraphElement} which have the direction specified by
	 * <code>direction</code>.
	 * 
	 * @param direction
	 *            {@link Direction} specifies the direction of the requested
	 *            {@link Incidence}s. If set to <code>null</code>
	 *            {@link Incidence}s of both directions are returned.
	 * @return {@link Iterable}&lt;{@link Incidence}&gt;
	 */
	public Iterable<Incidence> getIncidences(Direction direction);

	/**
	 * Returns an {@link Iterable} over all {@link Incidence}s at this
	 * {@link GraphElement} which is an instance of
	 * <code>anIncidenceClass</code>.
	 * 
	 * @param anIncidenceClass
	 *            {@link Class}
	 * @return {@link Iterable}&lt;{@link Incidence}&gt;
	 */
	public Iterable<Incidence> getIncidences(
			Class<? extends Incidence> anIncidenceClass);

	/**
	 * Returns an {@link Iterable} over all {@link Incidence}s at this
	 * {@link GraphElement} which is an instance of
	 * <code>anIncidenceClass</code>.
	 * 
	 * @param anIncidenceClass
	 *            {@link IncidenceClass}
	 * @return {@link Iterable}&lt;{@link Incidence}&gt;
	 */
	public Iterable<Incidence> getIncidences(IncidenceClass anIncidenceClass);

	/**
	 * Returns an {@link Iterable} over all {@link Incidence}s at this
	 * {@link GraphElement} which have the direction specified by
	 * <code>direction</code> and is an instance of
	 * <code>anIncidenceClass</code>.
	 * 
	 * @param anIncidenceClass
	 *            {@link Class}
	 * @param direction
	 *            {@link Direction} specifies the direction of the requested
	 *            {@link Incidence}s. If set to <code>null</code>
	 *            {@link Incidence}s of both directions are returned.
	 * @return {@link Iterable}&lt;{@link Incidence}&gt;
	 */
	public Iterable<Incidence> getIncidences(
			Class<? extends Incidence> anIncidenceClass, Direction direction);

	/**
	 * Returns an {@link Iterable} over all {@link Incidence}s at this
	 * {@link GraphElement} which have the direction specified by
	 * <code>direction</code> and is an instance of
	 * <code>anIncidenceClass</code>.
	 * 
	 * @param anIncidenceClass
	 *            {@link Class}
	 * @param direction
	 *            {@link Direction} specifies the direction of the requested
	 *            {@link Incidence}s. If set to <code>null</code>
	 *            {@link Incidence}s of both directions are returned.
	 * @return {@link Iterable}&lt;{@link Incidence}&gt;
	 */
	public Iterable<Incidence> getIncidences(IncidenceClass anIncidenceClass,
			Direction direction);

	/**
	 * Returns the number of connected {@link Incidence}s to this
	 * {@link GraphElement} .
	 * 
	 * @return int
	 */
	public int getDegree();

	/**
	 * Returns the number of connected {@link Incidence}s to this
	 * {@link GraphElement} which have the {@link Direction} specified by
	 * <code>direction</code>.
	 * 
	 * @param direction
	 *            {@link Direction}
	 * @return int number of {@link Incidence}s of desired direction which are
	 *         connected to this GraphElement
	 */
	public int getDegree(Direction direction);

	/**
	 * Returns the number of connected {@link Incidence}s to this
	 * {@link GraphElement} which are a subclass of <code>ic</code>.
	 * 
	 * @param ic
	 *            {@link IncidenceClass}
	 * @return int number of {@link Incidence}s which are an instance of
	 *         <code>ic</code>
	 */
	public int getDegree(IncidenceClass ic);

	/**
	 * Returns the number of connected {@link Incidence}s to this
	 * {@link GraphElement} which are a subclass of <code>ic</code>.
	 * 
	 * @param ic
	 *            {@link Class}
	 * @return int number of {@link Incidence}s which are an instance of
	 *         <code>ic</code>
	 */
	public int getDegree(Class<? extends Incidence> ic);

	/**
	 * Returns the number of connected {@link Incidence}s to this
	 * {@link GraphElement} which are a subclass of <code>ic</code>.
	 * 
	 * @param ic
	 *            {@link IncidenceClass}
	 * @param noSubClasses
	 *            boolean if set to <code>true</code>, subclasses of
	 *            <code>ic</code> are not counted
	 * @return int number of {@link Incidence}s which are an instance of
	 *         <code>ic</code>
	 */
	public int getDegree(IncidenceClass ic, boolean noSubClasses);

	/**
	 * Returns the number of connected {@link Incidence}s to this
	 * {@link GraphElement} which are a subclass of <code>ic</code>.
	 * 
	 * @param ic
	 *            {@link Class}
	 * @param noSubClasses
	 *            boolean if set to <code>true</code>, subclasses of
	 *            <code>ic</code> are not counted
	 * @return int number of {@link Incidence}s which are an instance of
	 *         <code>ic</code>
	 */
	public int getDegree(Class<? extends Incidence> ic, boolean noSubClasses);

	/**
	 * Returns the number of connected {@link Incidence}s to this
	 * {@link GraphElement} which have the {@link Direction} specified by
	 * <code>direction</code> and are a subclass of <code>ic</code>.
	 * 
	 * @param ic
	 *            {@link IncidenceClass}
	 * @param direction
	 *            {@link Direction}
	 * @return int number of {@link Incidence}s of desired direction which are
	 *         an instance of <code>ic</code>
	 */
	public int getDegree(IncidenceClass ic, Direction direction);

	/**
	 * Returns the number of connected {@link Incidence}s to this
	 * {@link GraphElement} which have the {@link Direction} specified by
	 * <code>direction</code> and are a subclass of <code>ic</code>.
	 * 
	 * @param ic
	 *            {@link Class}
	 * @param direction
	 *            {@link Direction}
	 * @return int number of {@link Incidence}s of desired direction which are
	 *         an instance of <code>ic</code>
	 */
	public int getDegree(Class<? extends Incidence> ic, Direction direction);

	/**
	 * Returns the number of connected {@link Incidence}s to this
	 * {@link GraphElement} which have the {@link Direction} specified by
	 * <code>direction</code> and are a subclass of <code>ic</code>.
	 * 
	 * @param ic
	 *            {@link IncidenceClass}
	 * @param direction
	 *            {@link Direction}
	 * @param noSubClasses
	 *            boolean if set to <code>true</code>, subclasses of
	 *            <code>ic</code> are not counted
	 * @return int number of {@link Incidence}s of desired direction which are
	 *         an instance of <code>ic</code>
	 */
	public int getDegree(IncidenceClass ic, Direction direction,
			boolean noSubClasses);

	/**
	 * Returns the number of connected {@link Incidence}s to this
	 * {@link GraphElement} which have the {@link Direction} specified by
	 * <code>direction</code> and are a subclass of <code>ic</code>.
	 * 
	 * @param ic
	 *            {@link Class}
	 * @param direction
	 *            {@link Direction}
	 * @param noSubClasses
	 *            boolean if set to <code>true</code>, subclasses of
	 *            <code>ic</code> are not counted
	 * @return int number of {@link Incidence}s of desired direction which are
	 *         an instance of <code>ic</code>
	 */
	public int getDegree(Class<? extends Incidence> ic, Direction direction,
			boolean noSubClasses);
}
