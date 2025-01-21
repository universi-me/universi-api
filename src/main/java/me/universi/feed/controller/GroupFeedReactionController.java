package me.universi.feed.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import me.universi.feed.dto.UpdatePostReactionDTO;
import me.universi.feed.entities.GroupPostReaction;
import me.universi.feed.services.GroupFeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feeds")
public class GroupFeedReactionController {
    private final GroupFeedService groupFeedService;

    public GroupFeedReactionController(GroupFeedService groupFeedService) {
        this.groupFeedService = groupFeedService;
    }

    @GetMapping("/posts/{groupPostId}/reactions")
    public ResponseEntity<List<GroupPostReaction>> getGroupPostReactions(@Valid @PathVariable @NotNull( message = "groupPostId inválido" ) String groupPostId) {
        return ResponseEntity.ok ( groupFeedService.getGroupPostReactions(groupPostId) );
    }

    @PatchMapping("/posts/{groupPostId}/reactions")
    public ResponseEntity<GroupPostReaction> setGroupPostReaction(@Valid @PathVariable @NotNull( message = "groupPostId inválido" ) String groupPostId, @Valid @RequestBody UpdatePostReactionDTO updatePostReactionDTO ) {
        return ResponseEntity.ok( groupFeedService.setGroupPostReaction(groupPostId, updatePostReactionDTO.reaction()) );
    }
}
