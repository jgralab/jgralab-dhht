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

package de.uni_koblenz.jgralab.greql2.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.pcollections.PSet;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib;

public class Slice {

	/**
	 * This HashMap stores references from a tuple (Element,State) to a list of
	 * tuples(ParentElement, ParentIncidence, ParentState, DistanceToRoot)
	 */
	private HashMap<PathSystemKey, List<PathSystemEntry>> keyToEntryMap;

	/**
	 * This HashMap stores references from an element to the first occurence of
	 * this element in the above HashMap<PathSystemKey, PathSystemEntry>
	 * keyToEntryMap
	 */
	private HashMap<GraphElement<?, ?, ?, ?>, PathSystemKey> elementToFirstKeyMap;

	/**
	 * This is the rootElement of the slice
	 */
	private PSet<GraphElement<?, ?, ?, ?>> sliCritElements;

	/**
	 * this set stores the keys of the leaves of this slice. It is created the
	 * first time it is needed. So the creation (which is in O(n²) ) has to be
	 * done only once.
	 */
	private ArrayList<PathSystemKey> leafKeys = null;

	/**
	 * returns the slicing criterion elements of this slice
	 */
	public PSet<GraphElement<?, ?, ?, ?>> getSlicingCriterionElements() {
		return sliCritElements;
	}

	/**
	 * This is a reference to the datagraph this slice is part of
	 */
	private Graph datagraph;

	/**
	 * returns the datagraph this slice is part of
	 */
	public Graph getDataGraph() {
		return datagraph;
	}

