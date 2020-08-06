package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(produces = "application/json; charset=UTF-8")
public class RsController {

    public static List<RsEvent> rsEventList = init();

    public static List<RsEvent> init() {
        List<RsEvent> rsEventList = new ArrayList<>();
        rsEventList.add(new RsEvent("第一条事件", "未分类"));
        rsEventList.add(new RsEvent("第二条事件", "未分类"));
        rsEventList.add(new RsEvent("第三条事件", "未分类"));
        return rsEventList;
    }

    @GetMapping("/rs/list")
    public List<RsEvent> getRsEvents(@RequestParam(required = false) Integer start,
        @RequestParam(required = false) Integer end) {
        // FIXME: Exception Handling - index out of bound.
        if (start == null || end == null) {
            return rsEventList;
        }

        return rsEventList.subList(start - 1, end);
    }

    @GetMapping("/rs/{index}")
    public RsEvent getRsEvent(@PathVariable int index) {
        // FIXME: Exception Handling - index out of bound.
        return rsEventList.get(index - 1);
    }

    @PostMapping("/rs/list")
    public ResponseEntity addRsEvent(@RequestBody RsEvent rsEvent) {
        rsEventList.add(rsEvent);
        return ResponseEntity.status(HttpStatus.CREATED).header("index", String.valueOf(rsEventList.size())).build();
    }

    @PostMapping("/rs/{index}")
    public ResponseEntity updateRsEvent(@PathVariable Integer index, @RequestBody RsEvent rsEvent) {
        if (rsEvent.getEventName() != null) {
            rsEventList.get(index - 1).setEventName(rsEvent.getEventName());
        }
        if (rsEvent.getKeyword() != null) {
            rsEventList.get(index - 1).setKeyword(rsEvent.getKeyword());
        }
        return ResponseEntity.status(HttpStatus.CREATED).header("index", String.valueOf(index)).build();
    }

    @DeleteMapping("/rs/{index}")
    public void removeRsEvent(@PathVariable int index) {
        rsEventList.remove(index-1);
    }
}
