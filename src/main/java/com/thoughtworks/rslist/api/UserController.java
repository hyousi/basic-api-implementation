package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    public static List<User> userList = init();

    public static List<User> init() {
        List<User> users = new ArrayList<>();
        users.add(new User("user1", 22, "male", "a@b.com", "11111111111"));
        return users;
    }

    @GetMapping("/user")
    public List<User> getUsers() {
        return userList;
    }

    @PostMapping("/user")
    public ResponseEntity addUser(@RequestBody @Valid User user) {
        if (!userList.contains(user)) {
            userList.add(user);
        }
        return ResponseEntity.status(HttpStatus.CREATED).header("index", String.valueOf(userList.size())).build();
    }

}
