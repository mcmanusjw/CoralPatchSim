package mcmanusjw.CoralPatchSimJuly2019;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Fix the division by 0 errors in the dead caco3 outputs!!!!!!!!!!!!!!! <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 * 
 * ToDo: Add a switch if you want to skip last species in similarity calculations <<<<<<<<
 *  
 * Fix the isCycled() issue, and check if we need two options as noted below <<<<<<<<<<<<<<<<<<<< (done??)
 * 
 * 
 * Calculations have been adjusted to the ratio of cells present 
 * to how many there should be in a circle of the reported radius, 
 * so as to account for damage to a coral.
 *  
 * @author John McManus
 *
 */

@SuppressWarnings("serial")
class StepCalculateAndStore implements Steppable
{
	// Fish size bin limits for three sizes S, M, L
	private static final double smlUpperBound = 5d;
	private static final double medUpperBound = 10d;

	// Non-final variables
	private static int seriesNumber; 
	private static int replicateNumber;
	private static int year;
	
	private static TableRunParamsListEnumMapsString runParamsTable;
	private static TableSpeciesListEnumMapsString   sppDataTable;
    private static TableOutputListEnumMapsDouble    origSpeciesResultsTable;
    private static TableOutputListEnumMapsDouble    annualSpeciesResultsTable;
    private static TableOutputListEnumMapsDouble    annualOverallResultsTable;
    //private static TableIndicesListEnumMapsDouble   annualIndexResultsTable;

    // To be initialized with each run
	//private static MapRunParams paramMap;    
	private static double caco3Previous;         // This will be used to calculate change between years in live 
	private static double caco3DeadSoFar;        // Accumulator for production of dead coral
	private static double overallCaco3DeadSoFar;
	
	// Values per species-form, but speciesID is not used -- only the ordering
	private static List < Double > speciesCaco3PreviousVals; 
	private static List < Double > speciesDeadCaco3Cumulater;
	private static List < Double > speciesSurfaces;
	private static List < Double > speciesAreas; // Use for putting into Competition Graph
 
	private static int numSpecies;
	private static String outputDirectoryString;
	private static String fileName; 
	
	StepCalculateAndStore()
	{
		init();
	}

     /**
     * 
     * Call init() once for every new series, but only once across replicates.
     * That means one set of replicates will be in one Excel Workbook.
     * This is called from CoralPatchSim.
     * 
     */
    static void init ()
    {
    	seriesNumber     = StepLooper.getSeriesNumber();
    	replicateNumber  = StepLooper.getReplicateNumber();
    	year             = 0;
    	
    	runParamsTable   		= StaticBenthosData.getRunParamsTable();
    	sppDataTable     		= StaticBenthosData.getSpeciesInputTable();
    	origSpeciesResultsTable = StaticBenthosData.getOrigSpeciesResults();
    	//origOverallResultsTable = StaticBenthosData.getOrigOverallResults();
    	
    	numSpecies = (int) runParamsTable.getDouble ( seriesNumber, "NumBenthicTypes" );
    	annualSpeciesResultsTable = new TableOutputListEnumMapsDouble ( numSpecies );
    	annualOverallResultsTable = new TableOutputListEnumMapsDouble (     1      );
    	//annualIndexResultsTable   = new TableIndicesListEnumMapsDouble(     1      );
    	
    	caco3Previous         = 0;
    	caco3DeadSoFar        = 0;
    	overallCaco3DeadSoFar = 0;
    	
    	speciesCaco3PreviousVals  = new ArrayList < Double >();
    	speciesDeadCaco3Cumulater = new ArrayList < Double >();
    	speciesSurfaces           = new ArrayList < Double >();
    	speciesAreas              = new ArrayList < Double >();
    	
    	numSpecies = (int) runParamsTable.getDouble ( seriesNumber, "NumBenthicTypes" );

    	// Setting up output file name and output file 
    	StringBuilder fileNameData = new StringBuilder();
    	
/* 		//Maybe put in this and broad damage info respectively if turned on /////////////////////////////////////////////     	
   		fileNameData.append( "_Damage_Years_" + ( int ) runParamsTable.getDouble ( seriesNumber, "LowerYearEllipse"    )
    						 + "_to_"         + ( int ) runParamsTable.getDouble ( seriesNumber, "UpperYearEllipse"    )
    		   		         + "_Percents_"   + ( int ) runParamsTable.getDouble ( seriesNumber, "LowerPercentEllipse" ) * 100
    				         + "_to_"         + ( int ) runParamsTable.getDouble ( seriesNumber, "UpperPercentEllipse" ) * 100 + "_" );
*/
    	
   		Date date = new Date();
    	DateFormat dateTime = new SimpleDateFormat( "hhmma_ddMMMyy" ); 
    	fileName = ( "CoralPatchSim_V1.0_Main_Output_" + fileNameData + dateTime.format( date ) + ".xls" );
    	outputDirectoryString = StaticBenthosData.getOutputDirectoryString();
    	
     	EnumSet< EnumOutputFields > labels = EnumSet.allOf( EnumOutputFields.class ); 
     	
    	//Target: makeOutputWorkbook( String fileName, EnumSet< EnumOutputFields > labels, int numSpecies )
       	if ( !UtilPoi.makeOutputWorkbook( outputDirectoryString, fileName, labels, numSpecies ) )
       	{
       		UtilJM.warner( " Could not create the main output file ");
       		return;
       	}
    }
    
