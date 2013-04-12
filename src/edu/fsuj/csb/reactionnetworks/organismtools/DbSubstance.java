package edu.fsuj.csb.reactionnetworks.organismtools;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.DataFormatException;

import edu.fsuj.csb.reactionnetworks.database.InteractionDB;
import edu.fsuj.csb.tools.organisms.Formula;
import edu.fsuj.csb.tools.organisms.Substance;
import edu.fsuj.csb.tools.urn.URN;
import edu.fsuj.csb.tools.xml.Tools;

public class DbSubstance extends Substance implements DBComponentMethods {


  public DbSubstance(int id, TreeSet<String> names, String mainName, Vector<URN> urns, Formula sumFormula) {
	  super(id, names, mainName, urns, sumFormula);
  }

	private static final long serialVersionUID = 8792215743381755527L;
	
	public static DbSubstance load(int id) throws SQLException{
		Substance dummy=Substance.get(id);
		if (dummy!=null) return (DbSubstance) dummy;		
		return new DbSubstance(id, null, null, null, null);
	}
	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.organisms.Component#names()
	 */
	@Override
	public TreeSet<String> names() {
	  if (super.names()==null) try {
	    addNames(InteractionDB.getNames(id()));
    } catch (SQLException e) {
	    e.printStackTrace();
    } catch (IOException e) {
	    e.printStackTrace();
	}
	  return super.names();
	}
	
	@Override
	public Vector<URN> urns() throws DataFormatException {
		if (super.urns()==null) try{
			addUrns(InteractionDB.getURNsFor(id()));
    } catch (SQLException e) {
	    e.printStackTrace();			
		} catch (IOException e) {
	    e.printStackTrace();
    }
	  return super.urns();
	}

	@Override
	public Formula formula() {
		Tools.startMethod("formula()");
		if (super.formula()==null) try{
			setFormula(InteractionDB.getFormula(id()));
    } catch (SQLException e) {
	    e.printStackTrace();			
		} catch (DataFormatException e) {
	    e.printStackTrace();
    } catch (IOException e) {
	    e.printStackTrace();
    }
		Tools.endMethod(super.formula());
	  return super.formula();
	}

	public void write() {
		// TODO: implement
		throw new NullPointerException("Not implemented, yet.");
	}

	public void uniteWith(int id) {
		// TODO: implement
		throw new NullPointerException("Not implemented, yet.");
	}

}
