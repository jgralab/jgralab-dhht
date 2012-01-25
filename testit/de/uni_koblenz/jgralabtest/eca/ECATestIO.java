package de.uni_koblenz.jgralabtest.eca;

import static org.junit.Assert.assertEquals;

import java.awt.PageAttributes.MediaType;
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User;

import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralabtest.eca.useractions.RevertEdgeChangingAction;
import de.uni_koblenz.jgralabtest.eca.userconditions.IsGreaterThan2012;

public class ECATestIO {

	private static SimpleLibraryGraph simlibgraph;
	private static User user1;
	private static User user2;
	private static Loans loans_u1_b1;

	public static String FOLDER_FOR_RULE_FILES = "testit/testdata/";

	@BeforeClass
	public static void setUp() {
		System.out.println("Start ECA IO Test.\n");
		JGraLab.setLogLevel(Level.OFF);
		initGraph();
	}

	@AfterClass
	public static void tearDown() {
		System.out.println("Finish ECA IO Test.\n");
	}

	@Test
	public void testSimpleSaveRule() {
		System.out.println("Saving an ECA Rule.");
		EventDescription bef_ev = new DeleteVertexEventDescription(
				EventDescription.EventTime.BEFORE, Book.class);
		Action bef_act = new PrintAction(
				"ECA Test Message: Book Vertex will become deleted.");
		ECARule bef_rule = new ECARule(bef_ev, bef_act);

		EventDescription aft_ev = new CreateVertexEventDescription(
				EventDescription.EventTime.AFTER, NewMedia.class);
		Condition aft_cond = new GreqlCondition("count( V{NewMedia} ) = 2");
		Action aft_act = new PrintAction(
				"ECA Test Message: New Medium after Condition Test created. "
						+ "This message should appear only once.");
		ECARule aft_rule = new ECARule(aft_ev, aft_cond, aft_act);

		ArrayList<ECARule> rules = new ArrayList<ECARule>();
		rules.add(bef_rule);
		rules.add(aft_rule);
		try {
			ECAIO.saveECArules(simlibgraph.getSchema(), FOLDER_FOR_RULE_FILES
					+ "testSaveRules1.eca", rules);
		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}

		System.out.println();
	}

	@Test
	public void testSimpleLoadRule() {
		System.out.println("Loading an ECA rule.");

		Book newBook = simlibgraph.createBook();
		List<ECARule> rules = null;
		try {

			rules = ECAIO.loadECArules(simlibgraph.getSchema(),
					FOLDER_FOR_RULE_FILES + "testSaveRules1.eca");

		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}

		ECARuleManager ecaRuleManager = (ECARuleManager) simlibgraph
				.getECARuleManager();
		for (ECARule rule : rules) {
			ecaRuleManager.addECARule(rule);
		}

		simlibgraph.deleteVertex(newBook);
		simlibgraph.createNewMedia();
		simlibgraph.createNewMedia();

		for (ECARule rule : rules) {
			ecaRuleManager.deleteECARule(rule);
		}

		System.out.println();
	}

	@Test
	public void saveRuleWithChangeAttributeEvent() {
		System.out.println("Saving a rule monitoring a ChangeAttributeEvent.");
		EventDescription bef_ev = new ChangeAttributeEventDescription(
				EventDescription.EventTime.BEFORE, Book.class, "title");
		Action bef_act = new PrintAction(
				"ECA Test Message: Title of Book Vertex will become changed.");
		ECARule bef_rule = new ECARule(bef_ev, bef_act);
		ArrayList<ECARule> rules = new ArrayList<ECARule>();
		rules.add(bef_rule);
		try {
			ECAIO.saveECArules(simlibgraph.getSchema(), FOLDER_FOR_RULE_FILES
					+ "testSaveRulesChangeAttribute", rules);
		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}
		System.out.println();
	}

	@Test
	public void loadRuleWithChangeAttributeEvent() {
		System.out.println("Loading a rule monitoring a ChangeAttributeEvent.");

		List<ECARule> rules = null;
		try {
			rules = ECAIO.loadECArules(simlibgraph.getSchema(),
					FOLDER_FOR_RULE_FILES + "testSaveRulesChangeAttribute");
		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}

		ECARuleManager ecaRuleManager = (ECARuleManager) simlibgraph
				.getECARuleManager();
		ecaRuleManager.addECARule(rules.get(0));

		Book book = simlibgraph.createBook();
		book.set_title("A new Book");

		ecaRuleManager.deleteECARule(rules.get(0));

		System.out.println();
	}

	@Test
	public void saveRuleWithContext() {
		System.out.println("Save ECA rule with context.");
		EventDescription aft_ev = new CreateVertexEventDescription(
				EventDescription.EventTime.AFTER, "V{Medium}");
		Action aft_act = new PrintAction(
				"ECA Test Message: New Medium created.");
		ECARule aft_rule = new ECARule(aft_ev, aft_act);

		ArrayList<ECARule> rules = new ArrayList<ECARule>();
		rules.add(aft_rule);
		try {
			ECAIO.saveECArules(simlibgraph.getSchema(), FOLDER_FOR_RULE_FILES
					+ "testSaveRules3.eca", rules);
		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}
		System.out.println();
	}

