package mcmanusjw.CoralPatchSimJuly2019;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.awt.BasicStroke;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import sim.display.Console;
import sim.display.GUIState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.util.Int2D;

/**
 * 
 * This heavily altered version of the Mason 2017 Console class consolidates the Console and the Display,
 * and adds 5 live-update graphs, a planter panel, and a text space.
 * 
 * Should use a graph label "CaCO3 in Living Benthos"
 *  
 * @author John McManus
 *
 */

@SuppressWarnings("serial")
public class ConsoleBig extends Console
{
	static JPanel displayPane       = new JPanel();
	static JPanel centerDisplayPane = new JPanel(); // Used by the UI to load ABM display. Does not show Mason console unless static
	
	static double xMax = ( int ) Math.round( StaticBenthosData.getStepsToEnd() / StaticBenthosData.getStepsPerYear() ); // Number of years total
	
	static JFreeChart [] charts = new JFreeChart [ 5 ];
	
	static int chartNumber;
	static int numSpecies = (int) Double.parseDouble( StaticBenthosData.getRunParamsTable().getValue( 0, EnumRunParams.NUM_BENTHIC_TYPES ) );  
			//Integer.parseInt( StaticBenthosData.getRunParamsTable().getValue( 0, EnumRunParams.NUM_BENTHIC_TYPES ) );
	static int numSeries = 6 + numSpecies; // 3 area data sets, 3 fish volume types plus numSpecies for the fifth graph for competition
	
	static XYSeries [] seriesArray = new XYSeries[ numSeries ];
	
	static List < String > y1Strings    = List.of(  "Bottom Cover", "Living Surface Area", 
									                "CaCO3 in Living Benthos", "Fish Refuge Volume",
									                "Area by 'Species'");
	
	static List < String > y2Strings = List.of(  "% seen from above", "square meters", "kilograms", 
												 "cubic meters", "square centimeters" );

	static boolean okToPlant = false;
	
	static JPanel planter     = new JPanel();
	static JPanel clickPane   = new JPanel();
	static int    clickWidth  = 0;
	static int    clickHeight = 0;
	
	static SpeciesIdDiameterLoc prevSdl = new SpeciesIdDiameterLoc( 0, 0, new Int2D( 0, 0 ) ); // to keep track for repeat planting
	
	static Set < SpeciesIdDiameterLoc > sdlSet = new HashSet < SpeciesIdDiameterLoc >(); 
	
	static Console console;
	
	public ConsoleBig( GUIState simulation ) 
	{
		super( simulation );
		super.getTabPane().remove ( 3 ); // remove inspectors tab
		super.getTabPane().remove ( 2 ); // remove displays tab

		console = this;
		
		// Set overall size to fit in a 1280 x 768 screen
		this.setMinimumSize( new Dimension( 1200, 760 ));
		
		// Get all panes on parent Console class and resize them
		Component [] panes = this.getComponents();

		for (int i = 0; i < panes.length; i++) 
		{
			panes[ i ].setPreferredSize( new Dimension( 30, 30 ) ); // was 30,30
		}
		
		// Make bottom right panel -- to be pushed down by the outer horizontal split pane
		JPanel newPanel0 = new JPanel();
		newPanel0.setPreferredSize( new Dimension ( 735, 130 ) ); // was 320, 730,360 
		newPanel0.setBorder(BorderFactory.createLineBorder(Color.black));
		newPanel0.setVisible( true ); 
		this.add( newPanel0, BorderLayout.EAST );
		
	   Schedule schedule = simulation.state.schedule;
		
	    newPanel0.add( new PanelDamageButtonsAndInfoText( schedule ) );
		
		// Initialize all series, including 3 for area charts, 3 for the refuge graph, and numSpecies for the competition graph 
		for (int i = 0; i < seriesArray.length; i++) 
		{
			seriesArray[ i ] = new XYSeries( "XYGraph" + i  );
		}
	
		// Initialize series as passed to graphing process
    	XYSeries [] seriesToGraph = new XYSeries[ 3 + numSpecies ]; 
    	
		// Make three area graphs and one line graph with three lines, plus one for competition
		JPanel [] graphPanes = new JPanel [ 5 ];
		
	    for (int i = 0; i < graphPanes.length; i++) 
	    {
	    	boolean areaGraphTF = true;
	    	boolean percentTF   = false;
	    	boolean compChartTF = false;
	    	
	    	if ( i < 1) percentTF = true;
	    	
	    	if ( i < 3 ) // 3 area charts
	    	{
	    		seriesToGraph[ 0 ] = seriesArray[ i ];
	    	}
	    	else if ( i == 3 ) // triple line chart
			{
				areaGraphTF = false;
				
				for (int j = 0; j < 3; j++) 
				{
					seriesToGraph[ j ] = seriesArray[ i + j ];
				}
			}
	    	else if ( i == 4 ) // Competition chart
	    	{
				areaGraphTF = false;
				percentTF   = true; // Percent of bottom for each species
				compChartTF = true;
				
				for (int j = 0; j < numSpecies; j++) 
				{
					seriesToGraph[ j ] = seriesArray[ 6 + j ]; //Already done 0,1,2 as areas and 3,4,5 as lines. j starts at 0.
				}
	    	}
	    	
	    	graphPanes [ i ] = new JPanel();
	    	graphPanes [ i ].add( makeChart ( seriesToGraph, 
	    									  areaGraphTF, percentTF, compChartTF,
	    									  y1Strings.get( i ), y2Strings.get( i )  ) );
	    	
	    	graphPanes [ i ].setLayout( new GridBagLayout() ); // Centers the graph in the pane
	    	graphPanes [ i ].setBackground( Color.WHITE );     // Color.WHITE );
		}
		
		// On left, put graphPane 0 above graphPane 1
	    JSplitPane lftSplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT, graphPanes[ 0 ], graphPanes[ 1 ] );
		lftSplit.setDividerSize( 5 );
		lftSplit.setDividerLocation( 240 ); // was 245

