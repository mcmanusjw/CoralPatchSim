package mcmanusjw.CoralPatchSimJuly2019;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;
import sim.util.Int2D;


/**
 * 
 * @author John McManus
 * @version 20 November 2017
 * 
 * This class finds close neighbors of corals and calculates their refuge volumes and typical fish lengths.
 * It does the following:
 *   Search for each coral its neighbors up to its neighbor height value (nHeight) from the associated shape enum.
 *   	For this search, it calculates the toroid-corrected distance between the coral and its potential neighbor,
 *   	subtracting the two radii to see if the gap between is within the nHeight distance. 
 *   Rejects itself as a partner and any corals with lower nHeight values (allowing them to find their own neighbors).
 *      We are basing the refuge volumes on the lowest height of each pair, so if the next coral is lower, we save it 
 *      for later pairings. 
 *   To allow for damage,it then calculates the distance between the cells paired up for the two corals, to find the
 *   	minimum distance, and then checking if the above criteria still hold. 
 *   If there is 0 distance between cells, there is no refuge volume, and so this pair is skipped (we only count
 *      through passage-ways for this, as identifying widths of closed 'canyons' would be complex given different
 *      shapes of potentially damaged coral edges). In reality, two corals' living tissues touching each other is rare 
 *      because of battling for space. Thus, only dead skeletons usually can touch. Our simulations do not account for this. 
 *   We search between the two most adjacent cells in case there is another coral in between (an 'intruder'). This is done 
 *      by passing the two locations to a lineFinder routine based on the Bresenham line finding algorithm. The locations
 *      along this (unaliased) line are stored in a Set, which is then searched to find thrid-party coral cells. If such 
 *      an intruder is found, the process stops and we move to the next pair of corals.   
 *   If the criteria hold, it then calculates the standard fish length (5 times width) and columnar refuge volume
 *      wrapping around the fish. These are kept in a HashMap of Double2D's doubles.
 *   Puts the coralId's of the completed pairs into an HashSet of Double2D's, to use in screening to avoid duplicate pairings.
 *   
 *   Note that we assume a standard fish is as wide as it is tall, both of which equal the lesser (or equal) 
 *   nHeight of the paired corals. We thus get a shoebox-sized volume of length * length * width of the fish, where
 *   fish length is 4 times its width. 
 *   
 *   In this analysis, we do not account for toroidal wrapping (which would be unnecessarily complex), and instead 
 *   treat the 'visible' grid as a representative snapshot of the community. 
 *        
 */
@SuppressWarnings("serial")
class StepCalculateNeighborRefuge implements Steppable
{
	static Map < Double, Double > neighborFishLengthToRefugeVolume;	

	// Grid dimensions
	static int width;
	static int height;
	
	Int2D pairIds; // Just making it class-wide accessible
	
	StepCalculateNeighborRefuge()
	{
		init();
	}
	
