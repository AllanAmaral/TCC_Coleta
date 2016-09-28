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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Allan.Amaral
 */
@Entity
@Table(name = "caminhao")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Caminhao.findAll", query = "SELECT c FROM Caminhao c"),
    @NamedQuery(name = "Caminhao.findByIdCaminhao", query = "SELECT c FROM Caminhao c WHERE c.idCaminhao = :idCaminhao"),
    @NamedQuery(name = "Caminhao.findByCapacidadeCaminhaoKg", query = "SELECT c FROM Caminhao c WHERE c.capacidadeCaminhaoKg = :capacidadeCaminhaoKg"),
    @NamedQuery(name = "Caminhao.findByColetadoCaminhaoKg", query = "SELECT c FROM Caminhao c WHERE c.coletadoCaminhaoKg = :coletadoCaminhaoKg")})
public class Caminhao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 7)
    @Column(name = "ID_CAMINHAO")
    private String idCaminhao;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "CAPACIDADE_CAMINHAO_KG")
    private BigDecimal capacidadeCaminhaoKg;
    @Column(name = "COLETADO_CAMINHAO_KG")
    private BigDecimal coletadoCaminhaoKg;

    public Caminhao() {
    }

    public Caminhao(String idCaminhao) {
        this.idCaminhao = idCaminhao;
    }

    public String getIdCaminhao() {
        return idCaminhao;
    }

    public void setIdCaminhao(String idCaminhao) {
        this.idCaminhao = idCaminhao;
    }

    public BigDecimal getCapacidadeCaminhaoKg() {
        return capacidadeCaminhaoKg;
    }

    public void setCapacidadeCaminhaoKg(BigDecimal capacidadeCaminhaoKg) {
        this.capacidadeCaminhaoKg = capacidadeCaminhaoKg;
    }

    public BigDecimal getColetadoCaminhaoKg() {
        return coletadoCaminhaoKg;
    }

    public void setColetadoCaminhaoKg(BigDecimal coletadoCaminhaoKg) {
        this.coletadoCaminhaoKg = coletadoCaminhaoKg;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCaminhao != null ? idCaminhao.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Caminhao)) {
            return false;
        }
        Caminhao other = (Caminhao) object;
        if ((this.idCaminhao == null && other.idCaminhao != null) || (this.idCaminhao != null && !this.idCaminhao.equals(other.idCaminhao))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "business.objects.Caminhao[ idCaminhao=" + idCaminhao + " ]";
    }
    
}
