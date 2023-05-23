package me.universi.group.builder;

import me.universi.group.entities.Group;
import me.universi.group.enums.GroupType;
import me.universi.user.UserBuilder;

import java.util.Date;

public class GroupBuilder {

    public static Group createGroup(){
        return new Group("Java","Linguagem de programação","Aulas", UserBuilder.createUser().getProfile(),
                GroupType.STUDY_GROUP,new Date());
    }
}
