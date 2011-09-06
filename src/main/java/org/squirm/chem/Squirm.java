/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright Tim Hutton and Berlin Brown <berlin dot brown at gmail.com> 2011
 *  
 * Tim Hutton is the original author, but a license not provided in source,
 * GPL was used for similar projects.  If Tim or anyone else has questions, please contact Berlin Brown.
 * 
 * http://www.sq3.org.uk/Evolution/Squirm3/
 * Squirm3 is an artificial chemistry simulation 
 */
package org.squirm.chem;

import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

/**
 * Main Class for JFrame Squirm Java Graphics Component. 
 */
public class Squirm extends JFrame implements Runnable {
      
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(Squirm.class);
    
    /**
     * THREAD SUPPORT: m_Squirm is the Thread object for the applet
     */
    private Thread m_Squirm = null;

    protected SquirmGrid squirmGrid;

    private static SquirmChemistry chemistry;

    protected final int grid_size_x = 50;
    protected final int grid_size_y = 50;
    protected final int drawing_size_x = 800;
    protected final int drawing_size_y = 600;
    protected final float scale = drawing_size_x / (float) grid_size_x;

    protected Image offscreenImage = null;
    protected Graphics off_g = null;

    private static final int FAST = 1;
    private int delay = 240;
    private boolean paused = false;
    private int draw_every = 1;

    // When an internal error has occurred this string is set to report the
    // problem
    private String error_msg;
    private boolean error_thrown = false;

    // When the mouse is moved around the pointed-at cell can be inspected by
    // the user (esp when paused)
    private String inspect_msg = "x";
    private int inspect_msg_x = 20, inspect_msg_y = 20;

    private String current_cell;
    private long counter = 0;

    /**
     * Squirm Class Constructor
     */
    public Squirm() {
        try {
            squirmGrid = new SquirmGrid(grid_size_x, grid_size_y);
            chemistry = new SquirmChemistry();
        } catch (Error e) {
            error_thrown = true;
            error_msg = e.getMessage();
        }
        error_msg = new String();
    }

    public void togglePaused() {
        paused = !paused;
    }

    public void setDelay(int d) {
        delay = d;
    }

    public void setFloodOnOff(boolean on) {
        squirmGrid.setFloodOnOff(on);
    }

    public void setFloodPeriod(int period) {
        squirmGrid.setFloodPeriod(period);
    }

    public void setDrawOnlyEvery(int every) {
        draw_every = every;
    }

    public void removeAllReactions() {
        chemistry.removeAllReactions();
        error_msg = "";
    }

    public void addReaction(String us_type, int us_state, boolean current_bond, String them_type, int them_state,
            int future_us_state, boolean future_bond, int future_them_state) {

        final SquirmReaction r = new SquirmReaction(us_type.toCharArray()[0], us_state, current_bond,
                them_type.toCharArray()[0], them_state, future_us_state, future_bond, future_them_state);
        chemistry.addReaction(r);

        // DEBUG: show each received reaction
        final String msg = us_type + us_state + (current_bond ? "-" : " ") + them_type + them_state + " => " + us_type
        + future_us_state + (future_bond ? "-" : " ") + them_type + future_them_state + "; ";
        error_msg += msg;
        final String addReaction = msg;
        LOGGER.info("Adding reaction allowed on the grid (init) : " + addReaction);        
    }

    /**
     * APPLET INFO SUPPORT: The getAppletInfo() method returns a string
     * describing the applet's author, copyright date, or miscellaneous
     * information.
     */
    public String getAppletInfo() {
        return "Name: Squirm\r\n" + "Author: Tim Hutton";
    }

