package de.uni_koblenz.jgralab.utilities.tg2schemagraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.uni_koblenz.jgralab.WorkInProgress;
import de.uni_koblenz.jgralab.grumlschema.SchemaGraph;
import de.uni_koblenz.jgralab.grumlschema.domains.CollectionDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.Domain;
import de.uni_koblenz.jgralab.grumlschema.domains.EnumDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.MapDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.RecordDomain;
import de.uni_koblenz.jgralab.grumlschema.impl.SchemaGraphImpl;
import de.uni_koblenz.jgralab.grumlschema.structure.AggregationClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Attribute;
import de.uni_koblenz.jgralab.grumlschema.structure.AttributedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Constraint;
import de.uni_koblenz.jgralab.grumlschema.structure.EdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.From;
import de.uni_koblenz.jgralab.grumlschema.structure.GraphClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Package;
import de.uni_koblenz.jgralab.grumlschema.structure.Schema;
import de.uni_koblenz.jgralab.grumlschema.structure.To;
import de.uni_koblenz.jgralab.grumlschema.structure.VertexClass;

/**
 * Converts a Schema to a SchemaGraph. This class is mend to be a reusable
 * converter class. This class is not thread safe!
 * 
 * Note for Developers:
 * 
 * All variables from the package "de.uni_koblenz.jgralab.schema" are written
 * normal with the exception of the variable for a package. "package" is a
 * keyword. In this case the variable is written with a prefix "x". All
 * variables from the package "de.uni_koblenz.jgralab.grumlschema.structure" are
 * written with an prefix "g".
 * 
 * @author mmce Eckhard Gro�mann
 */

@WorkInProgress(responsibleDevelopers = "mmce")
public class Schema2SchemaGraph {

	private Schema gSchema;
	private de.uni_koblenz.jgralab.schema.Schema schema;

	private Package gDefaultPackage;
	private de.uni_koblenz.jgralab.schema.Package defaultPackage;

	private GraphClass gGraphClass;
	private de.uni_koblenz.jgralab.schema.GraphClass graphClass;

	private Map<de.uni_koblenz.jgralab.schema.Package, Package> packageMap;
	private Map<de.uni_koblenz.jgralab.schema.AttributedElementClass, AttributedElementClass> attributedElementClassMap;
	private Map<de.uni_koblenz.jgralab.schema.VertexClass, VertexClass> vertexClassMap;
	private Map<de.uni_koblenz.jgralab.schema.EdgeClass, EdgeClass> edgeClassMap;
	private Map<de.uni_koblenz.jgralab.schema.Domain, Domain> domainMap;

	private SchemaGraph schemaGraph;

	/**
	 * Constructs a converter for transforming a Schema to a SchemaGraph. This
	 * constructor is empty, because the real work is done by the method
	 * convert2SchemaGraph(Schema).
	 */
	public Schema2SchemaGraph() {
	}

	/**
	 * SetUp method, which instantiates all necessary resources.
	 */
	private void setUp() {

		packageMap = new HashMap<de.uni_koblenz.jgralab.schema.Package, Package>();
		attributedElementClassMap = new HashMap<de.uni_koblenz.jgralab.schema.AttributedElementClass, AttributedElementClass>();
		domainMap = new HashMap<de.uni_koblenz.jgralab.schema.Domain, Domain>();
		vertexClassMap = new HashMap<de.uni_koblenz.jgralab.schema.VertexClass, VertexClass>();
		edgeClassMap = new HashMap<de.uni_koblenz.jgralab.schema.EdgeClass, EdgeClass>();

		schemaGraph = new SchemaGraphImpl();
	}

	/**
	 * Sets all member variables to null to indirectly free resources and
	 * performs a garbage collection.
	 */
	private void tearDown() {
		// All member variables are set to null
		// This should free resources after a garbage collection
		packageMap = null;
		attributedElementClassMap = null;
		domainMap = null;
		vertexClassMap = null;
		edgeClassMap = null;

		defaultPackage = null;
		gDefaultPackage = null;
		graphClass = null;
		gGraphClass = null;

		schemaGraph = null;

		// Calls the garbage collector
		System.gc();
		System.runFinalization();
	}

