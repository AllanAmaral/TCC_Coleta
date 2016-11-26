package business.controller;

import business.objects.Caminhao;
import business.objects.CaminhaoMotorista;
import business.objects.HistoricoColeta;
import business.objects.Motorista;
import business.util.JsfUtil;
import business.util.PaginationHelper;
import dao.CaminhaoFacade;
import dao.CaminhaoMotoristaFacade;
import dao.HistoricoColetaFacade;
import dao.MotoristaFacade;
import java.io.ByteArrayOutputStream;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
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
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.RichTextString;

@Named("historicoColetaController")
@SessionScoped
public class HistoricoColetaController extends GenericController implements Serializable {

    private HistoricoColeta current;
    private DataModel items = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    @EJB
    private dao.HistoricoColetaFacade ejbFacade;
    @EJB
    private dao.MotoristaFacade motoristaFacade;
    @EJB
    private dao.CaminhaoFacade caminhaoFacade;
    @EJB
    private dao.CaminhaoMotoristaFacade caminhaoMotoristaFacade;
    private Caminhao caminhao;
    private Motorista motorista;

    private DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private Date dataInicial;
    private Date dataFinal;

    public HistoricoColetaController() {
    }

    public HistoricoColeta getSelected() {
        if (current == null) {
            current = new HistoricoColeta();
            selectedItemIndex = -1;
        }
        return current;
    }

    private HistoricoColetaFacade getFacade() {
        return ejbFacade;
    }

    private CaminhaoFacade getFacadeCaminhao() {
        return caminhaoFacade;
    }

    private MotoristaFacade getFacadeMotorista() {
        return motoristaFacade;
    }

    public CaminhaoMotoristaFacade getFacadeCaminhaoMotorista() {
        return caminhaoMotoristaFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
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
        current = (HistoricoColeta) getItems().getRowData();
        setCaminhao(buscarCaminhao(current.getIdCaminhaoMotorista()));
        setMotorista(buscarMotorista(current.getIdCaminhaoMotorista()));
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new HistoricoColeta();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/conf").getString("HistoricoColetaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (HistoricoColeta) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/conf").getString("HistoricoColetaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (HistoricoColeta) getItems().getRowData();
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

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/conf").getString("HistoricoColetaDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
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
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public HistoricoColeta getHistoricoColeta(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = HistoricoColeta.class)
    public static class HistoricoColetaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            HistoricoColetaController controller = (HistoricoColetaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "historicoColetaController");
            return controller.getHistoricoColeta(getKey(value));
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
            if (object instanceof HistoricoColeta) {
                HistoricoColeta o = (HistoricoColeta) object;
                return getStringKey(o.getIdHistoricoColeta());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + HistoricoColeta.class.getName());
            }
        }

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

    public void emitirExcel() {
        try {
            validaDataLimite(dataInicial, dataFinal);

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet("HistoricosColeta");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            List<HistoricoColeta> lista = getFacade().buscaHistoricoExcel(this.dataInicial, this.dataFinal);

            if (lista == null || lista.isEmpty()) {
                throw new Exception("Não existe históricos para esse período");
            }

            try {
                HSSFFont font = workbook.createFont();
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

                int i = 0;
                HSSFRow row = firstSheet.createRow(i);

                RichTextString text = new HSSFRichTextString("Identificador");
                text.applyFont(font);
                row.createCell(0).setCellValue(text);
                text = new HSSFRichTextString("Lixeira");
                text.applyFont(font);
                row.createCell(1).setCellValue(text);
                text = new HSSFRichTextString("Caminhão");
                text.applyFont(font);
                row.createCell(2).setCellValue(text);
                text = new HSSFRichTextString("Motorista");
                text.applyFont(font);
                row.createCell(3).setCellValue(text);
                text = new HSSFRichTextString("Rota");
                text.applyFont(font);
                row.createCell(4).setCellValue(text);
                text = new HSSFRichTextString("Coletado");
                text.applyFont(font);
                row.createCell(5).setCellValue(text);
                text = new HSSFRichTextString("Data");
                text.applyFont(font);
                row.createCell(6).setCellValue(text);

                i++;
                for (HistoricoColeta hc : lista) {
                    row = firstSheet.createRow(i);

                    CaminhaoMotorista cm = getFacadeCaminhaoMotorista().find(hc.getIdCaminhaoMotorista());

                    row.createCell(0).setCellValue(hc.getIdHistoricoColeta());
                    row.createCell(1).setCellValue(hc.getIdLixeira());
                    row.createCell(2).setCellValue(cm.getIdCaminhao());
                    row.createCell(3).setCellValue(getFacadeMotorista().find(cm.getIdMotorista()).getNomeMotorista());
                    row.createCell(4).setCellValue(hc.getIdRota());
                    row.createCell(5).setCellValue(hc.getColetadoLixeiraKg().toString());
                    row.createCell(6).setCellValue(df.format(hc.getDataHora()));

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
                response.setHeader("Content-disposition", "attachment;filename=" + "RelatorioHistoricoColeta.xls");
                response.getOutputStream().write(baos.toByteArray());
                FacesContext.getCurrentInstance().responseComplete();
            }

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
        }
    }

    public DateFormat getDf() {
        return df;
    }

    public void setDf(DateFormat df) {
        this.df = df;
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

}
