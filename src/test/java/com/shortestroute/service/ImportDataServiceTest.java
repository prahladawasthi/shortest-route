package com.shortestroute.service;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import com.shortestroute.config.DataSourceConfig;
import com.shortestroute.config.PersistenceConfig;
import com.shortestroute.config.Resource;
import com.shortestroute.data.processor.ExcelDataProcessor;
import com.shortestroute.helper.Graph;
import com.shortestroute.model.Edge;
import com.shortestroute.model.TrafficInfo;
import com.shortestroute.model.Vertex;
import com.shortestroute.repository.EdgeRepository;
import com.shortestroute.repository.TrafficInfoRepository;
import com.shortestroute.repository.VertexRepository;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ExcelDataProcessor.class, TrafficInfo.class, EdgeRepository.class,
        VertexRepository.class, Resource.class, DataSourceConfig.class, PersistenceConfig.class},
        loader = AnnotationConfigContextLoader.class)
public class ImportDataServiceTest {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ExcelDataProcessor xlsxHandler;
    private EdgeRepository edgeDao;
    private VertexRepository vertexDao;
    private TrafficInfoRepository trafficDao;
    private ImportDataService importDataService;
    private int nextEdgeRecordId;
    private int nextTrafficInfoRecordId;

    @Before
    public void setUp() throws Exception {
        edgeDao = new EdgeRepository(sessionFactory);
        trafficDao = new TrafficInfoRepository(sessionFactory);
        vertexDao = new VertexRepository(sessionFactory);
        importDataService = new ImportDataService(vertexDao, edgeDao, trafficDao, xlsxHandler);
    }

    public void setEdgeRecord() {
        nextEdgeRecordId = edgeDao.findNextId();
    }

    public void setTrafficInfoRecord() {
        nextTrafficInfoRecordId = trafficDao.findNextId();
    }

    @Test
    public void verifyThatReadExcelAndPersistToGraphIsCorrect() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        setEdgeRecord();
        setTrafficInfoRecord();
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Vertex vertexC = new Vertex("C", "Jupiter");
        Vertex vertexD = new Vertex("D", "Venus");
        Vertex vertexE = new Vertex("E", "Mars");

        Edge edge1 = new Edge("A_B", 0.44f);
        edge1.setId(nextEdgeRecordId + 1L);
        Edge edge2 = new Edge("A_C", 1.89f);
        edge2.setId(nextEdgeRecordId + 2L);
        Edge edge3 = new Edge("A_D", 0.10f);
        edge3.setId(nextEdgeRecordId + 3L);
        Edge edge4 = new Edge("B_E", 2.44f);
        edge4.setId(nextEdgeRecordId + 4L);
        Edge edge5 = new Edge("C_E", 3.45f);
        edge5.setId(nextEdgeRecordId + 5L);

        TrafficInfo traffic1 = new TrafficInfo("A_B", 0.30f);
        traffic1.setId(nextTrafficInfoRecordId + 1L);
        TrafficInfo traffic2 = new TrafficInfo("A_C", 0.90f);
        traffic2.setId(nextTrafficInfoRecordId + 2L);
        TrafficInfo traffic3 = new TrafficInfo("A_D", 0.10f);
        traffic3.setId(nextTrafficInfoRecordId + 3L);
        TrafficInfo traffic4 = new TrafficInfo("B_E", 0.20f);
        traffic4.setId(nextTrafficInfoRecordId + 4L);
        TrafficInfo traffic5 = new TrafficInfo("C_E", 1.30f);
        traffic5.setId(nextTrafficInfoRecordId + 5L);

        edge1.addTrafficInfo(traffic1);
        edge2.addTrafficInfo(traffic2);
        edge3.addTrafficInfo(traffic3);
        edge4.addTrafficInfo(traffic4);
        edge5.addTrafficInfo(traffic5);

