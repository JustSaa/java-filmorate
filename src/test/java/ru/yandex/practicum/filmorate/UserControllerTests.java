package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAddUser() throws Exception {
        String userJson = "{\"login\":\"dolore\",\"email\":\"mail@mail.ru\",\"name\":\"Nick Name\", \"birthday\":\"1946-08-20\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("dolore"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Nick Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1946-08-20"))
                .andDo(print());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    public void testUpdateUser() throws Exception {
        String userJson = "{\"id\":1,\"login\":\"dolore\",\"email\":\"mail@mail.ru\",\"name\":\"Nick Name\", \"birthday\":\"1946-08-20\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("dolore"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Nick Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1946-08-20"))
                .andDo(print());
    }
}
