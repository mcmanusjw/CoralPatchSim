package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

enum EnumFunnel 
{
	// Should not put species specific values here because this is only a growth form, not a species
	SURFACE  ( "Surface"  ),
	VOLUME   ( "Volume"   ),
	R_VOLUME ( "R_Volume" ),
	F_LENGTH ( "F_Length" ),
	N_HEIGHT ( "N_Height" );
	
	String text;
	
	EnumFunnel ( String text )
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
	        	
	        	//      ( 1  / 200  ) * ( -1  + 110  * Sqrt[2] ) * dw^2  * Pi
	        	value = ( 1d / 200d ) * ( -1d + 110d * sqrt(2) ) * dw*dw * PI;
	        			
	        	return value;
	        	
	     	case VOLUME:
	     		
	     		//      ( ( -30  + 151  * Sqrt[ 2  ] ) * dw^3     * Pi ) / 12000
	     		value = ( ( -30d + 151d * sqrt( 2d ) ) * dw*dw*dw * PI ) / 12000d;
	     		
	     		return value;
	     		
	     	case R_VOLUME:
	     		
                //	            ( ( 1030  - 151  * Sqrt[ 2  ] ) * dw^3     * Pi ) / 12000
	    	     		value = ( ( 1030d - 151d * sqrt( 2d ) ) * dw*dw*dw * PI ) / 12000d;
	    	     		
	     		return value;
	     		
	     	case F_LENGTH:
	     		
	     	    //     ( Sqrt[ 3  ] * dw ) / 5
	     		value= ( sqrt( 3d ) * dw ) / 5d;
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		//      dw / 2
	     		value = dw / 2d;
	     		
	     		return value;	    
	     		
            default:
	              throw new AssertionError("Unknown operations" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumFunnel fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumFunnel b : EnumFunnel.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }
}

