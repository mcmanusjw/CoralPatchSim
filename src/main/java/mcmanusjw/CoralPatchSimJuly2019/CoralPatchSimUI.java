package mcmanusjw.CoralPatchSimJuly2019;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.util.gui.*;
import javax.swing.*;
//import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.*;
/**
 * Note: S1, S2 etc. not showing up on each species outputs!<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 * @author John McManus
 *
 */


public class CoralPatchSimUI extends GUIState
{
	   public static Display2D display;
	   public JFrame displayFrame;

	   FastValueGridPortrayal2D coralPortrayal = new FastValueGridPortrayal2D("StepCoral");
	   int displayDimension = 420; // was 400
	   private static Color [] colorSet;

	   public static void main(String[] args)
	   {
		  CoralPatchSimUI simGUI = new CoralPatchSimUI();  
	      ConsoleBig c = new ConsoleBig( simGUI ); // note that this uses the big console with 4 graphs
	      c.setVisible(true);
	   }

	  // public Object getSimulationInspectedObject() { return state; }  // non-volatile

	   public CoralPatchSimUI()
	   {
	       super(new CoralPatchSim( System.currentTimeMillis() ) );
	   }
		   
	   public static String getName ()
	   {
		    return "CoralPatchSim";
	   }

	   public void start()
	   {
	       super.start();
	       setupPortrayals();
	   }

	   public void load(SimState state)
	   {
	      super.load(state);
	      setupPortrayals();
	   }
	       
	   public void setupPortrayals()
	   {
		   //First color will be background color (value = 0)
		   //Last color will be damage color (value = number of species + 1)
		   Color [] colorSet = StaticColorMaker.getColorSet( StaticCoral.getFixedNumBenthicTypes() + 6 );
		                                    //was 2.  +1 is for the background +1 for destruction ellipse
		   SimpleColorMap simpleColorMap = new SimpleColorMap(); 
		   simpleColorMap.setColorTable( colorSet );

		   coralPortrayal.setField( StaticCoralCell.getSpeciesCellGrid() );
		   coralPortrayal.setMap(   simpleColorMap );
		   
	       // "reschedule the displayer"
	       display.reset();
		               
	       // "redraw the display"
//	       display.repaint();
	    }

		public void init( Controller c )
		{
		   super.init( c );

		   colorSet = StaticColorMaker.getColorSet( StaticCoral.getFixedNumBenthicTypes() + 6 ); //was 2.  +1 is for the background +1 for destruction ellipse
		   SimpleColorMap simpleColorMap = new SimpleColorMap(); 
		   simpleColorMap.setColorTable( colorSet );

		   coralPortrayal.setField( StaticCoralCell.getSpeciesCellGrid() );
		   coralPortrayal.setMap(   simpleColorMap );

		   // make the displayer
		   display = new Display2D(displayDimension,displayDimension,this);
		   display.setBackdrop(Color.white);		     
		   
//		   display.setScale(1.1); 
		  
//		   displayFrame = display.createFrame();
//		   displayFrame.setTitle("Sampling Reef");
//		   c.registerFrame(displayFrame);   // "register the frame so it appears
			     							//  in the "Display" list"
//		   displayFrame.setVisible(true);
		   display.attach( coralPortrayal, "StepCoral" ); 
		   
		   display.layersbutton .setVisible( false );
		   display.refreshbutton.setVisible( false );
		   display.movieButton  .setVisible( false );
		   display.optionButton .setVisible( false );
		   
			//display.setPreferredSize( new Dimension( 390, 450 ) );
			ConsoleBig.getCenterDisplayPane().add( display );
		}
		       
		public void quit()
		{
		    super.quit();
		       
		    if (displayFrame!=null) displayFrame.dispose();
		    displayFrame = null;
		    display = null;
	    }

		static Color[] getColorSet() {
			return colorSet;
		}
		
}
