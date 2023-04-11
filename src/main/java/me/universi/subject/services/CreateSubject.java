package me.universi.subject.services;

import me.universi.subject.entities.Subject;

@FunctionalInterface
public interface CreateSubject {

    Subject createSubject(Subject subject);
}
