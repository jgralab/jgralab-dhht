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

package de.uni_koblenz.jgralab.utilities.rsa;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.grumlschema.SchemaGraph;
import de.uni_koblenz.jgralab.grumlschema.domains.Domain;
import de.uni_koblenz.jgralab.grumlschema.domains.EnumDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.HasRecordDomainComponent_componentDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.RecordDomain;
import de.uni_koblenz.jgralab.grumlschema.structure.Annotates_annotatedElement;
import de.uni_koblenz.jgralab.grumlschema.structure.Attribute;
import de.uni_koblenz.jgralab.grumlschema.structure.AttributedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Comment;
import de.uni_koblenz.jgralab.grumlschema.structure.ConnectsToEdgeClass_connectedEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Constraint;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsDomain;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsGraphElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsSubPackage;
import de.uni_koblenz.jgralab.grumlschema.structure.EdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.GraphClass;
import de.uni_koblenz.jgralab.grumlschema.structure.GraphElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.HasAttribute_attributedElement;
import de.uni_koblenz.jgralab.grumlschema.structure.HasConstraint_constrainedElement;
import de.uni_koblenz.jgralab.grumlschema.structure.HidesIncidenceClassAtEdgeClass_hidingIncidenceClassAtEdge;
import de.uni_koblenz.jgralab.grumlschema.structure.HidesIncidenceClassAtVertexClass_hidingIncidenceClassAtVertex;
import de.uni_koblenz.jgralab.grumlschema.structure.IncidenceClass;
import de.uni_koblenz.jgralab.grumlschema.structure.NamedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Schema;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesVertexClass;
import de.uni_koblenz.jgralab.grumlschema.structure.VertexClass;


/**
 * TODO (24.01.2012) This tool does currently not convert sigma and kappa information and needs to be deleted or adapted
 * @author dbildh
 *
 */

public class SchemaGraph2Tg {

	private boolean useShortNames = true;

	/**
	 * @return the useShortNames
	 */
	public boolean isUseShortNames() {
		return useShortNames;
	}

	/**
	 * @param useShortNames
	 *            the useShortNames to set
	 */
	public void setUseShortNames(boolean useShortNames) {
		this.useShortNames = useShortNames;
	}

	private final static String SPACE = " ";
	private final static String EMPTY = "";

	private final static String STAR = "*";
	private final static String DOT = ".";
	private final static String COMMA = ",";
	private final static String DELIMITER = ";";
	private final static String COLON = ":";
	private final static String CURLY_BRACKET_OPENED = "{";
	private final static String CURLY_BRACKET_CLOSED = "}";
	private final static String SQUARE_BRACKET_OPENED = "[";
	private final static String SQUARE_BRACKET_CLOSED = "]";
	private final static String ROUND_BRACKET_OPENED = "(";
	private final static String ROUND_BRACKET_CLOSED = ")";

	private final static String FROM = "from";
	private final static String TO = "to";
	private final static String ROLE = "role";
	private final static String REDEFINES = "redefines";

	private final static String SCHEMA = "Schema";
	private final static String PACKAGE = "Package";
	private final static String COMMENT = "Comment";
	private final static String ABSTRACT = "abstract";
	private final static String VERTEX_CLASS = "VertexClass";
	private final static String GRAPH_CLASS = "GraphClass";
	private final static String RECORD_DOMAIN = "RecordDomain";
	private final static String ENUM_DOMAIN = "EnumDomain";
	private final static String EDGE_CLASS = "EdgeClass";
	private final static String AGGREGATION = "aggregation";
	private final static String AGG_SHARED = "shared";
	private final static String AGG_COMPOSITE = "composite";
	private final static String TGRAPH = "TGraph";
	private final static String TGRAPH_VERSION = "2";
	private final static String ASSIGN = "=";
	private static final String NEWLINE = "\n";

	/**
	 * SchemaGraph which should be transformed to a TG file.
	 */
	private final SchemaGraph schemaGraph;

