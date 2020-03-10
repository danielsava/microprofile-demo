package com.rafabene.microprofile.client;

import org.eclipse.microprofile.rest.client.ext.*;

import javax.ws.rs.core.*;

public class PreferenceExceptionWrapper implements ResponseExceptionMapper<PreferenceException> {


    @Override
    public PreferenceException toThrowable(final Response response) {
        return new PreferenceException(response.getStatus(), response.readEntity(String.class));
    }
}
