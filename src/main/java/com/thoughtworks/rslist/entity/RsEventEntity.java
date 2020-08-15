package com.thoughtworks.rslist.entity;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.repository.UserRepository;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rs_entity")
@Builder
public class RsEventEntity {

    @Id
    @GeneratedValue
    private int id;

    private String eventName;

    private String keyword;

    private int userId;

    @ManyToOne
    private UserEntity userEntity;

    public RsEvent toRsEvent() {
        return new RsEvent(eventName, keyword);
    }
}
