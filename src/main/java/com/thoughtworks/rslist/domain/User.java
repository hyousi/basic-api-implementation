package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.rslist.entity.UserEntity;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @NotNull
    @Size(max = 8)
    @JsonProperty(value = "user_name")
    private String userName;

    @NotNull
    @Min(18)
    @Max(100)
    @JsonProperty(value = "user_age")
    private Integer age;

    @NotNull
    @JsonProperty(value = "user_gender")
    private String gender;

    @Email
    @JsonProperty(value = "user_email")
    private String email;

    @JsonProperty(value = "user_phone")
    @Pattern(regexp = "1\\d{10}")
    private String phone;

    public UserEntity toUserEntity() {
        return UserEntity.builder()
            .username(userName)
            .age(age)
            .gender(gender)
            .email(email)
            .phone(phone)
            .build();
    }
}
