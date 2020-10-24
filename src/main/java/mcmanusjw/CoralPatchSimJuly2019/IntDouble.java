package mcmanusjw.CoralPatchSimJuly2019;

/**
 * Simple class for storing number pairs
 * each consisting of an int and a double. 
 * @author John McManus
 *
 */
class IntDouble 
{
	int    x;
	double y;
	
	IntDouble( int xIn, double yIn )
	{
		x = xIn;
		y = yIn;
	}
 
	@Override
	public String toString()
	{
		return ( "[" + x + ", " + y + "]" );
	}
	
	@Override
	public boolean equals( Object o ) 
	{
	    if (o == this) return true;
	    if (!(o instanceof IntDouble)) return false;
	    IntDouble iD = (IntDouble) o;
	    return ( iD.x == x ) && ( iD.y == y );
	}

	@Override
    public int hashCode() 
	{
	    int result = 17;
	    result = 31 * result + (int) x + (int) y;
	    return result;
	}
}
