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
package ofdmSimulator.network;
import genericSimulator.*;
import genericSimulator.network.genericResource;
/**
 *
 * @author Administrator
 */
public class ofdmResource extends genericResource
{
    int startSlot;
    int stopSlot;
    ofdmModulationToken modulation;
    
    public ofdmResource(int a,ofdmModulationToken somemodulation,int someindex)
    {        
        super(someindex);
        this.startSlot=a;
        this.stopSlot=a+somemodulation.slots-1;
        assert(this.startSlot<=this.stopSlot);
        this.modulation=somemodulation;        
    }

    public int getStartSlot() {
        return startSlot;
    }

    
    public int getStopSlot() {
        return stopSlot;
    }
    
    public ofdmModulationToken getModulation() {
        return modulation;
    }
    
    public String toString()
    {
        String s=modulation.toString();
        s+=" startSlot="+startSlot;
        s+=" stopSlot="+stopSlot;
        return s+" "+super.toString();        
    }
    
    public ofdmResource clone()
    {
        return (new ofdmResource(startSlot,modulation,this.getIndex()));           
    }
    
    public int getBw() {return this.modulation.bitrate;}
    public int getLLRCount() {return this.modulation.slots;}
    
}
