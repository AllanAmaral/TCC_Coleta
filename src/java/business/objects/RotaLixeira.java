package business.objects;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Allan.Amaral
 */
@Entity
@Table(name = "rota_lixeira")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RotaLixeira.findAll", query = "SELECT r FROM RotaLixeira r"),
    @NamedQuery(name = "RotaLixeira.findByIdRotaLixeira", query = "SELECT r FROM RotaLixeira r WHERE r.idRotaLixeira = :idRotaLixeira"),
    @NamedQuery(name = "RotaLixeira.findByIdRota", query = "SELECT r FROM RotaLixeira r WHERE r.idRota = :idRota"),
    @NamedQuery(name = "RotaLixeira.findByIdLixeira", query = "SELECT r FROM RotaLixeira r WHERE r.idLixeira = :idLixeira"),
    @NamedQuery(name = "RotaLixeira.findByDataHora", query = "SELECT r FROM RotaLixeira r WHERE r.dataHora = :dataHora")})
public class RotaLixeira implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_ROTA_LIXEIRA")
    private Integer idRotaLixeira;
    @Column(name = "ID_ROTA")
    private Integer idRota;
    @Column(name = "ID_LIXEIRA")
    private Integer idLixeira;
    @Column(name = "DATA_HORA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHora;

    public RotaLixeira() {
    }

    public RotaLixeira(Integer idRotaLixeira) {
        this.idRotaLixeira = idRotaLixeira;
    }

    public Integer getIdRotaLixeira() {
        return idRotaLixeira;
    }

    public void setIdRotaLixeira(Integer idRotaLixeira) {
        this.idRotaLixeira = idRotaLixeira;
    }

    public Integer getIdRota() {
        return idRota;
    }

    public void setIdRota(Integer idRota) {
        this.idRota = idRota;
    }

    public Integer getIdLixeira() {
        return idLixeira;
    }

    public void setIdLixeira(Integer idLixeira) {
        this.idLixeira = idLixeira;
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
        hash += (idRotaLixeira != null ? idRotaLixeira.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RotaLixeira)) {
            return false;
        }
        RotaLixeira other = (RotaLixeira) object;
        if ((this.idRotaLixeira == null && other.idRotaLixeira != null) || (this.idRotaLixeira != null && !this.idRotaLixeira.equals(other.idRotaLixeira))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "business.objects.RotaLixeira[ idRotaLixeira=" + idRotaLixeira + " ]";
    }
    
}
