package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    public UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        List<UserEntity> userEntityList = userRepository.findAll();
        return userEntityList.stream().map(UserEntity::toUser).collect(Collectors.toList());
    }

    @GetMapping("/users/{username}")
    public User getUser(@PathVariable String username) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isPresent()) {
            return userEntity.get().toUser();
        } else {
            throw new IllegalArgumentException("User Not Found.");
        }
    }

    @PostMapping("/user")
    public ResponseEntity addUser(@RequestBody @Valid User user) {
        Optional<UserEntity> userEntity = userRepository.findByPhone(user.getPhone());
        if (!userEntity.isPresent()) {
            userRepository.save(user.toUserEntity());
            return ResponseEntity.status(HttpStatus.CREATED).header("index", String.valueOf(userRepository.count())).build();
        } else {
            return ResponseEntity.ok().body("User Exists");
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleException(Exception e) {
        return e.getMessage();
    }

}
