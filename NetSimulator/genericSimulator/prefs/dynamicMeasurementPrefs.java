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
package genericSimulator.prefs;


/**
 *
 * @author FITHIS
 */
/**
 *
 * @author vanag
 */
import javax.xml.bind.annotation.*;
 
@XmlRootElement
public class dynamicMeasurementPrefs 
{
    
    private int startEpoch;
    private int stopEpoch;
    private int epochsteps;
    private int timestep;
    
    
    public void checkLogic()
    {
        if(this.epochsteps < 0)
        {
            System.out.println("epochsteps must be nonegative");
            System.exit(0);
        }
        
        if(this.timestep <= 0)
        {
            System.out.println("timestep must be nonegative");
            System.exit(0);
        }
        
        if(this.startEpoch < 0)
        {
            System.out.println("start epoch must be nonegative");
            System.exit(0);
        }
        
        if(this.stopEpoch < 0)
        {
            System.out.println("stop epoch must be nonegative");
            System.exit(0);
        }
        
        if(this.startEpoch > this.stopEpoch )
        {
            System.out.println("epochs must be monotonic");
            System.exit(0);
        }
    }

    @XmlElement
    public int getStartEpoch() {
        return startEpoch;
    }

    public void setStartEpoch(int startEpoch) {
        this.startEpoch = startEpoch;
    }

    @XmlElement
    public int getStopEpoch() {
        return stopEpoch;
    }

    public void setStopEpoch(int stopEpoch) {
        this.stopEpoch = stopEpoch;
    }

    @XmlElement
    public int getEpochsteps() {
        return epochsteps;
    }

    public void setEpochsteps(int epochsteps) {
        this.epochsteps = epochsteps;
    }

    @XmlElement
    public int getTimestep() {
        return timestep;
    }

    public void setTimestep(int timestep) {
        this.timestep = timestep;
    }
    
}
