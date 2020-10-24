package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import sim.engine.SimState;
import sim.engine.Steppable;

@ SuppressWarnings ( "serial" )
class StepReaper implements Steppable
{
	// This variable will change later on if you are running a test
	// private static Boolean	test	= false;

	/**
	 * From March 29, 2013, revised for age from radius December 31, 2017 
	 * 
	 * This method assumes that the frequency of individuals per age at any point in time is equal to that
	 * expected if one cohort were to be followed over time, as assumed in a standard catch-curve. We use 
	 * N(t) = N(0)exp(-Z * t), where N(0) is the recruitment level. 
	 * Because of overgrowth and potentially additional storm damage, etc., there will
	 * often be additional mortality anyway, but other causes of natural mortality exist in nature, and this
	 * ensures realistic age-frequency spectra. Our approach accounts for all other forms of mortality
	 * by just adding to killed corals when the numbers in a year exceed the expected for that year.  
	 * 
	 * We do not impose changes if the number of individuals unless they fall below our calculated 
	 * survival curve. This assumes that the background natural mortality tends to be density dependent,
	 * perhaps including some form of switching to more abundance categories when one category is sparse,
	 * something like interspecies predation switching but within the species.  
	 * 
	 * Note that effective age here is calculated from the coral radius and its growth rate per year, 
	 * so that a new effective age still comes out of a fragment. Here, we assume that whatever 
	 * the causes of background coral mortality are, they tend to vary with coral size more than 
	 * actual age. 
	 * 
	 * We get the current level of recruitment, already set for the size of the simulation area,
	 * from StepRecruiter, wherein it may be altered in cases of density-dependent recruitment by 
	 * adjusting it for deviations from the original sample's surface area by species. 
	 * Note that the recruitment level, being an intercept, moves the survival curve up or down
	 * without affecting its shape, which is set by mortality rate.   
	 * 
	 * Method to determine how many and which corals to kill as follows: 
	 * 1. For each BenthicType 
	 * 2.   Get age, number at age and expected number from that age (n_At_T) from empirical intercept and slope 
	 * 3.     Find out how many are there (n_Actual ) 
	 * 4.     Subtract ( n_Actual - n_At_T ) to find (n_To_Remove). 
	 * 5.     Select n_To_Remove indices within the n_Actual indices at that size and put them into an array. 
	 * 6.     Kill the corals with those indices in a loop through a shuffled array of indices to remove. 
	 * 10.  Cycle to next age if present (skip a size if the previous has zero abundance) 
	 * 11. Cycle to next BenthicType
	 */

	@ Override
	public void step( SimState state )
	{
		if ( !PanelDamageButtonsAndInfoText.isOkToGo() ) return;

		//System.out.println( " Now doing StepReaper ");

		// Run inputs
		int seriesNumber = StepLooper.getSeriesNumber();
		int numBenthicTypes = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "NumBenthicTypes" );
		TableListCoralSetNoNullNoDup coralByTypeTable = StaticCoral.getCoralByTypeTable();

		// Get intercepts ( = recruits), mortalities and growthRates for each benthic type for this cycle
		List < Integer > targetIntercepts  = StepRecruiter.getNumbersOfRecruitsScaled();
		List < Double  > targetMortalities 
		                 = StaticBenthosData.getSpeciesInputTable().getAllDoubleValuesForString( "Mortality" );

