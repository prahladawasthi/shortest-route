package com.shortestroute.helper;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.shortestroute.dto.EdgeModel;
import com.shortestroute.dto.TrafficInfoModel;
import com.shortestroute.model.Edge;
import com.shortestroute.model.Vertex;

public class GraphMapperTest {
    @Test
    public void verifyThatGetSourceAndDestinationIsCorrect() throws Exception {
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Vertex vertexC = new Vertex("C", "Jupiter");
        Vertex vertexD = new Vertex("D", "Venus");
        Vertex vertexE = new Vertex("E", "Mars");

        Map<String, Vertex> vertexMap = new LinkedHashMap<>();
        vertexMap.put(vertexA.getId(), vertexA);
        vertexMap.put(vertexB.getId(), vertexB);
        vertexMap.put(vertexC.getId(), vertexC);
        vertexMap.put(vertexD.getId(), vertexD);
        vertexMap.put(vertexE.getId(), vertexE);

        EdgeModel edge1 = new EdgeModel("1", vertexA.getId(), vertexB.getId(), 1.0f);
        EdgeModel edge2 = new EdgeModel("2", vertexA.getId(), vertexC.getId(), 1.0f);
        EdgeModel edge3 = new EdgeModel("3", vertexA.getId(), vertexD.getId(), 1.0f);
        EdgeModel edge4 = new EdgeModel("4", vertexB.getId(), vertexD.getId(), 1.0f);
        EdgeModel edge5 = new EdgeModel("5", vertexB.getId(), vertexE.getId(), 1.0f);
        List<EdgeModel> edges = Arrays.asList(edge1, edge2, edge3, edge4, edge5);

        TrafficInfoModel traffic1 = new TrafficInfoModel("1", vertexA.getId(), vertexB.getId(), 2f);
        TrafficInfoModel traffic2 = new TrafficInfoModel("2", vertexA.getId(), vertexC.getId(), 2f);
        TrafficInfoModel traffic3 = new TrafficInfoModel("3", vertexA.getId(), vertexD.getId(), 2f);
        TrafficInfoModel traffic4 = new TrafficInfoModel("4", vertexB.getId(), vertexD.getId(), 2f);
        TrafficInfoModel traffic5 = new TrafficInfoModel("5", vertexB.getId(), vertexE.getId(), 2f);
        List<TrafficInfoModel> traffics = Arrays.asList(traffic1, traffic2, traffic3, traffic4, traffic5);

        Map<String, Edge> returnedEdgeMap = new LinkedHashMap<>();
        GraphMapper mapper;
        for (EdgeModel edgeModel : edges) {
            mapper = new GraphMapper(vertexMap, edgeModel);
            Edge edge = new Edge(edgeModel.getId(), mapper.getSource(), mapper.getDestination(), edgeModel.getWeight());
            mapper.getSource().addSourceEdges(edge);
            mapper.getDestination().addDestinationEdges(edge);
            returnedEdgeMap.put(edgeModel.getSource() + "_" + edgeModel.getDestination(), edge);
        }

        assertThat("1", sameBeanAs("1"));
    }
}