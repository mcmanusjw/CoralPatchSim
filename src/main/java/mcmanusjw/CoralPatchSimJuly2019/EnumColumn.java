package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

enum EnumColumn 
{
	// Should not put species specific values here because this is only a growth form, not a species
	SURFACE  ( "Surface"  ),
	VOLUME   ( "Volume"   ),
	R_VOLUME ( "R_Volume" ),
	F_LENGTH ( "F_Length" ),
	N_HEIGHT ( "N_Height" );
	
	String text;
	
	EnumColumn ( String text )
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
	        	
	        	//      ( 1 /4  ) * dw^2  * Pi * ( 1  + ( 2  * Min[ dw/2,  hmc ] ) 
	        	value = ( 1d/4d ) * dw*dw * PI * ( 1d + ( 2d * min( dw/2d, hmc ) )
	        			
	    	    //      / Max[ 1,   ( dmc * ( dw - Min[ dw/2,   hmc ] ) * Min[ dw/2,   hmc ] ) / ( dw * hmc ) ] ) 
	        			/ max( 1d,  ( dmc * ( dw - min( dw/2d,  hmc ) ) * min( dw/2d,  hmc ) ) / ( dw * hmc ) ) );
	        	
	        	return value;
	        	
	     	case VOLUME:
	     		
	     		//      (1/48)   * Pi * ( dw^3     + 6  * dw^2  * Max[ 1,  ( dmc * ( dw - Min[ dw/2,  hmc ] ) * 
	     		value = (1d/48d) * PI * ( dw*dw*dw + 6d * dw*dw * max( 1d, ( dmc * ( dw - min( dw/2d, hmc ) ) * 

	     		//       Min[ dw/2,  hmc ] ) / ( dw * hmc ) ] -    ( dw - 2  * Min[ dw/2,  hmc ] )^3 )
	     				 min( dw/2d, hmc ) ) / ( dw * hmc ) ) - pow( dw - 2d * min( dw/2d, hmc ), 3d ) ); 

	     		return value;
	     		
	     	case R_VOLUME:
	     		
	     		//      (1/24)   * Pi * ( -3  * dw^2  * Max[ 1,  ( dmc * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw/2,  hmc ] )
	     		value = (1d/24d) * PI * ( -3d * dw*dw * max( 1d, ( dmc * ( dw - min( dw/2d, hmc ) ) * min( dw/2d, hmc ) ) 

	    	    //      / ( dw * hmc ) ] + Min[ dw/2,  hmc ] * ( 3  * dw^2  - 6  * dw * Min[ dw/2,  hmc ] + 4  *      Min[ dw/2,  hmc ]^2    ) )
	     				/ ( dw * hmc ) ) + min( dw/2d, hmc ) * ( 3d * dw*dw - 6d * dw * min( dw/2d, hmc ) + 4d * pow( min( dw/2d, hmc ), 2d ) ) );
	     		
	     		return value;
	     		
	     	case F_LENGTH:
	     		
	     	    //     4  * Max[ 1,  ( dmc * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw/2,  hmc ] ) / ( dw * hmc ) ]
	     		value= 4d * max( 1d, ( dmc * ( dw - min( dw/2d, hmc ) ) * min( dw/2d, hmc ) ) / ( dw * hmc ) );
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		//      (1/2) * Min[ dw/2, hmc ]
	     		value = (1d/2d) * min( dw/2d, hmc );
	     		
	     		return value;	    
	     		
            default:
	              throw new AssertionError("Unknown operations" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumColumn fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumColumn b : EnumColumn.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }
}