	/**
	 * Converts a given Schema to a SchemaGraph and returns it.
	 * 
	 * @param schema
	 *            Schema, which should be convert to a SchemaGraph.
	 * @return New SchemaGraph object.
	 */
	public SchemaGraph convert2SchemaGraph(
			de.uni_koblenz.jgralab.schema.Schema schema) {

		// Sets all resources up.
		setUp();

		this.schema = schema;

		// Creates the Schema
		createSchema();

		// Creates the GraphClass
		createGraphClass();

		// Creates all Packages
		createPackages();

		// Creates all Domains
		createDomains();

		// Creates all VertexClasses
		createVertexClasses();

		// Creates all EdgeClasses
		createEdgeClasses();

		// Creates all SpecializationEdges
		createSpecializations();

		// Creates all Attributes
		createAttributes();

		// Creates all Constraints
		createConstraints();

		// Creates all "From" and "To" Edges
		createEdges();

		// Stores the schemaGraph object, so that it will not be lost after
		// calling the tearDown Method.
		SchemaGraph schemaGraph = this.schemaGraph;

		// Frees all used and no longer needed resources
		tearDown();

		return schemaGraph;
	}

	private void createEdges() {

		for (Entry<de.uni_koblenz.jgralab.schema.EdgeClass, EdgeClass> entry : edgeClassMap
				.entrySet()) {
			createEdges(entry.getKey(), entry.getValue());
		}
	}

	private void createEdges(de.uni_koblenz.jgralab.schema.EdgeClass edgeClass,
			EdgeClass gEdgeClass) {

		Set<String> redefinedRoles;

		VertexClass vertexClass = vertexClassMap.get(edgeClass.getTo());
		To to = schemaGraph.createTo(gEdgeClass, vertexClass);
		to.setMin(edgeClass.getToMin());
		to.setMax(edgeClass.getToMax());
		to.setRoleName(edgeClass.getToRolename());

		redefinedRoles = edgeClass.getRedefinedToRoles();
		if (redefinedRoles != null && redefinedRoles.size() != 0) {
			to.setRedefinedRoles(new HashSet<String>(redefinedRoles));
		}

		vertexClass = vertexClassMap.get(edgeClass.getFrom());
		From from = schemaGraph.createFrom(gEdgeClass, vertexClass);
		from.setMin(edgeClass.getFromMin());
		from.setMax(edgeClass.getFromMax());
		from.setRoleName(edgeClass.getFromRolename());

		redefinedRoles = edgeClass.getRedefinedFromRoles();
		if (redefinedRoles != null && redefinedRoles.size() != 0) {
			from.setRedefinedRoles(new HashSet<String>(redefinedRoles));
		}
	}

	private void createConstraints() {
		for (Entry<de.uni_koblenz.jgralab.schema.AttributedElementClass, AttributedElementClass> entry : attributedElementClassMap
				.entrySet()) {
			createConstraints(entry.getKey(), entry.getValue());
		}
	}

	private void createConstraints(
			de.uni_koblenz.jgralab.schema.AttributedElementClass element,
			AttributedElementClass gElement) {

		Constraint gConstraint;

		for (de.uni_koblenz.jgralab.schema.Constraint constraint : element
				.getConstraints()) {
			gConstraint = schemaGraph.createConstraint();
			gConstraint.setMessage(constraint.getMessage());
			gConstraint.setPredicateQuery(constraint.getPredicate());
			gConstraint.setOffendingElementsQuery(constraint
					.getOffendingElementsQuery());

			schemaGraph.createHasConstraint(gElement, gConstraint);
		}

	}

