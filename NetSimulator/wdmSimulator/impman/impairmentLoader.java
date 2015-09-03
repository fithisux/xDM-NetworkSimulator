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

import genericSimulator.prefs.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author vanag
 */
public final class impairmentLoader 
{       
       
    public List<speedTuple>[] impairments;
    public int dist;
    public int lambdas;

    /** Creates a new instance of impairmentLoader */
    public impairmentLoader(String s) throws Exception
    {
        long nois;
        int a,b,c,d;
        speedTuple m;
        
        BufferedReader in= new BufferedReader(new FileReader(s));
        String data_line= in.readLine();
        StringTokenizer zz= new StringTokenizer(data_line,";");
        zz.nextToken();
        dist=Integer.parseInt(zz.nextToken());
        lambdas=Integer.parseInt(zz.nextToken());

        impairments=new List[lambdas];
        for(int i=0;i<this.lambdas;i++)
        {
            this.impairments[i]=new ArrayList<speedTuple>();
        }
        
        //Order1
        data_line=in.readLine();//read Start        
        while(!(data_line=in.readLine()).equalsIgnoreCase("END"))
        {
            nois=Long.parseLong(data_line);
            a=Integer.parseInt(in.readLine());
            b=Integer.parseInt(in.readLine());            
            m=new speedTuple(nois,b,1);
            this.impairments[a].add(m);
        }

        //Order2
        data_line=in.readLine();//read Start
        while(!(data_line=in.readLine()).equalsIgnoreCase("END"))
        {
            nois=Long.parseLong(data_line);
            a=Integer.parseInt(in.readLine());
            b=Integer.parseInt(in.readLine());
            c=Integer.parseInt(in.readLine());
            m=new speedTuple(nois,c,2);            
            this.impairments[a].add(m);
            this.impairments[b].add(m);
        }

        //Order3
        data_line=in.readLine();//read Start
        while(!(data_line=in.readLine()).equalsIgnoreCase("END"))
        {
            nois=Long.parseLong(data_line);
            a=Integer.parseInt(in.readLine());
            b=Integer.parseInt(in.readLine());
            c=Integer.parseInt(in.readLine());
            d=Integer.parseInt(in.readLine());
            m=new speedTuple(nois,d,3);
            this.impairments[a].add(m);
            this.impairments[b].add(m);
            this.impairments[c].add(m);
        }        
        in.close();  
    }
    
    
        
}
