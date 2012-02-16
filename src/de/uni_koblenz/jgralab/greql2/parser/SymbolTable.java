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

package de.uni_koblenz.jgralab.greql2.parser;



import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.exception.DuplicateVariableException;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Aggregation;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;
import de.uni_koblenz.jgralab.greql2.schema.Variable;

public class SymbolTable extends SimpleSymbolTable {

	@Override
	public void insert(String ident, Vertex v)
			throws DuplicateVariableException {

		if (!list.getFirst().containsKey(ident)) {
			list.getFirst().put(ident, v);
		} else {
			Vertex var = list.getFirst().get(ident);
			int offset = -1;
			Greql2Aggregation edge = (Greql2Aggregation) var.getFirstIncidence(Direction.VERTEX_TO_EDGE).getEdge();
			if (edge != null)
				offset = edge.get_sourcePositions().get(0).get_offset();
			
			throw new DuplicateVariableException((Variable) var,
					((Greql2Aggregation) v.getFirstIncidence(Direction.EDGE_TO_VERTEX).getEdge())
							.get_sourcePositions(), new SourcePosition(ident.length(), offset));
		}
	}

}
