package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.List;

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

@SuppressWarnings("serial")

class StepRecruiter implements Steppable 
{
	private static final List < Double > recruitsPerSqM 
						 = StaticBenthosData.getSpeciesInputTable().getAllDoubleValuesForString( "RecruitsPerSqM" );
	
	private static int seriesNumber;
	
	// Get grid size in sq m (10000 sq cm in 1 sq m)
	private static int height;     
	private static int width;      
	private static int gridSizeSqM;
	private static int numBenthicTypes;  
	
	private static boolean variableRecruitmentTF;

	private static List < Integer > numbersOfRecruitsScaled;
	
	StepRecruiter()
	{
		init();
	}
	
	static void init()
	{
        seriesNumber          = StepLooper.getSeriesNumber();
		width                 = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "Width"  );
		height = width;
		gridSizeSqM           = height * width / 10000;
		numBenthicTypes       = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "NumBenthicTypes"  );
		variableRecruitmentTF = StaticBenthosData.getRunParamsTable().getBoolean( seriesNumber, "VarRecruitmentTF" );
		numbersOfRecruitsScaled = new ArrayList < Integer > ();
	}

	@Override
	public void step(SimState state) 
	{

		if ( !PanelDamageButtonsAndInfoText.isOkToGo() ) return;

		//System.out.println( "Now doing StepRecruiter");
		
		// Use this code and method near bottom for variable recruitment later on if selected
        // based on original vs. current surface area.
		if ( ( state.schedule.getSteps() > 12 ) && variableRecruitmentTF ) reviseRecruitment( recruitsPerSqM );
      
		// Doing this each (annual) step makes possible a change if needed for density dependent recruitment
		for( Double recruit : recruitsPerSqM )
		{
			int scaledRecruitment = (int) Math.ceil( recruit * gridSizeSqM );
			numbersOfRecruitsScaled.add( scaledRecruitment );
		}
	
		// Counter of species number -- starts on 0
		int speciesId = 0; 
		
		for(int i = 0; i < numBenthicTypes; i++ )
		{
			final MersenneTwisterFast random = state.random;
			
			int numCoralsNow = StaticCoral .getNumIndividualsCombinedNow();
			int maxCorals    = StaticCoral .getMaxIndividualsAllTypesCombined();
			int fixedNumSpp  = StaticCoral .getFixedNumBenthicTypes();
			
			int numRecruits  = numbersOfRecruitsScaled.get( i );
			
			int tryNumTotal  = numCoralsNow + numRecruits;

			if (tryNumTotal > maxCorals)
			{
                UtilJM.warner( "Too many corals: " + tryNumTotal + " is more than max allowed: " + maxCorals );
				return;
			}
			
			for(int j = 0; j < (int) numRecruits; j++)
			{
				Int2D locTry = new Int2D(0,0);
			
				// Loop to find empty space
				for(int k = 0; k < 10; k++) // Try only a 10 times then give up (previously set at 1,000)
				{
					int xCenter = random.nextInt( width  );  //+1?
					int yCenter = random.nextInt( height );  //+1?
					
					locTry = new Int2D( xCenter, yCenter ); 
					
					int trySppID = StaticCoralCell.getSpeciesCellGrid().get( xCenter, yCenter );
					
					// If empty space found, then place new coral and exit loop
					if ( ( trySppID < 1 ) || ( trySppID  > fixedNumSpp ) )  // either 0 or a special damage code (spp number + 2)
					{
						// Calling "static StepCoral newCoral ( SimState state, Int2D centerLoc, int speciesId, int radius, List < SubStepCoralCell > cells )"
						StaticCoral.newCoral( state, locTry, speciesId, 1, null ); //All new corals now start with radius = 1. Null is no cells.
						// There is no problem here if the 'coral' is null. The loop just keeps going.
						break;
						
					} // End if empty space
				} // End loop to find empty space
			}  // End loop by new recruit
			
			// Increment species number
			speciesId++; 
			
		} //End loop by benthic type
	} // End step()
	
	/**
	 * Called only if variable recruitment selected. 
	 * Returns corrected list by species of recruits per sq. m 
	 * based on ratio of original to current surface area for a species.
	 * This still needs to be adjusted for grid size. 
	 *  
	 * @param numbersOfRecruitsScaled
	 * @return
	 */
	static List < Double > reviseRecruitment( List < Double > recruitsPerSqM )
	{
		List < Double > adjustedRecruitsPerSqM = new ArrayList < Double > ();
		List < Double > origSurfaces    = StepCalculateAndStore.getOrigResults().getAllValuesForField( EnumOutputFields.SURFACE );
		List < Double > currentSurfaces = StepCalculateAndStore.getSpeciesSurfaces();	
		
		//System.out.println( origSurfaces );
		//System.out.println( currentSurfaces );
		
		for ( int i = 0; i < recruitsPerSqM.size(); i++ )
		{
			double surfaceRatio = 1; 
			if (! ( currentSurfaces.get( i ) * origSurfaces.get(i) == 0 ) ) // Problem here -- be careful about default setting here
				surfaceRatio = currentSurfaces.get( i ) / origSurfaces.get(i);
			
			adjustedRecruitsPerSqM.add( recruitsPerSqM.get(i) * surfaceRatio );
		}
		return adjustedRecruitsPerSqM;
	}

	static List<Integer> getNumbersOfRecruitsScaled() {
		return numbersOfRecruitsScaled;
	}

}// End class
