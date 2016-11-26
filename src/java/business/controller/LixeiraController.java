package business.controller;

import business.dto.LixeiraColetadaDTO;
import business.objects.Lixeira;
import business.util.JsfUtil;
import business.util.PaginationHelper;
import dao.LixeiraFacade;
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

@Named("lixeiraController")
@SessionScoped
public class LixeiraController extends GenericController implements Serializable {

    private Lixeira current;
    private DataModel items = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private Date dataInicial;
    private Date dataFinal;

    @EJB
    private dao.LixeiraFacade ejbFacade;

    public LixeiraController() {
    }

    public Lixeira getSelected() {
        if (current == null) {
            current = new Lixeira();
            selectedItemIndex = -1;
        }
        return current;
    }

    private LixeiraFacade getFacade() {
        return ejbFacade;
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
        current = (Lixeira) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Lixeira();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/conf").getString("LixeiraCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Lixeira) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/conf").getString("LixeiraUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Lixeira) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/conf").getString("LixeiraDeleted"));
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

    public Lixeira getLixeira(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Lixeira.class)
    public static class LixeiraControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LixeiraController controller = (LixeiraController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "lixeiraController");
            return controller.getLixeira(getKey(value));
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
            if (object instanceof Lixeira) {
                Lixeira o = (Lixeira) object;
                return getStringKey(o.getIdLixeira());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Lixeira.class.getName());
            }
        }

    }

    public void emitirExcel() {
        try {
            validaDataLimite(dataInicial, dataFinal);

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet firstSheet = workbook.createSheet("Lixeiras");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            List<LixeiraColetadaDTO> lista = getFacade().buscaLixeiraExcel(this.dataInicial, this.dataFinal);

            if (lista == null || lista.isEmpty()) {
                throw new Exception("Não existe lixeiras coletadas para esse período");
            }

            try {
                HSSFFont font = workbook.createFont();
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

                int i = 0;
                HSSFRow row = firstSheet.createRow(i);

                RichTextString text = new HSSFRichTextString("Identificador");
                text.applyFont(font);
                row.createCell(0).setCellValue(text);
                text = new HSSFRichTextString("Capacidade");
                text.applyFont(font);
                row.createCell(1).setCellValue(text);
                text = new HSSFRichTextString("Latitude");
                text.applyFont(font);
                row.createCell(2).setCellValue(text);
                text = new HSSFRichTextString("Longitude");
                text.applyFont(font);
                row.createCell(3).setCellValue(text);
                text = new HSSFRichTextString("Total Coletado no Período");
                text.applyFont(font);
                row.createCell(4).setCellValue(text);

                i++;
                for (LixeiraColetadaDTO lc : lista) {
                    row = firstSheet.createRow(i);

                    row.createCell(0).setCellValue(lc.getIdLixeira());
                    row.createCell(1).setCellValue(lc.getCapacidadeLixeiraKg().toString());
                    row.createCell(2).setCellValue(lc.getLatitude().toString());
                    row.createCell(3).setCellValue(lc.getLongitude().toString());
                    row.createCell(4).setCellValue(lc.getColetadoPeriodo().toString());

                    i++;
                }

                firstSheet.autoSizeColumn(0);
                firstSheet.autoSizeColumn(1);
                firstSheet.autoSizeColumn(2);
                firstSheet.autoSizeColumn(3);
                firstSheet.autoSizeColumn(4);

                workbook.write(baos);

            } catch (Exception e) {
                throw new Exception("Erro ao exportar arquivo: " + e.getMessage());
            } finally {
                baos.close();

                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                response.setContentType("application/xls");
                response.setHeader("Content-disposition", "attachment;filename=" + "RelatorioLixeirasColetas.xls");
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

}
