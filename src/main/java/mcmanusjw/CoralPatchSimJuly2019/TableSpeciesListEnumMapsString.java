package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

class TableSpeciesListEnumMapsString 
{
	final List < EnumMap < EnumSpeciesInputField, String > > table; 
	 
	/**
	 * 
	 * @param numRows
	 */
	TableSpeciesListEnumMapsString( int numRows )
	{
		table = new ArrayList < EnumMap < EnumSpeciesInputField, String > > ( ); 
		for ( int i = 0; i < numRows; i++ )
		{
			EnumMap < EnumSpeciesInputField, String > map = 
					new EnumMap < EnumSpeciesInputField, String > ( EnumSpeciesInputField.class );
			for( EnumSpeciesInputField field: EnumSpeciesInputField.values() )
			{
				map.put( field, " " );
			}
			
			table.add( map );
		}
	}
	
	String getString( int rowIndex, String text )
	{
		EnumSpeciesInputField field = EnumSpeciesInputField.fromString(text);
		return table.get( rowIndex ).get( field );
	}
	
	double getDouble( int rowIndex, String text )
	{
		return Double.parseDouble( getString( rowIndex, text ) );
	}

	int getInt( int rowIndex, String text )
	{
		//return Integer.parseInt( getString( rowIndex, text ) );
		return ( int ) Double.parseDouble( getString( rowIndex, text ) );
	}
	
	boolean put( int rowIndex, String text, String value )
	{
		boolean valueAdded = false;
		
		if( ( rowIndex <= table.size() ) )
		{
			EnumSpeciesInputField field = EnumSpeciesInputField.fromString(text);
			table.get( rowIndex ).put( field, value );
			valueAdded = true;
		}
		return valueAdded;
	}
	
	EnumMap < EnumSpeciesInputField, String > getMap( int index )
	{
		return table.get(index);
	}
	
	void clear()
	{
		for( EnumMap < EnumSpeciesInputField, String > map : table )
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
	
	EnumMap < EnumSpeciesInputField, String > map( int rowIndex )
	{
		return table.get( rowIndex );
	}
	
//	void printAll()
//	{
//		for( EnumMap < EnumSpeciesInputField, String > map : table )
//		{
//			System.out.println( map );
//		}
//	}
	
//	void printAllTextValuePairs()
//	{
//		for( EnumMap < EnumSpeciesInputField, String > map : table )
//		{
//			for( EnumSpeciesInputField field : map.keySet() )
//			{
//				System.out.print( field.text + ", " + map.get( field ) + " ; " );
//			}
//			System.out.println();
// 		}
//	}
	
	String getValue( int index, EnumSpeciesInputField field )
	{
		return map( index ).get( field );
	}
	
	List < String > getAllStringValuesForString ( String text )
	{
		List < String > valuesBySpecies = new ArrayList < String >();
		for( EnumMap < EnumSpeciesInputField, String > map : table )
		{
			EnumSpeciesInputField field = EnumSpeciesInputField.fromString( text );
			valuesBySpecies.add( map.get( field ));
		}
		return valuesBySpecies;
	}

	List < Double > getAllDoubleValuesForString ( String text )
	{
		List < Double > valuesBySpecies = new ArrayList < Double >();
		for( EnumMap < EnumSpeciesInputField, String > map : table )
		{
			EnumSpeciesInputField field = EnumSpeciesInputField.fromString( text );
			//System.out.println( text + " " +  field + " " + Double.parseDouble( map.get( field ) ) ); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			valuesBySpecies.add( Double.parseDouble( map.get( field ) ) );
		}
		return valuesBySpecies;
	}
	
	List < Integer > getAllIntegerValuesForString ( String text )
	{
		List < Integer > valuesBySpecies = new ArrayList < Integer >();
		for( EnumMap < EnumSpeciesInputField, String > map : table )
		{
			EnumSpeciesInputField field = EnumSpeciesInputField.fromString( text );
			valuesBySpecies.add( Integer.parseInt( map.get( field ) ) );
		}
		return valuesBySpecies;
	}
}
