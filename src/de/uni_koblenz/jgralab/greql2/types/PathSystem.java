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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.pcollections.PSet;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib;
import de.uni_koblenz.jgralab.greql2.schema.EdgeDirection;

public class PathSystem {

	/**
	 * This HashMap stores references from a tuple (Vertex,State) to a
	 * tuple(ParentVertex, ParentEdge, ParentState, DistanceToRoot)
	 */
	private final HashMap<PathSystemKey, PathSystemEntry> keyToEntryMap;

	/**
	 * This HashMap stores references from an element which is a leaf is the path
	 * system to the first occurence of this element as a leaf in the above
	 * HashMap<PathSystemKey, PathSystemEntry> keyToEntryMap
	 */
	private final HashMap<GraphElement<?, ?, ?, ?>, PathSystemKey> leafElementToLeafKeyMap;

	/**
	 * This HashMap stores references from a vertex in the path system to the
	 * first occurence of this vertex in the above HashMap<PathSystemKey,
	 * PathSystemEntry> keyToEntryMap
	 */
	private final HashMap<GraphElement<?, ?, ?, ?>, PathSystemKey> elementToFirstKeyMap;

	/**
	 * This is the rootvertex of the pathsystem
	 */
	private GraphElement<?, ?, ?, ?> rootElement;

	/**
	 * stores if the pathsystem is finished
	 */
	private boolean finished = false;

	/**
	 * this set stores the keys of the leaves of this pathsystem. It is created
	 * the first time it is needed. So the creation (which is in O(n²) ) has to
	 * be done only once.
	 */
	private List<PathSystemKey> leafKeys = null;

	/**
	 * returns the rootVertex of this pathSystem
	 */
	public GraphElement<?, ?, ?, ?> getRootVertex() {
		return rootElement;
	}

	/**
	 * This is a reference to the datagraph this pathsystem is part of
	 */
	private final Graph datagraph;

	/**
	 * returns the datagraph this PathSystem is part of
	 */
	public Graph getDataGraph() {
		return datagraph;
	}

	/**
	 * returns the hashcode of this PathSystem
	 */
	@Override
	public int hashCode() {
		assertFinished();
		return keyToEntryMap.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		assertFinished();
		if (o == null || !(o instanceof PathSystem)) {
			return false;
		}
		return keyToEntryMap.equals(((PathSystem) o).keyToEntryMap);
	}

	/**
	 * finished the path system, after a call of this method, further changes
	 * are not possible
	 */
	public void finish() {
		completePathSystem();
		createLeafKeys();
		finished = true;
	}

	/**
	 * creates a new JValuePathSystem with the given rootVertex in the given
	 * datagraph
	 */
	public PathSystem(Graph graph) {
		datagraph = graph;
		keyToEntryMap = new HashMap<PathSystemKey, PathSystemEntry>();
		leafElementToLeafKeyMap = new HashMap<GraphElement<?, ?, ?, ?>, PathSystemKey>();
		elementToFirstKeyMap = new HashMap<GraphElement<?, ?, ?, ?>, PathSystemKey>();
	}

	private final Queue<PathSystemEntry> entriesWithoutParentEdge = new LinkedList<PathSystemEntry>();

	/**
	 * to some vertices there is a path with an vertex restriction on the end
	 * and thus the last transition in the dfa does not accept an edge - hence,
	 * the parent edge is not set. This method finds those vertices and set the
	 * edge information
	 */
	private void completePathSystem() {
		assertUnfinished();
		while (!entriesWithoutParentEdge.isEmpty()) {
			PathSystemEntry te = entriesWithoutParentEdge.poll();
			PathSystemEntry pe = null;
			if (te.getParentElement() != null) {
				pe = keyToEntryMap.get(new PathSystemKey(te.getParentElement(),
						te.getParentStateNumber()));
			} else {
				PathSystemKey key = new PathSystemKey(rootElement,
						te.getParentStateNumber());
				pe = keyToEntryMap.get(key);
			}
			// if pe is null, te is the entry of the root vertex
			if (pe != null) {
				te.setParentIncidence(pe.getParentIncidence());
				te.setDistanceToRoot(pe.getDistanceToRoot());
				te.setParentStateNumber(pe.getParentStateNumber());
				te.setParentElement(pe.getParentElement());
			}
		}
	}

