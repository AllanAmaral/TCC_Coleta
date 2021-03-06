package business.controller;

import business.objects.Lixeira;
import business.util.JsfUtil;
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
public class IndexController extends GenericController implements Serializable {

    private Integer idLixeira;
    private BigDecimal valorColetado;

    @EJB
    private dao.LixeiraFacade lixeiraFacade;

    public IndexController() {
    }

    private LixeiraFacade getFacadeLixeira() {
        return lixeiraFacade;
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

    public void randomValoresLixeiras() {
        Integer min = getFacadeLixeira().minId();
        Integer max = getFacadeLixeira().maxId();
        Lixeira lixeira = getFacadeLixeira().find((int) (Math.floor(Math.random() * (max - min + 1)) + min));
        BigDecimal valorColetado = lixeira.getColetadoLixeiraKg();
        valorColetado = valorColetado.add(new BigDecimal((Math.floor(Math.random()
                * (lixeira.getCapacidadeLixeiraKg().subtract(valorColetado)).doubleValue() - 1))));
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
}
