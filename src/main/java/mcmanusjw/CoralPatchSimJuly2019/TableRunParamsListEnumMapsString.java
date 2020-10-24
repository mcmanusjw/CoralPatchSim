package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

class TableRunParamsListEnumMapsString 
{
	final List < EnumMap < EnumRunParams, String > > table; 
	 
	/**
	 * 
	 * @param numRows
	 */
	TableRunParamsListEnumMapsString( int numRows )
	{
		table = new ArrayList < EnumMap < EnumRunParams, String > > ( ); 
		for ( int i = 0; i < numRows; i++ )
		{
			EnumMap < EnumRunParams, String > map = 
					new EnumMap < EnumRunParams, String > ( EnumRunParams.class );
			for( EnumRunParams field: EnumRunParams.values() )
			{
				map.put( field, " " );
			}
			
			table.add( map );
		}
	}
	
	void clear()
	{
		for( EnumMap < EnumRunParams, String > map : table )
		{
			map.clear();
		}
	}
	
	List < Double > getAllDoubleValuesForString ( String text )
	{
		List < Double > valuesBySpecies = new ArrayList < Double >();
		for( EnumMap < EnumRunParams, String > map : table )
		{
			EnumRunParams field = EnumRunParams.fromString( text );
			valuesBySpecies.add( Double.parseDouble( map.get( field ) ) );
		}
		return valuesBySpecies;
	}

	List < Integer > getAllIntegerValuesForString ( String text )
	{
		List < Integer > valuesBySpecies = new ArrayList < Integer >();
		for( EnumMap < EnumRunParams, String > map : table )
		{
			EnumRunParams field = EnumRunParams.fromString( text );
			valuesBySpecies.add( Integer.parseInt( map.get( field ) ) );
		}
		return valuesBySpecies;
	}
	
	List < String > getAllStringValuesForString ( String text )
	{
		List < String > valuesBySpecies = new ArrayList < String >();
		for( EnumMap < EnumRunParams, String > map : table )
		{
			EnumRunParams field = EnumRunParams.fromString( text );
			valuesBySpecies.add( map.get( field ));
		}
		return valuesBySpecies;
	}
	
	public boolean getBoolean( int rowIndex, String text ) 
	{
		return Boolean.parseBoolean( getString( rowIndex, text ) );
	}
	
	double getDouble( int rowIndex, String text )
	{
		return Double.parseDouble( getString( rowIndex, text ) );
	}
	
	int getInt( int rowIndex, String text )
	{
		return Integer.parseInt( getString( rowIndex, text ) );
		//return ( int ) Double.parseDouble( getString( rowIndex, text ) );
	}

	EnumMap < EnumRunParams, String > getMap( int index )
	{
		return table.get(index);
	}
	
	String getString( int rowIndex, String text )
	{
		EnumRunParams field = EnumRunParams.fromString(text);
		return table.get( rowIndex ).get( field );
	}
	
	String getValue( int index, EnumRunParams field )
	{
		return map( index ).get( field );
	}
	
	boolean isEmpty( int rowIndex )
	{
		return table.get( rowIndex ).isEmpty();
	}
	
	EnumMap < EnumRunParams, String > map( int rowIndex )
	{
		return table.get( rowIndex );
	}
	
	int numMaps ()
	{
		return table.size();
	}

//	void printAll()
//	{
//		for( EnumMap < EnumRunParams, String > map : table )
//		{
//			System.out.println( map );
//		}
//	}
	
//	void printAllTextValuePairs()
//	{
//		for( EnumMap < EnumRunParams, String > map : table )
//		{
//			for( EnumRunParams field : map.keySet() )
//			{
//				System.out.print( field.text + ", " + map.get( field ) + " ; " );
//			}
//			System.out.println();
//		}
//	}

	boolean put( int rowIndex, String text, String value )
	{
		boolean valueAdded = false;
		
		if( ( rowIndex <= table.size() ) )
		{
			EnumRunParams field = EnumRunParams.fromString(text);
			table.get( rowIndex ).put( field, value );
			valueAdded = true;
		}
		return valueAdded;
	}
	
	boolean put( int rowIndex, String text, boolean value )
	{
		boolean valueAdded = false;
		
		if( ( rowIndex <= table.size() ) )
		{
			put( rowIndex, text, Boolean.toString( value ) );
			valueAdded = true;
		}
		return valueAdded;
	}
	
	boolean put( int rowIndex, String text, double value )
	{
		boolean valueAdded = false;
		
		if( ( rowIndex <= table.size() ) )
		{
			put( rowIndex, text, Double.toString( value ) );
			valueAdded = true;
		}
		return valueAdded;
	}
	
	boolean put( int rowIndex, String text, int value )
	{
		boolean valueAdded = false;
		
		if( ( rowIndex <= table.size() ) )
		{
			put( rowIndex, text, Integer.toString( value ) );
			valueAdded = true;
		}
		return valueAdded;
	}
}
