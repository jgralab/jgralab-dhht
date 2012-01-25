package de.uni_koblenz.jgralabtest.eca;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.gretl.eca.GretlTransformAction;
import de.uni_koblenz.jgralabtest.gretl.SimpleCopyTransformation;

public class ECATestGretlAction {

	private static AddressBookGraph testGraph;

	@BeforeClass
	public static void setUp() {
		System.out.println("Start ECA Test with Gretl Transformation Action.");
		JGraLab.setLogLevel(Level.INFO);
		initGraph();
	}

	private static void initGraph() {
		AddressBookGraph g = AddressBookSchema.instance()
				.createAddressBookGraph();
		AddressBook ab1 = g.createAddressBook();
		ab1.set_name("Democrats");
		Contact c1 = g.createContact();
		c1.set_name("Barack Obama");
		c1.set_address("Honolulu, Hawaii, USA");
		g.createContains(ab1, c1);
		Contact c2 = g.createContact();
		c2.set_name("Bill Clinton");
		c2.set_address("Hope, Arkansas, USA");
		g.createContains(ab1, c2);

		AddressBook ab2 = g.createAddressBook();
		ab2.set_name("Republicans");
		Contact c3 = g.createContact();
		c3.set_name("George Bush");
		c3.set_address("New Haven, Connecticut, USA");
		g.createContains(ab2, c3);
		Contact c4 = g.createContact();
		c4.set_name("Ronald Reagan");
		c4.set_address("Tampico, Illinois, USA");
		g.createContains(ab2, c4);

		testGraph = g;
	}

	@AfterClass
	public static void tearDown() {
		System.out
				.println("Finish ECA Test with Gretl Transformation Action.\n");
		System.out.println("-----------------------------------------------");
		System.out.println();
	}

	@Test
	public void testDoGretlTransformAsAction() {
		Contact c5 = testGraph.createContact();

		EventDescription bef_ev = new DeleteVertexEventDescription(
				EventDescription.EventTime.BEFORE, Contact.class);
		Action bef_act = new GretlTransformAction(
				SimpleCopyTransformation.class);
		ECARule bef_rule = new ECARule(bef_ev, bef_act);

		((ECARuleManager) testGraph.getECARuleManager()).addECARule(bef_rule);

		int oldVCount = testGraph.getVCount();

		testGraph.deleteVertex(c5);

		// Duplicate all Vertices and then take the deleted one away
		assertEquals(testGraph.getVCount(), oldVCount * 2 - 1);

		((ECARuleManager) testGraph.getECARuleManager())
				.deleteECARule(bef_rule);

	}

	@Test
	public void testSaveGretlTransformAction() {
		System.out.println("Save rule with GretlTransformAction.");
		EventDescription bef_ev = new DeleteVertexEventDescription(
				EventDescription.EventTime.BEFORE, Contact.class);
		Action bef_act = new GretlTransformAction(
				SimpleCopyTransformation.class);
		ECARule bef_rule = new ECARule(bef_ev, bef_act);

		ArrayList<ECARule> rules = new ArrayList<ECARule>();
		rules.add(bef_rule);

		try {
			ECAIO.saveECArules(testGraph.getSchema(),
					ECATestIO.FOLDER_FOR_RULE_FILES + "testSaveRules2.eca",
					rules);
		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}
	}

	@Test
	public void testLoadGretlTransformAction() {
		System.out.println("Load rule with GretlTransformAction.");
		Contact c5 = testGraph.createContact();

		try {
			List<ECARule> rules = ECAIO.loadECArules(testGraph.getSchema(),
					ECATestIO.FOLDER_FOR_RULE_FILES + "testSaveRules2.eca");
			ECARuleManager ecaRuleManager = (ECARuleManager) testGraph
					.getECARuleManager();
			for (ECARule rule : rules) {
				ecaRuleManager.addECARule(rule);
			}
		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}

		int oldVCount = testGraph.getVCount();

		testGraph.deleteVertex(c5);

		// Duplicate all Vertices and then take the deleted one away
		assertEquals(testGraph.getVCount(), oldVCount * 2 - 1);
	}
}
