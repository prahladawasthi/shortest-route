package com.shortestroute.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity(name = "edge")
public class Edge implements Serializable {

    @Id
    @SequenceGenerator(name = "edgeSeq", sequenceName = "EDGE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "edgeSeq")

    @Column
    private Long id;

    @Column
    private String routeId;

    @ManyToOne
    private Vertex source;

    @ManyToOne
    private Vertex destination;

    @Column
    private Float distance;

    @OneToOne(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private TrafficInfo trafficInfo;

    public Edge(String routeId, Vertex source, Vertex destination, Float distance) {

        this.routeId = routeId;
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }

    public Edge(String routeId, Float distance) {

        this.routeId = routeId;
        this.distance = distance;
    }

    public Edge() {

    }

    public void addTrafficInfo(TrafficInfo trafficInfo) {
        trafficInfo.setRoute(this);
        this.trafficInfo = trafficInfo;
    }

    public void removeTrafficInfo() {
        if (trafficInfo != null) {
            trafficInfo.setRoute(null);
            this.trafficInfo = null;
        }
    }

    public TrafficInfo getTrafficInfo() {
        return trafficInfo;
    }

    public void setTrafficInfo(TrafficInfo trafficInfo) {
        this.trafficInfo = trafficInfo;
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

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public void setDestination(Vertex destination) {
        this.destination = destination;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (routeId != null ? !routeId.equals(edge.routeId) : edge.routeId != null) return false;
        if (source != null ? !source.equals(edge.source) : edge.source != null) return false;
        return destination != null ? destination.equals(edge.destination) : edge.destination == null;

    }
}
