package com.thoughtworks.rslist.entity;

import com.thoughtworks.rslist.domain.User;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@Builder
public class UserEntity {

    @Id
    @GeneratedValue()
    private Integer ID;

    private String username;

    private Integer age;

    private String gender;

    private String email;

    private String phone;

    public User toUser() {
        return new User(username, age, gender, email, phone);
    }
}
