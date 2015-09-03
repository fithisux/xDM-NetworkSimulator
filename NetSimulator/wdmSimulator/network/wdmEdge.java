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

package wdmSimulator.network;
import wdmSimulator.impman.impairmentLoader;
import wdmSimulator.impman.speedTuple;
import genericSimulator.network.genericHLR;
import genericSimulator.network.*;
import genericSimulator.*;
import genericSimulator.prefs.*;
import java.util.*;
import java.io.*;

public final class wdmEdge extends genericEdge 
{            
    private wdmRunner[] runners; 
    wdmPrefs sim_optical_type_prefs;    
    
    public wdmEdge(genericLink anedge,wdmGraph gr,genericSimulation gs)
    {
            super(anedge,gr);
            this.sim_optical_type_prefs= gr.otp;
            for(genericHLR hlr : this.hlrs)
            {
                hlr.pollution=0;
            }            
    }        
   
    public  void loadImpairments(impairmentLoader x)
    {
        assert(this.sim_optical_type_prefs.isImpaired());
        this.runners = new wdmRunner[this.hlrs.length];
        for(int i=0;i<this.hlrs.length;i++)
        {
            this.runners[i].invq=1.0/ ( (wdmGraph) this.gr).imp.tanya_getQ(
                    this.distance,
                    this.distance);
            this.runners[i].overlaps=new wdmOverlap[x.impairments[i].size()];
            for(int k=0;k<this.runners[i].overlaps.length;k++)
            {
                speedTuple s=(speedTuple) x.impairments[i].get(k);
                this.runners[i].overlaps[k]= new wdmOverlap(this.hlrs[s.target],s.nois,s.order);
            }
        }             
    }
    
    public void updateDescription()
    {      
        super.updateDescription();
        if(this.sim_optical_type_prefs.isImpaired())
        {
            int index=0;
            for(genericHLR hlr : this.hlrs)
            {
                this.runners[index++].invq=1.0/( (wdmGraph) this.gr).imp.tanya_getQ(
                this.distance+hlr.pollution,
                this.distance);                
            }
        }        
    }

    public  double getInvQ(int lambda)
    {
        return this.runners[lambda].invq;
    }
    

   
    public  final  boolean isEmpty()
    {
        boolean ans=super.isEmpty();
        if(this.sim_optical_type_prefs.isImpaired())
        {
            for(genericHLR hlr : this.hlrs)
            {
                ans = ans && (hlr.pollution==this.distance);
            }
            for(wdmRunner runner : this.runners)
            {
                for(wdmOverlap overlap : runner.overlaps)
                {        
                    ans = ans && overlap.isEmpty();                    
                }
            }
        }
        return ans;
    }

    public   void reserveHLR(int res)
    {       
        if(this.sim_optical_type_prefs.isImpaired())
        {                        
            for(wdmOverlap overlap : this.runners[res].overlaps)
            {
                overlap.inc();
            }  
        }                
    }

    public   void releaseHLR(int res)
    {    
        if(this.sim_optical_type_prefs.isImpaired())
        {                
            for(wdmOverlap overlap : this.runners[res].overlaps)
            {
                overlap.dec();
            } 
        }
    }  
    
    public   String toString()
    {
            String s = super.toString();

            s += " Having " + this.hlrs.length+" lambdas and mask \n";
            s += this.getMaskDesc();
            s+="\n";                          
            return s;
    } 

    public   String getMaskDesc()
    {
        String s="";
        for(int i=0;i<this.hlrs.length;i++)
        {
            s += (i + ";" + this.hlrs[i].getState().name());
            if(this.sim_optical_type_prefs.isImpaired())
            {
                s += ("#" + this.hlrs[i].pollution +"#");
            }
            s+=" ";
        }
        return s;
    }   
}
