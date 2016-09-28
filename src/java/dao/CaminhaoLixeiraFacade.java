package dao;

import business.objects.CaminhaoLixeira;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Allan.Amaral
 */
@Stateless
public class CaminhaoLixeiraFacade extends AbstractFacade<CaminhaoLixeira> {

    @PersistenceContext(unitName = "TCC_ColetaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CaminhaoLixeiraFacade() {
        super(CaminhaoLixeira.class);
    }
    
}
