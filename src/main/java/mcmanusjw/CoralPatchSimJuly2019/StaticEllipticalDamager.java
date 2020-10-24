package mcmanusjw.CoralPatchSimJuly2019;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ec.util.MersenneTwisterFast;
import sim.util.Int2D;

/**
 * 
 * 
 * @author John McManus
 *
 */

class StaticEllipticalDamager 
{
	static int cellsToDamage;
	
	static Set < Int2D > cellCoordsConsidered;
	
	static void init()
	{
		cellsToDamage = 0;
		cellCoordsConsidered = new HashSet< Int2D >(); // This is only cleared if all cells damaged as requested,
                                                       // or if no more ellipses to be called
	}
	
	public static boolean damage( double depthProb, int xCenter, int    yCenter, 
			                      double ellipseLength, double   ellipseWidth, 
			                      double angleDegrees , double   waveStrength,
			                      int    cellsToDamage, MersenneTwisterFast random ) 
	{
		//System.out.println( " cellsToDamage in Damager at top: " + cellsToDamage );//////////////////////////////
		
		StaticEllipticalDamager.cellsToDamage = cellsToDamage; // new 26 July 2019 //////////////////////
		
		boolean complete = false;
		Set < Int2D > processedCoordsThisWave = new HashSet < Int2D >();
		
		//Set species index of damage  
		int damageIntSpeciesGrid = 0 ; // set to white 

		//Get grid dimensions
		int seriesNumber = StepLooper.getSeriesNumber();
		int width  = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "Width"  );
		int height = width;
		
		double bufferProbability = 0.5;
		double bufferPercent     = 0.75;
		
		List < Double > ellipseValues = List.of( 1.0, bufferProbability ); 
       
		int countIterations = 0;
		//start loop of two	passes
		for ( double ellipseValue: ellipseValues )
		{
			//System.out.println( " Now doing the ellipse for percentage: " + ellipseValue );////////////////////////////

			double eLength = ellipseLength;
			double eWidth  = ellipseWidth;
			
			// For the case of the first, central 100% damage ellipse, make it 80% dimensional
			if ( countIterations < 1 )
			{
				eLength = ellipseLength * bufferPercent; 
				eWidth  = ellipseWidth  * bufferPercent;
			}

			//Find major and minor radii
			double aEllipse = eLength/2;
			double bEllipse = eWidth /2;

			//Find area = PI * a * b (so we know when to stop filling it)
			double areaEllipse     = Math.PI * aEllipse * bEllipse;
			double areaFullEllipse = Math.PI * ( ellipseLength / 2 ) * ( ellipseWidth / 2 );
			
			
			// Make smaller ellipses when close to total cells to be touched -- avoids incomplete wave effects
			// If running too near max only on first pass for the ellipse, 
			// calculate a small ellipse using same proportions 
			// as original but using roughly the total number of remaining cells to 'touch'.
			// L = long radius, W = width radius and ratio = L/W = 2L/2W, where 2L = full ellipse length.
			// Given area = L*W*PI, then L*W = area / Pi. Also, L = ratio * W.  
			// Thus (ratio * W) * W = area / PI, so W^2 = area / ( PI * ratio ).
			// width = sqrt( area / ( PI * ratio ) ). Then L = ratio * W.
			if ( ( countIterations < 1 ) && ( areaFullEllipse + cellCoordsConsidered.size() > cellsToDamage ) ) 
			{
				double newArea         = cellsToDamage - cellCoordsConsidered.size();
				if( newArea < 100 ) return true; // Too small to bother with and so return as complete 
                //System.out.println( " new ellipse area:  " + newArea ); 				
				double ratioAxes       = ellipseLength / ellipseWidth;
				double newWidthRadius  = Math.sqrt( newArea / ( Math.PI * ratioAxes ) );
				double newLengthRadius = ratioAxes * newWidthRadius;
				
				ellipseWidth  = 2 * newWidthRadius;      // This will remain changed for next pass in making the ellipse
				ellipseLength = 2 * newLengthRadius;     // This will remain changed for next pass in making the ellipse 
				eWidth  = ellipseWidth  * bufferPercent; // This will change back to full value during next pass in making the ellipse
				eLength = ellipseLength * bufferPercent; // This will change back to full value during next pass in making the ellipse
				
				//Find major and minor radii
				aEllipse = eLength/2;                // This will change back to full value during next pass in making the ellipse   
				bEllipse = eWidth /2;                // This will change back to full value during next pass in making the ellipse
				
				//Find area = PI * a * b (so we know when to stop filling it)
				areaEllipse     = Math.PI * aEllipse * bEllipse;
			}
			
			//Dimension the ellipse cells array for ellipse size plus 50% for discreet math errors
			int dim = (int) Math.ceil(areaEllipse * 1.5);
			int[][] ellipseCoords = new int[dim][2];
			
			// Create zero-centered horizontal ellipse as a set
			ellipseCoords = makeEllipse( ellipseCoords, dim, eLength, eWidth, aEllipse, bEllipse );
		
			// Rotate each cell in the set around the ellipse center. Note must use full axes for this.
			if ( angleDegrees != 0 )
				ellipseCoords = rotate2dCoords( ellipseCoords, dim, angleDegrees, ellipseLength, ellipseWidth, ellipseValue );
		
			// Fill in holes caused by stretching on rotation. Note must use full axes for this.
			ellipseCoords = fillHoles( ellipseCoords, dim, ellipseLength, ellipseWidth );
		
			// Translate each cell in the set
			ellipseCoords = translate2dCoords( ellipseCoords, dim, xCenter, yCenter );
		
			// Convert all coordinates  to toroidal
			make2dCoordsToroidal( ellipseCoords, dim, height, width );
			
			// Put cells into coralGrid and speciesGrid
			complete = applyShapeToGrid( ellipseCoords, dim, eLength, eWidth, 
										 depthProb, waveStrength, ellipseValue, damageIntSpeciesGrid, 
										 processedCoordsThisWave, random );
			if ( complete ) break;
			
			countIterations ++;

		} // end loop of two passes		
		//UtilJM.warner( " end of dual loop and returning complete: " + complete, true );
		
