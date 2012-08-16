package main;

public class Config {
	/*
	 * the address of the restful API, entrypoint of application
	 */
	public static String InstanceAddress = "ec2-107-21-141-114.compute-1.amazonaws.com";
	/*
	 * the path of the directory, which contains a lot of images to test the application
	 */
	//public static final String ImageGaleryPath = "/home/hokotro/thesis_galery/";
	public static final String ImageGaleryPath = "/home/ubuntu/image/";
	
	/*
	 * mérési eredmény: 
	 * - ha 1.0 vagy annál kisebb, akkor nagyobb a szórás, 
	 * van benne egy-két kirívóan magas érték ami nekünk jelen esetben nem megfelelő
	 * - ha 3, 5 vagy nagyobb, akkor meg túl közeli értékeket kapunk, 
	 * és ebben a nagyon kicsi intervallumhoz képes nem kapunk szignifikáns eltéréseket
	 */
	public static final double ParetoAlpha = 3.0;
}
