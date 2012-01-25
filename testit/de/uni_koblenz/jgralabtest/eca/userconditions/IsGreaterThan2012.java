package de.uni_koblenz.jgralabtest.eca.userconditions;


public class IsGreaterThan2012 implements Condition {

	@Override
	public boolean evaluate(Event event) {
		ChangeAttributeEvent cae = (ChangeAttributeEvent) event;
		if((Integer)cae.getNewValue() > 2012)
			return true;
		return false;
	}

}
