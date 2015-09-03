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
 
@XmlRootElement
public class networkPrefs 
{
    public enum MODES {HCC,HCU};
    private int nodes;
    private int llrcount;
    private int tranceivers;
    private MODES mode;

    @XmlElement
    public int getNodes() {
        return nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    @XmlElement
    public int getLlrcount() {
        return llrcount;
    }

    public void setLlrcount(int llrcount) {
        this.llrcount = llrcount;
    }

    @XmlElement
    public int getTranceivers() {
        return tranceivers;
    }

    public void setTranceivers(int tranceivers) {
        this.tranceivers = tranceivers;
    }

    @XmlElement
    public MODES getMode() {
        return mode;
    }

    public void setMode(MODES mode) {
        this.mode = mode;
    }
    
    public void checkLogic()
    {
        if(this.nodes < 0)
        {
            System.out.println("nodes must be nonegative");
            System.exit(0);
        }
        
        if(this.llrcount < 0)
        {
            System.out.println("llrcount must be nonegative");
            System.exit(0);
        }
        
        if(this.tranceivers < 0)
        {
            System.out.println("tranceivers must be nonegative");
            System.exit(0);
        }                
    }
}
