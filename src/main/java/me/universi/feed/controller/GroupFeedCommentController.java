package me.universi.feed.controller;

import jakarta.validation.Valid;
import me.universi.feed.dto.CreateCommentDTO;
import me.universi.feed.entities.GroupPostComment;
import me.universi.feed.services.GroupFeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feeds")
public class GroupFeedCommentController {
    private final GroupFeedService groupFeedService;

    public GroupFeedCommentController(GroupFeedService groupFeedService) {
        this.groupFeedService = groupFeedService;
    }

    @PostMapping("/posts/{groupPostId}/comments")
    public ResponseEntity<GroupPostComment> createGroupPostComment( @PathVariable String groupPostId, @Valid @RequestBody CreateCommentDTO createCommentDTO ) {
        return ResponseEntity.ok( groupFeedService.createGroupPostComment(groupPostId, createCommentDTO.content()) );
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<GroupPostComment> editGroupPostComment( @PathVariable String commentId, @Valid @RequestBody CreateCommentDTO createCommentDTO ) {
        return ResponseEntity.ok( groupFeedService.editGroupPostComment(commentId, createCommentDTO.content()) ) ;
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteGroupPostComment( @PathVariable String commentId ) {
        groupFeedService.deleteGroupPostComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
