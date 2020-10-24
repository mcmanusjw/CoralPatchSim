package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

enum EnumColumnPlate 
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
	
	EnumColumnPlate ( String text )
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
	        	
	        	//      ( 1  / 160  ) * dw^2  * Pi * ( 53  + ( 40  * Min[ dw/2,  hmc ] )	            
	        	value = ( 1d / 160d ) * dw*dw * PI * ( 53d + ( 40d * min( dw/2d, hmc ) )
	        			
	    	    //      / Max[ 1, ( dmc * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw/2,  hmc ] ) / ( dw * hmc ) ] )  
	        			/ max( 1, ( dmc * ( dw - min( dw/2d, hmc ) ) * min( dw/2d, hmc ) ) / ( dw * hmc ) ) ); // keep the 1 as int
	        	
	        	return value;
	        	
	     	case VOLUME: // Simpler than for column because we here omit the horizontal branches projected to the base as unecessary	     		
	     		
	     		//      ( 1  / 480  ) * Pi * ( 6  * dw^3     + 5  * ( dw^3       
	     		value = ( 1d / 480d ) * PI * ( 6d * dw*dw*dw + 5d * ( dw*dw*dw
	     				
	      		//      - ( dw - 2 * Min[ dw/2, hmc ] )^3  ) )
	     				- ( dw - 2d * min( dw/2d, hmc ) ) 
	     				* ( dw - 2d * min( dw/2d, hmc ) ) 
	     				* ( dw - 2d * min( dw/2d, hmc ) )  ) );


	     		return value;
	     		
	     	case R_VOLUME:
	     		
	     		//      ( 1  / 1728  ) * Pi * ( 7  * dw^3     - 108  * dw^2    	     	               
	     		value = ( 1d / 1728d ) * PI * ( 7d * dw*dw*dw - 108  * dw*dw    

	    	    //       * Max[ 1, ( dmc * ( dw - Min[ dw / 2 , hmc ] ) * Min[ dw / 2 , hmc ] ) / ( dw * hmc ) ] 
	     				 * max( 1, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) ) / ( dw * hmc ) ) // keep the 1 as int
	     				 
	     	    //	     + 36  * Min[ dw / 2  , hmc ] * ( 3  * dw^2  - 6  * dw * Min[ dw / 2 , hmc ]
	     				 + 36d * min( dw / 2d , hmc ) * ( 3d * dw*dw - 6d * dw * min( dw / 2d, hmc )	     		
	     		       
	     		//		 + 4  * Min[ dw / 2 , hmc ] ^ 2                   ) )
	     				 + 4d * min( dw / 2d, hmc ) * min( dw / 2d, hmc ) ) ); 
	     		
	     		return value;
	     		
	     	case F_LENGTH:
	     		
	     	    //     ( Sqrt[ 7  ] * dw ) / 8  + 2  * Max[ 1 , ( dmc * ( dw - Min[ dw / 2 , hmc ] ) 
	     		value= ( sqrt( 7d ) * dw ) / 8d + 2d * max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) 
	     		
	     		//     * Min[ dw / 2 , hmc ] ) / ( dw * hmc ) ]
	     			   * min( dw / 2d, hmc ) ) / ( dw * hmc ) );	     		
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		//      ( 1 / 2 ) * Min[ dw/2 , hmc ]
	     		value = ( 1d/2d ) * min( dw/2d, hmc );
	     		
	     		return value;	
	     	
	     	case R_VOL_TOP:
	     		
	     		//      ( 1  / 48  ) * Pi * ( -3  * dw^2  * Max[ 1,  ( dmc * ( dw - Min[ dw/2, hmc ] ) * Min[ dw/2, hmc ] ) / ( dw * hmc ) ] 
	     		value = ( 1d / 48d ) * PI * ( -3d * dw*dw * max( 1d, ( dmc * ( dw - min( dw/2, hmc ) ) * min( dw/2, hmc ) ) / ( dw * hmc ) ) 

	    	    //      + Min[ dw/2d, hmc ] * ( 3  * dw^2  - 6  * dw * Min[ dw/2 , hmc ] + 4  
	     				+ min( dw/2d, hmc ) * ( 3d * dw*dw - 6d * dw * min( dw/2d, hmc ) + 4d
	     		
	     		//      * Min[ dw/2 , hmc ]^2                 ) )
	     		        * min( dw/2d, hmc ) * min( dw/2d, hmc ) ) ); 
	     		
	     		return value;
	     		
	     	case F_LEN_TOP:
	     		
	     	    //     4  * Max[ 1, ( dmc * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw/2,  hmc ] ) / ( dw * hmc ) ]
	     		value= 4d * max( 1, ( dmc * ( dw - min( dw/2d, hmc ) ) * min( dw/2d, hmc ) ) / ( dw * hmc ) ); // keep the 1 as int
	     		
	     		return value;

	     	case R_VOL_BOT:
	     		
	     		//      ( 7 * dw^3     * Pi ) / 1728
	     		value = ( 7 * dw*dw*dw * PI ) / 1728d;  
	     		
	     		return value;
	     		
	     	case F_LEN_BOT:
	     		
	     	    //     ( Sqrt[ 7  ] * dw ) / 4 
	     		value= ( sqrt( 7d ) * dw ) / 4d;
	     		
	     		return value;	
	     		
	     		
            default:
	              throw new AssertionError("Unknown operations" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumColumnPlate fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumColumnPlate b : EnumColumnPlate.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }
}
