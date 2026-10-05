package com.example.cab302project.Dashboard.CommunityInsights;

import com.example.cab302project.Dashboard.Ollama.Connection;
import com.example.cab302project.Dashboard.Ollama.ResponseReceived;
import com.example.cab302project.Dashboard.Recommendations.RecommendationData;
import com.example.cab302project.MoodForm.CheckIn;

import java.util.Map;

/**
 * A class which aims to generate the Ollama response for the community data.
 */
public class CommunityService {
    private Connection ollamaConnection;

    /**
     * A constructs which constructs the ollama connection.
     */
    public CommunityService() {
        ollamaConnection = new Connection("http://localhost:11434/api/generate");
    }

    /**
     * A method which aims to fetch the ollama's response based ont he prompt given.
     * @param data: The community insights data for which the response is being built.
     * @param responseReceived: The response listener of ollama.
     */
    public void generateInsights (CommunityInsightsData data, ResponseReceived responseReceived) {
        String prompt = buildPrompt(data);
        System.out.println("Prompt is: ");
        System.out.println(prompt);

        //This fetches the response.
        ollamaConnection.fetchAsynchronousOllamaResponse("llama3.2", prompt, response -> {
            System.out.println("Ollama response: ");
            if(response == null) {
                System.out.println("Response null");
            } else {
                System.out.println(response.getResponse());

            }

            responseReceived.onResponseReceived(response);
        });
    }


    /**
     * A method which aims to construct a system prompt for passing to the ollama when generating a response.
     * @param data: The community insights data for which the service is being built.
     * @return Returns the system prompt for this service.
     */
    private String buildPrompt(CommunityInsightsData data)
    {
        StringBuilder prompt = new StringBuilder();

        //The main prompt which guides the Ollama on what to generate.
        prompt.append("You are a wellbeing assistant for a university student. ");
        prompt.append("You are generating COMMUNITY insights. ");
        prompt.append("The data provided represents aggregated data from users who have chosen to participate in community statistics. ");

        prompt.append("Generate exactly three pieces of content: ");
        prompt.append("1. A short motivational quote inspired by the community's wellbeing data. ");
        prompt.append("2. One short factual community insight. ");
        prompt.append("3. One short factual community insight. ");
        prompt.append("The motivational quote should: be positive and encouraging, relate generally to wellbeing, ");
        prompt.append("not claim that every student experiences the same thing");
        prompt.append("The community insights should: ");
        prompt.append("describe patterns in the provided data, be factual and based ONLY on the provided statistics");
        prompt.append(", not identify or refer to individual users, not give medical advice, ");
        prompt.append(" be suitable for a university wellbeing dashboard, ");
        prompt.append(" be no more than 25 words each");
        prompt.append("None of the generated content should contain any unsolicited advice. It should NOT identify individual users.");
        prompt.append("Return only those three lines. DO NOT NUMBER THEM OR USE BULLET POINTS.");
        prompt.append(" Community data: ");

        //Adding the community data through the getters.
        prompt.append("Participating users: ")
                .append(data.getParticipatingUsers());

        prompt.append(", Average sleep: ")
                .append(data.getAvgSleep());

        prompt.append(", Average study stress: ")
                .append(data.getAvgStudyStress());

        prompt.append(", Average water: ")
                .append(data.getAvgWater());

        prompt.append(", Total activity minutes: ")
                .append(data.getTotalActivityMinutes());

        prompt.append(", Most popular activity category: ")
                .append(data.getMostPopularActivity());

        prompt.append(", Most popular mood: ")
                .append(data.getMostPopularMood());

        return prompt.toString();
    }
}






