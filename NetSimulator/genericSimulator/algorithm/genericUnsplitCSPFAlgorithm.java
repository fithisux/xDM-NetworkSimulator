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
import genericSimulator.network.*;
import genericSimulator.*;
import genericSimulator.algorithm.combopt.*;
import java.util.*;
/**
 *
 * @author vanag
 */
public abstract class genericUnsplitCSPFAlgorithm extends genericAlgorithm
{
    Random seeder;
    public genericCSPFComputation computations;    
    
    public genericUnsplitCSPFAlgorithm(genericRCM rcm)
    {
        super(rcm);
        seeder=genericSimulator.genericSimulation.seeder;
        this.computations=new genericCSPFComputation(rcm.gr) ;        
    }
    
    private genericPath select(List<genericPath> solutions)
    {       
        if(solutions.size()==0) return null;
        String selectionstrategy=this.algo_params.get("selectionstrategy");
        genericPath solution=null;
        
        if(selectionstrategy.equalsIgnoreCase("ff"))
        {
            solution=solutions.get(0);
        }else if(selectionstrategy.equalsIgnoreCase("rf"))
        {
            solution = solutions.get(seeder.nextInt(solutions.size()));
        }else if(selectionstrategy.equalsIgnoreCase("bf"))
        {
                        
            solution=this.getMin(solutions);
        } else {
            System.out.println("Illegal selectionstrategy");
            System.exit(0);
        }
        return solution;
    }
    
    public abstract List<genericPath> unzip(genericRequest request,List<genericComputations> comps);
    
    public abstract int getCriterion();
    
    public genericPath getMin(List<genericPath> solutions)
    {     
        genericPath solution=solutions.get(0);
        genericSimulationReport metrics=solution.getMetrics();
        double criterionvalue=metrics.values[this.getCriterion()];
        for(genericPath path : solutions)
        {
            genericSimulationReport tempmetrics=path.getMetrics();
            double tempcriterionvalue=tempmetrics.values[this.getCriterion()];
            if(tempcriterionvalue < criterionvalue)
            {
                criterionvalue=tempcriterionvalue;
                solution=path;
            }
        }
        return solution;            
    }
    
    public List<genericPath> compute(genericRequest request)
    {
        List<genericPath>  solutions=new ArrayList<genericPath> ();
        List<genericComputations> c1=this.computations.compute(request, null);           
        List<genericPath>  p1=this.unzip(request,c1);        
        genericPath primary=this.select(p1);  
        if(primary != null )
        {
            primary.reserveResources();
            //System.out.println(primary);
            if(this.rcm.gr.getGs().prefs.getProtectionPrefs().isProtected())
            {
                List<genericComputations> c2=this.computations.compute(request, primary);
                List<genericPath>  p2=this.unzip(request,c2);
                genericPath secondary=this.select(p2);     
                if(secondary == null)
                {
                    primary.releaseResources();
                    return solutions;
                }
                secondary.reserveResources();
                secondary.setPrimary(primary);
                solutions.add(secondary);
            }
            else
            {
                solutions.add(primary);
            }
        }
        return solutions;
    }    
    
    
    
}
