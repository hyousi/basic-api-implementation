package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.entity.VoteEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<VoteEntity, Integer> {
    List<VoteEntity> findAll();
    List<VoteEntity> findAllByVoteTimeGreaterThanEqualAndVoteTimeLessThanEqual(LocalDate start, LocalDate end);
}
