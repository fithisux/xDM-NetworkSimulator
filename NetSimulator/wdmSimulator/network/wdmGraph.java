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

package wdmSimulator.network;
import wdmSimulator.calculator.impairmentCalculator;
import genericSimulator.prefs.wdmPrefs;
import genericSimulator.network.genericGraph;
import genericSimulator.network.genericEdge;
import wdmSimulator.impman.impairedFilter;
import wdmSimulator.impman.impairmentLoader;
import genericSimulator.genericLink;
import genericSimulator.*;
import java.io.File;
import java.util.*;
import ofdmSimulator.network.ofdmEdge;
import ofdmSimulator.network.ofdmGraph;
/**
 *
 * @author vanag
 */
public final class wdmGraph extends genericGraph        
{    
    wdmPrefs otp;
    impairedFilter filter;
    public impairmentCalculator imp;
    
    
    
    
    
    /** Creates a new instance of impairedGraph */
    public wdmPrefs getOpticalConf(){return this.otp;}

    public wdmGraph(genericSimulation gs)
    {
        super(gs); 
        otp=this.gs.prefs.getWdmPrefs();
        this.filter=new impairedFilter(this.otp);
        if(this.otp.isImpaired())
        {
            this.imp=new impairmentCalculator(gs.prefs,0);
        }        
    }
    
     public int getHLRCount()
     {
         return gs.prefs.getNetworkPrefs().getLlrcount();
     }

    public impairedFilter getFilter() {
        return filter;
    }
         
    public List<genericEdge>  initializeEdges()
    {
        ArrayList<genericEdge> edge_list=new ArrayList();
        System.out.println("Initializing the edges ....");                        
        System.out.println("Passing the initializers ....");        
        genericLink temp_initializer;        

        for(Iterator it=this.getGs().gei_list.iterator();it.hasNext();)
        {        
            temp_initializer=(genericLink) it.next();            
            edge_list.add(new wdmEdge(temp_initializer,this,this.getGs()));
        }                
        
        return edge_list;
    }
    
    public void initializeImpairments()
    {
        int distance;
        String impfile;
        if(!this.otp.isImpaired()) return;
        System.out.println("Loading impairments");
        for(genericEdge anedge : this.edges)
        {
            distance=anedge.distance;
            impfile=this.getGs().prefs.getFilePrefs().getPath()+File.pathSeparator+"impaired_"+
                    distance+"_"+this.getGs().prefs.getNetworkPrefs().getLlrcount()+".impaired";
            try
            {
                impairmentLoader implder=new impairmentLoader(impfile);
                ((wdmEdge) anedge).loadImpairments(implder);
            }
            catch(Exception e)
            {
                System.out.println("An exception was thrown by loadImpairments and reason ->"+e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }                
        }
        System.gc();
    }
}
