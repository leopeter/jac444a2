package testFile;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

import java.util.*;

public class SliderSimple {
	
	public int stateChanged(ChangeEvent e) {
		int value = 0;
    	JSlider source = (JSlider)e.getSource();
    	if (!source.getValueIsAdjusting()) {
    		value = (int)source.getValue();
    	}
    	return value;
	}
	
	public static void main ( String args[] ) {
		JFrame frame = new JFrame("JSlider Demo");
		Container c = frame.getContentPane();
		c.setLayout( new BorderLayout() );
		
		JSlider hs = new JSlider( JSlider.HORIZONTAL, 0, 19, 2 );
		Hashtable h = new Hashtable();
		h.put( new Integer(2), new JLabel("2") );
		h.put( new Integer(8), new JLabel("8") );
		h.put( new Integer(14), new JLabel("14") );
		h.put( new Integer(19), new JLabel("19") );
		hs.setLabelTable(h);
		hs.setPaintLabels(true);
		c.add( hs, BorderLayout.SOUTH );
		
		JSlider vs = new JSlider( JSlider.HORIZONTAL, 1, 19, 15 );
		vs.setMajorTickSpacing(3);
		vs.setPaintLabels(true);
		c.add( vs, BorderLayout.NORTH );
		
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.pack();
		frame.setVisible(true);
		
		JLabel value = new JLabel();
		value.setText(Integer.toString(vs.getValue()));
		c.add( value, BorderLayout.SOUTH );
		
		//c.stateChanged(ChangeListener e);
		
	}
}
