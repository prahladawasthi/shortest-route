package com.shortestroute.repository;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shortestroute.model.Edge;
import com.shortestroute.model.Vertex;

@Repository
@Transactional
public class EdgeRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public EdgeRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public void save(Edge edge) {
        Session session = sessionFactory.getCurrentSession();
        session.save(edge);
    }

    public void update(Edge edge) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(edge);
    }

    public void delete(Edge edge) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(edge);
    }

    public int findNextId() {
        Session session = sessionFactory.getCurrentSession();
        String qry = "values ( next value for EDGE_SEQ )";
        return (int) session.createNativeQuery(qry).uniqueResult();
    }

    public Edge selectUnique(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Edge> query = builder.createQuery(Edge.class);
        Root<Edge> edgeRoot = query.from(Edge.class);
        query.select(edgeRoot);
        query.where(builder.equal(edgeRoot.get("id"), id));

        return session.createQuery(query).uniqueResult();
    }

    public Edge selectUniqueLazyLoad(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Edge> query = builder.createQuery(Edge.class);
        Root<Edge> edgeRoot = query.from(Edge.class);
        query.select(edgeRoot);
        query.where(builder.equal(edgeRoot.get("id"), id));
        Edge edge = session.createQuery(query).uniqueResult();
        if (edge != null) {
            Hibernate.initialize(edge.getSource().getSourceEdges());
            Hibernate.initialize(edge.getDestination().getDestinationEdges());
        }
        return session.createQuery(query).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Edge> edgeExists(Edge edge) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM edge AS E WHERE E.source = :source AND E.destination = :destination";
        Query query = session.createQuery(hql);
        query.setParameter("source", edge.getSource());
        query.setParameter("destination", edge.getDestination());
        return query.getResultList();
    }

    public Edge findEdgeBetweenVertexes(Vertex source, Vertex destination) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM edge AS E WHERE E.source = :source AND E.destination = :destination";
        Query query = session.createQuery(hql);
        query.setParameter("source", source);
        query.setParameter("destination", destination);
        return (Edge) query.uniqueResult();
    }

    public List<Edge> selectAllByRecordId(long id) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Edge> query = builder.createQuery(Edge.class);
        Root<Edge> edgeRoot = query.from(Edge.class);
        query.select(edgeRoot);
        query.where(builder.equal(edgeRoot.get("id"), id));

        return session.createQuery(query).getResultList();
    }

    public List<Edge> selectAllByRouteId(String routeId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Edge> query = builder.createQuery(Edge.class);
        Root<Edge> edgeRoot = query.from(Edge.class);
        query.select(edgeRoot);
        query.where(builder.equal(edgeRoot.get("routeId"), routeId));

        return session.createQuery(query).getResultList();
    }

    public List<Edge> selectAll() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Edge> query = builder.createQuery(Edge.class);
        Root<Edge> edgeRoot = query.from(Edge.class);
        query.select(edgeRoot);
        return session.createQuery(query).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Edge> selectAllUnusedEdges() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT e FROM edge e LEFT JOIN FETCH e.trafficInfo WHERE e.trafficInfo.id is null";
        Query query = session.createQuery(hql);
        return query.getResultList();
    }
}
