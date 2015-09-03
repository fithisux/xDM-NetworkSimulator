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

import genericSimulator.network.genericRCM;
import genericSimulator.network.genericProtectedRCM;
import genericSimulator.network.genericUnprotectedRCM;
import wdmSimulator.impman.impairmentCalcCenter;
import wdmSimulator.network.wdmGraph;
import genericSimulator.prefs.genericOrientation;
import genericSimulator.prefs.simulationPrefs;
import genericSimulator.prefs.trafficPrefs;
import genericSimulator.prefs.nodeInjectorPrefs;
import genericSimulator.prefs.linkPrefs;
import genericSimulator.prefs.pairInjectorPrefs;
import genericSimulator.prefs.linkCharacteristics;
import genericSimulator.prefs.distributionPrefs;
import genericSimulator.prefs.topologyPrefs;
import ofdmSimulator.network.*;
import genericSimulator.network.*;
import java.io.*;
import java.util.*;
import genericSimulator.*;
import wdmSimulator.*;
import genericSimulator.stochastic.genericDistribution;
import genericSimulator.stochastic.genericVONTrafficGenerator;
import java.lang.reflect.Constructor;
import javax.xml.bind.*;
    
/**
 *
 * @authnms
 * or  vanag
 */
public abstract class genericSimulation
{        
    //Basic objects for the simulation    
    genericStateMeasurements measurement_mg;
    public simulationPrefs prefs;
    public List<genericVONTrafficGenerator> generators;
    public List<genericLink> gei_list;
    public genericNMS nms;
    public static Random seeder=new Random(0);
    

    public void recoverGenerators()
    {
        int index=0;
        trafficPrefs x= this.prefs.getTrafficPrefs();        
        for(nodeInjectorPrefs nipref : x.getListOfNodeInjectorPrefs())
        {
            distributionPrefs arrivalPrefs=nipref.getArrivalTime();
            distributionPrefs servicePrefs=nipref.getServiceTime();
            distributionPrefs bandwidthPrefs=nipref.getBandwidth();
            
            for(Integer ng : nipref.getGenerators())
            {
                genericIntPair p=new genericIntPair(ng.intValue(),
                        this.prefs.getNetworkPrefs().getNodes());                
                genericDistribution [] distros=new  genericDistribution[3];
                distros[0]=genericDistribution.manufacture(bandwidthPrefs, genericSimulation.seeder);
                distros[1]=genericDistribution.manufacture(arrivalPrefs, genericSimulation.seeder);
                distros[2]=genericDistribution.manufacture(servicePrefs, genericSimulation.seeder);
                
                genericVONTrafficGenerator pi=new genericNodeRequestFactory(p,distros,index++,genericSimulation.seeder);
                this.generators.add(pi);
            }
        }
        for(pairInjectorPrefs nipref : x.getListOfPairInjectorPrefs())
        {
            distributionPrefs arrivalPrefs=nipref.getArrivalTime();
            distributionPrefs servicePrefs=nipref.getServiceTime();
            distributionPrefs bandwidthPrefs=nipref.getBandwidth();
            
            for(genericOrientation ng : nipref.getGenerators())
            {
                genericIntPair p=new genericIntPair(ng.getFrom(),ng.getTo());                
                genericDistribution [] distros=new  genericDistribution[3];
                distros[0]=genericDistribution.manufacture(bandwidthPrefs, genericSimulation.seeder);
                distros[1]=genericDistribution.manufacture(arrivalPrefs, genericSimulation.seeder);
                distros[2]=genericDistribution.manufacture(servicePrefs, genericSimulation.seeder);
                
                genericVONTrafficGenerator pi=new genericPairRequestFactory(p,distros,index++);
                this.generators.add(pi);
            }
        }
    }
    
    public void recoverLinks()
    {
        HashMap<String,impairmentCalcCenter> calcs=new HashMap<String,impairmentCalcCenter>();
        topologyPrefs nprefs=this.prefs.getTopologyPrefs();
        for(linkPrefs lp : nprefs.getListOfLinkPrefs())
        {
            linkCharacteristics chars=lp.getLinkCharacteristics();
            int copies=chars.getCopies();
            boolean monitored=chars.isMonitor();
            int distance=chars.getDistance();
            if(this.prefs.getWdmPrefs()!=null)
            {
                String imp_file=new String(this.prefs.getFilePrefs().getPath()+
                        File.pathSeparator+"impaired_"+
                        distance+"_"+this.prefs.getNetworkPrefs().getLlrcount()+
                        ".impaired");
                if(!calcs.containsKey(imp_file))
                {
                    impairmentCalcCenter acalc=new impairmentCalcCenter(prefs,distance);
                    acalc.compressTerms();
                    acalc.filterTerms();
                    try
                    {
                        acalc.fillWithNoise(imp_file);
                        calcs.put(imp_file, acalc);
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
            }
            
            for(genericOrientation ng : lp.getListOfGenericOrientations())
            {
                for(int i=0;i<copies;i++)
                {
                    genericIntPair p=new genericIntPair(ng.getFrom(),ng.getTo());
                    if(ng.isBidirectional())
                    {
                        genericIntPair pp=p.toggle();
                        genericLink link=new genericLink();
                        link.setPair(pp);
                        link.setDistance(distance);
                        link.setMonitorable(monitored);
                        this.gei_list.add(link);
                    }
                    genericLink link=new genericLink();
                    link.setPair(p);
                    link.setDistance(distance);
                    link.setMonitorable(monitored);
                    this.gei_list.add(link);
                }
            }
        }
    }
    
    public genericSimulation(String configuration_xml)
    {
        this.nms=null;
        this.prefs=null; 
        try {
            JAXBContext jc = JAXBContext.newInstance(simulationPrefs.class);
            Unmarshaller u = jc.createUnmarshaller();
 
            File fin = new File(configuration_xml);
            this.prefs = (simulationPrefs) u.unmarshal(fin);
            
            File fout = new File(this.prefs.getFilePrefs().getPath()+File.separator+"config.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(simulationPrefs.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 
            jaxbMarshaller.marshal(this.prefs, fout);
 
            
        } catch (JAXBException e) {
            e.printStackTrace();
        }                
        prefs.checkLogic();      
        this.gei_list=new ArrayList<genericLink>();
        this.generators=new ArrayList<genericVONTrafficGenerator>();
        this.recoverLinks();
        this.recoverGenerators();
    }    

    public genericSimulation(List<genericVONTrafficGenerator> generators) {
        this.generators = generators;
    }
  
    public abstract void executeSimulation() throws IOException;
	
      
    public void initializeComponents()
    {        
        genericGraph gr=null;
        try 
        {
            if(this.prefs.getWdmPrefs()!=null)
            {
                gr=new wdmGraph(this);
            }else if(this.prefs.getOfdmPrefs()!=null)
            {
                gr=new ofdmGraph(this);
            }
            gr.fillEdges();
            if(this.prefs.getWdmPrefs()!=null)
            {
                ((wdmGraph) gr).initializeImpairments();
            }                    
            genericRCM rcm;
            
            if(this.prefs.getProtectionPrefs().isProtected())
            {               
                rcm=new genericProtectedRCM(gr);
            }
            else
            {                
                rcm=new genericUnprotectedRCM(gr);
            }
            rcm.loadAlgorithm();
            nms=new genericNMS(rcm);                                                                
        }
        catch(Exception e)
        {
            System.out.println("Initialization of NMS failed , aborting because ");
            e.printStackTrace();
            System.exit(0);
        }
    }      
  }        
