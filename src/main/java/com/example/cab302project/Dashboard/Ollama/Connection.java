package com.example.cab302project.Dashboard.Ollama;
import com.google.gson.Gson;
import javafx.application.Platform;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class which initiates the Ollama connection.
 */
public class Connection {
    private static final String USERAGENT = "OLLAMA FETCHER";
    public static final Logger logger = Logger.getLogger(Connection.class.getName());
    private final String apiURL;

    /**
     * Constructs the api url which is needed to get the connection.
     * @param apiURL: URL being connected to.
     */
    public Connection(String apiURL) {
        this.apiURL = apiURL;
    }

    /**
     * This method aims to create a HTTP connection for the provided API URL to connect the Ollama.
     * @return: Returns the received connection from Ollama.
     */
    protected HttpURLConnection getConnection() {
        HttpURLConnection conn = null;

        try {
            URL urlObj = new URI(apiURL).toURL(); //URL constructor is deprecated since java20
            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestProperty("User-Agent", USERAGENT);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Error getting connection", ex);
        }
        return conn;
    }

    /**
     * This method sends a JSON request to Ollama and aims to fetch the result.
     * @param simpleJsonObj: The JSON format request given to fetch the request.
     * @return: Returns the response provided by Ollama.
     */
    public Response fetchOllamaResponse(String simpleJsonObj) {

        HttpURLConnection conn = null;
        String output = null;
        OutputStream os = null;
        Response response = null;

        try {
            //Connects to the API url.
            logger.info("Attempting POST on " + apiURL);
            conn = getConnection();
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);

            os = conn.getOutputStream();

            os.write(
                    simpleJsonObj.getBytes((
                            StandardCharsets.UTF_8
                            ))
            );

            os.flush();
            os.close();
            os = null;

            //Gets the Ollama response
            int code = conn.getResponseCode();
            logger.info("Response: " + code);

            if(code == HttpURLConnection.HTTP_OK) {
                output = readConnInput(conn);
                response = Response.fromJson(output);
            }
        } catch(Exception e)
        {
            logger.log(Level.WARNING, "Error", e);
        } finally {
            //Ensures that the connection is closed if an Error occurred.
            if(os !=null) {
                try {
                    os.close();
                } catch(Exception e)
                {

                }
            }
        }
        return response;
    }

    /**
     * This method aims to create a JSON formatted request to fethc the ollama response.
     * @param model: The model being connected
     * @param prompt: The prompt being sent to retrieve the request.
     * @return: returns the response obtained.
     */
    public Response fetchOllamaResponse(String model, String prompt) {
        Gson gson = new Gson();

        String simpleJsonObj = String.format("""
                 {
                   "model": "%s",
                   "prompt": "%s",
                   "stream": false
                 }
                """, model, prompt);

        return fetchOllamaResponse(simpleJsonObj);
    }

    /**
     * This method aims to fetch an asynchronous response of the Ollama.
     * This is basically used to start a new Thread so that the main application code isn't blocked while
     *  waiting for Ollama to respond.
     * @param model: The model being called.
     * @param prompt: The prompt being sent as a request for the response.
     * @param responseListener: The listener which listens for the ollama's response.
     */
    public void fetchAsynchronousOllamaResponse(String model, String prompt, ResponseReceived responseListener) {
        Thread thread = new Thread() {
            public void run() {
                Response response = fetchOllamaResponse(model, prompt);
                responseListener.onResponseReceived(response);
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * This method aims to read the response received from the HTTP connection.
     * @param conn: The connection containing Ollama's response.
     * @return: Returns the contents as a String.
     */
    protected String readConnInput(HttpURLConnection conn) {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer(10000);
        try {
            is = conn.getInputStream();
            isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            br = new BufferedReader(isr);

            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Error reading response", ex);
        } finally {
            if (is != null) try {
                is.close();
            } catch (Exception ex) {
            }
            if (isr != null) try {
                isr.close();
            } catch (Exception ex) {
            }
            if (br != null) try {
                br.close();
            } catch (Exception ex) {
            }
        }

        return sb.toString();
    }
}