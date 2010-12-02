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
	 * Returns the first {@link Incidence} of this {@link GraphElement}.
	 * 
	 * @return {@link Incidence}
	 */
	public Incidence getFirstIncidence();

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
	 *            {@link Incidence}s
	 * @return {@link Iterable}&lt;{@link Incidence}&gt;
	 */
	public Iterable<Incidence> getIncidences(Direction direction);
}
