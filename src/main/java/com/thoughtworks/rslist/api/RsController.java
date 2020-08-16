package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.component.CommonException;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    private Logger logger = LoggerFactory.getLogger(RsController.class);

    @Autowired
    RsEventRepository rsEventRepository;

    @GetMapping("/rs/list")
    public List<RsEvent> getRsEvents(@RequestParam(required = false) Integer start,
        @RequestParam(required = false) Integer end) {
        try {
            List<RsEventEntity> rsEventEntityList;
            if (start != null && end != null) {
                rsEventEntityList = rsEventRepository.findAllByOrderByIdAsc().subList(start-1, end);
            } else if(start == null && end == null) {
                rsEventEntityList = rsEventRepository.findAllByOrderByIdAsc();
            } else {
                throw new IllegalArgumentException();
            }
            return rsEventEntityList.stream().map(RsEventEntity::toRsEvent).collect(Collectors.toList());
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("invalid request param");
        }

    }

    @GetMapping("/rs/{index}")
    public RsEvent getRsEvent(@PathVariable int index) {
        try {
            return rsEventRepository.findAllByOrderByIdAsc().get(index-1).toRsEvent();
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("invalid index");
        }
    }

    @PostMapping("/rs/list")
    public ResponseEntity addRsEvent (@RequestBody @Valid RsEvent rsEvent) {
        if (rsEventRepository.findByEventNameAndKeyword(rsEvent.getEventName(), rsEvent.getKeyword()) != null) {
            throw new IllegalArgumentException();
        } else {
            rsEventRepository.save(rsEvent.toRsEventEntity());
            return ResponseEntity.status(HttpStatus.CREATED).header("index", String.valueOf(rsEventRepository.count())).build();
        }
    }

    @PatchMapping("/rs/{rsEventId}")
    public ResponseEntity updateRsEvent(@PathVariable int rsEventId , @RequestBody @Valid RsEvent rsEvent) {
        Optional<RsEventEntity> target = rsEventRepository.findById(rsEventId);
        if (!target.isPresent()) {
            return ResponseEntity.badRequest().build();
        } else {
            if (target.get().getUserId() != rsEvent.getUserId()) {
                return ResponseEntity.badRequest().build();
            } else {
                RsEventEntity entity = target.get();
                String eventName = rsEvent.getEventName() == null ? entity.getEventName() : rsEvent.getEventName();
                String keyword = rsEvent.getKeyword() == null ? entity.getKeyword() : rsEvent.getKeyword();

                entity.setEventName(eventName);
                entity.setKeyword(keyword);
                rsEventRepository.save(entity);
                return ResponseEntity.ok().build();
            }
        }
    }

    @DeleteMapping("/rs/{index}")
    public void removeRsEvent(@PathVariable int index) {
        List<RsEventEntity> rsEventEntityList = rsEventRepository.findAllByOrderByIdAsc();
        rsEventRepository.deleteById(rsEventEntityList.get(index-1).getId());
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
