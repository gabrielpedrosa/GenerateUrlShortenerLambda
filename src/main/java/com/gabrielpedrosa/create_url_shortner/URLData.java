package com.gabrielpedrosa.create_url_shortner;

import lombok.Getter;
import lombok.Setter;

/**
 * Class representing URL data with original URL and expiration time.
 */
@Getter
@Setter
public class URLData {
    private String originalUrl;
    private long expirationTime;

    /**
     * Constructs a new URLData object.
     *
     * @param originalUrl     the original URL
     * @param expirationTime  the expiration time as a string
     */
    public URLData(String originalUrl, String expirationTime) {
        this.originalUrl = originalUrl;
        this.expirationTime = convertStringToLong(expirationTime);
    }

    /**
     * Converts a string to a long.
     *
     * @param expirationTime  the string to be converted
     * @return the converted long value
     */
    private long convertStringToLong(String expirationTime) {
        return Long.parseLong(expirationTime);
    }
}
