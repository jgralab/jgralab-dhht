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
package de.uni_koblenz.jgralab.utilities.tg2schemagraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.schema.HasAttribute;
import de.uni_koblenz.jgralab.grumlschema.SchemaGraph;
import de.uni_koblenz.jgralab.grumlschema.domains.CollectionDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.Domain;
import de.uni_koblenz.jgralab.grumlschema.domains.EnumDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.HasBaseDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.HasKeyDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.HasRecordDomainComponent;
import de.uni_koblenz.jgralab.grumlschema.domains.HasValueDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.MapDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.RecordDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.SetDomain;
import de.uni_koblenz.jgralab.grumlschema.impl.mem.structure.ConstraintImpl;
import de.uni_koblenz.jgralab.grumlschema.impl.mem.structure.IncidenceClassImpl;
import de.uni_koblenz.jgralab.grumlschema.impl.mem.structure.SchemaImpl;
import de.uni_koblenz.jgralab.grumlschema.structure.Annotates;
import de.uni_koblenz.jgralab.grumlschema.structure.Annotates_annotatedElement;
import de.uni_koblenz.jgralab.grumlschema.structure.Attribute;
import de.uni_koblenz.jgralab.grumlschema.structure.BinaryEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Comment;
import de.uni_koblenz.jgralab.grumlschema.structure.ConnectsToEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.ConnectsToVertexClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Constraint;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsDefaultPackage;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsDomain;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsGraphElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsSubPackage;
import de.uni_koblenz.jgralab.grumlschema.structure.DefinesGraphClass;
import de.uni_koblenz.jgralab.grumlschema.structure.EdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.GraphClass;
import de.uni_koblenz.jgralab.grumlschema.structure.GraphElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.HasAttribute_hasAttribute_ComesFrom_AttributedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.HasConstraint;
import de.uni_koblenz.jgralab.grumlschema.structure.HasDomain;
import de.uni_koblenz.jgralab.grumlschema.structure.HidesIncidenceClassAtEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.HidesIncidenceClassAtVertexClass;
import de.uni_koblenz.jgralab.grumlschema.structure.IncidenceClass;
import de.uni_koblenz.jgralab.grumlschema.structure.MayBeNestedIn;
import de.uni_koblenz.jgralab.grumlschema.structure.NamedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Schema;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesIncidenceClass;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesVertexClass;
import de.uni_koblenz.jgralab.grumlschema.structure.VertexClass;
import de.uni_koblenz.jgralab.schema.RecordDomain.RecordComponent;
import de.uni_koblenz.jgralab.schema.impl.GraphElementClassImpl;


/**
 * Converts a GrumlSchema SchemaGraph into a Schema.
 * 
 * This class is supposed to be used multiple times, but can only be used once
 * at the same time.
 * 
 * All variables are written like their classes from the package
 * "de.uni_koblenz.jgralab.schema" normal with the exception of the variable for
 * packages. "package" is a keyword. In this case the variable is written with a
 * prefix "x". All variables from the package
 * "de.uni_koblenz.jgralab.grumlschema.structure" are written with an prefix
 * "g".
 * 
 * All types from "de.uni_koblenz.jgralab.schema" are fully qualified with their
 * package name.;
 * 
 * @author ist@uni-koblenz.de, Eckhard Großmann
 */
public class SchemaGraph2Schema {

	private static final Direction OUTGOING = Direction.VERTEX_TO_EDGE;

	/**
	 * New created Schema, which will be returned by the convert method.
	 */
	private de.uni_koblenz.jgralab.schema.Schema xSchema;

	/**
	 * Schema of the SchemaGraph, which will be traversed to created a new
	 * Schema.
	 */
	private Schema gSchema;

	/**
	 * Holds all GraphElementClass objects of the SchemaGraph.
	 */
	private ArrayList<GraphElementClass> gGraphElementClasses;

	/**
	 * Hold all Domain objects of the SchemaGraph.
	 */
	private ArrayList<Domain> gDomains;

	/**
	 * Maps the IncidenceClass objects of the schemagraph to the IncidenceClass
	 * objects of the schema.
	 */
	private HashMap<IncidenceClass, de.uni_koblenz.jgralab.schema.IncidenceClass> incidenceMap;

	/**
	 * Hold all EdgeClass objects of the SchemaGraph.
	 */
	private ArrayList<EdgeClass> gSuperEdgeClasses;

	/**
	 * New created GraphClass of the Schema.
	 */
	private de.uni_koblenz.jgralab.schema.GraphClass graphClass;

	/**
	 * Boolean flag to signal, that work is in progress.
	 */
	private boolean workInProgress = false;

	/**
	 * Empty standard constructor.
	 */
	public SchemaGraph2Schema() {
	}

