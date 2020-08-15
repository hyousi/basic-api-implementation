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

    private String eventName;

    private String keyword;

    @NotNull
    private Integer userId;

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
