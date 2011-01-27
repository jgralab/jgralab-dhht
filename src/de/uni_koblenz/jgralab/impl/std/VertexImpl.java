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
package de.uni_koblenz.jgralab.impl.std;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

/**
 * The implementation of a {@link Vertex} accessing attributes without
 * versioning.
 * 
 * @author Jose Monte(monte@uni-koblenz.de)
 */
public abstract class VertexImpl extends
		de.uni_koblenz.jgralab.impl.VertexBaseImpl {

	private VertexImpl nextVertexInGraph;
	private VertexImpl prevVertexInGraph;
	private IncidenceImpl firstIncidenceAtVertex;
	private IncidenceImpl lastIncidenceAtVertex;

	@Override
	public Incidence getFirstIncidence() {
		return firstIncidenceAtVertex;
	}

	@Override
	public Vertex getNextVertex() {
		assert isValid();
		return nextVertexInGraph;
	}

	@Override
	public Vertex getPreviousVertex() {
		return prevVertexInGraph;
	}

	@Override
	public Incidence getLastIncidence() {
		return lastIncidenceAtVertex;
	}

	@Override
	protected void setNextVertex(Vertex nextVertex) {
		nextVertexInGraph = (VertexImpl) nextVertex;
	}

	@Override
	protected void setPrevVertex(Vertex prevVertex) {
		prevVertexInGraph = (VertexImpl) prevVertex;
	}

	@Override
	public void setFirstIncidence(IncidenceImpl firstIncidence) {
		firstIncidenceAtVertex = firstIncidence;
	}

	@Override
	public void setLastIncidence(IncidenceImpl lastIncidence) {
		lastIncidenceAtVertex = lastIncidence;
	}

	/**
	 * Creates a new {@link VertexImpl} instance.
	 * 
	 * @param id
	 *            int
	 * @param graph
	 *            {@link Graph}
	 */
	protected VertexImpl(int id, Graph graph) {
		super(id, graph);
		((GraphImpl) graph).addVertex(this);
	}

	@Override
	protected void setId(int id) {
		assert id >= 0;
		this.id = id;
	}
}
