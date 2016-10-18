package business.controller;

import business.objects.Lixeira;
import business.util.JsfUtil;
import dao.EsquinaFacade;
import dao.LixeiraFacade;
import java.io.FileOutputStream;

import java.io.Serializable;
import java.math.BigDecimal;
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
            int cor;
            String file = FacesContext.getCurrentInstance().getExternalContext().getRealPath("")
                    + "\\js\\pontos.json";
            FileOutputStream fos = new FileOutputStream(file);
            JsonGenerator geradorJson = Json.createGenerator(fos);
            
            String fileRota = FacesContext.getCurrentInstance().getExternalContext().getRealPath("")
                    + "\\js\\lixeirasRota.json";
            FileOutputStream fosRota = new FileOutputStream(fileRota);
            JsonGenerator geradorJsonRota = Json.createGenerator(fosRota);
            
            geradorJson.writeStartArray();
            geradorJsonRota.writeStartArray();
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
                
                if (cor == 1) {
                    geradorJsonRota.writeStartObject()
                        .write("Latitude", lixeira.getLatitude())
                        .write("Longitude", lixeira.getLongitude())
                        .writeEnd();
                }
            }
            
            geradorJson.writeEnd().close();
            geradorJsonRota.writeEnd().close();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
        }
    }
}
