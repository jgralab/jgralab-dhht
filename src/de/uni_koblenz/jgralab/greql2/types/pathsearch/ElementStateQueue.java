/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2011 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         http://jgralab.uni-koblenz.de
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
/**
 * 
 */
package de.uni_koblenz.jgralab.greql2.types.pathsearch;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.State;

public class ElementStateQueue {

	private static int initialSize = 1000;

	public GraphElement currentElement = null;

	public State currentState = null;

	int size = initialSize;

	GraphElement[] elements = null;

	State[] states = null;

	int last = 0;

	int first = 0;

	public ElementStateQueue() {
		elements = new GraphElement[initialSize];
		states = new State[initialSize];
		size = initialSize;
	}

	public final void put(GraphElement<?, ?, ?, ?> e, State s) {
		if (last == first + size - 1) {
			resize();
		}
		elements[last % size] = e;
		states[last % size] = s;
		last++;
	}

	public final boolean hasNext() {
		if (first == last) {
			return false;
		}
		currentElement = elements[first % size];
		currentState = states[first % size];
		first++;
		return true;
	}

	private final void resize() {
		GraphElement<?, ?, ?, ?>[] newElements = new GraphElement[size * 2];
		State[] newStates = new State[size * 2];

		for (int i = 0; i < size; i++) {
			newElements[i] = elements[(first + i) % size];
			newStates[i] = states[(first + i) % size];
		}
		states = newStates;
		elements = newElements;
		last = size - 1;
		first = 0;
		size *= 2;
		// initialSize *= 2;
	}
}
