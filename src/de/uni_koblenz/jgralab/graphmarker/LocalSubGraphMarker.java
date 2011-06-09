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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;

/**
 * This class serves as a special <code>BitSetGraphmarker</code>, although it
 * does not extend it. It is capable of marking both vertices and edges. This is
 * necessary for defining subgraphs. Internally all calls are delegated to an
 * instance of <code>BitSetVertexGraphMarker</code> and an instance of
 * <code>BitSetEdgeGraphMarker</code>.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class LocalSubGraphMarker extends AbstractGraphMarker<GraphElement<?, ?, ?>> {

	// TODO maybe replace with BitSets

	private final LocalBitSetEdgeMarker edgeGraphMarker;
	private final LocalBitSetVertexMarker vertexGraphMarker;
	private long version;

	public LocalSubGraphMarker(Graph graph) throws RemoteException {
		super(graph);
		edgeGraphMarker = new LocalBitSetEdgeMarker(graph);
		vertexGraphMarker = new LocalBitSetVertexMarker(graph);
	}

	@Override
	public void clear() {
		edgeGraphMarker.clear();
		vertexGraphMarker.clear();
	}

	@Override
	public boolean isEmpty() {
		return edgeGraphMarker.isEmpty() && vertexGraphMarker.isEmpty();
	}

	@Override
	public boolean isMarked(GraphElement<?, ?, ?> graphElement) {
		if (graphElement instanceof Edge) {
			return edgeGraphMarker.isMarked((Edge) graphElement);
		} else {
			return vertexGraphMarker.isMarked((Vertex) graphElement);
		}
	}

	@Override
	public long size() {
		return edgeGraphMarker.size() + vertexGraphMarker.size();
	}

	@Override
	public boolean removeMark(GraphElement<?, ?, ?> graphElement) {
		version++;
		if (graphElement instanceof Edge) {
			return edgeGraphMarker.removeMark((Edge) graphElement);
		} else {
			return vertexGraphMarker.removeMark((Vertex) graphElement);
		}
	}

	/**
	 * Does the same as <code>unmark</code> but without performing an
	 * <code>instanceof</code> check. It is recommended to use this method
	 * instead.
	 * 
	 * @param e
	 *            the edge to unmark
	 * @return false if the given edge has already been unmarked.
	 */
	public boolean removeMark(Edge e) throws RemoteException {
		version++;
		return edgeGraphMarker.removeMark(e);
	}

	/**
	 * Does the same as <code>unmark</code> but without performing an
	 * <code>instanceof</code> check. It is recommended to use this method
	 * instead.
	 * 
	 * @param v
	 *            the vertex to unmark
	 * @return false if the given vertex has already been unmarked.
	 */
	public boolean removeMark(Vertex v) throws RemoteException {
		version++;
		return vertexGraphMarker.removeMark(v);
	}

	/**
	 * Marks the given <code>graphElement</code>.
	 * 
	 * @param graphElement
	 *            the graph element to mark
	 * @return false if the given <code>graphElement</code> has already been
	 *         marked.
	 */
	public boolean mark(GraphElement<?, ?, ?> graphElement) throws RemoteException {
		version++;
		if (graphElement instanceof Edge) {
			return edgeGraphMarker.mark((Edge) graphElement);
		} else {
			return vertexGraphMarker.mark((Vertex) graphElement);
		}
	}

	/**
	 * Does the same as <code>mark</code> but without performing an
	 * <code>instanceof</code> check. It is recommended to use this method
	 * instead.
	 * 
	 * @param e
	 *            the edge to mark
	 * @return false if the given edge has already been marked.
	 */
	public boolean mark(Edge e) throws RemoteException {
		version++;
		return edgeGraphMarker.mark(e);
	}

	/**
	 * Does the same as <code>mark</code> but without performing an
	 * <code>instanceof</code> check. It is recommended to use this method
	 * instead.
	 * 
	 * @param v
	 *            the vertex to mark
	 * @return false if the given vertex has already been marked.
	 */
	public boolean mark(Vertex v) throws RemoteException {
		version++;
		return vertexGraphMarker.mark(v);
	}

	@Override
	public void edgeDeleted(Edge e)  {
		edgeGraphMarker.edgeDeleted(e);
	}

	@Override
	public void vertexDeleted(Vertex v)  {
		vertexGraphMarker.vertexDeleted(v);
	}

	@Override
	public void maxEdgeCountIncreased(int newValue) {
		// do nothing
	}

	@Override
	public void maxVertexCountIncreased(int newValue) {
		// do nothing
	}

	@Override
	public Iterable<GraphElement<?, ?, ?>> getMarkedElements() {
		return new Iterable<GraphElement<?, ?, ?>>() {

			@Override
			public Iterator<GraphElement<?, ?, ?>> iterator() {
				return new LocalArrayGraphMarkerIterator<GraphElement<?, ?, ?>>(
						version) {

					Iterator<Vertex> vertexIterator;
					Iterator<Edge> edgeIterator;

					{
						vertexIterator = vertexGraphMarker.getMarkedElements()
								.iterator();
						edgeIterator = edgeGraphMarker.getMarkedElements()
								.iterator();
					}

					@Override
					public boolean hasNext() {
						return vertexIterator.hasNext()
								|| edgeIterator.hasNext();
					}

					@Override
					protected void moveIndex() {
						// not required
					}

					@Override
					public GraphElement<?, ?, ?> next() {
						if (version != LocalSubGraphMarker.this.version) {
							throw new ConcurrentModificationException(
									MODIFIED_ERROR_MESSAGE);
						}
						if (vertexIterator.hasNext()) {
							return vertexIterator.next();
						}
						if (edgeIterator.hasNext()) {
							return edgeIterator.next();
						}
						throw new NoSuchElementException(
								NO_MORE_ELEMENTS_ERROR_MESSAGE);
					}

				};
			}

		};
	}

}
