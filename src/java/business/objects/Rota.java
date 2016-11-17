package business.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    @NamedQuery(name = "Rota.findByIdCaminhaoMotorista", query = "SELECT r FROM Rota r WHERE r.idCaminhaoMotorista = :idCaminhaoMotorista"),
    @NamedQuery(name = "Rota.findByDataHora", query = "SELECT r FROM Rota r WHERE r.dataHora = :dataHora")})
public class Rota implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_ROTA")
    private Integer idRota;
    @Column(name = "ID_CAMINHAO_MOTORISTA")
    private Integer idCaminhaoMotorista;
    @Column(name = "TOTAL_KM")
    private BigDecimal totalKm;
    @Column(name = "TOTAL_TEMPO")
    private BigDecimal totalTempo;
    @Column(name = "ORDEM_COLETA")
    private String ordemColeta;
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

    public BigDecimal getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(BigDecimal totalKm) {
        this.totalKm = totalKm;
    }

    public BigDecimal getTotalTempo() {
        return totalTempo;
    }

    public void setTotalTempo(BigDecimal totalTempo) {
        this.totalTempo = totalTempo;
    }

    public String getOrdemColeta() {
        return ordemColeta;
    }

    public void setOrdemColeta(String ordemColeta) {
        this.ordemColeta = ordemColeta;
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
