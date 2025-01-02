package com.example.demo.Controller;

import com.example.demo.controller.usercontroller;
import com.example.demo.model.user;
import com.example.demo.userrepo.userrepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableJpaRepositories(basePackages = "com.example.demo.userrepo")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private userrepo repo;

    private user testUser;

    @BeforeEach
    void setUp() {
        testUser = new user(1L, "Trung", "Ninh Bình", 21);
    }

    @Test
    void testGetAllUsers_WhenUsersExist() throws Exception {
        List<user> users = Arrays.asList(
                new user(1L, "Trung", "Ninh Bình", 21),
                new user(3L, "Bắc", "Bắc Ninh", 32)
        );
        Mockito.when(repo.findAll()).thenReturn(users);
        mockMvc.perform(get("/getbyallid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message", is("Users retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name", is("Trung")))
                .andExpect(jsonPath("$.data[1].name", is("Bắc")));
    }

    @Test
    void testGetAllUsers_WhenNoUsersExist() throws Exception {
        Mockito.when(repo.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/getbyallid"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status", is(0)))
                .andExpect(jsonPath("$.message", is("No users found")))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.data", nullValue()));
    }

    @Test
    void testGetUserById_WhenUserExists() throws Exception {
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/getuserbyid/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message", is("User retrieved successfully")))
                .andExpect(jsonPath("$.data.name", is("Trung")));
    }

    @Test
    void testGetUserById_WhenUserDoesNotExist() throws Exception {
        Mockito.when(repo.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/getuserbyid/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(0)))
                .andExpect(jsonPath("$.message", is("User not found")))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.data", nullValue()));
    }

    @Test
    void testAddUser_WhenDataIsValid() throws Exception {
        user newUser = new user(2L, "Bắc", "Hà Nội", 30);
        Mockito.when(repo.existsById(2L)).thenReturn(false);
        Mockito.when(repo.save(Mockito.any(user.class))).thenReturn(newUser);

        mockMvc.perform(post("/adduser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"id\": 2, " +
                                "\"name\": \"Bắc\", " +
                                "\"dc\": \"Hà Nội\", " +
                                "\"age\": 30" +
                                "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message", is("Thêm user thành công")))
                .andExpect(jsonPath("$.data.name", is("Bắc")));

        Mockito.verify(repo, times(1)).save(Mockito.any(user.class));
    }

    @Test
    void testDeleteUser_WhenUserExists() throws Exception {
        Mockito.when(repo.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/deleteuser/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message", is("User deleted successfully")));
    }

    @Test
    void testDeleteUser_WhenUserDoesNotExist() throws Exception {
        Mockito.when(repo.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/deleteuser/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(0)))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message", is("User not found")));
    }

    @Test
    void testUpdateUser_WhenUserExists() throws Exception {
        user existingUser = new user(1L, "Nguyen Van A", "Ha Noi", 25);
        user updatedUser = new user(1L, "Nguyen Van B", "TP HCM", 30);

        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(repo.save(Mockito.any(user.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/updateuser/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"id\": 1, " +
                                "\"name\": \"Nguyen Van B\", " +
                                "\"dc\": \"TP HCM\", " +
                                "\"age\": 30" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message", is("Cập nhật user thành công")))
                .andExpect(jsonPath("$.data.name", is("Nguyen Van B")));

        Mockito.verify(repo, times(1)).findById(1L);
        Mockito.verify(repo, times(1)).save(Mockito.any(user.class));
    }

    @Test
    void testFindNameContainingH() throws Exception {
        Mockito.when(repo.findByNameContainingIgnoreCase("h")).thenReturn(
                Collections.singletonList(testUser));
        mockMvc.perform(get("/findnamecontainingh")
                .param("name","H")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(4)))
                .andExpect(jsonPath("$.data[0].name", is("Hương")));
    }
    @Test
    void testSortUsersByNameAsc() throws Exception{
        List<user> users = Arrays.asList(
                new user(3L, "Bắc", "Bắc Ninh", 32),
                new user(4L, "Hương", "Hải Phòng", 20),
                new user(1L, "Trung", "Ninh Bình", 21)
        );
        Mockito.when(repo.findAllByOrderByNameAsc()).thenReturn(users);
        mockMvc.perform(get("/sortbynameasc")
                        .param("order","asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].name", is("Bắc")))
                .andExpect(jsonPath("$.data[1].name", is("Hương")))
                .andExpect(jsonPath("$.data[2].name", is("Trung")));

    }
    @Test
    void testSortUsersByNameDesc() throws Exception{
        List<user> users = Arrays.asList(
                new user(3L, "Bắc", "Bắc Ninh", 32),
                new user(4L, "Hương", "Hải Phòng", 20),
                new user(1L, "Trung", "Ninh Bình", 21)
        );
        Mockito.when(repo.findAllByOrderByNameAsc()).thenReturn(users);

        mockMvc.perform(get("/sortbynameasc")
                        .param("order","desc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].name", is("Bắc")))
                .andExpect(jsonPath("$.data[1].name", is("Hương")))
                .andExpect(jsonPath("$.data[2].name", is("Trung")));

    }
    @Test
    void testFindByDc() throws Exception{
        Mockito.when(repo.findByDc("Ninh Bình"))
                .thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/findbydc/Ninh Bình")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].dc", is("Ninh Bình")));
    }
    @Test
    void testFindByAge()  throws Exception{
        Mockito.when(repo.findByAge(21)).thenReturn(Collections.singletonList(testUser));
        mockMvc.perform(get("/findbyage/21")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].age", is(21)));

    }
    @Test
    void testFindByName()  throws Exception{
        Mockito.when(repo.findByName("Bắc")).thenReturn(Collections.singletonList(new user(3L, "Bắc", "Bắc Ninh", 32)));
        mockMvc.perform(get("/findbyname/Bắc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("Bắc")));
    }
    @Test
    void testFindByids()  throws Exception{
        List<user> users = Arrays.asList(
                new user(1L, "Trung", "Ninh Bình", 21),
                new user(3L, "Bắc", "Bắc Ninh", 32)
        );
        Mockito.when(repo.findByIdIn(Arrays.asList(1L, 3L))).thenReturn(users);
        mockMvc.perform(get("/findbyids")
                        .param("ids", "1,3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name", is("Trung")))
                .andExpect(jsonPath("$.data[1].name", is("Bắc")));

    }
}
