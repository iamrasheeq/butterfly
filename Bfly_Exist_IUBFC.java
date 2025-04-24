import java.io.*;
import java.util.*;

public class Bfly_Exist_IUBFC {

    public void existSoftl_prob(Bfly_UBFC.Node n0) {
        Collections.sort(n0.nb, Comparator.comparingDouble((MyPair<Integer, Double> pd) -> pd.two).reversed());
    }

    public void existCheck() {
        for (Map.Entry<Integer, Bfly_UBFC.Node> x : Bfly_UBFC.nodeMap.entrySet()) {
            existSoftl_prob(x.getValue());
        }
    }

    public int butterfly_counting_improved_EP() {
        int countId = 0;
        double thresholdMod = Bfly_UBFC.t / (Bfly_UBFC.ProbMax * Bfly_UBFC.ProbMax);

        for (int u1 : Bfly_UBFC.id_VerN) {
            Map<Integer, List<Double>> wedgeMap = new HashMap<>();
            Bfly_UBFC.Node nodeU = Bfly_UBFC.nodeMap.get(u1);

            for (MyPair<Integer, Double> v : nodeU.nb) {
                int v0 = v.one;
                double probUV = v.two;
                Bfly_UBFC.Node nodeV = Bfly_UBFC.nodeMap.get(v0);

                if (probUV >= thresholdMod) {
                    for (MyPair<Integer, Double> w1 : nodeV.nb) {
                        int w0 = w1.one;

                        if (u1 == w0) {
                            continue;
                        }

                        double vwProb = w1.two;
                        double probT = probUV * vwProb;
                        Bfly_UBFC.Node nodeW = Bfly_UBFC.nodeMap.get(w0);

                        if (probT >= thresholdMod &&
                            Bfly_UBFC.ver_pri_comp(nodeU, nodeV) &&
                            Bfly_UBFC.ver_pri_comp(nodeU, nodeW)) {

                            Bfly_IUBFC.prob_wedgemap(wedgeMap, w0, probT);
                        } else {
                            break;
                        }
                    }
                } else {
                    break;
                }
            }

            countId += Bfly_IUBFC.wedgeCount(wedgeMap);
        }

        return countId;
    }

    public static void main(String[] args) {
        double[] val_T = {0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80};

        for (double t : val_T) {
            long time_b = System.currentTimeMillis();

            Bfly_Exist_IUBFC ep = new Bfly_Exist_IUBFC();
            Bfly_UBFC.t = t;
            Bfly_UBFC.node_id_to_obj("/Users/rasheeqishmam/FSU/bFly_RI24C/dataset/IMDBID.txt");
            Bfly_IUBFC.remove_Edge("/Users/rasheeqishmam/FSU/bFly_RI24C/dataset/IMDBEdge.txt");

            ep.existCheck();
            int r1 = ep.butterfly_counting_improved_EP();

            long time_e = System.currentTimeMillis();
            long runTime = time_e - time_b;

            System.out.println("Threshold t: " + t + ", Uncertain Butterflies: " + r1 + ", Runtime: " + runTime + " milisecons");
        }
    }
}
