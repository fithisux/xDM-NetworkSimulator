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
import genericSimulator.genericSimulationReport;
import genericSimulator.network.genericRCM;
import java.util.*;
import ofdmSimulator.network.*;

/**
 *
 * @author vanag
 */
public abstract class ofdmSplitCSPF extends genericSplitCSPFAlgorithm
{
    ofdmGraph gr;
    public ofdmSplitCSPF(genericRCM rcm)
    {
        super(rcm);  
        gr=(ofdmGraph) rcm.gr;
    }
    
     public List<genericBWSplit[]> createSplitMap(int bw)
     {
        List<genericBWSplit[]> splitter=new ArrayList<genericBWSplit[]>();
        int[] counter=new int[gr.truncations.length];
        int[] bounds=new int[gr.truncations.length];
        
        for(int i=0;i<bounds.length;i++)
        {
            bounds[i]= bw / gr.truncations[i];
            if(bw % gr.truncations[i] !=0)
            {
                bounds[i]+=2;
            }
            else
            {
                bounds[i]+=1;
            }
        }
        
        int index=bounds.length-1;
        boolean progress=true;
        do
        {
            
            if(progress)
            {
                int temp=0;
                for(int i=0;i<bounds.length;i++)
                {
                    temp+=((counter[i])*gr.truncations[i]);
                }
                /*
                System.out.println(">>>"+bw+">>>"+temp);
                for(int i=0;i<bounds.length;i++)
                {
                    System.out.println(i+";"+counter[i]+";"+bounds[i]+";"+gr.truncations[i]);
                    System.out.println("result="+(counter[i]*gr.truncations[i]));
                }
                System.out.println("---");
                */
                if(temp==bw)
                {
                    genericBWSplit[] splits=new genericBWSplit[bounds.length];
                    for(int i=0;i<bounds.length;i++)
                    {
                        splits[i]=new genericBWSplit();
                        splits[i].bw=gr.truncations[bounds.length-1-i];
                        splits[i].occupy=counter[bounds.length-1-i];
                    }
                    splitter.add(splits);
                }
                progress=false;
            }            
            
            counter[index]= ( (counter[index] + 1 ) % bounds[index]);
            if(counter[index]==0) //no, go back previous 
            {
                index--;
                
            }
            else //yes, use it
            {
                progress=true;
                index++;
            }
            if(index==bounds.length)
            {
                index--;
            }                                         
        }while(index!=-1); //we went to the beggining  
     return splitter;     
     }
     
      public genericSimulationReport  executeMeasurement(){
        return this.unsplit.executeMeasurement();
    }
          
}
