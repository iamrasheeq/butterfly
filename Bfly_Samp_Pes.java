import java.io.*;
import java.util.*;

public class Bfly_Samp_Pes {

    public int coefCalcu(int n0, int r1) {
        if (r1 < 0 || r1 > n0) {
            return 0;
        }

        int res = 1;
        for (int i = 1; i <= r1; i++) {
            res = res * (n0 - i + 1);
            res = res / i;
        }

        return res;
    }

    public int bfsDetermin(Map<Integer, Integer> m1) {
        int countD = 0;

        for (int value : m1.values()) {
            countD += coefCalcu(value, 2);
        }

        return countD;
    }

    public int samplingDB_Ver(int n0) {
        Bfly_UBFC.Node nodeTarget = Bfly_UBFC.nodeMap.get(n0);
        Map<Integer, Integer> wedgeMap = new HashMap<>();

        if (nodeTarget == null) {
            return 0;
        }

        for (MyPair<Integer, Double> entry : nodeTarget.nb) {
            int id_Nei = entry.one;
            Bfly_UBFC.Node node_Nei = Bfly_UBFC.nodeMap.get(id_Nei);

            if (node_Nei != null) {
                for (MyPair<Integer, Double> entryW : node_Nei.nb) {
                    int idW = entryW.one;

                    if (idW != n0) {
                        wedgeMap.merge(idW, 1, Integer::sum);
                    } else {
                        continue;
                    }
                }
            }
        }

        return bfsDetermin(wedgeMap);
    }

    public int genRandSam(int range) {
        Random objRand = new Random();
        return objRand.nextInt(range);
    }

    public double proportion_estimate_sampling(int sampleNum, double al1) {
        Map<Integer, Boolean> samNode = new HashMap<>();
        double estCount = 0.0;

        for (int i = 0; i < sampleNum; i++) {
            int nodeID = Bfly_Samp_Vertex.sample_gen_checkRan(Bfly_UBFC.id_VerN.size(), samNode);
            double res1 = samplingDB_Ver(nodeID);
            double resExtraPol = (res1 * (double) Bfly_UBFC.id_VerN.size()) / 4.0;
            estCount = (resExtraPol + (i * estCount)) / (i + 1);
        }

        return al1 * estCount;
    }

    public static void main(String[] args) {
        double[] val_T = {0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80};

        for (double t : val_T) {
            long time_b = System.currentTimeMillis();

            Bfly_Samp_Pes ins1 = new Bfly_Samp_Pes();
            Bfly_Samp_Vertex ins2 = new Bfly_Samp_Vertex();
            Bfly_UBFC ins3 = new Bfly_UBFC();

            Bfly_UBFC.t = t;
            Bfly_UBFC.node_id_to_obj("/Users/rasheeqishmam/Desktop/bFly_RI24C/dataset/IMDBID.txt");
            Bfly_UBFC.findAddNb("/Users/rasheeqishmam/Desktop/bFly_RI24C/dataset/IMDBEdge.txt");

            double r1 = ins1.proportion_estimate_sampling(100, 0.0001);

            long time_e = System.currentTimeMillis();
            long runTime = time_e - time_b;

            System.out.println("Threshold t: " + t + ", Uncertain Butterflies: " + r1 + ", Runtime: " + runTime + " milisecons");
        }
    }
}
