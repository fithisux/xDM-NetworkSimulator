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

package genericSimulator.algorithm.combopt;
import genericSimulator.*;
import java.util.*;

/**
 *
 * @author vanag
 */
public final class genericBellmanFord extends genericCSPF
{
     lightWeightIndicator[] indicators;
     List<lightWeightPath> paths;

    public genericBellmanFord(lightWeightGraph somegr)
    {         
        super(somegr);
        this.indicators=new lightWeightIndicator[somegr.nodes.length];
        for(int i=0;i<this.indicators.length;i++)
        {
            this.indicators[i]=new lightWeightIndicator();
            this.indicators[i].node=somegr.nodes[i];
        }
        this.paths=new ArrayList<lightWeightPath>();
    }
        
    public void initialize(int origin)
    {
        this.paths.clear();
        for(lightWeightIndicator indicator : this.indicators)
        {
            indicator.cost=Long.MAX_VALUE;
            indicator.originator=null;
        } 
        this.indicators[origin].cost=0;
    }


    public void findShortestTree(int origin)
    {
        //System.out.println(this.sweep.size());
        this.initialize(origin);
        boolean goon=false;
        do
        {            
           goon=false;           
           for(lightWeightIndicator source : this.indicators)
           {            
               if(source.cost==Long.MAX_VALUE) {continue;}
               if(source.node.hide){continue;}
               for(lightWeightEdge e : source.node.edges)
               {
                   if(e.hide){continue;}
                   long candidateCost=e.cost+source.cost;
                   lightWeightIndicator target=this.indicators[e.to];
                   if(target.cost==Long.MAX_VALUE || (target.cost > candidateCost))
                   {                   
                       target.originator=e;
                       target.cost=candidateCost;
                       goon=true;
                   }
               }
           }
        }while(goon);
    }

    public List<lightWeightPath> solveFor(int from,int to)
    {
        this.findShortestTree(from);
        lightWeightIndicator source=this.indicators[to];
        if(source.originator!=null) 
        {                          
            List<lightWeightEdge> edges=new ArrayList<lightWeightEdge>();
            while(source.originator!=null)
            {
                edges.add(0, source.originator);
                source=this.indicators[source.originator.from];
            } 
            lightWeightPath p=new lightWeightPath(this.indicators.length,edges);
            this.paths.add(p);
        }        
        return this.paths;    
    }        
}