        vertexA.addSourceEdges(edge1);
        vertexB.addDestinationEdges(edge1);
        vertexA.addSourceEdges(edge2);
        vertexC.addDestinationEdges(edge2);
        vertexA.addSourceEdges(edge3);
        vertexD.addDestinationEdges(edge3);
        vertexB.addSourceEdges(edge4);
        vertexE.addDestinationEdges(edge4);
        vertexC.addSourceEdges(edge5);
        vertexE.addDestinationEdges(edge5);

        List<Vertex> vertices = Arrays.asList(vertexA, vertexB, vertexC, vertexD, vertexE);
        List<Edge> edges = Arrays.asList(edge1, edge2, edge3, edge4, edge5);
        List<TrafficInfo> traffics = Arrays.asList(traffic1, traffic2, traffic3, traffic4, traffic5);
        importDataService.readExcelFileAndImportIntoDatabase();
        Graph graph = importDataService.selectGraph();

        List<Edge> readEdges = graph.getEdges();
        List<Vertex> readVertices = graph.getVertexes();
        List<TrafficInfo> readTrafficInfos = graph.getTrafficInfos();

        assertThat(vertices, sameBeanAs(readVertices));
        assertThat(edges, sameBeanAs(readEdges));
        assertThat(traffics, sameBeanAs(readTrafficInfos));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSaveVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex = new Vertex("A", "Earth");
        List<Vertex> expectedVertexes = new ArrayList<>();
        expectedVertexes.add(vertex);
        //Test
        Vertex returned = importDataService.saveVertex(vertex);
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Vertex> query = builder.createQuery(Vertex.class);
        Root<Vertex> vertexRoot = query.from(Vertex.class);
        query.select(vertexRoot);
        List<Vertex> persistedVertexes = session.createQuery(query).getResultList();

        //Verify
        assertThat(vertex, sameBeanAs(returned));
        assertThat(persistedVertexes, sameBeanAs(expectedVertexes));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatUpdateVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex = new Vertex("A", "Earth");
        session.save(vertex);

        Vertex vertexToUpdate = new Vertex("A", "Jupiter");
        List<Vertex> expectedVertexes = new ArrayList<>();
        expectedVertexes.add(vertexToUpdate);

        Vertex persistedVertex = importDataService.updateVertex(vertexToUpdate);

        List<Vertex> persistedVertexes = new ArrayList<>();
        persistedVertexes.add(persistedVertex);

        assertThat(expectedVertexes, sameBeanAs(persistedVertexes));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatDeleteVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex v1 = new Vertex("A", "Mars");
        Vertex v2 = new Vertex("C", "Terre");
        List<Vertex> expectedVertexes = new ArrayList<>();
        expectedVertexes.add(v1);
        session.save(v1);
        session.save(v2);
        boolean expected = true;

        //Test
        boolean returned = importDataService.deleteVertex(v2.getId());

        // Verify
        assertThat(expected, sameBeanAs(returned));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetUniqueByNameVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex expected = new Vertex("C", "Moon");
        session.save(vertex1);
        session.save(vertex2);
        session.save(expected);

        //Test
        Vertex persistedVertex = importDataService.getVertexByName(expected.getName());

        //Verify
        assertThat(persistedVertex, sameBeanAs(expected));
        assertThat(persistedVertex.getName(), sameBeanAs("Moon"));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetUniqueByIdVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex expected = new Vertex("C", "Moon");
        session.save(vertex1);
        session.save(expected);

        //Test
        Vertex persistedVertex = importDataService.getVertexById(expected.getId());

        //Verify
        assertThat(persistedVertex, sameBeanAs(expected));
        assertThat(persistedVertex.getId(), sameBeanAs("C"));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetAllVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex v1 = new Vertex("A", "Jupiter");
        Vertex v2 = new Vertex("F", "Pluto");
        session.save(v1);
        session.save(v2);
        List<Vertex> expectedVertexes = new ArrayList<>();
        expectedVertexes.add(v1);
        expectedVertexes.add(v2);

        //Test
        List<Vertex> persistedVertexes = importDataService.getAllVertices();

        //Verify
        assertThat(persistedVertexes, sameBeanAs(expectedVertexes));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatVertexExistsIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        session.save(vertex1);

