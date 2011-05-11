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

import java.rmi.RemoteException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * This class provides an {@link Iterable} to iterate over vertices in a
 * {@link Graph}. One may use this class to use the advanced for-loop of Java 5.
 * Instances of this class should never, and this means <b>never</b> created
 * manually but only using the method {@link Graph#getVertices()} or its
 * variants. Every special {@link GraphClass} contains generated methods similar
 * to {@link Graph#getVertices()} for every {@link VertexClass} that is part of
 * the {@link GraphClass}.
 * 
 * @author ist@uni-koblenz.de
 * 
 * @param <V>
 *            The type of the vertices to iterate over. To mention it again,
 *            <b>don't</b> create instances of this class directly.
 */
public class VertexIterable<V extends Vertex> implements Iterable<V> {

	/**
	 * This {@link Iterator} iterates over all vertices in a {@link Graph}.
	 * 
	 * @author ist@uni-koblenz.de
	 * 
	 */
	class VertexIterator implements Iterator<V> {

		/**
		 * The {@link Vertex} that a call of next() will return
		 */
		protected V current = null;

		/**
		 * The {@link Graph} this {@link Iterator} works on.
		 */
		protected Graph graph = null;

		protected Class<? extends Vertex> vc;

		/**
		 * The version of the vertex list of the {@link Graph} at the beginning
		 * of the iteration. This information is used to check if the vertex
		 * list has changed, the failfast-iterator will then throw an exception
		 * the next time {@link #next()} is called
		 */
		protected long vertexListVersion;

		protected Graph traversalContext;

		/**
		 * Creates a new {@link VertexIterator} for the given {@link Graph}
		 * <code>graph</code>.
		 * 
		 * @param traversalContext
		 *            {@link Graph}
		 * @param graph
		 *            {@link Graph}
		 */
		@SuppressWarnings("unchecked")
		VertexIterator(Graph traversalContext, Graph graph,
				Class<? extends Vertex> vc)  {
			this.graph = graph;
			this.vc = vc;
			vertexListVersion = graph.getVertexListVersion();
			this.traversalContext = traversalContext;
			current = (V) (vc == null ? graph.getFirstVertex() : graph
					.getFirstVertex(vc));
		}

		@SuppressWarnings("unchecked")
		@Override
		public V next() {
				if (graph.isVertexListModified(vertexListVersion)) {
					throw new ConcurrentModificationException(
							"The vertex list of the graph has been modified - the iterator is not longer valid");
				}
				if (current == null) {
					throw new NoSuchElementException();
				}
				V result = current;
				current = (V) (vc == null ? current.getNextVertex(traversalContext)
						: current.getNextVertex(traversalContext, vc));
				return result;
		}

		@Override
		public boolean hasNext() {
			return current != null;
		}

		/**
		 * Checks if the sequence of {@link Vertex} was modified. In this case a
		 * {@link ConcurrentModificationException} is thrown
		 * 
		 * @throws ConcurrentModificationException
		 */
		protected void checkConcurrentModification()  {
			if (graph.isVertexListModified(vertexListVersion)) {
				throw new ConcurrentModificationException(
						"The vertex list of the graph has been modified - the iterator is not longer valid");
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"It is not allowed to remove vertices during iteration.");
		}
	}

	/**
	 * The current {@link Iterator}.
	 */
	private VertexIterator iter;

	/**
	 * Creates a {@link VertexIterable} to iterate over all vertices of
	 * <code>graph</code>.
	 * 
	 * @param graph
	 *            {@link Graph}
	 */
	public VertexIterable(Graph graph)  {
		this(graph.getTraversalContext(), graph, null);
	}

	/**
	 * Creates a {@link VertexIterable} to iterate over all vertices of
	 * <code>graph</code> which are an instance of <code>vc</code>.
	 * 
	 * @param graph
	 *            {@link Graph}
	 * @param vc
	 *            {@link Class}
	 */
	public VertexIterable(Graph g, Class<? extends Vertex> vc)  {
		assert g != null;
		iter = new VertexIterator(g.getTraversalContext(), g, vc);
	}

	/**
	 * Creates a {@link VertexIterable} to iterate over all vertices of
	 * <code>graph</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param graph
	 *            {@link Graph}
	 */
	public VertexIterable(Graph traversalContext, Graph graph)  {
		this(traversalContext, graph, null);
	}

	/**
	 * Creates a {@link VertexIterable} to iterate over all vertices of
	 * <code>graph</code> which are an instance of <code>vc</code>.
	 * 
	 * @param traversalContext
	 *            {@link Graph}
	 * @param graph
	 *            {@link Graph}
	 * @param vc
	 *            {@link Class}
	 */
	public VertexIterable(Graph traversalContext, Graph g,
			Class<? extends Vertex> vc)  {
		assert g != null;
		iter = new VertexIterator(traversalContext, g, vc);
	}

	@Override
	public Iterator<V> iterator() {
		return iter;
	}

}
