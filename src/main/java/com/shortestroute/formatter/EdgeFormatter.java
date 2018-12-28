package com.shortestroute.formatter;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import com.shortestroute.model.Edge;
import com.shortestroute.repository.EdgeRepository;

@Component
public class EdgeFormatter implements Formatter<Edge> {

    @Autowired
    EdgeRepository edgeRepository;

    @Override
    public String print(Edge object, Locale locale) {
        return String.valueOf(object.getId());
    }

    @Override
    public Edge parse(String id, Locale locale) throws ParseException {
        return edgeRepository.selectUnique(Long.valueOf(id));
    }
}
