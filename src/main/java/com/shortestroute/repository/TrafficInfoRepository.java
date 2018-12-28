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

import com.shortestroute.model.TrafficInfo;

@Repository
@Transactional
public class TrafficInfoRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public TrafficInfoRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(TrafficInfo trafficInfo) {
        Session session = sessionFactory.getCurrentSession();
        session.save(trafficInfo);
    }

    public void update(TrafficInfo trafficInfo) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(trafficInfo);
    }

    public void delete(TrafficInfo trafficInfo) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(trafficInfo);
    }

    public TrafficInfo selectUnique(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<TrafficInfo> query = builder.createQuery(TrafficInfo.class);
        Root<TrafficInfo> trafficInfoRoot = query.from(TrafficInfo.class);
        query.select(trafficInfoRoot);
        query.where(builder.equal(trafficInfoRoot.get("id"), id));

        TrafficInfo trafficInfo = session.createQuery(query).uniqueResult();
        if (trafficInfo != null) {
            Hibernate.initialize(trafficInfo.getRoute());
        }
        return trafficInfo;
    }

    public TrafficInfo selectUniqueByRouteId(String routeId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<TrafficInfo> query = builder.createQuery(TrafficInfo.class);
        Root<TrafficInfo> edgeRoot = query.from(TrafficInfo.class);
        query.select(edgeRoot);
        query.where(builder.equal(edgeRoot.get("routeId"), routeId));

        TrafficInfo trafficInfo = session.createQuery(query).uniqueResult();
        if (trafficInfo != null) {
            Hibernate.initialize(trafficInfo.getRoute());
        }
        return trafficInfo;
    }

    public int findNextId() {
        Session session = sessionFactory.getCurrentSession();
        String qry = "values ( next value for TRAFFIC_SEQ )";
        return (int) session.createNativeQuery(qry).uniqueResult();
    }

    public List<TrafficInfo> selectAll() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<TrafficInfo> query = builder.createQuery(TrafficInfo.class);
        Root<TrafficInfo> trafficInfoRoot = query.from(TrafficInfo.class);
        query.select(trafficInfoRoot);
        return session.createQuery(query).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<TrafficInfo> selectAllLazyLoading() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT t FROM traffic t LEFT JOIN FETCH t.route";
        Query query = session.createQuery(hql);
        return query.getResultList();
    }
}
