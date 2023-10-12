package com.pleisto;

import java.util.Map;
import lombok.Data;

@Data
public class PresignedRequest {
    /**
     * HTTP method of this request.
     */
    private final String method;

    /**
     * URI of this request.
     */
    private final String uri;

    /**
     * HTTP headers of this request.
     */
    private final Map<String, String> headers;
}
