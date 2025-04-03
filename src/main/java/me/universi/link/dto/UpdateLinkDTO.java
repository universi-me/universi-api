package me.universi.link.dto;

import me.universi.link.enums.TypeLink;

public record UpdateLinkDTO(
    String url,
    TypeLink type,
    String name
) { }
