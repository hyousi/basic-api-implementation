package com.thoughtworks.rslist.domain;

import com.thoughtworks.rslist.entity.VoteEntity;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    private Integer voteNum;

    private Integer userId;

    private String voteTime;

    public VoteEntity toVoteEntity() {
        return VoteEntity.builder()
            .voteNum(voteNum)
            .userId(userId)
            .voteTime(LocalDate.parse(voteTime))
            .build();
    }

    public VoteEntity toVoteEntity(int rsEventId) {
        VoteEntity voteEntity = this.toVoteEntity();
        voteEntity.setRsEventId(rsEventId);
        return voteEntity;
    }
}
