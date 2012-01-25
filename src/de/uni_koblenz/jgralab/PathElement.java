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
 * Represents a path element in a path description given to
 * {@link Vertex#reachableVertices(Class, PathElement...)}.
 * 
 * @author Tassilo Horn &lt;horn@uni-koblenz.de&gt;
 * 
 */
public class PathElement {

	/**
	 * Only instances of this {@link Class}, which has to be an subclass of
	 * {@link Edge}, are considered.
	 */
	public Class<? extends Edge> edgeClass;

	/**
	 * Only edges which are connected to the current vertex via an
	 * {@link Incidence} of this direction are considered.
	 */
	public Direction direction;

	/**
	 * Should only be edges considered which have the exact type
	 * {@link #edgeClass}?
	 */
	public boolean strictType = false;

	/**
	 * @see {@link #PathElement(Class, Direction, boolean)}.
	 *      <code>stricts</code> defaults to false.
	 * @param ec
	 *            {@link Class}
	 * @param d
	 *            {@link Direction}
	 */
	public PathElement(Class<? extends Edge> ec, Direction d) {
		this.edgeClass = ec;
		this.direction = d;
	}

	/**
	 * @param ec
	 *            {@link Class} of allowed {@link Edge}s
	 * @param d
	 *            {@link Direction} of allowed {@link Edge}
	 * @param strict
	 *            boolean allow only the exact type
	 */
	public PathElement(Class<? extends Edge> ec, Direction d, boolean strict) {
		this(ec, d);
		this.strictType = strict;
	}
}
