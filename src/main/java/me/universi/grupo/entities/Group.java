package me.universi.grupo.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import me.universi.grupo.enums.GrupoTipo;
import me.universi.perfil.entities.Perfil;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity(name = "Grupo")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo")
    public Long id;
    
    @Column(name = "nickname")
    public String nickname;

    @Column(name = "nome")
    public String name;

    @Column(name = "descricao", columnDefinition = "TEXT")
    public String description;

    @Column(name = "imagem")
    public String image;

    @ManyToOne
    @JoinColumn(name="id_profile")
    public Perfil admin;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "perfil_grupo",
            joinColumns = { @JoinColumn(name = "id_grupo") },
            inverseJoinColumns = { @JoinColumn(name =  "id_perfil") }
    )
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Perfil.class)
    @JsonIdentityReference(alwaysAsId = true)
    public Collection<Perfil> participants;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinTable(
            name = "grupo_grupo",
            joinColumns = { @JoinColumn(name = "id_grupo", referencedColumnName = "id_grupo") },
            inverseJoinColumns = { @JoinColumn(name = "id_subgrupo", referencedColumnName = "id_grupo") }
    )
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Group.class)
    @JsonIdentityReference(alwaysAsId = true)
    public Collection<Group> subGroups;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    public GrupoTipo type;

    /** The group's ability to be accessed directly through the URL (parent of all groups) */
    @Column(name = "gruporoot")
    public boolean rootGroup;

    /** Can create subGroups */
    @Column(name = "podeCriarGrupo")
    public boolean canCreateGroup;

    @Column(name = "podeEntrar")
    public boolean canEnter;

    @Column(name = "podeAddParticipante")
    public boolean canAddParticipant;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_de_criacao")
    private Date createdAt;

    @Column(name = "publico")
    public boolean publicGroup;

    public Group() {
    }

    public Group(String nickname, String name, String description, Perfil admin, Collection<Perfil> participants, GrupoTipo type, Collection<Group> subGroups, boolean rootGroup, boolean canCreateGroup) {
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

    public Perfil getAdmin() {
        return admin;
    }

    public void setAdmin(Perfil admin) {
        this.admin = admin;
    }

    public Collection<Perfil> getParticipants() {
        return participants;
    }

    public void setParticipants(Collection<Perfil> participants) {
        this.participants = participants;
    }

    public GrupoTipo getType() {
        return type;
    }

    public void setType(GrupoTipo type) {
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
