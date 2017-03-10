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
	private JButton undoBtn; // Reverses actions taken
	private JButton clearBtn; // Erases the drawing
	private JLabel sectorLbl; // Label attached to sectorSpin
	private JSpinner sectorSpin; // Controls number of sectors visible
	private JLabel sizeLbl; // Attached to sizeSlider
	private JSlider sizeSlider; // Controls the size of the pen
	private JButton colourBtn; // Button to allow pen colour to be changed
	private JCheckBox sectorToggle; // Toggle whether sector lines should be drawn
	private JCheckBox reflectToggle; // Toggle whether drawn points should be reflected in each sector
	private JButton saveBtn; // Saves current image state into the gallery
	private JButton removeBtn; // Removes selected image from the gallery
	
	private JPanel galleryPanel; // Holds all the gallery images
	private JScrollPane galleryScroll; // Allows galleryPanel to scroll
	private ButtonGroup imageGroup = new ButtonGroup(); // Button group for gallery image selection logic
	private ArrayList<GalleryImage> images = new ArrayList<GalleryImage>(); // Images stored in gallery
	
	/**
	 * Constructs a new frame with default title Digital Doily
	 */
	public DoilyFrame(){
		super("Digital Doily");
	}
	
	/**
	 * Attaches listeners to all buttons as anonymous classes (as only 1 line each). Most link to corresponding method
	 * in DoilyPanel.
	 * Requires initComponents to be run first
	 */
	private void attachListeners(){
		clearBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dp.clear();
			}
		});
		
		undoBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dp.undo();
			}
		});
		
		sectorSpin.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				dp.setSectorCount((int)sectorSpin.getValue());
			}
		});
		
		colourBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// JColorChoser that returns the selected colour. The default colour is provided as the current colour
				dp.setColour(JColorChooser.showDialog(null, "Select New Colour", dp.getColour()));
			}
		});
		
		sectorToggle.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				dp.setSectorLineVisible(sectorToggle.isSelected());
			}
		});
		
		reflectToggle.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				dp.setReflect(reflectToggle.isSelected());
			}
		});
		
		sizeSlider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				dp.setDiameter(sizeSlider.getValue());
			}
		});
		
		// Gallery logic more complex so separated into inner classes
		saveBtn.addActionListener(new SaveButtonListener());
		removeBtn.addActionListener(new RemoveButtonListener());
	}
	
	/**
	 * Instantiates all GUI components (not containers) and sets their properties.
	 * Default settings provided by constants specified in main class
	 */
	private void initComponents(){
		this.undoBtn = new JButton("Undo");
		undoBtn.setEnabled(false);
		
		this.clearBtn = new JButton("Clear");
		this.sectorLbl = new JLabel("Sector Count");
		sectorLbl.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.sectorSpin = new JSpinner(new SpinnerNumberModel(DigitalDoily.START_SECTOR_COUNT,2,24,1)); // Minimum value set as 2, max 24 with step 1
		this.sizeLbl = new JLabel("Size of pen:");
		sizeLbl.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.sizeSlider = new JSlider(2,20,DigitalDoily.START_DIAMETER);
		sizeSlider.setMinorTickSpacing(1);
		sizeSlider.setPaintTicks(true);
		
		this.colourBtn = new JButton("Colour");
		this.sectorToggle = new JCheckBox(": Show Sector Lines");
		sectorToggle.setSelected(DigitalDoily.START_SHOW_SECTOR_LINES);
		
		this.reflectToggle = new JCheckBox(": Reflect Drawing");
		this.reflectToggle.setSelected(DigitalDoily.START_REFLECT_DRAWING);
		
		this.saveBtn = new JButton("Save");
		this.removeBtn = new JButton("Remove");
		dp = new DoilyPanel(undoBtn);
		
		// Gallery panel initialised and attached to scroll pane
		this.galleryPanel = new JPanel();
		this.galleryPanel.setLayout(new BoxLayout(galleryPanel, BoxLayout.PAGE_AXIS));
		this.galleryScroll = new JScrollPane(galleryPanel);
		this.galleryScroll.setPreferredSize(new Dimension(220, 700));
		this.galleryScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	
	/**
	 * Sets up the layout for the GUI and adds components to it.
	 * Requires initComponents to be run first.
	 */
	private void addComponents(){
		Container panel = this.getContentPane();
		panel.setLayout(new BorderLayout());		
		
		// Creates grid layout to combine the sizeSlider and its label
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new GridLayout(2,1));
		sliderPanel.add(sizeLbl);
		sliderPanel.add(sizeSlider);
		
		// Creates grid layout to combine sectorSpin with its label
		JPanel sectorPanel = new JPanel();
		sectorPanel.setLayout(new GridLayout(2,1));
		sectorPanel.add(sectorLbl);
		sectorPanel.add(sectorSpin);
		
		// Master controls including undoing and clearing the image
		JPanel masterControlPanel = new JPanel();
		masterControlPanel.setLayout(new GridLayout(2,1));
		masterControlPanel.add(undoBtn);
		masterControlPanel.add(clearBtn);
		
		// This panel combines the two toggle buttons together
		JPanel togglePanel = new JPanel();
		togglePanel.setLayout(new GridLayout(2,1));
		togglePanel.add(sectorToggle);
		togglePanel.add(reflectToggle);		
		
		// Main control panel which includes all the panels above and remaining buttons
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		controlPanel.add(masterControlPanel);
		controlPanel.add(sectorPanel);
		controlPanel.add(sliderPanel);
		controlPanel.add(colourBtn);
		controlPanel.add(togglePanel);
		controlPanel.add(saveBtn);
		
		// Side panel which contains the gallery scroll pane and the remove button
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new BorderLayout());
		sidePanel.add(galleryScroll,BorderLayout.CENTER);
		sidePanel.add(removeBtn, BorderLayout.SOUTH);		
		
		// Adding Containers to main panel
		panel.add(dp,BorderLayout.CENTER);
		panel.add(sidePanel,BorderLayout.EAST);
		panel.add(controlPanel, BorderLayout.SOUTH);		
	}
	
	/**
	 * Initialises the GUI, this is the only method which needs to be run as calls individual steps (why others private)
	 */
	public void init(){
		// Setting Frame Parameters
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(800,600);
		this.setMinimumSize(new Dimension(700,500));
		//this.setResizable(false);
		
		// Component initialisation
		this.initComponents();
		this.attachListeners();
		this.addComponents();
		
		// Shows Frame
		this.setVisible(true);
	}
	
	/**
	 * Handles the logic for what to do when clicking the save button (adding to gallery etc.)
	 * @author dan
	 *
	 */
	class SaveButtonListener implements ActionListener{
		
		/**
		 * When the save button is pressed, a new GalleryImage object is created that scales the image down,
		 * then adds it to the gallery panel, button group and the array of images.
		 * If limit reached it disables the save button.
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			GalleryImage image = new GalleryImage(dp, galleryScroll.getWidth()-20);
			galleryPanel.add(image);
			images.add(image);
			imageGroup.add(image);
			galleryScroll.revalidate();
			galleryScroll.repaint();
			if (images.size() == 12){
				saveBtn.setEnabled(false);
			}
		}
	}
	
	/**
	 * Handles the logic when clicking the remove button to delete an image from the gallery
	 * @author dan
	 *
	 */
	class RemoveButtonListener implements ActionListener{
		
		/**
		 * Iterates through all the gallery images until the selected one is found, and removes it from the panel,
		 * array list and button group.
		 * Re-enables the save button if previously disabled
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Iterator<GalleryImage> it = images.iterator();
			while (it.hasNext()){
				GalleryImage gi = it.next();
				if (gi.isSelected()){
					it.remove();
					imageGroup.remove(gi);
					galleryPanel.remove(gi);
					galleryScroll.revalidate();
					galleryScroll.repaint();
					if (!saveBtn.isEnabled()){
						saveBtn.setEnabled(true);
					}
				}
			}
		}
	}

	
}
