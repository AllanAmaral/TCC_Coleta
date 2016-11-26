package dao;

import business.dto.LixeiraColetadaDTO;
import business.objects.HistoricoColeta;
import business.objects.HistoricoColeta_;
import business.objects.Lixeira;
import business.objects.Lixeira_;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Allan.Amaral
 */
@Stateless
public class LixeiraFacade extends AbstractFacade<Lixeira> {

    @PersistenceContext(unitName = "TCC_ColetaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LixeiraFacade() {
        super(Lixeira.class);
    }
    
    public Integer maxId() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<Lixeira> rt = cq.from(Lixeira.class);
        cq.select(getEntityManager().getCriteriaBuilder().max(rt.get(Lixeira_.idLixeira)));
        Query q = getEntityManager().createQuery(cq);
        return (Integer) q.getSingleResult();
    }
    
    public Integer minId() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<Lixeira> rt = cq.from(Lixeira.class);
        cq.select(getEntityManager().getCriteriaBuilder().min(rt.get(Lixeira_.idLixeira)));
        Query q = getEntityManager().createQuery(cq);
        return (Integer) q.getSingleResult();
    }
    
    public List<LixeiraColetadaDTO> buscaLixeiraExcel(Date dataInicial, Date dataFinal) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<Lixeira> li = cq.from(Lixeira.class);
        cq.select(li);
        
        List<Lixeira> lixeiras = getEntityManager().createQuery(cq).getResultList();
        List<LixeiraColetadaDTO> result = new ArrayList<>();
        
        for (Lixeira lixeira : lixeiras) {
            CriteriaQuery cqH = getEntityManager().getCriteriaBuilder().createQuery();
            Root<HistoricoColeta> hc = cqH.from(HistoricoColeta.class);
            cqH.select(getEntityManager().getCriteriaBuilder().sum(hc.get(HistoricoColeta_.coletadoLixeiraKg)));
            
            if (dataInicial != null && dataFinal != null) {
                cqH.where(getEntityManager().getCriteriaBuilder().equal(hc.get(HistoricoColeta_.idLixeira), lixeira.getIdLixeira()),
                    getEntityManager().getCriteriaBuilder().between(hc.get(HistoricoColeta_.dataHora), dataInicial, dataFinal));
            
            } else {
                cqH.where(getEntityManager().getCriteriaBuilder().equal(hc.get(HistoricoColeta_.idLixeira), lixeira.getIdLixeira()));
            }
            
            Query q = getEntityManager().createQuery(cqH);
            BigDecimal coletado = (BigDecimal) q.getSingleResult();
            
            if (coletado != null && coletado.compareTo(BigDecimal.ZERO) > 0) {
                LixeiraColetadaDTO lc = new LixeiraColetadaDTO();
                lc.setIdLixeira(lixeira.getIdLixeira());
                lc.setCapacidadeLixeiraKg(lixeira.getCapacidadeLixeiraKg());
                lc.setLatitude(lixeira.getLatitude());
                lc.setLongitude(lixeira.getLongitude());
                lc.setColetadoPeriodo(coletado);
                
                result.add(lc);
            }   
        }

        return result;
    }
}
