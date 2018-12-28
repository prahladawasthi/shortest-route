package com.shortestroute.helper;

import java.util.Map;

import com.shortestroute.dto.EdgeModel;
import com.shortestroute.dto.TrafficInfoModel;
import com.shortestroute.model.Edge;
import com.shortestroute.model.Vertex;

public class GraphMapper {
    private Map<String, Vertex> vertexMap;
    private Map<String, Edge> edgeMap;
    private EdgeModel edgeModel;
    private TrafficInfoModel trafficModel;
    private Vertex source;
    private Vertex destination;
    private Edge edge;

    public GraphMapper() {
    }

    public GraphMapper(Map<String, Vertex> vertexMap, EdgeModel edgeModel) {
        this.vertexMap = vertexMap;
        this.edgeModel = edgeModel;
        mapVertices();
    }

    public GraphMapper(Map<String, Edge> edgeMap, TrafficInfoModel trafficModel) {
        this.edgeMap = edgeMap;
        this.trafficModel = trafficModel;
        mapEdges();
    }

    private void mapVertices() {
        source = vertexMap.get(edgeModel.getSource());
        destination = vertexMap.get(edgeModel.getDestination());
    }

    private void mapEdges() {
        edge = edgeMap.get(toStringMessage(trafficModel.getSource(), trafficModel.getDestination()));
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Edge getEdge() {
        return edge;
    }

    public String toStringMessage(String source, String destination) {
        return source +
                "_" +
                destination;
    }
}
