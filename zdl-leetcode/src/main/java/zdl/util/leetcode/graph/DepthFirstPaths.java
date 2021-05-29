package zdl.util.leetcode.graph;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 深度优先路径查询
 * <p>
 * 要实现路径查询，我们必须定义一个变量来记录所探索到的路径。
 * 所以在上面的基础上定义一个edgesTo变量来后向记录所有到s的顶点的记录，和仅记录从当前节点到起始节点不同，
 * 我们记录图中的每一个节点到开始节点的路径。为了完成这一日任务，通过设置edgesTo[w]=v，
 * 我们记录从v到w的边，换句话说，v-w是最后一条从s到达w的边。edgesTo[]其实是一个指向其父节点的树。
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2021/05/29/ 14:22
 */
public class DepthFirstPaths<T> {
    /**
     * 记录是否被dfs访问过
     */
    private final boolean[] marked;

    /**
     * 记录最后一个到当前节点的顶点
     */
    private final int[] edgesTo;

    /**
     * 搜索的起始点
     */
    private final T t;

    private final UndirectedGraph<T> graph;

    public DepthFirstPaths(UndirectedGraph<T> graph, T t) {
        this.graph = graph;
        marked = new boolean[graph.size()];
        edgesTo = new int[graph.size()];
        this.t = t;
        dfs(graph.getIndex(t));
    }

    private void dfs(int v) {
        marked[v] = true;
        for (int w : graph.adj(v)) {
            if (!marked[w]) {
                edgesTo[w] = v;
                dfs(w);
            }
        }
    }

    public boolean hasPathTo(int v) {
        return marked[v];
    }

    public Deque<T> pathTo(int v) {

        if (!hasPathTo(v)) {
            return new ArrayDeque<>();
        }

        Deque<T> path = new ArrayDeque<>();

        for (int x = v; x != graph.getIndex(t); x = edgesTo[x]) {
            T t1 = graph.ofIndex(x);
            path.push(t1);
        }

        path.push(t);
        return path;
    }
}
