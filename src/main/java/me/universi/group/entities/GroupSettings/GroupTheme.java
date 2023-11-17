package me.universi.group.entities.GroupSettings;

/*
@primary-color:         #0091B9;
@secondary-color:       #934588;
@tertiary-color:        #FFFFFF;
@background-color:      #F5F4F4;
@card-background-color: #D9D9D9;
@card-item-color:       #F3F3F3;

@font-color-v1:       #FFFFFF;
@font-color-v2:       #191919;
@font-color-v3:       #8A8A8A;
@font-color-v4:       #7D7EAE;
@font-color-v5:       #F5F5F5;
@font-color-v6:       #4E4E4E;
@font-disabled-color: #6F6F6F;

@forms-color:               #E0E0E0;
@skills-1-color:            @secondary-color;
@wave-color:                #9294CC;
@button-yellow-hover-color: #d3a61e;
@button-hover-color:        @tertiary-color;
@alert-color:               #CC615B;
@success-color:             #35BD00;
@wrong-invalid-color:       #B33B3B;
@rank-color:                #6E70AF;
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity(name = "group_theme")
@SQLDelete(sql = "UPDATE group_theme SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupTheme {

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
    @Column(name = "tertiary_color")
    public String tertiaryColor;
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
    @Column(name = "font_color_v4")
    public String fontColorV4;
    @Column(name = "font_color_v5")
    public String fontColorV5;
    @Column(name = "font_color_v6")
    public String fontColorV6;
    @Column(name = "font_disabled_color")
    public String fontDisabledColor;
    @Column(name = "forms_color")
    public String formsColor;
    @Column(name = "skills_1_color")
    public String skills1Color;
    @Column(name = "wave_color")
    public String waveColor;
    @Column(name = "button_yellow_hover_color")
    public String buttonYellowHoverColor;
    @Column(name = "button_hover_color")
    public String buttonHoverColor;
    @Column(name = "alert_color")
    public String alertColor;
    @Column(name = "success_color")
    public String successColor;
    @Column(name = "wrong_invalid_color")
    public String wrongInvalidColor;
    @Column(name = "rank_color")
    public String rankColor;

    @JsonIgnore
    @Column(name = "deleted")
    public boolean deleted = Boolean.FALSE;

    @JsonIgnore
    @JoinColumn(name="group_settings_id")
    @OneToOne
    @NotNull
    public GroupSettings groupSettings;

    public GroupTheme() {
    }


}
