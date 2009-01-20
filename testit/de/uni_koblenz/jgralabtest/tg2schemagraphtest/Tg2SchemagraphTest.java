package de.uni_koblenz.jgralabtest.tg2schemagraphtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.uni_koblenz.jgralab.Attribute;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.GraphMarker;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.QualifiedName;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.impl.AggregationClassImpl;
import de.uni_koblenz.jgralab.schema.impl.CompositionClassImpl;
import de.uni_koblenz.jgralab.schema.impl.EdgeClassImpl;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.Tg2SchemaGraph;

@RunWith(Parameterized.class)
public class Tg2SchemagraphTest {

	private static final String pathName = "src/de/uni_koblenz/jgralabtest/tg2schemagraphtest/";
	private de.uni_koblenz.jgralab.schema.Schema schema;
	private de.uni_koblenz.jgralab.Graph schemagraph;

	public Tg2SchemagraphTest(de.uni_koblenz.jgralab.schema.Schema schema,
			de.uni_koblenz.jgralab.Graph schemagraph) {
		super();

		this.schema = schema;
		this.schemagraph = schemagraph;
	}

	@Parameters
	public static Collection<Object[]> data() {
		String[] tgfiles = new File(pathName).list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".tg");
			}
		});
		Object[][] data = new Object[tgfiles.length][];

		for (int i = 0; i < data.length; i++) {
			data[i] = new Object[2];
			try {
				de.uni_koblenz.jgralab.schema.Schema s = GraphIO
						.loadSchemaFromFile(pathName + tgfiles[i]);
				Tg2SchemaGraph conv = new Tg2SchemaGraph(s);
				de.uni_koblenz.jgralab.Graph sGraph = conv.getSchemaGraph();
				data[i][0] = s;
				data[i][1] = sGraph;
			} catch (GraphIOException e) {

			}
		}
		return Arrays.asList(data);
	}

	// ///////////////////////////////
	// TESTS ////////////////////////
	// ///////////////////////////////

	/**
	 * Tests if the schemagraph contains exactly one 'Schema'-Vertex
	 */
	@Test
	public void schemagraphContainsExactlyOneSchemaVertex() {
		int schemaVertexCount = countClassVertices("Schema");
		assertEquals(
				"The Schemagraph does not contain exactly one Schema vertex.",
				1, schemaVertexCount);
	}

	/**
	 * Tests if the schemagraphs 'Schema'-Vertex attribute packagePrefix equals
	 * the packagePrefix of the schema.
	 */
	@Test
	public void testSchemaPackagePrefix() {
		try {
			assertEquals(
					"The package name of the schema does not match the attribute packagePrefix of the schemagraphs schema vertex.",
					schema.getPackageName(),
					((de.uni_koblenz.jgralab.grumlschema.Schema) schemagraph
							.getFirstVertexOfClass(de.uni_koblenz.jgralab.grumlschema.Schema.class))
							.getAttribute("packagePrefix"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests if the schemagraphs 'Schema'-Vertex attribute name equals the
	 * simple name of the schema.
	 */
	@Test
	public void testSchemaName() {
		try {
			assertEquals(
					"The name of the schema does not match the attribute name of the schemagraphs schema vertex.",
					schema.getSimpleName(),
					((de.uni_koblenz.jgralab.grumlschema.Schema) schemagraph
							.getFirstVertexOfClass(de.uni_koblenz.jgralab.grumlschema.Schema.class))
							.getAttribute("name"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests if there is exactly one ContainsGraphClass-Edge in the schemagraph.
	 */
	@Test
	public void schemagraphContainsExactlyOneDefinesGraphClassEdge() {
		int definesGraphClassCount = countClassVertices("DefinesGraphClass");
		assertEquals(
				"The Schemagraph does not define exactly one DefaultGraphClass.",
				1, definesGraphClassCount);
	}

	/**
	 * Tests if there is exactly one ContainsDefaultPackage-Edge in the
	 * schemagraph.
	 */
	@Test
	public void schemagraphContainsExactlyOneContainsDefaultPackageEdge() {
		int containsDefaultPackageCount = countClassVertices("ContainsDefaultPackage");
		assertEquals(
				"The schemagraph contains not exactly one DefaultPackage.", 1,
				containsDefaultPackageCount);

	}

	/**
	 * Tests if the schemagraph contains as many 'Package'-Vertices as the
	 * schema defines Packages.
	 */
	@Test
	public void schemagraphContainsPackageVertices() {
		int packagesInSchema = schema.getPackages().size();
		int packageVerticesInSchemagraph = countClassVertices("Package");

		assertEquals(
				"The schemagraph does not contain exactly as many packages as the schema.",
				packagesInSchema, packageVerticesInSchemagraph);
	}

	/**
	 * Obsolete.
	 *
	 * @see everyQualifiedNameAttributeOfTheSchemagraphsVertexClassVerticesEqualsExactlyOneQualifiedNameOfAVertexClassInTheSchema
	 *      ()
	 */
	@Test
	public void schemagraphContainsVertexClassVertices() {
		int vertexClassesInSchema = 0;
		for (VertexClass v : schema.getVertexClassesInTopologicalOrder()) {
			if (!v.isInternal()) {
				vertexClassesInSchema++;
			}
		}
		int vertexClassVerticesInSchemagraph = countClassVertices("VertexClass");
		assertEquals(
				"The schemagraph does not contain exactly as many packages as the schema.",
				vertexClassesInSchema, vertexClassVerticesInSchemagraph);
	}

	/**
	 * Obsolete.
	 *
	 * @see everyQualifiedNameAttributeOfTheSchemagraphsEdgeClassVerticesEqualsExactlyOneQualifiedNameOfAEdgeClassInTheSchema
	 *      ()
	 */
	@Test
	public void schemagraphContainsEdgeClassVertices() {
		int edgeClassesInSchema = 0;
		for (EdgeClass ec : schema.getEdgeClassesInTopologicalOrder()) {
			if (!ec.isInternal()) {
				edgeClassesInSchema++;
			}
		}
		int edgeClassVerticesInSchemagraph = countClassVertices("EdgeClass");
		assertEquals(
				"The schemagraph does not contain exactly as many edge class vertices as the schema defines edge classes.\nEdge classes in schema: "
						+ edgeClassesInSchema
						+ "\nEdgeclass vertices in schemagraph: "
						+ edgeClassVerticesInSchemagraph, edgeClassesInSchema,
				edgeClassVerticesInSchemagraph);
	}

	/**
	 * Obsolete.
	 *
	 * @see everyQualifiedNameAttributeOfTheSchemagraphsEdgeClassVerticesEqualsExactlyOneQualifiedNameOfAEdgeClassInTheSchema
	 *      ()
	 */
	@Test
	public void schemagraphContainsCompositionClassVertices() {
		int edgeClassesInSchema = 0;
		for (EdgeClass ec : schema.getEdgeClassesInTopologicalOrder()) {
			if (!ec.isInternal()
					&& ec.getClass().equals(CompositionClassImpl.class)) {
				edgeClassesInSchema++;
			}
		}
		int edgeClassVerticesInSchemagraph = countClassVertices("CompositionClass");
		assertEquals(
				"The schemagraph does not contain exactly as many edge class vertices as the schema defines edge classes.\nEdge classes in schema: "
						+ edgeClassesInSchema
						+ "\nEdgeclass vertices in schemagraph: "
						+ edgeClassVerticesInSchemagraph, edgeClassesInSchema,
				edgeClassVerticesInSchemagraph);
	}

	/**
	 * Obsolete.
	 *
	 * @see everyQualifiedNameAttributeOfTheSchemagraphsEdgeClassVerticesEqualsExactlyOneQualifiedNameOfAEdgeClassInTheSchema
	 *      ()
	 */
	@Test
	public void schemagraphContainsAggregationClassVertices() {
		int edgeClassesInSchema = 0;
		for (EdgeClass ec : schema.getEdgeClassesInTopologicalOrder()) {
			if (!ec.isInternal()
					&& ec.getClass().equals(AggregationClassImpl.class)) {
				edgeClassesInSchema++;
			}
		}
		int edgeClassVerticesInSchemagraph = countClassVertices("AggregationClass");
		assertEquals(
				"The schemagraph does not contain exactly as many edge class vertices as the schema defines edge classes.\nEdge classes in schema: "
						+ edgeClassesInSchema
						+ "\nEdgeclass vertices in schemagraph: "
						+ edgeClassVerticesInSchemagraph, edgeClassesInSchema,
				edgeClassVerticesInSchemagraph);
	}

	/**
	 * Test if the schemagraph contains as many 'Domain'-Vertices as the schema
	 * defines Domains.
	 */
	@Test
	public void schemagraphContainsDomainVertices() {
		int domainsInSchema = schema.getDomains().size();
		int domainVerticesInSchemagraph = countClassVertices("Domain");
		assertEquals(
				"The schemagraph doe not contains as many Domain Vertices as the schema defines Domains.",
				domainsInSchema, domainVerticesInSchemagraph);
	}

	/**
	 * This test iterates over every VertexClass in the schema.
	 *
	 * A GraphMarker marks every 'VertexClass'-Vertex in the schemagraph if
	 *
	 * 1. its attribute 'qualifiedName' equals the qualifiedName of the
	 * VertexClass of the current iteration.
	 *
	 * 2. the 'VertexClass'-Vertex has not been marked before.
	 *
	 * After the loop the number of marked elements should be equal to the
	 * number of 'VertexClass'-Vertices in the schemagraph. It verifies that
	 *
	 * 1. every VertexClass has a correspondent VertexClass-Vertex in the
	 * schemagraph
	 *
	 * 2. there are no VertexClass-Vertices in the schemagraph that have no
	 * correspondence in the schema.
	 */
	@Test
	public void qualifiedNamesOfVertexClassesMatch() {
		GraphMarker<Object> marker = new GraphMarker<Object>(schemagraph);
		for (VertexClass vc : schema.getVertexClassesInTopologicalOrder()) {
			if (!vc.isInternal()) {
				String vertexClassesQualifiedName = vc.getQualifiedName();
				for (Vertex vcv : schemagraph
						.vertices((VertexClass) schemagraph.getSchema()
								.getAttributedElementClass(
										new QualifiedName("VertexClass")))) {
					if (marker.getMark(vcv) == null) {
						try {
							if (((de.uni_koblenz.jgralab.grumlschema.VertexClass) vcv)
									.getAttribute("qualifiedName").equals(
											vertexClassesQualifiedName)) {
								marker.mark(vcv, new Object());
							}
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}
		int markedSchemagraphVertexClassVertices = marker.size();
		int schemagraphVertices = countClassVertices("VertexClass");

		assertEquals(
				"Not all name attributes in the schemagraphs VertexClass vertices have a corresponding qualified name in the schemas VertexClasses.",
				markedSchemagraphVertexClassVertices, schemagraphVertices);

	}

	/**
	 * This test checks if every EdgeClass-Vertex in the schemagraph has exactly
	 * one To- and From-Edge.
	 */
	@Test
	public void schemagraphsEdgeClassesHaveOnlyOneToAndFromEdge() {
		boolean success = true;
		for (Vertex v : schemagraph.vertices((VertexClass) schemagraph
				.getSchema().getAttributedElementClass(
						new QualifiedName("EdgeClass")))) {
			de.uni_koblenz.jgralab.grumlschema.EdgeClass ecv = (de.uni_koblenz.jgralab.grumlschema.EdgeClass) v;
			de.uni_koblenz.jgralab.grumlschema.To to = ecv.getFirstTo();
			boolean hasOneToEdge = to != null;
			boolean hasNotTwoToEdges = to.getNextTo() == null;
			de.uni_koblenz.jgralab.grumlschema.From from = ecv.getFirstFrom();
			boolean hasOneFromEdge = from != null;
			boolean hasNotTwoFromEdges = from.getNextFrom() == null;
			success &= hasOneToEdge && hasNotTwoToEdges && hasOneFromEdge
					&& hasNotTwoFromEdges;

		}

		assertEquals(
				"To- and From-Edges are not unique per EdgeClass-Vertex in the schemagraph.",
				true, success);
	}

	/**
	 * Tests if every Attribute of the GraphClass vertex in the schemagraph is
	 * arranged correctly. 1) links it to the correct Domain-Vertex 2) links it
	 * to exactly one Domain-Vertex
	 */
	@Test
	public void eachGraphClassAttributeMatches() {
		boolean success = true;
		for (GraphClass gc : schema.getGraphClassesInTopologicalOrder()) {
			if (!gc.isInternal()) {
				// get the correspondent graphClass in the schemagraph
				de.uni_koblenz.jgralab.grumlschema.GraphClass schemagraphGraphClass = null;
				for (Vertex gcv : schemagraph
						.vertices((VertexClass) schemagraph.getSchema()
								.getAttributedElementClass(
										new QualifiedName("GraphClass")))) {
					try {
						if ((((de.uni_koblenz.jgralab.grumlschema.GraphClass) gcv)
								.getAttribute("qualifiedName").equals(gc
								.getQualifiedName()))) {
							schemagraphGraphClass = (de.uni_koblenz.jgralab.grumlschema.GraphClass) gcv;
						}
					} catch (NoSuchFieldException e) {
						success = false;
					}
				}
				this.sizeOfIterableEqualsInt(schemagraphGraphClass
						.getHasAttributeIncidences(), gc.getAttributeCount());
				for (Attribute attr : gc.getAttributeList()) {
					boolean schemagraphsAttributeHasExactlyOneDomain = true;
					boolean matchingSchemagraphAttributeExists = false;
					String attrName = attr.getName();
					String attrDomainName = attr.getDomain().getQualifiedName();
					Iterator<de.uni_koblenz.jgralab.grumlschema.HasAttribute> iter = schemagraphGraphClass
							.getHasAttributeIncidences().iterator();
					while (iter.hasNext()) {
						de.uni_koblenz.jgralab.grumlschema.Attribute hasAttributeEdge = (de.uni_koblenz.jgralab.grumlschema.Attribute) iter
								.next().getThat();
						String schemagraphAttrName = "";
						String schemagraphAttrDomainName = "";
						try {
							schemagraphAttrName = hasAttributeEdge
									.getAttribute("name").toString();
							schemagraphAttrDomainName = hasAttributeEdge
									.getFirstHasDomain().getThat()
									.getAttribute("qualifiedName").toString();
							schemagraphsAttributeHasExactlyOneDomain = hasAttributeEdge
									.getFirstHasDomain().getNextHasDomain() == null;
						} catch (NoSuchFieldException e) {
							success = false;
						}
						if (schemagraphAttrName.equals(attrName)
								&& schemagraphAttrDomainName
										.equals(attrDomainName)) {
							matchingSchemagraphAttributeExists = true;
							break;
						}
					}
					success &= schemagraphsAttributeHasExactlyOneDomain
							&& matchingSchemagraphAttributeExists;
				}
			}
		}
		assertEquals(
				"GraphClass attributes are not correctly arranged in the schemagraph.",
				true, success);
	}

	/**
	 * Tests if every Attribute of VertexClass vertices in the schemagraph is
	 * arranged correctly. 1) links it to the correct Domain-Vertex 2) links it
	 * to exactly one Domain-Vertex
	 */
	@Test
	public void eachVertexClassAttributeMatches() {
		boolean success = true;
		for (VertexClass vc : schema.getVertexClassesInTopologicalOrder()) {
			if (!vc.isInternal()) {
				// get the correspondent graphClass in the schemagraph
				de.uni_koblenz.jgralab.grumlschema.VertexClass schemagraphVertexClass = null;
				for (Vertex vcv : schemagraph
						.vertices((VertexClass) schemagraph.getSchema()
								.getAttributedElementClass(
										new QualifiedName("VertexClass")))) {
					try {
						if ((((de.uni_koblenz.jgralab.grumlschema.VertexClass) vcv)
								.getAttribute("qualifiedName").equals(vc
								.getQualifiedName()))) {
							schemagraphVertexClass = (de.uni_koblenz.jgralab.grumlschema.VertexClass) vcv;
						}
					} catch (NoSuchFieldException e) {
						success = false;
					}
				}
				this.sizeOfIterableEqualsInt(schemagraphVertexClass
						.getHasAttributeIncidences(), vc.getAttributeCount());
				for (Attribute attr : vc.getAttributeList()) {
					boolean schemagraphsAttributeHasExactlyOneDomain = true;
					boolean matchingSchemagraphAttributeExists = false;
					String attrName = attr.getName();
					String attrDomainName = attr.getDomain().getQualifiedName();
					Iterator<de.uni_koblenz.jgralab.grumlschema.HasAttribute> iter = schemagraphVertexClass
							.getHasAttributeIncidences().iterator();
					while (iter.hasNext()) {
						de.uni_koblenz.jgralab.grumlschema.Attribute hasAttributeEdge = (de.uni_koblenz.jgralab.grumlschema.Attribute) iter
								.next().getThat();
						String schemagraphAttrName = "";
						String schemagraphAttrDomainName = "";
						schemagraphAttrName = hasAttributeEdge.getName();
						schemagraphAttrDomainName = ((de.uni_koblenz.jgralab.grumlschema.Domain) hasAttributeEdge
								.getFirstHasDomain().getThat())
								.getQualifiedName();
						schemagraphsAttributeHasExactlyOneDomain = hasAttributeEdge
								.getFirstHasDomain().getNextHasDomain() == null;

						if (schemagraphAttrName.equals(attrName)
								&& schemagraphAttrDomainName
										.equals(attrDomainName)) {
							matchingSchemagraphAttributeExists = true;
							break;
						} else {
							System.out.println("Wrong domain name: "
									+ schemagraphAttrDomainName
									+ ", attrDomainName = " + attrDomainName
									+ ", schemagraphAttrName = "
									+ schemagraphAttrName + ", attrName = "
									+ attrName);
						}

					}
					success &= schemagraphsAttributeHasExactlyOneDomain
							&& matchingSchemagraphAttributeExists;
					if (success == false
							&& !(schemagraphsAttributeHasExactlyOneDomain && matchingSchemagraphAttributeExists)) {
						System.out.println(vc.getQualifiedName() + " "
								+ attrName + " " + attrDomainName + " in "
								+ " attributeExists = "
								+ matchingSchemagraphAttributeExists
								+ ", oneDomain = "
								+ schemagraphsAttributeHasExactlyOneDomain);
					}
				}
			}
		}
		assertTrue(
				"VertexClass attributes are not correctly arranged in the schemagraph.",
				success);
	}

	/**
	 * Tests if every Attribute of EdgeClass vertices in the schemagraph is
	 * arranged correctly. 1) links it to the correct Domain-Vertex 2) links it
	 * to exactly one Domain-Vertex
	 */
	@Test
	public void eachEdgeClassAttributeMatches() {
		boolean success = true;
		for (EdgeClass ec : schema.getEdgeClassesInTopologicalOrder()) {
			if (!ec.isInternal()) {
				// get the correspondent graphClass in the schemagraph
				de.uni_koblenz.jgralab.grumlschema.EdgeClass schemagraphEdgeClass = null;
				for (Vertex ecv : schemagraph
						.vertices((VertexClass) schemagraph.getSchema()
								.getAttributedElementClass(
										new QualifiedName("EdgeClass")))) {
					try {
						if ((((de.uni_koblenz.jgralab.grumlschema.EdgeClass) ecv)
								.getAttribute("qualifiedName").equals(ec
								.getQualifiedName()))) {
							schemagraphEdgeClass = (de.uni_koblenz.jgralab.grumlschema.EdgeClass) ecv;
						}
					} catch (NoSuchFieldException e) {
						success = false;
					}
				}
				this.sizeOfIterableEqualsInt(schemagraphEdgeClass
						.getHasAttributeIncidences(), ec.getAttributeCount());
				for (Attribute attr : ec.getAttributeList()) {
					boolean schemagraphsAttributeHasExactlyOneDomain = true;
					boolean matchingSchemagraphAttributeExists = false;
					String attrName = attr.getName();
					String attrDomainName = attr.getDomain().getQualifiedName();
					Iterator<de.uni_koblenz.jgralab.grumlschema.HasAttribute> iter = schemagraphEdgeClass
							.getHasAttributeIncidences().iterator();
					while (iter.hasNext()) {
						de.uni_koblenz.jgralab.grumlschema.Attribute hasAttributeEdge = (de.uni_koblenz.jgralab.grumlschema.Attribute) iter
								.next().getThat();
						String schemagraphAttrName = "";
						String schemagraphAttrDomainName = "";
						schemagraphAttrName = hasAttributeEdge.getName();
						schemagraphAttrDomainName = ((de.uni_koblenz.jgralab.grumlschema.Domain) hasAttributeEdge
								.getFirstHasDomain().getThat())
								.getQualifiedName();
						schemagraphsAttributeHasExactlyOneDomain = hasAttributeEdge
								.getFirstHasDomain().getNextHasDomain() == null;

						if (schemagraphAttrName.equals(attrName)
								&& schemagraphAttrDomainName
										.equals(attrDomainName)) {
							matchingSchemagraphAttributeExists = true;
							break;
						}

					}
					success &= schemagraphsAttributeHasExactlyOneDomain
							&& matchingSchemagraphAttributeExists;
					if (success == false
							&& !(schemagraphsAttributeHasExactlyOneDomain && matchingSchemagraphAttributeExists)) {
						System.out.println(ec.getQualifiedName() + " "
								+ attrName + " " + attrDomainName + " in "
								+ " attributeExists = "
								+ matchingSchemagraphAttributeExists
								+ ", oneDomain = "
								+ schemagraphsAttributeHasExactlyOneDomain);
					}
				}
			}
		}
		assertEquals(
				"EdgeClass attributes are not correctly arranged in the schemagraph.",
				true, success);
	}

	/**
	 * This test checks for each EdgeClass in the schema and the correspondent
	 * EdgeClass-Vertex in the Schemagraph: -
	 * GetFirstTo().getThat().getQualifiedName() equals - GetFirstTo().getMin()
	 * equals - GetFirstTo().getMax() equals - GetFirstTo().getRoleName() equals
	 * - GetFirstFrom().getThat().getQualifiedName() equals -
	 * GetFirstFrom().getMin() equals - GetFirstFrom().getMax() equals -
	 * GetFirstFrom().getRoleName() equals
	 */
	@Test
	public void edgeClassesToAndFromEdgesMatch() {
		boolean success = true;
		// for each edge class in the schema
		for (EdgeClass ec : schema.getEdgeClassesInTopologicalOrder()) {
			if (!ec.isInternal()) {
				// get the correspondent edge class in the schemagraph
				de.uni_koblenz.jgralab.grumlschema.EdgeClass schemagraphEdgeClass = null;
				for (Vertex ecv : schemagraph
						.vertices((VertexClass) schemagraph.getSchema()
								.getAttributedElementClass(
										new QualifiedName("EdgeClass")))) {
					try {
						if ((((de.uni_koblenz.jgralab.grumlschema.EdgeClass) ecv)
								.getAttribute("qualifiedName").equals(ec
								.getQualifiedName()))) {
							schemagraphEdgeClass = (de.uni_koblenz.jgralab.grumlschema.EdgeClass) ecv;
						}
					} catch (NoSuchFieldException e) {
						success = false;
					}
				}
				// /////////////////
				// Examine the To-Edge
				// /////////////////

				// determine if qualifiedName of the To-Edge's VertexClass
				// equals.
				String to_qNameSchema = ec.getTo().getQualifiedName();
				String to_qNameSchemagraph = "";
				try {
					to_qNameSchemagraph = schemagraphEdgeClass.getFirstTo()
							.getThat().getAttribute("qualifiedName").toString();
				} catch (NoSuchFieldException e) {
					success = false;
				}
				boolean to_qualifiedNameEquals = to_qNameSchema
						.equals(to_qNameSchemagraph);

				// determine if the min attribute of the To-Edge equals
				int to_minSchema = ec.getToMin();
				int to_minSchemagraph = schemagraphEdgeClass.getFirstTo()
						.getMin();

				boolean to_minEquals = to_minSchema == to_minSchemagraph;

				// determine if the max attribute of the To-Edge equals
				int to_maxSchema = ec.getToMax();
				int to_maxSchemagraph = schemagraphEdgeClass.getFirstTo()
						.getMax();
				boolean to_maxEquals = to_maxSchema == to_maxSchemagraph;

				// determine if the rolename of the To-Edges equals
				String to_rolenameSchema = ec.getToRolename();
				String to_rolenameSchemagraph = schemagraphEdgeClass
						.getFirstTo().getRoleName();
				boolean to_rolenameEquals = to_rolenameSchema
						.equals(to_rolenameSchemagraph);

				boolean to_equals = to_qualifiedNameEquals && to_minEquals
						&& to_maxEquals && to_rolenameEquals;

				// /////////////////
				// Examine the From-Edge
				// /////////////////

				// determine if qualifiedName of the From-Edge's VertexClass
				// equals.
				String from_qNameSchema = ec.getFrom().getQualifiedName();
				String from_qNameSchemagraph = "";
				try {
					from_qNameSchemagraph = schemagraphEdgeClass.getFirstFrom()
							.getThat().getAttribute("qualifiedName").toString();
				} catch (NoSuchFieldException e) {
					success = false;
				}
				boolean from_qualifiedNameEquals = from_qNameSchema
						.equals(from_qNameSchemagraph);

				// determine if the min attribute of the From-Edge equals
				int from_minSchema = ec.getFromMin();
				int from_minSchemagraph = schemagraphEdgeClass.getFirstFrom()
						.getMin();

				boolean from_minEquals = from_minSchema == from_minSchemagraph;

				// determine if the max attribute of the From-Edge equals
				int from_maxSchema = ec.getFromMax();
				int from_maxSchemagraph = schemagraphEdgeClass.getFirstFrom()
						.getMax();
				boolean from_maxEquals = from_maxSchema == from_maxSchemagraph;

				// determine if the rolename of the From-Edges equals
				String from_rolenameSchema = ec.getFromRolename();
				String from_rolenameSchemagraph = schemagraphEdgeClass
						.getFirstFrom().getRoleName();
				boolean from_rolenameEquals = from_rolenameSchema
						.equals(from_rolenameSchemagraph);

				boolean from_equals = from_qualifiedNameEquals
						&& from_minEquals && from_maxEquals
						&& from_rolenameEquals;

				success &= to_equals && from_equals;
			}
		}

		assertEquals(
				"To- and From-Edges in the schmagraph do not match the schema.",
				true, success);
	}

	/**
	 * This test iterates over every EdgeClass of the schema.
	 *
	 * A GraphMarker marks an 'EdgeClass'-Vertex of the schemagraph if
	 *
	 * 1. its attribute 'qualifiedName' matches the qualified Name of the
	 * schemas EdgeClass.
	 *
	 * 2. the 'EdgeClass'-Vertex has not been marked before.
	 *
	 * 3. the subclass of the EdgeClass and the 'EdgeClass'-Vertex equals (i.e
	 * de.uni_koblenz.jgralab.schema.impl.AggregationClassImpl equals
	 * de.uni_koblenz.jgralab.grumlschema.impl.AggregationClassImpl).
	 *
	 * When after this loop the number of marked 'EdgeClass'-Vertices equals the
	 * total number of 'EdgeClass'-Vertices it shows that
	 *
	 * 1. every EdgeClass has a correspondent 'EdgeClass'-Vertex (qualifiedName
	 * used as identifier)
	 *
	 * 2. there is no 'EdgeClass'-Vertex in the schemagraph that has no
	 * corresponding EdgeClass in the schema.
	 */
	@Test
	public void qualifiedNamesOfEdgeClassesMatch() {
		GraphMarker<Object> marker = new GraphMarker<Object>(schemagraph);
		for (EdgeClass ec : schema.getEdgeClassesInTopologicalOrder()) {
			if (!ec.isInternal()) {
				String edgeClassesQualifiedName = ec.getQualifiedName();
				for (Vertex ecv : schemagraph
						.vertices((VertexClass) schemagraph.getSchema()
								.getAttributedElementClass(
										new QualifiedName("EdgeClass")))) {
					if (marker.getMark(ecv) == null) {
						try {
							if ( // the qualified name equals
							(((de.uni_koblenz.jgralab.grumlschema.EdgeClass) ecv)
									.getAttribute("qualifiedName")
									.equals(edgeClassesQualifiedName))
									// and the classes correspond to another
									&& ((ec.getClass().equals(
											EdgeClassImpl.class) && ecv
											.getClass()
											.equals(
													de.uni_koblenz.jgralab.grumlschema.impl.EdgeClassImpl.class))
											|| (ec.getClass().equals(
													AggregationClassImpl.class) && ecv
													.getClass()
													.equals(
															de.uni_koblenz.jgralab.grumlschema.impl.AggregationClassImpl.class)) || (ec
											.getClass().equals(
													CompositionClassImpl.class) && ecv
											.getClass()
											.equals(
													de.uni_koblenz.jgralab.grumlschema.impl.CompositionClassImpl.class)))) {
								marker.mark(ecv, new Object());
							}

						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}
		int markedSchemagraphEdgeClassVertices = marker.size();
		int schemagraphVertices = countClassVertices("EdgeClass");

		assertEquals(
				"Not all name attributes in the schemagraphs VertexClass vertices have a corresponding qualified name in the schemas VertexClasses.",
				markedSchemagraphEdgeClassVertices, schemagraphVertices);

	}

	private boolean sizeOfIterableEqualsInt(Iterable<?> itr, int size) {
		Iterator<?> iter = itr.iterator();
		try {
			for (int i = 0; i < size; i++) {
				iter.next();
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return !iter.hasNext();
	}

	private int countClassVertices(String attributedElementClassName) {
		int count = 0;
		AttributedElementClass aec = schemagraph.getSchema()
				.getAttributedElementClass(
						new QualifiedName(attributedElementClassName));
		if (aec instanceof VertexClass) {
			for (@SuppressWarnings("unused")
			Vertex v : schemagraph.vertices((VertexClass) aec)) {
				count++;
			}
		} else if (aec instanceof EdgeClass) {
			for (@SuppressWarnings("unused")
			Edge e : schemagraph.edges((EdgeClass) aec)) {
				count++;
			}
		}
		return count;
	}
}