package com.shortestroute.formatter;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import com.shortestroute.model.TrafficInfo;
import com.shortestroute.repository.TrafficInfoRepository;

@Component
public class TrafficFormatter implements Formatter<TrafficInfo> {

    @Autowired
    TrafficInfoRepository trafficInfoRepository;

    @Override
    public String print(TrafficInfo object, Locale locale) {
        return String.valueOf(object.getId());
    }

    @Override
    public TrafficInfo parse(String id, Locale locale) throws ParseException {
        return trafficInfoRepository.selectUnique(Long.valueOf(id));
    }
}
