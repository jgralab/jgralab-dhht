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

package de.uni_koblenz.jgralab.codegenerator;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;


/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgeCodeGenerator extends GraphElementCodeGenerator<EdgeClass> {

	
	public EdgeCodeGenerator(EdgeClass edgeClass, String schemaPackageName,
			CodeGeneratorConfiguration config) {
		super(edgeClass, schemaPackageName, config, false);
		rootBlock.setVariable("baseClassName", "EdgeImpl");
		rootBlock.setVariable("graphElementClass", "Edge");
	}




	@Override
	protected CodeList createBody() {
		CodeList code = (CodeList) super.createBody();
	    createMethodsForBinaryEdge(code);
		return code;
	}
	
	protected void createMethodsForBinaryEdge(CodeList code) {
		if (currentCycle.isStdImpl()) {
			code.add(createBinaryConstructor());
		}
	}


	protected CodeBlock createBinaryConstructor() {
		if (aec.isAbstract())
			return null;
		CodeList code = new CodeList();
		addImports("#jgPackage#.#ownElementClass#");
		IncidenceClass alphaInc = null;
		IncidenceClass omegaInc = null;
		for (IncidenceClass ic : aec.getAllIncidenceClasses()) {
			if (!ic.isAbstract()) {
				if (ic.getDirection() == Direction.EDGE_TO_VERTEX) {
					omegaInc = ic;
				} else {
					alphaInc = ic;
				}
			}
		}
		code.setVariable("alphaVertex", absoluteName(alphaInc.getVertexClass()));
		code.setVariable("omegaVertex", absoluteName(omegaInc.getVertexClass()));
		code.setVariable("alphaInc", absoluteName(alphaInc));
		code.setVariable("omegaInc", absoluteName(omegaInc));
		code.addNoIndent(new CodeSnippet(
						true,
						"public #simpleClassName#Impl(int id, #jgPackage#.Graph g, #alphaVertex# alpha, #omegaVertex# omega) {",
						"\tthis(id, g);"));
		code.addNoIndent(new CodeSnippet("alpha.connect(#alphaInc#.class, this);"));
		code.addNoIndent(new CodeSnippet("omega.connect(#omegaInc#.class, this);"));
		//code.addNoIndent(new CodeSnippet("/* implement setting of alpha and omega */"));
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}

	
}
