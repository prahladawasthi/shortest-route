package com.shortestroute.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shortestroute.data.processor.ExcelDataProcessor;
import com.shortestroute.dto.EdgeModel;
import com.shortestroute.dto.TrafficInfoModel;
import com.shortestroute.helper.Graph;
import com.shortestroute.helper.GraphMapper;
import com.shortestroute.model.Edge;
import com.shortestroute.model.TrafficInfo;
import com.shortestroute.model.Vertex;
import com.shortestroute.repository.EdgeRepository;
import com.shortestroute.repository.TrafficInfoRepository;
import com.shortestroute.repository.VertexRepository;

@Service
public class ImportDataService {

    private VertexRepository vertexRepository;
    private EdgeRepository edgeRepository;
    private TrafficInfoRepository trafficInfoRepository;
    private ExcelDataProcessor xlsxProcessor;

    @Autowired
    public ImportDataService(VertexRepository vertexRepository, 
                                EdgeRepository edgeRepository, 
                                TrafficInfoRepository trafficInfoRepository,
                                ExcelDataProcessor xlsxProcessor) {
        this.vertexRepository = vertexRepository;
        this.edgeRepository = edgeRepository;
        this.trafficInfoRepository = trafficInfoRepository;
        this.xlsxProcessor = xlsxProcessor;
    }

    @SuppressWarnings("unchecked")
    public void readExcelFileAndImportIntoDatabase() {
        Map<String, Edge> edgeMap = new LinkedHashMap();

        Map<String, Vertex> vertexMap = new LinkedHashMap<>(xlsxProcessor.readVertexes());
        List<EdgeModel> edges = new ArrayList<>(xlsxProcessor.readEdges());
        List<TrafficInfoModel> traffics = new ArrayList<>(xlsxProcessor.readTraffics());

        for (EdgeModel edgeModel : edges) {
            GraphMapper mapper = new GraphMapper(vertexMap, edgeModel);
            if (mapper.getSource() != null && mapper.getDestination() != null) {
                Edge edge = new Edge(mapper.toStringMessage(edgeModel.getSource(), edgeModel.getDestination()), edgeModel.getWeight());
                mapper.getSource().addSourceEdges(edge);
                mapper.getDestination().addDestinationEdges(edge);
                edgeMap.put(mapper.toStringMessage(edgeModel.getSource(), edgeModel.getDestination()), edge);
            }
        }

        for (TrafficInfoModel trafficModel : traffics) {
            GraphMapper mapper = new GraphMapper(edgeMap, trafficModel);
            if (mapper.getEdge() != null) {
                TrafficInfo traffic = new TrafficInfo(mapper.toStringMessage(trafficModel.getSource(), trafficModel.getDestination()), trafficModel.getWeight());
                mapper.getEdge().addTrafficInfo(traffic);
            }
        }

        for (Vertex vertex : vertexMap.values()) {
            vertexRepository.save(vertex);
        }
    }


    public Graph selectGraph() {
        List<Vertex> vertices = vertexRepository.selectAll();
        List<Edge> edges = edgeRepository.selectAll();
        List<TrafficInfo> traffics = trafficInfoRepository.selectAllLazyLoading();

        return new Graph(vertices, edges, traffics);
    }

    public Vertex saveVertex(Vertex vertex) {
        vertexRepository.save(vertex);
        return vertex;
    }

    public Vertex updateVertex(Vertex vertex) {
        vertexRepository.update(vertex);
        return vertex;
    }

    public boolean deleteVertex(String id) {
        Vertex vertex = vertexRepository.selectUniqueLazyLoad(id);
        vertexRepository.delete(vertex);
        return true;
    }

    public List<Vertex> getAllVertices() {
        return vertexRepository.selectAll();
    }

    public Vertex getVertexByName(String name) {
        return vertexRepository.selectUniqueByName(name);
    }

    public Vertex getVertexById(String vertexId) {
        return vertexRepository.selectUnique(vertexId);
    }

    public boolean vertexExist(String vertexId) {
        Vertex vertex = vertexRepository.selectUnique(vertexId);
        return vertex != null;
    }

    public Edge saveEdge(Edge edge) {
        edgeRepository.save(edge);
        return edge;
    }

    public Edge updateEdge(Edge edge) {
        edgeRepository.update(edge);
        return edge;
    }

    public boolean deleteEdge(long id) {
        Edge edge = edgeRepository.selectUniqueLazyLoad(id);
        edgeRepository.delete(edge);
        return true;
    }

    public List<Edge> getAllEdges() {
        return edgeRepository.selectAll();
    }

    public List<Edge> getAllUnusedEdges() {
        return edgeRepository.selectAllUnusedEdges();
    }

    public Edge getEdgeById(Long id) {
        return edgeRepository.selectUnique(id);
    }

    public boolean edgeExists(Edge edge) {
        List<Edge> edges = edgeRepository.edgeExists(edge);
        return !edges.isEmpty();
    }

    public Edge findEdgeBetweenVertexes(Vertex source, Vertex destination) {
        return edgeRepository.findEdgeBetweenVertexes(source, destination);
    }

    public TrafficInfo saveTraffic(TrafficInfo traffic) {
        trafficInfoRepository.save(traffic);
        return traffic;
    }

    public TrafficInfo updateTraffic(TrafficInfo traffic) {
        trafficInfoRepository.update(traffic);
        return traffic;
    }

    public boolean deleteTraffic(Long id) {
        TrafficInfo traffic = trafficInfoRepository.selectUnique(id);
        trafficInfoRepository.delete(traffic);
        return true;
    }

    public List<TrafficInfo> getAllTrafficInfos() {
        return trafficInfoRepository.selectAllLazyLoading();
    }

    public TrafficInfo getTrafficById(Long id) {
        return trafficInfoRepository.selectUnique(id);
    }
}
