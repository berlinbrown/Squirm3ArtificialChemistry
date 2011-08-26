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

// SquirmCellSlot.java

/**
 * Cell Slot.
 */
public class SquirmCellSlot {

    protected boolean has_occupant;
    protected SquirmCell occupant;

    // Public constructor initializes size of grid
    public SquirmCellSlot() {
        has_occupant = false;
    }

    public void makeOccupied(SquirmCell occ) {
        // !has_occupant is a necessary condition for calling this function
        if (has_occupant)
            throw new Error("SquirmCellSlot::makeOccupied : already occupied!");

        has_occupant = true;
        occupant = occ;
    }

    public void makeEmpty() {
        has_occupant = false;
    }

    public boolean queryEmpty() {
        return !has_occupant;
    }

    public SquirmCell getOccupant() {
        // has_occupant is a necessary condition for calling this function
        if (!has_occupant)
            throw new Error("SquirmCellSlot::getOccupant : no occupant!");

        return occupant;
    }
} // End of the Class //