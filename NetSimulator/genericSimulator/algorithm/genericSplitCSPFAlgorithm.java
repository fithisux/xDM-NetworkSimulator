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
import genericSimulator.genericSimulation;
import genericSimulator.network.genericPath;
import genericSimulator.network.genericRCM;
import genericSimulator.network.genericRequest;
import java.util.*;
/**
 *
 * @author vanag
 */
public abstract class genericSplitCSPFAlgorithm  extends genericAlgorithm
{
    protected genericUnsplitCSPFAlgorithm unsplit;         
    public enum SCANNING{WIDER,NARROWER,RANDOM};
    SCANNING scanner;
    public genericSplitCSPFAlgorithm(genericRCM rcm)
    {
        super(rcm);
        String scanstrategy=this.algo_params.get("scanstrategy");
        
        if(scanstrategy.equalsIgnoreCase("wider"))
        {
            scanner=SCANNING.WIDER;
        }else if(scanstrategy.equalsIgnoreCase("narrower"))
        {
            scanner=SCANNING.NARROWER;
        }else if(scanstrategy.equalsIgnoreCase("random"))
        {                        
            scanner=SCANNING.RANDOM;
        } else {
            System.out.println("Illegal selectionstrategy");
            System.exit(0);
        }
    }
    
    public abstract List<genericBWSplit[]>  createSplitMap(int bw);
    
    public List<genericPath> compute(genericRequest request)
    {
        int bw=request.getBw();
        List<genericBWSplit[]> splits=this.createSplitMap(bw);
        
        
        List<genericPath> solution=new ArrayList<genericPath>();
        while(!splits.isEmpty())
        {
            genericBWSplit[] split;
            switch( scanner)
            {
                case WIDER : 
                    split=splits.remove(0);
                    break;
                case NARROWER : 
                    split=splits.remove(splits.size()-1);
                    break;
                default : 
                    split=splits.remove(genericSimulation.seeder.nextInt(splits.size()));
                    break;
            }
            boolean feasible=true;
            for(genericBWSplit split_element : split)
            {        
                assert(split_element.occupy>=0);
                if(split_element.occupy==0) continue;
                genericRequest spreq=new genericRequest(request.getConnectivity(),split_element.bw);                    
                for(int i=0;i<split_element.occupy;i++)
                {
                    List<genericPath> answer=this.unsplit.compute(spreq);
                    if(answer.isEmpty())
                    {
                        feasible=false;
                    }
                    else
                    {
                        solution.add(answer.get(0));
                    }
                }
            }   
            if(!feasible)
            {
                for(genericPath top : solution)            
                {
                    if(top.getPrimary()!=null)
                    {
                        top.getPrimary().releaseResources();
                    }
                    top.releaseResources();
                }
                solution.clear();
            }
            else
            {
                return solution;
            }
        }        
        return solution;
    }
}
