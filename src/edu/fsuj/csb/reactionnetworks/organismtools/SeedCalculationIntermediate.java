package edu.fsuj.csb.reactionnetworks.organismtools;

import java.io.Serializable;
import java.util.TreeSet;

import edu.fsuj.csb.tools.organisms.CalculationIntermediate;


/**
 * this representa a intermediate result of a seed calculation
 * @author Stephan Richter
 *
 */
public class SeedCalculationIntermediate  extends CalculationIntermediate implements Serializable{
	
  private static final long serialVersionUID = 6144605632175355487L;
	private TreeSet<Integer> potentialPrecursors;
	private TreeSet<TreeSet<Integer>> continouslyAvailableSubstances;

	/**
	 * creates a new seed calculation intermediate
	 * @param potPrecursors the set of substances that may be precursors to a certain substance
	 * @param cycles the set of substances occuring in cycles
	 */
	public SeedCalculationIntermediate(TreeSet<Integer> potPrecursors, TreeSet<TreeSet<Integer>> cycles) {
		this.potentialPrecursors=potPrecursors;
		this.continouslyAvailableSubstances=cycles;
	}
	
	/**
	 * @return the set of substances that may be potential precursors to a certain substance
	 */
	public TreeSet<Integer> potentialPrecursors(){
		return potentialPrecursors;
	}
	
	/**
	 * @return the sets of substances found to occur in cycles
	 */
	public TreeSet<TreeSet<Integer>> continouslyAvailableSubstances(){
		return continouslyAvailableSubstances;
	}
}
