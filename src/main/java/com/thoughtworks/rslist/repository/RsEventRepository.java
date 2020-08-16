package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.entity.RsEventEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RsEventRepository extends CrudRepository<RsEventEntity, Integer> {
    List<RsEventEntity> findAll();
    List<RsEventEntity> findAllByOrderByIdAsc();
    RsEventEntity findByEventNameAndKeyword(String eventName, String keyword);
}
