#include "graph.h"

myGraph::myGraph(int n, bool orgraph)
    : m_num(n), is_orgraph(orgraph),
      m_matrix_smezh(n, std::vector<int>(n, 0))
{}

void myGraph::addEdge(int u, int v) {
    m_matrix_smezh[u][v] = 1;
    if (!is_orgraph)
        m_matrix_smezh[v][u] = 1;
}

int myGraph::getNum() const {
    return m_num;
}

std::vector<int> myGraph::bfsShortestPath(int start, int end) {
    std::vector<int> parent(m_num, -1);
    std::vector<bool> visited(m_num, false);
    std::queue<int> q;

    visited[start] = true;
    q.push(start);

    while (!q.empty()) {
        int v = q.front();
        q.pop();

        if (v == end) break;

        for (int i = 0; i < m_num; ++i) {
            if (m_matrix_smezh[v][i] && !visited[i]) {
                visited[i] = true;
                parent[i] = v;
                q.push(i);
            }
        }
    }

    if (!visited[end])
        return {};

    std::vector<int> path;
    for (int v = end; v != -1; v = parent[v])
        path.push_back(v);
    std::reverse(path.begin(), path.end());
    return path;
}