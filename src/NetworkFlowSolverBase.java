import java.util.*;
import static java.lang.Math.min;
abstract class NetworkFlowSolverBase {

    // To avoid overflow, set infinity to a value less than long.MAX_VALUE;
    protected static final long INF = Long.MAX_VALUE / 2;

    public static class Edge {
        public int from, to;
        public String fromLabel, toLabel;
        public Edge residual;
        public long flow, cost;
        public final long capacity, originalCost;

        public Edge(int from, int to, long capacity) {
            this(from, to, capacity, 0 /* unused */);
        }

        public Edge(int from, int to, long capacity, long cost) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.originalCost = this.cost = cost;
        }



        public long remainingCapacity() {

            return capacity - flow;
        }

        public void augment(long bottleNeck) {
            flow += bottleNeck;
            residual.flow -= bottleNeck;
        }

        public String toString(int s, int t) {
            String u;
            String v;
            if (from == s)
            {
                u="s";
            }
            else{
                if (from==t)
                {
                    u="t";
                }
                else{
                    u=String.valueOf(from);
                }
            }


            if (to==s)
            {
                v="s";
            }
            else{
                if(to==t)
                {
                    v="t";
                }
                else{
                    v=String.valueOf(to);
                }
            }
            String m;
            if (capacity==0)
            {
                m="true";
            }
            else
            {
                m="false";
            }
            return String.format(
                    "Edge %s -> %s | flow = %d | capacity = %d | is residual: %s",
                    u, v, flow, capacity, m);
        }
    }

    // Inputs: n = number of nodes, s = source, t = sink
    protected int n, s, t;

    protected long maxFlow;
    protected long minCost;

    protected boolean[] minCut;
    protected List<Edge>[] graph;
    protected String[] vertexLabels;
    protected List<Edge> edges;


    private int visitedToken = 1;
    private int[] visited;

    // Indicates whether the network flow algorithm has ran. We should not need to
    // run the solver multiple times, because it always yields the same result.
    protected boolean solved;
    public NetworkFlowSolverBase(int n, String[] vertexLabels) {
        this.n = n;

        graph = new List[n];
        for (int i = 0; i < n; i++)
        {
            graph[i] = new ArrayList<Edge>();
        }
        assignLabelsToVertices(vertexLabels);
        minCut = new boolean[n];
        visited = new int[n];
        edges = new ArrayList<>();
    }

    // Add labels to vertices in the graph.
    private void assignLabelsToVertices(String[] vertexLabels) {
        if(vertexLabels.length != n){
            System.out.println("Incorrect Labels");
            return;
        }
        this.vertexLabels = vertexLabels;
    }


    public void addEdges(List<Edge> edges) {
        if (edges == null) {
            System.out.println("Edges cannot be null");
            return;
        }
        //for(Edge edge : edges)
        for(int i=0;i< edges.size();i++){
            Edge edge=edges.get(i);
            addEdge(edge.from, edge.to, edge.capacity);
        }
    }


    public void addEdge(int from, int to, long capacity) {
        if (capacity < 0)
        {
            System.out.println("Capacity < 0");
            return;
        }
        Edge e1 = new Edge(from, to, capacity);
        Edge e2 = new Edge(to, from, 0);
        e1.residual = e2;
        e2.residual = e1;
        graph[from].add(e1);
        graph[to].add(e2);
        edges.add(e1);
    }

    /** Cost variant of {@link #addEdge(int, int, int)} for min-cost max-flow */
    public void addEdge(int from, int to, long capacity, long cost) {
        Edge e1 = new Edge(from, to, capacity, cost);
        Edge e2 = new Edge(to, from, 0, -cost);
        e1.residual = e2;
        e2.residual = e1;
        graph[from].add(e1);
        graph[to].add(e2);
        edges.add(e1);
    }

    // Marks node 'i' as visited.
    public void visit(int i) {
        visited[i] = 1;
    }

    // Returns whether or not node 'i' has been visited.
    public boolean visited(int i) {
        return visited[i] == 1;
    }

    // Resets all nodes as unvisited. This is especially useful to do
    // between iterations of finding augmenting paths, O(1)
    public void markAllNodesAsUnvisited() {
        visitedToken++;
    }


    public List<Edge>[] getGraph() {
        execute();
        return graph;
    }

    /**
     * Returns all edges in this flow network
     */
    public List<Edge> getEdges() {
        return edges;
    }

    // Returns the maximum flow from the source to the sink.
    public long getMaxFlow() {
        execute();
        return maxFlow;
    }

    // Returns the min cost from the source to the sink.
    // NOTE: This method only applies to min-cost max-flow algorithms.
    public long getMinCost() {
        execute();
        return minCost;
    }

    // Returns the min-cut of this flow network in which the nodes on the "left side"
    // of the cut with the source are marked as true and those on the "right side"
    // of the cut with the sink are marked as false.
    public boolean[] getMinCut() {
        execute();
        return minCut;
    }

    /**
     * Used to set the source for this flow network
     */
    public void setSource(int s) {
        this.s = s;
    }

    /**
     * Used to set the sink for this flow network
     */
    public void setSink(int t) {
        this.t = t;
    }

    /**
     * Get source for this flow network
     */
    public int getSource() {
        return s;
    }

    /**
     * Get sink for this flow network
     */
    public int getSink() {
        return t;
    }


    public void printEdges() {
        //for(Edge edge : edges) {
        int[] visited = new int[edges.size()];
        Arrays.fill(visited,0);
        for(int i=0;i< edges.size();i++){
            if(visited[i]==1){
                continue;
            }
            Edge edge = edges.get(i);
            int matchFound = 0;
            visited[i]=1;
            for(int j=i+1;j< edges.size();j++) {

                Edge edgeNext = edges.get(j);
                if(vertexLabels[edge.from].equals(vertexLabels[edgeNext.to]) && vertexLabels[edgeNext.from].equals(vertexLabels[edge.to])){
                    matchFound = 1;
                    visited[j]=1;
                    if(edge.capacity > edgeNext.capacity){
                        System.out.println(String.format("%s ----%s----> %s", vertexLabels[edge.from], edge.capacity-edgeNext.capacity, vertexLabels[edge.to]));
                    } else if (edge.capacity < edgeNext.capacity) {
                        System.out.println(String.format("%s ----%s----> %s", vertexLabels[edgeNext.from], edgeNext.capacity-edge.capacity, vertexLabels[edgeNext.to]));
                    }else {
                        continue;
                    }
                }
                //System.out.println(String.format("%s ----%s----> %s", vertexLabels[edge.from], edge.capacity, vertexLabels[edge.to]));
            }
            if(matchFound == 0){
                System.out.println(String.format("%s ----%s----> %s", vertexLabels[edge.from], edge.capacity, vertexLabels[edge.to]));
            }
        }
    }

    public void printEdgesOlder() {
        //for(Edge edge : edges) {
        for(int i=0;i< edges.size();i++){
            Edge edge=edges.get(i);
            System.out.println(String.format("%s ----%s----> %s", vertexLabels[edge.from], edge.capacity, vertexLabels[edge.to]));
        }
    }

    public void recompute() {
        solved = false;
    }

    // Wrapper method that ensures we only call solve() once
    private void execute() {
        if (solved==true)
        {
            return;
        }
        else
        {
            solved = true;
            solve();
        }
    }

    // Method to implement which solves the network flow problem.
    public abstract void solve();
}