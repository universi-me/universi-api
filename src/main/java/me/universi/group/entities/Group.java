package me.universi.group.entities;

import com.fasterxml.jackson.annotation.*;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

import me.universi.activity.entities.Activity;
import me.universi.capacity.entidades.Folder;
import me.universi.group.services.GroupService;
import me.universi.image.entities.ImageMetadata;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.role.entities.Role;
import me.universi.role.services.RoleService;
import me.universi.user.services.EnvironmentService;
import me.universi.user.services.JsonUserLoggedFilter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Entity(name = "Group")
@Table( name = "system_group", schema = "system_group" )
@SQLDelete(sql = "UPDATE system_group.system_group SET deleted = true WHERE id=?")
@SQLRestriction( value = "NOT deleted" )
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
    private String nickname;

    @Column(name = "name")
    private String name;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Nullable
    @OneToOne
    @JoinColumn( name = "image_metadata_id" )
    private ImageMetadata image;

    @Nullable
    @OneToOne
    @JoinColumn( name = "banner_image_metadata_id" )
    private ImageMetadata bannerImage;

    @Nullable
    @OneToOne
    @JoinColumn( name = "header_image_metadata_id" )
    private ImageMetadata headerImage;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="profile_id")
    @NotNull
    private Profile admin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="group_settings_id")
    @NotNull
    private GroupSettings groupSettings;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    @JsonIgnore
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private Collection<ProfileGroup> participants;

    @JsonIgnore
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "parent_group_id", nullable = true, referencedColumnName = "id" )
    private Group parentGroup;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Nullable
    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    @JoinColumn( name = "activity_id", nullable = true )
    private Activity activity;

    @JsonIgnore
    @OneToMany(mappedBy = "parentGroup", fetch = FetchType.LAZY)
    @NotNull
    private Collection<Group> subGroups;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @NotNull
    @ManyToOne
    @JoinColumn( name = "type_id", nullable = false )
    private GroupType type;

    /** Can create subGroups */
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "can_create_group")
    @NotNull
    private boolean canCreateGroup;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "can_enter")
    @NotNull
    private boolean canEnter;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "can_add_participant")
    @NotNull
    private boolean canAddParticipant;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Column(name = "public_group")
    @NotNull
    private boolean publicGroup;

    @ManyToMany(mappedBy = "grantedAccessGroups", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<Folder> foldersGrantedAccess;

    public Group() {
    }

    public Group(String nickname, String name, String description, Profile admin, Collection<ProfileGroup> participants, GroupType type, Group parentGroup, Collection<Group> subGroups, boolean canCreateGroup) {
        this.nickname = nickname;
        this.name = name;
        this.description = description;
        this.admin = admin;
        this.participants = participants;
        this.type = type;
        this.parentGroup = parentGroup;
        this.subGroups = subGroups;
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

    public Collection<ProfileGroup> getParticipants() {
        return this.participants == null
            ? Collections.emptyList()
            : this.participants.stream()
                    .filter( pg -> ProfileService.getInstance().isValid(pg.getProfile()) )
                    .toList();
    }

    public void setParticipants(Collection<ProfileGroup> participants) {
        this.participants = participants;
    }

    @Transient
    @JsonIgnore
    public Collection<ProfileGroup> getAdministrators() {
        return this.getParticipants().stream()
            .filter( pg -> ProfileService.getInstance().isValid(pg.getProfile()) )
            .filter( ProfileGroup::isAdmin )
            .toList();
    }

    public @NotNull GroupType getType() {
        return type;
    }

    public void setType( @NotNull GroupType type ) {
        this.type = type;
    }
    
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Optional<Group> getParentGroup() { return Optional.ofNullable( parentGroup ); }
    public void setParentGroup(Group parentGroup) { this.parentGroup = parentGroup; }

    public Optional<Activity> getActivity() { return Optional.ofNullable( activity ); }
    public void setActivity( Activity activity ) { this.activity = activity; }

    /**
     * Checks if the Group is a regular group or an special group ( eg. an {@link Activity} group )
     * @return {@code true} if group is regular, otherwise returns {@code false};
     */
    @Transient public boolean isRegularGroup() { return activity == null; }
    @Transient @JsonIgnore public boolean isActivityGroup() { return activity != null; }

    public Collection<Group> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(Collection<Group> subGroups) {
        this.subGroups = subGroups;
    }

    /** The group's ability to be accessed directly through the URL (parent of all groups) */
    @Transient
    public boolean isRootGroup() {
        return this.getParentGroup().isEmpty();
    }

    public boolean isCanCreateGroup() {
        return canCreateGroup;
    }

    public void setCanCreateGroup(boolean canCreateGroup) {
        this.canCreateGroup = canCreateGroup;
    }

    public @Nullable ImageMetadata getImage() { return image; }
    public void setImage(ImageMetadata image) { this.image = image; }

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

    public static final String PATH_DIVISOR = "/";

    @Transient
    public String getPath() {
        return getParentGroup().map( Group::getPath ).orElse( "" ) + PATH_DIVISOR + this.nickname;
    }

    @JsonIgnore
    @Transient
    public @NotNull Group getOrganization() {
        return getParentGroup().map( Group::getOrganization ).orElse( this );
    }

    @JsonProperty( "organization" )
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @Transient
    private @Nullable Group getJsonOrganization() {
        return this.getParentGroup()
            .map( parent -> parent.isRootGroup()
                ? parent
                : parent.getJsonOrganization()
            )
            .orElse( null );
    }

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @Transient
    public boolean isCanEdit() {
        return GroupService.getInstance().hasPermissionToEdit(this);
    }

    @Override
    public String toString() {
        return "Grupo [id=\""+this.id+"\", nome=\""+this.name+"\", descricao=\""+this.description+"\"]";
    }

    public @Nullable ImageMetadata getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(ImageMetadata bannerImage) {
        this.bannerImage = bannerImage;
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public GroupSettings getGroupSettings() {
        return this.groupSettings;
    }

    public void setGroupSettings(GroupSettings groupSettings) {
        this.groupSettings = groupSettings;
    }

    public @Nullable ImageMetadata getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(ImageMetadata headerImage) {
        this.headerImage = headerImage;
    }

    public Collection<Folder> getFoldersGrantedAccess() {
        return this.foldersGrantedAccess;
    }

    public void setFoldersGrantedAccess(Collection<Folder> foldersGrantedAccess) {
        this.foldersGrantedAccess = foldersGrantedAccess;
    }

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getBuildHash() {
        if(!this.isRootGroup()) {
            return null;
        }
        return EnvironmentService.getInstance().getBuildHash();
    }

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public @Nullable Role getRole() {
        return ProfileService.getInstance()
            .getProfileInSession()
            .map( profile ->
                RoleService.getInstance().getAssignedRole(
                    profile.getId(),
                    this.id
                ) )
            .orElse( null );
    }
}
