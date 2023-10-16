/*
package com.OnlineAuction.Controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final String URI_TO_USERS = "/api/users";
    private final String username = "";
    private final String password = "";
    private final String credentials =  username + ":" + password;
    private final String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetUsers_ReturnListUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URI_TO_USERS))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(4));
    }

    @Test
    void testGetById_ReturnUserDTO() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URI_TO_USERS + "/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.first_name").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last_name").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").value("not test"));
    }

    @Test
    void testGetById_ReturnNotFoundStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URI_TO_USERS + "/99999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testCreate_ReturnUserDTO() throws Exception {
        String first_name = "14.10", last_name = "14.10", email = "test1@gmail.com", password = "test", image = "path";
        String body = "{\"first_name\": \"" + first_name + "\", \"last_name\": \"" + last_name + "\", \"email\": \"" + email + "\", \"password\": \"" + password +
                "\", \"image\": \"" + image +  "\"}";
        mockMvc.perform(MockMvcRequestBuilders
                .post(URI_TO_USERS)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.first_name").value(first_name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last_name").value(last_name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").value(image));
    }

    @Test
    void testCreate_ReturnBadRequestStatus() throws Exception {
        String first_name = "14.10", last_name = "14.10", email = "test2@gmail.com", password = "test", image = "path";
        String body = "{\"first_name\": \"" + first_name + "\", \"last_name\": \"" + last_name + "\", \"email\": \"" + email + "\", \"password\": \""
                + password + "\"}";
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URI_TO_USERS)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}*/
