package mcmanusjw.CoralPatchSimJuly2019;

import java.util.EnumMap;

/**
 * Simple wrapper for an EnumMap < EnumRunParams, String >
 * which makes putting and getting simple via getters and putters
 * which directly handle Strings, and make conversions
 * to and from Strings for boolean, double and int values. 
 * 
 * @author John McManus
 *
 */

class MapRunParams 
{
	private EnumMap < EnumRunParams, String > paramMap =
	        new EnumMap < EnumRunParams, String > ( EnumRunParams.class );
	
//  Initialize paramMap <--------- Do we need to do this?
/*	MapRunParams()
	{
		for( EnumRunParams field: EnumRunParams.values() )
		{
			paramMap.put( field, " " );
		}	
	}
*/	
	void put( String text, String value )
	{
		paramMap.put( EnumRunParams.fromString( text ), value );
	}

	void put( String text, boolean value )
	{
		String val = Boolean.toString( value );
		put( text, val );
	}
	
	void put( String text, int value )
	{
		String val = Integer.toString( value );
		put( text, val );
	}
	
	void put( String text, double value )
	{
		String val = Double.toString( value );
		put( text, val );
	}
	
	String getString( String text )
	{
		return paramMap.get( EnumRunParams.fromString( text ) );
	}
	
	boolean getBoolean( String text )
	{
		return Boolean.parseBoolean( getString( text ) );
	}
	
	int getInt( String text )
	{
		return Integer.parseInt( getString( text ) );
	}
	
	double getDouble( String text )
	{
		return Double.parseDouble( getString( text ) );
	}
}
