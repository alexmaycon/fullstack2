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
class TaskListControllerIT extends AbstractIntegrationTest {

    @Test
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/task-lists"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCrudTaskList() throws Exception {
        String token = registerAndLogin("Alex", uniqueEmail("tl"), "secret123");

        ObjectNode body = objectMapper.createObjectNode().put("name", "Trabalho");
        MvcResult created = mockMvc.perform(post("/api/v1/task-lists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Trabalho"))
                .andReturn();
        String id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/v1/task-lists").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));

        ObjectNode rename = objectMapper.createObjectNode().put("name", "Casa");
        mockMvc.perform(put("/api/v1/task-lists/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rename)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Casa"));

        mockMvc.perform(delete("/api/v1/task-lists/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn409WhenDuplicateName() throws Exception {
        String token = registerAndLogin("Alex", uniqueEmail("dup"), "secret123");
        ObjectNode body = objectMapper.createObjectNode().put("name", "Mesmo");
        mockMvc.perform(post("/api/v1/task-lists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/task-lists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn409WhenDeletingListWithTasks() throws Exception {
        String token = registerAndLogin("Alex", uniqueEmail("del"), "secret123");
        ObjectNode list = objectMapper.createObjectNode().put("name", "ListaX");
        MvcResult created = mockMvc.perform(post("/api/v1/task-lists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isCreated()).andReturn();
        String listId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        ObjectNode task = objectMapper.createObjectNode()
                .put("title", "Tarefa1")
                .put("taskListId", listId);
        mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/task-lists/" + listId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict());
    }
}
