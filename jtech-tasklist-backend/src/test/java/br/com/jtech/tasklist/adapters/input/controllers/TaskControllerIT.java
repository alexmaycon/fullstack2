package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskControllerIT extends AbstractIntegrationTest {

    private String createList(String token, String name) throws Exception {
        ObjectNode list = objectMapper.createObjectNode().put("name", name);
        MvcResult result = mockMvc.perform(post("/api/v1/task-lists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/tasks")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCrudTaskAndFilterByList() throws Exception {
        String token = registerAndLogin("Alex", uniqueEmail("task"), "secret123");
        String listId = createList(token, "L1");

        ObjectNode body = objectMapper.createObjectNode()
                .put("title", "Estudar")
                .put("description", "Spring Boot")
                .put("taskListId", listId);
        MvcResult created = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Estudar"))
                .andExpect(jsonPath("$.completed").value(false))
                .andReturn();
        String taskId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/v1/tasks?taskListId=" + listId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(taskId));

        ObjectNode update = objectMapper.createObjectNode()
                .put("title", "Estudar mais")
                .put("completed", true)
                .put("taskListId", listId);
        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Estudar mais"))
                .andExpect(jsonPath("$.completed").value(true));

        mockMvc.perform(delete("/api/v1/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn409WhenDuplicateTitleInSameList() throws Exception {
        String token = registerAndLogin("Alex", uniqueEmail("dupt"), "secret123");
        String listId = createList(token, "L1");
        ObjectNode body = objectMapper.createObjectNode()
                .put("title", "T1")
                .put("taskListId", listId);
        mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn404WhenCreatingTaskOnOtherUsersList() throws Exception {
        String tokenA = registerAndLogin("UserA", uniqueEmail("a"), "secret123");
        String listId = createList(tokenA, "OnlyA");

        String tokenB = registerAndLogin("UserB", uniqueEmail("b"), "secret123");
        ObjectNode body = objectMapper.createObjectNode()
                .put("title", "Hack")
                .put("taskListId", listId);
        mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }
}
