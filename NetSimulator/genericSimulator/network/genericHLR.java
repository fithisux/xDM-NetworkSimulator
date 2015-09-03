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

package genericSimulator.network;
import genericSimulator.algorithm.combopt.lightWeightEdge;
import genericSimulator.*;
import java.util.*;
/**
 *
 * @author Administrator
 */
public class genericHLR
{
    public enum RESOURCE_STATE{BACKUP,PRIMARY,FREE};
    private RESOURCE_STATE state;
    public long pollution;
    private List<genericPath> reservations;
    boolean ofdmconstraint;
    
    
    public boolean canReserve(int bw)
    {
        if(ofdmconstraint && (this.pollution!=0) ) return false;            
        if(res.getBw()!=bw) return false;
        return true;
    }
    
    public void insert(genericPath path)
    {                
        if(this.reservations.isEmpty())
        {
            assert (this.state==RESOURCE_STATE.FREE);
            if(path.getPrimary()==null)
            {
                this.state=RESOURCE_STATE.PRIMARY;
            }
            else
            {
                this.state=RESOURCE_STATE.BACKUP;
            }
        }
        else
        {
            assert (this.state==RESOURCE_STATE.BACKUP);
            assert(path.getPrimary()!=null);
        }
        this.reservations.add(path);
    }
    
    public void remove(genericPath path)
    {
        this.reservations.remove(path);
        if(this.reservations.isEmpty())
        {
            this.state=RESOURCE_STATE.FREE;
        }
    }
    
    public RESOURCE_STATE getState(){return this.state;}
   
    
    public List<genericPath> getList()
    {
        return this.reservations;
    }
    
    public genericResource res;
            
    public genericHLR(boolean isofdm, genericResource someres)
    {        
        this.ofdmconstraint=isofdm;
        this.state=RESOURCE_STATE.FREE;
        this.reservations=new ArrayList<genericPath>();
        this.pollution=0;
        this.res=someres;
    }
    
    public String toString()
    {
        return "An hlr with resource "+res+" and state "+state+" pollution "+pollution;        
    }
}
