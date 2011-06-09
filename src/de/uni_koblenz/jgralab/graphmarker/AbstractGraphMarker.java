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
package de.uni_koblenz.jgralab.graphmarker;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphStructureChangedAdapterWithAutoRemove;
import de.uni_koblenz.jgralab.Vertex;

public abstract class AbstractGraphMarker<T extends AttributedElement<?,?>> extends
		GraphStructureChangedAdapterWithAutoRemove implements GraphMarker<T> {
	protected final Graph graph;

	protected AbstractGraphMarker(Graph graph) {
		this.graph = graph;
		// register the graph marker at the graph
		graph.addGraphStructureChangedListener(this);
	}

	/* (non-Javadoc)
	 * @see de.uni_koblenz.jgralab.graphmarker.GraphMarker#isMarked(T)
	 */
	@Override
	public abstract boolean isMarked(T graphElement);

	/* (non-Javadoc)
	 * @see de.uni_koblenz.jgralab.graphmarker.GraphMarker#removeMark(T)
	 */
	@Override
	public abstract boolean removeMark(T graphElement);

	/* (non-Javadoc)
	 * @see de.uni_koblenz.jgralab.graphmarker.GraphMarker#size()
	 */
	@Override
	public abstract int size();

	/* (non-Javadoc)
	 * @see de.uni_koblenz.jgralab.graphmarker.GraphMarker#isEmpty()
	 */
	@Override
	public abstract boolean isEmpty();

	/* (non-Javadoc)
	 * @see de.uni_koblenz.jgralab.graphmarker.GraphMarker#clear()
	 */
	@Override
	public abstract void clear();

	/* (non-Javadoc)
	 * @see de.uni_koblenz.jgralab.graphmarker.GraphMarker#getGraph()
	 */
	@Override
	public Graph getGraph() {
		return graph;
	}

	/* (non-Javadoc)
	 * @see de.uni_koblenz.jgralab.graphmarker.GraphMarker#getMarkedElements()
	 */
	@Override
	public abstract Iterable<T> getMarkedElements();

	@Override
	public abstract void edgeDeleted(Edge e);

	@Override
	public abstract void maxEdgeCountIncreased(int newValue);

	@Override
	public abstract void maxVertexCountIncreased(int newValue);

	@Override
	public abstract void vertexDeleted(Vertex v);

}
