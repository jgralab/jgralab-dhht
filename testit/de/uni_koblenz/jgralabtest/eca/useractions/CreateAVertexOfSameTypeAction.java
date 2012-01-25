package de.uni_koblenz.jgralabtest.eca.useractions;

import de.uni_koblenz.jgralab.Vertex;

public class CreateAVertexOfSameTypeAction implements Action {

	@SuppressWarnings("unchecked")
	@Override
	public void doAction(Event ev) {
		if (ev instanceof CreateVertexEvent) {
			System.out.println("ECA Test Action: Create a new Vertex of Type: "
					+ ev.getType().getName());
			ev.getGraph()
					.createVertex((Class<? extends Vertex>) (ev.getType()));
			
		}
	}

}
