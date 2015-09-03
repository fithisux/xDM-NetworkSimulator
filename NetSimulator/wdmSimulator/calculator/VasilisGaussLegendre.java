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
package wdmSimulator.calculator;

public final class VasilisGaussLegendre
{

    double x1;
    double x2;
    int rank;
    double[] roots;
    double[] weights;
    
    public VasilisGaussLegendre(double a , double b,int r)
    {
    
      x1=a;
      x2=b;
      rank=r;
      roots = new double[r];
      weights = new double[r];
      
      
      double EPS = 1e-15;
      int m=(rank+1)/2;  //it is enough to compute half of the values by symmetry
      double z1,z,xm,xl,pp,p1,p2,p3;
      
      xm=(x1+x2)*0.5;  // We translate coefficients from (-1,1)
      xl=(x2-x1)*0.5;  // to (x1,x2)
      
      for(int i=0;i<m;i++)
      {
      
          z=Math.cos( Math.PI * (i+0.75)/(rank+0.5)); // Clever guess for the i-th root
          
          do
          {
            p1=1.0;p2=0;
            for(int j=0;j<rank;j++) {  p3=p2 ; p2=p1 ; p1= ((2.0*j+1.0)*z*p2-j*p3)/(j+1); } // Get P_{rank(z)             
            pp = rank * (z * p1 - p2) / (z*z-1.0);   //Get P'_{rank}(z)
            z1=z ; z= z1-p1/pp;                      // the good-old Newton's formula
           }while ( Math.abs(z-z1) > EPS);           
          roots[i] = xm-xl*z;
          roots[rank-i-1] = xm + xl*z;
          weights[i]= 2.0 * xl / ((1.0 - z*z) *pp *pp) ;
          weights[rank-i-1]=weights[i];
        }
       }
       
       public double[] getWeights(){return weights;}
       public double[] getRoots(){return roots;}
        
       public int getRank() { return rank;}       
       public double getLower() { return x1;}
       public double getUpper() { return x2;}
       
       
       public String toString()
       {       	
       	  String s;
       	  
       	  s="A Gauss-Legendre set with rank "+Integer.toString(rank)+" lower bound ";
       	  s+=Double.toString(x1)+" upper bound "+Double.toString(x2)+"\n";
       	  s+="With roots and weights\n";
       	  for(int i=0;i<rank;i++)
       	  {
       	    s+=Integer.toString(i)+" "+Double.toString(roots[i])+" "+Double.toString(weights[i])+"\n";
       	  }
       	  
       	  return s;
       	}
    }   
