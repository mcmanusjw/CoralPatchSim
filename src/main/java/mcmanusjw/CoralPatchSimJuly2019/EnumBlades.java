package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

enum EnumBlades 
{
	// Change name to 'EnumBlades'
	// Should not put species specific values here because this is only a growth form, not a species
	SURFACE  ( "Surface"  ),
	VOLUME   ( "Volume"   ),
	R_VOLUME ( "R_Volume" ),
	F_LENGTH ( "F_Length" ),
	N_HEIGHT ( "N_Height" );
	
	String text;
	
	EnumBlades ( String text )
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
	        	
	        	//      ( dw * ( dw * Max[ 1,  ( dmc * ( dw - Min[ dw / 2,  hmc ] ) * Min[ dw/2, hmc ] )  / ( dw * hmc ) ] +
	        	//      ( dw * ( dw * Max[ 1,  ( dmc * ( dw - Min[ dw / 2,  hmc ] ) * Min[ dw/2,  hmc ] ) / ( dw * hmc ) ] + 
	        	value = ( dw * ( dw * max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw/2d, hmc ) ) / ( dw * hmc ) ) + 
	        			
	            //      22  * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw / 2,   hmc]  ) ) /	   
	            //      22  * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw / 2,   hmc ] ) ) /	  
	        			22d * ( dw - min( dw/2d, hmc ) ) * min( dw / 2d,  hmc ) ) ) /
	        	
	        	//      ( 20  * Max[ 1,  ( dmc * ( dw - Min[ dw / 2,  hmc ] ) * Min[ dw / 2,  hmc ] ) / ( dw * hmc ) ] )
	        	//      ( 20  * Max[ 1 , ( dmc * ( dw - Min[ dw / 2,  hmc ] ) * Min[ dw / 2,  hmc ] ) / ( dw * hmc ) ] )
	        			( 20d * max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) ) / ( dw * hmc ) ) );
	        	
	        	return value;
	        	
	     	case VOLUME:
	     		
	     		//      ( dw * ( dw^3     -      ( dw - 2  * Min[ dw / 2,  hmc ] )^3     ) * ( Min[ dw/2,  hmc ] -  
	     		value = ( dw * ( dw*dw*dw - pow( ( dw - 2d * min( dw / 2d, hmc ) ), 3d ) ) * ( min( dw/2d, hmc ) -  

	     		//           Min[ dw / 2,  hmc]^2      / dw ) ) / ( 120  * ( dw - Min[ dw / 2,  hmc ) ) * Min[ dw / 2,  hmc ] )
	     				pow( min( dw / 2d,  hmc), 2d ) / dw ) ) / ( 120d * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) ); 

	     		return value;
	     		
	     	case R_VOLUME:
	     		
	     		//      ( 1  / 48  ) * Pi * ( dw^3     -      (dw - 2d * Min[ dw / 2,  hmc ] )^3      )
	     		value = ( 1d / 48d ) * PI * ( dw*dw*dw - pow( (dw - 2d * min( dw / 2d, hmc ) ) , 3d ) );

	    	   
	     		return value;
	     		
	     	case F_LENGTH:
	     		
	     	    //     4  * Max[ 1,  ( dmc * ( dw - Min[ dw / 2,  hmc ] ) * Min[ dw / 2,  hmc ] ) / ( dw * hmc ) ]
	     		value= 4d * max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) ) / ( dw * hmc ) );
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		//      ( 1  / 2  ) * Min[ dw / 2 , hmc ]
	     		value = ( 1d / 2d ) * min( dw / 2d, hmc );
	     		
	     		return value;	    
	     		
            default:
	              throw new AssertionError("Unknown operations" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumBlades fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumBlades b : EnumBlades.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }
}