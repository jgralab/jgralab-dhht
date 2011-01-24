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

import de.uni_koblenz.jgralab.schema.EdgeClass;


/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgeCodeGenerator extends GraphElementCodeGenerator<EdgeClass> {

	
	public EdgeCodeGenerator(EdgeClass edgeClass, String schemaPackageName,
			String implementationName, CodeGeneratorConfiguration config) {
		super(edgeClass, schemaPackageName, implementationName, config, false);
	}




	@Override
	protected CodeBlock createBody() {
		CodeList code = (CodeList) super.createBody();
	    createMethodsForBinaryEdge(code);
		return code;
	}
	
	protected void createMethodsForBinaryEdge(CodeList code) {
		code.add(createBinaryConstructor());
	}


	protected CodeBlock createBinaryConstructor() {
		CodeList code = new CodeList();
		addImports("#jgPackage#.#ownElementClass#");
		code.addNoIndent(new CodeSnippet(
						true,
						"public #simpleClassName#Impl(int id, #jgPackage#.Graph g, Vertex alpha, Vertex omega) {",
						"\tthis(id, g);"));
		code.addNoIndent(new CodeSnippet("/* implement setting of alpha and omega */"));
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}

	
}
