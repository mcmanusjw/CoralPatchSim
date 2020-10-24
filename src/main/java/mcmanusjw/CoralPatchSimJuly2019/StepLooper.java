package mcmanusjw.CoralPatchSimJuly2019;

import java.util.Set;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Not currently doing second loop properly -- stopping on year 1 repeatedly <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 * Need to adjust what is re-initialized in calculate and store, and to erase data in the live graphs
 * 
 * Not stopping after 2 repetitions, and still repeating Year 1 over and over 
 * 
 * Called each year, but returns immediately unless this is the end of a replicate.
 * If so, it either resets appropriate parameters or ends everything.
 * If we scheduled it at the end of all replicates and series, we would be unable to change 
 * the number of year for a new series.
 * 
 * @author John McManus
 *
 */

@SuppressWarnings("serial")
class StepLooper implements Steppable 
{
	private static int replicateNumber; // starts by incrementing to 1 below
	private static int numberOfReplicates;
	private static int seriesNumber;    // starts by incrementing to 1 below
	private static int numberOfSeries;
	private static int year;

	private static boolean doBleachAll       = false;
	private static boolean doBleach75Percent = false;
	private static boolean doWaves           = false;
	private static boolean doBoat            = false;
	
	StepLooper()
	{
		init();
	}
	
	static void init()
	{
		ConsoleBig.clearAllValuePairs(); // This also sets year x-axis numbering.
	}
	
	@Override
	public void step( SimState state ) 
	{
		int yearsInThisSeries = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "YearsToEnd" );
     	year++;
     	
     	String text = "\n Doing Series Number   \t: " + ( seriesNumber    + 1 ) 
     				+ "\n Doing Replicate Number\t: " + ( replicateNumber + 1 )
     				+ "\n Doing Year Number     \t: " + year;
     	PanelDamageButtonsAndInfoText.setDisplayText( text );
     	
		if ( doBleachAll )
		{
			PanelDamageButtonsAndInfoText.buttonBleachAll();
			doBleachAll = false;
		}
		
		if ( doBleach75Percent )
		{
			PanelDamageButtonsAndInfoText.buttonBleach75Percent();
			doBleach75Percent = false;
		}
     	
		if ( doWaves )
		{
			PanelDamageButtonsAndInfoText.buttonWaves( state );
			doWaves = false;
		}
		
		if ( doBoat )
		{
			PanelDamageButtonsAndInfoText.buttonBoat( state );
			doBoat = false;
		}

     	if ( year <= yearsInThisSeries ) return;

     	// If not, this is the start of a new replicate or the end overall
     	year = 0;
     	replicateNumber++;
     	
		numberOfReplicates = (int) StaticBenthosData.getRunParamsTable().getDouble( 0, "NumberOfReplicates" );
		numberOfSeries     = (int) StaticBenthosData.getRunParamsTable().getDouble( 0, "NumberOfSeries"     );
     	
		// If new series starting (second and onwards -- the first does not go through looper)
		if ( replicateNumber >= numberOfReplicates ) // 1 replicate is listed as 1
		{
			replicateNumber = 0;
			seriesNumber++;

			// This ends the full simulation
			if ( seriesNumber >= numberOfSeries )
			{
				end( state );
				return;
			}
			
			// New series, so initialize with new output file
			StepCalculateAndStore.init();
		} 
		else // Just a new replicate in the current seriesNumber
		{
			// Initialize with no new output file
			StepCalculateAndStore.replicateInit(); 
		}
		
		// These are reset for each replicate, including for each new series
		Set < StepCoral > corals = StaticCoral.getCoralByTypeTable().getAllCorals();
		for( StepCoral coral : corals )
		{
			// This may be overkill, as we will re-do all collections except the schedule,
			// and perhaps just need to remove corals from schedule via Stoppables.
			// However a brief pause between repetitions does not slow everything much,
			// and this adds certainty to the kill and future flexibility on what collections to re-do.
			StaticCoral.killNotFromCellKill( coral ); 
		}
		
		ConsoleBig.clearAllValuePairs();
		StaticCoral.init();
		StaticCoralCell.replicateInit(); // This clears and keeps the speciesGraph attached to the UI
		StepCalculateNeighborRefuge.init();
		StepRecruiter.init();
		StepEllipticalDamageStepper.init(); // This also initializes StaticEllipticalDamager
		
		if ( StaticBenthosData.getRunParamsTable().getBoolean( 0, "IncludeEllipticalDamageTF" ) )
		{
			StepEllipticalDamageStepper.init();
		}
		

	}
	
	public static void end( SimState state ) 
	{
		PanelDamageButtonsAndInfoText.setDisplayText( "\n\n End of Simulations " );
		state.schedule.clear(); // ends simulation run
	}
	
	// Getters start here:
	static int getReplicateNumber() {
		return replicateNumber;
	}

	static int getSeriesNumber() 
	{
		return seriesNumber;
	}

	static int getYear() {
		return year;
	}
	
	public static void setDoBleachAll(boolean doBleachAll) {
		StepLooper.doBleachAll = doBleachAll;
	}

	public static void setDoBleach75Percent(boolean doBleach75Percent) {
		StepLooper.doBleach75Percent = doBleach75Percent;
	}

	public static void setDoWaves(boolean doWaves) {
		StepLooper.doWaves = doWaves;
	}

	public static void setDoBoat(boolean doBoat) {
		StepLooper.doBoat = doBoat;
	}
}
