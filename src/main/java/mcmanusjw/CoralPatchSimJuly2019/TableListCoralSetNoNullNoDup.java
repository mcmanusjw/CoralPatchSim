package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This cannot hold nulls nor duplicates,
 * because they cannot be added.
 * 
 * @author John McManus
 *
 */

class TableListCoralSetNoNullNoDup 
{
	final List < HashSet < StepCoral > > table; 
	 
	TableListCoralSetNoNullNoDup( int numRows )
	{
		table = new ArrayList < HashSet < StepCoral > > ( numRows );
		for ( int i = 0; i < numRows; i++ )
		{
			HashSet < StepCoral > set = new HashSet < StepCoral > ();
			table.add( set );
		}
	}
	
	/**
	 * Adds a coral only if not null and not already present
	 * among all the HashTables.
	 * 
	 * @param rowIndex
	 * @param stepCoral
	 * @return
	 */
	
	boolean add( int rowIndex, StepCoral stepCoral )
	{
		boolean coralAdded = false;
		if ( ( stepCoral != null ) && ( !containsValue ( stepCoral ) ) )
		{
			// the HashSet will return true if coral is added
			coralAdded = table.get( rowIndex ).add( stepCoral );
		}
		return coralAdded;
	}
	
	void clear()
	{
		for( HashSet < StepCoral > set : table)
		{
			set.clear();
		}
	}
	
	boolean containsValue( StepCoral stepCoral )
	{
		boolean foundAtLeastOne = false;
		for( HashSet < StepCoral > set : table)
		{
			if ( set.contains( stepCoral ) )
			{
				foundAtLeastOne = true;
				break;
			}
		}
		return foundAtLeastOne;
	}
	
	int countValues( StepCoral stepCoral )
	{
		int count = 0;
		for( HashSet < StepCoral > set : table)
		{
			if ( set.contains( stepCoral ) ) count++;
		}
		return count;
	}
	
	Set < StepCoral > getAllCorals()
	{
		Set < StepCoral > combinedSet = new HashSet < StepCoral > (); 
		for( HashSet < StepCoral > set : table)
		{
			combinedSet.addAll( set );
		}
		return combinedSet;
	}
	
	StepCoral getOneCoral( int rowIndex )
	{
		return table.get( rowIndex ).iterator().next();
	}
	
	boolean isEmpty( int rowIndex )
	{
		return table.get( rowIndex ).isEmpty();
	}

	int numSets ()
	{
		return table.size();
	}
	
//	void printTableToScreen()
//	{
//		System.out.println( "\n Table follows:" );
//		for ( HashSet < StepCoral > set : table )
//		{
//			for ( StepCoral stepCoral : set )
//			{
//				String output = stepCoral.getCoralId() + ", ";
//				System.out.print( output );
//			}
//			System.out.println();
//		}
//		System.out.println();
//	}

	boolean remove( int rowIndex, StepCoral stepCoral )
	{
		// HashSet returns a boolean for success or not
		return table.get( rowIndex ).remove( stepCoral );
	}
	
	boolean removeAll( StepCoral stepCoral )
	{
		boolean foundAtLeastOne = false;
		for( HashSet < StepCoral > set : table)
		{
			// remove() returns true if found and removed
			if ( set.remove( stepCoral ) )
			{
				foundAtLeastOne = true;
				break; // Only one instance was allowed in anyway
			}
		}
		return foundAtLeastOne;
	}
	
	Set < StepCoral > row( int rowIndex )
	{
		return table.get( rowIndex );
	}
	
	/**
	 * @return
	 */
	Set < StepCoral > uniqueValues()
	{
		Set < StepCoral > allSetValues = new HashSet < StepCoral > ();
		for( HashSet < StepCoral > set : table)
		{
			allSetValues.addAll( set );
		}
		return allSetValues;
	}
	
}
