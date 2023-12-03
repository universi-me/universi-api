package me.universi.group.entities.GroupSettings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;
import me.universi.user.services.JsonUserLoggedFilter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "group_settings")
@SQLDelete(sql = "UPDATE group_settings SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GroupSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = -41635422453554343L;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @JsonIgnore
    @OneToMany(mappedBy = "groupSettings", fetch = FetchType.EAGER)
    public Collection<GroupEmailFilter> filterEmails;

    @OneToOne(mappedBy = "groupSettings", fetch = FetchType.EAGER)
    public GroupTheme theme;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonUserLoggedFilter.class)
    @OneToMany(mappedBy = "groupSettings", fetch = FetchType.EAGER)
    public Collection<GroupFeatures> features;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public GroupSettings() {
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public UUID getId() {
        return id;
    }

    public Collection<GroupEmailFilter> getFilterEmails() {
        return filterEmails;
    }

    public void setFilterEmails(Collection<GroupEmailFilter> filtersEmail) {
        this.filterEmails = filtersEmail;
    }
}
