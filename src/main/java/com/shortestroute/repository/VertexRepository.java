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

import com.shortestroute.model.Vertex;

@Repository
@Transactional
public class VertexRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public VertexRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Vertex vertex) {
        Session session = sessionFactory.getCurrentSession();
        session.save(vertex);
    }

    public void update(Vertex vertex) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(vertex);
    }

    public int delete(String id) {
        Session session = sessionFactory.getCurrentSession();
        String qry = "DELETE FROM vertex AS V WHERE V.id = :idParameter";
        Query query = session.createQuery(qry);
        query.setParameter("idParameter", id);

        return query.executeUpdate();
    }

    public void delete(Vertex vertex) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(vertex);
    }

    public Vertex selectUnique(String id) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Vertex> query = builder.createQuery(Vertex.class);
        Root<Vertex> edgeRoot = query.from(Vertex.class);
        query.select(edgeRoot);
        query.where(builder.equal(edgeRoot.get("id"), id));

        return session.createQuery(query).uniqueResult();
    }

    public Vertex selectUniqueLazyLoad(String id) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Vertex> query = builder.createQuery(Vertex.class);
        Root<Vertex> edgeRoot = query.from(Vertex.class);
        query.select(edgeRoot);
        query.where(builder.equal(edgeRoot.get("id"), id));
        Vertex vertex = (Vertex) session.createQuery(query).uniqueResult();
        if (vertex != null) {
            Hibernate.initialize(vertex.getSourceEdges());
            Hibernate.initialize(vertex.getDestinationEdges());
        }
        return session.createQuery(query).uniqueResult();
    }

    public Vertex selectUniqueByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Vertex> query = builder.createQuery(Vertex.class);
        Root<Vertex> edgeRoot = query.from(Vertex.class);
        query.select(edgeRoot);
        query.where(builder.equal(edgeRoot.get("name"), name));

        return session.createQuery(query).uniqueResult();
    }

    public List<Vertex> selectAll() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Vertex> query = builder.createQuery(Vertex.class);
        Root<Vertex> edgeRoot = query.from(Vertex.class);
        query.select(edgeRoot);
        return session.createQuery(query).getResultList();
    }
}
