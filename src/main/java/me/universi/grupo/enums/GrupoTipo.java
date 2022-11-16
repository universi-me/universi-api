package me.universi.grupo.enums;

public enum GrupoTipo {
    INSTITUICAO("Instituição"),
    CAMPUS("Campus"),
    CURSO("Curso"),
    PROJETO("Projeto"),
    SALA_DE_AULA("Sala de Aula"),
    MONITORIA("Monitoria"),
    LABORATORIO("Laboratório"),
    CA("Centro Acadêmico"),
    GRUPO_DE_ESTUDO("Grupo de Estudo");
	
    public final String label;
	
    private GrupoTipo(String label) {
        this.label = label;
    }
}
