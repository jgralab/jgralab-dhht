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
 * Represents the incidences between the edges and vertices in a graph.
 * 
 * @author ist@uni-koblenz.de
 */
public interface Incidence extends TypedElement<IncidenceClass, Incidence> {

	/**
	 * Returns the direction of this {@link Incidence}.
	 * 
	 * @return {@link Direction}
	 */
	public Direction getDirection();

	/**
	 * Returns the graph containing this {@link GraphElement}.
	 * 
	 * @return {@link Graph} containing this {@link GraphElement}
	 */
	public Graph getGraph();

	/**
	 * Returns the {@link Edge} to which this {@link Incidence} is connected.
	 * 
	 * @return {@link Edge}
	 */
	public Edge getEdge();

	/**
	 * Returns the {@link Vertex} to which this {@link Incidence} is connected.
	 * 
	 * @return {@link Vertex}
	 */
	public Vertex getVertex();

	/**
	 * Checks if this {@link Incidence} is visible at a kappa value of
	 * <code>kappa</code>.
	 * 
	 * @param kappa
	 *            int
	 * @return boolean <code>getEdge().isVisible(kappa)</code>
	 */
	public boolean isVisible(int kappa);

	/**
	 * Returns the next {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getEdge()}. If this
	 * {@link Incidence} is the last {@link Incidence} in the sequence,
	 * <code>null</code> is returned.
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge();

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(Direction direction);

	/**
	 * Gets the next {@link Incidence} at the current {@link Edge}, which has
	 * one of <code>incidenceTypes</code> aggregation semantics at this (
	 * <code>thisIncidence == true</code>) or that (
	 * <code>thisIncidence == false</code>) side.
	 * 
	 * If no <code>incidenceType</code> is given, it simply returns the first
	 * {@link Incidence} .
	 * 
	 * @see GraphElement#getFirstIncidence(boolean, IncidenceType...)
	 * 
	 * @param thisIncidence
	 *            if true, <code>incidenceTypes</code> has to match the
	 *            incidence at the current vertex, else it has to match the
	 *            incidence at the opposite vertex
	 * @param incidenceTypes
	 *            the acceptable incidence types
	 * @return {@link Incidence} the next incident edge at the current
	 *         {@link Edge}, which has one of <code>incidenceTypes</code>
	 *         aggregation semantics at this (
	 *         <code>thisIncidence == true</code>) or that (
	 *         <code>thisIncidence == false</code>) side.
	 */
	public Incidence getNextIncidenceAtEdge(boolean thisIncidence,
			IncidenceType... incidenceTypes);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			Direction direction);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, Direction direction);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses);

	/**
	 * Returns the next {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getEdge()}. If this
	 * {@link Incidence} is the last {@link Incidence} in the sequence,
	 * <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(Graph traversalContext);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Direction direction);

	/**
	 * Gets the next {@link Incidence} at the current {@link Edge}, which has
	 * one of <code>incidenceTypes</code> aggregation semantics at this (
	 * <code>thisIncidence == true</code>) or that (
	 * <code>thisIncidence == false</code>) side.
	 * 
	 * If no <code>incidenceType</code> is given, it simply returns the first
	 * {@link Incidence} .
	 * 
	 * @see GraphElement#getFirstIncidence(boolean, IncidenceType...)
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param thisIncidence
	 *            if true, <code>incidenceTypes</code> has to match the
	 *            incidence at the current vertex, else it has to match the
	 *            incidence at the opposite vertex
	 * @param incidenceTypes
	 *            the acceptable incidence types
	 * @return {@link Incidence} the next incident edge at the current
	 *         {@link Edge}, which has one of <code>incidenceTypes</code>
	 *         aggregation semantics at this (
	 *         <code>thisIncidence == true</code>) or that (
	 *         <code>thisIncidence == false</code>) side.
	 */
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			boolean thisIncidence, IncidenceType... incidenceTypes);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction,
			boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtEdge()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtEdge(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses);

	/**
	 * Returns the next {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getVertex()}. If this
	 * {@link Incidence} is the last {@link Incidence} in the sequence,
	 * <code>null</code> is returned.
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex();

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(Direction direction);

	/**
	 * Gets the next {@link Incidence} at the current {@link Vertex}, which has
	 * one of <code>incidenceTypes</code> aggregation semantics at this (
	 * <code>thisIncidence == true</code>) or that (
	 * <code>thisIncidence == false</code>) side.
	 * 
	 * If no <code>incidenceType</code> is given, it simply returns the first
	 * {@link Incidence} .
	 * 
	 * @see GraphElement#getFirstIncidence(boolean, IncidenceType...)
	 * 
	 * @param thisIncidence
	 *            if true, <code>incidenceTypes</code> has to match the
	 *            incidence at the current vertex, else it has to match the
	 *            incidence at the opposite vertex
	 * @param incidenceTypes
	 *            the acceptable incidence types
	 * @return {@link Incidence} the next incident edge at the current
	 *         {@link Vertex}, which has one of <code>incidenceTypes</code>
	 *         aggregation semantics at this (
	 *         <code>thisIncidence == true</code>) or that (
	 *         <code>thisIncidence == false</code>) side.
	 */
	public Incidence getNextIncidenceAtVertex(boolean thisIncidence,
			IncidenceType... incidenceTypes);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			Direction direction);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, Direction direction);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses);

	/**
	 * Returns the next {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getVertex()}. If this
	 * {@link Incidence} is the last {@link Incidence} in the sequence,
	 * <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(Graph traversalContext);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Direction direction);

	/**
	 * Gets the next {@link Incidence} at the current {@link Vertex}, which has
	 * one of <code>incidenceTypes</code> aggregation semantics at this (
	 * <code>thisIncidence == true</code>) or that (
	 * <code>thisIncidence == false</code>) side.
	 * 
	 * If no <code>incidenceType</code> is given, it simply returns the first
	 * {@link Incidence} .
	 * 
	 * @see GraphElement#getFirstIncidence(boolean, IncidenceType...)
	 * @param traversalContext
	 *            {@link Graph}
	 * @param thisIncidence
	 *            if true, <code>incidenceTypes</code> has to match the
	 *            incidence at the current vertex, else it has to match the
	 *            incidence at the opposite vertex
	 * @param incidenceTypes
	 *            the acceptable incidence types
	 * @return {@link Incidence} the next incident edge at the current
	 *         {@link Vertex}, which has one of <code>incidenceTypes</code>
	 *         aggregation semantics at this (
	 *         <code>thisIncidence == true</code>) or that (
	 *         <code>thisIncidence == false</code>) side.
	 */
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			boolean thisIncidence, IncidenceType... incidenceTypes);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass, boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link IncidenceClass} the next incidence should be an
	 *            instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			IncidenceClass anIncidenceClass, Direction direction,
			boolean noSubclasses);

	/**
	 * @see #getNextIncidenceAtVertex()
	 * @param traversalContext
	 *            {@link Graph}
	 * @param anIncidenceClass
	 *            {@link Class} the next incidence should be an instance of
	 * @param direction
	 *            {@link Direction} the direction the next incidence should
	 *            have.
	 * @param noSubclasses
	 *            boolean if <code>true</code> the next {@link Incidence} must
	 *            not be an instance of an subclass of
	 *            <code>anIncidenceClass</code>
	 * @return {@link Incidence}
	 */
	public Incidence getNextIncidenceAtVertex(Graph traversalContext,
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses);

	/**
	 * Returns the previous {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getEdge()}. If this
	 * {@link Incidence} is the first {@link Incidence} in the sequence,
	 * <code>null</code> is returned.
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getPreviousIncidenceAtEdge();

	/**
	 * Returns the previous {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getEdge()}. If this
	 * {@link Incidence} is the first {@link Incidence} in the sequence,
	 * <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Incidence}
	 */
	public Incidence getPreviousIncidenceAtEdge(Graph traversalContext);

	/**
	 * Returns the previous {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getVertex()}. If this
	 * {@link Incidence} is the first {@link Incidence} in the sequence,
	 * <code>null</code> is returned.
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getPreviousIncidenceAtVertex();

	/**
	 * Returns the previous {@link Incidence} in the sequence of all
	 * {@link Incidence}s connected to {@link Incidence#getVertex()}. If this
	 * {@link Incidence} is the first {@link Incidence} in the sequence,
	 * <code>null</code> is returned.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Incidence}
	 */
	public Incidence getPreviousIncidenceAtVertex(Graph traversalContext);

	/**
	 * Returns the sequence of all {@link Edge}s which are connected to
	 * {@link Incidence#getVertex()} via an {@link Incidence} of the same
	 * direction as this {@link Incidence} has.
	 * 
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getTheseEdges();

	/**
	 * Returns the sequence of all {@link Edge}s which are connected to
	 * {@link Incidence#getVertex()} via an {@link Incidence} of the same
	 * direction as this {@link Incidence} has.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getTheseEdges(Graph traversalContext);

	/**
	 * Returns the sequence of all {@link Edge}s which are connected to
	 * {@link Incidence#getVertex()} via an {@link Incidence} of the reverted
	 * direction as this {@link Incidence} has.
	 * 
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getThoseEdges();

	/**
	 * Returns the sequence of all {@link Edge}s which are connected to
	 * {@link Incidence#getVertex()} via an {@link Incidence} of the reverted
	 * direction as this {@link Incidence} has.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Iterable}&lt;{@link Edge}&gt;
	 */
	public Iterable<Edge> getThoseEdges(Graph traversalContext);

	/**
	 * Returns {@link Incidence#getVertex()} of a binary {@link Edge}.
	 * 
	 * @return {@link Vertex}
	 * @throws UnsupportedOpperationException
	 *             if {@link Incidence#getEdge()} is not binary.
	 */
	public Vertex getThis();

	/**
	 * Returns a sequence of all vertices, which are connected to
	 * {@link Incidence#getEdge()} via an {@link Incidence} of the same
	 * direction as this one.
	 * 
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getTheseVertices();

	/**
	 * Returns a sequence of all vertices, which are connected to
	 * {@link Incidence#getEdge()} via an {@link Incidence} of the same
	 * direction as this one.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getTheseVertices(Graph traversalContext);

	/**
	 * Returns the {@link Vertex} which is at the other end of a binary
	 * {@link Edge}.
	 * 
	 * @return {@link Vertex}
	 * @throws UnsupportedOpperationException
	 *             if {@link Incidence#getEdge()} is not binary.
	 */
	public Vertex getThat();

	/**
	 * Returns a sequence of all vertices, which are connected to
	 * {@link Incidence#getEdge()} via an {@link Incidence} of the reverted
	 * direction as this one.
	 * 
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getThoseVertices();

	/**
	 * Returns a sequence of all vertices, which are connected to
	 * {@link Incidence#getEdge()} via an {@link Incidence} of the reverted
	 * direction as this one.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @return {@link Iterable}&lt;{@link Vertex}&gt;
	 */
	public Iterable<Vertex> getThoseVertices(Graph traversalContext);

	/**
	 * Returns the m1-class of this {@link Incidence}.
	 * 
	 * @return {@link Class}
	 */
	public Class<? extends Incidence> getM1Class();

	/**
	 * The semanctics of this {@link Incidence}, e.g. {@link IncidenceType#EDGE}
	 * , {@link IncidenceType#AGGREGATION} or {@link IncidenceType#COMPOSITION}.
	 * 
	 * @return {@link IncidenceType}
	 */
	public IncidenceType getThisSemantics();

	/**
	 * The semanctics of that {@link Incidence}, e.g. {@link IncidenceType#EDGE}
	 * , {@link IncidenceType#AGGREGATION} or {@link IncidenceType#COMPOSITION}.
	 * 
	 * @return {@link IncidenceType}
	 */
	public IncidenceType getThatSemantics();

	/**
	 * Returns <code>true</code> if this {@link Incidence} is before
	 * <code>i</code> in the sequence of {@link Incidence}s of
	 * {@link #getVertex()}.
	 * 
	 * @param i
	 *            {@link Incidence}
	 * @return boolean
	 */
	public boolean isBeforeAtVertex(Incidence i);

	/**
	 * Puts this {@link Incidence} immediately before <code>i</code> in the
	 * sequence of {@link Incidence}s of {@link #getVertex()}.
	 * 
	 * @param i
	 *            {@link Incidence}
	 */
	public void putBeforeAtVertex(Incidence i);

	/**
	 * Returns <code>true</code> if this {@link Incidence} is after
	 * <code>i</code> in the sequence of {@link Incidence}s of
	 * {@link #getVertex()}.
	 * 
	 * @param i
	 *            {@link Incidence}
	 * @return boolean
	 */
	public boolean isAfterAtVertex(Incidence i);

	/**
	 * Puts this {@link Incidence} immediately after <code>i</code> in the
	 * sequence of {@link Incidence}s of {@link #getVertex()}.
	 * 
	 * @param i
	 *            {@link Incidence}
	 */
	public void putAfterAtVertex(Incidence i);

	/**
	 * Returns <code>true</code> if this {@link Incidence} is before
	 * <code>i</code> in the sequence of {@link Incidence}s of
	 * {@link #getEdge()}.
	 * 
	 * @param i
	 *            {@link Incidence}
	 * @return boolean
	 */
	public boolean isBeforeAtEdge(Incidence i);

	/**
	 * Puts this {@link Incidence} immediately before <code>i</code> in the
	 * sequence of {@link Incidence}s of {@link #getEdge()}.
	 * 
	 * @param i
	 *            {@link Incidence}
	 */
	public void putBeforeAtEdge(Incidence i);

	/**
	 * Returns <code>true</code> if this {@link Incidence} is after
	 * <code>i</code> in the sequence of {@link Incidence}s of
	 * {@link #getEdge()}.
	 * 
	 * @param i
	 *            {@link Incidence}
	 * @return boolean
	 */
	public boolean isAfterAtEdge(Incidence i);

	/**
	 * Puts this {@link Incidence} immediately after <code>i</code> in the
	 * sequence of {@link Incidence}s of {@link #getEdge()}.
	 * 
	 * @param i
	 *            {@link Incidence}
	 */
	public void putAfterAtEdge(Incidence i);

}
