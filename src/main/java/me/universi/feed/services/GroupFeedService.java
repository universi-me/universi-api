package me.universi.feed.services;


import me.universi.feed.dto.GroupPostDTO;
import me.universi.feed.exceptions.GroupFeedException;
import me.universi.feed.exceptions.PostNotFoundException;
import me.universi.feed.entities.GroupPost;
import me.universi.feed.repositories.GroupPostRepository;
import me.universi.profile.entities.Profile;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupFeedService {

    private final GroupPostRepository groupPostRepository;

    @Autowired
    public GroupFeedService(GroupPostRepository groupPostRepository) {
        this.groupPostRepository = groupPostRepository;
    }

    public List<GroupPost> getGroupPosts(String groupId) {
        Optional<List<GroupPost>> posts = groupPostRepository.findByGroupIdAndDeletedIsFalse(groupId);
        return posts.orElseThrow(() -> new PostNotFoundException("No posts found for the specified group."));
    }

    public GroupPost createGroupPost(String groupId, GroupPostDTO groupPostDTO) {
        String authorId = String.valueOf(UserService.getInstance().getUserInSession().getProfile().getId());
        if (groupPostDTO.getContent() == null || groupPostDTO.getContent().isEmpty()) {
            throw new GroupFeedException("O conteúdo do post não pode estar vazio.");
        } else if(groupPostDTO.getContent().length() > 3000) {
            throw new GroupFeedException("O conteúdo do post não pode ter mais de 3000 caracteres.");
        }
        GroupPost groupPost = new GroupPost(groupId, groupPostDTO.getContent(), authorId, false);
        return groupPostRepository.save(groupPost);
    }

    public GroupPost getGroupPost(String groupId, String postId) {
        Optional<GroupPost> existingPost = groupPostRepository.findFirstByGroupIdAndId(groupId, postId);
        if (existingPost.isPresent() && !existingPost.get().isDeleted()) {
            return existingPost.get();
        } else {
            throw new PostNotFoundException("Post not found or does not belong to the specified group.");
        }
    }

    public void checkPermissionForEdit(GroupPost post) {
        String authorId = String.valueOf(UserService.getInstance().getUserInSession().getProfile().getId());
        if (!post.getAuthorId().equals(authorId)) {
            if(!UserService.getInstance().isUserAdminSession()) {
                throw new GroupFeedException("Você não tem permissão para editar este post.");
            }
        }
    }

    public GroupPost editGroupPost(String groupId, String postId, GroupPostDTO groupPostDTO) {
        GroupPost post = getGroupPost(groupId, postId);

        checkPermissionForEdit(post);

        if(groupPostDTO.getContent() != null && !groupPostDTO.getContent().isEmpty()) {
            if(groupPostDTO.getContent().length() > 3000) {
                throw new GroupFeedException("O conteúdo do post não pode ter mais de 3000 caracteres.");
            }
            post.setContent(groupPostDTO.getContent());
        }

        return groupPostRepository.save(post);
    }

    public boolean deleteGroupPost(String groupId, String postId) {
        GroupPost post = getGroupPost(groupId, postId);

        checkPermissionForEdit(post);

        post.setDeleted(true);
        groupPostRepository.save(post);

        return true;
    }
}