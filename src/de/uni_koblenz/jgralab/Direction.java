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
 * Specifies direction of {@link Incidence}s for traversal methods.
 * 
 * @see Direction#EDGE_TO_VERTEX
 * @see Direction#VERTEX_TO_EDGE
 * @see Direction#BOTH
 * @author ist@uni-koblenz.de
 */
public enum Direction {

	/**
	 * This direction describes an {@link Incidence} which begins at an
	 * {@link Edge} and leads to a {@link Vertex}. Such an {@link Incidence} is
	 * called ingoing.
	 */
	EDGE_TO_VERTEX,
	/**
	 * This direction describes an {@link Incidence} which begins at a
	 * {@link Vertex} and leads to an {@link Edge}. Such an {@link Incidence} is
	 * called outgoing.
	 */
	VERTEX_TO_EDGE,
	/**
	 * This direction describes an {@link Incidence} which begins at a
	 * {@link Vertex} and leads to an {@link Edge} or the other way round.
	 */
	BOTH;

	/**
	 * Parses a given {@link String} for {@link Direction}.
	 * 
	 * @param direction
	 *            {@link String} containing only EDGE_TO_VERTEX or
	 *            VERTEX_TO_EDGE.
	 * @return {@link Direction} Matching {@link Incidence} direction.
	 * @throws Exception
	 *             When no {@link Direction} could be matched from
	 *             <code>direction</code>.
	 */
	public static Direction parse(String direction) throws Exception {
		if (direction.equals("EDGE_TO_VERTEX")) {
			return EDGE_TO_VERTEX;
		} else if (direction.equals("VERTEX_TO_EDGE")) {
			return VERTEX_TO_EDGE;
		} else if (direction.equals("BOTH")) {
			return BOTH;
		} else {
			throw new Exception("Could not determine direction from string.");
		}
	}

	public Direction getOppositeDirection() {
		// TODO Auto-generated method stub
		return null;
	}
}
