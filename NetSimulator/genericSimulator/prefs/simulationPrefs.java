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
package genericSimulator.prefs;

import javax.xml.bind.annotation.*;
 
@XmlRootElement
public class simulationPrefs 
{    
    private ofdmPrefs ofdmPrefs;
    private wdmPrefs wdmPrefs;
    private steadyMeasurementPrefs steadyMeasurementPrefs;
    private dynamicMeasurementPrefs dynamicMeasurementPrefs;
    private protectionPrefs protectionPrefs;
    private algorithmPrefs algorithmPrefs;
    private filePrefs filePrefs;
    private topologyPrefs topologyPrefs;
    private networkPrefs networkPrefs;
    private trafficPrefs trafficPrefs;

    @XmlElement
    public ofdmPrefs getOfdmPrefs() {
        return ofdmPrefs;
    }

    public void setOfdmPrefs(ofdmPrefs ofdmPrefs) {
        this.ofdmPrefs = ofdmPrefs;
    }

    @XmlElement
    public wdmPrefs getWdmPrefs() {
        return wdmPrefs;
    }

    public void setWdmPrefs(wdmPrefs wdmPrefs) {
        this.wdmPrefs = wdmPrefs;
    }

    @XmlElement
    public steadyMeasurementPrefs getSteadyMeasurementPrefs() {
        return steadyMeasurementPrefs;
    }

    public void setSteadyMeasurementPrefs(steadyMeasurementPrefs steadyMeasurementPrefs) {
        this.steadyMeasurementPrefs = steadyMeasurementPrefs;
    }

    @XmlElement
    public dynamicMeasurementPrefs getDynamicMeasurementPrefs() {
        return dynamicMeasurementPrefs;
    }

    public void setDynamicMeasurementPrefs(dynamicMeasurementPrefs dynamicMeasurementPrefs) {
        this.dynamicMeasurementPrefs = dynamicMeasurementPrefs;
    }

    @XmlElement
    public protectionPrefs getProtectionPrefs() {
        return protectionPrefs;
    }

    public void setProtectionPrefs(protectionPrefs protectionPrefs) {
        this.protectionPrefs = protectionPrefs;
    }

    @XmlElement
    public algorithmPrefs getAlgorithmPrefs() {
        return algorithmPrefs;
    }

    public void setAlgorithmPrefs(algorithmPrefs algorithmPrefs) {
        this.algorithmPrefs = algorithmPrefs;
    }

    @XmlElement
    public filePrefs getFilePrefs() {
        return filePrefs;
    }

    public void setFilePrefs(filePrefs filePrefs) {
        this.filePrefs = filePrefs;
    }

    @XmlElement
    public topologyPrefs getTopologyPrefs() {
        return topologyPrefs;
    }

    public void setTopologyPrefs(topologyPrefs topologyPrefs) {
        this.topologyPrefs = topologyPrefs;
    }

    public networkPrefs getNetworkPrefs() {
        return networkPrefs;
    }

    public void setNetworkPrefs(networkPrefs networkPrefs) {
        this.networkPrefs = networkPrefs;
    }

    public trafficPrefs getTrafficPrefs() {
        return trafficPrefs;
    }

    public void setTrafficPrefs(trafficPrefs trafficPrefs) {
        this.trafficPrefs = trafficPrefs;
    }
    
    public void checkLogic()
    {
        if( (this.ofdmPrefs==null) && (this.wdmPrefs==null) )
        {
            System.out.println("cannot proceed with no network type");
            System.exit(0);
        }
        
        if( (this.ofdmPrefs!=null) && (this.wdmPrefs!=null) )
        {
            System.out.println("cannot proceed with both network type");
            System.exit(0);
        }
        
        this.dynamicMeasurementPrefs.checkLogic();
        this.filePrefs.checkLogic();
        this.networkPrefs.checkLogic();
        if(this.ofdmPrefs!=null) this.ofdmPrefs.checkLogic(this.networkPrefs);
        if(this.wdmPrefs!=null) this.wdmPrefs.checkLogic();
        this.steadyMeasurementPrefs.checkLogic();        
    }
    
    
    
}
