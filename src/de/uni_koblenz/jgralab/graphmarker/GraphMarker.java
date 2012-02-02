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

package de.uni_koblenz.jgralab.graphmarker;

import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphStructureChangedListener;

/**
 * This class can be used to "colorize" graphs, edges and vertices. If a
 * algorithm only needs to distinguish between "marked" and "not marked", a look
 * at the class <code>BooleanGraphMarker</code> may be reasonable. If a specific
 * kind of marking is used, it may be reasonalbe to extends this GraphMarker. A
 * example how that could be done is located in the tutorial in the class
 * <code>DijkstraVertexMarker</code>.
 * 
 * This Marker only exists for compatibility reasons to older versions of
 * JGraLab. The new marker class <code>GenericGraphMarker</code> allows a
 * stricter limitation to specific <code>AttributedElement</code>s.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
@SuppressWarnings("rawtypes")
public interface GraphMarker<T extends AttributedElement> extends GraphStructureChangedListener {
	
	/**
	 * Checks if the given <code>graphElement</code> is marked.
	 * 
	 * @param graphElement
	 *            the graph element to check.
	 * @return true if the given <code>graphElement</code> is marked.
	 */
	public boolean isMarked(T graphElement) throws RemoteException;

	/**
	 * Unmarks the given <code>graphElement</code>.
	 * 
	 * @param graphElement
	 *            the graph element to unmark.
	 * @return false if the given <code>graphElement</code> has already been
	 *         unmarked.
	 */
	public boolean removeMark(T graphElement) throws RemoteException;

	/**
	 * Returns the number of marked graph elements.
	 * 
	 * @return the number of marked graph elements.
	 */
	public long size() throws RemoteException;

	/**
	 * Checks if this graph marker is empty.
	 * 
	 * @return true if this graph marker is empty.
	 */
	public boolean isEmpty() throws RemoteException;

	/**
	 * Unmarks all marked graph elements.
	 */
	public void clear() throws RemoteException;


	public abstract Iterable<T> getMarkedElements() throws RemoteException;


	
	Graph getGraph() throws RemoteException;



}
