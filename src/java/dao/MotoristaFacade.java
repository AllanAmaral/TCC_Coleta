package dao;

import business.objects.Motorista;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Allan.Amaral
 */
@Stateless
public class MotoristaFacade extends AbstractFacade<Motorista> {

    @PersistenceContext(unitName = "TCC_ColetaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MotoristaFacade() {
        super(Motorista.class);
    }

}
