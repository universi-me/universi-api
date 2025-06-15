package me.universi.profile.dto;

import java.util.Collection;

import me.universi.capacity.entidades.FolderFavorite;
import me.universi.capacity.entidades.FolderProfile;

public record ProfileFoldersDTO(
    Collection<FolderFavorite> favorites,
    Collection<FolderProfile> assignments
) {}
