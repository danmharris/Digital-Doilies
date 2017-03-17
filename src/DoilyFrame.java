import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * JFrame which contains and sets up various components, but does not control the actual drawing (this is handled by DoilyPanel)
 * @author dmh2g16
 *
 */
public class DoilyFrame extends JFrame{	
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
		sidePanel.addComponentListener(new ComponentAdapter(){
			public void componentResized(java.awt.event.ComponentEvent e) {
				removeBtn.repaint(); // Prevents glitch on button on resize
			};
		});
		
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