		return complete;
	}
	
	private static int [][] makeEllipse(int [][] ellipseCoords, int dim,
			double ellipseLength, double ellipseWidth, double aEllipse, double bEllipse)
	{
		/** 
		 *  Creates a box slightly wider and higher than a required ellipse. 
		 *  Then searches all cell coordinates within the box, keeping only
		 *  those that are within the ellipse. Note that the 'cells' are not
		 *  actually on a grid, allowing this method to operate in -x,y
		 *  x,-y, and -x,-y quadrants as required for a zero-centered ellipse.
		 */

		int counter = 0;
	    int x = 0;
	    int y = 0;
	    int boxLength = (int) ellipseLength; 
	    int boxWidth  = (int) ellipseWidth ;   
	    int midLength = (int) ( boxLength / 2d );
	    int midWidth  = (int) ( boxWidth  / 2d );
	    
        for (int i = 0; i < (boxLength); i++)
		{
			for (int j = 0; j < (boxWidth); j++)
			{
	    		x = (int) (i - midLength);
				y = (int) (j - midWidth);

				// x^2/a^2 + y^2/b^2 = 1 when ellipse is zero-centered
				double leftExpression = ( ( x * x ) / ( aEllipse * aEllipse ) ) + ( ( y * y ) / ( bEllipse * bEllipse ) );
				
				//keep only those coordinates that fall within the ellipse
				if (leftExpression <= 1)
				{
					ellipseCoords[counter][0] = x;
					ellipseCoords[counter][1] = y;
					counter++;
				}
			}
		}

		return ellipseCoords;
	}

	private static int[][] rotate2dCoords( int [][] shapeCoords, int dim, double angleDegrees, 
			                               double ellipseLength, double ellipseWidth, double ellipseValue ) 
	{
		/** 
		*  Applies rotation to all coordinates of a shape as follows (alpha is in radians):
		* 	x = X*cos(alpha) - Y*sin(alpha),
		* 	y = X*sin(alpha) + Y*cos(alpha).
		*/
		int x = 0;
		int y = 0;
		
		double midLength = ellipseLength / 2d;
		double midWidth  = ellipseWidth  / 2d;

		// Convert the angle to radians and make it negative to act from the right
		double angleRadians = - Math.toRadians(angleDegrees);
		
		for(int i = 0; i < dim; i++)
		{
			x = shapeCoords[i][0];
			y = shapeCoords[i][1];

			// translate point to origin
        	double tempX = x - midWidth;
            double tempY = y - midLength;

			// rotate point around ellipse center (currently considered to be at ( 0, 0 ))
			double rotatedX = tempX * Math.cos( angleRadians ) - tempY * Math.sin( angleRadians );
			double rotatedY = tempX * Math.sin( angleRadians ) + tempY * Math.cos( angleRadians );

			// translate back
			x = (int) Math.round( rotatedX + midWidth  );
			y = (int) Math.round( rotatedY + midLength );
			
			shapeCoords[i][0] = x;
			shapeCoords[i][1] = y;
		}
		
		return shapeCoords;
	}
	
	private static int[][] fillHoles( int [][] shapeCoords, int rows, double ellipseLength, double ellipseWidth )
	{
		// Get the maximum of ellipse length and width, and make a square temporary grid of that dimension
		int tempGridDim  = (int) ( Math.max( Math.ceil( ellipseLength ), Math.ceil( ellipseWidth ) )  ); 
		int[][] tempGrid = new int[ tempGridDim * 3 ][ tempGridDim * 3 ];

		int shift = (int) ( tempGridDim  );

		// First fill the square tempGrid with 0s
		for (int i = 0; i < tempGrid.length; i++) 
		{
			for (int j = 0; j < tempGrid.length; j++) 
			{
				tempGrid [ i ][ j ] = 0;
			}
		}
		
		//printBox( tempGrid );///////////////////////////////////////////////
		
		// Next apply the shapeCoords to the tempGrid as 1s, moving them to range 0 to tempGridDim
		// Prior to translation, the lowest values will be negative, because the ellipse is still
		// centered on zero. 
		for (int i = 0; i < rows; i++) 
		{
			int x = shapeCoords[i][0] + shift; 
			int y = shapeCoords[i][1] + shift; 
			tempGrid[ x ][ y ] = 1;
		}
		
		//printBox( tempGrid );////////////////////////////////////////////////
		
		// Now find any 0s sandwiched in 1s, and convert those to 1's
		for ( int i = 0; i < tempGrid.length; i++ ) 
		{
			for ( int j = 0; j < tempGrid.length; j++ ) 
			{
				if (   ( tempGrid [ i ][ j     ] == 1 )
					&& ( tempGrid [ i ][ j + 1 ] == 0 )
					&& ( tempGrid [ i ][ j + 2 ] == 1 ) )
				{
					tempGrid [ i ][ j + 1 ] = 1;
				}
			}
		}
		
		//printBox( tempGrid );////////////////////////////////////////////////
			
		// Finally, retrieve the new shapeCoords ( == 1s in the tempGrid ), moving them back up to the original range
		int counter = 0;
		for ( int i = 0; i < tempGrid.length; i++ ) 
		{
			for ( int j = 0; j < tempGrid.length; j++ ) 
			{
				if ( tempGrid [ i ][ j ] == 1 )
				{
					shapeCoords[ counter ][ 0 ] = i - shift;
					shapeCoords[ counter ][ 1 ] = j - shift;
					counter++;
				}
			}
		}
		
		return shapeCoords;
	}
