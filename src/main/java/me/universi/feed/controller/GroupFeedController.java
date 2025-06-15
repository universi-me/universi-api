package me.universi.feed.controller;

import jakarta.validation.Valid;
import me.universi.feed.dto.GroupGetDTO;
import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.entities.GroupPost;
import me.universi.feed.services.GroupFeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feeds/groups")
public class GroupFeedController {

    private final GroupFeedService groupFeedService;

    public GroupFeedController(GroupFeedService groupFeedService) {
        this.groupFeedService = groupFeedService;
    }

    @GetMapping("/{groupId}/posts")
    public ResponseEntity<List<GroupGetDTO>> getGroupPosts( @PathVariable String groupId ) {
        return ResponseEntity.ok( groupFeedService.getGroupPostsDTO(groupId) );
    }

    @PostMapping("/{groupId}/posts")
    public ResponseEntity<GroupPost> createGroupPost( @PathVariable String groupId, @Valid @RequestBody GroupPostDTO groupPostDTO ) {
        return ResponseEntity.ok( groupFeedService.createGroupPost(groupId, groupPostDTO) );
    }

    @PatchMapping("/{groupId}/posts/{postId}")
    public ResponseEntity<GroupPost> editGroupPost( @PathVariable String groupId, @PathVariable String postId, @Valid @RequestBody GroupPostDTO groupPostDTO ) {
        return ResponseEntity.ok ( groupFeedService.editGroupPost(groupId, postId, groupPostDTO) );
    }

    @DeleteMapping("/{groupId}/posts/{postId}")
    public ResponseEntity<Void> deleteGroupPost( @PathVariable String groupId, @PathVariable String postId ) {
        groupFeedService.deleteGroupPost(groupId, postId);
        return ResponseEntity.noContent().build();
    }
}
