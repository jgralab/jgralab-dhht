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

import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.DEFAULT_MAX_MULTIPLICITY;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.DEFAULT_MIN_MULTIPLICITY;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ANNOTATED_ELEMENT;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ASSOCIATION;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ASSOCIATION_CLASS;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_AGGREGATION;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_ASSOCIATION;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_CLASSIFIER;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_CLIENT;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_CONSTRAINED_ELEMENT;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_GENERAL;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_HREF;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_ISDERIVED;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_IS_ABSRACT;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_KEY;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_NAME;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_SUPPLIER;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_TYPE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ATTRIBUTE_VALUE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_BODY;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_CLASS;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_COMPOSITE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_DEFAULT_VALUE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_DETAILS;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ENUMERATION;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_ENUMERATION_LITERAL;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_E_ANNOTATIONS;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_GENERALIZATION;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_INSTANCE_VALUE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_LANGUAGE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_LITERAL_BOOLEAN;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_LITERAL_INTEGER;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_LITERAL_STRING;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_LOWER_VALUE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_MEMBER_END;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_MODEL;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_OPAQUE_EXPRESSION;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_OWNEDEND;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_OWNEDRULE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_OWNED_ATTRIBUTE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_OWNED_COMMENT;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_OWNED_LITERAL;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_PACKAGE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_PACKAGED_ELEMENT;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_PRIMITIVE_TYPE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_PROPERTY;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_REALIZATION;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_SHARED;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_SPECIFICATION;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_TRUE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.UML_UPPER_VALUE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.XMI_EXTENSION;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.XMI_NAMESPACE_PREFIX;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.XMI_TYPE;
import static de.uni_koblenz.jgralab.utilities.rsa.XMIConstants.XMI_XMI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import de.uni_koblenz.ist.utilities.option_handler.OptionHandler;
import de.uni_koblenz.ist.utilities.xml.XmlProcessor;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.LocalBooleanGraphMarker;
import de.uni_koblenz.jgralab.graphmarker.LocalGenericGraphMarker;
import de.uni_koblenz.jgralab.graphmarker.LocalIntegerVertexMarker;
import de.uni_koblenz.jgralab.graphvalidator.ConstraintViolation;
import de.uni_koblenz.jgralab.graphvalidator.GraphValidator;
import de.uni_koblenz.jgralab.grumlschema.GrumlSchema;
import de.uni_koblenz.jgralab.grumlschema.SchemaGraph;
import de.uni_koblenz.jgralab.grumlschema.domains.CollectionDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.Domain;
import de.uni_koblenz.jgralab.grumlschema.domains.EnumDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.HasRecordDomainComponent;
import de.uni_koblenz.jgralab.grumlschema.domains.MapDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.RecordDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.StringDomain;
import de.uni_koblenz.jgralab.grumlschema.impl.disk.structure.ConnectsToEdgeClass_connectedEdgeClassImpl;
import de.uni_koblenz.jgralab.grumlschema.impl.mem.structure.ConnectsToVertexClassImpl;
import de.uni_koblenz.jgralab.grumlschema.structure.Annotates;
import de.uni_koblenz.jgralab.grumlschema.structure.Annotates_annotatedElement;
import de.uni_koblenz.jgralab.grumlschema.structure.Attribute;
import de.uni_koblenz.jgralab.grumlschema.structure.AttributedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.BinaryEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Comment;
import de.uni_koblenz.jgralab.grumlschema.structure.ConnectsToEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.ConnectsToEdgeClass_connectedEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.ConnectsToEdgeClass_connectsToEdgeClass_ComesFrom_IncidenceClass;
import de.uni_koblenz.jgralab.grumlschema.structure.ConnectsToVertexClass;
import de.uni_koblenz.jgralab.grumlschema.structure.ConnectsToVertexClass_connectedVertexClass;
import de.uni_koblenz.jgralab.grumlschema.structure.ConnectsToVertexClass_connectsToVertexClass_ComesFrom_IncidenceClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Constraint;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsGraphElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsGraphElementClass_containsGraphElementClass_ComesFrom_Package;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsSubPackage;
import de.uni_koblenz.jgralab.grumlschema.structure.Direction;
import de.uni_koblenz.jgralab.grumlschema.structure.EdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.GraphClass;
import de.uni_koblenz.jgralab.grumlschema.structure.GraphElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.HasAttribute;
import de.uni_koblenz.jgralab.grumlschema.structure.HasAttribute_attribute;
import de.uni_koblenz.jgralab.grumlschema.structure.HasAttribute_hasAttribute_ComesFrom_AttributedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.HasDomain;
import de.uni_koblenz.jgralab.grumlschema.structure.HasDomain_domain;
import de.uni_koblenz.jgralab.grumlschema.structure.IncidenceClass;
import de.uni_koblenz.jgralab.grumlschema.structure.IncidenceType;
import de.uni_koblenz.jgralab.grumlschema.structure.MayBeNestedIn;
import de.uni_koblenz.jgralab.grumlschema.structure.MayBeNestedIn_nestedElement;
import de.uni_koblenz.jgralab.grumlschema.structure.MayBeNestedIn_nestingElement;
import de.uni_koblenz.jgralab.grumlschema.structure.NamedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Package;
import de.uni_koblenz.jgralab.grumlschema.structure.Schema;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesIncidenceClass;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesIncidenceClass_specializesIncidenceClass_ComesFrom_IncidenceClass;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesIncidenceClass_specializesIncidenceClass_GoesTo_IncidenceClass;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesTypedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesTypedElementClass_subclass;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesTypedElementClass_superclass;
import de.uni_koblenz.jgralab.grumlschema.structure.TypedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.VertexClass;
import de.uni_koblenz.jgralab.impl.mem.IncidenceImpl;
import de.uni_koblenz.jgralab.impl.mem.VertexImpl;
import de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot;

