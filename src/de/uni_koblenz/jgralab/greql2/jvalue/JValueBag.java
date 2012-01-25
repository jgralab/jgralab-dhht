/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

package de.uni_koblenz.jgralab.greql2.jvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * JValueBag implements a mathematical multiset of JValue-Objects. This includes
 * methods for union, difference, intersection etc.
 * 
 * @author ist@uni-koblenz.de
 */
public class JValueBag extends JValueCollection implements Cloneable {

	/**
	 * The backing instance of <code>HashMap</code> where the elements of this
	 * set are stored.
	 */
	private Map<JValue, Integer> myHashMap;

	/**
	 * Used by JValueCollection.toString()
	 */
	{
		OPENING_PAREN = "[";
		CLOSING_PAREN = "]";
	}

	// private List<JValue> sortedMembers;

	/**
	 * Acts as a cache for the size value of this set out of performance
	 * considerations. The value of storedSize is always up-to-date since it
	 * gets updated by all destructive methods in this class.
	 */
	private int storedSize = 0;

	/**
	 * Used to check, whether this multiset was modified by an destructive
	 * method while iterating over it.
	 */
	private boolean isConcurrentlyModified = false;

	/**
	 * An iterator that, in spite of the specific element storage technique in a
	 * <code>Multiset</code> (equal elements get 'counted' instead of each being
	 * stored separately), iterates over individual <code>Multiset</code>
	 * elements.
	 */
	private class JValueBagIterator implements Iterator<JValue> {
		private Iterator<JValue> myKeySetIterator;

		private JValue currentElement;

		private int multiElementIndex;

		private int multiElementNumber;

		private boolean removeEnabled;

		public JValueBagIterator() {
			myKeySetIterator = myHashMap.keySet().iterator();
			removeEnabled = false;
			JValueBag.this.isConcurrentlyModified = false;
			currentElement = null;
			multiElementNumber = 0;
			multiElementIndex = 0;
		}

		public boolean hasNext() {
			if (JValueBag.this.isConcurrentlyModified) {
				throw new ConcurrentModificationException();
			} else if (multiElementIndex > 0) {
				return true;
			} else {
				return (myKeySetIterator.hasNext());
			}
		}

		public JValue next() {
			if (hasNext()) {
				removeEnabled = true;
				if (multiElementIndex == 0) {
					currentElement = myKeySetIterator.next();
					multiElementNumber = getQuantity(currentElement);
					multiElementIndex = multiElementNumber - 1;
				} else {
					multiElementIndex--;
				}
			} else {
				throw new NoSuchElementException();
			}
			return currentElement;
		}

