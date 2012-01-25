package de.uni_koblenz.jgralabtest.eca.useractions;

import de.uni_koblenz.jgralab.Edge;

public class RevertEdgeChangingOnHighesLevelAction implements Action {

	@Override
	public void doAction(Event event) {
		if (event.getNestedCalls() > 1) {
			return;
		}
		if (event instanceof ChangeEdgeEvent) {
			ChangeEdgeEvent cee = (ChangeEdgeEvent) event;
			Edge edge = (Edge) (cee.getElement());
			if (edge.getAlpha().equals(cee.getNewVertex())) {
				System.out.println("ECA Test Action: Revert changed Edge. "
						+ "Reset Alpha Vertex of Edge \"" + cee.getElement()
						+ "\" from \"" + cee.getNewVertex() + "\" to \""
						+ cee.getOldVertex() + "\"");
				edge.setAlpha(cee.getOldVertex());
			} else if (edge.getOmega().equals(cee.getNewVertex())) {
				System.out.println("ECA Test Action: Revert changed Edge. "
						+ "Reset Omega Vertex of Edge \"" + cee.getElement()
						+ "\" from \"" + cee.getNewVertex() + "\" to \""
						+ cee.getOldVertex() + "\"");
				edge.setOmega(cee.getOldVertex());
			}
		}

	}

}
