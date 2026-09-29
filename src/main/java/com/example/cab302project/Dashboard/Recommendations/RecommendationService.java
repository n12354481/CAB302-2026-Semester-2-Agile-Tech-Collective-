package com.example.cab302project.Dashboard.Recommendations;

import com.example.cab302project.Dashboard.Ollama.Connection;
import com.example.cab302project.Dashboard.Ollama.Response;
import com.example.cab302project.Dashboard.Ollama.ResponseReceived;
import com.example.cab302project.MoodForm.CheckIn;

import java.util.List;
import java.util.Map;

public class RecommendationService {
    private Connection ollamaConnection;

    public RecommendationService() {
        ollamaConnection = new Connection("http://localhost:11434/api/generate");
    }

//    public Response generateRecommendations (List<Map<String, Object>> activityData, List<CheckIn> checkinData) {
//        String prompt = buildPrompt(activityData, checkinData);
//        return ollamaConnection.fetchOllamaResponse("llama3.2", prompt);
//    }

//    public Response generateRecommendations (RecommendationData data) {
//        String prompt = buildPrompt(data);
//        return ollamaConnection.fetchOllamaResponse("llama3.2", prompt);
//    }

    public void generateRecommendations (RecommendationData data, ResponseReceived responseReceived) {
        String prompt = buildPrompt(data);
        System.out.println("Prompt is: ");
        System.out.println(prompt);
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

    private String buildPrompt(RecommendationData data)
    {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a wellbeing assisstant for a university student. ");
        prompt.append("Based only on the student's recent activity and checkin data, ");
        prompt.append("provide exactly three short, practical wellbeing recommendations. ");

        prompt.append("Each recommendation should: be supportive and practical, be specific to the student's data, be no more than 25 words, be suitable for displaying on a dashboard. ");
        prompt.append("Return ONLY the three recommendations, one per line. ");
        prompt.append("Do not number them. Do not use bullet points.  Recent activity:");

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
//    private String buildPrompt(List<Map<String, Object>> activityData, List<CheckIn> checkinData)
//    {
//        StringBuilder prompt = new StringBuilder();
//
//        prompt.append("""
//                You are a wellbeing assisstant for a university student.
//
//                Based only on the student's recent activity and checkin data,
//                provide exactly three short, practical wellbeing recommendations.
//
//                Each recommendation should:
//                - be supportive and practical
//                - be specific to the student's data
//                - be no more than 25 words
//                - be suitable for displaying on a dashboard
//
//                Return ONLY the three recommendations, one per line.
//                Do not number them.
//                Do not use bullet points.
//
//                Recent activity:
//                """);
//
//        for(Map<String, Object> activity : activityData)
//        {
//            prompt.append("Activity: ")
//                    .append(activity.get("name"));
//
//            prompt.append((", Minutes: "))
//                    .append(activity.get("minutes"));
//
//            if(activity.get("category") != null) {
//                prompt.append(", Category: ")
//                        .append(activity.get("category"));
//            }
//        }
//
//        prompt.append("\n\nRecent check-in:\n");
//
//        for(CheckIn checkIn : checkinData)
//        {
//            prompt.append("\nDate: ")
//                    .append(checkIn.getCheckinDate());
//            prompt.append(", Emotion: ")
//                    .append(checkIn.getEmotionToday());
//            prompt.append(", Sleep: ")
//                    .append(checkIn.getSleep());
//            prompt.append(", Water: ")
//                    .append(checkIn.getWater());
//            prompt.append(", Study Stress: ")
//                    .append(checkIn.getStudyStress());
//            prompt.append(", Mood: ")
//                    .append(checkIn.getMoods());
//        }
//
//        return prompt.toString();
//    }
}
