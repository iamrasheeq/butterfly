import java.io.*;
import java.util.*;

public class Bfly_IUBFC {

    public static void remove_Edge(String file_name) {
        try (BufferedReader name_S1 = new BufferedReader(new FileReader(file_name))) {
            String s1;
            int edgeCount = 0, totalEdges = 0;

            while ((s1 = name_S1.readLine()) != null) {
                totalEdges++;

                if (s1.isEmpty()) continue;

                String[] t1 = s1.split("\t");

                int x11 = Integer.parseInt(t1[0]);
                int x22 = Integer.parseInt(t1[1]);
                double pd = Double.parseDouble(t1[2]);

                if (pd < Bfly_UBFC.t) continue;

                Bfly_UBFC.Node node1 = Bfly_UBFC.nodeMap.computeIfAbsent(x11, q -> new Bfly_UBFC.Node());
                Bfly_UBFC.Node node2 = Bfly_UBFC.nodeMap.computeIfAbsent(x22, q -> new Bfly_UBFC.Node());

                node1 = Bfly_UBFC.nodeMap.get(x11);
                node2 = Bfly_UBFC.nodeMap.get(x22);

                node1.nb.add(new MyPair<>(x22, pd));
                node2.nb.add(new MyPair<>(x11, pd));

                Bfly_UBFC.myEdge e0 = new Bfly_UBFC.myEdge();
                e0.node1 = x11;
                e0.node2 = x22;
                e0.p1 = pd;

                Bfly_UBFC.edgeList.add(e0);

                if (pd > Bfly_UBFC.ProbMax) {
                    Bfly_UBFC.ProbMax = pd;
                }
                edgeCount++;
            }

            System.out.println("Edges= " + totalEdges);
            System.out.println("Edge Satisfying Threshold= " + edgeCount);

        } catch (IOException e0) {
            System.out.println("No Edge file exists");
            System.exit(3);
        }
    }

    public static int myBinSearch(List<Double> wedg, int set_l, int set_r, double tar) {
        if (set_l > set_r) return -1;

        int set_mid = (set_l + set_r) / 2;

        if (wedg.get(set_mid) >= tar) {
            if (set_mid == 0 || wedg.get(set_mid - 1) < tar) {
                return set_mid;
            } else {
                return myBinSearch(wedg, set_l, set_mid - 1, tar);
            }
        } else {
            return myBinSearch(wedg, set_mid + 1, set_r, tar);
        }
    }

    public static int improvedListCount(List<Double> listWedge) {
        int size = listWedge.size();

        if (size <= 1) return 0;

        Collections.sort(listWedge, (a, b) -> Double.compare(b, a));

        int c1 = 0;
        int ind1 = 0, ind2 = 1;

        while (ind2 < size) {
            if (listWedge.get(ind1) * listWedge.get(ind2) < Bfly_UBFC.t) {
                double thresholdMin = Bfly_UBFC.t / listWedge.get(ind2);
                int set_l = 0, set_r = ind1 - 1;
                int temp_res = myBinSearch(listWedge, set_l, set_r, thresholdMin);

                if (ind1 == -1) return c1;

                ind1 = temp_res;
            }

            c1 = c1 + ind1 + 1;
            ind1++;
            ind2++;
        }

        return c1;
    }

    public static void prob_wedgemap(Map<Integer, List<Double>> wedgeMap, int nodeTarget, double p1) {
        wedgeMap.computeIfAbsent(nodeTarget, x -> new ArrayList<>()).add(p1);
    }

    public static int wedgeCount(Map<Integer, List<Double>> wedgeMap) {
        int c1 = 0;

        for (Map.Entry<Integer, List<Double>> entry : wedgeMap.entrySet()) {
            c1 += improvedListCount(entry.getValue());
        }

        return c1;
    }

    public int count_Bfly_Im_VP() {
        int countId = 0;
        double thresholdMod = Bfly_UBFC.t / (Bfly_UBFC.ProbMax * Bfly_UBFC.ProbMax);

        for (int ux : Bfly_UBFC.id_VerN) {
            Map<Integer, List<Double>> wedgeMap = new HashMap<>();
            Bfly_UBFC.Node nodeU = Bfly_UBFC.nodeMap.get(ux);

            for (MyPair<Integer, Double> v : nodeU.nb) {
                int v0 = v.one;
                Bfly_UBFC.Node nodeV = Bfly_UBFC.nodeMap.get(v0);

                if (Bfly_UBFC.ver_pri_comp(nodeU, nodeV)) {
                    double probUV = v.two;

                    for (MyPair<Integer, Double> w1 : nodeV.nb) {
                        int w0 = w1.one;

                        if (ux == w0) continue;

                        Bfly_UBFC.Node nodeW = Bfly_UBFC.nodeMap.get(w0);

                        if (Bfly_UBFC.ver_pri_comp(nodeU, nodeW)) {
                            double vw_prob = w1.two;
                            double probT = probUV * vw_prob;

                            if (probT >= thresholdMod) {
                                prob_wedgemap(wedgeMap, w0, probT);
                            } else {
                                break;
                            }
                        }
                    }
                } else {
                    break;
                }
            }

            countId += wedgeCount(wedgeMap);
        }

        return countId;
    }

    public static void main(String[] args) {
        double[] val_T = {0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80};

        for (double t : val_T) {
            long time_b = System.currentTimeMillis();
            Bfly_UBFC.t = t;

            Bfly_UBFC ins = new Bfly_UBFC();
            Bfly_IUBFC gp = new Bfly_IUBFC();

            Bfly_UBFC.node_id_to_obj("/Users/rasheeqishmam/Desktop/bFly_RI24C/dataset/IMDBID.txt");
            Bfly_UBFC.findAddNb("/Users/rasheeqishmam/Desktop/bFly_RI24C/dataset/IMDBEdge.txt");
            Bfly_UBFC.vpCheck();

            int r1 = gp.count_Bfly_Im_VP();

            long time_e = System.currentTimeMillis();
            long runTime = time_e - time_b;

            System.out.println("Threshold t: " + t + ", Uncertain Butterflies: " + r1 + ", Runtime: " + runTime + " milisecons");
        }
    }
}
