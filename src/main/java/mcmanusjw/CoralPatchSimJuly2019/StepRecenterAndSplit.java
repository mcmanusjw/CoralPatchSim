package mcmanusjw.CoralPatchSimJuly2019;

import java.util.*;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.*;
/**
 * Checks each coral's list of cells to see if contiguous.
 * If not, it finds a valid new center cell (or near-center) 
 * from the original coral's contiguous cells. Then it searches 
 * remaining cells from the previous list, takes out a set
 * with which to create a new coral with a new center, then 
 * does this again until the original list of cells is 
 * exhausted.
 * 
 * The center is important because the circular growth of the
 * coral depends on the distance from a center. Otherwise the
 * coral tends to grow in squares. With the wrong center, growth
 * could be inhibited even when it should occur.
 * 
 * This could be stepped, or called from EllipseDamageStepper.
 * However, two dominant corals could possibly grow together
 * through a weaker one, breaking it in two. Thus we will try
 * stepping this annually.  
 * 
 * We will assume wrapped corals do not include both the cell at the center 
 * of the space and the edge cells. Thus, the max diameter of a coral should
 * always be < half of the smallest grid dimension.
 * 
 * Crescent Moon Problem   
 * Moving the full moon center to the center of a crescent will likely
 * increase the maximum distance (radius) to some portions at each of
 * the two tips. Other shapes may yield only one set of such outsiders. 
 * If the coral is near or at maximal radius, this may leave the 
 * outsiders beyond this distance. One could unrealistically trim 
 * them away, or in our more realistic case, return the outsiders to be
 * made into one or more new corals of the same species. 
 * 
 * @author John McManus
 *
 * Most of the code is from 2012, but integrated in Nov 2017 - May 2018
 * 
 * 
 */

@SuppressWarnings("serial")
class StepRecenterAndSplit implements Steppable
{
	@Override
	public void step( SimState state ) 
	{
		//System.out.println( "Now doing StepRecenterAndSplit");
		
		// Get current Set of all corals
		Set < StepCoral > stepCorals = Collections.unmodifiableSet( StaticCoral.getCoralByTypeTable().uniqueValues() );// was getCorals();

		// Initiates and clears temporary Set of corals to prevent Concurrent Modification
		Set< StepCoral > coralsTemp = new HashSet< StepCoral > ();

		// Make a shallow copy of corals 
		// (the Set is different but objects pointed to within it are the same).
		// Because corals Set will change, and we only pointed to it, 
		// we need to be running through an independent copy of it 
		coralsTemp.addAll( stepCorals );

		//Process all damaged corals in coralsTemp (corals which have lost one or more cells)
		for( StepCoral stepCoral : coralsTemp )
		{
			if( stepCoral.isDamaged() ) processCoral( stepCoral, state );
			stepCoral.setDamaged( false );
		}
	}

