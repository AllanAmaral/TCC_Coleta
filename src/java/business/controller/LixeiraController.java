package business.controller;

import business.objects.Lixeira;
import business.util.JsfUtil;
import business.util.PaginationHelper;
import dao.LixeiraFacade;
import java.io.FileOutputStream;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.json.Json;
import javax.json.stream.JsonGenerator;

@Named("lixeiraController")
@SessionScoped
public class LixeiraController implements Serializable {

    private Lixeira current;
    private DataModel items = null;
    @EJB
    private dao.LixeiraFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

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
    
    public void carregarPontosLixeiras() {
        try {
            List<Lixeira> lixeiras = getFacade().findAll();
            FileOutputStream fos = new FileOutputStream("C:/Users/allan.amaral/Documents/GitHub/TCC_Coleta/web/js/pontos.json");
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
                                            + "\n" + "Capacidade Lt: " + lixeira.getCapacidadeLixeiraLt()
                                            + "\n" + "Coletado Kg: " + lixeira.getColetadoLixeiraKg()
                                            + "\n" + "Coletado Lt: " + lixeira.getColetadoLixeiraLt())
                        .writeEnd();
            }

            geradorJson.writeEnd().close();
            
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
        }
    }

}
