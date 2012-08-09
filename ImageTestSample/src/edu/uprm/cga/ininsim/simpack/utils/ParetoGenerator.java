package edu.uprm.cga.ininsim.simpack.utils;

import java.util.Random;


/**
 * Pareto distribution random number generator.
 * 
 * @author Gabriel J. Pérez Irizarry
 */
public class ParetoGenerator
{

	static Random random = new Random();
	
  private double scale;

  public ParetoGenerator(final double scale)
  {
    this.scale = scale;
  }

  public final double generate() 
  {

    return ParetoGenerator.generate(this.scale);
  }

  /**
   * Pareto distribution random number generator.
   * 
   * <p>Generator using the inverse-transformation method:
   * 
   * <pre>x = 1 / u ^ (1/a)</pre>
   * 
   * <p>Note that the discrete uniform distribution is defined by the 
   * shape parameter, or equivalently:
   * <ul>
   * <li>mean = a / (a -1), for a > 1</li>
   * <li>variance = a / [(a - 1) ^2 * (a - 2)]. for a > 2</li>
   * </ul>
   * 
   * @param shape The shape parameter.
   * @return Generated random number.
   */
  public static double generate(final double shape)
  {
    return 1 / Math.pow(random.nextDouble(), 1 / shape);
  }

}
