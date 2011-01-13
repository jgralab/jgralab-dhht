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
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;

/**
 * Implementation of all methods of the interface {@link Incidence} which are
 * independent of the fields of a specific IncidenceImpl.
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class IncidenceBaseImpl implements Incidence {

	/**
	 * Creates a new instance of IncidenceImpl and appends it to the lambda
	 * sequences of <code>v</code> and <code>e</code>.
	 * 
	 * @param v
	 *            {@link Vertex}
	 * @param e
	 *            {@link Edge}
	 */
	protected IncidenceBaseImpl(Vertex v, Edge e) {
		super();
	}

	@Override
	public Incidence getNextIncidenceAtEdge(Direction direction) {
		Incidence i = getNextIncidenceAtEdge();
		while ((i != null) && direction != null
				&& i.getDirection() != direction) {
			i = i.getNextIncidenceAtEdge();
		}
		return i;
	}

	@Override
	public Incidence getNextIncidenceAtEdge(boolean thisIncidence,
			IncidenceType... incidenceTypes) {
		Incidence i = getNextIncidenceAtEdge();
		if (incidenceTypes.length == 0) {
			return i;
		}
		while (i != null) {
			for (IncidenceType element : incidenceTypes) {
				if ((thisIncidence ? i.getThisSemantics() : i
						.getThatSemantics()) == element) {
					return i;
				}
			}
			i = i.getNextIncidenceAtEdge();
		}
		return null;
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(anIncidenceClass, null, false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(anIncidenceClass, null, false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			Direction direction) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(anIncidenceClass, direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, Direction direction) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(anIncidenceClass, direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(anIncidenceClass, null, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(anIncidenceClass, null, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtEdge(anIncidenceClass.getM1Class(), direction,
				noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtEdge(
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		Incidence currentIncidence = getNextIncidenceAtEdge(direction);
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
			currentIncidence = currentIncidence
					.getNextIncidenceAtEdge(direction);
		}
		return null;
	}

	@Override
	public Incidence getNextIncidenceAtVertex(Direction direction) {
		Incidence i = getNextIncidenceAtVertex();
		while ((i != null) && direction != null
				&& i.getDirection() != direction) {
			i = i.getNextIncidenceAtVertex();
		}
		return i;
	}

	@Override
	public Incidence getNextIncidenceAtVertex(boolean thisIncidence,
			IncidenceType... incidenceTypes) {
		Incidence i = getNextIncidenceAtVertex();
		if (incidenceTypes.length == 0) {
			return i;
		}
		while (i != null) {
			for (IncidenceType element : incidenceTypes) {
				if ((thisIncidence ? i.getThisSemantics() : i
						.getThatSemantics()) == element) {
					return i;
				}
			}
			i = i.getNextIncidenceAtVertex();
		}
		return null;
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(anIncidenceClass, null, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(anIncidenceClass, null, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			Direction direction) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(anIncidenceClass, direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, Direction direction) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(anIncidenceClass, direction, false);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(anIncidenceClass, null, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(anIncidenceClass, null, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(IncidenceClass anIncidenceClass,
			Direction direction, boolean noSubclasses) {
		assert anIncidenceClass != null;
		return getNextIncidenceAtVertex(anIncidenceClass.getM1Class(),
				direction, noSubclasses);
	}

	@Override
	public Incidence getNextIncidenceAtVertex(
			Class<? extends Incidence> anIncidenceClass, Direction direction,
			boolean noSubclasses) {
		assert anIncidenceClass != null;
		Incidence currentIncidence = getNextIncidenceAtVertex(direction);
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
			currentIncidence = currentIncidence
					.getNextIncidenceAtVertex(direction);
		}
		return null;
	}

	@Override
	public boolean isBeforeAtVertex(Incidence i) {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence prev = i.getPreviousIncidenceAtVertex();
		while ((prev != null) && (prev != this)) {
			prev = i.getPreviousIncidenceAtVertex();
		}
		return prev != null;
	}

	@Override
	public boolean isAfterAtVertex(Incidence i) {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence next = i.getNextIncidenceAtVertex();
		while ((next != null) && (next != this)) {
			next = i.getNextIncidenceAtVertex();
		}
		return next != null;
	}

	@Override
	public boolean isBeforeAtEdge(Incidence i) {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence prev = i.getPreviousIncidenceAtEdge();
		while ((prev != null) && (prev != this)) {
			prev = i.getPreviousIncidenceAtEdge();
		}
		return prev != null;
	}

	@Override
	public boolean isAfterAtEdge(Incidence i) {
		assert i != null;
		assert getGraph() == i.getGraph();
		if (this == i) {
			return false;
		}
		Incidence next = i.getNextIncidenceAtEdge();
		while ((next != null) && (next != this)) {
			next = i.getNextIncidenceAtEdge();
		}
		return next != null;
	}

}
