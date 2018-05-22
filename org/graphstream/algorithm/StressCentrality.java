package org.graphstream.algorithm;

import org.graphstream.algorithm.measure.AbstractCentrality;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
/**
 * Compute the "stress" centrality of each vertex of a given graph.
 *
 * This is based on the algorithm described in "On variants of shortest-path
 * betweenness centrality and their generic computation", Ulrik Brandes, 2008.
 *
 */


public class StressCentrality extends AbstractCentrality {

    protected static final double INFINITY = 1000000000.0;

    /**
     * Store the centrality value in this attribute on nodes and edges.
     */
    protected static String centralityAttributeName = "Cs";

    /**
     * Display progress?
     */
    protected boolean showProgress = false;

    /**
     * The predecessors value.
     */
    protected String predAttributeName = "pred";

    /**
     * The sigma value.
     */
    protected String sigmaAttributeName = "sigma";

    /**
     * The delta value.
     */
    protected String deltaAttributeName = "delta";

    /**
     * The distance value.
     */
    protected String distAttributeName = "distance";

    /**
     * The graph to modify.
     */
    protected Graph graph;

    /**
     * Default constructor. By default the centrality will be stored
     * in a "Cs" attribute on each node and progress will not be displayed.
     */
    public StressCentrality() {
        super(null, null);
        this.showProgress = false;
    }

    /**
     * Constructor allowing to configure showprogres value. By default the centrality will be stored
     * in a "Cs" attribute on each node.
     */
    public StressCentrality(boolean showProgress) {
        this();
        this.showProgress = showProgress;
    }

    /**
     * Construtor allowing to configure centrality attribute.
     *
     * @param centralityAttributeName name attribute where centrality will be stored
     */
    public StressCentrality(String centralityAttributeName) {
        this();
        this.centralityAttributeName = centralityAttributeName;
    }

    /**
     * Fully configurable construtor.
     * Constructor allowing to configure attribute and displaying progress. Same as
     * calling `StressCentrality(attributeName, true)`.
     *
     * @param centralityAttributeName attribute where centrality will be stored
     * @param showProgress            defines the normalization mode
     */
    public StressCentrality(String centralityAttributeName, boolean showProgress) {
        this(showProgress);
        this.centralityAttributeName = centralityAttributeName;
        this.showProgress = showProgress;
    }

    /**
     * Setup the algorithm to work on the given graph.
     */
    @Override
    public void init(Graph graph) {
        this.graph = graph;
    }

    /**
     * Set a default centrality of 0 to all nodes.
     *
     * @param graph The graph to modify.
     */
    public void initAllNodes(Graph graph) {
        for (Node node : graph) {
            setCentrality(node, 0.0);
        }
    }

    /**
     * Add a default value for attributes used during computation.
     *
     * @param graph The graph to modify.
     */
    protected void setupAllNodes(Graph graph) {
        /*
         * for weV do Pred[w] <- empty list
         * for teV do dist[t] -< INFINITY;
         * sigma <- 0
         */
        for (Node node : graph) {
            clearPred(node);
            setDistance(node, INFINITY);
            setSigma(node, 0.0);
            setDelta(node, 0.0);
        }
    }

