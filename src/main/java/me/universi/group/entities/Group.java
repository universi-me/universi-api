package me.universi.group.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import me.universi.capacity.entidades.Folder;
import me.universi.group.entities.GroupSettings.GroupSettings;
import me.universi.group.enums.GroupType;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.user.services.JsonUserLoggedFilter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Entity(name = "system_group")
@SQLDelete(sql = "UPDATE system_group SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Group implements Serializable {

    @Serial
    private static final long serialVersionUID = -2163545345342344343L;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
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

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    @Column(name = "image")
    public String image;

    @Column(name = "bannerImage")
    public String bannerImage;

    @Column(name = "headerImage")
    public String headerImage;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="profile_id")
    @NotNull
    public Profile admin;

    @JsonIgnore
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    public Collection<GroupAdmin> administrators;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="group_settings_id")
    @NotNull
    public GroupSettings groupSettings;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    @JsonIgnore
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    public Collection<ProfileGroup> participants;

    @JsonIgnore
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    public Collection<Subgroup> subGroups;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public GroupType type;

    /** The group's ability to be accessed directly through the URL (parent of all groups) */
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "group_root")
    @NotNull
    public boolean rootGroup;

    /** Can create subGroups */
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "can_create_group")
    @NotNull
    public boolean canCreateGroup;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "can_enter")
    @NotNull
    public boolean canEnter;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "can_add_participant")
    @NotNull
    public boolean canAddParticipant;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "public_group")
    @NotNull
    public boolean publicGroup;

    @OneToMany(mappedBy = "ownerGroup", fetch = FetchType.EAGER)
    @JsonIgnore
    private Collection<Folder> folders;

    /*Attribute indicates that the group must be part of the person's resume*/
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "enable_curriculum")
    private boolean enableCurriculum;

    public Group() {
    }

    public Group(String nickname, String name, String description, Profile admin, Collection<ProfileGroup> participants, GroupType type, Collection<Subgroup> subGroups, boolean rootGroup, boolean canCreateGroup, boolean enableCurriculum) {
        this.nickname = nickname;
        this.name = name;
        this.description = description;
        this.admin = admin;
        this.participants = participants;
        this.type = type;
        this.subGroups = subGroups;
        this.rootGroup = rootGroup;
        this.canCreateGroup = canCreateGroup;
        this.enableCurriculum = enableCurriculum;
    }

    public Group(String nickname, String name, String description, Profile admin, GroupType type, Date createdAt) {
        this.nickname = nickname;
        this.name = name;
        this.description = description;
        this.admin = admin;
        this.type = type;
        this.createdAt = createdAt;
        this.enableCurriculum = false;
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

    public Collection<ProfileGroup> getParticipants() {
        return participants;
    }

    public void setParticipants(Collection<ProfileGroup> participants) {
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

    public Collection<Subgroup> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(Collection<Subgroup> subGroups) {
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

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Transient
    public Group getOrganization() {
        return GroupService.getInstance().getGroupRootFromGroupId(this.id);
    }

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Transient
    public boolean isCanEdit() {
        return GroupService.getInstance().canEditGroup(this);
    }

    @Override
    public String toString() {
        return "Grupo [id=\""+this.id+"\", nome=\""+this.name+"\", descricao=\""+this.description+"\"]";
    }

    public boolean isEnableCurriculum() {
        return enableCurriculum;
    }

    public void setEnableCurriculum(boolean enableCurriculum) {
        this.enableCurriculum = enableCurriculum;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public GroupSettings getGroupSettings() {
        return groupSettings;
    }

    public void setGroupSettings(GroupSettings groupSettings) {
        this.groupSettings = groupSettings;
    }

    public Collection<GroupAdmin> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(Collection<GroupAdmin> administrators) {
        this.administrators = administrators;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }
}
