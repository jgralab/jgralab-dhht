package de.uni_koblenz.jgralab.greql2.executable;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.schema.IncDirection;

public abstract class AbstractExecutableQuery implements ExecutableQuery {

	protected static boolean checkDirection(boolean isVertex, Incidence inc, IncDirection dir) {
		if (dir == IncDirection.BOTH)
			return true;
		if (isVertex) {
			if (dir == IncDirection.IN)
				return inc.getDirection() == Direction.EDGE_TO_VERTEX;
			else
				return inc.getDirection() == Direction.VERTEX_TO_EDGE;
		} else {
			if (dir == IncDirection.IN)
				return inc.getDirection() == Direction.VERTEX_TO_EDGE;
			else
				return inc.getDirection() == Direction.EDGE_TO_VERTEX;
		}
	}
	
	
}
