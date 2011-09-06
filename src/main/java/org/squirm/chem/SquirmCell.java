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
 * 
 * Squirm3 is an artificial chemistry simulation 
 */
package org.squirm.chem;

// SquirmCell.java

import java.awt.Color;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;


public class SquirmCell extends SquirmCellProperties {

    private static final Logger LOGGER = Logger.getLogger(SquirmCell.class);
    
    // / the cell's current location
    private int x, y;

    // / bonds is a list of all the cells currently bonded with
    private Vector<SquirmCell> bonds;

    // / which direction did we move in previously?
    private int last_x, last_y; // for momentum-style physics

    /*
     * encoding of an 8-neighbourhood: 1 2 3 0 8 4 7 6 5
     */
    private static final int EIGHT_x[] = { -1, -1, 0, 1, 1, 1, 0, -1, 0 };
    private static final int EIGHT_y[] = { 0, -1, -1, -1, 0, 1, 1, 1, 0 };

    private static final boolean momentum_style_physics = false;

    /**
     * Default constructor
     */
    public SquirmCell(int x_loc, int y_loc, int cell_type, int cell_state, Vector<SquirmCell> cell_list,
            SquirmCellSlot cell_grid[][]) {
        // initialize the superclass (SquirmCellProperties)
        super(cell_type, cell_state);
        if (cell_grid[x_loc][y_loc].queryEmpty()) {
            x = x_loc;
            y = y_loc;
            cell_list.addElement(this);
            cell_grid[x][y].makeOccupied(this);
            bonds = new Vector<SquirmCell>();

            if (momentum_style_physics) {
                // pick a movement direction at random
                last_x = EIGHT_x[(int) Math.floor(Math.random() * 8)];
                last_y = EIGHT_y[(int) Math.floor(Math.random() * 8)];
            }
        } else {
            // couldn't create! (square was occupied)
            throw new Error("SquirmCell::SquirmCell : couldn't create, square is occupied!");
        }
    }
    
    public String toString() {
        return super.toString();
    }

    /**
     * access function returning x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * access function returning y-coordinate
     */
    public int getY() {
        return y;
    }

    public final Vector<SquirmCell> getBonds() {
        return bonds;
    }

    /**
     * Draws the cell
     */
    public void draw(Graphics g, float scale, SquirmCellSlot cell_grid[][], boolean fast) {
        // draw ourselves
        // if(!fast || !isState(0))
        {
            g.setColor(getColour());
            g.fillRect((int) (x * scale), (int) (y * scale), (int) scale, (int) scale);
        }

        // draw our bonds
        g.setColor(Color.black);
        int hx, hy;
        hx = (int) ((x + 0.5) * scale);
        hy = (int) ((y + 0.5) * scale);
        for (final Enumeration<SquirmCell> e = bonds.elements(); e.hasMoreElements();) {
            final SquirmCell cell = (SquirmCell) e.nextElement();
            float tx, ty;
            tx = cell.getX();
            ty = cell.getY();
            tx = (tx - x) / 2.0F + x;
            ty = (ty - y) / 2.0F + y;
            int gx, gy;
            gx = (int) ((tx + 0.5) * scale);
            gy = (int) ((ty + 0.5) * scale);
            g.drawLine(hx, hy, gx, gy);
        }

        // draw our state (if enough room)
        if (scale >= 12) {
            final Integer i = new Integer(getState());
            final String type = this.getStringType();
            final String str = type + i.toString();
            //g.drawString(i.toString(), (int) ((x * scale) + 2), (int) ((y * scale) + scale - 2));
            g.drawString(str, (int) ((x * scale) + 2), (int) ((y * scale) + scale - 2));
        }
    }

    /**
     * find any reactions we can make
     */
    public void makeReactions(final SquirmChemistry chemistry, int n_x, int n_y, SquirmCellSlot cell_grid[][]) {
        // collect the unbonded neighbours of this cell (up to 8)
        final Vector<SquirmCell> neighbours = new Vector<SquirmCell>();
        int tx, ty;
        SquirmCellSlot slot;
        SquirmCell cell;
        for (int i = 0; i < 8; i++) {
            tx = x + EIGHT_x[i];
            ty = y + EIGHT_y[i];
            if (tx >= 0 && tx < n_x && ty >= 0 && ty < n_y) {
                slot = cell_grid[tx][ty];
                // does this cell slot contain a cell?
                if (!slot.queryEmpty()) {
                    cell = slot.getOccupant();
                    // is this cell unbonded with us?                    
                    if (!bonds.contains(cell)) {                        
                        neighbours.addElement(cell);
                    }
                }
            }
        }

        // see if this situation causes a reaction in the current chemistry
        chemistry.react(cell_grid, this, neighbours);
    }

