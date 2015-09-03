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
package genericSimulator.network;
import genericSimulator.*;
import ofdmSimulator.network.*;
import genericSimulator.genericLink;
import java.util.*;

public abstract class genericEdge
{        
    public int from;
    public int to;
    public int distance;
    public int id;
    private boolean failed;
    protected boolean usable;
    protected genericGraph gr;        
    private int freehlrcount;
    protected int freellrcount;
    protected genericHLR[] hlrs;
    int tranceivers;
    protected long llrfragmentation;
    protected long llrutiliztion;
    protected long hlrpollution;

    public genericEdge(genericLink somegei,genericGraph somegr)
    {
        this.from=somegei.getPair().ef;
        this.to=somegei.getPair().et;
        this.distance=somegei.getDistance();
        this.id=somegei.getId();
        this.gr=somegr;
        this.freehlrcount=this.gr.resources.length;
        this.freellrcount=this.gr.gs.prefs.getNetworkPrefs().getLlrcount();
        this.failed=false;
        this.usable=true; 
        this.llrutiliztion=0;
        this.llrfragmentation=0; 
        this.hlrpollution=0;
        this.hlrs =new genericHLR[this.freehlrcount];
        for(int i=0;i<this.hlrs.length;i++)
        {
            this.hlrs[i]=new genericHLR((this instanceof ofdmEdge),this.gr.resources[i]);
        }
        this.tranceivers=  gr.gs.prefs.getNetworkPrefs().getTranceivers();
        assert(this.hlrs.length>0);
    }

    public boolean canRoute()
    {
        if(this.isFailed()) return false;        
        if(this.tranceivers<=0) return false;        
        return true;
    }
            
    public final void makeFailed(HashSet<genericPath> affected) 
    {
        this.failed = true;
        for(genericHLR hlr : hlrs)
        {
            for(genericPath path : hlr.getList())
            {
                affected.add(path);
            }
        }  
    }

    public final void revive() 
    {
        this.failed = false;
        for(genericHLR hlr : hlrs)
        {
            for(genericPath path : hlr.getList())
            {
                assert(path.isReserved());
            }
        }  
    }
    
    public void residents(List<genericPath> sack)
    {
        for(genericHLR hlr : this.hlrs)
        {
            sack.addAll(hlr.getList());
        }
    }
     
    public final boolean isFailed()
    {
        return this.failed;
    }

    public boolean isEmpty()
    {
        boolean ans=!this.failed;
        for(genericHLR hlr : hlrs)
        {
            ans = ans & (hlr.getState()==genericHLR.RESOURCE_STATE.FREE);            
        }
        ans = ans & (this.freehlrcount==this.gr.resources.length);
        ans = ans & (this.freellrcount==this.gr.gs.prefs.getNetworkPrefs().getLlrcount());
        ans = ans & (this.tranceivers == gr.gs.prefs.getNetworkPrefs().getTranceivers());
        return ans;
    }

    public void updateDescription()
    {        
        double x1= this.hlrs.length- this.freellrcount;
        this.llrutiliztion= (long) Math.ceil( 1000 * x1 / this.hlrs.length );
         
        boolean prev=(this.hlrs[this.hlrs.length-1].getState()==genericHLR.RESOURCE_STATE.FREE);
        int toggles=0;
        boolean next; 
        double x2=0;
        for(genericHLR  hlr : this.hlrs)
        {
            next=(hlr.getState()==genericHLR.RESOURCE_STATE.FREE);
            if(prev!=next) toggles++;
            prev=next;
            x2+=hlr.pollution;
        }
        
        this.llrfragmentation=(long) Math.ceil(1000 * ((double) toggles) / this.hlrs.length);
        this.hlrpollution=(long) Math.ceil(1000 * x2 / this.hlrs.length);
    }
        
    public void insert(genericPath path,genericReservation resv)
    {
        assert(resv.edge==this);
        assert(!this.isFailed());
        assert(this.tranceivers > 0);
        genericHLR hlr=this.hlrs[resv.hlrindex];        
        if(hlr.getState()==genericHLR.RESOURCE_STATE.FREE)
        {            
            this.freehlrcount--;
            this.reserveHLR(resv.hlrindex);
            this.updateDescription();
            this.tranceivers--; 
            assert(this.tranceivers>=0);
        }
        hlr.insert(path);                
    }

    public void remove(genericPath path,genericReservation resv)
    {
        assert(resv.edge==this);
        genericHLR hlr=this.hlrs[resv.hlrindex];
        hlr.remove(path);
        if(hlr.getState()==genericHLR.RESOURCE_STATE.FREE)
        {
            this.freehlrcount++;
            this.releaseHLR(resv.hlrindex);
            this.updateDescription();
            this.tranceivers++;
            assert(this.tranceivers <= this.gr.gs.prefs.getNetworkPrefs().getTranceivers());
        }          
    }


    public abstract void reserveHLR(int res);
    public abstract void releaseHLR(int res);
    
    public long getLLRUtilization()
    {
        return this.llrutiliztion;
    }
    public long getLLRFragmentation()
    {
        return this.llrfragmentation;
    }
    public long getHLRPollution()
    {
        return this.hlrpollution;
    }
    public int getTranceivers()
    {
        return this.tranceivers;
    }    
    public final genericHLR[] getHLRs()
    {
        return this.hlrs;
    }    
    public final long getPollution(int res)
    {
        return this.hlrs[res].pollution;
    }

    
    public String toString()
    {
            String s = "An edge with id "+ this.id+
                    " from "+from+
                    " to "+to+
                    " distance "+distance+
                    " , with workingresources "+Integer.toString(this.freehlrcount);
            return s;
    }               	
}