	/**
	 * Name of the output TG file.
	 */
	private final String outputFilename;

	/**
	 * Stores the current used package name.
	 */
	private String currentPackageName;

	/**
	 * PrintWriter object, which is used to write the TG file.
	 */
	private Writer stream;

	/**
	 * Constructs an object, which will print out the specified
	 * {@link SchemaGraph} to a TG file with the given output filename. The TG
	 * output will be hierarchical ordered. This means all qualified names will
	 * be simple names.<br>
	 * <br>
	 * 
	 * <strong>Note:</strong> run() have to be executed to get a TG file.
	 * 
	 * @param sg
	 *            {@link SchemaGraph}, which will be written to a TG file.
	 * @param outputFilename
	 *            {@link String} of the Location of the TG file. Note: The file
	 *            will be overwritten!
	 */
	public SchemaGraph2Tg(SchemaGraph sg, String outputFilename) {
		schemaGraph = sg;
		this.outputFilename = outputFilename;
	}

	/**
	 * Prints the specified {@link SchemaGraph} to a location according to the
	 * given outputFilename via a {@link PrintWriter}.<br>
	 * 
	 * @throws IOException
	 */
	public void process() throws IOException {

		try {
			assert (outputFilename != null) && !outputFilename.equals(EMPTY) : "No output filename specified!";
			assert schemaGraph != null : "No SchemaGraph specified!";
			stream = new PrintWriter(outputFilename);

			// This line is for debugging and developing purposes only.
			// stream = new PrintWriter(System.out);

			printTGSchema(schemaGraph);

			// Write out, close and dispose the Printstream object.
			stream.append(NEWLINE);
			stream.flush();
		} finally {
			stream.close();
			stream = null;
		}
	}

	public void setStream(StringWriter stream) {
		this.stream = stream;
	}

	/**
	 * Transforms a {@link SchemaGraph} to a TG string, which is written to a
	 * {@link PrintWriter} object stored in the member variable
	 * <code>stream</code>. The transformation rules
	 * <code>PackageDeclaration</code>, <code>DomainDefinition</code>,
	 * <code>VertexClassDefinition</code>, <code>EdgeClassDefinition</code>,
	 * <code>AggregationClassDefinition</code> and
	 * <code>CompositionClassDefinition</code> are encapsulated in methods
	 * corresponding to a prefix "print" and the name of the EBNF rule.<br>
	 * <br>
	 * 
	 * @param schemaGraph
	 *            {@link SchemaGraph}, which should be transformed to TG string.
	 */
	private void printTGSchema(SchemaGraph schemaGraph) {
		// The version of the TG format
		println(TGRAPH, SPACE, TGRAPH_VERSION, DELIMITER, NEWLINE);

		// schema
		Schema schema = schemaGraph.getFirstSchema();
		assert schema != null;
		println(SCHEMA, SPACE, schema.get_packagePrefix(), DOT,
				schema.get_name(), DELIMITER, NEWLINE);

		de.uni_koblenz.jgralab.grumlschema.structure.Package defaultPackage = (de.uni_koblenz.jgralab.grumlschema.structure.Package) schema
				.getFirstIncidenceToContainsDefaultPackage(Direction.VERTEX_TO_EDGE).getThat();
		setCurrentPackageName(defaultPackage);

		// graphclass
		GraphClass gc = (GraphClass) schema.getFirstIncidenceToDefinesGraphClass(
				Direction.VERTEX_TO_EDGE).getThat();
		printGraphClass(gc);
		printComments(gc);

		printPackageWithElements((de.uni_koblenz.jgralab.grumlschema.structure.Package) schema
				.getFirstIncidenceToContainsDefaultPackage(Direction.VERTEX_TO_EDGE).getThat());
	}

