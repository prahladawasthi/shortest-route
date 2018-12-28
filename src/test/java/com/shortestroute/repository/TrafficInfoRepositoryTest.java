package com.shortestroute.repository;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;

import java.util.ArrayList;
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
import com.shortestroute.model.Edge;
import com.shortestroute.model.TrafficInfo;
import com.shortestroute.model.Vertex;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TrafficInfo.class, TrafficInfoRepository.class, DataSourceConfig.class, PersistenceConfig.class},
        loader = AnnotationConfigContextLoader.class)
public class TrafficInfoRepositoryTest {
    @Autowired
    private SessionFactory sessionFactory;
    private TrafficInfoRepository trafficRepository;

    @Before
    public void setUp() throws Exception {
        trafficRepository = new TrafficInfoRepository(sessionFactory);
    }

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
        trafficRepository.save(traffic);
        List<TrafficInfo> persistedTrafficInfos = getTrafficInfos(session);

        //Verify
        assertThat(persistedTrafficInfos, sameBeanAs(expectedTrafficInfos));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    private List<TrafficInfo> getTrafficInfos(Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<TrafficInfo> query = builder.createQuery(TrafficInfo.class);
        Root<TrafficInfo> trafficInfoRoot = query.from(TrafficInfo.class);
        query.select(trafficInfoRoot);
        return session.createQuery(query).getResultList();
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

        List<TrafficInfo> expectedTrafficInfo = new ArrayList<>();
        expectedTrafficInfo.add(trafficToUpdate);

        //Test
        trafficRepository.update(trafficToUpdate);
        List<TrafficInfo> persistedTrafficInfos = getTrafficInfos(session);

        // Verify
        assertThat(persistedTrafficInfos, sameBeanAs(expectedTrafficInfo));
        assertThat(persistedTrafficInfos.get(0).getRoute().getDestination().getName(), sameBeanAs("Venus"));

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

        //Test
        trafficRepository.delete(traffic2);
        List<TrafficInfo> persistedTrafficInfos = getTrafficInfos(session);

        // Verify
        assertThat(persistedTrafficInfos, sameBeanAs(expectedTrafficInfos));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectUniqueByRouteIdTrafficInfoIsCorrect() throws Exception {
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
        TrafficInfo expected = new TrafficInfo("5", edge2, 4f);
        session.save(traffic);
        session.save(expected);

        //Test
        TrafficInfo persisted = trafficRepository.selectUniqueByRouteId(expected.getRouteId());

        //Verify
        assertThat(persisted, sameBeanAs(expected));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectUniqueIsCorrect() throws Exception {
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
        TrafficInfo expected = new TrafficInfo("5", edge2, 4f);
        session.save(traffic);
        session.save(expected);

        //Test
        TrafficInfo persisted = trafficRepository.selectUnique(expected.getId());

        //Verify
        assertThat(persisted, sameBeanAs(expected));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectAllTrafficInfosIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Moon");
        Vertex vertex3 = new Vertex("C", "Congo");
        Vertex vertex4 = new Vertex("D", "Denzel");
        Vertex vertex5 = new Vertex("W", "Washington");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        session.save(vertex4);
        session.save(vertex5);
        Edge edge = new Edge("20", vertex1, vertex2, 1f);
        Edge edge2 = new Edge("21", vertex1, vertex3, 2.0f);
        Edge edge3 = new Edge("23", vertex3, vertex4, 3.0f);
        Edge edge4 = new Edge("24", vertex1, vertex5, 4.0f);
        session.save(edge);
        session.save(edge2);
        session.save(edge3);
        session.save(edge4);

        TrafficInfo traffic1 = new TrafficInfo("1", edge, 4.1f);
        TrafficInfo traffic2 = new TrafficInfo("2", edge2, 1.2f);
        TrafficInfo traffic3 = new TrafficInfo("3", edge3, 11f);
        TrafficInfo traffic4 = new TrafficInfo("4", edge4, 4f);
        session.save(traffic1);
        session.save(traffic2);
        session.save(traffic3);
        session.save(traffic4);
        List<TrafficInfo> expectedTrafficInfos = new ArrayList<>();
        expectedTrafficInfos.add(traffic1);
        expectedTrafficInfos.add(traffic2);
        expectedTrafficInfos.add(traffic3);
        expectedTrafficInfos.add(traffic4);

        //Test
        List<TrafficInfo> persistedTrafficInfos = trafficRepository.selectAll();

        //Verify
        assertThat(persistedTrafficInfos, sameBeanAs(expectedTrafficInfos));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }
}
