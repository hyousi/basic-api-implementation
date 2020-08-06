package com.thoughtworks.rslist.api;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class RsController {

    private List<String> rsList = Stream.of("第一条事件", "第二条事件", "第三条事件").collect(Collectors.toList());

    @GetMapping("/rs/list")
    public String getRsEvents() {
        return rsList.toString();
    }

    @GetMapping("/rs/list/{index}")
    public String getRsEvent(@PathVariable int index) {
        // FIXME: Exception Handling - index out of bound.
        return rsList.get(index - 1);
    }
}
