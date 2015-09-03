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

package genericSimulator.algorithm;
import java.util.*;
import genericSimulator.algorithm.combopt.*;
import genericSimulator.network.*;
import genericSimulator.prefs.*;
import genericSimulator.*;
/**
 *
 * @author vanag
 */
public class genericCSPFComputation 
{
    private HashMap<String,genericComputations> comptasks;
    private String desc;
    public genericCSPF[] shpa;
    public cspfElement[] elements;    
    private cspfElement[][] groupings;
    List<genericComputations> gencomps;     
    genericGraph gr;
    public lightWeightGraph[] cspfgraphs;
    
    public genericCSPFComputation(genericGraph somegr)
    {
        this.gr=somegr;
        this.desc="";
        for(int i=0;i<this.gr.getEdges().length;i++)
        {
            this.desc+="0";
        }
        this.elements=new cspfElement[this.gr.getEdges().length*this.gr.resources.length]; 
        this.groupings=new cspfElement[this.gr.getEdges().length][this.gr.getResources().length];
        int index=0;
        for(genericEdge anedge : this.gr.getEdges())
        {
            int id=anedge.id;
            for(genericHLR hlr : anedge.getHLRs())
            {                
                cspfElement e=new cspfElement();
                e.avoid=false;
                e.inclusioncache=true;
                lightWeightEdge le =new lightWeightEdge();
                le.from=anedge.from;
                le.to=anedge.to;
                le.cost=0;
                le.color=hlr.res.getIndex();
                le.index=id;
                le.id=e;
                e.le=le;  
                e.edge=anedge;
                e.hlr=hlr;
                this.elements[index++]=e;
                this.groupings[le.index][le.color]=e;
            }
        } 
        
        if(gr.isHCC())
        {
            cspfgraphs=new lightWeightGraph[gr.resources.length];
        }
        else
        {
            cspfgraphs=new lightWeightGraph[1];
        }
        for(int i=0;i<cspfgraphs.length;i++)
        {
            cspfgraphs[i]=new lightWeightGraph(gr.gs.prefs.getNetworkPrefs().getNodes());            
        }
        
        if(cspfgraphs.length==1)
        {
            for(cspfElement e : this.elements)
            {
                cspfgraphs[0].add(e.le);
            }
        }
        else
        {
            for(cspfElement e : this.elements)
            {
                cspfgraphs[e.le.color].add(e.le);
            }
        }
        this.shpa=null;
        this.gencomps=new ArrayList<genericComputations>();
        this.comptasks=new HashMap<String,genericComputations>();
    }         
    
    public  HashMap<String,genericComputations> updateCacheHCC(genericRequest request)
    {              
        assert(this.gr.isHCC());
        comptasks.clear();  
        int bw=request.getBw();
        for(genericResource res : this.gr.resources)
        {     
            if(res.getBw()!=bw) continue;
            lightWeightGraph gr=this.cspfgraphs[res.getIndex()]; 
            StringBuilder cacheline=new StringBuilder(this.desc);
            for(lightWeightEdge le : gr.cache)
            {
                cacheline.setCharAt(le.index,'1');
            }
            String temp=cacheline.toString();            
            if(comptasks.containsKey(temp))
            {
                comptasks.get(temp).eqs.add(res);
            }
            else
            {
                genericComputations l = new genericComputations();
                l.eqs.add(res);
                comptasks.put(temp, l);
            }
        }
        for(genericComputations gc : comptasks.values())
        {
            List<lightWeightPath> ps=this.shpa[gc.eqs.get(0).getIndex()].
                    solveFor(request.getConnectivity().ef,request.getConnectivity().et);
            /*
            System.out.println("NOW");
            for(lightWeightPath p :  ps)
            {
                System.out.println(p.getCost());
                for(lightWeightEdge e : p.getEdges())
                {
                    System.out.println(e.connectivity+";"+e.index+";"+e.color);
                }
            }
                    */
            gc.ps.addAll(ps);
        }
        return this.comptasks;
    } 
    
    public void updateFormulation(int bw)
    {       
       for(cspfElement e : this.elements)
        {                  
            boolean theold=e.inclusioncache;
            e.inclusioncache=(e.edge.canRoute() && e.hlr.canReserve(bw) && !e.avoid);
            if(theold!=e.inclusioncache)
            {
                int index=this.gr.isHCC() ? e.le.color : 0;
                if(e.inclusioncache)
                {
                    this.cspfgraphs[index].add(e.le);
                }
                else
                {
                    this.cspfgraphs[index].remove(e.le);
                }
            }
        }        
    } 
    
    
    public void findConditionalEdges(genericRequest request,genericPath other)
    {        
        genericSimulation gs=this.gr.getGs();
        if(other != null) 
        {            
            assert(gs.prefs.getProtectionPrefs().isProtected());            
            
            //in case of sharing
            if(gs.prefs.getProtectionPrefs().getProtection()==protectionPrefs.PROTECTIONTYPE.SHARED) //if sharing
            {
                //initialize everything to available except primary intersections
                for(cspfElement e : this.elements)
                {
                    e.avoid=(e.hlr.getState()!=genericHLR.RESOURCE_STATE.PRIMARY);
                } 
                
                for(genericPath path : genericPath.pathDB)
                {
                    //avoid backups with primaries intersecting THE PRIMARY
                    genericPath primary=path.getPrimary();
                    assert(primary!=null);
                    if(primary.intersect(other)) //it is a primary and intersects lsp
                    {
                        for(genericReservation resv : path.getReservations() )
                        {
                            this.groupings[resv.edge.id][resv.hlrindex].avoid=true;                                                     
                        }
                    }
                }                
            }
            else //no sharing
            {                
                //THE PRIMARY avoids reservations
                for(cspfElement e : this.elements)
                {
                    e.avoid=(e.hlr.getState()!=genericHLR.RESOURCE_STATE.FREE);
                }  
            } 
            
            //avoid THE PRIMARY
            for(genericReservation resv : other.getReservations() )
            {
                for(cspfElement e : this.groupings[resv.edge.id])
                {
                    e.avoid=true;
                }                                 
            }
        }
        else  //THE PRIMARY avoids primary or (when protected, backup reservations)
        {  
            for(cspfElement e : this.elements)
            {
                e.avoid=(e.hlr.getState()!=genericHLR.RESOURCE_STATE.FREE);
            }             
        }                     
    }
    
    public List<genericComputations> compute(genericRequest request,genericPath path)
    {
        if(path !=null) assert(path.isReserved());
        this.findConditionalEdges(request, path);
        this.updateFormulation(request.getBw());
        this.gencomps.clear();
        if(this.gr.isHCC())
        {
            HashMap<String,genericComputations> tasks=this.updateCacheHCC(request);
            for(genericComputations comps : tasks.values())
            {
                this.gencomps.add(comps);
            }
        }
        else
        {
            List<lightWeightPath> p=this.shpa[0].solveFor(
                    request.getConnectivity().ef,request.getConnectivity().et);
            if(!p.isEmpty())
            {
                this.gencomps.add( new genericComputations(p) );
            }
        }
        
        return this.gencomps;
    }
}