    /**
     * remove this cell from the grid and updates any references to itself in
     * other cells
     */
    public void killSelf(Vector<SquirmCell> cell_list, SquirmCellSlot cell_grid[][]) {
        // remove self from the grid
        cell_grid[x][y].makeEmpty();
        // remove self from the list
        cell_list.removeElement(this);
        // remove any bonds
        while (!bonds.isEmpty())
            breakBondWith((SquirmCell) bonds.firstElement());
    }

    // ----------------------------------------------------------

    /**
     * move to an 8-neighbourhood empty square subject to all bonds being
     * maintained (8-connectivity)
     */
    public void makeMove(int n_x, int n_y, SquirmCellSlot cell_grid[][]) {
        if (momentum_style_physics) {
            // can we move in our previous direction?
            int tx, ty;
            tx = x + last_x;
            ty = y + last_y;
            if (tx >= 0 && tx < n_x && ty >= 0 && ty < n_y && cell_grid[tx][ty].queryEmpty()
                    && wouldMaintainBonds(tx, ty)) {
                // move there
                moveTo(tx, ty, cell_grid);
            } else {
                // pick a movement direction at random
                last_x = EIGHT_x[(int) Math.floor(Math.random() * 8)];
                last_y = EIGHT_y[(int) Math.floor(Math.random() * 8)];
            }
        } else {

            // which of the 8 possible moves is valid? (empty and maintains
            // bonds)
            boolean valid_move[] = new boolean[8];
            int n_valid_moves = 0;
            int tx, ty;
            for (int i = 0; i < 8; i++) {
                tx = x + EIGHT_x[i];
                ty = y + EIGHT_y[i];
                if (tx >= 0 && tx < n_x && ty >= 0 && ty < n_y && cell_grid[tx][ty].queryEmpty()
                        && wouldMaintainBonds(tx, ty)) {
                    valid_move[i] = true;
                    n_valid_moves++;
                } else
                    valid_move[i] = false;
            }

            // can only move if there is at least one valid move
            if (n_valid_moves > 0) {
                // pick a valid move at random (Brownie in motion style of
                // thing)
                int choices[] = new int[n_valid_moves];
                int j = 0;
                for (int i = 0; i < 8; i++)
                    if (valid_move[i])
                        choices[j++] = i;
                int which = (int) Math.floor(Math.random() * (float) n_valid_moves);
                int move = choices[which];

                // move there
                moveTo(x + EIGHT_x[move], y + EIGHT_y[move], cell_grid);
            }
        }
    }

    /**
     * bonded cells have a limited lifetime before they revert to an unbonded
     * state
     */
    public void ageSelf() {
        /*
         * time_since_last_reaction++;
         * 
         * if(time_since_last_reaction>MAX_AGE) { // we've got too old with
         * nothing happening, break our bonds and return to state 0 SquirmCell
         * cell; while(!bonds.isEmpty()) { cell =
         * (SquirmCell)bonds.firstElement(); // make them old too so the effect
         * propagates cell.time_since_last_reaction = time_since_last_reaction;
         * // break the bond we have with them breakBondWith(cell); // apply
         * ageSelf to them too cell.ageSelf(); } setState(0);
         * time_since_last_reaction=0; }
         */
    }

    /**
     * would a move to tx,ty break any bonds?
     */
    private boolean wouldMaintainBonds(int tx, int ty) {
        // if the x or y difference between this position and that of any
        // bonded-to cell is greater than 1
        // then this move would break a bond and is not valid

        SquirmCell cell;
        for (Enumeration<SquirmCell> e = bonds.elements(); e.hasMoreElements();) {
            cell = (SquirmCell) e.nextElement();
            if (Math.abs(tx - cell.getX()) > 1 || Math.abs(ty - cell.getY()) > 1)
                // if(Math.abs(tx-cell.getX())+Math.abs(ty-cell.getY())>1)
                return false;
        }
        return true;
    }

    /**
     * Link this cell to the other
     */
    public void makeBondWith(final SquirmCell other) {
        bonds.addElement(other);
        other.bonds.addElement(this);
        LOGGER.info("Making bond : us=" + this + " -> withThem=" + other  + " numberBondsUs=" + bonds.size());        
        
    }

    /**
     * Break the specified link
     */
    public void breakBondWith(SquirmCell other) {
        if (!bonds.contains(other))
            throw new Error("SquirmCell::breakBondWith : we have no such bond with them!");

        bonds.removeElement(other);

        if (!other.bonds.contains(this))
            throw new Error("SquirmCell::breakBondWith : they have no such bond with us!");

        other.bonds.removeElement(this);
    }

    /**
     * Moves the cell to a new location
     */
    private void moveTo(int new_x, int new_y, SquirmCellSlot cell_grid[][]) {
        // move there
        cell_grid[x][y].makeEmpty();
        x = new_x;
        y = new_y;
        cell_grid[x][y].makeOccupied(this);
    }

} // End of the class //