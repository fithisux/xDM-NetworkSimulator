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

/**
 *
 * @author vanag
 */
public abstract class genericResource implements Comparable<genericResource> {
    public abstract int getBw();    
    public abstract int getLLRCount();    
    public genericResource(int someindex)
    {
        index=someindex;
    }
    
    int index;
    
    public int getIndex() {
        return index;
    }
    
    public int compareTo(genericResource res)
    {
        return(this.getBw()-res.getBw());
    }
    
    public String toString()
    {
        return "A resource with index "+index+" and bandwidth "+this.getBw();
    }
}
