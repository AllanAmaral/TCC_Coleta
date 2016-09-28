package business.objects;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Allan.Amaral
 */
public class Vertice {
    private Integer idEsquina;
    private Integer idLixeira;
    private List<Aresta> adj;

    public Vertice(Integer idEsquina, Integer idLixeira) {
        this.idEsquina = idEsquina;
        this.idLixeira = idLixeira;
        this.adj = new ArrayList<>();
    }

    public void addAdj(Aresta e) {
        adj.add(e);
    } 

    public Integer getIdEsquina() {
        return idEsquina;
    }

    public void setIdEsquina(Integer idEsquina) {
        this.idEsquina = idEsquina;
    }

    public Integer getIdLixeira() {
        return idLixeira;
    }

    public void setIdLixeira(Integer idLixeira) {
        this.idLixeira = idLixeira;
    }

    public List<Aresta> getAdj() {
        return adj;
    }

    public void setAdj(List<Aresta> adj) {
        this.adj = adj;
    }
    
}
