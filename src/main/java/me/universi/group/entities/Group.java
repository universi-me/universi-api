package me.universi.group.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import me.universi.capacity.entidades.Folder;
import me.universi.group.enums.GroupType;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;


@Entity(name = "system_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;
    
    @Column(name = "nickname")
    @NotNull
    public String nickname;

    @Column(name = "name")
    public String name;

    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    @Column(name = "image")
    public String image;

    @ManyToOne
    @JoinColumn(name="profile_id")
    @NotNull
    public Profile admin;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "profile_group",
            joinColumns = { @JoinColumn(name = "group_id") },
            inverseJoinColumns = { @JoinColumn(name =  "profile_id") }
    )
    @JsonIgnore
    public Collection<Profile> participants;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinTable(
            name = "subgroup",
            joinColumns = { @JoinColumn(name = "group_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "subgroup_id", referencedColumnName = "id") }
    )
    @JsonIgnore
    public Collection<Group> subGroups;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public GroupType type;

    /** The group's ability to be accessed directly through the URL (parent of all groups) */
    @Column(name = "group_root")
    @NotNull
    public boolean rootGroup;

    /** Can create subGroups */
    @Column(name = "can_create_group")
    @NotNull
    public boolean canCreateGroup;

    @Column(name = "can_enter")
    @NotNull
    public boolean canEnter;

    @Column(name = "can_add_participant")
    @NotNull
    public boolean canAddParticipant;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "public_group")
    @NotNull
    public boolean publicGroup;

    @OneToMany(mappedBy = "ownerGroup", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<Folder> folders;

    public Group() {
    }

    public Group(String nickname, String name, String description, Profile admin, Collection<Profile> participants, GroupType type, Collection<Group> subGroups, boolean rootGroup, boolean canCreateGroup) {
        this.nickname = nickname;
        this.name = name;
        this.description = description;
        this.admin = admin;
        this.participants = participants;
        this.type = type;
        this.subGroups = subGroups;
        this.rootGroup = rootGroup;
        this.canCreateGroup = canCreateGroup;
    }

    public Group(String nickname, String name, String description, Profile admin, GroupType type, Date createdAt) {
        this.nickname = nickname;
        this.name = name;
        this.description = description;
        this.admin = admin;
        this.type = type;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
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

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }
    
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Collection<Group> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(Collection<Group> subGroups) {
        this.subGroups = subGroups;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public void setId(UUID id) {
        this.id = id;
    }

    @Transient
    public Collection<Folder> getFolders() {
        return this.folders;
    }

    public void setFolders(Collection<Folder> folders) {
        this.folders = folders;
    }

    @Transient
    public String getPath() {
        return GroupService.getInstance().getGroupPath(this.id);
    }

    @Override
    public String toString() {
        return "Grupo [id=\""+this.id+"\", nome=\""+this.name+"\", descricao=\""+this.description+"\"]";
    }
}
