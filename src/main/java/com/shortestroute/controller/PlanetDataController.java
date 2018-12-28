package com.shortestroute.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.shortestroute.dto.ShortestRouteModel;
import com.shortestroute.model.Edge;
import com.shortestroute.model.TrafficInfo;
import com.shortestroute.model.Vertex;
import com.shortestroute.service.ImportDataService;
import com.shortestroute.service.ShortestPathService;

@Controller
public class PlanetDataController {

    private ImportDataService importDataService;
    private ShortestPathService shortestPathService;

    public PlanetDataController() {
    }

    @Autowired
    public PlanetDataController(ImportDataService importDataService, ShortestPathService shortestPathService) {
        this.importDataService = importDataService;
        this.shortestPathService = shortestPathService;
    }

    @RequestMapping("vertex/{id}")
    public String viewVertex(@PathVariable String id, Model model) {
        model.addAttribute("vertex", importDataService.getVertexById(id));
        return "vertex/vertexView";
    }
    
    @RequestMapping("vertex/add")
    public String addVertex(Model model) {
        model.addAttribute("vertex", new Vertex());
        return "vertex/vertexAdd";
    }

    @RequestMapping("vertex/edit/{id}")
    public String editVertex(@PathVariable String id, Model model) {
        model.addAttribute("vertex", importDataService.getVertexById(id));
        return "vertex/vertexUpdate";
    }
    @RequestMapping("vertex/delete/{vertexId}")
    public String deleteVertex(@PathVariable String vertexId) {
        importDataService.deleteVertex(vertexId);
        return "redirect:/vertices";
    }
    @RequestMapping("edge/{id}")
    public String viewEdge(@PathVariable long id, Model model) {
        model.addAttribute("edge", importDataService.getEdgeById(id));
        return "route/edgeView";
    }

    @RequestMapping(value = "edge/add", method = RequestMethod.GET)
    public String addEdge(Model model) {
        ShortestRouteModel sh = new ShortestRouteModel();
        List vertices = importDataService.getAllVertices();
        model.addAttribute("edge", new Edge());
        model.addAttribute("edgeModel", sh);
        model.addAttribute("routeList", vertices);
        return "route/edgeAdd";
    }

    @RequestMapping(value = "edge/edit/{id}", method = RequestMethod.GET)
    public String editEdge(@PathVariable Long id, Model model) {
        ShortestRouteModel pathModel = new ShortestRouteModel();
        List vertices = importDataService.getAllVertices();
        Edge edgeToEdit = importDataService.getEdgeById(id);
        pathModel.setSourceVertex(edgeToEdit.getSource());
        pathModel.setDestinationVertex(edgeToEdit.getDestination());
        model.addAttribute("edge", edgeToEdit);
        model.addAttribute("edgeModel", pathModel);
        model.addAttribute("routeList", vertices);
        return "route/edgeUpdate";
    }

    @RequestMapping("edge/delete/{id}")
    public String deleteEdge(@PathVariable long id) {
        importDataService.deleteEdge(id);
        return "redirect:/edges";
    }

    @RequestMapping("traffic/{id}")
    public String viewTraffic(@PathVariable Long id, Model model) {
        model.addAttribute("traffic", importDataService.getTrafficById(id));
        return "traffic/trafficView";
    }

    @RequestMapping(value = "traffic/add", method = RequestMethod.GET)
    public String addTraffic(Model model) {
        ShortestRouteModel sh = new ShortestRouteModel();
        List edges = importDataService.getAllUnusedEdges();
        model.addAttribute("traffic", new TrafficInfo());
        model.addAttribute("trafficModel", sh);
        model.addAttribute("trafficList", edges);
        return "traffic/trafficAdd";
    }
    @RequestMapping(value = "traffic/edit/{id}", method = RequestMethod.GET)
    public String editTraffic(@PathVariable Long id, Model model) {
        ShortestRouteModel pathModel = new ShortestRouteModel();
        List edges = importDataService.getAllEdges();
        TrafficInfo trafficToEdit = importDataService.getTrafficById(id);
        pathModel.setSelectedEdge(trafficToEdit.getRoute());
        model.addAttribute("traffic", trafficToEdit);
        model.addAttribute("trafficModel", pathModel);
        model.addAttribute("trafficList", edges);
        return "traffic/trafficUpdate";
    }

    @RequestMapping("traffic/delete/{id}")
    public String deleteTraffic(@PathVariable Long id) {
        importDataService.deleteTraffic(id);
        return "redirect:/traffics";
    }
}
