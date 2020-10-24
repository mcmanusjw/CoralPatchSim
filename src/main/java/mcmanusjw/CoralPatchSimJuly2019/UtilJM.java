package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.util.Int2D;

class UtilJM
{
	// Note that sec(x) = 1/cos(x), and Degree in Mathematica converts degrees to radians
    static final double SEC10DEG = 1d / cos( toRadians( 10d ) );
    static final double TAN10DEG =      tan( toRadians( 10d ) );
    static final double COS20DEG =      cos( toRadians( 20d ) );
    static final double SIN20DEG =      sin( toRadians( 20d ) );
	
	/**
	 * This routine implements a Bresenham algorithm to
	 * find a line between two points (Int2D locFrom & locTo).
	 * It returns a Set of the coordinates in the line.
	 * It can be used to search for corals between corals
	 * under investigation for neighbor refuge volume
	 * calculations. It does not wrap the coordinates
	 * toroidally -- that should be done as needed by the
	 * calling method.
	 *   
	 * Adapted from:
	 * http://lifestyle2all.com/q/java-program-to-implement-bresenham-line-algorithm_10993  
	 *   
	 * @return List
	 */
	
	static Set < Int2D > findLine( Int2D locFrom, Int2D locTo ) 
	{
		Set < Int2D > line = new HashSet < Int2D > ();
		
		int x0 = locFrom.x;
		int y0 = locFrom.y;
		int x1 = locTo.x;
		int y1 = locTo.y;

		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);

		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;

		int err = dx - dy;
		int e2;

		while ( true ) // endless loop
		{
			line.add( new Int2D( x0, y0 ) );

			if ( x0 == x1 && y0 == y1 ) break; // this is the only loop exit

			e2 = 2 * err;
			if ( e2 > -dy ) 
			{
				err = err - dy;
				x0 = x0 + sx;
			}

			if (e2 < dx) 
			{
				err = err + dx;
				y0 = y0 + sy;
			}
		}
		return line;
	}
	
	/**
	 * Inputs a List of Integer ages and returns a List wherein
	 * the indices represent start years and Integer amounts are
	 * histogram numbers of individuals per age.
	 */
	static List < Integer > makeAnnualHistoList ( List < Integer > ages, Integer maxAge )
	{
		List < Integer > histoList = new ArrayList < Integer >( maxAge );

		for (int i = 0; i < maxAge; i++) // Loop by age 
		{
			histoList.add( 0 ); // Initializes that List item
			for ( Integer age : ages )// Loop by List of ages present
			{
				if ( age == i )
				{
					int soFar = histoList.get( i );
					histoList.set( i, ++soFar ); // adds 1 to value before setting it back into the List slot
				}
			}
		}
		return histoList;
	}
	
	/**
	 * Shuffles any List using MersenneTwisterFast()
	 * This is needed because this random number generator 
	 * is not compatible with Collections, etc.
	 * It uses a Fisher�Yates shuffle. 
	 *  
	 * @param list
	 */
	static void shuffleList( List< ? > list, SimState state )
	{
	    int index;
	    MersenneTwisterFast random = state.random;
	    
	    for (int i = list.size() - 1; i > 0; i--)
	    {
	        index = random.nextInt( i + 1 );
	        Collections.swap( list, index, i);
	    }
	}

	static void warner( String s )
	{
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog( frame, s, "Note", JOptionPane.INFORMATION_MESSAGE ); 
	}
	
	static void warner( String s, boolean withSyso )
	{
		//if( withSyso ) System.out.println( s );
		warner( s );
	}
}