    /**
     * The init() method is called by the AWT when an applet is first loaded or
     * reloaded. Override this method to perform whatever initialization your
     * applet needs, such as initializing data structures, loading images or
     * fonts, creating frame windows, setting the layout manager, or adding UI
     * components.
     */
    public void init() {
        // If you use a ResourceWizard-generated "control creator" class to
        // arrange controls in your applet, you may want to call its
        // CreateControls() method from within this method. Remove the following
        // call to resize() before adding the call to CreateControls();
        // CreateControls() does its own resizing.
        resize(drawing_size_x, drawing_size_y);

        if (offscreenImage == null) {
            offscreenImage = createImage(drawing_size_x, drawing_size_y);
            // may need gjt.Util.waitForImage(this, offscreenImage);
            off_g = offscreenImage.getGraphics();
        }

        // ------------ manually added reactions --------------------------

        // DEBUG: add some reactions directly (have disabled the javascript
        // interface)

        removeAllReactions();

        // which reaction-set do you want?
        final int typeOfReactionAllowedEditableFromSrc = 0;
        switch (typeOfReactionAllowedEditableFromSrc) {
        case 0: {
            // new slimline replication reactions (for: e8-a1-b1-...-f1)
            addReaction("e", 8, false, "e", 0, 4, true, 3); // R1
            addReaction("x", 4, true,  "y", 1, 2, true, 5); // R2
            addReaction("x", 5, false, "x", 0, 7, true, 6); // R3
            addReaction("x", 3, false, "y", 6, 2, true, 3); // R4
            addReaction("x", 7, true,  "y", 3, 4, true, 3); // R5
            addReaction("f", 4, true,  "f", 3, 8, false, 8); // R6
            addReaction("x", 2, true,  "y", 8, 9, true, 1); // R7
            addReaction("x", 9, true,  "y", 9, 8, false, 8); // R8
            break;
        }
        case 1: {
            // genes as instructions (for: e1-a1-b1-...-f1)
            addReaction("e", 1, false, "e", 0, 4, true, 3); // R1
            addReaction("x", 4, true,  "y", 1, 2, true, 5); // R2
            addReaction("x", 5, false, "x", 0, 7, true, 6); // R3
            addReaction("x", 3, false, "y", 6, 2, true, 3); // R4
            addReaction("x", 7, true,  "y", 3, 4, true, 3); // R5
            addReaction("f", 4, true,  "f", 3, 8, false, 8); // R6
            addReaction("x", 2, true,  "y", 8, 9, true, 10); // R7
            addReaction("x", 9, true,  "y", 9, 8, false, 8); // R8

            addReaction("f", 10, true, "x", 10, 1, true, 11); // R9
            addReaction("x", 12, true, "y", 10, 1, true, 11); // R10
            addReaction("e", 8, true,  "x", 12, 1, true, 1); // R11

            // some catalysing reactions to try
            addReaction("a", 11, false, "b", 0, 12, false, 1); // R12?
            addReaction("b", 11, false, "a", 0, 12, false, 0); // R13?
            addReaction("c", 11, false, "d", 0, 12, false, 0); // R14?
            addReaction("d", 11, false, "d", 0, 12, false, 0); // R15?
            break;
        }
        case 5: // Variant 5 replication reactions
        {
            // -- the reactions for self-replicating strings --
            addReaction("e", 1, false, "e", 0, 4, true, 10); // R1
            addReaction("x", 4, true, "y", 1, 2, true, 5); // R2
            addReaction("x", 5, false, "x", 0, 7, true, 6); // R3
            addReaction("x", 10, false, "y", 6, 3, true, 10); // R4
            addReaction("x", 7, true, "y", 10, 4, true, 10); // R5
            addReaction("f", 4, true, "f", 10, 8, false, 8); // R6
            addReaction("x", 2, true, "y", 8, 9, true, 1); // R7
            addReaction("x", 3, true, "y", 8, 9, true, 1); // R8
            addReaction("x", 9, true, "y", 9, 8, false, 8); // R9
            addReaction("e", 8, true, "x", 1, 1, true, 1); // R10
            break;
        }
        case 9: // Variant 9 replication and membrane reactions
        {
            // pre-duplication hurdles (to slow things down)
            addReaction("e", 1, false, "d", 0, 37, false, 0);
            addReaction("e", 37, false, "c", 0, 38, false, 0);
            // start of duplication
            addReaction("e", 38, false, "a", 11, 5, true, 10); // R8 // was 4,
            // not 11 (slows
            // down+tidies)
            addReaction("a", 10, true, "a", 4, 10, true, 11); // R9
            addReaction("a", 11, false, "e", 6, 13, true, 3); // R10
            // mid-duplication
            addReaction("x", 4, true, "y", 1, 2, true, 5); // R1
            addReaction("x", 5, false, "x", 0, 7, true, 6); // R2
            addReaction("x", 3, false, "y", 6, 2, true, 3); // R3
            addReaction("x", 7, true, "x", 3, 4, true, 3); // R4
            // start of splitting
            addReaction("f", 4, false, "a", 4, 8, true, 10); // R7a
            addReaction("f", 3, false, "a", 11, 8, false, 12); // R7b
            // mid-splitting
            addReaction("x", 8, true, "y", 8, 9, false, 9); // R5
            addReaction("x", 9, true, "y", 2, 1, true, 8); // R6

            // start of pulling
            addReaction("a", 10, true, "f", 1, 19, true, 12); // R18
            addReaction("a", 12, false, "f", 11, 20, true, 21); // R19
            addReaction("a", 19, true, "f", 21, 19, true, 11); // R20
            addReaction("a", 20, false, "x", 13, 15, true, 14); // R21
            // mid-pulling
            addReaction("x", 12, true, "y", 1, 11, true, 13); // R12
            addReaction("a", 9, false, "x", 13, 15, true, 14); // R13
            addReaction("a", 15, true, "x", 11, 16, false, 17); // R14
            addReaction("a", 16, true, "x", 14, 9, true, 12); // R15
            addReaction("x", 17, true, "y", 11, 1, true, 11); // R16
            // end of pulling
            addReaction("x", 12, true, "e", 9, 11, true, 13); // R17

            // membranes join
            addReaction("a", 15, true, "e", 12, 22, false, 13); // R23
            addReaction("a", 22, true, "a", 19, 22, true, 25); // R23b
            addReaction("a", 25, true, "f", 1, 26, false, 1); // R23c
            addReaction("a", 26, false, "a", 10, 24, true, 23); // R24

            // membranes separate
            addReaction("a", 22, true, "a", 24, 11, false, 4); // R25 // tried
            // with 11 here
            // instead of 4
            addReaction("a", 14, true, "a", 23, 27, false, 28); // R26

            // gene-strings are released into their new membranes
            addReaction("a", 27, true, "e", 9, 4, false, 1); // R27
            addReaction("a", 28, true, "e", 13, 30, false, 29); // R28
            addReaction("e", 29, true, "x", 17, 1, true, 1); // R29
            addReaction("a", 30, true, "a", 11, 4, true, 4); // R31

            // synthesis of a31's through contact of a0 with x1 or x2 (pretty
            // liberal)
            addReaction("x", 1, false, "a", 0, 1, false, 31); // R32
            addReaction("x", 2, false, "a", 0, 2, false, 31); // R32b

            // membrane growth
            addReaction("a", 4, false, "a", 31, 33, true, 32); // R33
            addReaction("a", 33, true, "a", 4, 34, true, 35); // R34
            addReaction("a", 35, false, "a", 32, 36, true, 4); // R35
            addReaction("a", 36, true, "a", 34, 4, false, 4); // R36
            break;
        }
        }

    }

