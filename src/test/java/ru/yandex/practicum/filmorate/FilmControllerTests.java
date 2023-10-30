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
public class FilmControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAddFilm() throws Exception {
        String filmJson = "{\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\", \"duration\":100}";

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("nisi eiusmod"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("adipisicing"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("1967-03-25"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(100))
                .andDo(print());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/films"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    public void testUpdateFilm() throws Exception {
        String filmJson = "{\"id\":1,\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\", \"duration\":100}";

        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("nisi eiusmod"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("adipisicing"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("1967-03-25"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(100))
                .andDo(print());
    }
}
