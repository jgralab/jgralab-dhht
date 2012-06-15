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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.ObjectGraphMarker;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.IncDirection;
import de.uni_koblenz.jgralab.greql2.schema.SimpleIncidencePathDescription;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

/**
 * this class models a nondeterministic finite automaton. It created during
 * evaluation of the path-description via thompson-construction, but before
 * pathsearch, it will be transformed to an deterministic finite automaton
 * 
 * @see DFA
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class NFA extends FiniteAutomaton {

	private DFA dfa = null;

	@Override
	public DFA getDFA() {
		if (dfa == null) {
			dfa = new DFA(this);
		}
		return dfa;
	}

	/**
	 * Constructs a new NFA which is a copy of the given NFA. Depending on the
	 * parameter <code>realcopy</code> the NFA gets copied deep (that means,
	 * every transitions and every state gets copied) or flat (even the
	 * transition and stateList are the same object)
	 * 
	 * @param realcopy
	 *            : if true, a deep copy will be created
	 * 
	 */
	protected NFA(NFA nfaToCopy) {
		Map<Integer, State> oldStateToNewStateMap = new HashMap<Integer, State>();
		nfaToCopy.updateStateAttributes();
		finalStates = new ArrayList<State>();
		stateList = new ArrayList<State>();
		transitionList = new ArrayList<Transition>();
		// copy all states
		for (State currentState : nfaToCopy.stateList) {
			State newState = new State();
			newState.number = currentState.number;
			if (nfaToCopy.finalStates.contains(currentState)) {
				finalStates.add(newState);
			}
			if (nfaToCopy.initialState == currentState) {
				initialState = newState;
			}
			oldStateToNewStateMap.put(currentState.number, newState);
			stateList.add(newState);
		}
		updateStateAttributes();
		// now copy all transitions
		for (Transition currentTransition : nfaToCopy.transitionList) {
			Transition newTransition = currentTransition.copy(false);
			transitionList.add(newTransition);
			State startState = oldStateToNewStateMap.get(currentTransition
					.getStartState().number);
			State endState = oldStateToNewStateMap
					.get(currentTransition.endState.number);
			newTransition.setStartState(startState);
			newTransition.setEndState(endState);
		}
		updateStateAttributes();
	}

	/**
	 * creates a nfa for the given path description, calling the right
	 * create-method for this path description
	 */

	/**
	 * Constructs a NFA for the Kleene-Iteration p* of the given NFA.
	 * 
	 * @param iteratedNFA
	 *            the NFA for the path description p
	 * @param optional
	 *            if set to true, the resulting automaton will accept p*,
	 *            otherwise it will accept p+
	 * @return a NFA which accepts iteratedNFA* oder iteratedNFA+
	 */
	public static NFA createIteratedPathDescriptionNFA(NFA iteratedNFA,
			boolean optional) {
		State newFinalState;
		// if (iteratedNFA.finalStates.size() > 1) {
		// there are at least two final states, there should be only one, so
		// create a new one and epsilon-transitions
		newFinalState = new State();
		iteratedNFA.constructFinalStatesEpsilonTransitions(newFinalState, true);
		iteratedNFA.stateList.add(newFinalState);
		// this is the only final state
		iteratedNFA.finalStates.add(newFinalState);
		// }
		// else {
		// newFinalState = iteratedNFA.finalStates.get(0);
		// }
		Transition t = new EpsilonTransition(newFinalState,
				iteratedNFA.initialState);
		iteratedNFA.transitionList.add(t);
		if (optional) {
			// this is a kleene-star iteration, so also 0 iterations are
			// allowed, create a new epsilon-transition from start to end
			iteratedNFA.initialState.isFinal = true;
			iteratedNFA.finalStates.add(iteratedNFA.initialState);
		}
		iteratedNFA.updateStateAttributes();
		return iteratedNFA;
	}

	/**
	 * Constructs a NFA for the given sequential path description
	 * 
	 * @param nfaList
	 *            the list of NFAs that should be concatenated
	 * @return a NFA which accepts the concatenation of the given sequential
	 *         path description
	 */
	public static NFA createSequentialPathDescriptionNFA(List<NFA> nfaList) {
		NFA resultNFA = nfaList.get(0);
		for (int i = 1; i < nfaList.size(); i++) {
			NFA nextNFA = nfaList.get(i);
			resultNFA.constructFinalStatesEpsilonTransitions(
					nextNFA.initialState, true);
			resultNFA.stateList.addAll(nextNFA.stateList);
			resultNFA.finalStates.addAll(nextNFA.finalStates);
			resultNFA.transitionList.addAll(nextNFA.transitionList);
		}
		resultNFA.updateStateAttributes();
		return resultNFA;
	}

	/**
	 * Construct a NFA which accepts the given AlternativePaths
	 */
	public static NFA createAlternativePathDescriptionNFA(List<NFA> nfaList) {
		NFA resultNFA = new NFA();
		State finalState = new State();
		State initialState = new State();
		resultNFA.stateList.add(initialState);
		resultNFA.stateList.add(finalState);
		resultNFA.initialState = initialState;
		resultNFA.finalStates.add(finalState);
		// resultNFA.constructFinalStatesEpsilonTransitions(finalState, true);
		for (int i = 0; i < nfaList.size(); i++) {
			NFA nextNFA = nfaList.get(i);
			Transition t = new EpsilonTransition(resultNFA.initialState,
					nextNFA.initialState);
			resultNFA.transitionList.add(t);
			nextNFA.constructFinalStatesEpsilonTransitions(finalState, true);
			resultNFA.stateList.addAll(nextNFA.stateList);
			resultNFA.transitionList.addAll(nextNFA.transitionList);
		}
		resultNFA.updateStateAttributes();
		return resultNFA;
	}

	/**
	 * Constructs a NFA which accepts the given OptionalPathDescription
	 */
	public static NFA createOptionalPathDescriptionNFA(NFA optionalNFA) {
		Transition t = new EpsilonTransition(optionalNFA.initialState,
				optionalNFA.finalStates.get(0));
		optionalNFA.transitionList.add(t);
		optionalNFA.updateStateAttributes();
		return optionalNFA;
	}

	/**
	 * Reverts the given Path Description. Method is needed by
	 * createTransposedPathDescription and for evaluation of regular
	 * BackwardVertexSets. Does not create a new NFA
	 */
	public static NFA revertNFA(NFA nfa) {
		// first reverse all transitions
		Iterator<Transition> iter = nfa.transitionList.iterator();
		while (iter.hasNext()) {
			Transition t = iter.next();
			t.reverse();
		}
		// and create a new initial state, the former initial state will become
		// the final state
		State newInitialState = null;
		if (nfa.finalStates.size() > 1) {
			newInitialState = new State();
			nfa.stateList.add(newInitialState);
			Iterator<State> finalStateIter = nfa.finalStates.iterator();
			while (finalStateIter.hasNext()) {
				State s = finalStateIter.next();
				Transition t = new EpsilonTransition(newInitialState, s);
				nfa.transitionList.add(t);
			}
		} else {
			newInitialState = nfa.finalStates.get(0);
		}
		nfa.finalStates = new ArrayList<State>();
		nfa.finalStates.add(nfa.initialState);
		nfa.initialState = newInitialState;
		nfa.updateStateAttributes();
		return nfa;
	}

	/**
	 * Constructs a NFA which accepts the given TransposedPathDescription
	 */
	public static NFA createTransposedPathDescriptionNFA(NFA transposedNFA) {
		return revertNFA(transposedNFA);
	}

	/**
	 * Constructs a NFA which accepts the given ExponentiatedPathDescription
	 */
	public static NFA createExponentiatedPathDescriptionNFA(
			NFA exponentiatedNFA, int exponent) {
		NFA nfaToCopy = new NFA(exponentiatedNFA);

		for (int i = 1; i < exponent; i++) {
			NFA nextIterationNFA = new NFA(nfaToCopy);
			exponentiatedNFA.constructFinalStatesEpsilonTransitions(
					nextIterationNFA.initialState, true);
			exponentiatedNFA.finalStates.addAll(nextIterationNFA.finalStates);
			exponentiatedNFA.stateList.addAll(nextIterationNFA.stateList);
			exponentiatedNFA.transitionList
					.addAll(nextIterationNFA.transitionList);
		}
		exponentiatedNFA.updateStateAttributes();
		return exponentiatedNFA;
	}

	/**
	 * Constructs a NFA which accepts the given
	 * IntermediateVertexPathDescription
	 */
	public static NFA createIntermediateVertexPathDescriptionNFA(NFA firstNFA,
			VertexEvaluator intermediateVertices, NFA secondNFA) {

		State newFinalState = new State();
		firstNFA.stateList.add(newFinalState);
		firstNFA.constructFinalStatesEpsilonTransitions(newFinalState, true);

		IntermediateVertexTransition t = new IntermediateVertexTransition(
				newFinalState, secondNFA.initialState, intermediateVertices);
		firstNFA.transitionList.add(t);
		firstNFA.stateList.addAll(secondNFA.stateList);
		firstNFA.finalStates.addAll(secondNFA.finalStates);
		firstNFA.transitionList.addAll(secondNFA.transitionList);
		firstNFA.updateStateAttributes();
		return firstNFA;
	}

	/**
	 * Constructs a NFA which accepts the given EdgePathDescription The
	 * EdgeRestrictions (RoleId, TypeId) are modeled in (one of) the Transition(s).
	 */
	public static NFA createEdgePathDescriptionNFA(
			Transition.AllowedEdgeDirection dir, TypeCollection typeCollection,
			Set<String> roles, VertexEvaluator edgeEval,
			VertexEvaluator predicateEvaluator,
			ObjectGraphMarker<Vertex, VertexEvaluator> marker) {
		NFA nfa = new NFA();
		nfa.transitionList.clear();
		nfa.initialState.outTransitions.clear();
		State middleState1 = new State();
		nfa.stateList.add(middleState1);
		State middleState2 = new State();
		nfa.stateList.add(middleState2);
		nfa.finalStates.get(0).inTransitions.clear();

		translateEdgeToIncidences(dir, typeCollection, edgeEval,
				predicateEvaluator, nfa, middleState1, middleState2, roles,
				false);

		nfa.updateStateAttributes();
		return nfa;
	}

	/**
	 * Constructs an NFA which accepts the given {@link SimpleIncidencePathDescription}.
	 * 
	 * @param dir The direction of the incidence
	 * @param roles The valid 'types' of the incidence.
	 * @return An NFA accepting the incidence
	 */
	public static NFA createSimpleIncidencePathDescriptionNFA(IncDirection dir,
			Set<String> roles, ObjectGraphMarker<Vertex, VertexEvaluator> marker) {
		NFA nfa = new NFA();
		nfa.transitionList.clear();
		nfa.initialState.outTransitions.clear();
		nfa.finalStates.get(0).inTransitions.clear();
		SimpleIncidenceTransition t = new SimpleIncidenceTransition(
				nfa.initialState, nfa.finalStates.get(0), dir, roles);
		nfa.transitionList.add(t);
		nfa.updateStateAttributes();
		return nfa;
	}

	/**
	 * Constructs a NFA which accepts the given SimpleEdgePathDescription.
	 * This is done via translation of the Edge into two (or more)
	 * equivalent Incidences.
	 */
	public static NFA createSimpleEdgePathDescriptionNFA(
			Transition.AllowedEdgeDirection dir, TypeCollection typeCollection,
			Set<String> roles, VertexEvaluator predicateEvaluator,
			ObjectGraphMarker<Vertex, VertexEvaluator> marker) {
		NFA nfa = new NFA();
		nfa.transitionList.clear();
		nfa.initialState.outTransitions.clear();
		State middleState1 = new State();
		nfa.stateList.add(middleState1);
		State middleState2 = new State();
		nfa.stateList.add(middleState2);
		nfa.finalStates.get(0).inTransitions.clear();

		translateEdgeToIncidences(dir, typeCollection, null,
				predicateEvaluator, nfa, middleState1, middleState2, roles,
				false);
		nfa.updateStateAttributes();
		return nfa;
	}

	private static void translateEdgeToIncidences(
			Transition.AllowedEdgeDirection dir, TypeCollection typeCollection,
			VertexEvaluator elementRestr, VertexEvaluator predicateEvaluator,
			NFA nfa, State middleState1, State middleState2, Set<String> roles,
			boolean aggregation) {
		if (roles == null) {
			roles = new HashSet<String>();
		}
		// Translate the direction to the one used by the incidences
		IncDirection direction;
		boolean any = false;
		if (!(dir == Transition.AllowedEdgeDirection.ANY)) {
			direction = dir == Transition.AllowedEdgeDirection.OUT ? IncDirection.OUT
					: IncDirection.IN;
		} else {
			any = true;
			direction = IncDirection.OUT;
		}

		// Represent the edge as two incidences
		SimpleIncidenceTransition t1 = null;
		SimpleIncidenceTransition t2 = null;

		// If the edge is restricted, handle the restrictions
		Transition rt = null;

		t1 = new SimpleIncidenceTransition(nfa.initialState, middleState1,
				direction, roles);
		nfa.transitionList.add(t1);

		if (typeCollection != null) {
			if (elementRestr != null) {
				throw new UnsupportedOperationException(
						"Both Element- and TypeRestriction can not be handled at the same time.");
			}
			rt = new TypeRestrictionTransition(middleState1, middleState2,
					typeCollection, predicateEvaluator);

		} else if (elementRestr != null) {
			rt = new ElementRestrictionTransition(middleState1, middleState2,
					elementRestr, predicateEvaluator);
		} else {
			// If the edge is not restricted, use an epsilon-Transition between
			// both middle states.
			rt = new EpsilonTransition(middleState1, middleState2);
		}
		nfa.transitionList.add(rt);

		t2 = new SimpleIncidenceTransition(middleState2,
				nfa.finalStates.get(0), direction, roles);
		nfa.transitionList.add(t2);

		// A <-> (edge of any direction) is NOT equivalent to <+><+> (two
		// incidences of any direction). So it has to be translated to two
		// ways through the automaton (+>+> and <+<+)
		if (any) {
			State middleStateAlternative1 = new State();
			nfa.stateList.add(middleStateAlternative1);

			State middleStateAlternative2 = new State();
			nfa.stateList.add(middleStateAlternative2);

			direction = IncDirection.IN;
			SimpleIncidenceTransition t3 = new SimpleIncidenceTransition(
					nfa.initialState, middleStateAlternative1, direction);
			nfa.transitionList.add(t3);

			Transition rtAlt = null;
			if (typeCollection != null) {
				rtAlt = new TypeRestrictionTransition(middleStateAlternative1,
						middleStateAlternative2, typeCollection,
						predicateEvaluator);
			} else {
				rtAlt = new EpsilonTransition(middleStateAlternative1,
						middleStateAlternative2);
			}
			nfa.transitionList.add(rtAlt);

			SimpleIncidenceTransition t4 = new SimpleIncidenceTransition(
					middleStateAlternative2, nfa.finalStates.get(0), direction);
			nfa.transitionList.add(t4);
		}
	}

	/**
	 * Constructs a NFA which accepts the given AggregationPathDescription. The
	 * EdgeRestrictions (RoleId, TypeId) are modeled in the middle Transition.
	 */
	public static NFA createAggregationPathDescriptionNFA(
			boolean aggregateFrom, TypeCollection typeCollection,
			Set<String> roles, VertexEvaluator predicateEvaluator,
			ObjectGraphMarker<Vertex, VertexEvaluator> marker) {
		NFA nfa = new NFA();
		nfa.transitionList.clear();
		nfa.initialState.outTransitions.clear();
		nfa.finalStates.get(0).inTransitions.clear();
		State middleState1 = new State();
		nfa.stateList.add(middleState1);
		State middleState2 = new State();
		nfa.stateList.add(middleState2);
		translateEdgeToIncidences(
				aggregateFrom ? Transition.AllowedEdgeDirection.OUT
						: Transition.AllowedEdgeDirection.IN, typeCollection,
				null, predicateEvaluator, nfa, middleState1, middleState2,
				roles, true);
		nfa.updateStateAttributes();
		return nfa;
	}

	/**
	 * Constructs a new epsilon-NFA, this is a NFA which has only two states,
	 * one initial and one final state. There is only one transition, which is a
	 * epsilon-transition from the initial to the final state
	 */
	protected NFA() {
		finalStates = new ArrayList<State>();
		transitionList = new ArrayList<Transition>();
		stateList = new ArrayList<State>();
		initialState = new State();
		State finalState = new State();
		finalStates.add(finalState);
		stateList.add(initialState);
		stateList.add(finalState);
		EpsilonTransition t = new EpsilonTransition(initialState, finalState);
		transitionList.add(t);
	}

	/**
	 * creates a new final state to the nfa that accepts a goal restriction
	 * 
	 * @param nfa
	 *            the NFA to add the goal restriction to
	 * @param typeCollection
	 *            The allowed types of the goal vertex
	 */
	public static void addGoalTypeRestriction(NFA nfa,
			TypeCollection typeCollection) {
		State newEndState;
		if (nfa.finalStates.size() == 1) {
			newEndState = nfa.finalStates.get(0);
			newEndState.isFinal = false;
			nfa.finalStates.clear();
		} else {
			newEndState = new State();
			nfa.constructFinalStatesEpsilonTransitions(newEndState, false);
			nfa.stateList.add(newEndState);
			for (State s : nfa.finalStates) {
				s.isFinal = false;
			}
			nfa.finalStates.clear();
		}
		State restrictedFinalState = new State();
		nfa.stateList.add(restrictedFinalState);
		nfa.finalStates.add(restrictedFinalState);
		TypeRestrictionTransition trans = new TypeRestrictionTransition(
				newEndState, restrictedFinalState, typeCollection, null);
		nfa.transitionList.add(trans);
	}

	/**
	 * Adds a boolean expression as goal restriction
	 * 
	 * @param nfa
	 *            the NFA to add the goal restriction to
	 * @param boolEval
	 *            the VertexEvaluator, which restricts this nfa
	 */
	public static void addGoalBooleanRestriction(NFA nfa,
			VertexEvaluator boolEval,
			ObjectGraphMarker<Vertex, VertexEvaluator> marker) {
		State newEndState;
		if (nfa.finalStates.size() == 1) {
			newEndState = nfa.finalStates.get(0);
			newEndState.isFinal = false;
			nfa.finalStates.clear();
		} else {
			newEndState = new State();
			nfa.constructFinalStatesEpsilonTransitions(newEndState, false);
			nfa.stateList.add(newEndState);
			for (State s : nfa.finalStates) {
				s.isFinal = false;
			}
			nfa.finalStates.clear();
		}
		State restrictedFinalState = new State();
		nfa.stateList.add(restrictedFinalState);
		nfa.finalStates.add(restrictedFinalState);
		TypeRestrictionTransition transition = new TypeRestrictionTransition(
				newEndState, restrictedFinalState, null, boolEval);
		nfa.transitionList.add(transition);
	}

	/**
	 * Adds a boolean expression as start restriction
	 * 
	 * @param nfa
	 *            the NFA to add the start restriction to
	 * @param boolEval
	 *            the VertexEvaluator, which restricts this nfa
	 */
	public static void addStartBooleanRestriction(NFA nfa,
			VertexEvaluator boolEval,
			ObjectGraphMarker<Vertex, VertexEvaluator> marker) {
		State newInitialState = new State();
		nfa.stateList.add(newInitialState);
		TypeRestrictionTransition transition = new TypeRestrictionTransition(
				newInitialState, nfa.initialState, null, boolEval);
		nfa.transitionList.add(transition);
		nfa.initialState = newInitialState;
	}

	/**
	 * creates a new initial state to the nfa and a transition from the new
	 * state to the old initial state that accepts a start restriction
	 * 
	 * @param nfa
	 *            the NFA to add the start restriction to
	 * @param typeCollection
	 *            The allowed types of the start vertex
	 */
	public static void addStartTypeRestriction(NFA nfa,
			TypeCollection typeCollection) {
		State newInitialState = new State();
		nfa.stateList.add(newInitialState);
		TypeRestrictionTransition trans = new TypeRestrictionTransition(
				newInitialState, nfa.initialState, typeCollection, null);
		nfa.transitionList.add(trans);
		nfa.initialState = newInitialState;
	}

	/**
	 * constructs epsilon-transition from all final states of this nfa to the
	 * given state
	 * 
	 * @param target
	 *            the target state of all transitions which will be created
	 * @param removeFinalStates
	 *            if set to true, all final states will become normal states
	 */
	protected void constructFinalStatesEpsilonTransitions(State target,
			boolean removeFinalStates) {
		Iterator<State> iter = finalStates.iterator();
		while (iter.hasNext()) {
			EpsilonTransition t = new EpsilonTransition(iter.next(), target);
			transitionList.add(t);
			if (removeFinalStates) {
				iter.remove();
			}
		}
	}

	/**
	 * For testing purposes of CodeGenerator only!
	 * @return
	 */

	// public static NFA createSimpleIncidenceTransition_Db() {
	// NFA nfa = new NFA();
	// State startState = new State();
	// startState.number = 0;
	// nfa.stateList.add(startState);
	// nfa.initialState = startState;
	// State interState = new State();
	// nfa.stateList.add(interState);
	// interState.number = 1;
	// State endState = new State();
	// nfa.stateList.add(endState);
	// endState.isFinal = true;
	// endState.number = 2;
	// nfa.finalStates.add(endState);
	// Set<TypedElementClass> types = new HashSet<TypedElementClass>();
	// types.add(Greql2Schema.instance()
	// .getIncidenceClassesInTopologicalOrder().get(7));
	// TypeCollection typeColl = new TypeCollection(types, false);
	// Transition t = new SimpleIncidenceTransition_Db(startState, interState,
	// IncDirection.IN, typeColl);
	// nfa.transitionList.add(t);
	// t = new SimpleIncidenceTransition_Db(endState, interState,
	// IncDirection.IN, typeColl);
	// nfa.transitionList.add(t);
	// t = new AggregationIncidenceTransition_Db(interState, endState,
	// typeColl);
	// nfa.transitionList.add(t);
	// return nfa;
	// }

}
