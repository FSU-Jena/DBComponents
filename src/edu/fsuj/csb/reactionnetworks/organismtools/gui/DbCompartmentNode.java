package edu.fsuj.csb.reactionnetworks.organismtools.gui;

import edu.fsuj.csb.reactionnetworks.organismtools.DbCompartment;
import edu.fsuj.csb.tools.organisms.gui.CompartmentNode;
import edu.fsuj.csb.tools.xml.XmlObject;

public class DbCompartmentNode extends CompartmentNode implements XmlObject{

  private static final long serialVersionUID = 3274083084255265328L;

	public DbCompartmentNode(DbCompartment c) {
	  super(c);
  }

	/**
	 * @return the id of the related compartment
	 */
	public DbCompartment compartment(){
		return (DbCompartment)super.compartment();
	}

	@Override
  public StringBuffer getCode() {
		return compartment().getCode();
  }
}
