Digital Doily Coursework: Information
-------------------------------------
The main entry point for this application is the DigitalDoily class. In here all of the constants are located for setting the initial parameters
for the drawing, such as default pen colour, size etc.

My approach for this was to store the drawing as a stack of strokes inside a Path2D object. This allows me to easily draw the strokes onto
the panel by iterating through the stack and prevents unnecessary redundancy of data for each point.
However, the strokes are never drawn directly to the panel, instead they are drawn to a BufferedImage which is then drawn to the panel to aid performance.
A full redraw is done (iterating through the stack) whenever an undo is called, the number of sectors is changed or the window is resized.
The last one is due to the fact that strokes can be recorded if the user drags the mouse outside the window, and the BufferedImage does not draw this initially.

One interesting implementation choice I have made is to model the Gallery as a series of JToggleButtons, as this allows the functionality of the selection to be altered easily.
For instance, currently all of these JToggleButtons are added to a button group, so that only one image can be selected at a time. If the desired behaviour is for multiple selections,
the button group simply needs removing.