	private void createAttributes() {
		for (Entry<de.uni_koblenz.jgralab.schema.AttributedElementClass, AttributedElementClass> entry : attributedElementClassMap
				.entrySet()) {
			createAttributes(entry.getKey(), entry.getValue());
		}
	}

	private void createAttributes(
			de.uni_koblenz.jgralab.schema.AttributedElementClass element,
			AttributedElementClass gElement) {

		Attribute gAttribute;
		Domain gDomain;

		for (de.uni_koblenz.jgralab.Attribute attribute : element
				.getAttributeList()) {

			gAttribute = schemaGraph.createAttribute();
			gAttribute.setName(attribute.getName());

			gDomain = domainMap.get(attribute.getDomain());
			assert (gDomain != null) : "";

			schemaGraph.createHasAttribute(gElement, gAttribute);
			schemaGraph.createHasDomain(gAttribute, gDomain);
		}
	}

	private void createSpecializations() {

		for (Entry<de.uni_koblenz.jgralab.schema.VertexClass, VertexClass> entry : vertexClassMap
				.entrySet()) {
			for (de.uni_koblenz.jgralab.schema.AttributedElementClass superClass : entry
					.getKey().getDirectSuperClasses()) {

				if (superClass.isInternal()) {
					continue;
				}

				schemaGraph.createSpecializesVertexClass(entry.getValue(),
						vertexClassMap.get(superClass));
			}
		}

		for (Entry<de.uni_koblenz.jgralab.schema.EdgeClass, EdgeClass> entry : edgeClassMap
				.entrySet()) {
			for (de.uni_koblenz.jgralab.schema.AttributedElementClass superClass : entry
					.getKey().getDirectSuperClasses()) {

				if (superClass.isInternal()) {
					continue;
				}

				schemaGraph.createSpecializesEdgeClass(entry.getValue(),
						edgeClassMap.get(superClass));
			}
		}
	}

	private void createEdgeClasses() {

		for (Entry<de.uni_koblenz.jgralab.schema.Package, Package> entry : packageMap
				.entrySet()) {
			createEdgeClasses(entry.getKey(), entry.getValue());
		}
	}

	private void createVertexClasses() {

		for (Entry<de.uni_koblenz.jgralab.schema.Package, Package> entry : packageMap
				.entrySet()) {
			createVertexClasses(entry.getKey(), entry.getValue());
		}
	}

	private void createDomains() {

		for (Entry<de.uni_koblenz.jgralab.schema.Package, Package> entry : packageMap
				.entrySet()) {
			createDomains(entry.getKey(), entry.getValue());
		}
	}

	private void createPackages() {
		createDefaultPackage();

		createSubPackages(defaultPackage, gDefaultPackage);
	}

	private void createDefaultPackage() {
		defaultPackage = schema.getDefaultPackage();
		gDefaultPackage = schemaGraph.createPackage();

		gDefaultPackage.setQualifiedName(defaultPackage.getQualifiedName());
		packageMap.put(defaultPackage, gDefaultPackage);

		schemaGraph.createContainsDefaultPackage(gSchema, gDefaultPackage);
	}

	private void createEdgeClasses(
			de.uni_koblenz.jgralab.schema.Package Package, Package gPackage) {

		EdgeClass gEdgeClass;

		for (de.uni_koblenz.jgralab.schema.EdgeClass edgeClass : Package
				.getEdgeClasses().values()) {

			if (edgeClass.isInternal()) {
				continue;
			}

			gEdgeClass = createEdgeClass(edgeClass);

			attributedElementClassMap.put(edgeClass, gEdgeClass);
			edgeClassMap.put(edgeClass, gEdgeClass);
			schemaGraph.createContainsGraphElementClass(gPackage, gEdgeClass);
		}
	}

