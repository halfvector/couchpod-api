package com.couchpod.exceptions;

import com.google.inject.Singleton;
import io.dropwizard.jersey.errors.LoggingExceptionMapper;
import org.skife.jdbi.v2.exceptions.DBIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * JDBI exceptions are verbose and long.
 * Don't try to serialize DBIException into the json response, as they may break responses.
 */
@Provider
@Singleton
public class DbiExceptionMapper extends LoggingExceptionMapper<DBIException> {
    private static Logger log = LoggerFactory.getLogger(DbiExceptionMapper.class);

    protected Throwable getInnerMostException(Throwable exception) {
        Throwable cause = exception;
        Throwable innerMostCause = exception;
        while (cause != null) {
            innerMostCause = cause;
            cause = cause.getCause();
        }
        return innerMostCause;
    }

    @Override
    protected String formatLogMessage(long id, Throwable exception) {
        return String.format("[#%016x] Error handling request.\n", id);
    }

    @Override
    protected void logException(long id, DBIException exception) {
        log.error(formatLogMessage(id, exception), exception);
    }

    @Override
    public Response toResponse(DBIException exception) {
        final long id = ThreadLocalRandom.current().nextLong();
        logException(id, exception);

        Throwable innerMostException = getInnerMostException(exception);

        HashMap<Object, Object> details = new HashMap<>();
        details.put("status", 500);
        details.put("message", String.format("Error handling request. Logged with requestId=[#%016x]", id));
        details.put("developer_message", innerMostException.getMessage());

        return Response
                .serverError()
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(details)
                .build();
    }
}

