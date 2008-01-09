/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2007 Institute for Software Technology
 *               University of Koblenz-Landau, Germany
 *
 *               ist@uni-koblenz.de
 *
 * Please report bugs to http://serres.uni-koblenz.de/bugzilla
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
package de.uni_koblenz.jgralab.greql2.evaluator.logging;

import org.jdom.*;
import java.util.*;



/**
 * This class stores the number of logging-calls for inputsize
 * and the summed up value. The average or estimated size can be calculated by dividing
 * the result size
 */
public class ArrayLogEntry extends LogEntry {
	
	/**
	 * stores the summed-up value of logvalues
	 */
	private ArrayList<Integer> sumList;
	
	
	/**
	 * Creates a new ArrayLogEntry from given JDOM-Element
	 */
	public ArrayLogEntry(Element elem) {
		super(elem);
		int i = 0;
		Element sumElem = elem.getChild("sum" + i);
		sumList = new ArrayList<Integer>();
		while (sumElem != null) {
			sumList.add(Integer.parseInt(sumElem.getText()));
			i++;
			sumElem = elem.getChild("sum" + i);
		}
	}
	
	
	/**
	 * Creates a new ArrayLogEntry with the given name
	 */
	public ArrayLogEntry(String name) {
		super(name);
		sumList = new ArrayList<Integer>();
	}
	
	/**
	 * logs the given integer value, adds it to the sum and increases the callcount
	 * @param n
	 */
	public void logSum(ArrayList<Integer> n) {
		for (int i=0; i<n.size(); i++) {
			if (sumList.size() <= i) {
				sumList.add(0);
			}
			int value = sumList.get(i);
			value += n.get(i);
			sumList.set(i, value);
		}	
		calls++;
	}
	
	/**
	 * returns the sumed-up value of logvalues
	 * @return the sumed-up value of logvalues
	 */
	public ArrayList<Integer> getSum() {
		return sumList;
	}
	
	/**
	 * returns the average or estimated value
	 * @return the average or estimated value
	 */
	public ArrayList<Double> getAverageValue() {
		Iterator<Integer> iter = sumList.iterator();
		ArrayList<Double> returnList = new ArrayList<Double>();
		while (iter.hasNext()) {
			int value = iter.next();
			Double currentAvg = (double) (value / calls);
			returnList.add( currentAvg );
		}
		return returnList;
	}
	
	/**
	 * returns the data stored in this logentry as JDom Element
	 * The structure of the element is like this
	 * &lt;ArrayLogEntry>
	 * 	 	&lt;calls&gt; Y &lt;/sum&gt;
	 *		&lt;sum0&gt; X0 &lt;/sum01&gt;
	 *		&lt;sum1&gt; X1 &lt;/sum1&gt;
 	 *		&lt;sum2&gt; X2 &lt;/sum2&gt;
 	 *		&lt;sum3&gt; X3 &lt;/sum3&gt;
	 * &lt;/ArrayLogEntry&gt;
	 */
	public Element toJDOMEntry() {
		Element logEntryElem = new Element("ArrayLogEntry");
		Element nameElem = new Element("name");
		nameElem.addContent(name);
		logEntryElem.addContent(nameElem);
		Element callsElem = new Element("calls");
		callsElem.addContent(String.valueOf(calls));
		logEntryElem.addContent(callsElem);
		Iterator<Integer> iter = sumList.iterator();
		int i = 0;
		while (iter.hasNext()) {
			Element sumElem = new Element("sum" + i);
			sumElem.addContent(String.valueOf( iter.next()));
			logEntryElem.addContent(sumElem);
			i++;
		}
		return logEntryElem;
	}
	
	
}