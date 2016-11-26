package business.dto;

import java.math.BigDecimal;

/**
 *
 * @author Allan.Amaral
 */
public class LixeiraColetadaDTO {

    private Integer idLixeira;

    private BigDecimal capacidadeLixeiraKg;

    private BigDecimal coletadoPeriodo;

    private BigDecimal latitude;

    private BigDecimal longitude;

    public LixeiraColetadaDTO() {
    }

    public Integer getIdLixeira() {
        return idLixeira;
    }

    public void setIdLixeira(Integer idLixeira) {
        this.idLixeira = idLixeira;
    }

    public BigDecimal getCapacidadeLixeiraKg() {
        return capacidadeLixeiraKg;
    }

    public void setCapacidadeLixeiraKg(BigDecimal capacidadeLixeiraKg) {
        this.capacidadeLixeiraKg = capacidadeLixeiraKg;
    }

    public BigDecimal getColetadoPeriodo() {
        return coletadoPeriodo;
    }

    public void setColetadoPeriodo(BigDecimal coletadoPeriodo) {
        this.coletadoPeriodo = coletadoPeriodo;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

}
