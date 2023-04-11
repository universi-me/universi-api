package me.universi.subject.services;

import me.universi.subject.entities.Subject;

@FunctionalInterface
public interface CreateSubjectService {

    Subject createSubject(Subject subject);
}
