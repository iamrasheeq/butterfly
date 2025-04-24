import java.io.*;
import java.util.*;

public class Bfly_Samp_Vertex {

    public int sam_Ver_ub(int node) {
        double probV, probW, edge_prob2;
        Bfly_UBFC.Node u1 = Bfly_UBFC.nodeMap.get(node);

        if (u1 == null) {
            return -1;
        }

        Map<Integer, List<Double>> wedgeMap = new HashMap<>();

        for (MyPair<Integer, Double> v : u1.nb) {
            if (v == null) {
                return -1;
            }

            int v0 = v.one;
            Bfly_UBFC.Node nodeV = Bfly_UBFC.nodeMap.get(v0);
            probV = v.two;

            if (probV < Bfly_UBFC.t) {
                continue;
            }

            for (MyPair<Integer, Double> w1 : nodeV.nb) {
                int w0 = w1.one;
                Bfly_UBFC.Node nodeW = Bfly_UBFC.nodeMap.get(w0);
                probW = w1.two;
                edge_prob2 = probV * probW;

                if (edge_prob2 >= Bfly_UBFC.t) {
                    List<Double> listWedge = wedgeMap.getOrDefault(w0, new ArrayList<>());
                    listWedge.add(edge_prob2);
                    wedgeMap.put(w0, listWedge);
                }
            }
        }

        return Bfly_IUBFC.wedgeCount(wedgeMap);
    }

    public static int sample_gen_checkRan(int range, Map<Integer, Boolean> temp_value) {
        Random objRand = new Random();
        int val_rand = objRand.nextInt(range);

        if (!temp_value.containsKey(val_rand)) {
            temp_value.put(val_rand, true);
            return val_rand;
        } else {
            return sample_gen_checkRan(range, temp_value);
        }
    }

    public double bfly_sampling_Ver_uncertain(int sampleNum) {
        Map<Integer, Boolean> samNode = new HashMap<>();
        double estCount = 0.0;

        for (int i = 0; i < sampleNum; i++) {
            int nodeId = sample_gen_checkRan(Bfly_UBFC.id_VerN.size(), samNode);
            double res1 = sam_Ver_ub(nodeId);

            if (res1 == -1.0) {
                continue;
            }

            double vertexCount = (double) Bfly_UBFC.id_VerN.size();
            double weightedEst = (res1 * vertexCount) / 4.0;
            estCount = (weightedEst + (estCount * i)) / (i + 1);
        }

        return estCount;
    }

    public static void main(String[] args) {
        double[] val_T = {0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80};

        for (double t : val_T) {
            long time_b = System.currentTimeMillis();

            Bfly_IUBFC ins = new Bfly_IUBFC();
            Bfly_Samp_Vertex samp = new Bfly_Samp_Vertex();
            Bfly_UBFC ins2 = new Bfly_UBFC();

            Bfly_UBFC.t = t;
            Bfly_UBFC.node_id_to_obj("/Users/rasheeqishmam/Desktop/bFly_RI24C/dataset/IMDBID.txt");
            Bfly_UBFC.findAddNb("/Users/rasheeqishmam/Desktop/bFly_RI24C/dataset/IMDBEdge.txt");

            double r1 = samp.bfly_sampling_Ver_uncertain(100);
            long time_e = System.currentTimeMillis();
            long runTime = time_e - time_b;

            System.out.println("Threshold t: " + t + ", Uncertain Butterflies: " + r1 + ", Runtime: " + runTime + " milisecons");
        }
    }
}
