package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

enum EnumMassive 
{
	// Should not put species specific values here because this is only a growth form, not a species
	SURFACE  ( "Surface"  ),
	VOLUME   ( "Volume"   ),
	R_VOLUME ( "R_Volume" ),
	F_LENGTH ( "F_Length" ),
	N_HEIGHT ( "N_Height" );
	
	String text;
	
	EnumMassive ( String text )
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
	        	
	        	//      ( 23  * dw^2    * Pi ) / 48
	        	value = ( 23d * dw * dw * PI ) / 48d;
	        			
	        	return value;
	        	
	     	case VOLUME:
	     		
	     		//      ( 137  * dw^3     * Pi ) / 1728
	     		value = ( 137d * dw*dw*dw * PI ) / 1728d;
	     		
	     		return value;
	     		
	     	case R_VOLUME:
	     		
                //	            ( 7  * dw^3     * Pi ) / 1728
	    	     		value = ( 7d * dw*dw*dw * PI ) / 1728d;
	    	     		
	     		return value;
	     		
	     	case F_LENGTH:
	     		
	     	    //     ( Sqrt[ 7  ] * dw ) / 4
	     		value= ( sqrt( 7d ) * dw ) / 4d;
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		//      dw / 4
	     		value = dw / 4d;
	     		
	     		return value;	    
	     		
            default:
	              throw new AssertionError("Unknown operations" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumMassive fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumMassive b : EnumMassive.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }
}


