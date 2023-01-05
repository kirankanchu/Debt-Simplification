import java.util.*;
import static java.lang.Math.min;
class Dinics extends NetworkFlowSolverBase {

    private int[] level;

    public Dinics(int n, String[] vertexLabels) {
        super(n, vertexLabels);
        level = new int[n];
    }

    @Override
    public void solve() {

        int[] npart = new int[n];

        while (bfs()) {
            Arrays.fill(npart, 0);
            // Find max flow by adding all augmenting path flows.
            for (long f = dfs(s, npart, INF); f != 0; f = dfs(s, npart, INF)) {
                maxFlow += f;
            }
        }

        for (int i = 0; i < n; i++)
        {
            if (level[i] != -1)
            {
                minCut[i] = true;
            }
        }
    }

    // Do a BFS from source to sink and compute the depth/level of each node
    // which is the minimum number of edges from that node to the source.
    private boolean bfs() {
        Arrays.fill(level, -1);
        level[s] = 0;
        Deque<Integer> q = new ArrayDeque<>(n);
        q.offerLast(s);
        while (!q.isEmpty()) {
            int node = q.pollFirst();
            //for (Edge edge : graph[node]) {
            for(int i=0;i<graph[node].size();i++){
                Edge edge=graph[node].get(i);
                long cap = edge.remainingCapacity();
                if (cap > 0 && level[edge.to] == -1) {
                    level[edge.to] = level[node] + 1;
                    q.offerLast(edge.to);
                }
            }
        }
        return level[t] != -1;
    }

    private long dfs(int pos, int[] npart, long flow) {
        if (pos == t) return flow;
        final int numEdges = graph[pos].size();

        for (; npart[pos] < numEdges; npart[pos]++) {
            Edge edge = graph[pos].get(npart[pos]);
            long cap = edge.remainingCapacity();
            if (cap > 0 && level[edge.to] == level[pos] + 1) {

                long bottleNeck = dfs(edge.to, npart, min(flow, cap));
                if (bottleNeck > 0) {
                    edge.augment(bottleNeck);
                    return bottleNeck;
                }
            }
        }
        return 0;
    }
}