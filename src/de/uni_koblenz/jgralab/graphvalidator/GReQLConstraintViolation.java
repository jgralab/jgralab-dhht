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
package de.uni_koblenz.jgralab.graphvalidator;

import java.util.Set;

import de.uni_koblenz.jgralab.TypedElement;
import de.uni_koblenz.jgralab.schema.Constraint;
import de.uni_koblenz.jgralab.schema.TypedElementClass;

/**
 * 
 * @author <ist@uni-koblenz.de>
 * 
 */
public class GReQLConstraintViolation extends ConstraintViolation {

	private Constraint constraint;

	/**
	 * @return the constraint
	 */
	public Constraint getConstraint() {
		return constraint;
	}

	/**
	 * @return the offendingElements
	 */
	@Override
	public Set<? extends TypedElement<?, ?>> getOffendingElements() {
		return offendingElements;
	}

	public GReQLConstraintViolation(TypedElementClass<?,?> aec,
			Constraint constraint, Set<? extends TypedElement<?, ?>> set) {
		super(aec);
		this.constraint = constraint;
		offendingElements = set;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GReQLConstraintViolation) {
			GReQLConstraintViolation other = (GReQLConstraintViolation) o;
			return this.compareTo(other) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 29;
		hash = hash * 97 + constraint.hashCode();
		hash = hash * 97 + offendingElements.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("The predicate attached to ");
		sb.append(affectedTypedElementClass.getQualifiedName());
		sb.append(" is not satisfied! ");
		sb.append(constraint.getMessage());
		if (offendingElements != null) {
			sb.append(" Offending elements: ");
			boolean first = true;
			for (TypedElement<?,?> ae : offendingElements) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(ae);
			}
		}
		return sb.toString();
	}

	@Override
	public String getMessage() {
		return constraint.getMessage();
	}
}
