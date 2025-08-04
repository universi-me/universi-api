package me.universi.group.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity(name = "GroupTheme")
@Table(name = "group_theme", schema = "system_group")
@SQLDelete(sql = "UPDATE system_group.group_theme SET deleted = true WHERE id=?")
@SQLRestriction( value = "NOT deleted" )
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupTheme  implements Serializable {

    @Serial
    private static final long serialVersionUID = -31637842245354343L;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @Column(name = "primary_color")
    public String primaryColor;
    @Column(name = "secondary_color")
    public String secondaryColor;
    @Column(name = "background_color")
    public String backgroundColor;
    @Column(name = "card_background_color")
    public String cardBackgroundColor;
    @Column(name = "card_item_color")
    public String cardItemColor;
    @Column(name = "font_color_v1")
    public String fontColorV1;
    @Column(name = "font_color_v2")
    public String fontColorV2;
    @Column(name = "font_color_v3")
    public String fontColorV3;
    @Column(name = "font_color_links")
    public String fontColorLinks;
    @Column(name = "font_color_disabled")
    public String fontColorDisabled;
    @Column(name = "button_hover_color")
    public String buttonHoverColor;
    @Column(name = "font_color_alert")
    public String fontColorAlert;
    @Column(name = "font_color_success")
    public String fontColorSuccess;
    @Column(name = "wrong_invalid_color")
    public String wrongInvalidColor;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    @JsonIgnore
    @JoinColumn(name="group_settings_id")
    @OneToOne(fetch = FetchType.LAZY)
    @NotNull
    private GroupSettings groupSettings;

    public GroupTheme() {
    }

    public GroupSettings getGroupSettings() {
        return groupSettings;
    }

    public void setGroupSettings(GroupSettings groupSettings) {
        this.groupSettings = groupSettings;
    }
}
