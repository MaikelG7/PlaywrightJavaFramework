package tests;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

public class APITesting {
    @Test
    public void e2eApiTest() {
        // Login
        HashMap<Object, Object> loginPayload = new HashMap<>();
        loginPayload.put("email", "rahulshetty1@yahoo.com");
        loginPayload.put("password", "Magiclife1!");

        Playwright playwright = Playwright.create();
        APIRequestContext apiRequest = playwright.request().newContext();
        APIResponse loginResponse = apiRequest.post("https://api.eventhub.rahulshettyacademy.com/api/auth/login",
                RequestOptions.create().setData(loginPayload));
        Assert.assertTrue(loginResponse.ok());

        String token = JsonPath.read(loginResponse.text(), "$.token");
        System.out.println("Login successful, token: " + token);

        // Create a new event
        HashMap<Object, Object> createEventPayload = new HashMap<>();
        String title = "API Test Event Playwright";
        createEventPayload.put("title", title);
        createEventPayload.put("description", "API Testing details");
        createEventPayload.put("category", "Sports");
        createEventPayload.put("venue", "API Square Garden");
        createEventPayload.put("city", "New York");
        createEventPayload.put("eventDate", "2026-12-29T13:52:00.000Z");
        createEventPayload.put("price", "100");
        createEventPayload.put("totalSeats", "400");
        APIResponse eventResponse = apiRequest.post("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create().setHeader("Authorization", "Bearer " + token).setData(createEventPayload));
        Assert.assertTrue(eventResponse.ok(), "Event creation failed!");
        int eventId = JsonPath.read(eventResponse.text(), "$.data.id");
        System.out.println("Event created, id: " + eventId);

        // Check the newly created event
        APIResponse retrieveEvents = apiRequest.get("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create().setQueryParam("page", "1").setQueryParam("limit", "12")
                        .setHeader("Authorization", "Bearer " + token));
        Assert.assertTrue(retrieveEvents.ok());
        List<Integer> allEventIds = JsonPath.read(retrieveEvents.text(), "$.data[*].id");
        Assert.assertTrue(allEventIds.contains(eventId), "Created event should appear in events list");

       // Delete event and check deletion
       APIResponse deleteResponse = apiRequest.delete("https://api.eventhub.rahulshettyacademy.com/api/events/" + eventId,
               RequestOptions.create().setHeader("Authorization", "Bearer " + token));
       Assert.assertTrue(deleteResponse.ok());
       retrieveEvents = apiRequest.get("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create().setQueryParam("page", "1").setQueryParam("limit", "12")
                        .setHeader("Authorization", "Bearer " + token));
       allEventIds = JsonPath.read(retrieveEvents.text(), "$.data[*].id");
       Assert.assertFalse(allEventIds.contains(eventId), "Deleted event should not appear in events list");
       System.out.println("Event successfully deleted");
    }
}
