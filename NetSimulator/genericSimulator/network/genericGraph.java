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
import genericSimulator.stochastic.genericVONTrafficGenerator;
import genericSimulator.*;
import genericSimulator.genericLink;
import genericSimulator.genericNodeRequestFactory;
import genericSimulator.genericPairRequestFactory;
import genericSimulator.prefs.networkPrefs;
import java.util.*;

public abstract class genericGraph implements genericObservations
{
        protected genericEdge[] edges;      
        public double[] edge_quantities;
        public double[] quantities;
        
        public genericSimulation gs;        

        public genericEdge[] getEdges() {
            return edges;
        }
        
        public genericResource[] resources;
        
        public genericResource[] getResources(){return resources;}
        
  
        public void fillEdges()
        {
             //initialize edges
            genericVONTrafficGenerator pi;
            List<genericEdge> edges_list=this.initializeEdges();
            this.edges=new genericEdge[edges_list.size()];
            int indy=0;
            System.out.println("edges = "+edges_list.size());
            for(genericEdge e : edges_list)
            {
                this.edges[indy++]=e;                                 
            }            
            System.out.println("graph has "+edges_list.size()+" edges");
        }
        
       
        public boolean isHCC()
        {
            return (this.gs.prefs.getNetworkPrefs().getMode()==genericSimulator.prefs.networkPrefs.MODES.HCC);
        }

	public genericGraph(genericSimulation somegsc)
	{            
            this.gs=somegsc;                            
            this.edge_quantities=new double[8];           
            this.quantities=new double[4];                                  
        }
        
        
        public final genericSimulation getGs() {
            return gs;
        }

        
        public genericSimulationReport executeMeasurement()
        {    
            int found=0;
            for(genericEdge e : this.edges)
            {
                if(e.isFailed()) continue;
                found++;
                quantities[0]=e.getLLRUtilization();
                quantities[1]=e.getLLRFragmentation();
                quantities[2]=e.getHLRPollution();
                quantities[3]=e.getTranceivers();
                for(int i=0;i<4;i++)
                {
                    this.edge_quantities[2*i]+=quantities[i];
                    this.edge_quantities[2*i+1]+=(quantities[i]*quantities[i]);
                }
            }
            
            for(int i=0;i<this.edge_quantities.length;i++)
            {
                this.edge_quantities[i]/=found;                
            }
            
            for(int i=0;i<4;i++)
            {
                this.edge_quantities[2*i+1]-=this.edge_quantities[2*i]*this.edge_quantities[2*i];
                if(this.edge_quantities[2*i+1]<0) this.edge_quantities[2*i+1]=0;
                this.edge_quantities[2*i+1]=Math.sqrt(this.edge_quantities[2*i+1]);
            }
            
                
            
            String[] s={
                "LLRUtilizationM",
                "LLRUtilizationV",
                "LLRFragmentationM",
                "LLRFragmentationV",
                "HLRPolutionM",
                "HLRPolutionV",
                "TranceiversM",
                "TranceiversV",
            };
            genericSimulationReport sr=new genericSimulationReport(s);
            for(int i=0;i<this.edge_quantities.length;i++)
            {
                sr.values[i]=this.edge_quantities[i];
            }    
            Arrays.fill(this.edge_quantities,0);
            return sr;
        }
        
        public boolean isEmpty()
        {
            boolean ans=true;
            for(genericEdge e : this.edges)
            {
                assert(!e.isFailed());
                assert(e.isEmpty());
                ans &= (e.isEmpty()& !e.isFailed());
            }
            return ans;
        }
       
        public abstract List<genericEdge> initializeEdges();
        
        public final String toString()
	{
            String s= "A graph with \n";
            for(genericEdge e : this.edges)
            {
                s+=(e.toString()+"\n");
            }
            return s;
	}   
 }

