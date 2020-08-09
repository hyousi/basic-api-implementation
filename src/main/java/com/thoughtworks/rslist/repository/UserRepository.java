package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    List<UserEntity> findAll();
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByPhone(String phone);
}
