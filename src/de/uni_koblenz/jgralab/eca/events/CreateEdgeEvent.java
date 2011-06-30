package de.uni_koblenz.jgralab.eca.events;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.eca.events.EventDescription.EventTime;

public class CreateEdgeEvent extends Event {

	/**
	 * The created Edge or null if the EventTime is before
	 */
	private Edge edge;

	/**
	 * Creates an CreateEdgeEvent with the given parameters, EventTime is after
	 * 
	 * @param nestedCallsdepth
	 *            of nested trigger calls
	 * @param graph
	 *            Graph where the Event happened
	 * @param edge
	 *            the created Edge or null if the EventTime is before
	 */
	public CreateEdgeEvent(int nestedCalls, Graph graph, Edge edge) {
		super(nestedCalls, EventTime.AFTER, graph, edge.getM1Class());
		this.edge = edge;
	}

	/**
	 * Creates an CreateEdgeEvent with the given parameters, EventTime is before
	 * 
	 * @param nestedCallsdepth
	 *            of nested trigger calls
	 * @param graph
	 *            Graph where the Event happened
	 * @param type
	 *            the type
	 */
	public CreateEdgeEvent(int nestedCalls, Graph graph,
			Class<? extends AttributedElement> type) {
		super(nestedCalls, EventTime.BEFORE, graph, type);
		this.edge = null;
	}

	/**
	 * @return the created Edge or null if the EventTime is before
	 */
	public Edge getEdge() {
		return edge;
	}

	/**
	 * @return the AttributedElement that causes this Event or null if the
	 *         EventTime is before
	 */
	@Override
	public AttributedElement getElement() {
		return this.edge;
	}

}