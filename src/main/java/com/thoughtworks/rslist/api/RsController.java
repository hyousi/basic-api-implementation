package com.thoughtworks.rslist.api;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RsController {

    public static List<String> rsList = init();

    public static List<String> init() {
        return Stream.of("第一条事件", "第二条事件", "第三条事件").collect(Collectors.toList());
    }

    @GetMapping("/rs/list")
    public String getRsEvents(@RequestParam(required = false) Integer start,
                              @RequestParam(required = false) Integer end) {
        // FIXME: Exception Handling - index out of bound.
        if (start == null || end == null) {
            return rsList.toString();
        }

        return rsList.subList(start - 1, end).toString();
    }

    @GetMapping("/rs/{index}")
    public String getRsEvent(@PathVariable int index) {
        // FIXME: Exception Handling - index out of bound.
        return rsList.get(index - 1);
    }

    @PostMapping("/rs/list")
    public void addRsEvent(@RequestBody String rsEvent) {
        rsList.add(rsEvent);
    }

    @PostMapping("/rs/{index}")
    public void updateRsEvent(@PathVariable Integer index, @RequestBody String rsEvent) {
        rsList.set(index, rsEvent);
    }
}
