package edu.fsuj.csb.reactionnetworks.organismtools;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.DataFormatException;

import edu.fsuj.csb.reactionnetworks.database.InteractionDB;
import edu.fsuj.csb.tools.organisms.Compartment;
import edu.fsuj.csb.tools.organisms.ReactionSet;
import edu.fsuj.csb.tools.organisms.Substance;
import edu.fsuj.csb.tools.urn.URN;
import edu.fsuj.csb.tools.xml.Tools;
import edu.fsuj.csb.tools.xml.XmlToken;

public class DbCompartment extends Compartment implements DBComponentMethods {

	private static final long serialVersionUID = 1680539836013966984L;

	public DbCompartment(int id, TreeSet<String> names, String mainName, Vector<URN> urns, TreeSet<Integer> containedCompartments, TreeSet<Integer> enzymes) {
		super(id, names, mainName, urns, containedCompartments, enzymes);
	}

	/**
	 * tries to retrieve the compartment belonging to the given id from the list of loaded compartments
	 * 
	 * @param id the id of the compartment, which shall be loaded
	 * @return the compartment object
	 * @throws SQLException if compartment can not be loaded from the database
	 */
	public static DbCompartment load(int id) throws SQLException {
		Compartment dummy = Compartment.get(id);
		if (dummy != null) return (DbCompartment) dummy;
		return new DbCompartment(id, null, null, null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.fsuj.csb.tools.organisms.Component#names()
	 */
	@Override
	public TreeSet<String> names() {
		// System.err.println("DBCompartment.names()");
		if (super.names() == null) try {
			addNames(InteractionDB.getNames(id()));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.names();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.fsuj.csb.tools.organisms.Component#urns()
	 */
	@Override
	public Vector<URN> urns() throws DataFormatException {
		if (super.urns() == null) try {
			addUrns(InteractionDB.getURNsFor(id()));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.urns();
	}

	@Override
	public TreeSet<Integer> enzymes() {
		Tools.startMethod("DbCompartment.enzymes()");
		if (super.enzymes() == null) try {
			addEnzymes(InteractionDB.loadEnzymesOfCompartment(id()));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		TreeSet<Integer> result = super.enzymes();
		Tools.endMethod(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.fsuj.csb.tools.organisms.Compartment#containedCompartments(boolean)
	 */
	@Override
	public TreeSet<Integer> containedCompartments(boolean recursive) {
		TreeSet<Integer> result = new TreeSet<Integer>();
		String query = "SELECT contained FROM hierarchy WHERE container=" + id();
		try {
			Statement s = InteractionDB.createStatement();
			ResultSet r = s.executeQuery(query);
			while (r.next()) {
				result.add(r.getInt(1));
			}
			r.close();
			s.close();
		} catch (SQLException e) {
			System.err.println(query);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(query);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public ReactionSet reactions() {
		Tools.startMethod("DbCompartment.reactions()");
		String query = null;
		if (super.reactions() == null) try {
			ReactionSet reactions = new ReactionSet();
			Statement st = InteractionDB.createStatement();
			// System.err.print("selecting reactions by enzymes...");
			if (!enzymes().isEmpty()) {
				query = "SELECT rid FROM reaction_enzymes WHERE eid IN " + (enzymes().toString().replace('[', '(').replace(']', ')'));
				// System.err.println(query);
				ResultSet rs = st.executeQuery(query);
				while (rs.next())
					reactions.add(rs.getInt(1));
				rs.close();
			}
			// System.err.print("...done\nselecting reactions by compartment ids");
			TreeSet<Integer> cids = containedCompartments(true);
			cids.add(id());
			query = "SELECT rid FROM reaction_directions WHERE cid IN " + cids.toString().replace("[", "(").replace("]", ")");
			// System.out.println(query);
			ResultSet rs = st.executeQuery(query);
			while (rs.next())
				reactions.add(rs.getInt(1));
			// System.err.print("...done\nloading reactions");
			rs.close();
			for (Iterator<Integer> it = reactions.iterator(); it.hasNext();)
				DbReaction.load(it.next());
			// System.err.print("...done\nreading utilized substances");
			TreeSet<Integer> substances = reactions.utilizedSubstances(); // only gets substances utilized by non spontaneous reactions, as spontaneous reaction will at first be loaded in the next step.
			// System.err.print("...done\nreading list of spontaneous reactions");
			TreeSet<Integer> spontaneousReactions = InteractionDB.getSpontaneousReactionsActingOn(substances);
			// System.err.print("...done\nloading spontaneous reactions");
			for (Iterator<Integer> it = spontaneousReactions.iterator(); it.hasNext();)
				DbReaction.load(it.next());
			reactions.addAll(spontaneousReactions);
			st.close();
			// System.err.print("...done\nadding reactions to list");
			super.addReactions(reactions);
			// System.err.println("...done");
		} catch (SQLException e) {
			System.err.println("Error on " + query);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		else {
			// System.err.println("super.Reactions: "+super.reactions());
		}
		ReactionSet result = super.reactions();
		Tools.endMethod(result);
		return result;
	}

	@Override
	public TreeSet<Integer> containingCompartments() {
		TreeSet<Integer> result = new TreeSet<Integer>();
		String query = "SELECT container FROM hierarchy WHERE contained=" + id();
		try {
			Statement s = InteractionDB.createStatement();
			ResultSet r = s.executeQuery(query);
			while (r.next()) {
				result.add(r.getInt(1));
			}
			r.close();
			s.close();
		} catch (SQLException e) {
			System.err.println(query);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(query);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void write() {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniteWith(int id) {
		// TODO Auto-generated method stub
	}

	public void addContainedCompartment(int cid) {
		containedCompartments(false);
		super.addContainedCompartment(cid);
	}

	public static Compartment get(int id) {
		Compartment result = Compartment.get(id);
		if (result == null) try {
			result = load(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void getCode(StringBuffer result) {
		Tools.startMethod("DbCompartment.getCode()");
		tokenClass = "compartment";
		super.getCode(result);
		Tools.endMethod();
	}

	protected XmlToken speciesList() {
		Tools.startMethod("DbCompartment.speciesList()");
		XmlToken sList = new XmlToken("listOfSpecies");

		TreeSet<Integer> subs = utilizedSubstances();
		int number = subs.size() / 50;
		int count = 0;
		System.err.print("\n[");
		for (Integer speciesId : subs) {
			if (++count % number == 0) System.err.print("#");
			Substance substance;
			try {
				substance = DbSubstance.load(speciesId);
				substance.setValue("compartment", "c"+id());
				sList.add(substance);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.err.println(']');
		try {
	    Thread.sleep(10);
    } catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
		Tools.endMethod(sList,40);
		return sList;
	}
}
