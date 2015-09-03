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
import genericSimulator.algorithm.combopt.lightWeightPath;
import genericSimulator.prefs.networkPrefs;
import genericSimulator.network.*;
import genericSimulator.*;
import genericSimulator.prefs.*;
import wdmSimulator.network.*;
import java.util.*;

/**
 *
 * @author vanag
 */
public class wdmPath extends genericPath
{
    
    /** Creates a new instance of impairedPath */    
    wdmPrefs optConf;
    wdmGraph gr;
    
    public wdmPath(wdmGraph somegr,genericReservation[] slots,double cost)
    {
        super(slots,cost);
        this.gr=somegr;
        this.optConf= gr.getOpticalConf();
        
        //public static  enum CONTROLCOSTS {COST,BER,HOPS,DISTANCE,LLRUTILIZATION,LRFRAGMENTATION};
    }
    
    
    public  double getBER()
    {
        double interaction=0;
        double tempinteraction=0;
        int distance=0;

        if(!this.optConf.isImpaired()) return 0;

        int bw;
        if(this.gr.isHCC())
        {
            for(genericReservation resv : this.reservations)
            {
                distance+=resv.edge.distance;
                interaction=resv.edge.getPollution(resv.hlrindex);
                tempinteraction= interaction+this.optConf
                        .getImpairmentPrefs().getGfactor()*tempinteraction;
            }           
            return this.gr.imp.tanya_getBER(tempinteraction,distance);
        }
        else
        {   
            double totalber=0;
            double ber;
            for(genericReservation resv : this.reservations)
            {
                distance+=resv.edge.distance;
                interaction=resv.edge.getPollution(resv.hlrindex);
                tempinteraction= interaction+
                        this.optConf.getImpairmentPrefs().getGfactor()*tempinteraction;
                ber=this.gr.imp.tanya_getBER(tempinteraction,distance);
                totalber=totalber+ber*(1-2*totalber);
            }            
            return totalber;            
        }
    }                        
}
