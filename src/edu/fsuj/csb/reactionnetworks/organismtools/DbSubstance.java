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
import edu.fsuj.csb.tools.xml.XmlToken;

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
	
  /**
   * creates a xml description of this substance, assigning it to the given compartment
   * @param compartmentId the id of the compartment, which shall be referenced in the tag
   * @return the xml tag for this substance
   * @throws SQLException 
   */
  public void getCode(StringBuffer result) {
  	Tools.startMethod("DbSubstance.getCode()");
  	tokenClass="species";
  	Vector<URN> urnList=null;
    try {
	    urnList = urns();
    } catch (DataFormatException e) {}
  	if (urnList!=null && urnList.size()>0){
  		XmlToken annotation = new XmlToken("annotation");
  		XmlToken rdf=new XmlToken("rdf:RDF");
  		rdf.setValue("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
  		rdf.setValue("xmlns:dc", "http://purl.org/dc/elements/1.1/");
  		rdf.setValue("xmlns:dcterms", "http://purl.org/dc/terms/");
  		rdf.setValue("xmlns:vCard", "http://www.w3.org/2001/vcard-rdf/3.0#");
  		rdf.setValue("xmlns:bqbiol", "http://biomodels.net/biology-qualifiers/");
  		rdf.setValue("xmlns:bqmodel", "http://biomodels.net/model-qualifiers/");
  		XmlToken rdfDescription=new XmlToken("rdf:Description");
  		XmlToken bqBiolIs=new XmlToken("bqbiol:is");
  		XmlToken rdfBag=new XmlToken("rdf:Bag");
  		for (URN urn:urnList){
  			XmlToken rdfLi = new XmlToken("rdf:li");
  			rdfLi.setValue("rdf:resource", urn);
  			rdfBag.add(rdfLi);
  		}
  		bqBiolIs.add(rdfBag);
  		rdfDescription.add(bqBiolIs);
  		rdf.add(rdfDescription);
  		annotation.add(rdf);  		
  		add(annotation);
  	}
  	super.getCode(result);
  	Tools.endMethod();
  }
}
