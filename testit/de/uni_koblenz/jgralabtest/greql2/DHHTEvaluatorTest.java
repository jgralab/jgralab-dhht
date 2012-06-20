package de.uni_koblenz.jgralabtest.greql2;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.codegenerator.CodeGeneratorConfiguration;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.impl.GraphFactoryImpl;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralabtest.dhht.schema.BusinessProcess;
import de.uni_koblenz.jgralabtest.dhht.schema.TraceabilityLink;
import de.uni_koblenz.jgralabtest.dhht.schema.TraceabilityLink_source;
import de.uni_koblenz.jgralabtest.dhht.schema.TraceabilityLink_target;
import de.uni_koblenz.jgralabtest.dhht.schema.impl.mem.DHHTTestGraphImpl;

public class DHHTEvaluatorTest {

	private static Graph graph = null;

	@BeforeClass
	public static void setUpClass() {
		try {
			Schema schema = GraphIO
					.loadSchemaFromFile("C:\\Users\\Jon\\git\\jgralab-dhht\\testit\\testgraphs\\dhhttestgraph.tg");
			// schema.compile(CodeGeneratorConfiguration.FULL);
			schema.commit(CodeGeneratorConfiguration.FULL);
			graph = DHHTTestGraphImpl.createDHHTTestGraphImpl(
					GraphFactoryImpl.generateUniqueGraphId(), 100, 100);
			BusinessProcess process1 = graph
					.createVertex(BusinessProcess.class);
			BusinessProcess process2 = graph
					.createVertex(BusinessProcess.class);
			TraceabilityLink edge1 = graph.createEdge(TraceabilityLink.class);
			graph.connect(TraceabilityLink_source.class, process1, edge1);
			graph.connect(TraceabilityLink_target.class, process2, edge1);
		} catch (GraphIOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSimpleQueryEvaluation() {
		GreqlEvaluator eval = new GreqlEvaluator("getVertex(1) +>+", graph,
				null);
		eval.startEvaluation();
		Object result = eval.getResult();
		assertNotNull(result);
		System.out.println(result);
	}
}