	private void printPackageWithElements(de.uni_koblenz.jgralab.grumlschema.structure.Package gPackage) {
		setCurrentPackageName(gPackage);
		printComments(gPackage);
		println(PACKAGE, SPACE, currentPackageName, DELIMITER);

		for (ContainsDomain cd : gPackage
				.getIncidentEdgesOfType_ContainsDomain(Direction.VERTEX_TO_EDGE)) {
			printDomain((Domain) cd.getOmega());
		}

		for (ContainsGraphElementClass cgec : gPackage
				.getIncidentEdgesOfType_ContainsGraphElementClass(Direction.VERTEX_TO_EDGE)) {
			GraphElementClass gec = (GraphElementClass) cgec.getOmega();
			if (gec instanceof EdgeClass) {
				printEdgeClass((EdgeClass) gec);
			} else {
				printVertexClass((VertexClass) gec);
			}
		}

		for (ContainsSubPackage csp : gPackage
				.getIncidentEdgesOfType_ContainsSubPackage(Direction.VERTEX_TO_EDGE)) {
			printPackageWithElements((de.uni_koblenz.jgralab.grumlschema.structure.Package) csp.getOmega());
		}
	}

	private void setCurrentPackageName(de.uni_koblenz.jgralab.grumlschema.structure.Package pkg) {
		currentPackageName = pkg.get_qualifiedName();
	}

	private void printComments(NamedElementClass ne) {
		for (Annotates_annotatedElement ann : ne.getAnnotates_annotatedElementIncidences()) {
			Comment com = (Comment) ann.getThat();
			println(COMMENT, SPACE, ne.get_qualifiedName(), SPACE,
					GraphIO.toUtfString(com.get_text()), DELIMITER);
		}
	}

	public void printVertexClass(VertexClass vc) {
		printComments(vc);
		if (vc.is_abstract()) {
			print(ABSTRACT, SPACE);
		}
		print(VERTEX_CLASS, SPACE, shortName(vc.get_qualifiedName()));

		// superclasses
		if (vc.getFirstIncidenceToSpecializesVertexClass(Direction.VERTEX_TO_EDGE) != null) {
			print(COLON, SPACE);
			boolean first = true;
			for (SpecializesVertexClass svc : vc
					.getIncidentEdgesOfType_SpecializesVertexClass(Direction.VERTEX_TO_EDGE)) {
				if (first) {
					first = false;
				} else {
					print(COMMA, SPACE);
				}
				VertexClass superVC = (VertexClass) svc.getOmega();
				print(shortName(superVC.get_qualifiedName()));
			}
		}

		// attributes
		printAttributes(vc);

		// constraints
		printConstraints(vc);
		println(DELIMITER);
	}

	public void printEdgeClass(EdgeClass ec) {
		printComments(ec);
		if (ec.is_abstract()) {
			print(ABSTRACT, SPACE);
		}
		print(EDGE_CLASS, SPACE, shortName(ec.get_qualifiedName()));

		// superclasses
		if (ec.getFirstIncidenceToSpecializesEdgeClass(Direction.VERTEX_TO_EDGE) != null) {
			print(COLON, SPACE);
			boolean first = true;
			for (SpecializesEdgeClass svc : ec
					.getIncidentEdgesOfType_SpecializesEdgeClass(Direction.VERTEX_TO_EDGE)) {
				if (first) {
					first = false;
				} else {
					print(COMMA, SPACE);
				}
				EdgeClass superEC = (EdgeClass) svc.getOmega();
				print(shortName(superEC.get_qualifiedName()));
			}
		}


		IncidenceClass toIC = null;
		IncidenceClass fromIC = null;
		for (ConnectsToEdgeClass_connectedEdgeClass inc : ec.getConnectsToEdgeClass_connectedEdgeClassIncidences()) {
			IncidenceClass ic = (IncidenceClass) inc.getThat();
			if (ic.get_direction() == de.uni_koblenz.jgralab.grumlschema.structure.Direction.VERTEX_TO_EDGE)
				fromIC = ic;
			if (ic.get_direction() == de.uni_koblenz.jgralab.grumlschema.structure.Direction.EDGE_TO_VERTEX)
				toIC = ic;
		}
		VertexClass fromVC = (VertexClass) fromIC.getFirst_incidenceClassAtVertex().getThat();
		VertexClass toVC = (VertexClass) toIC.getFirst_incidenceClassAtVertex().getThat();

		print(SPACE, FROM, SPACE, shortName(fromVC.get_qualifiedName()));
		printMultiplicitiesAndRoles(fromIC);
		print(SPACE, TO, SPACE, shortName(toVC.get_qualifiedName()));
		printMultiplicitiesAndRoles(toIC);

		// attrs
		printAttributes(ec);

		// constraints
		printConstraints(ec);
		println(DELIMITER);
	}

