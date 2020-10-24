package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

enum EnumColumnEncrusting 
{
	SURFACE  ( "Surface"  ),
	VOLUME   ( "Volume"   ),
	R_VOLUME ( "R_Volume" ),
	F_LENGTH ( "F_Length" ),
	N_HEIGHT ( "N_Height" );
	
	String text;
	
	EnumColumnEncrusting ( String text )
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
	        	
	        	//      ( 1  / 200  )   * dw^2  * Pi * ( 51  + ( 50  * Min[ dw/2,  hmc ] ) / 
	        	value = ( 1d / 200d )   * dw*dw * PI * ( 51d + ( 50d * min( dw/2d, hmc ) ) /
	        			
	    	    //      Max[ 1,  ( dmc * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw/2,  hmc ] ) / ( dw * hmc ) ] ) 
	        			max( 1d, ( dmc * ( dw - min( dw/2d, hmc ) ) * min( dw/2d, hmc ) ) / ( dw * hmc ) ) );
	        	
	        	return value;
	        	
	     	case VOLUME: // Simpler than for column because we here omit the horizontal branches projected to the base as unecessary	     		
	     		
	     		//      ( dw^3     * Pi ) / 400  + ( 1  / 96  ) * Pi * ( dw^3     -      ( dw - 2  * Min[ dw/2, hmc ] )^3     ) 
	     		value = ( dw*dw*dw * PI ) / 400d + ( 1d / 96d ) * PI * ( dw*dw*dw - pow( ( dw - 2d * min( dw/2d, hmc ) ), 3d) ); 


	     		return value;
	     		
	     	case R_VOLUME:
	     		
	     		//      ( 1  / 48  ) * Pi * ( -3  * dw^2  * Max[ 1,  ( dmc * ( dw - Min[ dw/2, hmc ] ) * Min[ dw/2, hmc ] ) / ( dw * hmc ) ] + 
	     		value = ( 1d / 48d ) * PI * ( -3d * dw*dw * max( 1d, ( dmc * ( dw - min( dw/2, hmc ) ) * min( dw/2, hmc ) ) / ( dw * hmc ) ) + 

	    	    //      Min [ dw/2d, hmc ] * ( 3  * dw^2  - 6  * dw * Min[ dw/2 , hmc ] + 4  *      Min[ dw/2 , hmc ]^2    ) )
	     				min ( dw/2 , hmc ) * ( 3d * dw*dw - 6d * dw * min( dw/2d, hmc ) + 4d * pow( min( dw/2d, hmc ), 2d ) ) );
	     		
	     		return value;
	     		
	     	case F_LENGTH:
	     		
	     	    //     4  * Max[ 1,  ( dmc * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw/2,  hmc ] ) / ( dw * hmc ) ]
	     		value= 4d * max( 1d, ( dmc * ( dw - min( dw/2d, hmc ) ) * min( dw/2d, hmc ) ) / ( dw * hmc ) );
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		//      ( 1 / 2 ) * Min[ dw/2 , hmc ]
	     		value = ( 1d/2d ) * min( dw/2d, hmc );
	     		
	     		return value;	    
	     		
            default:
	              throw new AssertionError("Unknown operations" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumColumnEncrusting fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumColumnEncrusting b : EnumColumnEncrusting.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }

}
