package mcmanusjw.CoralPatchSimJuly2019;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  TestGrowthFormEnums
 *  
 *  Loops through a hard-coded list of enum access classes, 
 *  gets each enum's list of property strings (text we put next to enum value), 
 *  passes values to each enum value, 
 *  retrieves results from calculation for each property, 
 *  and stores the results. 
 *  
 *  To use, be sure the access classes implement the AccessInterface, and just replace the 
 *  values in List accessClasses (i.e. new ColumnAceess(), etc. ). The output file will be in the current directory (usually in a
 *  package such as "DigiReef" within the active Eclipse workspace).
 *  
 * @author John McManus
 * @version 26 October 2017 
 *
 */

class JMTestGrowthFormEnums 
{
	static double dmc; // Max diameter of a column, branch, or branchlet 
	static double hmc; // Max height   of a column, branch, or branchlet
	
	static List < Double > diameters = List.of( 20d, 40d, 60d, 80d, 100d );
	
	static List < Double > results  = new ArrayList < Double > ( 5 );
	
	static List < InterfaceForAccessClasses > accessClasses = List.of( new AccessColumn(),															 
			                                                           new AccessColumnEncrusting(),
			                                                           new AccessColumnPlate(),
			                                                           new AccessEncrusting(), 
			                                                           new AccessFoliose(),
			                                                           new AccessFunnel(),
			                                                           new AccessMassive(),
			                                                           new AccessPlate(),
			                                                           new AccessBlades(),
			                                                           new AccessTable(),
			                                                           new AccessBarrel(),
			                                                           new AccessTube()       );
	
	static List < String > propertyStrings;

	static Date date = new Date();
    static DateFormat dateTime = new SimpleDateFormat("hhmma_ddMMMyy"); 
    static String fileName = ("TestGrowthFormEnums" + dateTime.format(date)+".txt");
     
	static void testGrowthForms()
	{
		// Loop through each growthform access class
		for (InterfaceForAccessClasses accessClass : accessClasses )
		{
			// Get String name of the enum being addressed from the access class
			String enumName = accessClass.getEnumString();
			
			// Get List of properties available for that enum
			propertyStrings = accessClass.getAllPropertyStrings();
			
			// Set parameters for this growthform

			double maxHeight = ( EnumGrowthForms.fromString( enumName )).getTestMaxHeight();
			double maxWidth  = ( EnumGrowthForms.fromString( enumName )).getTestMaxWidth();
			
			// Loop through each property, have access class calculate a value and add to results List which is passed to here.
			for( String propertyString : propertyStrings )
			{
				results.clear();

				// For each diameter
				for( Double diameter : diameters )
				{
					double value = accessClass.calculateFromString( propertyString, maxHeight, maxWidth, diameter );
					System.out.println("\n" + value + " from " + enumName + " " 
					                        + propertyString + " " + maxHeight + " " + maxWidth + " " + diameter);
					results.add( value );
				}
				// Store results List by appending to file started when the class was first accessed (which sets the time for the file name)
				fileArray( fileName, enumName, propertyString, diameters, results );
			}
		}
	}
    
	/**
	 * Call this after getting each property within each growthform enum and looping though the List of diameters
	 * 
	 * @param fileName
	 * @param growthFormString
	 * @param property
	 * @param results
	 * @return
	 */
	static boolean fileArray( String fileName, String growthFormString, String property, List < Double > diameters, List < Double > results )
	{
		boolean ok = true;
		
		// Open PrintWriter with automatic flushing on println (true), with a FileWriter for appending (true). 
		try( PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( fileName, true )), true)) 
		{
			out.println( );
			out.println( growthFormString + ": " + property ); // Label by growthform and property

			for (int i = 0; i < results.size(); i++) 
			{
				out.println( " " + diameters.get(i) + "\t = " + results.get( i )  ); // simple list of values
			}
			out.close(); // not sure if needed
		}
		catch (IOException e) 
		{
			UtilJM.warner ( "Problem filing" ); 
			ok = false;
		}
		return ok;
    }
    
	public static void main(String[] args) 
	{
		testGrowthForms();
	}
}
