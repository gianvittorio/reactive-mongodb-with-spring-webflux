package com.gianvittorio.reactivespringwebflux.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(final ServerRequest request, final ErrorAttributeOptions options) {
        return this.assembleErrors(request);
    }

    private Map<String, Object> assembleErrors(final ServerRequest request) {

        final Map<String, Object> errorAttributes = new LinkedHashMap<>();

        final Throwable error = this.getError(request);

        if (error instanceof OptimisticLockingFailureException) {
            errorAttributes.put("errorCode", 400);
            errorAttributes.put("errorMessage", "Version Mismatch");
        } else {
            errorAttributes.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR);
            errorAttributes.put("errorMessage", "INTERNAL SERVER ERROR");
        }

        return errorAttributes;
    }
}
