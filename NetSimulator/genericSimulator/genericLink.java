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
import genericSimulator.*;

public final class genericLink
{	
    private genericIntPair pair;
    private int distance;
    private boolean monitorable;
    private int Id;
    public static int ids=0;
    

    public genericLink()
    {
        this.Id=genericLink.ids++;
    }
    
    public final int getDistance() {
        return distance;
    }

    public final void setDistance(int distance) {
        this.distance = distance;
    }

    public final int getId() {
        return Id;
    }

    public final void setId(int Id) {
        this.Id = Id;
    }

    public final boolean isMonitorable() {
        return monitorable;
    }

    public final void setMonitorable(boolean monitorable) {
        this.monitorable = monitorable;
    }

    public final genericIntPair getPair() {
        return pair;
    }

    public final void setPair(genericIntPair pair) {
        this.pair = pair;
    }

    public final String toString()
    {
            String s = "A descriptor "+
                    " , with Id "+Integer.toString(Id)+
                    " , with direction ("+Integer.toString(pair.ef)+","+Integer.toString(pair.et)+")"+
                    " , with distance "+Integer.toString(distance)+
                    " , with monitorable "+Boolean.toString(monitorable);
            return s;
    }
               
}
