package me.universi.group.DTO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import me.universi.group.entities.Group;
import me.universi.group.enums.GroupType;
import me.universi.profile.entities.Profile;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class GroupDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2192157095656137102L;

    public Long id;

    public String nickname;

    public String name;

    public String description;

    public String image;

    public Profile admin;


    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Profile.class)
    @JsonIdentityReference(alwaysAsId = true)
    public Collection<Profile> participants;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Group.class)
    @JsonIdentityReference(alwaysAsId = true)
    public Collection<Group> subGroups;

    @Enumerated(EnumType.STRING)
    public GroupType type;

    /** The group's ability to be accessed directly through the URL (parent of all groups) */
    public boolean rootGroup;

    /** Can create subGroups */
    public boolean canCreateGroup;

    public boolean canEnter;

    public boolean canAddParticipant;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public boolean publicGroup;

    public GroupDTO(Long id, String nickname, String name, String description, String image, Profile admin, Collection<Profile> participants, Collection<Group> subGroups, GroupType type, boolean rootGroup, boolean canCreateGroup, boolean canEnter, boolean canAddParticipant, Date createdAt, boolean publicGroup) {
        this.id = id;
        this.nickname = nickname;
        this.name = name;
        this.description = description;
        this.image = image;
        this.admin = admin;
        this.participants = participants;
        this.subGroups = subGroups;
        this.type = type;
        this.rootGroup = rootGroup;
        this.canCreateGroup = canCreateGroup;
        this.canEnter = canEnter;
        this.canAddParticipant = canAddParticipant;
        this.createdAt = createdAt;
        this.publicGroup = publicGroup;
    }

    public GroupDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Profile getAdmin() {
        return admin;
    }

    public void setAdmin(Profile admin) {
        this.admin = admin;
    }

    public Collection<Profile> getParticipants() {
        return participants;
    }

    public void setParticipants(Collection<Profile> participants) {
        this.participants = participants;
    }

    public Collection<Group> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(Collection<Group> subGroups) {
        this.subGroups = subGroups;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public boolean isRootGroup() {
        return rootGroup;
    }

    public void setRootGroup(boolean rootGroup) {
        this.rootGroup = rootGroup;
    }

    public boolean isCanCreateGroup() {
        return canCreateGroup;
    }

    public void setCanCreateGroup(boolean canCreateGroup) {
        this.canCreateGroup = canCreateGroup;
    }

    public boolean isCanEnter() {
        return canEnter;
    }

    public void setCanEnter(boolean canEnter) {
        this.canEnter = canEnter;
    }

    public boolean isCanAddParticipant() {
        return canAddParticipant;
    }

    public void setCanAddParticipant(boolean canAddParticipant) {
        this.canAddParticipant = canAddParticipant;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPublicGroup() {
        return publicGroup;
    }

    public void setPublicGroup(boolean publicGroup) {
        this.publicGroup = publicGroup;
    }
}