	/**
	 * Sets all maps up.
	 */
	private void setUp() {

		incidenceMap = new HashMap<IncidenceClass, de.uni_koblenz.jgralab.schema.IncidenceClass>();
		gGraphElementClasses = new ArrayList<GraphElementClass>();
		gDomains = new ArrayList<Domain>();
		gSuperEdgeClasses = new ArrayList<EdgeClass>();
	}

	/**
	 * Deletes every references and frees memory by this.
	 * 
	 * Note:
	 * 
	 * A Garbage Collection is performed with processing the finalization queue!
	 */
	private void tearDown() {

		incidenceMap = null;
		gSchema = null;
		xSchema = null;
		graphClass = null;

		gGraphElementClasses = null;
		gDomains = null;
		gSuperEdgeClasses = null;

		System.gc();
		System.runFinalization();
	}

	/**
	 * Converts a Schema of a given SchemaGraph into a Schema.
	 * 
	 * Note: A Garbage Collection is performed.
	 * 
	 * @param schemaGraph
	 *            SchemaGraph, of which a corresponding Schema should be
	 *            constructed.
	 * @return New Schema.
	 */
	public de.uni_koblenz.jgralab.schema.Schema convert(SchemaGraph schemaGraph) {

		de.uni_koblenz.jgralab.schema.Schema schema = null;
		if (!workInProgress) {
			workInProgress = true;

			setUp();

			createSchema(schemaGraph);

			createGraphClass();

			retrieveAllGraphElementClassesAndDomains();

			createAllDomains();

			createAllGraphElementClasses();

			linkSuperClasses();

			setSigmaValues();

			schema = this.xSchema;

			tearDown();

			workInProgress = false;
		}

		return schema;
	}

	/**
	 * Converts the Schema of a SchemaGraph to a Schema.
	 * 
	 * @param schemaGraph
	 *            SchemaGraph, of which the Schema should be converted to a
	 *            Schema.
	 */
	private void createSchema(SchemaGraph schemaGraph) {

		this.gSchema = schemaGraph.getFirstSchema();
		assert (gSchema != null) : "FIXME! The Schema of the SchemaGraph should be null.";

		// Gets all attributes of the Schema
		String name = gSchema.get_name();
		String packagePrefix = gSchema.get_packagePrefix();
		assert ((name != null) && (packagePrefix != null)) : "One of attributes \"name\" or \"packagePrefix\" is null";

		// Creates a Schema with the given attributes
		xSchema = new SchemaImpl(name, packagePrefix);

		// Check
		assert (xSchema.getName().equals(name) && xSchema.getPackagePrefix()
				.equals(packagePrefix)) : "The attribute \"name\" or \"packagePrefix\" is not equal.";
	}

	/**
	 * Converts a GraphClass of a SchemaGraph to a GraphClass of a Schema.
	 */
	private void createGraphClass() {

		assert (gSchema != null) : "FIXME! The Schema of the SchemaGraph shouldn't be null.";

		// Gets the GraphClass
		DefinesGraphClass definesGraphClass = (DefinesGraphClass) getFirstIncidentEdge(
				gSchema, DefinesGraphClass.class, OUTGOING);
		assert (definesGraphClass != null) : "FIXME! No \"DefinesGraphClass\" edge defined.";
		assert (definesGraphClass.getOmega() instanceof GraphClass) : "FIXME! That is not an instance of \"GraphClass\"";
		GraphClass gGraphClass = (GraphClass) definesGraphClass.getOmega();

		// Creates a new GraphClass of the Schema
		this.graphClass = xSchema.createGraphClass(gGraphClass
				.get_qualifiedName());

		// Sets its attributes and constraints
		createAllAttributes(graphClass, gGraphClass);
		createAllConstraints(graphClass, gGraphClass);
		if (getFirstIncidentEdge(gGraphClass, Annotates.class, Direction.BOTH) != null) {
			graphClass.addComment(createComments(gGraphClass));
		}

		// Check
		assert (graphClass.getQualifiedName().equals(gGraphClass
				.get_qualifiedName())) : "FIXME! The attribute \"qualifiedName\" is different.";
	}

	/**
	 * Retrieves all GraphElementClass and Domain objects and stores them into
	 * the member variable <code>gGraphElementClasses</code>. Only the
	 * DefaultPackage of the SchemaGraph is retrieved and the same methode is
	 * called to with the DefaultPackage.
	 */
	private void retrieveAllGraphElementClassesAndDomains() {

		assert (gSchema != null) : "FIXME! The given Schema of the SchemaGraph shouldn't be null.";

		// Gets the DefaultPackage
		ContainsDefaultPackage containsDefaultPackage = (ContainsDefaultPackage) getFirstIncidentEdge(
				gSchema, ContainsDefaultPackage.class, OUTGOING);
		assert (containsDefaultPackage != null) : "No \"ContainsDefaultPackage\" edge defined.";
		assert (containsDefaultPackage.getOmega() instanceof Package) : "FIXME! That should be an instance of \"Package\".";
		Package defaultPackage = (Package) containsDefaultPackage.getOmega();

		// Starts the recursive collecting process with the DefaultPackage
		getAllGraphElementClassesAndDomains(defaultPackage);
	}

