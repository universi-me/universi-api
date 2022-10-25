package me.universi.link.entities;

import javax.persistence.*;

@Entity(name = "link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_link")
    private long id;
    @Column(name = "tipo")
    private TipoLink tipo;
    @Column(name = "url")
    private String url;

    public Link(TipoLink tipo, String url){
        this.tipo = tipo;
        this.url = url;
    }

    public Link() {}

    public TipoLink getTipo() {
        return tipo;
    }

    public void setTipo(TipoLink tipo) {
        this.tipo = tipo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
}