	private void printAggregation(IncidenceClass inc) {
		assert inc != null;
		switch (inc.get_incidenceType()) {
		case EDGE:
			break;
		case AGGREGATION:
			print(SPACE, AGGREGATION, SPACE, AGG_SHARED);
			break;
		case COMPOSITION:
			print(SPACE, AGGREGATION, SPACE, AGG_COMPOSITE);
			break;
		}
	}

	private String shortName(String qname) {
		if (!useShortNames || isPredefinedDomainName(qname)) {
			return qname;
		}

		int lastDotIdx = qname.lastIndexOf('.');

		// To refer to elements in the default package while not being there, we
		// need to add a DOT.
		if ((lastDotIdx == -1) && !currentPackageName.isEmpty()) {
			return '.' + qname;
		}

		if ((lastDotIdx != -1)
				&& currentPackageName.equals(qname.substring(0, lastDotIdx))) {
			return qname.substring(lastDotIdx + 1);
		}
		return qname;
	}

	private boolean isPredefinedDomainName(String qname) {
		return qname.equals("Integer") || qname.equals("String")
				|| qname.equals("Long") || qname.equals("Double")
				|| qname.startsWith("List<") || qname.startsWith("Set<")
				|| qname.startsWith("Map<") || qname.equals("Boolean");
	}

	private void printMultiplicitiesAndRoles(IncidenceClass ic) {
		if ((ic.get_roleName() != null) && !ic.get_roleName().isEmpty()) {
			print(SPACE, ROLE, SPACE, ic.get_roleName());
		}
	
		String minV = ic.get_minVerticesAtEdge() == Integer.MAX_VALUE ? STAR : String
				.valueOf(ic.get_minVerticesAtEdge());
		String maxV = ic.get_maxVerticesAtEdge() == Integer.MAX_VALUE ? STAR : String
				.valueOf(ic.get_maxVerticesAtEdge());

		print(SPACE, ROUND_BRACKET_OPENED, minV, COMMA, maxV,
				ROUND_BRACKET_CLOSED);


		if (ic.getFirst_hidingIncidenceClassAtVertex() != null) {
			print(SPACE, REDEFINES, SPACE);
			boolean first = true;
			for (HidesIncidenceClassAtVertexClass_hidingIncidenceClassAtVertex r : ic.getHidesIncidenceClassAtVertexClass_hidingIncidenceClassAtVertexIncidences()) {
				if (first) {
					first = false;
				} else {
					print(COMMA, SPACE);
				}
				IncidenceClass redefined = (IncidenceClass) r.getThat();
				print(redefined.get_roleName());
			}
		}
		
		
		String minE = ic.get_minEdgesAtVertex() == Integer.MAX_VALUE ? STAR : String
				.valueOf(ic.get_minEdgesAtVertex());
		String maxE = ic.get_maxEdgesAtVertex() == Integer.MAX_VALUE ? STAR : String
				.valueOf(ic.get_maxEdgesAtVertex());
		print(SPACE, ROUND_BRACKET_OPENED, minE, COMMA, maxE,
				ROUND_BRACKET_CLOSED);	
		if (ic.getFirst_hidingIncidenceClassAtVertex() != null) {
			print(SPACE, REDEFINES, SPACE);
			boolean first = true;
			for (HidesIncidenceClassAtEdgeClass_hidingIncidenceClassAtEdge r : ic.getHidesIncidenceClassAtEdgeClass_hidingIncidenceClassAtEdgeIncidences()) {
				if (first) {
					first = false;
				} else {
					print(COMMA, SPACE);
				}
				IncidenceClass redefined = (IncidenceClass) r.getThat();
				print(redefined.get_roleName());
			}
		}

		printAggregation(ic);
	}

