package com.shortestroute.data.processor;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.shortestroute.config.Resource;
import com.shortestroute.dto.EdgeModel;
import com.shortestroute.dto.TrafficInfoModel;
import com.shortestroute.model.Vertex;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ExcelDataProcessor.class, Resource.class},
        loader = AnnotationConfigContextLoader.class)
public class ExcelDataProcessorTest {
    
    @Autowired
    private ExcelDataProcessor excelDataProcessor;

    @Test
    public void verifyThatReadingVerticesFromFileIsCorrect() throws Exception {
        //Set
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Moon");
        Vertex vertex3 = new Vertex("C", "Jupiter");
        Vertex vertex4 = new Vertex("D", "Venus");
        Vertex vertex5 = new Vertex("E", "Mars");

        Map<String, Vertex> expectedVertexes = new HashMap<>();
        expectedVertexes.put(vertex1.getId(), vertex1);
        expectedVertexes.put(vertex2.getId(), vertex2);
        expectedVertexes.put(vertex3.getId(), vertex3);
        expectedVertexes.put(vertex4.getId(), vertex4);
        expectedVertexes.put(vertex5.getId(), vertex5);

        //Test
        Map<String, Vertex> readVertexes = excelDataProcessor.readVertexes();

        //Verify
        assertThat(expectedVertexes, sameBeanAs(readVertexes));
    }

    @Test
    public void verifyThatReadingEdgesFromFileIsCorrect() throws Exception {
        //Set
        EdgeModel edge1 = new EdgeModel("1", "A", "B", 0.44f);
        EdgeModel edge2 = new EdgeModel("2", "A", "C", 1.89f);
        EdgeModel edge3 = new EdgeModel("3", "A", "D", 0.10f);
        EdgeModel edge4 = new EdgeModel("4", "B", "E", 2.44f);
        EdgeModel edge5 = new EdgeModel("5", "C", "E", 3.45f);

        List<EdgeModel> expectedEdges = new ArrayList<>();
        expectedEdges.add(edge1);
        expectedEdges.add(edge2);
        expectedEdges.add(edge3);
        expectedEdges.add(edge4);
        expectedEdges.add(edge5);

        //Test
        List<EdgeModel> readEdges = excelDataProcessor.readEdges();

        //Verify
        assertThat(expectedEdges, sameBeanAs(readEdges));
    }

    @Test
    public void verifyThatReadingTrafficsFromFileIsCorrect() throws Exception {
        //Set
        TrafficInfoModel traffic1 = new TrafficInfoModel("1", "A", "B", 0.30f);
        TrafficInfoModel traffic2 = new TrafficInfoModel("2", "A", "C", 0.90f);
        TrafficInfoModel traffic3 = new TrafficInfoModel("3", "A", "D", 0.10f);
        TrafficInfoModel traffic4 = new TrafficInfoModel("4", "B", "E", 0.20f);
        TrafficInfoModel traffic5 = new TrafficInfoModel("5", "C", "E", 1.30f);

        List<TrafficInfoModel> expectedTraffics = new ArrayList<>();
        expectedTraffics.add(traffic1);
        expectedTraffics.add(traffic2);
        expectedTraffics.add(traffic3);
        expectedTraffics.add(traffic4);
        expectedTraffics.add(traffic5);

        //Test
        List<TrafficInfoModel> readTraffics = excelDataProcessor.readTraffics();

        //Verify
        assertThat(expectedTraffics, sameBeanAs(readTraffics));
    }
}