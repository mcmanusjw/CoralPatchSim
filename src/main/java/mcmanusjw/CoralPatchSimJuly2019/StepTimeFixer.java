package mcmanusjw.CoralPatchSimJuly2019;

import sim.engine.SimState;
import sim.engine.Steppable;

@SuppressWarnings("serial")
class StepTimeFixer implements Steppable 
{
	@Override
	public void step( SimState state ) 
	{
		// Because DigiReef steps corals irregularly depending on which
		// month they are chosen to grow, 
		// we add this dummy class to the scheduler 
		// to be something that steps every month
		// and this makes number of steps = time for simplicity.
		
		// Note that StepLooper determines when to stop the simulation.

	}

}
