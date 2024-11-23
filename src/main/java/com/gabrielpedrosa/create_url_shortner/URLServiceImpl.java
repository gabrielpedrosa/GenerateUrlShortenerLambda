package com.gabrielpedrosa.create_url_shortner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the URLService interface to handle URL shortening requests.
 * This class processes the request, generates a shortened URL, and stores the URL data in an S3 bucket.
 */
@SuppressWarnings("FieldCanBeLocal")
@AllArgsConstructor
public class URLServiceImpl implements URLService {

    // Keys for response and request map
    private final String RESPONSE_CODE_KEY = "code";
    private final String REQUEST_BODY_KEY = "body";
    private final String BODY_ORIGINAL_URL_KEY = "originalUrl";
    private final String BODY_EXPIRATION_TIME_KEY = "expirationTime";

    // Dependencies for JSON processing and S3 client
    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private final String bucketName;

    // Maximum length for the shortened URL code
    private final int MAX_UUID_LENGTH = 8;

    // Extension for URL data files
    private final String URL_DATA_EXTENSION = ".json";

    /**
     * Processes the incoming request to generate a shortened URL.
     *
     * @param request The request map containing the original URL and expiration time.
     * @return A map containing the response code for the shortened URL.
     */
    @Override
    public Map<String, String> processRequest(Map<String, Object> request) {
        String body = extractBodyRequest(request);

        Map<String, String> bodyMap = parseRequestBody(body);

        String shortUrlCode = generateShortUrlCode();

        URLData urlData = createUrlData(bodyMap);

        saveUrlDataToS3(shortUrlCode, urlData);

        return createResponse(shortUrlCode);
    }

    /**
     * Extracts the body from the request map.
     *
     * @param request The request map.
     * @return The body as a string.
     */
    private String extractBodyRequest(@NotNull Map<String, Object> request) {
        return request.get(REQUEST_BODY_KEY).toString();
    }

    /**
     * Parses the request body JSON string into a map.
     *
     * @param body The request body JSON string.
     * @return A map containing the parsed request body.
     */
    private Map<String, String> parseRequestBody(String body) {
        try {
            return objectMapper.readValue(body, new TypeReference<>() {});
        } catch (Exception exception) {
            throw new RuntimeException("Error parsing JSON body: " + exception.getMessage(), exception);
        }
    }

    /**
     * Creates a URLData object from the request body map.
     *
     * @param bodyMap The map containing the request body.
     * @return A URLData object.
     */
    private @NotNull URLData createUrlData(@NotNull Map<String, String> bodyMap) {
        String originalUrl = bodyMap.get(BODY_ORIGINAL_URL_KEY);
        String expirationTimeInString = bodyMap.get(BODY_EXPIRATION_TIME_KEY);

        return new URLData(originalUrl, expirationTimeInString);
    }

    /**
     * Saves the URL data to an S3 bucket.
     *
     * @param shortUrlCode The shortened URL code.
     * @param urlData The URL data to be saved.
     */
    private void saveUrlDataToS3(String shortUrlCode, URLData urlData) {
        try {
            String urlDataJson = objectMapper.writeValueAsString(urlData);
            PutObjectRequest requestObject = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(shortUrlCode + ".json")
                    .build();
            s3Client.putObject(requestObject, RequestBody.fromString(urlDataJson));
        } catch (Exception exception) {
            throw new RuntimeException("Cannot save URL data on S3: " + exception.getMessage(), exception);
        }
    }
    /**
     * Creates a response map containing the shortened URL code.
     *
     * @param shortUrlCode The shortened URL code.
     * @return A map containing the response code.
     */
    private @NotNull Map<String, String> createResponse(String shortUrlCode) {
        Map<String, String> response = new HashMap<>();
        response.put(RESPONSE_CODE_KEY, shortUrlCode);
        return response;
    }

    /**
     * Generates a shortened URL code.
     *
     * @return A shortened URL code.
     */
    private @NotNull String generateShortUrlCode() {
        return UUID.randomUUID().toString().substring(0, MAX_UUID_LENGTH);
    }

    @Contract(pure = true)
    private @NotNull String parseShortUrlCodeToS3Key(String shortUrlCode) {
        return shortUrlCode + URL_DATA_EXTENSION;
    }
}
