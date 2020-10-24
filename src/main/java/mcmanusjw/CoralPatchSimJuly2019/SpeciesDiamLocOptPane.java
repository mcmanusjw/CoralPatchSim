package mcmanusjw.CoralPatchSimJuly2019;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import sim.util.Int2D;

public class SpeciesDiamLocOptPane 
{
	private SpeciesIdDiameterLoc sd;
	private int maxDiameter;
	private Int2D loc;
	
	SpeciesDiamLocOptPane( List < String > speciesNames, int maxDiameter, Int2D loc ) 
	{
		this.maxDiameter = maxDiameter;
		this.loc         = loc;
		
	    JFrame f = new JFrame("Options");
	    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(100, 100);
	    f.setResizable(false);
	    f.setLocationByPlatform(true);
	    f.setLocationRelativeTo(null);
	    f.setVisible(true);

	    sd = makeOptionPane( speciesNames, f );
	}

	SpeciesIdDiameterLoc makeOptionPane( List < String > speciesNames, JFrame frame )
	{
		// see: Sage88 answer to 
		// https://stackoverflow.com/questions/1291704/how-do-i-populate-a-jcombobox-with-an-arraylist
		String[] corals = speciesNames.toArray( new String[ speciesNames.size() ] );
		
		@SuppressWarnings("unused")
		String coralName = " ";
		int speciesId = 0;
		int diameter = 0;
        
		while( ( diameter < 1 ) || ( diameter > maxDiameter ) )
		{
			JPanel p = new JPanel(new BorderLayout(5,5));
			JPanel labels = new JPanel(new GridLayout(0,1,2,2));
			labels.add(new JLabel("Coral", SwingConstants.RIGHT));
			labels.add(new JLabel("Diameter", SwingConstants.RIGHT));
			p.add(labels, BorderLayout.WEST);

			JPanel controls = new JPanel(new GridLayout(0,1,2,2));
 
			JComboBox < String > coralList = new JComboBox < String > ( corals );
			coralList.setSelectedIndex(1);
			controls.add( coralList );
        
			NumberFormat numFormat = NumberFormat.getNumberInstance();
			numFormat.setMaximumFractionDigits( 0 );
			numFormat.setGroupingUsed(false);

			JFormattedTextField sizer = new JFormattedTextField( numFormat );
			//sizer.addAncestorListener( new RequestFocusListener( false ) );
			controls.add( sizer );
			p.add(controls, BorderLayout.CENTER);

			JOptionPane.showMessageDialog(
					frame, p, "Choices", JOptionPane.QUESTION_MESSAGE );
			
			coralName = ( String ) coralList.getSelectedItem();
			speciesId = coralList.getSelectedIndex();
			
			// For the following null check, see nom at 
			// https://stackoverflow.com/questions/30321611/in-java-how-can-i-avoid-blank-jtextfield-input
			if ( !sizer.getText().isEmpty() )
			{
				diameter = Integer.parseInt( sizer.getText() );
			}
			if ( ( sizer.getText().isEmpty() ) || ( diameter > maxDiameter ) )
			{
				diameter = 0; // catch this on return
				
				int a = JOptionPane.showConfirmDialog( frame, 
						"Put in an integer diameter less than 1/2 the patch min dimension to plant something\n"
						+ "                                                        Try again?", 
						"Warning", JOptionPane.YES_NO_OPTION );
				if ( !( a == JOptionPane.OK_OPTION ) ) break;
			}
		}
		
		frame.dispose();
        
       //System.out.println( coralName + ", " + speciesId + ", " + diameter );
       
       return new SpeciesIdDiameterLoc( speciesId, diameter, loc );
	}
	
	public SpeciesIdDiameterLoc getSd() {
		return sd;
	}
}
