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

// SquirmCellProperties.java

import java.awt.Color;

class SquirmCellProperties {
    
    private int type;
    private int state;
    protected int time_since_last_reaction = 0;
    
    private static final int MAX_TYPES = 6; // 0-5
    private static final int MAX_STATES = 11; // this is controversial right now

    protected static final int MAX_AGE = 1000;

    private Color TYPE_COLOURS[] = { Color.red.brighter(), Color.green, Color.orange, Color.gray,
            Color.cyan, Color.blue.brighter() };

    public static int getRandomType() {
        return (int) Math.floor(Math.random() * MAX_TYPES);
    }

    public int getRandomCodonType() {
        return (int) Math.floor(Math.random() * (MAX_TYPES - 2)) + 2;
    }

    public int getRandomState() {
        return (int) Math.floor(Math.random() * MAX_STATES);
    }

    public SquirmCellProperties(int t, int s) {
        if (t < 0 || t >= MAX_TYPES)
            throw new Error("SquirmCellProperties::SquirmCellProperties : type not in valid range");

        // if(s<0 || s>=MAX_STATES)
        // throw new
        // Error("SquirmCellProperties::SquirmCellProperties : state not in valid range");

        type = t;
        state = s;
    }

    public Color getColour() {
        return TYPE_COLOURS[type];
    }

    public int getType() {
        return type;
    }

    public int getState() {
        return state;
    }

    public String getStringType() {
        switch (type) {
        case 0:
            return "e";
        case 1:
            return "f";
        case 2:
            return "a";
        case 3:
            return "b";
        case 4:
            return "c";
        case 5:
            return "d";
        default:
            throw new Error("SquirmCellProperties::getStringType : type out of range");
        }
    }

    public int getTimeSinceLastReaction() {
        return time_since_last_reaction;
    }
    public boolean isType(int t) {
        return type == t;
    }

    public boolean isType(char t) {
        return type == getType(t);
    }

    public boolean isState(int s) {
        return state == s;
    }

    public boolean isTypeAndState(int t, int s) {
        return isType(t) && isState(s);
    }

    public boolean isTypeAndState(char t, int s) {
        return isType(t) && isState(s);
    }

    public void setState(int s) {
        // if this is a change then reset counter
        if (state != s) {
            time_since_last_reaction = 0;
            state = s;
        }
        // (else actually no change...)
    }

    public static int getType(char t) {
        switch (t) {
        case 'e':
            return 0;
        case 'f':
            return 1;
        case 'a':
            return 2;
        case 'b':
            return 3;
        case 'c':
            return 4;
        case 'd':
            return 5;
        default:
            throw new Error("SquirmCellProperties::getType : unknown type!");
        }
    }
    
    public String toString() {
        return "[Super.SquirmCell : " + getStringType() + state + " / type=" + getStringType() + " state=" + state + "]"; 
    }
    
} // End of the class //