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
 
package de.uni_koblenz.jgralab.impl.array;

import de.uni_koblenz.jgralab.Composition;
import de.uni_koblenz.jgralab.EdgeClass;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;

public abstract class CompositionImpl extends AggregationImpl implements Composition {

	public CompositionImpl(int anId, Graph aGraph, EdgeClass theClass) {
		super(anId, aGraph, theClass);
	}
	
	public Composition getNextCompositionInGraph() {
		return (Composition)getNextEdgeOfClassInGraph(Composition.class);
	}
	
	public Composition getNextCompositionInGraph(boolean noSubClasses) {
		return (Composition)getNextEdgeOfClassInGraph(Composition.class, noSubClasses);
	}
	
	public Composition getNextComposition() {
		return (Composition)getNextEdgeOfClass(Composition.class);
	}

	public Composition getNextComposition(EdgeDirection orientation) {
		return (Composition)getNextEdgeOfClass(Composition.class, orientation);
	}
	
	public Composition getNextComposition(EdgeDirection orientation, boolean noSubClasses) {
		return (Composition)getNextEdgeOfClass(Composition.class, orientation, noSubClasses);
	}

	public Composition getNextComposition(boolean noSubClasses) {
		return (Composition)getNextEdgeOfClass(Composition.class, noSubClasses);
	}
	
}