	/**
	 * adds an element of the PathSystem which is described by the parameters to
	 * the PathSystem
	 * 
	 * @param element
	 *            the element to add
	 * @param stateNumber
	 *            the number of the DFAState the DFA was in when this element was
	 *            visited
	 * @param finalState
	 *            true if the rootelement is visited by the dfa in a final state
	 */
	public void setRootElement(GraphElement<?, ?, ?, ?> element,
			int stateNumber, boolean finalState) {
		assertUnfinished();
		PathSystemKey key = new PathSystemKey(element, stateNumber);
		PathSystemEntry entry = new PathSystemEntry(null, null, -1, 0,
				finalState);
		keyToEntryMap.put(key, entry);
		if (finalState && !leafElementToLeafKeyMap.containsKey(element)) {
			leafElementToLeafKeyMap.put(element, key);
		}
		elementToFirstKeyMap.put(element, key);
		leafKeys = null;
		rootElement = element;
	}

	/**
	 * adds an element of the PathSystem which is described by the parameters to
	 * the PathSystem
	 * 
	 * @param element
	 *            the element to add
	 * @param stateNumber
	 *            the number of the DFAState the DFA was in when this element was
	 *            visited
	 * @param parentIncidence
	 *            the incidence which leads from element to parentElement
	 * @param parentElement
	 *            the parentElement of the element in the PathSystem
	 * @param parentStateNumber
	 *            the number of the DFAState the DFA was in when the
	 *            parentElement was visited
	 * @param distance
	 *            the distance to the rootElement of the PathSystem
	 */
	public void addElement(GraphElement<?, ?, ?, ?> element, int stateNumber,
			Incidence parentIncidence, GraphElement<?, ?, ?, ?> parentElement,
			int parentStateNumber, int distance, boolean finalState) {
		assertUnfinished();
		PathSystemKey key = new PathSystemKey(element, stateNumber);
		PathSystemEntry entry = keyToEntryMap.get(key);
		if ((entry == null)
				|| ((entry.getDistanceToRoot() > distance) && (!entry
						.getStateIsFinal() || finalState))) {
			entry = new PathSystemEntry(parentElement, parentIncidence,
					parentStateNumber, distance, finalState);
			keyToEntryMap.put(key, entry);
			// add vertex to leaves
			if (finalState) {
				PathSystemKey existingLeafkey = leafElementToLeafKeyMap
						.get(element);
				if ((existingLeafkey == null)
						|| (keyToEntryMap.get(existingLeafkey)
								.getDistanceToRoot() > distance)) {
					leafElementToLeafKeyMap.put(element, key);
				}
			}
			if (parentIncidence != null) {
				PathSystemKey firstKey = elementToFirstKeyMap.get(element);
				if ((firstKey == null)
						|| (keyToEntryMap.get(firstKey).getDistanceToRoot() > distance)) {
					elementToFirstKeyMap.put(element, key);
				}
			} else {
				if (!(element == rootElement && distance == 0)) {
					entriesWithoutParentEdge.add(entry);
				}
			}
		}
	}

	/**
	 * Calculates the set of children the given element has in this PathSystem.
	 * If the given element exists more than one times in this slice, the first
	 * occurrence if used.
	 */
	public PSet<GraphElement<?, ?, ?, ?>> children(
			GraphElement<?, ?, ?, ?> element) {
		PathSystemKey key = elementToFirstKeyMap.get(element);
		return children(key);
	}

	/**
	 * Calculates the set of child the given key has in this PathSystem
	 */
	public PSet<GraphElement<?, ?, ?, ?>> children(PathSystemKey key) {
		assertFinished();

		PSet<GraphElement<?, ?, ?, ?>> returnSet = JGraLab.set();
		for (Map.Entry<PathSystemKey, PathSystemEntry> mapEntry : keyToEntryMap
				.entrySet()) {
			PathSystemEntry thisEntry = mapEntry.getValue();
			if ((thisEntry.getParentElement() == key.getElement())
					&& (thisEntry.getParentStateNumber() == key
							.getStateNumber())) {
				returnSet = returnSet.plus(mapEntry.getKey().getElement());
			}
		}
		return returnSet;
	}

	/**
	 * Calculates the set of siblings of the given element in this PathSystem. If
	 * the given element exists more than one times in this pathsystem, the first
	 * occurence if used.
	 */
	public PSet<GraphElement<?, ?, ?, ?>> siblings(
			GraphElement<?, ?, ?, ?> element) {
		PathSystemKey key = elementToFirstKeyMap.get(element);
		return siblings(key);
	}

