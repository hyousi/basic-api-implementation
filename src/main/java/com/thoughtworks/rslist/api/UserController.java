package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    public static List<User> userList = init();

    public static List<User> init() {
        List<User> users = new ArrayList<>();
        return users;
    }

    @PostMapping("/user")
    public void addUser(@RequestBody @Valid User user) {
        userList.add(user);
    }

}
