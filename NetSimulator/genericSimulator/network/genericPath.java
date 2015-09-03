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
import genericSimulator.algorithm.combopt.lightWeightEdge;
import genericSimulator.algorithm.combopt.lightWeightPath;
import genericSimulator.network.genericEdge;
import genericSimulator.genericIntPair;
import java.util.*;
import genericSimulator.*;
/**
 *
 * @author vanag
 */
public abstract class genericPath
{
    static int privateId=0;
    int Id;
    public List<genericPath> affected;
    protected genericReservation[] reservations;    
    public boolean reserved;
    public abstract double getBER();
    public static List<genericPath> pathDB=new ArrayList<genericPath>();
    genericSimulationReport metrics;
    public static String[] metricnames=
    {"HOPS",
            "DISTANCE",
            "LLRUTILIZATION",
            "LLRFRAGMENTATION",
            "HLRPOLLUTION",
            "BER",
            "COST",
            "LLRCOUNT"};
    
    public genericSimulationReport getMetrics(){
        return this.metrics;
    }
        
    public final boolean isReserved() {
        return reserved;
    }

    public int getId() {
        return Id;
    }
    
    public genericPath primary;   

    public genericPath getPrimary() {
        return primary;
    }

    public void setPrimary(genericPath primary) {
        this.primary = primary;
    }
    
    
    public genericPath(genericReservation[] slots,double cost)
    {
        affected=new ArrayList<genericPath>();
        this.primary=null;
        this.reserved=false;        
        Id=this.privateId++;
        this.reservations=slots; 
        this.metrics=new genericSimulationReport(metricnames);        
        this.metrics.values[0]=(double) this.reservations.length;
        for(genericReservation slot : this.reservations)
        {
            this.metrics.values[1]+=slot.edge.distance;
            this.metrics.values[2]+=slot.edge.llrutiliztion;
            this.metrics.values[3]+=slot.edge.llrfragmentation;
            this.metrics.values[4]+=slot.edge.hlrpollution;
            this.metrics.values[7]+=slot.edge.getHLRs()[slot.hlrindex].res.getLLRCount();
        }
        this.metrics.values[5]=this.getBER();
        this.metrics.values[6]=cost;        
    }

    
   
   
    public final boolean getReserved() {        
        return reserved;
    }
    
    public final genericReservation[] getReservations(){return this.reservations;}
       
    public final void reserveResources()
    {
        assert(!reserved);
        for(genericReservation slot : this.reservations)
        {            
            slot.edge.insert(this,slot);
        }
        this.reserved=true;  
        genericPath.pathDB.add(this);
        //System.out.println("reserve "+Id);
    }
       
    public final void releaseResources()
    {
        assert(reserved);        
        for(genericReservation slot : this.reservations)
        {            
            slot.edge.remove(this,slot);
        }
        this.reserved=false;  
        genericPath.pathDB.remove(this);
        //System.out.println("release "+Id);
    }
    
    public final void makePrimary()
    {
        assert(this.primary!=null);
        assert(this.affected.isEmpty());
        for(genericReservation resv : this.reservations)
        {
            resv.edge.residents(this.affected);            
        }
        
        for(genericPath path : new HashSet<genericPath>(this.affected))
        {
            path.releaseResources();
        }
        
        this.affected.clear();
        this.primary=null;
        this.reserveResources();
    }
    
    
    public boolean intersect(genericPath path)
    {
        boolean intersection=false;
        
        for(genericReservation resv1 : this.reservations)
        {
            for(genericReservation resv2 : path.reservations)
            {
                if(resv1.edge==resv2.edge) intersection=true;
            }           
        }
        
        return intersection;
    }
    
    
    public final String toString()
    {
            String s= "A path with primary="+this.primary+" as\n";
            for(genericReservation resv : this.reservations) s+=(resv+"\n");
            return s;
    }   
}

