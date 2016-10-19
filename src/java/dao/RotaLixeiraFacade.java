package dao;

import business.objects.RotaLixeira;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Allan.Amaral
 */
@Stateless
public class RotaLixeiraFacade extends AbstractFacade<RotaLixeira> {

    @PersistenceContext(unitName = "TCC_ColetaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RotaLixeiraFacade() {
        super(RotaLixeira.class);
    }

}
