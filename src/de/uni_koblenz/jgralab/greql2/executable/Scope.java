package de.uni_koblenz.jgralab.greql2.executable;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Works like a symbol table for the variables during the evaluation.
 * Check for duplicate variables etc. is done during query parsing,
 * so instead of insert and lookup, there are the two operations
 * setValue and getValue that set or retrieve the value of an variable.
 * @author dbildh
 *
 */
public class Scope {

	protected LinkedList<TreeSet<String>> list = null;

	public Scope() {
		list = new LinkedList<TreeSet<String>>();
	}

	public void blockBegin() {
		TreeSet<String> set = new TreeSet<String>();
		list.addFirst(set);
	}

	public void blockEnd() {
		if (!list.isEmpty()) {
			list.removeFirst();
		}
	}

	public void addVariable(String ident) {
		list.getFirst().add(ident);
	}	

	public TreeSet<String> getDefinedVariables() {
		TreeSet<String> r = new TreeSet<String>();
		for (TreeSet<String> set : list) {
			r.addAll(set);
		}
		return r;
	}

	
}
