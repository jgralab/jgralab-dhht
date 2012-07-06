package de.uni_koblenz.jgralabtest.greql2;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pcollections.ArrayPSet;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.codegenerator.CodeGeneratorConfiguration;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.impl.GraphFactoryImpl;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralabtest.dhht.schema.EdgeType1;
import de.uni_koblenz.jgralabtest.dhht.schema.EdgeType1_incidence1;
import de.uni_koblenz.jgralabtest.dhht.schema.EdgeType1_incidence2;
import de.uni_koblenz.jgralabtest.dhht.schema.EdgeType1_incidence3;
import de.uni_koblenz.jgralabtest.dhht.schema.EdgeType1_incidence4;
import de.uni_koblenz.jgralabtest.dhht.schema.EdgeType2;
import de.uni_koblenz.jgralabtest.dhht.schema.EdgeType2_incidence5;
import de.uni_koblenz.jgralabtest.dhht.schema.EdgeType2_incidence6;
import de.uni_koblenz.jgralabtest.dhht.schema.EdgeType2_incidence7;
import de.uni_koblenz.jgralabtest.dhht.schema.VertexType1;
import de.uni_koblenz.jgralabtest.dhht.schema.VertexType2;
import de.uni_koblenz.jgralabtest.dhht.schema.VertexType3;
import de.uni_koblenz.jgralabtest.dhht.schema.VertexType4;
import de.uni_koblenz.jgralabtest.dhht.schema.VertexType5;
import de.uni_koblenz.jgralabtest.dhht.schema.impl.mem.DHHTTestGraphImpl;

public class DHHTEvaluatorTest {

	private static Graph graph = null;
	private static Vertex[] vertices = new Vertex[8];
	private static Edge[] edges = new Edge[2];

