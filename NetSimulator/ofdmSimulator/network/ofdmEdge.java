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
import genericSimulator.network.genericHLR;
import genericSimulator.network.*;
import genericSimulator.network.genericEdge;
import genericSimulator.genericLink;
import genericSimulator.prefs.*;
import java.util.*;

public final class ofdmEdge extends genericEdge 
{    
    boolean [] subcarriers;
    genericHLR[][] overlaps;
    ofdmPrefs sim_ofdm_type_prefs;    
    ofdmGraph graph;    
    
    
    public ofdmEdge(genericLink anedge,genericGraph gr)
    {
            super(anedge,gr);
            this.sim_ofdm_type_prefs= gr.gs.prefs.getOfdmPrefs();
            this.graph=(ofdmGraph) gr;
            subcarriers=new boolean[this.gr.gs.prefs.getNetworkPrefs().getLlrcount()];
            Arrays.fill(subcarriers,false);                      
            //populate overlaps
            overlaps=new genericHLR[this.graph.overlaps.length][];
            for(int i=0;i<overlaps.length;i++)
            {
                overlaps[i]=new genericHLR[this.graph.overlaps[i].length];
                for(int j=0;j<overlaps[i].length;j++)
                {
                    overlaps[i][j]=this.hlrs[this.graph.overlaps[i][j].getIndex()];
                }                
            };
    }  
    
    public  final   boolean isEmpty()
    {
        boolean ans=super.isEmpty();        
        for(genericHLR hlr : this.hlrs) 
        {
            ans = ans & (hlr.pollution==0);
        }
        for(boolean b : this.subcarriers)
        {
            ans = ans & !b;
        }
        return ans;
    }        
  
       

    
    public void reserveHLR(int res)
    {        
        //System.out.println("reserve "+res+" on "+this.id);
        ofdmResource ofdmres=(ofdmResource) this.graph.resources[res];        
        this.freellrcount-=ofdmres.modulation.slots;
        for(int i=ofdmres.startSlot;i<=ofdmres.stopSlot;i++)
        {
            assert(!this.subcarriers[i]);
            this.subcarriers[i]=true;
        }
        assert(this.hlrs[res].pollution==0);
        for(genericHLR hlr : this.overlaps[res])
        {
            hlr.pollution++;            
        }                 
    }
    
    public void releaseHLR(int res)
    {
        //System.out.println("release "+res+" on "+this.id);
        ofdmResource ofdmres=(ofdmResource) this.graph.resources[res];
        this.freellrcount+=ofdmres.modulation.slots;
        for(int i=ofdmres.startSlot;i<=ofdmres.stopSlot;i++)
        {
            assert(this.subcarriers[i]);
            this.subcarriers[i]=false;
        }  
        assert(this.hlrs[res].pollution==1);
        for(genericHLR hlr : this.overlaps[res])
        {
            hlr.pollution--;            
        }        
    }
}