	private void printDomain(Domain dom) {
		if (dom instanceof RecordDomain) {
			printRecordDomain((RecordDomain) dom);
			return;
		} else if (dom instanceof EnumDomain) {
			printEnumDomain((EnumDomain) dom);
			return;
		}
	}

	private void printEnumDomain(EnumDomain dom) {
		printComments(dom);
		print(ENUM_DOMAIN, SPACE, shortName(dom.get_qualifiedName()), SPACE,
				ROUND_BRACKET_OPENED);
		boolean first = true;
		for (String constant : dom.get_enumConstants()) {
			if (first) {
				first = false;
			} else {
				print(COMMA, SPACE);
			}
			print(constant);
		}
		println(ROUND_BRACKET_CLOSED, DELIMITER);
	}

	private void printRecordDomain(RecordDomain dom) {
		printComments(dom);
		print(RECORD_DOMAIN, SPACE, dom.get_qualifiedName(), SPACE,
				ROUND_BRACKET_OPENED);
		boolean first = true;
		for (HasRecordDomainComponent_componentDomain hc : dom
				.getHasRecordDomainComponent_componentDomainIncidences()) {
			if (first) {
				first = false;
			} else {
				print(COMMA, SPACE);
			}
			Domain compDom = (Domain) hc.getThat();
			print(hc.getEdge().get_name(), COLON, SPACE,
					shortName(compDom.get_qualifiedName()));
		}
		println(ROUND_BRACKET_CLOSED, DELIMITER);
	}

	private void printGraphClass(GraphClass gc) {
		print(GRAPH_CLASS, SPACE, gc.get_qualifiedName());
		printAttributes(gc);
		printConstraints(gc);
		println(DELIMITER, NEWLINE);
	}

	private void printConstraints(AttributedElementClass aec) {
		for (HasConstraint_constrainedElement hc : aec
				.getHasConstraint_constrainedElementIncidences()) {
			Constraint constr = (Constraint) hc.getThat();
			print(SPACE, SQUARE_BRACKET_OPENED);

			print(GraphIO.toUtfString(constr.get_message()), SPACE);
			print(GraphIO.toUtfString(constr.get_predicateQuery()));

			String offElemQ = constr.get_offendingElementsQuery();
			if (offElemQ != null) {
				print(SPACE, GraphIO.toUtfString(offElemQ));
			}

			print(SQUARE_BRACKET_CLOSED);
		}
	}

	private void printAttributes(AttributedElementClass aec) {
		if (aec.getFirst_attributedElement() == null) {
			return;
		}

		print(SPACE, CURLY_BRACKET_OPENED);
		boolean first = true;
		for (HasAttribute_attributedElement ha : aec.getHasAttribute_attributedElementIncidences()) {
			if (first) {
				first = false;
			} else {
				print(COMMA, SPACE);
			}
			Attribute attr = (Attribute) ha.getThat();
			Domain dom = (Domain) attr.getFirst_attributeWithDomain()
					.getThat();
			print(attr.get_name(), COLON, SPACE,
					shortName(dom.get_qualifiedName()));
			String defaultValue = attr.get_defaultValue();
			if (defaultValue != null) {
				print(SPACE, ASSIGN, SPACE, GraphIO.toUtfString(defaultValue));
			}
		}
		print(CURLY_BRACKET_CLOSED);
	}

	private void println(String... strings) {
		print(strings);
		try {
			stream.write(NEWLINE);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void print(String... strings) {
		try {
			for (int i = 0; i < strings.length - 1; i++) {
				stream.write(strings[i]);
			}
			stream.write(strings[strings.length - 1]);
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
}
