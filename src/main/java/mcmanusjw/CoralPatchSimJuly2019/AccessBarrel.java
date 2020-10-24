package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.List;

class AccessBarrel implements InterfaceForAccessClasses 
{

	static final String enumString = "EnumBarrel";	
	
	static final boolean topAndBottom = true;
	
	@Override
	public Double calculateFromString( String propertyString, double maxHeight, double maxWidth, Double diameter ) 
	{
		return ( EnumBarrel.fromString( propertyString ) ).calculate( maxHeight,  maxWidth,  diameter );
	}

	@Override
	public String getEnumString() 
	{
		return enumString;
	}

	@Override	
	public List < String > getAllPropertyStrings()
	{
		List < String > propertyStrings = new ArrayList < String > ();
		
		for ( EnumBarrel value : EnumBarrel.values() )
		{
			propertyStrings.add( value.getText() );
		}
		return propertyStrings;
	}
	
	@Override
	public boolean isTopandBottom() 
	{
		return topAndBottom;
	}	
}
