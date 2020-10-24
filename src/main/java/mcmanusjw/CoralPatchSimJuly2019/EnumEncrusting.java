package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;

enum EnumEncrusting 
{
	// Should not put species specific values here because this is only a growth form, not a species
	SURFACE  ( "Surface"  ),
	VOLUME   ( "Volume"   ),
	R_VOLUME ( "R_Volume" ),
	F_LENGTH ( "F_Length" ),
	N_HEIGHT ( "N_Height" );
	
	String text;
	
	EnumEncrusting ( String text )
	{
		this.text = text;
	}
	
	// Use switch to pass parameters and diameter to desired formula copied from Mathematica notebooks (in comments above each formula
	double calculate ( double hmc, double dmc, double dw)
	{
		double value = 0;
		switch (this) 
	     {
	        case SURFACE:
	        	
	        	//      ( 13   * dw^2 * Pi ) / 50 
	        	value = ( 13d * dw*dw * PI ) / 50d; 
        	
	        	return value;
	        	
	     	case VOLUME:
	     		
	     		//      ( dw^3     * Pi ) / 400
	     		value = ( dw*dw*dw * PI ) / 400d; 

	     		return value;
	     		
	     	case R_VOLUME:
	     		
	     		value = 0d;
	     		
	     		return value;
	     		
	     	case F_LENGTH:
	     		
	     		value = 0d;
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		value = 0d;
	     		
	     		return value;	    
	     		
            default:
	              throw new AssertionError("Unknown operation" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumEncrusting fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumEncrusting b : EnumEncrusting.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }

}

