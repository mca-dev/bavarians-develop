package com.bavarians.graphql;


import graphql.schema.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

@Component
public class DateScalarType extends GraphQLScalarType {

    DateScalarType() {
        super("Date", "Date value", new Coercing<Date,String>() {
            @Override
            public String serialize(Object o) throws CoercingSerializeException {
                return Instant.ofEpochMilli(((Date)o).getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate().toString();
            }

            @Override
            public Date parseValue(Object o) throws CoercingParseValueException {
                return null;
            }

            @Override
            public Date parseLiteral(Object o) throws CoercingParseLiteralException {
                return null;
            }
            // ...
        });
    }

}