	/**
	 * Calculates the set of children the given key has in this PathSystem
	 */
	public PSet<GraphElement<?, ?, ?, ?>> siblings(PathSystemKey key) {
		assertFinished();
		PathSystemEntry entry = keyToEntryMap.get(key);
		if (entry == null) {
			return null;
		}
		PSet<GraphElement<?, ?, ?, ?>> returnSet = JGraLab.set();
		for (Map.Entry<PathSystemKey, PathSystemEntry> mapEntry : keyToEntryMap
				.entrySet()) {
			PathSystemEntry value = mapEntry.getValue();

			if ((value.getParentElement() == entry.getParentElement())
					&& (value.getParentStateNumber() == entry
							.getParentStateNumber())
					&& (mapEntry.getKey().getElement() != key.getElement())) {
				returnSet = returnSet.plus(mapEntry.getKey().getElement());
			}
		}
		return returnSet;
	}

	/**
	 * Calculates the parent element of the given element in this PathSystem. If
	 * the given element exists more than one times in this pathsystem, the first
	 * occurrence if used. If the given element is not part of this pathsystem, a
	 * invalid JValue will be returned
	 */
	public GraphElement<?, ?, ?, ?> parent(GraphElement<?, ?, ?, ?> element) {
		PathSystemKey key = elementToFirstKeyMap.get(element);
		return parent(key);
	}

	/**
	 * Calculates the parent element of the given key in this PathSystem.
	 */
	public GraphElement<?, ?, ?, ?> parent(PathSystemKey key) {
		assertFinished();

		if (key == null) {
			return null;
		}
		PathSystemEntry entry = keyToEntryMap.get(key);
		return entry.getParentElement();
	}

