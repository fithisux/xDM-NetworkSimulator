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

import genericSimulator.prefs.protectionPrefs;
import genericSimulator.network.*;
import genericSimulator.*;
import genericSimulator.algorithm.genericAlgorithm;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 *
 * @author Administrator
 */
public abstract class genericRCM {

    protected protectionPrefs protectionprefs;
    public int affected;
    public int invalidated;
    protected int served;
    protected int rejected;
    public int bw_requested;
    protected int bw_served;
    protected int bw_rejected;
    
    genericAlgorithm algorithm;
    public genericGraph gr;
    public genericSimulation gs;
    protected int paths;
    protected double[] metrics; 
    public HashMap<genericRequest,List<genericPath>> inservice;

    public genericRCM(genericGraph gr) {
        this.inservice = new HashMap<genericRequest,List<genericPath>>();
        this.gr = gr;
        this.gs = this.gr.getGs();
        this.protectionprefs = this.gs.prefs.getProtectionPrefs();
        this.algorithm = null;
        this.paths=this.served = this.rejected = this.affected=this.invalidated= 0;
        this.bw_served=this.bw_requested=this.bw_rejected=0;
        metrics = new double[genericPath.metricnames.length];        
    }

    
    

    public void observeNewSolution(List<genericPath> solution) {
        for (genericPath path : solution) {
            assert (path.isReserved());
            if (this.protectionprefs.isProtected()) {
                assert (path.getPrimary()!=null);
                assert (path.getPrimary().isReserved());                
            } else {
                assert (path.getPrimary()==null);
            }
        }
        for (genericPath path : solution) {
            genericPath metrical;
            if(path.getPrimary()==null)
            {
                metrical=path;
            }
            else
            {
                metrical=path.getPrimary();
            }
            int index=0;
            for (double d : metrical.getMetrics().values) {
                this.metrics[index++] += d;
            }
            this.bw_served+=path.reservations[0]
                    .edge.getHLRs()[path.reservations[0].hlrindex]
                    .res.getBw();
        }
        this.paths += solution.size();
    }

    public protectionPrefs getProtectionprefs() {
        return protectionprefs;
    }

    public void unroute(genericRequest request) {
        List<genericPath> solution=this.inservice.remove(request);
        assert(solution!=null);
        for(genericPath path : solution)
        {
            genericPath primary=path.getPrimary();
            if(primary !=null)
            {
                if(primary.isReserved()) primary.releaseResources();
            }
            if(path.isReserved()) path.releaseResources();
        }
        this.inservice.remove(request);        
    }

    public boolean route(genericRequest request) {
        //System.out.println("Route");
        assert (inservice.get(request) == null);
        this.bw_requested+=request.getBw();
        List<genericPath> solution = this.algorithm.compute(request);
        if (!solution.isEmpty()) { 
            for(genericPath p : solution)
            {
                assert(p.isReserved());
            }
            this.served++;            
            this.observeNewSolution(solution);
            inservice.put(request, solution);
            return true;            
        } 
        else
        {
            this.rejected++;
            this.bw_rejected+=request.getBw();
            return false;
        }
    }

    public boolean isEmpty() {
        return this.inservice.isEmpty() && this.gr.isEmpty();
    }

    public abstract void handleFailures();

    public void loadAlgorithm() {
        String algourl = "genericSimulator.algorithm";

        if (gs.prefs.getOfdmPrefs() != null) {
            algourl += ".ofdm.";
        } else {
            algourl += ".wdm.";
        }
        algourl += gs.prefs.getAlgorithmPrefs().getName();

        System.out.println("Announcing " + algourl);

        try {
            Class cc = Class.forName(algourl);
            Class[] cons = new Class[1];
            cons[0] = Class.forName("genericSimulator.network.genericRCM");
            Constructor c = cc.getConstructor(cons);
            Object[] oo = new Object[1];
            oo[0] = this;
            this.algorithm = (genericAlgorithm) c.newInstance(oo);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }    

    
    public genericSimulationReport executeMeasurement() {
       
        String[] s = {
            "Served",            
            "Rejected",
            "Affected",
            "Invalidated",
            "Carried",
            "Solutions",
            "BW_Requested",
            "BW_Served",
            "BW_Rejected"
        };
        genericSimulationReport sr1 = new genericSimulationReport(s);
        sr1.values[0] = (double) this.served;
        sr1.values[1] = (double) this.rejected;
        sr1.values[2] = (double) this.affected;
        sr1.values[3] = (double) this.invalidated;
        sr1.values[4] = (double) this.inservice.size();
        sr1.values[5] = (double) this.paths;
        sr1.values[6] = (double) this.bw_requested;
        sr1.values[7] = (double) this.bw_served;
        sr1.values[8] = (double) this.bw_rejected;
        
        genericSimulationReport sr2 = new genericSimulationReport(genericPath.metricnames);
        for (int i = 0; i < this.metrics.length; i++) {
            sr2.values[i] = this.metrics[i];
        }
        genericSimulationReport sr3 = this.algorithm.executeMeasurement();        
        this.paths=this.served = this.rejected = this.affected=this.invalidated= 0;
        this.bw_served=this.bw_requested=this.bw_rejected=0;
        Arrays.fill(metrics, 0);
        sr2=new genericSimulationReport(sr2, sr3);
        return new genericSimulationReport(sr1, sr2);
    }    
}
