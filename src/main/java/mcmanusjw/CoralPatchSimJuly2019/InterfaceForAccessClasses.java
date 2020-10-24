package mcmanusjw.CoralPatchSimJuly2019;

import java.util.List;

interface InterfaceForAccessClasses 
{
	// Should list here all methods needed when looping through
	// classes based on implementing in this interface 

	String getEnumString();
	
	Double calculateFromString(String propertyString, double dmc, double hmc, Double diameter);
    
	List<String> getAllPropertyStrings();
	
	boolean isTopandBottom(); 
}
