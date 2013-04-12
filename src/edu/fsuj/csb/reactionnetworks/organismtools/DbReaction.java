package edu.fsuj.csb.reactionnetworks.organismtools;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.DataFormatException;

import edu.fsuj.csb.reactionnetworks.database.InteractionDB;
import edu.fsuj.csb.tools.organisms.Reaction;
import edu.fsuj.csb.tools.urn.URN;
import edu.fsuj.csb.tools.xml.Tools;

public class DbReaction extends Reaction implements DBComponentMethods {


	
	public DbReaction(int id, TreeSet<String> names, String mainName, Vector<URN> urns, TreeMap<Integer, Integer> substrates, TreeMap<Integer, Integer> products, TreeMap<Integer, Byte> directions) {
	  super(id, names, mainName, urns, substrates, products, directions);
  }

	public static DbReaction load(int id) throws SQLException{
		Reaction dummy=Reaction.get(id);
		if (dummy!=null) return (DbReaction) dummy;		
		return new DbReaction(id, null, null, null, null, null, null); // fields will be loaded on demand
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
	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.organisms.Component#urns()
	 */
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
	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.organisms.Reaction#products()
	 */
	@Override
	public TreeMap<Integer, Integer> products() {
		Tools.startMethod("DbReaction.products()");
		if (super.products()==null) try {
			super.addProducts(InteractionDB.loadProducts(id()));
		} catch (SQLException e){
			e.printStackTrace();
		} catch (IOException e) {
	    e.printStackTrace();
    }
		Tools.endMethod(super.products());
		return super.products();
	}
	
	/* (non-Javadoc)
	 * @see edu.fsuj.csb.tools.organisms.Reaction#substrates()
	 */
	@Override
	public TreeMap<Integer, Integer> substrates() {
		Tools.startMethod("DbReaction.substrates()");
		if (super.substrates()==null) try {
			super.addSubstrates(InteractionDB.loadSubstrates(id()));
		} catch (SQLException e){
			e.printStackTrace();
		} catch (IOException e) {
	    e.printStackTrace();
    }
		Tools.endMethod(super.substrates());
		return super.substrates();
	}

	@Override
	public void write() {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniteWith(int id) {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected byte directions(int cid) {
		try {
			return super.directions(cid);	
		} catch (NullPointerException e){
			byte direction=FORWARD+BACKWARD;
      try {
	      direction = InteractionDB.readDirections(cid,id());
				super.addDirection(cid,direction);
      } catch (SQLException e1) {
	      e1.printStackTrace();
      } catch (IOException e1) {
	      e1.printStackTrace();
      }
      return direction;
		}		
	}
}
