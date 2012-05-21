/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2011 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         http://jgralab.uni-koblenz.de
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

package de.uni_koblenz.jgralab.greql2.evaluator.fa;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.parser.GreqlLexer;
import de.uni_koblenz.jgralab.greql2.parser.TokenTypes;
import de.uni_koblenz.jgralab.greql2.schema.IncDirection;
import de.uni_koblenz.jgralab.greql2.schema.SimpleIncidencePathDescription;

/**
 * This transition accepts a SimpleIncidencePathDescription. A SimpleIncidencePathDescription is
 * a Incidence-transition denoted by an Incidence-arrow (+>, <+ or <+>).
 * 
 * @author jtheegarten@uni-koblenz.de 2012, Diploma Thesis
 * 
 */
public class SimpleIncidenceTransition extends Transition {

	/**
	 * The collection of incidence-types that are accepted by this transition
	 */
	protected Set<String> roles;

	/**
	 * this transition may accept transitions in direction in, out or any
	 */
	protected IncDirection validDirection;

	/**
	 * returns a string which describes the incidence
	 */
	@Override
	public String incidenceString() {
		// String desc = "SimpleTransition";
		String desc = "SimpleIncidenceTransition (Dir:"
				+ validDirection.toString();
		if (roles != null) {
			desc = desc + "\n " + roles.toString() + "\n ";
		}
		desc += ")";
		return desc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * greql2.evaluator.fa.Transition#equalSymbol(greql2.evaluator.fa.EdgeTransition
	 * )
	 */
	@Override
	public boolean equalSymbol(Transition t) {
		if (!(t instanceof SimpleIncidenceTransition)) {
			return false;
		}
		SimpleIncidenceTransition et = (SimpleIncidenceTransition) t;
		if (!roles.equals(et.roles)) {
			return false;
		}
		if (!validDirection.equals(et.validDirection)) {
			return false;
		}

		return true;
	}

	/**
	 * Copy-constructor, creates a copy of the given transition
	 */
	protected SimpleIncidenceTransition(SimpleIncidenceTransition t,
			boolean addToStates) {
		super(t, addToStates);
		validDirection = t.validDirection;
		roles = new HashSet<String>(t.roles);
	}

	/**
	 * returns a copy of this transition
	 */
	@Override
	public Transition copy(boolean addToStates) {
		return new SimpleIncidenceTransition(this, addToStates);
	}

	/**
	 * Creates a new transition from start state to end state. The Transition
	 * accepts all incidences that have the right direction, role, startElementType and
	 * endElementType.
	 * 
	 * @param start
	 *            The state where this transition starts
	 * @param end
	 *            The state where this transition ends
	 * @param dir
	 *            The direction of the accepted incidences, may be IncDirection.IN,
	 *            IncDirection.OUT or IncDirection.ANY
	 */
	public SimpleIncidenceTransition(State start, State end, IncDirection dir) {
		super(start, end);
		validDirection = dir;
		roles = new HashSet<String>();
	}

	/**
	 * Creates a new transition from start state to end state. The Transition
	 * accepts all incidences that have the right direction, role, startElementType and
	 * endElementType.
	 * 
	 * This constructor creates a transition to accept a {@link SimpleIncidencePathDescription}.
	 * 
	 * @param start
	 *            The state where this transition starts
	 * @param end
	 *            The state where this transition ends
	 * @param dir
	 *            The direction of the accepted incidences, may be IncDirection.IN,
	 *            IncDirection.OUT or IncDirection.ANY
	 * @param typeCollection
	 *            The types which restrict the possible transitions
	 */
	public SimpleIncidenceTransition(State start, State end, IncDirection dir,
			Set<String> roles) {
		super(start, end);
		validDirection = dir;
		this.roles = roles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see greql2.evaluator.fa.Transition#reverse()
	 */
	@Override
	public void reverse() {
		super.reverse();
		if (validDirection == IncDirection.IN) {
			validDirection = IncDirection.OUT;
		} else if (validDirection == IncDirection.OUT) {
			validDirection = IncDirection.IN;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see greql2.evaluator.fa.Transition#isEpsilon()
	 */
	@Override
	public boolean isEpsilon() {
		return false;
	}

	@Override
	public boolean accepts(GraphElement<?, ?, ?, ?> e, Incidence i) {
		if (i == null) {
			return false;
		}

		boolean typeAccepted = false;
		for (String incClass : roles) {
			if (i.getType().getRolename().equals(incClass)) {
				typeAccepted = true;
				break;
			}
		}
		if (!typeAccepted) {
			return false;
		}

		boolean vertex = false;
		if (e instanceof Vertex) {
			vertex = true;
		}

		boolean outgoing = (vertex == (i.getDirection() == Direction.VERTEX_TO_EDGE));
		if (validDirection == IncDirection.OUT && !outgoing) {
			return false;
		} else if (validDirection == IncDirection.IN && outgoing) {
			return false;
		}

		return true;
	}

	@Override
	public String prettyPrint() {
		StringBuilder b = new StringBuilder();
		String delim = "";
		for (String c : roles) {
			b.append(delim);
			b.append(c);
			delim = ",";
		}
		String symbol = GreqlLexer.getTokenString(TokenTypes.IARROW);
		if (validDirection == IncDirection.IN) {
			symbol = GreqlLexer.getTokenString(TokenTypes.ILARROW);
		} else if (validDirection == IncDirection.OUT) {
			symbol = GreqlLexer.getTokenString(TokenTypes.IRARROW);
		}
		return symbol + "{" + b + "}";
	}

	@Override
	public boolean consumesIncidence() {
		return true;
	}

}