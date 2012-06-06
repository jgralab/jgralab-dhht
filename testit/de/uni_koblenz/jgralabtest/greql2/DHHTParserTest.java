package de.uni_koblenz.jgralabtest.greql2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uni_koblenz.jgralab.greql2.exception.ParsingException;
import de.uni_koblenz.jgralab.greql2.parser.GreqlParser;
import de.uni_koblenz.jgralab.greql2.schema.Comprehension;
import de.uni_koblenz.jgralab.greql2.schema.ElementRestriction;
import de.uni_koblenz.jgralab.greql2.schema.ElementTypeRestriction;
import de.uni_koblenz.jgralab.greql2.schema.ForwardElementSet;
import de.uni_koblenz.jgralab.greql2.schema.FunctionApplication;
import de.uni_koblenz.jgralab.greql2.schema.GreqlSyntaxGraph;
import de.uni_koblenz.jgralab.greql2.schema.IncDirection;
import de.uni_koblenz.jgralab.greql2.schema.IncidenceDirection;
import de.uni_koblenz.jgralab.greql2.schema.IncidenceRestriction;
import de.uni_koblenz.jgralab.greql2.schema.IsConstrainedExpressionOf;
import de.uni_koblenz.jgralab.greql2.schema.IsIdOfPartialGraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.IsIncTypeIdOf;
import de.uni_koblenz.jgralab.greql2.schema.IsPathOf;
import de.uni_koblenz.jgralab.greql2.schema.IsStartExprOf;
import de.uni_koblenz.jgralab.greql2.schema.IsSubgraphDefinitionOf;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeIdOfRestriction;
import de.uni_koblenz.jgralab.greql2.schema.KappaSubgraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.LocalSubgraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.NestedSubgraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.PartialSubgraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.SimpleEdgePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.SimpleIncidencePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.SubgraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.SubgraphExpression;
import de.uni_koblenz.jgralab.greql2.schema.TypeId;

public class DHHTParserTest {

	private GreqlSyntaxGraph parseQuery(String query, String file)
			throws ParsingException {
		GreqlSyntaxGraph graph = GreqlParser.parse(query);
		return graph;
	}

	private GreqlSyntaxGraph parseQuery(String query) throws ParsingException {
		return parseQuery(query, null);
	}

	@Test
	public void testSimpleEdgePathDescription() {
		GreqlSyntaxGraph graph = parseQuery("using v: v --> ");
		SimpleEdgePathDescription pathDescr = graph
				.getFirstSimpleEdgePathDescription();
		assertNotNull(pathDescr);
	}

	@Test
	public void testSimpleIncidencePathDescription() {
		GreqlSyntaxGraph graph = parseQuery("using v: v +>");
		SimpleIncidencePathDescription pathDescr = graph
				.getFirstSimpleIncidencePathDescription();
		assertNotNull(pathDescr);
		IncidenceDirection incDirection = graph.getFirstIncidenceDirection();
		assertNotNull(incDirection);
		assertTrue(incDirection.get_dir() == IncDirection.OUT);

		graph = parseQuery("using v: v <+");
		pathDescr = graph.getFirstSimpleIncidencePathDescription();
		assertNotNull(pathDescr);
		incDirection = graph.getFirstIncidenceDirection();
		assertNotNull(incDirection);
		assertTrue(incDirection.get_dir() == IncDirection.IN);

		graph = parseQuery("using v: v <+>");
		pathDescr = graph.getFirstSimpleIncidencePathDescription();
		assertNotNull(pathDescr);
		incDirection = graph.getFirstIncidenceDirection();
		assertNotNull(incDirection);
		assertTrue(incDirection.get_dir() == IncDirection.BOTH);

		graph = parseQuery("using v: v <+{IncType1}");
		pathDescr = graph.getFirstSimpleIncidencePathDescription();
		assertNotNull(pathDescr);
		incDirection = graph.getFirstIncidenceDirection();
		assertNotNull(incDirection);
		assertTrue(incDirection.get_dir() == IncDirection.IN);
		IncidenceRestriction incRestriction = graph
				.getFirstIncidenceRestriction();
		assertNotNull(incRestriction);
		IsIncTypeIdOf typeIdOf = graph.getFirstIsIncTypeIdOf();
		assertNotNull(typeIdOf);
		TypeId typeId = graph.getFirstTypeId();
		assertNotNull(typeId);
		assertEquals("IncType1", typeId.get_name());

	}

