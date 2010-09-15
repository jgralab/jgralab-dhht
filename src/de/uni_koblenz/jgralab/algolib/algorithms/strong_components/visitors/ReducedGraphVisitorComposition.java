/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2010 Institute for Software Technology
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
package de.uni_koblenz.jgralab.algolib.algorithms.strong_components.visitors;

import java.util.Collection;
import java.util.LinkedHashSet;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.visitors.Visitor;
import de.uni_koblenz.jgralab.algolib.visitors.VisitorComposition;

public class ReducedGraphVisitorComposition extends VisitorComposition
		implements ReducedGraphVisitor {

	private Collection<ReducedGraphVisitor> visitors;

	@Override
	protected void createVisitorsLazily() {
		super.createVisitorsLazily();
		if (visitors == null) {
			visitors = new LinkedHashSet<ReducedGraphVisitor>();
		}
	}

	@Override
	public void addVisitor(Visitor visitor) {
		if (visitor instanceof ReducedGraphVisitor) {
			super.addVisitor(visitor);
			visitors.add((ReducedGraphVisitor) visitor);
		} else {
			throw new IllegalArgumentException(
					"This visitor composition is only compatible with implementations of "
							+ ReducedGraphVisitor.class.getSimpleName() + ".");
		}
	}
	
	@Override
	public void removeVisitor(Visitor visitor) {
		super.removeVisitor(visitor);
		if (visitors != null) {
			if (visitor instanceof ReducedGraphVisitor) {
				visitors.remove(visitor);
				if (visitors.size() == 0) {
					visitors = null;
				}
			}
		}
	}
	
	@Override
	public void clearVisitors() {
		super.clearVisitors();
		visitors = null;
	}

	@Override
	public void visitReducedEdge(Edge e) {
		if (visitors != null) {
			for (Visitor currentVisitor : visitors) {
				((ReducedGraphVisitor) currentVisitor).visitReducedEdge(e);
			}
		}
	}

	@Override
	public void visitRepresentativeVertex(Vertex v) {
		if (visitors != null) {
			for (Visitor currentVisitor : visitors) {
				((ReducedGraphVisitor) currentVisitor)
						.visitRepresentativeVertex(v);
			}
		}
	}
}
