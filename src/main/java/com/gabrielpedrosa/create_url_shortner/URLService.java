package com.gabrielpedrosa.create_url_shortner;

import java.util.Map;

/**
 * Interface for URL Service to process URL shortening requests.
 */
public interface URLService {
    /**
     * Processes the incoming request to generate a shortened URL.
     *
     * @param request The request map containing the original URL and expiration time.
     * @return A map containing the response code for the shortened URL.
     */
    Map<String, String> processRequest(Map<String, Object> request);
}
