package mcmanusjw.CoralPatchSimJuly2019;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import javax.swing.JComponent;

@SuppressWarnings("serial")
final class CircleXComponent extends JComponent
{
    private int x, y, diameter;
    private Color color;

    CircleXComponent( int x, int y, int diameter, Color color )
    {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.color = color;        
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        //Color c = new Color( 1.0F, 0.0F, 1.0F);
        g.setColor( color );
        
        // Get center from upper left corner x and y
        int xCenter = x - (int) ( diameter / 2 );
        int yCenter = y - (int) ( diameter / 2 );

        if ( diameter > 10)
        {
        	g.fillOval( xCenter, yCenter, diameter, diameter );
        }
        else // Make 10 by 10 'X' if circle too small
        {
        	int x1 = xCenter - 5;
        	int y1 = yCenter - 5;
        	int x2 = x1 + 10;
        	int y2 = y1 + 10;
        	int x3 = x2;
        	int y3 = y1;
        	int x4 = x1;
        	int y4 = y2;
        	
        	Graphics2D g2 = ( Graphics2D ) g;
            g2.setStroke( new BasicStroke( 3 ) );
        	
        	g2.drawLine( x1, y1, x2, y2 );
        	g2.drawLine( x3, y3, x4, y4 );
        }
    }
}


