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
@RequestMapping("/feed")
public class GroupFeedCommentController {
    private final GroupFeedService groupFeedService;

    public GroupFeedCommentController(GroupFeedService groupFeedService) {
        this.groupFeedService = groupFeedService;
    }

    @PostMapping("/posts/{groupPostId}/comments")
    public Response setGroupPostComment(@PathVariable String groupPostId, @RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            GroupPostComment commentSet = groupFeedService.createGroupPostComment(groupPostId, body.get("content").toString());
            response.body.put("comments", commentSet);
            response.message = "Comentário publicado com sucesso";
        });
    }

    @PostMapping("/comments/{commentId}/edit")
    public Response editGroupPostComment(@PathVariable String commentId, @RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            GroupPostComment commentSet = groupFeedService.editGroupPostComment(commentId, body.get("content").toString());
            response.body.put("comments", commentSet);
            response.message = "Comentário editado com sucesso";
        });
    }

    @DeleteMapping("/comments/{commentId}")
    public Response deleteGroupPostComment(@PathVariable String commentId) {
        return Response.buildResponse(response -> {
            groupFeedService.deleteGroupPostComment(commentId);
            response.message = "Comentário deletado com sucesso";
        });
    }
}