	/**
	 * Finds out if coral integrity is broken. If so, assigns new centers and radii
	 * to each piece, then creates new corals as needed.
	 * Works by tracing adjacent cells among those in the coralCells array in the coral.
	 */
	static void processCoral( StepCoral stepCoral, SimState state )
	{
		int maxRadius   = stepCoral.getMaxRadius();
		Int2D centerLoc = stepCoral.getCenterLoc();
		Set < SubStepCoralCell > cells           = stepCoral.getCells();
		Set < Int2D >     cellLocsToCheck = new HashSet  < Int2D >     ();
		
		// Put all existing cell locations into List cellLocsToCheck, 
		// but pass through a Set to ensure no duplicates
    	for( SubStepCoralCell cell: cells )
		{
			Int2D loc  = cell.getLoc();
			cellLocsToCheck.add( loc );
		}
		
		// Regardless of whether or not the center cell and number of cells are OK, there might have been losses
		// and gains, which could have altered the shape of the coral. So, process all corals similarly.
		// If centerLoc missing, stick with the first cell and start finding its connections, then redo center if missing, and radius. 
    	Int2D startLoc = new Int2D( 0, 0 );
    	if( cellLocsToCheck.iterator().hasNext() ) // new condition
		{
			startLoc = cellLocsToCheck.iterator().next();
		}
		else
		{
			return;
		}
    	if ( cellLocsToCheck.contains( centerLoc ) )
		{
			startLoc = centerLoc;
		}
		
		// The following involves collections passed by reference, so both will be altered.
		// cellLocsToCheck will be reduced by cell locations transferred to CellLocsOk
		Set < Int2D > cellLocsOk = findAdjacentCells( startLoc, cellLocsToCheck ); // On return, cellLocsOk must be at least = 1, which is the centerLoc
 
		// Find and set new center for original coral if missing, then update its list of cells
		if ( !cellLocsOk.contains( centerLoc ) )
		{
			centerLoc = newCenter( cellLocsOk, stepCoral.getCoralId() );
			stepCoral.setCenterLoc( centerLoc );
		}
		Set < SubStepCoralCell > cellsOkSet = findCellsFromLocs( cells, cellLocsOk );

		// This returns a new radius as well as a list of cells beyond max radius to go back
		// into the cellLocsToCheck to become new corals (even without any gaps with the starting coral).
		WrapperRadiusAndOutliers wrapperRadiusAndOutliers = StaticCoral.findRadiusAndOutliers( centerLoc, maxRadius, cellsOkSet, stepCoral );
		stepCoral.setRadius(  wrapperRadiusAndOutliers.newRadius );
		cellLocsOk.add(   centerLoc ); // Just to be sure the center is put in. Set prevents duplicates anyway. Should remove this.
		cellLocsToCheck.addAll( wrapperRadiusAndOutliers.outlierLocs );
		stepCoral.getCells().clear(); // Try removing this
		stepCoral.setCells( cellsOkSet );
			
		cellsOkSet = new HashSet < SubStepCoralCell > (); 
		cellLocsOk.clear();

		// If still more cells to process, try all cellLocsToCheck to find sets of adjacent cells, until all cells tried.
		// For each set of interlinked cells separated by a gap, find new center and make new coral.
		while ( cellLocsToCheck.size() > 0 ) // could be only 1 cell present
		{
			// Get any location
			startLoc = cellLocsToCheck.iterator().next();
			cellLocsOk.add( startLoc );
			cellLocsOk = findAdjacentCells( startLoc, cellLocsToCheck );
			centerLoc  = newCenter( cellLocsOk, stepCoral.getCoralId() );
			cellsOkSet = findCellsFromLocs( cells, cellLocsOk );

			// This returns a new radius as well as a list of cells beyond max radius to go back
			// into the cellLocsToCheck to become new corals (even without any gaps with the starting coral).
			// This may never happen in this loop, as the loop only deals with fragments.
			wrapperRadiusAndOutliers = StaticCoral.findRadiusAndOutliers( centerLoc, maxRadius, cellsOkSet, stepCoral );
			int newRadius = wrapperRadiusAndOutliers.newRadius;
			int speciesId = stepCoral.getSpeciesId();
			Integer coralId = StaticCoral.newCoral( state, centerLoc, speciesId, newRadius, cellsOkSet ); //All new coral now start with radius = 1 

			if ( coralId < 0 ) // newCoral returns a -1 for no new coral -- safer than returning an actual null coral
			{
				// No room for a new coral. Kill the rest of the cells in cellLocsToCheck
				UtilJM.warner("\nNew coral could not be added. From StepRecenterAndSplit");
				Set < SubStepCoralCell > cellsToKill = findCellsFromLocs( cells, cellLocsToCheck );
				for ( SubStepCoralCell cell : cellsToKill )
				{
					StaticCoralCell.killNotFromCoralKill( cell ); 
					//System.out.println( " SubStepCoralCell killed in StepRecenterAndSplit ");
				}
				cellLocsToCheck.clear();
				
				// This scours the whole grid to find and remove all cells linked to null corals (a bit slow) <<<< 
			//	StaticCoralCell.clearCoralCellGridOfCellsWithNullCorals();
				
				break;
			}

			StepCoral newCoral = StaticCoral.getCoralIdToCoralObject().get( coralId ); 
			for (SubStepCoralCell cell: newCoral.getCells() )
			{
				cell.setCoralId( coralId ); // Thus, coralId of a cell must be mutable in SubStepCoralCell, not final
			}
			
			if ( wrapperRadiusAndOutliers.outlierLocs.size() > 0 )
			{
				cellLocsToCheck.addAll( wrapperRadiusAndOutliers.outlierLocs );
				//System.out.println( "Adding back to looped cellsToCheck this many locations: " + wrapperRadiusAndOutliers.outlierLocs.size() );
			}
			
			cellsOkSet = new HashSet < SubStepCoralCell > ();		// This is still linked to the ones in the coral, so cannot clear it -- must destroy	    
		    cellLocsOk.clear();
		}
		wrapperRadiusAndOutliers = null; // Destroy this object to prevent memory leak due to the ArrayList
	}

