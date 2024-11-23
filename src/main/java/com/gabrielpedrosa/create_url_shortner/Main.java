package com.gabrielpedrosa.create_url_shortner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SuppressWarnings("FieldCanBeLocal")
public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {
    private final String BUCKET_NAME = "gabrielpedrosa-rockeatseat-url-shortener";

    @Override
    public Map<String, String> handleRequest(Map<String, Object> request, Context context) {
        URLService urlService = new URLServiceImpl(
                new ObjectMapper(),
                S3Client.builder().build(),
                BUCKET_NAME);

        return urlService.processRequest(request);
    }
}