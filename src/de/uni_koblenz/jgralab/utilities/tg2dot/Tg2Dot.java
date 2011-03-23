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

package de.uni_koblenz.jgralab.utilities.tg2dot;

import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.utilities.tg2whatever.Tg2Whatever;

public class Tg2Dot extends Tg2Whatever {

	private double ranksep = 1.5;
	private boolean ranksepEqually = false;
	private double nodesep = 0.25;
	private String fontname = "Helvetica";
	private int fontsize = 14;
	private boolean abbreviateEdgeAttributeNames = false;
	private boolean printIncidenceNumbers = false;
	private Set<Class<? extends Edge>> reversedEdgeTypes = null;
	private Map<Class<? extends Edge>, Boolean> revEdgeTypeCache = null;

	public boolean isPrintIncidenceNumbers() {
		return printIncidenceNumbers;
	}

	/**
	 * @param printIncidenceNumbers
	 *            if true, then the incidence numbers will be printed near the
	 *            start and end points of edges.
	 */
	public void setPrintIncidenceNumbers(boolean printIncidenceNumbers) {
		this.printIncidenceNumbers = printIncidenceNumbers;
	}

	/**
	 * prints the graph to the output file
	 */
	@Override
	public void graphStart(PrintStream out) {
		try {
			out.println("digraph \"" + graph.getCompleteGraphUid() + "\"");
			out.println("{");
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		
		// Set the ranksep
		if (ranksepEqually) {
			out.println("ranksep=\"" + ranksep + " equally\";");
		} else {
			out.println("ranksep=\"" + ranksep + "\";");
		}

		// Set the nodesep
		out.println("nodesep=\"" + nodesep + "\";");

		out.println("node [shape=\"record\" " + "fontname=\"" + fontname
				+ "\" " + "fontsize=\"" + fontsize + "\" color=\"#999999\"];");
		out.println("edge [style=\"dashed\" fontname=\"" + fontname + "\" fontsize=\""
				+ fontsize + "\" labelfontname=\"" + fontname
				+ "\" labelfontsize=\"" + fontsize + "\" color=\"#999999\"];");
	}

	@Override
	public void graphEnd(PrintStream out) {
		out.println("}");
	}

	public void printBeforeEdges(PrintStream out) {
		out.println("node [shape=\"diamond\" " + "fontname=\"" + fontname
				+ "\" " + "fontsize=\"" + fontsize + "\" color=\"#999999\"];");
	}
	
	
	@Override
	protected void printVertex(PrintStream out, Vertex v) {
		try {
		VertexClass cls = v.getType();
		out.print("v" + v.getId() + " [label=\"{{v" + v.getId() + "|"
				+ cls.getUniqueName().replace('$', '.') + "}");
		if (cls.getAttributeCount() > 0) {
			out.print("|");
			printAttributes(out, v);
		}
		out.println("}\"];");
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void printEdge(PrintStream out, Edge e) {
		try {
		EdgeClass cls = e.getType();
		out.print("e" + e.getId() + " [label=\"{{e" + e.getId() + "|"
				+ cls.getUniqueName().replace('$', '.') + "}");
		if (cls.getAttributeCount() > 0) {
			out.print("|");
			printAttributes(out, e);
		}
		out.println("}\"];");
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}


	@Override
	protected String stringQuote(String s) {
		StringBuffer sb = new StringBuffer();
		for (char ch : s.toCharArray()) {
			switch (ch) {
			case '\\':
				sb.append("\\\\");
				break;
			case '<':
				sb.append("\\<");
				break;
			case '>':
				sb.append("\\>");
				break;
			case '{':
				sb.append("\\{");
				break;
			case '}':
				sb.append("\\}");
				break;
			case '"':
				sb.append("\\\"");
				break;
			case '|':
				sb.append("\\|");
				break;
			case '\n':
				sb.append("\\\\n");
				break;
			case '\r':
				sb.append("\\\\r");
				break;
			case '\t':
				sb.append("\\\\t");
				break;
			default:
				if ((ch < ' ') || (ch > '\u007F')) {
					sb.append("\\\\u");
					String code = "000" + Integer.toHexString(ch);
					sb.append(code.substring(code.length() - 4, code.length()));
				} else {
					sb.append(ch);
				}
				break;
			}
		}
		return sb.toString();
	}

	private boolean printEdgeReversed(Edge e) {
		try {
		if (reversedEdgeTypes == null) {
			return reversedEdges;
		}

		Class<? extends Edge> ec = (Class<? extends Edge>) e.getM1Class();
		Boolean reversed = revEdgeTypeCache.get(ec);
		if (reversed != null) {
			return reversedEdges ^ reversed;
		}

		boolean rev = false;
		if (reversedEdgeTypes.contains(ec)) {
			rev = true;
		} else {
			for (Class<? extends Edge> ecls : reversedEdgeTypes) {
				if (ecls.isInstance(e)) {
					rev = true;
					break;
				}
			}
		}
		revEdgeTypeCache.put(ec, rev);
		return reversedEdges ^ rev;
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
	}

	
	
	@Override
	protected void printIncidence(PrintStream out, Incidence i) {
		try {
		boolean reversed = printEdgeReversed(i.getEdge());

		//assume Vertex_TO_EDGE to be the direction to use
		String startLabel = "v";
		String endLabel = "e";
		GraphElement<? extends GraphElementClass<?,?>,? extends GraphElement<?,?,?>,? extends GraphElement<?,?,?>> start = null;
		GraphElement<? extends GraphElementClass<?,?>,? extends GraphElement<?,?,?>,? extends GraphElement<?,?,?>> end = null; 
		
		if (i.getDirection() == Direction.EDGE_TO_VERTEX  ^ reversed) {
			start =  i.getEdge();
			end =  i.getVertex();
			startLabel = "e";
			endLabel ="v";
		} else {
			start = i.getVertex();
			end = i.getEdge();
		}
		
		out.print(startLabel + start.getId() + " -> " + endLabel + end.getId() + " [");

		IncidenceClass cls = (IncidenceClass) i.getType();
		if (roleNames) {
			String role = cls.getRolename();
			if ((role != null) && (role.length() > 0)) {
				out.print("label=\"" + stringQuote(role) + "\" ");
			}
		}

		out.print("dir=\"both\" ");

		/*
		 * The first 2 cases handle the case were the aggregation/composition
		 * diamond is at the opposite side of the direction arrow.
		 */
		if (cls.getIncidenceType() == IncidenceType.COMPOSITION) {
			if (start==i.getVertex()) {
				out.print("arrowhead=\"odiamond\" ");
			} else {
				out.print("arrowtail=\"odiamond\" ");
			}
		} else if (cls.getIncidenceType() == IncidenceType.AGGREGATION) {
			if (start==i.getVertex()) {
				out.print("arrowhead=\"diamond\" ");
			} else {
				out.print("arrowtail=\"diamond\" ");
			}
		}
		/*
		 * Ok, this is the default case with no diamond. So simply deactivate
		 * one arrow label and keep the implicit normal at the other side.
		 */
		else {
			if (reversed) {
				out.print("arrowhead=\"none\" ");
			} else {
				out.print("arrowtail=\"none\" ");
			}
		}

		out.print("\"");

		if (printIncidenceNumbers) {
			out.print(" taillabel=\"" + getIncidenceNumber(i, end) + "\"");
			out.print(" headlabel=\"" + getIncidenceNumber(i, start) + "\"");
		}
		out.println("];");
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	private void printAttributes(PrintStream out, AttributedElement<?,?> elem) {
		try {
		AttributedElementClass<?,?> cls = elem.getType();
		for (Attribute attr : cls.getAttributeList()) {
			if (abbreviateEdgeAttributeNames && (elem instanceof Edge)) {
				// sourcePosition => sP
				// fooBarBaz => fBB
				out.print(attr.getName().charAt(0)
						+ attr.getName().replaceAll("[a-z]+", ""));
			} else {
				out.print(attr.getName());
			}
			if (domainNames) {
				out.print(": "
						+ stringQuote(attr.getDomain().getQualifiedName()));
			}
			Object attribute = elem.getAttribute(attr.getName());
			String attributeString = attribute != null ? attribute.toString()
					: "null";
			if (shortenStrings && (attributeString.length() > 17)) {
				attributeString = attributeString.substring(0, 18) + "...";
			}
			if (attribute instanceof String) {
				attributeString = '"' + attributeString + '"';
			}
			out.print(" = " + stringQuote(attributeString) + "\\l");
		}
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	public double getRanksep() {
		return ranksep;
	}

	/**
	 * Sets the desired rank separation, in inches.
	 * 
	 * This is the minimum vertical distance between the bottom of the nodes in
	 * one rank and the tops of nodes in the next.
	 * 
	 * @param ranksep
	 *            The value as described above.
	 */
	public void setRanksep(double ranksep) {
		this.ranksep = ranksep;
	}

	public double getNodesep() {
		return nodesep;
	}

	/**
	 * Sets the minimum space between two adjacent nodes in the same rank, in
	 * inches.
	 * 
	 * @param nodesep
	 *            Minimum space between two adjacent nodes in the same rank, in
	 *            inches.
	 */
	public void setNodesep(double nodesep) {
		this.nodesep = nodesep;
	}

	public boolean isRanksepEqually() {
		return ranksepEqually;
	}

	/**
	 * Decides if the space between all ranks should be equal.
	 * 
	 * @param ranksepEqually
	 *            If true the space between all ranks is equal.
	 */
	public void setRanksepEqually(boolean ranksepEqually) {
		this.ranksepEqually = ranksepEqually;
	}

	public String getFontname() {
		return fontname;
	}

	/**
	 * @param fontname
	 *            The name of the font to be used for nodes, edges and labels.
	 */
	public void setFontname(String fontname) {
		this.fontname = fontname;
	}

	public int getFontsize() {
		return fontsize;
	}

	/**
	 * @param fontsize
	 *            The size of the font used for nodes, edges and labels.
	 */
	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Tg2Dot converter = new Tg2Dot();
		converter.getOptions(args);
		converter.printGraph();
	}

	public boolean isAbbreviateAttributeNames() {
		return abbreviateEdgeAttributeNames;
	}

	public void setAbbreviateAttributeNames(boolean abbreviateAttributeNames) {
		abbreviateEdgeAttributeNames = abbreviateAttributeNames;
	}

	public static void printGraphAsDot(Graph graph, boolean reversedEdges,
			String outputFileName) {
		printGraphAsDot(graph, reversedEdges, outputFileName);
	}

	public static void printGraphAsDot(Graph graph, boolean reversedEdges,
			String outputFileName, List<Class<? extends Edge>> reversedEdgeTypes) {
		Tg2Dot t2d = new Tg2Dot();
		t2d.setGraph(graph);
		t2d.setReversedEdges(reversedEdges);
		t2d.setPrintEdgeAttributes(true);
		t2d.setRanksep(0.5);
		t2d.setOutputFile(outputFileName);

		HashSet<Class<? extends Edge>> revEdgeTypes = new HashSet<Class<? extends Edge>>();
		if (reversedEdgeTypes != null) {
			for (Class<? extends Edge> ec : reversedEdgeTypes) {
				revEdgeTypes.add(ec);
			}
		}
		t2d.setReversedEdgeTypes(revEdgeTypes);

		t2d.printGraph();
	}

	public static void printGraphAsDot(BooleanGraphMarker marker,
			boolean reversedEdges, String outputFileName) {
		Tg2Dot t2d = new Tg2Dot();
		t2d.setGraphMarker(marker);
		t2d.setGraph(marker.getGraph());
		t2d.setReversedEdges(reversedEdges);
		t2d.setPrintEdgeAttributes(true);
		t2d.setRanksep(0.5);
		t2d.setOutputFile(outputFileName);
		t2d.printGraph();
	}

	/**
	 * All edge instances of an edge type contained in the given set
	 * <code>reversedEdgeTypes</code> (or subtypes) will be printed reversed.
	 * This is especially useful when certain conceptual edges are modeled as
	 * nodes, like: State <--{ComesFrom} Transition -->{GoesTo}. Here, reversing
	 * the direction of either ComesFrom or GoesTo results in much nicer
	 * layouts.
	 * 
	 * @param reversedEdgeTypes
	 *            the set of edge types whose instances should be printed
	 *            reversed
	 */
	public void setReversedEdgeTypes(
			Set<Class<? extends Edge>> reversedEdgeTypes) {
		this.reversedEdgeTypes = reversedEdgeTypes;
		revEdgeTypeCache = new HashMap<Class<? extends Edge>, Boolean>();
	}
}