    /**
     * Same as init() but without making a new file.
     * Call this for each new replicate after the first,
     * but use init() for a new series.
     */
    
    static void replicateInit ()
    {
       	seriesNumber     = StepLooper.getSeriesNumber();
    	replicateNumber  = StepLooper.getReplicateNumber();
    	year             = 0;
    	
    	runParamsTable   = StaticBenthosData.getRunParamsTable();
    	sppDataTable     = StaticBenthosData.getSpeciesInputTable();
    	origSpeciesResultsTable = StaticBenthosData.getOrigSpeciesResults();

    	numSpecies =  ( int ) runParamsTable.getDouble ( seriesNumber, "NumBenthicTypes" );
    	
    	annualSpeciesResultsTable = new TableOutputListEnumMapsDouble ( numSpecies );
    	annualOverallResultsTable = new TableOutputListEnumMapsDouble (     1      );
    	
    	caco3Previous         = 0;
    	caco3DeadSoFar        = 0;
    	overallCaco3DeadSoFar = 0;
    	
    	speciesCaco3PreviousVals  = new ArrayList < Double >();
    	speciesDeadCaco3Cumulater = new ArrayList < Double >();
    	speciesSurfaces           = new ArrayList < Double >();
    	speciesAreas              = new ArrayList < Double >();
    	
    	numSpecies =  ( int ) runParamsTable.getDouble ( seriesNumber, "NumBenthicTypes" );
    }

