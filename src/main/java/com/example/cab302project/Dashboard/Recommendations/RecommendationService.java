package com.example.cab302project.Dashboard.Recommendations;

import com.example.cab302project.Dashboard.Ollama.Connection;
import com.example.cab302project.Dashboard.Ollama.Response;
import com.example.cab302project.Dashboard.Ollama.ResponseReceived;
import com.example.cab302project.MoodForm.CheckIn;

import java.util.List;
import java.util.Map;

/**
 * This class aims to generate the response of Ollama for personalised user recommendations.
 */
public class RecommendationService {
    //Creates the ollama connection.
    private Connection ollamaConnection;

    /**
     * Constructs the connection for ollama.
     */
    public RecommendationService() {
        ollamaConnection = new Connection("http://localhost:11434/api/generate");
    }

    /**
     * This method aims to generate the Ollama response based on a defined system prompt.
     * @param data: The recommendation data given to the model.
     * @param responseReceived: The response listener for ollama.
     */
    public void generateRecommendations (RecommendationData data, ResponseReceived responseReceived) {
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
     * This method aims to build the system prompt for the recommendation data.
     * @param data: The recommendation data of the user.
     * @return: Returns the system prompt to be used when fetching a response.
     */
    private String buildPrompt(RecommendationData data)
    {
        //The prompt which defines the rules of the AI when generating the recommendation.
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a wellbeing assisstant for a university student. ");
        prompt.append("Based only on the student's recent activity and checkin data, ");
        prompt.append("provide exactly three short, practical wellbeing recommendations. ");

        prompt.append("If no recent activity or check-in data is provided, ");
        prompt.append("provide only exactly three generic short, practical wellbeing recommendations suitable for a university student. Do not give any additional messages.");
        prompt.append("Do not claim the recommendations are personal when no data is available.");

        prompt.append("Each recommendation should: be supportive and practical, be specific to the student's data, be no more than 25 words, be suitable for displaying on a dashboard. ");
        prompt.append("Return ONLY the three recommendations, one per line. ");
        prompt.append("Do not number them. Do not use bullet points.  Recent activity:");

        //The activity data of the user put into the prompt.
        for(Map<String, Object> activity : data.getActivityData())
        {
            prompt.append("Activity: ")
                    .append(activity.get("name"));

            prompt.append((", Minutes: "))
                    .append(activity.get("minutes"));

            if(activity.get("category") != null) {
                prompt.append(", Category: ")
                        .append(activity.get("category"));
            }

            prompt.append(". ");
        }

        //The checkin data of the user.
        prompt.append("Recent check-in: ");

        for(CheckIn checkIn : data.getCheckinData())
        {
            prompt.append("Date: ")
                    .append(checkIn.getCheckinDate());
            prompt.append(", Emotion: ")
                    .append(checkIn.getEmotionToday());
            prompt.append(", Sleep: ")
                    .append(checkIn.getSleep());
            prompt.append(", Water: ")
                    .append(checkIn.getWater());
            prompt.append(", Study Stress: ")
                    .append(checkIn.getStudyStress());
            prompt.append(", Mood: ")
                    .append(checkIn.getMoods());
        }

        return prompt.toString();
    }
}
