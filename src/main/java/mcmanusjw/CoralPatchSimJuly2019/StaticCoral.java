package mcmanusjw.CoralPatchSimJuly2019;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sim.engine.SimState;
import sim.util.Double2D;
import sim.util.Int2D;

/**
 * 
 * Contains all static variables and methods 
 * applicable to corals. This keeps creation 
 * and killing plus all collection modifications
 * in a simple single method. 
 * 
 * @author John McManus
 *
 */

class StaticCoral 
{ 
	private final static int maxCoralsAllTypesCombined = StaticBenthosData.getMaxCoralsAllTypesCombined();

	private static int seriesNumber;
	private static int fixedNumBenthicTypes; 
	private static int numIndividualsCombinedEverCreated;
	private static int numIndividualsCombinedNow;

	private static Map < Integer, StepCoral > coralIdToCoralObject; 

	// Create a HashBasedtable so nulls are sorted away. This is speciesId, CoralId, StepCoral. Rows are species.
	private static TableListCoralSetNoNullNoDup coralByTypeTable; // = new TableListCoralSetNoNullNoDup( fixedNumBenthicTypes );
	
	private static long previousCellId ; // use for debugging
	
	StaticCoral()
	{
		init();
	}
	
	static void init()
	{
		seriesNumber = StepLooper.getSeriesNumber(); // gets for the current series upon initialization
		fixedNumBenthicTypes = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "NumBenthicTypes" );
		numIndividualsCombinedEverCreated = 0;
		numIndividualsCombinedNow 		  = 0;
		coralIdToCoralObject = new HashMap < Integer, StepCoral > (); 
		coralByTypeTable = new TableListCoralSetNoNullNoDup( fixedNumBenthicTypes );
		previousCellId = -1;
	}

	/**
	 * This method creates and schedules a new coral
	 * For brooders, a new centerLoc can be near the outer edge of the parent. 
	 * For broadcasters, the new center loc can be randomized based on the 
	 * dimensions of the sampler.
	 * 
	 * You can pass null in for cells, and it will make a new ArrayList for cells.
	 *  
	 * @param loc
	 */
	static int newCoral ( SimState state, Int2D centerLoc, int speciesId, int radius, Set < SubStepCoralCell > cells )
	{
		//System.out.println( "new Coral made ");
		
		if (numIndividualsCombinedNow + 1 > maxCoralsAllTypesCombined) 
		{
			UtilJM.warner( "Number of corals exceeds maximum: StaticCoral", true );
			return -1;
		}
		// Note that coralId starts on 0, as does speciesId (in StepRecruiter)
		int coralId   = numIndividualsCombinedEverCreated; // starts with 0
		int maxRadius = (int) StaticBenthosData.getSpeciesInputTable().getDouble( speciesId,   "MaxRadius" ); // This must not exceed half of the width or height of the grid.
		int width  =    (int) StaticBenthosData.getRunParamsTable()   .getDouble( seriesNumber, "Width"    );
		int height = width;
		
		if ( maxRadius > ( Math.min( height, width ) / 2d ) )
		{
			UtilJM.warner( "Stopping program: Species maximum radius exceeds half the shortest grid dimension: " 
						   + StaticBenthosData.getSpeciesInputTable(). getString( speciesId, "Name" ) );
			System.exit( 1 ); // This crashes the program (abnormal if > 0)
			return -1;
		}
		
		// If this is a recruit, it will need a new Set of cells.
		if ( ( cells == null ) || ( cells.size() < 1 ) ) cells = new HashSet < SubStepCoralCell > ();

		// Here is where we create a new coral using the stepped builder pattern in StepCoral
		StepCoral stepCoral = StepCoral.builder() 
  				
				// Variables put in here 
				.withCoralId          ( coralId   )
				.withSpeciesId        ( speciesId )

				// Computed final value 
				.withMaxRadius        ( maxRadius )
		
				// These can change over time
				.withRadius           ( radius    )
				.withCenterLoc        ( centerLoc )
				.withCells            ( cells     )
				
				// Final values from StaticBenthosData
				.withName             ( StaticBenthosData.getSpeciesInputTable(). getString( speciesId, "Name"              ) )
				.withGrowthForm       ( StaticBenthosData.getSpeciesInputTable(). getString( speciesId, "GrowthForm"        ) )
				.withMaxColumnDiameter( StaticBenthosData.getSpeciesInputTable(). getDouble( speciesId, "MaxColumnDiameter" ) )
				.withMaxColumnHeight  ( (int) StaticBenthosData.getSpeciesInputTable(). getDouble   ( speciesId, "MaxColumnHeight"   ) )
				.withOriginalAbundance( (int) StaticBenthosData.getSpeciesInputTable(). getDouble   ( speciesId, "OriginalAbundance" ) )
				.withBattleSkill      ( (int) StaticBenthosData.getSpeciesInputTable(). getDouble   ( speciesId, "BattleSkill"       ) )
				.withGrowthRate       ( (int) StaticBenthosData.getSpeciesInputTable(). getDouble   ( speciesId, "GrowthRate"        ) )
				.withRecruitmentRate  ( StepRecruiter  .getNumbersOfRecruitsScaled().get ( speciesId                      ) )
				.withMortalityRate    ( StaticBenthosData.getSpeciesInputTable(). getDouble( speciesId, "Mortality"         ) )
				
				// Now building the coral
				.build();
		
		// Build ends here
		
		//This makes the new coral and tells us if it could not be added because it is a duplicate or null
		boolean coralAdded = coralByTypeTable .add(speciesId, stepCoral);;
		if ( !coralAdded )
		{
			UtilJM.warner( "New coral could not be added to Set in StaticCoral.newCoral(....). Duplicate or null. Now crashing!!" );
			System.exit( 1 ); // Non-zero status means abnormal exit
		}
		
		coralIdToCoralObject.put( coralId, stepCoral );

		// This creates new cell and updates coralCell and speciesCell grids, 
		// but only if this is a new recruit:
		if ( cells.size() < 1 ) StaticCoralCell.newCoralCell( centerLoc, stepCoral );
		
	   	// Create monthly growth from annual growthRate by skipping months as needed
		// System.out.println( " growth rate: " + CoralPatchSimCycler.getGrowthRates().get( speciesId ) + " for species number: " + speciesId );
		int interval = ( int ) Math.round( 12d / StaticBenthosData.getSpeciesInputTable().getDouble( speciesId, "GrowthRate" ) ); 
		
		// This schedules this coral and sets a stopper so the coral can be stopped as it dies
		stepCoral.setStopThis( state.schedule.scheduleRepeating( stepCoral, 2, interval ) ); // Set to occur in order after the StepReaper
		
		numIndividualsCombinedEverCreated++; 	// starts with 1 from 0 initially -- this prevents duplicate coralIDs
		numIndividualsCombinedNow++;			// starts with 1 from 0 initially -- only used to prevent too many corals at one time.
		
    	return coralId;
	}
	
	static void killFromCellKill( StepCoral stepCoral )
	{
		stepCoral.getStopThis().stop();
		
		// These remove the coral no matter where it is, including if it is null (?)
		coralIdToCoralObject.values().remove( stepCoral );
		coralByTypeTable.remove( stepCoral.getSpeciesId(), stepCoral );
		numIndividualsCombinedNow--;

		// This removes any cells linked to null corals in the grid and warns that one or more was found
		// It also removes all null corals from the HashMap if present.
		if ( StaticCoralCell.clearCoralCellGridOfCellsWithNullCorals() )
		{
			//System.out.println( "Links to null corals found and cleared in StaticCoral.killFromCellKill(...)" );
		}
	}
	
	static void killNotFromCellKill( StepCoral stepCoral )
	{
		boolean killedCell = false; 
		
		if ( stepCoral != null )
		{
			// Remove all extant cells. Must do this to clear all spaces of those cells 
			// off the grids instead of just killing the coral with all cells at once.
			while( stepCoral.getCells().size() > 0 )
			{
				SubStepCoralCell cell = stepCoral.getCells().iterator().next(); // Do not remove the cell here, but rather in the kill routine next
				killedCell = StaticCoralCell.killFromCoralKill( cell, stepCoral ); 
				if ( !killedCell ) break; // could only mean it encountered a null coral
				// three lines of debugging code///////////////////////////<<<<<<<<<<<<<<<<<<
				long cellId = cell.getCoralCellId();
				if ( cellId == previousCellId ) UtilJM.warner( "StaticCoral -- Killing same cell as previous" );
				previousCellId = cellId;
			}
			stepCoral.getStopThis().stop();
		}
		
//		try
//		{
//			// These remove the coral no matter where it is, including if it is null (?)
//			coralIdToCoralObject.values().remove( stepCoral );
//		}
//		catch( java.util.ConcurrentModificationException e)
//		{
//			UtilJM.warner( "Pushing buttons too quickly.");
//		}
		
		coralByTypeTable.remove( stepCoral.getSpeciesId(), stepCoral );
		numIndividualsCombinedNow--;

		// This removes any cells linked to null corals in the grid and warns that one or more was found
		// It also removes all null corals from the HashMap if present.
		if ( StaticCoralCell.clearCoralCellGridOfCellsWithNullCorals() )
		{
			//System.out.println( "Links to null corals found and cleared in StaticCoral.killNotFromCellKill(...)");
		}
	}
	
	
	static int adjustRadius( Int2D centerLoc, int maxRadius, Set < SubStepCoralCell >  cells, StepCoral stepCoral ) 
	{
		// Gets the List of radius plus outlierLocs in Int2D form, and grabs the former as the x part of the first Int2D
		int newRadius = findRadiusAndOutliers( centerLoc, maxRadius, cells, stepCoral).newRadius;
		return newRadius;
	}
	
	static WrapperRadiusAndOutliers findRadiusAndOutliers( Int2D centerLoc, int maxRadius, Set < SubStepCoralCell >  cells, StepCoral stepCoral )
	{
		// Loop through to find greatest distance center to cell and use to calculate new radius
		// in case the netGrowth was because this was a broken, trapped coral healing itself.
		// If no growth, must reverse previous radius increment anyway.
		// Return list of any cell locations too far from the new max radius because of the shift in the center
		// to a new shape, such as from a'moon' to a 'crescent moon', making a longer 'radius'.
		Set < Int2D > outlierLocs = new HashSet < Int2D > (); 
		int maxAllowedRadiusSqrd = maxRadius * maxRadius;
		double maxFoundSqrdDistance = 0;

		for ( SubStepCoralCell cell : cells )
		{
			Int2D tempLoc = cell.getLoc();
			Double2D tempDouble   = new Double2D( ( double ) tempLoc.x,   ( double ) tempLoc.y   );
			Double2D centerDouble = new Double2D( ( double ) centerLoc.x, ( double ) centerLoc.y );
			double distSquared = StaticCoralCell.getContinuousSpace().tds(centerDouble, tempDouble);
			
			if ( distSquared > maxAllowedRadiusSqrd ) 
			{
				outlierLocs.add( tempLoc );
			}
			if ( distSquared > maxFoundSqrdDistance ) maxFoundSqrdDistance = distSquared;
		}
		int newRadius = ( int ) Math.ceil( Math.sqrt( maxFoundSqrdDistance ) ); 
		if ( newRadius < 1 ) newRadius = 1; // In case there is only one cell, the maxSqrdDistance will be zero.
		

		return new WrapperRadiusAndOutliers ( newRadius, outlierLocs );
	}
	
	// ********************* Start Getters and Setters **********************

	static int getMaxIndividualsAllTypesCombined() {
		return maxCoralsAllTypesCombined;
	}

	static int getFixedNumBenthicTypes() {
		return fixedNumBenthicTypes;
	}

	static int getNumIndividualsCombinedEverCreated() {
		return numIndividualsCombinedEverCreated;
	}

	static void setNumIndividualsCombinedEverCreated(int numIndividualsCombinedEverCreated) {
		StaticCoral.numIndividualsCombinedEverCreated = numIndividualsCombinedEverCreated;
	}

	static int getNumIndividualsCombinedNow() {
		return numIndividualsCombinedNow;
	}

	static void setNumIndividualsCombinedNow(int numIndividualsCombinedNow) {
		StaticCoral.numIndividualsCombinedNow = numIndividualsCombinedNow;
	}

	static Map<Integer, StepCoral> getCoralIdToCoralObject() {
		return coralIdToCoralObject;
	}

	static TableListCoralSetNoNullNoDup getCoralByTypeTable() {
		return coralByTypeTable;
	}
}
