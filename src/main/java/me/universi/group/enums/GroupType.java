package me.universi.group.enums;

/**
 * Enum with the types of groups that can be created
 */
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
    STUDY_GROUP("Grupo de Estudos"),

    GROUP_GENERAL("Grupo Geral"),
    DIRECTORATE("Diretoria"),
    MANAGEMENT("Gerência"),
    COORDINATION("Coordenação"),
    COMPANY_AREA("Área da Empresa"),
    DEVELOPMENT_TEAM("Time de Desenvolvimento"),
    INTEREST_GROUP("Grupo de Interesse"),
    MISCELLANEOUS_SUBJECTS("Assuntos Diversos"),
    ENTERTAINMENT("Entretenimento");
	
    public final String label;
	
    private GroupType(String label) {
        this.label = label;
    }
}