        boolean expected = true;

        //Test
        boolean returned = importDataService.vertexExist(vertex1.getId());

        //Verify
        assertThat(returned, sameBeanAs(expected));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    //Edges

    @Test
    public void verifyThatSaveEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);
        Edge edge = new Edge("2", vertex1, vertex2, 2f);
        List<Edge> expectedEdges = new ArrayList<>();
        expectedEdges.add(edge);
        //Test
        Edge returned = importDataService.saveEdge(edge);
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Edge> query = builder.createQuery(Edge.class);
        Root<Edge> edgeRoot = query.from(Edge.class);
        query.select(edgeRoot);
        List<Edge> persistedEdges = session.createQuery(query).getResultList();

        //Verify
        assertThat(edge, sameBeanAs(returned));
        assertThat(persistedEdges, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatUpdateEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        session.save(edge);

        Vertex vertex3 = new Vertex("C", "Moon");
        session.save(vertex3);
        Edge edgeToUpdate = new Edge("20", vertex1, vertex3, 20f);
        List<Edge> expectedEdges = new ArrayList<>();
        expectedEdges.add(edgeToUpdate);

        Edge persistedEdge = importDataService.updateEdge(edgeToUpdate);

        List<Edge> persistedEdges = new ArrayList<>();
        persistedEdges.add(persistedEdge);

        assertThat(expectedEdges, sameBeanAs(persistedEdges));
        assertThat(persistedEdge.getDestination(), sameBeanAs(vertex3));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatDeleteEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Moon");
        Vertex vertex4 = new Vertex("D", "Jupiter");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        session.save(vertex4);

        Edge edge1 = new Edge("10", vertex1, vertex2, 20.1f);
        Edge edge2 = new Edge("12", vertex3, vertex4, 1.3f);
        session.save(edge1);
        session.save(edge2);
        List<Edge> expectedEdges = new ArrayList<>();
        expectedEdges.add(edge1);

        boolean expected = true;

        //Test
        boolean returned = importDataService.deleteEdge(edge2.getId());

        // Verify
        assertThat(expected, sameBeanAs(returned));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetEdgeByIdIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge edge1 = new Edge("5", vertex1, vertex2, 0.5f);
        Edge expected = new Edge("1", vertex1, vertex2, 20.1f);
        session.save(edge1);
        session.save(expected);

        //Test
        Edge persistedEdge = importDataService.getEdgeById(expected.getId());

        //Verify
        assertThat(persistedEdge, sameBeanAs(expected));
        assertThat(persistedEdge.getDestination(), sameBeanAs(vertex2));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetAllEdgesIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge edge1 = new Edge("1", vertex1, vertex2, 2.4f);
        Edge edge2 = new Edge("2", vertex2, vertex1, 1.3f);
        session.save(edge1);
        session.save(edge2);
        List<Edge> expectedEdges = new ArrayList<>();
        expectedEdges.add(edge1);
        expectedEdges.add(edge2);

        //Test
        List<Edge> persistedEdges = importDataService.getAllEdges();

        //Verify
        assertThat(persistedEdges, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatEdgeExistsIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Moon");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);

        Edge e1 = new Edge("1", vertex1, vertex2, 2.4f);
        Edge e2 = new Edge("2", vertex1, vertex3, 100.3f);
        session.save(e1);
        session.save(e2);

        Edge edgeToCommit = new Edge("3", vertex1, vertex2, 0.3f);
        edgeToCommit.setId(3L);

        boolean expected = true;

        //Test
        boolean returned = importDataService.edgeExists(edgeToCommit);

        //Verify
        assertThat(returned, sameBeanAs(expected));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    //Traffcs

    @Test
    public void verifyThatSaveTrafficInfoIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        session.save(edge);

        TrafficInfo traffic = new TrafficInfo("1", edge, 4f);
        List<TrafficInfo> expectedTrafficInfos = new ArrayList<>();
        expectedTrafficInfos.add(traffic);
        //Test
        TrafficInfo returned = importDataService.saveTraffic(traffic);
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<TrafficInfo> query = builder.createQuery(TrafficInfo.class);
        Root<TrafficInfo> trafficInfoRoot = query.from(TrafficInfo.class);
        query.select(trafficInfoRoot);
        List<TrafficInfo> persistedTrafficInfos = session.createQuery(query).getResultList();

        //Verify
        assertThat(traffic, sameBeanAs(returned));
        assertThat(persistedTrafficInfos, sameBeanAs(expectedTrafficInfos));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatUpdateTrafficInfoIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Venus");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        Edge edge2 = new Edge("21", vertex1, vertex3, 2.0f);
        session.save(edge);
        session.save(edge2);

        TrafficInfo traffic = new TrafficInfo("1", edge, 4f);
        session.save(traffic);
        Long id = traffic.getId();

        TrafficInfo trafficToUpdate = new TrafficInfo("1", edge2, 1.2f);
        trafficToUpdate.setId(id);
        List<TrafficInfo> expectedTrafficInfos = new ArrayList<>();
        expectedTrafficInfos.add(trafficToUpdate);

        TrafficInfo persistedTrafficInfo = importDataService.updateTraffic(trafficToUpdate);

        List<TrafficInfo> persistedTrafficInfos = new ArrayList<>();
        persistedTrafficInfos.add(persistedTrafficInfo);

        assertThat(expectedTrafficInfos, sameBeanAs(persistedTrafficInfos));
        assertThat(persistedTrafficInfo.getRoute(), sameBeanAs(edge2));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatDeleteTrafficInfoIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Venus");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        Edge edge2 = new Edge("21", vertex1, vertex3, 2.0f);
        session.save(edge);
        session.save(edge2);

        TrafficInfo traffic1 = new TrafficInfo("1", edge, 4f);
        TrafficInfo traffic2 = new TrafficInfo("2", edge2, 2f);
        List<TrafficInfo> expectedTrafficInfos = new ArrayList<>();
        expectedTrafficInfos.add(traffic1);
        session.save(traffic1);
        session.save(traffic2);
        boolean expected = true;

        //Test
        boolean returned = importDataService.deleteTraffic(traffic2.getId());

        // Verify
        assertThat(expected, sameBeanAs(returned));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetTrafficInfoByIdIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("F", "Moon");
        Vertex vertex3 = new Vertex("Z", "Congo");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        Edge edge2 = new Edge("21", vertex1, vertex3, 2.0f);
        session.save(edge);
        session.save(edge2);

        TrafficInfo traffic = new TrafficInfo("100", edge, 4f);
        TrafficInfo expected = new TrafficInfo("5", edge2, 1.89f);
        session.save(traffic);
        session.save(expected);

        //Test
        TrafficInfo persistedTrafficInfo = importDataService.getTrafficById(expected.getId());

        //Verify
        assertThat(persistedTrafficInfo, sameBeanAs(expected));
        assertThat(persistedTrafficInfo.getDelay(), sameBeanAs(1.89f));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetAllTrafficInfosIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Moon");
        Vertex vertex3 = new Vertex("C", "Congo");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);

        Edge edge = new Edge("20", vertex1, vertex2, 1f);
        Edge edge2 = new Edge("21", vertex1, vertex3, 2.0f);
        session.save(edge);
        session.save(edge2);

        TrafficInfo traffic1 = new TrafficInfo("1", edge, 4.1f);
        TrafficInfo traffic2 = new TrafficInfo("2", edge2, 1.2f);
        session.save(traffic1);
        session.save(traffic2);
        List<TrafficInfo> expectedTrafficInfos = new ArrayList<>();
        expectedTrafficInfos.add(traffic1);
        expectedTrafficInfos.add(traffic2);

        //Test
        List<TrafficInfo> persistedTrafficInfos = importDataService.getAllTrafficInfos();

        //Verify
        assertThat(persistedTrafficInfos, sameBeanAs(expectedTrafficInfos));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }
}
