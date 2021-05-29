package zdl.util.leetcode.graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

/**
 * 广度优先算法
 * <p>
 * 通常我们更关注的是一类单源最短路径的问题，那就是给定一个图和一个源S，是否存在一条从s到给定顶点v的路径，
 * 如果存在，找出最短的那条(这里最短定义为边的条数最小)。深度优先算法是将未被访问的节点放到一个栈中(stack)，
 * 虽然在上面的代码中没有明确在代码中写stack，但是递归间接的利用递归堆实现了这一原理。和深度优先算法不同，
 * 广度优先是将所有未被访问的节点放到了队列中。
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2021/05/29/ 14:35
 */
public class BreadthFirstSearch<T> {
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

    public BreadthFirstSearch(UndirectedGraph<T> graph, T t) {
        this.graph = graph;
        marked = new boolean[graph.size()];
        edgesTo = new int[graph.size()];
        this.t = t;
        bfs(graph.getIndex(t));
    }

    private void bfs(int s) {
        Queue<Integer> queue = new ArrayDeque<>();
        marked[s] = true;
        queue.offer(s);
        while (queue.size() != 0) {
            int v = queue.poll();
            for (int w : graph.adj(v)) {
                if (!marked[w]) {
                    edgesTo[w] = v;
                    marked[w] = true;
                    queue.offer(w);
                }
            }
        }
    }

    public boolean hasPathTo(int v) {
        return marked[v];
    }

    public Deque<T> PathTo(int v) {
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
