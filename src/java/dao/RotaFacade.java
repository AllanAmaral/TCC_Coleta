package dao;

import business.objects.Rota;
import business.objects.Rota_;
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
public class RotaFacade extends AbstractFacade<Rota> {

    @PersistenceContext(unitName = "TCC_ColetaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RotaFacade() {
        super(Rota.class);
    }

    public List<Rota> buscaRotasExcel(Date dataInicial, Date dataFinal) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<Rota> rt = cq.from(Rota.class);
        cq.select(rt);

        if (dataInicial != null && dataFinal != null) {
            cq.where(getEntityManager().getCriteriaBuilder().between(rt.get(Rota_.dataHora), dataInicial, dataFinal));
        }

        return getEntityManager().createQuery(cq).getResultList();
    }
}