    @Override
	public void step(SimState state) 
	{
		if ( !PanelDamageButtonsAndInfoText.isOkToGo() ) return;

    	//System.out.println( " Now doing StepCalculateAndStore" );////////////////
    	
    	year++; // Starts with 1 because year 0 will be for original data for each replicate and series
    	
		TableListCoralSetNoNullNoDup coralByTypeTable = StaticCoral.getCoralByTypeTable();
		int numTypes = coralByTypeTable.numSets();
		
		speciesSurfaces = new ArrayList < Double >();
		speciesAreas    = new ArrayList < Double >();
		
		// Prepare for summing across all species (all corals). 
		// Neighbor values also initiated here as not associated with any one species
		int    overallNumCorals   = 0;
		int    overallCells       = 0;
		double overallVolume      = 0d;
		double overallSurface     = 0d;
		double overallBottomCover = 0d;
		double overallCaco3Live   = 0d; 
		double overallCaco3Change = 0d;
		double overallCaco3Dead   = 0d;
		double overallSmlFishVol  = 0d;
		double overallMedFishVol  = 0d;
		double overallLrgFishVol  = 0d;
		double nMdSum             = 0d;
		double nLgSum             = 0d;
		double combMdSum          = 0d;
		double combLgSum          = 0d;

		for( int i = 0; i < numTypes; i++ ) // Loop by species-form combination
		{
			//System.out.println( " Doing species: " + i );
			
			String growthForm      = sppDataTable.getString( i, "GrowthForm"        );
			double maxColumnHeight = sppDataTable.getDouble( i, "MaxColumnHeight"   );
			double maxColumnWidth  = sppDataTable.getDouble( i, "MaxColumnDiameter" );
			
			Set < StepCoral >  coralRowSet = coralByTypeTable.row( i );

			// Prepare for summing within a species
			int    countCells    = 0;
			double sumVolume     = 0;
			double sumSurface    = 0;
			double sumSmlFishVol = 0;
			double sumMedFishVol = 0;
			double sumLrgFishVol = 0;
			
			for( StepCoral stepCoral : coralRowSet ) 
			{
				int    numCells = stepCoral.getCells().size();
				
				// Here calculate number of expected cells for that radius, and make ratio to
				// apply to each returned calculated value to account for corals being damaged.
				// Apply only to surface, volume and refuge volumes. CaCO3 comes from volumes.
				int radius = stepCoral.getRadius();
				int expectedCells = ListOfCellCountsByRadius.radiusToCells.get( radius ); // Returns expected number of cells
				
				double damageRatio = ( double ) numCells / ( double ) expectedCells;
				
				if ( damageRatio > 1.0 ) damageRatio = 1.0;
				
				//This gets the Accessor Class for that growthform
				//InterfaceForAccessClasses accessClass = StaticCoral.getTextToAccess().get( growthForm );
				
				InterfaceForAccessClasses accessClass =	EnumAccessor.fromString( growthForm ).getAccessor();
				
				// Calculate diameter to use to get other calculated values from enums via accessor classes
				double diameter  = 2 * radius; // Use to get calculated values from enums via accessor classes
				if ( diameter < 2 ) diameter = 2;
				// Must match calculateFromString( String propertyString, double maxHeight, double maxWidth, Double diameter ) 
				double volume  = damageRatio * accessClass.calculateFromString ( "Volume"  , maxColumnHeight, maxColumnWidth, diameter );                                               
				double surface = damageRatio * accessClass.calculateFromString ( "Surface" , maxColumnHeight, maxColumnWidth, diameter );  
				
				// Accumulate this data on individual corals in accumulators for the species (= row)
				countCells += numCells;
				sumVolume  += volume;
				sumSurface += surface;
				
				// Declare these outside the 'if' statement.
				double refVolume  = 0;
				double fishLength = 0;
				
				if ( !accessClass.isTopandBottom() )
				{
					refVolume  =  damageRatio * accessClass.calculateFromString ( "R_Volume" , maxColumnHeight, maxColumnWidth, diameter );
					fishLength =  accessClass.calculateFromString ( "F_Length" , maxColumnHeight, maxColumnWidth, diameter );
					if (   fishLength <  smlUpperBound )      sumSmlFishVol += refVolume;
					if ( ( fishLength >= smlUpperBound ) && ( fishLength < medUpperBound ) )  
														      sumMedFishVol += refVolume;
					if (   fishLength >= medUpperBound )      sumLrgFishVol += refVolume;
				}
				else
				{
					// We simply compile both kinds of refuge volumes as per fish length, so we can reuse variables
					refVolume  =  damageRatio * accessClass.calculateFromString ( "R_Vol_Top" , maxColumnHeight, maxColumnWidth, diameter );
					fishLength =  accessClass.calculateFromString ( "F_Len_Top" , maxColumnHeight, maxColumnWidth, diameter );
					if (   fishLength <  smlUpperBound )      sumSmlFishVol += refVolume;
					if ( ( fishLength >= smlUpperBound ) && ( fishLength < medUpperBound ) )  
														      sumMedFishVol += refVolume;
					if ( fishLength >= medUpperBound )        sumLrgFishVol += refVolume;
					
					refVolume  =  damageRatio * accessClass.calculateFromString ( "R_Vol_Bot" , maxColumnHeight, maxColumnWidth, diameter );
					fishLength =  accessClass.calculateFromString ( "F_Len_Bot" , maxColumnHeight, maxColumnWidth, diameter );
					if (   fishLength <  smlUpperBound )      sumSmlFishVol += refVolume;
					if ( ( fishLength >= smlUpperBound ) && ( fishLength < medUpperBound ) )  
														      sumMedFishVol += refVolume;
					if (   fishLength >= medUpperBound )      sumLrgFishVol += refVolume;
				}
				
			} // end of loop across corals of a species
			
			// This is also used to adjust recruitment if set to variable
			speciesSurfaces.add( i, sumSurface );
			
			// Calculate percent cover from total cells across all corals for that species
			double width  = StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "Width"  );
			double height = width;
			double bottomCover = ( double ) countCells / ( height * width );
			speciesAreas.add( i, bottomCover );
			
			// Calculate CaCO3 here from total coral volume across all corals for that species
			double skeletalDensity = sppDataTable.getDouble( i, "SkeletalDensity" );
			double caco3Live       = skeletalDensity * sumVolume;
			double caco3Change     = 0d;
			if ( ( speciesCaco3PreviousVals.size() > i ) // If there are two items (size = 2), highest index is 1 
			  && ( speciesCaco3PreviousVals.get( i ) != null ) ) 
			{ 
				caco3Change = caco3Live - speciesCaco3PreviousVals.get( i );			
			}
			speciesCaco3PreviousVals.add( caco3Live ); // Ready for next round
			
			double caco3Dead = 0;
			if ( ( StaticCoralCell.getSpeciesCaco3AnnualCumulator().size() > i ) // If there are two items (size = 2), highest index is 1 
			  && ( StaticCoralCell.getSpeciesCaco3AnnualCumulator().get( i ) != null ) ) 
			{ 
				caco3Dead = StaticCoralCell.getSpeciesCaco3AnnualCumulator().get( i ); 
			}
			
			double previousTotal = 0;
			// If results have been stored from two species so far (size = 2), highest index is 1
			if ( ( speciesDeadCaco3Cumulater.size() > i )  
			  && ( speciesDeadCaco3Cumulater.get( i ) != null ) ) 
			{ 
				previousTotal = speciesDeadCaco3Cumulater.get( i );
				speciesDeadCaco3Cumulater.set( i, ( previousTotal + caco3Dead ) );
			}
			else // working on first entry for a species
			{
				// Reset cumulater by species each time we are working 1st entry for species #0 in replicate #1
				int replicateNumber = StepLooper.getReplicateNumber();
				if ( ( i == 0 ) && ( replicateNumber == 1 ) )
				{
					speciesDeadCaco3Cumulater.clear();
				}

				speciesDeadCaco3Cumulater.add( caco3Dead );
			}
			
			int numCorals = coralRowSet.size();
			
			// Put each of those 9 values into  accumulators 
			overallNumCorals     += numCorals;
			overallCells         += countCells;
			overallBottomCover   += bottomCover;
			overallVolume        += sumVolume;
			overallCaco3Live     += caco3Live;
			overallSurface       += sumSurface;
			overallSmlFishVol    += sumSmlFishVol;
			overallMedFishVol    += sumMedFishVol;
			overallLrgFishVol    += sumLrgFishVol;
			
			// Proper order is "|| Year, Total Corals      |\t Total Cells        |\t BottomCover       |\t"
	        //                  +" Volume            |\t CaCO3              |\t Surface Area      |\t"
		    //                  +" Small Fish Refuge |\t Medium Fish Refuge |\t Large Fish Refuge ||";
			//
			// For data by species, put out the species name before each data set
			double caco3DeadSoFar = 0;
			if ( ( speciesDeadCaco3Cumulater.size() > i ) // If there are two items (size = 2), highest index is 1 
			  && ( speciesDeadCaco3Cumulater.get( i ) != null ) ) 
			{ 
				caco3DeadSoFar = speciesDeadCaco3Cumulater.get( i );
			}
			annualSpeciesResultsTable.put ( i, "NumCorals",      numCorals      );
			annualSpeciesResultsTable.put ( i, "CountCells",     countCells     );
			annualSpeciesResultsTable.put ( i, "Volume",         sumVolume      );
			annualSpeciesResultsTable.put ( i, "Surface",        sumSurface     );
			annualSpeciesResultsTable.put ( i, "BottomCover",    bottomCover    );
			annualSpeciesResultsTable.put ( i, "Caco3Live",      caco3Live      );
			annualSpeciesResultsTable.put ( i, "Caco3Change",    caco3Change    );
			annualSpeciesResultsTable.put ( i, "Caco3Dead",      caco3Dead      ); 
			annualSpeciesResultsTable.put ( i, "caco3DeadSoFar", caco3DeadSoFar );
			annualSpeciesResultsTable.put ( i, "SmlFishVol",     sumSmlFishVol  );
			annualSpeciesResultsTable.put ( i, "MedFishVol",     sumMedFishVol  );
			annualSpeciesResultsTable.put ( i, "LrgFishVol",     sumLrgFishVol  );
			annualSpeciesResultsTable.put ( i, "NeighborMedFishVol", -1d        );
			annualSpeciesResultsTable.put ( i, "NeighborLrgFishVol", -1d        );
			annualSpeciesResultsTable.put ( i, "CombinedMedFishVol", -1d        );
			annualSpeciesResultsTable.put ( i, "CombinedLrgFishVol", -1d        );

		} // end of loop across all species in the table

