package de.uni_koblenz.jgralabtest.eca.useractions;


public class PrintNewAndOldAttributeValueAction implements Action {

	@Override
	public void doAction(Event e) {
		if (e instanceof ChangeAttributeEvent) {
			ChangeAttributeEvent cae = (ChangeAttributeEvent) e;
			if (cae.getTime().equals(EventDescription.EventTime.BEFORE)) {
				System.out.println("ECA Test Message: " + "Attribute \""
						+ cae.getAttributeName() + "\" of \""
						+ cae.getElement() + "\" will change from \""
						+ cae.getOldValue() + "\" to \"" + cae.getNewValue()
						+ "\"");
			} else {
				System.out.println("ECA Test Message: " + "Attribute \""
						+ cae.getAttributeName() + "\" of \""
						+ cae.getElement() + "\" changed from \""
						+ cae.getOldValue() + "\" to \"" + cae.getNewValue()
						+ "\"");
			}
		}

	}

}
