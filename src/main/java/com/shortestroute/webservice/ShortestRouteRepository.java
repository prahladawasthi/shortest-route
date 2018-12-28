package com.shortestroute.webservice;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.shortestroute.dto.ShortestRouteModel;
import com.shortestroute.helper.Graph;
import com.shortestroute.model.Edge;
import com.shortestroute.model.Vertex;
import com.shortestroute.service.ImportDataService;
import com.shortestroute.service.ShortestPathService;


@Component
public class ShortestRouteRepository {

    private static final String PATH_NOT_AVAILABLE = "There is no path to ";
    private static final String PATH_NOT_NEEDED = "Not needed. You are already on planet ";
    private static final String NO_PLANET_FOUND = "No planet found.";
    private static final String PLANET_DOES_NOT_EXIST = " does not exist in the Interstellar Transport System.";
    private PlatformTransactionManager platformTransactionManager;
    private ImportDataService importDataService;
    private ShortestPathService shortestPathService;

    @Autowired
    public ShortestRouteRepository(@Qualifier("transactionManager") PlatformTransactionManager platformTransactionManager,
                                   ImportDataService importDataService, ShortestPathService shortestPathService) {
        this.platformTransactionManager = platformTransactionManager;
        this.importDataService = importDataService;
        this.shortestPathService = shortestPathService;
    }

    @PostConstruct
    public void initData() {

        TransactionTemplate tmpl = new TransactionTemplate(platformTransactionManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                importDataService.readExcelFileAndImportIntoDatabase();
            }
        });
    }

    public ShortestRouteModel getShortestPath(String name) {
        StringBuilder path = new StringBuilder();
        ShortestRouteModel result = new ShortestRouteModel();
        String distanceTravelled = "";
        Graph graph = importDataService.selectGraph();

        if (graph == null || graph.getVertexes() == null || graph.getVertexes().isEmpty()) {
            result.setThePath(NO_PLANET_FOUND);
            result.setDistanceTravelled("");
            return result;
        }
        Vertex source = graph.getVertexes().get(0);
        Vertex destination = importDataService.getVertexByName(name);
        if (destination == null) {
            destination = importDataService.getVertexById(name);
            if (destination == null) {
                result.setThePath( name + PLANET_DOES_NOT_EXIST);
                result.setDistanceTravelled(distanceTravelled);
                return result;
            }
        } else if (source != null && destination != null && source.getId().equals(destination.getId())) {
            result.setThePath( PATH_NOT_NEEDED + source.getName() + ".");
            result.setDistanceTravelled(distanceTravelled);
            return result;
        }

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
            }
            distanceTravelled = roundOff(distance) + " (Light Years)";
        } else {
            path.append(PATH_NOT_AVAILABLE).append(destination.getName());
            path.append(".");
        }
        result.setThePath( path.toString());
        result.setDistanceTravelled(distanceTravelled);
        return result;
    }
    private float roundOff(float d) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}