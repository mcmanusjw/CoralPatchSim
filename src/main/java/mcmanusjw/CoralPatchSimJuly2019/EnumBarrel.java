package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

import static mcmanusjw.CoralPatchSimJuly2019.UtilJM.SEC10DEG; //double sec10Deg = 1d / cos( toRadians( 10d ) );
import static mcmanusjw.CoralPatchSimJuly2019.UtilJM.TAN10DEG; //double tan10Deg =      tan( toRadians( 10d ) );
import static mcmanusjw.CoralPatchSimJuly2019.UtilJM.COS20DEG; //double cos20Deg =      cos( toRadians( 20d ) );
import static mcmanusjw.CoralPatchSimJuly2019.UtilJM.SIN20DEG; //double sin20Deg =      sin( toRadians( 20d ) );

/**
 * 
 * 
 * @author John McManus
 *
 */

enum EnumBarrel 
{
	SURFACE      ( "Surface"   ),
	VOLUME       ( "Volume"    ),
	R_VOLUME     ( "R_Volume"  ),
	F_LENGTH     ( "F_Length"  ),
	N_HEIGHT     ( "N_Height"  ),
	R_VOL_TOP    ( "R_Vol_Top" ),
	F_LEN_TOP    ( "F_Len_Top" ),
	R_VOL_BOT    ( "R_Vol_Bot" ),
	F_LEN_BOT    ( "F_Len_Bot" );
	
	String text;
	
	EnumBarrel ( String text )
	{
		this.text = text;
	}
		
	// Use switch to pass parameters and diameter to desired formula copied from Mathematica notebooks (in comments above each formula
	double calculate ( double hmc, double dmc, double dw)
	{
		double value= 0;
		
		switch (this) 
	     {
	        case SURFACE: 
	        		
 	        		 // ( 1  / 100  ) * dw * Pi * ( Sqrt[ dw^2  ] * ( 7  * Sqrt[ 6 *  ( 5  + 2  * Sqrt[ 6  ] ) ] 
	        	value = ( 1d / 100d ) * dw * PI * ( sqrt( dw*dw ) * ( 7d * sqrt( 6d * ( 5d + 2d * sqrt( 6d ) ) )
	        	
	        	     // + Sec[ 10*Degree ] * ( 4 * Sqrt[ 3  ] - 3  * Tan[ 10*Degree ] ) ) 
	        			+ SEC10DEG         * ( 4 * sqrt( 3d ) - 3d * TAN10DEG         ) )
	        	
	        	     // + ( 1  / 3  ) * dw * ( 2  * Sqrt[ 3  ] - 3  * Tan[ 10*Degree ] ) ^2 )
	        			+ ( 1d / 3d ) * dw * ( 2d * sqrt( 3d ) - 3d * TAN10DEG         ) 
	        					           * ( 2d * sqrt( 3d ) - 3d * TAN10DEG         )    );
	        	return value;
	        	
	     	case VOLUME:     		
	     		
	     		//      ( dw^3     * Pi * Sec[ 10*Degree ]^2  * ( 39  + 8  * Sqrt[ 3  ] 
	     		value = ( dw*dw*dw * PI * SEC10DEG * SEC10DEG * ( 39d + 8d * sqrt( 3d ) 

	     		//      + ( 39  + 10  * Sqrt[ 3  ] ) * Cos[ 20*Degree ] + 6  * Sin[ 20*Degree ] ) ) / 2000
	     		        + ( 39d + 10d * sqrt( 3d ) ) * COS20DEG         + 6d * SIN20DEG         ) ) / 2000d; 

	     		return value;
	     		
	     	case R_VOLUME:
	     		
	     		//      ( dw^3     * Pi * ( 72  + Sec[ 10*Degree ] ^ 2 * ( 5  * Sqrt[ 3  ] 
	     		value = ( dw*dw*dw * PI * ( 72d + SEC10DEG * SEC10DEG  * ( 5d * sqrt( 3d )
	     		
	     		//      + 3  * Sqrt[ 3  ] * Cos[ 20*Degree ] - 6 * Sin[ 20*Degree ] ) ) ) / 2000d
	     				+ 3d * sqrt( 3d ) * COS20DEG         - 6 * SIN20DEG         ) ) ) / 2000d;
	     		
	     		return value;
	     		
	     	case F_LENGTH:	 
	     		
	     	    //     ( 1  / 2  ) * ( ( 3  * dw ) / 5  + Min[ ( Sqrt[ 3  ] * dw ) / 10   
	     		value= ( 1d / 2d ) * ( ( 3d * dw ) / 5d + min( ( sqrt( 3d ) * dw ) / 10d 
	     				
	     		//    , ( -( 2  / 5  ) ) * dw * ( -4  + Sqrt[ 3  ] * Tan[ 10*Degree ] ) ] )
	     		      , ( -2d / 5d     ) * dw * ( -4d + sqrt( 3d ) * TAN10DEG         ) ) );
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		//      ( 3  * dw ) / 10
	     		value = ( 3d * dw ) / 10d;
	     		
	     		return value;	
	     	
	     	case R_VOL_TOP:
	     		
	     		//      ( dw^3     * Pi * Sec[10*Degree]^2    * ( 5  * Sqrt[ 3  ] + 3  * Sqrt[ 3  ] * Cos[20*Degree]
	     		value = ( dw*dw*dw * PI * SEC10DEG * SEC10DEG * ( 5d * sqrt( 3d ) + 3d * sqrt( 3d ) * COS20DEG                                   
	     		
	     		//      - 6  * Sin[20*Degree] ) ) / 2000
	     		        - 6d * SIN20DEG       ) ) / 2000d;
	     		
	     		return value;
	     		
	     	case F_LEN_TOP:
	     		
	     	    //     Min[ ( Sqrt[ 3  ] * dw ) / 10  , ( -( 2 / 5 ) ) * dw * ( -4  + Sqrt[ 3  ] * Tan[10*Degree] ) ]
	     		value= min( ( sqrt( 3d ) * dw ) / 10  , (  -2d / 5d  ) * dw * ( -4d + sqrt( 3d ) * TAN10DEG       ) );
	     		
	     		return value;

	     	case R_VOL_BOT:
	     		
	     		//      ( 9  * dw^3     * Pi ) / 250
	     		value = ( 9d * dw*dw*dw * PI ) / 250d;  
	     		
	     		return value;
	     		
	     	case F_LEN_BOT:
	     		
	     		//     ( 3  * dw ) / 5
	     		value= ( 3d * dw ) / 5d;
	     		
	     		return value;	
	     		
	     		
            default:
            	UtilJM.warner( "Unknown operation in EnumBarrel" );
	              throw new AssertionError("Unknown operations" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumBarrel fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumBarrel b : EnumBarrel.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		UtilJM.warner( "Null in EnumBarrel" );

		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }
}
