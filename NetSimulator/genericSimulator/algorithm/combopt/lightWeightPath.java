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
import java.util.*;
/**
 *
 * @author Administrator
 */
public final class lightWeightPath
{
    List<lightWeightEdge> edges;
    private long cost;
    lightWeightEdge [] nodes;

    public List<lightWeightEdge> getEdges() {
        return edges;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public List<yenNodeCandidate> getCandidates()
    {
        List<yenNodeCandidate> candidates=new ArrayList<yenNodeCandidate>();
        if(this.edges.isEmpty()) return candidates;
        ArrayList<lightWeightEdge> working=new ArrayList<lightWeightEdge>();
        yenNodeCandidate candidate;
        
        candidate=new yenNodeCandidate();
        candidate.lastnode=this.edges.get(0).from;
        candidate.p=new lightWeightPath(this.nodes.length,(ArrayList<lightWeightEdge>) working.clone());
        candidates.add(candidate);
        for(int i=0;i<this.edges.size();i++)
        {
            lightWeightEdge e=this.edges.get(i);
            working.add(e);
            candidate=new yenNodeCandidate();
            candidate.lastnode=e.to;
            candidate.p=new lightWeightPath(this.nodes.length,(ArrayList<lightWeightEdge>) working.clone());
            candidates.add(candidate);
        }
        return candidates;
    }
    public lightWeightPath(int nodes,List<lightWeightEdge> edges)
    {
        this.edges=edges;
        for(lightWeightEdge e :edges)
        {
            cost+=e.cost;
        }
        this.nodes=new lightWeightEdge [nodes];
        for(lightWeightEdge e : this.edges)
        {
            this.nodes[e.from]=e;            
        }        
    }
    
    public boolean isPath()
    {
        for(int i=0;i<this.edges.size()-1;i++)
        {
            if(this.edges.get(i).to != this.edges.get(i+1).from) return false;
        }
        
        return true;
    }

    
    
    public lightWeightPath join(lightWeightPath other)
    {
        assert(other.isPath());
        assert(this.isPath());
        List<lightWeightEdge> xedges=new ArrayList<lightWeightEdge>();
        xedges.addAll(this.edges);
        xedges.addAll(other.edges);
        lightWeightPath p=new lightWeightPath(this.nodes.length,xedges);
        assert(p.isPath());
        return p;
    }
    
    
    
    public void add(lightWeightEdge e)
    {
        edges.add(e);
    }

    public String toString()
    {
        String s="lightWeightPath with cost "+this.cost+" and edges ";
        for(lightWeightEdge e : this.edges)
        {
            s+=(" "+e.toString());
        }
        return s;
    }    
}