    /**
     * Place additional applet clean up code here. destroy() is called when when
     * your applet is terminating and being unloaded.
     */
    public void destroy() {
    }

    /**
     * Squirm Paint Handler
     */
    public void paint(final Graphics g) {
        if (off_g == null) {
            return;
        }
        // Clear the background
        off_g.setColor(Color.white);
        off_g.fillRect(0, 0, drawing_size_x, drawing_size_y);

        // Draw the cells
        squirmGrid.draw(off_g, scale, delay <= FAST);

        // Show the result.
        g.drawImage(offscreenImage, 0, 0, this);
        counter++;
        if ((counter % 100) == 0) {
            LOGGER.info("Counter update : value=" + counter);
        }
    }

    /**
     * Override the default update method to call paint rather than clear and
     * paint.
     */
    public void update(Graphics g) {
        paint(g);
        if (error_thrown) {
            g.drawString(error_msg, 10, 100);
        }
        g.drawString(inspect_msg, inspect_msg_x, inspect_msg_y);
        g.drawString(current_cell, 20, 120);
    }

    /**
     * The start() method is called when the page containing the applet first
     * appears on the screen. The AppletWizard's initial implementation of this
     * method starts execution of the applet's thread.
     */
    public void start() {
        if (m_Squirm == null) {
            m_Squirm = new Thread(this);
            m_Squirm.start();
        }
    }

    /**
     * The stop() method is called when the page containing the applet is no
     * longer on the screen. The AppletWizard's initial implementation of this
     * method stops execution of the applet's thread.
     */
    public void stop() {
        if (m_Squirm != null) {
            m_Squirm = null;
        }
    }

    /**
     * THREAD SUPPORT The run() method is called when the applet's thread is
     * started. If your applet performs any ongoing activities without waiting
     * for user input, the code for implementing that behavior typically goes
     * here. For example, for an applet that performs animation, the run()
     * method controls the display of images.
     */
    public void run() {
        while (true) {
            try {
                // ask the squirm world to execute one time step
                try {
                    if (!paused) {
                        squirmGrid.doTimeStep(chemistry);
                    }
                } catch (Error e) {
                    error_msg = e.getMessage();
                    error_thrown = true;
                }

                if (squirmGrid.getCount() % draw_every == 0)
                    repaint();

                Thread.sleep(delay);
            } catch (final InterruptedException e) {
                // TODO: Place exception-handling code here in case an
                // InterruptedException is thrown by Thread.sleep(),
                // meaning that another thread has interrupted this one
                stop();
            }
        }
    }

    /**
     * MOUSE SUPPORT: The mouseMove() method is called if the mouse cursor moves
     * over the applet's portion of the screen and the mouse button isn't being
     * held down.
     */
    public boolean mouseMove(Event evt, int x, int y) {
        // find which slot we're pointing at
        int slot_x = (int) ((float) x / scale);
        int slot_y = (int) ((float) y / scale);
        inspect_msg = squirmGrid.getContents(slot_x, slot_y);
        inspect_msg_x = x;
        inspect_msg_y = y - 3;
        return true;
    }

} // End of the class //