	@Test
	public void loadRuleWithContext() {
		System.out.println("Load rule with context.");
		List<ECARule> rules = null;
		try {
			rules = ECAIO.loadECArules(simlibgraph.getSchema(),
					FOLDER_FOR_RULE_FILES + "testSaveRules3.eca");
		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}
		ECARuleManager ecaRuleManager = (ECARuleManager) simlibgraph
				.getECARuleManager();
		ecaRuleManager.addECARule(rules.get(0));

		simlibgraph.createBook();

		ecaRuleManager.deleteECARule(rules.get(0));
		System.out.println();
	}

	@Test
	public void saveRuleWithOwnAction() {
		System.out.println("Save rule with own action.");

		EventDescription aft_ev = new ChangeEdgeEventDescription(
				EventDescription.EventTime.AFTER, Loans.class, EdgeEnd.ANY);
		Condition aft_cond = new GreqlCondition(
				"startVertex(context).name = 'Stephanie Plum'");
		Action aft_act = new RevertEdgeChangingAction();
		ECARule aft_rule = new ECARule(aft_ev, aft_cond, aft_act);

		ArrayList<ECARule> rules = new ArrayList<ECARule>();
		rules.add(aft_rule);
		try {
			ECAIO.saveECArules(simlibgraph.getSchema(), FOLDER_FOR_RULE_FILES
					+ "testSaveRules4.eca", rules);
		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}

		System.out.println();
	}

	@Test
	public void loadRuleWithOwnAction() {
		System.out.println("Load rule with own action.");

		List<ECARule> rules = null;
		try {
			rules = ECAIO.loadECArules(simlibgraph.getSchema(),
					FOLDER_FOR_RULE_FILES + "testSaveRules4.eca");
		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}
		ECARuleManager ecaRuleManager = (ECARuleManager) simlibgraph
				.getECARuleManager();
		ecaRuleManager.addECARule(rules.get(0));

		loans_u1_b1.setAlpha(user2);

		assertEquals(loans_u1_b1.getAlpha(), user1);

		ecaRuleManager.deleteECARule(rules.get(0));

		System.out.println();
	}

	@Test
	public void saveRuleWithOwnCondition(){
		System.out.println("Save rule with own Condition.");
		EventDescription aft_ev = new ChangeAttributeEventDescription(
				EventDescription.EventTime.AFTER, Magazin.class, "year");
		Condition cond = new IsGreaterThan2012();
		Action act = new PrintAction("new year is greater than 2012");
		ECARule aft_rule = new ECARule(aft_ev, cond,act);
		
		ArrayList<ECARule> rules = new ArrayList<ECARule>();
		rules.add(aft_rule);
		try {
			ECAIO.saveECArules(simlibgraph.getSchema(), FOLDER_FOR_RULE_FILES
					+ "testSaveRules5.eca", rules);
		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}

		System.out.println();
	}
	@Test
	public void loadRuleWithOwnCondition() {
		System.out.println("Load rule with own condition.");

		List<ECARule> rules = null;
		try {
			rules = ECAIO.loadECArules(simlibgraph.getSchema(),
					FOLDER_FOR_RULE_FILES + "testSaveRules5.eca");
		} catch (ECAIOException e) {
			e.printStackTrace();
			assert false;
		}
		ECARuleManager ecaRuleManager = (ECARuleManager) simlibgraph
				.getECARuleManager();
		ecaRuleManager.addECARule(rules.get(0));

		simlibgraph.getFirstMagazin().set_year(2013);

		ecaRuleManager.deleteECARule(rules.get(0));

		System.out.println();
	}
	
	/**
	 * Create the Graph for testing
	 */
	public static void initGraph() {
		SimpleLibraryGraph graph = SimpleLibrarySchema.instance()
				.createSimpleLibraryGraph();
		graph.set_version("v1.0");

		// Library
		Library lib = graph.createLibrary();
		lib.set_name("Bibliothequa");

		// Users
		user1 = graph.createUser();
		user1.set_name("Martin King");

		user2 = graph.createUser();
		user2.set_name("Stephanie Plum");

		// Media
		Book book1 = graph.createBook();
		book1.set_title("Lord of the Rings");
		book1.set_author("J.R.R. Tokien");

		Magazin magazin1 = graph.createMagazin();
		magazin1.set_title("CT");
		magazin1.set_publisher("Blub");
		magazin1.set_year(2011);

		NewMedia newmedia1 = graph.createNewMedia();
		newmedia1.set_title("Rush Hour 1");
		newmedia1.set_type(MediaType.DVD);

		// Media are in Library
		lib.add_media(book1);
		lib.add_media(magazin1);
		lib.add_media(newmedia1);

		// user1 loans book1
		loans_u1_b1 = graph.createLoans(user1, book1);
		loans_u1_b1.set_date(new Date(1, 1, 2011));

		// user 2 loans magazin1
		Loans loans_u2_m1 = graph.createLoans(user2, magazin1);
		loans_u2_m1.set_date(new Date(5, 5, 2011));

		simlibgraph = graph;
	}

}
