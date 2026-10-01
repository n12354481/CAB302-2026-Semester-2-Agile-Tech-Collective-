package com.example.cab302project.Dashboard.Ollama;
import com.google.gson.Gson;

/**
 * This class aims to represent the response given by Ollama.
 * It aims to store the relevant ollama json response so that the rest of the classes can access them.
 */
public class Response {
    private String model;
    private String created_at;
    private String response;
    private String done;


    public String getResponse() {
        return response;
    }

    public static Response fromJson(String body) {
        //for documentation of how to decode JSON response https://github.com/ollama/ollama/blob/main/docs/api.md
        Gson gson = new Gson();
        Response response = gson.fromJson(body, Response.class);
        return response;
    }
}