/*	
	private static void printBox( int[][] box )
	{
		/**
		 * Use this with TestEllipticalDamager to see one ellipse being cleaned up
		 */
/*		System.out.println();
		
		for ( int i = 0; i < box.length; i++ ) 
		{
			for ( int j = 0; j < box.length; j++ ) 
			{
				System.out.print( box[ i ][ j ] );
			}
			System.out.println();
		}
		System.out.println();
	}	
*/	
	private static int[][] translate2dCoords(int [][] shapeCoords, int dim, int xCenter, int yCenter)
	{
		/**
		 *  Translates all coordinates of a shape so that the shape is centered 
		 *  at xCenter, yCenter. 
		*/
		
		int x = 0;
		int y = 0;
		
		for(int i = 0; i < dim; i++)
		{
			x = shapeCoords[i][0];
			y = shapeCoords[i][1];
			x += xCenter;
			y += yCenter;
			shapeCoords[i][0] = x;
			shapeCoords[i][1] = y;
		}		
		return shapeCoords;
	}
	
	private static int[][] make2dCoordsToroidal(int [][] shapeCoords, int size, int height, int width)
	{
		/**
		 *  Wraps coordinates of a shape such that any falling 
		 *  left, right, above or below a grid of height
		 *  x width will be wrapped within it. 
		 *  For shapeCoords[i][j], i is for the data pairs (0 to n-1 inclusive),
		 *  and j is always 0 (for x) or 1 (for y). 
		 */
		
		int x = 0;
		int y = 0;
		
		for(int i = 0; i < size; i++)
		{
			x = StaticCoralCell.getCoralCellGrid().tx( shapeCoords[i][0] );
			y = StaticCoralCell.getCoralCellGrid().tx( shapeCoords[i][1] );
			
			shapeCoords[i][0] = x;
			shapeCoords[i][1] = y;
		}		
		return shapeCoords;
	}
	

	private static boolean applyShapeToGrid( int [][] shapeCoords, int dim,
			                                 double ellipseLength, double ellipseWidth,
			                                 double depthProb,     double waveStrength,  
			                                 double ellipseValue,  int damageIntSpeciesGrid, 
			                                 Set < Int2D > processedCoordsThisWave, MersenneTwisterFast random )
	{
		/**
		 *  Replaces the cells of a grid with damage codes for each cell coordinate pair
		 *  in a shape, calling the killCell method if a coral cell is encountered.
		 *  If the cell draws a number below its overall damage probability (probDeath), 
		 *  the coral itself is put into a temporary set if it is to be marked for death 
		 *  (or into a saved set if it is not so unfortunate, and this guides further encounters
		 *  with cells from that coral under this damaging ellipse.  
		 */
		
		//System.out.println( " applyShapeToGrid() ");
		//System.out.println( " dim: " + dim + " cellsToDamage: " + cellsToDamage );
		
		boolean complete = false;
		int x = 0;
		int y = 0;
		
		//int numCellsKilledThisEllipse = 0;///////////////////////////////////////////
		
		// Make Sets of corals which can be killed or saved under this ellipse, decided on first encounter.
		// This is better than just deciding cell by cell, which could lead to highly fragmented corals.
		Set < StepCoral > markedCorals = new HashSet < StepCoral >(); 
		Set < StepCoral > savedCorals  = new HashSet < StepCoral >();
		
		for(int i = 0; i < dim; i++)
		{
			x = shapeCoords[i][0];
			y = shapeCoords[i][1];
			Int2D cellLoc = new Int2D( x, y );
			

			cellCoordsConsidered.add( new Int2D( x, y ) ); // Included even if nothing in that cell, if not hit before
			if ( cellCoordsConsidered.size() > cellsToDamage ) 
			{
				clearCellCoordsDamaged();
				complete = true;
				break;
			}

			if ( StaticCoralCell.getCoralCellGrid().get( x, y ) != null )
			{
				//if ( i == 0 ) System.out.println( "If not null ");//////////////////////////
				
				SubStepCoralCell cell = ( SubStepCoralCell ) StaticCoralCell.getCoralCellGrid().get( x, y );
				int coralId           = cell.getCoralId(); 
				StepCoral stepCoral   = StaticCoral.getCoralIdToCoralObject().get( coralId );
				int speciesId         = stepCoral.getSpeciesId();
				double vulnerability  = StaticBenthosData.getSpeciesInputTable().getDouble(speciesId, "EllipseVulnerability" );
				if ( waveStrength > 0.95) vulnerability = 1.0;
				double probDeath      = depthProb * vulnerability * waveStrength * ellipseValue; // ellipseValue is 1.0 or less

				// If not in any of the Sets, add it into one or the other based on probability of mortality // put it on one with 50% chance
				if (  !processedCoordsThisWave.contains( cellLoc ) )
				{		
					processedCoordsThisWave.add( cellLoc );
					
					if ( !savedCorals.contains( stepCoral   ) ) //( !markedCorals .contains( coral ) && ( !savedCorals.contains( coral   ) ) )
			       {
			    	   double randomDraw = random.nextDouble();
					
			    	   if ( randomDraw <= probDeath )
			    	   {
			    		   markedCorals.add( stepCoral );
			    	   }
			    	   else if ( !markedCorals .contains( stepCoral ) )
			    	   {
			    		   savedCorals.add( stepCoral ); 
			    		   continue; // shortcut out for this cellLoc
			    	   }
					
			    	   if( savedCorals.contains( stepCoral ) )
			    	   {
			    		   continue; // any cells from any saved corals will be spared by skipping the rest of this loop
			    	   }

			    	   StaticCoralCell.killNotFromCoralKill( cell );
			    	   
			    	  // System.out.println( "Killed a cell ");/////////////////////////////////
			    	   
			    	  // numCellsKilledThisEllipse++;
			    	   // Put damaged cell on the grids
			    	  
			    	  // Flag follows
			    	  // if ( ellipseValue == 1.0 ) damageIntSpeciesGrid = 8; ///////////
			    	  // else damageIntSpeciesGrid = 9;/////////////////////////////
			    	   
			    	   StaticCoralCell.getSpeciesCellGrid().set( x, y, damageIntSpeciesGrid ); //<<<<<<<<<<<<<<<<<<<<<
			    	   StaticCoralCell.getCoralCellGrid()  .set( x, y, null );
			       } //  end of cell's coral not processed before
				} // end of if cell not processed before
			} // end of if cell not null
		} // end of loop by coordinates
		
		return complete;
	}
	/**
	 * This is called from applyShapeToGrid() if enough cells damaged,
	 * or from the StepEllipticalDamageStepper.step() if all looped out and 
	 * still no damage completion  
	 */
	static void clearCellCoordsDamaged() 
	{
		cellCoordsConsidered.clear();
	}
}
