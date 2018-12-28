package com.shortestroute.webservice;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.shortestroute.config.DataSourceConfig;
import com.shortestroute.config.PersistenceConfig;
import com.shortestroute.config.Resource;
import com.shortestroute.data.processor.ExcelDataProcessor;
import com.shortestroute.repository.EdgeRepository;
import com.shortestroute.repository.TrafficInfoRepository;
import com.shortestroute.repository.VertexRepository;
import com.shortestroute.service.ImportDataService;
import com.shortestroute.service.ShortestPathService;
import com.shortestroute.webservice.config.WebServiceConfiguration;
import com.shortestroute.webservice.schema.FindShortestPathToRequest;
import com.shortestroute.webservice.schema.FindShortestPathToResponse;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PathImpl.class, ShortestPathService.class, ExcelDataProcessor.class,
        Resource.class, DataSourceConfig.class, PersistenceConfig.class, WebServiceConfiguration.class,
        ShortestRouteEndpoint.class, ShortestRouteRepository.class, ImportDataService.class, EdgeRepository.class,
        VertexRepository.class,
        TrafficInfoRepository.class},
        loader = AnnotationConfigContextLoader.class)
public class ShortestPathEndpointTest {

    @Autowired
    private ShortestRouteEndpoint shortestPathEndpoint;

    @Test
    public void verifyThatShortestPathSOAPEndPointIsCorrect() throws Exception {
        // Set Up Fixture
        FindShortestPathToRequest shortestPathRequest = new FindShortestPathToRequest();
        shortestPathRequest.setName("Moon");

        StringBuilder path = new StringBuilder();
        path.append("Earth (A) Moon (B) ");

        FindShortestPathToResponse expectedResponse = new FindShortestPathToResponse();
        expectedResponse.setPath(path.toString());
        expectedResponse.setDistanceTravelled("0.44 (Light Years)");

        //Test
        FindShortestPathToResponse actualResponse = shortestPathEndpoint.getShortestPath(shortestPathRequest);

        // Verify
        assertThat(actualResponse, sameBeanAs(expectedResponse));
        assertThat(actualResponse.getPath(), sameBeanAs(path));
    }

}
