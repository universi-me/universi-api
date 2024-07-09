package me.universi.feed.controller;

import java.util.List;
import java.util.Map;
import me.universi.api.entities.Response;
import me.universi.feed.dto.GroupPostCommentDTO;
import me.universi.feed.entities.GroupPostComment;
import me.universi.feed.entities.GroupPostReaction;
import me.universi.feed.services.GroupFeedService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed")
public class GroupFeedCommentController {
    private final GroupFeedService groupFeedService;

    public GroupFeedCommentController(GroupFeedService groupFeedService) {
        this.groupFeedService = groupFeedService;
    }

    @GetMapping("/posts/{groupPostId}/comments")
    public Response getGroupPostComments(@PathVariable String groupPostId) {
        return Response.buildResponse(response -> {
            List<GroupPostCommentDTO> groupPosts = groupFeedService.getGroupPostComments(groupPostId);
            response.body.put("comments", groupPosts);
        });
    }

    @PostMapping("/posts/{groupPostId}/comments")
    public Response setGroupPostComment(@PathVariable String groupPostId, @RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            GroupPostComment commentSet = groupFeedService.createGroupPostComment(groupPostId, body.get("content").toString());
            response.body.put("comments", commentSet);
            response.message = "Você comentou a publicação";
        });
    }
}