	/**
	 * Retrieves all GraphElementClass and Domain objects and stores them into
	 * the member variables <code>gGraphElementClasses</code> and
	 * <code>gDomains</code>. This method is called recursively by itself with
	 * the subpackages of the current Package object.
	 * 
	 * @param gPackage
	 *            Package, of which all GraphElementClass and Domain objects are
	 *            retrieved.
	 */
	private void getAllGraphElementClassesAndDomains(Package gPackage) {

		// DOMAINS

		getAllDomains(gPackage);

		// GRAPHELEMENTCLASSES

		getAllGraphElementClasses(gPackage);

		// SUBPACKAGES

		// Loop over all subpackages
		for (ContainsSubPackage containsSubPackage : gPackage.getIncidentEdges(
				ContainsSubPackage.class, OUTGOING)) {
			assert ((containsSubPackage != null) && (containsSubPackage
					.getOmega() instanceof Package)) : "FIXME! That should be an instance of Package.";

			// Recursion
			getAllGraphElementClassesAndDomains((Package) containsSubPackage
					.getOmega());
		}
	}

	/**
	 * Retrieves all GraphElementClass objects and stores them into the member
	 * variable <code>gGraphElementClasses</code>.
	 * 
	 * @param gPackage
	 *            Package, of which all GraphElementClass objects are retrieved.
	 */
	private void getAllGraphElementClasses(Package gPackage) {

		// Loop over all GraphElementClass objects of this Package
		for (ContainsGraphElementClass containsGraphElementClass : gPackage
				.getIncidentEdges(ContainsGraphElementClass.class, OUTGOING)) {

			assert ((containsGraphElementClass != null) && (containsGraphElementClass
					.getOmega() instanceof GraphElementClass)) : "FIXME! That should be an instance of GraphElementClass";

			GraphElementClass gGraphElementClass = (GraphElementClass) containsGraphElementClass
					.getOmega();

			// Adds a GraphElementClass object to the ArrayList
			gGraphElementClasses.add(gGraphElementClass);

			// find all EdgeClass objects which have no outgoing
			// SpecializesEdgeClass
			if ((gGraphElementClass instanceof EdgeClass)
					&& (((EdgeClass) gGraphElementClass)
							.getDegree(gGraphElementClass
									.getIncidenceClassForRolename("subclass")) == 0)) {
				gSuperEdgeClasses.add((EdgeClass) gGraphElementClass);
			}
		}
	}

	/**
	 * Retrieves all Domain objects and stores them into the member variable
	 * <code>gDomains</code>.
	 * 
	 * @param gPackage
	 *            Package, of which all Domain objects are retrieved.
	 */
	private void getAllDomains(Package gPackage) {

		// Loop over all Domains of this Package
		for (ContainsDomain containsDomain : gPackage.getIncidentEdges(
				ContainsDomain.class, OUTGOING)) {

			assert (containsDomain.getOmega() instanceof Domain) : "FIXME! That should be an instance of Domain.";

			// Adds a Domain to the ArrayList
			gDomains.add((Domain) containsDomain.getOmega());
		}
	}

	/**
	 * Converts all existing Domain objects in <code>gDomains</code> into
	 * corresponding Domain objects of the Schema.
	 */
	private void createAllDomains() {

		// Loop over all Domain objects
		for (Domain gDomain : gDomains) {

			assert (gDomain != null) : "Domain is null.";
			createDomain(gDomain);
		}
	}

	/**
	 * Converts a given Domain object of the SchemaGraph into corresponding
	 * Domain objects of the Schema.
	 * 
	 * @param gDomain
	 *            Domain, which is converted into a Domain of the Schema.
	 * @return Created Domain.
	 */
	private de.uni_koblenz.jgralab.schema.Domain createDomain(Domain gDomain) {

		// Gets the QualifiedName and tries to query a Domain.
		String qualifiedName = gDomain.get_qualifiedName();
		de.uni_koblenz.jgralab.schema.Domain domain = xSchema
				.getDomain(qualifiedName);

		// In the case, that this Domain isn't found, create a new Domain
		if (domain == null) {
			if (gDomain instanceof EnumDomain) {

				domain = createDomain((EnumDomain) gDomain);

			} else if (gDomain instanceof MapDomain) {

				domain = createDomain((MapDomain) gDomain);

			} else if (gDomain instanceof CollectionDomain) {

				domain = createDomain((CollectionDomain) gDomain);

			} else if (gDomain instanceof RecordDomain) {

				domain = createDomain((RecordDomain) gDomain);
			}
			// set comments
			if (getFirstIncidentEdge(gDomain, Annotates.class, Direction.BOTH) != null) {
				domain.addComment(createComments(gDomain));
			}
		}

		// This case shouldn't happen.
		if (domain == null) {
			throw new GraphException("FIXME! No \"Domain\" has been created.");
		}

		assert (domain.getQualifiedName().equals(qualifiedName)) : "FIXME! The attribute \"QualifiedName\" is different.";

		return domain;
	}

