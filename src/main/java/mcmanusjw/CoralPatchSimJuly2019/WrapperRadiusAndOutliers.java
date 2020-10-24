package mcmanusjw.CoralPatchSimJuly2019;

import java.util.Set;

import sim.util.Int2D;

/**
 * Small semi-immutable 'struct' class to carry both a new radius and 
 * a List of outlier cells from a single method so as to avoid double looping.
 * Note that the items in the List can be changed, so set the instance to null when done.
 * 
 * @author John McManus
 *
 */
class WrapperRadiusAndOutliers
{
	final int newRadius;
	final Set < Int2D > outlierLocs;
	
	WrapperRadiusAndOutliers( Integer newRadius, Set < Int2D > outlierLocs)
	{
		this.newRadius = newRadius;
		this.outlierLocs = outlierLocs;
	}
}