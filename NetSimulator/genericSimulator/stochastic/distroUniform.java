/*
===========================================================================
xDM-NetworkSimulator GPL Source Code
Copyright (C) 2012 Vasileios Anagnostopoulos.
This file is part of thexDM-NetworkSimulator Source Code (?xDM-NetworkSimulator Source Code?).  
xDM-NetworkSimulator Source Code is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
xDM-NetworkSimulator Source Code is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with xDM-NetworkSimulator Source Code.  If not, see <http://www.gnu.org/licenses/>.
In addition, the xDM-NetworkSimulator Source Code is also subject to certain additional terms. You should have received a copy of these additional terms immediately following the terms and conditions of the GNU General Public License which accompanied the Doom 3 Source Code.  If not, please request a copy in writing from id Software at the address below.
If you have questions concerning this license or the applicable additional terms, you may contact in writing Vasileios Anagnostopoulos, Campani 3 Street, Athens Greece, POBOX 11252.
===========================================================================
*/

package genericSimulator.stochastic;
import genericSimulator.prefs.distributionPrefs;
import genericSimulator.stochastic.genericDistribution;
import java.util.*;
/**
 *
 * @author  vanag
 */
public final class distroUniform extends genericDistribution
{
    int up;
    int down;

    int active_up;
    int active_down;
    
    /** Creates a new instance of UniformDistribution */
    public void initialize(distributionPrefs prefs,Random go)
    {
        super.initialize(prefs,go);

        if(this.params.containsKey("down"))
        {
            active_down=down=Integer.parseInt(this.params.get("down"));
        }
        else
        {
            System.out.println("Uniform without down");
            System.exit(0);
        }

        if(this.params.containsKey("up"))
        {
            active_up=up=Integer.parseInt(this.params.get("up"));
        }
        else
        {
            System.out.println("Uniform without up");
            System.exit(0);
        }
    }
    
    public int spitRandomNumber()
    {
        return active_down+slgen.nextInt(active_up-active_down+1);
    }    
    
    public void startEpoch(int epoch)
    {
        active_up= (int) Math.round(up/(1+epoch*this.xi));
        active_down= (int) Math.round(down/(1+epoch*this.xi));
    }
        
    public String toString()
    {
        String s="A uniform with down "+(down)+" up "+(up);
        return s;
    }
    
}
