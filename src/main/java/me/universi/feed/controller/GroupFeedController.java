package me.universi.feed.controller;

import me.universi.api.entities.Response;
import me.universi.feed.dto.GroupGetDTO;
import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.entities.GroupPost;
import me.universi.feed.services.GroupFeedService;
import me.universi.profile.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/feed/groups")
public class GroupFeedController {

    private final GroupFeedService groupFeedService;
    @Autowired
    private ProfileService profileService;

    public GroupFeedController(GroupFeedService groupFeedService) {
        this.groupFeedService = groupFeedService;
    }

    @GetMapping("/{groupId}/posts")
    public Response getGroupPosts(@PathVariable String groupId) {
        return Response.buildResponse(response -> {
            List<GroupPost> groupPosts = groupFeedService.getGroupPosts(groupId);
            List<GroupGetDTO> groupGetDTOS = new ArrayList<>();
            for(GroupPost post : groupPosts){
                groupGetDTOS.add(new GroupGetDTO(post.getContent(), profileService.findFirstById(post.getAuthorId())));
            }
            response.body.put("posts", groupGetDTOS);
        });
    }

    @PostMapping("/{groupId}/posts")
    public Response createGroupPost(@PathVariable String groupId, @RequestBody GroupPostDTO groupPostDTO) {
        return Response.buildResponse(response ->{
            GroupPost createdPost = groupFeedService.createGroupPost(groupId, groupPostDTO);
            response.body.put("createdPost", createdPost);
            response.message = "Post criado com sucesso";
        });
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
