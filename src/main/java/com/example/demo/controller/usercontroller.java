package com.example.demo.controller;


import com.example.demo.model.user;
import com.example.demo.userrepo.userrepo;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class usercontroller {
    @Autowired
    private userrepo repo;
    @GetMapping("/getbyallid")
    private ResponseEntity<Map<String, Object> >getbyallid(){
        try {
            List<user> userList = new ArrayList<>();
            repo.findAll().forEach(userList::add);

            if (userList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of(
                        "status", 0,
                        "message", "No users found",
                        "code", 204,
                        "data", null
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", 1,
                    "message", "Users retrieved successfully",
                    "code", 200,
                    "data", userList
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "An error occurred while retrieving users",
                    "code", 500,
                    "data", null
            ));
        }
    }
    @GetMapping("/getuserbyid/{id}")
    private ResponseEntity<Map<String, Object>> getuserbyid(@PathVariable long id){
        try {
        Optional<user> userdata=repo.findById(id);
        if(userdata.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status",1,
                    "message","User retrieved successfully",
                    "code",200,
                    "data",userdata.get()
            ));
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status",0,
                    "message","User not found",
                    "code",404,
                    "data",null
            ));
        }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status",0,
                    "message","An error occurred while retrieving user",
                    "code",500,
                    "data",null
            ));
        }
    }
    @PostMapping("/adduser")
    private ResponseEntity<Map<String, Object>> adduser(@RequestBody user user1) {
        try {
            if (user1 == null || user1.getName() == null && user1.getName().trim().isEmpty() ||user1.getDc().trim().isEmpty() && user1.getDc() == null || user1.getAge() < 1 || user1.getAge() > 100) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "status", 0,
                        "message", "Dữ liệu ràng buộc không đúng",
                        "code", 900,
                        "data", null
                ));
            }

            if (repo.existsById(user1.getId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "status", 0,
                        "message", "Người dùng đã tồn tại trong cơ sở dữ liệu",
                        "code", 902,
                        "data", null
                ));
            }

            user user = repo.save(user1);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", 1,
                    "message", "Thêm user thành công",
                    "code", 201,
                    "data", user
            ));
        } catch (Exception e) {

            if (e instanceof DataAccessException) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "status", 0,
                        "message", "Truy cập vào cơ sở dữ liệu thất bại",
                        "code", 901,
                        "data", null
                ));
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "Đã xảy ra lỗi khi thêm user",
                    "code", 500,
                    "data", null
            ));
        }
    }

    @DeleteMapping("/deleteuser/{id}")
    private ResponseEntity<Map<String,Object>> deleteuser(@PathVariable long id) {
        try {
            if (repo.existsById(id)) {
                repo.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                        "status", 1,
                        "message", "User deleted successfully",
                        "code", 200,
                        "data", null
                ));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", 0,
                    "message", "User not found",
                    "code", 404,
                    "data", null
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "An error occurred while deleting the user",
                    "code", 500,
                    "data", null
            ));
        }
    }
    @PutMapping("/updateuser/{id}")
    private ResponseEntity<Map<String,Object>> updateuser(@PathVariable long id,@RequestBody user user1){
       Optional<user> userdata=repo.findById(id);
       if(userdata.isPresent()){
           user user2=userdata.get();
           user2.setName(user1.getName());
           user2.setDc(user1.getDc());
           user2.setAge(user1.getAge());
           user2=repo.save(user2);
           return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                   "status",1,
                   "message","User updated successfully",
                   "code",200,
                   "data",user2
           ));
       }else {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                   "status",0,
                   "message","User not found",
                   "code",404,
                   "data",null
           ));
       }
    }
    @GetMapping("/sortusersbyname")
    private ResponseEntity<Map<String,Object>> sortusersbyname(
            @RequestParam(required = false, defaultValue = "asc") String order) {
        try {
            List<user> sapxep;
            if (order != null && order.equalsIgnoreCase("desc")) {
                sapxep = repo.findAllByOrderByNameAsc();
            } else {
                sapxep = repo.findAllByOrderByNameDesc();
            }
            if (sapxep.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of(

                        "status", 0,
                        "message", "No users found",
                        "code", 204,
                        "data", null
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", 1,
                    "message", "Users retrieved successfully",
                    "code", 200,
                    "data", sapxep
            ));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "An error occurred while retrieving users",
                    "code", 500,
                    "data", null
            ));
        }
    }

        @GetMapping("/findname/{name}")
    private ResponseEntity<Map<String ,Object>> findname(@PathVariable String name) {

        try {
            List<user> result = repo.findByName(name);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of(
                        "status", 0,
                        "message", "No users found",
                        "code", 204,
                        "data", null
                ));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "status", 1,
                    "message", "Users retrieved successfully",
                    "code", 200,
                    "data", result
            ));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "An error occurred while retrieving users",
                    "code", 500,
                    "data", null
            ));
        }

    }
    @GetMapping("/finddc/{dc}")
    public ResponseEntity<Map<String,Object>> finddc(@PathVariable String dc) {
        try {
            List<user> result2 = repo.findByDc(dc);
            if (result2.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of(
                        "status", 0,
                        "message", "No users found",
                        "code", 204,
                        "data", null
                ));

            }
            return ResponseEntity.status( HttpStatus.OK).body(Map.of(
                    "status", 1,
                    "message", "Users retrieved successfully",
                    "code", 200,
                    "data", result2
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "An error occurred while retrieving users",
                    "code", 500,
                    "data", null
            ));
        }
    }

    @GetMapping("/findage/{age}")
    public ResponseEntity<Map<String,Object>> findage(@PathVariable int age) {

        try {
            List<user> result3 = repo.findByAge(age);
            if (result3.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of(
                        "status", 0,
                        "message", "No users found",
                        "code", 204,
                        "data", null
                ));
            }
            return ResponseEntity.status( HttpStatus.OK).body(Map.of(
                    "status", 1,
                    "message", "Users retrieved successfully",
                    "code", 200,
                    "data", result3
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "An error occurred while retrieving users",
                    "code", 500,
                    "data", null
            ));
        }

    }
    @GetMapping("/findnamestarth/{prefix}")
    private ResponseEntity<Map<String,Object>> findnamestarth(@PathVariable String prefix){
        try {
            List<user> result4=repo.findByNameStartingWith("h");
            if(result4.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of(
                        "status",0,
                        "message","No users found",
                        "code",204,
                        "data",null
                ));
            }else {
                return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                        "status",1,
                        "message","Users retrieved successfully",
                        "code",200,
                        "data",result4
                ));
            }
        }catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "status",0,
                        "message","An error occurred while retrieving users",
                        "code",500,
                        "data",null
                ));
        }
    }
    @GetMapping("/findnamekytuh")
    private ResponseEntity<Map<String, Object>> findNameContainingH(@RequestParam(required = false, defaultValue = "H") String name) {
        try {
            List<user> result = repo.findByNameContainingIgnoreCase(name);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of(
                        "status", 0,
                        "message", "No users found",
                        "code", 204,
                        "data", null
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "status", 1,
                    "message", "Users found",
                    "code", 200,
                    "data", result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "An error occurred",
                    "code", 500,
                    "error", e.getMessage()
            ));
        }
    }
    @GetMapping("/findbyids")
    private ResponseEntity<Map<String, Object>> findUsersByIds(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "status", 0,
                        "message", "Input list of IDs is empty",
                        "code", 400,
                        "data", null
                ));
            }

            List<user> result = repo.findByIdIn(ids);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of(
                        "status", 0,
                        "message", "No users found",
                        "code", 204,
                        "data", null
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "status", 1,
                    "message", "Users found",
                    "code", 200,
                    "data", result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 0,
                    "message", "An error occurred",
                    "code", 500,
                    "error", e.getMessage()
            ));
        }
    }


};
