package me.universi.subject.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class Subject implements Serializable {

    @Serial
    private static final long serialVersionUID = 7301030877136239243L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subject_generator")
    @SequenceGenerator(name = "subject_generator", sequenceName = "subject_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }
}
