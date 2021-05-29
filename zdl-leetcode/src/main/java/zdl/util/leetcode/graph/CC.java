package zdl.util.leetcode.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * 连通分量
 * <p>
 * 使用深度优先遍历计算图的所有连通分量。
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2021/05/29/ 14:09
 */
public class CC<T> {
    private final boolean[] marked;
    private final int[] id;

    /**
     * 连通分量
     */
    private int count;
    private final UndirectedGraph<T> graph;

    public CC(UndirectedGraph<T> graph) {
        this.graph = graph;
        marked = new boolean[graph.size()];
        id = new int[graph.size()];
        for (int i = 0; i < graph.size(); i++) {
            if (!marked[i]) {
                dfs(i);
                count++;
            }
        }
    }

    /**
     * 计算无向图的连通分量
     *
     * @return 连通分量列表
     */
    public List<List<T>> to() {
        List<List<T>> ll = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ll.add(new ArrayList<>());
        }
        for (int i = 0; i < id.length; i++) {
            ll.get(id[i]).add(graph.ofIndex(i));
        }
        return ll;
    }

    /**
     * 处理索引为V的元素的连通分量
     */
    private void dfs(int v) {
        marked[v] = false;
        id[v] = count;
        for (int w : graph.adj(v)) {
            if (!marked[w]) {
                dfs(w);
            }
        }
    }

    /**
     * 判断v和W是否连通
     */
    public boolean connected(T v, T w) {
        int iv = graph.getIndex(v);
        int iw = graph.getIndex(w);
        return id[iv] == id[iw];
    }

    public int getCount() {
        return count;
    }
}
