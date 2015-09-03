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

package genericSimulator;
import genericSimulator.stochastic.genericVONScheduler;
import genericSimulator.stochastic.genericVONSchedule;
import genericSimulator.stochastic.genericVONTrafficGenerator;
import genericSimulator.network.genericNMS;
import genericSimulator.*;
import genericSimulator.network.*;
import java.util.*;
/**
 *
 * @author Administrator
 */
public final class dynamicScheduleManager
{
    genericSimulation gs;
    genericVONScheduler scheduler;
    genericNMS NMS;
    List<genericVONSchedule> expired;       
    int epocher;
    int events;

    public dynamicScheduleManager(genericSimulation somegsc,genericNMS someNMS)
    {
        this.gs=somegsc;
        int flowgens=this.gs.generators.size();
        if(flowgens==0)
        {
            System.out.println("TM cannot be empty");
            System.exit(0);
        }
        
        this.NMS=someNMS;
        this.scheduler=new genericVONScheduler();
        expired=new ArrayList<genericVONSchedule>();
        this.epocher=0;
        
    }

    public void startEpoch(int epoch)
    {
        for(genericVONTrafficGenerator gen : gs.generators)
        {
            gen.startEpoch(epoch);
        }                        
        for(genericVONTrafficGenerator gen : gs.generators)
        {
            genericVONSchedule schedule=gen.createArrivalSchedule();
            this.scheduler.scheduleObject(schedule);            
        }  
        this.epocher=epoch;
    }

    public void stopEpoch()
    {
        this.scheduler.reset(expired);        
        genericVONSchedule schedule;   
        while(!this.expired.isEmpty())
        {
            schedule=this.expired.remove(0);
            if(schedule.traffic instanceof genericRequest)
            {
                genericRequest request=(genericRequest) schedule.traffic;
                this.NMS.unrouteStraightRequest(request);
            }
        }                      
        this.NMS.reset();
        this.events=0;
    }
    
        
    public int HeartBeat()
    {      
        this.scheduler.HeartBeat(expired);                             
        while(!this.expired.isEmpty())
        {
            genericVONSchedule schedule=this.expired.remove(0);
            if(schedule.traffic instanceof genericRequest)
            {
                genericRequest request=(genericRequest) schedule.traffic;
                //System.out.println("finalize "+request.toString());                
                this.NMS.unrouteStraightRequest(request);                
            }
            else
            {
                genericVONSchedule nschedule=this.gs.generators.get(schedule.generatorId).createArrivalSchedule();
                this.scheduler.scheduleObject(nschedule);
                genericRequest request=(genericRequest) ((genericVONSchedule) schedule.traffic).traffic;
                this.events++;
                if(this.NMS.routeStraightRequest(request))
                {
                    this.scheduler.scheduleObject( (genericVONSchedule) schedule.traffic);    
                }
                //System.out.println("reschedule "+request.toString());
            }
        }                    
        return this.events;
    }
}
