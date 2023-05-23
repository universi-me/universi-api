package me.universi.profile.entities;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import me.universi.competence.entities.Competence;
import me.universi.group.entities.Group;
import me.universi.link.entities.Link;
import me.universi.profile.enums.Gender;
import me.universi.recommendation.entities.Recommendation;
import me.universi.user.entities.User;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Collection;
import java.util.Date;

@Entity(name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_generator")
    @SequenceGenerator(name = "profile_generator", sequenceName = "profile_sequence", allocationSize = 1)
    @Column(name = "id_profile")
    private Long id;

    @OneToOne
    private User user;
    @Column(name = "name")
    private String firstname;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "image")
    private String image;
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_link")
    private Link link;
    @ManyToMany(mappedBy = "profile", fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    private Collection<Competence> competences;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "profile_group",
            joinColumns = { @JoinColumn(name = "id_profile") },
            inverseJoinColumns = { @JoinColumn(name = "id_group") }
    )
    private Collection<Group> groups;
    @OneToMany(mappedBy = "profile")
    private Collection<Link> links;
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @OneToMany(mappedBy = "origin")
    private Collection<Recommendation> recomendacoesFeitas;
    @OneToMany(mappedBy = "destiny")
    private Collection<Recommendation> recomendacoesRecebidas;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    private Date creationDate;

    public Profile(Long id, User user, String bio, Link link, Collection<Competence> competences, Collection<Group> groups, Collection<Link> links) {
        this.id = id;
        this.user = user;
        this.bio = bio;
        this.link = link;
        this.competences = competences;
        this.groups = groups;
        this.links = links;
    }

    public Profile(){

    }

    public Long getId() {
        return id;
    }

    public User getUsuario() {
        return user;
    }

    public void setUsuario(User user) {
        this.user = user;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Collection<Competence> getCompetences() {
        return competences;
    }

    public void setCompetences(Collection<Competence> competences) {
        this.competences = competences;
    }

    public Collection<Group> getGroups() {
        return groups;
    }

    public void setGroups(Collection<Group> groups) {
        this.groups = groups;
    }

    public Collection<Link> getLinks() {
        return links;
    }

    public void setLinks(Collection<Link> links) {
        this.links = links;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Gender getSexo() {
        return gender;
    }

    public void setSexo(Gender gender) {
        this.gender = gender;
    }

    public Collection<Recommendation> getRecomendacoesFeitas() {
        return recomendacoesFeitas;
    }

    public Collection<Recommendation> getRecomendacoesRecebidas() {
        return recomendacoesRecebidas;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setRecomendacoesFeitas(Collection<Recommendation> recomendacoesFeitas) {
        this.recomendacoesFeitas = recomendacoesFeitas;
    }

    public void setRecomendacoesRecebidas(Collection<Recommendation> recomendacoesRecebidas) {
        this.recomendacoesRecebidas = recomendacoesRecebidas;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
