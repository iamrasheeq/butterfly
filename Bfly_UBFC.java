import java.io.*;
import java.util.*;

class MyPair<P1, P2> {
    public P1 one;
    public P2 two;

    public MyPair(P1 one, P2 two) {
        this.one = one;
        this.two = two;
    }

    @Override
    public String toString() {
        return String.format("{%s, %s}", one, two);
    }
}

public class Bfly_UBFC {

    public static List<Integer> id_VerN;
    public static List<myEdge> edgeList;
    public static Map<Integer, Node> nodeMap;
    public static double ProbMax, t;

    public Bfly_UBFC() {
        id_VerN = new ArrayList<>();
        edgeList = new ArrayList<>();
        nodeMap = new HashMap<>();
    }

    public static void node_id_to_obj(String file_name) {
        try (BufferedReader br = new BufferedReader(new FileReader(file_name))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split("\t");
                if (splitLine.length > 0 && !splitLine[0].trim().isEmpty()) {
                    try {
                        Node node = new Node();
                        int id = Integer.parseInt(splitLine[0]);
                        node.nodeid = id;
                        id_VerN.add(id);
                        nodeMap.put(id, node);
                    } catch (NumberFormatException e) {
                        System.out.println("Integer parse error. Check your input file.");
                        System.exit(2);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("File read error.");
            System.exit(2);
        }
    }

    public static void findAddNb(String file_name) {
        try (BufferedReader br = new BufferedReader(new FileReader(file_name))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\t");
                int a = Integer.parseInt(fields[0]);
                int b = Integer.parseInt(fields[1]);
                double prob = Double.parseDouble(fields[2]);

                Node nodeA = nodeMap.computeIfAbsent(a, k -> new Node());
                Node nodeB = nodeMap.computeIfAbsent(b, k -> new Node());

                if (nodeA == null || nodeB == null) {
                    System.out.println("Node error in edge list.");
                    System.exit(4);
                }

                nodeA.nb.add(new MyPair<>(b, prob));
                nodeB.nb.add(new MyPair<>(a, prob));

                myEdge edge = new myEdge();
                edge.node1 = a;
                edge.node2 = b;
                edge.p1 = prob;
                edgeList.add(edge);

                if (prob > ProbMax) {
                    ProbMax = prob;
                }
            }
        } catch (IOException e) {
            System.out.println("Edge file not found.");
            System.exit(3);
        }
    }

    public static class Node {
        public int nodeid;
        public List<MyPair<Integer, Double>> nb;

        public Node() {
            nb = new ArrayList<>();
        }

        public void addNeighbor(int id, double val) {
            nb.add(new MyPair<>(id, val));
        }

        public int get_nodeId() {
            return nodeid;
        }
    }

    public static class myEdge {
        public double p1;
        public int node1, node2;
    }

    public static boolean ver_pri_comp(Node a, Node b) {
        if (a.nb.size() == b.nb.size()) {
            return a.nodeid > b.nodeid;
        } else {
            return a.nb.size() > b.nb.size();
        }
    }

    public static boolean ver_pri_Rev_comp(Node a, Node b) {
        if (a.nb.size() == b.nb.size()) {
            return a.nodeid < b.nodeid;
        } else {
            return a.nb.size() < b.nb.size();
        }
    }

    public static void nb_vertex_priority_sort(Node node) {
        node.nb.sort(Comparator.comparing(pair -> nodeMap.get(pair.one),
                (n1, n2) -> ver_pri_Rev_comp(n1, n2) ? -1 : 1));
    }

    public static void vpCheck() {
        for (Node node : nodeMap.values()) {
            nb_vertex_priority_sort(node);
        }
    }

    public double prob_calcu_base(int u1, int w1, List<MyPair<Integer, Double>> edgeList) {
        Map<Integer, Double> probMap = new HashMap<>();
        for (MyPair<Integer, Double> pair : edgeList) {
            probMap.put(pair.one, pair.two);
        }
        double pu = probMap.getOrDefault(u1, 0.0);
        double pw = probMap.getOrDefault(w1, 0.0);
        return pu * pw;
    }

    public int count_butterfly_base(int u1, int w1, List<Integer> wedgeList) {
        int count = 0;
        int len = wedgeList.size();
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                Node node1 = nodeMap.get(wedgeList.get(i));
                Node node2 = nodeMap.get(wedgeList.get(j));

                double prob1 = prob_calcu_base(u1, w1, node1.nb);
                double prob2 = prob_calcu_base(u1, w1, node2.nb);

                if (prob1 * prob2 >= t) {
                    count++;
                }
            }
        }
        return count;
    }

    public int exact_uncertain_butterfly() {
        int totalCount = 0;
        Map<Integer, List<Integer>> wedgeMap = new HashMap<>();
        for (int u : id_VerN) {
            wedgeMap.clear();
            Node nodeU = nodeMap.get(u);
            for (MyPair<Integer, Double> pair : nodeU.nb) {
                int v = pair.one;
                Node nodeV = nodeMap.get(v);
                if (ver_pri_comp(nodeU, nodeV)) {
                    for (MyPair<Integer, Double> innerPair : nodeV.nb) {
                        int w = innerPair.one;
                        if (u == w) continue;
                        if (ver_pri_comp(nodeU, nodeMap.get(w))) {
                            wedgeMap.computeIfAbsent(w, k -> new ArrayList<>()).add(v);
                        } else {
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
            for (Map.Entry<Integer, List<Integer>> entry : wedgeMap.entrySet()) {
                totalCount += count_butterfly_base(u, entry.getKey(), entry.getValue());
            }
        }
        return totalCount;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Bfly_UBFC obj = new Bfly_UBFC();
        Bfly_UBFC.t = 0.50;
        node_id_to_obj("/Users/rasheeqishmam/Desktop/bFly_RI24C/dataset/IMDBID.txt");
        findAddNb("/Users/rasheeqishmam/Desktop/bFly_RI24C/dataset/IMDBEdge.txt");
        vpCheck();

        int result = obj.exact_uncertain_butterfly();
        long endTime = System.currentTimeMillis();
        System.out.println("Threshold t: " + t + ", Uncertain Butterflies: " + result + ", Runtime: " + (endTime - startTime) + " milisecons");
    }
}