    @Override
    public void compute() {
        if (graph != null) {
            computeCentrality();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see
     * org.graphstream.algorithm.measure.AbstractCentrality#computeCentrality()
     *
     * Compute the stress centrality on the given graph for each node.
     * This method is equivalent to a call in sequence to the
     * two methods {@link #init(Graph)} then {@link #compute()}.
     */
    @Override
    protected void computeCentrality() {
        Graph graph = this.graph;
        init(graph);
        initAllNodes(graph);

        int n = graph.getNodeCount();
        int i = 0;

        System.out.println("\n\nStarting calculate Stress Centrality...");

        /*
         * Single-source shortest-paths problem
         */
        // for seV
        for (Node s : graph) {
            PriorityQueue<Node> S = null;

            /*
             * INITIALIZATION
             */
            S = initialization(s, graph);

            /*
             * ACCUMULATION
             */
            while (!S.isEmpty()) {
                //pop w <- S
                Node w = S.poll();
                //for vePred[w]
                for (Node v : getPred(w)) {
                    //c = 1.0 + delta[w]
                    double c = 1.0 + getDelta(w);

                    //delta[v] <- delta[v] + (1.0 + deltra[w]
                    setDelta(v, getDelta(v) + c);
                }
                //if w =/= s
                if (w != s) {
                    double c = getSigma(w) * getDelta(w);
                    //stressCentrality[w] <- stressCentrality[w] + (sigma[w] * delta[w])"/2"
                    setCentrality(w, getCentrality(w) + c / 2);
                }
            }

            if (showProgress) {
                try {
                    Thread.sleep(50);
                    printProgress(++i, n);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * Calculations and display progress for calculated stress Centrality.
     *
     * @param current Current node
     * @param total Total count of nodes
     */
    protected void printProgress(int current, int total) {
        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string.append('\r')
                .append(String.format("Progress: %d%% [", percent))
                .append(String.join("", Collections.nCopies(percent, "="))).append('>')
                .append(String.join("", Collections.nCopies(100 - percent, " "))).append(']')
                .append(String.format(" %d/%d", current, total));

        System.out.print(string);
    }

    /**
     * Compute single-source multiple-targets shortest paths on an unweighted
     * graph.
     *
     * @param source The source node.
     * @param graph  The graph.
     * @return A priority queue of explored nodes with sigma values usable to
     * compute the centrality.
     */
    protected PriorityQueue<Node> initialization(Node source, Graph graph) {
        /*
         * INITIALIZATION
         */
        LinkedList<Node> Q = new LinkedList<Node>();
        PriorityQueue<Node> S = new PriorityQueue<Node>(graph.getNodeCount(),new BrandesNodeComparatorLargerFirst());

        // for weV
        setupAllNodes(graph);
        // for teV
        //dist[s] <- 0 OR dist[s] <- INFINITY
        setDistance(source, 0.0);
        //sigma[s] <- 1
        setSigma(source, 1.0);
        //enqueue s -> Q
        Q.add(source);

        //while Q not empty do
        while (!Q.isEmpty()) {
            //dequeue v <- Q
            Node v = Q.removeFirst();
            //push v -> S
            S.add(v);

            //foreach vertex w such that (v, w)eE
            Iterator<Edge> ww = v.getLeavingEdgeIterator();
            while (ww.hasNext()) {
                Edge e = ww.next();
                Node w = e.getOpposite(v);//ww.next();

                /*
                 * Path discovery
                 */
                // if dist[w] == INFINITY
                if (getDistance(w) == INFINITY) {
                    //dist[w] <- dist[v] + 1
                    setDistance(w, getDistance(v) + 1);
                    //enqueue w -> Q
                    Q.add(w);
                }

                /*
                 * Patch counting
                 */
                // if dist[w] <- dist[v] + 1
                if (getDistance(w) == (getDistance(v) + 1.0)) {
                    //sigma[w] <- sigma[w] + signa[v]
                    setSigma(w, getSigma(w) + getSigma(v));
                    //append v -> Pred[w]
                    addToPred(w, v);
                }
            }
        }
        return S;
    }

    /**
     * Specify the name of the attribute used to store the computed centrality
     * values for each node.
     */
    public void setCentralityAttributeName(String centralityAttributeName) {
        this.centralityAttributeName = centralityAttributeName;
    }

    /**
     * Name of the attribute used to store centrality values on nodes.
     */
    public String getCentralityAttributeName() {
        return centralityAttributeName;
    }

    /**
     * Set the sigma value of the given node.
     *
     * @param node  The node to modify.
     * @param sigma The sigma value to store on the node.
     */
    protected void setSigma(Node node, double sigma) {
        node.setAttribute(sigmaAttributeName, sigma);
    }

    /**
     * The sigma value of the given node.
     *
     * @param node Extract the sigma value of this node.
     * @return The sigma value.
     */
    protected double getSigma(Node node) {
        return node.getNumber(sigmaAttributeName);
    }

    /**
     * Set the delta value of the given node.
     *
     * @param node  The node to modify.
     * @param delta The delta value to store on the node.
     */
    protected void setDelta(Node node, double delta) {
        node.setAttribute(deltaAttributeName, delta);
    }

    /**
     * The delta value of the given node.
     *
     * @param node Extract the delta value of this node.
     * @return The delta value.
     */
    protected double getDelta(Node node) {
        return node.getNumber(deltaAttributeName);
    }

    /**
     * Set the distance value of the given node.
     *
     * @param node     The node to modify.
     * @param distance The delta value to store on the node.
     */
    protected void setDistance(Node node, double distance) {
        node.setAttribute(distAttributeName, distance);
    }

    /**
     * The distance value of the given node.
     *
     * @param node Extract the distance value of this node.
     * @return The distance value.
     */
    protected double getDistance(Node node) {
        return node.getNumber(distAttributeName);
    }

    /**
     * Set the centrality of the given node.
     *
     * @param elt        The node to modify.
     * @param centrality The centrality to store on the node.
     */
    public void setCentrality(Element elt, double centrality) {
        elt.setAttribute(centralityAttributeName, centrality);
    }

    /**
     * The centrality value of the given node.
     *
     * @param elt Extract the centrality of this node.
     * @return The centrality value.
     */
    public double getCentrality(Element elt) {
        return elt.getNumber(centralityAttributeName);
    }

    /**
     * Activate or deactivate calculations and display progress for calculated
     * stress Centrality. By default it is deactivated.
     *
     * @param status If it is true, progress is displayed.
     */
    public void setShowProgress(boolean status) {
        this.showProgress = status;
    }

    /**
     * Delete attributes used by this algorithm in nodes and edges of the graph
     */
    public void cleanGraph() {
        cleanEdges();
        cleanNodes();
    }

    /**
     * Delete attributes used by this algorithm in nodes of the graph
     */
    public void cleanNodes() {
        cleanElement(graph.getEachNode());
    }

    /**
     * Delete attributes used by this algorithm in edges of the graph
     */
    public void cleanEdges() {
        cleanElement(graph.getEachEdge());
    }

    /**
     * Delete attributes used by this algorithm in elements of a graph
     *
     * @param it the list of elements
     */
    private void cleanElement(Iterable<? extends Element> it) {
        for (Element e : it) {
            if (e.hasAttribute(predAttributeName)) e.removeAttribute(predAttributeName);
            if (e.hasAttribute(sigmaAttributeName)) e.removeAttribute(sigmaAttributeName);
            if (e.hasAttribute(distAttributeName)) e.removeAttribute(distAttributeName);
            if (e.hasAttribute(deltaAttributeName)) e.removeAttribute(deltaAttributeName);
        }
    }

    /**
     * List of predecessors of the given node.
     *
     * @param node Extract the predecessors of this node.
     * @return The list of predecessors.
     */
    protected Set<Node> getPred(Node node) {
        return (HashSet<Node>) node.getAttribute(predAttributeName);
    }

    /**
     * Remove all predecessors of the given node and then add it a first
     * predecessor.
     *
     * @param node        The node to modify.
     * @param predecessor The predecessor to add.
     */
    protected void replacePred(Node node, Node predecessor) {
        HashSet<Node> set = new HashSet<Node>();

        set.add(predecessor);
        node.setAttribute(predAttributeName, set);
    }

    /**
     * Add a node to the predecessors of another.
     *
     * @param node        Modify the predecessors of this node.
     * @param predecessor The predecessor to add.
     */
    @SuppressWarnings("all")
    protected void addToPred(Node node, Node predecessor) {
        HashSet<Node> preds = (HashSet<Node>) node.getAttribute(predAttributeName);

        preds.add(predecessor);
    }

    /**
     * Remove all predecessors of the given node.
     *
     * @param node Remove all predecessors of this node.
     */
    protected void clearPred(Node node) {
        HashSet<Node> set = new HashSet<Node>();
        node.setAttribute(predAttributeName, set);
    }

    /**
     * Increasing comparator used for priority queues.
     */
    protected class BrandesNodeComparatorLargerFirst implements Comparator<Node> {
        public int compare(Node x, Node y) {
            double yy = getDistance(y);
            double xx = getDistance(x);

            if (xx > yy)
                return -1;
            else if (xx < yy)
                return 1;

            return 0;
        }
    }
}
