package de.uni_koblenz.jgralabtest.utilities.rsa2tg;

import java.io.IOException;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.utilities.rsa2tg.Rsa2Tg;
import de.uni_koblenz.jgralabtest.utilities.tg2schemagraph.CompareSchemaWithSchemaGraph;

public class Rsa2TgTest {

	private static String folder = "testit/testschemas/rsa-xmi/";

	private Rsa2Tg r;

	{
		r = new Rsa2Tg();

		r.setUseFromRole(true);
		r.setRemoveUnusedDomains(true);
		r.setUseNavigability(true);
	}

	@BeforeClass
	public static void setUp() {
		JGraLab.setLogLevel(Level.OFF);
	}

	@AfterClass
	public static void tearDown() {
		System.out.println("fini.");
	}

	public void testASchema(String filename) throws GraphIOException,
			IOException, SAXException, ParserConfigurationException,
			XMLStreamException {

		// Loads the SchemaGraph
		System.out.println("Testing with: " + folder + filename);
		System.out
				.print("Loading XMI, creating SchemaGraph and creating TG-file... ");
		r.process(folder + filename, null, null, null);
		System.out.println("\tdone");

		de.uni_koblenz.jgralab.grumlschema.structure.Schema gSchema = r
				.getSchemaGraph().getFirstSchema();

		// Converts the SchemaGraph to a Schema
		System.out.print("Loading Schema from File ...");
		Schema schema = GraphIO.loadSchemaFromFile(folder + gSchema.getName()
				+ ".rsa.tg");
		System.out.println("\t\t\t\t\tdone");

		// Compares the SchemaGraph with the created Schema
		System.out.print("Testing ...");
		new CompareSchemaWithSchemaGraph().compare(schema, r.getSchemaGraph());
		System.out.println("\t\t\t\t\t\t\tdone");
		System.out.println();
	}

	@Test
	public void testTest() throws GraphIOException, IOException, SAXException,
			ParserConfigurationException, XMLStreamException {
		testASchema("test.xmi");

	}

	@Test
	public void testgrUML_M3() throws GraphIOException, IOException,
			SAXException, ParserConfigurationException, XMLStreamException {
		testASchema("grUML-M3.xmi");
	}

	@Test
	public void testOsmSchema() throws GraphIOException, IOException,
			SAXException, ParserConfigurationException, XMLStreamException {
		testASchema("OsmSchema.xmi");
	}

	@Test
	public void testSoamig() throws GraphIOException, IOException,
			SAXException, ParserConfigurationException, XMLStreamException {
		testASchema("soamig.xmi");
	}
}