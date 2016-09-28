package business.controller;

import business.objects.Aresta;
import business.objects.Esquina;
import business.objects.Grafo;
import business.objects.Lixeira;
import business.objects.Vertice;
import business.util.JsfUtil;
import dao.EsquinaFacade;
import dao.LixeiraFacade;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.json.Json;
import javax.json.stream.JsonGenerator;

@Named("indexController")
@SessionScoped
public class IndexController implements Serializable {
    
    private Integer idLixeira;
    private BigDecimal valorColetado;
    @EJB
    private dao.LixeiraFacade lixeiraFacade;
    @EJB
    private dao.EsquinaFacade esquinaFacade;

    public IndexController() {
        if (getFacadeLixeira() != null && getFacadeEsquina() != null)
            criarGrafo();
    }

    private LixeiraFacade getFacadeLixeira() {
        return lixeiraFacade;
    }
    
    private EsquinaFacade getFacadeEsquina() {
        return esquinaFacade;
    }

    public Integer getIdLixeira() {
        return idLixeira;
    }

    public void setIdLixeira(Integer idLixeira) {
        this.idLixeira = idLixeira;
    }

    public BigDecimal getValorColetado() {
        return valorColetado;
    }

    public void setValorColetado(BigDecimal valorColetado) {
        this.valorColetado = valorColetado;
    }
    
    public String getImagemStatus(Lixeira lixeira) {
        String img = null;
        switch (getStatus(lixeira)) {
            case 1:
                img = "lixeira-vermelha-icon.png";
                break;
            case 2:
                img = "lixeira-amarela-icon.png";
                break;
            case 3:
                img = "lixeira-azul-icon.png";
                break;
        }
        return img;
    }
    
    private int getStatus(Lixeira lixeira) { 
        int cor = 3;
        
        if (lixeira == null) return cor;
        
        if (lixeira.getColetadoLixeiraKg()
                    .compareTo(lixeira.getCapacidadeLixeiraKg()
                            .multiply(new BigDecimal(0.3))) < 1)
            cor = 3;
        if ((lixeira.getColetadoLixeiraKg()
                .compareTo(lixeira.getCapacidadeLixeiraKg()
                        .multiply(new BigDecimal(0.7))) < 1)
            && (lixeira.getColetadoLixeiraKg()
                .compareTo(lixeira.getCapacidadeLixeiraKg()
                        .multiply(new BigDecimal(0.3))) > -1))
            cor = 2;
        if (lixeira.getColetadoLixeiraKg()
                .compareTo(lixeira.getCapacidadeLixeiraKg()
                        .multiply(new BigDecimal(0.7))) > -1)
            cor = 1;
        return cor;
    }
    
    public void randomValoresLixeiras() {
        Integer min = getFacadeLixeira().minId();
        Integer max = getFacadeLixeira().maxId();
        Lixeira lixeira = getFacadeLixeira().find((int) (Math.floor(Math.random() * (max - min + 1)) + min));
        BigDecimal valorColetado = lixeira.getColetadoLixeiraKg();
        valorColetado = valorColetado.add(new BigDecimal((Math.floor(Math.random() 
                * (lixeira.getCapacidadeLixeiraKg().subtract(valorColetado)).doubleValue() -  1))));
        lixeira.setColetadoLixeiraKg(valorColetado);
        getFacadeLixeira().edit(lixeira);
        
        carregarPontosLixeiras();
    }
    
    public void carregarPontosLixeiras() {
        try {
            if (idLixeira != null && valorColetado != null) {
                Lixeira lixeira = getFacadeLixeira().find(idLixeira);
                if (lixeira != null) {
                    valorColetado = valorColetado.add(lixeira.getColetadoLixeiraKg());
                    if (valorColetado.compareTo(lixeira.getCapacidadeLixeiraKg()) < 1) {
                        lixeira.setColetadoLixeiraKg(valorColetado);
                        getFacadeLixeira().edit(lixeira);
                    }
                }
            }
            
            List<Lixeira> lixeiras = getFacadeLixeira().findAll();
            //FileOutputStream fos = new FileOutputStream("C:/Users/allan.amaral/Documents/GitHub/TCC_Coleta/web/js/pontos.json");
            String file = FacesContext.getCurrentInstance().getExternalContext().getRealPath("")
                    + "\\js\\pontos.json";
            FileOutputStream fos = new FileOutputStream(file);
            JsonGenerator geradorJson = Json.createGenerator(fos);
            int cor;
            
            geradorJson.writeStartArray();
            for (Lixeira lixeira : lixeiras) {
                cor = getStatus(lixeira);
                
                // começando a escrever o objeto JSON e então as propriedades, por fim fecha o objeto
                geradorJson.writeStartObject()
                        .write("Id", lixeira.getIdLixeira())
                        .write("Cor", cor)
                        .write("Latitude", lixeira.getLatitude())
                        .write("Longitude", lixeira.getLongitude())
                        .write("Descricao", "Capacidade Kg: " + lixeira.getCapacidadeLixeiraKg()
                                            + "\n" + "Coletado Kg: " + lixeira.getColetadoLixeiraKg())
                        .writeEnd();
            }

            geradorJson.writeEnd().close();

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
        }
    }

