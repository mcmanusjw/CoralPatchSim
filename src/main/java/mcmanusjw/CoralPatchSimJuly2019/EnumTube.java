package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;

enum EnumTube 
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
	
	EnumTube ( String text )
	{
		this.text = text;
	}
		
	// Use switch to pass parameters and diameter to desired formula copied from Mathematica notebooks (in comments above each formula)
	double calculate ( double hmc, double dmc, double dw)
	{
		double value= 0;
		
		switch (this) 
	     {
	        case SURFACE: 
	        	
 	        	//      ( dw * Pi * ( ( 16  + 9  * dw ) * Max[ 1 , ( dmc * ( dw - Min[ dw / 2 , hmc ] ) * Min[ dw / 2 , hmc ] )
	        	value = ( dw * PI * ( ( 16d + 9d * dw ) * max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) )
	        	
	        	//    / ( dw * hmc ) ] + 16  * dw * Min[ dw / 2 , hmc ] + 3  * dw
	        		  / ( dw * hmc ) ) + 16d * dw * min( dw / 2d, hmc ) + 3d * dw 	
	        	
	        	//    * Sqrt[ Max[ 1 , ( dmc * ( dw - Min[ dw / 2 , hmc ] ) * Min[ dw / 2 , hmc ] ) / ( dw * hmc ) ]^2 
	                  * sqrt( max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) ) / ( dw * hmc ) )
	                 		* max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) ) / ( dw * hmc ) )
	        	
	           //      + ( 9  * ( dw - Min[ dw / 2 , hmc ] )^2           
	                   + ( 9d * ( dw - min( dw / 2d, hmc ) ) 
	                		  * ( dw - min( dw / 2d, hmc ) )
	                
	            //     * Min[ dw / 2 , hmc ]^2                     ) / dw^2    ] ) ) 
	                   * min( dw / 2d, hmc ) * min( dw / 2d, hmc ) ) / (dw*dw) ) ) ) 
	        	
	        	//     / ( 32  * Max[ 1 , ( dmc * ( dw - Min[ dw / 2 , hmc ] ) * Min[ dw / 2 , hmc ] ) / ( dw * hmc ) ] )
	         	       / ( 32d * max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) ) / ( dw * hmc ) ) );

	        	 return value;
	        	
	     	case VOLUME:     		
	     		
	     		//      ( 1  / 128  ) * dw * Pi * ( 16  * dw + 9  * ( dw - Min[ dw / 2 , hmc ] ) * Min[ dw / 2 , hmc ] )
	     		value = ( 1d / 128d ) * dw * PI * ( 16d * dw + 9d * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) ); 

	     		return value;
	     		
	     	case R_VOLUME:
	     		
	     		//      ( 1  / 384  ) * Pi * ( -48  * dw^2  * Max[ 1 , ( dmc * ( dw - Min[ dw / 2 , hmc ] ) * Min[ dw / 2 , hmc ] )
	     		value = ( 1d / 384d ) * PI * ( -48d * dw*dw * max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) )	
	            
	     		//      / ( dw * hmc ) ] + Min[ dw / 2 , hmc ] * ( 69  * dw^2  - 117  * dw * Min[ dw / 2 , hmc ] 
	     				/ ( dw * hmc ) ) + min( dw / 2d, hmc ) * ( 69d * dw*dw - 117d * dw * min( dw / 2d, hmc )
	            
	     		//      + 64  * Min[ dw / 2 , hmc ] ^2                    ) ) 
	                    + 64d * min( dw / 2d, hmc ) * min( dw / 2d, hmc ) ) );       		 
	     		
	     		return value;
	     		
	     	case F_LENGTH:	 
	     		
	     	    //     ( 7  / 2  ) * Max[ 1 , ( dmc * ( dw - Min[ dw / 2 , hmc ] ) * Min[ dw / 2 , hmc ] ) / ( dw * hmc ) ]
	     		value= ( 7d / 2d ) * max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) ) / ( dw * hmc ) );
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		//      ( 1  / 2  ) * Min[ dw / 2 , hmc ]
	     		value = ( 1d / 2d ) * min( dw / 2d, hmc );
	     		
	     		return value;	
	     	
	     	case R_VOL_TOP:
	     		
	     		//      ( 7  / 128  ) * dw * Pi * ( dw - Min[ dw / 2 , hmc ] ) * Min[ dw / 2 , hmc ]
	     		value = ( 7d / 128d ) * dw * PI * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc );
	     		
	     		return value;
	     		
	     	case F_LEN_TOP:
	     		
	     	    //     3    Max[ 1 , ( dmc * ( dw - Min[ dw / 2 , hmc ] ) * Min[ dw / 2 , hmc ] ) / ( dw * hmc ) ]
	     		value= 3d * max( 1d, ( dmc * ( dw - min( dw / 2 , hmc ) ) * min( dw / 2d, hmc ) ) / ( dw * hmc ) );
	     		
	     		return value;

	     	case R_VOL_BOT:
	     		
	     		//      ( 1  / 24  ) * Pi * ( -3  * dw^2  * Max[ 1 , ( dmc * ( dw - Min[ dw / 2 , hmc ] )
	     		value = ( 1d / 24d ) * PI * ( -3d * dw*dw * max( 1d, ( dmc * ( dw - min( dw / 2d , hmc ) )
	     		
	     		//      * Min[ dw / 2 , hmc ] ) / ( dw * hmc ) ] + Min[ dw / 2 , hmc ]
	     				* min( dw / 2d, hmc ) ) / ( dw * hmc ) ) + min( dw / 2d, hmc )
	     		
	     		//      * ( 3  * dw^2   - 6  * dw * Min[ dw / 2 , hmc ] + 4  * Min[ dw / 2 , hmc ]^2  ) )
         				* ( 3  * dw*dw  - 6d * dw * min( dw / 2d, hmc ) + 4d * min( dw / 2d, hmc )  
	     				                                                     * min( dw / 2d, hmc )    ) ); 	
	     		return value;
	     		
	     	case F_LEN_BOT:
	     		
	     		//     4 * Max[ 1 , ( dmc * ( dw - Min[ dw / 2 , hmc ] ) * Min[ dw / 2 , hmc ] ) / ( dw * hmc ) ]
	     		value= 4 * max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) ) / ( dw * hmc ) );
	     		
	     		return value;	
	     		
	     		
            default:
            	UtilJM.warner( "Unknown operation in EnumTube" );
	              throw new AssertionError("Unknown operations" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumTube fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumTube b : EnumTube.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		UtilJM.warner( "Null in EnumBarrel" );

		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }

}
