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
import java.util.*;
/**
 *
 * @author vanag
 */
public class testUtorus 
{
    public static void main(String[] args)
    {
        int edge=5;
        int a,b;
        int c,d;
        Random r=new Random();
        for(int i=0;i<edge;i++)
        {
            for(int j=0;j<edge;j++)
            {
                a=j;
                b=(a+1) % edge;
                a+=(edge*i);
                b+=(edge*i);
                c=1+r.nextInt(2);
                d=5*(1+r.nextInt(3));
                String s="<genericOrientation bidirectional=\"true\" from=\""+a+
                        "\" to=\""+b+"\"/>";
                System.out.println("<linkPrefs>");
                System.out.println("<listOfGenericOrientations>");
                System.out.println(s);
                System.out.println("</listOfGenericOrientations>");
                System.out.println("<linkCharacteristics copies=\""+c+"\" distance=\""+d+"\"  monitor=\"false\"/>	");
                System.out.println("</linkPrefs>");
            }
        }
        
        for(int i=0;i<edge;i++)
        {
            for(int j=0;j<edge;j++)
            {
                a=j;
                b=(a+1) % edge;
                a=i+edge*a;
                b=i+edge*b;
                c=1+r.nextInt(2);
                d=1+r.nextInt(10);
                String s="<genericOrientation bidirectional=\"true\" from=\""+a+
                        "\" to=\""+b+"\"/>";
                System.out.println("<linkPrefs>");
                System.out.println("<listOfGenericOrientations>");
                System.out.println(s);
                System.out.println("</listOfGenericOrientations>");
                System.out.println("<linkCharacteristics copies=\""+c+"\" distance=\""+d+"\"  monitor=\"false\"/>	");
                System.out.println("</linkPrefs>");
            }
        }
    }
    
}