		double caco3Change = overallCaco3Live - caco3Previous; 
		caco3Previous = overallCaco3Live; // Save this year's value for calculating change next year
		
		double caco3Dead = StaticCoralCell.getCaco3AnnualCumulator(); // Calculated from cells killed
		StaticCoralCell.setCaco3AnnualCumulator( 0d ); // Clearing annual cumulator (keeps it annual).

		overallCaco3Change     = caco3Change;
		overallCaco3Dead       = caco3Dead;
		overallCaco3DeadSoFar += caco3Dead; 
		
		// Capture annual neighbor refuge by fish size. No small fish are possible here. 
		// Neighbor distance is based on minimum gap width of 1 cell = 1 cm. Thus fish lengths are never < 5 cm.
		// Combine with sums so far for medium and large fish.
		nMdSum    = 0d;
		nLgSum    = 0d;
		
		// Must calculate the neighbor refuge values by fish size from StaticCoral, 
		// as calculated previously this year in StepCalculateNeighborRefuge.
		// These do not need damage correction because they are volumes between corals.
		if( !StepCalculateNeighborRefuge.getNeighborFishLengthToRefugeVolume().isEmpty() )
		{
			Map < Double, Double > lengthToVols = StepCalculateNeighborRefuge.getNeighborFishLengthToRefugeVolume();
			Set < Double > fishLengths = lengthToVols.keySet();
		
			for( Double fishLength: fishLengths )
			{
				Double refVolume = lengthToVols.get( fishLength );
				if ( ( fishLength >= smlUpperBound ) && ( fishLength < medUpperBound ) ) 
				{
				     nMdSum    += refVolume;
				}
				if (   fishLength >= medUpperBound )
				{
					 nLgSum    += refVolume;
				}
			}
		}
		
