package de.uni_koblenz.jgralab.impl.mem;

import java.io.IOException;
import java.util.Iterator;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.GraphFactoryImpl;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;

public class MarkerGraphImpl extends CompleteGraphImpl {

	protected Schema schema = null;
	protected boolean markThatElements = false;

	public MarkerGraphImpl(boolean markThatElements, GraphClass cls) {
		this(cls);
		this.markThatElements = markThatElements;
	}

	protected MarkerGraphImpl(GraphClass cls) {
		super(GraphFactoryImpl.generateUniqueGraphId(), cls);
	}

	@Override
	public void readAttributeValueFromString(String attributeName, String value)
			throws GraphIOException, NoSuchAttributeException {
		// TODO Auto-generated method stub

	}

	@Override
	public String writeAttributeValueToString(String attributeName)
			throws IOException, GraphIOException, NoSuchAttributeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeAttributeValues(GraphIO io) throws IOException,
			GraphIOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void readAttributeValues(GraphIO io) throws GraphIOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getAttribute(String name) throws NoSuchAttributeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String name, Object data)
			throws NoSuchAttributeException {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<? extends Graph> getM1Class() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphClass getType() {
		return getGraphClass();
	}

	@Override
	public void addVertex(Vertex v) {
		super.addVertex(v);
		for (Iterator<Incidence> iIterator = v.getIncidences().iterator(); iIterator
				.hasNext();) {
			Incidence i = iIterator.next();
			addIncidence(i);
			if (markThatElements) {
				addEdge(i.getEdge());
			}
		}
	}

	@Override
	public void addEdge(Edge e) {
		super.addEdge(e);
		for (Iterator<Incidence> iIterator = e.getIncidences().iterator(); iIterator
				.hasNext();) {
			Incidence i = iIterator.next();
			addIncidence(i);
			if (markThatElements) {
				addVertex(i.getVertex());
			}
		}
	}

}
