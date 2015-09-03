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
import genericSimulator.algorithm.combopt.*;
import java.util.List;
/**
 *
 * @author vanag
 */
public class testYen 
{
    public static void main(String[] args)
    {
        lightWeightGraph gr=new lightWeightGraph(3);
        lightWeightEdge e;
        for(int i=0;i<10;i++)
        {
            e=new lightWeightEdge();
            e.index=i;
            e.color=0;
            e.cost=2*i+1;
            e.from=0;
            e.to=1;
            e.hide=false;
            gr.add(e);
        }
        for(int i=0;i<10;i++)
        {
            e=new lightWeightEdge();
            e.index=i;
            e.color=0;
            e.cost=2*i+1;
            e.from=1;
            e.to=2;
            e.hide=false;
            gr.add(e);
        }
        genericYenAlgorithm yen=new genericYenAlgorithm(gr,500);
        List<lightWeightPath> paths=yen.solveFor(0,2);
        
        for(lightWeightPath p : paths)
        {
            System.out.println(p);
        }
        
        
    }
    
}
