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

    @NotNull
    private String eventName;

    @NotNull
    private String keyword;

    private int userId;

    public RsEvent(String eventName, String keyword) {
        this.eventName = eventName;
        this.keyword = keyword;
    }

    public RsEventEntity toRsEventEntity() {
        return RsEventEntity.builder()
            .eventName(eventName)
            .keyword(keyword)
            .userId(userId)
            .build();
    }
}