		// For each BenthicType
		for ( int i = 0; i < numBenthicTypes; i++ )
		{
			// Skip if no mortality rate (such as some algae patches)
			if ( targetMortalities.get( i ) < 0.00001 ) continue;

			Set < StepCoral > row = coralByTypeTable.row( i );
			// Skip if not enough corals for analysis
			if ( row.size() < 4 ) continue; 

			List < StepCoral > rowList = new ArrayList < StepCoral >( row );
			List < Integer > ages = new ArrayList < Integer >();
			ages.clear();

			// Get list of effective ages and find maximum age,
			// which will be the number of categories (1 year each)
			int maxAge = Integer.MIN_VALUE;
			for ( StepCoral stepCoral : rowList )
			{
				int age = stepCoral.getRadius() / stepCoral.getGrowthRate();
				ages.add( age );
				
				if ( age > maxAge ) maxAge = age;
			}

     		// Make into year-category histogram (discrete survival curve)
			// as a List where the index is the start of each effective age year.
			List < Integer > actualHistoList = UtilJM.makeAnnualHistoList( ages, maxAge ); 

			// Get intercept as N0 and instantaneous mortality rate for that BenthicType
			// Nt = N0 * e^(-m * t).
			// I use bPrime to represent b', the non-ln value of the intercept
			// (actual average count per unit area of annual recruits scaled to the grid).
			double bPrime = targetIntercepts.get( i );
			double m =  targetMortalities.get( i );

			List < Integer > expectedHistoList = new ArrayList < Integer >( maxAge );
			
			// Get expected values using empirical slope and intercept 
			for ( int j = 0; j < maxAge; j++ )
			{
				double expected = Math.exp( ( -m ) * (double) j ) * bPrime; 

				// Convert to int
				int upperLimit = ( int ) Math.ceil( expected );
				expectedHistoList.add( upperLimit );
			}
/*			
			if ( i == 4 )
			{
				System.out.println( "\nFor species: " + i ); //////////////////////flag
				System.out.println( " with max age: " + maxAge ); //////////////////////flag
				System.out.println( " with m = " + m ); //////////////////////flag
				System.out.println( " with bPrime = " + bPrime ); //////////////////////flag
				System.out.println( "Actual    histogram: " + actualHistoList  ); //////////////////////flag
				System.out.println( "Expected histogram: " + expectedHistoList ); //////////////////////flag
			}
*/			
			// Loop by age
			for ( int j = 0; j < maxAge; j++ ) 
			{
				// Find out how many of species i of age j are present vs. expected-- using starting ages including 0 to 1.
				int nActual   = actualHistoList  .get( j );
				int expected  = expectedHistoList.get( j); 
				int nToRemove = 0;

				if ( nActual > expected ) // Note that being less than the expected is OK.
				{
					nToRemove = nActual - expected;
				} 
				else
				{
					continue;
				}
				
				// First get the coral indices to remove:
				List < StepCoral   > coralsToKill  = new ArrayList < StepCoral >();
				List < Integer > randomIndices = makeRandomIndices( row, state );

				int removalCounter = 0;

				// Look for each coral of that age in order of the shuffled
				// indices, stopping when enough have been identified and stored.
				// Only those corals are stored of the proper age j.
				// The removal counter must only increment when an index has
				// been grabbed for a coral to remove.
				for ( Integer index : randomIndices )
				{
					StepCoral stepCoral = rowList.get( index ); 
					int age = stepCoral.getRadius() / stepCoral.getGrowthRate();
					if ( age == j )
					{
						coralsToKill.add( stepCoral );
						removalCounter++ ;
					}
					if ( removalCounter == nToRemove ) break;
				}

				// Now kill all the corals in the coralsTokill ArrayList
				// We do not kill them in the loop above, as that would mess up the indexing
				//System.out.println( "       starting kill loop " ); ///////////////////////////////////////
				coralsToKill.removeIf(Objects::isNull); // should not need this <<<<<<<<<<<<
				for ( StepCoral stepCoral : coralsToKill )
				{
					StaticCoral.killNotFromCellKill( stepCoral );
					if( coralByTypeTable.containsValue( stepCoral ) )
					{
						//System.out.println( " \n " + stepCoral + " " + stepCoral.getName() );
						//coralByTypeTable.printTableToScreen();
						UtilJM.warner( "coral not eliminated in StepReaper", true );
					}
				}
				//System.out.println( "       ending kill loop " ); ///////////////////////////////////////
			} // End of age loop
		} // End of BenthicType loop
		
//		System.out.println( "   Done with StepReaper ");
		
	} // End of step method 

	private List < Integer > makeRandomIndices( Set < StepCoral > row, SimState state )
	{
		// Make list of indices for the corals in the row for later sorting
		// Note that these may include gaps, so an indexed loop may not work. 
		List < Integer > randomIndices = new ArrayList < Integer >( row.size() );

		for ( int i = 0; i < row.size(); i++ )
		{
			randomIndices.add( i );
		}

		// Shuffle that list of coral indices 
		// so can later remove corals from that row randomly
		UtilJM.shuffleList( randomIndices , state);

		return randomIndices;
	}

	// Getters and setters follow -----------------------------------------
//	static void setTest( Boolean test )
//	{
//		StepReaper.test = test;
//	}

}
