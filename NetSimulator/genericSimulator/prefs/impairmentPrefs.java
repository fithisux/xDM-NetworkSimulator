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


/**
 *
 * @author Administrator
 */
import javax.xml.bind.annotation.*;
 
@XmlRootElement
public class impairmentPrefs 
{
    private double berbound;
    private double gfactor;
    private double Pinput;
    private double tolerance;
    private boolean enablexpm;
    private boolean enablefwm;
    
    @XmlElement
    public double getBerbound() {
        return berbound;
    }
    
    public void setBerbound(double berbound) {
        this.berbound = berbound;
    }

    @XmlElement
    public double getGfactor() {
        return gfactor;
    }

    public void setGfactor(double gfactor) {
        this.gfactor = gfactor;
    }

    @XmlElement
    public double getPinput() {
        return Pinput;
    }

    public void setPinput(double Pinput) {
        this.Pinput = Pinput;
    }

    @XmlElement
    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    @XmlElement
    public boolean isEnablexpm() {
        return enablexpm;
    }

    public void setEnablexpm(boolean enablexpm) {
        this.enablexpm = enablexpm;
    }

    @XmlElement
    public boolean isEnablefwm() {
        return enablefwm;
    }

    public void setEnablefwm(boolean enablefwm) {
        this.enablefwm = enablefwm;
    }
    
    
    public void checkLogic()
    {        
        if((this.tolerance > 1) || (this.tolerance < 0))
        {
            System.out.println("tolerance is not a 0..1 number");
            System.exit(0);
        }
        
        if(this.berbound <= 0)
            
        {
            System.out.println("berbound must be > 0");
            System.exit(0);
        }
        
        if(this.Pinput <= 0)
            
        {
            System.out.println("Pinput must be > 0");
            System.exit(0);
        }
        
        if(this.gfactor <  0)
            
        {
            System.out.println("gfactor must be >= 0");
            System.exit(0);
        }
    }
    
}
