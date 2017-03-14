import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * JFrame which contains and sets up various components, but does not control the actual drawing (this is handled by DoilyPanel)
 * @author dmh2g16
 *
 */
public class DoilyFrame extends JFrame{
	private DoilyPanel dp; // Panel where drawing takes place	
	
	/**
	 * Constructs a new frame with default title Digital Doily
	 */
	public DoilyFrame(){
		super("Digital Doily");
	}
	

	/**
	 * Initialises the GUI by instantiating all components and laying them out correctly
	 */
	public void init(){
		// Setting Frame Parameters
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(800,600);
		this.setMinimumSize(new Dimension(700,500));
		this.setLayout(new BorderLayout());		
		
		DoilyPanel dp = new DoilyPanel(); // DoilyPanel used to display DoilyDrawing
		
		// Side panel layout
		JButton removeBtn = new JButton("Remove");
		GalleryScrollPanel gp = new GalleryScrollPanel();
		removeBtn.addActionListener(e->gp.removeSelectedFromGallery());
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new BorderLayout());
		sidePanel.add(gp,BorderLayout.CENTER);
		sidePanel.add(removeBtn, BorderLayout.SOUTH);
		
		// Control panel layout
		ControlPanel cp = new ControlPanel(dp,gp);
		dp.getDoilyDrawing().setUndoBtn(cp.getUndoBtn());
		
		this.add(dp, BorderLayout.CENTER);
		this.add(cp, BorderLayout.SOUTH);
		this.add(sidePanel,BorderLayout.EAST);
		
		
		// Shows Frame
		this.setVisible(true);
		dp.init();
	}
	
}
