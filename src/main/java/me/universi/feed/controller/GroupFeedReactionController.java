package me.universi.feed.controller;

import java.util.List;
import java.util.Map;
import me.universi.api.entities.Response;
import me.universi.feed.entities.GroupPostReaction;
import me.universi.feed.services.GroupFeedService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed")
public class GroupFeedReactionController {
    private final GroupFeedService groupFeedService;

    public GroupFeedReactionController(GroupFeedService groupFeedService) {
        this.groupFeedService = groupFeedService;
    }

    @GetMapping("/posts/{groupPostId}/reactions")
    public Response getGroupPostReactions(@PathVariable String groupPostId) {
        return Response.buildResponse(response -> {
            List<GroupPostReaction> groupPosts = groupFeedService.getGroupPostReactions(groupPostId);
            response.body.put("reactions", groupPosts);
        });
    }

    @PostMapping("/posts/{groupPostId}/reactions")
    public Response setGroupPostReaction(@PathVariable String groupPostId, @RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            GroupPostReaction reactionSet = groupFeedService.setGroupPostReaction(groupPostId, body.get("reaction").toString());
            response.body.put("reactions", reactionSet);
            response.message = "Você reagiu a publicação";
        });
    }
}
