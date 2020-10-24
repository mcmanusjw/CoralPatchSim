package mcmanusjw.CoralPatchSimJuly2019;

import java.util.EnumMap;
import java.util.List;

/**
 * NOTE: 
 * Check 500x500 and scale <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 * 
 * This version is for CoralPatchSim with data read from an Excel workbook via StaticInputRunInfo, 
 * or from StaticDefaultFastRunData, via StaticBenthosData.
 * 
 * Analyzes data file of original coral sizes by species-growthform designations.
 * 
 * The area from which the original data were taken is used, and this routine will divide
 * to get per sq meter values and raise it to the grid size. 
 * 
 * @author John McManus
 *
 */

class UtilOriginalDataPrepper 
{
	private static TableOutputListEnumMapsDouble origSpeciesResults;
	private static TableOutputListEnumMapsDouble origOverallResults;
	private static boolean complete = false;
	
	private static void processOriginalData() // Must be run from the getters
	{
    	complete = true; // OK to get the two tables

		int seriesNumber = StepLooper.getSeriesNumber();
		
		boolean originalDataAvailable 
				= StaticBenthosData.getRunParamsTable().getBoolean( seriesNumber, "IncludeOriginalSizeDataTF" );
		if ( !originalDataAvailable ) return;
		
		int numBenTypes = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "NumBenthicTypes" );
		
		origSpeciesResults = new TableOutputListEnumMapsDouble( numBenTypes );
		origOverallResults = new TableOutputListEnumMapsDouble(      1      );
		
		// Get the list of file entries	
		List < IntDouble > originalDataList;
		
     	if( StaticBenthosData.isUsingDefaultData() )
		{
			originalDataList = StaticBenthosData.getOriginalDataList();
		}
		else
		{
			originalDataList = StaticInputRunInfo.getOriginalDataList();
		}

	    // Fish size bin limits for three sizes S, M, L
		final double smlUpperBound = StepCalculateAndStore.getSmlupperbound();
		final double medUpperBound = StepCalculateAndStore.getMedupperbound();
		
