package me.universi.feed.controller;

import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.entities.GroupPost;
import me.universi.feed.services.GroupFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feed/groups")
public class GroupFeedController {

    @Autowired
    private GroupFeedService groupFeedService;

    @GetMapping("/{groupId}/posts")
    public ResponseEntity<List<GroupPost>> getGroupPosts(@PathVariable String groupId) {
        List<GroupPost> groupPosts = groupFeedService.getGroupPosts(groupId);
        return ResponseEntity.ok(groupPosts);
    }

    @PostMapping("/{groupId}/posts")
    public ResponseEntity<GroupPost> createGroupPost(@PathVariable String groupId, @RequestBody GroupPostDTO groupPostDTO) {
        GroupPost createdPost = groupFeedService.createGroupPost(groupId, groupPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @DeleteMapping("/{groupId}/posts/{postId}")
    public ResponseEntity<String> deleteGroupPost(@PathVariable String groupId, @PathVariable String postId) {
        boolean success = groupFeedService.deleteGroupPost(groupId, postId);
        if (success) {
            return ResponseEntity.ok("Group post deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group post not found.");
        }
    }
}
