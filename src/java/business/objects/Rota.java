package business.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Allan.Amaral
 */
@Entity
@Table(name = "rota")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rota.findAll", query = "SELECT r FROM Rota r"),
    @NamedQuery(name = "Rota.findByIdRota", query = "SELECT r FROM Rota r WHERE r.idRota = :idRota"),
    @NamedQuery(name = "Rota.findByLatitude", query = "SELECT l FROM Esquina l WHERE l.latitude = :latitude"),
    @NamedQuery(name = "Rota.findByLongitude", query = "SELECT l FROM Esquina l WHERE l.longitude = :longitude")})
public class Rota implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_ROTA")
    private Integer idRota;
    @Column(name = "ID_CAMINHAO_MOTORISTA")
    private Integer idCaminhaoMotorista;
    @Column(name = "DESCRICAO")
    private String descricao;
    @Column(name = "DATA_HORA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHora;
    
    public Rota() {
    }

    public Rota(Integer idRota) {
        this.idRota = idRota;
    }

    public Integer getIdRota() {
        return idRota;
    }

    public void setIdRota(Integer idRota) {
        this.idRota = idRota;
    }

    public Integer getIdCaminhaoMotorista() {
        return idCaminhaoMotorista;
    }

    public void setIdCaminhaoMotorista(Integer idCaminhaoMotorista) {
        this.idCaminhaoMotorista = idCaminhaoMotorista;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRota != null ? idRota.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rota)) {
            return false;
        }
        Rota other = (Rota) object;
        if ((this.idRota == null && other.idRota != null) || (this.idRota != null && !this.idRota.equals(other.idRota))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "business.objects.Rota[ idRota=" + idRota + " ]";
    }
    
}