	static void init()
	{
		int seriesNumber = StepLooper.getSeriesNumber();
		width  = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "Width"  );
		height = width;
	}
	
	@Override
	public void step( SimState state ) 
	{
		Set < StepCoral > stepCorals = Collections.unmodifiableSet( StaticCoral.getCoralByTypeTable().uniqueValues() );// was getCorals();
		Set < Int2D > coralPairsTried = new HashSet < Int2D > ();
		
		neighborFishLengthToRefugeVolume = new HashMap < Double, Double > ();
		
		for( StepCoral stepCoral : stepCorals )			
		{
			// These variables and statements result in a calculated nHeight for this coral
			double radius            = ( double ) stepCoral.getRadius ();           // Widening conversion
			String growthForm        = stepCoral.getGrowthForm();                   // This currently should capitalized like "Column"
			double maxColumnDiameter = ( double ) stepCoral.getMaxColumnDiameter(); // This = 0 if not needed for this growthform
			double maxColumnHeight   = ( double ) stepCoral.getMaxColumnHeight();   // This = 0 if not needed for this growthform

			double neighborHeight = EnumAccessor.fromString( growthForm ).getAccessor()
					     .calculateFromString( "N_Height", maxColumnHeight, maxColumnDiameter, radius * 2 );
			
			// Other variables
			int      coralId         = stepCoral.getCoralId();
			Int2D    loc             = stepCoral.getCenterLoc();
			Double2D locD            = new Double2D( loc.x, loc.y ); 
			
			// Squared distance to search is equal to the coral nHeight + 1 cell in between, all squared 
			int searchDist     = ( int ) Math.ceil( neighborHeight ); // Need for coral center distances so we can subtract away coral radii
		    int searchDistSqrd = searchDist * searchDist;             // Need for cell distances to reduce calculations 
		    double minDistSqrd = Double.MAX_VALUE;
		    
		    Set < SubStepCoralCell > cells  = stepCoral.getCells();
		    
			for( StepCoral nextCoral : stepCorals )			
			{
				// Prevent comparing with itself
				if   ( nextCoral  == stepCoral  )  continue;

				int nextCoralId = nextCoral.getCoralId();
				
				if ( coralId < nextCoralId ) // Putting lowest ID on left for consistent searches if present in coralPairsFound
				{
					pairIds = new Int2D ( coralId, nextCoralId );
				}
				else
				{
					pairIds = new Int2D ( nextCoralId , coralId );
				}				

				// No already-tried pairs (in this order of coralIds) allowed
				//if ( coralPairsTried.contains( pairIds ) ) continue;

				double nextRadius = ( double ) nextCoral.getRadius ();  
				growthForm        =            nextCoral.getGrowthForm();
				maxColumnDiameter = ( double ) nextCoral.getMaxColumnDiameter(); 
				maxColumnHeight   = ( double ) nextCoral.getMaxColumnHeight();

				double nextNeighborHeight =	EnumAccessor.fromString( growthForm ).getAccessor()
						.calculateFromString( "N_Height", maxColumnHeight, maxColumnDiameter, nextRadius * 2 ); 
				
				// nextNeighborHeight should not be more than neighborHeight because we use the neighborHeight
				// which should be the lesser of the two to define the refuge volume
				if   ( nextNeighborHeight < neighborHeight ) continue;

				// Add these coral id's to Set so we know which pairs have been considered  
				coralPairsTried.add( pairIds ); 
		
				// Potential gap between corals, not yet at cell level, must not be too large.
				// We calculate the distance between coral centers loc and nLoc (as doubles locD and nLocD),
				// then subtract away the sum of the two radii, giving the gap between.
				// Note cannot simply subtract squared portion coral portion from squared distance.
				// For example, 5 - 2 = 3, but the square root of (25 - 4 = 21) is 4.58..., not 3.
				// So we must find the unsquared distance, then subtract to get the gap and
				// then compare that to an unsquared search distance
				
				Int2D    nextLoc         = nextCoral.getCenterLoc();
				Double2D nextLocD        = new Double2D( nextLoc.x, nextLoc.y );  				
				double   distCentersSqrd = StaticCoralCell.getContinuousSpace().tds( nextLocD, locD );
				double   distCenters     = Math.sqrt( distCentersSqrd ); //////////////slow part
				double   sumCoralRadii   = ( radius + nextRadius );
				double   potentialGap    = Math.abs( distCenters - sumCoralRadii );

				// Skip out if potential gap too large (this speeds things up).
				if ( potentialGap > searchDist ) continue;
				
				Set < SubStepCoralCell > nCells = nextCoral.getCells();
			
				// Now find the smallest actual gap between the two coral colonies by comparing all their cells
				Int2D closestLoc  = new Int2D( -1, -1 ); // Just to have the closetLoc available outside the next 2 loops
				Int2D closestNLoc = new Int2D( -1, -1 ); // store for testing only
				
				for( SubStepCoralCell cell : cells )
				{
					Int2D    locCell  = cell.getLoc();
					Double2D locCellD = new Double2D( ( double ) locCell.x, ( double ) locCell.y );
					
					for( SubStepCoralCell nCell : nCells )
					{
						Int2D    nLocCell  = nCell.getLoc();
						Double2D nLocCellD = new Double2D( ( double ) nLocCell.x, ( double ) nLocCell.y );

						double   cellDistSqrd = StaticCoralCell.getContinuousSpace().tds( nLocCellD, locCellD );
						
						// No 0 gap allowed
						if ( cellDistSqrd < 1 ) break; // corals are too close so try another pair of corals
						
						if ( cellDistSqrd < minDistSqrd )
						{
							minDistSqrd = cellDistSqrd;   // Capture smallest paired cell distance 
							closestLoc  = locCell;
							closestNLoc = nLocCell; // for testing only
						}
					}
				}
					
				if( closestLoc.x < 0 ) continue; // negative value indicates no closeness search done -- try another coral pair

			
				// Cells too far apart (possibly due to coral damage) so try new coral pair
				if ( minDistSqrd > searchDistSqrd ) continue;
					
     			// Check that no other coral is in between
	    		// We use an expanding radial search from the closest cell up to the minDistSqrd
				boolean intruder = false;
				
				// Find line of locations between the two closest cells (order does not matter here so we use a Set)
				Set < Int2D > line = UtilJM.findLine( closestLoc, closestNLoc );
				
				// Check this Set of coordinates in the line for the occurrence of cells from other corals
				for ( Int2D coord : line ) 
				{	
					SubStepCoralCell cell = ( SubStepCoralCell ) StaticCoralCell.getCoralCellGrid().get( coord.x, coord.y );
					if  (  ( cell != null ) && ( cell instanceof SubStepCoralCell ) 
				    	&& ( cell.getCoralId() != coralId ) && ( cell.getCoralId() != nextCoralId ) )
					{
						intruder = true;
						//System.out.println( " Intruder found ");
						
						break; // intruder found so no need to look further
					}
				}

				if ( intruder ) continue; // move to next coral pair, as this pair is not valid
				
				double minDist = Math.sqrt( minDistSqrd );
				
				double fishLength = 4 * minDist; // Was 5 * 
				// Note: Area = D^2 * length with minDistSqrd = D
				double refugeVolume = minDist * minDist * fishLength; 
				
				//System.out.println( "fishlength: " + fishLength + " refugeVolume: " + refugeVolume );
				
				// Finally, add to the StaticCoral neighborFishLengthToRefugeVolume HashMap 
				neighborFishLengthToRefugeVolume.put( fishLength, refugeVolume );
				
			} // end NextCoral inner loop
		} // end of StepCoral outer loop by coral
	}  // end of step

	// Getter follows
	
	static Map<Double, Double> getNeighborFishLengthToRefugeVolume() {
		return neighborFishLengthToRefugeVolume;
	}
} // end of class

