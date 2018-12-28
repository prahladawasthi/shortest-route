package com.shortestroute.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.shortestroute.helper.Graph;
import com.shortestroute.model.Edge;
import com.shortestroute.model.TrafficInfo;
import com.shortestroute.model.Vertex;


@Service
public class PathService {

    public Graph overlayGraph(Graph graph) {
        List<Edge> edges = new ArrayList<>(graph.getEdges());
        List<TrafficInfo> traffics = new ArrayList<>(graph.getTrafficInfos());
        if (graph.isTrafficAllowed()) {
            edges = processTraffics(edges, traffics);
        }
        if (graph.isUndirectedGraph()) {
            edges = getUndirectedEdges(edges);
        }
        graph.setEdges(edges);
        return graph;
    }

    private List<Edge> processTraffics(List<Edge> edges, List<TrafficInfo> traffics) {
        for (TrafficInfo traffic : traffics) {
            edges.stream().filter(edge -> edge.equals(traffic.getRoute())).forEach(edge -> {
                Float actualDistance = edge.getDistance() + traffic.getDelay();
                edge.setDistance(actualDistance);
            });
        }
        return edges;
    }

    @SuppressWarnings("unchecked")
    private List<Edge> getUndirectedEdges(List<Edge> edges) {
        List<Edge> undirectedEdges = new ArrayList();
        for (Edge fromEdge : edges) {
            Edge toEdge = copyAdjacentEdge(fromEdge);
            undirectedEdges.add(fromEdge);
            undirectedEdges.add(toEdge);
        }
        return undirectedEdges;
    }

    private Edge copyAdjacentEdge(Edge fromEdge) {
        Edge toEdge = new Edge();
        toEdge.setRouteId(fromEdge.getRouteId());
        toEdge.setSource(fromEdge.getDestination());
        toEdge.setDestination(fromEdge.getSource());
        toEdge.setDistance(fromEdge.getDistance());
        return toEdge;
    }

    public Vertex getVertexWithLowestDistance(Map<Vertex, Float> distance, Set<Vertex> vertexes) {
        Vertex lowestVertex = null;
        for (Vertex vertex : vertexes) {
            if (lowestVertex == null) {
                lowestVertex = vertex;
            } else if (getShortestDistance(distance, vertex) < getShortestDistance(distance, lowestVertex)) {
                lowestVertex = vertex;
            }
        }
        return lowestVertex;
    }

    public Float getShortestDistance(Map<Vertex, Float> distance, Vertex destination) {
        Float d = distance.get(destination);
        if (d == null) {
            return Float.POSITIVE_INFINITY;
        } else {
            return d;
        }
    }

    public float getDistance(List<Edge> edges, Vertex source, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(source) && edge.getDestination().equals(target)) {
                return edge.getDistance();
            }
        }
        throw new RuntimeException("Error: Something went wrong!");
    }

    public List<Vertex> getNeighbors(List<Edge> edges, Set<Vertex> visitedVertices, Vertex currentVertex) {
        List<Vertex> neighbors = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(currentVertex) && !isVisited(visitedVertices, edge.getDestination())) {
                neighbors.add(edge.getDestination());
            }
        }
        return neighbors;
    }

    private boolean isVisited(Set<Vertex> visitedVertices, Vertex vertex) {
        return visitedVertices.contains(vertex);
    }
}
