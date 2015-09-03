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
import genericSimulator.prefs.networkPrefs;
import genericSimulator.prefs.simulationPrefs;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.complex.Complex;
import genericSimulator.prefs.*;

/**
 *
 * @author  tanyatanya
 */
public final class impairmentCalculator {

// 
//
//  Noise Computation Library
//
//
    public static final double sq2 = Math.sqrt(2);
    public static final double cb = 1.6e-19;
    public static final double h = 6.63e-34;
    public static final double Aeff1 = 80.0e-12;
    public static final double Aeff2 = 20.0e-12;
    public static final double fc = 1931e11;
    public static final double c = 299792458;
    public static final double a1 = 0.00023 * Math.log(10) / 10;
    public static final double a2 = 0.0005 * Math.log(10) / 10;
    public static final double n21 = 26e-21;
    public static final double n22 = 26e-21;
    public static final double Disp10 = 17e-6;
    public static final double slope1 = 58.0;
    public static final double Disp20 = -85e-6;
    public static final double slope2 = -320.0;
    public static final double lambdac = c / fc;
    public static final double dfreq = 5e10;  //in HZ
    public double P;
    public static final double L1 = 40000.0; //in meters
    public static final double L2 = 8000.0; // in meteres
    public static final double no = 1.43;
    public static final double thermal = 81e-24;
    public static final double x111 = n21 * 10 * (c / 480.0) * Math.pow(no / Math.PI, 2);
    public static final double Be = 0.7e10;
    public static final double Bo = 4e10;
    public static final double GEDFA = Math.pow(10, 1.32);
    public static final double NFEDFA = Math.pow(10, 0.6);
    public static final double Resp = 1.0;
    public static final double SDFamp = 0.5 * NFEDFA * h * fc;
    public static final double BitRate = 1e10;
    public Complex tanya_one = new Complex(1, 0);
    public static final double square_const=Math.pow(Resp, 2) * Math.pow(SDFamp * (GEDFA - 1), 2) * (2 * Bo - Be) * Be;
    public static final double linear_const=(4 * Math.pow(Resp, 2) * (GEDFA - 1) * Be * SDFamp);
    public static final double sigma_const_0=(thermal * Be);
    public static final double sigma_const_1=( 2 * cb * Resp * Be);
    
    public double eff_linear_const;
    public double eff_sigma_const_0;
    public double eff_sigma_const_1;
    public double eff_square_const;
    public double eff_P;
    public double scaler;
    
    private VasilisGaussLegendre myGauss;
    int numspans;
    int numchans;
    public double[] freqs;
    public double[] lams;
    public double[][][] dik;
    public double[][] preBeta;
    public double[][] gamma;
    public double[][][][] deltaB;
    wdmPrefs wdmprefs;
    networkPrefs topprefs;
    //General functions

    public impairmentCalculator(simulationPrefs prefs,int numOfSpans) {
        
        this.wdmprefs=prefs.getWdmPrefs();
        this.P=this.wdmprefs.getImpairmentPrefs().getPinput();
        
        this.scaler=this.P*this.linear_const;
        this.eff_linear_const=(this.P*this.linear_const) / this.scaler;
        this.eff_sigma_const_0=this.sigma_const_0 / this.scaler;
        this.eff_sigma_const_1=(this.P*this.sigma_const_1) /this.scaler;
        this.eff_square_const=this.square_const / this.scaler;        
        this.eff_P=this.P/Math.sqrt(this.scaler);
        System.out.println("The linear === "+this.eff_linear_const);
        myGauss = new VasilisGaussLegendre(0.01, 0.7 * BitRate, 100);
        this.initializeLambdas(numOfSpans, prefs.getNetworkPrefs().getLlrcount());
        this.initializeForEfficiency();
    }

    final public void initializeLambdas(int numOfSpans, int numOfChans) {
        // spans
        numspans = numOfSpans;
        //number of channels
        numchans = numOfChans;

        this.freqs = new double[this.numchans];
        this.lams = new double[this.numchans];
        int centering = this.numchans / 2;
        if (this.numchans % 2 == 0) {
            this.freqs[centering] = dfreq * 0.5;
            this.freqs[centering - 1] = -dfreq * 0.5;
            for (int i = centering + 1; i < this.numchans; i++) {
                this.freqs[i] = dfreq + this.freqs[i - 1];
            }
            for (int i = centering - 2; i >= 0; i--) {
                this.freqs[i] = -dfreq + this.freqs[i + 1];
            }
        }
        else {
            this.freqs[centering] = 0;
            for (int i = centering + 1; i < this.numchans; i++) {
                this.freqs[i] = dfreq + this.freqs[i - 1];
            }
            for (int i = centering - 1; i >= 0; i--) {
                this.freqs[i] = -dfreq + this.freqs[i + 1];
            }
        }
        for (int i = 0; i < this.numchans; i++) {
            freqs[i]+=fc;
            lams[i] = c / freqs[i];
        }
    }
    
