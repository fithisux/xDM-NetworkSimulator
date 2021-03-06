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
import java.util.*;
@XmlRootElement
public class distributionPrefs 
{
    public enum DISTRIBUTIONTYPE{distroDeterministic,distroBernoulli,distroUniform,distroLUT,distroGaussian};
    private DISTRIBUTIONTYPE name;
    private double simXi;
    private ArrayList<genericParam> parameters;

    @XmlAttribute(name = "name" )
    public DISTRIBUTIONTYPE getName() {
        return name;
    }

    public void setName(DISTRIBUTIONTYPE name) {
        this.name = name;
    }

    @XmlAttribute(name = "simXi" )
    public double getSimXi() {
        return simXi;
    }

    public void setSimXi(double simXi) {
        this.simXi = simXi;
    }

    @XmlElementWrapper(name = "parameters" )
    @XmlElement(name = "genericParam")
    public ArrayList<genericParam> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<genericParam> parameters) {
        this.parameters = parameters;
    }
    
    void checkLogic()
    {
        if(this.simXi <= 0)
            
        {
            System.out.println("simXi must be >=0");
            System.exit(0);
        }
    }
}