		// Combine neighbor volumes with sums for medium and large fish.
		combMdSum = overallMedFishVol + nMdSum;
		combLgSum = overallLrgFishVol + nLgSum;	
		
		annualOverallResultsTable.put ( 0, "NumCorals",          overallNumCorals      );
		annualOverallResultsTable.put ( 0, "CountCells",         overallCells          );
		annualOverallResultsTable.put ( 0, "Volume",             overallVolume         );
		annualOverallResultsTable.put ( 0, "Surface",            overallSurface        );
		annualOverallResultsTable.put ( 0, "BottomCover",        overallBottomCover    );
		annualOverallResultsTable.put ( 0, "Caco3Live",          overallCaco3Live      );
		annualOverallResultsTable.put ( 0, "Caco3Change",        overallCaco3Change    );
		annualOverallResultsTable.put ( 0, "Caco3Dead",          overallCaco3Dead      ); 
		annualOverallResultsTable.put ( 0, "caco3DeadSoFar",     overallCaco3DeadSoFar );
		annualOverallResultsTable.put ( 0, "SmlFishVol",         overallSmlFishVol     );
		annualOverallResultsTable.put ( 0, "MedFishVol",         overallMedFishVol     );
		annualOverallResultsTable.put ( 0, "LrgFishVol",         overallLrgFishVol     );
		annualOverallResultsTable.put ( 0, "NeighborMedFishVol", nMdSum                );
		annualOverallResultsTable.put ( 0, "NeighborLrgFishVol", nLgSum                );
		annualOverallResultsTable.put ( 0, "CombinedMedFishVol", combMdSum             );
		annualOverallResultsTable.put ( 0, "CombinedLrgFishVol", combLgSum             );
		
		// Calculate similarity indices if needed
		// int     seriesNumber     = StepLooper.getSeriesNumber();
		//boolean outputDataExists = StaticBenthosData.getRunParamsTable().getBoolean( seriesNumber, "IncludeOriginalSizeDataTF" ); 

