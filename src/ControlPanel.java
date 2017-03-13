import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

public class ControlPanel extends JPanel{
	private DoilyPanel dp;
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

	public ControlPanel(DoilyPanel dp, GalleryScrollPanel gp){
		this.dp = dp;
		
		this.initComponents();
		this.attachListeners(gp);
		this.addComponents();
		
		
	}

	private void attachListeners(GalleryScrollPanel gp){
		DoilyDrawing dd = dp.getDoilyDrawing();
		clearBtn.addActionListener(e->dd.clear());
		undoBtn.addActionListener(e->dd.undo());
		sectorSpin.addChangeListener(e->dd.setSectorCount((int)sectorSpin.getValue()));
		colourBtn.addActionListener(e->dd.setColour(JColorChooser.showDialog(null, "Select New Colour", dd.getColour())));
		sectorToggle.addChangeListener(e->dd.setSectorLineVisible(sectorToggle.isSelected()));
		reflectToggle.addChangeListener(e->dd.setReflect(reflectToggle.isSelected()));
		sizeSlider.addChangeListener(e->dd.setDiameter(sizeSlider.getValue()));
		
		this.saveBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				gp.saveToGallery(dp);
				if (gp.isFull()){
					saveBtn.setEnabled(false);
				}
			}
			
		});
	}

	private void initComponents(){
		this.undoBtn = new JButton("Undo");
		undoBtn.setEnabled(false);

		this.clearBtn = new JButton("Clear");
		this.sectorLbl = new JLabel("Sector Count");
		sectorLbl.setHorizontalAlignment(SwingConstants.CENTER);

		this.sectorSpin = new JSpinner(new SpinnerNumberModel(DigitalDoily.START_SECTOR_COUNT,DigitalDoily.MIN_SECTORS,DigitalDoily.MAX_SECTORS,1)); // Minimum value set as 2, max 24 with step 1
		this.sizeLbl = new JLabel("Size of pen:");
		sizeLbl.setHorizontalAlignment(SwingConstants.CENTER);

		this.sizeSlider = new JSlider(DigitalDoily.MIN_DIAMETER,DigitalDoily.MAX_DIAMETER,DigitalDoily.START_DIAMETER);
		sizeSlider.setMinorTickSpacing(1);
		sizeSlider.setPaintTicks(true);

		this.colourBtn = new JButton("Colour");
		this.sectorToggle = new JCheckBox(": Show Sector Lines");
		sectorToggle.setSelected(DigitalDoily.START_SHOW_SECTOR_LINES);

		this.reflectToggle = new JCheckBox(": Reflect Drawing");
		this.reflectToggle.setSelected(DigitalDoily.START_REFLECT_DRAWING);

		this.saveBtn = new JButton("Save");
	}

	private void addComponents(){
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
		this.setLayout(new FlowLayout());
		this.add(masterControlPanel);
		this.add(sectorPanel);
		this.add(sliderPanel);
		this.add(colourBtn);
		this.add(togglePanel);
		this.add(saveBtn);
	}
	
	public JButton getSaveBtn(){
		return this.saveBtn;
	}
	
	public JButton getUndoBtn(){
		return this.undoBtn;
	}
}