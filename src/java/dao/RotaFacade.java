package dao;

import business.objects.Rota;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

}
