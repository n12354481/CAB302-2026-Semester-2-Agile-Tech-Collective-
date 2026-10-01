package com.example.cab302project.Dashboard.Ollama;

/**
 * This is an interface which defines the callback fro receivng the request in an asynchronous response.
 */
public interface ResponseReceived {
    public void onResponseReceived(Response response);
}