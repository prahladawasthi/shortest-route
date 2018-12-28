package com.shortestroute.repository;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
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
import com.shortestroute.model.Edge;
import com.shortestroute.model.TrafficInfo;
import com.shortestroute.model.Vertex;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Edge.class, EdgeRepository.class, Vertex.class, 
        VertexRepository.class, DataSourceConfig.class, PersistenceConfig.class},
        loader = AnnotationConfigContextLoader.class)

public class EdgeRepositoryTest {

    @Autowired
    private SessionFactory sessionFactory;
    private EdgeRepository edgeRepository;
    private int nextEdgeRecordId;

    @Before
    public void setUp() throws Exception {
        edgeRepository = new EdgeRepository(sessionFactory);
    }

    @Test
    public void verifyThatSaveEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        setUpFixtures();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge edge = new Edge("2", vertex1, vertex2, 2f);
        Edge expectedEdge = new Edge("2", vertex1, vertex2, 2f);
        expectedEdge.setId(1L + nextEdgeRecordId);

        //Test
        edgeRepository.save(edge);

        Edge persistedEdge = edgeRepository.selectUnique(expectedEdge.getId());

        //Verify
        assertThat(persistedEdge, sameBeanAs(expectedEdge));
        assertEquals("Earth", edge.getSource().getName());
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSaveEdgeAlsoSaveTrafficInfo() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        setUpFixtures();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge edge = new Edge("2", vertex1, vertex2, 2f);

        TrafficInfo traffic = new TrafficInfo("2", 1f);
        edge.addTrafficInfo(traffic);


        //Test
        edgeRepository.save(edge);

        Edge expectedEdge = new Edge("2", vertex1, vertex2, 2f);
        expectedEdge.setId(1L + nextEdgeRecordId);
        TrafficInfo expectedTrafficInfo = new TrafficInfo("2", 1f);
        expectedTrafficInfo.setId(2L);
        expectedEdge.addTrafficInfo(expectedTrafficInfo);

        List<TrafficInfo> expectedTrafficInfos = singletonList(expectedTrafficInfo);

        Criteria criteria = session.createCriteria(TrafficInfo.class);
        List<TrafficInfo> actualTrafficInfos = (List<TrafficInfo>) criteria.list();

        //Verify
        assertThat(actualTrafficInfos, sameBeanAs(expectedTrafficInfos));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSaveVertexAlsoSaveEdgeAndTrafficInfo() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        setUpFixtures();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");

        Edge edge = new Edge("2", 2f);

        vertex1.addSourceEdges(edge);
        vertex2.addDestinationEdges(edge);

        TrafficInfo traffic = new TrafficInfo("2", 1f);
        edge.addTrafficInfo(traffic);

        //Test
        session.save(vertex1);
        session.save(vertex2);

        Vertex expectedVertex1 = new Vertex("A", "Earth");
        Vertex expectedVertex2 = new Vertex("B", "Mars");

        Edge expectedEdge = new Edge("2", 2f);
        expectedEdge.setId(1L + nextEdgeRecordId);
        expectedVertex1.addSourceEdges(expectedEdge);
        expectedVertex2.addDestinationEdges(expectedEdge);

        TrafficInfo expectedTrafficInfo = new TrafficInfo("2", 1f);
        expectedTrafficInfo.setId(1L);
        expectedEdge.addTrafficInfo(expectedTrafficInfo);

        List<Edge> expectedEdges = singletonList(expectedEdge);
        List<TrafficInfo> expectedTrafficInfos = singletonList(expectedTrafficInfo);

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<TrafficInfo> query = builder.createQuery(TrafficInfo.class);
        Root<TrafficInfo> trafficInfoRoot = query.from(TrafficInfo.class);
        query.select(trafficInfoRoot);
        List<TrafficInfo> actualTrafficInfos = session.createQuery(query).getResultList();

        CriteriaQuery<Edge> queryE = builder.createQuery(Edge.class);
        Root<Edge> edgeRoot = queryE.from(Edge.class);
        queryE.select(edgeRoot);

        List<Edge> actualEdges = session.createQuery(queryE).getResultList();

