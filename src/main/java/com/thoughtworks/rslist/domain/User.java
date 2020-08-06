package com.thoughtworks.rslist.domain;

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
    private String userName;
    @NotNull
    @Min(18)
    @Max(100)
    private Integer age;
    @NotNull
    private String gender;
    @Email
    private String email;
    @Pattern(regexp = "1\\d{10}")
    private String phone;
}