	/**
	 * Checks, wether the given element (vertex or edge) is part of this
	 * pathsystem
	 * 
	 * @return true, if the element is part of this system, false otherwise
	 */
	public boolean contains(GraphElement elem) {
		assertFinished();
		for (Map.Entry<PathSystemKey, PathSystemEntry> entry : keyToEntryMap
				.entrySet()) {
			if (entry.getKey().getElement() == elem) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calculates the number of incoming or outgoing incidences of the given element
	 * which are part of this PathSystem
	 * 
	 * @param element
	 *            the element for which the number of incidences gets counted
	 * @param direction
	 *            direction of elements to be counted
	 * @param typeCol
	 *            the JValueTypeCollection which toggles whether a type is
	 *            accepted or not
	 * @return the number of incidences with the given orientation connected to the
	 *         given element or -1 if the given element is not part of this
	 *         PathSystem
	 */
	public int degree(GraphElement<?, ?, ?, ?> element,
			EdgeDirection direction, TypeCollection typeCol) {
		assertFinished();

		if (element == null) {
			return -1;
		}
		int degree = 0;
		// boolean countIncomingEdges = direction == EdgeDirection.IN
		// || direction == EdgeDirection.INOUT;
		// boolean countOutgoingEdges = direction == EdgeDirection.OUT
		// || direction == EdgeDirection.INOUT;

		// for (Entry<PathSystemKey, PathSystemEntry> entry : keyToEntryMap
		// .entrySet()) {
		// if (isAcceptedByTypeCollection(typeCol, entry)) {
		// if (countOutgoingEdges && isOutgoingEdge(entry, vertex)) {
		// degree++;
		// }
		// if (countIncomingEdges && isIncommingEdge(entry, vertex)) {
		// degree++;
		// }
		// }
		// }
		return degree;
	}

	public boolean isAcceptedByTypeCollection(TypeCollection typeCollection,
			Entry<PathSystemKey, PathSystemEntry> entry) {
		return typeCollection == null
				|| typeCollection.acceptsType(entry.getValue()
						.getParentIncidence().getType());
	}

	public boolean isOutgoingEdge(Entry<PathSystemKey, PathSystemEntry> entry,
			GraphElement<?, ?, ?, ?> element) {
		return entry.getValue().getParentElement() == element;
	}

	public boolean isIncommingEdge(Entry<PathSystemKey, PathSystemEntry> entry,
			GraphElement<?, ?, ?, ?> element) {
		return entry.getKey().getElement() == element;
	}

	/**
	 * Calculates the number of incomming and outgoing incidences of the given element
	 * which are part of this PathSystem
	 * 
	 * @param element
	 *            the element for which the number of incidences gets counted
	 * @param typeCol
	 *            the JValueTypeCollection which toggles wether a type is
	 *            accepted or not
	 * @return the number of incidences connected to the given element or -1 if the
	 *         given element is not part of this pathsystem
	 */
	public int degree(GraphElement<?, ?, ?, ?> element, TypeCollection typeCol) {
		assertFinished();

		if (element == null) {
			return -1;
		}
		int degree = 0;
		for (Map.Entry<PathSystemKey, PathSystemEntry> entry : keyToEntryMap
				.entrySet()) {
			PathSystemEntry pe = entry.getValue();
			if ((typeCol == null)
					|| typeCol.acceptsType(pe.getParentIncidence().getType())) {
				if (pe.getParentElement() == element) {
					degree++;
				}
				// cannot transform two if statements to one if with an or,
				// because an edge may be a loop
				if (entry.getKey().getElement() == element) {
					degree++;
				}
			}
		}
		return degree;
	}

	/**
	 * Calculates the set of incoming or outgoing incidences of the given element,
	 * which are also part of this pathsystem
	 * 
	 * @param element 
	 *            the element for which the edgeset will be created
	 * @param direction
	 *            direction of incidences to be returned
	 * @return a set of incidences with the given orientation connected to the given
	 *         element or an empty set, if the element is not part of this
	 *         pathsystem
	 */
	public PSet<Incidence> edgesConnected(GraphElement<?, ?, ?, ?> element,
			Direction direction) {
		assertFinished();
		if (element == null) {
			return null;
		}
		PSet<Incidence> resultSet = JGraLab.set();
		for (Map.Entry<PathSystemKey, PathSystemEntry> entry : keyToEntryMap
				.entrySet()) {

			// TODO reduce switch case
			if (entry.getKey().getElement() == element) {
				Incidence inc = entry.getValue().getParentIncidence();
				if (inc == null) {
					continue;
				}
				// switch (direction) {
				// case EDGE_TO_VERTEX:
				// if (edge.isNormal()) {
				// addEdgeToResult(resultSet, edge, vertex);
				// }
				// break;
				// case VERTEX_TO_EDGE:
				// if (!edge.isNormal()) {
				// addEdgeToResult(resultSet, edge, vertex);
				// }
				// break;
				// case BOTH:
				// addEdgeToResult(resultSet, edge, vertex);
				// break;
				// default:
				// throw new RuntimeException(
				// "FIXME: Incomplete switch statement in JValuePathSystem");
				// }
			} else if (entry.getValue().getParentElement() == element) {
				Incidence inc = entry.getValue().getParentIncidence();
				if (inc == null) {
					continue;
				}
				// switch (direction) {
				// case IN:
				// if (!edge.isNormal()) {
				// resultSet = addEdgeToResult(resultSet, edge, vertex);
				// }
				// break;
				// case OUT:
				// if (edge.isNormal()) {
				// resultSet = addEdgeToResult(resultSet, edge, vertex);
				// }
				// break;
				// case INOUT:
				// resultSet = addEdgeToResult(resultSet, edge, vertex);
				// break;
				// default:
				// throw new RuntimeException(
				// "FIXME: Incomplete switch statement in JValuePathSystem");
				// }
			}
		}
		return null;
		// return resultSet;
	}

	private PSet<Incidence> addIncidenceToResult(PSet<Incidence> resultSet,
			Incidence incidence, GraphElement<?, ?, ?, ?> element) {
		// if (context == edge.getAlpha()) {
		// return resultSet.plus(edge.getNormalEdge());
		// } else if (context == edge.getOmega()) {
		// return resultSet.plus(edge.getNormalEdge().getReversedEdge());
		// } else {
		// return resultSet;
		// }
		return null;
	}

	/**
	 * Calculates the set of incidences which are connected to the given element, and
	 * which are also part of this pathsystem
	 * 
	 * @param element
	 *            the element for which the edgeset will be created
	 * @return a set of incidences connected to the given element or an empty set, if
	 *         the element is not part of this pathsystem
	 */
	public PSet<Incidence> edgesConnected(GraphElement<?, ?, ?, ?> element) {
		assertFinished();
		if (element == null) {
			return null;
		}
		PSet<Incidence> resultSet = JGraLab.set();

		for (Map.Entry<PathSystemKey, PathSystemEntry> entry : keyToEntryMap
				.entrySet()) {
			if (entry.getValue().getParentElement() == element) {
				resultSet = resultSet.plus(entry.getValue()
						.getParentIncidence());
			}
			if (entry.getKey().getElement() == element) {
				resultSet = resultSet.plus(entry.getValue()
						.getParentIncidence());
			}
		}
		return resultSet;
	}

	/**
	 * Calculates the set of leaves in this PathSystem. Costs: O(n) where n is
	 * the number of elements in the path system. The created set is stored as
	 * private field <code>leaves</code>, so the creating has to be done only
	 * once.
	 */
	public PSet<GraphElement<?, ?, ?, ?>> getLeaves() {
		assertFinished();

		PSet<GraphElement<?, ?, ?, ?>> leaves = JGraLab.set();
		// create the set of leaves out of the key set
		for (PathSystemKey key : leafKeys) {
			leaves = leaves.plus(key.getElement());
		}
		return leaves;
	}

	/**
	 * create the set of leaf keys
	 */
	private void createLeafKeys() {
		assertUnfinished();

		if (leafKeys != null) {
			return;
		}
		leafKeys = new LinkedList<PathSystemKey>();
		for (Map.Entry<PathSystemKey, PathSystemEntry> entry : keyToEntryMap
				.entrySet()) {
			if (entry.getValue().getStateIsFinal()) {
				leafKeys.add(entry.getKey());
			}
		}
	}

	/**
	 * Extract the path which starts with the root element and ends with the
	 * given element from the PathSystem. If the given element exists more than
	 * one time in this pathsystem, the first occurrence is used. If the given
	 * element is not part of this pathsystem, null will be returned
	 * 
	 * @param GraphElement<?, ?, ?, ?>
	 * @return a HyperPath from rootElement to given element
	 */
	public HyperPath extractPath(GraphElement<?, ?, ?, ?> element) {
		assertFinished();
		PathSystemKey key = leafElementToLeafKeyMap.get(element);
		return extractPath(key);
	}

	/**
	 * Extract the path which starts with the root element and ends with the
	 * given element from the PathSystem.
	 * 
	 * @param key
	 *            the pair (GraphElement, Statenumber) which is the target of the path
	 * @return a Path from rootElement to given element
	 */
	public HyperPath extractPath(PathSystemKey key) {
		assertFinished();
		HyperPath path = HyperPath.start(key.getElement());
		// while (key != null) {
		// PathSystemEntry entry = keyToEntryMap.get(key);
		// if (entry.getParentEdge() != null) {
		// path = path.append(entry.getParentEdge().getReversedEdge());
		// key = new PathSystemKey(entry.getParentVertex(),
		// entry.getParentStateNumber());
		// } else {
		// key = null;
		// }
		// }
		return path.reverse();
	}

	/**
	 * Extract the set of paths which are part of this path system. These paths
	 * start with the root element and end with a leaf.
	 * 
	 * @return a set of HyperPaths from rootElement to leaves
	 */
	public PSet<HyperPath> extractPaths() {
		assertFinished();
		PSet<HyperPath> pathSet = JGraLab.set();
		for (PathSystemKey leaf : leafKeys) {
			pathSet = pathSet.plus(extractPath(leaf));
		}
		return pathSet;
	}

	/**
	 * Extracts all paths with lengths equal to <code>len</code>
	 * 
	 * @return a set of Paths from rootVertex to leaves
	 */
	public PSet<HyperPath> extractPaths(int len) {
		assertFinished();
		PSet<HyperPath> pathSet = JGraLab.set();
		for (PathSystemKey leaf : leafKeys) {
			HyperPath path = extractPath(leaf);
			if (path.getLength() == len) {
				pathSet = pathSet.plus(path);
			}
		}
		return pathSet;
	}

	/**
	 * calculate the number of element this pathsystem has. If a element is part
	 * of this PathSystem n times, it is counted n times
	 */
	public int getWeight() {
		assertFinished();
		return keyToEntryMap.size();
	}

	/**
	 * calculates the depth of this pathtree
	 */
	public int getDepth() {
		assertFinished();
		int maxdepth = 0;
		for (Map.Entry<PathSystemKey, PathSystemEntry> entry : keyToEntryMap
				.entrySet()) {
			PathSystemEntry thisEntry = entry.getValue();
			if (thisEntry.getDistanceToRoot() > maxdepth) {
				maxdepth = thisEntry.getDistanceToRoot();
			}
		}
		return maxdepth;
	}

	/**
	 * Calculates the distance between the root element of this path system and
	 * the given element. If the given element is part of the pathsystem more
	 * than one times, the first occurence is used
	 * 
	 * @return the distance or -1 if the given element is not part of this path
	 *         system
	 */
	public int distance(GraphElement<?, ?, ?, ?> element) {
		PathSystemKey key = elementToFirstKeyMap.get(element);
		return distance(key);
	}

	/**
	 * Calculates the distance between the root element of this path system and
	 * the given key
	 * 
	 * @return the distance or -1 if the given element is not part of this path
	 *         system.
	 */
	private int distance(PathSystemKey key) {
		assertFinished();

		if (key == null) {
			return -1;
		}
		PathSystemEntry entry = keyToEntryMap.get(key);
		return entry.getDistanceToRoot();
	}

	/**
	 * Calculates the shortest distance between a leaf of this pathsystem and
	 * the root element, this is the length of the shortest path in this
	 * pathsystem
	 */
	public int minPathLength() {
		assertFinished();

		int minDistance = Integer.MAX_VALUE;
		for (PathSystemKey key : leafKeys) {
			PathSystemEntry entry = keyToEntryMap.get(key);
			if (entry.getDistanceToRoot() < minDistance) {
				minDistance = entry.getDistanceToRoot();
			}
		}
		return minDistance;
	}

	/**
	 * Calculates the longest distance between a leaf of this pathsystem and the
	 * root element, this is the length of the longest path in this pathsystem
	 */
	public int maxPathLength() {
		assertFinished();

		int maxDistance = 0;
		for (PathSystemKey key : leafKeys) {
			PathSystemEntry entry = keyToEntryMap.get(key);
			if (entry.getDistanceToRoot() > maxDistance) {
				maxDistance = entry.getDistanceToRoot();
			}
		}
		return maxDistance;
	}

	/**
	 * @return true if the given first vertex is a neighbour of the given second
	 *         vertex, that means, if there is a edge in the pathtree from v1 to
	 *         v2. If one or both of the given vertices are part of the
	 *         pathsystem more than one times, the first occurence is used. If
	 *         one of the vertices is not part of this pathsystem, false is
	 *         returned
	 */
	public boolean isNeighbour(Vertex v1, Vertex v2) {
		PathSystemKey key1 = elementToFirstKeyMap.get(v1);
		PathSystemKey key2 = elementToFirstKeyMap.get(v2);
		return isNeighbour(key1, key2);
	}

	/**
	 * @return true if the given first key is a neighbour of the given second
	 *         key, that means, if there is a edge in the pathtree from
	 *         key1.element to key2.element and the states matches. If one of the
	 *         keys is not part of this pathsystem, false is returned
	 */
	public boolean isNeighbour(PathSystemKey key1, PathSystemKey key2) {
		assertFinished();

		if ((key1 == null) || (key2 == null)) {
			return false;
		}
		PathSystemEntry entry1 = keyToEntryMap.get(key1);
		PathSystemEntry entry2 = keyToEntryMap.get(key2);
		if ((entry1.getParentElement() == key2.getElement())
				&& (entry1.getParentStateNumber() == key2.getStateNumber())) {
			return true;
		}
		if ((entry2.getParentElement() == key1.getElement())
				&& (entry2.getParentStateNumber() == key1.getStateNumber())) {
			return true;
		}
		return false;
	}

	/**
	 * @return true if the given first element is a brother of the given second
	 *         element. That means they have the same father. If one or both
	 *         of the given element are part of the pathsystem more than one
	 *         time, the first occurence is used. If one of the elements is not
	 *         part of this pathsystem, false is returned
	 */
	public boolean isSibling(Vertex v1, Vertex v2) {
		PathSystemKey key1 = elementToFirstKeyMap.get(v1);
		PathSystemKey key2 = elementToFirstKeyMap.get(v2);
		return isSibling(key1, key2);
	}

	/**
	 * @return true if the given first key is a brother of the given second key,
	 *         that means, if thy have the same father in the pathtree from
	 *         key1.element to key2.element and the states matches. If one of the
	 *         keys is not part of this pathsystem, false is returned
	 */
	public boolean isSibling(PathSystemKey key1, PathSystemKey key2) {
		assertFinished();

		if ((key1 == null) || (key2 == null)) {
			return false;
		}
		PathSystemEntry entry1 = keyToEntryMap.get(key1);
		PathSystemEntry entry2 = keyToEntryMap.get(key2);
		if ((entry1.getParentElement() == entry2.getParentElement())
				&& (entry1.getParentStateNumber() == entry2
						.getParentStateNumber())) {
			return true;
		}
		return false;
	}

	/**
	 * Prints this pathsystem as ascii-art
	 */
	public void printAscii() {
		assertFinished();
		if (FunLib.getLogger() == null) {
			return;
		}
		PSet<HyperPath> pathSet = extractPaths();
		for (HyperPath path : pathSet) {
			FunLib.getLogger().info(path.toString());
		}
	}

	/**
	 * returns a string representation of this path system
	 */
	@Override
	public String toString() {
		StringBuilder returnString = new StringBuilder("PathSystem:\n");
		PSet<HyperPath> pathSet = extractPaths();
		for (HyperPath path : pathSet) {
			returnString.append(path.toString());
			returnString.append('\n');
		}
		return returnString.toString();
	}

	/**
	 * prints the <key, entry> map
	 */
	public void printEntryMap() {
		assertFinished();
		if (FunLib.getLogger() == null) {
			return;
		}
		FunLib.getLogger().info("<Key, Entry> Set of PathSystem is:");
		for (Map.Entry<PathSystemKey, PathSystemEntry> entry : keyToEntryMap
				.entrySet()) {
			PathSystemEntry thisEntry = entry.getValue();
			PathSystemKey thisKey = entry.getKey();
			FunLib.getLogger().info(
					thisKey.toString() + " maps to " + thisEntry.toString());
		}
	}

	/**
	 * prints the <element, key> map
	 */
	public void printKeyMap() {
		assertFinished();
		if (FunLib.getLogger() == null) {
			return;
		}
		FunLib.getLogger().info("<Vertex, FirstKey> Set of PathSystem is:");
		for (Map.Entry<GraphElement<?, ?, ?, ?>, PathSystemKey> entry : elementToFirstKeyMap
				.entrySet()) {
			PathSystemKey thisKey = entry.getValue();
			GraphElement<?, ?, ?, ?> vertex = entry.getKey();
			FunLib.getLogger().info(vertex + " maps to " + thisKey.toString());
		}
	}

	private void assertUnfinished() {
		if (finished) {
			throw new IllegalStateException(
					"Cannot modify a finished path system");
		}
	}

	private void assertFinished() {
		if (!finished) {
			throw new IllegalStateException(
					"Path System needs to be finished before it can be used. Use PathSystem.finish()");
		}
	}

	public PSet<GraphElement<?, ?, ?, ?>> getElements() {
		assertFinished();
		PSet<GraphElement<?, ?, ?, ?>> returnSet = JGraLab.set();
		for (PathSystemKey key : keyToEntryMap.keySet()) {
			returnSet = returnSet.plus(key.getElement());
		}
		return returnSet;
	}

	public PSet<Vertex> getVertices() {
		assertFinished();
		PSet<Vertex> returnSet = JGraLab.set();
		for (PathSystemKey key : keyToEntryMap.keySet()) {
			if (key.getElement() instanceof Vertex) {
				returnSet = returnSet.plus((Vertex) key.getElement());
			}
		}
		return returnSet;
	}

	public PSet<Edge> getEdges() {
		assertFinished();
		PSet<Edge> returnSet = JGraLab.set();
		for (PathSystemKey key : keyToEntryMap.keySet()) {
			if (key.getElement() instanceof Edge) {
				returnSet = returnSet.plus((Edge) key.getElement());
			}
		}
		return returnSet;
	}

	public PSet<Incidence> getIncidences() {
		assertFinished();
		PSet<Incidence> resultSet = JGraLab.set();
		for (Map.Entry<PathSystemKey, PathSystemEntry> mapEntry : keyToEntryMap
				.entrySet()) {
			PathSystemEntry thisEntry = mapEntry.getValue();
			if (thisEntry.getParentIncidence() != null) {
				resultSet = resultSet.plus(thisEntry.getParentIncidence());
			}
		}
		return resultSet;
	}
}