	private static Set < SubStepCoralCell > findCellsFromLocs( Set<SubStepCoralCell> cells, Set<Int2D> cellLocsOk )	
	{
		Set < SubStepCoralCell > cellsOkList = new HashSet < SubStepCoralCell >();
		// For each OK location, find the corresponding coral cell and add that to the OK corals stack
		for( Int2D loc : cellLocsOk )
		{
			for( SubStepCoralCell cell : cells )
			{
				if ( loc.equals( cell.getLoc() ) )
				{
					cellsOkList.add( cell );
				}
			}
		}
		return cellsOkList;
	}
	
	private static Set <Int2D> findAdjacentCells (	Int2D  startLoc, 
													Set <Int2D> cellLocsToCheck )
	{
		/** This method starts with a focalCellLoc and passes this to a double loop.
		 *  If a neighbor is found, it saves the old focalCellLoc and 
		 *	makes the neighbor the new focalCellLoc (like sequencing). When no new neighbors
		 *	can be found (perhaps a cul-de-sac), a second double loop grabs a potential
		 * 	new focalCell. If this one is not a neighbor in the OK collection, it tries 
		 *	another new focalCellLoc until it finds a good one, then passes this back to the 
		 *	sequencing loops. All cells connected to the focal cell are returned in a Set (cellLoksOk)
		 *  and removed from the Stack of cells to check (cellsLocsToCheck).  
		 *
		 *	The loop set continues until:
		 *	1) no cell locations are left to check (cellLocsToCheck.size() == 0)
		 *	2) no suitable focalCellLocs (cells with neighbors associated with the input location)
		 *   can be found.
		 *   The method returns the HashSet of cells connected to the startLoc (cellLocsOk). 
		*/

		// Make independent shallow copy of cell locations to check 
		ArrayList < Int2D > allCells = new ArrayList < Int2D > ();
		allCells.addAll( cellLocsToCheck );
		Set < Int2D > cellLocsOk = new HashSet < Int2D > (); 
		
		if ( cellLocsToCheck.size() == 1 ) // Only one cell so no sequence checking needed
		{
			cellLocsOk.add( startLoc ); 
			cellLocsToCheck.remove( startLoc );
			return cellLocsOk;
		}

		// Find all cell locations adjacent to any good start location in the OK list
		Int2D goodLoc = startLoc;
		cellLocsToCheck.remove( goodLoc );
		cellLocsOk.add( goodLoc );
		
		Set < Int2D >  newOkLocs = new HashSet < Int2D > ();
		newOkLocs.add(  goodLoc );
		boolean noneLeft = false;
		
	    // This ends when no more to check or two loops and no adjacent pairs found
		while( cellLocsToCheck.size() > 0 )
		{
			noneLeft = true;
			for ( Int2D loc : cellLocsToCheck )
			{
				for ( Int2D okLoc : cellLocsOk )
				{
					if ( isAdjacent( loc, okLoc  )  )
					{
						goodLoc = loc;     // Setting up to find the next cell in a chain 
						noneLeft = false;  // Found a linked cell, so keep going until none found 
						break;
					}
				}
				cellLocsOk.add( goodLoc ); // Long term storage
				newOkLocs.add(  goodLoc );  // Short term just to remove from cellLocsToCheck for efficiency
			}
			cellLocsToCheck.removeAll( newOkLocs );
			newOkLocs.clear();
			if ( noneLeft ) break;     // Tried all combinations and no adjacent pairs found 
		}
		cellLocsToCheck.removeAll( cellLocsOk ); // should not need this but seemingly necessary to get one last coral out

		return cellLocsOk;
	}