	/**
	 * returns the hashcode of this slice
	 */
	@Override
	public int hashCode() {
		return keyToEntryMap.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Slice)) {
			return false;
		}
		return keyToEntryMap.equals(((Slice) o).keyToEntryMap);
	}

	/**
	 * creates a new JValueSlice with the given rootElement in the given
	 * datagraph
	 */
	public Slice(Graph graph) {
		datagraph = graph;
		keyToEntryMap = new HashMap<PathSystemKey, List<PathSystemEntry>>();
		elementToFirstKeyMap = new HashMap<GraphElement<?, ?, ?, ?>, PathSystemKey>();
		sliCritElements = JGraLab.set();

	}

	private Queue<PathSystemEntry> entriesWithoutParentIncidence = new LinkedList<PathSystemEntry>();

	boolean isCleared = true;

	public void clearPathSystem() {
		if (!isCleared) {
			while (!entriesWithoutParentIncidence.isEmpty()) {
				PathSystemEntry te = entriesWithoutParentIncidence.poll();
				GraphElement<?, ?, ?, ?> p = te.getParentElement();
				if (p == null) {
					// root element
				} else {
					List<PathSystemEntry> pel = keyToEntryMap
							.get(new PathSystemKey(p, te.getParentStateNumber()));
					PathSystemEntry pe = pel.get(0);
					te.setParentIncidence(pe.getParentIncidence());
					te.setDistanceToRoot(pe.getDistanceToRoot());
					te.setParentStateNumber(pe.getParentStateNumber());
					te.setParentElement(pe.getParentElement());
					if (te.getParentIncidence() == null) {
						entriesWithoutParentIncidence.add(te);
					}
				}
			}
			isCleared = true;
		}
	}

	/**
	 * adds an element of the slice which is described by the parameters to the
	 * slicing criterion
	 * 
	 * @param element
	 *            the element to add
	 * @param stateNumber
	 *            the number of the DFAState the DFA was in when this element was
	 *            visited
	 * @param finalState
	 *            true if the element is visited by the dfa in a final state
	 */
	public void addSlicingCriterionElement(GraphElement<?, ?, ?, ?> element,
			int stateNumber, boolean finalState) {
		// System.out.println("Adding vertex " + vertex +
		// " as slicing criterion");
		PathSystemKey key = new PathSystemKey(element, stateNumber);
		PathSystemEntry entry = new PathSystemEntry(null, null, -1, 0,
				finalState);
		List<PathSystemEntry> entryList = new ArrayList<PathSystemEntry>();
		entryList.add(entry);
		keyToEntryMap.put(key, entryList);
		if (!elementToFirstKeyMap.containsKey(element)) {
			elementToFirstKeyMap.put(element, key);
		}
		leafKeys = null;
		sliCritElements = sliCritElements.plus(element);
	}

	/**
	 * adds an element of the slice which is described by the parameters to the
	 * slice
	 * 
	 * @param element
	 *            the element to add
	 * @param stateNumber
	 *            the number of the DFAState the DFA was in when this element was
	 *            visited
	 * @param parentIncidence
	 *            the incidence which leads from element to parentElement
	 * @param parentElement
	 *            the parentElement of the element in the slice
	 * @param parentStateNumber
	 *            the number of the DFAState the DFA was in when the
	 *            parentElement was visited
	 */
	public void addElement(GraphElement<?, ?, ?, ?> element, int stateNumber,
			Incidence parentIncidence, GraphElement<?, ?, ?, ?> parentElement,
			int parentStateNumber, boolean finalState) {
		PathSystemKey key = new PathSystemKey(element, stateNumber);
		List<PathSystemEntry> entryList = keyToEntryMap.get(key);
		if (entryList == null) {
			entryList = new ArrayList<PathSystemEntry>();
			keyToEntryMap.put(key, entryList);
			if (!elementToFirstKeyMap.containsKey(element)) {
				elementToFirstKeyMap.put(element, key);
			}
			leafKeys = null;
		}
		PathSystemEntry entry = new PathSystemEntry(parentElement,
				parentIncidence, parentStateNumber, 0, finalState);
		if (!entryList.contains(entry)) {
			entryList.add(entry);
		}
		if (parentIncidence == null) {
			entriesWithoutParentIncidence.add(entry);
			isCleared = false;
		}
	}

	/**
	 * Calculates the parent elements of the given element in this slice. If the
	 * given element exists more than one time in this slice, the first
	 * occurrence is used. If the given element is not part of this slice, an
	 * invalid JValue will be returned
	 */
	public PSet<GraphElement<?, ?, ?, ?>> parents(
			GraphElement<?, ?, ?, ?> element) {
		clearPathSystem();
		PathSystemKey key = elementToFirstKeyMap.get(element);
		return parents(key);
	}

	/**
	 * Calculates the parent elements of the given key in this slice.
	 */
	public PSet<GraphElement<?, ?, ?, ?>> parents(PathSystemKey key) {
		clearPathSystem();
		PSet<GraphElement<?, ?, ?, ?>> resultSet = JGraLab.set();

		for (PathSystemEntry entry : keyToEntryMap.get(key)) {
			resultSet = resultSet.plus(entry.getParentElement());
		}

		return resultSet;
	}

	/**
	 * Calculates the set of edges nodes in this slice.
	 */
	public PSet<Incidence> getIncidences() {
		clearPathSystem();
		PSet<Incidence> resultSet = JGraLab.set();
		for (Map.Entry<PathSystemKey, List<PathSystemEntry>> mapEntry : keyToEntryMap
				.entrySet()) {
			for (PathSystemEntry thisEntry : mapEntry.getValue()) {
				if (thisEntry.getParentIncidence() != null) {
					resultSet = resultSet.plus(thisEntry.getParentIncidence());
				}
			}
		}
		return resultSet;
	}

	public boolean contains(GraphElement elem) {
		for (Entry<PathSystemKey, List<PathSystemEntry>> e : keyToEntryMap
				.entrySet()) {

			if (e.getKey().getElement() == elem) {
				return true;
			}
			if (!(elem instanceof Edge)) {
				continue;
			}
			for (PathSystemEntry pse : e.getValue()) {
				if (pse.getParentIncidence() == elem) {
					return true;
				}
				// TODO: Don't we need to check parentVertex, too?? Or is that
				// the key?
			}
		}
		return false;
	}

	/**
	 * Calculates the set of nodes which are part of this slice.
	 */
	public PSet<GraphElement<?, ?, ?, ?>> getElements() {
		clearPathSystem();
		PSet<GraphElement<?, ?, ?, ?>> resultSet = JGraLab.set();
		for (PathSystemKey mapKey : keyToEntryMap.keySet()) {
			resultSet = resultSet.plus(mapKey.getElement());
		}

		return resultSet;
	}

	public PSet<Edge> getEdges() {
		clearPathSystem();
		PSet<Edge> resultSet = JGraLab.set();
		for (PathSystemKey mapKey : keyToEntryMap.keySet()) {
			if (mapKey.getElement() instanceof Edge) {
				resultSet = resultSet.plus((Edge) mapKey.getElement());
			}
		}
		return resultSet;
	}

	public PSet<Vertex> getVertices() {
		clearPathSystem();
		PSet<Vertex> resultSet = JGraLab.set();
		for (PathSystemKey mapKey : keyToEntryMap.keySet()) {
			if (mapKey.getElement() instanceof Vertex) {
				resultSet = resultSet.plus((Vertex) mapKey.getElement());
			}
		}
		return resultSet;
	}

	/**
	 * Calculates the set of leaves in this slice. Costs: O(n²) where n is the
	 * number of elements in the slice. The created set is stored as private
	 * field <code>leaves</code>, so the creation has to be done only once.
	 */
	public PSet<GraphElement<?, ?, ?, ?>> getLeaves() {
		clearPathSystem();
		PSet<GraphElement<?, ?, ?, ?>> leaves = JGraLab.set();
		if (leafKeys == null) {
			createLeafKeys();
		}
		// create the set of leaves out of the key set
		for (PathSystemKey key : leafKeys) {
			leaves = leaves.plus(key.getElement());
		}
		return leaves;
	}

	/**
	 * create the set of leaf keys if it is not already created
	 */
	private void createLeafKeys() {
		clearPathSystem();
		if (leafKeys != null) {
			return;
		}
		leafKeys = new ArrayList<PathSystemKey>();
		for (Map.Entry<PathSystemKey, List<PathSystemEntry>> mapEntry : keyToEntryMap
				.entrySet()) {
			boolean isFinal = false;
			for (PathSystemEntry entry : mapEntry.getValue()) {
				if (entry.getStateIsFinal()) {
					isFinal = true;
				}
			}
			if (isFinal) {
				leafKeys.add(mapEntry.getKey());
			}
		}
	}

	/**
	 * calculate the number of elements this slice has. If an element is part of
	 * this slice n times, it is counted n times
	 */
	public int weight() {
		clearPathSystem();
		return keyToEntryMap.size();
	}

	/**
	 * @return true if the given first element is a neighbour of the given second
	 *         element. That means there is an incidence from e1 to e2. If one or
	 *         both of the given elements are part of the slice more than once,
	 *         the first occurence is used. If one of the elements is not part
	 *         of this slice, false is returned
	 */
	public boolean isNeighbour(GraphElement<?, ?, ?, ?> e1,
			GraphElement<?, ?, ?, ?> e2) {
		clearPathSystem();
		PathSystemKey key1 = elementToFirstKeyMap.get(e1);
		PathSystemKey key2 = elementToFirstKeyMap.get(e2);
		return isNeighbour(key1, key2);
	}

	/**
	 * @return true if the given first key is a neighbour of the given second
	 *         key, that means, if there is an incidence from key1.element to
	 *         key2.element and the states match. If one of the keys is not
	 *         part of this slice, false is returned
	 */
	public boolean isNeighbour(PathSystemKey key1, PathSystemKey key2) {
		clearPathSystem();
		if ((key1 == null) || (key2 == null)) {
			return false;
		}
		for (PathSystemEntry entry1 : keyToEntryMap.get(key1)) {
			for (PathSystemEntry entry2 : keyToEntryMap.get(key2)) {
				if ((entry1.getParentElement() == key2.getElement())
						&& (entry1.getParentStateNumber() == key2
								.getStateNumber())) {
					return true;
				}
				if ((entry2.getParentElement() == key1.getElement())
						&& (entry2.getParentStateNumber() == key1
								.getStateNumber())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Prints the <key, List<entry>> map as single <key, entry> entries, i.e. a
	 * key may occur multiple times.
	 */
	public void printEntryMap() {
		clearPathSystem();
		if (FunLib.getLogger() == null) {
			return;
		}
		FunLib.getLogger().info("<Key, Entry> set of slice is:");
		for (Map.Entry<PathSystemKey, List<PathSystemEntry>> mapEntry : keyToEntryMap
				.entrySet()) {
			for (PathSystemEntry entry : mapEntry.getValue()) {
				FunLib.getLogger().info(
						mapEntry.getKey().toString() + " maps to "
								+ entry.toString());
			}
		}
	}

	/**
	 * Prints the <element, key map>.
	 */
	public void printKeyMap() {
		clearPathSystem();
		if (FunLib.getLogger() == null) {
			return;
		}
		Iterator<Map.Entry<GraphElement<?, ?, ?, ?>, PathSystemKey>> iter = elementToFirstKeyMap
				.entrySet().iterator();
		FunLib.getLogger().info("<Vertex, FirstKey> set of slice is:");
		while (iter.hasNext()) {
			Map.Entry<GraphElement<?, ?, ?, ?>, PathSystemKey> mapEntry = iter
					.next();
			PathSystemKey thisKey = mapEntry.getValue();
			GraphElement<?, ?, ?, ?> vertex = mapEntry.getKey();
			FunLib.getLogger().info(vertex + " maps to " + thisKey.toString());
		}
	}

	/**
	 * returns a string representation of this slice
	 */
	@Override
	public String toString() {
		clearPathSystem();
		Set<GraphElement<?, ?, ?, ?>> eset = new HashSet<GraphElement<?, ?, ?, ?>>();
		eset.addAll(elementToFirstKeyMap.keySet());
		Set<Incidence> iset = new HashSet<Incidence>();
		for (List<PathSystemEntry> pl : keyToEntryMap.values()) {
			for (PathSystemEntry pe : pl) {
				iset.add(pe.getParentIncidence());
			}
		}

		StringBuffer returnString = new StringBuffer("Slice: ");
		returnString.append("Vertices: ");
		boolean first = true;
		for (GraphElement<?, ?, ?, ?> e : eset) {
			if (first) {
				first = false;
				returnString.append(e);
			} else {
				returnString.append(", ");
				returnString.append(e);
			}
		}
		returnString.append(", Edges: ");
		first = true;
		for (Incidence i : iset) {
			if (first) {
				first = false;
				returnString.append(i);
			} else {
				returnString.append(", ");
				returnString.append(i);
			}
		}
		return returnString.toString();
	}
}
