Example Artificial Chemistry Demo:

<img src="https://github.com/berlinbrown/Squirm3ArtificialChemistry/raw/master/media/screenshot_art.png" />

Squirm3 is an artificial chemistry simulation - a world where we can make up our own rules of construction and let them rip.

http://www.sq3.org.uk/Evolution/Squirm3/ - Tim Hutton

Mirror of squirm demo

In the screenshot above, each block represents an atom.  When two atoms collide then a reaction might occur.  If the reaction is a one of the available strong reactions then a bond will form.  Lines are drawn between atoms with bonds.  A cluster of atoms with strong bonds represent a molecule.

DNA and RNA are types of macromolecules.  They aren't represented in the demo but consider that DNA and RNA molecules have interesting behavior.

This application is an artificial chemistry simulation implemented through a cellular automaton. We show that basic rules can be used to produce complex behavior through simulated artificial chemical reactions. The chemical simulation exists on a fixed size two dimensional grid of cells.  Each active cell is represented by an artificial atom element, these atoms may collide with other atoms to produce a chemical reaction.  Strong chemical bonds will form if the chemical reaction is allowed by system.  Clusters of strong bonded atoms form molecule strings.   We show that self replicating molecule string patterns emerge from the artificial simulation.  This analysis is based on Timothy Hutton's artificial chemistry model from Squirm3.  It it is a basic model but a necessary step for analyzing and recreating similar natural systems.  Self-replication and self-organization is fundamental to all biological life.  Engineers use scientific knowledge to solve real-world problems.  Scientists study and experiment with nature and the Universe in order to add their research to the scientific knowledge base.  This small experiment attempts to simulate real chemical reactions using simple artificial physics.

 Chemical reactions occur when two atom cells collide.  In the simulated physical environment, this occurs when an atom cell sits next to another atom cell.  The chemical reaction rules are tested between the two atoms, if the rules allow the reaction then a bond forms.

The simulation consists of a 2D grid, fifty cells in height and fifty cells in width.  A cell grid is inactive or active, the active grid cell may contain an atom element represented by a  'a', 'b', 'c', 'd', 'e', 'f' type and a 0-8 state,  e8 represents atom cell type 'e' with state 8.  Molecules consist of clumps of atoms.  We only use the atom type to chain molecule strings, cfab is a common molecule that self replicates in the simulation.

Establishing a chemical reaction, example reaction cell1=e8 + cell2=e0 creates cells e0 and e4 and a bond may form
After thousands of iterations, common patterns emerge on the grid.  Common bonded atoms (molecules) survive through out the simulation. 
