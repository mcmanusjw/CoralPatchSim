package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.List;

class AccessTube implements InterfaceForAccessClasses 
{

	static final String enumString = "EnumTube";	
	
	static final boolean topAndBottom = true;
	
	@Override
	public Double calculateFromString( String propertyString, double maxHeight, double maxWidth, Double diameter ) 
	{
		return ( EnumTube.fromString( propertyString ) ).calculate( maxHeight,  maxWidth,  diameter );
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
		
		for ( EnumTube value : EnumTube.values() )
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