	/**
	 * Creates an String array of all comments.
	 * 
	 * @param gNamedElement
	 * @return String array of all comments
	 */
	private String[] createComments(NamedElementClass gNamedElement) {

		// create array of all comment Strings
		String[] comments = new String[gNamedElement
				.getDegree(Annotates_annotatedElement.class)];
		int i = 0;
		for (Annotates a : gNamedElement.getIncidentEdges(Annotates.class)) {
			Comment comment = (Comment) a.getAlpha();
			assert comment != null : "FIXME! There are no comments.";
			assert i < comments.length : "There are more comments than expected.";
			comments[i++] = comment.get_text();
		}
		return comments;
	}

	/**
	 * Converts a given EnumDomain object of the SchemaGraph into corresponding
	 * EnumDomain objects of the Schema.
	 * 
	 * @param gDomain
	 *            EnumDomain, which is converted into a Domain of the Schema.
	 * @return Created EnumDomain.
	 */
	private de.uni_koblenz.jgralab.schema.Domain createDomain(EnumDomain gDomain) {

		// Creates a EnumDomain
		return xSchema.createEnumDomain(gDomain.get_qualifiedName(),
				gDomain.get_enumConstants());
	}

	/**
	 * Converts a given RecordDomain object of the SchemaGraph into
	 * corresponding RecordDomain objects of the Schema.
	 * 
	 * @param gDomain
	 *            RecordDomain, which is converted into a Domain of the Schema.
	 * @return Created RecordDomain.
	 */
	private de.uni_koblenz.jgralab.schema.Domain createDomain(
			RecordDomain gDomain) {

		// Creates a map of record components
		List<RecordComponent> recordComponents = new ArrayList<RecordComponent>();
		// Loop over all existing record components
		for (HasRecordDomainComponent hasRecordComponent : gDomain
				.getIncidentEdges(HasRecordDomainComponent.class, OUTGOING)) {
			assert ((hasRecordComponent != null) && (hasRecordComponent
					.getOmega() instanceof Domain)) : "FIXME! That should be an instance of Domain.";

			recordComponents.add(new RecordComponent(hasRecordComponent
					.get_name(), queryDomain((Domain) hasRecordComponent
					.getOmega())));
		}

		// Creates a RecordDomain
		return xSchema.createRecordDomain(gDomain.get_qualifiedName(),
				recordComponents);
	}

	/**
	 * Converts a given CollectionDomain object of the SchemaGraph into
	 * corresponding CollectionDomain objects of the Schema.
	 * 
	 * @param gDomain
	 *            CollectionDomain, which is converted into a Domain of the
	 *            Schema.
	 * @return Created CollectionDomain.
	 */
	private de.uni_koblenz.jgralab.schema.Domain createDomain(
			CollectionDomain gDomain) {

		assert (gDomain != null) : "FIXME! The given Domain shouldn't be null.";

		// Gets the BaseDomain
		HasBaseDomain gHasBaseDomain = (HasBaseDomain) getFirstIncidentEdge(
				gDomain, HasBaseDomain.class, OUTGOING);
		assert (gHasBaseDomain != null) : "FIXME! No \"HasBaseDomain\" has been defined.";
		assert (gHasBaseDomain.getOmega() instanceof Domain) : "FIXME! That should be an instance of Domain.";
		Domain base = (Domain) gHasBaseDomain.getOmega();

		// Creates a CollectionDomain
		return (gDomain instanceof SetDomain) ? xSchema
				.createSetDomain(queryDomain(base)) : xSchema
				.createListDomain(queryDomain(base));
	}

