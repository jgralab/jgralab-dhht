package de.uni_koblenz.jgralab.utilities.tg2schemagraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.GraphException;
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
import de.uni_koblenz.jgralab.grumlschema.structure.AggregationClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Attribute;
import de.uni_koblenz.jgralab.grumlschema.structure.AttributedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.CompositionClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Constraint;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsDefaultPackage;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsDomain;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsGraphElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsSubPackage;
import de.uni_koblenz.jgralab.grumlschema.structure.DefinesGraphClass;
import de.uni_koblenz.jgralab.grumlschema.structure.EdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.From;
import de.uni_koblenz.jgralab.grumlschema.structure.GraphClass;
import de.uni_koblenz.jgralab.grumlschema.structure.GraphElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.HasAttribute;
import de.uni_koblenz.jgralab.grumlschema.structure.HasConstraint;
import de.uni_koblenz.jgralab.grumlschema.structure.HasDomain;
import de.uni_koblenz.jgralab.grumlschema.structure.Package;
import de.uni_koblenz.jgralab.grumlschema.structure.Schema;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesVertexClass;
import de.uni_koblenz.jgralab.grumlschema.structure.To;
import de.uni_koblenz.jgralab.grumlschema.structure.VertexClass;
import de.uni_koblenz.jgralab.schema.impl.ConstraintImpl;
import de.uni_koblenz.jgralab.schema.impl.SchemaImpl;

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

	private static final EdgeDirection OUTGOING = EdgeDirection.OUT;

	/**
	 * New created Schema, which will be returned by the convert method.
	 */
	private de.uni_koblenz.jgralab.schema.Schema schema;

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

		gGraphElementClasses = new ArrayList<GraphElementClass>();
		gDomains = new ArrayList<Domain>();
	}

	/**
	 * Deletes every references and frees memory by this.
	 * 
	 * Note:
	 * 
	 * A Garbage Collection is performed with processing the finalization queue!
	 */
	private void tearDown() {

		gSchema = null;
		schema = null;
		graphClass = null;

		gGraphElementClasses = null;
		gDomains = null;

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

			RetrieveAllGraphElementClassesAndDomains();

			createAllDomains();

			createAllGraphElementClasses();

			linkSuperClasses();

			schema = this.schema;

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
		String name = gSchema.getName();
		String packagePrefix = gSchema.getPackagePrefix();
		assert (name != null && packagePrefix != null) : "One of attributes \"name\" or \"packagePrefix\" is null";

		// Creates a Schema with the given attributes
		schema = new SchemaImpl(name, packagePrefix);

		// Check
		assert (schema.getName().equals(name) && schema.getPackagePrefix()
				.equals(packagePrefix)) : "The attribute \"name\" or \"packagePrefix\" is not equal.";
	}

	/**
	 * Converts a GraphClass of a SchemaGraph to a GraphClass of a Schema.
	 */
	private void createGraphClass() {

		assert (gSchema != null) : "FIXME! The Schema of the SchemaGraph shouldn't be null.";

		// Gets the GraphClass
		DefinesGraphClass definesGraphClass = gSchema
				.getFirstDefinesGraphClass(OUTGOING);
		assert (definesGraphClass != null) : "FIXME! No \"DefinesGraphClass\" edge defined.";
		assert (definesGraphClass.getThat() instanceof GraphClass) : "FIXME! That is not an instance of \"GraphClass\"";
		GraphClass gGraphClass = (GraphClass) definesGraphClass.getThat();
		assert (definesGraphClass.getNextDefinesGraphClass(OUTGOING) == null) : "FIXME! There is more than one GraphClass defined.";

		// Creates a new GraphClass of the Schema
		this.graphClass = schema.createGraphClass(gGraphClass
				.getQualifiedName());

		// Sets its attributes and constraints
		graphClass.setAbstract(gGraphClass.isIsAbstract());
		createAllAttributes(graphClass, gGraphClass);
		createAllConstraints(graphClass, gGraphClass);

		// Check
		assert (graphClass.getQualifiedName().equals(gGraphClass
				.getQualifiedName())) : "FIXME! The attribute \"qualifiedName\" is different.";
		assert (graphClass.isAbstract() == gGraphClass.isIsAbstract()) : "FIXME! The attribute \"isAbstract\" is different.";
	}

	/**
	 * Retrieves all GraphElementClass and Domain objects and stores them into
	 * the member variable <code>gGraphElementClasses</code>. Only the
	 * DefaultPackage of the SchemaGraph is retrieved and the same methode is
	 * called to with the DefaultPackage.
	 */
	private void RetrieveAllGraphElementClassesAndDomains() {

		assert (gSchema != null) : "FIXME! The given Schema of the SchemaGraph shouldn' t be null.";

		// Gets the DefaultPackage
		ContainsDefaultPackage containsDefaultPackage = gSchema
				.getFirstContainsDefaultPackage(OUTGOING);
		assert (containsDefaultPackage != null) : "No \"ContainsDefaultPackage\" edge defined.";
		assert (containsDefaultPackage.getThat() instanceof Package) : "FIXME! That should be an instance of \"Package\".";
		Package defaultPackage = (Package) containsDefaultPackage.getThat();
		assert (containsDefaultPackage.getNextContainsDefaultPackage(OUTGOING) == null) : "FIXME! There should be only one \"ContainsDefaultPackage\".";

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
		for (ContainsSubPackage containsSubPackage : gPackage
				.getContainsSubPackageIncidences(OUTGOING)) {
			assert (containsSubPackage != null && containsSubPackage.getThat() instanceof Package) : "FIXME! That should be an instance of Package.";

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
				.getContainsGraphElementClassIncidences(OUTGOING)) {

			assert (containsGraphElementClass != null && containsGraphElementClass
					.getThat() instanceof GraphElementClass) : "FIXME! That should be an instance of GraphElementClass";

			// Adds a GraphElementClass object to the ArrayList
			gGraphElementClasses
					.add((GraphElementClass) containsGraphElementClass
							.getThat());
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
		for (ContainsDomain containsDomain : gPackage
				.getContainsDomainIncidences(OUTGOING)) {

			assert (containsDomain.getThat() instanceof Domain) : "FIXME! That should be an instance of Domain.";

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
		String qualifiedName = gDomain.getQualifiedName();
		de.uni_koblenz.jgralab.schema.Domain domain = schema
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
		}

		// This case shouldn't happen.
		if (domain == null) {
			throw new GraphException("FIXME! No \"Domain\" has been created.");
		}

		assert (domain.getQualifiedName().equals(qualifiedName)) : "FIXME! The attribute \"QualifiedName\" is different.";

		return domain;
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
		return schema.createEnumDomain(gDomain.getQualifiedName(), gDomain
				.getEnumConstants());
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
		Map<String, de.uni_koblenz.jgralab.schema.Domain> recordComponents = new HashMap<String, de.uni_koblenz.jgralab.schema.Domain>();

		// Loop over all existing record components
		for (HasRecordDomainComponent hasRecordComponent : gDomain
				.getHasRecordDomainComponentIncidences(OUTGOING)) {
			assert (hasRecordComponent != null && hasRecordComponent.getThat() instanceof Domain) : "FIXME! That should be an instance of Domain.";

			recordComponents.put(hasRecordComponent.getName(),
					queryDomain((Domain) hasRecordComponent.getThat()));
		}

		// Creates a RecordDomain
		return schema.createRecordDomain(gDomain.getQualifiedName(),
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
		HasBaseDomain hasBaseDomain = gDomain.getFirstHasBaseDomain(OUTGOING);
		assert (hasBaseDomain != null) : "FIXME! No \"HasBaseDomain\" has been defined.";
		assert (hasBaseDomain.getThat() instanceof Domain) : "FIXME! That should be an instance of Domain.";
		Domain base = (Domain) hasBaseDomain.getThat();
		assert (hasBaseDomain.getNextHasBaseDomain(OUTGOING) == null) : "FIXME! There is more than one \"HasBaseDomain\" defined.";

		// Creates a CollectionDomain
		return (gDomain instanceof SetDomain) ? schema
				.createSetDomain(queryDomain(base)) : schema
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
		HasKeyDomain hasKeyDomain = gDomain.getFirstHasKeyDomain(OUTGOING);
		assert (hasKeyDomain != null) : "No \"HasKeyDomain\" has been defined.";
		assert (hasKeyDomain.getThat() instanceof Domain) : "That should be an instance of Domain.";
		key = (Domain) hasKeyDomain.getThat();
		assert (hasKeyDomain.getNextHasKeyDomain(OUTGOING) == null) : "There is more than one \"HasKeyDomain\" defined.";

		// Gets the ValueDomain
		HasValueDomain hasValueDomain = gDomain
				.getFirstHasValueDomain(OUTGOING);
		assert (hasValueDomain != null) : "No \"HasValueDomain\" has been defined.";
		assert (hasValueDomain.getThat() instanceof Domain) : "That should be an instance of Domain.";
		value = (Domain) hasValueDomain.getThat();
		assert (hasValueDomain.getNextHasValueDomain(OUTGOING) == null) : "There is more than one \"HasValueDomain\" defined.";

		// Creates a MapDomain
		return schema.createMapDomain(queryDomain(key), queryDomain(value));
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
	private de.uni_koblenz.jgralab.schema.GraphElementClass createGraphElementClass(
			GraphElementClass gElement) {

		de.uni_koblenz.jgralab.schema.GraphElementClass element = null;

		if (gElement instanceof VertexClass) {
			// VertexClass is created.
			element = graphClass.createVertexClass(gElement.getQualifiedName());

		} else if (gElement instanceof EdgeClass) {
			// EdgeClass, AggregationClass or CompositionClass is created.

			EdgeClass gEdgeClass = (EdgeClass) gElement;

			// To and From edges are retrieved
			To to = gEdgeClass.getFirstTo(OUTGOING);
			From from = gEdgeClass.getFirstFrom(OUTGOING);

			assert (to != null && from != null) : "No \"To\" or \"From\" edge has been defined.";
			// An EdgeClass is created
			element = createEdgeClass(gElement, to, from);

			// Only one To and one From edge should be defined.
			assert (to.getNextTo(OUTGOING) == null && from
					.getNextFrom(OUTGOING) == null) : "There is more than one To or From edge defined.";
		}

		assert (element != null) : "FIXME! No GraphElementClass has been created.";

		// Gets and sets the attribute "isAbstract"
		element.setAbstract(gElement.isIsAbstract());
		assert (element.isAbstract() == gElement.isIsAbstract()) : "FIXME! The attribute \"isAbstract\" is not equal.";

		// Sets all Attribute objects
		createAllAttributes(element, gElement);
		// Sets all Constraint objects
		createAllConstraints(element, gElement);

		return element;
	}

	/**
	 * Converts a given EdgeClass objects with its To and From edge into a
	 * corresponding EdgeClass object of the Schema.
	 */
	private de.uni_koblenz.jgralab.schema.EdgeClass createEdgeClass(
			GraphElementClass gElement, To gTo, From gFrom) {

		de.uni_koblenz.jgralab.schema.VertexClass to, from;

		int fromMin, fromMax, toMin, toMax;
		String fromRoleName, toRoleName;
		Set<String> fromRedefinedRoles, toRedefinedRoles;

		assert (gElement != null) : "The given GraphElementClass of the SchemaGraph is null.";
		assert (gTo != null && gFrom != null) : "One fo the edges To or From is null.";
		assert (gTo.getThat() instanceof VertexClass && gFrom != null && gFrom
				.getThat() instanceof VertexClass) : "One of the referenced objects is not an instance of the class VertexClass";

		// Gets all attributes of the To edge
		to = queryVertexClass((VertexClass) gTo.getThat());
		toMin = gTo.getMin();
		toMax = gTo.getMax();
		toRoleName = gTo.getRoleName();
		toRedefinedRoles = gTo.getRedefinedRoles();

		// Gets all attributes of the From edge
		from = queryVertexClass((VertexClass) gFrom.getThat());
		fromMin = gFrom.getMin();
		fromMax = gFrom.getMax();
		fromRoleName = gFrom.getRoleName();
		fromRedefinedRoles = gFrom.getRedefinedRoles();

		// Gets some missing attribute
		String qualifiedName = gElement.getQualifiedName();
		boolean isAggegatedFrom = (gElement instanceof AggregationClass) ? ((AggregationClass) gElement)
				.isAggregateFrom()
				: false;

		de.uni_koblenz.jgralab.schema.EdgeClass edgeClass;

		if (gElement instanceof CompositionClass) {

			// Creates a CompositionClass
			edgeClass = graphClass.createCompositionClass(qualifiedName, from,
					fromMin, fromMax, fromRoleName, isAggegatedFrom, to, toMin,
					toMax, toRoleName);

		} else if (gElement instanceof AggregationClass) {

			// Creates an AggregationClass
			edgeClass = graphClass.createAggregationClass(qualifiedName, from,
					fromMin, fromMax, fromRoleName, isAggegatedFrom, to, toMin,
					toMax, toRoleName);

		} else {

			// Creates an EdgeClass
			edgeClass = graphClass.createEdgeClass(qualifiedName, from,
					fromMin, fromMax, fromRoleName, to, toMin, toMax,
					toRoleName);
		}

		// Adds RedefinedRoles
		edgeClass.redefineFromRole(fromRedefinedRoles);
		edgeClass.redefineToRole(toRedefinedRoles);

		return edgeClass;
	}

	/**
	 * Converts all Constraint objects of a given GraphClassElement and adds
	 * them to a GraphClassElement of the Schema.
	 * 
	 * @param element
	 *            GraphElementClass, to which all converted Constraint objects
	 *            are added.
	 * @param gElement
	 *            GraphElementClass, of which all Constraint objects will be
	 *            converted.
	 */
	private void createAllConstraints(
			de.uni_koblenz.jgralab.schema.AttributedElementClass element,
			AttributedElementClass gElement) {

		// Loop over all HasConstraint edges
		for (HasConstraint hasConstraint : gElement
				.getHasConstraintIncidences(OUTGOING)) {
			assert (hasConstraint != null && hasConstraint.getThat() instanceof Constraint) : "FIXME! That should be an instance of Constraint.";

			// Gets the Constraint
			Constraint constraint = (Constraint) hasConstraint.getThat();
			// Creates and adds the constraint
			element.addConstraint(new ConstraintImpl(constraint.getMessage(),
					constraint.getPredicateQuery(), constraint
							.getOffendingElementsQuery()));
		}
	}

	/**
	 * Converts all Attribute objects of a given GraphClassElement and adds them
	 * to a GraphClassElement of the Schema.
	 * 
	 * @param element
	 *            GraphElementClass, to which all converted Attribute objects
	 *            are added.
	 * @param gElement
	 *            GraphElementClass, of which all Attributes objects will be
	 *            converted.
	 */
	private void createAllAttributes(
			de.uni_koblenz.jgralab.schema.AttributedElementClass element,
			AttributedElementClass gElement) {

		// Loop over all HasAttibute edge of a given GraphElementClass
		for (HasAttribute hasAttribute : gElement
				.getHasAttributeIncidences(OUTGOING)) {

			// Gets the Attribute
			assert (hasAttribute != null && hasAttribute.getThat() instanceof Attribute) : "That should be an instance of Attribute.";
			Attribute attribute = (Attribute) hasAttribute.getThat();
			assert (attribute.getName() != null) : "The name of the Attribute is null.";

			// Gets the Domain
			HasDomain hasDomain = attribute.getFirstHasDomain(OUTGOING);
			assert (hasDomain != null) : "No \"HasDomain\" edge has been defined.";
			assert (hasDomain.getThat() instanceof Domain) : "That should be an instance of Domain.";
			// Creates and adds an Attribute
			element.addAttribute(attribute.getName(),
					queryDomain((Domain) hasDomain.getThat()));
			assert (hasDomain.getNextHasDomain(OUTGOING) == null);
		}
	}

	/**
	 * Links all GraphElementClass objects with their superclass.
	 */
	private void linkSuperClasses() {

		// Loop over all GraphElementClass
		for (GraphElementClass gGraphElement : gGraphElementClasses) {

			// Gets a corresponding AttributedElementClass object
			de.uni_koblenz.jgralab.schema.AttributedElementClass element = schema
					.getAttributedElementClass(gGraphElement.getQualifiedName());
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
				.getSpecializesEdgeClassIncidences()) {

			// Gets the superclass
			assert (specializesEdgeClass != null && specializesEdgeClass
					.getThat() instanceof EdgeClass) : "That should be an instance of EdgeClass.";
			EdgeClass gSuperClass = (EdgeClass) specializesEdgeClass.getThat();

			// Gets the corresponding superclass
			de.uni_koblenz.jgralab.schema.AttributedElementClass superClass = schema
					.getAttributedElementClass(gSuperClass.getQualifiedName());
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
				.getSpecializesVertexClassIncidences()) {

			// Gets the superclass
			assert (specializesVertexClass.getOmega() instanceof VertexClass) : "That should be an instance of VertexClass.";
			VertexClass gSuperClass = (VertexClass) specializesVertexClass
					.getOmega();

			// Gets the corresponding superclass
			de.uni_koblenz.jgralab.schema.AttributedElementClass superClass = schema
					.getAttributedElementClass(gSuperClass.getQualifiedName());
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
		de.uni_koblenz.jgralab.schema.AttributedElementClass element = schema
				.getAttributedElementClass(gElement.getQualifiedName());

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
		de.uni_koblenz.jgralab.schema.Domain domain = schema.getDomain(gDomain
				.getQualifiedName());

		// In the case of no found Domain a new Domain is created.
		if (domain == null) {
			domain = createDomain(gDomain);
		}
		return domain;
	}
}