#include <iostream>
#include <string>
#include <regex>
#include "graph.h"

static int readInt(const std::string& prompt, int lo, int hi, int exclude = -1) {
    std::regex num_r("^[0-9]+$");
    std::string input;
    while (true) {
        std::cout << prompt;
        std::cin >> input;
        if (std::regex_match(input, num_r)) {
            int v = std::stoi(input);
            if (v >= lo && v <= hi && v != exclude)
                return v;
        }
        std::cout << "Incorrect input! Enter a number from "
                  << lo << " to " << hi;
        if (exclude != -1)
            std::cout << " (not " << exclude << ")";
        std::cout << ".\n";
    }
}

static bool readYN(const std::string& prompt) {
    std::regex yn("^[ynYN]$");
    std::string input;
    while (true) {
        std::cout << prompt;
        std::cin >> input;
        if (std::regex_match(input, yn))
            return (input == "y" || input == "Y");
        std::cout << "Incorrect input! Enter y or n.\n";
    }
}

int main() {
    // --- Number of vertices ---
    int n = readInt("Enter number of vertices (>1): ", 2, 10000);

    // --- Oriented? ---
    bool oriented = readYN("Oriented graph? (y/n): ");

    myGraph gr(n, oriented);

    // --- Enter edges ---
    std::cout << "Vertices are numbered from 0 to " << n - 1 << ".\n";
    std::cout << "Enter edges. Type -1 to finish.\n";

    std::regex num_r("^(-1|[0-9]+)$");
    while (true) {
        std::string input;
        std::cout << "  From (-1 to finish): ";
        std::cin >> input;

        if (input == "-1") break;

        if (!std::regex_match(input, num_r) || std::stoi(input) >= n) {
            std::cout << "Incorrect input!\n";
            continue;
        }
        int u = std::stoi(input);

        std::cout << "  To   (-1 to finish): ";
        std::cin >> input;

        if (input == "-1") break;

        if (!std::regex_match(input, num_r) || std::stoi(input) >= n) {
            std::cout << "Incorrect input!\n";
            continue;
        }
        int v = std::stoi(input);

        if (u == v) {
            std::cout << "Self-loops are not allowed!\n";
            continue;
        }

        gr.addEdge(u, v);
        std::cout << "  Edge " << u << " -> " << v << " added.\n";
    }

    // --- Start vertex ---
    int start = readInt("Enter start vertex (0 to " + std::to_string(n - 1) + "): ", 0, n - 1);

    // --- End vertex ---
    int end = readInt("Enter end vertex (0 to " + std::to_string(n - 1) + "): ", 0, n - 1, start);

    // --- Find and print shortest path ---
    std::vector<int> path = gr.bfsShortestPath(start, end);

    if (path.empty()) {
        std::cout << "No path from " << start << " to " << end << ".\n";
    } else {
        std::cout << "Shortest path (" << path.size() - 1 << " edges): ";
        for (int i = 0; i < (int)path.size(); i++) {
            if (i > 0) std::cout << " -> ";
            std::cout << path[i];
        }
        std::cout << "\n";
    }

    return 0;
}