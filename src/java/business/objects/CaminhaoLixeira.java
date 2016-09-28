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
@Table(name = "caminhao_lixeira")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CaminhaoLixeira.findAll", query = "SELECT c FROM CaminhaoLixeira c"),
    @NamedQuery(name = "CaminhaoLixeira.findByIdCaminhaoLixeira", query = "SELECT c FROM CaminhaoLixeira c WHERE c.idCaminhaoLixeira = :idCaminhaoLixeira"),
    @NamedQuery(name = "CaminhaoLixeira.findByIdCaminhao", query = "SELECT c FROM CaminhaoLixeira c WHERE c.idCaminhao = :idCaminhao"),
    @NamedQuery(name = "CaminhaoLixeira.findByIdLixeira", query = "SELECT c FROM CaminhaoLixeira c WHERE c.idLixeira = :idLixeira"),
    @NamedQuery(name = "CaminhaoLixeira.findByDataHora", query = "SELECT c FROM CaminhaoLixeira c WHERE c.dataHora = :dataHora")})
public class CaminhaoLixeira implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_CAMINHAO_LIXEIRA")
    private Integer idCaminhaoLixeira;
    @Size(max = 7)
    @Column(name = "ID_CAMINHAO")
    private String idCaminhao;
    @Column(name = "ID_LIXEIRA")
    private Integer idLixeira;
    @Column(name = "DATA_HORA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHora;

    public CaminhaoLixeira() {
    }

    public CaminhaoLixeira(Integer idCaminhaoLixeira) {
        this.idCaminhaoLixeira = idCaminhaoLixeira;
    }

    public Integer getIdCaminhaoLixeira() {
        return idCaminhaoLixeira;
    }

    public void setIdCaminhaoLixeira(Integer idCaminhaoLixeira) {
        this.idCaminhaoLixeira = idCaminhaoLixeira;
    }

    public String getIdCaminhao() {
        return idCaminhao;
    }

    public void setIdCaminhao(String idCaminhao) {
        this.idCaminhao = idCaminhao;
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
        hash += (idCaminhaoLixeira != null ? idCaminhaoLixeira.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CaminhaoLixeira)) {
            return false;
        }
        CaminhaoLixeira other = (CaminhaoLixeira) object;
        if ((this.idCaminhaoLixeira == null && other.idCaminhaoLixeira != null) || (this.idCaminhaoLixeira != null && !this.idCaminhaoLixeira.equals(other.idCaminhaoLixeira))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "business.objects.CaminhaoLixeira[ idCaminhaoLixeira=" + idCaminhaoLixeira + " ]";
    }
    
}
