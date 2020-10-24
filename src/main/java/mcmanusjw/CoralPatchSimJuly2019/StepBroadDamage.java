package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sim.engine.SimState;
import sim.engine.Steppable;

@SuppressWarnings("serial")
class StepBroadDamage implements Steppable 
{
	// Get MapRunParams
	private static TableRunParamsListEnumMapsString runParamsTable;
	
	//Get grid dimensions
	@SuppressWarnings("unused")
	private static int height;
	private static int width ;

	private static int simulationYear      ;
	private static int actionYear          ;
	private static int seriesNumber        ;
	private static int previousSeriesNumber;

	private static boolean broadDamageOn;
	private static int     lowerYear    ;       
	private static int     upperYear    ;      
	private static double  lowerStrength;
	private static double  upperStrength;
	
	// Constructor
	StepBroadDamage()
	{
		init();
	}

	static void init()
	{
		seriesNumber  = StepLooper.getSeriesNumber(); // gets for the current series upon initialization 
		runParamsTable = StaticBenthosData.getRunParamsTable();
		if ( seriesNumber < 1 )broadDamageOn = runParamsTable.getBoolean( seriesNumber, "IncludeBroadDamageTF" ); // IncludeBroadDamageTF
    
		if( !broadDamageOn ) return; // This is here so that a new series can turn it on or off

		runParamsTable =       StaticBenthosData.getRunParamsTable(); 
		width          = (int) runParamsTable.getDouble ( seriesNumber, "Width"              );
		lowerYear      = (int) runParamsTable.getDouble ( seriesNumber, "LowerYearEllipse"   );        
		upperYear      = (int) runParamsTable.getDouble ( seriesNumber, "UpperYearEllipse"   );      
	    lowerStrength  =       runParamsTable.getDouble ( seriesNumber, "LowerBroadStrength" );
	    upperStrength  =       runParamsTable.getDouble ( seriesNumber, "UpperBroadStrength" );
	    
	    height               = width;  
	 	simulationYear       = 0;
		actionYear           = 0;
		seriesNumber         = 0;
		previousSeriesNumber = 0;
	 }
	
    /**
     *  This will always be stepped but will just return if no broad damage needed in a series.
     *  Removing it from the schedule for some and not all series would be tricky, so we
     *  just leave it in with a short-circuit if broad damage is not needed for a series. 
     */
	@Override
	public void step(SimState state) 
	{
		if ( !PanelDamageButtonsAndInfoText.isOkToGo() ) return;
		
		int seriesNumber = StepLooper.getSeriesNumber();

		// Choose year of simulation
    	if( ( !broadDamageOn ) || ( StepLooper.getYear() < 1 ) ) return; // This is also here so that a new series can turn it on or off
    	
     	// If first time through for a series, calculate first action year
    	if ( seriesNumber != previousSeriesNumber )
    	{
    		actionYear = state.random.nextInt( ( upperYear - lowerYear ) + 1 ) + lowerYear; 
    		previousSeriesNumber = seriesNumber;
    	}
    	
    	simulationYear++;
    	
    	if ( simulationYear < actionYear ) return;
    	
    	// Choose next action year, and damage strength
    	// Note that a bounded random integer = rand.nextInt((max - min) + 1) + min;
    	actionYear = simulationYear + state.random.nextInt( ( upperYear - lowerYear ) + 1 ) + lowerYear; 

		// ( true, true ) makes it inclusive of 0d and 1d. If it is 0d, the value becomes = lowerPercent. 
		double strength = state.random.nextDouble( false, true ) * ( upperStrength - lowerStrength ) + lowerStrength;
		
		// Round up t0 next higher tenth
		strength = ( Math.ceil( strength * 10 ) ) / 10;

		killCorals( strength );
	}
	/**
	 * Get all corals and kill those whose (1 - vulnerability) is more than the computed probability of death
	 * for that coral.
	 * 
	 * Try making random draw [0.9 to 1.0]?
	 * For example, if vulnerability is 0.8, percentToDamage is 0.5 and the random draw for that coral is 0.9,
	 * then ( 1 - 0.8 ) = 0.2, but the probability of death is 0.5 * 0.9 = 0.45, so the coral dies. 
	 *      However if vulnerability is 0.4, percentToDamage is 0.5 and the random draw for that coral is 0.9,
	 * then ( 1 - 0.4 ) = 0.4, but the probability of death is 0.5 * 0.6 = 0.45, so the coral lives.
	 * 
	 * If percent to damage is 1.0 (100%), if vulnerability is 0.8, percentToDamage is 1.0 and the random draw 
	 * for that coral is 0.9, then ( 1 - 0.8 ) = 0.2, but the probability of death is 1.0 * 0.9 = 0.9, so the coral dies. 
	 * All corals would die (1 - 0.1 = 0.9 ).
	 * 
	 * If vulnerability is 1.0, percentToDamage is 0.5 and the random draw for that coral is 0.9,
	 * then ( 1 - 1 ) = 0, but the probability of death is 0.5 * 0.9 = 0.45, so the coral dies.    
	 * 
	 * We choose the corals by rows (=species) to we only need to get the vulnerability for that species
	 * one time.
	 *  
	 * @param state
	 * @param intensity
	 */
	
	static void killCorals( double strength )
	{
		double vulnerability = 0; 
		TableListCoralSetNoNullNoDup coralTable = StaticCoral.getCoralByTypeTable();
		int numSpecies = coralTable.numSets();
		
		// Each row is one species
		for( int i = 0; i < numSpecies; i++ )
		{
			Set  < StepCoral > set      = coralTable.row( i );
			List < StepCoral > killList = new ArrayList < StepCoral >( set.size() ); // mark for death, then kill later, so no concurrent modification error 

			boolean firstCoralInSet = true;
			for( StepCoral coral: set )
			{
				if ( firstCoralInSet ) // This reduces number of times we check for vulnerability for that species
				{
					int speciesId = coral.getSpeciesId();
					vulnerability = StaticBenthosData.getSpeciesInputTable().getDouble( speciesId, "BroadVulnerability" );
					firstCoralInSet = false;
				}
				
				// Get a probability of death. Note that (true, true) means include 0.0 and 1.0 among possibilities
				// double draw = state.random.nextDouble( false, true )  * ( 1.0 - 0.9 ) + 0.9;
				// double prob = draw 	* percentToDamage;
				
				/// Making this deterministic for simplicity
				double survivability = 1.0 - vulnerability;
				
				//System.out.println( "\n strength: " + strength + " survivability: " + survivability );
				
				if ( strength > survivability )  //was: if ( prob < ( 1 - vulnerability ) )
				{
					killList.add( coral );
				}
			}
			// Now kill, so not looping as you do it (coral is not removed from here until another loop started)
			// This removes by one species at a time, being inside the species loop.
			// Stop the schedule until this is complete

			int numCorals = killList.size();

			for (int j = 0; j < numCorals; j++) 
			{
				StaticCoral.killNotFromCellKill( killList.get( j ) );
			}
		}
	}
 
}