	private EdgeClass createEdgeClass(
			de.uni_koblenz.jgralab.schema.EdgeClass edgeClass) {

		EdgeClass gEdgeClass = null;

		if (edgeClass instanceof de.uni_koblenz.jgralab.schema.AggregationClass) {
			AggregationClass gAggregationClass;
			if (edgeClass instanceof de.uni_koblenz.jgralab.schema.CompositionClass) {
				gAggregationClass = schemaGraph.createCompositionClass();
			} else {
				gAggregationClass = schemaGraph.createAggregationClass();
			}

			de.uni_koblenz.jgralab.schema.AggregationClass aggregationClass = (de.uni_koblenz.jgralab.schema.AggregationClass) edgeClass;
			gAggregationClass.setAggregateFrom(aggregationClass
					.isAggregateFrom());
			gEdgeClass = gAggregationClass;

		} else {
			gEdgeClass = schemaGraph.createEdgeClass();
		}

		gEdgeClass.setIsAbstract(edgeClass.isAbstract());
		gEdgeClass.setQualifiedName(edgeClass.getQualifiedName());

		assert (gEdgeClass != null);
		return gEdgeClass;
	}

	private void createVertexClasses(
			de.uni_koblenz.jgralab.schema.Package Package, Package gPackage) {

		VertexClass gVertexClass;
		for (de.uni_koblenz.jgralab.schema.VertexClass vertexClass : Package
				.getVertexClasses().values()) {

			if (vertexClass.isInternal()) {
				continue;
			}

			gVertexClass = schemaGraph.createVertexClass();
			gVertexClass.setIsAbstract(vertexClass.isAbstract());
			gVertexClass.setQualifiedName(vertexClass.getQualifiedName());

			attributedElementClassMap.put(vertexClass, gVertexClass);
			vertexClassMap.put(vertexClass, gVertexClass);

			schemaGraph.createContainsGraphElementClass(gPackage, gVertexClass);
		}
	}

	private void createSubPackages(
			de.uni_koblenz.jgralab.schema.Package Package, Package gPackage) {

		Package gSubPackage;

		for (de.uni_koblenz.jgralab.schema.Package subPackage : Package
				.getSubPackages().values()) {
			gSubPackage = schemaGraph.createPackage();
			gSubPackage.setQualifiedName(subPackage.getQualifiedName());

			packageMap.put(subPackage, gSubPackage);

			schemaGraph.createContainsSubPackage(gPackage, gSubPackage);
			createSubPackages(subPackage, gSubPackage);
		}
	}

	private void createDomains(de.uni_koblenz.jgralab.schema.Package Package,
			Package gPackage) {

		for (de.uni_koblenz.jgralab.schema.Domain domain : Package.getDomains()
				.values()) {
			createDomain(domain);
		}
	}

	private Domain createDomain(de.uni_koblenz.jgralab.schema.Domain domain) {

		assert (schemaGraph != null);
		assert (domain != null);

		Domain gDomain = null;

		if (domainMap.containsKey(domain)) {
			gDomain = domainMap.get(domain);
		} else {
			if (domain instanceof de.uni_koblenz.jgralab.schema.BooleanDomain) {
				gDomain = schemaGraph.createBooleanDomain();
			} else if (domain instanceof de.uni_koblenz.jgralab.schema.IntDomain) {
				gDomain = schemaGraph.createIntDomain();
			} else if (domain instanceof de.uni_koblenz.jgralab.schema.LongDomain) {
				gDomain = schemaGraph.createLongDomain();
			} else if (domain instanceof de.uni_koblenz.jgralab.schema.DoubleDomain) {
				gDomain = schemaGraph.createDoubleDomain();
			} else if (domain instanceof de.uni_koblenz.jgralab.schema.StringDomain) {
				gDomain = schemaGraph.createStringDomain();
			} else if (domain instanceof de.uni_koblenz.jgralab.schema.RecordDomain) {
				gDomain = createRecordDomain((de.uni_koblenz.jgralab.schema.RecordDomain) domain);
			} else if (domain instanceof de.uni_koblenz.jgralab.schema.CollectionDomain) {
				gDomain = createCollectionDomain((de.uni_koblenz.jgralab.schema.CollectionDomain) domain);
			} else if (domain instanceof de.uni_koblenz.jgralab.schema.MapDomain) {
				gDomain = createMapDomain((de.uni_koblenz.jgralab.schema.MapDomain) domain);
			} else if (domain instanceof de.uni_koblenz.jgralab.schema.EnumDomain) {
				gDomain = createEnumDomain((de.uni_koblenz.jgralab.schema.EnumDomain) domain);
			} else {
				throw new RuntimeException("FIXME: Unforseen domain occured! "
						+ domain);
			}

			gDomain.setQualifiedName(domain.getQualifiedName());
			Package gPackage = packageMap.get(domain.getPackage());
			schemaGraph.createContainsDomain(gPackage, gDomain);

			domainMap.put(domain, gDomain);
		}

		assert (gDomain != null);

		return gDomain;
	}

