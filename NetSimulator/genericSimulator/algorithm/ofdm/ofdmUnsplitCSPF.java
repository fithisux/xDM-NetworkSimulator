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

package genericSimulator.algorithm.ofdm;
import genericSimulator.algorithm.*;
import genericSimulator.algorithm.combopt.*;
import genericSimulator.genericSimulationReport;
import genericSimulator.network.genericPath;
import genericSimulator.network.*;
import ofdmSimulator.network.*;
import java.util.*;
/**
 *
 * @author vanag
 */
public abstract class ofdmUnsplitCSPF extends genericUnsplitCSPFAlgorithm
{
    double dfailureprob;
    double bfailureprob;
    double ticks=0;
    int criterionindex=-1;
    String actualcost;
    ofdmGraph gr;
    public ofdmUnsplitCSPF(genericRCM rcm)
    {
        super(rcm);               
        String selectioncost=this.algo_params.get("selectioncost");
        if(selectioncost==null){
            System.out.println("No selectioncost");
            System.exit(0);
        } 
        
        this.criterionindex=-1; 
        int index=0;
        for(String s : genericPath.metricnames)
        {
            if(s.equalsIgnoreCase(selectioncost))
            {
                this.criterionindex=index;
                break;
            }
            index++;
        }
        if(this.criterionindex==-1){
            System.out.println("illegal selectioncost");
            System.exit(0);
        }
        actualcost=this.algo_params.get("actualcost");
        if(actualcost==null){
            System.out.println("No actualcost");
            System.exit(0);
        } 
        
        gr=(ofdmGraph) rcm.gr;
    }
    
    public int getCriterion()
    {
        return this.criterionindex;
    }
    
    
    public List<genericPath> unzip(genericRequest request,List<genericComputations> comps)
    {
        List<genericPath>  solutions=new ArrayList<genericPath> ();
        //main search loop
        double fail_b=0;
        double fail_d=0;      
        for(genericComputations gc : comps)
        {      
            if(gc.ps.isEmpty())
            {
                fail_b=1;
                fail_d=0;
            }
            else
            {
                int scans=0;
                int fails=0;
                for(lightWeightPath p : gc.ps)
                {                    
                    int distance=0;
                    for(lightWeightEdge le : p.getEdges())
                    {
                        distance += ((cspfElement) le.id).edge.distance;
                    }                   
                    for(genericResource gres : gc.eqs)
                    {                           
                        scans++;
                        ofdmResource res= (ofdmResource) gres;                        
                        if(distance<=res.getModulation().distance)
                        {                               
                            genericReservation[] slots=new genericReservation[p.getEdges().size()];
                            int index=0; 
                            for(lightWeightEdge le : p.getEdges())
                            {
                                genericReservation resv=new genericReservation();
                                resv.edge=((cspfElement) le.id).edge;
                                resv.hlrindex=le.color;
                                slots[index++]=resv;
                            }    
                            ofdmPath lsp=new ofdmPath(slots,p.getCost());
                            solutions.add(lsp);                            
                        }
                        else
                        {
                            fails++;
                        }
                    } 
                }
                fail_d= ((fails/ ((double) scans)));
                fail_b=0;
            }
            this.ticks+=1;
            this.bfailureprob += fail_b;
            this.dfailureprob += fail_d;
        }     
        
        
        return solutions;
    }
    
    public List<genericPath> compute(genericRequest request)
    {
        if(this.actualcost.equalsIgnoreCase("distance")) {
            for(cspfElement e : this.computations.elements)
            {
                e.le.cost=e.edge.distance;
            }
        } else if(this.actualcost.equalsIgnoreCase("hops")) {
            for(cspfElement e : this.computations.elements)
            {
                e.le.cost=1;
            }
        } else if(this.actualcost.equalsIgnoreCase("llrfragmentation")) {
            for(cspfElement e : this.computations.elements)
            {
                e.le.cost=1+e.edge.getLLRFragmentation();
            }
        } else if(this.actualcost.equalsIgnoreCase("llrutilization")) {
            for(cspfElement e : this.computations.elements)
            {
                e.le.cost=1+e.edge.getLLRUtilization();
            }
        } else if(this.actualcost.equalsIgnoreCase("hlrpollution")) {
            for(cspfElement e : this.computations.elements)
            {
                e.le.cost=1+e.edge.getHLRPollution();
            }
        } else {
            System.out.println("illegal actualcost");
            System.exit(0);
        }
        
        int bw=request.getBw();
        
        int index=-1;
        for(int i=0;i<this.gr.truncations.length;i++)
        {
            if(bw <= this.gr.truncations[i])
            {
                index=i;
                break;
            }
        }
        
        if(index==-1)
        {
            return new ArrayList();
        }
        else
        {
            genericRequest truncated=new genericRequest(request.getConnectivity(),this.gr.truncations[index]);
            List<genericPath> solution=super.compute(truncated);
            if(!solution.isEmpty())
            {
                //System.out.println(request.getBw()+";"+
                //        ((ofdmResource) solution.get(0).getReservations()[0].res).getModulation());
            }
            return solution;
        }        
    }
    
    public genericSimulationReport  executeMeasurement(){
        String[] s={"BFAILURE","DFAILURE","TICKS"};
        genericSimulationReport sr=new genericSimulationReport(s);
        sr.values[0]=this.bfailureprob;
        sr.values[1]=this.dfailureprob;        
        sr.values[2]=this.ticks;
        this.bfailureprob=this.dfailureprob=this.ticks=0;
        return sr;
    }
    
}
