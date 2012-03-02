package de.uni_koblenz.jgralab.greql2.executable;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.pcollections.PSet;

import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.ObjectGraphMarker;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.executable.queries.SampleQuery;
import de.uni_koblenz.jgralab.greql2.parser.GreqlParser;
import de.uni_koblenz.jgralab.greql2.schema.GreqlSyntaxGraph;


public class ExecutableGreqlTest {

	@Test
	public void testSimpleFunction() {
		String query = "2 + 3";
		GreqlEvaluator eval = new GreqlEvaluator(query, null, null);
		ObjectGraphMarker<Vertex, VertexEvaluator> graphMarker = eval.getVertexEvaluators();
		GreqlSyntaxGraph queryGraph = eval.getSyntaxGraph();
		GreqlCodeGenerator greqlcodeGen = new GreqlCodeGenerator(queryGraph, graphMarker);
		try {
			greqlcodeGen.createFiles("/Users/dbildh/repos/git/jgralab-dhht/src/");
		} catch (GraphIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testGenerateComprehension() {
		//String query = "using X,Y: from x:X, y:Y with (y % 2 <> 1) and (x % 3 = 0) reportList x*y end";
		String query = "using X,Y: from x:X, y:Y with (true) and true reportList x*y end";
		GreqlEvaluator eval = new GreqlEvaluator(query, null, null);
		ObjectGraphMarker<Vertex, VertexEvaluator> graphMarker = eval.getVertexEvaluators();
		GreqlSyntaxGraph queryGraph = eval.getSyntaxGraph();
		GreqlCodeGenerator greqlcodeGen = new GreqlCodeGenerator(queryGraph, graphMarker);
		try {
			greqlcodeGen.createFiles("/Users/dbildh/repos/git/jgralab-dhht/src/");
		} catch (GraphIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenerateForallExpression() {
		//String query = "using X,Y: from x:X, y:Y with (y % 2 <> 1) and (x % 3 = 0) reportList x*y end";
		String query = "using X,Y: forall x:X, y:Y @ x*y > 0";
		GreqlEvaluator eval = new GreqlEvaluator(query, null, null);
		ObjectGraphMarker<Vertex, VertexEvaluator> graphMarker = eval.getVertexEvaluators();
		GreqlSyntaxGraph queryGraph = eval.getSyntaxGraph();
		GreqlCodeGenerator greqlcodeGen = new GreqlCodeGenerator(queryGraph, graphMarker);
		try {
			greqlcodeGen.createFiles("/Users/dbildh/repos/git/jgralab-dhht/src/");
		} catch (GraphIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenerateListConstruction() {
		String query = "list(1,2,3)";
		GreqlEvaluator eval = new GreqlEvaluator(query, null, null);
		ObjectGraphMarker<Vertex, VertexEvaluator> graphMarker = eval.getVertexEvaluators();
		GreqlSyntaxGraph queryGraph = eval.getSyntaxGraph();
		GreqlCodeGenerator greqlcodeGen = new GreqlCodeGenerator(queryGraph, graphMarker);
		try {
			greqlcodeGen.createFiles("/Users/dbildh/repos/git/jgralab-dhht/src/");
		} catch (GraphIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenerateListRangeConstruction() {
		String query = "list(1..1000)";
		GreqlEvaluator eval = new GreqlEvaluator(query, null, null);
		ObjectGraphMarker<Vertex, VertexEvaluator> graphMarker = eval.getVertexEvaluators();
		GreqlSyntaxGraph queryGraph = eval.getSyntaxGraph();
		GreqlCodeGenerator greqlcodeGen = new GreqlCodeGenerator(queryGraph, graphMarker);
		try {
			greqlcodeGen.createFiles("/Users/dbildh/repos/git/jgralab-dhht/src/");
		} catch (GraphIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenerateMapComprehension() {
		String query = "using X,Y: from x:X, y:Y reportMap y->x end";
		GreqlEvaluator eval = new GreqlEvaluator(query, null, null);
		ObjectGraphMarker<Vertex, VertexEvaluator> graphMarker = eval.getVertexEvaluators();
		GreqlSyntaxGraph queryGraph = eval.getSyntaxGraph();
		GreqlCodeGenerator greqlcodeGen = new GreqlCodeGenerator(queryGraph, graphMarker);
		try {
			greqlcodeGen.createFiles("/Users/dbildh/repos/git/jgralab-dhht/src/");
		} catch (GraphIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenerateVertexSetExpression() {
		//String query = "using X,Y: from x:X, y:Y with (y % 2 <> 1) and (x % 3 = 0) reportList x*y end";
		String query = "from v:V{MyVertex} report v end";
		GreqlEvaluator eval = new GreqlEvaluator(query, null, null);
		ObjectGraphMarker<Vertex, VertexEvaluator> graphMarker = eval.getVertexEvaluators();
		GreqlSyntaxGraph queryGraph = eval.getSyntaxGraph();
		GreqlCodeGenerator greqlcodeGen = new GreqlCodeGenerator(queryGraph, graphMarker);
		try {
			greqlcodeGen.createFiles("/Users/dbildh/repos/git/jgralab-dhht/src/");
		} catch (GraphIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testGenerateForwardVertexSet() {
		//String query = "using X,Y: from x:X, y:Y with (y % 2 <> 1) and (x % 3 = 0) reportList x*y end";
		String query = "from v:V{MyVertex} report v--> end";
		GreqlEvaluator eval = new GreqlEvaluator(query, null, null);
		ObjectGraphMarker<Vertex, VertexEvaluator> graphMarker = eval.getVertexEvaluators();
		GreqlSyntaxGraph queryGraph = eval.getSyntaxGraph();
		GreqlCodeGenerator greqlcodeGen = new GreqlCodeGenerator(queryGraph, graphMarker);
		try {
			greqlcodeGen.createFiles("/Users/dbildh/repos/git/jgralab-dhht/src/");
		} catch (GraphIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Test
	public void testGeneratedComprehension() {
		System.out.println("Testing generated comprehensin");
		Map<String, Object> boundVars = new HashMap<String, Object>();
		PSet x = JGraLab.set();
		for (int i=1; i<2000; i++) {
			x = x.plus(i);
		}
		PSet y = JGraLab.set();
		for (int i=1; i<3000; i++) {
			y = y.plus(i);
		}
		boundVars.put("X", x);
		boundVars.put("Y", y);
		long startTime = System.currentTimeMillis();
		new SampleQuery().execute(null, boundVars);
		long usedTime = System.currentTimeMillis() - startTime;
		System.out.println("Evaluation of generated query took " + usedTime + "msec");
		
		
		
	}
	
}
