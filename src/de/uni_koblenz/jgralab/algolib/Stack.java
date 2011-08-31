package de.uni_koblenz.jgralab.algolib;

import java.util.LinkedList;

public class Stack<T> extends java.util.Stack<T> implements Buffer<T> {


	public Stack() {
		super();
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3407565090966892641L;

	@Override
	public T get() {
		return pop();
	}


}