	/**
	 * Converts a given MapDomain object of the SchemaGraph into corresponding
	 * MapDomain objects of the Schema.
	 * 
	 * @param gDomain
	 *            MapDomain, which is converted into a Domain of the Schema.
	 * @return Created MapDomain.
	 */
	private de.uni_koblenz.jgralab.schema.Domain createDomain(MapDomain gDomain) {

		Domain key, value;

		// Gets the KeyDomain
		HasKeyDomain gHasKeyDomain = (HasKeyDomain) getFirstIncidentEdge(
				gDomain, HasKeyDomain.class, OUTGOING);
		assert (gHasKeyDomain != null) : "No \"HasKeyDomain\" has been defined.";
		assert (gHasKeyDomain.getOmega() instanceof Domain) : "That should be an instance of Domain.";
		key = (Domain) gHasKeyDomain.getOmega();

		// Gets the ValueDomain
		HasValueDomain gHasValueDomain = (HasValueDomain) getFirstIncidentEdge(
				gDomain, HasValueDomain.class, OUTGOING);
		assert (gHasValueDomain != null) : "No \"HasValueDomain\" has been defined.";
		assert (gHasValueDomain.getOmega() instanceof Domain) : "That should be an instance of Domain.";
		value = (Domain) gHasValueDomain.getOmega();

		// Creates a MapDomain
		return xSchema.createMapDomain(queryDomain(key), queryDomain(value));
	}

	/**
	 * Converts all existing GraphElementClass objects in <code>gDomains</code>
	 * into corresponding GraphElementClass objects of the Schema. VertexClass
	 * objects are converted first and then EdgeClass objects.
	 */
	private void createAllGraphElementClasses() {

		// Loop over all GraphElementClass (only VertexClass objects are used)
		for (GraphElementClass gGraphElementClass : gGraphElementClasses) {

			if (gGraphElementClass instanceof VertexClass) {

				createGraphElementClass(gGraphElementClass);
			}
		}

		// Loop over all GraphElementClass (only EdgeClass objects are used)
		for (GraphElementClass gGraphElementClass : gGraphElementClasses) {

			if (gGraphElementClass instanceof EdgeClass) {

				createGraphElementClass(gGraphElementClass);
			}
		}
	}

	/**
	 * Converts a given GraphElementClass objects into corresponding
	 * GraphElementClass object of the Schema.
	 */
	private de.uni_koblenz.jgralab.schema.GraphElementClass<?, ?> createGraphElementClass(
			GraphElementClass gElement) {

		de.uni_koblenz.jgralab.schema.GraphElementClass<?, ?> element = null;

		if (gElement instanceof VertexClass) {
			// VertexClass is created.
			element = graphClass
					.createVertexClass(gElement.get_qualifiedName());

		} else if (gElement instanceof EdgeClass) {
			// EdgeClass, AggregationClass or CompositionClass is created.

			EdgeClass gEdgeClass = (EdgeClass) gElement;

			// An EdgeClass is created
			element = createEdgeClass(gElement);

			// create Incidences
			for (ConnectsToEdgeClass gctec : gEdgeClass
					.getIncidentEdges(ConnectsToEdgeClass.class)) {
				IncidenceClass gIncidenceClass = (IncidenceClass) gctec
						.getAlpha();
				incidenceMap
						.put(gIncidenceClass,
								createIncidenceClass(
										gIncidenceClass,
										(de.uni_koblenz.jgralab.schema.EdgeClass) element));
			}

		}

		assert (element != null) : "FIXME! No GraphElementClass has been created.";

		// Gets and sets the attribute "isAbstract"
		element.setAbstract(gElement.is_abstract());
		assert (element.isAbstract() == gElement.is_abstract()) : "FIXME! The attribute \"isAbstract\" is not equal.";

		// sets the kappa values
		element.setAllowedKappaRange(gElement.get_minKappa(),
				gElement.get_maxKappa());

		// Sets all Attribute objects
		createAllAttributes(element, gElement);
		// Sets all Constraint objects
		createAllConstraints(element, gElement);

		// set comments
		if (getFirstIncidentEdge(gElement, Annotates.class, Direction.BOTH) != null) {
			element.addComment(createComments(gElement));
		}
		return element;
	}

	private de.uni_koblenz.jgralab.schema.IncidenceClass createIncidenceClass(
			IncidenceClass gIncidenceClass,
			de.uni_koblenz.jgralab.schema.EdgeClass xEdgeClass) {
		de.uni_koblenz.jgralab.schema.VertexClass xVertexClass = queryVertexClass((GraphElementClass) getFirstIncidentEdge(
				gIncidenceClass, ConnectsToVertexClass.class, OUTGOING)
				.getOmega());
		assert xEdgeClass != null : "The edgeclass must not be null";
		assert xVertexClass != null : "The vertexclass must not be null";
		de.uni_koblenz.jgralab.schema.IncidenceClass xIncidenceClass = graphClass
				.createIncidenceClass(xEdgeClass, xVertexClass, gIncidenceClass
						.get_roleName(), gIncidenceClass.is_abstract(),
						gIncidenceClass.get_minEdgesAtVertex(), gIncidenceClass
								.get_maxEdgesAtVertex(), gIncidenceClass
								.get_minVerticesAtEdge(), gIncidenceClass
								.get_maxVerticesAtEdge(),
						de.uni_koblenz.jgralab.Direction
								.valueOf(gIncidenceClass.get_direction()
										.toString()),
						de.uni_koblenz.jgralab.schema.IncidenceType
								.valueOf(gIncidenceClass.get_incidenceType()
										.toString()));
		return xIncidenceClass;
	}

