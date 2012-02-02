package de.uni_koblenz.jgralab.greql2.funlib.schema;

import java.util.SortedSet;

import org.pcollections.PSet;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;

public class AttributeNames extends Function {

	public AttributeNames() {
		super(
				"Returns the set of attribute names of the specified element or schema class.",
				5, 5, 1.0, Category.SCHEMA_ACCESS);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PSet<String> evaluate(AttributedElementClass cls) {
		PSet<String> result = JGraLab.set();
		SortedSet<Attribute> attributeSet = cls.getAttributeList();
		for (Attribute a : attributeSet) {
			result = result.plus(a.getName());
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public PSet<String> evaluate(AttributedElement el) {
		return evaluate((AttributedElementClass)el.getType());
	}
}
