package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoteController {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public RsEventRepository rsEventRepository;

    @Autowired
    public VoteRepository voteRepository;

    @Transactional
    @PostMapping("/rs/{rsEventId}/vote")
    public ResponseEntity voteForRsEvent(@PathVariable int rsEventId, @RequestBody Vote vote) throws Exception {
        Optional<UserEntity> userEntity = userRepository.findById(vote.getUserId());
        Optional<RsEventEntity> rsEventEntity = rsEventRepository.findById(rsEventId);
        if (userEntity.isPresent() && rsEventEntity.isPresent()) {
            int votes = vote.getVoteNum();
            if (votes <= userEntity.get().getVoteNum()) {

                rsEventEntity.get().setVoteNum(rsEventEntity.get().getVoteNum() + votes);
                rsEventRepository.save(rsEventEntity.get());

                userEntity.get().setVoteNum(userEntity.get().getVoteNum() - votes);
                userRepository.save(userEntity.get());

                VoteEntity voteEntity = vote.toVoteEntity(rsEventId);
                voteEntity.setRsEventEntity(rsEventEntity.get());
                voteEntity.setUserEntity(userEntity.get());
                voteRepository.save(voteEntity);

                return ResponseEntity.created(null).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
