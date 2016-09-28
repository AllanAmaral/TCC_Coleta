package dao;

import business.objects.HistoricoColeta;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
}
