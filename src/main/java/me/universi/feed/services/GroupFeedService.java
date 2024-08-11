package me.universi.feed.services;


import java.util.ArrayList;
import me.universi.Sys;
import me.universi.feed.dto.GroupPostCommentDTO;
import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.entities.GroupPostComment;
import me.universi.feed.entities.GroupPostReaction;
import me.universi.feed.exceptions.GroupFeedException;
import me.universi.feed.exceptions.PostNotFoundException;
import me.universi.feed.entities.GroupPost;
import me.universi.feed.repositories.GroupPostCommentRepository;
import me.universi.feed.repositories.GroupPostReactionRepository;
import me.universi.feed.repositories.GroupPostRepository;
import me.universi.profile.services.ProfileService;
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
    private final GroupPostCommentRepository groupPostCommentRepository;

    private static final Integer MAX_CONTENT_LENGTH = 3000;
    private static final Integer MAX_COMMENT_LENGTH = 2000;

    @Autowired
    public GroupFeedService(GroupPostRepository groupPostRepository, GroupPostReactionRepository groupPostReactionRepository, GroupPostCommentRepository groupPostCommentRepository) {
        this.groupPostRepository = groupPostRepository;
        this.groupPostReactionRepository = groupPostReactionRepository;
        this.groupPostCommentRepository = groupPostCommentRepository;
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
        } else if(groupPostDTO.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new GroupFeedException("O conteúdo da publicação não pode ter mais de "+MAX_CONTENT_LENGTH+" caracteres.");
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

    public void checkPermissionForEditComment(GroupPostComment post, boolean forDelete) {
        String authorId = String.valueOf(UserService.getInstance().getUserInSession().getProfile().getId());
        if (!post.getAuthorId().equals(authorId)) {
            if(UserService.getInstance().isUserAdminSession()) {
                if(forDelete) {
                    // admin can delete any post
                    return;
                }
            }

            String groupId = groupPostRepository.findFirstByIdAndDeletedIsFalse(post.getGroupPostId()).get().getGroupId();

            if(forDelete && RolesService.getInstance().hasPermission(groupId, FeaturesTypes.FEED, Permission.READ_WRITE_DELETE)) {
                // user with permission can delete any post
                return;
            }

            throw new GroupFeedException("Você não tem permissão para editar este comentário.");
        }
    }

    public GroupPost editGroupPost(String groupId, String postId, GroupPostDTO groupPostDTO) {

        // check permission post
        RolesService.getInstance().checkPermission(groupId, FeaturesTypes.FEED, Permission.READ_WRITE);

        GroupPost post = getGroupPost(groupId, postId);

        checkPermissionForEdit(post, false);

        if(groupPostDTO.getContent() != null && !groupPostDTO.getContent().isEmpty()) {
            if(groupPostDTO.getContent().length() > MAX_CONTENT_LENGTH) {
                throw new GroupFeedException("O conteúdo da publicação não pode ter mais de "+MAX_CONTENT_LENGTH+" caracteres.");
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

        checkAccessToGroupPost(groupPostId);

        if(reaction == null || reaction.isEmpty() || reaction.length() > 20) {
            throw new GroupFeedException("Reação inválida.");
        }

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
    public List<GroupPostCommentDTO> getGroupPostComments(String groupPostId) {
        Optional<List<GroupPostComment>> comments = groupPostCommentRepository.findByGroupPostIdAndDeletedIsFalse(groupPostId);
        if(comments.isPresent()) {
            List<GroupPostCommentDTO> commentsDTO = new ArrayList<GroupPostCommentDTO>();
            for(GroupPostComment comment : comments.get()) {
                GroupPostCommentDTO commentObj = new GroupPostCommentDTO();
                commentObj.setId(comment.getId());
                commentObj.setContent(comment.getContent());
                commentObj.setAuthorId(comment.getAuthorId());
                commentObj.setAuthor(ProfileService.getInstance().findFirstById(comment.getAuthorId()));
                commentsDTO.add(commentObj);
            }

            return commentsDTO;
        }
        return null;
    }

    private void checkCommentContent(String comment) {
        if(comment == null || comment.isEmpty()) {
            throw new GroupFeedException("O conteúdo do comentário não pode estar vazio.");
        } else if(comment.length() > MAX_COMMENT_LENGTH) {
            throw new GroupFeedException("O conteúdo do comentário não pode ter mais de "+MAX_COMMENT_LENGTH+" caracteres.");
        }
    }

    private void checkAccessToGroupPost(String groupPostId) {
        // check permission post for read
        String groupId = groupPostRepository.findFirstByIdAndDeletedIsFalse(groupPostId).get().getGroupId();
        RolesService.getInstance().checkPermission(groupId, FeaturesTypes.FEED, Permission.READ);
    }

    public GroupPostComment editGroupPostComment(String commentId, String comment) {

        checkCommentContent(comment);

        GroupPostComment existingComment = groupPostCommentRepository.findFirstByIdAndDeletedFalse(commentId).orElseThrow(() -> new PostNotFoundException("Comentário não foi encontrado."));

        checkAccessToGroupPost(existingComment.getGroupPostId());

        checkPermissionForEditComment(existingComment, false);

        existingComment.setContent(comment);
        return groupPostCommentRepository.save(existingComment);

    }

    public boolean deleteGroupPostComment(String commentId) {
        GroupPostComment existingComment = groupPostCommentRepository.findFirstByIdAndDeletedFalse(commentId).orElseThrow(() -> new PostNotFoundException("Comentário não foi encontrado."));

        checkAccessToGroupPost(existingComment.getGroupPostId());

        checkPermissionForEditComment(existingComment, true);

        existingComment.setDeleted(true);
        groupPostCommentRepository.save(existingComment);

        return true;
    }

    public GroupPostComment createGroupPostComment(String groupPostId, String comment) {

        checkCommentContent(comment);

        checkAccessToGroupPost(groupPostId);

        String authorId = String.valueOf(UserService.getInstance().getUserInSession().getProfile().getId());
        GroupPostComment commentObj = new GroupPostComment();
        commentObj.setGroupPostId(groupPostId);
        commentObj.setContent(comment);
        commentObj.setAuthorId(authorId);
        return groupPostCommentRepository.save(commentObj);
    }

}