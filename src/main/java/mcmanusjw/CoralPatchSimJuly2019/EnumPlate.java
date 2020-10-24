package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

enum EnumPlate 
{
	// Should not put species specific values here because this is only a growth form, not a species
	SURFACE  ( "Surface"  ),
	VOLUME   ( "Volume"   ),
	R_VOLUME ( "R_Volume" ),
	F_LENGTH ( "F_Length" ),
	N_HEIGHT ( "N_Height" );
	
	String text;
	
	EnumPlate ( String text )
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
	        	
	        	//      ( 19  * dw^2  * Pi ) / 80
	        	value = ( 19d * dw*dw * PI ) / 80d;
	        			
	        	return value;
	        	
	     	case VOLUME:
	     		
	     		//      ( dw^3     * Pi ) / 80
	     		value = ( dw*dw*dw * PI ) / 80d;
	     		
	     		return value;
	     		
	     	case R_VOLUME:
	     		
                //	            ( 7  * dw^3     * Pi ) / 1728
	    	     		value = ( 7d * dw*dw*dw * PI ) / 1728d;
	    	     		
	     		return value;
	     		
	     	case F_LENGTH:
	     		
	     	    //      ( Sqrt[ 7  ] * dw ) / 4
	     		value=  ( sqrt( 7d ) * dw ) / 4d;
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		//      0
	     		value = 0;
	     		
	     		return value;	    
	     		
            default:
	              throw new AssertionError("Unknown operations" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumPlate fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumPlate b : EnumPlate.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }
}


