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
 */
package org.squirm.chem;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

/**
 * Main entry point.
 * @author bbrown
 *
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);
    
    /**
     * Main entry point.
     */
    public static void main(final String [] args) {        
        LOGGER.info(">>> Running");
        final Squirm frame = new Squirm();
        frame.setTitle("Squirm Artificial Chemistry");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(20, 20);    
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setResizable(false);
        frame.setFocusable(true);        
        frame.setVisible(true);  
        frame.init();
        frame.start();        
        LOGGER.info(">>> Done");       
    }
    
} // End of the class //
