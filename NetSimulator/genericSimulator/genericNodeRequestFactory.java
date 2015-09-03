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
import genericSimulator.stochastic.genericDistribution;
import genericSimulator.stochastic.genericVONTrafficGenerator;
import genericSimulator.network.genericRequest;
import java.util.*;

public class genericNodeRequestFactory extends genericVONTrafficGenerator
{
    int node;
    Random randomnode;
    int numoutnodes;

    public genericNodeRequestFactory(genericIntPair p,genericDistribution[] dists,int someid,Random slgen)
    {
        super(p,dists,someid);
        this.node=p.ef;
        randomnode=slgen;
        this.numoutnodes=p.et-1;
    }

   
    public genericIntPair getPair()
    {
        int nextnode=randomnode.nextInt(numoutnodes);
        genericIntPair p=null;
        if(nextnode < this.node)
        {
            p=new genericIntPair(node,nextnode);
        }
        else
        {
            p=new genericIntPair(node,nextnode+1);
        }
        return p;
    }
}


