package me.universi.capacity.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.Content;
import me.universi.capacity.exceptions.CapacityException;

public interface CapacityServiceInterface {
    List<Content> getContentsByCategory(UUID categoryId) throws CapacityException;
    Collection<Content> getContentsByFolder(UUID folderId) throws CapacityException;
}