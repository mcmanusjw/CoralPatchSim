package mcmanusjw.CoralPatchSimJuly2019;

import java.util.HashSet;
import java.util.Set;

import sim.engine.SimState;
import sim.util.Int2D;

/**
 * Class to input planted corals.
 * 
 * Get rid of negatives for corals near zero (use toroid conversions?).
 * 
 * Call this only after certain there are no deletions from the plantings.
 *  
 * @author John McManus
 *
 */

class StaticMakeAndGrowPlantedCorals 
{
	static Set < SubStepCoralCell > cellSet    = new HashSet < SubStepCoralCell >();
	
	static final double SQRT2 = 1.41421356237;
 	
	static boolean makeCorals( SimState state, Set < SpeciesIdDiameterLoc > sdlSet )
	{
		//System.out.println( "<<<< width, height: " + ConsoleBig.planter.getWidth() 
		//					+ ", " + ConsoleBig.planter.getHeight() );//////////////////
		// use these to convert center location to percent of width and height
		//int pWidth  = ConsoleBig.planter.getWidth();
		//int pHeight = ConsoleBig.planter.getHeight();
		int pWidth  = ConsoleBig.getClickWidth();   //.clickPane.getWidth();
		int pHeight = ConsoleBig.getClickHeight();  //.clickPane.getHeight();
		
		//System.out.println( " pWidth: " + pWidth + " pHeight: " + pHeight );
		
		boolean ok = true;
		
		for( SpeciesIdDiameterLoc sdl : sdlSet )
		{
			Int2D centerLoc = sdl.getLoc();
			int   speciesId = sdl.getSpeciesId();
			int radius = (int) Math.ceil( sdl.getDiameter() / 2.0 ); // go back and change sdl to use radius instead
			
			// Convert centerloc to percent of planter panel
			double wPercent = centerLoc.x / ( double ) pWidth ;
			double hPercent = centerLoc.y / ( double ) pHeight;
			
			//System.out.println( "\n Initial centerLoc " + centerLoc );/////////////////
			//System.out.println( " wPercent: " + wPercent ); /////////////////////
			//System.out.println( " hPercent: " + hPercent ); ////////////////
			
			// Get simulation dimensions
			int seriesNumber = StepLooper.getSeriesNumber();
			int sWidth  = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "Width"  );
			int sHeight = sWidth;
			
			// Correct centerLoc to simulation dimensions (don't use round because you might go out of bounds)
			int   newWidth     = ( int )  ( sWidth  * wPercent  );
			int   newHeight    = ( int )  ( sHeight * hPercent  );
			Int2D newCenterLoc = new Int2D( newWidth, newHeight );
			//System.out.println( " newCenterLoc" + newCenterLoc  );/////////
			
			//System.out.println( " sWidth , newWidth  " + sWidth  + ", " + newWidth  );///////////
			//System.out.println( " sHeight, newHeight " + sHeight + ", " + newHeight );/////////
			
			int newRadius = (int) ( radius * ( sWidth / pWidth ) ); // WARNING only good for width = height <<<<<<<<<<<
			//System.out.println( "radius, newRadius: " + radius + ", " + newRadius );///////////
			//System.out.println();////////////
			
			//Set < Int2D > locs = makeCircle( newRadius, newCenterLoc );
			Set < Int2D > locs = simpleMakeCircle( newRadius, newCenterLoc );
			
			// Make the new coral
			// Variables: SimState state, Int2D centerLoc, int speciesId, int radius, Set < SubStepCoralCell > cells
			int coralId = StaticCoral.newCoral( state, newCenterLoc, speciesId, radius, cellSet );
			
			StepCoral stepCoral = StaticCoral.getCoralIdToCoralObject().get( coralId );
			
			// make the new coral cells and give them to the coral
			for( Int2D loc : locs )
			{
				ok = StaticCoralCell.newCoralCell( loc, stepCoral);
			}
		}
		return ok;
	}
	
	// Make a grow class here to update cellSet
