#pragma once

#include <vector>
#include <iostream>
#include <queue>

class myGraph {
    int m_num;
    bool is_orgraph;
    std::vector<std::vector<int>> m_matrix_smezh;

public:
    myGraph(int n, bool orgraph);

    void addEdge(int u, int v);
    std::vector<int> bfsShortestPath(int start, int end);
    int getNum() const;
};