/**
 * Rsa2Tg is a utility that converts XMI files exported from IBM (tm) Rational
 * Software Architect (tm) into a TG schema file. The converter is based on a
 * SAX parser. As intermediate format, a grUML schema graph is created from the
 * XMI elements.
 * 
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class Rsa2Tg extends XmlProcessor {

	private static final String OPTION_FILENAME_VALIDATION = "r";

	private static final String OPTION_FILENAME_SCHEMA_GRAPH = "s";

	private static final String OPTION_FILENAME_DOT = "e";

	private static final String OPTION_FILENAME_SCHEMA = "o";

	private static final String OPTION_USE_NAVIGABILITY = "n";

	private static final String OPTION_REMOVE_UNUSED_DOMAINS = "u";

	private static final String OPTION_KEEP_EMPTY_PACKAGES = "k";

	private static final String OPTION_USE_ROLE_NAME = "f";

	/**
	 * Contains XML element names in the format "name>xmiId"
	 */
	private Stack<String> xmiIdStack;

	/**
	 * The schema graph.
	 */
	private SchemaGraph sg;

	/**
	 * The {@link Schema} vertex of the schema graph.
	 */
	private Schema schema;

	/**
	 * The {@link GraphClass} vertex of the schema graph.
	 */
	private GraphClass graphClass;

	/**
	 * A Stack containing the package hierarchy. Packages and their nesting are
	 * represented as tree in XML. The top element is the current package.
	 */
	private Stack<Package> packageStack;

	/**
	 * Maps XMI-Ids to vertices and edges of the schema graph.
	 */
	private Map<String, Vertex> idMap;

	/**
	 * Remembers the current class id for processing of nested elements.
	 */
	private String currentClassId;

	/**
	 * Remembers the current {@link VertexClass}/{@link EdgeClass} vertex for
	 * processing of nested elements.
	 */
	private AttributedElementClass currentClass;

	/**
	 * Remembers the current {@link RecordDomain} vertex for processing of
	 * nested elements.
	 */
	private RecordDomain currentRecordDomain;

	/**
	 * Remembers the current domain component edge for processing of nested
	 * elements.
	 */
	private HasRecordDomainComponent currentRecordDomainComponent;

	/**
	 * Remembers the current {@link Attribute} vertex for processing of nested
	 * elements.
	 */
	private Attribute currentAttribute;

	/**
	 * Marks {@link VertexClass}, {@link EdgeClass} and {@link IncidenceClass}
	 * vertices with a set of XMI Ids of superclasses.
	 */
	private LocalGenericGraphMarker<Set<String>> generalizations;

	/**
	 * Keeps track of 'uml:Realization's (key = client id, value = set of
	 * supplier ids) as workaround for missing generalizations between
	 * association and association class.
	 */
	private Map<String, Set<String>> realizations;

	/**
	 * Marks {@link Attribute} vertices with the XMI Id of its type if the type
	 * can not be resolved at the time the Attribute is processed.
	 */
	private LocalGenericGraphMarker<String> attributeType;

	/**
	 * Marks {@link HasRecordDomainComponent} edges with the XMI Id of its type
	 * if the type can not be resolved at the time the component is processed.
	 */
	private LocalGenericGraphMarker<String> recordComponentType;

	/**
	 * Maps qualified names of domains to the corresponding {@link Domain}
	 * vertex.
	 */
	private Map<String, Domain> domainMap;

	/**
	 * A set of preliminary vertices which are created to have a target vertex
	 * for edges where the real target can only be created later (i.e. forward
	 * references in XMI). After processing is finished, this set must be empty,
	 * since each preliminary vertex has to be replaced by the correct vertex.
	 */
	private Set<Vertex> preliminaryVertices;

	/**
	 * This VertexClass is used to create MayBeNestedIn edges for compositions
	 * which have an incident associationClass.
	 */
	private VertexClass preliminaryMayBeNestedInVertexClass;

	/**
	 * Stores EdgeClasses, which represents a composition with an incident
	 * associationClass
	 */
	private Set<EdgeClass> wrongEdgeClasses;

	/**
	 * This GraphMarker maps composition EdgeClasses stored in wrongEdgeClasses
	 * to the representing MayBeNestedIn edges.
	 */
	private LocalGenericGraphMarker<MayBeNestedIn> getMayBeNestedInRepresentation;

	/**
	 * Remembers the current association end edge ({@link To}/{@link From}
	 * edge), which can be an ownedEnd or an ownedAttribute, for processing of
	 * nested elements.
	 */
	private IncidenceClass currentAssociationEnd;

	/**
	 * The Set of {@link To}/{@link From} edges, which are represented by
	 * ownedEnd elements (used to determine the direction of edges).
	 */
	private Set<IncidenceClass> ownedEnds;

	/**
	 * True if currently processing a constraint (ownedRule) element.
	 */
	private boolean inConstraint;

	/**
	 * The XMI Id of the constrained element if the constraint has exactly one
	 * constrained element, null otherwise. If set to null, the constraint will
	 * be attached to the {@link GraphClass} vertex.
	 */
	private String constrainedElementId;

	/**
	 * Maps the XMI Id of constrained elements to the list of constraints.
	 * Constrains are the character data inside a body element of ownedRule
	 * elements.
	 */
	private Map<String, List<String>> constraints;

	/**
	 * During the conversion of a VertexClass to a EdgeClass the old EdgeClasses
	 * are replaced by IncidenceClasses.<br>
	 * An old EdgeClass: VC--oldICatVC--oldEC--oldICatNewEC->oldVC<br>
	 * The new IncidenceClass: VC--newIC->NewEC<br>
	 * Now the id of oldICatVC, oldICatNewEC and oldEC belongs to newIC.<br>
	 * {@link #constraints} stores GReQL, subsets and redefines constraints.
	 * These constrained elements are identified by their id. If a EdgeClass is
	 * converted into a IncidenceClass the new IncidenceClass has three ids as
	 * described above. To determine if a redefined constraint hides rolenames
	 * at at the EdgeClass the ids of the oldICatNewEC must be stored. By
	 * default a redefined constraint hides the rolenames at the VertexClass.
	 */
	private Set<String> idsOfOldIncidenceclassAtNewEdgeClass;

	/**
	 * Maps the XMI Id of commented elements to the list of comments.
	 */
	private Map<String, List<String>> comments;

	/**
	 * marks incidence classes with the set of redefined rolenames at the verex
	 * class
	 */
	private LocalGenericGraphMarker<Set<String>> redefinesAtVertex;

	/**
	 * marks incidence classes with the set of redefined rolenames at the edge
	 * class
	 */
	private LocalGenericGraphMarker<Set<String>> redefinesAtEdge;

	/**
	 * marks incidence classes with the set of subsetted rolenames
	 */
	private LocalGenericGraphMarker<Set<String>> subsets;

	/**
	 * Stores the {@link VertexClass}es which have an edge stereotype.
	 */
	private Set<VertexClass> edgeStereotypedVertexClasses;

	/**
	 * Stores the {@link EdgeClass}es which were modeled as an
	 * {@link VertexClass} with an edge stereotype.
	 */
	private Set<EdgeClass> edgeStereotypedEdgeClasses;

	/**
	 * Stores the information, if a BinaryEdgeClass was already transformed into
	 * a BinaryEdgeClass.
	 */
	private LocalBooleanGraphMarker isBinaryEdgeAlreadyConverted;

	/**
	 * Stores all nestedIn information to set the {@link MayBeNestedIn} edges
	 * later on.
	 */
	private LocalGenericGraphMarker<Set<GraphElementClass>> nestedElements;

	/**
	 * Stores the information, if the IncidenceClass created out of a EdgeClass
	 * is a nesting composition.
	 */
	private LocalBooleanGraphMarker isNestingIncidenceOrEdgeClass;

	/**
	 * When creating {@link EdgeClass} names, also use the role name of the
	 * 'from' end.
	 */
	private boolean useFromRole;

	/**
	 * After processing is complete, remove {@link Domain} vertices which are
	 * not used by an attribute or by a record domain component.
	 */
	private boolean removeUnusedDomains;

	/**
	 * After processing is complete, also keep {@link Package} vertices which
	 * contain no {@link Domains} and no {@link GraphElementClass}es.
	 */
	private boolean keepEmptyPackages;

	/**
	 * When determining the edge direction, also take navigability of
	 * associations into account (rather than the drawing direction only).
	 */
	private boolean useNavigability;

	/**
	 * Suppresses the direct output into a dot- and tg-file.
	 */
	private boolean suppressOutput;

	/**
	 * Filename for the {@link Schema}.
	 */
	private String filenameSchema;

	/**
	 * Filename for the {@link SchemaGraph};
	 */
	private String filenameSchemaGraph;

	/**
	 * Filename for dot.
	 */
	private String filenameDot;

	/**
	 * Filename for validation
	 */
	private String filenameValidation;

	private String annotatedElementId;

	private boolean inComment;

	private boolean inOwnedAttribute;

	// private GreqlEvaluator edgeClassAcyclicEvaluator;
	// private GreqlEvaluator vertexClassAcyclicEvaluator;

	private boolean inDefaultValue;

	private int modelRootElementNestingDepth;

	private Set<Package> ignoredPackages;

	private boolean inSpecification;

	/**
	 * Stores the ProcessingExceptions durcing the call of isSubsettable
	 */
	private Set<ProcessingException> subsettibilityErrors;

	/**
	 * Processes an XMI-file to a TG-file as schema or a schema in a grUML
	 * graph. For all command line options see
	 * {@link Rsa2Tg#processCommandLineOptions(String[])}.
	 * 
	 * @param args
	 *            {@link String} array of command line options.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		System.out.println("RSA to DHHTG");
		System.out.println("=========");
		JGraLab.setLogLevel(Level.OFF);

		// Retrieving all command line options
		CommandLine cli = processCommandLineOptions(args);

		assert cli != null : "No CommandLine object has been generated!";
		// All XMI input files
		File input = new File(cli.getOptionValue('i'));

		Rsa2Tg r = new Rsa2Tg();

		r.setUseFromRole(cli.hasOption(OPTION_USE_ROLE_NAME));
		r.setRemoveUnusedDomains(cli.hasOption(OPTION_REMOVE_UNUSED_DOMAINS));
		r.setKeepEmptyPackages(cli.hasOption(OPTION_KEEP_EMPTY_PACKAGES));
		r.setUseNavigability(cli.hasOption(OPTION_USE_NAVIGABILITY));

		// apply options
		r.setFilenameSchema(cli.getOptionValue(OPTION_FILENAME_SCHEMA));
		r.setFilenameSchemaGraph(cli
				.getOptionValue(OPTION_FILENAME_SCHEMA_GRAPH));
		r.setFilenameDot(cli.getOptionValue(OPTION_FILENAME_DOT));
		r.setFilenameValidation(cli.getOptionValue(OPTION_FILENAME_VALIDATION));

		// If no output option is selected, Rsa2Tg will write at least the
		// schema file.
		boolean noOutputOptionSelected = !cli.hasOption(OPTION_FILENAME_SCHEMA)
				&& !cli.hasOption(OPTION_FILENAME_SCHEMA_GRAPH)
				&& !cli.hasOption(OPTION_FILENAME_DOT)
				&& !cli.hasOption(OPTION_FILENAME_VALIDATION);
		if (noOutputOptionSelected) {
			System.out.println("No output option has been selected. "
					+ "A DHHTG-file for the Schema will be written.");

			// filename have to be set
			r.setFilenameSchema(createFilename(input));
		}

		try {
			System.out.println("processing: " + input.getPath() + "\n");
			r.process(input.getPath());
		} catch (Exception e) {
			System.err.println("An Exception occured while processing " + input
					+ ".");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		System.out.println("Fini.");
	}

	/**
	 * Processes all command line parameters and returns a {@link CommandLine}
	 * object, which holds all values included in the given {@link String}
	 * array.
	 * 
	 * @param args
	 *            {@link CommandLine} parameters.
	 * @return {@link CommandLine} object, which holds all necessary values.
	 */
	public static CommandLine processCommandLineOptions(String[] args) {

		// Creates a OptionHandler.
		String toolString = "java " + Rsa2Tg.class.getName();
		String versionString = JGraLab.getInfo(false);

		// TODO Add an additional help string to the help page.
		// This String needs to be included into the OptionHandler, but
		// the functionality is not present.

		// String additional =
		// "If no optional output option is selected, a file with the name "
		// + "\"<InputFileName>.rsa.tg\" will be written."
		// + "\n\n"
		// + toolString;

		OptionHandler oh = new OptionHandler(toolString, versionString);

		// Several Options are declared.
		Option validate = new Option(
				OPTION_FILENAME_VALIDATION,
				"report",
				true,
				"(optional): writes a validation report to the given filename. "
						+ "Free naming, but should look like this: '<filename>.html'");
		validate.setRequired(false);
		validate.setArgName("filename");
		oh.addOption(validate);

		Option export = new Option(
				OPTION_FILENAME_DOT,
				"export",
				true,
				"(optional): writes a GraphViz DOT file to the given filename. "
						+ "Free naming, but should look like this: '<filename>.dot'");
		export.setRequired(false);
		export.setArgName("filename");
		oh.addOption(export);

		Option schemaGraph = new Option(
				OPTION_FILENAME_SCHEMA_GRAPH,
				"schemaGraph",
				true,
				"(optional): writes a TG-file of the Schema as graph instance to the given filename. "
						+ "Free naming, but should look like this:  '<filename>.tg'");
		schemaGraph.setRequired(false);
		schemaGraph.setArgName("filename");
		oh.addOption(schemaGraph);

		Option input = new Option("i", "input", true,
				"(required): UML 2.1-XMI exchange model file of the Schema.");
		input.setRequired(true);
		input.setArgName("filename");
		oh.addOption(input);

		Option output = new Option(
				OPTION_FILENAME_SCHEMA,
				"output",
				true,
				"(optional): writes a TG-file of the Schema to the given filename. "
						+ "Free naming, but should look like this: '<filename>.rsa.tg.'");
		output.setRequired(false);
		output.setArgName("filename");
		oh.addOption(output);

		Option fromRole = new Option(
				OPTION_USE_ROLE_NAME,
				"useFromRole",
				false,
				"(optional): if this flag is set, the name of from roles will be used for creating undefined EdgeClass names.");
		fromRole.setRequired(false);
		oh.addOption(fromRole);

		Option unusedDomains = new Option(OPTION_REMOVE_UNUSED_DOMAINS,
				"removeUnusedDomains", false,
				"(optional): if this flag is set, all unused domains be deleted.");
		unusedDomains.setRequired(false);
		oh.addOption(unusedDomains);

		Option emptyPackages = new Option(OPTION_KEEP_EMPTY_PACKAGES,
				"keepEmptyPackages", false,
				"(optional): if this flag is set, empty packages will be retained.");
		unusedDomains.setRequired(false);
		oh.addOption(emptyPackages);

		Option navigability = new Option(
				OPTION_USE_NAVIGABILITY,
				"useNavigability",
				false,
				"(optional): if this flag is set, navigability information will be interpreted as reading direction.");
		navigability.setRequired(false);
		oh.addOption(navigability);

		// Parses the given command line parameters with all created Option.
		return oh.parse(args);
	}

	/**
	 * Creates a file path similar to the of <code>inputFile</code>, but with
	 * the file extension '.rsa.tg'.
	 * 
	 * @param file
	 *            Is a File object, which is path used to created the new Path.
	 * @return New generated Path with the extension '.rsa.tg'.
	 */
	public static String createFilename(File file) {
		StringBuilder filenameBuilder = new StringBuilder();

		// The path of the input XMI-file is used.
		filenameBuilder.append(file.getParent());
		filenameBuilder.append(File.separatorChar);

		String filename = file.getName();
		int periodePosition = filename.lastIndexOf('.');
		if (periodePosition != -1) {
			filename = filename.substring(0, periodePosition);
		}

		// The simple name of the Schema will be the filename.
		// filenameBuilder.append(r.getSchemaGraph().getFirstSchema()
		// .get_name());
		filenameBuilder.append(filename);
		// The extension is ....
		filenameBuilder.append(".rsa.tg");
		return filenameBuilder.toString();
	}

	/**
	 * Creates a Rsa2Tg converter.
	 */
	public Rsa2Tg() {
		// Sets all names of XML-elements, which should be ignored.
		addIgnoredElements("profileApplication", "packageImport",
				"Ecore:EReference");
	}

	/**
	 * Sets up several a {@link SchemaGraph} and data structures before the
	 * processing can start.
	 */
	@Override
	public void startDocument() {

		sg = GrumlSchema.instance().createSchemaGraphInMem();

		// Initializing all necessary data structures for processing purposes.
		xmiIdStack = new Stack<String>();
		idMap = new HashMap<String, Vertex>();
		packageStack = new Stack<Package>();
		generalizations = new LocalGenericGraphMarker<Set<String>>(sg);
		realizations = new HashMap<String, Set<String>>();
		attributeType = new LocalGenericGraphMarker<String>(sg);
		recordComponentType = new LocalGenericGraphMarker<String>(sg);
		domainMap = new HashMap<String, Domain>();
		preliminaryVertices = new HashSet<Vertex>();
		ownedEnds = new HashSet<IncidenceClass>();
		constraints = new HashMap<String, List<String>>();
		idsOfOldIncidenceclassAtNewEdgeClass = new HashSet<String>();
		comments = new HashMap<String, List<String>>();
		redefinesAtVertex = new LocalGenericGraphMarker<Set<String>>(sg);
		redefinesAtEdge = new LocalGenericGraphMarker<Set<String>>(sg);
		subsets = new LocalGenericGraphMarker<Set<String>>(sg);
		edgeStereotypedVertexClasses = new HashSet<VertexClass>();
		edgeStereotypedEdgeClasses = new HashSet<EdgeClass>();
		isBinaryEdgeAlreadyConverted = new LocalBooleanGraphMarker(sg);
		nestedElements = new LocalGenericGraphMarker<Set<GraphElementClass>>(sg);
		isNestingIncidenceOrEdgeClass = new LocalBooleanGraphMarker(sg);
		ignoredPackages = new HashSet<Package>();
		modelRootElementNestingDepth = 1;
		preliminaryMayBeNestedInVertexClass = sg.createVertexClass();
		preliminaryMayBeNestedInVertexClass
				.set_qualifiedName("preliminary VertexClass for preliminary MayBeNestedInEdges");
		wrongEdgeClasses = new HashSet<EdgeClass>();
		getMayBeNestedInRepresentation = new LocalGenericGraphMarker<MayBeNestedIn>(
				sg);
	}

	/**
	 * Processes a XML element and decides how to handle it in order to get a
	 * {@link Schema} element.
	 * 
	 * @throws XMLStreamException
	 */
	@Override
	protected void startElement(String name) throws XMLStreamException {

		// TODO Comment the Meaning of this action. Does '1' or '2' have a
		// meaning?
		if ((getNestingDepth() == 1) && name.equals(XMI_XMI)) {
			modelRootElementNestingDepth = 2;
			return;
		}

		String xmiId = getAttribute(XMI_NAMESPACE_PREFIX, "id");
		xmiIdStack.push(xmiId);

		Vertex vertexId = null;
		if (getNestingDepth() == modelRootElementNestingDepth) {
			// In case of a root element
			vertexId = createDefaultElements(name);
		} else {
			vertexId = processXMIElements(name, xmiId);
		}

		// Links an existing XMI-id to a Vertex-id
		if ((xmiId != null) && (vertexId != null)) {
			idMap.put(xmiId, vertexId);
		}
	}

	private Vertex createDefaultElements(String name) throws XMLStreamException {
		if (name.equals(UML_MODEL) || name.equals(UML_PACKAGE)) {
			setSchemaQualifiedName();
			createGraphClass();
			createDefaultPackage();
		} else {
			// Unexpected root element
			throw new ProcessingException(getParser(), getFileName(),
					"Root element must be " + UML_MODEL + " or " + UML_PACKAGE
							+ ", buf was " + name);
		}
		return schema;
	}

	private void setSchemaQualifiedName() throws XMLStreamException {
		// Gets the Schema name, creates a Schema and processes it.
		String nm = getAttribute(UML_ATTRIBUTE_NAME);

		int p = nm.lastIndexOf('.');
		schema = sg.createSchema();

		// In case nm (:= Schema-name) contains only a name and not a
		// package prefix
		if (p == -1) {
			throw new ProcessingException(getParser(), getFileName(),
					"A Schema must have a package prefix!\nProcessed qualified name: "
							+ nm);
		}

		schema.set_packagePrefix(nm.substring(0, p));
		schema.set_name(nm.substring(p + 1));
	}

	private void createGraphClass() {
		// Generates a GraphClass and links it with the created Schema
		graphClass = sg.createGraphClass();
		sg.createDefinesGraphClass(schema, graphClass);
	}

	private void createDefaultPackage() {
		// Creates a default Package, links it and pushes it to the
		// packageStack.
		Package defaultPackage = sg.createPackage();
		defaultPackage.set_qualifiedName("");
		sg.createContainsDefaultPackage(schema, defaultPackage);
		packageStack.push(defaultPackage);
	}

	private Vertex processXMIElements(String name, String xmiId)
			throws XMLStreamException {
		// inside top level element

		// Type is retrieved
		String type = getAttribute(XMI_NAMESPACE_PREFIX, UML_ATTRIBUTE_TYPE);
		Vertex vertexId = null;
		// Package element, which TODO
		if (name.equals(UML_PACKAGED_ELEMENT)) {
			if (type.equals(UML_PACKAGE)) {
				vertexId = handlePackage();
			} else if (type.equals(UML_CLASS)) {
				vertexId = handleClass(xmiId);
			} else if (type.equals(UML_ASSOCIATION)
					|| type.equals(UML_ASSOCIATION_CLASS)) {
				vertexId = handleAssociation(xmiId,
						type.equals(UML_ASSOCIATION_CLASS));
			} else if (type.equals(UML_ENUMERATION)) {
				vertexId = handleEnumeration();
			} else if (type.equals(UML_PRIMITIVE_TYPE)) {
				vertexId = handlePrimitiveType(xmiId);
			} else if (type.equals(UML_REALIZATION)) {
				handleRealization();
			} else {
				throw new ProcessingException(getParser(), getFileName(),
						createUnexpectedElementMessage(name, type));
			}

		} else if (name.equals(UML_OWNEDRULE)) {
			// Owned rule
			inConstraint = true;
			constrainedElementId = getAttribute(UML_ATTRIBUTE_CONSTRAINED_ELEMENT);
			// If the ID is null, the constraint is attached to the
			// GraphClass

			if (constrainedElementId != null) {
				// There can be more than one ID, separated by spaces ==>
				// the constraint is attached to the GraphClass.
				int p = constrainedElementId.indexOf(' ');
				if (p >= 0) {
					constrainedElementId = null;
				}
			}
		} else if (name.equals(UML_BODY)) {
			if (!inConstraint && !inComment && !inDefaultValue
					&& !inSpecification) {
				// Throw an error for body elements, which aren't
				// contained in a constraint or comment
				throw new ProcessingException(getParser(), getFileName(),
						createUnexpectedElementMessage(name, null));
			}
		} else if (name.equals(UML_SPECIFICATION)) {
			// Specification is ignored for most elements
			inSpecification = true;
		} else if (name.equals(UML_LANGUAGE)) {
			if (!inConstraint) {
				// Throw an error for specification elements, which aren't
				// contained in a constraint.
				throw new ProcessingException(getParser(), getFileName(),
						createUnexpectedElementMessage(name, null));
			}
		} else if (name.equals(UML_OWNEDEND)) {
			// Owned end marks the end of the current class, which should be
			// an edgeClasss.
			if (type.equals(UML_PROPERTY)
					&& (currentClass instanceof EdgeClass)) {
				handleAssociationEnd(xmiId);
			} else {
				throw new ProcessingException(getParser(), getFileName(),
						createUnexpectedElementMessage(name, type));
			}

		} else if (name.equals(UML_OWNED_ATTRIBUTE)) {
			inOwnedAttribute = true;
			// Handles the attributes of the current element
			if (type.equals(UML_PROPERTY)) {
				handleOwnedAttribute(xmiId);
			} else {
				throw new ProcessingException(getParser(), getFileName(),
						createUnexpectedElementMessage(name, type));
			}

		} else if (name.equals(UML_ATTRIBUTE_TYPE)) {
			// Handles the type of the current attribute, which should be a
			// primitive type.
			if (!inDefaultValue) {
				if (type.equals(UML_PRIMITIVE_TYPE)) {
					handleNestedTypeElement(xmiId);
				} else {
					throw new ProcessingException(getParser(), getFileName(),
							createUnexpectedElementMessage(name, type));
				}
			}
		} else if (name.equals(UML_OWNED_LITERAL)) {
			// Handles the literal of the current enumeration.
			if (type.equals(UML_ENUMERATION_LITERAL)) {
				handleEnumerationLiteral();
			} else {
				throw new ProcessingException(getParser(), getFileName(),
						createUnexpectedElementMessage(name, type));
			}
		} else if (name.equals(XMI_EXTENSION)) {
			// ignore
		} else if (name.equals(UML_E_ANNOTATIONS)) {
			// ignore
		} else if (name.equals(UML_GENERALIZATION)) {
			handleGeneralization();
		} else if (name.equals(UML_DETAILS)) {
			handleStereotype();
		} else if (name.equals(UML_LOWER_VALUE)) {
			handleLowerValue();
		} else if (name.equals(UML_UPPER_VALUE)) {
			handleUpperValue();
		} else if (name.equals(UML_OWNED_COMMENT)) {
			annotatedElementId = getAttribute(UML_ANNOTATED_ELEMENT);
			inComment = true;
		} else if (name.equals(UML_DEFAULT_VALUE)) {
			String xmiType = getAttribute(XMI_NAMESPACE_PREFIX, XMI_TYPE);
			if (isPrimitiveDefaultValue(xmiType)) {
				// boolean, integer, string, or enumeration value
				handlePrimitiveDefaultValue(xmiId, xmiType);
			} else if (!xmiType.equals(UML_OPAQUE_EXPRESSION)) {
				System.out.println("Warning: Unexpected default value type '"
						+ xmiType + "' for attribute '"
						+ currentAttribute.get_name() + "' of "
						+ currentClass.getM1Class().getSimpleName() + " '"
						+ currentClass.get_qualifiedName() + "' in file '"
						+ getFileName() + "' at line "
						+ getParser().getLocation().getLineNumber());
				// throw new ProcessingException(getParser(), getFileName(),
				// "Unexpected default value type " + xmiType
				// + " for attribute '"
				// + currentAttribute.get_name() + "' of "
				// + currentClass.getM1Class().getSimpleName()
				// + " '" + currentClass.get_qualifiedName() + "'");
			}
			inDefaultValue = true;
		} else {
			// for unexpected XMI tags
			throw new ProcessingException(getParser(), getFileName(),
					createUnexpectedElementMessage(name, type));
		}
		return vertexId;
	}

	private boolean isPrimitiveDefaultValue(String xmiType) {
		return xmiType.equals(UML_LITERAL_STRING)
				|| xmiType.equals(UML_LITERAL_INTEGER)
				|| xmiType.equals(UML_LITERAL_BOOLEAN)
				|| xmiType.equals(UML_INSTANCE_VALUE);
	}

	private void handlePrimitiveDefaultValue(String xmiId, String xmiType)
			throws XMLStreamException {
		if (xmiType.equals(UML_INSTANCE_VALUE)) {
			String value = getAttribute(UML_ATTRIBUTE_NAME);
			handleDefaultValue(xmiId, value);
			return;
		}

		String value = getAttribute(UML_ATTRIBUTE_VALUE);
		if (xmiType.equals(UML_LITERAL_BOOLEAN)) {
			if (value == null) {
				value = "f"; // XML schema default
			} else {
				assert value.equals("true") || value.equals("false");
				// true/false => t/f
				value = value.substring(0, 1);
			}
			handleDefaultValue(xmiId, value);
			return;
		} else if (xmiType.equals(UML_LITERAL_INTEGER)) {
			if (value == null) {
				value = "0"; // XML schema default
			}
			handleDefaultValue(xmiId, value);
			return;
		}

		if (value == null) {
			System.out
					.println("Warning: Undefined default value for attribute '"
							+ currentAttribute.get_name() + "' of "
							+ currentClass.getM1Class().getSimpleName() + " '"
							+ currentClass.get_qualifiedName() + "' in file '"
							+ getFileName() + "' at line "
							+ getParser().getLocation().getLineNumber());
			return;
		}

		if (xmiType.equals(UML_LITERAL_STRING)) {
			value = "\"" + value + "\"";
			handleDefaultValue(xmiId, value);
		} else {
			System.out.println("Warning: Undefined default value type '"
					+ xmiType + "' for attribute '"
					+ currentAttribute.get_name() + "' of "
					+ currentClass.getM1Class().getSimpleName() + " '"
					+ currentClass.get_qualifiedName() + "' in file '"
					+ getFileName() + "' at line "
					+ getParser().getLocation().getLineNumber());
		}
	}

	private void handleDefaultValue(String xmiId, String value) {
		if (currentAttribute == null) {
			throw new ProcessingException(
					getFileName(),
					"Found a <defaultValue> tag (XMI id "
							+ xmiId
							+ ") outside an attribute definition (e.g. in a <<record>> class)");
		}
		currentAttribute.set_defaultValue(value);
	}

	/**
	 * Processes a XML end element tags in order to set internal states.
	 * 
	 * @param name
	 *            Name of the XML element, which will be closed.
	 * @param content
	 *            StringBuilder object, which holds the contents of the current
	 *            end element.
	 * 
	 * @throws XMLStreamException
	 */
	@Override
	protected void endElement(String name, StringBuilder content)
			throws XMLStreamException {
		if (getNestingDepth() < modelRootElementNestingDepth) {
			return;
		}

		String xmiId = xmiIdStack.pop();

		if (name.equals(UML_BODY)) {
			if (inConstraint) {
				assert !inComment && !inDefaultValue;
				handleConstraint(content.toString().trim().replace("\\s+", " "));
			} else if (inComment) {
				assert !inDefaultValue;
				handleComment(content.toString());
			} else if (inDefaultValue) {
				handleDefaultValue(xmiId, content.toString().trim());
			}
		}
		AttributedElement<?, ?> elem = idMap.get(xmiId);
		if (elem != null) {
			if (elem instanceof Package) {

				// There should be at least one package element in the
				// stack.
				if (packageStack.size() <= 1) {
					throw new ProcessingException(getParser(), getFileName(),
							"XMI file is malformed. There is probably one end element to much.");
				}
				packageStack.pop();
			} else if (elem instanceof AttributedElementClass) {
				currentClassId = null;
				currentClass = null;
				currentAttribute = null;
			} else if (elem instanceof RecordDomain) {
				currentRecordDomain = null;
				currentAttribute = null;
			} else if (elem instanceof Attribute) {
				currentAttribute = null;
			}
		}
		if (name.equals(UML_PACKAGE)) {
			packageStack.pop();

			// There should be no packages left over.
			if (packageStack.size() != 0) {
				throw new ProcessingException(getParser(), getFileName(),
						"XMI file is malformed. There is probably one end element to much.");
			}
		} else if (name.equals(UML_OWNED_ATTRIBUTE)) {
			currentRecordDomainComponent = null;
			if (currentAssociationEnd != null) {
				checkMultiplicities(currentAssociationEnd);
				currentAssociationEnd = null;
			}
			inOwnedAttribute = false;
		} else if (name.equals(UML_OWNEDEND)) {
			checkMultiplicities(currentAssociationEnd);
			currentAssociationEnd = null;
		} else if (name.equals(UML_OWNEDRULE)) {
			inConstraint = false;
			constrainedElementId = null;
		} else if (name.equals(UML_OWNED_COMMENT)) {
			inComment = false;
			annotatedElementId = null;
		} else if (name.equals(UML_DEFAULT_VALUE)) {
			inDefaultValue = false;
		} else if (name.equals(UML_SPECIFICATION)) {
			inSpecification = false;
		}
	}

	private void checkMultiplicities(IncidenceClass inc) {
		checkMultiplicityValues(inc, inc.get_minEdgesAtVertex(),
				inc.get_maxEdgesAtVertex());
		checkMultiplicityValues(inc, inc.get_minVerticesAtEdge(),
				inc.get_maxVerticesAtEdge());
	}

	private void checkMultiplicityValues(IncidenceClass inc, int min, int max) {
		assert min >= 0;
		assert max > 0;
		if (min == Integer.MAX_VALUE) {
			throw new ProcessingException(getFileName(),
					"Error in multiplicities: lower bound must not be *"
							+ " at association end " + inc);
		}
		if (min > max) {
			throw new ProcessingException(getFileName(),
					"Error in multiplicities: lower bound (" + min
							+ ") must be <= upper bound (" + max
							+ ") at association end " + inc);
		}
	}

	private void handleComment(String body) {
		// decode RSA's clumsy HTML-like comments...
		body = body.replaceAll("\\s+", " ");
		body = body.replace("<p>", " ");
		body = body.replace("</p>", "\n");
		String[] lines = body.split("\n");
		StringBuilder text = new StringBuilder();
		for (String line : lines) {
			line = line.replaceAll("\\s+", " ").trim();
			if (line.length() > 0) {
				if (text.length() > 0) {
					text.append("\n");
				}
				text.append(line);
			}
		}
		if (text.length() == 0) {
			return;
		}
		List<String> commentList = comments.get(annotatedElementId);
		if (commentList == null) {
			commentList = new LinkedList<String>();
			comments.put(annotatedElementId, commentList);
		}
		commentList.add(text.toString());
	}

	/**
	 * Finalizes the created {@link SchemaGraph} by creating missing links
	 * between several objects.
	 * 
	 * @throws XMLStreamException
	 * @throws GraphIOException
	 */
	@Override
	public void endDocument() throws XMLStreamException {
		// finalizes processing by creating missing links
		assert schema != null;
		assert graphClass != null;
		// The qualified name of the GraphClass should be set.
		if (graphClass.get_qualifiedName() == null) {
			throw new ProcessingException(getFileName(),
					"No <<graphclass>> defined in schema '"
							+ schema.get_packagePrefix() + "."
							+ schema.get_name() + "'");
		}

		// Checks whether each enum domain has at least one literal
		checkEnumDomains();

		// transform the VertexClasses with an edge stereotype to EdgeClasses
		convertToEdgeClasses();

		// Now the RSA XMI file has been processed, pending actions to link
		// elements can be performed
		linkGeneralizations();
		linkRecordDomainComponents();
		linkAttributeDomains();

		deleteCompositionsWhichRepresentsNoBinaryEdgeClass();

		removeIgnoredPackages();

		if (isUseNavigability()) {
			correctEdgeDirection();
		}

		// the following depends on correct subsets relations between incidences
		attachConstraints();

		// the following depends on correct edge directions and edgeclass
		// generalizations
		createSubsetsAndRedefinesRelations();

		createMayBeNestedIn();

		// convert EdgeClass to BinaryEdgeClass where possible
		convertEdgeClassesToBinaryEdgeClasses();

		createEdgeClassNames();

		if (isRemoveUnusedDomains()) {
			removeUnusedDomains();
		}

		attachComments();

		if (!isKeepEmptyPackages()) {
			removeEmptyPackages();
		}

		// preliminaryVertices must be empty at this time of processing,
		// otherwise there is an error...
		if (!preliminaryVertices.isEmpty()) {
			System.err.println("Remaining preliminary vertices ("
					+ preliminaryVertices.size() + "):");
			for (Vertex v : preliminaryVertices) {
				System.err.println(attributedElement2String(v));
			}
			throw new ProcessingException(getFileName(),
					"There are still vertices left over. ");
		}

		if (!suppressOutput) {
			try {
				writeOutput();
			} catch (GraphIOException e) {
				throw new XMLStreamException(e);
			}
		}
	}

	private void deleteCompositionsWhichRepresentsNoBinaryEdgeClass() {
		for (EdgeClass ec = sg.getFirstEdgeClass(true); ec != null; ec = ec
				.getNextEdgeClass(true)) {
			boolean isBinaryEdgeClassCandidate = isValidBinaryEdgeClassCandidate(ec);
			if (!isBinaryEdgeClassCandidate) {
				for (Incidence i = ec
						.getFirstIncidence(ConnectsToEdgeClass_connectedEdgeClass.class); i != null;) {
					IncidenceClass incidenceClass = (IncidenceClass) i
							.getThat();
					i = i.getNextIncidenceAtVertex(ConnectsToEdgeClass_connectedEdgeClass.class);
					if (isNestingIncidenceOrEdgeClass.isMarked(incidenceClass)) {
						// at this point a composition edge which ends at a
						// EdgeClass was transformed into a IncidenceClass
						deleteIncidenceClass(incidenceClass);
					}
				}
			}
		}
	}

	private void deleteIncidenceClass(IncidenceClass incidenceClass) {
		if (generalizations.isMarked(incidenceClass)) {
			generalizations.removeMark(incidenceClass);
		}

		if (redefinesAtVertex.isMarked(incidenceClass)) {
			redefinesAtVertex.removeMark(incidenceClass);
		}
		if (redefinesAtEdge.isMarked(incidenceClass)) {
			redefinesAtEdge.removeMark(incidenceClass);
		}

		if (subsets.isMarked(incidenceClass)) {
			subsets.removeMark(incidenceClass);
		}

		ArrayList<String> ids = new ArrayList<String>();
		for (Entry<String, Vertex> entry : idMap.entrySet()) {
			if (entry.getValue() == incidenceClass) {
				ids.add(entry.getKey());
			}
		}

		for (String id : ids) {
			idMap.remove(id);
			idsOfOldIncidenceclassAtNewEdgeClass.remove(id);
			constraints.remove(id);
			comments.remove(id);
		}

		incidenceClass.delete();
	}

	/**
	 * 
	 */
	private void createMayBeNestedIn() {
		System.out.println("Create MayBeNestedIn relations ...");
		updateNestedElements();

		// stores the GraphElementClass which have nested elements but are not
		// nested in another GraphElementClass
		Queue<GraphElementClass> workingList = new LinkedList<GraphElementClass>();
		Queue<GraphElementClass> topLevelNestingElements = new LinkedList<GraphElementClass>();

		// handle MayBeNestedIn edges which were created as compositions with
		// incident associationClasses
		for (GraphElement<?, ?, ?> ge : getMayBeNestedInRepresentation
				.getMarkedElements()) {
			EdgeClass oldEC = (EdgeClass) ge;
			assert oldEC.getDegree() == 3;

			IncidenceClass containingIC = null, containedIC = null;
			for (ConnectsToEdgeClass_connectedEdgeClass i : oldEC
					.getIncidences(ConnectsToEdgeClass_connectedEdgeClass.class)) {
				IncidenceClass ic = (IncidenceClass) i.getThat();
				if (ic.get_incidenceType() == IncidenceType.AGGREGATION
						|| (ic.get_incidenceType() == IncidenceType.EDGE && containingIC != null)) {
					throw new ProcessingException(
							getParser(),
							getFileName(),
							"Association '"
									+ oldEC.get_qualifiedName()
									+ "' has an incident EdgeClass. This implies, that it must be a composition.");
				} else if (ic.get_incidenceType() == IncidenceType.COMPOSITION) {
					assert containedIC == null;
					containedIC = ic;
				} else {
					assert containingIC == null;
					containingIC = ic;
				}
			}

			assert containedIC != null && containingIC != null;

			GraphElementClass containingGEC = getConnectedVertexClass(containingIC);
			GraphElementClass containedGEC = getConnectedVertexClass(containedIC);
			assert containingGEC != containedGEC
					&& (containingGEC != null || containedGEC != null);

			MayBeNestedIn mbni = getMayBeNestedInRepresentation.getMark(ge);
			assert mbni != null;
			GraphElementClass containedMbniEnd = (GraphElementClass) mbni
					.getAlpha();
			GraphElementClass containingMbniEnd = (GraphElementClass) mbni
					.getOmega();
			if (containedMbniEnd == preliminaryMayBeNestedInVertexClass) {
				assert containedGEC != null;
				assert containingMbniEnd != preliminaryMayBeNestedInVertexClass;
				mbni.delete();
				sg.createMayBeNestedIn(containedGEC, containingMbniEnd);
			} else if (containingMbniEnd == preliminaryMayBeNestedInVertexClass) {
				assert containingGEC != null;
				assert containedMbniEnd != preliminaryMayBeNestedInVertexClass;
				mbni.delete();
				sg.createMayBeNestedIn(containedMbniEnd, containingGEC);
			}
			oldEC.delete();
			containedIC.delete();
			containingIC.delete();
		}
		// all preliminary MayBeNested in edges must be removed
		if (preliminaryMayBeNestedInVertexClass.getDegree() == 0) {
			preliminaryMayBeNestedInVertexClass.delete();
		} else {
			StringBuilder sb = new StringBuilder();
			for (Incidence i : preliminaryMayBeNestedInVertexClass
					.getIncidences()) {
				MayBeNestedIn mbni = (MayBeNestedIn) i.getEdge();
				sb.append("\n"
						+ ((GraphElementClass) mbni.getAlpha())
								.get_qualifiedName()
						+ "-->"
						+ ((GraphElementClass) mbni.getOmega())
								.get_qualifiedName());
			}
			throw new ProcessingException(getParser(), getFileName(),
					"There still exists MaBeNestedIn edges which werde not created completely:"
							+ sb.toString());

		}

		try {
			GraphIO.saveGraphToFile("D:\\Beispiele\\_test2.dhhtg", sg, null);
		} catch (GraphIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Sonderknoten behandeln

		// all edges have to be treated
		for (EdgeClass ec : sg.getEdgeClassVertices()) {
			workingList.add(ec);
			topLevelNestingElements.add(ec);
		}

		// create the explicitly modeled MayBeNestedIn edges
		for (GraphElement<?, ?, ?> ge : nestedElements.getMarkedElements()) {
			GraphElementClass containingGEC = (GraphElementClass) ge;
			assert nestedElements.getMark(containingGEC) != null;
			assert !nestedElements.getMark(containingGEC).isEmpty();

			for (GraphElementClass containedGEC : nestedElements
					.getMark(containingGEC)) {
				sg.createMayBeNestedIn(containedGEC, containingGEC);
				insertContainingGECIntoWorkingList(containingGEC, containedGEC,
						topLevelNestingElements);
			}
		}

		checkAcyclicityOfMayBeNestedIn(topLevelNestingElements);

		// check correctness of explicit modeled MayBeNestedIn edges and create
		// implicit MayBeNestedIn edges during a breadth first search over the
		// GraphElementClasses participating in the MayBeNestedIn tree
		LocalBooleanGraphMarker isImplicitlyNested = new LocalBooleanGraphMarker(
				sg);
		while (!workingList.isEmpty()) {
			GraphElementClass current = workingList.poll();
			assert current != null;

			if (EdgeClass.class.isInstance(current)) {
				EdgeClass containedEC = (EdgeClass) current;

				// check constraints for explicitly nested EdgeClasses
				for (MayBeNestedIn_nestedElement i : containedEC
						.getIncidences(MayBeNestedIn_nestedElement.class)) {
					if (!isImplicitlyNested.isMarked(i.getEdge())) {
						GraphElementClass containingGEC = (GraphElementClass) i
								.getThat();
						checkNestingConstraints(containedEC, containingGEC);
					}
				}

				// create implicit MayBeNestedIn edges
				for (GraphElementClass containingGEC : getAllNestingElements(containedEC)) {
					isImplicitlyNested.mark(sg.createMayBeNestedIn(containedEC,
							containingGEC));
					if (topLevelNestingElements.contains(containedEC)) {
						topLevelNestingElements.remove(containedEC);
					}
				}
			}

			// insert all nested GraphElementClasses into workingList
			for (MayBeNestedIn_nestingElement i : current
					.getIncidences(MayBeNestedIn_nestingElement.class)) {
				if (!workingList.contains(i.getThat())
						&& !isImplicitlyNested.isMarked(i.getEdge())) {
					workingList.add((GraphElementClass) i.getThat());
				}
			}
		}

		checkAcyclicityOfMayBeNestedIn(topLevelNestingElements);
		// TODO at work
	}

	/**
	 * If an EdgeClass ec is nested in {@link #nestedElements} and ec is a
	 * BinaryEdgClass candidate which has not the stereotype nested, than ec
	 * must be replaced by the incident VertexClass, which is not key in
	 * {@link #nestedElements}.
	 */
	private void updateNestedElements() {
		for (GraphElement<?, ?, ?> ge : nestedElements.getMarkedElements()) {
			GraphElementClass key = (GraphElementClass) ge;

			Set<GraphElementClass> toDelete = new HashSet<GraphElementClass>();
			Set<GraphElementClass> toAdd = new HashSet<GraphElementClass>();
			Set<GraphElementClass> values = nestedElements.getMark(key);
			for (GraphElementClass value : values) {
				if (EdgeClass.class.isInstance(value)
						&& isValidBinaryEdgeClassCandidate((EdgeClass) value)) {

					if (!value.isValid()) {
						toDelete.add(value);
						continue;
					}

					for (ConnectsToEdgeClass_connectedEdgeClass i : value
							.getIncidences(ConnectsToEdgeClass_connectedEdgeClass.class)) {
						if (!isNestingIncidenceOrEdgeClass
								.isMarked(i.getThat())
								&& VertexClass.class.isInstance(key)) {
							VertexClass vc = getConnectedVertexClass((IncidenceClass) i
									.getThat());
							if (vc != key) {
								toAdd.add(vc);
								toDelete.add(value);
							}
						}
					}
				}
			}

			for (GraphElementClass del : toDelete) {
				values.remove(del);
			}

			for (GraphElementClass add : toAdd) {
				values.add(add);
			}

			if (values.isEmpty()) {
				nestedElements.removeMark(key);
			}
		}
	}

	private List<GraphElementClass> getAllNestingElements(EdgeClass containedEC) {
		List<GraphElementClass> nestedGECs = new LinkedList<GraphElementClass>();

		Set<VertexClass> incidentVertexClasses = new HashSet<VertexClass>();

		LocalGenericGraphMarker<Set<GraphElementClass>> nestedIncidentVertexClasses = new LocalGenericGraphMarker<Set<GraphElementClass>>(
				sg);
		LocalGenericGraphMarker<Set<Edge>> edgesInPath = new LocalGenericGraphMarker<Set<Edge>>(
				sg);

		// depth first search started at each incident VertexClass
		for (IncidenceClass ic : getAllIncidenceClasses(containedEC)) {
			VertexClass vc = getConnectedVertexClass(ic);
			if (!incidentVertexClasses.contains(vc)) {
				markAllNestingGraphElementClasses(nestedIncidentVertexClasses,
						vc, edgesInPath);
				incidentVertexClasses.add(vc);
			}
		}

		// all edges are candidates
		for (GraphElement<?, ?, ?> ge : nestedIncidentVertexClasses
				.getMarkedElements()) {
			if (EdgeClass.class.isInstance(ge)) {
				// ge must nest one incident VertexClasses of containedEC
				if (ge != containedEC) {
					nestedGECs.add((EdgeClass) ge);
				}
			} else {
				if (nestedIncidentVertexClasses.getMark(ge).size() == incidentVertexClasses
						.size()) {
					// ge must nest all incident VertetexClasses of containedEC
					nestedGECs.add((VertexClass) ge);
				}
			}
		}

		Set<GraphElementClass> toDelete = new HashSet<GraphElementClass>();
		for (int i = 0; i < nestedGECs.size(); i++) {
			GraphElementClass currentI = nestedGECs.get(i);
			Set<Edge> edgesOfI = edgesInPath.getMark(currentI);
			for (int j = 0; j < nestedGECs.size(); j++) {
				if (j == i) {
					continue;
				}
				GraphElementClass currentJ = nestedGECs.get(j);
				Set<Edge> edgesOfJ = edgesInPath.getMark(currentJ);
				if (edgesOfJ.containsAll(edgesOfI)
						&& !existsAPath(incidentVertexClasses, currentJ,
								edgesOfI)) {
					/*
					 * edgesOfJ is the set of all edges, which are on a path to
					 * GraphElementClass j. edgesOfI is the set of all edges,
					 * which are on a path to GraphElementClass j. If edgesOfI
					 * is a subset of edgesOfJ and j could not be reached via a
					 * path, which does not contain edges of the set edgesOfI,
					 * then j can be deleted.
					 */
					toDelete.add(currentJ);
				}
			}
		}
		nestedGECs.removeAll(toDelete);

		return nestedGECs;
	}

	private boolean existsAPath(Set<VertexClass> starts,
			GraphElementClass target, Set<Edge> forbiddenEdges) {
		for (VertexClass start : starts) {
			if (existsAPath(start, target, forbiddenEdges)) {
				return true;
			}
		}
		return false;
	}

	private boolean existsAPath(VertexClass start, GraphElementClass target,
			Set<Edge> forbiddenEdges) {
		if (start == target) {
			return true;
		}

		// if incidentVertexClass could be nested in gec, it could be nested in
		// all subclasses of gec, too
		for (SpecializesTypedElementClass_superclass i : start
				.getIncidences(SpecializesTypedElementClass_superclass.class)) {
			if (!forbiddenEdges.contains(i.getEdge())) {
				if (existsAPath((VertexClass) i.getThat(), target,
						forbiddenEdges)) {
					return true;
				}
			}
		}

		// if incidentVertexClass could be nested in gec, it could be nested in
		// all nesting GraphElementClasses of gec, too
		for (MayBeNestedIn_nestedElement i : start
				.getIncidences(MayBeNestedIn_nestedElement.class)) {
			if (!forbiddenEdges.contains(i.getEdge())) {
				if (VertexClass.class.isInstance(i.getThat())) {
					if (existsAPath((VertexClass) i.getThat(), target,
							forbiddenEdges)) {
						return true;
					}
				} else {
					// EdgeClasses do not propagate the nesting information to
					// its nesting elements
					if (i.getThat() == target) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void markAllNestingGraphElementClasses(
			LocalGenericGraphMarker<Set<GraphElementClass>> nestedIncidentVertexClasses,
			VertexClass vc, LocalGenericGraphMarker<Set<Edge>> edgesInPath) {

		// mark all gec in which vc could be nested directly
		for (MayBeNestedIn_nestedElement i : vc
				.getIncidences(MayBeNestedIn_nestedElement.class)) {

			Set<Edge> currentEdges = edgesInPath.getMark(i.getThat());
			if (currentEdges == null) {
				currentEdges = new HashSet<Edge>();
				edgesInPath.mark(i.getThat(), currentEdges);
			}
			if (edgesInPath.isMarked(i.getThis())) {
				currentEdges.addAll(edgesInPath.getMark(i.getThis()));
			}
			currentEdges.add(i.getEdge());

			markNestingGEC(nestedIncidentVertexClasses, vc, i, edgesInPath);
		}
		// mark all gec in which a superclass of vc could be nested
		// directly
		markAllNestingSuperClassesOfGraphElementClass(vc,
				nestedIncidentVertexClasses, vc, edgesInPath);
	}

	private void markAllNestingSuperClassesOfGraphElementClass(
			GraphElementClass gec,
			LocalGenericGraphMarker<Set<GraphElementClass>> nestedIncidentVertexClasses,
			VertexClass vc, LocalGenericGraphMarker<Set<Edge>> edgesInPath) {
		for (SpecializesTypedElementClass_subclass subI : gec
				.getIncidences(SpecializesTypedElementClass_subclass.class)) {

			Set<Edge> currentEdges = edgesInPath.getMark(subI.getThat());
			if (currentEdges == null) {
				currentEdges = new HashSet<Edge>();
				edgesInPath.mark(subI.getThat(), currentEdges);
			}
			if (edgesInPath.isMarked(subI.getThis())) {
				currentEdges.addAll(edgesInPath.getMark(subI.getThis()));
			}
			currentEdges.add(subI.getEdge());

			for (MayBeNestedIn_nestedElement i : subI.getThat().getIncidences(
					MayBeNestedIn_nestedElement.class)) {

				currentEdges = edgesInPath.getMark(i.getThat());
				if (currentEdges == null) {
					currentEdges = new HashSet<Edge>();
					edgesInPath.mark(i.getThat(), currentEdges);
				}
				if (edgesInPath.isMarked(i.getThis())) {
					currentEdges.addAll(edgesInPath.getMark(i.getThis()));
				}
				currentEdges.add(i.getEdge());

				markNestingGEC(nestedIncidentVertexClasses, vc, i, edgesInPath);
			}
			markAllNestingSuperClassesOfGraphElementClass(
					(GraphElementClass) subI.getThat(),
					nestedIncidentVertexClasses, vc, edgesInPath);
		}
	}

	private void markNestingGEC(
			LocalGenericGraphMarker<Set<GraphElementClass>> nestedIncidentVertexClasses,
			VertexClass vc, MayBeNestedIn_nestedElement i,
			LocalGenericGraphMarker<Set<Edge>> edgesInPath) {
		if (VertexClass.class.isInstance(i.getThat())) {
			markAllNestingGraphElementClasses((GraphElementClass) i.getThat(),
					vc, nestedIncidentVertexClasses, edgesInPath);
		} else {
			// EdgeClasses do not propagate the nesting information to
			// its nesting elements
			Set<GraphElementClass> incidentVCs = nestedIncidentVertexClasses
					.getMark(i.getThat());
			if (incidentVCs == null) {
				incidentVCs = new HashSet<GraphElementClass>();
				nestedIncidentVertexClasses.mark(i.getThat(), incidentVCs);
			}
			incidentVCs.add(vc);
		}
	}

	private void markAllNestingGraphElementClasses(
			GraphElementClass gec,
			VertexClass incidentVertexClass,
			LocalGenericGraphMarker<Set<GraphElementClass>> nestedIncidentVertexClasses,
			LocalGenericGraphMarker<Set<Edge>> edgesInPath) {

		// insert incidentVertexClass into mark of gec
		Set<GraphElementClass> incidentVCs = nestedIncidentVertexClasses
				.getMark(gec);
		if (incidentVCs == null) {
			incidentVCs = new HashSet<GraphElementClass>();
			nestedIncidentVertexClasses.mark(gec, incidentVCs);
		}
		incidentVCs.add(incidentVertexClass);

		// if incidentVertexClass could be nested in gec, it could be nested in
		// all subclasses of gec, too
		for (SpecializesTypedElementClass_superclass i : gec
				.getIncidences(SpecializesTypedElementClass_superclass.class)) {

			Set<Edge> currentEdges = edgesInPath.getMark(i.getThat());
			if (currentEdges == null) {
				currentEdges = new HashSet<Edge>();
				edgesInPath.mark(i.getThat(), currentEdges);
			}
			if (edgesInPath.isMarked(i.getThis())) {
				currentEdges.addAll(edgesInPath.getMark(i.getThis()));
			}
			currentEdges.add(i.getEdge());

			markAllNestingGraphElementClasses((GraphElementClass) i.getThat(),
					incidentVertexClass, nestedIncidentVertexClasses,
					edgesInPath);
		}

		// if incidentVertexClass could be nested in gec, it could be nested in
		// all nesting GraphElementClasses of gec, too
		for (MayBeNestedIn_nestedElement i : gec
				.getIncidences(MayBeNestedIn_nestedElement.class)) {

			Set<Edge> currentEdges = edgesInPath.getMark(i.getThat());
			if (currentEdges == null) {
				currentEdges = new HashSet<Edge>();
				edgesInPath.mark(i.getThat(), currentEdges);
			}
			if (edgesInPath.isMarked(i.getThis())) {
				currentEdges.addAll(edgesInPath.getMark(i.getThis()));
			}
			currentEdges.add(i.getEdge());

			if (VertexClass.class.isInstance(i.getThat())) {
				markAllNestingGraphElementClasses(
						(GraphElementClass) i.getThat(), incidentVertexClass,
						nestedIncidentVertexClasses, edgesInPath);
			} else {
				// EdgeClasses do not propagate the nesting information to its
				// nesting elements
				incidentVCs = nestedIncidentVertexClasses.getMark(i.getThat());
				if (incidentVCs == null) {
					incidentVCs = new HashSet<GraphElementClass>();
					nestedIncidentVertexClasses.mark(i.getThat(), incidentVCs);
				}
				incidentVCs.add(incidentVertexClass);
			}
		}
	}

	private void checkNestingConstraints(EdgeClass containedEC,
			GraphElementClass containingGEC) {
		Set<GraphElementClass> nestedGECs = collectNestedElements(containingGEC);
		if (EdgeClass.class.isInstance(containingGEC)) {
			// one incident VertexClass of containedEC must be nested in
			// containingGEC
			boolean isOneVCContained = false;
			for (IncidenceClass ic : getAllIncidenceClasses(containedEC)) {
				VertexClass incidentVC = getConnectedVertexClass(ic);
				if (nestedGECs.contains(incidentVC)) {
					isOneVCContained = true;
					break;
				}
			}
			if (!isOneVCContained) {
				throw new ProcessingException(
						getParser(),
						getFileName(),
						"The EdgeClass '"
								+ containingGEC.get_qualifiedName()
								+ "' does not contain any incident VertexClass of EdgeClass '"
								+ containedEC.get_qualifiedName()
								+ "'. That is why they cannot be nested.");
			}
		} else {
			// all incident VertexClass of containedEC must be nested in
			// containingGEC
			for (IncidenceClass ic : getAllIncidenceClasses(containedEC)) {
				VertexClass incidentVC = getConnectedVertexClass(ic);
				if (!nestedGECs.contains(incidentVC)) {
					throw new ProcessingException(
							getParser(),
							getFileName(),
							"The VertexClass '"
									+ containingGEC.get_qualifiedName()
									+ "' does not contain the incident VertexClass '"
									+ incidentVC.get_qualifiedName()
									+ "' of EdgeClass '"
									+ containedEC.get_qualifiedName()
									+ "'. That is why they cannot be nested.");
				}
			}
		}
	}

	/**
	 * DepthFirstSearch about the subgraph consisting of MayBeNestedIn and
	 * SpecializedTypedElementClass edges. <code>containingGEC</code> is added
	 * to the result.
	 * 
	 * @param containingGEC
	 */
	private Set<GraphElementClass> collectNestedElements(
			GraphElementClass containingGEC) {
		Set<GraphElementClass> nestedGECs = new HashSet<GraphElementClass>();

		collectNestedElements(containingGEC, nestedGECs);

		return nestedGECs;
	}

	/**
	 * DepthFirstSearch about the subgraph consisting of MayBeNestedIn and
	 * SpecializedTypedElementClass edges. <code>containingGEC</code> is added
	 * to <code>nestedGECs</code>.
	 * 
	 * @param containingGEC
	 * @param nestedGECs
	 */
	private void collectNestedElements(GraphElementClass containingGEC,
			Set<GraphElementClass> nestedGECs) {

		addAllSubClasses(nestedGECs, containingGEC);

		// add all nestedElements, which are inherited of the superclass
		for (SpecializesTypedElementClass_subclass i : containingGEC
				.getIncidences(SpecializesTypedElementClass_subclass.class)) {
			collectNestedElements((GraphElementClass) i.getThat(), nestedGECs);
		}

		// add all nestedElements
		for (MayBeNestedIn_nestingElement i : containingGEC
				.getIncidences(MayBeNestedIn_nestingElement.class)) {
			GraphElementClass nestedGEC = (GraphElementClass) i.getThat();
			if (!nestedGECs.contains(nestedGEC)) {
				if (VertexClass.class.isInstance(nestedGEC)) {
					collectNestedElements(nestedGEC, nestedGECs);
				} else {
					// if the nested element is an EdgeClass, its nested classes
					// are not part of nestedGECs
					addAllSubClasses(nestedGECs, nestedGEC);
				}
			}
		}

	}

	private void addAllSubClasses(Set<GraphElementClass> nestedGECs,
			GraphElementClass containingGEC) {
		nestedGECs.add(containingGEC);
		for (SpecializesTypedElementClass_superclass i : containingGEC
				.getIncidences(SpecializesTypedElementClass_superclass.class)) {
			addAllSubClasses(nestedGECs, (GraphElementClass) i.getThat());
		}
	}

	private void checkAcyclicityOfMayBeNestedIn(
			Queue<GraphElementClass> topLevelNestingElements) {
		LocalIntegerVertexMarker number = new LocalIntegerVertexMarker(sg);
		LocalIntegerVertexMarker rnumber = new LocalIntegerVertexMarker(sg);
		int num = 0;
		int rnum = 0;

		// depth first search
		Stack<GraphElementClass> stack = new Stack<GraphElementClass>();
		for (GraphElementClass root : topLevelNestingElements) {
			stack.push(root);
			while (!stack.isEmpty()) {
				GraphElementClass current = stack.pop();
				number.mark(current, ++num);
				for (MayBeNestedIn_nestingElement i : current
						.getIncidences(MayBeNestedIn_nestingElement.class)) {
					GraphElementClass child = (GraphElementClass) i.getThat();
					if (!number.isMarked(child)) {
						stack.push(child);
					} else {
						if (!rnumber.isMarked(child)) {
							// there exists a backward arc
							throw new ProcessingException(getParser(),
									getFileName(),
									"The nesting hierarchy is not acyclic.");
						}
					}
				}
				rnumber.mark(current, ++rnum);
			}
		}
	}

	/**
	 * @param containingGEC
	 * @param containedGEC
	 * @param workingList
	 *            {@link Queue} which contains all elements which are nesting
	 *            some element but are not nested in any element
	 */
	private void insertContainingGECIntoWorkingList(
			GraphElementClass containingGEC, GraphElementClass containedGEC,
			Queue<GraphElementClass> workingList) {
		if (workingList.contains(containedGEC)) {
			workingList.remove(containedGEC);
		}
		if (!workingList.contains(containingGEC)) {
			workingList.add(containingGEC);
		}
	}

	/**
	 * Converts all EdgeClasses which have exactly 2 connected IncidenceClasses
	 * as well as all super and sub classes into BinaryEdgeClasses.
	 */
	private void convertEdgeClassesToBinaryEdgeClasses() {
		System.out
				.println("Converting EdgeClasses to BinaryEdgeClasses if possible...");
		for (EdgeClass ec : getEdgeClassesInTopologicalOrder()) {
			if (isValidBinaryEdgeClassCandidate(ec)) {
				convertToBinaryEdgeClass(ec);
			} else {
				// every IncidenceClass must have the inicdenceType EDGE
				checkIncidenceTypes(ec);
			}
		}

	}

	private List<EdgeClass> getEdgeClassesInTopologicalOrder() {
		List<EdgeClass> result = new ArrayList<EdgeClass>();
		Map<EdgeClass, Integer> numberOfPredecessors = new HashMap<EdgeClass, Integer>();
		LinkedList<EdgeClass> zeroValued = new LinkedList<EdgeClass>();

		for (EdgeClass ec : sg.getEdgeClassVertices()) {
			if (!BinaryEdgeClass.class.isInstance(ec)) {
				int numberOfPred = ec
						.getDegree(SpecializesTypedElementClass_subclass.class);
				if (numberOfPred == 0) {
					zeroValued.add(ec);
				} else {
					numberOfPredecessors.put(ec, numberOfPred);
				}
			}
		}

		while (!zeroValued.isEmpty()) {
			EdgeClass current = zeroValued.removeFirst();
			result.add(current);
			for (SpecializesEdgeClass sec : current.getIncidentEdges(
					SpecializesEdgeClass.class,
					de.uni_koblenz.jgralab.Direction.EDGE_TO_VERTEX)) {
				EdgeClass otherEnd = (EdgeClass) sec.getAlpha();
				Integer numberOfPred = numberOfPredecessors.get(otherEnd);
				if (numberOfPred != null) {
					if (numberOfPred == 1) {
						numberOfPredecessors.remove(otherEnd);
						zeroValued.add(otherEnd);
					} else {
						numberOfPredecessors.put(otherEnd, --numberOfPred);
					}
				}
			}
		}

		if (numberOfPredecessors.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	private void checkIncidenceTypes(EdgeClass ec) {
		for (ConnectsToEdgeClass_connectedEdgeClass inc : ec
				.getIncidences(ConnectsToEdgeClass_connectedEdgeClass.class)) {
			IncidenceClass incidenceClass = (IncidenceClass) inc.getThat();
			if (incidenceClass.get_incidenceType() != IncidenceType.EDGE) {
				throw new ProcessingException(getParser(), getFileName(),
						"The IncidenceClass '" + incidenceClass.get_roleName()
								+ "' of HyperEdgeClass '"
								+ ec.get_qualifiedName()
								+ "' must not have the IncidenceType '"
								+ incidenceClass.get_incidenceType() + "'.");
			}
		}
	}

	private boolean isValidBinaryEdgeClassCandidate(EdgeClass ec) {
		LocalBooleanGraphMarker alreadySeenMarker = new LocalBooleanGraphMarker(
				sg);
		Stack<EdgeClass> workingList = new Stack<EdgeClass>();
		workingList.add(ec);
		alreadySeenMarker.mark(ec);
		while (!workingList.isEmpty()) {
			EdgeClass current = workingList.pop();
			alreadySeenMarker.mark(current);
			if (BinaryEdgeClass.class.isInstance(current)) {
				continue;
			}
			// a BinaryEdgeClass must have exactly two IncidenceClasses
			Set<IncidenceClass> allIncidenceClasses = getAllIncidenceClasses(current);
			if (allIncidenceClasses.size() != 2) {
				return false;
			}
			// both incidences have to have different directions and both
			// incidences are not abstract and the multiplicities must fit and
			// only one of both may have a IncidenceType different from EDGE
			IncidenceClass firstIC = null, lastIC = null;
			for (IncidenceClass ic : allIncidenceClasses) {
				if (firstIC == null) {
					firstIC = ic;
				} else {
					lastIC = ic;
				}
			}
			if (firstIC.get_direction() == lastIC.get_direction()
					&& (firstIC.is_abstract() || lastIC.is_abstract())
					&& (firstIC.get_minVerticesAtEdge() == 1
							&& firstIC.get_maxVerticesAtEdge() == 1
							&& lastIC.get_minVerticesAtEdge() == 1 && lastIC
							.get_maxVerticesAtEdge() == 1)) {
				return false;
			}
			if ((firstIC.get_incidenceType() != IncidenceType.EDGE && (lastIC
					.get_incidenceType() != IncidenceType.EDGE))
					|| (isNestingIncidenceOrEdgeClass.isMarked(firstIC) || isNestingIncidenceOrEdgeClass
							.isMarked(lastIC))) {
				return false;
			}

			// every subclass and superclass of a BinaryEdgeClass must be a
			// BinaryEdgeClass candidate, too
			for (SpecializesEdgeClass sec : current
					.getIncidentEdges(SpecializesEdgeClass.class)) {
				EdgeClass genEC = (EdgeClass) (sec.getAlpha() == current ? sec
						.getOmega() : sec.getAlpha());
				if (alreadySeenMarker.isMarked(genEC)) {
					continue;
				} else if (!workingList.contains(genEC)
						&& !BinaryEdgeClass.class.isInstance(genEC)) {
					workingList.push(genEC);
				}
			}
		}
		return true;
	}

	/**
	 * Finds all defined and inherited {@link IncidenceClass}es. Subsetted
	 * {@link IncidenceClass}es are not collected.
	 * 
	 * @param current
	 * @return
	 */
	private Set<IncidenceClass> getAllIncidenceClasses(EdgeClass current) {
		Set<IncidenceClass> subsettingIncidenceClasses = new HashSet<IncidenceClass>();
		for (ConnectsToEdgeClass_connectedEdgeClass inc : current
				.getIncidences(ConnectsToEdgeClass_connectedEdgeClass.class)) {
			IncidenceClass ic = (IncidenceClass) inc.getThat();
			subsettingIncidenceClasses.add(ic);
		}
		for (Incidence inc : current
				.getIncidences(SpecializesTypedElementClass_subclass.class)) {
			collectInheritedIncidenceClasses((EdgeClass) inc.getThat(),
					subsettingIncidenceClasses);
		}
		return subsettingIncidenceClasses;
	}

	private void collectInheritedIncidenceClasses(EdgeClass current,
			Set<IncidenceClass> subsettingIncidenceClasses) {
		for (ConnectsToEdgeClass_connectedEdgeClass inc : current
				.getIncidences(ConnectsToEdgeClass_connectedEdgeClass.class)) {
			IncidenceClass ic = (IncidenceClass) inc.getThat();
			removeSuperIncidenceClasses(ic, subsettingIncidenceClasses);
			if (!isSubIncidenceClassContained(ic, subsettingIncidenceClasses)) {
				subsettingIncidenceClasses.add(ic);
			}
		}
		for (Incidence inc : current
				.getIncidences(SpecializesTypedElementClass_subclass.class)) {
			collectInheritedIncidenceClasses((EdgeClass) inc.getThat(),
					subsettingIncidenceClasses);
		}
	}

	/**
	 * Removes <code>ic</code> and its superclasses from
	 * <code>subsettingIncidenceClasses</code>.
	 * 
	 * @param ic
	 * @param subsettingIncidenceClasses
	 */
	private void removeSuperIncidenceClasses(IncidenceClass ic,
			Set<IncidenceClass> subsettingIncidenceClasses) {
		subsettingIncidenceClasses.remove(ic);
		for (Incidence inc : ic
				.getIncidences(SpecializesIncidenceClass_specializesIncidenceClass_ComesFrom_IncidenceClass.class)) {
			removeSuperIncidenceClasses((IncidenceClass) inc.getThat(),
					subsettingIncidenceClasses);
		}
	}

	private boolean isSubIncidenceClassContained(IncidenceClass ic,
			Set<IncidenceClass> subsettingIncidenceClasses) {
		if (subsettingIncidenceClasses.contains(ic)) {
			return true;
		}
		for (Incidence inc : ic
				.getIncidences(SpecializesIncidenceClass_specializesIncidenceClass_GoesTo_IncidenceClass.class)) {
			if (isSubIncidenceClassContained((IncidenceClass) inc.getThat(),
					subsettingIncidenceClasses)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The incidenceType information are corrected again e.g. the incidence type
	 * of both IncidenceClasses are interchanged if they were modelled as
	 * UML:classes with stereotype &lt;edge&gt;.
	 * 
	 * @see #convertToEdgeClasses()
	 * @param ec
	 */
	private void convertToBinaryEdgeClass(EdgeClass ec) {
		BinaryEdgeClass bec = sg.createBinaryEdgeClass();
		bec.set_abstract(ec.is_abstract());
		bec.set_maxKappa(ec.get_maxKappa());
		bec.set_minKappa(ec.get_minKappa());
		bec.set_qualifiedName(ec.get_qualifiedName());

		IncidenceClass first = null, last = null;

		Incidence i = ec.getFirstIncidence();
		while (i != null) {
			if (ConnectsToEdgeClass_connectedEdgeClass.class.isInstance(i)) {
				if (first == null) {
					first = (IncidenceClass) i.getThat();
				} else {
					last = (IncidenceClass) i.getThat();
				}
			}
			Incidence current = i;
			i = i.getNextIncidenceAtVertex();
			setIncidentVertex(current, bec);
		}

		if (first != null && last != null) {
			// this BinaryEdge has two defined IncidenceClasses
			if (edgeStereotypedEdgeClasses.contains(ec)) {
				// set the correct IncidenceTypes again
				IncidenceType incType = first.get_incidenceType();
				first.set_incidenceType(last.get_incidenceType());
				last.set_incidenceType(incType);
			}
		} else if (first != null || last != null) {
			// if one of both IncidenceClasses is inherited, it has to be
			// checked, that their incidence Type is equal because the
			// IncidenceType of the current BinaryEdgeClass must still be
			// changed (have a look at the JavaDoc of this method)
			IncidenceClass definedIC = first != null ? first : last;
			IncidenceClass inheritedIC = null;
			for (IncidenceClass ic : getAllIncidenceClasses(bec)) {
				if (ic != definedIC) {
					assert inheritedIC == null;
					inheritedIC = ic;
					break;
				}
			}
			assert inheritedIC != null;

			// At this point the EdgeClasses must be handled in topological
			// order because the incidence type of all superclasses is set
			// correctly
			if (inheritedIC.get_incidenceType() == definedIC
					.get_incidenceType()) {
				// change the incidence type of the current BinaryEdgeClass
				definedIC.set_incidenceType(IncidenceType.EDGE);
			} else {
				throw new ProcessingException(
						getParser(),
						getFileName(),
						"At the BinaryEdgeClass '"
								+ bec.get_qualifiedName()
								+ "the IncidenceType of the inherited IncidenceClass '"
								+ inheritedIC.get_roleName()
								+ "' is set to "
								+ definedIC.get_incidenceType()
								+ ". But at the BinaryEdgeClass '"
								+ getConnectedEdgeClass(inheritedIC)
										.get_qualifiedName()
								+ "' where the IncidenceClass is defined, its IncidenceType was set to "
								+ inheritedIC.get_incidenceType() + ".");
			}
		}

		String id = getXMIId(ec);
		if (id != null) {
			idMap.put(id, bec);
		}

		ec.delete();
		isBinaryEdgeAlreadyConverted.mark(bec);
	}

	/**
	 * After converting<br>
	 * VC1--EC1-ic1->VC_EC-ic2-EC2-->VC2<br>
	 * into<br>
	 * VC1-new_ic1-newEC-new_ic2->VC2<br>
	 * the incidenceTypes should have the values<br>
	 * new_ic2.set_incidenceType(ic1.get_incidenceType())<br>
	 * new_ic1.set_incidenceType(ic2.get_incidenceType())<br>
	 * but they have the form<br>
	 * new_ic2.set_incidenceType(ic2.get_incidenceType())<br>
	 * new_ic1.set_incidenceType(ic1.get_incidenceType())<br>
	 * In case of a HyperEdge all IncidenceTypes must be
	 * {@link IncidenceType#EDGE}. In case of a BinaryEdge the IncidentTypes are
	 * corrected in {@link #convertToBinaryEdgeClass(EdgeClass)}.
	 */
	private void convertToEdgeClasses() {
		System.out
				.println("Converting VertexClasses with stereotype <<edge>> to EdgeClasses...");
		for (VertexClass oldVertexClass : edgeStereotypedVertexClasses) {
			EdgeClass ec = sg.createEdgeClass();
			edgeStereotypedEdgeClasses.add(ec);
			ec.set_qualifiedName(oldVertexClass.get_qualifiedName());
			ec.set_abstract(oldVertexClass.is_abstract());
			ec.set_maxKappa(oldVertexClass.get_maxKappa());
			ec.set_minKappa(oldVertexClass.get_minKappa());

			Incidence i = oldVertexClass.getFirstIncidence();
			while (i != null) {
				Incidence n = i.getNextIncidenceAtVertex();
				if (i.getEdge() instanceof ConnectsToVertexClass) {
					Edge e = i.getEdge();
					IncidenceClass incidenceClass = (IncidenceClass) (e
							.getFirstIncidence() == i ? e.getLastIncidence()
							.getVertex() : e.getFirstIncidence().getVertex());
					EdgeClass oldEdgeClass = (EdgeClass) ((BinaryEdge) incidenceClass
							.getFirstIncidence(
									ConnectsToEdgeClass_connectsToEdgeClass_ComesFrom_IncidenceClass.class)
							.getEdge()).getOmega();
					if (!edgeStereotypedEdgeClasses.contains(oldEdgeClass)) {
						// this is not a composition edge between two
						// EdgeClasses. This could happen if the "oldEdgeClass"
						// was already transformed into a EdgeClas
						convertToIncidenceClass(oldEdgeClass, i.getDirection(),
								oldVertexClass, ec);
					}
				} else {
					setIncidentVertex(i, ec);
				}
				i = n;
			}

			if (generalizations.isMarked(oldVertexClass)) {
				generalizations.mark(ec,
						generalizations.getMark(oldVertexClass));
				generalizations.removeMark(oldVertexClass);
			}

			// update the information of nestedElements
			for (GraphElement<?, ?, ?> elem : nestedElements
					.getMarkedElements()) {
				GraphElementClass gec = (GraphElementClass) elem;
				Set<GraphElementClass> containedElements = nestedElements
						.getMark(gec);
				assert containedElements != null;
				if (containedElements.contains(oldVertexClass)) {
					containedElements.remove(oldVertexClass);
					containedElements.add(ec);
				}
			}
			if (nestedElements.isMarked(oldVertexClass)) {
				nestedElements.mark(ec, nestedElements.getMark(oldVertexClass));
				nestedElements.removeMark(oldVertexClass);
			}

			String id = getXMIId(oldVertexClass);
			if (id != null) {
				idMap.put(id, ec);
			}
			oldVertexClass.delete();
		}
	}

	private String getXMIId(Vertex v) {
		for (Entry<String, Vertex> entry : idMap.entrySet()) {
			if (entry.getValue().equals(v)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private void convertToIncidenceClass(EdgeClass oldEdgeClass,
			de.uni_koblenz.jgralab.Direction direction,
			AttributedElementClass oldVertexClass, EdgeClass newEdgeClass) {
		assert oldEdgeClass
				.getDegree(ConnectsToEdgeClass_connectedEdgeClassImpl.class) <= 2;
		IncidenceClass from = null;
		IncidenceClass to = null;
		Edge containsGraphElementClass = null;
		for (Edge edge : oldEdgeClass.getIncidentEdges()) {
			if (ConnectsToEdgeClass.class.isInstance(edge)) {
				IncidenceClass ic = (IncidenceClass) ((ConnectsToEdgeClass) edge)
						.getAlpha();
				if (ic.get_direction() == Direction.VERTEX_TO_EDGE) {
					from = ic;
				} else {
					to = ic;
				}
			} else if (ContainsGraphElementClass.class.isInstance(edge)) {
				containsGraphElementClass = edge;
			} else {
				throw new ProcessingException(getParser(), getFileName(),
						"The UML association '"
								+ oldEdgeClass.get_qualifiedName()
								+ "' must not have an incident edge of type '"
								+ edge.getType().getQualifiedName() + "'.");
			}
		}
		containsGraphElementClass.delete();
		assert from != null && to != null : currentClassId;

		Direction directionOfNewIncidence = extractDirection(from,
				oldVertexClass);
		IncidenceClass atVertex = directionOfNewIncidence == Direction.VERTEX_TO_EDGE ? from
				: to;
		IncidenceClass atEdge = atVertex == from ? to : from;

		// create a new IncidenceClass and set its attributes
		IncidenceClass newIncidenceClass = sg.createIncidenceClass();
		newIncidenceClass.set_abstract(oldEdgeClass.is_abstract());
		newIncidenceClass.set_direction(directionOfNewIncidence);
		// store the IncidenceType at the wrong IncidenceClass
		// @see convertToEdgeClasses()
		newIncidenceClass.set_incidenceType(atEdge.get_incidenceType());
		newIncidenceClass.set_maxEdgesAtVertex(atVertex.get_maxEdgesAtVertex());
		newIncidenceClass.set_maxVerticesAtEdge(atEdge.get_maxEdgesAtVertex());
		newIncidenceClass.set_minEdgesAtVertex(atVertex.get_minEdgesAtVertex());
		newIncidenceClass.set_minVerticesAtEdge(atEdge.get_minEdgesAtVertex());
		String roleName = extractSimpleName(oldEdgeClass.get_qualifiedName());
		newIncidenceClass
				.set_roleName(Character.toLowerCase(roleName.charAt(0))
						+ (roleName.length() > 1 ? roleName.substring(1) : ""));

		if (isNestingIncidenceOrEdgeClass.isMarked(oldEdgeClass)) {
			isNestingIncidenceOrEdgeClass.mark(newIncidenceClass);
			isNestingIncidenceOrEdgeClass.removeMark(oldEdgeClass);
		} else if (atVertex.get_incidenceType() != IncidenceType.EDGE) {
			throw new ProcessingException(
					getFileName(),
					"The IncidenceType "
							+ atVertex.get_incidenceType()
							+ " of the modelled association '"
							+ newIncidenceClass.get_roleName()
							+ "' must be defined at the other incidence."
							+ (atVertex.get_incidenceType() == IncidenceType.COMPOSITION ? " If you wandet to medel a nesting relation with this composition, you have to define the stereotype <<nested>>."
									: ""));
		}

		// set specializations
		if (generalizations.isMarked(oldEdgeClass)) {
			generalizations.mark(newIncidenceClass,
					generalizations.getMark(oldEdgeClass));
			generalizations.removeMark(oldEdgeClass);
		}

		// set redefines
		if (redefinesAtVertex.isMarked(from)) {
			redefinesAtVertex.mark(newIncidenceClass,
					redefinesAtVertex.getMark(from));
			redefinesAtVertex.removeMark(from);
		}
		if (redefinesAtVertex.isMarked(to)) {
			redefinesAtEdge.mark(newIncidenceClass,
					redefinesAtVertex.getMark(to));
			redefinesAtVertex.removeMark(to);
		}

		// set subsets
		if (subsets.isMarked(from)) {
			subsets.mark(newIncidenceClass, subsets.getMark(from));
			subsets.removeMark(from);
		}
		if (subsets.isMarked(to)) {
			subsets.mark(newIncidenceClass, subsets.getMark(to));
			subsets.removeMark(to);
		}

		// connect IncidenceClass with new EdgeClass and original VertexClass
		sg.createConnectsToVertexClass(
				newIncidenceClass,
				(VertexClass) ((BinaryEdge) atVertex
						.getFirstIncidence(
								ConnectsToVertexClass_connectsToVertexClass_ComesFrom_IncidenceClass.class)
						.getEdge()).getOmega());
		sg.createConnectsToEdgeClass(newIncidenceClass, newEdgeClass);

		// find xmi ids of the old classes
		String xmiIdOldEdgeClass = null;
		String xmiIdOldVertexClass = null;
		String xmiIdOldAtVertexIncidenceClass = null;
		String xmiIdOldAtEdgeIncidenceClass = null;
		for (Entry<String, Vertex> entry : idMap.entrySet()) {
			if (entry.getValue() == oldEdgeClass) {
				xmiIdOldEdgeClass = entry.getKey();
			} else if (entry.getValue() == oldVertexClass) {
				xmiIdOldVertexClass = entry.getKey();
			} else if (entry.getValue() == atVertex) {
				xmiIdOldAtVertexIncidenceClass = entry.getKey();
			} else if (entry.getValue() == atEdge) {
				xmiIdOldAtEdgeIncidenceClass = entry.getKey();
			}
		}

		idMap.put(xmiIdOldVertexClass, newEdgeClass);
		idMap.put(xmiIdOldEdgeClass, newIncidenceClass);
		idMap.put(xmiIdOldAtEdgeIncidenceClass, newIncidenceClass);
		idMap.put(xmiIdOldAtVertexIncidenceClass, newIncidenceClass);

		// store the id of the old IncidenceClass which is connected to the
		// replaces old VertexClass, which is now an EdgeClass
		// @see #idsOfOldIncidenceclassAtNewEdgeClass
		idsOfOldIncidenceclassAtNewEdgeClass.add(xmiIdOldAtEdgeIncidenceClass);

		// delete old EdgeClass
		oldEdgeClass.delete();
		atVertex.delete();
		atEdge.delete();
	}

	private String extractSimpleName(String qualifiedName) {
		return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
	}

	private Direction extractDirection(IncidenceClass from,
			AttributedElementClass oldVertexClass) {
		for (ConnectsToVertexClass ctvc : from
				.getIncidentEdges(ConnectsToVertexClass.class)) {
			if (ctvc.getAlpha() == oldVertexClass
					|| ctvc.getOmega() == oldVertexClass) {
				// VertexClasss <--oldEdgeClass-from- oldVertexClass
				// will result in
				// VertexClass <--newEdgeClass
				return Direction.EDGE_TO_VERTEX;
			}
		}
		// VertexClasss -from-oldEdgeClass--> oldVertexClass
		// will result in
		// VertexClass -->newEdgeClass
		return Direction.VERTEX_TO_EDGE;
	}

	/**
	 * Removes all GraphElementClasses in ignored Packages from the schema
	 * graph.
	 */
	private void removeIgnoredPackages() {
		System.out.println("Removing ignored packages...");
		for (Package pkg : ignoredPackages) {
			removePackage(pkg);
		}
	}

	/**
	 * Removes the GraphElementClasses in the Package <code>pkg</code> from the
	 * schema graph, including subpackages.
	 * 
	 * @param pkg
	 *            a Package
	 */
	private void removePackage(Package pkg) {
		if (!pkg.isValid()) {
			// possibly already deleted
			return;
		}
		System.out.println("\tremoving " + pkg.get_qualifiedName());
		// recursively descend into subpackages
		List<Package> subPackages = new ArrayList<Package>();
		for (ContainsSubPackage csp : pkg
				.getIncidentEdges(ContainsSubPackage.class)) {
			subPackages.add((Package) csp.getOmega());
		}
		for (Package sub : subPackages) {
			removePackage(sub);
		}

		// remove all GraphElementClasses
		for (Incidence i = pkg
				.getFirstIncidence(ContainsGraphElementClass_containsGraphElementClass_ComesFrom_Package.class); i != null; i = i
				.getNextIncidenceAtVertex(ContainsGraphElementClass_containsGraphElementClass_ComesFrom_Package.class)) {
			ContainsGraphElementClass c = (ContainsGraphElementClass) i
					.getEdge();
			GraphElementClass gec = (GraphElementClass) c.getOmega();

			if (gec instanceof EdgeClass) {
				// in case of an EdgeClass, also remove IncidenceClasses
				EdgeClass ec = (EdgeClass) gec;
				deleteIncidentIncidenceClasses(ec);
			} else if (gec instanceof VertexClass) {
				// in case of an VertexClass, also remove incident EdgeClasses
				VertexClass vc = (VertexClass) gec;
				for (Incidence inc = vc
						.getFirstIncidence(ConnectsToVertexClass_connectedVertexClass.class); inc != null; inc = inc
						.getNextIncidenceAtVertex(ConnectsToVertexClass_connectedVertexClass.class)) {
					ConnectsToVertexClass ctec = (ConnectsToVertexClass) inc
							.getEdge();
					IncidenceClass incClassAtEdgeClass = (IncidenceClass) ctec
							.getAlpha();
					EdgeClass ec = (EdgeClass) ((ConnectsToEdgeClass) incClassAtEdgeClass
							.getFirstIncidence(
									ConnectsToEdgeClass_connectsToEdgeClass_ComesFrom_IncidenceClass.class)
							.getEdge()).getOmega();
					deleteIncidentIncidenceClasses(ec);
					// remove Attributes of EdgeClass
					removeAttributes(ec);
					ec.delete();
				}
			}
			// remove Attributes of GraphElementClass
			removeAttributes(gec);
			gec.delete();
		}
		// remove the package itself if it's totally empty (degree is 1 since
		// the ContainsSubpackage edge to the parent package still exists)
		if (pkg.getDegree() == 1) {
			pkg.delete();
		}
	}

	private void deleteIncidentIncidenceClasses(EdgeClass ec) {
		for (Incidence inc = ec
				.getFirstIncidence(ConnectsToEdgeClass_connectedEdgeClass.class); inc != null; inc = inc
				.getNextIncidenceAtVertex(ConnectsToEdgeClass_connectedEdgeClass.class)) {
			ConnectsToEdgeClass ctec = (ConnectsToEdgeClass) inc.getEdge();
			ctec.getAlpha().delete();
			ctec.delete();
		}
	}

	/**
	 * Removes all Attribute vertices of AttributedElementClass <code>aec</code>
	 * from the schema graph
	 * 
	 * @param aec
	 */
	private void removeAttributes(AttributedElementClass aec) {
		for (Incidence inc = aec
				.getFirstIncidence(HasAttribute_hasAttribute_ComesFrom_AttributedElementClass.class); inc != null; inc = inc
				.getNextIncidenceAtVertex(HasAttribute_hasAttribute_ComesFrom_AttributedElementClass.class)) {
			HasAttribute ha = (HasAttribute) inc.getEdge();
			ha.getOmega().delete();
		}
	}

	/**
	 * Checks whether all Enumeration domains contain at least one literal.
	 * 
	 * @throws ProcessingException
	 *             if any enumeration is empty
	 */
	private void checkEnumDomains() {
		System.out.println("Checking enumeration domains...");
		ArrayList<String> faultyDomains = new ArrayList<String>();
		for (EnumDomain ed : sg.getEnumDomainVertices()) {
			if (ed.get_enumConstants().size() < 1) {
				faultyDomains.add(ed.get_qualifiedName());
			}
		}
		if (faultyDomains.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("The following enumeration domain")
					.append(faultyDomains.size() == 1 ? " has" : "s have")
					.append(" no literals");
			String delim = ": ";
			for (String name : faultyDomains) {
				sb.append(delim).append(name);
				delim = ", ";
			}
			throw new ProcessingException(getFileName(), sb.toString());
		}
	}

	private void attachComments() {
		System.out.println("Attaching comments to annotated elements...");
		for (String id : comments.keySet()) {
			NamedElementClass annotatedElement = null;
			if (domainMap.containsKey(id)) {
				annotatedElement = domainMap.get(id);
			} else if (idMap.containsKey(id)) {
				Vertex v = idMap.get(id);
				annotatedElement = (NamedElementClass) v;
			}
			if (annotatedElement == null) {
				System.out
						.println("Warning: Couldn't find annotated element for XMI id "
								+ id
								+ " ==> attaching to GraphClass (Comment starts with '"
								+ comments.get(id).get(0) + "'");
				annotatedElement = graphClass;
			}
			assert annotatedElement != null;
			if (annotatedElement.isValid()) {
				List<String> lines = comments.get(id);
				for (String line : lines) {
					Comment c = sg.createComment();
					c.set_text(line);
					sg.createAnnotates(c, annotatedElement);
				}
			}
		}
	}

	private void createSubsetsAndRedefinesRelations() {
		System.out.println("Creating subsets and redefines relationships...");
		for (SpecializesTypedElementClass spec : getSpecializesTypedElementClassInTopologicalOrder(1)) {
			EdgeClass subClass = (EdgeClass) spec.getAlpha();
			EdgeClass superClass = (EdgeClass) spec.getOmega();

			for (ConnectsToEdgeClass ctec : subClass
					.getIncidentEdges(ConnectsToEdgeClass.class)) {

				subsettibilityErrors = new HashSet<ProcessingException>();
				IncidenceClass subIC = (IncidenceClass) ctec.getAlpha();
				List<IncidenceClass> possibleSubsettedICs = findPossibleSubsettedIncidenceClasses(
						superClass, subIC);
				Set<String> subsettedRoleNames = getExplicitlySubsettedAndRedefinedRolenames(subIC);

				if (possibleSubsettedICs.isEmpty()) {
					// there does not exist an IncidenceClass which can be
					// subsetted
					StringBuilder sb = new StringBuilder();
					for (ProcessingException pe : subsettibilityErrors) {
						sb.append(pe.getMessage() + "\n");
					}
					throw new ProcessingException(
							getFileName(),
							"IncidenceClass '"
									+ subIC.get_roleName()
									+ "' of EdgeClass '"
									+ subClass.get_qualifiedName()
									+ "' has no subsetted IncidenceClass at EdgeClass '"
									+ superClass.get_qualifiedName()
									+ "'. Causes are:\n" + sb.toString());
				} else if (subsettedRoleNames.isEmpty()
						&& possibleSubsettedICs.size() == 1) {
					// there exists only one subsettable IncidenceClass
					// this case is the implicit subsetting
					createSpecializesIncidenceClassForIncidences(subIC,
							possibleSubsettedICs.get(0));
				} else if (subsettedRoleNames.isEmpty()
						&& possibleSubsettedICs.size() > 1) {
					// there should be an implicit subsetting but several
					// IncidenceClasses could be subsetted
					StringBuilder sb = new StringBuilder();
					for (IncidenceClass supClass : possibleSubsettedICs) {
						sb.append("\n\t'"
								+ supClass.get_roleName()
								+ "' of EdgeClass '"
								+ ((NamedElementClass) supClass
										.getFirstIncidence(
												ConnectsToEdgeClass_connectsToEdgeClass_ComesFrom_IncidenceClass.class)
										.getThat()).get_qualifiedName() + "'");
					}
					throw new ProcessingException(
							getFileName(),
							"\nIncidenceClass '"
									+ subIC.get_roleName()
									+ "' of EdgeClass '"
									+ subClass.get_qualifiedName()
									+ "' has "
									+ possibleSubsettedICs.size()
									+ " IncidenceClasses which can be subsetted:"
									+ sb.toString());
				} else {
					for (String rolename : subsettedRoleNames) {
						IncidenceClass superIC = null;
						for (IncidenceClass ic : possibleSubsettedICs) {
							if (ic.get_roleName().equals(rolename)) {
								superIC = ic;
							} else {
								superIC = findClosestSuperclassWithRolename(ic,
										rolename);
							}
							if (superIC != null) {
								break;
							}
						}
						if (superIC == null) {
							throw new ProcessingException(getFileName(),
									"IncidenceClass '" + subIC.get_roleName()
											+ "' of EdgeClass '"
											+ subClass.get_qualifiedName()
											+ "' subsets unknown role names: '"
											+ rolename + "'.");
						}
						createSpecializesIncidenceClassForIncidences(subIC,
								superIC);
						// set redefinitions
						if (redefinesAtEdge.isMarked(subIC)
								&& redefinesAtEdge.getMark(subIC).contains(
										rolename)) {
							sg.createHidesIncidenceClassAtEdgeClass(subIC,
									superIC);
						}
						if (redefinesAtVertex.isMarked(subIC)
								&& redefinesAtVertex.getMark(subIC).contains(
										rolename)) {
							sg.createHidesIncidenceClassAtVertexClass(subIC,
									superIC);
						}
					}
				}
			}
		}
	}

	/**
	 * @param type
	 *            <ui> <li><code>type==1</code> means SpecializesEdgeClass</li>
	 *            <li><code>type==2</code> means SpecializesVertexClass</li> <li>
	 *            <code>type==3</code> means SpecializesIncidenceClass</li>
	 *            </ui>
	 * @return <code>null</code> if the specialization hierarchy is not acyclic
	 */
	private List<SpecializesTypedElementClass> getSpecializesTypedElementClassInTopologicalOrder(
			int type) {
		List<SpecializesTypedElementClass> resultList = new ArrayList<SpecializesTypedElementClass>();
		Map<SpecializesTypedElementClass, Integer> map = new HashMap<SpecializesTypedElementClass, Integer>();
		List<SpecializesTypedElementClass> zeroValued = new LinkedList<SpecializesTypedElementClass>();

		// initialize working list
		for (SpecializesTypedElementClass sec : type == 1 ? sg
				.getSpecializesEdgeClassEdges() : type == 2 ? sg
				.getSpecializesVertexClassEdges() : sg
				.getSpecializesIncidenceClassEdges()) {
			int numberOfPredecessor = sec.getOmega().getDegree(
					SpecializesTypedElementClass_subclass.class);
			if (numberOfPredecessor == 0) {
				// find sec without predecessors
				zeroValued.add(sec);
			} else {
				map.put(sec, numberOfPredecessor);
			}
		}

		// handle zero valued sec
		while (!zeroValued.isEmpty()) {
			SpecializesTypedElementClass sec = zeroValued.get(0);
			zeroValued.remove(0);
			resultList.add(sec);
			// decrement number of predecessors for all SpecialicesEdgeClasses
			// which have sec.getAlpha as omega vertex
			for (SpecializesTypedElementClass sec2 : sec.getAlpha()
					.getIncidentEdges(SpecializesTypedElementClass.class,
							de.uni_koblenz.jgralab.Direction.EDGE_TO_VERTEX)) {
				Integer value = map.get(sec2);
				if (value != null) {
					if (value == 1) {
						zeroValued.add(sec2);
						map.remove(sec2);
					} else {
						map.put(sec2, value--);
					}
				}
			}
		}

		if (!map.isEmpty()) {
			return null;
		} else {
			return resultList;
		}
	}

	private Set<String> getExplicitlySubsettedAndRedefinedRolenames(
			IncidenceClass subIC) {
		// If a rolename is redefined there has to be a
		// SpecializedIncidenceClass too
		Set<String> roleNames = new HashSet<String>();

		Set<String> setOfRoleNames = subsets.getMark(subIC);
		if (setOfRoleNames != null) {
			roleNames.addAll(setOfRoleNames);
		}

		setOfRoleNames = redefinesAtEdge.getMark(subIC);
		if (setOfRoleNames != null) {
			roleNames.addAll(setOfRoleNames);
		}

		setOfRoleNames = redefinesAtVertex.getMark(subIC);
		if (setOfRoleNames != null) {
			roleNames.addAll(setOfRoleNames);
		}

		return roleNames;
	}

	/**
	 * Finds possible IncidenceClasses of <code>ec</code> or its superclasses
	 * which can be subsetted by <code>currentIc</code>. If there is an
	 * generalization of several possible subsettable IncidenceClasses than the
	 * most specific one is returned.
	 * 
	 * @param ec
	 * @param currentIc
	 * @return
	 */
	private List<IncidenceClass> findPossibleSubsettedIncidenceClasses(
			EdgeClass ec, IncidenceClass currentIc) {
		return findPossibleSubsettedIncidenceClasses(ec, currentIc,
				new ArrayList<IncidenceClass>());
	}

	private List<IncidenceClass> findPossibleSubsettedIncidenceClasses(
			EdgeClass ec, IncidenceClass currentIc,
			List<IncidenceClass> possibleSubsettedIncidenceClasses) {
		for (ConnectsToEdgeClass ctec : ec
				.getIncidentEdges(ConnectsToEdgeClass.class)) {
			IncidenceClass ic = (IncidenceClass) ctec.getAlpha();
			if (isSubsetable(currentIc, ic)
					&& !containsASubIncidenceClass(
							possibleSubsettedIncidenceClasses, ic)) {
				possibleSubsettedIncidenceClasses.add(ic);
			}
		}
		for (SpecializesEdgeClass sec : ec.getIncidentEdges(
				SpecializesEdgeClass.class,
				de.uni_koblenz.jgralab.Direction.VERTEX_TO_EDGE)) {
			findPossibleSubsettedIncidenceClasses((EdgeClass) sec.getOmega(),
					currentIc, possibleSubsettedIncidenceClasses);
		}
		return possibleSubsettedIncidenceClasses;
	}

	private boolean containsASubIncidenceClass(
			List<IncidenceClass> incidenceClasses, IncidenceClass ic) {
		if (incidenceClasses.isEmpty()) {
			return false;
		} else if (incidenceClasses.contains(ic)) {
			return true;
		} else {
			for (SpecializesIncidenceClass sic : ic.getIncidentEdges(
					SpecializesIncidenceClass.class,
					de.uni_koblenz.jgralab.Direction.EDGE_TO_VERTEX)) {
				if (containsASubIncidenceClass(incidenceClasses,
						(IncidenceClass) sic.getAlpha())) {
					return true;
				}
			}
			return false;
		}
	}

	private boolean isSubsetable(IncidenceClass subInc, IncidenceClass superInc) {
		ProcessingException error = checkSubsetability(subInc, superInc);
		subsettibilityErrors.add(error);
		return error == null;
	}

	private boolean isSubclassOf(GraphElementClass subclass,
			GraphElementClass superclass) {
		if (subclass == superclass) {
			return true;
		}
		for (SpecializesTypedElementClass stec : subclass.getIncidentEdges(
				SpecializesTypedElementClass.class,
				de.uni_koblenz.jgralab.Direction.VERTEX_TO_EDGE)) {
			if (isSubclassOf((GraphElementClass) stec.getOmega(), superclass)) {
				return true;
			}
		}
		return false;
	}

	private void createSpecializesIncidenceClassForIncidences(
			IncidenceClass subInc, IncidenceClass superInc) {

		for (SpecializesIncidenceClass spic : subInc.getIncidentEdges(
				SpecializesIncidenceClass.class,
				de.uni_koblenz.jgralab.Direction.VERTEX_TO_EDGE)) {
			if (spic.getOmega() == superInc) {
				// there already exists a SpecializesIncidenceClass between both
				// incidences
				return;
			}
		}

		ProcessingException exception = checkSubsetability(subInc, superInc);
		if (exception != null) {
			throw exception;
		}

		sg.createSpecializesIncidenceClass(subInc, superInc);
	}

	private ProcessingException checkSubsetability(IncidenceClass subInc,
			IncidenceClass superInc) {
		assert subInc.get_direction() != null;
		assert superInc.get_direction() != null;

		EdgeClass subEC = getConnectedEdgeClass(subInc);
		EdgeClass superEC = getConnectedEdgeClass(superInc);
		VertexClass subVC = getConnectedVertexClass(subInc);
		VertexClass superVC = getConnectedVertexClass(superInc);

		// Check incidence directions
		if (subInc.get_direction() != superInc.get_direction()) {
			return new ProcessingException(getFileName(),
					"Incompatible incidence direction in specialisation "
							+ subEC.get_qualifiedName() + ".."
							+ subInc.get_roleName() + " --> "
							+ superEC.get_qualifiedName() + ".."
							+ superInc.get_roleName());
		}

		// Check multiplicities: Subclass must not have greater upper bound than
		// superclass
		if (subInc.get_maxEdgesAtVertex() > superInc.get_maxEdgesAtVertex()) {
			return new ProcessingException(
					getFileName(),
					"The multiplicity at the vertex of the subclass ("
							+ subInc.get_minEdgesAtVertex()
							+ ","
							+ (subInc.get_maxEdgesAtVertex() == Integer.MAX_VALUE ? "*"
									: subInc.get_maxEdgesAtVertex())
							+ ") than superclass ("
							+ superInc.get_minEdgesAtVertex()
							+ ","
							+ (superInc.get_maxEdgesAtVertex() == Integer.MAX_VALUE ? "*"
									: superInc.get_maxEdgesAtVertex())
							+ ") in specialisation "
							+ subEC.get_qualifiedName() + ".."
							+ subInc.get_roleName() + " --> "
							+ superEC.get_qualifiedName() + ".."
							+ superInc.get_roleName());
		}
		if (subInc.get_maxVerticesAtEdge() > superInc.get_maxVerticesAtEdge()) {
			return new ProcessingException(
					getFileName(),
					"The multiplicity at the edge of the subclass ("
							+ subInc.get_minVerticesAtEdge()
							+ ","
							+ (subInc.get_maxVerticesAtEdge() == Integer.MAX_VALUE ? "*"
									: subInc.get_maxVerticesAtEdge())
							+ ") than superclass ("
							+ superInc.get_minVerticesAtEdge()
							+ ","
							+ (superInc.get_maxVerticesAtEdge() == Integer.MAX_VALUE ? "*"
									: superInc.get_maxVerticesAtEdge())
							+ ") in specialisation "
							+ subEC.get_qualifiedName() + ".."
							+ subInc.get_roleName() + " --> "
							+ superEC.get_qualifiedName() + ".."
							+ superInc.get_roleName());
		}

		// COMPOSITION end may specialize any other end
		// AGGREGATION end may specialize only AGGREGATION and EDGE ends
		// EDGE end may specialize only EDGE ends
		IncidenceType subType = subInc.get_incidenceType();
		IncidenceType superType = superInc.get_incidenceType();
		if (((subType == IncidenceType.AGGREGATION) && (superType == IncidenceType.COMPOSITION))
				|| ((subType == IncidenceType.EDGE) && (superType != IncidenceType.EDGE))) {
			return new ProcessingException(getFileName(),
					"Incompatible aggregation kinds (" + subType
							+ " specialises " + superType
							+ ") in specialisation "
							+ subEC.get_qualifiedName() + ".."
							+ subInc.get_roleName() + " --> "
							+ superEC.get_qualifiedName() + ".."
							+ superInc.get_roleName());

		}

		// both IncidenceClasses must have the same EdgeClass
		if (!isSubclassOf(subEC, superEC)) {
			return new ProcessingException(getFileName(), "The IncidenceClass "
					+ subInc.get_roleName()
					+ " is not connected to the same EdgeClass "
					+ superInc.get_roleName() + ") in specialisation "
					+ subEC.get_qualifiedName() + " --> "
					+ superEC.get_qualifiedName());
		}

		// both IncidenceClasses must have the same VertexClass
		if (!isSubclassOf(subVC, superVC)) {
			return new ProcessingException(getFileName(), "The IncidenceClass "
					+ subInc.get_roleName()
					+ " is not connected to the same VertexClass "
					+ superInc.get_roleName() + ") in specialisation "
					+ subEC.get_qualifiedName() + ".." + subInc.get_roleName()
					+ " --> " + superEC.get_qualifiedName() + ".."
					+ superInc.get_roleName());
		}

		return null;
	}

	private VertexClass getConnectedVertexClass(IncidenceClass ic) {
		for (ConnectsToVertexClass ctec : ic
				.getIncidentEdges(ConnectsToVertexClass.class)) {
			return (VertexClass) ctec.getOmega();
		}
		return null;
	}

	private EdgeClass getConnectedEdgeClass(IncidenceClass ic) {
		for (ConnectsToEdgeClass ctec : ic
				.getIncidentEdges(ConnectsToEdgeClass.class)) {
			return (EdgeClass) ctec.getOmega();
		}
		return null;
	}

	/**
	 * breadth first search over SpecializesIncidenceClass edges for closest
	 * superclass with correct rolename
	 * 
	 * @param inc
	 * @param rolename
	 * @return
	 */
	private IncidenceClass findClosestSuperclassWithRolename(
			IncidenceClass inc, String rolename) {
		IncidenceClass sup = null;
		Queue<IncidenceClass> q = new LinkedList<IncidenceClass>();
		LocalBooleanGraphMarker m = new LocalBooleanGraphMarker(sg);
		m.mark(inc);
		q.offer(inc);
		while (!q.isEmpty()) {
			IncidenceClass curr = q.poll();
			m.mark(curr);
			if ((curr != inc) && rolename.equals(curr.get_roleName())) {
				sup = curr;
				break;
			}
			for (SpecializesIncidenceClass sic : curr.getIncidentEdges(
					SpecializesIncidenceClass.class,
					de.uni_koblenz.jgralab.Direction.VERTEX_TO_EDGE)) {
				IncidenceClass i = (IncidenceClass) sic.getOmega();
				if (!m.isMarked(i)) {
					m.mark(i);
					q.offer(i);
				}
			}

		}
		return sup;
	}

	/**
	 * Writes a DOT file and a TG file out.
	 * 
	 * @throws XMLStreamException
	 * @throws GraphIOException
	 */
	public void writeOutput() throws XMLStreamException, GraphIOException {

		boolean fileCreated = false;

		if (filenameDot != null) {
			writeDotFile(filenameDot);
			printTypeAndFilename("GraphvViz DOT file", filenameDot);
			fileCreated = true;
		}

		if (filenameSchemaGraph != null) {
			writeSchemaGraph(filenameSchemaGraph);
			printTypeAndFilename("schemagraph", filenameSchemaGraph);
			fileCreated = true;
		}

		// The Graph is always validated, but not always written to a hard
		// drive.validateGraph(filenameValidation);
		// TODO if GraphValidator works again comment it in
		if (filenameValidation != null) {
			printTypeAndFilename("validation report", filenameValidation);
			fileCreated = true;
		}

		if (filenameSchema != null) {
			writeSchema(filenameSchema);
			printTypeAndFilename("schema", filenameSchema);
			fileCreated = true;
		}

		if (!fileCreated) {
			System.out.println("No files have been created.");
		}
	}

	private void printTypeAndFilename(String type, String filename) {
		System.out.println("Writing " + type + " to: " + filename);
	}

	/**
	 * Performs a graph validation and writes a report in a file.
	 * 
	 * @param schemaName
	 *            Name of the Schema.
	 * @param relativePathPrefix
	 *            Relative path to a folder.
	 */
	private void validateGraph(String filePath) {

		try {
			GraphValidator validator = new GraphValidator(sg);
			Set<ConstraintViolation> s;
			if (filePath != null) {
				s = validator.createValidationReport(filePath);
			} else {
				s = validator.validate();
			}
			if (!s.isEmpty()) {
				System.err.println("The schema graph is not valid:");
				for (ConstraintViolation currentViolation : s) {
					// print out violations
					System.err.println(currentViolation.getMessage());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a message for an unexpected element and includes its type.
	 * 
	 * @param name
	 *            Name of the unexpected element.
	 * @param type
	 *            Type of the unexpected element.
	 * @return Created message.
	 */
	private String createUnexpectedElementMessage(String name, String type) {

		String typeInsertion = type != null ? " of type " + type : "";
		return "Unexpected element <" + name + ">" + typeInsertion + ".";
	}

	/**
	 * Handles a 'uml:Package' element by creating a corresponding grUML Package
	 * element.
	 * 
	 * @return Created Package object as Vertex.
	 */
	private Vertex handlePackage() throws XMLStreamException {

		Package pkg = sg.createPackage();
		pkg.set_qualifiedName(getQualifiedName(getAttribute(UML_ATTRIBUTE_NAME)));
		sg.createContainsSubPackage(packageStack.peek(), pkg);
		packageStack.push(pkg);
		return pkg;
	}

	/**
	 * Handles a 'uml:Class' element by creating a corresponding grUML
	 * {@link VertexClass} element.
	 * 
	 * @param xmiId
	 *            XMI ID in RSA XMI file.
	 * @return Created VertexClass as {@link Vertex}.
	 * @throws XMLStreamException
	 */
	private Vertex handleClass(String xmiId) throws XMLStreamException {

		AttributedElement<?, ?> ae = idMap.get(xmiId);
		VertexClass vc = null;
		if (ae != null) {

			// Element with ID xmiID must be a VertexClass
			if (!(ae instanceof VertexClass)) {
				throw new ProcessingException(getParser(), getFileName(),
						"The element with ID '" + xmiId
								+ "' is not a class. (VertexClass)");
			}

			assert preliminaryVertices.contains(ae);
			preliminaryVertices.remove(ae);
			vc = (VertexClass) ae;
		} else {
			vc = sg.createVertexClass();
		}
		currentClassId = xmiId;
		currentClass = vc;
		String abs = getAttribute(UML_ATTRIBUTE_IS_ABSRACT);
		vc.set_abstract((abs != null) && abs.equals(UML_TRUE));
		vc.set_qualifiedName(getQualifiedName(getAttribute(UML_ATTRIBUTE_NAME)));
		vc.set_minKappa(0);
		vc.set_maxKappa(Integer.MAX_VALUE);
		sg.createContainsGraphElementClass(packageStack.peek(), vc);

		// System.out.println("currentClass = " + currentClass + " "
		// + currentClass.getQualifiedName());
		return vc;
	}

	/**
	 * Handles a 'uml:Association' or a 'uml:AssociationClass' element by
	 * creating a corresponding {@link EdgeClass} element.
	 * 
	 * @param xmiId
	 *            XMI ID in XMI file.
	 * @param isAssociationClass
	 * @return Created EdgeClass as {@link Vertex}.
	 * @throws XMLStreamException
	 */
	private Vertex handleAssociation(String xmiId, boolean isAssociationClass)
			throws XMLStreamException {

		// create an EdgeClass at first, probably, this has to
		// become an Aggregation or Composition later...
		AttributedElement<?, ?> ae = idMap.get(xmiId);
		EdgeClass ec = null;
		if (ae != null) {
			if (EdgeClass.class.isInstance(ae)) {
				ec = (EdgeClass) ae;
			} else if (!isAssociationClass || !VertexClass.class.isInstance(ae)) {
				throw new ProcessingException(getParser(), getFileName(),
						"The XMI id " + xmiId
								+ " must denote an EdgeClass, but is "
								+ ae.getType().getQualifiedName());
			}

			assert preliminaryVertices.contains(ae);
			preliminaryVertices.remove(ae);
		}
		if (ae == null
				|| (isAssociationClass && VertexClass.class.isInstance(ae))) {
			ec = sg.createBinaryEdgeClass();
			ec.set_minKappa(0);
			ec.set_maxKappa(Integer.MAX_VALUE);
			if (ae != null && isAssociationClass) {
				// the preliminary VertexClass was created because the current
				// associationClass has an incident composition which was
				// transformed first
				idMap.put(xmiId, ec);
				replacePreliminaryVertexClassWithEdgeClass((VertexClass) ae, ec);
			}
		}
		currentClassId = xmiId;
		currentClass = ec;
		String abs = getAttribute(UML_ATTRIBUTE_IS_ABSRACT);
		ec.set_abstract((abs != null) && abs.equals(UML_TRUE));
		String n = getAttribute(UML_ATTRIBUTE_NAME);
		n = n == null ? "" : n.trim();
		if (n.length() > 0) {
			n = Character.toUpperCase(n.charAt(0)) + n.substring(1);
		}
		ec.set_qualifiedName(getQualifiedName(n));
		sg.createContainsGraphElementClass(packageStack.peek(), ec);

		String memberEnd = getAttribute(UML_MEMBER_END);
		// An association have to have a end member.
		if (memberEnd == null) {
			throw new ProcessingException(getParser(), getFileName(),
					"The association with ID '" + xmiId
							+ "' has no end member. (EdgeClass)");
		}
		memberEnd = memberEnd.trim().replaceAll("\\s+", " ");
		int p = memberEnd.indexOf(' ');
		String targetEnd = memberEnd.substring(0, p);
		String sourceEnd = memberEnd.substring(p + 1);

		IncidenceClass inc = (IncidenceClass) idMap.get(sourceEnd);
		if (inc == null) {
			VertexClass vc = sg.createVertexClass();
			preliminaryVertices.add(vc);
			vc.set_qualifiedName("preliminary for source end " + sourceEnd);
			vc.set_minKappa(0);
			vc.set_maxKappa(Integer.MAX_VALUE);
			inc = sg.createIncidenceClass();
			inc.set_incidenceType(IncidenceType.EDGE);
			inc.set_abstract(false);
			inc.set_minEdgesAtVertex(DEFAULT_MIN_MULTIPLICITY);
			inc.set_maxEdgesAtVertex(DEFAULT_MAX_MULTIPLICITY);
			inc.set_minVerticesAtEdge(DEFAULT_MIN_MULTIPLICITY);
			inc.set_maxVerticesAtEdge(DEFAULT_MAX_MULTIPLICITY);
			inc.set_direction(Direction.VERTEX_TO_EDGE);
			sg.createConnectsToEdgeClass(inc, ec);
			sg.createConnectsToVertexClass(inc, vc);
			idMap.put(sourceEnd, inc);
		}

		inc = (IncidenceClass) idMap.get(targetEnd);
		if (inc != null) {
			assert inc.isValid();
			assert inc.get_direction() == Direction.VERTEX_TO_EDGE;

			VertexClass vc = null;
			for (ConnectsToVertexClass ctvc : inc
					.getIncidentEdges(ConnectsToVertexClass.class)) {
				vc = (VertexClass) ctvc.getOmega();
				break;
			}

			IncidenceClass to = sg.createIncidenceClass();

			IncidenceClass from = inc;
			to.set_direction(Direction.EDGE_TO_VERTEX);
			to.set_incidenceType(from.get_incidenceType());
			to.set_abstract(from.is_abstract());
			to.set_minVerticesAtEdge(from.get_minVerticesAtEdge());
			to.set_maxVerticesAtEdge(from.get_maxVerticesAtEdge());
			to.set_minEdgesAtVertex(from.get_minEdgesAtVertex());
			to.set_maxEdgesAtVertex(from.get_maxEdgesAtVertex());
			to.set_roleName(from.get_roleName());

			sg.createConnectsToEdgeClass(to, ec);
			if (vc != null) {
				sg.createConnectsToVertexClass(to, vc);
			} else {
				// the incident VertexClass was deleted, because this is a
				// composition with an incident associationClass, which was
				// represented by the deleted preliminary VertexClass
				// TODO check
				wrongEdgeClasses.add(ec);
			}

			if (ownedEnds.contains(from)) {
				ownedEnds.remove(from);
				ownedEnds.add(to);
			}
			inc.delete();
			idMap.put(targetEnd, to);
		} else {
			VertexClass vc = sg.createVertexClass();
			preliminaryVertices.add(vc);
			vc.set_qualifiedName("preliminary for target end " + targetEnd);
			vc.set_minKappa(0);
			vc.set_maxKappa(Integer.MAX_VALUE);
			inc = sg.createIncidenceClass();
			inc.set_incidenceType(IncidenceType.EDGE);
			inc.set_abstract(false);
			inc.set_minEdgesAtVertex(DEFAULT_MIN_MULTIPLICITY);
			inc.set_maxEdgesAtVertex(DEFAULT_MAX_MULTIPLICITY);
			inc.set_minVerticesAtEdge(DEFAULT_MIN_MULTIPLICITY);
			inc.set_maxVerticesAtEdge(DEFAULT_MAX_MULTIPLICITY);
			inc.set_direction(Direction.EDGE_TO_VERTEX);
			sg.createConnectsToEdgeClass(inc, ec);
			sg.createConnectsToVertexClass(inc, vc);
			idMap.put(targetEnd, inc);
		}
		String isDerived = getAttribute(XMIConstants.UML_ATTRIBUTE_ISDERIVED);
		if (isDerived != null && isDerived.equals(XMIConstants.UML_TRUE)) {
			ec.set_abstract(true);
		}
		return ec;
	}

	private void replacePreliminaryVertexClassWithEdgeClass(VertexClass vc,
			EdgeClass ec) {
		EdgeClass oldEC = null;
		MayBeNestedIn oldMBNI = null;
		MayBeNestedIn newMBNI = null;
		for (Incidence currI = vc.getFirstIncidence(); currI != null;) {
			Incidence nextI = currI.getNextIncidenceAtVertex();
			if (MayBeNestedIn_nestedElement.class.isInstance(currI)) {
				// ec is nested
				newMBNI = sg.createMayBeNestedIn(ec,
						(GraphElementClass) ((MayBeNestedIn) currI.getEdge())
								.getOmega());
				oldMBNI = (MayBeNestedIn) currI.getEdge();
			} else if (MayBeNestedIn_nestingElement.class.isInstance(currI)) {
				// ec is nesting
				newMBNI = sg.createMayBeNestedIn(
						(GraphElementClass) ((MayBeNestedIn) currI.getEdge())
								.getOmega(), ec);
				oldMBNI = (MayBeNestedIn) currI.getEdge();
			} else if (ConnectsToVertexClass_connectedVertexClass.class
					.isInstance(currI)) {
				oldEC = getConnectedEdgeClass((IncidenceClass) currI.getThat());
				wrongEdgeClasses.add(oldEC);

				// currI.getEdge().delete();
			}
			currI = nextI;
		}

		if (newMBNI != null) {
			getMayBeNestedInRepresentation.mark(oldEC, newMBNI);
			oldMBNI.delete();
		} else {
			updateMayBeNestedIn(vc, ec, oldEC);
		}

		for (GraphElement<?, ?, ?> ge : nestedElements.getMarkedElements()) {
			Set<GraphElementClass> mark = nestedElements.getMark(vc);
			if (mark.contains(vc)) {
				mark.remove(vc);
				mark.add(ec);
			}
			if (ge == vc) {
				nestedElements.mark(ec, mark);
				nestedElements.removeMark(vc);
			}

		}
		for (GraphElement<?, ?, ?> ge : generalizations.getMarkedElements()) {
			if (ge == vc) {
				generalizations.mark(ec, generalizations.getMark(vc));
				generalizations.removeMark(vc);
			}
		}

		vc.delete();
	}

	/**
	 * Handles a 'uml:Enumeration' element by creating a corresponding
	 * {@link EnumDomain} element.
	 * 
	 * @return Created EnumDomain as {@link Vertex}.
	 * @throws XMLStreamException
	 */
	private Vertex handleEnumeration() throws XMLStreamException {
		EnumDomain ed = sg.createEnumDomain();
		Package p = packageStack.peek();
		ed.set_qualifiedName(getQualifiedName(getAttribute(UML_ATTRIBUTE_NAME)));
		sg.createContainsDomain(p, ed);
		ed.set_enumConstants(new ArrayList<String>());
		Domain dom = domainMap.get(ed.get_qualifiedName());
		if (dom != null) {
			// there was a preliminary vertex for this domain
			// link the edges to the correct one

			assert preliminaryVertices.contains(dom);
			reconnectEdges(dom, ed);
			// delete preliminary vertex
			dom.delete();
			preliminaryVertices.remove(dom);
		}
		domainMap.put(ed.get_qualifiedName(), ed);
		return ed;
	}

	/**
	 * Handles a 'uml:PrimitiveType' element by creating a corresponding
	 * {@link Domain} element.
	 * 
	 * @return Created Domain as Vertex.
	 */
	private Vertex handlePrimitiveType(String xmiId) throws XMLStreamException {

		String typeName = getAttribute(UML_ATTRIBUTE_NAME);

		if (typeName == null) {
			throw new ProcessingException(getParser(), getFileName(),
					"No type name in primitive type. XMI ID: " + xmiId);
		}
		typeName = typeName.replaceAll("\\s", "");

		if (typeName.length() == 0) {
			throw new ProcessingException(getParser(), getFileName(),
					"Type name in primitive type is empty. XMI ID: " + xmiId);
		}
		Domain dom = createDomain(typeName);

		assert dom != null;
		return dom;
	}

	/**
	 * Handles a 'uml:Realization' by putting it into a map of realizations. By
	 * this, missing generalizations can be traced.
	 * 
	 * @throws XMLStreamException
	 */
	private void handleRealization() throws XMLStreamException {

		String supplier = getAttribute(UML_ATTRIBUTE_SUPPLIER);
		String client = getAttribute(UML_ATTRIBUTE_CLIENT);
		Set<String> reals = realizations.get(client);
		if (reals == null) {
			reals = new TreeSet<String>();
			realizations.put(client, reals);
		}
		reals.add(supplier);
	}

	/**
	 * Creates a String for a {@link AttributedElementClass} by writing the
	 * AttributedElementClass name first and than a list of attributes with
	 * their values of the AttributedElementClass.
	 * 
	 * @param attributedElement
	 *            {@link AttributedElement}, of which a {@link String}
	 *            representation should be created.
	 * @return A String representing the given AttributedElement.
	 */
	private String attributedElement2String(
			AttributedElement<?, ?> attributedElement) {

		StringBuilder sb = new StringBuilder();

		de.uni_koblenz.jgralab.schema.AttributedElementClass<?, ?> aec = attributedElement
				.getType();
		sb.append(attributedElement);
		sb.append(" { ");

		for (de.uni_koblenz.jgralab.schema.Attribute attr : aec
				.getAttributeList()) {
			sb.append(attr.getName());
			sb.append(" = ");
			sb.append(attributedElement.getAttribute(attr.getName()));
			sb.append("; ");
		}
		sb.append("}\n");

		return sb.toString();
	}

	/**
	 * Handles a 'uml:EnumerationLiteral' by creating a corresponding
	 * enumeration literal and adding it to its {@link EnumDomain}.
	 * 
	 * @param xmiId
	 * 
	 * @throws XMLStreamException
	 */
	private void handleEnumerationLiteral() throws XMLStreamException {

		String s = getAttribute(UML_ATTRIBUTE_NAME);

		// A Literal must be declared.
		if (s == null) {
			throw new ProcessingException(getParser(), getFileName(),
					"No Literal declared.");
		}
		s = s.trim();

		// Literal must not be empty.
		if (s.length() <= 0) {
			throw new ProcessingException(getParser(), getFileName(),
					"Literal is empty.");
		}

		String classifier = getAttribute(UML_ATTRIBUTE_CLASSIFIER);

		// Exception "No Enum found for Literal " ... " found.
		if (classifier == null) {
			throw new ProcessingException(getParser(), getFileName(),
					"No Enumeration found for Literal '" + s + "'.");
		}
		EnumDomain ed = (EnumDomain) idMap.get(classifier);

		if (!s.equals(s.toUpperCase())) {
			System.out.println("Warning: Enumeration literal '" + s
					+ "' in enumeration + '" + ed.get_qualifiedName()
					+ "' should be all uppercase letters.");
		}

		ed.get_enumConstants().add(s);
	}

	/**
	 * Writes the current processed {@link Schema} as a Schema to a TG file.
	 * 
	 * @param schemaName
	 *            Name of the Schema.
	 */
	private void writeSchema(String schemaName) {
		try {
			SchemaGraph2Tg sg2tg = new SchemaGraph2Tg(sg, schemaName);
			sg2tg.process();
		} catch (IOException e) {
			throw new RuntimeException(
					"SchemaGraph2Tg failed with an IOException!", e);
		}
	}

	/**
	 * Corrects the current edge direction of every {@link EdgeClass} by using
	 * the navigability.
	 */
	private void correctEdgeDirection() {
		if (!isUseNavigability()) {
			return;
		}
		System.out
				.println("Correcting edge directions according to navigability...");
		for (EdgeClass e : sg.getEdgeClassVertices()) {
			IncidenceClass from = null;
			IncidenceClass to = null;
			for (ConnectsToEdgeClass ctec : e
					.getIncidentEdges(ConnectsToEdgeClass.class)) {
				IncidenceClass ic = (IncidenceClass) ctec.getAlpha();
				if (ic.get_direction() == Direction.VERTEX_TO_EDGE) {
					from = ic;
				} else {
					to = ic;
				}
			}
			if (from == null) {
				throw new ProcessingException(getFileName(), "EdgeClass "
						+ e.get_qualifiedName() + " has no start incidence");
			}
			if (to == null) {
				throw new ProcessingException(getFileName(), "EdgeClass "
						+ e.get_qualifiedName() + " has no end incidence");
			}

			boolean fromIsNavigable = !ownedEnds.contains(from);
			boolean toIsNavigable = !ownedEnds.contains(to);
			if (fromIsNavigable == toIsNavigable) {
				// no navigability specified or both ends navigable:
				// do nothing, edge direction is determined by order of memerEnd
				// in association
				continue;
			}

			if (toIsNavigable) {
				// "to" end is marked navigable, nothing to change
				continue;
			}

			// "from" end is marked navigable, swap edge direction
			assert to.get_direction() == Direction.VERTEX_TO_EDGE;
			assert from.get_direction() == Direction.EDGE_TO_VERTEX;
		}

	}

	/**
	 * Attaches all Constraint objects to their corresponding
	 * {@link AttributedElementClass}.
	 * 
	 * @throws XMLStreamException
	 */
	private void attachConstraints() throws XMLStreamException {
		System.out.println("Attaching constraints...");
		for (String constrainedElementId : constraints.keySet()) {
			List<String> l = constraints.get(constrainedElementId);
			if (l.size() == 0) {
				continue;
			}
			Vertex ae = idMap.get(constrainedElementId);
			if (ae == null) {
				ae = graphClass;
			}

			if (!ae.isValid()) {
				// vertex has been removed
				continue;
			}

			// Constraint are attached to GraphClass, VertexClass, EdgeClass or
			// Association Ends.
			if (!(ae instanceof AttributedElementClass)
					&& !(ae instanceof IncidenceClass)) {
				throw new ProcessingException(
						getFileName(),
						"Constraint can only be attached to GraphClass, "
								+ "VertexClass, EdgeClass or association ends. Offending element is "
								+ ae + " (XMI id " + constrainedElementId + ")");
			}

			if (ae instanceof AttributedElementClass) {
				if (((AttributedElementClass) ae).isValid()) {
					for (String text : l) {
						if (text.startsWith("kappa")
								&& ae instanceof GraphElementClass) {
							setKappaValues((GraphElementClass) ae, text);
						} else {
							addGreqlConstraint((AttributedElementClass) ae,
									text);
						}
					}
				}
			} else if (ae instanceof IncidenceClass) {
				if (((IncidenceClass) ae).isValid()) {
					for (String text : l) {
						if (text.startsWith("redefines")
								|| text.startsWith("subsets")) {
							addRedefinesOrSubsetsConstraint(
									(IncidenceClass) ae, text,
									constrainedElementId);
						} else {
							throw new ProcessingException(
									getFileName(),
									"Only 'redefines' and 'subsets' constraints are allowed at association ends. Offending element: "
											+ ae
											+ " (XMI id "
											+ constrainedElementId + ")");
						}
					}
				}
			} else {
				throw new ProcessingException(getFileName(),
						"Don't know what to do with constraint(s) at element "
								+ ae + " (XMI id " + constrainedElementId + ")");
			}
		}
	}

	private void setKappaValues(GraphElementClass gec, String text) {
		String value = text.substring(6).trim();
		if (value.contains("..")) {
			String[] values = value
					.split("\\s*" + Pattern.quote("..") + "\\s*");
			assert values.length == 2;
			assert !values[0].equals("*");
			gec.set_minKappa(Integer.parseInt(values[0]));
			gec.set_maxKappa(values[1].equals("*") ? Integer.MAX_VALUE
					: Integer.parseInt(values[1]));
		} else {
			if (value.equals("*")) {
				gec.set_minKappa(0);
				gec.set_maxKappa(Integer.MAX_VALUE);
			} else {
				int kappa = Integer.parseInt(value);
				gec.set_minKappa(kappa);
				gec.set_maxKappa(kappa);
			}
		}
	}

	/*
	 * EdgeClasses with a simple name of the form $<numbers>$ will be renamed as
	 * if there was no name. $<number>:<midName>$ will be renamed as if there
	 * was no name, but the middle part is fixed to <midName> instead of using
	 * Contains/IsPartOf/LinksTo. That allows for "unnamed" association classes.
	 */
	private static final Pattern GENNAME_PATTERN = Pattern
			.compile("(.*)\\$\\p{Digit}+(:(\\w+))?\\$$");

	/**
	 * Creates {@link EdgeClass} names for all EdgeClass objects, which do have
	 * an empty String or a String, which ends with a '.'.
	 */
	private void createEdgeClassNames() {
		System.out.println("Creating missing edge class names...");
		for (EdgeClass ec : sg.getEdgeClassVertices()) {
			String name = ec.get_qualifiedName().trim();

			// invent an edgeclass name
			String ecName = null;

			Matcher m = GENNAME_PATTERN.matcher(name);
			if (m.matches()) {
				name = m.group(1);
				ecName = m.group(m.groupCount());
			}

			if (!name.equals("") && !name.endsWith(".")) {
				continue;
			}

			IncidenceClass to = null;
			IncidenceClass from = null;
			for (ConnectsToEdgeClass ctec : ec
					.getIncidentEdges(ConnectsToEdgeClass.class)) {
				IncidenceClass inc = (IncidenceClass) ctec.getAlpha();
				if (inc.get_direction() == Direction.VERTEX_TO_EDGE) {
					if (from == null) {
						from = inc;
					}
				} else {
					if (to == null) {
						to = inc;
					}
				}
			}
			assert to != null && from != null;

			String toRole = to.get_roleName();
			if ((toRole == null) || toRole.equals("")) {
				toRole = ((VertexClass) ((ConnectsToVertexClass) to
						.getFirstIncidence(
								ConnectsToVertexClass_connectsToVertexClass_ComesFrom_IncidenceClass.class)
						.getEdge()).getOmega()).get_qualifiedName();
				int p = toRole.lastIndexOf('.');
				if (p >= 0) {
					toRole = toRole.substring(p + 1);
				}
			} else {
				toRole = Character.toUpperCase(toRole.charAt(0))
						+ toRole.substring(1);
			}

			// There must be a 'to' role name, which is different than null and
			// not empty.
			if ((toRole == null) || (toRole.length() <= 0)) {
				throw new ProcessingException(getFileName(),
						"There is no role name 'to' for the edge '" + name
								+ "' defined.");
			}

			if (ecName == null) {
				if ((from.get_incidenceType() != IncidenceType.EDGE)
						|| (to.get_incidenceType() != IncidenceType.EDGE)) {
					if (to.get_incidenceType() != IncidenceType.EDGE) {
						ecName = "Contains" + toRole;
					} else {
						ecName = "IsPartOf" + toRole;
					}
				} else {
					ecName = "LinksTo" + toRole;
				}
			} else {
				ecName += toRole;
			}

			if (isUseFromRole()) {
				String fromRole = from.get_roleName();
				if ((fromRole == null) || fromRole.equals("")) {
					fromRole = ((VertexClass) ((ConnectsToVertexClass) from
							.getFirstIncidence(
									ConnectsToVertexClass_connectsToVertexClass_ComesFrom_IncidenceClass.class)
							.getEdge()).getOmega()).get_qualifiedName();
					int p = fromRole.lastIndexOf('.');
					if (p >= 0) {
						fromRole = fromRole.substring(p + 1);
					}
				} else {
					fromRole = Character.toUpperCase(fromRole.charAt(0))
							+ fromRole.substring(1);
				}

				// There must be a 'from' role name, which is different than
				// null and not empty.
				if ((fromRole == null) || (fromRole.length() <= 0)) {
					throw new ProcessingException(getFileName(),
							"There is no role name of 'from' for the edge '"
									+ name + "' defined.");
				}
				name += fromRole;
			}

			assert (ecName != null) && (ecName.length() > 0);
			ec.set_qualifiedName(name + ecName);
		}
	}

	/**
	 * Removes unused {@link Domain} objects, which are included in the current
	 * {@link SchemaGraph}.
	 */
	private void removeUnusedDomains() {
		System.out.println("Removing unused domains...");
		Domain d = sg.getFirstDomain();
		while (d != null) {
			Domain n = d.getNextDomain();
			// unused if in-degree of all but Annotates edges is <=1 (one
			// incoming edge is the ContainsDomain edge from a Package)
			if (d.getDegree(de.uni_koblenz.jgralab.Direction.EDGE_TO_VERTEX)
					- d.getDegree(Annotates_annotatedElement.class) <= 1) {
				// System.out.println("...remove unused domain '"
				// + d.getQualifiedName() + "'");

				// remove possible comments
				for (Incidence i = d
						.getFirstIncidence(Annotates_annotatedElement.class); i != null; i = d
						.getFirstIncidence(Annotates_annotatedElement.class)) {
					Comment c = (Comment) ((Annotates) i.getEdge()).getAlpha();
					c.delete();
				}
				d.delete();
				d = sg.getFirstDomain();
			} else {
				d = n;
			}
		}
	}

	/**
	 * Resolves all preliminary {@link StringDomain}, which store the domain id,
	 * to existing {@link Domain} objects and links them to their corresponding
	 * {@link RecordDomain} objects.
	 */
	private void linkRecordDomainComponents() {

		for (HasRecordDomainComponent comp : sg
				.getHasRecordDomainComponentEdges()) {

			String domainId = recordComponentType.getMark(comp);
			if (domainId == null) {
				recordComponentType.removeMark(comp);
				continue;
			}

			Domain dom = (Domain) idMap.get(domainId);
			if (dom != null) {
				Domain d = (Domain) comp.getOmega();

				// preliminary domain vertex exists and has type StringDomain,
				// but the name of the StringDomain is the "real" domain name
				assert (d instanceof StringDomain)
						&& d.get_qualifiedName().equals(domainId)
						&& preliminaryVertices.contains(d);
				comp.setOmega(dom);
				d.delete();
				preliminaryVertices.remove(d);
				recordComponentType.removeMark(comp);
			} else {
				throw new ProcessingException(getFileName(),
						"Undefined Domain with ID '" + domainId + "' found.");
			}
		}

		if (!recordComponentType.isEmpty()) {
			throw new ProcessingException(getFileName(),
					"Some RecordDomains have unresolved component types.");
		}
	}

	/**
	 * Links {@link Attribute} and {@link Domain} objects to each other by
	 * creating a {@link HasDomain} edge.
	 */
	private void linkAttributeDomains() {

		for (Attribute att : sg.getAttributeVertices()) {
			String domainId = attributeType.getMark(att);
			if (domainId == null) {
				assert att.getDegree(HasAttribute_attribute.class) == 1 : "Attribute '"
						+ att.get_name()
						+ "' of "
						+ att.getFirst_attribute(false).getThat().getM1Class()
								.getSimpleName()
						+ " '"
						+ ((AttributedElementClass) att.getFirst_attribute(
								false).getThat()).get_qualifiedName()
						+ "' has "
						+ att.getDegree(HasDomain_domain.class)
						+ " domain(s)";
				continue;
			}
			Domain dom = (Domain) idMap.get(domainId);
			if (dom != null) {
				sg.createHasDomain(att, dom);
				attributeType.removeMark(att);
			} else {
				// Every Attribute must have a Domain.
				throw new ProcessingException(getFileName(),
						"Undefined Domain with ID '" + domainId + "' found.");
			}

			assert att.getDegree(HasDomain_domain.class) == 1;
		}

		// If 'attributeType' is not empty, there will be a Domain objects
		// left over.
		if (!attributeType.isEmpty()) {
			throw new ProcessingException(getFileName(),
					"There are some Attribute objects, whos domains are not resolved.");
		}
	}

	/**
	 * Writes the {@link SchemaGraph} as Dotty-Graph to a DOT file with the name
	 * of 'dotName'.
	 * 
	 * @param dotName
	 *            File name of the DOT output file.
	 */
	private void writeDotFile(String dotName) {
		Tg2Dot tg2Dot = new Tg2Dot();
		tg2Dot.setGraph(sg);
		tg2Dot.setPrintEdgeAttributes(true);
		tg2Dot.setOutputFile(dotName);
		tg2Dot.printGraph();
	}

	/**
	 * Writes the {@link SchemaGraph} as a Graph to a TG file with the specified
	 * file name <code>schemaGraphName</code>.
	 * 
	 * @param schemaGraphName
	 *            File name of the TG output file.
	 * @throws GraphIOException
	 */
	private void writeSchemaGraph(String schemaGraphName)
			throws GraphIOException {
		GraphIO.saveGraphToFile(schemaGraphName, sg, null);
	}

	/**
	 * Realizes the Generalization relationship by linking
	 * {@link AttributedElementClass} objects to their direct superclass(es).
	 */
	private void linkGeneralizations() {
		for (String clientId : realizations.keySet()) {
			Set<String> suppliers = realizations.get(clientId);
			AttributedElementClass client = (AttributedElementClass) idMap
					.get(clientId);
			if (suppliers.size() > 0) {
				Set<String> superClasses = generalizations.getMark(client);
				if (superClasses == null) {
					superClasses = new TreeSet<String>();
					generalizations.mark(client, superClasses);
				}
				superClasses.addAll(suppliers);
			}
		}

		for (AttributedElement<?, ?> ae : generalizations.getMarkedElements()) {
			TypedElementClass sub = (TypedElementClass) ae;

			Set<String> superclasses = generalizations.getMark(sub);
			for (String id : superclasses) {
				TypedElementClass sup = (TypedElementClass) idMap.get(id);

				if (sup == null) {
					// No superclass with the specified ID has been found.
					throw new ProcessingException(getFileName(),
							"The superclass with XMI id '" + id
									+ "' could not be found.");
				}
				if (sup instanceof VertexClass) {
					// VertexClass can only specialize a VertexClass
					if (!(sub instanceof VertexClass)) {
						throw new ProcessingException(getFileName(),
								"Different types in generalization: "
										+ sub.getM1Class().getSimpleName()
										+ " '" + sub.get_qualifiedName()
										+ "' can not be subclass of "
										+ sub.getM1Class().getSimpleName()
										+ " '" + sup.get_qualifiedName() + "'");
					}

					sg.createSpecializesVertexClass((VertexClass) ae,
							(VertexClass) sup);
					if (!vertexClassHierarchyIsAcyclic()) {
						throw new ProcessingException(getFileName(),
								"Cycle in vertex class hierarchy. Involved classes are '"
										+ sub.get_qualifiedName() + "' and '"
										+ sup.get_qualifiedName() + "'");
					}
				} else if (sup instanceof EdgeClass) {
					// EdgeClass can only specialize an EdgeClass
					if (!(sub instanceof EdgeClass)) {
						throw new ProcessingException(getFileName(),
								"Different types in generalization: "
										+ sub.getM1Class().getSimpleName()
										+ " '" + sub.get_qualifiedName()
										+ "' can not be subclass of "
										+ sub.getM1Class().getSimpleName()
										+ " '" + sup.get_qualifiedName() + "'");
					}

					sg.createSpecializesEdgeClass((EdgeClass) ae,
							(EdgeClass) sup);
					if (!edgeClassHierarchyIsAcyclic()) {
						throw new ProcessingException(getFileName(),
								"Cycle in edge class hierarchy. Involved classes are '"
										+ sub.get_qualifiedName() + "' and '"
										+ sup.get_qualifiedName() + "'");
					}
				} else if (sup instanceof IncidenceClass) {
					// IncidenceClass can only specialize an IncidenceClass
					if (!(sub instanceof IncidenceClass)) {
						throw new ProcessingException(getFileName(),
								"Different types in generalization: "
										+ sub.getM1Class().getSimpleName()
										+ " '" + sub.get_qualifiedName()
										+ "' can not be subclass of "
										+ sub.getM1Class().getSimpleName()
										+ " '" + sup.get_qualifiedName() + "'");
					}

					sg.createSpecializesIncidenceClass((IncidenceClass) ae,
							(IncidenceClass) sup);
					if (!incidenceClassHierarchyIsAcyclic()) {
						throw new ProcessingException(getFileName(),
								"Cycle in incidence class hierarchy. Involved classes are '"
										+ sub.get_qualifiedName() + "' and '"
										+ sup.get_qualifiedName() + "'");
					}
				} else {
					// Should not get here
					throw new RuntimeException(
							"FIXME: Unexpected super class type. Super class must be VertexClass or EdgeClass!");
				}
			}
		}
		generalizations.clear();
	}

	/**
	 * Checks whether the edge class generalization hierarchy is acyclic.
	 * 
	 * @return true iff the edge class generalization hierarchy is acyclic.
	 */
	private boolean edgeClassHierarchyIsAcyclic() {
		return getSpecializesTypedElementClassInTopologicalOrder(1) != null;
		// if (edgeClassAcyclicEvaluator == null) {
		// edgeClassAcyclicEvaluator = new GreqlEvaluator(
		// "isAcyclic(vSubgraph{structure.EdgeClass})", sg, null);
		// }
		// edgeClassAcyclicEvaluator.startEvaluation();
		// return edgeClassAcyclicEvaluator.getEvaluationResult().toBoolean();
	}

	/**
	 * Checks whether the vertex class generalization hierarchy is acyclic.
	 * 
	 * @return true iff the vertex class generalization hierarchy is acyclic.
	 */
	private boolean vertexClassHierarchyIsAcyclic() {
		return getSpecializesTypedElementClassInTopologicalOrder(2) != null;
		// if (vertexClassAcyclicEvaluator == null) {
		// vertexClassAcyclicEvaluator = new GreqlEvaluator(
		// "isAcyclic(vSubgraph{structure.VertexClass})", sg, null);
		// }
		// vertexClassAcyclicEvaluator.startEvaluation();
		// return vertexClassAcyclicEvaluator.getEvaluationResult().toBoolean();
	}

	/**
	 * Checks whether the incidence class generalization hierarchy is acyclic.
	 * 
	 * @return true iff the incidence class generalization hierarchy is acyclic.
	 */
	private boolean incidenceClassHierarchyIsAcyclic() {
		return getSpecializesTypedElementClassInTopologicalOrder(3) != null;
		// if (vertexClassAcyclicEvaluator == null) {
		// vertexClassAcyclicEvaluator = new GreqlEvaluator(
		// "isAcyclic(vSubgraph{structure.VertexClass})", sg, null);
		// }
		// vertexClassAcyclicEvaluator.startEvaluation();
		// return vertexClassAcyclicEvaluator.getEvaluationResult().toBoolean();
	}

	/**
	 * Removes empty {@link Package} objects from the {@link SchemaGraph}.
	 */
	private void removeEmptyPackages() {
		// remove all empty packages except the default package
		System.out.println("Removing empty packages...");
		Package p = sg.getFirstPackage();
		while (p != null) {
			Package n = p.getNextPackage();
			int commentCount = p.getDegree(Annotates_annotatedElement.class);
			if ((p.getDegree() - commentCount == 1)
					&& (p.get_qualifiedName().length() > 0)) {
				System.out
						.println("\t- empty package '"
								+ p.get_qualifiedName()
								+ "' removed"
								+ (commentCount > 0 ? commentCount == 1 ? " including 1 comment"
										: " including " + commentCount
												+ " comments"
										: ""));
				if (commentCount > 0) {
					for (Incidence i = p
							.getFirstIncidence(Annotates_annotatedElement.class); i != null; i = p
							.getFirstIncidence(Annotates_annotatedElement.class)) {
						((Annotates) i.getEdge()).getAlpha().delete();
					}
				}
				p.delete();
				// start over to capture packages that become empty after
				// deletion of p
				p = sg.getFirstPackage();
			} else {
				p = n;
			}
		}
	}

	/**
	 * Handles a {@link Constraint} by adding it to a preliminary {@link Map} of
	 * Constraints and their ids.
	 * 
	 * @param text
	 *            Constraint as {@link String}.
	 * @param line
	 *            Line number, at which the current Constraint has been found.
	 *            Only needed for exception purposes.
	 * @throws XMLStreamException
	 */
	private void handleConstraint(String text) throws XMLStreamException {
		if (text.startsWith("redefines") || text.startsWith("\"")
				|| text.startsWith("subsets") || text.startsWith("kappa")) {
			List<String> l = constraints.get(constrainedElementId);
			if (l == null) {
				l = new LinkedList<String>();
				constraints.put(constrainedElementId, l);
			}
			l.add(text);
		} else if (text.startsWith("union")) {
			System.err
					.println("warning: {union} constraint at element "
							+ constrainedElementId
							+ " ignored (don't forget to add an <<abstract>> stereotype to the association)");
		} else if (text.startsWith("ordered")) {
			System.err.println("warning: {ordered} constraint at element "
					+ constrainedElementId
					+ " ignored (TGraphs are ordered by default)");
		} else {
			throw new ProcessingException(getFileName(), getParser()
					.getLocation().getLineNumber(),
					"Illegal constraint format: " + text);
		}
	}

	/**
	 * Adds redefines or subsets constraint {@link String} objects to a specific
	 * {@link Edge}.
	 * 
	 * @param constrainedEnd
	 *            Edge, to which all redefinesConstraint String objects will be
	 *            added.
	 * @param text
	 *            Redefined or subsetted constraint String, which can contain
	 *            multiple constraints.
	 * @param constrainedElementId
	 * @throws XMLStreamException
	 */
	private void addRedefinesOrSubsetsConstraint(IncidenceClass constrainedEnd,
			String text, String constrainedElementId) throws XMLStreamException {
		text = text.trim().replaceAll("\\s+", " ");

		/*
		 * typeOfConstraing == 1 => redefines typeOfConstraing == 2 => subsets
		 * otherwise => neither redefines nor subsets
		 */
		byte typeOfConstraint = 0;
		if (text.startsWith("redefines ")) {
			typeOfConstraint = 1;
		} else if (text.startsWith("subsets ")) {
			typeOfConstraint = 2;
		} else {
			throw new ProcessingException(getFileName(),
					"Wrong redefines or subsets constraint format.");
		}
		assert typeOfConstraint == 1 || typeOfConstraint == 2;

		String[] roles = text.substring(typeOfConstraint == 1 ? 10 : 8).split(
				"\\s*,\\s*");

		// String array of 'roles' must not be empty.
		if (roles.length < 1) {
			throw new ProcessingException(getFileName(),
					(typeOfConstraint == 1 ? "Redefines" : "Subsets")
							+ " constraint without rolenames");
		}
		Set<String> affectedRoles = new TreeSet<String>();
		for (String role : roles) {

			// A role String must not be empty.
			if (role.length() < 1) {
				throw new ProcessingException(getFileName(),
						"Empty role name in "
								+ (typeOfConstraint == 1 ? "redefines"
										: "subsets") + " constraint");
			}
			affectedRoles.add(role);
		}

		// At least one affected role must have been added.
		if (affectedRoles.size() < 1) {
			throw new ProcessingException(getFileName(),
					(typeOfConstraint == 1 ? "Redefines" : "Subsets")
							+ " constraint without rolenames");
		}

		// remember the set of redefined or subsetted role names
		Set<String> oldAffectedRoles = (typeOfConstraint == 2 ? subsets
				: (idsOfOldIncidenceclassAtNewEdgeClass
						.contains(constrainedElementId) ? redefinesAtEdge
						: redefinesAtVertex)).getMark(constrainedEnd);
		if (oldAffectedRoles == null) {
			(typeOfConstraint == 2 ? subsets
					: (idsOfOldIncidenceclassAtNewEdgeClass
							.contains(constrainedElementId) ? redefinesAtEdge
							: redefinesAtVertex)).mark(constrainedEnd,
					affectedRoles);
		} else {
			oldAffectedRoles.addAll(affectedRoles);
		}
	}

	/**
	 * Adds a Greql constraint to a {@link AttributedElementClass} object.
	 * 
	 * @param constrainedClass
	 *            {@link AttributedElementClass}, which should be constraint.
	 * @param text
	 *            Constraint as String.
	 * @throws XMLStreamException
	 */
	private void addGreqlConstraint(AttributedElementClass constrainedClass,
			String text) throws XMLStreamException {

		assert constrainedClass != null;
		Constraint constraint = sg.createConstraint();
		sg.createHasConstraint(constrainedClass, constraint);

		// the "text" must contain 2 or 3 space-separated quoted ("...") strings
		int stringCount = 0;
		char[] ch = text.toCharArray();
		boolean inString = false;
		boolean escape = false;
		int beginIndex = 0;
		for (int i = 0; i < ch.length; ++i) {
			char c = ch[i];
			if (inString) {
				if (c == '\\') {
					escape = true;
				} else if (!escape && (c == '"')) {
					++stringCount;
					String constraintText = text.substring(beginIndex + 1, i)
							.trim().replaceAll("\\\\(.)", "$1");
					if (constraintText.isEmpty()) {
						constraintText = null;
					}
					switch (stringCount) {
					case 1:
						constraint.set_message(constraintText);
						break;
					case 2:
						constraint.set_predicateQuery(constraintText);
						break;
					case 3:
						constraint.set_offendingElementsQuery(constraintText);
						break;
					default:
						throw new ProcessingException(getFileName(),
								"Illegal constraint format. The constraint text was '"
										+ text + "'.");
					}
					inString = false;
				} else if (escape && (c == '"')) {
					escape = false;
				}
			} else {
				if (Character.isWhitespace(c)) {
					// ignore
				} else {
					if (c == '"') {
						inString = true;
						beginIndex = i;
					} else {
						throw new ProcessingException(getFileName(),
								"Illegal constraint format. The constraint text was '"
										+ text + "'.  Expected '\"' but got '"
										+ c + "'.  (position = " + i + ").");
					}
				}
			}
		}
		if (inString || escape || (stringCount < 2) || (stringCount > 3)) {
			throw new ProcessingException(getFileName(),
					"Illegal constraint format.  The constraint text was '"
							+ text + "'.");
		}
	}

	/**
	 * Sets the upper bound of the multiplicity of an {@link Edge} as the 'max'
	 * value of the current 'from' or 'to' Edge.
	 * 
	 * @throws XMLStreamException
	 */
	private void handleUpperValue() throws XMLStreamException {
		assert (currentAssociationEnd != null) || inOwnedAttribute;
		int n = getValue();
		if ((currentAssociationEnd == null) && inOwnedAttribute) {
			if (n != 1) {
				throw new ProcessingException(getFileName(),
						"grUML does not support attribute multiplicities other than 1..1");
			}
		} else {
			assert currentAssociationEnd != null;
			assert n >= 1;
			currentAssociationEnd.set_maxEdgesAtVertex(n);
			currentAssociationEnd.set_maxVerticesAtEdge(1);
		}
	}

	/**
	 * Retrieves the value of the 'value' attribute of the current XML element
	 * and returns it.
	 * 
	 * @throws XMLStreamException
	 * @return Retrieved integer value.
	 */
	private int getValue() throws XMLStreamException {
		String val = getAttribute(UML_ATTRIBUTE_VALUE);
		return val == null ? 0 : val.equals("*") ? Integer.MAX_VALUE : Integer
				.parseInt(val);
	}

	/**
	 * Sets the lower bound of the multiplicity of an {@link Edge} as the 'min'
	 * value of the current 'from' or 'to' Edge.
	 * 
	 * @throws XMLStreamException
	 */
	private void handleLowerValue() throws XMLStreamException {
		assert (currentAssociationEnd != null) || inOwnedAttribute;
		int n = getValue();
		if ((currentAssociationEnd == null) && inOwnedAttribute) {
			if (n != 1) {
				throw new ProcessingException(getFileName(),
						"grUML does not support attribute multiplicities other than 1..1");
			}
		} else {
			assert currentAssociationEnd != null;
			assert n >= 0;
			currentAssociationEnd.set_minEdgesAtVertex(n);
			currentAssociationEnd.set_minVerticesAtEdge(1);
		}
	}

	/**
	 * Handles the stereotypes '&lt;&lt;graphclass&gt;&gt;',
	 * '&lt;&lt;record&gt;&gt;', '&lt;&lt;edge&gt;&gt;',
	 * '&lt;&lt;nested&gt;&gt;' and '&lt;&lt;abstract&gt;&gt;' by taking the
	 * appropriate action for every stereotype.
	 * 
	 * '&lt;&lt;graphclass&gt;&gt;': The GraphClass will get the qualified name
	 * and all edge of the stereotyped class. The stereotyped class will be
	 * deleted.
	 * 
	 * '&lt;&lt;record&gt;&gt;': A RecordDomain will be created and the
	 * qualified name and all attributes will be transfered to it. The
	 * stereotyped class will be deleted.
	 * 
	 * '&lt;&lt;edge&gt;&gt;': An EdgeClass will be created out of a VertexClass
	 * marked with this stereotype. The marked VertexClass will be deleted.
	 * 
	 * '&lt;&lt;abstract&gt;&gt;': The stereotype will be set to abstract.
	 * 
	 * '&lt;&lt;nested&gt;&gt;': An Association with this stereotype will be
	 * tranformed into a MayBeNestedIn edge. The stereotyped EdgeClass will be
	 * deleted.
	 * 
	 * @throws XMLStreamException
	 */
	private void handleStereotype() throws XMLStreamException {
		String key = getAttribute(UML_ATTRIBUTE_KEY);

		if ((currentClass == null) && (currentClassId == null)
				&& (currentAssociationEnd == null)
				&& (currentAttribute == null) && (currentRecordDomain == null)
				&& (currentRecordDomainComponent == null)) {
			if (key.equals("rsa2tg_ignore")) {
				ignoredPackages.add(packageStack.peek());
				return;
			} else {
				throw new ProcessingException(getParser(), getFileName(),
						"Unexpected stereotype <<" + key + ">>");
			}
		}

		if (currentClass == null) {
			throw new ProcessingException(getParser(), getFileName(),
					"Unexpected stereotype <<" + key + ">>");
		}

		if (key.equals("graphclass")) {
			// convert currentClass to graphClass

			// The stereotype '<<graphclass>>' can only be attached to UML
			// classes.
			if (!(currentClass instanceof VertexClass)) {
				throw new ProcessingException(getParser(), getFileName(),
						"The stereotype '<<graphclass>>' is only valid for a UML class.");
			}

			AttributedElementClass aec = (AttributedElementClass) idMap
					.get(currentClassId);
			assert graphClass != null;
			graphClass.set_qualifiedName(aec.get_qualifiedName());
			Incidence i = aec.getFirstIncidence();
			while (i != null) {
				Incidence n = i.getNextIncidenceAtVertex();
				if (i.getEdge() instanceof ContainsGraphElementClass) {
					i.getEdge().delete();
				} else {
					setIncidentVertex(i, graphClass);
				}
				i = n;
			}
			aec.delete();
			idMap.put(currentClassId, graphClass);
			currentClass = graphClass;

		} else if (key.equals("record")) {
			// convert current class to RecordDomain

			// The stereotype '<<record>>' can only be attached to UML classes.
			if (!(currentClass instanceof VertexClass)) {
				throw new ProcessingException(getParser(), getFileName(),
						"The stereotype '<<record>>' is only allow for UML-classes.");
			}

			RecordDomain rd = sg.createRecordDomain();
			rd.set_qualifiedName(currentClass.get_qualifiedName());
			Incidence i = currentClass.getFirstIncidence();
			while (i != null) {
				Incidence n = i.getNextIncidenceAtVertex();
				if (i.getEdge() instanceof ContainsGraphElementClass) {
					sg.createContainsDomain((Package) i.getThat(), rd);
					i.getEdge().delete();
				} else if (i.getEdge() instanceof HasAttribute) {
					Attribute att = (Attribute) i.getThat();
					Edge d = att.getFirst_attribute(false).getEdge();
					if (d != null) {
						Domain dom = (Domain) i.getThat();
						HasRecordDomainComponent comp = sg
								.createHasRecordDomainComponent(rd, dom);
						comp.set_name(att.get_name());
					} else {
						String typeId = attributeType.getMark(att);

						// There have to be a typeId.
						if (typeId == null) {
							throw new ProcessingException(getParser(),
									getFileName(),
									"No type id has been defined.");
						}
						Domain dom = sg.createStringDomain();
						dom.set_qualifiedName(typeId);
						preliminaryVertices.add(dom);
						HasRecordDomainComponent comp = sg
								.createHasRecordDomainComponent(rd, dom);
						recordComponentType.mark(comp, typeId);
						attributeType.removeMark(att);
					}
					att.delete();
				}
				i = n;
			}
			if (currentClass.getDegree() != 0) {
				throw new ProcessingException(getParser(), getFileName(),
						"The <<record>> class '"
								+ currentClass.get_qualifiedName()
								+ "' must not have any association.");
			}
			domainMap.put(rd.get_qualifiedName(), rd);
			idMap.put(currentClassId, rd);
			currentRecordDomain = rd;
			currentClass.delete();
			currentClass = null;
			currentClassId = null;
		} else if (key.equals("abstract")) {
			setCurrentClassToAbstract();
		} else if (key.equals("edge")) {
			edgeStereotypedVertexClasses.add((VertexClass) currentClass);
		} else if (key.equals("abstract edge")) {
			edgeStereotypedVertexClasses.add((VertexClass) currentClass);
			setCurrentClassToAbstract();
		} else if (key.equals("nested")) {
			isNestingIncidenceOrEdgeClass.mark(currentClass);
		} else if (key.equals("nested edge")) {
			isNestingIncidenceOrEdgeClass.mark(currentClass);
			edgeStereotypedVertexClasses.add((VertexClass) currentClass);
		} else if (key.equals("nested abstract edge")
				|| key.equals("abstract nested edge")) {
			isNestingIncidenceOrEdgeClass.mark(currentClass);
			edgeStereotypedVertexClasses.add((VertexClass) currentClass);
			setCurrentClassToAbstract();
		} else {
			throw new ProcessingException(getParser(), getFileName(),
					"Unexpected stereotype '<<" + key + ">>'.");
		}
	}

	private void setIncidentVertex(Incidence incidence, Vertex newIncidentVertex) {
		((VertexImpl) incidence.getVertex())
				.removeIncidenceFromLambdaSeq((IncidenceImpl) incidence);
		((IncidenceImpl) incidence)
				.setIncidentVertex((VertexImpl) newIncidentVertex);
		if (newIncidentVertex.getLastIncidence() == null) {
			// incidence is the first incidence at newIncidentVertex
			((VertexImpl) newIncidentVertex)
					.setFirstIncidence((IncidenceImpl) incidence);
			((VertexImpl) newIncidentVertex)
					.setLastIncidence((IncidenceImpl) incidence);
		} else {
			((IncidenceImpl) newIncidentVertex.getLastIncidence())
					.setNextIncidenceAtVertex((IncidenceImpl) incidence);
			((IncidenceImpl) incidence)
					.setPreviousIncidenceAtVertex((IncidenceImpl) newIncidentVertex
							.getLastIncidence());
			((VertexImpl) newIncidentVertex)
					.setLastIncidence((IncidenceImpl) incidence);
		}
		((VertexImpl) newIncidentVertex).incidenceListModified();
	}

	/**
	 * Sets {@link #currentClass} to abstract.
	 */
	private void setCurrentClassToAbstract() {
		if (currentClass instanceof GraphElementClass) {
			GraphElementClass gec = (GraphElementClass) currentClass;
			gec.set_abstract(true);
		} else {
			throw new ProcessingException(
					getParser(),
					getFileName(),
					"The stereotype <<abstract>> can only be specified for vertex and edge classes, but not for class '"
							+ currentClass.get_qualifiedName() + "'");
		}
	}

	/**
	 * Handles a 'generalization' XML element by marking the current class.
	 * 
	 * @param parser
	 *            {@link XMLStreamReader}, which points to the current XML
	 *            element.
	 */
	private void handleGeneralization() throws XMLStreamException {
		String general = getAttribute(UML_ATTRIBUTE_GENERAL);
		Set<String> gens = generalizations.getMark(currentClass);
		if (gens == null) {
			gens = new TreeSet<String>();
			generalizations.mark(currentClass, gens);
		}
		gens.add(general);
	}

	/**
	 * Handles a nested 'uml:PrimitivType' XML element by creating a
	 * corresponding {@link Domain}.
	 * 
	 * @param xmiId
	 *            XMI id of corresponding attribute
	 * @throws XMLStreamException
	 */
	private void handleNestedTypeElement(String xmiId)
			throws XMLStreamException {
		if ((currentAttribute == null) && (currentRecordDomain == null)) {
			throw new ProcessingException(getParser(), getFileName(),
					"unexpected primitive type in element (XMI id " + xmiId
							+ ")");
		}
		String href = getAttribute(UML_ATTRIBUTE_HREF);
		if (href == null) {
			throw new ProcessingException(getParser(), getFileName(),
					"No type name specified in primitive type href of attribute (XMI id "
							+ xmiId + ")");
		}
		Domain dom = null;
		if (href.endsWith("#String")) {
			dom = createDomain("String");
		} else if (href.endsWith("#Integer")) {
			dom = createDomain("Integer");
		} else if (href.endsWith("#Boolean")) {
			dom = createDomain("Boolean");
		} else {
			throw new ProcessingException(getParser(), getFileName(),
					"Unknown primitive type with href '" + href
							+ "' in attribute (XMI id " + xmiId + ")");
		}

		assert dom != null;
		if (currentRecordDomain != null) {
			// type of record domain component
			assert currentRecordDomainComponent != null;
			Domain d = (Domain) currentRecordDomainComponent.getOmega();
			assert (d instanceof StringDomain)
					&& (d.get_qualifiedName() == null)
					&& preliminaryVertices.contains(d);
			currentRecordDomainComponent.setOmega(dom);
			d.delete();
			preliminaryVertices.remove(d);
			recordComponentType.removeMark(currentRecordDomainComponent);
		} else {
			// type of an attribute of an AttributedElementClass
			assert currentAttribute != null;
			sg.createHasDomain(currentAttribute, dom);
			attributeType.removeMark(currentAttribute);
		}
	}

	/**
	 * Handles a 'ownedAttribute' XML element of type 'uml:Property' by creating
	 * a {@link Attribute} and linking it with its
	 * {@link AttributedElementClass}.
	 * 
	 * @param parser
	 *            {@link XMLStreamReader}, which points to the current XML
	 *            element.
	 * @param xmiId
	 *            XMI id of the current XML element.
	 */
	private void handleOwnedAttribute(String xmiId) throws XMLStreamException {
		String association = getAttribute(UML_ATTRIBUTE_ASSOCIATION);
		if (association == null) {
			String attrName = getAttribute(UML_ATTRIBUTE_NAME);

			if ((currentClass == null) && (currentRecordDomain == null)) {
				throw new ProcessingException(getParser(), getFileName(),
						"Found an attribute '" + attrName + "' (XMI id "
								+ xmiId + ") outside a class!");
			}

			if (attrName == null) {
				throw new ProcessingException(getParser(), getFileName(),
						"No attribute name in ownedAttribute (XMI id " + xmiId
								+ ")");
			}
			attrName = attrName.trim();

			if (attrName.length() == 0) {
				throw new ProcessingException(getParser(), getFileName(),
						"Empty attribute name in ownedAttribute (XMI id "
								+ xmiId + ")");
			}

			String isDerived = getAttribute(UML_ATTRIBUTE_ISDERIVED);
			boolean derived = (isDerived != null) && isDerived.equals(UML_TRUE);

			if (derived) {
				// ignore derived attributes
				return;
			}

			String typeId = getAttribute(UML_ATTRIBUTE_TYPE);

			if (currentClass != null) {
				// property is an "ordinary" attribute
				Attribute att = sg.createAttribute();
				currentAttribute = att;
				att.set_name(attrName);
				sg.createHasAttribute(currentClass, att);
				if (typeId != null) {
					attributeType.mark(att, typeId);
				}
			} else {
				// property is a record component
				assert currentRecordDomain != null;
				currentAttribute = null;
				currentRecordDomainComponent = null;
				if (typeId != null) {
					Vertex v = idMap.get(typeId);
					if (v != null) {
						assert v instanceof Domain : "typeID says " + typeId
								+ " which is no Domain!";
						currentRecordDomainComponent = sg
								.createHasRecordDomainComponent(
										currentRecordDomain, (Domain) v);
					} else {
						Domain dom = sg.createStringDomain();
						dom.set_qualifiedName(typeId);
						preliminaryVertices.add(dom);
						currentRecordDomainComponent = sg
								.createHasRecordDomainComponent(
										currentRecordDomain, dom);
						recordComponentType.mark(currentRecordDomainComponent,
								typeId);
					}
				} else {
					Domain dom = sg.createStringDomain();
					preliminaryVertices.add(dom);
					currentRecordDomainComponent = sg
							.createHasRecordDomainComponent(
									currentRecordDomain, dom);
				}
				currentRecordDomainComponent.set_name(attrName);
			}
		} else {
			handleAssociationEnd(xmiId);
		}
	}

	/**
	 * Handles a 'ownedEnd' XML element of type 'uml:Property' by creating an
	 * appropriate {@link From} edge.
	 * 
	 * @param xmiId
	 * @throws XMLStreamException
	 */
	private void handleAssociationEnd(String xmiId) throws XMLStreamException {
		String endName = getAttribute(UML_ATTRIBUTE_NAME);
		if ((currentClass == null) || (currentRecordDomain != null)) {
			throw new ProcessingException(getParser(), getFileName(),
					"Found an association end '" + endName + "' (XMI id "
							+ xmiId + ") outside a class or in a record domain");
		}

		String agg = getAttribute(UML_ATTRIBUTE_AGGREGATION);
		boolean aggregation = (agg != null) && agg.equals(UML_SHARED);
		boolean composition = (agg != null) && agg.equals(UML_COMPOSITE);

		// id of the uml:Class of the other side of the association
		String typeId = getAttribute(UML_ATTRIBUTE_TYPE);

		if (typeId == null) {
			throw new ProcessingException(getParser(), getFileName(),
					"No type attribute in association end (XMI id" + xmiId
							+ ")");
		}

		IncidenceClass inc = (IncidenceClass) idMap.get(xmiId);
		if (inc == null) {
			// try to find the end's VertexClass
			// if not found, create a preliminary VertexClass
			VertexClass vc = null;

			// vertex class id is in "type" attribute

			AttributedElement<?, ?> ae = idMap.get(typeId);
			if (ae != null) {
				if (VertexClass.class.isInstance(ae)) {
					// VertexClass found
					vc = (VertexClass) ae;
				} else if (EdgeClass.class.isInstance(ae)) {
					if (aggregation) {
						throw new ProcessingException(
								getParser(),
								getFileName(),
								"Type attribute of association end (XMI id "
										+ xmiId
										+ ") is a "
										+ ae.getType().getQualifiedName()
										+ ". That's why this association end must belong to a composition.");
					}
					// TODO
				} else {
					throw new ProcessingException(
							getParser(),
							getFileName(),
							"Type attribute of association end (XMI id "
									+ xmiId
									+ ") must denote a VertexClass or Composition EdgeClass, but is "
									+ ae.getType().getQualifiedName());
				}
			} else {
				// create a preliminary vertex class
				vc = sg.createVertexClass();
				vc.set_qualifiedName(typeId);
				vc.set_minKappa(0);
				vc.set_maxKappa(Integer.MAX_VALUE);
				preliminaryVertices.add(vc);
				idMap.put(typeId, vc);
			}

			// try to find the end's EdgeClass
			EdgeClass ec = null;
			if (EdgeClass.class.isInstance(currentClass)) {
				// we have an "ownedEnd", so the end's Edge is the
				// currentClass
				ec = (EdgeClass) currentClass;
				idMap.put(currentClassId, currentClass);
			} else {
				// we have an ownedAttribute
				// edge class id is in "association"
				String associationId = getAttribute(UML_ATTRIBUTE_ASSOCIATION);

				if (associationId == null) {
					throw new ProcessingException(getParser(), getFileName(),
							"No assiocation attribute in association end (XMI id "
									+ xmiId + ")");
				}
				ae = idMap.get(associationId);

				if (ae != null) {
					if (!(ae instanceof EdgeClass)) {
						throw new ProcessingException(getParser(),
								getFileName(),
								"Assiocation attribute of association end (XMI id "
										+ xmiId
										+ ") must denote an EdgeClass, but is "
										+ ae.getType().getQualifiedName());
					}
					// EdgeClass found
					ec = (EdgeClass) ae;
				} else {
					// create a preliminary edge class
					ec = sg.createEdgeClass();
					ec.set_minKappa(0);
					ec.set_maxKappa(Integer.MAX_VALUE);
				}

				preliminaryVertices.add(ec);
				idMap.put(associationId, ec);
			}

			inc = sg.createIncidenceClass();
			inc.set_minEdgesAtVertex(DEFAULT_MIN_MULTIPLICITY);
			inc.set_maxEdgesAtVertex(DEFAULT_MAX_MULTIPLICITY);
			inc.set_minVerticesAtEdge(DEFAULT_MIN_MULTIPLICITY);
			inc.set_maxVerticesAtEdge(DEFAULT_MAX_MULTIPLICITY);
			inc.set_abstract(false);
			inc.set_direction(Direction.VERTEX_TO_EDGE);
			assert ec != null;
			if (vc != null) {
				sg.createConnectsToVertexClass(inc, vc);
			} else {
				wrongEdgeClasses.add(ec);
			}
			sg.createConnectsToEdgeClass(inc, ec);
		} else {
			// at this point the IncidenceClass was already created because the
			// association was seen before via memberEnd
			EdgeClass ec = null;
			for (ConnectsToEdgeClass ctec : inc
					.getIncidentEdges(ConnectsToEdgeClass.class)) {
				ec = (EdgeClass) ctec.getOmega();
				break;
			}
			assert ec != null;

			String id = null;
			for (Entry<String, Vertex> idEntry : idMap.entrySet()) {
				if (idEntry.getValue() == ec) {
					id = idEntry.getKey();
					break;
				}
			}

			assert id != null;
			idMap.put(id, ec);

			// an ownedEnd of an association or an ownedAttribute of a class
			// with a possibly preliminary vertex class
			VertexClass vc = null;
			for (ConnectsToVertexClass ctvc : inc
					.getIncidentEdges(ConnectsToVertexClass.class)) {
				vc = (VertexClass) ctvc.getOmega();
				break;
			}

			if (vc != null && preliminaryVertices.contains(vc)) {

				AttributedElement<?, ?> ae = idMap.get(typeId);

				if ((ae != null) && !vc.equals(ae)) {
					if (VertexClass.class.isInstance(ae)) {
						for (ConnectsToVertexClass ctvc : inc
								.getIncidentEdges(ConnectsToVertexClass.class)) {
							((ConnectsToVertexClassImpl) ctvc)
									.setOmega((VertexClass) ae);
							break;
						}
					} else if (EdgeClass.class.isInstance(ae)) {
						// this IncidenceClass belongs to a composition which
						// has an incident associationClass

						// this association end is a "ownedEnd"

						// ae is the incident associationClass
						// vc is a preliminary VertexClass created at the
						// memberEnd of the parent Association
						wrongEdgeClasses.add(ec);
						updateMayBeNestedIn(vc, (GraphElementClass) ae, ec);
						// TODO
					} else {
						throw new ProcessingException(
								getParser(),
								getFileName(),
								"Type attribute of association end (XMI id "
										+ xmiId
										+ ") must denote a VertexClass, but is "
										+ ae.getType().getQualifiedName());
					}

					for (GraphElement<?, ?, ?> ge : nestedElements
							.getMarkedElements()) {
						Set<GraphElementClass> mark = nestedElements
								.getMark(ge);
						if (ge == vc) {
							nestedElements.mark((GraphElement<?, ?, ?>) ae,
									mark);
							nestedElements.removeMark(vc);
						}
						if (mark.contains(vc)) {
							mark.remove(ae);
						}
					}

					Set<String> gens = generalizations.getMark(vc);
					if (gens != null) {
						generalizations.removeMark(vc);
						generalizations.mark((GraphElement<?, ?, ?>) ae, gens);
					}

					preliminaryVertices.remove(vc);
					vc.delete();
				} else if (ae == null) {
					idMap.put(typeId, vc);
				} else {
					throw new RuntimeException(
							"FIXME: Unexpected type. You should not get here!");
				}
			}
		}

		assert inc != null;
		currentAssociationEnd = inc;
		if (currentClass instanceof EdgeClass) {
			ownedEnds.add(inc);
		}
		inc.set_incidenceType(aggregation ? IncidenceType.AGGREGATION
				: composition ? IncidenceType.COMPOSITION : IncidenceType.EDGE);
		idMap.put(xmiId, inc);
		inc.set_roleName(endName);
		if (!wrongEdgeClasses.contains(getConnectedEdgeClass(inc))) {
			setMayBeNestedInInformation(inc, composition);
		} else {
			System.out.print("\twrongEdgeClasses=");
			prettyPrint2(wrongEdgeClasses);// TODO delete
			System.out.println("\tgetMayBeNestedInRepresentation:");
			prettyPrint(getMayBeNestedInRepresentation);
			try {
				GraphIO.saveGraphToFile("D:\\Beispiele\\_test.dhhtg", sg, null);
			} catch (GraphIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void prettyPrint(
			LocalGenericGraphMarker<MayBeNestedIn> getMayBeNestedInRepresentation2) {
		// TODO delete
		for (GraphElement<?, ?, ?> ge : getMayBeNestedInRepresentation2
				.getMarkedElements()) {
			GraphElementClass gec = (GraphElementClass) ge;
			System.out.print(gec.get_qualifiedName() + " = ");
			MayBeNestedIn mbni = getMayBeNestedInRepresentation2.getMark(gec);
			System.out
					.println(((GraphElementClass) mbni.getAlpha())
							.get_qualifiedName()
							+ "-->"
							+ ((GraphElementClass) mbni.getOmega())
									.get_qualifiedName());
		}
	}

	private void prettyPrint2(Set<EdgeClass> wrongEdgeClasses2) {
		// TODO Delete
		System.out.print("{");
		String delim = "";
		for (EdgeClass ec : wrongEdgeClasses2) {
			System.out.print(delim + ec.get_qualifiedName());
			delim = ", ";
		}
		System.out.print("}\n");
	}

	private void updateMayBeNestedIn(GraphElementClass preliminary,
			GraphElementClass real, EdgeClass oldEC) {
		MayBeNestedIn mbni = getMayBeNestedInRepresentation.getMark(oldEC);
		if (mbni == null) {
			IncidenceClass ic = null;
			for (ConnectsToEdgeClass_connectedEdgeClass i : oldEC
					.getIncidences(ConnectsToEdgeClass_connectedEdgeClass.class)) {
				ic = (IncidenceClass) i.getThat();
				if (getConnectedVertexClass(ic) == preliminary) {
					if (ic.get_incidenceType() == IncidenceType.COMPOSITION) {
						// TODO
						mbni = sg.createMayBeNestedIn(real,
								preliminaryMayBeNestedInVertexClass);
					} else {
						mbni = sg.createMayBeNestedIn(
								preliminaryMayBeNestedInVertexClass, real);
					}
				}
			}
		} else {
			if (mbni.getAlpha() == preliminary) {
				mbni = sg.createMayBeNestedIn(real,
						(GraphElementClass) mbni.getOmega());
			} else {
				assert mbni.getOmega() == preliminary;
				mbni = sg.createMayBeNestedIn(
						(GraphElementClass) mbni.getAlpha(), real);
			}
		}
		getMayBeNestedInRepresentation.mark(oldEC, mbni);
	}

	private void setMayBeNestedInInformation(IncidenceClass inc,
			boolean isIncidenceTypeSetAtTheWrongIncidenceClass) {

		EdgeClass compositionEC = getConnectedEdgeClass(inc);

		if (!isIncidenceTypeSetAtTheWrongIncidenceClass) {
			boolean isComposition = false;
			GraphElementClass containingGEC = null;
			GraphElementClass containedGEC = null;
			for (Incidence i : compositionEC
					.getIncidences(ConnectsToEdgeClass_connectedEdgeClass.class)) {
				IncidenceClass ic = (IncidenceClass) i.getThat();
				if (ic.get_incidenceType() == IncidenceType.COMPOSITION) {
					// this end of the edge has the composition attribute
					isComposition = true;
					assert containedGEC == null;
					containedGEC = getConnectedVertexClass(ic);
				} else {
					// this end of the edge has no composition attribute
					assert !isComposition || containingGEC == null;
					containingGEC = getConnectedVertexClass(ic);
				}
			}

			if (isComposition) {
				assert containingGEC != null && containedGEC != null;
				Set<GraphElementClass> containedElements = nestedElements
						.getMark(containingGEC);
				if (containedElements == null) {
					containedElements = new HashSet<GraphElementClass>();
					nestedElements.mark(containingGEC, containedElements);
				}
				if (containedGEC != null) {
					containedElements.add(containedGEC);
				}
				// printNestedElements(nestedElements);// TODO delete
			}
		}
	}

	// TODO delete this method
	private void printNestedElements(
			LocalGenericGraphMarker<Set<GraphElementClass>> nestedElements) {
		for (GraphElement<?, ?, ?> elem : nestedElements.getMarkedElements()) {
			GraphElementClass e = (GraphElementClass) elem;
			System.out.print(e.get_qualifiedName() + "=");
			prettyPrint(nestedElements.getMark(e));
		}
	}

	// TODO delete this method
	private void prettyPrint(Collection<GraphElementClass> set) {
		System.out.print("{");
		String delim = "";
		for (GraphElementClass gec : set) {
			System.out.print(delim + gec.get_qualifiedName());
			delim = ", ";
		}
		System.out.print("}\n");
	}

	/**
	 * Reconnects all edges of an <code>oldVertex</code> to
	 * <code>newVertex</code>.
	 * 
	 * @param oldVertex
	 *            Old {@link Vertex}, of which all edge should be reattached.
	 * @param newVertex
	 *            New {@link Vertex}, to which all edge should be attached.
	 */
	private void reconnectEdges(Vertex oldVertex, Vertex newVertex) {
		Incidence curr = oldVertex.getFirstIncidence();
		while (curr != null) {
			Incidence next = curr.getNextIncidenceAtVertex();
			setIncidentVertex(curr, newVertex);
			curr = next;
		}
	}

	/**
	 * Creates a Domain vertex corresponding to the specified
	 * <code>typeName</code>.
	 * 
	 * This vertex can also be a preliminary vertex which has to be replaced by
	 * the correct Domain later. In this case, there is no "ContainsDomain"
	 * edge, and the type is "StringDomain".
	 * 
	 * @param typeName
	 *            Describes the Domain, which should be created.
	 * @return Created Domain.
	 */
	private Domain createDomain(String typeName) {
		Domain dom = domainMap.get(typeName);
		if (dom != null) {
			return dom;
		}

		if (typeName.equals("String")) {
			dom = sg.createStringDomain();
		} else if (typeName.equals("Integer")) {
			dom = sg.createIntegerDomain();
		} else if (typeName.equals("Double")) {
			dom = sg.createDoubleDomain();
		} else if (typeName.equals("Long")) {
			dom = sg.createLongDomain();
		} else if (typeName.equals("Boolean")) {
			dom = sg.createBooleanDomain();
		} else if (typeName.startsWith("Map<") && typeName.endsWith(">")) {
			dom = sg.createMapDomain();
			String keyValueDomains = typeName.substring(4,
					typeName.length() - 1);
			char[] c = keyValueDomains.toCharArray();
			// find the delimiting ',' and take into account nested domains
			int p = 0;
			for (int i = 0; i < c.length; ++i) {
				if ((c[i] == ',') && (p == 0)) {
					p = i;
					break;
				}
				if (c[i] == '<') {
					++p;
				} else if (c[i] == '>') {
					--p;
				}
				if (p < 0) {
					throw new ProcessingException(getFileName(),
							"Error in primitive type name: '" + typeName + "'");
				}
			}

			if ((p <= 0) || (p >= c.length - 1)) {
				throw new ProcessingException(getFileName(),
						"Error in primitive type name: '" + typeName + "'");
			}
			String keyDomainName = keyValueDomains.substring(0, p);
			Domain keyDomain = createDomain(keyDomainName);
			assert keyDomain != null;

			String valueDomainName = keyValueDomains.substring(p + 1);
			Domain valueDomain = createDomain(valueDomainName);
			assert valueDomain != null;

			sg.createHasKeyDomain((MapDomain) dom, keyDomain);
			sg.createHasValueDomain((MapDomain) dom, valueDomain);

			// Adds a space between
			typeName = "Map<" + keyDomainName + ", " + valueDomainName + '>';

		} else if (typeName.startsWith("List<") && typeName.endsWith(">")) {
			dom = sg.createListDomain();
			String compTypeName = typeName.substring(5, typeName.length() - 1);
			Domain compDomain = createDomain(compTypeName);
			assert compDomain != null;

			sg.createHasBaseDomain((CollectionDomain) dom, compDomain);
		} else if (typeName.startsWith("Set<") && typeName.endsWith(">")) {
			dom = sg.createSetDomain();
			String compTypeName = typeName.substring(4, typeName.length() - 1);
			Domain compDomain = createDomain(compTypeName);
			assert compDomain != null;

			sg.createHasBaseDomain((CollectionDomain) dom, compDomain);
		}
		if (dom != null) {
			sg.createContainsDomain(packageStack.get(0), dom);
		} else {
			// there must exist a named domain (Enum or Record)
			// but this was not yet created in the graph
			// create preliminary domain vertex which will
			// later be re-linked and deleted
			dom = sg.createStringDomain();
			preliminaryVertices.add(dom);
		}

		assert dom != null;
		dom.set_qualifiedName(typeName);
		domainMap.put(typeName, dom);
		return dom;
	}

	/**
	 * Returns the qualified name for the simple name <code>simpleName</code>.
	 * The qualified name consists the (already qualified) name of the package
	 * on top of the package stack and the name <code>simpleName</code>,
	 * separated by a dot. If the top package is the default package, the name
	 * <code>simpleName</code> is already the qualified name. If the package
	 * stack is empty
	 * 
	 * @param simpleName
	 *            a simple name of a class or package
	 * @return the qualified name for the simple name
	 */
	private String getQualifiedName(String simpleName) {

		assert simpleName != null;
		simpleName = simpleName.trim();
		Package p = packageStack.peek();

		assert p != null;
		if (p.get_qualifiedName() == null || p.get_qualifiedName().equals("")) {
			return simpleName;
		} else {
			return p.get_qualifiedName() + "." + simpleName;
		}
	}

	/**
	 * <code>true</code> indicates, that the roles from {@link From} edges
	 * should be used.
	 * 
	 * @param useFromRole
	 *            Value for the <code>useFromRole</code> flag.
	 */
	public void setUseFromRole(boolean useFromRole) {
		this.useFromRole = useFromRole;
	}

	/**
	 * Will return <code>true</code>, if the roles from the {@link From} edge
	 * should be used.
	 * 
	 * @return Value of the <code>useFromRole</code> flag.
	 */
	public boolean isUseFromRole() {
		return useFromRole;
	}

	/**
	 * <code>true</code> forces the removal of all unlinked {@link Domain}
	 * objects.
	 * 
	 * @param removeUnusedDomains
	 *            Value of the <code>removeUnusedDomain</code> flag.
	 */
	public void setRemoveUnusedDomains(boolean removeUnusedDomains) {
		this.removeUnusedDomains = removeUnusedDomains;
	}

	/**
	 * Will return <code>true</code>, if unlinked {@link Domain} objects should
	 * be removed in the last processing step.
	 * 
	 * @return Value of the <code>removeUnusedDoimain</code> flag.
	 */
	public boolean isRemoveUnusedDomains() {
		return removeUnusedDomains;
	}

	/**
	 * <code>true</code> indicates, that the navigability of edges should be
	 * used.
	 * 
	 * @param useNavigability
	 *            Value for the <code>useNavigability</code> flag.
	 */
	public void setUseNavigability(boolean useNavigability) {
		this.useNavigability = useNavigability;
	}

	/**
	 * Will return <code>true</code>, if the navigability of edges should be
	 * used.
	 * 
	 * @return Value of the <code>useNavigability</code> flag.
	 */
	public boolean isUseNavigability() {
		return useNavigability;
	}

	/**
	 * Returns the {@link SchemaGraph}, which has been created after executing
	 * {@link Rsa2Tg#process(String)}.
	 * 
	 * @return Created SchemaGraph.
	 */
	public SchemaGraph getSchemaGraph() {
		return sg;
	}

	/**
	 * Determines whether or not all output will be suppressed.
	 * 
	 * @param suppressOutput
	 *            Value for the <code>suppressOutput</code> flag.
	 */
	public void setSuppressOutput(boolean suppressOutput) {
		this.suppressOutput = suppressOutput;
	}

	/**
	 * Returns the file name of the TG Schema file.
	 * 
	 * @return File name as {@link String}.
	 */
	public String getFilenameSchema() {
		return filenameSchema;
	}

	/**
	 * Sets the file name of the TG Schema file.
	 * 
	 * @param filenameSchema
	 *            File name as {@link String}.
	 */
	public void setFilenameSchema(String filenameSchema) {
		this.filenameSchema = filenameSchema;
	}

	/**
	 * Returns the file name of the TG grUML SchemaGraph file.
	 * 
	 * @return File name as {@link String}.
	 */
	public String getFilenameSchemaGraph() {
		return filenameSchemaGraph;
	}

	/**
	 * Sets the file name of the TG grUML SchemaGraph file.
	 * 
	 * @param filenameSchemaGraph
	 *            file name as {@link String}.
	 */
	public void setFilenameSchemaGraph(String filenameSchemaGraph) {
		this.filenameSchemaGraph = filenameSchemaGraph;
	}

	/**
	 * Returns the file name of the DOT file.
	 * 
	 * @return File name as {@link String}.
	 */
	public String getFilenameDot() {
		return filenameDot;
	}

	/**
	 * Sets the file name of the DOT file.
	 * 
	 * @param filenameDot
	 *            File name as {@link String}.
	 */
	public void setFilenameDot(String filenameDot) {
		this.filenameDot = filenameDot;
	}

	/**
	 * Returns the file name of the HTML validation file.
	 * 
	 * @return File name as {@link String}.
	 */
	public String getFilenameValidation() {
		return filenameValidation;
	}

	/**
	 * Sets the file name of the HTML validation file.
	 * 
	 * @param filenameValidation
	 *            File name as {@link String}.
	 */
	public void setFilenameValidation(String filenameValidation) {
		this.filenameValidation = filenameValidation;
	}

	public boolean isKeepEmptyPackages() {
		return keepEmptyPackages;
	}

	public void setKeepEmptyPackages(boolean removeEmptyPackages) {
		this.keepEmptyPackages = removeEmptyPackages;
	}

}
