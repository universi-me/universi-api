package me.universi.feed.controller;

import me.universi.api.entities.Response;
import me.universi.feed.dto.GroupGetDTO;
import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.entities.GroupPost;
import me.universi.feed.exceptions.GroupFeedException;
import me.universi.feed.exceptions.PostNotFoundException;
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
    private final ProfileService profileService;

    public GroupFeedController(GroupFeedService groupFeedService, ProfileService profileService) {
        this.groupFeedService = groupFeedService;
        this.profileService = profileService;
    }

    @GetMapping("/{groupId}/posts")
    public Response getGroupPosts(@PathVariable String groupId) {
        return Response.buildResponse(response -> {
            List<GroupPost> groupPosts = groupFeedService.getGroupPosts(groupId);
            List<GroupGetDTO> groupGetDTOS = new ArrayList<>();
            for(GroupPost post : groupPosts){
                groupGetDTOS.add(new GroupGetDTO(post.getContent(), profileService.findFirstById(post.getAuthorId()), post.getId(), post.getGroupId()));
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

    @PutMapping("/{groupId}/posts/{postId}")
    public Response editGroupPost(@PathVariable String groupId, @PathVariable String postId, @RequestBody GroupPostDTO groupPostDTO) {
        return Response.buildResponse(response ->{
            GroupPost createdPost = groupFeedService.editGroupPost(groupId, postId, groupPostDTO);
            response.body.put("editedPost", createdPost);
            response.message = "Post editado com sucesso";
        });
    }

    @DeleteMapping("/{groupId}/posts/{postId}")
    public Response deleteGroupPost(@PathVariable String groupId, @PathVariable String postId) {
        return Response.buildResponse(response-> {
            boolean success = groupFeedService.deleteGroupPost(groupId, postId);
            if (success) {
                response.message = "Post excluído com sucesso";
                return;
            }
            throw  new GroupFeedException("Houve um erro interno ao excluir o post");
        });
    }
}
