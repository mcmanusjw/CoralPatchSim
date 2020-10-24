package mcmanusjw.CoralPatchSimJuly2019;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.SimState;

@SuppressWarnings("serial")
class PanelDamageButtonsAndInfoText extends JPanel
{
	private static boolean okToGo = true;
	
	int pHeight = 130;
	final static JTextPane textPane0 = new JTextPane();
//	static int waveYear = 0;

	public PanelDamageButtonsAndInfoText( Schedule schedule )  
	{
		this.setLayout( new FlowLayout(FlowLayout.LEFT) );
		this.add ( new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, buttonPane(), noteDisplay() ) );
		this.setBackground( Color.white );
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}

	JPanel buttonPane()
	{
		//System.out.println( "Simulation state: " + ConsoleBig.getConsole().getPlayState() );
		
		JPanel outerPane   = new JPanel();
		outerPane.setBackground( Color.white );
		
		JPanel buttonPane  = new JPanel();
		buttonPane.setLayout( new GridBagLayout() ); 
		buttonPane.setBackground( Color.white ); // Do this before setting JButton label invisible
		List < String > buttonNames = List.of
		(
			"Bleach Full Power",
			"Bleach 3/4 Power",
			"Storm Wave Damage",
			"  Boat Grounding "
		);
		JButton [] buttons = new JButton[ buttonNames.size() ];

		int i = 0;
		for( String name : buttonNames )
		{
			GridBagConstraints gbc = new GridBagConstraints();
	        gbc.gridwidth = GridBagConstraints.REMAINDER;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
			buttons[ i ] = new JButton( name );
			gbc.weighty = 1;
			buttons[ i ].setHorizontalAlignment( SwingConstants.LEFT );
			Font font = new Font( "Dialog", Font.BOLD, 12 );
			buttons[ i ].setFont( font );
			buttons[ i ].setForeground( Color.black );
			buttonPane.add( buttons[ i ], gbc );
			i++;
		}
		
		buttons[ 0 ].addActionListener // bleach all
		(
				e -> {
						StepLooper.setDoBleachAll( true );
					 }	
		);
		
		buttons[ 1 ].addActionListener // bleach 75 percent 
		(
				e -> {
						StepLooper.setDoBleach75Percent( true );
				 	}	
		);
		
		buttons[ 2 ].addActionListener // wave damage /// put in activate buttons only when running (StepLooper more than 0 ?)
		(
			e -> 
			{
				StepLooper.setDoWaves( true );
			}
		);

		buttons[ 3 ].addActionListener // boat damage  
		(
			e -> 
			{
					StepLooper.setDoBoat( true );
			}
		);

		outerPane.add( buttonPane, BorderLayout.CENTER );
		outerPane.setBorder(BorderFactory.createLineBorder(Color.black));
		outerPane.setPreferredSize( new Dimension( 130, pHeight ) );
		
		return outerPane;
	}
	
	JPanel noteDisplay()
	{
		JPanel display = new JPanel();
		
		textPane0.setBackground( Color.getHSBColor( 180, 100, 100) );
		textPane0.setFont( textPane0.getFont().deriveFont( 16f ) ); 
		textPane0.setText( "\nPress arrow to begin" );

		display.add( textPane0, BorderLayout.SOUTH );
		display.setPreferredSize( new Dimension( 575, pHeight ) );
		display.setBackground( Color.getHSBColor( 180, 100, 100) );
		display.setBorder(BorderFactory.createLineBorder(Color.black));
		
		return display;
	}
	
	/**
	 * This is to be called from a delayed class, such as StepLooper, to start at the beginning of a new year
	 * based on a request from the waveButton. This avoids problems from conccurrent access in other routines.   
	 */
	static void buttonBleachAll()
	{
		StepBroadDamage.killCorals( 1.0 );
	}
	
	/**
	 * This is to be called from a delayed class, such as StepLooper, to start at the beginning of a new year
	 * based on a request from the waveButton. This avoids problems from conccurrent access in other routines.   
	 */
	static void buttonBleach75Percent()
	{
		StepBroadDamage.killCorals( 0.75 ); // was 0.5
	}

	/**
	 * This is to be called from a delayed class, such as StepLooper, to start at the beginning of a new year
	 * based on a request from the waveButton. This avoids problems from conccurrent access in other routines.   
	 */
	static void buttonWaves( SimState state )
	{
		MersenneTwisterFast random = state.random;

		TableRunParamsListEnumMapsString runParamsTable = StaticBenthosData.getRunParamsTable(); 
		int seriesNumber = StepLooper.getSeriesNumber(); // gets for the current series upon initialization 
		int width = (int) runParamsTable.getDouble ( seriesNumber, "Width"  );
		int height = width;
		boolean complete = false;
		double percentToDamage = 0.8; // No need to randomize this for the button.
		int cellsToDamage    = ( int ) Math.round( percentToDamage * width * height );
	
		System.out.println( " width: " + width );///////////////////////////
		System.out.println( "starting cellsToDamage: " + cellsToDamage );//////////////////////////
	
		int ellipseLengthMax = ( int ) width / 2;
		int ellipseLengthMin = ( int ) width / 3;
		int ellipseWidthMax  = ( int ) width / 4;
		int ellipseWidthMin  = ( int ) width / 5;
		int angleDegreesMax  = 120;
		int angleDegreesMin  = 100;

		double upperWaveStrength = 0.9;
		double lowerWaveStrength = 0.8;
	
		double depthProb = 1.0; // setting for no effect -- this is assumed to be a very shallow spot
	
		for ( int j = 0; j < 100; j++ ) // this limits run in case not all specified cells will be hit for a long time 
		{
			if ( j == 0 ) System.out.println( "Starting wave loop");
		
			int xCenter = 0;
			int yCenter = 0; 
			
			xCenter = random.nextInt( width  + 1 );
			yCenter = random.nextInt( height + 1 );

			int ellipseLength = random.nextInt( ( ellipseLengthMax - ellipseLengthMin ) + 1 ) + ellipseLengthMin;
			int ellipseWidth  = random.nextInt( ( ellipseWidthMax  - ellipseWidthMin  ) + 1 ) + ellipseWidthMin;
			int angleDegrees  = random.nextInt( ( angleDegreesMax  - angleDegreesMin  ) + 1 ) + angleDegreesMin;

			// double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			double waveStrength = 
    				 lowerWaveStrength + ( ( upperWaveStrength - lowerWaveStrength ) * random.nextDouble() );
    		
			complete = StaticEllipticalDamager.damage( depthProb, xCenter, yCenter, 
	        	   ellipseLength, ellipseWidth, angleDegrees, waveStrength, cellsToDamage, random ); 
		    
			if ( complete ) break; // continues to throw waves out until the total cells to damage reached
		}
		StaticEllipticalDamager.init();
		okToGo = true;
	}
	
	/**
	 * This is to be called from a delayed class, such as StepLooper, to start at the beginning of a new year
	 * based on a request from the boatButton. This avoids problems from conccurrent access in other routines.   
	 */
	static void buttonBoat( SimState state )
	{

		MersenneTwisterFast random = state.random;
		
		TableRunParamsListEnumMapsString runParamsTable = StaticBenthosData.getRunParamsTable(); 
		int seriesNumber = StepLooper.getSeriesNumber(); // gets for the current series upon initialization 
		int width = (int) runParamsTable.getDouble ( seriesNumber, "Width"  );
		int height = width;

		int cellsToDamage    = width * height; // just to have a meaningless number

		double depthProb = 1.0; // setting for no effect -- this is assumed to be a very shallow spot
			
    	int xCenter = 0;
	    int yCenter = 0; 
		    		
		//Put one ellipse in the center
	    xCenter = ( int ) Math.abs( Math.round( ( width  / 2 ) + ( width / 4  ) ) );
	    yCenter = ( int ) Math.abs( Math.round( ( height / 2 ) + ( height / 4 ) ) );
	    
	    System.out.println( "xCenter, yCenter: [ " + xCenter + ", " + yCenter + " ]" );////////////////

	    int ellipseLength = ( int ) width;
	    int ellipseWidth  = ( int ) width / 2;
	    int angleDegrees  = 90;

	    // double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
	    double waveStrength = 1.0; // boats get maximum damage level
		    		
		StaticEllipticalDamager.damage( depthProb, xCenter, yCenter, 
				ellipseLength, ellipseWidth, angleDegrees, waveStrength, cellsToDamage, random ); 
	}

	static void setDisplayText( String text )
	{
		textPane0.setText( text );
	}

	static boolean isOkToGo() {
		return okToGo;
	}
}
