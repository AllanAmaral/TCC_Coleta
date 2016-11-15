package business.controller;

import business.objects.Lixeira;
import business.objects.Rota;
import business.util.JsfUtil;
import business.util.PaginationHelper;
import dao.LixeiraFacade;
import dao.RotaFacade;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;

@Named("rotaController")
@SessionScoped
public class RotaController extends GenericController implements Serializable {

    private Rota current;
    private String totalKm;
    private String totalTempo;
    private DataModel items = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    @EJB
    private dao.LixeiraFacade lixeiraFacade;
    @EJB
    private dao.RotaFacade rotaFacade;

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
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Rota();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            totalKm = FacesContext.getCurrentInstance().
		getExternalContext().getRequestParameterMap().get("totalKm");
            totalTempo = FacesContext.getCurrentInstance().
		getExternalContext().getRequestParameterMap().get("totalTempo");
            
            getFacadeRota().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/conf").getString("RotaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/conf").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Rota) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
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
                    + "\\rota\\js\\lixeirasRota.json";
            FileOutputStream fosRota = new FileOutputStream(fileRota);
            JsonGenerator geradorJsonRota = Json.createGenerator(fosRota);

            geradorJsonRota.writeStartArray();
            int cont = 0;
            for (Lixeira lixeira : lixeiras) {

                if (cont < 8) {
                    geradorJsonRota.writeStartObject()
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

    public String getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(String totalKm) {
        this.totalKm = totalKm;
    }

    public void actionTotalKm(String value) {
        this.totalKm = value;
    }
    
    public String getTotalTempo() {
        return totalTempo;
    }

    public void setTotalTempo(String totalTempo) {
        this.totalTempo = totalTempo;
    }
    
}
