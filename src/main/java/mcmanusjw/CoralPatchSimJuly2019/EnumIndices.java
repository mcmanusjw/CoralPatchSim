package mcmanusjw.CoralPatchSimJuly2019;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This seems redundant, but we use the list to set up the storage table including some of the output spreadsheet.
 * So, there is one enum type for each index. 
 * 
 * @author John McManus
 *
 */

enum EnumIndices 
{
	PERCENT_SIMILARITY_NUMBERS ( "PercentSimilarityNumbers" )
    { 
		public double calculate( List < Double > xList, List < Double > yList ) 
   	 	{ 
			return similarityIndex( xList, yList , "PS" ); 
   	 	}
    },
   
    PERCENT_SIMILARITY_AREA    ( "PercentSimilarityArea" )
    { 
		public double calculate( List < Double > xList, List < Double > yList ) 
   	 	{ 
			return similarityIndex( xList, yList , "PS" ); 
   	 	}
    },
    
    RUZICKA_SIMILARITY_NUMBERS ( "RuzickaSimilarityNumbers" )
    { 
 		public double calculate( List < Double > xList, List < Double > yList ) 
    	 	{ 
 			return similarityIndex( xList, yList , "RS" ); 
    	 	}
     },
    
    RUZICKA_SIMILARITY_AREA    ( "RuzickaSimilarityArea"  )
    { 
 		public double calculate( List < Double > xList, List < Double > yList ) 
    	 	{ 
 			return similarityIndex( xList, yList , "RS" ); 
    	 	}
     };

	String text;
	
	static Map < String, Integer > ordinalFieldTextMap = new HashMap < String, Integer > ();
	
	EnumIndices ( String text )
	{
		this.text = text;
	}

	public abstract double calculate( List < Double > xList, List < Double > yList );
	
	double similarityIndex( List<Double> xList, List<Double> yList, String kind ) 
	{
		if ( xList.size() != yList.size() )
		{
			UtilJM.warner( "The two arrays for index calculation are unequal in size.");
			return -1d;
		}
		
		double index  = 0d;
		double sumAll = 0d;
		double sumMin = 0d;
		double sumMax = 0d;
		
		for( int i = 0; i < xList.size(); i++ )
		{
			sumAll = sumAll + xList.get( i ) + yList.get( i );
			sumMin = sumMin + Math.min( xList.get( i ), yList.get( i ) );
			sumMax = sumMax + Math.max( xList.get( i ), yList.get( i ) );
		}
		
		if ( kind == "PS" ) // Percent similarity
		{
			index = 200 * ( sumMin / sumAll );
		}
		
		if ( kind == "RS" ) // Ruzicka similarity 
		{
			index = 100 * ( sumMin / sumMax );
		}
   
		return index;
	} 
	
	 public String getText()
	 {
	      return text;
	 }
	 

	 public static EnumIndices fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumIndices b : EnumIndices.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }

}

