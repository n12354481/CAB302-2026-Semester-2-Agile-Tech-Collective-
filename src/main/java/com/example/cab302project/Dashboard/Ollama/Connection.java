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

public class Connection {
    private static final String USERAGENT = "OLLAMA FETCHER";
    public static final Logger logger = Logger.getLogger(Connection.class.getName());
    private final String apiURL;

    public Connection(String apiURL) {
        this.apiURL = apiURL;
    }

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

    public Response fetchOllamaResponse(String simpleJsonObj) {
        HttpURLConnection conn = null;
        String output = null;
        OutputStream os = null;
        Response response = null;

        try {
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

    public Response fetchOllamaResponse(String model, String prompt) {
        // tested with model llava v1.6 - for documentation on how to format the JSON request https://ollama.com/library/llava

        //String imageContents = JollamaImageUtil.imageToBase64(image);
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