		public void remove() {
			if (JValueBag.this.isConcurrentlyModified) {
				throw new ConcurrentModificationException();
			} else if (removeEnabled) {
				if (multiElementNumber > 1) {
					Integer oldKeyCount, newKeyCount;

					oldKeyCount = myHashMap.get(currentElement);
					newKeyCount = Integer.valueOf(oldKeyCount.intValue() - 1);
					myHashMap.put(currentElement, newKeyCount);
					multiElementNumber--;
				} else {
					myKeySetIterator.remove();
					multiElementNumber = 0;
				}
				removeEnabled = false;
				storedHashCode = 0;
				storedSize--;
			} else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * Constructs a new, empty multiset; the backing <code>HashMap</code>
	 * instance has default initial capacity (16) and load factor (0.75).
	 */
	public JValueBag() {
		myHashMap = new HashMap<JValue, Integer>();
	}

	/**
	 * Constructs a new multiset containing the elements in the specified
	 * collection. The backing <code>HashMap</code> instance is created with
	 * default load factor (0.75) and an initial capacity sufficient to contain
	 * the elements in the specified collection.
	 * 
	 * @param c
	 *            the collection whose elements are to be placed into this
	 *            multiset.
	 * @throws NullPointerException
	 *             if the specified collection is null.
	 */
	public JValueBag(JValueCollection c) {
		myHashMap = new HashMap<JValue, Integer>();
		addAll(c);
	}

	/**
	 * Constructs a new, empty multiset; the backing <code>HashMap</code>
	 * instance has specified initial capacity and default load factor (0.75).
	 * Note that the backing <code>HashMap</code> only stores single copies of
	 * equal elements.
	 * <p>
	 * 
	 * @param initialCapacity
	 *            the initial capacity for distinct elements.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative.
	 */
	public JValueBag(int initialCapacity) {
		myHashMap = new HashMap<JValue, Integer>(initialCapacity);
	}

	/**
	 * Constructs a new, empty multiset; the backing <code>HashMap</code>
	 * instance has specified initial capacity and load factor. Note that the
	 * backing <code>HashMap</code> only stores single copies of equal elements.
	 * 
	 * @param initialCapacity
	 *            the initial capacity for distinct elements.
	 * @param loadFactor
	 *            the load factor.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative or the load factor is
	 *             nonpositive.
	 */
	public JValueBag(int initialCapacity, float loadFactor) {
		myHashMap = new HashMap<JValue, Integer>(initialCapacity, loadFactor);
	}

	@Override
	public boolean isJValueBag() {
		return true;
	}

	/**
	 * Returns the hash code value for this multiset. To get the hash code of
	 * this multiset, new hash code values for every element of this multiset
	 * are calculated from a polynomial of 3rd order and finally summed up. This
	 * ensures that <code>s1.equals(s2)</code> implies that
	 * <code>s1.hashCode()==s2.hashCode()</code> for any two multisets
	 * <code>s1</code> and <code>s2</code>, as required by the general contract
	 * of <code>Object.hashCode()</code>.
	 * 
	 * @return the hash code value for this multiset.
	 */
	@Override
	public int hashCode() {
		if (storedHashCode == 0) {
			int elementHashCode = 0;
			int newHashCode = -1;

			for (Entry<JValue, Integer> currentEntry : myHashMap.entrySet()) {
				elementHashCode = currentEntry.getKey().hashCode()
						* currentEntry.getValue();
				newHashCode += -1 + (3 + elementHashCode)
						* (7 + elementHashCode) * (11 + elementHashCode);
			}
			newHashCode += this.getClass().hashCode();
			storedHashCode = newHashCode;
		}
		return storedHashCode;
	}

	/**
	 * Compares the specified object with this bag for equality. Returns
	 * <code>true</code> if the specified object is also a bag, the two bags
	 * have the same size, and every element of the specified set is contained
	 * in this set the same number of times.
	 * 
	 * @param o
	 *            object to be compared for equality with this multiset.
	 * @return <code>true</code> if the specified object is equal to this
	 *         multiset, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof JValueBag) {
			JValueBag other = (JValueBag) o;
			if (size() != other.size()) {
				return false;
			}
			for (JValue val : this) {
				if (!other.contains(val)
						|| (getQuantity(val) != other.getQuantity(val))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns an iterator over the elements in this multiset. Different
	 * elements are returned in no particular order, however, equal elements are
	 * always returned subsequently.
	 * 
	 * @see ConcurrentModificationException
	 * @return an Iterator over the elements in this multiset.
	 */
	@Override
	public Iterator<JValue> iterator() {
		return new JValueBagIterator();
	}

	/**
	 * Returns a shallow copy of this <code>HashMultiset</code> instance: the
	 * elements themselves are not cloned.
	 * 
	 * @return a shallow copy of this multiset.
	 */
	@Override
	public Object clone() {
		JValueBag copy = new JValueBag(this.myHashMap.size());
		copy.myHashMap.putAll(this.myHashMap);
		copy.storedSize = this.storedSize;
		copy.storedHashCode = this.storedHashCode;
		return copy;
	}

	/**
	 * Returns <code>true</code> if this multiset contains no elements.
	 * 
	 * @return <code>true</code> if this multiset contains no elements,
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean isEmpty() {
		return myHashMap.isEmpty();
	}

	/**
	 * Returns <code>true</code> if this set contains the specified element.
	 * 
	 * @param element
	 *            element whose presence in this set is to be tested.
	 * @return <code>true</code> if this set contains the specified element,
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean contains(JValue element) {
		return myHashMap.containsKey(element);
	}

	/**
	 * Returns the number of elements in this multiset (its cardinality).
	 * 
	 * @return the number of elements in this multiset (its cardinality).
	 */
	@Override
	public int size() {
		return storedSize;
	}

	/**
	 * Removes all elements from this set.
	 */
	@Override
	public void clear() {
		storedSize = 0;
		storedHashCode = 0;
		isConcurrentlyModified = true;
		myHashMap.clear();
	}

	/**
	 * Returns the number of times the specified element is present in this
	 * multiset.
	 * 
	 * @param element
	 *            element whose quantity is returned.
	 * @return quantity of the specified element, 0 if it is not present.
	 * @see #setQuantity
	 */
	public int getQuantity(JValue element) {
		Integer keyCount = myHashMap.get(element);
		if (keyCount == null) {
			return 0;
		} else {
			return keyCount.intValue();
		}
	}

	/**
	 * Adjusts the number of times the specified element is present in this
	 * multiset to be the specified value (zero if the value is negative).
	 * <p>
	 * 
	 * This implementation sets <code>storedHashCode</code> to 0 (representing
	 * an unavailable hash code value), which forces <code>hashCode()</code> to
	 * recalculate the actual hash code value.
	 * 
	 * @param element
	 *            element whose quantity gets set.
	 * @param quantity
	 *            quantity of the specified element to be set.
	 * @return <code>true</code> if this multiset has been modified,
	 *         <code>false</code> otherwise.
	 * @see #getQuantity
	 */
	public final boolean setQuantity(JValueImpl element, int quantity) {
		int oldQuantity = getQuantity(element);
		if (quantity <= 0) {
			storedSize -= oldQuantity;
			myHashMap.remove(element);
		} else {
			if (oldQuantity != quantity) {
				storedSize = storedSize + quantity - oldQuantity;
				myHashMap.put(element, Integer.valueOf(quantity));
			} else {
				return false;
			}
		}
		storedHashCode = 0;
		isConcurrentlyModified = true;
		return true;
	}

	/**
	 * Returns a new <code>Set</code> containing the 'flattened' version of this
	 * multiset in which every element of this multiset is present exactly once.
	 * 
	 * @return the 'flattened' version of this multiset.
	 */
	@Override
	public JValueSet toJValueSet() {
		return new JValueSet(this);
	}

	/**
	 * Returns the size of a 'flattened' version of this multiset in which every
	 * element of this multiset is present exactly once.
	 * 
	 * @return the size of the 'flattened' version of this multiset.
	 */
	public int elementCount() {
		return myHashMap.size();
	}

	/**
	 * Returns <code>true</code> if this multiset is a superset of the specified
	 * collection. That is, if all elements of the specified collection are also
	 * present in this multiset at least the same number of times.
	 * <p>
	 * 
	 * This implementation checks if the specified collection is an instance of
	 * <code>Multiset</code> or <code>Set</code>. If so, the result of the super
	 * method <code>isSuperset</code> is returned. Otherwise, it tries to create
	 * the intersection of this HashMultiset and the specified Collection c by
	 * iterating over c and adding common elements to a new multiset. If an
	 * element is found whose quantity in the current intersection multiset is
	 * greater or equal than in this HashMultiset, false is returned. If the
	 * intersection can be built up completely, this HashMultiset is a superset
	 * of c and true is returned.
	 * 
	 * @param c
	 *            collection to be checked for being a subset.
	 * @return <code>true</code> if this multiset is a superset of the specifed
	 *         collection, <code>false</code> otherwise.
	 */
	public boolean isSupersetOf(JValueCollection c) {
		JValueBag bag = c.toJValueBag();
		Iterator<JValue> iter = this.iterator();
		while (iter.hasNext()) {
			JValue currentElement = iter.next();
			if (this.getQuantity(currentElement) < bag
					.getQuantity(currentElement)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the sum with the specified collection. This is a new
	 * <code>Multiset</code> containing all elements that are present in this
	 * multiset or in the specified collection. The quantities of equal elements
	 * get added up.
	 * 
	 * @param c
	 *            collection to be united with.
	 * @return the union with the specified collection.
	 */
	public JValueBag sum(JValueCollection c) {
		JValueBag resultingBag = new JValueBag(c);
		Iterator<JValue> iter = this.iterator();
		while (iter.hasNext()) {
			resultingBag.add(iter.next());
		}
		return resultingBag;
	}

	/**
	 * Returns the union with the specified collection. This is a new
	 * <code>JValueBag</code> containing all elements that are present in this
	 * bag or in the specified collection. For equal elements, the resulting
	 * quantity is the maximum of the two given quantities.
	 * 
	 * @param c
	 *            collection to be united with.
	 * @return the union with the specified collection.
	 */
	public JValueBag union(JValueCollection c) {
		JValueBag resultingBag = new JValueBag(c);
		Iterator<JValue> iter = this.iterator();
		while (iter.hasNext()) {
			JValue currentElement = iter.next();
			int ownQuantity = this.getQuantity(currentElement);
			int resultingQuantity = resultingBag.getQuantity(currentElement);
			if (ownQuantity > resultingQuantity) {
				resultingBag.add(currentElement, ownQuantity
						- resultingQuantity);
			}
		}
		return resultingBag;
	}

	/**
	 * Returns the intersection with the specified collection. This is a new
	 * <code>HashMultiset</code> containing all elements that are present in
	 * this multiset as well as in the specified collection. For equal elements,
	 * the resulting quantity is the minimum of the two given quantities.
	 * 
	 * @param c
	 *            collection to be intersected with.
	 * @return the intersection with the specified collection.
	 */
	public JValueBag intersection(JValueCollection c) {
		JValueBag resultingBag;

		resultingBag = new JValueBag(size());
		JValueBag foreignBag = c.toJValueBag();
		Iterator<JValue> iter = this.iterator();
		// add all elements which are in this bag but not the foreign bag
		while (iter.hasNext()) {
			JValue currentElement = iter.next();
			int foreignQuantity = foreignBag.getQuantity(currentElement);
			int ownQuantity = this.getQuantity(currentElement);
			int count;
			if (foreignQuantity > ownQuantity) {
				count = ownQuantity;
			} else {
				count = foreignQuantity;
			}
			resultingBag.add(currentElement, count);
		}
		return resultingBag;
	}

	/**
	 * Returns the asymmetric difference between this multiset and the specified
	 * collection. This is a new <code>HashMultiset</code> containing all
	 * elements that are present in this multiset but not in the specified
	 * collection. The quantities of equal elements get subtracted.
	 * 
	 * @param c
	 *            collection from which the difference is calculated.
	 * @return the difference with the specified collection.
	 */
	public JValueBag difference(JValueCollection c) {
		JValueBag resultingBag;

		resultingBag = new JValueBag(size());
		JValueBag foreignBag = c.toJValueBag();
		// add all elements which are in this bag but not the foreign bag
		for (JValue currentElement : this.toJValueSet()) {
			int foreignQuantity = foreignBag.getQuantity(currentElement);
			int ownQuantity = this.getQuantity(currentElement);
			int difference = ownQuantity - foreignQuantity;
			if (difference > 0) {
				resultingBag.add(currentElement, difference);
			}
		}
		return resultingBag;
	}

	/**
	 * Returns the symmetric difference between this multiset and the specified
	 * collection. This is a new <code>HashMultiset</code> containing all
	 * elements that are present either in this multiset or in the specified
	 * collection but not in both. The quantities of equal elements get
	 * subtracted from each other (maximum minus minimum).
	 * 
	 * @param c
	 *            collection from which the symmetric difference is calculated
	 * @return the symmetric difference with the specified collection.
	 */
	public JValueBag symmetricDifference(JValueCollection c) {
		JValueBag resultingBag;

		resultingBag = new JValueBag(size());
		JValueBag foreignBag = c.toJValueBag();
		// add all elements which are in this bag but not the foreign bag
		for (JValue currentElement : this.toJValueSet()) {
			int foreignQuantity = foreignBag.getQuantity(currentElement);
			int ownQuantity = this.getQuantity(currentElement);
			int difference = foreignQuantity - ownQuantity;
			if (difference != 0) {
				resultingBag.add(currentElement, Math.abs(difference));
			}
		}
		// add all elements which are in foreign bag but not in this bag
		for (JValue currentElement : foreignBag) {
			int ownQuantity = this.getQuantity(currentElement);
			if (ownQuantity == 0) {
				// int foreignQuantity = foreignBag.getQuantity(currentElement);
				resultingBag.add(currentElement);
			}
		}

		return resultingBag;
	}

	/**
	 * Adds the specified element <code>quantity</code> of times to this
	 * multiset. If <code>quantity</code> is negative or 0, the multiset remains
	 * unchanged and <code>false</code> is returned.
	 * <p>
	 * 
	 * If the set gets altered, this implementation sets
	 * <code>storedHashCode</code> to 0 (representing an unavailable hash code
	 * value), which forces <code>hashCode()</code> to recalculate the actual
	 * hash code value.
	 * 
	 * @param element
	 *            element to be added to this set.
	 * @param quantity
	 *            quantity of elements to add.
	 * @return <code>true</code> if <code>quantity</code> is greater than 0,
	 *         <code>false</code> otherwise
	 */
	public boolean add(JValue element, int quantity) {
		if (quantity <= 0) {
			return false;
		} else {
			myHashMap.put(element, getQuantity(element) + quantity);
			storedSize += quantity;
			storedHashCode = 0;
			return true;
		}
	}

	/**
	 * adds the specified element 1 times to this collection
	 */
	@Override
	public boolean add(JValue element) {
		return add(element, 1);
	}

	/**
	 * Removes the specified element from this multiset if it is present. If the
	 * element is present more than once, its quantity gets decreased by one.
	 * <p>
	 * 
	 * If the set gets altered, this implementation sets
	 * <code>storedHashCode</code> to 0 (representing an unavailable hash code
	 * value), which forces <code>hashCode()</code> to recalculate the actual
	 * hash code value.
	 * 
	 * @param element
	 *            object to be removed from this multiset, if present.
	 * @return <code>true</code> if the multiset contained the specified
	 *         element, <code>false</code> otherwise.
	 */
	public boolean remove(JValue element, int quantity) {
		Integer oldKeyCount;
		Integer newKeyCount;

		if (quantity < 1) {
			return false;
		}

		oldKeyCount = myHashMap.get(element);
		if (oldKeyCount != null) {
			int i = oldKeyCount.intValue() - quantity;
			int removedItems = quantity;
			if (i < 0) {
				removedItems = i + quantity;
				i = 0;
			}
			if (i == 0) {
				myHashMap.remove(element);
			} else {
				newKeyCount = Integer.valueOf(i);
				myHashMap.put(element, newKeyCount);
			}
			storedSize -= removedItems;
			storedHashCode = 0;
			isConcurrentlyModified = true;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * removes the specified element 1 times from this collection
	 */
	@Override
	public boolean remove(JValue element) {
		return remove(element, 1);
	}

	/**
	 * returns a pointer to the objec itself, very usefull to get a reference of
	 * the right type if you guess that a JValueCollection is a bag Cleaner and
	 * faster than casting
	 */
	@Override
	public JValueBag toJValueBag() {
		return this;
	}

	/**
	 * Removes all elements that are not in the specified collection from this
	 * collection
	 */
	public boolean retainAll(Collection<?> foreignCollection) {
		Iterator<JValue> iter = iterator();
		while (iter.hasNext()) {
			JValue v = iter.next();
			if (!foreignCollection.contains(v)) {
				iter.remove();
			}
		}
		return true;
	}

	/**
	 * accepts the given visitor to visit this jvalue
	 */
	@Override
	public void accept(JValueVisitor v) {
		v.visitBag(this);
	}

	@Override
	public Object toObject() {
		ArrayList<Object> result = new ArrayList<Object>(myHashMap.size());
		for (JValue jv : myHashMap.keySet()) {
			result.add(jv.toObject());
		}
		return result;
	}

	@Override
	public void sort() {
		if (myHashMap instanceof TreeMap<?, ?>) {
			return;
		}
		myHashMap = new TreeMap<JValue, Integer>(myHashMap);
	}
}
