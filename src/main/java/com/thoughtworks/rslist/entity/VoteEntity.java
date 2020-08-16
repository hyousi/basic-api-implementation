package com.thoughtworks.rslist.entity;

import com.thoughtworks.rslist.domain.Vote;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Builder
@Table(name ="vote")
public class VoteEntity {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer voteNum;

    private LocalDate voteTime;

    private Integer userId;

    private Integer rsEventId;

    @ManyToOne
    @JoinColumn(name = "u_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "e_id")
    private RsEventEntity rsEventEntity;

    public Vote toVote() {
        return new Vote(voteNum, userId, voteTime.toString());
    }
}