	private MapDomain createMapDomain(
			de.uni_koblenz.jgralab.schema.MapDomain domain) {

		MapDomain gDomain = schemaGraph.createMapDomain();

		schemaGraph.createHasKeyDomain(gDomain, queryGDomain(domain
				.getKeyDomain()));
		schemaGraph.createHasValueDomain(gDomain, queryGDomain(domain
				.getValueDomain()));
		return gDomain;
	}

	private EnumDomain createEnumDomain(
			de.uni_koblenz.jgralab.schema.EnumDomain domain) {

		EnumDomain gDomain = schemaGraph.createEnumDomain();

		gDomain.setEnumConstants(new ArrayList<String>(domain.getConsts()));
		return gDomain;
	}

	private CollectionDomain createCollectionDomain(
			de.uni_koblenz.jgralab.schema.CollectionDomain domain) {

		CollectionDomain gDomain = (domain instanceof de.uni_koblenz.jgralab.schema.ListDomain) ? schemaGraph
				.createListDomain()
				: schemaGraph.createSetDomain();

		schemaGraph.createHasBaseDomain(gDomain, queryGDomain(domain
				.getBaseDomain()));
		return gDomain;
	}

	private RecordDomain createRecordDomain(
			de.uni_koblenz.jgralab.schema.RecordDomain domain) {

		RecordDomain gDomain = schemaGraph.createRecordDomain();

		for (Entry<String, de.uni_koblenz.jgralab.schema.Domain> entry : domain
				.getComponents().entrySet()) {

			// Creates a new hasRecordDomainComponent-edge and sets its name.
			schemaGraph.createHasRecordDomainComponent(gDomain,
					queryGDomain(entry.getValue())).setName(entry.getKey());
		}

		return gDomain;
	}

	private Domain queryGDomain(de.uni_koblenz.jgralab.schema.Domain domain) {

		Domain gDomain = domainMap.get(domain);

		if (gDomain == null) {
			gDomain = createDomain(domain);
		}

		return gDomain;
	}

	/**
	 * Creates a Schema in the SchemaGraph.
	 */
	private void createSchema() {

		assert (schemaGraph != null) : "No SchemaGraph created! (setUp()-Methode may not been executed.)";
		assert (schema != null) : "No schema defined!";

		gSchema = schemaGraph.createSchema();

		gSchema.setName(schema.getName());
		gSchema.setPackagePrefix(schema.getPackagePrefix());
	}

	private void createGraphClass() {

		assert (schemaGraph != null);
		assert (schema != null);
		assert (gSchema != null);

		this.gGraphClass = schemaGraph.createGraphClass();

		this.graphClass = schema.getGraphClass();

		assert (!graphClass.isInternal()) : "There have to be a GraphClass, which isn't internal!";

		// Is needed to reference to the new AttributedElementClass-objects.
		attributedElementClassMap.put(graphClass, gGraphClass);

		gGraphClass.setIsAbstract(graphClass.isAbstract());
		gGraphClass.setQualifiedName(graphClass.getQualifiedName());

		schemaGraph.createDefinesGraphClass(gSchema, gGraphClass);
	}
}
