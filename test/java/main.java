import org.graphstream.algorithm.StressCentrality;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class main {
    public static void main(String[] args) {

        SingleGraph graph = new SingleGraph("StressCentrality");
        SingleGraph temp = new SingleGraph("Temp Graph");

        StressCentrality sc = new StressCentrality(true);

        graph.clear();

        /**
         * Create graph
         */
//        generateGraph(graph);
//        lessonGraph(graph);
//        stressGraph(graph);
//        stressGraph2(graph);
//        stressGraph3(graph);
        examplegraph1(graph);

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }

        graph.setAutoCreate(true);
        graph.setStrict(true);
        graph.display();

        sc.init(graph);
        sc.compute();

        Node n = temp.addNode("b");
        n.addAttribute(sc.getCentralityAttributeName(), "0.0");

        String out = "";
        for (Node node : graph) {
            if (Float.parseFloat(n.getAttribute(sc.getCentralityAttributeName()).toString()) < Float.parseFloat(node.getAttribute(sc.getCentralityAttributeName()).toString())) {
                n = node;
            }
        }
        for (Node node : graph) {
            if (Double.parseDouble(n.getAttribute(sc.getCentralityAttributeName()).toString()) == Double.parseDouble(node.getAttribute(sc.getCentralityAttributeName()).toString())) {
                graph.addAttribute("ui.stylesheet", nodeStyle);
                node.addAttribute("ui.class", "marked");
                out += "Node -> " + node + " == " + node.getAttribute(sc.getCentralityAttributeName()) + "\n";
            }
        }

        System.out.println("\n\nCounted centrality of stress in the nodes:\n" + out + "\n");

//        if(false) {
        if (true) {
            System.out.println("Counted centrality of stress in the nodes:");
            for (Node node : graph) {
                System.out.println(node + " == " + node.getAttribute(sc.getCentralityAttributeName()));
            }
        }
    }

    /**
     * Create graphs__________________________________________________________________
     */
    private static void generateGraph(org.graphstream.graph.Graph graph) {
        graph.clear();
        Random rand = new Random();
        Generator gen = new DorogovtsevMendesGenerator(rand);
        gen.addSink(graph);
        gen.begin();
        int total = 337;
        System.out.println("Create graph!!!");
        for (int i = 0; i <= total; i++) {
            gen.nextEvents();
            printPercent(i, total);
        }
        gen.end();
    }

    private static void stressGraph(org.graphstream.graph.Graph graph) {
        Node A = graph.addNode("A"); //1
        Node B = graph.addNode("B"); //2
        Node C = graph.addNode("C"); //3
        Node D = graph.addNode("D"); //4
        Node E = graph.addNode("E"); //5
        Node F = graph.addNode("F"); //6
        Node G = graph.addNode("G"); //7

        graph.addEdge("AG", "A", "G");
        graph.addEdge("AD", "A", "D");
        graph.addEdge("GD", "G", "D");
        graph.addEdge("DB", "D", "B");
        graph.addEdge("BF", "B", "F");
        graph.addEdge("FE", "F", "E");
        graph.addEdge("FC", "F", "C");
        graph.addEdge("CE", "C", "E");
    }

    private static void lessonGraph(org.graphstream.graph.Graph graph) {

//         A   B    A=0.0
//          \ /     B=0.0
//           C      C=35.666666666666664
//          / \     D=11.0
//         E   D    E=27.333333333333332
//        / \ /     F=30.333333333333336
//       G   F      G=12.0
//        \ /       H=43.66666666666667
//         H        I=32.0
//         |        J=0.0
//         I        K=0.0
//        / \
//       J---K

        Node A = graph.addNode("A"); //1
        Node B = graph.addNode("B"); //2
        Node C = graph.addNode("C"); //3
        Node D = graph.addNode("D"); //4
        Node E = graph.addNode("E"); //5
        Node F = graph.addNode("F"); //6
        Node G = graph.addNode("G"); //7
        Node H = graph.addNode("H"); //8
        Node I = graph.addNode("I"); //9
        Node J = graph.addNode("J"); //10
        Node K = graph.addNode("K"); //11

//        Node Q = graph.addNode("Q");
//        graph.addEdge("AB", "A", "B");
//        graph.addEdge("QC", "Q", "C");
//        graph.addEdge("QD", "Q", "D");//C -> Q
//        graph.addEdge("QE", "Q", "E");//C -> Q

        graph.addEdge("AC", "A", "C");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CD", "C", "D");//C -> Q
        graph.addEdge("CE", "C", "E");//C -> Q
        graph.addEdge("DF", "D", "F");
        graph.addEdge("EF", "E", "F");
        graph.addEdge("EG", "E", "G");
        graph.addEdge("GH", "G", "H");
        graph.addEdge("FH", "F", "H");
        graph.addEdge("HI", "H", "I");
        graph.addEdge("IJ", "I", "J");
        graph.addEdge("IK", "I", "K");
        graph.addEdge("KJ", "K", "J");

    }

    private static void examplegraph1(org.graphstream.graph.Graph graph){
        Node A = graph.addNode("A"); //1
        Node B = graph.addNode("B"); //2
        Node C = graph.addNode("C"); //3
        Node D = graph.addNode("D"); //4
        Node E = graph.addNode("E"); //5...
        Node F = graph.addNode("F"); //6...
        Node G = graph.addNode("G"); //7...
        Node H = graph.addNode("H"); //8
        Node I = graph.addNode("I"); //9
        Node J = graph.addNode("J"); //10
        Node K = graph.addNode("K"); //11

        graph.addEdge("AE", "A", "E");
        graph.addEdge("AF", "A", "F");
        graph.addEdge("AG", "A", "G");

        graph.addEdge("BE", "B", "E");
        graph.addEdge("BF", "B", "F");
        graph.addEdge("BG", "B", "G");

        graph.addEdge("CE", "C", "E");
        graph.addEdge("CF", "C", "F");
        graph.addEdge("CG", "C", "G");

        graph.addEdge("DE", "D", "E");
        graph.addEdge("DF", "D", "F");
        graph.addEdge("DG", "D", "G");

        graph.addEdge("HE", "H", "E");
        graph.addEdge("HF", "H", "F");
        graph.addEdge("HG", "H", "G");

        graph.addEdge("IE", "I", "E");
        graph.addEdge("IF", "I", "F");
        graph.addEdge("IG", "I", "G");

        graph.addEdge("JE", "J", "E");
        graph.addEdge("JF", "J", "F");
        graph.addEdge("JG", "J", "G");

        graph.addEdge("KE", "K", "E");
        graph.addEdge("KF", "K", "F");
        graph.addEdge("KG", "K", "G");

    }

    private static void examplegraph2(org.graphstream.graph.Graph graph){
        Node A = graph.addNode("A"); //1
        Node B = graph.addNode("B"); //2
        Node C = graph.addNode("C"); //3
        Node D = graph.addNode("D"); //4
        Node E = graph.addNode("E"); //5...
        Node F = graph.addNode("F"); //6
        Node G = graph.addNode("G"); //7
        Node H = graph.addNode("H"); //8
        Node I = graph.addNode("I"); //9

        graph.addEdge("AE", "A", "E");

        graph.addEdge("BE", "B", "E");

        graph.addEdge("CE", "C", "E");

        graph.addEdge("DE", "D", "E");

        graph.addEdge("FE", "F", "E");

        graph.addEdge("GE", "G", "E");

        graph.addEdge("HE", "H", "E");

        graph.addEdge("IE", "I", "E");

    }

    protected static void createlist(LinkedList edge) {
        edge.add("1-22");
        edge.add("1-12");
        edge.add("1-18");
        edge.add("1-13");
        edge.add("1-11");
        edge.add("1-6");
        edge.add("1-7");
        edge.add("1-8");
        edge.add("1-14");
        edge.add("1-3");
        edge.add("1-32");
        edge.add("17-7");
        edge.add("17-6");
        edge.add("11-5");
        edge.add("13-4");
        edge.add("18-2");
        edge.add("22-2");
        edge.add("31-2");
        edge.add("31-9");
        edge.add("31-34");
        edge.add("31-33");
        edge.add("6-7");
        edge.add("6-11");
        edge.add("5-7");
        edge.add("23-34");
        edge.add("33-23");
        edge.add("21-33");
        edge.add("21-34");
        edge.add("19-33");
        edge.add("19-34");
        edge.add("15-33");
        edge.add("15-34");
        edge.add("16-33");
        edge.add("16-34");
        edge.add("30-33");
        edge.add("30-34");
        edge.add("30-27");
        edge.add("27-34");
        edge.add("24-30");
        edge.add("24-33");
        edge.add("24-34");
        edge.add("24-26");
        edge.add("26-32");
        edge.add("26-25");
        edge.add("25-28");
        edge.add("25-32");
        edge.add("28-24");
        edge.add("28-3");
        edge.add("28-34");
        edge.add("10-34");
        edge.add("10-3");
        edge.add("32-33");
        edge.add("32-34");
        edge.add("29-32");
        edge.add("29-34");
        edge.add("29-3");
        edge.add("8-5");
        edge.add("8-4");
        edge.add("8-2");
        edge.add("3-33");
        edge.add("3-32");
        edge.add("3-9");
        edge.add("9-33");
        edge.add("9-34");
        edge.add("14-34");
        edge.add("14-3");
        edge.add("14-4");
        edge.add("14-2");
        edge.add("20-34");
        edge.add("20-2");
        edge.add("20-1");
    }

    private static void stressGraph3(org.graphstream.graph.Graph graph) {
        Node A = graph.addNode("A"); //1
        Node B = graph.addNode("B"); //2
        Node C = graph.addNode("C"); //3
        Node D = graph.addNode("D"); //4
        Node E = graph.addNode("E"); //5

        graph.addEdge("AB", "A", "B");
        graph.addEdge("AC", "A", "C");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CD", "C", "D");
        graph.addEdge("DE", "D", "E");
    }

    /**
     * End create graphs__________________________________________________________________
     */

    protected static void printPercent(int current, int total) {
        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string.append('\r')
                //.append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format("Progress: %d%% [", percent))
                .append(String.join("", Collections.nCopies(percent, "=")))
                .append('>')
                .append(String.join("", Collections.nCopies(100 - percent, " ")))
                .append(']')
                .append(String.format(" %d/%d", current, total));

        System.out.print(string);
    }

    protected static String nodeStyle =
            "node {" +
                    "	fill-color: black;" +
                    "}" +
                    "node.marked {" +
                    "	fill-color: red;" +
                    "   size: 15px;" +
                    "}";
}
