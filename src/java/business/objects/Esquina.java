package business.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Allan.Amaral
 */
@Entity
@Table(name = "esquina")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Esquina.findAll", query = "SELECT l FROM Esquina l"),
    @NamedQuery(name = "Esquina.findByIdEsquina", query = "SELECT l FROM Esquina l WHERE l.idEsquina = :idEsquina"),
    @NamedQuery(name = "Esquina.findByLatitude", query = "SELECT l FROM Esquina l WHERE l.latitude = :latitude"),
    @NamedQuery(name = "Esquina.findByLongitude", query = "SELECT l FROM Esquina l WHERE l.longitude = :longitude")})
public class Esquina implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_ESQUINA")
    private Integer idEsquina;
    @Column(name = "LATITUDE")
    private BigDecimal latitude;
    @Column(name = "LONGITUDE")
    private BigDecimal longitude;
    
    public Esquina() {
    }

    public Esquina(Integer idEsquina) {
        this.idEsquina = idEsquina;
    }

    public Integer getIdEsquina() {
        return idEsquina;
    }

    public void setIdEsquina(Integer idEsquina) {
        this.idEsquina = idEsquina;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEsquina != null ? idEsquina.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Esquina)) {
            return false;
        }
        Esquina other = (Esquina) object;
        if ((this.idEsquina == null && other.idEsquina != null) || (this.idEsquina != null && !this.idEsquina.equals(other.idEsquina))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "business.objects.Esquina[ idEsquina=" + idEsquina + " ]";
    }
    
}
