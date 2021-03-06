package business.controller;

import business.objects.Caminhao;
import business.objects.CaminhaoMotorista;
import business.objects.HistoricoColeta;
import business.objects.Lixeira;
import business.objects.Motorista;
import business.objects.Rota;
import business.objects.RotaLixeira;
import business.util.JsfUtil;
import business.util.PaginationHelper;
import dao.CaminhaoFacade;
import dao.CaminhaoMotoristaFacade;
import dao.HistoricoColetaFacade;
import dao.LixeiraFacade;
import dao.MotoristaFacade;
import dao.RotaFacade;
import dao.RotaLixeiraFacade;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.RichTextString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Named("rotaController")
@SessionScoped
public class RotaController extends GenericController implements Serializable {

    private Rota current;
    private String totalKm;
    private String totalTempo;
    private String ordemColeta;
    private DataModel items = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private Caminhao caminhao;
    private Motorista motorista;
    private HistoricoColeta historicoColeta;
    List<HistoricoColeta> historicos;
    List<Lixeira> lixeiras;
    private Date dataInicial;
    private Date dataFinal;

    @EJB
    private dao.LixeiraFacade lixeiraFacade;
    @EJB
    private dao.RotaFacade rotaFacade;
    @EJB
    private dao.CaminhaoFacade caminhaoFacade;
    @EJB
    private dao.MotoristaFacade motoristaFacade;
    @EJB
    private dao.CaminhaoMotoristaFacade caminhaoMotoristaFacade;
    @EJB
    private dao.RotaLixeiraFacade rotaLixeiraFacade;
    @EJB
    private dao.HistoricoColetaFacade historicoColetaFacade;

    public RotaController() {
    }

    public Rota getSelected() {
        if (current == null) {
            current = new Rota();
            selectedItemIndex = -1;
        }
        return current;
    }

    private LixeiraFacade getFacadeLixeira() {
        return lixeiraFacade;
    }

    private RotaFacade getFacadeRota() {
        return rotaFacade;
    }

    private MotoristaFacade getFacadeMotorista() {
        return motoristaFacade;
    }

    private CaminhaoFacade getFacadeCaminhao() {
        return caminhaoFacade;
    }

    public CaminhaoMotoristaFacade getFacadeCaminhaoMotorista() {
        return caminhaoMotoristaFacade;
    }

    public RotaLixeiraFacade getFacadeRotaLixeira() {
        return rotaLixeiraFacade;
    }