/*	
	/**
	 * This returns a Set of coordinates for a circle centered on 0,0, of input radius. 
	 * It speeds up the process by calculating distances only for 1/4 of the circle, and
	 * then replicating those in three other virtual quadrants. Cells in the inner inscribed
	 * circle are added without calculation. 
	 * 
	 * These points are then translated to the actual center coordinates by adding to them
	 * the x and y values of the center coordinates. 
	 * 
	 * The diagonal of the box = circle diameter = 2r. Find the side length (s) for the box. 
	 * For a square with diagonal of 3, s√2=3. Thus s=3/√2. 
	 * For a square with diagonal 2r, s√2= 2r. Thus s= 2r/√2.
	 * Half the side is in the first quadrant, so h=s/2=2r/2√2=r/√2.
	 * So, the corner of the inner box is at [r/√2,r/√2].
	 * The 1st quadrant box is thus [0,0], [0,r/√2], [r/√2,r/√2], [r/√2,0].
	 * Using the virtual coordinates instead of the y-inverted monitor ones,
	 * anything with x < r/√2 or y < r/√2 are automatically in the quadrant
	 * part of the circle, and no calculation for those points need be made.
	 * Just add them to the Set as they are encountered in the double loop.
	 * The outer quadrant search box will go [0,0], [0,r], [r,r], [r,0].
	 * Just calculate the squared distance from [0,0] to each point outside
	 * the inscribed box to see what other points go into the Set.   
	 *      
	 */
/*	static Set < Int2D > makeCircle( int radius, Int2D centerLoc )
	{
		Set < Int2D > zLocs = new HashSet < Int2D >(); // zeroed     circle locations
		Set < Int2D > locs  = new HashSet < Int2D >(); // translated circle locations
		
		// Find dimension of 1st quartile part of the inscribed box
//		int    qSide    = ( int ) (int) Math.floor( radius / SQRT2 );
		double rSqr = radius * radius;
		
		// Outer search loop (add 1 to protect for int to double conversion errors)
		for (int x = 0; x < radius + 1; x++) 			
		{
			for (int y = 0; y < radius + 1; y++) 
			{
	//			if ( ( x < qSide -3  ) || ( y < qSide -3  ) ) ///  for safety // not working !!!!!!!
	//			{
	//				zLocs.add( new Int2D ( x, y ) );
	//				
	//				//if not [0,0] which only goes in once (no need to make the Set to reject them)
	//				if ( ( x > 0 ) || ( y > 0 ) ) 
	//				{
	//					zLocs.add( new Int2D (  x, -y ) );
	//					zLocs.add( new Int2D ( -x,  y ) );
	//					zLocs.add( new Int2D ( -x, -y ) );
	//				}
	//			}
	//				else 
	// 			{
				double distSqr = ( x * x ) + ( y * y ); // Euclidean distance = hypotenuse for 2D
					if ( distSqr <= rSqr ) //no [0.0] to worry about here
					{
						zLocs.add( new Int2D (  x,  y ) );
						zLocs.add( new Int2D (  x, -y ) );
						zLocs.add( new Int2D ( -x,  y ) );
						zLocs.add( new Int2D ( -x, -y ) );
					}
				}
			}
		}
		
		//Now translate here for simplicity
		for ( Int2D loc : zLocs )
		{
			locs.add( new Int2D(  ( loc.x + centerLoc.x ) , ( loc.y + centerLoc.y ) )  );
			if ( loc.x < 0 )
			{
				System.out.println( " -x >>>>>>>> " + loc.x);
			}
			if ( loc.y < 0 )
			{
				System.out.println( " -y >>>>>>>> " + loc.y);
				System.exit( 0 );
			}
		}
		
		return locs;
	}
*/
	static Set < Int2D > simpleMakeCircle( int radius, Int2D centerLoc ) // <<<check if sizes consistent!!!!!
	{
		Set < Int2D > locs  = new HashSet < Int2D >(); // translated circle locations
		
		int    side     = radius + radius;
		int    halfSide = ( int ) Math.ceil( side / 2 );
		int    leftX    = centerLoc.x - halfSide;
		int    rightx   = centerLoc.x + halfSide;
		int    upperY   = centerLoc.y - halfSide;
		int    lowerY   = centerLoc.y + halfSide;
		
		double rSqr     = radius * radius;
		
		// Outer search loop (add 1 to protect for int to double conversion errors)
		for (int x = leftX; x < rightx + 1; x++) 			
		{
			for (int y = upperY; y < lowerY + 1; y++) 
			{
				double distSqr = ( ( x - centerLoc.x ) * ( x - centerLoc.x ) ) 
						       + ( ( y - centerLoc.y ) * ( y - centerLoc.y ) ); // Euclidean distance = hypotenuse for 2D
				if ( distSqr <= rSqr ) locs.add( new Int2D ( x, y ) );
			}
		}
		
		for ( Int2D loc : locs )
		{
			// System.out.println( loc );
			if ( loc.x < 0 )
			{
				System.out.println( " -x >>>>>>>> " + loc.x);
			}
			if ( loc.y < 0 )
			{
				System.out.println( " -y >>>>>>>> " + loc.y);
				System.exit( 0 );
			}
		}
		
		return locs;
	}
}
