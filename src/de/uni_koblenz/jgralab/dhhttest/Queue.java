package de.uni_koblenz.jgralab.dhhttest;

import java.util.LinkedList;

public class Queue<T> extends LinkedList<T> implements Buffer<T> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3407565090966892641L;

	@Override
	public T get() {
		return poll();
	}


}
