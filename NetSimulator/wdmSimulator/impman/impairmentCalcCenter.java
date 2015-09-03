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
import wdmSimulator.calculator.impairmentCalculator;
import genericSimulator.prefs.simulationPrefs;
import java.util.*;
import java.io.*;
/**
 *
 * @author vanag
 */
public  final class impairmentCalcCenter
{
  /** Creates a new instance of fithisAccelerator */
  //
  //The meat
  //
  int numChans;
  int numSpans;
  impairmentCalculator impCalculator;
  boolean validation;
  double [][] xpm;
  double[][][][] fwm;
  double tolerance;
  double[][]order1;
  double[][][] order2;
  double[][][][] order3;
  
  public impairmentCalcCenter(simulationPrefs prefs,int length)
  {
        System.out.println("Initializing Impairment Calculation Center ....");
        this.numChans=prefs.getNetworkPrefs().getLlrcount();
        this.numSpans=length;
        this.tolerance=prefs.getWdmPrefs().getImpairmentPrefs().getTolerance();
        this.impCalculator=new impairmentCalculator(prefs,this.numSpans);
        this.xpm=new double[this.numChans][this.numChans];
        this.fwm=new double[this.numChans][this.numChans][this.numChans][this.numChans];

        for(int n=0;n<this.numChans;n++)
        {
            for(int i=0;i<this.numChans;i++)
            {
                this.xpm[n][i]=this.impCalculator.tanya_getXPM(n, i);
            }
        }

        for(int n=0;n<this.numChans;n++)
        {
            for(int i=0;i<this.numChans;i++)
            {
                for(int j=0;j<this.numChans;j++)
                {
                    for(int k=0;k<this.numChans;k++)
                    {
                        this.fwm[n][i][j][k]=this.impCalculator.tanya_getFWM(n,i,j,k);
                    }
                }
            }
        }

        this.order1=new double[this.numChans][this.numChans];
        this.order2=new double[this.numChans][this.numChans][this.numChans];
        this.order3=new double[this.numChans][this.numChans][this.numChans][this.numChans];
  }

  public  void compressTerms()
  {
        //compress order 1
        for(int n=0;n<this.numChans;n++)
        {
            for(int i=0;i<this.numChans;i++)
            {
                this.order1[n][i]+=this.xpm[n][i];
                this.order1[n][i]+=this.fwm[n][i][i][i];
            }
        }
        //compress order2
        for(int n=0;n<this.numChans;n++)
        {
            for(int i=0;i<this.numChans;i++)
            {
                for(int j=0;j<this.numChans;j++)
                {
                    if(i<j)
                    {
                        this.order2[n][i][j]+=this.fwm[n][i][i][j];
                        this.order2[n][i][j]+=this.fwm[n][i][j][i];
                        this.order2[n][i][j]+=this.fwm[n][j][i][i];
                        this.order2[n][i][j]+=this.fwm[n][j][j][i];
                        this.order2[n][i][j]+=this.fwm[n][j][i][j];
                        this.order2[n][i][j]+=this.fwm[n][i][j][j];
                    }
                }
            }
        }
        //compress order 3
        for(int n=0;n<this.numChans;n++)
        {
            for(int i=0;i<this.numChans;i++)
            {
                for(int j=0;j<this.numChans;j++)
                {
                    for(int k=0;k<this.numChans;k++)
                    {
                        if( (i< j) && (j<k) )
                        {
                            this.order3[n][i][j][k]+=this.fwm[n][i][j][k];
                            this.order3[n][i][j][k]+=this.fwm[n][i][k][j];
                            this.order3[n][i][j][k]+=this.fwm[n][j][i][k];
                            this.order3[n][i][j][k]+=this.fwm[n][j][k][i];
                            this.order3[n][i][j][k]+=this.fwm[n][k][i][j];
                            this.order3[n][i][j][k]+=this.fwm[n][k][j][i];
                        }
                    }
                }
            }
        }
  }

