package com.shortestroute.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.shortestroute.dto.ShortestRouteModel;
import com.shortestroute.webservice.schema.FindShortestPathToRequest;
import com.shortestroute.webservice.schema.FindShortestPathToResponse;


@Endpoint
public class ShortestRouteEndpoint {

    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";
    private ShortestRouteRepository pathRepository;

    @Autowired
    public ShortestRouteEndpoint(ShortestRouteRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "findShortestPathToRequest")

    @ResponsePayload
    public FindShortestPathToResponse getShortestPath(@RequestPayload FindShortestPathToRequest request) {
        FindShortestPathToResponse response = new FindShortestPathToResponse();
        ShortestRouteModel shortestPath = pathRepository.getShortestPath(request.getName());
        response.setPath(shortestPath.getThePath());
        response.setDistanceTravelled(shortestPath.getDistanceTravelled());
        return response;
    }
}