	/**
	 * Converts a given EdgeClass objects with its To and From edge into a
	 * corresponding EdgeClass object of the Schema.
	 */
	private de.uni_koblenz.jgralab.schema.EdgeClass createEdgeClass(
			GraphElementClass gElement) {

		assert (gElement != null) : "The given GraphElementClass of the SchemaGraph is null.";

		// Gets some missing attribute
		String qualifiedName = gElement.get_qualifiedName();

		// create EdgeClass
		de.uni_koblenz.jgralab.schema.EdgeClass xEdgeClass = null;
		if (gElement instanceof BinaryEdgeClass) {
			xEdgeClass = graphClass.createBinaryEdgeClass(qualifiedName);
		} else {
			xEdgeClass = graphClass.createEdgeClass(qualifiedName);
		}
		assert (xEdgeClass != null) : "No EdgeClass was created.";

		return xEdgeClass;
	}

	/**
	 * Converts all Constraint objects of a given GraphClassElement and adds
	 * them to a GraphClassElement of the Schema.
	 * 
	 * @param xElement
	 *            GraphElementClass, to which all converted Constraint objects
	 *            are added.
	 * @param gElement
	 *            GraphElementClass, of which all Constraint objects will be
	 *            converted.
	 */
	private void createAllConstraints(
			de.uni_koblenz.jgralab.schema.AttributedElementClass<?, ?> xElement,
			Vertex gElement) {

		// Loop over all HasConstraint edges
		for (HasConstraint hasConstraint : gElement.getIncidentEdges(
				HasConstraint.class, OUTGOING)) {
			assert ((hasConstraint != null) && (hasConstraint.getOmega() instanceof Constraint)) : "FIXME! That should be an instance of Constraint.";

			// Gets the Constraint
			Constraint constraint = (Constraint) hasConstraint.getOmega();
			// Creates and adds the constraint
			xElement.addConstraint(new ConstraintImpl(constraint.get_message(),
					constraint.get_predicateQuery(), constraint
							.get_offendingElementsQuery()));
		}
	}

	/**
	 * Converts all Attribute objects of a given GraphClassElement and adds them
	 * to a GraphClassElement of the Schema.
	 * 
	 * @param xElement
	 *            GraphElementClass, to which all converted Attribute objects
	 *            are added.
	 * @param gElement
	 *            GraphElementClass, of which all Attributes objects will be
	 *            converted.
	 */
	private void createAllAttributes(
			de.uni_koblenz.jgralab.schema.AttributedElementClass<?, ?> xElement,
			de.uni_koblenz.jgralab.Vertex gElement) {

		// Loop over all HasAttibute edge of a given GraphElementClass
		for (HasAttribute_hasAttribute_ComesFrom_AttributedElementClass gHasAttribute : gElement.getIncidences(
				HasAttribute_hasAttribute_ComesFrom_AttributedElementClass.class)) {

			createAttribute(xElement, gHasAttribute.getEdge());
		}
	}

	private void createAttribute(
			de.uni_koblenz.jgralab.schema.AttributedElementClass<?, ?> xElement,
			HasAttribute gHasAttribute) {
		// Gets the Attribute
		assert ((gHasAttribute != null) && (gHasAttribute.getOmega() instanceof Attribute)) : "That should be an instance of Attribute.";
		Attribute gAttribute = (Attribute) gHasAttribute.getOmega();
		assert (gAttribute.get_name() != null) : "The name of the Attribute is null.";

		// Gets the Domain
		HasDomain gHasDomain = (HasDomain) getFirstIncidentEdge(gAttribute,
				HasDomain.class, OUTGOING);
		assert (gHasDomain != null) : "No \"HasDomain\" edge has been defined.";
		assert (gHasDomain.getOmega() instanceof Domain) : "That should be an instance of Domain.";
		// Creates and adds an Attribute
		xElement.addAttribute(gAttribute.get_name(),
				queryDomain((Domain) gHasDomain.getOmega()),
				gAttribute.get_defaultValue());
	}