	@Test(expected = ParsingException.class)
	public void testSimpleIncidencePathDescriptionWithRestrictions() {
		GreqlSyntaxGraph graph = parseQuery("using v: v <+{IncType1, IncType2, BlaType, AnotherType} & {VE:EdgeType}");
		SimpleIncidencePathDescription pathDescr = graph
				.getFirstSimpleIncidencePathDescription();
		assertNotNull(pathDescr);
		IncidenceDirection incDirection = graph.getFirstIncidenceDirection();
		assertNotNull(incDirection);
		assertTrue(incDirection.get_dir() == IncDirection.IN);
		IncidenceRestriction incRestriction = graph
				.getFirstIncidenceRestriction();
		assertNotNull(incRestriction);
		IsIncTypeIdOf typeIdOf = graph.getFirstIsIncTypeIdOf();
		assertNotNull(typeIdOf);
		TypeId typeId = graph.getFirstTypeId();
		assertNotNull(typeId);
		assertEquals("IncType1", typeId.get_name());
		assertEquals("IncType2", typeId.getNextTypeId().get_name());
		assertEquals("BlaType", typeId.getNextTypeId().getNextTypeId()
				.get_name());
		assertEquals("AnotherType", typeId.getNextTypeId().getNextTypeId()
				.getNextTypeId().get_name());
		ElementRestriction elementRest = graph.getFirstElementRestriction();
		assertNotNull(elementRest);
	}

	@Test
	public void testSimpleIncidencePathDescriptionWithGoalRestriction() {
		GreqlSyntaxGraph graph = parseQuery("getVertex(1) +> {V:V}");
		ForwardElementSet forwardSet = graph.getFirstForwardElementSet();
		assertNotNull(forwardSet);
		FunctionApplication func = graph.getFirstFunctionApplication();
		assertNotNull(func);
		IsStartExprOf startOf = graph.getFirstIsStartExprOf();
		assertNotNull(startOf);
		assertTrue(startOf.getAlpha() == func);
		assertTrue(startOf.getOmega() == forwardSet);

		SimpleIncidencePathDescription pathDescr = graph
				.getFirstSimpleIncidencePathDescription();
		assertNotNull(pathDescr);

		IsPathOf pathOf = graph.getFirstIsPathOf();
		assertNotNull(pathOf);
		assertTrue(pathOf.getAlpha() == pathDescr);
		assertTrue(pathOf.getOmega() == forwardSet);

		IncidenceDirection incDirection = graph.getFirstIncidenceDirection();
		assertNotNull(incDirection);
		assertTrue(incDirection.get_dir() == IncDirection.OUT);
		ElementRestriction elementRest = graph.getFirstElementRestriction();
		assertNotNull(elementRest);
		assertTrue(elementRest instanceof ElementTypeRestriction);
		IsTypeIdOfRestriction isIdOfRestr = graph
				.getFirstIsTypeIdOfRestriction();
		assertNotNull(isIdOfRestr);
		assertTrue(isIdOfRestr.getOmega() == elementRest);
		TypeId type = isIdOfRestr.getAlpha();
		assertNotNull(type);
		assertTrue(type.get_name().equals("V"));
		isIdOfRestr = isIdOfRestr.getNextIsTypeIdOfRestriction();
		assertTrue(isIdOfRestr == null);
		pathDescr = pathDescr.getNextSimpleIncidencePathDescription();
		assertTrue(pathDescr == null);
	}

