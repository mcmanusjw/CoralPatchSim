package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

class TableOutputListEnumMapsDouble 
{
	final List < EnumMap < EnumOutputFields, Double > > table; 

	TableOutputListEnumMapsDouble( int numRows )
	{
		table = new ArrayList < EnumMap < EnumOutputFields, Double > > ( ); 
		
		for ( int i = 0; i < numRows; i++ )
		{
			EnumMap < EnumOutputFields, Double > map = new EnumMap < EnumOutputFields, Double > ( EnumOutputFields.class );
			
			for( EnumOutputFields field: EnumOutputFields.values() )
			{
				map.put( field, 0.0 );
			}
			
			table.add( map );
		}
	}
	
	boolean addIn( int rowIndex, String text, Double value )
	{
		Double oldValue = getValue( rowIndex, text );
		if ( oldValue == null ) oldValue = 0d; //<<<<<<<should not need this >
		Double newValue = oldValue + value;
		
		//This returns true if put was successful
		return put( rowIndex, text, newValue );
	}
	
	Double getValue( int rowIndex, String text )
	{
		EnumOutputFields field = EnumOutputFields.fromString(text);
		return table.get( rowIndex ).get( field );
	}
	
	boolean put( int rowIndex, String text, Double value )
	{
		boolean valueAdded = false;

		if( ( rowIndex <= table.size() ) )
		{
			EnumOutputFields field = EnumOutputFields.fromString(text);
			table.get( rowIndex ).put( field, value );
			valueAdded = true;
		}
		return valueAdded;
	}
	
	boolean put( int rowIndex, String text, int intVal )
	{
		Double value = ( double ) intVal;
		return put( rowIndex, text, value );
	}
	
	List < Double > getAllValuesAsList( )
	{
		List < Double > list = new ArrayList < Double >(); 
		
		for( EnumMap < EnumOutputFields, Double > map : table )
		{
			list.addAll( map.values() );
		}
		return list;
	}
	
	EnumMap < EnumOutputFields, Double > getMap( int index )
	{
		return table.get(index);
	}
	
	void clear()
	{
		for( EnumMap < EnumOutputFields, Double > map : table )
		{
			map.clear();
		}
	}
	
	boolean isEmpty( int rowIndex )
	{
		return table.get( rowIndex ).isEmpty();
	}

	int numMaps ()
	{
		return table.size();
	}
	
	EnumMap < EnumOutputFields, Double > map( int rowIndex )
	{
		return table.get( rowIndex );
	}
	
	Double getValue( int index, EnumOutputFields field )
	{
		return map( index ).get( field );
	}
	
	List < Double > getAllValuesForField ( EnumOutputFields field )
	{
		List < Double > valuesBySpecies = new ArrayList < Double >();
		for( EnumMap < EnumOutputFields, Double > map : table )
		{
			valuesBySpecies.add( map.get( field ));
		}
		return valuesBySpecies;
	}
	
	List < Double > getAllValuesForFieldByText ( String text )
	{

		EnumOutputFields field = EnumOutputFields.fromString( text );
		return getAllValuesForField( field );
	}
	
	/**
	 * Use this to test table of enums
	 */
//	void printAll()
//	{
//		for( EnumMap < EnumOutputFields, Double > map : table )
//		{
//			System.out.println( map );
//		}
//	}

	/**
	 * Use this to test table of enums
	 */
	void printAllTextValuePairs()
	{
		for( EnumMap < EnumOutputFields, Double > map : table )
		{
			for( EnumOutputFields field : map.keySet() )
			{
				System.out.print( field.text + ", " + map.get( field ) + " ; " );
			}
			System.out.println();
		}
	}
}
