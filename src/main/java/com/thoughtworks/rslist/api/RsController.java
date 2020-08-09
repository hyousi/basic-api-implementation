package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.component.CommonException;
import com.thoughtworks.rslist.domain.RsEvent;
import java.util.ArrayList;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(produces = "application/json; charset=UTF-8")
public class RsController {

    public static List<RsEvent> rsEventList = init();

    private Logger logger = LoggerFactory.getLogger(RsController.class);


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
        try {
            if (start != null && end != null) {
                return rsEventList.subList(start - 1, end);
            } else if (start != null) {
                return rsEventList.subList(start, rsEventList.size());
            } else if (end != null) {
                return rsEventList.subList(0, end);
            }
            return rsEventList;
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("invalid request param");
        }

    }

    @GetMapping("/rs/{index}")
    public RsEvent getRsEvent(@PathVariable int index) {
        try {
            return rsEventList.get(index - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("invalid index");
        }
    }

    @PostMapping("/rs/list")
    public ResponseEntity addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
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
        rsEventList.remove(index - 1);
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<CommonException> indexOutOfBoundsHandler(Exception exception) {
        String msg =exception.getMessage();
        logger.error(msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonException(msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonException> bodyArgNotValidHandler(Exception exception) {
        String msg = "invalid param";
        logger.error(msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonException(msg));
    }
}
