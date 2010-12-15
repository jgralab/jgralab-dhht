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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.utilities.tg2whatever.Tg2Whatever;

@Deprecated
public class SimpleTg2Dot extends Tg2Whatever {

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
		out.println("digraph \"" + graph.getId() + "\"");
		out.println("{");

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
		out.println("edge [fontname=\"" + fontname + "\" fontsize=\""
				+ fontsize + "\" labelfontname=\"" + fontname
				+ "\" labelfontsize=\"" + fontsize + "\" color=\"#999999\"];");
	}

	@Override
	public void graphEnd(PrintStream out) {
		out.println("}");
	}

	@Override
	protected void printVertex(PrintStream out, Vertex v) {
		AttributedElementClass cls = v.getAttributedElementClass();
		out.print("v" + v.getId() + " [label=\"{{v" + v.getId() + "|"
				+ cls.getUniqueName().replace('$', '.') + "}");
		if (cls.getAttributeCount() > 0) {
			out.print("|");
			printAttributes(out, v);
		}
		out.println("}\"];");
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
		if (reversedEdgeTypes == null) {
			return reversedEdges;
		}

		@SuppressWarnings("unchecked")
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
	}

	@Override
	protected void printEdge(PrintStream out, Edge e) {
		boolean reversed = printEdgeReversed(e);
		Vertex alpha = reversed ? e.getOmega() : e.getAlpha();
		Vertex omega = reversed ? e.getAlpha() : e.getOmega();
		out.print("v" + alpha.getId() + " -> v" + omega.getId() + " [");

		EdgeClass cls = (EdgeClass) e.getAttributedElementClass();
		if (roleNames) {
			String toRole = cls.getTo().getRolename();
			if ((toRole != null) && (toRole.length() > 0)) {
				out.print((reversed ? "tail" : "head") + "label=\""
						+ stringQuote(toRole) + "\" ");
			}
			String fromRole = cls.getFrom().getRolename();
			if ((fromRole != null) && (fromRole.length() > 0)) {
				out.print((reversed ? "head" : "tail") + "label=\""
						+ stringQuote(fromRole) + "\" ");
			}
		}

		out.print("dir=\"both\" ");
		assert e.isNormal();
		/*
		 * The first 2 cases handle the case were the aggregation/composition
		 * diamond is at the opposite side of the direction arrow.
		 */
		if (e.getOmegaSemantics() == AggregationKind.SHARED) {
			if (reversed) {
				out.print("arrowhead=\"odiamond\" ");
			} else {
				out.print("arrowtail=\"odiamond\" ");
			}
		} else if (e.getOmegaSemantics() == AggregationKind.COMPOSITE) {
			if (reversed) {
				out.print("arrowhead=\"diamond\" ");
			} else {
				out.print("arrowtail=\"diamond\" ");
			}
		}
		/*
		 * The next 2 cases handle the case were the aggregation/composition
		 * diamond is at the same side as the direction arrow. Here, we print
		 * only the diamond.
		 */
		else if (e.getAlphaSemantics() == AggregationKind.SHARED) {
			if (reversed) {
				out.print("arrowtail=\"odiamondnormal\" ");
				out.print("arrowhead=\"none\" ");
			} else {
				out.print("arrowhead=\"odiamondnormal\" ");
				out.print("arrowtail=\"none\" ");
			}
		} else if (e.getAlphaSemantics() == AggregationKind.COMPOSITE) {
			if (reversed) {
				out.print("arrowtail=\"diamondnormal\" ");
				out.print("arrowhead=\"none\" ");
			} else {
				out.print("arrowhead=\"diamondnormal\" ");
				out.print("arrowtail=\"none\" ");
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

		out.print("label=\"e" + e.getId() + ": "
				+ cls.getUniqueName().replace('$', '.'));

		if (edgeAttributes && (cls.getAttributeCount() > 0)) {
			out.print("\\l");
			printAttributes(out, e);
		}
		out.print("\"");

		if (printIncidenceNumbers) {
			out.print(" taillabel=\"" + getIncidenceNumber(e, alpha) + "\"");
			out.print(" headlabel=\""
					+ getIncidenceNumber(e.getReversedEdge(), omega) + "\"");
		}
		out.println("];");

	}

	private int getIncidenceNumber(Edge e, Vertex v) {
		int num = 1;
		for (Edge inc : v.incidences()) {
			if (inc == e) {
				return num;
			}
			num++;
		}
		return -1;
	}

	private void printAttributes(PrintStream out, AttributedElement elem) {
		AttributedElementClass cls = elem.getAttributedElementClass();
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
		SimpleTg2Dot converter = new SimpleTg2Dot();
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
		printGraphAsDot(graph, reversedEdges, outputFileName, null);
	}

	public static void printGraphAsDot(Graph graph, boolean reversedEdges,
			String outputFileName, List<Class<? extends Edge>> reversedEdgeTypes) {
		SimpleTg2Dot t2d = new SimpleTg2Dot();
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
		SimpleTg2Dot t2d = new SimpleTg2Dot();
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