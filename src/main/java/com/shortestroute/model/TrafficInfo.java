package com.shortestroute.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity(name = "traffic")
public class TrafficInfo implements Serializable {

    @Id
    @SequenceGenerator(name = "trafficSeq", sequenceName = "TRAFFIC_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trafficSeq")
    @Column
    private Long id;

    @Column
    private String routeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edge_id")
    private Edge route;

    @Column
    private Float delay;

    public TrafficInfo(String routeId, Edge route, Float delay) {
        this.routeId = routeId;
        this.route = route;
        this.delay = delay;
    }

    public TrafficInfo(String routeId, Float delay) {

        this.routeId = routeId;
        this.delay = delay;
    }

    public TrafficInfo() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Edge getRoute() {
        return route;
    }

    public void setRoute(Edge route) {
        this.route = route;
    }

    public Float getDelay() {
        return delay;
    }

    public void setDelay(Float delay) {
        this.delay = delay;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TrafficInfo other = (TrafficInfo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
