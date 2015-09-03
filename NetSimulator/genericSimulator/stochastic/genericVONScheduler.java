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

package genericSimulator.stochastic;
import java.util.*;

/**
 *
 * @author  vanag
 */
public final class genericVONScheduler
{
    private List<genericTask> scheduledTokens;
    private int timer;

    /** Creates a new instance of vonTokenScheduler */
    public genericVONScheduler()
    {
        scheduledTokens = new ArrayList<genericTask>();
        this.timer=0;
    }

    
    public int getTimer() {
        return timer;
    }

    public void scheduleObject(genericVONSchedule schedule)
    {
        assert (schedule.duration >= 0 ) : "Pushed non-causal task";
        genericTask task=new genericTask();
        task.startTime=timer;
        task.stopTime=timer+schedule.duration;
        task.s=schedule;            
        this.scheduledTokens.add(task);   
    }

    public void HeartBeat(List<genericVONSchedule> expired)
    {
        genericTask bb;
                
        for(Iterator<genericTask> it=this.scheduledTokens.iterator();it.hasNext();)
        {
            bb=it.next();            
            if(bb.stopTime==timer)
            {
                it.remove();
                expired.add(bb.s);                
            }            
        }  
        this.timer++;
    }

    public  void reset(List<genericVONSchedule> expired)
    {
        while(scheduledTokens.size()>0)
        {
            expired.add(scheduledTokens.remove(0).s);
        }
        timer=0;
    }

    public String toString()
    {
        String mesg;
        int index=0;
        mesg="A scheduler  with size "+scheduledTokens.size();
        mesg+=" at time "+(this.timer)+"\n";
        for(genericTask tok : this.scheduledTokens)
        {
            mesg+="Token "+index+" with remove time "+tok.stopTime+"\n";
            index++;
        }
        return mesg;
    }
}
