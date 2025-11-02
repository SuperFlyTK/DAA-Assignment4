#  Smart Campus Scheduling - Graph Algorithm Analysis

## Project Overview

This project implements and analyzes graph algorithms for solving scheduling problems in a smart campus environment. The system handles task dependencies, detects circular dependencies, and finds optimal scheduling paths using three core algorithms.

## Architecture

### Core Algorithms Implemented

1. **Strongly Connected Components (SCC)** - Tarjan's Algorithm
2. **Topological Sort** - Kahn's Algorithm
3. **Critical Path Analysis** - DAG Longest Paths

### Key Features

- **Cycle Detection**: Identifies circular dependencies in task graphs
- **Dependency Resolution**: Compresses cycles into manageable units
- **Optimal Scheduling**: Finds critical paths for resource allocation
- **Performance Metrics**: Comprehensive operation counting and timing

## Performance Analysis

### Algorithm Performance Summary

| Algorithm | Time Complexity | Space Complexity | Best Use Case |
|-----------|-----------------|------------------|---------------|
| **SCC** | O(V + E) | O(V) | Cycle detection in unknown graphs |
| **Topological Sort** | O(V + E) | O(V) | Task ordering in DAGs |
| **Critical Path** | O(V + E) | O(V) | Resource scheduling optimization |

### Real Performance Metrics (tasks.json)

| Algorithm | Time (ns) | Operations | Key Metrics |
|-----------|-----------|------------|-------------|
| SCC | 625,400 | 49 | 8 DFS visits, 7 edge relaxations |
| Topological Sort | 566,600 | 26 | 20 Kahn operations |
| Critical Path | 448,400 | 132 | 7 edge relaxations |

## Dataset Structure

### Component Analysis
```
Component_ID, Size, Nodes
0, 1, [0]
1, 3, [1, 2, 3]  ← Cyclic component
2, 1, [4]
3, 1, [5]
4, 1, [6]
5, 1, [7]
```

### Test Dataset Categories

- **Small**: 6-10 nodes, 8-14 edges
- **Medium**: 10-20 nodes, 15-35 edges
- **Large**: 20-50 nodes, 40-90 edges

## Key Findings

### Structural Impact on Performance

1. **Graph Density**
    - Sparse graphs: Faster DFS completion
    - Dense graphs: Increased edge processing overhead

2. **SCC Distribution**
    - Many small components: Faster convergence
    - Few large components: Complex DFS traversal

3. **Cycle Complexity**
    - Acyclic graphs: Direct topological sorting
    - Cyclic graphs: Requires condensation preprocessing

### Algorithm Bottlenecks

- **SCC**: DFS stack operations and lowlink updates
- **Topological Sort**: Queue operations and in-degree calculations
- **Critical Path**: Edge relaxations on condensation graph

## Practical Recommendations

### When to Use Each Algorithm

| Scenario | Recommended Approach | Reason |
|----------|---------------------|--------|
| **Unknown Dependencies** | SCC → Topo Sort → Critical Path | Comprehensive analysis |
| **Known Acyclic** | Direct Topo Sort → Critical Path | Skip unnecessary SCC |
| **Cycle Detection Only** | SCC Algorithm | Focused analysis |
| **Resource Planning** | Critical Path Analysis | Optimization focus |

### Performance Optimization Strategies

1. **Preprocessing**: Use condensation graphs for cyclic inputs
2. **Early Termination**: Detect cycles during topological sort
3. **Caching**: Store condensation graphs for repeated analysis
4. **Incremental Updates**: Update rather than recompute for dynamic graphs

## Usage Guide

### For Smart Campus Scheduling

1. **Initial Setup**: Load task dependencies as directed graph
2. **Dependency Analysis**: Run SCC to detect circular dependencies
3. **Cycle Resolution**: Compress cycles into single scheduling units
4. **Task Ordering**: Apply topological sort on condensation graph
5. **Optimization**: Use critical path analysis for resource allocation

### Implementation Pipeline

```java
// 1. Detect cycles and create condensation graph
StronglyConnectedComponents scc = new StronglyConnectedComponents(graph);
List<List<Integer>> components = scc.findSCCs();
Graph condensationGraph = scc.getCondensationGraph();

// 2. Topologically sort the condensation graph  
TopologicalSort topo = new TopologicalSort(condensationGraph);
List<Integer> topologicalOrder = topo.sort();

// 3. Find critical path for optimal scheduling
CriticalPath criticalPath = new CriticalPath(condensationGraph);
int longestPath = criticalPath.findLongestPath();
```

## Results Interpretation

### Metrics to Monitor

- **DFS Visits**: Indicates graph traversal complexity
- **Edge Relaxations**: Shows path optimization workload
- **Kahn Operations**: Measures topological sorting overhead
- **Total Operations**: Overall algorithm complexity indicator

### Performance Scaling

Algorithms scale linearly with graph size (O(V+E)), but actual performance depends on:
- Graph density and connectivity
- SCC size distribution
- Presence of long dependency chains
- Weight distribution patterns

## Future Enhancements

1. **Parallel Processing**: Implement concurrent SCC detection
2. **Incremental Algorithms**: Support dynamic graph updates
3. **Visualization**: Graph and critical path visualization tools
4. **Machine Learning**: Predictive scheduling based on historical data

## Conclusion

This project demonstrates that effective campus scheduling requires a multi-algorithm approach: SCC for robustness, topological sort for efficiency, and critical path analysis for optimization. The implemented solution provides a complete pipeline for handling real-world scheduling constraints with circular dependencies.

The performance analysis shows that while all algorithms maintain linear complexity, careful algorithm selection based on graph structure can significantly improve practical performance for smart campus applications.

---
**Report Generated**: Comprehensive analysis of graph algorithms for scheduling  
**Data Source**: Multiple JSON test datasets with varying structures  
**Analysis Period**: Performance metrics collected across algorithm implementations  
**Validation**: JUnit test suite verification and cross-algorithm consistency checks