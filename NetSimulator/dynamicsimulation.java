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

import genericSimulator.dynamicScheduleManager;
import genericSimulator.dynamicStateMeasurements;
import ofdmSimulator.network.*;
import genericSimulator.network.*;
import java.io.*;
import java.util.*;
import genericSimulator.*;
import genericSimulator.prefs.*;
/**
 *
 * @authnms
 * or  vanag
 */
public class dynamicsimulation extends genericSimulation
{        
    //Basic objects for the simulation    
    dynamicStateMeasurements measurement_mg;
    
    public dynamicsimulation(String configuration_xml) throws Exception
    {
        super(configuration_xml);        
    }    
  
    public void executeSimulation() throws IOException
    {          
        System.out.println("Simulation started , please wait ....");
        this.measurement_mg=new dynamicStateMeasurements(this.nms);
        dynamicScheduleManager sm=new dynamicScheduleManager(this,this.nms);
        dynamicMeasurementPrefs dp=this.prefs.getDynamicMeasurementPrefs();
        FileWriter plots = new FileWriter(this.prefs.getFilePrefs().getPath()+"/plot");                  
        for(int i=dp.getStartEpoch();i<=dp.getStopEpoch();i++)
        {
            FileWriter epoch = new FileWriter(this.prefs.getFilePrefs().getPath()+"/epoch"+i);
            sm.startEpoch(i);
            System.out.println("started for epoch "+i);
            int iters=1;
            int time=0;
            while(iters <= dp.getEpochsteps())
            {
                for(int j=0;j<dp.getTimestep();j++)
                {
                    sm.HeartBeat();
                }                
                time+=dp.getTimestep();
                genericSimulationReport sr=this.measurement_mg.updateReport(); 
                String reporto="time:"+time+";iterations:"+iters+";"+sr;
                System.out.println(reporto);                    
                epoch.write(reporto+"\n");                  
                iters++;
            }    
            epoch.close();
            genericSimulationReport sr=this.measurement_mg.finalizeSimulation();
            String reporto="epoch:"+i+";"+sr;
            System.out.println(reporto);
            plots.write(reporto+"\n");
            sm.stopEpoch();
        }
        plots.close();
        System.out.println("Kiss my simulator goodbye .....");         
    }
        
    public static void main(String[] args)
    {
        dynamicsimulation asimulator=null;

        try
        {
            asimulator = new dynamicsimulation(args[0]);
            asimulator.initializeComponents();
        }
        catch(Exception e)
        {
            System.out.println("OOPS "+e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }

        try
        {
            asimulator.executeSimulation();            
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
        
  }        
