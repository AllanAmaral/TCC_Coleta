package dao;

import business.objects.CaminhaoMotorista;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Allan.Amaral
 */
@Stateless
public class CaminhaoMotoristaFacade extends AbstractFacade<CaminhaoMotorista> {

    @PersistenceContext(unitName = "TCC_ColetaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CaminhaoMotoristaFacade() {
        super(CaminhaoMotorista.class);
    }
    
}
