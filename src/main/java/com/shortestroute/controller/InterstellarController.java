package com.shortestroute.controller;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.shortestroute.dto.ShortestRouteModel;
import com.shortestroute.helper.Graph;
import com.shortestroute.helper.GraphMapper;
import com.shortestroute.model.Edge;
import com.shortestroute.model.TrafficInfo;
import com.shortestroute.model.Vertex;
import com.shortestroute.service.ImportDataService;
import com.shortestroute.service.ShortestPathService;

@Controller
public class InterstellarController {

    private static final String PATH_NOT_AVAILABLE = "Path not available.";
    private static final String PATH_NOT_NEEDED = "Not needed. You are already on planet ";
    private static final String NO_PLANET_FOUND = "No planet found.";
    private static final String DUPLICATE_ROUTE = "You cannot link a route to itself.";
    private static final String VALIDATION_PAGE = "validation";

    private ImportDataService importDataService;
    private ShortestPathService shortestPathService;

    public InterstellarController() {
    }
    
    @Autowired
    public InterstellarController(ImportDataService importDataService, ShortestPathService shortestPathService) {
        this.importDataService = importDataService;
        this.shortestPathService = shortestPathService;
    }

    @RequestMapping(value = "/vertices", method = RequestMethod.GET)
    public String listVertices(Model model) {
        List vertices = importDataService.getAllVertices();
        model.addAttribute("vertices", vertices);
        return "vertex/vertices";
    }

    @RequestMapping(value = "save_vertex", method = RequestMethod.POST)
    public String saveVertex(Vertex vertex, Model model) {
        if (importDataService.vertexExist(vertex.getId())) {
            buildVertexValidation(vertex.getId(), model);
            return VALIDATION_PAGE;
        }
        importDataService.saveVertex(vertex);
        return "redirect:/vertex/" + vertex.getId();
    }

    private void buildVertexValidation(String vertexId, Model model) {
        String vertexName = importDataService.getVertexById(vertexId) == null ? "" : importDataService.getVertexById(vertexId).getName();
        String message = "Planet " + vertexId + " already exists as " + vertexName;
        model.addAttribute("validationMessage", message);
    }

    @RequestMapping(value = "update_vertex", method = RequestMethod.POST)
    public String updateVertex(Vertex vertex) {
        importDataService.updateVertex(vertex);
        return "redirect:/vertex/" + vertex.getId();
    }

    @RequestMapping(value = "/edges", method = RequestMethod.GET)
    public String listEdges(Model model) {
        List edges = importDataService.getAllEdges();
        model.addAttribute("edges", edges);
        return "route/edges";
    }

    @RequestMapping(value = "save_edge", method = RequestMethod.POST)
    public String saveEdge(Edge edge, @ModelAttribute ShortestRouteModel pathModel, Model model) {
        GraphMapper mapper = new GraphMapper();
        edge.setSource(pathModel.getSourceVertex());
        edge.setDestination(pathModel.getDestinationVertex());

        if (pathModel.getSourceVertex().equals(pathModel.getDestinationVertex())) {
            model.addAttribute("validationMessage", DUPLICATE_ROUTE);
            return VALIDATION_PAGE;
        }
        if (importDataService.edgeExists(edge)) {
            buildEdgeValidation(pathModel, model);
            return VALIDATION_PAGE;
        }
        edge.setRouteId(mapper.toStringMessage(edge.getSource().getId(), edge.getDestination().getId()));
        importDataService.saveEdge(edge);
        return "redirect:/edge/" + edge.getId();
    }

    private void buildEdgeValidation(@ModelAttribute ShortestRouteModel pathModel, Model model) {
        String message = "The route from " + pathModel.getSourceVertex().getName() + " (" + pathModel.getSourceVertex().getId() + ") to " + pathModel.getDestinationVertex().getName() + "(" + pathModel.getDestinationVertex().getId() + ") exists already.";
        model.addAttribute("validationMessage", message);
    }

