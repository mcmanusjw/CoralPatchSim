package mcmanusjw.CoralPatchSimJuly2019;

import sim.engine.SimState;

/**
 * 
 * ToDo: Put in a new option to get species dominance data via an upper half-matrix
 *       instead of just by ranking. 
 *  
 * @author John McManus
 *
 */

@SuppressWarnings("serial")
class CoralPatchSim extends SimState 
{
	// Calculated constant that does not need resetting in each cycle
	// private static final int STEPS_TO_END 	= StaticBenthosData.getStepsToEnd();
	
	// Last place in ordering of things to step
	private static final int FINALPOSITION  = Integer.MAX_VALUE;
	
	//private Schedule schedule = this.schedule;
	
    public CoralPatchSim( long seed )
    {
    	super(seed);
    	StaticBenthosData.init();
    	init();
    }
    
    void init()  
    {
		StaticCoral            .init();
		StaticCoralCell        .init();
		//UtilOriginalDataPrepper.init(); // New 18 Dec 2019 due to weird ExceptionInInitializerError from that class (due to null pointer exception)

		// Just to initialize these temporarily
		// StaticBenthosData.getRunParamsTable().put( 0, "1.0", "NumberOfReplicates" );
		// StaticBenthosData.getRunParamsTable().put( 0, "1.0", "NumberOfSeries"     );
		
		// Note: all Steppables are initialized via their constructors
	}
    
    public void start()
    {
    	super.start();
    	
    	System.out.println( "<<<<<<<<<<<<<<<<<< started >>>>>>>>>>>>>>>>>");
    	
      	// Schedule a dummy class to step every (monthly) time increment 
    	// because new corals are actually scheduled irregularly in StaticCoral.
    	// This makes steps = time for simplicity.
    	StepTimeFixer timeStepper = new StepTimeFixer();
    	schedule.scheduleRepeating( timeStepper, 2 ); // The 2 means it starts at time 1, not 0
       	
    	// Instantiate classes needing scheduling (StaticEllipticalDamager is handled further down).
    	StepReaper 			        stepReaper 		     = new StepReaper();
    	StepBroadDamage             bDamager             = new StepBroadDamage(); 
  		StepEllipticalDamageStepper eDamager             = new StepEllipticalDamageStepper( this );
       	StepRecenterAndSplit        stepRecenterAndSplit = new StepRecenterAndSplit();
       	StepCalculateNeighborRefuge neighborCalcs        = new StepCalculateNeighborRefuge();
       	StepCalculateAndStore       calcStore            = new StepCalculateAndStore();  // Do this last except final year
       	StepRecruiter 		        stepRecruiter 	     = new StepRecruiter();
       	StepLooper                  stepLooper           = new StepLooper();             // At end, this ends all simulations   
       	
       	// Maybe put the makeCorals option here
       	stepRecruiter.step( this ); ////// Trial only -- maybe should skip first recruitment below?????
       	if( !ConsoleBig.sdlSet.isEmpty() ) StaticMakeAndGrowPlantedCorals.makeCorals( this, ConsoleBig.sdlSet );
       	
       	// We schedule the ordering of reaper to be first each month
       	// so there will be less corals around to step. Thus all corals
       	// are scheduled to be ordered at int = 2 and they will go in 
       	// shuffled order.
       	schedule.scheduleRepeating( stepReaper, 1, 1.0 );  //<<<<<<<<<<<<<<<<<<<< put in a switch to run on and off
       	
       	// Scheduling annually (time step is 1 month) in order of occurrence, after stepping all corals.
       	// Note that when there is elliptical damage this happens before these and is scheduled further down.
       	// Making damage early reduces time on others, such as recenterAndSplit, as well as StepReaper and coral growth. 

       	schedule.scheduleRepeating( bDamager,             FINALPOSITION - 6, 12.0 );
       	schedule.scheduleRepeating( eDamager,             FINALPOSITION - 5, 12.0 );
       	schedule.scheduleRepeating( stepRecenterAndSplit, FINALPOSITION - 4, 12.0 ); 
   		schedule.scheduleRepeating( neighborCalcs,        FINALPOSITION - 3, 12.0 ); // maybe just link this to calcStore and not by itself
   		schedule.scheduleRepeating( calcStore,            FINALPOSITION - 2, 12.0 );
   		
   	    // This comes last so that numbers of recruits can be adjusted for surface if needed.
   		// That means there will be recruits right after the storms if storms turned on.
      	// schedule.scheduleRepeating( stepRecruiter,       FINALPOSITION - 1, 12.0 );
    	// delayed pattern is: scheduleRepeating(double time, int ordering, Steppable event, double interval)
   		schedule.scheduleRepeating( 12.0, FINALPOSITION - 1, stepRecruiter, 12.0 );
      	
      	// This checks each year if a replicate or new series is starting - or if we can end the whole set of series.
      	schedule.scheduleRepeating( stepLooper,           FINALPOSITION,     12.0 ); 
    }
}  

/*
	Copyright 2018, John W. McManus

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Portions of code from the Mason Package are copyright under the 
Mason Open Source License 
http://cs.gmu.edu/~eclab/projects/mason/docs/license.html
*/

