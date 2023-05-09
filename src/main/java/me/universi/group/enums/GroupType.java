package me.universi.group.enums;

public enum GroupType {
    INSTITUTION("Instituição"),
    CAMPUS("Campus"),
    COURSE("Curso"),
    PROJECT("Projeto"),
    CLASSROOM("Sala de Aula"),
    MONITORIA("Monitoria"),
    LABORATORY("Laboratório"),
    ACADEMIC_CENTER("Centro Acadêmico"),
    DEPARTMENT("Departamento"),
    STUDY_GROUP("Grupo de Estudo");
	
    public final String label;
	
    private GroupType(String label) {
        this.label = label;
    }
}