		// Get lists for original and annual, and then do indices
		List < Double > origNums  = origSpeciesResultsTable  .getAllValuesForFieldByText( "NumCorals"  );// These use species number order
		List < Double > origAreas = origSpeciesResultsTable  .getAllValuesForFieldByText( "CountCells" );
		List < Double > simNums   = annualSpeciesResultsTable.getAllValuesForFieldByText( "NumCorals"  );// These should also use species number order
		List < Double > simAreas  = annualSpeciesResultsTable.getAllValuesForFieldByText( "CountCells" );
		
		// Make this an option for Run Parameters! -- skip last species to avoid algae, etc. <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		if( numSpecies > 5 ) // <<<<<<<<<<<<<<<<<<<<<<<<<< put in the switch instead <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		{
			origNums .remove( numSpecies - 1 );
			origAreas.remove( numSpecies - 1 );
			simNums  .remove( numSpecies - 1 );
			simAreas .remove( numSpecies - 1 );
		}
		
		System.out.println( "origNums.size(): " + origNums.size() + " simAreas.size(): " + simNums.size() );
		System.out.println( "origNums:" + origNums );
		System.out.println( "simNums :" + simNums  );
		System.out.println();
		
		System.out.println( "origAreas.size(): " + origNums.size() + " simAreas.size(): " + simNums.size() );
		System.out.println( "origAreas:" + origAreas );
		System.out.println( "simAreas :" + simAreas  );
		System.out.println();

        // Do similarity indices
		double psNum  = EnumIndices.PERCENT_SIMILARITY_NUMBERS.calculate(origNums , simNums  );
		double psArea = EnumIndices.PERCENT_SIMILARITY_AREA   .calculate(origAreas, simAreas );
		double rsNum  = EnumIndices.RUZICKA_SIMILARITY_NUMBERS.calculate(origNums , simNums  );
		double rsArea = EnumIndices.RUZICKA_SIMILARITY_AREA   .calculate(origAreas, simAreas );
		
		EnumMap < EnumIndices, Double > mapSimIndex = new EnumMap < EnumIndices, Double > ( EnumIndices.class );
		
		mapSimIndex.put( EnumIndices.PERCENT_SIMILARITY_NUMBERS, psNum  );
		mapSimIndex.put( EnumIndices.PERCENT_SIMILARITY_AREA,    psArea );
		mapSimIndex.put( EnumIndices.RUZICKA_SIMILARITY_NUMBERS, rsNum  );
		mapSimIndex.put( EnumIndices.RUZICKA_SIMILARITY_AREA,    rsArea );
	
		UtilPoi.enterAnnualData( outputDirectoryString, fileName, origSpeciesResultsTable, 
				                 annualOverallResultsTable, annualSpeciesResultsTable,
				                 mapSimIndex,   replicateNumber, year, numSpecies );
		
		// Pass updated values to 4 graphs (fish refuge graph has 3 lines), so 6 datasets
		// Note that all values so far are from cm to cm2, or cm3, or grams. We display the graphs as m2, m3, or kg. 
		// To convert cm2 to m2, divide by 10,000
		// To convert cm3 to m3, divide by 1,000,000
		// To convert gm  to kg, divide by 1,000 
		List < Double > yVals = new ArrayList < Double >( 6 ); 
		yVals.add( overallBottomCover * 100d     ); // note that begins as 0 1o 1, not 0 to 100%
		yVals.add( overallSurface     / 10000d   );
		yVals.add( overallCaco3Live   / 1000d    );
		yVals.add( overallSmlFishVol  / 1000000d );
		yVals.add( overallMedFishVol  / 1000000d );
		yVals.add( overallLrgFishVol  / 1000000d );
		
		for (int i = 0; i < numSpecies; i++) 
		{
			yVals.add( speciesAreas.get( i ) * 100d );
		}
		
		//Sending update to ConsoleBig.addValuePair( List < Double > yVals, SimState state )
		ConsoleBig.addValuePairs( yVals, state );
		
		//System.out.println( "Added to graphs for state: " +  yVals ); //////////////////
		
		//System.out.println(" Done filing  for year: " + year + "\n" );

	} // end of step method
	
	static double getCaco3DeadSoFar() {
		return caco3DeadSoFar;
	}

	static TableOutputListEnumMapsDouble getOrigResults() {
		return origSpeciesResultsTable;
	}

	static List<Double> getSpeciesSurfaces() {
		return speciesSurfaces;
	}

	static double getSmlupperbound() {
		return smlUpperBound;
	}

	static double getMedupperbound() {
		return medUpperBound;
	}
}
