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

import javax.xml.bind.annotation.*;
import java.util.*;
@XmlRootElement
public class pairInjectorPrefs 
{
    private ArrayList<genericOrientation> generators;
    private distributionPrefs bandwidth;
    private distributionPrefs arrivalTime;
    private distributionPrefs serviceTime;

    @XmlElementWrapper(name = "generators" )
    @XmlElement(name = "genericOrientation")
    public ArrayList<genericOrientation> getGenerators() {
        return generators;
    }

    public void setGenerators(ArrayList<genericOrientation> generators) {
        this.generators = generators;
    }

    @XmlElement
    public distributionPrefs getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(distributionPrefs bandwidth) {
        this.bandwidth = bandwidth;
    }

    @XmlElement
    public distributionPrefs getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(distributionPrefs arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public distributionPrefs getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(distributionPrefs serviceTime) {
        this.serviceTime = serviceTime;
    }
    
    public void checkLogic(networkPrefs top)
    {
        for(genericOrientation gen : this.generators)
        {
            gen.checkLogic(top);
        }
        
        this.bandwidth.checkLogic();
        this.arrivalTime.checkLogic();
        this.serviceTime.checkLogic();
    }
    
    
}