    public final void initializeForEfficiency() {
        tanya_one = new Complex(1, 0);
        dik = new double[this.numchans][this.numchans][2];
        preBeta = new double[this.numchans][2];
        gamma = new double[this.numchans][2];
        deltaB = new double[this.numchans][this.numchans][this.numchans][2];

        double[][] theD = new double[this.numchans][2];

        double temp1, temp2;

        for (int i = 0; i < this.numchans; i++) {
            theD[i][0] = (slope1 * (lams[i] - lambdac) + Disp10) * L1;
            theD[i][1] = (slope2 * (lams[i] - lambdac) + Disp20) * L2;
        }

        for (int i = 0; i < this.numchans; i++) {
            preBeta[i][0] = (lams[i] * lams[i] * theD[i][0]) / (4 * Math.PI * c);
            preBeta[i][1] = (lams[i] * lams[i] * theD[i][1]) / (4 * Math.PI * c);
            gamma[i][0] = (2 * Math.PI * n21 * L1) / (lams[i] * Aeff1);
            gamma[i][1] = (2 * Math.PI * n22 * L2) / (lams[i] * Aeff2);
            for (int j = 0; j < this.numchans; j++) {
                dik[i][j][0] = 0.5 * (lams[i] - lams[j]) * (theD[i][0] + theD[j][0]);
                dik[i][j][1] = 0.5 * (lams[i] - lams[j]) * (theD[i][1] + theD[j][1]);
                for (int k = 0; k < this.numchans; k++) {
                    temp1 = (freqs[i] - freqs[k]) * (freqs[j] - freqs[k]) * 2 * c * Math.PI / (freqs[k] * freqs[k]);
                    temp2 = 0.5 * (freqs[i] + freqs[j] - 2 * freqs[k]) * c / (freqs[k] * freqs[k]);

                    deltaB[i][j][k][0] = temp1 * (theD[k][0] - temp2 * slope1 * L1);
                    deltaB[i][j][k][1] = temp1 * (theD[k][1] - temp2 * slope2 * L2);
                }
            }
        }
    }

    public double tanya_getXPM(int n, int i) {
        if(!this.wdmprefs.getImpairmentPrefs().isEnablexpm()) return 0;
        if (n != i) {
            return this.tanya_varXPM(i, n) / this.scaler;
        }
        else {
            return 0;
        }
    }

    public double tanya_getFWM(int n, int i, int j, int k) {
        if(!this.wdmprefs.getImpairmentPrefs().isEnablefwm()) return 0;
        if ((i == k) || (j == k) || (i + j - k != n) || (j < i)) {
            return 0;
        }
        else {
            return this.tanya_varFWM(n, i, j, k) / this.scaler;
        }
    }

    final public double tanya_getQ(double Pinter, int spans) {
        // Pinter is typically the result of  varFWM or noiseXPM
        //komple
        double sigma0 = this.eff_square_const*spans * spans + this.eff_sigma_const_0;
        double sigma1 = sigma0;
        sigma0 = Math.sqrt(sigma0);
        sigma1 += this.eff_sigma_const_1 + Pinter;
        //sigma1 += spans * this.eff_linear_const;
        sigma1 = Math.sqrt(sigma1);        
        double totalQ = impairmentCalculator.Resp * this.eff_P / (sigma0 + sigma1);
        return totalQ;
    }

