package com.personaltraining.app;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authEndpointsRegisterRejectDuplicatesAndLogin() throws Exception {
        String email = uniqueEmail("auth");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Daniel",
                                "email", email,
                                "password", "123456"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.email").value(email));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Daniel",
                                "email", email,
                                "password", "123456"
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already registered"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", email,
                                "password", "123456"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.email").value(email));

        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedTrainingEndpointsSupportTheMainUserFlow() throws Exception {
        String token = registerAndGetToken(uniqueEmail("flow"));
        String otherUserToken = registerAndGetToken(uniqueEmail("other"));

        Long activityId = createActivity(token, "Rodaje suave");

        mockMvc.perform(get("/api/activities/{id}", activityId)
                        .header("Authorization", bearer(otherUserToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/activities")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Rodaje suave"));

        mockMvc.perform(get("/api/activities/{id}", activityId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(activityId))
                .andExpect(jsonPath("$.averagePace").value(5.50));

        mockMvc.perform(put("/api/activities/{id}", activityId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(activityPayload("Rodaje editado"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Rodaje editado"));

        Long workoutId = createPlannedWorkout(token, "Series");

        mockMvc.perform(get("/api/planned-workouts")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Series"));

        mockMvc.perform(put("/api/planned-workouts/{id}", workoutId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(plannedWorkoutPayload("Series editadas"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Series editadas"));

        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActivities").value(1))
                .andExpect(jsonPath("$.totalDistanceKm").value(8.5))
                .andExpect(jsonPath("$.totalDurationMinutes").value(45))
                .andExpect(jsonPath("$.upcomingWorkouts", hasSize(1)));

        mockMvc.perform(get("/api/stats/monthly")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].distanceKm").value(8.5))
                .andExpect(jsonPath("$[0].durationMinutes").value(45));

        mockMvc.perform(get("/api/stats/sports")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sportType").value("RUNNING"))
                .andExpect(jsonPath("$[0].activities").value(1));

        mockMvc.perform(get("/api/stats/pace-summary")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averagePace").value(5.50));

        mockMvc.perform(patch("/api/planned-workouts/{id}/status", workoutId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "DONE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));

        mockMvc.perform(delete("/api/planned-workouts/{id}", workoutId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/activities/{id}", activityId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/activities")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private Long createActivity(String token, String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/activities")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(activityPayload(title))))
                .andExpect(status().isCreated())
                .andReturn();

        return readId(result);
    }

    private Long createPlannedWorkout(String token, String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/planned-workouts")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(plannedWorkoutPayload(title))))
                .andExpect(status().isCreated())
                .andReturn();

        return readId(result);
    }

    private String registerAndGetToken(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Test User",
                                "email", email,
                                "password", "123456"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private Map<String, Object> activityPayload(String title) {
        return Map.of(
                "activityDate", LocalDate.now().toString(),
                "sportType", "RUNNING",
                "title", title,
                "description", "Entrenamiento de prueba",
                "durationMinutes", 45,
                "distanceKm", new BigDecimal("8.50"),
                "averagePace", new BigDecimal("5.50"),
                "location", "Parque",
                "notes", "Buenas sensaciones"
        );
    }

    private Map<String, Object> plannedWorkoutPayload(String title) {
        return Map.of(
                "plannedDate", LocalDate.now().plusDays(1).toString(),
                "title", title,
                "description", "Entrenamiento planificado de prueba",
                "sportType", "RUNNING",
                "targetDurationMinutes", 50,
                "targetDistanceKm", new BigDecimal("9.00"),
                "status", "PENDING"
        );
    }

    private Long readId(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    private String uniqueEmail(String prefix) {
        return prefix + "-" + System.nanoTime() + "@example.com";
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
