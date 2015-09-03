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

package wdmSimulator.impman;
import genericSimulator.prefs.wdmPrefs;
import genericSimulator.network.*;
import java.util.*;
import genericSimulator.*;
import genericSimulator.network.genericHLR;
import wdmSimulator.network.wdmPath;
/**
 *
 * @author Administrator
 */
public final class impairedFilter
{
    public enum BER_ERROR { NOERROR,BERERROR,INTERACTIONERROR};
    private BER_ERROR state;
    private double ber;
    private wdmPrefs sim_optical_type_prefs;
    private int solution_counter;
    private int ber_failure_counter;
    private int if_failure_counter;
    public List<genericPath> affected;

    public impairedFilter(wdmPrefs otp)
    {
        this.state=BER_ERROR.NOERROR;
        this.ber=0;
        this.sim_optical_type_prefs=otp;
        this.resetCounters();
        affected=new ArrayList<genericPath>();
    }

    public int getBer_failure_counter() {
        return ber_failure_counter;
    }

    public int getIf_failure_counter() {
        return if_failure_counter;
    }

    public int getSolution_counter() {
        return solution_counter;
    }
    
    public void resetCounters()
    {
        this.solution_counter=this.ber_failure_counter=this.if_failure_counter=0;
    }

    public double getBer() {
        return ber;
    }
   
    public BER_ERROR getState() {
        return state;
    }

    public wdmPath checkAgainstReserved(wdmPath lsp)
    {
         this.state=BER_ERROR.NOERROR;
         this.ber=0;
         this.solution_counter++;
         if(this.sim_optical_type_prefs.isImpaired())
         {
             wdmPath somepath;
             double probeBER;
             lsp.reserveResources();
             assert (this.affected.isEmpty());
             for(genericReservation slot : lsp.getReservations())
             {
                 slot.edge.residents(this.affected);                 
             }             
             this.ber=lsp.getBER();
             if(this.ber > this.sim_optical_type_prefs.getImpairmentPrefs().getBerbound())
             {
                this.state=BER_ERROR.BERERROR;
             }
             else
             {
                for(genericPath path : new HashSet<genericPath>(this.affected))
                {
                    probeBER=((wdmPath) path).getBER();
                    if(probeBER > this.sim_optical_type_prefs.getImpairmentPrefs().getBerbound())
                    {
                        this.state=BER_ERROR.INTERACTIONERROR;                                                        
                    }                              
                }                
             }
             this.affected.clear();
             lsp.releaseResources();
         }
         
         
         switch(this.state)
         {
             case NOERROR : return lsp;
             case BERERROR :  this.ber_failure_counter++; return null;
             case INTERACTIONERROR :  this.if_failure_counter++;; return null;
         }
         System.out.println("Invalid state");
         System.exit(0);
         return null;         
    }

}