    final public double tanya_getBER(double Pinter, int spans) {
        double Q = this.tanya_getQ(Pinter, spans);

        double res = 1.0;
        try {
            res = (0.5 * (1.0 - Erf.erf(Q / sq2)));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return res;
    }

    public Complex waveSum(Complex Z0, Complex Z1, double E0, double E1) {
        Complex wave1 = (new Complex(0, numspans * (E0 + E1))).exp();
        Complex wave2 = (new Complex(0, E0 + E1)).exp();
        Complex mask = (new Complex(0, E0)).exp().multiply(Z1).add(Z0);
        Complex frac;
        if (Math.abs(1 - Math.cos(E0 + E1)) < 1e-6) {
            frac = (new Complex(0, (numspans - 1) * 0.5 * (E0 + E1))).exp().multiply(numspans);
        }
        else {
            frac = tanya_one.add(wave1.negate()).divide(tanya_one.add(wave2.negate()));
        }
        return mask.multiply(frac);
    }

    public Complex zeta(double b, double a, double gam, double Gain) {
        Complex operand = new Complex(-a, b);
        Complex frac = operand.exp().add(tanya_one.negate()).divide(operand);

        return frac.multiply(new Complex(0, 0.5 * gam * Gain));
    }

    public Complex amplitude(double omega, int s, int t) {
        double fac = omega * omega;
        double beta_s_0 = fac * preBeta[s][0];
        double beta_t_0 = fac * preBeta[t][0];
        double beta_s_1 = fac * preBeta[s][1];
        double beta_t_1 = fac * preBeta[t][1];
        double mydik_0 = omega * dik[s][t][0];
        double mydik_1 = omega * dik[s][t][1];
        double alpha_0 = a1 * L1;
        double alpha_1 = a2 * L2;

        Complex z0, z1;
        double comb_beta_0, comb_beta_1;
        //First summand up
        comb_beta_0 = mydik_0 - (beta_s_0 + beta_t_0);
        comb_beta_1 = mydik_1 - (beta_s_1 + beta_t_1);
        z0 = this.zeta(comb_beta_0, alpha_0, this.gamma[s][0], 1);
        z1 = this.zeta(comb_beta_1, alpha_1, this.gamma[s][1], Math.exp(-alpha_0));
        Complex sum11 = this.waveSum(z0, z1, comb_beta_0, comb_beta_1);

        //Second summand down
        comb_beta_0 = mydik_0 + beta_s_0 + beta_t_0;
        comb_beta_1 = mydik_1 + beta_s_1 + beta_t_1;
        z0 = this.zeta(comb_beta_0, alpha_0, this.gamma[s][0], 1);
        z1 = this.zeta(comb_beta_1, alpha_1, this.gamma[s][1], Math.exp(-alpha_0));
        Complex sum12 = this.waveSum(z0, z1, comb_beta_0, comb_beta_1);

        //Curly first summand up
        comb_beta_0 = mydik_0 - (beta_s_0 - beta_t_0);
        comb_beta_1 = mydik_1 - (beta_s_1 - beta_t_1);
        z0 = this.zeta(comb_beta_0, alpha_0, this.gamma[s][0], 1);
        z1 = this.zeta(comb_beta_1, alpha_1, this.gamma[s][1], Math.exp(-alpha_0));
        Complex sum21 = this.waveSum(z0, z1, comb_beta_0, comb_beta_1);

        //Curly second summand down
        comb_beta_0 = mydik_0 + beta_s_0 - beta_t_0;
        comb_beta_1 = mydik_1 + beta_s_1 - beta_t_1;
        z0 = this.zeta(comb_beta_0, alpha_0, this.gamma[s][0], 1);
        z1 = this.zeta(comb_beta_1, alpha_1, this.gamma[s][1], Math.exp(-alpha_0));
        Complex sum22 = this.waveSum(z0, z1, comb_beta_0, comb_beta_1);

        Complex phaser = (new Complex(0, this.numspans * (beta_s_0 + beta_s_1))).exp();

        Complex sum1 = sum11.add(sum21).multiply(phaser);

        phaser = phaser.conjugate();

        Complex sum2 = sum12.add(sum22).multiply(phaser);

        return sum2.add(sum1.negate());
    }

    final public double filteredRAW(int i, int k, double omega) {
        double fac1, fac2, normz, tanya2;

        fac1 = 1 / (4.0 * BitRate);
        normz = omega / (2.0 * BitRate);

        if (Math.abs(normz) < 1e-6) {
            fac2 = 1.0;
        }
        else {
            fac2 = Math.sin(normz) / normz;
        }
        tanya2 = 1.0;
        double powXPM = amplitude(omega, i, k).multiply(2 * P).abs();
        return 2 * fac1 * Math.pow(P * fac2 * powXPM, 2) * tanya2;
    }

    protected double tanya_varXPM(int i, int k) {
        double temp = 0;
        double xi, wi;
        double[] weights = null;
        double[] roots = null;

        if (i != k) {
            weights = myGauss.getWeights();
            roots = myGauss.getRoots();

            for (int di = 0; di < myGauss.getRank(); di++) {
                wi = weights[di];
                xi = roots[di];
                temp += wi * this.filteredRAW(i, k, 2 * Math.PI * xi);
            }
        }
        return temp;
    }

    //
    //  FWM noise
    //
    protected double tanya_varFWM(int n, int i, int j, int k) {
        double correction, temp, temp1, temp2;
        double dgen = 3;
        if (i == j) {
            correction = 0.25;
        }
        else {
            dgen = 6;
            correction = ((n == k) ? 0.25 : 0.125);
        }

        Complex c1 = new Complex(-a1 * L1, deltaB[i][j][k][0]);
        Complex c2 = new Complex(-a2 * L2, deltaB[i][j][k][1]);
        Complex c11 = tanya_one.add(c1.exp().negate()).divide(c1).multiply(-L1);
        Complex c22 = tanya_one.add(c2.exp().negate()).divide(c2).multiply(-L2);

        double factorFWM = c11.add(c22.multiply(c1.exp())).abs();

        temp = 0.5 * (deltaB[i][j][k][0] + deltaB[i][j][k][1]);
        temp1 = Math.sin(this.numspans * temp);
        temp2 = Math.sin(temp);

        double ekfrasi1 = 1024 * Math.pow(Math.PI, 6) * P * Math.pow(P * x111 * dgen * freqs[k], 2);
        double ekfrasi2 = ekfrasi1 / (Math.pow(c * no, 4) * Math.pow(Aeff1, 2));

        double powFWM;

        if (Math.abs(temp2) < 1e-6) {
            powFWM = ekfrasi2 * Math.pow(this.numspans * factorFWM, 2);
        }
        else {
            powFWM = ekfrasi2 * Math.pow(temp1 * factorFWM / temp2, 2);
        }

        return 2 * P * correction * powFWM;
    }
}
