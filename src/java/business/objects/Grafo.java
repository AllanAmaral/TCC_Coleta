package business.objects;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Allan.Amaral
 */
public class Grafo {
    private List<Vertice> vertices;
    private List<Aresta> arestas;

    public Grafo() {
        vertices = new ArrayList<>();
        arestas = new ArrayList<>();
    }

    public Vertice addVertice(Integer idEsquina, Integer idLixeira) {
        Vertice v = new Vertice(idEsquina, idLixeira);
        vertices.add(v);
        return v;
    }

    public Aresta addAresta(Vertice origem, Vertice destino) {
        Aresta e = new Aresta(origem, destino);
        origem.addAdj(e);
        arestas.add(e);
        return e;
    }

    @Override
    public String toString() {
        String r = "";
        for (Vertice u : vertices) {
            r += (u.getIdEsquina() != null ? ("E" + u.getIdEsquina()) : "")
                    + (u.getIdLixeira() != null ? ("L" + u.getIdLixeira()) : "")
                    + " -> ";
            for (Aresta e : u.getAdj()) {
                Vertice v = e.getDestino();
                r += (v.getIdEsquina() != null ? ("E" + v.getIdEsquina()) : "")
                        + (v.getIdLixeira() != null ? ("L" + v.getIdLixeira()) : "")
                        + ", ";
            }
            r += "\n";
        }
        return r;
    }

    public List<Aresta> getArestas() {
        return arestas;
    }

    public void setArestas(List<Aresta> arestas) {
        this.arestas = arestas;
    }
    
    public List<Vertice> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertice> vertices) {
        this.vertices = vertices;
    }
    
}
