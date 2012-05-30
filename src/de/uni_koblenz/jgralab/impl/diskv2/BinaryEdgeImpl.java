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

package de.uni_koblenz.jgralab.impl.diskv2;

import java.io.IOException;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

public abstract class BinaryEdgeImpl extends EdgeImpl implements BinaryEdge {


	protected BinaryEdgeImpl(long anId, GraphDatabaseBaseImpl graphDatabase)
			throws IOException {
		super(anId, graphDatabase);
	}

	@Override
	public Vertex getAlpha() {
		Incidence firstIncidence = getFirstIncidence((Graph) null);
		if (firstIncidence.getDirection() == Direction.VERTEX_TO_EDGE) {
			return firstIncidence.getVertex();
		} else {
			return firstIncidence.getNextIncidenceAtEdge((Graph)null).getVertex();
		}
	}

	@Override
	public void setAlpha(Vertex vertex) {
		Incidence incidence = getFirstIncidence((Graph) null);
		if (incidence.getDirection() == Direction.VERTEX_TO_EDGE)
			incidence = incidence.getNextIncidenceAtEdge((Graph)null);
		((VertexImpl) incidence.getVertex()).removeIncidenceFromLambdaSeq((IncidenceImpl) incidence);
		((IncidenceImpl) incidence).setIncidentVertex((VertexImpl) vertex);
		((VertexImpl) vertex).appendIncidenceToLambdaSeq((IncidenceImpl) incidence);
	}

	@Override
	public Vertex getOmega() {
		Incidence firstIncidence = getFirstIncidence((Graph) null);
		if (firstIncidence.getDirection() == Direction.EDGE_TO_VERTEX) {
			return firstIncidence.getVertex();
		} else {
			return firstIncidence.getNextIncidenceAtEdge((Graph)null).getVertex();
		}
	}

	@Override
	public void setOmega(Vertex vertex) {
		Incidence incidence = getFirstIncidence((Graph) null);
		if (incidence.getDirection() == Direction.EDGE_TO_VERTEX)
			incidence = incidence.getNextIncidenceAtEdge((Graph)null);
		((VertexImpl) incidence.getVertex()).removeIncidenceFromLambdaSeq((IncidenceImpl) incidence);
		((IncidenceImpl) incidence).setIncidentVertex((VertexImpl) vertex);
		((VertexImpl) vertex).appendIncidenceToLambdaSeq((IncidenceImpl) incidence);
	}

	@Override
	public boolean isBinary() {
		return true;
	}
}
