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

import genericSimulator.genericIntPair;
import java.util.*;

/**
 *
 * @author vanag
 */
public class genericYenAlgorithm extends genericCSPF
{
    genericBellmanFord bf;
    List<List<yenEquivalence>> nodeequivalences;
    lightWeightGraph gr;
    int theK;
    public genericYenAlgorithm(lightWeightGraph somegr,int theK)
    {         
        super(somegr);
        this.theK=theK;
        assert(theK>=1);
        this.gr=somegr;
        this.bf=new genericBellmanFord(this.gr);
        this.nodeequivalences=new ArrayList<List<yenEquivalence>>();
        for(int i=0;i<this.gr.nodes.length;i++)
        {
            this.nodeequivalences.add(new ArrayList<yenEquivalence>());
        }        
    }
    
    public void initialize()
    {
        for(List<yenEquivalence> eqs : this.nodeequivalences)
        {
            eqs.clear();
        }               
    }
    
    public  List<lightWeightPath> solveFor(int from,int to)
    {
        this.initialize();
        List<lightWeightPath> solutions=new ArrayList<lightWeightPath>();
        List<lightWeightPath> paths=this.bf.solveFor(from,to);
        //System.out.println("Go");
        if(!paths.isEmpty())
        {
            lightWeightPath p=paths.get(0);
            solutions.add(p);
            //System.out.println("Seed is "+p);
            for(int i=1;i<this.theK;i++)
            {        
                //System.out.println("Updating");                
                this.update(p,to);
                //System.out.println("Targeting "+p);
                p=this.extractPath();
                //System.out.println("Extracting "+p);
                if(p==null)
                {                    
                    break;
                }
                else
                {
                    solutions.add(p);
                }
            }
        }
        //System.out.println("Size:"+solutions.size());
        //System.out.println("Thek:"+this.theK);
        for(lightWeightPath p : solutions)
        {
            assert(p.isPath());
        }        
        return solutions;
    }
    //210-8640574 zwi
    
    public void update(lightWeightPath p,int finishnode)
    {
        //update lastnode infos
        List<yenNodeCandidate> candidates=p.getCandidates();
        assert(!candidates.isEmpty());
        for(yenNodeCandidate candidate : candidates)
        {
            List<yenEquivalence> eqs=this.nodeequivalences.get(candidate.lastnode);
            yenEquivalence found=null;
            int count=0;
            for(yenEquivalence yeq : eqs)
            {
                if(yeq.isEqual(candidate.p))
                {
                    found=yeq;
                    count++;
                }
            }
            
            if(found==null)
            {
                found=new yenEquivalence();
                found.q=null;
                found.klasi=candidate.p;
                found.lastnode=candidate.lastnode;
                eqs.add(found);
            }
            else
            {
                assert(count==1);               
            }
            if(candidate.lastnode!=finishnode)
            {
                assert(p.nodes[candidate.lastnode]!=null);
                int diff=found.outgoing.size();
                found.outgoing.add(p.nodes[candidate.lastnode]);
                diff=found.outgoing.size()-diff;
                found.flag=(diff==1);
            }
        } 
        
        
        
        for(List<yenEquivalence> eqs : this.nodeequivalences)
        {
            for(yenEquivalence yeq : eqs)
            {
                if(yeq.flag)
                {
                    for(lightWeightEdge e : yeq.outgoing)
                    {
                        e.hide=true;
                    }
                    for(lightWeightEdge e : yeq.klasi.edges)
                    {
                        this.gr.nodes[e.from].hide=true;
                    }
                    List<lightWeightPath> paths=this.bf.solveFor(yeq.lastnode,finishnode);                
                    if(paths.isEmpty())
                    {
                        yeq.q=null;
                    }
                    else
                    {
                        yeq.q=paths.get(0);                    
                    }
                    for(lightWeightEdge e : yeq.outgoing)
                    {
                        e.hide=false;
                    }
                    for(lightWeightEdge e : yeq.klasi.edges)
                    {
                        this.gr.nodes[e.from].hide=false;
                    }                    
                }                
            }
        }
        
        for(List<yenEquivalence> eqs : this.nodeequivalences)
        {
            for(yenEquivalence yeq : eqs)
            {
                //System.out.println(yeq);
                yeq.flag=false;
            }
        }
    }
    
    public lightWeightPath extractPath()
    {
        List<yenSpur> solutions=new ArrayList<yenSpur>();
        //System.out.println("extract");
        for(List<yenEquivalence> eqs : this.nodeequivalences)
        {
            //System.out.println("nodeequivalences");
            for(yenEquivalence yeq : eqs)
            {
                //System.out.println("equivalence");
                if(yeq.q==null) continue;
                yenSpur spur=new yenSpur();
                spur.yeq=yeq;
                spur.p=yeq.klasi.join(yeq.q);
                //System.out.println("Q : "+yeq.q);
                //System.out.println("P : "+yeq.klasi);
                //System.out.println("Node : "+yeq.lastnode);
                //System.out.println("Spur : "+spur.p);
                solutions.add(spur);
            }
        }
        if(solutions.isEmpty()) return null;
        yenSpur spur=solutions.get(0);
        
        for(yenSpur temp : solutions)
        {
            if(temp.p.getCost() < spur.p.getCost())
            {
                spur=temp;
            }
        }
        
        spur.yeq.q=null; 
        //System.out.println("solved : "+spur.p);
        return spur.p;        
    }
    
}
