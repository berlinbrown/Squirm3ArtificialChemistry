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
 */
package org.squirm.chem;

// SquirmChemistry.java

import java.util.Enumeration;
import java.util.Vector;

class SquirmChemistry {

    private static final double P_R5 = 0.0002; // probability of R5 happening
    private static final double P_R6 = 0.0001; // probability of R6 happening
    private static final double P_R7 = 0.01; // probability of R7 happening

    private double P_cosmic = 0.0;

    private Vector reactions;

    public SquirmChemistry() {
        reactions = new Vector();
    }

    public void removeAllReactions() {
        reactions.removeAllElements();
    }

    public void addReaction(SquirmReaction r) {
        reactions.addElement(r);
    }

    public void setCosmicStateProb(double cosmic) {
        P_cosmic = cosmic;
    }

    public void react(SquirmCellSlot cell_grid[][], SquirmCell cell, Vector neighbours) {
        // try all the reactions in turn
        for (Enumeration e = reactions.elements(); e.hasMoreElements();) {
            tryReaction(cell_grid, cell, neighbours, (SquirmReaction) e.nextElement());
        }
    }

    private static void tryReaction(SquirmCellSlot cell_grid[][], SquirmCell cell, Vector neighbours, SquirmReaction r) {
        tryReaction(cell_grid, cell, neighbours, r.us_type, r.us_state, r.current_bond, r.them_type, r.them_state,
                r.future_us_state, r.future_bond, r.future_them_state);
    }

    private static void tryReaction(SquirmCellSlot cell_grid[][], SquirmCell cell, Vector neighbours, char us_type,
            int us_state, boolean current_bond, char them_type, int them_state, int future_us_state,
            boolean future_bond, int future_them_state) {
        
        // us_type is one of {e,f,a,b,c,d,x}
        if (us_type != 'e' && us_type != 'f' && us_type != 'a' && us_type != 'b' && us_type != 'c' && us_type != 'd'
                && us_type != 'x')
            throw new Error("SquirmChemistry::Reaction() : invalid us_type");

        // them_type is one of {e,f,a,b,c,d,x,y}
        if (them_type != 'e' && them_type != 'f' && them_type != 'a' && them_type != 'b' && them_type != 'c'
                && them_type != 'd' && them_type != 'x' && them_type != 'y')
            throw new Error("SquirmChemistry::Reaction() : invalid them_type");

        // sanity check on the states requested
        if (us_state < 0 || them_state < 0 || future_us_state < 0 || future_them_state < 0)
            throw new Error("SquirmChemistry::tryReaction() : states less than zero not permitted");

        // are we the right kind of cell for this reaction?
        if ((us_type != 'x' && cell.isTypeAndState(us_type, us_state)) || (us_type == 'x' && cell.isState(us_state))) {
            // do we have a neighbour (bonded/not) that is the right kind for
            // this reaction?
            Vector search_from = current_bond ? cell.getBonds() : neighbours;
            Vector ns;
            // if them_type specified then search for it
            if (them_type != 'x' && them_type != 'y')
                ns = getThoseOfTypeAndState(search_from, them_type, them_state);
            // if unspecified but to be same as us_type then search for it
            else if (them_type == 'x' && us_type == 'x')
                ns = getThoseOfTypeAndState(search_from, cell.getType(), them_state);
            // must be unspecified
            else if ((them_type == 'x' && us_type != 'x') || them_type == 'y')
                ns = getThoseOfState(search_from, them_state);
            else
                throw new Error("SquirmChemistry::tryReaction() : unexpected case statement");
            // try the reaction on each of the possibles
            for (Enumeration e = ns.elements(); e.hasMoreElements();) {
                SquirmCell n = (SquirmCell) e.nextElement();
                // reactions can happen if the two cells are right next to each
                // other
                // (share a face)
                // or over a diagonal (share a corner) if the other diagonal
                // doesn't
                // have a bond
                boolean can_react = false;

                if (rightNextToEachOther(cell, n))
                    can_react = true;
                else {
                    // if either other diagonal square is empty then OK
                    if (cell_grid[cell.getX()][n.getY()].queryEmpty() || cell_grid[n.getX()][cell.getY()].queryEmpty())
                        can_react = true;
                    else {
                        // otherwise, if there is no bond between diagonals then
                        // still OK
                        SquirmCell cellA = cell_grid[cell.getX()][n.getY()].getOccupant();
                        SquirmCell cellB = cell_grid[n.getX()][cell.getY()].getOccupant();
                        if (!cellA.getBonds().contains(cellB))
                            can_react = true;
                    }
                }

                if (can_react) {
                    // make or break bonds as specified
                    if (current_bond && !future_bond)
                        cell.breakBondWith(n);
                    else if (!current_bond && future_bond)
                        cell.makeBondWith(n);
                    // set our states to their new values
                    cell.setState(future_us_state);
                    n.setState(future_them_state);

                    break;
                }
            }
        }
    }

    private static Vector getThoseOfTypeAndState(Vector cells, char type, int state) {
        return getThoseOfTypeAndState(cells, SquirmCellProperties.getType(type), state);
    }

    private static Vector getThoseOfTypeAndState(Vector cells, int type, int state) {
        // do any of these cells match the type and state specified?
        // if so return all that match (empty list if none)

        Vector v = new Vector();
        SquirmCell c;
        for (Enumeration enumx = cells.elements(); enumx.hasMoreElements();) {
            c = ((SquirmCell) enumx.nextElement());
            if (type != -1) {
                if (c.isTypeAndState(type, state))
                    v.addElement(c);
            } else if (c.isState(state))
                v.addElement(c);
        }

        return v;
    }

    private static Vector getThoseOfState(Vector cells, int state) {
        // do any of these cells match the state specified? (any type)
        // if so return all that match (empty list if none)

        Vector v = new Vector();
        SquirmCell c;
        for (Enumeration enumx = cells.elements(); enumx.hasMoreElements();) {
            c = ((SquirmCell) enumx.nextElement());
            if (c.isState(state))
                v.addElement(c);
        }

        return v;
    }

    private static boolean nextToEachOther(SquirmCell cell1, SquirmCell cell2) {
        return (Math.abs(cell1.getX() - cell2.getX()) < 2 && Math.abs(cell1.getY() - cell2.getY()) < 2);
    }

    private static boolean rightNextToEachOther(SquirmCell cell1, SquirmCell cell2) {
        return (Math.abs(cell1.getX() - cell2.getX()) + Math.abs(cell1.getY() - cell2.getY()) < 2);
    }

} // End of the class //