	@BeforeClass
	public static void createGraph() {
		try {
			Schema schema = GraphIO
					.loadSchemaFromFile("C:\\Users\\Jon\\git\\jgralab-dhht\\testit\\testschemas\\dhhttestschema2.dhhtg");
			// schema.compile(CodeGeneratorConfiguration.FULL);
			schema.commit(CodeGeneratorConfiguration.FULL);
			graph = DHHTTestGraphImpl.createDHHTTestGraphImpl(
					GraphFactoryImpl.generateUniqueGraphId(), 100, 100);
			vertices[0] = graph.createVertex(VertexType1.class);
			vertices[1] = graph.createVertex(VertexType2.class);
			vertices[2] = graph.createVertex(VertexType3.class);
			vertices[3] = graph.createVertex(VertexType3.class);
			vertices[4] = graph.createVertex(VertexType4.class);
			vertices[5] = graph.createVertex(VertexType5.class);
			vertices[6] = graph.createVertex(VertexType2.class);
			vertices[7] = graph.createVertex(VertexType3.class);
			vertices[2].setSigma(vertices[1]);
			vertices[3].setSigma(vertices[1]);
			vertices[4].setSigma(vertices[1]);
			vertices[5].setSigma(vertices[1]);
			vertices[7].setSigma(vertices[6]);

			vertices[0].setKappa(2);
			vertices[1].setKappa(2);
			vertices[2].setKappa(1);
			vertices[3].setKappa(1);
			vertices[4].setKappa(1);
			vertices[5].setKappa(0);
			vertices[6].setKappa(2);
			vertices[7].setKappa(1);

			edges[0] = graph.createEdge(EdgeType1.class);
			edges[1] = graph.createEdge(EdgeType2.class);
			edges[0].setSigma(vertices[1]);
			edges[0].connect(EdgeType1_incidence1.class, vertices[2]);
			edges[0].connect(EdgeType1_incidence1.class, vertices[3]);
			edges[0].connect(EdgeType1_incidence2.class, vertices[4]);
			edges[0].connect(EdgeType1_incidence3.class, vertices[5]);
			edges[0].connect(EdgeType1_incidence4.class, vertices[2]);
			edges[0].connect(EdgeType1_incidence4.class, vertices[3]);
			edges[1].connect(EdgeType2_incidence5.class, vertices[0]);
			edges[1].connect(EdgeType2_incidence6.class, vertices[2]);
			edges[1].connect(EdgeType2_incidence6.class, vertices[7]);
			edges[1].connect(EdgeType2_incidence7.class, vertices[6]);

			edges[0].setKappa(1);
			edges[1].setKappa(2);

		} catch (GraphIOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTextExampleEvaluation() {
		GreqlEvaluator eval = new GreqlEvaluator(
				"(kappa(1) : getVertex(1) -->{EdgeType2}  --> +>{incidence1, incidence2}+ [<+>{incidence3}])",
				graph, null);
		eval.startEvaluation();
		Object result = eval.getResult();
		assertNotNull(result);
		ArrayPSet<GraphElement<?, ?, ?, ?>> resultList = (ArrayPSet<GraphElement<?, ?, ?, ?>>) result;
		assertTrue(resultList.contains(vertices[4]));
		assertTrue(resultList.contains(edges[0]));
		assertTrue(resultList.size() == 2);
	}

	@Test
	public void testSimpleIncidenceEvaluation() {
		GreqlEvaluator eval = new GreqlEvaluator("getEdge(1) +>", graph, null);
		eval.startEvaluation();
		Object result = eval.getResult();
		assertNotNull(result);
		ArrayPSet<GraphElement<?, ?, ?, ?>> resultList = (ArrayPSet<GraphElement<?, ?, ?, ?>>) result;
		assertTrue(resultList.size() == 4);
		assertTrue(resultList.contains(vertices[2]));
		assertTrue(resultList.contains(vertices[3]));
		assertTrue(resultList.contains(vertices[4]));
		assertTrue(resultList.contains(vertices[5]));
	}

	@Test
	public void testSimpleIncidencePlusEvaluation() {
		GreqlEvaluator eval = new GreqlEvaluator(
				"(kappa(1) : getVertex(4) +>+)", graph, null);
		eval.startEvaluation();
		Object result = eval.getResult();
		assertNotNull(result);
		ArrayPSet<GraphElement<?, ?, ?, ?>> resultList = (ArrayPSet<GraphElement<?, ?, ?, ?>>) result;
		assertTrue(resultList.size() == 4);
		assertTrue(resultList.contains(edges[0]));
		assertTrue(resultList.contains(vertices[3]));
		assertTrue(resultList.contains(vertices[4]));
		assertTrue(resultList.contains(vertices[2]));
		assertTrue(!resultList.contains(vertices[5]));
	}

	@Test
	public void testLocalSubgraphExpression() {
		GreqlEvaluator eval = new GreqlEvaluator("(local : getVertex(3) -->)",
				graph, null);
		eval.startEvaluation();
		Object result = eval.getResult();
		ArrayPSet<GraphElement<?, ?, ?, ?>> resultList = (ArrayPSet<GraphElement<?, ?, ?, ?>>) result;
		assertTrue(resultList.size() == 4);
		assertTrue(resultList.contains(vertices[2]));
		assertTrue(resultList.contains(vertices[3]));
		assertTrue(resultList.contains(vertices[4]));
		assertTrue(resultList.contains(vertices[5]));
	}

	@Test
	public void testNestedSubgraphExpression() {
		GreqlEvaluator eval = new GreqlEvaluator(
				"(subgraph(getVertex(2)) : getEdge(1) +> <+>)", graph, null);
		eval.startEvaluation();
		Object result = eval.getResult();
		ArrayPSet<GraphElement<?, ?, ?, ?>> resultList = (ArrayPSet<GraphElement<?, ?, ?, ?>>) result;
		assertTrue(resultList.size() == 1);
		assertTrue(resultList.contains(edges[0]));
	}

	@Test
	public void testIsReachable1() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("v1", vertices[0]);
		variables.put("v2", vertices[5]);
		GreqlEvaluator eval = new GreqlEvaluator(
				"getVertex(1) +>+ getVertex(2)", graph, variables);
		eval.startEvaluation();
		Object result = eval.getResult();
		assertTrue(!((Boolean) result));
	}

	@Test
	public void testIsReachable2() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("v1", vertices[0]);
		variables.put("v2", vertices[5]);
		GreqlEvaluator eval = new GreqlEvaluator(
				"getVertex(1) +>+ getVertex(6)", graph, variables);
		eval.startEvaluation();
		Object result = eval.getResult();
		assertTrue((Boolean) result);
	}
}