    public HistoricoColetaFacade getFacadeHistoricoColeta() {
        return historicoColetaFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacadeRota().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacadeRota().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Rota) getItems().getRowData();
        setCaminhao(buscarCaminhao(current.getIdCaminhaoMotorista()));
        setMotorista(buscarMotorista(current.getIdCaminhaoMotorista()));
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Rota();
        selectedItemIndex = -1;
        return "Create";
    }

    public String createR() {
        try {
            JSONArray jsonArray;
            JSONParser parser = new JSONParser();
            jsonArray = (JSONArray) parser.parse(new FileReader(
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("")
                    + "\\rota\\js\\lixeirasRotaR.json"));
            return createGeneric(jsonArray);

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String create() {
        try {
            JSONArray jsonArray;
            JSONParser parser = new JSONParser();
            jsonArray = (JSONArray) parser.parse(new FileReader(
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("")
                    + "\\rota\\js\\lixeirasRota.json"));
            return createGeneric(jsonArray);

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    private String createGeneric(JSONArray jsonArray) throws Exception {
        totalKm = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get("totalKm");
        totalTempo = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get("totalTempo");
        ordemColeta = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get("ordemColeta");

        String[] ordem = ordemColeta.split(",");
        String ordemLixeiras = "";

        if (ordem == null || ordem.length < 1 || ordem[0].equals(""))
            throw new Exception("Não existe lixeiras para coleta.");
        
        for (int i = 0; i < ordem.length; i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(Integer.valueOf(ordem[i]));
            ordemLixeiras += jsonObject.get("Id");
            if (i < (ordem.length - 1)) {
                ordemLixeiras += ",";
            }
        }

        CaminhaoMotorista caminhaoMotorista = new CaminhaoMotorista();
        caminhaoMotorista.setIdCaminhao(caminhao.getIdCaminhao());
        caminhaoMotorista.setIdMotorista(motorista.getIdMotorista());
        getFacadeCaminhaoMotorista().create(caminhaoMotorista);

        current = new Rota();
        current.setIdCaminhaoMotorista(caminhaoMotorista.getIdCaminhaoMotorista());
        current.setTotalKm(new BigDecimal(totalKm));
        current.setTotalTempo(new BigDecimal(totalTempo));
        current.setOrdemColeta(ordemLixeiras);
        current.setDataHora(new Date());
        getFacadeRota().create(current);

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            Lixeira l = getFacadeLixeira().find(((Long) jsonObject.get("Id")).intValue());

            RotaLixeira rl = new RotaLixeira();
            rl.setIdLixeira(l.getIdLixeira());
            rl.setIdRota(current.getIdRota());
            rl.setDataHora(new Date());
            getFacadeRotaLixeira().create(rl);
        }

        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/conf").getString("RotaCreated"));
        return prepareCreate();
    }

    public String prepareEdit() {
        current = (Rota) getItems().getRowData();
        setCaminhao(buscarCaminhao(current.getIdCaminhaoMotorista()));
        setMotorista(buscarMotorista(current.getIdCaminhaoMotorista()));
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            CaminhaoMotorista cm = getFacadeCaminhaoMotorista().find(current.getIdCaminhaoMotorista());
            cm.setIdCaminhao(caminhao.getIdCaminhao());
            cm.setIdMotorista(motorista.getIdMotorista());
            cm.setDataHora(new Date());
            getFacadeCaminhaoMotorista().edit(cm);

            getFacadeRota().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/conf").getString("RotaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Rota) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    public String prepareColeta() {
        current = (Rota) getItems().getRowData();
        setCaminhao(buscarCaminhao(current.getIdCaminhaoMotorista()));
        setMotorista(buscarMotorista(current.getIdCaminhaoMotorista()));
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

        lixeiras = buscarLixeirasColeta(current.getOrdemColeta());
        historicos = new ArrayList<>();

        return "Coletar";
    }

    private List<Lixeira> buscarLixeirasColeta(String ordem) {
        String[] ordemL = ordem.split(",");
        List<Lixeira> result = new ArrayList<>();

        for (int i = 0; i < ordemL.length; i++) {
            Lixeira lixeira = getFacadeLixeira().find(Integer.valueOf(ordemL[i]));
            result.add(lixeira);
        }

        return result;
    }

    public void coletar(Integer idLixeira) {
        historicoColeta = new HistoricoColeta();
        historicoColeta.setIdCaminhaoMotorista(current.getIdCaminhaoMotorista());
        historicoColeta.setIdRota(current.getIdRota());

        for (Lixeira lixeira : lixeiras) {
            if (lixeira.getIdLixeira() == idLixeira) {
                historicoColeta.setIdLixeira(lixeira.getIdLixeira());
                historicoColeta.setCheioVolume("N");
                historicoColeta.setColetadoLixeiraKg(lixeira.getColetadoLixeiraKg());
                historicoColeta.setDataHora(new Date());
                historicos.add(historicoColeta);

                lixeira.setColetadoLixeiraKg(BigDecimal.ZERO);
            }
        }
    }

    public String finalizarColeta() {
        try {
            for (Lixeira l : lixeiras) {
                getFacadeLixeira().edit(l);
            }

            for (HistoricoColeta h : historicos) {
                getFacadeHistoricoColeta().create(h);
            }

            getFacadeRota().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/conf").getString("RotaColetada"));
            return "Coletar";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    private void performDestroy() {
        try {
            getFacadeRota().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/conf").getString("RotaDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacadeRota().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacadeRota().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(rotaFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(rotaFacade.findAll(), true);
    }

    public Rota getRota(java.lang.Integer id) {
        return rotaFacade.find(id);
    }

    @FacesConverter(forClass = Rota.class)
    public static class RotaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RotaController controller = (RotaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rotaController");
            return controller.getRota(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Rota) {
                Rota o = (Rota) object;
                return getStringKey(o.getIdRota());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Rota.class.getName());
            }
        }

    }

    public void definirLixeirasRotasOtimizada() {
        try {
            List<Lixeira> lixeiras = getFacadeLixeira().findAll();
            carregarLixeiras(lixeiras);
            int cor;
            String fileRota = FacesContext.getCurrentInstance().getExternalContext().getRealPath("")
                    + "\\rota\\js\\lixeirasRota.json";
            FileOutputStream fosRota = new FileOutputStream(fileRota);
            JsonGenerator geradorJsonRota = Json.createGenerator(fosRota);

            geradorJsonRota.writeStartArray();
            int cont = 0;
            for (Lixeira lixeira : lixeiras) {
                cor = getStatus(lixeira);

                if (cor == 1 && cont < 8) {
                    geradorJsonRota.writeStartObject()
                            .write("Id", lixeira.getIdLixeira())
                            .write("Latitude", lixeira.getLatitude())
                            .write("Longitude", lixeira.getLongitude())
                            .writeEnd();
                    cont++;
                }
            }

            geradorJsonRota.writeEnd().close();

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
        }
    }

    public void definirLixeirasRotasNormal() {
        try {
            List<Lixeira> lixeiras = getFacadeLixeira().findAll();
            carregarLixeiras(lixeiras);
            String fileRota = FacesContext.getCurrentInstance().getExternalContext().getRealPath("")
                    + "\\rota\\js\\lixeirasRotaR.json";
            FileOutputStream fosRota = new FileOutputStream(fileRota);
            JsonGenerator geradorJsonRota = Json.createGenerator(fosRota);

            geradorJsonRota.writeStartArray();
            int cont = 0;
            for (Lixeira lixeira : lixeiras) {

                if (cont < 8) {
                    geradorJsonRota.writeStartObject()
                            .write("Id", lixeira.getIdLixeira())
                            .write("Latitude", lixeira.getLatitude())
                            .write("Longitude", lixeira.getLongitude())
                            .writeEnd();
                    cont++;
                }
            }

            geradorJsonRota.writeEnd().close();

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
        }
    }

    public void carregarLixeiras(List<Lixeira> lixeiras) {
        try {
            int cor;
            String file = FacesContext.getCurrentInstance().getExternalContext().getRealPath("")
                    + "\\rota\\js\\pontos.json";
            FileOutputStream fos = new FileOutputStream(file);
            JsonGenerator geradorJson = Json.createGenerator(fos);

            geradorJson.writeStartArray();

            for (Lixeira lixeira : lixeiras) {
                cor = getStatus(lixeira);

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

    public void emitirExcel() {
        try {
            validaDataLimite(dataInicial, dataFinal);

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet("Rotas");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            List<Rota> lista = getFacadeRota().buscaRotasExcel(this.dataInicial, this.dataFinal);

            if (lista == null || lista.isEmpty()) {
                throw new Exception("Não existe rotas para esse período");
            }

            try {
                HSSFFont font = workbook.createFont();
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

                int i = 0;
                HSSFRow row = firstSheet.createRow(i);

                RichTextString text = new HSSFRichTextString("Identificador");
                text.applyFont(font);
                row.createCell(0).setCellValue(text);
                text = new HSSFRichTextString("Caminhão");
                text.applyFont(font);
                row.createCell(1).setCellValue(text);
                text = new HSSFRichTextString("Motorista");
                text.applyFont(font);
                row.createCell(2).setCellValue(text);
                text = new HSSFRichTextString("Ordem da Coleta");
                text.applyFont(font);
                row.createCell(3).setCellValue(text);
                text = new HSSFRichTextString("Km Total");
                text.applyFont(font);
                row.createCell(4).setCellValue(text);
                text = new HSSFRichTextString("Tempo Total (min)");
                text.applyFont(font);
                row.createCell(5).setCellValue(text);
                text = new HSSFRichTextString("Data");
                text.applyFont(font);
                row.createCell(6).setCellValue(text);

                i++;
                for (Rota rota : lista) {
                    row = firstSheet.createRow(i);

                    CaminhaoMotorista cm = getFacadeCaminhaoMotorista().find(rota.getIdCaminhaoMotorista());

                    row.createCell(0).setCellValue(rota.getIdRota());
                    row.createCell(1).setCellValue(cm.getIdCaminhao());
                    row.createCell(2).setCellValue(getFacadeMotorista().find(cm.getIdMotorista()).getNomeMotorista());
                    row.createCell(3).setCellValue(rota.getOrdemColeta());
                    row.createCell(4).setCellValue(rota.getTotalKm().toString());
                    row.createCell(5).setCellValue(rota.getTotalTempo().toString());
                    row.createCell(6).setCellValue(df.format(rota.getDataHora()));

                    i++;
                }

                firstSheet.autoSizeColumn(0);
                firstSheet.autoSizeColumn(1);
                firstSheet.autoSizeColumn(2);
                firstSheet.autoSizeColumn(3);
                firstSheet.autoSizeColumn(4);
                firstSheet.autoSizeColumn(5);
                firstSheet.autoSizeColumn(6);

                workbook.write(baos);

            } catch (Exception e) {
                throw new Exception("Erro ao exportar arquivo: " + e.getMessage());
            } finally {
                baos.close();

                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                response.setContentType("application/xls");
                response.setHeader("Content-disposition", "attachment;filename=" + "RelatorioRotas.xls");
                response.getOutputStream().write(baos.toByteArray());
                FacesContext.getCurrentInstance().responseComplete();
            }

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
        }
    }

    public List<Caminhao> getListCaminhao() {
        return getFacadeCaminhao().findAll();
    }

    public List<Motorista> getListMotorista() {
        return getFacadeMotorista().findAll();
    }

    public Caminhao buscarCaminhao(Integer idCaminhaoMotorista) {
        if (idCaminhaoMotorista == null) {
            return null;
        }
        CaminhaoMotorista cm = getFacadeCaminhaoMotorista().find(idCaminhaoMotorista);
        return getFacadeCaminhao().find(cm.getIdCaminhao());
    }

    public Motorista buscarMotorista(Integer idCaminhaoMotorista) {
        if (idCaminhaoMotorista == null) {
            return null;
        }
        CaminhaoMotorista cm = getFacadeCaminhaoMotorista().find(idCaminhaoMotorista);
        return getFacadeMotorista().find(cm.getIdMotorista());
    }

    public boolean isColetar(BigDecimal coletado) {
        return coletado.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public String getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(String totalKm) {
        this.totalKm = totalKm;
    }

    public String getTotalTempo() {
        return totalTempo;
    }

    public void setTotalTempo(String totalTempo) {
        this.totalTempo = totalTempo;
    }

    public String getOrdemColeta() {
        return ordemColeta;
    }

    public void setOrdemColeta(String ordemColeta) {
        this.ordemColeta = ordemColeta;
    }

    public Caminhao getCaminhao() {
        return caminhao;
    }

    public void setCaminhao(Caminhao caminhao) {
        this.caminhao = caminhao;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

    public DateFormat getDf() {
        return df;
    }

    public List<Lixeira> getLixeiras() {
        return lixeiras;
    }

    public void setLixeiras(List<Lixeira> lixeiras) {
        this.lixeiras = lixeiras;
    }

    public HistoricoColeta getHistoricoColeta() {
        return historicoColeta;
    }

    public void setHistoricoColeta(HistoricoColeta historicoColeta) {
        this.historicoColeta = historicoColeta;
    }

    public List<HistoricoColeta> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<HistoricoColeta> historicos) {
        this.historicos = historicos;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

}
