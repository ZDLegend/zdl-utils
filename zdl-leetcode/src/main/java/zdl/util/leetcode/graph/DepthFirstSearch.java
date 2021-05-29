package zdl.util.leetcode.graph;

/**
 * 深度优先算法
 * <p>
 * 迷宫探索问题。下面是一个迷宫和图之间的对应关系：迷宫中的每一个交会点代表图中的一个顶点，
 * 每一条通道对应一个边。 迷宫探索可以采用Trémaux绳索探索法。
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2021/05/29/ 14:16
 */
public class DepthFirstSearch<T> {

    private final boolean[] marked;

    /**
     * number of vertices connected to s
     */
    private int count;

    private final UndirectedGraph<T> graph;

    public DepthFirstSearch(UndirectedGraph<T> graph, int s) {
        this.graph = graph;
        marked = new boolean[graph.size()];
        dfs(s);
    }

    /**
     * depth first search from v
     */
    private void dfs(int v) {
        count++;
        marked[v] = true;
        for (int w : graph.adj(v)) {
            if (!marked[w]) {
                dfs(w);
            }
        }
    }

    public boolean marked(int v) {
        return marked[v];
    }

    public int count() {
        return count;
    }
}
