package de.uni_koblenz.jgralab;


import java.rmi.Remote;
import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.TypedElementClass;

public interface TypedElement<ConcreteMetaClass extends TypedElementClass<ConcreteMetaClass, ConcreteInterface>, ConcreteInterface extends TypedElement<ConcreteMetaClass, ConcreteInterface>>
                 extends Comparable<ConcreteInterface>, Remote {

	/**
	 * Returns the m1-class of this {@link AttributedElement}.
	 * 
	 * @return {@link Class}
	 */
	public abstract Class<? extends ConcreteInterface> getM1Class() throws RemoteException;
	
	/**
	 * Returns the {@link GraphClass} of which this {@link Graph} is an instance
	 * of.
	 * 
	 * @return {@link GraphClass}
	 */
	public ConcreteMetaClass getType() throws RemoteException;

	public GraphClass getGraphClass() throws RemoteException;

	/**
	 * @return the schema this AttributedElement belongs to
	 */
	public Schema getSchema() throws RemoteException;
	
	/**
	 * Returns the M1 implementation class for this IncidenceClass.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>m1ImplClass = incidenceClass.getM1Class();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> not yet defined
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> not yet defined
	 * </p>
	 * 
	 * @return the M1 implementation class for this IncidenceClass
	 * 
	 * @throws M1ClassAccessException
	 *             if:
	 *             <ul>
	 *             <li>this IncidenceClass is abstract</li>
	 *             <li>there are reflection exceptions</li>
	 *             </ul>
	 */
	//public Class<? extends Incidence> getM1ImplementationClass();

}