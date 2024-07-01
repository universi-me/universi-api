package me.universi.feed.services;


import me.universi.Sys;
import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.entities.GroupPostReaction;
import me.universi.feed.exceptions.GroupFeedException;
import me.universi.feed.exceptions.PostNotFoundException;
import me.universi.feed.entities.GroupPost;
import me.universi.feed.repositories.GroupPostReactionRepository;
import me.universi.feed.repositories.GroupPostRepository;
import me.universi.roles.enums.FeaturesTypes;
import me.universi.roles.enums.Permission;
import me.universi.roles.services.RolesService;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupFeedService {

    private final GroupPostRepository groupPostRepository;
    private final GroupPostReactionRepository groupPostReactionRepository;

    @Autowired
    public GroupFeedService(GroupPostRepository groupPostRepository, GroupPostReactionRepository groupPostReactionRepository) {
        this.groupPostRepository = groupPostRepository;
        this.groupPostReactionRepository = groupPostReactionRepository;
    }

    public static GroupFeedService getInstance() {
        return Sys.context.getBean("groupFeedService", GroupFeedService.class);
    }

    public List<GroupPost> getGroupPosts(String groupId) {

        // check permission post
        RolesService.getInstance().checkPermission(groupId, FeaturesTypes.FEED, Permission.READ);

        Optional<List<GroupPost>> posts = groupPostRepository.findByGroupIdAndDeletedIsFalse(groupId);
        return posts.orElseThrow(() -> new PostNotFoundException("Publicação não foi encontrada."));
    }

    public GroupPost createGroupPost(String groupId, GroupPostDTO groupPostDTO) {

        // check permission post
        RolesService.getInstance().checkPermission(groupId, FeaturesTypes.FEED, Permission.READ_WRITE);

        String authorId = String.valueOf(UserService.getInstance().getUserInSession().getProfile().getId());
        if (groupPostDTO.getContent() == null || groupPostDTO.getContent().isEmpty()) {
            throw new GroupFeedException("O conteúdo da publicação não pode estar vazio.");
        } else if(groupPostDTO.getContent().length() > 3000) {
            throw new GroupFeedException("O conteúdo da publicação não pode ter mais de 3000 caracteres.");
        }
        GroupPost groupPost = new GroupPost(groupId, groupPostDTO.getContent(), authorId, false);
        return groupPostRepository.save(groupPost);
    }

    public GroupPost getGroupPost(String groupId, String postId) {

        // check permission post
        RolesService.getInstance().checkPermission(groupId, FeaturesTypes.FEED, Permission.READ);

        Optional<GroupPost> existingPost = groupPostRepository.findFirstByGroupIdAndIdAndDeletedIsFalse(groupId, postId);
        if (existingPost.isPresent() && !existingPost.get().isDeleted()) {
            return existingPost.get();
        } else {
            throw new PostNotFoundException("Publicação não foi encontrada.");
        }
    }

    public void checkPermissionForEdit(GroupPost post, boolean forDelete) {
        String authorId = String.valueOf(UserService.getInstance().getUserInSession().getProfile().getId());
        if (!post.getAuthorId().equals(authorId)) {
            if(UserService.getInstance().isUserAdminSession()) {
                if(forDelete) {
                    // admin can delete any post
                    return;
                }
            }

            if(forDelete && RolesService.getInstance().hasPermission(post.getGroupId(), FeaturesTypes.FEED, Permission.READ_WRITE_DELETE)) {
                // user with permission can delete any post
                return;
            }

            throw new GroupFeedException("Você não tem permissão para editar esta publicação.");
        }
    }

    public GroupPost editGroupPost(String groupId, String postId, GroupPostDTO groupPostDTO) {

        // check permission post
        RolesService.getInstance().checkPermission(groupId, FeaturesTypes.FEED, Permission.READ_WRITE);

        GroupPost post = getGroupPost(groupId, postId);

        checkPermissionForEdit(post, false);

        if(groupPostDTO.getContent() != null && !groupPostDTO.getContent().isEmpty()) {
            if(groupPostDTO.getContent().length() > 3000) {
                throw new GroupFeedException("O conteúdo da publicação não pode ter mais de 3000 caracteres.");
            }
            post.setContent(groupPostDTO.getContent());
        }

        return groupPostRepository.save(post);
    }

    public boolean deleteGroupPost(String groupId, String postId) {

        // check permission post
        RolesService.getInstance().checkPermission(groupId, FeaturesTypes.FEED, Permission.READ_WRITE_DELETE);

        GroupPost post = getGroupPost(groupId, postId);

        checkPermissionForEdit(post, true);

        post.setDeleted(true);
        groupPostRepository.save(post);

        return true;
    }

    public List<GroupPostReaction> getGroupPostReactions(String groupPostId) {
        Optional<List<GroupPostReaction>> reactions = groupPostReactionRepository.findByGroupPostIdAndDeletedIsFalse(groupPostId);
        return reactions.orElse(null);
    }

    public GroupPostReaction setGroupPostReaction(String groupPostId, String reaction) {
        String authorId = String.valueOf(UserService.getInstance().getUserInSession().getProfile().getId());
        GroupPost post = groupPostRepository.findFirstByIdAndDeletedIsFalse(groupPostId).orElseThrow(() -> new PostNotFoundException("Publicação não foi encontrada."));

        Optional<GroupPostReaction> existingReaction = groupPostReactionRepository.findFirstByGroupPostIdAndAuthorIdAndDeletedIsFalse(groupPostId, authorId);
        if(existingReaction.isPresent()) {
            GroupPostReaction reactionObj = existingReaction.get();
            reactionObj.setReaction(reaction);
            return groupPostReactionRepository.save(reactionObj);
        } else {
            GroupPostReaction reactionObj = new GroupPostReaction(post.getId(), reaction, authorId, false);
            return groupPostReactionRepository.save(reactionObj);
        }
    }

}