package com.thoughtworks.rslist.domain;

import com.thoughtworks.rslist.entity.RsEventEntity;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsEvent {

    private int id;

    private String eventName;

    private String keyword;

    @NotNull
    private Integer userId;

    private int voteNum;

    public RsEvent(String eventName, String keyword,
        @NotNull Integer userId) {
        this.eventName = eventName;
        this.keyword = keyword;
        this.userId = userId;
    }

    public RsEvent(int id, String eventName, String keyword, int voteNum) {
        this.id = id;
        this.eventName = eventName;
        this.keyword = keyword;
        this.voteNum = voteNum;
    }

    public RsEventEntity toRsEventEntity() {
        return RsEventEntity.builder()
            .eventName(eventName)
            .keyword(keyword)
            .userId(userId)
            .build();
    }
}