  public  void filterTerms()
  {
    double maxorder1=0;
    double maxorder2=0;
    double maxorder3=0;
    double temp;
    
    //compress order 3
    for(int n=0;n<this.numChans;n++)
    {
        for(int i=0;i<this.numChans;i++)
        {
            for(int j=0;j<this.numChans;j++)
            {
                for(int k=0;k<this.numChans;k++)
                {
                    if( (i< j) && (j<k) )
                    {
                        if(this.order3[n][i][j][k] > maxorder3) maxorder3=this.order3[n][i][j][k];                        
                    }
                }
            }
        }
    }
    maxorder3*=(1-this.tolerance);
    for(int n=0;n<this.numChans;n++)
    {
        for(int i=0;i<this.numChans;i++)
        {
            for(int j=0;j<this.numChans;j++)
            {
                for(int k=0;k<this.numChans;k++)
                {
                    if( (i< j) && (j<k) )
                    {
                        if(this.order3[n][i][j][k] <= maxorder3)
                        {
                            temp=this.order3[n][i][j][k];
                            this.order3[n][i][j][k]=0;
                            this.order2[n][i][j]+=temp/3;
                            this.order2[n][i][k]+=temp/3;
                            this.order2[n][j][k]+=temp/3;
                        }                        
                    }
                }
            }
        }
    }
    
    
    //compress order2    
    for(int n=0;n<this.numChans;n++)
    {
        for(int i=0;i<this.numChans;i++)
        {
            for(int j=0;j<this.numChans;j++)
            {
                if(i<j)
                {
                    if(this.order2[n][i][j] > maxorder2) maxorder2=this.order2[n][i][j];                
                }
            }
        }
    }    
    maxorder2*=(1-this.tolerance);    
    for(int n=0;n<this.numChans;n++)
    {
        for(int i=0;i<this.numChans;i++)
        {
            for(int j=0;j<this.numChans;j++)
            {
                if(i<j)
                {
                    if(this.order2[n][i][j] <= maxorder2)
                    {
                        temp=this.order2[n][i][j];
                        this.order2[n][i][j]=0;
                        this.order1[n][i]+=temp/2;
                        this.order1[n][j]+=temp/2;
                    }
                }
            }
        }
    }
    
    //compress order1
    for(int n=0;n<this.numChans;n++)
    {
        for(int i=0;i<this.numChans;i++)
        {
            if(this.order1[n][i] > maxorder1) maxorder1=this.order1[n][i];
        }
    }
    maxorder1*=(1-this.tolerance);
    for(int n=0;n<this.numChans;n++)
    {
        for(int i=0;i<this.numChans;i++)
        {
            if(this.order1[n][i] <= maxorder1) this.order1[n][i]=0;
        }
    }
    
    
  }
  
  private  String printOrder1(int n,int i)
  {
        double nois=this.order1[n][i];
        long writer= Math.round(nois);
        String s=null;
        if(writer!=0)
        {
            s= Long.toString(writer) + "\n";
            s+= Integer.toString(i)+"\n";
            s+= Integer.toString(n)+"\n";
        }
        return s;
  }

  private  String printOrder2(int n,int i,int j)
  {
        double nois=this.order2[n][i][j];
        long writer= Math.round(nois);
        String s=null;
        if(writer!=0)
        {
            s= Long.toString(writer) + "\n";
            s+= Integer.toString(i)+"\n";
            s+= Integer.toString(j)+"\n";
            s+= Integer.toString(n)+"\n";
        }
        return s;
  }

  private  String printOrder3(int n,int i,int j,int k)
  {
        double nois=this.order3[n][i][j][k];
        long writer= Math.round(nois);
        String s=null;
        if(writer!=0)
        {
            s= Long.toString(writer) + "\n";
            s+= Integer.toString(i)+"\n";
            s+= Integer.toString(j)+"\n";
            s+= Integer.toString(k)+"\n";
            s+= Integer.toString(n)+"\n";
        }
        return s;
  }

  
  public  void fillWithNoise(String fname) throws IOException
  {
        System.out.println("Launching calculation center");        
        File f =new File(fname);
        BufferedWriter beef = new BufferedWriter(new FileWriter(f));
        beef.write("Data;"+this.numSpans+";"+this.numChans+"\n");
        System.out.println("Calculating orders 1,2,3 for : ");
        System.out.println("Data;"+this.numSpans+";"+this.numChans);

        String s;

        System.out.println("Order1");
        beef.write("Order1\n");
        for(int n=0;n<numChans;n++)
        {
            for(int i=0;i<numChans;i++)
            {
                s=this.printOrder1(n,i);
                if(s!=null) beef.write(s);
            }
        }
        beef.write("END\n");

        System.out.println("Order2");
        beef.write("Order2\n");
        for(int n=0;n<numChans;n++)
        {
            for(int i=0;i<numChans;i++)
            {
                for(int j=0;j<numChans;j++)
                {
                    s=this.printOrder2(n,i,j);
                    if(s!=null) beef.write(s);
                }
            }
        }
        beef.write("END\n");

        System.out.println("Order3");
        beef.write("Order3\n");
        for(int n=0;n<numChans;n++)
        {
            for(int i=0;i<numChans;i++)
            {
                for(int j=0;j<numChans;j++)
                {
                    for(int k=0;k<numChans;k++)
                    {
                        s=this.printOrder3(n,i,j,k);
                        if(s!=null) beef.write(s);
                    }
                }
            }
        }
        beef.write("END\n");
        beef.flush();
        beef.close();
        System.out.println("All lambdas calculated");
  }

}