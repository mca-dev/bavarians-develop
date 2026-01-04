package com.bavarians.service;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DateUtilToDateSQLConverter implements Converter<java.util.Date, Date> {

    @Override
    public Date convert(java.util.Date source) {
        return new Date(source.getTime());
    }
}