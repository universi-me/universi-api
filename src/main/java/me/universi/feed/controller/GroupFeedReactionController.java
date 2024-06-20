package me.universi.feed.controller;

import java.util.List;
import java.util.Map;
import me.universi.api.entities.Response;
import me.universi.feed.entities.GroupPostReaction;
import me.universi.feed.services.GroupFeedService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed/groups")
public class GroupFeedReactionController {
    private final GroupFeedService groupFeedService;

    public GroupFeedReactionController(GroupFeedService groupFeedService) {
        this.groupFeedService = groupFeedService;
    }

    @GetMapping("/{groupId}/posts/{groupPostId}/reactions")
    public Response getGroupPostReactions(@PathVariable String groupId, @PathVariable String groupPostId) {
        return Response.buildResponse(response -> {
            List<GroupPostReaction> groupPosts = groupFeedService.getGroupPostReactions(groupId, groupPostId);
            response.body.put("reactions", groupPosts);
        });
    }

    @PostMapping("/{groupId}/posts/{groupPostId}/reactions")
    public Response setGroupPostReaction(@PathVariable String groupId, @PathVariable String groupPostId, @RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            GroupPostReaction reactionSet = groupFeedService.setGroupPostReaction(groupId, groupPostId, body.get("reaction").toString());
            response.body.put("reactions", reactionSet);
            response.message = "Você reagiu a publicação";
        });
    }
}
