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

import org.apache.log4j.Logger;

// SquirmReaction.java

class SquirmReaction {

    private static final Logger LOGGER = Logger.getLogger(SquirmReaction.class);
    
    public char us_type;
    public int us_state;
    public boolean current_bond;
    public char them_type;
    public int them_state;
    public int future_us_state;
    public boolean future_bond;
    public int future_them_state;

    public SquirmReaction(char us_type, int us_state, boolean current_bond, char them_type, int them_state,
            int future_us_state, boolean future_bond, int future_them_state) {
        this.us_type = us_type;
        this.us_state = us_state;
        this.current_bond = current_bond;
        this.them_type = them_type;
        this.them_state = them_state;
        this.future_us_state = future_us_state;
        this.future_bond = future_bond;
        this.future_them_state = future_them_state;        
    }
};