	/**
	 * Links all GraphElementClass and IncidenceClass objects with their
	 * superclass.
	 */
	private void linkSuperClasses() {

		// Loop over all GraphElementClass
		for (GraphElementClass gGraphElement : gGraphElementClasses) {

			// Gets a corresponding AttributedElementClass object
			de.uni_koblenz.jgralab.schema.AttributedElementClass<?, ?> element = xSchema
					.getAttributedElementClass(gGraphElement
							.get_qualifiedName());
			assert (element != null) : "FIXME! No AttributedElementClass object found.";

			if (gGraphElement instanceof VertexClass) {
				// VertexClass have to be linked
				assert (element instanceof de.uni_koblenz.jgralab.schema.VertexClass) : "AttributeElementClass object of Schema is not an instance of an VertexClass.";

				// Linking is done in this method
				linkSuperClasses(
						(de.uni_koblenz.jgralab.schema.VertexClass) element,
						(VertexClass) gGraphElement);

			} else {
				// EdgeClass have to be linked
				assert (gGraphElement instanceof EdgeClass) : "GraphElementClass object of the SchemaGraph is not an instance of EdgeClass.";
				assert (element instanceof de.uni_koblenz.jgralab.schema.EdgeClass) : "GraphElementClass object of the Schema is not an instance of EdgeClass.";

				// Linking is done in this method
				linkSuperClasses(
						(de.uni_koblenz.jgralab.schema.EdgeClass) element,
						(EdgeClass) gGraphElement);
			}
		}

		// loop over IncidenceClasses
		for (Entry<IncidenceClass, de.uni_koblenz.jgralab.schema.IncidenceClass> entry : incidenceMap
				.entrySet()) {
			linkSuperClasses(entry.getValue(), entry.getKey());
			linkHiddenEnds(entry.getValue(), entry.getKey());
		}
	}

	private void linkHiddenEnds(
			de.uni_koblenz.jgralab.schema.IncidenceClass xIncidenceClass,
			IncidenceClass gIncidenceClass) {
		for (HidesIncidenceClassAtEdgeClass ghicae : gIncidenceClass
				.getIncidentEdges(HidesIncidenceClassAtEdgeClass.class,
						OUTGOING)) {
			((IncidenceClassImpl) xIncidenceClass)
					.addHiddenRolenameAtEdge(incidenceMap.get(ghicae.getOmega()));
		}
		for (HidesIncidenceClassAtVertexClass ghicav : gIncidenceClass
				.getIncidentEdges(HidesIncidenceClassAtVertexClass.class,
						OUTGOING)) {
			((IncidenceClassImpl) xIncidenceClass)
					.addHiddenRolenameAtVertex(incidenceMap.get(ghicav
							.getOmega()));
		}
	}

	private void linkSuperClasses(
			de.uni_koblenz.jgralab.schema.IncidenceClass xIncidenceClass,
			IncidenceClass gIncidenceClass) {
		for (SpecializesIncidenceClass gsic : gIncidenceClass.getIncidentEdges(
				SpecializesIncidenceClass.class, OUTGOING)) {
			xIncidenceClass.addSuperClass(incidenceMap.get(gsic.getOmega()));
		}
	}

	/**
	 * Links all EdgeClass objects with their superclass.
	 * 
	 * @param edgeClass
	 *            EdgeClass of the Schema, which should be linked with their
	 *            superclass.
	 * @param gEdgeClass
	 *            EdgeClass of the SchemaGraph, which holds the linkage.
	 */
	private void linkSuperClasses(
			de.uni_koblenz.jgralab.schema.EdgeClass edgeClass,
			EdgeClass gEdgeClass) {

		// List of all super classes is created
		ArrayList<de.uni_koblenz.jgralab.schema.EdgeClass> superClasses = new ArrayList<de.uni_koblenz.jgralab.schema.EdgeClass>();

		// Loop over all SpecializesEdgeClass edges
		for (SpecializesEdgeClass specializesEdgeClass : gEdgeClass
				.getIncidentEdges(SpecializesEdgeClass.class, OUTGOING)) {

			// Gets the superclass
			assert ((specializesEdgeClass != null) && (specializesEdgeClass
					.getOmega() instanceof EdgeClass)) : "That should be an instance of EdgeClass.";
			EdgeClass gSuperClass = (EdgeClass) specializesEdgeClass.getOmega();

			// Gets the corresponding superclass
			de.uni_koblenz.jgralab.schema.AttributedElementClass<?, ?> superClass = xSchema
					.getAttributedElementClass(gSuperClass.get_qualifiedName());
			assert (superClass instanceof de.uni_koblenz.jgralab.schema.EdgeClass) : "The retrieved superclass is not an instance of EdgeClass.";
			// Stores the superclass
			superClasses
					.add((de.uni_koblenz.jgralab.schema.EdgeClass) superClass);
		}

		// Loop over all found superclasses
		for (de.uni_koblenz.jgralab.schema.EdgeClass superClass : superClasses) {
			// Adds the superclass
			edgeClass.addSuperClass(superClass);
		}
	}