    private void criarGrafo() {
        try {
            Grafo grafo = new Grafo();
            List<Lixeira> listLixeiras = getFacadeLixeira().findAll();
            List<Esquina> listEsquinas = getFacadeEsquina().findAll();
            HashMap<Integer,Lixeira> mapLixeiras = new HashMap<>();
            HashMap<Integer,Esquina> mapEsquinas = new HashMap<>();

            for (Lixeira lixeira : listLixeiras) {
                mapLixeiras.put(lixeira.getIdLixeira(), lixeira);
                grafo.addVertice(null, lixeira.getIdLixeira());
            }
            for (Esquina esquina : listEsquinas) {
                mapEsquinas.put(esquina.getIdEsquina(), esquina);
                grafo.addVertice(esquina.getIdEsquina(), null);
            }

            grafo = addArestaPadrao(grafo, 1, 12, 2);
            grafo = addArestaPadrao(grafo, 2, 11, 3);
            grafo = addArestaPadrao(grafo, 3, 10, 4);
            grafo = addArestaPadrao(grafo, 4, 9, 5);
            grafo = addArestaPadrao(grafo, 5, 8, 6);
            grafo.addAresta(getVerticeEsquina(grafo, 7), getVerticeEsquina(grafo, 6));
            grafo = addArestaPadrao(grafo, 7, 36, 8);
            grafo = addArestaPadrao(grafo, 8, 40, 5);
            grafo = addArestaPadrao(grafo, 8, 37, 9);
            grafo = addArestaPadrao(grafo, 4, 41, 9);
            grafo = addArestaPadrao(grafo, 9, 38, 10);
            grafo = addArestaPadrao(grafo, 3, 42, 10);
            grafo = addArestaPadrao(grafo, 10, 39, 11);
            grafo.addAresta(getVerticeEsquina(grafo, 1), getVerticeEsquina(grafo, 11));
            grafo = addArestaPadrao(grafo, 12, 7, 7);
            grafo = addArestaPadrao(grafo, 12, 35, 13);
            grafo = addArestaPadrao(grafo, 9, 34, 13);
            grafo.addAresta(getVerticeEsquina(grafo, 13), getVerticeEsquina(grafo, 14));
            grafo.addAresta(getVerticeEsquina(grafo, 14), getVerticeEsquina(grafo, 15));
            grafo = addArestaPadrao(grafo, 10, 33, 15);
            grafo = addArestaPadrao(grafo, 15, 32, 16);
            grafo.addAresta(getVerticeEsquina(grafo, 11), getVerticeEsquina(grafo, 16));
            grafo = addArestaPadrao(grafo, 17, 30, 14);
            grafo = addArestaPadrao(grafo, 18, 29, 17);
            grafo.addAresta(getVerticeEsquina(grafo, 16), getVerticeEsquina(grafo, 18));
            grafo = addArestaPadrao(grafo, 19, 6, 12);
            grafo = addArestaPadrao(grafo, 19, 26, 20);
            grafo = addArestaPadrao(grafo, 13, 28, 20);
            grafo = addArestaPadrao(grafo, 20, 27, 21);
            grafo.addAresta(getVerticeEsquina(grafo, 18), getVerticeEsquina(grafo, 21));
            grafo = addArestaPadrao(grafo, 22, 5, 19);
            grafo = addArestaPadrao(grafo, 23, 23, 22);
            grafo = addArestaPadrao(grafo, 20, 25, 23);
            grafo = addArestaPadrao(grafo, 24, 24, 23);
            grafo.addAresta(getVerticeEsquina(grafo, 21), getVerticeEsquina(grafo, 24));
            grafo = addArestaPadrao(grafo, 25, 4, 22);
            grafo = addArestaPadrao(grafo, 26, 20, 25);
            grafo = addArestaPadrao(grafo, 23, 22, 26);
            grafo.addAresta(getVerticeEsquina(grafo, 26), getVerticeEsquina(grafo, 27));
            grafo = addArestaPadrao(grafo, 27, 21, 28);
            grafo.addAresta(getVerticeEsquina(grafo, 24), getVerticeEsquina(grafo, 28));
            grafo = addArestaPadrao(grafo, 29, 3, 25);
            grafo = addArestaPadrao(grafo, 29, 15, 30);
            grafo = addArestaPadrao(grafo, 26, 18, 30);
            grafo = addArestaPadrao(grafo, 30, 16, 31);
            grafo = addArestaPadrao(grafo, 31, 19, 27);
            grafo = addArestaPadrao(grafo, 31, 17, 32);
            grafo.addAresta(getVerticeEsquina(grafo, 28), getVerticeEsquina(grafo, 32));
            grafo = addArestaPadrao(grafo, 33, 2, 29);
            grafo = addArestaPadrao(grafo, 33, 13, 34);
            grafo = addArestaPadrao(grafo, 30, 14, 34);
            grafo.addAresta(getVerticeEsquina(grafo, 35), getVerticeEsquina(grafo, 33));
            grafo = addArestaPadrao(grafo, 36, 1, 35);
            grafo.addAresta(getVerticeEsquina(grafo, 34), getVerticeEsquina(grafo, 36));

            transformarGrafoEmJson(grafo, mapLixeiras, mapEsquinas);
            
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
        }
    }
    
