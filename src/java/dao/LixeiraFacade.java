package dao;

import business.objects.Lixeira;
import business.objects.Lixeira_;
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
}