	/**
	 * Links all VertexClass objects with their superclass.
	 * 
	 * @param edgeClass
	 *            VertexClass of the Schema, which should be linked with their
	 *            superclass.
	 * @param gEdgeClass
	 *            VertexClass of the SchemaGraph, which holds the linkage.
	 */
	private void linkSuperClasses(
			de.uni_koblenz.jgralab.schema.VertexClass vertexClass,
			VertexClass gVertexClass) {

		// List of all super classes is created
		ArrayList<de.uni_koblenz.jgralab.schema.VertexClass> superClasses = new ArrayList<de.uni_koblenz.jgralab.schema.VertexClass>();

		// Loop over all SpecializesEdgeClass edges
		for (SpecializesVertexClass specializesVertexClass : gVertexClass
				.getIncidentEdges(SpecializesVertexClass.class, OUTGOING)) {

			// Gets the superclass
			assert (specializesVertexClass.getOmega() instanceof VertexClass) : "That should be an instance of VertexClass.";
			VertexClass gSuperClass = (VertexClass) specializesVertexClass
					.getOmega();

			// Gets the corresponding superclass
			de.uni_koblenz.jgralab.schema.AttributedElementClass<?, ?> superClass = xSchema
					.getAttributedElementClass(gSuperClass.get_qualifiedName());
			assert (superClass instanceof de.uni_koblenz.jgralab.schema.VertexClass) : "The retrieved superclass is not an instance of VertexClass.";
			// Stores the superclass
			superClasses
					.add((de.uni_koblenz.jgralab.schema.VertexClass) superClass);
		}

		// Loop over all found superclasses
		for (de.uni_koblenz.jgralab.schema.VertexClass superClass : superClasses) {
			// Adds the superclass
			vertexClass.addSuperClass(superClass);
		}
	}

	private void setSigmaValues() {
		for (Edge gEdge : gSchema.getGraph().getEdges(MayBeNestedIn.class)) {
			MayBeNestedIn gMbni = (MayBeNestedIn) gEdge;

			GraphElementClass gContainedVC = (GraphElementClass) gMbni
					.getAlpha();
			GraphElementClass gContainingVC = (GraphElementClass) gMbni
					.getOmega();

			GraphElementClassImpl<?, ?> xContainedVC = (GraphElementClassImpl<?, ?>) xSchema
					.getAttributedElementClass(gContainedVC.get_qualifiedName());
			GraphElementClassImpl<?, ?> xContainingVC = (GraphElementClassImpl<?, ?>) xSchema
					.getAttributedElementClass(gContainingVC
							.get_qualifiedName());

			xContainedVC.addAllowedSigmaClass(xContainingVC);
		}
	}

	private BinaryEdge getFirstIncidentEdge(Vertex gVertex,
			Class<? extends de.uni_koblenz.jgralab.Edge> gEdgeClass,
			Direction direction) {
		for (de.uni_koblenz.jgralab.Edge e : gVertex.getIncidentEdges(
				gEdgeClass, direction)) {
			return (BinaryEdge) e;
		}
		return null;
	}

	/**
	 * Queries the corresponding GraphElementClass in the Schema of a
	 * GraphElementClass in the SchemaGraph.
	 * 
	 * @param gElement
	 *            GraphElementClass, of which the corresponding
	 *            GraphElementClass should be queried.
	 * @return GraphElementClass, which responds to the QualifiedName of the
	 *         given GraphElementClass.
	 */
	private de.uni_koblenz.jgralab.schema.VertexClass queryVertexClass(
			GraphElementClass gElement) {

		// Queries the VertexClass
		de.uni_koblenz.jgralab.schema.AttributedElementClass<?, ?> element = xSchema
				.getAttributedElementClass(gElement.get_qualifiedName());

		// Returns only instances of VertexClass
		return (element instanceof de.uni_koblenz.jgralab.schema.VertexClass) ? (de.uni_koblenz.jgralab.schema.VertexClass) element
				: null;
	}

	/**
	 * Queries the corresponding Domain in the Schema of a Domain in the
	 * SchemaGraph.
	 * 
	 * @param gElement
	 *            Domain, of which the corresponding Domain should be queried.
	 * @return Domain, which responds to the QualifiedName of the given Domain.
	 */
	private de.uni_koblenz.jgralab.schema.Domain queryDomain(Domain gDomain) {

		// Queries the Domain
		de.uni_koblenz.jgralab.schema.Domain domain = xSchema.getDomain(gDomain
				.get_qualifiedName());

		// In the case of no found Domain a new Domain is created.
		if (domain == null) {
			domain = createDomain(gDomain);
		}
		return domain;
	}
}
