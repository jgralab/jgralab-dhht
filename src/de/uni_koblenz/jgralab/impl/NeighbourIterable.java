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

import java.util.Iterator;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;

/**
 * This class provides an Iterable for the Vertices adjacent to a given vertex.
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class NeighbourIterable<OwnType extends GraphElement<OwnType, DualType>, DualType extends GraphElement<DualType, OwnType>> implements
		Iterable<OwnType> {
	
	/**
	 * Creates an Iterable for all neighbours adjacent to <code>elem</code>.
	 * 
	 * @param elem 
	 *            
	 */
	public NeighbourIterable(OwnType elem) {
		this(elem, null, Direction.BOTH);
	}

	/**
	 * Creates an Iterable for all neighbours adjacent to <code>v</code> via
	 * edges of the specified edgeclass <code>ec</code>.
	 * 
	 * @param v
	 *            
	 * @param ec
	 *            restricts edges to that class or subclasses
	 */
	public NeighbourIterable(OwnType elem, Class<? extends DualType> other) {
		this(elem, other, Direction.BOTH);
	}

	/**
	 * Creates an Iterable for all neighbours adjacent to <code>v</code> via
	 * edges of the specified <code>orientation</code>.
	 * 
	 * @param v
	 *            a Vertex
	 * @param orientation
	 *            desired orientation
	 */
	public NeighbourIterable(OwnType elem, Direction dir) {
		this(elem, null, dir);
	}

	/**
	 * Creates an Iterable for all neighbours adjacent to <code>v</code> via
	 * edges of the specified edgeclass <code>ec</code> and
	 * <code>orientation</code>.
	 * 
	 * @param v
	 *            a Vertex
	 * @param ec
	 *            restricts edges to that class or subclasses
	 * @param orientation
	 *            desired orientation
	 */
	public NeighbourIterable(OwnType elem, Class<? extends DualType> ec,
			Direction dir) {
		assert elem != null && elem.isValid();
		
	}
		
	class NeigbourIterator implements Iterator<OwnType> {
        private Iterator<Incidence> incidencesAtOwnElementIterator;
        private Iterator<Incidence> incidencesAtIncidentElementIterator;
        private Incidence currentIncidenceToIncidentElement;
        private boolean gotNext = false;
        private OwnType nextElem = null;
        private Class<? extends OwnType> classOfAdjacentElements;
       
		
		public NeigbourIterator(OwnType elem, Class<? extends OwnType> oc, Direction dir ) {
			classOfAdjacentElements = oc;
			incidencesAtOwnElementIterator = elem.getIncidences(dir).iterator();			
		}
		

		@Override
		public boolean hasNext() {
			if (gotNext)
				nextElem = findNextElem();
			return nextElem != null;
		}	
		
		@SuppressWarnings("unchecked")
		private OwnType findNextElem() {
			OwnType element = null;
			do {
				element = null;
				if ((incidencesAtIncidentElementIterator != null) && incidencesAtIncidentElementIterator.hasNext()) {
					element = (OwnType) incidencesAtIncidentElementIterator.next().getThat();
				} else {
					while (incidencesAtOwnElementIterator.hasNext()) {
						currentIncidenceToIncidentElement = incidencesAtOwnElementIterator.next();
						Direction currentDirection = currentIncidenceToIncidentElement.getDirection();
						incidencesAtIncidentElementIterator = currentIncidenceToIncidentElement.getEdge().getIncidences(currentDirection.getOppositeDirection()).iterator(); 
						if (incidencesAtIncidentElementIterator.hasNext()) {
							element = (OwnType) incidencesAtIncidentElementIterator.next().getThat();
							break;
						}	
					}
				}
			} while ((element != null) && !classOfAdjacentElements.isInstance(element));	
			return element;
		}

		@Override
		public OwnType next() {
			hasNext();
			return nextElem;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private Iterator<OwnType> neighbourIterator;

	@Override
	public Iterator<OwnType> iterator() {
		return neighbourIterator;
	}
}
