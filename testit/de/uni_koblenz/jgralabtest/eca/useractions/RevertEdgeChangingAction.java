package de.uni_koblenz.jgralabtest.eca.useractions;

import de.uni_koblenz.jgralab.Edge;

public class RevertEdgeChangingAction implements Action {

	@Override
	public void doAction(Event event) {
		if (event instanceof ChangeEdgeEvent) {
			ChangeEdgeEvent cee = (ChangeEdgeEvent) event;
			Edge edge = (Edge) (cee.getElement());
			if (cee.getEdgeEnd().equals(EdgeEnd.ALPHA)) {
				System.out.println("ECA Test Action: Revert changed Edge. "
						+ "Reset Alpha Vertex of Edge \"" + cee.getElement()
						+ "\" from \"" + cee.getNewVertex() + "\" to \""
						+ cee.getOldVertex() + "\"");
				edge.setAlpha(cee.getOldVertex());
			} else if (cee.getEdgeEnd().equals(EdgeEnd.OMEGA)) {
				System.out.println("ECA Test Action: Revert changed Edge. "
						+ "Reset Omega Vertex of Edge \"" + cee.getElement()
						+ "\" from \"" + cee.getNewVertex() + "\" to \""
						+ cee.getOldVertex() + "\"");
				edge.setOmega(cee.getOldVertex());
			}
		}

	}

}
