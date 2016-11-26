package dao;

import business.objects.HistoricoColeta;
import business.objects.HistoricoColeta_;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Allan.Amaral
 */
@Stateless
public class HistoricoColetaFacade extends AbstractFacade<HistoricoColeta> {

    @PersistenceContext(unitName = "TCC_ColetaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HistoricoColetaFacade() {
        super(HistoricoColeta.class);
    }
    
    public List<HistoricoColeta> buscaHistoricoExcel(Date dataInicial, Date dataFinal) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<HistoricoColeta> hc = cq.from(HistoricoColeta.class);
        cq.select(hc);

        if (dataInicial != null && dataFinal != null) {
            cq.where(getEntityManager().getCriteriaBuilder().between(hc.get(HistoricoColeta_.dataHora), dataInicial, dataFinal));
        }

        return getEntityManager().createQuery(cq).getResultList();
    }
}
