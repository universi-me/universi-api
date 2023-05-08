package me.universi.group.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import me.universi.group.enums.GroupType;
import me.universi.profile.entities.Profile;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity(name = "system_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_group")
    public Long id;
    
    @Column(name = "nickname")
    public String nickname;

    @Column(name = "name")
    public String name;

    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    @Column(name = "image")
    public String image;

    @ManyToOne
    @JoinColumn(name="id_profile")
    public Profile admin;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "profile_group",
            joinColumns = { @JoinColumn(name = "id_group") },
            inverseJoinColumns = { @JoinColumn(name =  "id_profile") }
    )
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Profile.class)
    @JsonIdentityReference(alwaysAsId = true)
    public Collection<Profile> participants;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinTable(
            name = "subgroup",
            joinColumns = { @JoinColumn(name = "id_group", referencedColumnName = "id_group") },
            inverseJoinColumns = { @JoinColumn(name = "id_subgroup", referencedColumnName = "id_group") }
    )
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Group.class)
    @JsonIdentityReference(alwaysAsId = true)
    public Collection<Group> subGroups;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public GroupType type;

    /** The group's ability to be accessed directly through the URL (parent of all groups) */
    @Column(name = "group_root")
    public boolean rootGroup;

    /** Can create subGroups */
    @Column(name = "can_create_group")
    public boolean canCreateGroup;

    @Column(name = "can_enter")
    public boolean canEnter;

    @Column(name = "can_add_participant")
    public boolean canAddParticipant;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "public_group")
    public boolean publicGroup;

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

    public Long getId() {
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

    @Override
    public String toString() {
        return "Grupo [id=\""+this.id+"\", nome=\""+this.name+"\", descricao=\""+this.description+"\"]";
    }
}