    @RequestMapping(value = "update_edge", method = RequestMethod.POST)
    public String updateEdge(@ModelAttribute Edge edge, @ModelAttribute ShortestRouteModel pathModel, Model model) {
        GraphMapper mapper = new GraphMapper();
        edge.setSource(pathModel.getSourceVertex());
        edge.setDestination(pathModel.getDestinationVertex());
        if (pathModel.getSourceVertex().equals(pathModel.getDestinationVertex())) {
            model.addAttribute("validationMessage", DUPLICATE_ROUTE);
            return VALIDATION_PAGE;
        }

        if (importDataService.edgeExists(edge)) {
            buildEdgeValidation(pathModel, model);
            return VALIDATION_PAGE;
        }
        edge.setRouteId(mapper.toStringMessage(edge.getSource().getId(), edge.getDestination().getId()));
        importDataService.updateEdge(edge);
        return "redirect:/edge/" + edge.getId();
    }

    @RequestMapping(value = "/traffics", method = RequestMethod.GET)
    public String listTraffics(Model model) {
        List allTraffics = importDataService.getAllTrafficInfos();
        model.addAttribute("trafficInfos", allTraffics);
        return "traffic/traffics";
    }

    @RequestMapping(value = "save_traffic", method = RequestMethod.POST)
    public String saveTraffic(TrafficInfo traffic, @ModelAttribute ShortestRouteModel pathModel) {
        GraphMapper mapper = new GraphMapper();
        traffic.setRoute(pathModel.getSelectedEdge());
        traffic.setRouteId(mapper.toStringMessage(traffic.getRoute().getSource().getId(), traffic.getRoute().getDestination().getId()));
        importDataService.saveTraffic(traffic);
        return "redirect:/traffic/" + traffic.getId();
    }

    @RequestMapping(value = "update_traffic", method = RequestMethod.POST)
    public String updateTraffic(TrafficInfo traffic, @ModelAttribute ShortestRouteModel pathModel) {
        importDataService.updateTraffic(traffic);
        return "redirect:/traffic/" + traffic.getId();
    }

    @RequestMapping(value = "/shortest", method = RequestMethod.GET)
    public String shortestForm(Model model) {
        ShortestRouteModel shortestRouteModel = new ShortestRouteModel();
        List<Vertex> vertices = importDataService.getAllVertices();
        if (vertices == null || vertices.isEmpty()) {
            model.addAttribute("validationMessage", NO_PLANET_FOUND);
            return VALIDATION_PAGE;
        }
        Vertex origin = vertices.get(0);
        shortestRouteModel.setVertexName(origin.getName());
        shortestRouteModel.setVertexId(origin.getId());
        model.addAttribute("shortest", shortestRouteModel);
        model.addAttribute("pathList", vertices);
        return "shortest";
    }

    @RequestMapping(value = "/shortest", method = RequestMethod.POST)
    public String shortestSubmit(@ModelAttribute ShortestRouteModel pathModel, Model model) {

        StringBuilder path = new StringBuilder();
        String distanceTravelled = "";
        Graph graph = importDataService.selectGraph();
        if (pathModel.isTrafficAllowed()) {
            graph.setTrafficAllowed(true);
        }
        if (pathModel.isUndirectedGraph()) {
            graph.setUndirectedGraph(true);
        }
        Vertex source = importDataService.getVertexById(pathModel.getVertexId());
        Vertex destination = pathModel.getSelectedVertex();
        //
        Map<Vertex, Vertex> previousPaths = shortestPathService.run(graph, source);
        LinkedList<Vertex> paths = shortestPathService.getPath(previousPaths, destination);
        Float distance = (float) 0;
        if (paths != null) {
            int i = 0;
            for (Vertex v : paths) {
                if(!v.equals(paths.getLast())) {
                    Edge edge = importDataService.findEdgeBetweenVertexes(paths.get(i), paths.get(i+1));
                    distance = distance + edge.getDistance();
                    i += 1;
                }
                path.append(v.getName()).append(" (").append(v.getId()).append(") ");
                distanceTravelled = "Distance travelled = " + roundOff(distance) + " (Light Years)";
            }
        } else if (source != null && destination != null && source.getId().equals(destination.getId())) {
            path.append(PATH_NOT_NEEDED).append(source.getName());
        } else {
            path.append(PATH_NOT_AVAILABLE);
        }
        pathModel.setThePath(path.toString());
        pathModel.setDistanceTravelled(distanceTravelled);
        pathModel.setSelectedVertexName(destination == null ? "" : destination.getName());
        model.addAttribute("shortest", pathModel);
        return "result";
    }

    private float roundOff(float d) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
