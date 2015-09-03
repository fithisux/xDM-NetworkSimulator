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
import genericSimulator.steadyStateMeasurements;
import ofdmSimulator.network.*;
import genericSimulator.network.*;
import java.io.*;
import java.util.*;
import genericSimulator.*;
import genericSimulator.prefs.*;
import genericSimulator.dynamicScheduleManager;
/**
 *
 * @authnms
 * or  vanag
 */
public class steadysimulation extends genericSimulation
{        
    //Basic objects for the simulation    
    steadyStateMeasurements measurement_mg;
    
    public steadysimulation(String configuration_xml) throws Exception
    {
        super(configuration_xml);        
    }    
  
    public void executeSimulation() throws IOException
    {          
        ArrayList<genericEdge> edgefaults=new ArrayList<genericEdge>();
        System.out.println("Simulation started , please wait ....");
        this.measurement_mg=new steadyStateMeasurements(this.nms,edgefaults);        
        Random rr=genericSimulation.seeder;
        double availability;
        double availabilityfactor;
        dynamicMeasurementPrefs dp=this.prefs.getDynamicMeasurementPrefs();
        steadyMeasurementPrefs sp=this.prefs.getSteadyMeasurementPrefs();
        
        availabilityfactor=(sp.getStopAvailability()-sp.getStartAvailability());
        
        availabilityfactor /= (sp.getAvailabilitysteps()-1);
        FileWriter plots = new FileWriter(this.prefs.getFilePrefs().getPath()+"/plot");
        for(int i=0;i<sp.getAvailabilitysteps();i++)
        {
            availability= sp.getStartAvailability();
            availability += (availabilityfactor * i);
              
            System.out.println("started with availability step "+i+" and availability "+availability);
            FileWriter epoch = new FileWriter(this.prefs.getFilePrefs().getPath()+"/epoch"+(i-1));
            for(int iters=1;iters<=sp.getIterations();iters++)
            {     
                dynamicScheduleManager sm=new dynamicScheduleManager(this,this.nms);
                sm.startEpoch(sp.getEpoch());
                int Tmax=dp.getEpochsteps()*dp.getTimestep();
                int t=0;
                do                
                {
                    t=sm.HeartBeat();
                }while(t < Tmax);
                genericGraph gr=measurement_mg.getNms().getGr();                
                for(genericEdge edge : gr.getEdges())
                {
                    double x=rr.nextDouble();
                    if(x >= availability)
                    {
                        edgefaults.add(edge);
                    }
                }
                genericSimulationReport sr=this.measurement_mg.updateReport(); 
                String reporto="availability:"+availability+";iterations:"+iters+";"+sr;
                System.out.println(reporto);                    
                epoch.write(reporto+"\n");
                edgefaults.clear();
                sm.stopEpoch();                               
            } 
            epoch.close();
            genericSimulationReport sr=this.measurement_mg.finalizeSimulation();
            String reporto="availability:"+availability+";"+sr;
            System.out.println(reporto);
            plots.write(reporto+"\n");        
        }
        plots.close();
        System.out.println("Kiss my simulator goodbye .....");         
    }
        
    public static void main(String[] args)
    {
        steadysimulation asimulator=null;

        try
        {
            asimulator = new steadysimulation(args[0]);
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