    private Grafo addArestaPadrao(Grafo grafo, Integer e1, Integer l, Integer e2) {
        grafo.addAresta(getVerticeEsquina(grafo, e1), getVerticeLixeira(grafo, l));
        grafo.addAresta(getVerticeLixeira(grafo, 61), getVerticeEsquina(grafo, e2));
        grafo.addAresta(getVerticeEsquina(grafo, e1), getVerticeEsquina(grafo, e2));
        
        return grafo;
    }
    
    private Vertice getVerticeLixeira(Grafo grafo, Integer idLixeira) {
        for (Vertice vertice : grafo.getVertices()) {
            if (idLixeira.equals(vertice.getIdLixeira()))
                return vertice;
        }
        return null;
    }
    
    private Vertice getVerticeEsquina(Grafo grafo, Integer idEsquina) {
        for (Vertice vertice : grafo.getVertices()) {
            if (idEsquina.equals(vertice.getIdEsquina()))
                return vertice;
        }
        return null;
    }
    
    private void transformarGrafoEmJson(Grafo grafo, HashMap<Integer, Lixeira> mapLixeiras, HashMap<Integer, Esquina> mapEsquinas) throws FileNotFoundException {
        String file = FacesContext.getCurrentInstance().getExternalContext().getRealPath("")
                    + "\\js\\arestas.json";
        FileOutputStream fos = new FileOutputStream(file);
        JsonGenerator geradorJson = Json.createGenerator(fos);

        geradorJson.writeStartArray();
        for (Aresta aresta : grafo.getArestas()) {
            geradorJson.writeStartObject()
                .write("OrigemLatitude", aresta.getOrigem().getIdEsquina() != null 
                        ? mapEsquinas.get(aresta.getOrigem().getIdEsquina()).getLatitude()
                        : mapLixeiras.get(aresta.getOrigem().getIdLixeira()).getLatitude())
                .write("OrigemLongitude", aresta.getOrigem().getIdEsquina() != null 
                        ? mapEsquinas.get(aresta.getOrigem().getIdEsquina()).getLongitude()
                        : mapLixeiras.get(aresta.getOrigem().getIdLixeira()).getLongitude())
                .write("DestinoLatitude", aresta.getDestino().getIdEsquina() != null 
                        ? mapEsquinas.get(aresta.getDestino().getIdEsquina()).getLatitude()
                        : mapLixeiras.get(aresta.getDestino().getIdLixeira()).getLatitude())
                .write("DestinoLongitude", aresta.getDestino().getIdEsquina() != null 
                        ? mapEsquinas.get(aresta.getDestino().getIdEsquina()).getLongitude()
                        : mapLixeiras.get(aresta.getDestino().getIdLixeira()).getLongitude())
                .writeEnd();
        }

        geradorJson.writeEnd().close();
    }
    
    
}
