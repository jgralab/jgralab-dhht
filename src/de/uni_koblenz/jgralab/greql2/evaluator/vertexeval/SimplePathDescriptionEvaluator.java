/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2007 Institute for Software Technology
 *               University of Koblenz-Landau, Germany
 *
 *               ist@uni-koblenz.de
 *
 * Please report bugs to http://serres.uni-koblenz.de/bugzilla
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueTypeCollection;
import de.uni_koblenz.jgralab.greql2.schema.IsEdgeRestrOf;
import de.uni_koblenz.jgralab.greql2.schema.SimplePathDescription;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;

/**
 * Evaluates a SimplePathDescription, that is something link v -->{isExprOf} w.
 * Creates a NFA which accepts the simplePath the vertex to evaluate describes.
 * @author Daniel Bildhauer <dbildh@uni-koblenz.de> 
 * Summer 2006, Diploma Thesis
 *
 */
public class SimplePathDescriptionEvaluator extends
		PrimaryPathDescriptionEvaluator {

	public SimplePathDescriptionEvaluator(SimplePathDescription vertex,
			GreqlEvaluator eval) {
		super(vertex, eval);
	}

	@Override
	public JValue evaluate() throws EvaluateException {
		Edge inc2 = vertex.getFirstEdge(EdgeDirection.IN);
		while (inc2 != null) {
			inc2 = inc2.getNextEdge();
		}
		JValueTypeCollection typeCollection = new JValueTypeCollection();
		IsEdgeRestrOf inc = vertex.getFirstIsEdgeRestrOf(EdgeDirection.IN);
	//	IsTypeRestrOf inc = vertex.getFirstIsTypeRestrOf(EdgeDirection.IN);
		EdgeRestrictionEvaluator edgeRestEval = null;
		if (inc != null) {
			edgeRestEval = (EdgeRestrictionEvaluator) greqlEvaluator.getVertexEvaluatorGraphMarker().getMark(inc.getAlpha());
			typeCollection.addTypes(edgeRestEval.getTypeCollection());
		}
		createdNFA = NFA.createSimplePathDescriptionNFA(
				getEdgeDirection(vertex), typeCollection,
				getEdgeRole(edgeRestEval));
		return new JValue(createdNFA);
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return this.greqlEvaluator.getCostModel()
				.calculateCostsSimplePathDescription(this, graphSize);
	}

}