	@Test
	public void testSubgraphExpression() {
		GreqlSyntaxGraph graph = parseQuery("using v: (kappa(1) : v +>)");
		SubgraphExpression subExpr = graph.getFirstSubgraphExpression();
		assertNotNull(subExpr);
		SubgraphDefinition subDef = graph.getFirstSubgraphDefinition();
		assertNotNull(subDef);
		assertTrue(subDef instanceof KappaSubgraphDefinition);
		assertTrue(((KappaSubgraphDefinition) subDef).get_kappa() == 1);
		IsSubgraphDefinitionOf subDefOf = graph
				.getFirstIsSubgraphDefinitionOf();
		assertNotNull(subDefOf);
		assertEquals(subDefOf.getAlpha(), subDef);
		assertEquals(subDefOf.getOmega(), subExpr);

		graph = parseQuery("(local : true)");
		subExpr = graph.getFirstSubgraphExpression();
		assertNotNull(subExpr);
		subDef = graph.getFirstSubgraphDefinition();
		assertNotNull(subDef);
		assertTrue(subDef instanceof LocalSubgraphDefinition);
		subDefOf = graph.getFirstIsSubgraphDefinitionOf();
		assertNotNull(subDefOf);
		assertEquals(subDefOf.getAlpha(), subDef);
		assertEquals(subDefOf.getOmega(), subExpr);

		graph = parseQuery("(partial(2) : true)");
		subExpr = graph.getFirstSubgraphExpression();
		assertNotNull(subExpr);
		subDef = graph.getFirstSubgraphDefinition();
		assertNotNull(subDef);
		assertTrue(subDef instanceof PartialSubgraphDefinition);
		subDefOf = graph.getFirstIsSubgraphDefinitionOf();
		assertNotNull(subDefOf);
		assertEquals(subDefOf.getAlpha(), subDef);
		assertEquals(subDefOf.getOmega(), subExpr);

		graph = parseQuery("using v: (subgraph(v) : from e:E report e end)");
		subExpr = graph.getFirstSubgraphExpression();
		assertNotNull(subExpr);
		subDef = graph.getFirstSubgraphDefinition();
		assertNotNull(subDef);
		assertTrue(subDef instanceof NestedSubgraphDefinition);
		subDefOf = graph.getFirstIsSubgraphDefinitionOf();
		assertNotNull(subDefOf);
		assertTrue(subDefOf.getAlpha() == subDef);
		assertTrue(subDefOf.getOmega() == subExpr);
		Comprehension comp = graph.getFirstComprehension();
		assertNotNull(comp);
		IsConstrainedExpressionOf isConstExpr = graph
				.getFirstIsConstrainedExpressionOf();
		assertNotNull(isConstExpr);
		assertEquals(isConstExpr.getAlpha(), comp);
		assertEquals(isConstExpr.getOmega(), subExpr);
	}

	@Test(expected = ParsingException.class)
	public void testImplementationExampleExpression() {
		GreqlSyntaxGraph graph = parseQuery("(partial(1): (kappa(1): v1 -->{EdgeType2} {V:v3} --> +>{Incidence1, Incidence2}+ [<+>{Incidence3}]))");

		// Test the partial(1)
		SubgraphExpression subExpr = graph.getFirstSubgraphExpression();
		assertNotNull(subExpr);
		SubgraphDefinition subDef = graph.getFirstSubgraphDefinition();
		assertNotNull(subDef);
		assertTrue(subDef instanceof PartialSubgraphDefinition);
		IsSubgraphDefinitionOf subDefOf = graph
				.getFirstIsSubgraphDefinitionOf();
		assertNotNull(subDefOf);
		assertTrue(subDefOf.getAlpha() == subDef);
		assertTrue(subDefOf.getOmega() == subExpr);
		IsIdOfPartialGraphDefinition idOfPartial = graph
				.getFirstIsIdOfPartialGraphDefinition();
		assertNotNull(idOfPartial);
		assertTrue(idOfPartial.getOmega() == subDef);
		assertTrue(idOfPartial.getAlpha().get_intValue() == 1);

		// Test the kappa(1)
		subExpr = subExpr.getNextSubgraphExpression();
		assertNotNull(subExpr);
		subDef = subDef.getNextSubgraphDefinition();
		assertNotNull(subDef);
		assertTrue(subDef instanceof KappaSubgraphDefinition);
		subDefOf = subDefOf.getNextIsSubgraphDefinitionOf();
		assertNotNull(subDefOf);
		assertTrue(subDefOf.getAlpha() == subDef);
		assertTrue(subDefOf.getOmega() == subExpr);
		assertTrue(((KappaSubgraphDefinition) subDef).get_kappa() == 1);
	}
}
