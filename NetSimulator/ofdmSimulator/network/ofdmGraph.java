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

package ofdmSimulator.network;
import genericSimulator.prefs.ofdmPrefs;
import genericSimulator.network.genericGraph;
import genericSimulator.network.genericEdge;
import genericSimulator.genericLink;
import genericSimulator.*;
import genericSimulator.network.genericResource;
import java.util.*;
import java.io.*;
/**
 *
 * @author vanag
 */
public final class ofdmGraph extends genericGraph        
{    
    ofdmPrefs ofdmConf;
    ofdmResource[][] overlaps;
    public Integer[] truncations;
    
    
    public ofdmGraph(genericSimulation gs)
    {
        super(gs); 
        ofdmConf=(ofdmPrefs) this.gs.prefs.getOfdmPrefs();      
    }
    
    public void loadBulkConstraints()
    {
        List<ofdmModulationToken> bulkconstraints=new ArrayList<ofdmModulationToken>();        
        BufferedReader in=null;
        ofdmResource res;
        System.out.println("Create search space");
        try
        {
            in= new BufferedReader(new FileReader(this.ofdmConf.getConstraints()));
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        String data_line;

                
        try
        {
            ofdmModulationToken tok;
            while( (data_line=in.readLine()) != null)
            {
                tok=new ofdmModulationToken(data_line);
                bulkconstraints.add(tok);                 
            }
            in.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        List<ofdmResource> rindex=new ArrayList<ofdmResource>();
        int upper;
        int index=0;
        for(ofdmModulationToken tok : bulkconstraints)
        {
            upper=this.gs.prefs.getNetworkPrefs().getLlrcount()-tok.slots+1;
            for(int k=0;k<upper;k++)
            {                    
                res=new ofdmResource(k,tok,index++);
                rindex.add(res);
            }
        }
        
        System.out.println("Resource space created "+rindex.size());
        this.resources=rindex.toArray(new ofdmResource[rindex.size()]);
        this.overlaps=new ofdmResource[this.resources.length][];
        List<ofdmResource> sack=new ArrayList<ofdmResource>();
        for(ofdmResource res1 : (ofdmResource[]) this.resources)
        {
            for(ofdmResource res2 : (ofdmResource[]) this.resources)
            {               
                if((res2.stopSlot<res1.startSlot)||(res2.startSlot>res1.stopSlot)) continue;               
                sack.add(res2); //put real overlaps
            }
            this.overlaps[res1.getIndex()]=sack.toArray(new ofdmResource[sack.size()]);
            sack.clear();
        }        
        int counter=0;
        for(ofdmResource [] acc : this.overlaps)
        {
            counter+=acc.length;
        }
        HashSet<Integer> bandwidths=new HashSet<Integer>();
        for(genericResource tres : this.getResources())
        {
            bandwidths.add(tres.getBw());
        }        
        this.truncations=bandwidths.toArray(new Integer[0]);
        Arrays.sort(truncations);
        System.out.println("Search space created "+counter);        
    }
    
     
    
    public List<genericEdge>  initializeEdges()
    {
        ArrayList<genericEdge> edge_list=new ArrayList();
        System.out.println("Initializing the edges ....");                        
        System.out.println("Passing the initializers ....");        
        genericLink temp_initializer;        
        this.loadBulkConstraints();
        for(Iterator it=this.getGs().gei_list.iterator();it.hasNext();)
        {        
            temp_initializer=(genericLink) it.next();            
            edge_list.add(new ofdmEdge(temp_initializer,this));
        }        
        return edge_list;
    }         
}
