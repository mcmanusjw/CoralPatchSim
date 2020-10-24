package mcmanusjw.CoralPatchSimJuly2019;

import sim.engine.Steppable;
import ec.util.MersenneTwisterFast;
import sim.engine.SimState;


/**
 * >>>>To Do:  Check if we need to use the approach of broad damage (draw 9-1, 
 * and use ceil to next tenth for the percent to damage) <<<<<<<<<<<<<<<<<<<<<<<<<<
 * >>>>To Do:  Check the adjust to depth option output<<<<<<<<<<<<<<<<<<<<<<<<<<<
 * 
 * Steps the production of elliptical damage with random width, height and direction
 * 
 * Should switch to a builder for this?
 */

@SuppressWarnings("serial")
class StepEllipticalDamageStepper implements Steppable 
{
	// Get MapRunParams
	private static TableRunParamsListEnumMapsString runParamsTable;
	
	//Get grid dimensions
	private static int height;
	private static int width;

	private static int simulationYear;
	private static int actionYear;
	private static int seriesNumber;
	private static int previousSeriesNumber;
	
	@SuppressWarnings("unused")
	private SimState state;
	
	// Variables needed for StepEllipticalDamageStepper
	private static int     siteDepth        ;
	private static boolean adjustForDepth   ;
	private static int     lowerYear        ;       
	private static int     upperYear        ;
	private static boolean ellipticalDamageOn;
	private static int     ellipseLengthMin ; 
	private static int     ellipseLengthMax ; 
	private static int     ellipseWidthMin  ; 
	private static int     ellipseWidthMax  ;  
	private static int     angleDegreesMin  ; 
	private static int     angleDegreesMax  ;  
	private static double  lowerPercent     ;  
	private static double  upperPercent     ;     
	private static double  lowerWaveStrength;
	private static double  upperWaveStrength;
	private static boolean center           ;
	private static boolean singleEllipse    ;

	// Constructor
	StepEllipticalDamageStepper( SimState state )
	{
		this.state = state;
		init();
	}

	static void init()
	{
		StaticEllipticalDamager.init();
		
		runParamsTable = StaticBenthosData.getRunParamsTable(); 
		seriesNumber = StepLooper.getSeriesNumber(); // gets for the current series upon initialization 
		ellipticalDamageOn = runParamsTable.getBoolean( seriesNumber, "IncludeEllipticalDamageTF" );
		if( !ellipticalDamageOn ) return; // This is here so that a new series can turn it on or off

		width             = (int) runParamsTable.getDouble ( seriesNumber, "Width"                  );
	    siteDepth         = (int) runParamsTable.getDouble ( seriesNumber, "SiteDepth"              );
	    adjustForDepth    =       runParamsTable.getBoolean( seriesNumber, "AdjustImpactForDepthTF" ); 
	    lowerYear         = (int) runParamsTable.getDouble ( seriesNumber, "LowerYearEllipse"       );        
	    upperYear         = (int) runParamsTable.getDouble ( seriesNumber, "UpperYearEllipse"       );      
	    ellipseLengthMin  = (int) runParamsTable.getDouble ( seriesNumber, "EllipseLengthMin"       ); 
	    ellipseLengthMax  = (int) runParamsTable.getDouble ( seriesNumber, "EllipseLengthMax"       ); 
	    ellipseWidthMin   = (int) runParamsTable.getDouble ( seriesNumber, "EllipseWidthMin"        ); 
	    ellipseWidthMax   = (int) runParamsTable.getDouble ( seriesNumber, "EllipseWidthMax"        );  
	    angleDegreesMin   = (int) runParamsTable.getDouble ( seriesNumber, "AngleDegreesMin"        ); 
	    angleDegreesMax   = (int) runParamsTable.getDouble ( seriesNumber, "AngleDegreesMax"        );  
	    lowerPercent      =       runParamsTable.getDouble ( seriesNumber, "LowerPercentEllipse"    );  
	    upperPercent      =       runParamsTable.getDouble ( seriesNumber, "UpperPercentEllipse"    );     
	    lowerWaveStrength =       runParamsTable.getDouble ( seriesNumber, "LowerWaveStrength"      );
	    upperWaveStrength =       runParamsTable.getDouble ( seriesNumber, "UpperWaveStrength"      );
	    center            =       runParamsTable.getBoolean( seriesNumber, "CenterEllipseTF"        ); 
	    singleEllipse     =       runParamsTable.getBoolean( seriesNumber, "SingleEllipseTF"        );
	    
	    height               = width;  
	 	simulationYear       = 0;
		actionYear           = 0;
		seriesNumber         = 0;
		previousSeriesNumber = 0;

	 }
	