	private static boolean isAdjacent( Int2D focalLoc, Int2D tryLoc ) 
	{
		Double2D focalLocDouble = new Double2D( (double) focalLoc.x, (double) focalLoc.y );
		Double2D tryLocDouble   = new Double2D( (double) tryLoc.x  , (double) tryLoc.y   );

		//Distance returned will be squared, so adjacent is 0, 1 or 2 units away: 
		// (1*1 + 1*1),  (1*1 + 0*0),  (0*0 + 1*1 ) = 2,  1,  1
		
		double distanceSqrd = StaticCoralCell.getContinuousSpace().tds( focalLocDouble, tryLocDouble );
		
		return ( distanceSqrd < 3.0 ); 
	   
	}
	
	private static Int2D newCenter( Set<Int2D> cellLocsSet, int coralId )
	{
		/**
		 * This method finds a new center at or near 
		 * center of gravity. If the center of gravity 
		 * is not a living part of the coral, the routine 
		 * will search for a nearby part of the coral.   
		 * 
		 *  A wrapped coral will have either or both x = 0 and x = xDim,
		 *  and/or y = 0 and y = yDim. If only the x is wrapped, adjust for 
		 *  x by putting low values back to their would-be big values and 
		 *  find center, then wrap again. The y's need no treatment unless
		 *  also wrapped. Treat y wrapping similarly wrt y.
		 *  
		 */
		
		// Shortcut for small corals
		if ( cellLocsSet.size() < 3 )
		{
			// With only 1 or 2 cells, either will do for a center 
			// This is the recommended way to get a single object from a set
			Int2D newCenter =  cellLocsSet.iterator().next();  
			return newCenter; 
		}

		// Otherwise, start here
		int width  = StaticCoralCell.getCoralCellGrid().getWidth ();
		int height = StaticCoralCell.getCoralCellGrid().getHeight();
		
		// Find if the 0 and max dimension cells are both occupied
		// in x and/or y directions
		boolean zeroX   = false;
		boolean widthX  = false;
		boolean zeroY   = false;
		boolean heightY = false;
		
		for( Int2D cellLoc : cellLocsSet )
		{
			// A coral which needs unwrapping will always be present at both x = 0 and width -1, 
			// and/or y = 0 and height -1
			// Note that a grid of width 5 goes up to x = 4
			if ( cellLoc.x == 0          ) zeroX   = true;
			if ( cellLoc.x == width - 1  ) widthX  = true;
			if ( cellLoc.y == 0          ) zeroY   = true;
			if ( cellLoc.y == height - 1 ) heightY = true;
		}
		
		// Grab all the coral's cell coordinates for temporary, independent alteration
		List < Int2D > coords = new ArrayList < Int2D > ();
		coords.addAll( cellLocsSet );
		
		// A potentially wrapped coral will always have cells at both 0 and ( grid (width or height) - 1 )
		if ( ( zeroX && widthX ) || ( zeroY && heightY ) )
		{
			// We will assume wrapped corals do not include both the middle cell
		    // and the edge cells. Thus, the max diameter of a coral should
			// always be < half of the smallest grid dimension. This is controlled 
			// in StaticCoral.
			int midX = ( int ) ( width  / 2d );
			int midY = ( int ) ( height / 2d );
			
			// Grab all the coral's cell coordinates for looping through even though
			// the coords List is likely to change. This avoids changing the List 
			// which is being looped through.
			List < Int2D > coordsTemp = new ArrayList < Int2D > ();
			coordsTemp.addAll( coords );
			int counter = 0;
			
			for(Int2D cellLoc : coordsTemp) // go through each cell of that potentially wrapped coral
			{
				int newX = cellLoc.x;
				int newY = cellLoc.y;
				
				// Note that for a grid of width 10, with cells at 0 and 9,
				// a cell at x = 0 should end up at location 10.
				// a cell at x = 1 should end up at location 11.
				// So, it will be moved temporarily to x + 10 or x + width.

				if ( ( zeroX && widthX ) && ( cellLoc.x < midX ) ) // coral is wrapped wrt x
				{
					newX = cellLoc.x + width; // Unwrap the x coordinate 
				}
			
				if ( ( zeroY && heightY ) && ( cellLoc.y < midY ) )// coral is wrapped wrt y
				{
					newY = cellLoc.y + height; // Unwrap the y coordinate
				}
				
				Int2D newLoc = new Int2D ( newX, newY ); // These might be changed or unchanged above as needed
				coords.set( counter, newLoc );           // Replace the wrapped coordinates with possibly unwrapped coordinates

				counter++;
			}
		}
		
		// Both previously wrapped and not wrapped corals continue here
		// Sum x and y values after subtracting away the minimum
		// to put the edge of the coral wrt that dimension on 0.
		
		// Find min and max values
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;

		for( Int2D cellLoc : coords )
		{
			if ( minX > cellLoc.x ) minX = cellLoc.x; 
			if ( minY > cellLoc.y ) minY = cellLoc.y;
		}
		
		int sumX = 0;
		int sumY = 0;
	
		for( Int2D cellLoc : coords )
		{
			sumX = sumX + cellLoc.x; // - minX;  // No need to move the coral, the center can be found anyway
			sumY = sumY + cellLoc.y; // - minY;
		}
	
		// Find approx. weighted middle values, whether or not unwrapped (integer part of mean of x or y distances from zero)
		int centerX   = ( ( int ) ( sumX / (double) coords.size() ) );  
		int centerY   = ( ( int ) ( sumY / (double) coords.size() ) );
		
		//Check if that center corresponds to the right coral in the wrapped grid
		int toroidalX = StaticCoralCell.getCoralCellGrid().tx( centerX );
		int toroidalY = StaticCoralCell.getCoralCellGrid().ty( centerY );
	
		Object tryCoral = StaticCoralCell.getCoralCellGrid().get( toroidalX, toroidalY ); // This gets whatever object is at that location
		
		if (   ! ( ( tryCoral instanceof StepCoral ) && (  ( ( StepCoral ) tryCoral ).getCoralId() == coralId ) )   )
		{
			// If center cell does not contain a cell of that coral, loop through the coral's actual cells to find nearest one.  
			
			int bestX = centerX;
			int bestY = centerY;
			double minSqrdDist = Double.MAX_VALUE;
			
			for( Int2D cellLoc : coords )
			{
				//double trySrdDist = ( ( centerX - cellLoc.x ) * ( centerX - cellLoc.x ) + ( centerY - cellLoc.y ) * ( centerY - cellLoc.y ) );
				Double2D centerDouble = new Double2D( ( double ) centerX,   (double ) centerY   );
			    Double2D cellDouble   = new Double2D( ( double ) cellLoc.x, (double ) cellLoc.y );

				double trySqrdDist = StaticCoralCell.getContinuousSpace().tds( centerDouble, cellDouble );
				 
				if( minSqrdDist > trySqrdDist )
				{
					bestX       = cellLoc.x;
					bestY       = cellLoc.y;
					minSqrdDist = trySqrdDist; 
				}
			}
			centerX = bestX;
			centerY = bestY;
		}
		// Make the final 'center' coordinates toroidal 
		centerX = StaticCoralCell.getCoralCellGrid().tx( centerX );
		centerY = StaticCoralCell.getCoralCellGrid().ty( centerY );
		
		//Whether or not shifted, define the 'center' location 
		Int2D centerLoc = new Int2D( centerX, centerY );
		
		return centerLoc;
	}
}
