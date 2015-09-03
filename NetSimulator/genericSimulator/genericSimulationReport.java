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
import java.util.*;
/**
 *
 * @author  vanag
 */
public final class genericSimulationReport
{ 
   public String[] names;
   public double [] values;

   
   public genericSimulationReport(String[] names)
   {            
       this.names=Arrays.copyOf(names,names.length);
       this.values=new double[names.length];          
   }
   
   public genericSimulationReport(genericSimulationReport sr1,genericSimulationReport sr2)
   {            
       this.names=new String[sr1.names.length+sr2.names.length];
       System.arraycopy(sr1.names,0,this.names,0,sr1.names.length);
       System.arraycopy(sr2.names,0,this.names,sr1.names.length,sr2.names.length);
       this.values=new double[sr1.values.length+sr2.values.length];
       System.arraycopy(sr1.values,0,this.values,0,sr1.values.length);
       System.arraycopy(sr2.values,0,this.values,sr1.values.length,sr2.values.length);
   }
   
    public static <T> T[] concat(T[] first, T[] second) 
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
   
   public double[] getValues()
   {        
       return this.values;      
   }
   
   public String toString()
   {
       String s="";
       for(int i=0;i<this.names.length;i++)
       {
           s+=(";"+this.names[i]+":"+this.values[i]);
       }
       return s;
   }
}