    /**
     *  This will always be stepped but will just return if no ellipses needed in a series.
     *  Removing it from the schedule for some and not all series would be tricky, so we
     *  just leave it in with a short-circuit if ellipses not needed for a series. 
     */
	public void step(SimState state)
	{
    	if( ( !ellipticalDamageOn ) || ( StepLooper.getYear() < 1 ) ) return; // This is also here so that a new series can turn it on or off

		if ( !PanelDamageButtonsAndInfoText.isOkToGo() ) return;

    	boolean complete = false;
    	
		MersenneTwisterFast random = state.random; // This is so we do not have to pass a state in to the damager (facilitates the button option)
		
    	// If first time through for a series, calculate first action year
    	if ( seriesNumber != previousSeriesNumber )
    	{
    		actionYear = state.random.nextInt( ( upperYear - lowerYear ) + 1 ) + lowerYear; 
    		previousSeriesNumber = seriesNumber;
    	}
    	
    	simulationYear++;
    	
    	if ( simulationYear < actionYear ) return;
    	
    	//System.out.println( "This is a storm year: now doing StepEllipticalDamageStepper");
    	
    	// Choose next action year, ellipse length and width, and percent to damage
    	// Note that a bounded random integer = rand.nextInt((max - min) + 1) + min;
    	actionYear = simulationYear + random.nextInt( ( upperYear - lowerYear ) + 1 ) + lowerYear; 

		// A bounded random = random() * ( max - min )  + min
		// ( true, true ) makes it inclusive of 0d and 1d. If it is 0d, the value becomes = lowerPercent. 
		double percentToDamage = random.nextDouble( true, true ) * ( upperPercent - lowerPercent ) + lowerPercent;
		
        int cellsToDamage = (int) Math.round( percentToDamage * width * height );
        
        // Depth correction for waves: (see spreadsheet for this)
        // Depth probability:   EXP( -siteDepth * 0.1 ) +      MAX( (  ( 10 - siteDepth ) * 0.01 ), 0 ) 
        double depthProb = Math.exp( -siteDepth * 0.1 ) + Math.max( (  ( 10 - siteDepth ) * 0.01 ), 0 );
        if ( !adjustForDepth ) depthProb = 1.0; // If not correcting for depth, the probability correction becomes 1.0 (no effect) 
        
    	for ( int i = 0; i < 100; i++ ) // this limits run in case not all specified cells will be hit for a long time 
    	{
    		int xCenter = 0;
		    int yCenter = 0; 
    		
    		if( center )
      	    {
        		xCenter = ( int ) Math.abs( Math.round( width  / 2 ) );
    		    yCenter = ( int ) Math.abs( Math.round( height / 2 ) );
      	    }
      	    else
      	    {
      	   		xCenter = random.nextInt( width  + 1 );
    		    yCenter = random.nextInt( height + 1 );
      	    }

    		int ellipseLength = random.nextInt( ( ellipseLengthMax - ellipseLengthMin ) + 1 ) + ellipseLengthMin;
    		int ellipseWidth  = random.nextInt( ( ellipseWidthMax  - ellipseWidthMin  ) + 1 ) + ellipseWidthMin;
    		int angleDegrees  = random.nextInt( ( angleDegreesMax  - angleDegreesMin  ) + 1 ) + angleDegreesMin;

    		// double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    		double waveStrength = 
    				 lowerWaveStrength + ( ( upperWaveStrength - lowerWaveStrength ) * random.nextDouble() );
    		
    		
		    complete = StaticEllipticalDamager.damage( depthProb, xCenter, yCenter, 
		    		ellipseLength, ellipseWidth, angleDegrees, waveStrength, cellsToDamage, random ); 

		    if ( singleEllipse ) return;
		    if ( complete      ) break;
    	}
    	//UtilJM.warner( " Done with StepEllipticalDamageStepper ", true );
    	
    	StaticEllipticalDamager.clearCellCoordsDamaged();
    	System.gc(); // Suggest cleaning up objects after all that destruction
	}

    // Setters for alteration during cycling follow:

	static void setLowerYear(int lowerYear) {
		StepEllipticalDamageStepper.lowerYear = lowerYear;
	}

	static void setUpperYear(int upperYear) {
		StepEllipticalDamageStepper.upperYear = upperYear;
	}

	static void setEllipseLengthMin(int ellipseLengthMin) {
		StepEllipticalDamageStepper.ellipseLengthMin = ellipseLengthMin;
	}

	static void setEllipseLengthMax(int ellipseLengthMax) {
		StepEllipticalDamageStepper.ellipseLengthMax = ellipseLengthMax;
	}

	static void setEllipseWidthMin(int ellipseWidthMin) {
		StepEllipticalDamageStepper.ellipseWidthMin = ellipseWidthMin;
	}

	static void setEllipseWidthMax(int ellipseWidthMax) {
		StepEllipticalDamageStepper.ellipseWidthMax = ellipseWidthMax;
	}

	static void setAngleDegreesMin(int angleDegreesMin) {
		StepEllipticalDamageStepper.angleDegreesMin = angleDegreesMin;
	}

	static void setAngleDegreesMax(int angleDegreesMax) {
		StepEllipticalDamageStepper.angleDegreesMax = angleDegreesMax;
	}

	static void setLowerPercent(double lowerPercent) {
		StepEllipticalDamageStepper.lowerPercent = lowerPercent;
	}

	static void setUpperPercent(double upperPercent) {
		StepEllipticalDamageStepper.upperPercent = upperPercent;
	}

	static void setLowerWaveStrength(double lowerWaveStrength) {
		StepEllipticalDamageStepper.lowerWaveStrength = lowerWaveStrength;
	}

	static void setUpperWaveStrength(double upperWaveStrength) {
		StepEllipticalDamageStepper.upperWaveStrength = upperWaveStrength;
	}

	static void setCenter(boolean center) {
		StepEllipticalDamageStepper.center = center;
	}

	static void setSingleEllipse(boolean singleEllipse) {
		StepEllipticalDamageStepper.singleEllipse = singleEllipse;
	}

}