		// On right, put graphPane 2 above graphPane 3
		JSplitPane rgtSplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT, graphPanes[ 2 ], graphPanes[ 3 ] );
		rgtSplit.setDividerSize( 5 );
		rgtSplit.setDividerLocation( 240 ); // was 245

		// Put those left and right splitpanes into a horizontal splitpane
		JSplitPane graphSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, lftSplit, rgtSplit );
		graphSplitPane.setDividerSize( 5 );
		graphSplitPane.setDividerLocation( 360 ); 
		
		// Make a tabbed pane to hold graphs and coral planter
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize( new Dimension( 730, 520 ) ); 
		
		// Add tab for 4 graphs
		tabbedPane.add( "Ecoservices", graphSplitPane );

		JPanel speciesPane     = new JPanel();
		JPanel speciesPane1    = new JPanel();
		JPanel competitionPane = new JPanel();
		JPanel coralPlanter    = new JPanel();		
		JPanel planterText     = new JPanel();
		speciesPane    .setBackground( Color.white );
		speciesPane1   .setBackground( Color.white );
		competitionPane.setBackground( Color.white );
		coralPlanter   .setBackground( Color.gray  );
		planter        .setBackground( Color.white );
		planterText    .setBackground( Color.white );
		
		JPanel boxesPane = new JPanel();
		boxesPane.setLayout( new GridBagLayout() ); 
		boxesPane.setBackground( Color.white );
		
		JPanel boxesPane1 = new JPanel();
		boxesPane1.setLayout( new GridBagLayout() ); 
		boxesPane1.setBackground( Color.white );
		
		// Make legend color boxes
		for (int i = 0; i < numSpecies; i++) 
		{
			String name = StaticBenthosData.getSpeciesInputTable().getMap( i ).get( EnumSpeciesInputField.NAME );
			StringBuilder label = new StringBuilder( ( i + 1 ) + " " + name );
			
			GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
			String fLabel = label.toString();
			JButton box = new JButton( fLabel );
			box.setHorizontalAlignment( SwingConstants.LEFT );
			Font font = new Font( "Dialog", Font.BOLD, 15 );
			box.setFont( font );
			box.setMinimumSize( new Dimension ( 100, 100 ) );
			box.setForeground( Color.white );
			box.setBackground( CoralPatchSimUI.getColorSet()[ i + 1 ] );
			boxesPane.add( box, gbc );
			
			GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.gridwidth = GridBagConstraints.REMAINDER;
            gbc1.fill = GridBagConstraints.HORIZONTAL;
			String fLabel1 = label.toString();
			JButton box1 = new JButton( fLabel1 );
			box1.setHorizontalAlignment( SwingConstants.LEFT );
			Font font1 = new Font( "Dialog", Font.BOLD, 15 );
			box1.setFont( font1 );
			box1.setMinimumSize( new Dimension ( 100, 100 )   );
			box1.setForeground( Color.white );
			box1.setBackground( CoralPatchSimUI.getColorSet()[ i + 1 ] );
			boxesPane1.add( box1, gbc1 );
		}
        JScrollPane scrollableTextArea = new JScrollPane( boxesPane );
        scrollableTextArea.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );// was HORIZONTAL_SCROLLBAR_ALWAYS
        scrollableTextArea.setVerticalScrollBarPolicy(   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED   ); // VERTICAL_SCROLLBAR_ALWAYS
        speciesPane.add(scrollableTextArea);
		
		JPanel catchGraph = new JPanel();
		catchGraph.setBackground( new Color( 0, 0, 204) );
		catchGraph.add( graphPanes[ 4 ] );
        // Note that the chart will be chPanel.setPreferredSize( new Dimension( 350, 450 ) ); 
		
		catchGraph.setPreferredSize( new Dimension( 600, 455 ) );
		competitionPane.add( catchGraph );
		competitionPane.setBorder( BorderFactory.createLineBorder(Color.black) );
		competitionPane.setBackground( new Color( 0, 0, 204) ); // .Color.blue ));
		
		JPanel compLayoutPane = new JPanel();
		compLayoutPane.setLayout( new BoxLayout( compLayoutPane, BoxLayout.Y_AXIS ) );
		compLayoutPane.add( competitionPane );
		
		JSplitPane compSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, speciesPane, compLayoutPane );//competitionPane );
		compSplitPane.setDividerLocation( 255 ); //was 280
		
		tabbedPane.add( "Competition", compSplitPane );
		
		JScrollPane scrollableTextArea1 = new JScrollPane( boxesPane1 );
	    scrollableTextArea1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);// was HORIZONTAL_SCROLLBAR_ALWAYS
	    scrollableTextArea1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // VERTICAL_SCROLLBAR_ALWAYS
	    speciesPane1.add( scrollableTextArea1 );
		
		JSplitPane planterSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, speciesPane1, coralPlanter );
		tabbedPane.add( "CoralPlanter", planterSplitPane );

		coralPlanter.setLayout( new BoxLayout( coralPlanter, BoxLayout.Y_AXIS ) );
		coralPlanter.setBorder( BorderFactory.createLineBorder(Color.black) );
	    
	    JPanel clickPane = clickablePane();
	    clickPane.setPreferredSize( new Dimension( 400, 400 ) );
	    clickPane.setBorder( BorderFactory.createLineBorder(Color.black) );
	    
	    planter.add( clickPane );
		planter.setBackground( new Color( 0, 0, 204) ); // .Color.blue ));
		planterText.add( makePlantTextPanel() );
		planterText.setPreferredSize( new Dimension( 300, 55 ) );
		planterText.setBorder( BorderFactory.createLineBorder(Color.black) );
		coralPlanter.add( planter );
		coralPlanter.add( planterText );
		
		displayPane.setLayout( new BorderLayout() );
		displayPane.add( centerDisplayPane, BorderLayout.CENTER );
		displayPane.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, displayPane, tabbedPane ); //graphSplitPane );
		splitPane.setDividerLocation( 450 ); 
		splitPane.setDividerSize( 5 );
		splitPane.setBorder( BorderFactory.createLineBorder(Color.black) );
		
		this.add( splitPane, BorderLayout.NORTH );
		this.pack();
		this.setLocationRelativeTo( null );
		this.setVisible( true );
		
		clickWidth  = clickPane.getWidth();
		clickHeight = clickPane.getHeight();
			
	}

	/**
	 * @author John McManus
	 * 
	 * This makes each area or line graph, and adds custom x and y labels, 
	 * y sublabels, and a legend for a 3-line graph 
	 * 
	 * @param seriesToGraph
	 * @param areaGraphTF
	 * @param yLabel
	 * @param ySubLabel
	 * @return
	 */
	static JPanel makeChart( XYSeries [] seriesToGraph, 
							 boolean areaGraphTF, boolean percentTF, boolean compChartTF,
			                 String y1String, String  y2String ) 
	{
		// Add the series to data set
		XYSeriesCollection dataset = new XYSeriesCollection();
		JFreeChart chart; // = charts[ chartNumber ];

		// Generate the graph
		if ( areaGraphTF )
		{
			dataset.addSeries( seriesToGraph[ 0 ] );

			chart = ChartFactory.createXYAreaChart
					(
							null,//"XY Chart3", // Title				   
							null, //"Months", // x-axis Label
							null, // "Number of Cells", // y-axis Label
							dataset, // Dataset
							PlotOrientation.VERTICAL, // Plot Orientation
							true, // Show Legend
							false, // Use tooltips
							false // Configure chart to generate URLs?      
					);
		} 
		else // So must be a line chart (3 for refuge chart or numSpecies for Competition chart)
		{
			int endBefore = 3;
			if ( compChartTF ) endBefore = numSpecies;
			
			// seriesToGraph is always 0 to the number of lines (either 3 or numSpecies)
			for (int i = 0; i < endBefore; i++) 
			{
				dataset.addSeries( seriesToGraph[ i ] );
			}
			
			chart = ChartFactory.createXYLineChart(
					   null,//"XY Chart3", // Title				   
					   null, //"Months", // x-axis Label
					   null, // "Number of Cells", // y-axis Label
					   dataset, // Dataset
					   PlotOrientation.VERTICAL, // Plot Orientation
					   true, // Show Legend
					   false, // Use tooltips
					   false // Configure chart to generate URLs?      
						);
		}
		
		chart.getXYPlot().getDomainAxis().setRange( 0d, xMax );
		
		if ( percentTF )
		{
			chart.getXYPlot().getRangeAxis() .setRange( 0d, 100d );	
		}
		else
		{
			chart.getXYPlot().getRangeAxis() .setAutoRange( true );
		}

		chart.setTextAntiAlias( true ); // Why is this not working?????????????????????????????????
		chart.getXYPlot().setBackgroundPaint( Color.white );
		chart.getXYPlot().setDomainGridlinePaint( Color.GRAY );
		chart.getXYPlot().setRangeGridlinePaint ( Color.GRAY );
		Font font = new Font( "Dialog", Font.BOLD, 16 ); 
		chart.getXYPlot().getDomainAxis().setTickLabelFont( font );
		chart.getXYPlot().getRangeAxis() .setTickLabelFont( font );
		
		NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();  

		int gap = 0;
		if      ( xMax < 11  ) gap = 1;
		else if ( xMax < 21  ) gap = 2;
		else if ( xMax < 51  ) gap = 5;
		else if ( xMax < 101 ) gap = 10;
		else if ( xMax < 201 ) gap = 20;
		else if ( xMax < 501 ) gap = 50;
		else                   gap = 100;

		xAxis.setTickUnit(new NumberTickUnit( gap ));
		
		if ( areaGraphTF )
		{
			XYAreaRenderer2 r1 = new XYAreaRenderer2(); 
			r1.setSeriesPaint(0, new Color( 153, 51, 255 ) ); 
			chart.getXYPlot().setRenderer(0, r1);
		}
		else
		{
			float lineWidth = 2.0f;
			BasicStroke stroke = new BasicStroke( lineWidth );
			XYItemRenderer xyir = chart.getXYPlot().getRenderer();
			
			int endBefore = 3;
			if ( compChartTF ) endBefore = numSpecies;
			
			for (int i = 0; i < endBefore; i++) 
			{
				xyir.setSeriesStroke( i , stroke);
				
				// Below, note that the first color number is white for background, so we skip it here and in species grid
				if ( compChartTF ) xyir.setSeriesPaint( i, CoralPatchSimUI.getColorSet()[ i + 1 ] );
			}
			
		}

		chart.getLegend().setVisible( false );  /// Warning -- we need this!!!!!!!!!!!!!!!!!!!!!!
						   
		ChartPanel chPanel = new ChartPanel(chart); //creating the chart panel, which extends JPanel
		chPanel.setPreferredSize( new Dimension( 290, 190 ) ); //size according to my window  -- was 180
		if ( compChartTF ) chPanel.setPreferredSize( new Dimension( 350, 450 ) );
		chPanel.setBackground( Color.WHITE);

		JPanel yLabel    = new JPanel( new GridBagLayout() );
		yLabel.setBackground( Color.WHITE);
		JPanel ySubLabel = new JPanel( new GridBagLayout() );
		ySubLabel.setBackground( Color.WHITE);
		
		JLabel vert = new JLabel( y1String );
		vert.setUI( new VerticalLabelUI( false ) );
		vert.setFont( font );
		yLabel.add( vert );
		
		JLabel vBlank = new JLabel( y2String ); // put into an if statement -- else add blanks
		vBlank.setUI( new VerticalLabelUI( false ) );
		vBlank.setFont( font );
		ySubLabel.add( vBlank );
		
		JSplitPane yLabelSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, yLabel, ySubLabel );
		yLabelSplit.setDividerSize( 0 );
		yLabelSplit.setBorder(null);
		
		JPanel xLabelPanel    = new JPanel( new GridBagLayout() );
		xLabelPanel.setBackground( Color.WHITE ); // Color.WHITE);
		JPanel xSubLabelPanel = new JPanel( new GridBagLayout() );
		xSubLabelPanel.setBackground( Color.WHITE ); // Color.WHITE);
		
		JLabel xLabel = new JLabel ( "                   Years" );
		xLabelPanel.add( xLabel  );
		xLabel.setFont( font );
		
		JPanel legend = new JPanel( new GridBagLayout() );
		legend.setBackground( Color.WHITE );
		JLabel hBlank = new JLabel( "                 ");
		hBlank.setFont( font );
				
		if ( !areaGraphTF && !compChartTF )
		{
			// Red, blue, green series legend
			
			Dimension boxSize = new Dimension( 14, 14 );
			JButton redBox = new JButton();
			redBox.setBackground( Color.RED );
			redBox.setPreferredSize( boxSize );
			JButton blueBox = new JButton();
			blueBox.setBackground( Color.BLUE );
			blueBox.setPreferredSize( boxSize );
			JButton greenBox = new JButton();
			greenBox.setBackground( Color.GREEN );
			greenBox.setPreferredSize( boxSize );
			Font boxFont = new Font( "Dialog", Font.BOLD, 14 ); 
			JLabel redLabel   = new JLabel( " Small    "  );
			JLabel blueLabel  = new JLabel( " Medium    " );
			JLabel greenLabel = new JLabel( " Large"  ); 
			redLabel  .setFont( boxFont );
			blueLabel .setFont( boxFont );
			greenLabel.setFont( boxFont );
			legend.add( hBlank     );
			legend.add( redBox     );
			legend.add( redLabel   );
			legend.add( blueBox    );
			legend.add( blueLabel  );
			legend.add( greenBox   );
			legend.add( greenLabel );
		} 
		else
		{
			legend.add( hBlank     );
		}
		xSubLabelPanel.add( legend );
		
		JSplitPane xLabelSplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT, xLabelPanel, xSubLabelPanel );
		xLabelSplit.setDividerSize( 0 );
		xLabelSplit.setBorder(null);
		xLabelSplit.setDividerLocation( 20 );

		JPanel xLabelSplitPanel = new JPanel();
		xLabelSplitPanel.add( xLabelSplit );
		
		JPanel jPanel = new JPanel( new BorderLayout() );
		jPanel.add( xLabelSplitPanel,   BorderLayout.SOUTH    ); // was xLabelSplit
		jPanel.add( chPanel,            BorderLayout.CENTER   );
		jPanel.add( yLabelSplit,        BorderLayout.WEST     ); 
		jPanel.add( xLabelSplit,        BorderLayout.SOUTH    );
		
		// Put chart into array and increment for next chart
		charts[ chartNumber ] = chart;
		chartNumber++;
		
		return jPanel; 
	 }

	 static void addValuePairs( List < Double > yVals, SimState state )
	 {
		 int year = StepLooper.getYear();
		 
		 for ( int i = 0; i < seriesArray.length; i++ )
		 {
			 seriesArray[ i ].add( year, yVals.get( i ) );
		 }
	 }
	 
	 static void clearAllValuePairs() 
	 {
		 for ( int i = 0; i < seriesArray.length; i++ )
		 {
			 seriesArray[ i ].clear();
		 }
		 
		 // Resetting x axes. See David Gilbert comments in 
		 // https://stackoverflow.com/questions/26030886/how-to-clean-up-plotted-chart-and-restart-the-graph-plotting-from-left-end
		 for (int i = 0; i < charts.length; i++) 
		 {
			 charts[ i ].getXYPlot().getDomainAxis().setRange( 0d, xMax ); 
			 NumberAxis xAxis = (NumberAxis) charts[ i ].getXYPlot().getDomainAxis();  

			 int gap = 0;
			 if      ( xMax < 11  ) gap = 1;
			 else if ( xMax < 21  ) gap = 2;
			 else if ( xMax < 51  ) gap = 5;
			 else if ( xMax < 101 ) gap = 10;
			 else if ( xMax < 201 ) gap = 20;
			 else if ( xMax < 501 ) gap = 50;
			 else                   gap = 100;

			 xAxis.setTickUnit( new NumberTickUnit( gap ) );
		 }
	 } 

	 /**
	  * @author John McManus
	  * @since  June 7, 2019
	  * 
	  * https://stackoverflow.com/questions/12396066/how-to-get-location-of-a-mouse-click-relative-to-a-swing-window
	  * see David Kroucamp answer
	  *
	  * @return JPanel
	  */
	 static JPanel clickablePane()
	 {
		JPanel panel = new JPanel();
		
		List < String  > names                = new ArrayList < String  > ();		
		HashMap< Integer, Color > mapIdColors = new HashMap< Integer, Color >();
		
		for (int i = 0; i < numSpecies; i++) 
		{
			String name = StaticBenthosData.getSpeciesInputTable()
					          .getMap( i ).get( EnumSpeciesInputField.NAME ); 
			Color color = CoralPatchSimUI.getColorSet()[ i + 1 ];
			names .add ( name  );
			mapIdColors.put( i, color );
		}
		 
		panel.addMouseListener
		( 
			new MouseListener() 
			{
				@Override
				public void mouseClicked( MouseEvent e ) 
				{
					if( okToPlant )
					{
						Point mousePos = panel.getMousePosition();
				
						int xCoord = (int) mousePos.getX();//e.getX();
						int yCoord = (int) mousePos.getY();//e.getY();
						Int2D loc = new Int2D( xCoord, yCoord );
						
						// Like prevSdl, this starts with diameter = 0, used later to skip missing data
						SpeciesIdDiameterLoc sdl = new SpeciesIdDiameterLoc( 0, 0, loc ); 
							
						if ( e.getButton() != MouseEvent.BUTTON3 ) 
						{
							while ( true )
							{
								sdl = new SpeciesDiamLocOptPane( names, 100, loc ).getSd();//change 100 to max size
								
								if( sdl.getDiameter() < 1 ) break;
								
								int radius = ( int ) Math.ceil( sdl.getDiameter() / 2.0 ); 
								
								boolean noOverlap = checkAllDistances( loc, radius ); 
								boolean noOffEdge = checkCoralToEdges( loc, radius );
							
								if ( !noOverlap ) // if overlapping coral(s)
								{
								    sdl = null;
								    UtilJM.warner( "This coral overlaps a previous one." );

								    return;
								}
								if ( !noOffEdge ) // if overlapping one or more edges
								{
									sdl = null;
									UtilJM.warner( "This coral overlaps the field edge." );

									return;
								}
								// If the above two conditions are OK:
								if ( sdl != null )
								{
									sdlSet.add( sdl );									
									prevSdl = sdl;
									break;
								}
							}
						}
						else // Mouse3
						{
							if( prevSdl.getDiameter() < 1 )
							{
								UtilJM.warner( "Right click repeat requires previous selection.");
								return;
							}
							
							sdl = new SpeciesIdDiameterLoc
								  (
									  prevSdl.getSpeciesId(), 
									  prevSdl.getDiameter(), 
									  loc 
								   ); 
							if( sdl.getDiameter() < 1 ) return;
							int radius = ( int ) Math.ceil( sdl.getDiameter() / 2.0 ); 
							
							boolean noOverlap = checkAllDistances( loc, radius ); 
							boolean noOffEdge = checkCoralToEdges( loc, radius );
						
							if ( !noOverlap ) // if overlapping coral(s)
							{
							    sdl = null;
							    UtilJM.warner( "This coral overlaps a previous one." );
								return;
							}
							if ( !noOffEdge ) // if overlapping one or more edges
							{
								sdl = null;
								UtilJM.warner( "This coral overlaps the field edge." );
								return;
							}
							// If the above two conditions are OK:
							if ( sdl != null )
							{
								sdlSet.add( sdl );									
								prevSdl = sdl;
							}
						}
				    
						if ( ( sdl.getDiameter() > 0 ) )
						{
							CircleXComponent rc 
								= new CircleXComponent
								( 
									xCoord, 
									yCoord, 
									sdl.getDiameter(), 
									mapIdColors.get( sdl.getSpeciesId() ) 
								);
							panel.add(rc);
							panel.revalidate();
							panel.repaint();
						}
						//System.out.println( xCoord +","+ yCoord );//these coords are relative to the component

					}
				}

			@Override
			public void mousePressed( MouseEvent e ) {}

			@Override
			public void mouseReleased( MouseEvent e ) {}

			@Override
			public void mouseEntered ( MouseEvent e ) {}

 			@Override
			public void mouseExited  ( MouseEvent e ) {}

			}
		);
			
		panel.setLayout       ( new BorderLayout() ); // this seems to be necessary to get corals to appear
		panel.setBackground   ( Color.white );
		
		return panel;
	}
	
	JPanel makePlantTextPanel ()
	{
		JPanel panel      = new JPanel ( new BorderLayout () );
		JPanel textPane   = new JPanel ();
		JPanel buttonPane = new JPanel ();
		JLabel text       = new JLabel ();
		JButton start     = new JButton( "Start Planting" );
		JButton clear     = new JButton( "Start Over"     ); 
		JButton done      = new JButton( "Done Planting"  ); 
		
		// see https://www.codejava.net/java-core/the-java-language/java-8-lambda-listener-example
		start.addActionListener(e -> okToPlant = true );
		clear.addActionListener
		(
			e -> {
					sdlSet.clear();
					planter.removeAll(); // This removes the clickablePane()
					planter.revalidate();
					planter.repaint();
					planter.add( clickablePane() );
				 }	
		);
		done .addActionListener
		(
			e -> { 
					okToPlant = false;
					start.setEnabled(false); 
					clear.setEnabled(false);
					//System.out.println( "\n" + sdlSet );
				 }
		);
		
		Font font = new Font("Arial", Font.BOLD, 10);
		start.setFont( font );
		clear.setFont( font );
		done .setFont( font );
		buttonPane.setBackground( Color.white );
		buttonPane.add( start );
		buttonPane.add( clear );
		buttonPane.add( done  );
		text.setText( "Before running, click on the panel where you want to plant something.");
		textPane.setBackground( Color.white );
		textPane.add( text );
		textPane.setFont( font );
		panel.add( buttonPane, BorderLayout.NORTH  );
		panel.add( textPane,   BorderLayout.CENTER );
		
		return panel;
	}
	
	/**
	 * Used to ensure planted corals do not overlap
	 * 
	 * @param newLoc
	 * @param newRadius
	 * @return
	 */
	
	static boolean checkAllDistances( Int2D newLoc, int newRadius )
	{
		boolean ok = true;
		
		// if sdlSet is empty, this will be skipped and the return will be ok == true
		if ( sdlSet.isEmpty() ) return ok;
		
		for( SpeciesIdDiameterLoc sdl : sdlSet )
		{
			Int2D loc  = sdl.getLoc();
			int radius = (int) Math.ceil( sdl.getDiameter() / 2.0 );

			ok = checkCoralDist( loc, radius, newLoc, newRadius );
			if( !ok ) break; 
		}
		
		return ok;
	}
	
	/**
	 *  Used with checkAllDistances(...)
	 *  
	 * @param loc1
	 * @param radius1
	 * @param loc2
	 * @param radius2
	 * @return
	 */

	static boolean checkCoralDist( Int2D loc1, int radius1, Int2D loc2, int radius2 )
	{
		boolean ok = false;
		
		int x1      = loc1.x;
		int y1      = loc1.y;
		int x2      = loc2.x;
		int y2      = loc2.y;		
		
		double sqrDistNeeded = ( radius1 + radius2 ) * ( radius1 + radius2 );

		// In 2D, euclidean distance squared = hypotenuse squared = x * x + y * y
		double xDist         =  x1 - x2;
		double yDist         =  y1 - y2;
		double sqrDistActual = xDist * xDist + yDist * yDist;
		
		if ( sqrDistNeeded <= sqrDistActual ) ok = true;
		
		return ok;
	}
	
	static boolean checkCoralToEdges( Int2D loc, int radius )
	{
		boolean ok = true;
		
		if ( 	   ( ( loc.x + radius ) > 400 ) 
				|| ( ( loc.x - radius ) < 0   )
				|| ( ( loc.y + radius ) > 400 ) 
				|| ( ( loc.y - radius ) < 0   )
			)
		{
			ok = false;
		}
		
		return ok;
	}
	
	static JPanel getCenterDisplayPane() {
		return centerDisplayPane;
	}

	public static int getClickWidth() {
		return clickWidth;
	}

	public static int getClickHeight() {
		return clickHeight;
	}
	
	public static Console getConsole() {
		return console;
	}
	
}
