package com.shortestroute.service;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.shortestroute.helper.Graph;
import com.shortestroute.model.Edge;
import com.shortestroute.model.TrafficInfo;
import com.shortestroute.model.Vertex;

public class PathServiceTest {
    @Test
    public void verifyThatTrafficOverlayOnGraphIsCorrect() throws Exception {
        //Set
        List<Vertex> vertices = new ArrayList<>();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Venus");

        Edge edge1 = new Edge("1", vertex1, vertex2, 1.5f);
        Edge edge2 = new Edge("2", vertex2, vertex3, 2.5f);
        Edge edge3 = new Edge("3", vertex1, vertex3, 3.5f);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);

        TrafficInfo traffic1 = new TrafficInfo("1", edge1, 0.5f);
        TrafficInfo traffic2 = new TrafficInfo("2", edge2, 1.0f);
        TrafficInfo traffic3 = new TrafficInfo("3", edge3, 1.5f);

        List<TrafficInfo> traffics = new ArrayList<>();
        traffics.add(traffic1);
        traffics.add(traffic2);
        traffics.add(traffic3);

        Edge edgeExpected1 = new Edge("1", vertex1, vertex2, 2.0f);
        Edge edgeExpected2 = new Edge("2", vertex2, vertex3, 3.5f);
        Edge edgeExpected3 = new Edge("3", vertex1, vertex3, 5.0f);
        List<Edge> edgesExpected = new ArrayList<>();
        edgesExpected.add(edgeExpected1);
        edgesExpected.add(edgeExpected2);
        edgesExpected.add(edgeExpected3);
        boolean expectedTraffic = true;
        Graph expectedGraph = new Graph(vertices, edgesExpected, traffics);
        expectedGraph.setTrafficAllowed(expectedTraffic);


        //Test
        Graph actualGraph = new Graph(vertices, edges, traffics);
        actualGraph.setTrafficAllowed(true);
        PathService pathImplementation = new PathService();
        actualGraph = pathImplementation.overlayGraph(actualGraph);
        boolean actualTraffic = actualGraph.isTrafficAllowed();

        List<Vertex> verticesExpected = expectedGraph.getVertexes();
        List<TrafficInfo> trafficsExpected = expectedGraph.getTrafficInfos();
        //Verify
        assertThat(actualGraph, sameBeanAs(expectedGraph));
        assertThat(actualGraph, sameBeanAs(expectedGraph));
        assertThat(vertices, sameBeanAs(verticesExpected));
        assertThat(traffics, sameBeanAs(trafficsExpected));
        assertThat(actualTraffic, sameBeanAs(expectedTraffic));
    }

    @Test
    public void verifyThatUndirectedEdgesOnGraphIsCorrect() throws Exception {
        //Set
        List<Vertex> vertices = new ArrayList<>();
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Mars");
        Vertex vertexC = new Vertex("C", "Venus");
        Vertex vertexD = new Vertex("D", "Dream Team");

        Edge edge1 = new Edge("1", vertexA, vertexB, 0.44f);
        Edge edge2 = new Edge("2", vertexA, vertexC, 1.89f);
        Edge edge3 = new Edge("3", vertexA, vertexD, 0.10f);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);

        TrafficInfo traffic1 = new TrafficInfo("1", edge1, 0.30f);
        TrafficInfo traffic2 = new TrafficInfo("2", edge2, 0.90f);
        TrafficInfo traffic3 = new TrafficInfo("3", edge3, 0.10f);

        List<TrafficInfo> traffics = new ArrayList<>();
        traffics.add(traffic1);
        traffics.add(traffic2);
        traffics.add(traffic3);

        boolean expectedUndirected = true;

        //Test
        Graph graph = new Graph(vertices, edges, traffics);
        graph.setUndirectedGraph(true);
        PathService pathImplementation = new PathService();
        graph = pathImplementation.overlayGraph(graph);
        List<Edge> actualEdges = graph.getEdges();
        boolean actualUndirected = graph.isUndirectedGraph();

        Graph actualGraph = new Graph(vertices, actualEdges, traffics);


        Edge edgeExpected1 = new Edge("1", vertexA, vertexB, 0.44f);
        Edge edgeExpected2 = new Edge("1", vertexB, vertexA, 0.44f);
        Edge edgeExpected3 = new Edge("2", vertexA, vertexC, 1.89f);
        Edge edgeExpected4 = new Edge("2", vertexC, vertexA, 1.89f);
        Edge edgeExpected5 = new Edge("3", vertexA, vertexD, 0.10f);
        Edge edgeExpected6 = new Edge("3", vertexD, vertexA, 0.10f);
        List<Edge> edgesExpected = new ArrayList<>();
        edgesExpected.add(edgeExpected1);
        edgesExpected.add(edgeExpected2);
        edgesExpected.add(edgeExpected3);
        edgesExpected.add(edgeExpected4);
        edgesExpected.add(edgeExpected5);
        edgesExpected.add(edgeExpected6);

        Graph expectedGraph = new Graph(vertices, edgesExpected, traffics);

        //Verify
        assertThat(actualEdges, sameBeanAs(edgesExpected));
        assertThat(actualGraph, sameBeanAs(expectedGraph));
        assertEquals(actualUndirected, expectedUndirected);
    }

}