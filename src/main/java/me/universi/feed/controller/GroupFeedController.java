package me.universi.feed.controller;

import me.universi.api.entities.Response;
import me.universi.feed.dto.GroupGetDTO;
import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.entities.GroupPost;
import me.universi.feed.exceptions.GroupFeedException;
import me.universi.feed.services.GroupFeedService;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.enums.Permission;
import me.universi.role.services.RoleService;
import me.universi.util.CastingUtil;
import me.universi.profile.services.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/feeds/groups")
public class GroupFeedController {

    private final GroupFeedService groupFeedService;
    private final ProfileService profileService;

    public GroupFeedController(GroupFeedService groupFeedService, ProfileService profileService) {
        this.groupFeedService = groupFeedService;
        this.profileService = profileService;
    }

    @GetMapping("/{groupId}/posts")
    public ResponseEntity<List<GroupGetDTO>> getGroupPosts( @PathVariable String groupId ) {
        return ResponseEntity.ok( groupFeedService.getGroupPostsDTO(groupId) );
    }

    @PostMapping("/{groupId}/posts")
    public ResponseEntity<GroupPost> createGroupPost( @PathVariable String groupId, @RequestBody GroupPostDTO groupPostDTO ) {
        return ResponseEntity.ok( groupFeedService.createGroupPost(groupId, groupPostDTO) );
    }

    @PatchMapping("/{groupId}/posts/{postId}")
    public ResponseEntity<GroupPost> editGroupPost( @PathVariable String groupId, @PathVariable String postId, @RequestBody GroupPostDTO groupPostDTO ) {
        return ResponseEntity.ok ( groupFeedService.editGroupPost(groupId, postId, groupPostDTO) );
    }

    @DeleteMapping("/{groupId}/posts/{postId}")
    public ResponseEntity<Void> deleteGroupPost( @PathVariable String groupId, @PathVariable String postId ) {
        groupFeedService.deleteGroupPost(groupId, postId);
        return ResponseEntity.noContent().build();
    }
}
