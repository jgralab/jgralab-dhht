package de.uni_koblenz.jgralab.dhhttest;

interface Buffer<T> {
	/** @return true if the buffer contains no elements */
	public boolean isEmpty();
	
	/** Stores the given element <code>elem</code> in the buffer */
	public boolean add(T elem);	

	/** Retrieves the next element from the buffer. Depending on the buffer strategy,
	 * this may be e.g. the last one which was put into the buffer (stack strategy),
	 * or the first one (queue strategy) 
	 * @return the next element from the buffer or <code>null</code>
	 *         if there is no next element */
	public T get();
}  