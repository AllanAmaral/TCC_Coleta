package business.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Allan.Amaral
 */
@Entity
@Table(name = "lixeira")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Lixeira.findAll", query = "SELECT l FROM Lixeira l"),
    @NamedQuery(name = "Lixeira.findByIdLixeira", query = "SELECT l FROM Lixeira l WHERE l.idLixeira = :idLixeira"),
    @NamedQuery(name = "Lixeira.findByCapacidadeLixeiraKg", query = "SELECT l FROM Lixeira l WHERE l.capacidadeLixeiraKg = :capacidadeLixeiraKg"),
    @NamedQuery(name = "Lixeira.findByColetadoLixeiraKg", query = "SELECT l FROM Lixeira l WHERE l.coletadoLixeiraKg = :coletadoLixeiraKg"),
    @NamedQuery(name = "Lixeira.findByLatitude", query = "SELECT l FROM Lixeira l WHERE l.latitude = :latitude"),
    @NamedQuery(name = "Lixeira.findByLongitude", query = "SELECT l FROM Lixeira l WHERE l.longitude = :longitude")})
public class Lixeira implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_LIXEIRA")
    private Integer idLixeira;
    
    @Column(name = "CAPACIDADE_LIXEIRA_KG")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal capacidadeLixeiraKg;
    
    @Column(name = "COLETADO_LIXEIRA_KG")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal coletadoLixeiraKg;
    
    @Column(name = "LATITUDE")
    @Digits(integer = 8, fraction = 10)
    private BigDecimal latitude;
    
    @Column(name = "LONGITUDE")
    @Digits(integer = 8, fraction = 10)
    private BigDecimal longitude;
    
    @Size(max = 1)
    @Column(name = "CHEIO_VOLUME")
    private String cheioVolume;

    public Lixeira() {
    }

    public Lixeira(Integer idLixeira) {
        this.idLixeira = idLixeira;
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

    public BigDecimal getColetadoLixeiraKg() {
        return coletadoLixeiraKg;
    }

    public void setColetadoLixeiraKg(BigDecimal coletadoLixeiraKg) {
        this.coletadoLixeiraKg = coletadoLixeiraKg;
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

    public String getCheioVolume() {
        return cheioVolume;
    }

    public void setCheioVolume(String cheioVolume) {
        this.cheioVolume = cheioVolume;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLixeira != null ? idLixeira.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lixeira)) {
            return false;
        }
        Lixeira other = (Lixeira) object;
        if ((this.idLixeira == null && other.idLixeira != null) || (this.idLixeira != null && !this.idLixeira.equals(other.idLixeira))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "business.objects.Lixeira[ idLixeira=" + idLixeira + " ]";
    }

}
