package de.uni_koblenz.jgralabtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.junit.Test;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.parser.Greql2Lexer;
import de.uni_koblenz.jgralab.greql2.parser.Greql2Parser;
import de.uni_koblenz.jgralab.greql2.schema.Greql2;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Schema;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class LoadTest {

	public static void main(String[] args) {
		LoadTest t = new LoadTest();
		t.testFreeElementList();
	}

	protected Graph createTestGraph() throws Exception {
		String query = "from i:c report i end where d:=\"drölfundfünfzig\", c:=b, b:=a, a:=\"Mensaessen\"";
		Greql2Lexer lexer = new Greql2Lexer(new ANTLRStringStream(query));
		CommonTokenStream tokens = new CommonTokenStream();
		tokens.setTokenSource(lexer);
		Greql2Parser parser = new Greql2Parser(tokens);
		parser.greqlExpression();
		// parser.saveGraph("createdTestGraph.tg");
		return parser.getGraph();
	}

	@Test
	public void testFreeElementList() {
		Greql2 g1 = null;
		Greql2 g2 = null;
		try {
			g1 = (Greql2) createTestGraph();
			GraphIO.saveGraphToFile("testgraph.tg", g1, null);
			g2 = Greql2Schema.instance().loadGreql2("testgraph.tg");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		checkEqualVertexList(g1, g2);
		checkEqualEdgeList(g1, g2);
		fillVertexList(g1, g2);
		checkEqualVertexList(g1, g2);
		checkEqualEdgeList(g1, g2);
		removeVertices(g1, g2);
		checkEqualVertexList(g1, g2);
		checkEqualEdgeList(g1, g2);
		fillVertexList(g1, g2);
		checkEqualVertexList(g1, g2);
		checkEqualEdgeList(g1, g2);
		removeVertices(g1, g2);
		checkEqualVertexList(g1, g2);
		checkEqualEdgeList(g1, g2);
	}

	private void checkEqualVertexList(Graph g1, Graph g2) {
		Vertex v1 = g1.getFirstVertex();
		Vertex v2 = g2.getFirstVertex();
		while (v1 != null) {
			if (v2 == null) {
				fail();
			}
			assertEquals(v1.getId(), v2.getId());
			assertEquals(v1.getAttributedElementClass().getQualifiedName(), v2
					.getAttributedElementClass().getQualifiedName());
			v1 = v1.getNextVertex();
			v2 = v2.getNextVertex();
		}
		if (v2 != null) {
			fail();
		}
	}

	private void checkEqualEdgeList(Graph g1, Graph g2) {
		Edge v1 = g1.getFirstEdgeInGraph();
		Edge v2 = g2.getFirstEdgeInGraph();
		while (v1 != null) {
			if (v2 == null) {
				fail();
			}
			assertEquals(v1.getId(), v2.getId());
			assertEquals(v1.getAttributedElementClass().getQualifiedName(), v2
					.getAttributedElementClass().getQualifiedName());
			v1 = v1.getNextEdgeInGraph();
			v2 = v2.getNextEdgeInGraph();
		}
		if (v2 != null) {
			fail();
		}
	}

	private void fillVertexList(Greql2 g1, Greql2 g2) {
		GraphClass gc = g1.getGraphClass();
		for (int i = 0; i < 100; i++) {
			VertexClass vertexClass = gc.getVertexClasses().get(
					i % gc.getVertexClasses().size());
			if (vertexClass.isInternal() || vertexClass.isAbstract()) {
				continue;
			}
			Class<? extends Vertex> vc = vertexClass.getM1Class();
			g1.createVertex(vc);
			// VertexClass vertexClass2 = gc.getVertexClasses().get(i %
			// gc.getVertexClasses().size());
			// if (vertexClass.isInternal())
			// continue;
			// Class<? extends Vertex> vc2 = vertexClass2.getM1Class();
			g2.createVertex(vc);
		}
	}

	private void removeVertices(Greql2 g1, Greql2 g2) {
		for (int i = 1; i < g1.getVCount(); i += 7) {
			Vertex v1 = g1.getVertex(i);
			Vertex v2 = g2.getVertex(i);
			assertEquals(v1.getId(), v2.getId());
			assertEquals(v1.getAttributedElementClass().getQualifiedName(), v2
					.getAttributedElementClass().getQualifiedName());
			v1.delete();
			v2.delete();
		}
	}

}