package de.uni_koblenz.jgralabtest.instancetest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.trans.CommitFailedException;
import de.uni_koblenz.jgralabtest.schemas.minimal.MinimalGraph;
import de.uni_koblenz.jgralabtest.schemas.minimal.MinimalSchema;
import de.uni_koblenz.jgralabtest.schemas.minimal.Node;

@RunWith(Parameterized.class)
public class EdgeListTest extends InstanceTest {
	private static final int V = 4;
	private static final int E = 4;
	private static final int N = 10;
	private MinimalGraph g;

	@Parameters
	public static Collection<Object[]> configure() {
		return getParameters();
	}

	public EdgeListTest(Boolean transactionsEnabled) {
		super(transactionsEnabled.booleanValue());
	}

	@Before
	public void setup() throws CommitFailedException {
		g = transactionsEnabled ? MinimalSchema.instance()
				.createMinimalGraphWithTransactionSupport() : MinimalSchema
				.instance().createMinimalGraph(V, E);
		createTransaction(g);
		for (int i = 0; i < N; ++i) {
			g.createNode();
		}
		for (int i = 0; i < N; ++i) {
			g.createLink((Node) g.getVertex(i + 1), (Node) g.getVertex((i + 1)
					% N + 1));
		}
		commit(g);
		createTransaction(g);
	}
	
	@After
	public void tearDown() throws CommitFailedException{
		commit(g);
	}

	@Test
	public void addEdgeTest() throws Exception {
		assertEquals(10, g.getECount());
		assertEquals("e1 e2 e3 e4 e5 e6 e7 e8 e9 e10", getESeq());
		for (Vertex v : g.vertices()) {
			assertEquals(2, v.getDegree());
			assertEquals(1, v.getDegree(EdgeDirection.IN));
			assertEquals(1, v.getDegree(EdgeDirection.OUT));
		}
	}

	private String getESeq() {
		StringBuilder sb = new StringBuilder();
		for (Edge e : g.edges()) {
			sb.append('e').append(e.getId()).append(' ');
		}
		return sb.toString().trim();
	}

	@Test
	public void putBeforeTest() throws Exception {		
		Edge e5 = g.getEdge(5).getReversedEdge();
		e5.putBeforeInGraph(g.getEdge(6));
		assertTrue(e5.isBeforeInGraph(g.getEdge(6)));
		assertEquals("e1 e2 e3 e4 e5 e6 e7 e8 e9 e10", getESeq());
		assertTrue(e5.isAfterInGraph(g.getEdge(4)));
		assertFalse(e5.isBeforeInGraph(g.getEdge(4)));

		e5.putBeforeInGraph(g.getEdge(4));
		assertEquals("e1 e2 e3 e5 e4 e6 e7 e8 e9 e10", getESeq());
		assertFalse(e5.isAfterInGraph(g.getEdge(4)));
		assertTrue(e5.isBeforeInGraph(g.getEdge(4)));

		e5.putBeforeInGraph(g.getEdge(10).getReversedEdge());
		assertEquals("e1 e2 e3 e4 e6 e7 e8 e9 e5 e10", getESeq());
		assertFalse(e5.isAfterInGraph(g.getEdge(10)));
		assertTrue(e5.isBeforeInGraph(g.getEdge(10)));
		assertFalse(e5.isBeforeInGraph(g.getEdge(1)));
		assertTrue(g.getEdge(1).isBeforeInGraph(e5));

		e5.putBeforeInGraph(g.getEdge(1));
		assertEquals("e5 e1 e2 e3 e4 e6 e7 e8 e9 e10", getESeq());
		assertTrue(e5.isBeforeInGraph(g.getEdge(1)));
		assertFalse(e5.isAfterInGraph(g.getEdge(1)));
		assertTrue(g.getEdge(1).isAfterInGraph(e5));

	}

	@Test(expected = GraphException.class)
	public void putBeforeSelf() throws Exception {	
		g.getEdge(5).putBeforeInGraph(g.getEdge(5));
	}

	@Test(expected = GraphException.class)
	public void putAfterSelf() throws Exception {
		g.getEdge(5).putAfterInGraph(g.getEdge(5));
	}

	@Test
	public void putAfterTest() throws Exception {
		Edge e5 = g.getEdge(5).getReversedEdge();
		e5.putAfterInGraph(g.getEdge(4));
		assertEquals("e1 e2 e3 e4 e5 e6 e7 e8 e9 e10", getESeq());
		commit(g);

		createTransaction(g);
		e5.putAfterInGraph(g.getEdge(6).getReversedEdge());
		assertEquals("e1 e2 e3 e4 e6 e5 e7 e8 e9 e10", getESeq());
		commit(g);

		createTransaction(g);
		e5.putAfterInGraph(g.getEdge(10));
		assertEquals("e1 e2 e3 e4 e6 e7 e8 e9 e10 e5", getESeq());
		commit(g);

		createTransaction(g);
		e5.putAfterInGraph(g.getEdge(1));
		assertEquals("e1 e5 e2 e3 e4 e6 e7 e8 e9 e10", getESeq());
	}

	@Test
	public void deleteEdgeTest() throws Exception {
		Edge e = g.getEdge(5);
		e.delete();
		assertFalse(e.isValid());
		assertEquals(null, g.getEdge(5));
		assertEquals(9, g.getECount());
		assertEquals(1, g.getVertex(5).getDegree());
		assertEquals(1, g.getVertex(5).getDegree(EdgeDirection.IN));
		assertEquals(0, g.getVertex(5).getDegree(EdgeDirection.OUT));
		assertEquals(1, g.getVertex(6).getDegree());
		assertEquals(0, g.getVertex(6).getDegree(EdgeDirection.IN));
		assertEquals(1, g.getVertex(6).getDegree(EdgeDirection.OUT));
		assertEquals("e1 e2 e3 e4 e6 e7 e8 e9 e10", getESeq());
		commit(g);

		createTransaction(g);
		e = g.getFirstEdgeInGraph().getReversedEdge();
		e.delete();
		assertFalse(e.isValid());
		assertEquals(null, g.getEdge(1));
		assertEquals(8, g.getECount());
		assertEquals("e2 e3 e4 e6 e7 e8 e9 e10", getESeq());
		commit(g);

		createTransaction(g);
		e = g.getEdge(10);
		e.delete();
		assertFalse(e.isValid());
		assertEquals(null, g.getEdge(1));
		assertEquals(7, g.getECount());
		assertEquals("e2 e3 e4 e6 e7 e8 e9", getESeq());
		commit(g);

		createTransaction(g);
		Node v1 = (Node) g.getVertex(1);
		Node v2 = (Node) g.getVertex(2);
		g.createLink(v1, v2);
		assertEquals("e2 e3 e4 e6 e7 e8 e9 e1", getESeq());
		commit(g);

		createTransaction(g);
		g.createLink(v1, v2);
		assertEquals("e2 e3 e4 e6 e7 e8 e9 e1 e5", getESeq());
		commit(g);

		createTransaction(g);
		g.createLink(v1, v2);
		assertEquals("e2 e3 e4 e6 e7 e8 e9 e1 e5 e10", getESeq());
		commit(g);

		createTransaction(g);
		g.createLink(v1, v2);
		assertEquals("e2 e3 e4 e6 e7 e8 e9 e1 e5 e10 e11", getESeq());
	}
}