		//double originalArea    = StaticInputRunInfo.getOriginalSampleArea();
		double originalArea = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "OriginalSiteArea" );
		
		// Process each speciesId, value pair (each coral)
		for ( IntDouble pair : originalDataList )
		{
			int    speciesId         = pair.x;
		    double diameter          = pair.y;
		    double maxColumnDiameter = (int) StaticBenthosData.getSpeciesInputTable().getDouble ( speciesId, "MaxColumnDiameter" );
		    double maxColumnHeight   = (int) StaticBenthosData.getSpeciesInputTable().getDouble ( speciesId, "MaxColumnHeight"   );
	    	double skeletalDensity   = StaticBenthosData.getSpeciesInputTable()      .getDouble ( speciesId, "SkeletalDensity"   );
		    String growthForm        = StaticBenthosData.getSpeciesInputTable()      .getString ( speciesId, "GrowthForm"        );
	    	
		    //if ( speciesId == 4 )
		    //{
		    //	System.out.println( "\nspeciesId:    " + speciesId +      " diameter:     "    + diameter     );
		    //	System.out.println( "maxColumnWidth: " + maxColumnWidth + " maxColumnHeight: " + maxColumnHeight );
		    //	System.out.println( "growthForm:     " + growthForm + "\n" );
		    //	UtilJM.warner( "Reading done");
		    //}
	    	
			//This gets the Accessor Class for that growthform
			//InterfaceForAccessClasses accessClass = StaticCoral.getTextToAccess().get( growthForm );
			
			InterfaceForAccessClasses accessClass =	EnumAccessor.fromString( growthForm ).getAccessor();

			double calculatedValue = 0; 
			
			// Calculate all values for that coral and add to correct category for the species and overall (index = 0).
		    for( String text: accessClass.getAllPropertyStrings() )
		    {
		    	if ( text.contains( "R_" ) ) // For refuge volume
		    	{
					// Declare this outside the next 'if' statement.
					double fishLength = 0;
					
					if ( !accessClass.isTopandBottom() ) // Only top values needed
					{
						calculatedValue =  accessClass.calculateFromString ( "R_Volume" , maxColumnHeight, maxColumnDiameter, diameter );
						fishLength      =  accessClass.calculateFromString ( "F_Length" , maxColumnHeight, maxColumnDiameter, diameter );
						if (   fishLength <  smlUpperBound )
						{
							//speciesId starts with 0, which we use for overall, so we move them all up by 1 for this
							origSpeciesResults.addIn( speciesId, "SmlFishVol", calculatedValue ); 
							origOverallResults.addIn(         0, "SmlFishVol", calculatedValue );
						}
						if ( ( fishLength >= smlUpperBound ) && ( fishLength < medUpperBound ) ) 
						{
							origSpeciesResults.addIn( speciesId, "MedFishVol", calculatedValue );
							origOverallResults.addIn(         0, "MedFishVol", calculatedValue );
						}
						if (   fishLength >= medUpperBound )
						{
							origSpeciesResults.addIn( speciesId, "LrgFishVol", calculatedValue );
							origOverallResults.addIn(         0, "LrgFishVol", calculatedValue );
						}
					}
					else // top and bottom values need to be calculated
					{
						// We simply compile both kinds of refuge volumes as per fish length, so we can reuse variables
						calculatedValue  =  accessClass.calculateFromString ( "R_Vol_Top" , maxColumnHeight, maxColumnDiameter, diameter );
						fishLength       =  accessClass.calculateFromString ( "F_Len_Top" , maxColumnHeight, maxColumnDiameter, diameter );
						if (   fishLength <  smlUpperBound )
						{
							origSpeciesResults.addIn( speciesId, "SmlFishVol", calculatedValue );
					    	origOverallResults.addIn(         0, "SmlFishVol", calculatedValue );
						}
						if ( ( fishLength >= smlUpperBound ) && ( fishLength < medUpperBound ) ) 
						{
							origSpeciesResults.addIn( speciesId, "MedFishVol", calculatedValue );
							origOverallResults.addIn(         0, "MedFishVol", calculatedValue );
						}
						if (   fishLength >= medUpperBound )
						{
							origSpeciesResults.addIn( speciesId, "LrgFishVol", calculatedValue );
							origOverallResults.addIn(         0, "LrgFishVol", calculatedValue );
						}	
						
						calculatedValue =  accessClass.calculateFromString ( "R_Vol_Bot" , maxColumnHeight, maxColumnDiameter, diameter );
						fishLength      =  accessClass.calculateFromString ( "F_Len_Bot" , maxColumnHeight, maxColumnDiameter, diameter );
						if (   fishLength <  smlUpperBound )
						{
							origSpeciesResults.addIn( speciesId, "SmlFishVol", calculatedValue );
							origOverallResults.addIn(         0, "SmlFishVol", calculatedValue );
						}
						if ( ( fishLength >= smlUpperBound ) && ( fishLength < medUpperBound ) ) 
						{
							origSpeciesResults.addIn( speciesId, "MedFishVol", calculatedValue );
							origOverallResults.addIn(         0, "MedFishVol", calculatedValue );
						}
						if (   fishLength >= medUpperBound )
						{
							origSpeciesResults.addIn( speciesId, "LrgFishVol", calculatedValue );
							origOverallResults.addIn(         0, "LrgFishVol", calculatedValue );
						}
					}
					break; // go to add coral and to next coral, so a to skip any other "R_" or "F_" fields such as top and bottom
		    	}
		    	else // if not looking for an addition to a refuge volume, but CountCells, surface or volume or bottom cover instead
		    	{
		    		calculatedValue = accessClass.calculateFromString( text, maxColumnDiameter, maxColumnHeight, diameter); 
		    		
		    		origSpeciesResults.addIn( speciesId, text, calculatedValue );
		    		origOverallResults.addIn(         0, text, calculatedValue );
		    	
		    		// Additional calculation if this was Volume
		    		if ( text == "Volume")
		    		{
		    			double caco3 = calculatedValue * skeletalDensity;
		    			origSpeciesResults.addIn( speciesId, "Caco3Live", caco3 );
		    			origOverallResults.addIn(         0, "Caco3Live", caco3 );
		    		}
		    	}
		    } // end of loop by growthform fields
		    
		    origSpeciesResults.addIn( speciesId, "NumCorals", 1d );
		    origOverallResults.addIn(         0, "NumCorals", 1d );

	    	// Bottom cover is the sum of sq. cm. divided by the area. 
    		// Diameter is in cm, so result is in sq cm.
    		// This is raised to the whole grid size later on.
    		double radius = diameter / 2d;
    		double areaPerCoral = Math.PI * radius * radius / 10000; // this gives the area in sq m from sq cm
    		
    		double countCells   = ( int ) Math.round( areaPerCoral * 10000 ); // raise back to sq cm and round off
    		origSpeciesResults.addIn( speciesId, "CountCells", countCells );
    		origOverallResults.addIn(         0, "CountCells", countCells );
    		
    		double percentArea  = areaPerCoral / originalArea;
    		origSpeciesResults.addIn( speciesId, "BottomCover", percentArea );
    		origOverallResults.addIn(         0, "BottomCover", percentArea );
 
		} // End loop by file line
		
		System.out.println( " Num indivduals: " + origSpeciesResults.getValue( 0, "NumCorals" ) );

    	// Raise all values (should per per sq cm) to grid size.
		int    gridWidth  = ( int ) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "Width" );
		int    gridHeight = gridWidth;
    	double gridSize   = gridWidth * gridHeight; 
    	double multiplier = gridSize / originalArea;
    	
    	System.out.println( " gridSize: " + gridSize + " originalArea: " + originalArea + " multiplier: " + multiplier   );
    	
		// First loop across outer ArrayList
	    for (int i = 0; i < origSpeciesResults.numMaps(); i++)
	    {
	    	EnumMap < EnumOutputFields, Double > map = origSpeciesResults.getMap( i );
	    	for( EnumOutputFields field: EnumOutputFields.values() )
			{
				double value = map.get( field );
				if ( field ==  EnumOutputFields.NUMCORALS ) System.out.println( " NumCorals: " + value ); ///////////////////////////////
				
				if ( field != EnumOutputFields.BOTTOMCOVER ) // This is already in percent form
				{
					//System.out.println( "in UtilOrig field = " + field + " value = " + value + " multiplier = " + multiplier);/////////////////////
					value = value * multiplier;
					map.put( field, value ); // Note that this replaces original value.
				}
			}
		}
	    
	    System.out.println( " Num indivduals after raising: " + origSpeciesResults.getValue( 0, "NumCorals" ) );
	    
	    // Do the same for original overall results where only the 0 row exists
    	EnumMap < EnumOutputFields, Double > map = origOverallResults.getMap( 0 );
    	for( EnumOutputFields field: EnumOutputFields.values() )
		{
			double value = map.get( field );
			if ( field != EnumOutputFields.BOTTOMCOVER ) // This is already in percent form
			{
				//System.out.println( "in UtilOrig field = " + field + " value = " + value + " multiplier = " + multiplier);/////////////////////
				value = value * multiplier;
				map.put( field, value ); // Note that this replaces original value.
			}
		}
		return;
	}

	public static TableOutputListEnumMapsDouble getOrigSpeciesResults() 
	{
		if ( !complete ) processOriginalData(); // Calling for the results initiates the processing run if not done previously
		return origSpeciesResults;
	}

	public static TableOutputListEnumMapsDouble getOrigOverallResults() 
	{
		if ( !complete ) processOriginalData(); // Calling for the results initiates the processing run if not done previously
		return origOverallResults;
	}
	
}