        //Verify
        assertThat(actualTrafficInfos, sameBeanAs(expectedTrafficInfos));
        assertThat(actualEdges, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatUpdateEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        setUpFixtures();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        session.save(edge);

        Vertex vertex3 = new Vertex("C", "Moon");
        session.save(vertex3);
        Edge expectedEdge = new Edge("20", vertex1, vertex3, 20f);
        expectedEdge.setId(nextEdgeRecordId + 1L);
        List<Edge> expectedEdges = singletonList(expectedEdge);

        //Test
        edgeRepository.update(expectedEdge);
        List<Edge> persistedEdges = edgeRepository.selectAllByRecordId(expectedEdge.getId());

        // Verify
        assertThat(persistedEdges, sameBeanAs(expectedEdges));
        assertEquals("Moon", edge.getDestination().getName());
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatDeleteEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        setUpFixtures();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Moon");
        Vertex vertex4 = new Vertex("D", "Jupiter");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        session.save(vertex4);

        Edge e1 = new Edge("10", vertex1, vertex2, 20.1f);
        Edge e2 = new Edge("12", vertex3, vertex4, 1.3f);
        session.save(e1);
        session.save(e2);


        Edge exp = new Edge("10", vertex1, vertex2, 20.1f);
        exp.setId(1L + nextEdgeRecordId);
        List<Edge> expectedEdges = singletonList(exp);

        //Test
        edgeRepository.delete(e2);
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Edge> query = builder.createQuery(Edge.class);
        Root<Edge> edgeRoot = query.from(Edge.class);
        query.select(edgeRoot);
        List<Edge> persistedEdges = session.createQuery(query).getResultList();

        // Verify
        assertThat(persistedEdges, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectUniqueEdgeIsCorrect() {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge edge = new Edge("5", vertex1, vertex2, 0.5f);
        Edge expectedEdge = new Edge("1", vertex1, vertex2, 20.1f);
        expectedEdge.setId(1L);
        session.save(edge);
        session.save(expectedEdge);

        //Test
        Edge persistedEdge = edgeRepository.selectUnique(expectedEdge.getId());

        //Verify
        assertThat(persistedEdge, sameBeanAs(expectedEdge));
        assertEquals(expectedEdge, persistedEdge);
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectAllEdgesByRouteIdIsCorrect() {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge e1 = new Edge("1", vertex1, vertex2, 2.4f);
        Edge e2 = new Edge("2", vertex2, vertex1, 1.3f);
        session.save(e1);
        session.save(e2);

        List<Edge> expectedEdges = singletonList(e1);

        //Test
        List<Edge> persistedEdge = edgeRepository.selectAllByRouteId(e1.getRouteId());

        //Verify
        assertThat(persistedEdge, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectAllEdgesIsCorrect() {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");


        Edge e1 = new Edge("4", 2.4f);
        Edge e2 = new Edge("9", 1.3f);

        vertex1.addSourceEdges(e1);
        vertex1.addDestinationEdges(e2);
        vertex2.addDestinationEdges(e1);
        vertex2.addSourceEdges(e2);


        session.save(vertex1);
        session.save(vertex2);

        List<Edge> expectedEdges = Arrays.asList(e2, e1);

        //Test
        List<Edge> persistedEdge = edgeRepository.selectAll();

        //Verify
        assertThat(persistedEdge, sameBeanAs(expectedEdges));
        //Rollback for testing purpose

        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatEdgeExistsSelectionIsCorrect() {
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
        List<Edge> expectedEdges = singletonList(e1);

        //Test
        List<Edge> returnedEdges = edgeRepository.edgeExists(edgeToCommit);
        //Verify
        assertThat(returnedEdges, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectAllUnusedEdgesIsCorrect() {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");


        Edge e1 = new Edge("4", 2.4f);
        Edge e2 = new Edge("9", 1.3f);

        TrafficInfo traffic1 = new TrafficInfo("2", 1f);
        TrafficInfo traffic2 = new TrafficInfo("3", 1f);
        e1.addTrafficInfo(traffic1);
        e2.addTrafficInfo(traffic2);
        e2.removeTrafficInfo();

        vertex1.addSourceEdges(e1);
        vertex1.addDestinationEdges(e2);
        vertex2.addDestinationEdges(e1);
        vertex2.addSourceEdges(e2);


        session.save(vertex1);
        session.save(vertex2);

        List<Edge> expectedEdges = Arrays.asList(e2);

        //Test
        List<Edge> persistedEdge = edgeRepository.selectAllUnusedEdges();

        //Verify
        assertThat(persistedEdge, sameBeanAs(expectedEdges));
        //Rollback for testing purpose

        session.getTransaction().rollback();
    }

    public void setUpFixtures() {
        nextEdgeRecordId = edgeRepository.findNextId();
    }
}