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

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

/**
 * This class provides an Iterable for the Edges incident to a given vertex.
 * 
 * @author ist@uni-koblenz.de
 */
public class IncidenceIterable<I extends Incidence> implements Iterable<I> {
	/**
	 * Creates an Iterable for all incident edges of GraphElement <code>v</code>
	 * .
	 * 
	 * @param ge
	 *            a GraphElement
	 */
	public IncidenceIterable(GraphElement ge) {
		this(ge, null, null);
	}

	/**
	 * Creates an Iterable for all incident edges of GraphElement <code>v</code>
	 * with the specified <code>orientation</code>.
	 * 
	 * @param ge
	 *            a GraphElement
	 * @param orientation
	 *            desired orientation
	 */
	public IncidenceIterable(GraphElement ge, Direction orientation) {
		this(ge, null, orientation);
	}

	/**
	 * Creates an Iterable for all incident edges of GraphElement <code>v</code>
	 * with the specified edgeclass <code>ec</code>.
	 * 
	 * @param ge
	 *            a GraphElement
	 * @param ic
	 *            restricts edges to that class or subclasses
	 */
	public IncidenceIterable(GraphElement ge, Class<? extends Incidence> ic) {
		this(ge, ic, null);
	}

	/**
	 * Creates an Iterable for all incident edges of GraphElement <code>v</code>
	 * with the specified edgeclass <code>ec</code> and <code>orientation</code>
	 * .
	 * 
	 * @param ge
	 *            a GraphElement
	 * @param ic
	 *            restricts edges to that class or subclasses
	 * @param orientation
	 *            desired orientation
	 */
	public IncidenceIterable(GraphElement ge, Class<? extends Incidence> ic,
			Direction orientation) {
		assert ge != null && ge.isValid();
		iter = new IncidenceIterator(ge, ic, orientation);
	}

	class IncidenceIterator implements Iterator<I> {
		protected I current = null;

		protected GraphElement graphElement = null;

		protected Class<? extends Incidence> ic;

		protected Direction dir;

		/**
		 * the version of the incidence list of the vertex at the beginning of
		 * the iteration. This information is used to check if the incidence
		 * list has changed, the failfast-iterator will then throw an exception
		 * the next time "next()" is called
		 */
		protected long incidenceListVersion;

		@SuppressWarnings("unchecked")
		public IncidenceIterator(GraphElement graphElement,
				Class<? extends Incidence> ic, Direction dir) {
			this.graphElement = graphElement;
			this.ic = ic;
			this.dir = dir;
			incidenceListVersion = ((GraphElementImpl) graphElement)
					.getIncidenceListVersion();
			current = (I) ((ic == null) ? graphElement.getFirstIncidence(dir)
					: graphElement.getFirstIncidence(ic, dir));
		}

		@SuppressWarnings("unchecked")
		public I next() {
			checkConcurrentModification();
			if (current == null) {
				throw new NoSuchElementException();
			}
			I result = current;
			current = (I) ((ic == null) ? current.getNextIncidence(dir)
					: current.getNextIncidence(ic, dir));
			return result;
		}

		public boolean hasNext() {
			checkConcurrentModification();
			return current != null;
		}

		/**
		 * Checks if the sequence of {@link Incidence}s was modified. In this
		 * case a {@link ConcurrentModificationException} is thrown
		 * 
		 * @throws ConcurrentModificationException
		 */
		private void checkConcurrentModification() {
			if (((GraphElementImpl) graphElement)
					.isIncidenceListModified(incidenceListVersion)) {
				throw new ConcurrentModificationException(
						"The incidence list of the "
								+ (graphElement instanceof Vertex ? "vertex"
										: "edge")
								+ " has been modified - the iterator is not longer valid");
			}
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"Cannot remove Edges using Iterator");
		}

	}

	private IncidenceIterator iter = null;

	public Iterator<I> iterator() {
		return iter;
	}
}
