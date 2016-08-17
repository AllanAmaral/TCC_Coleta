/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import business.objects.Caminhao;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Allan.Amaral
 */
@Stateless
public class CaminhaoFacade extends AbstractFacade<Caminhao> {

    @PersistenceContext(unitName = "TCC_ColetaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CaminhaoFacade() {
        super(Caminhao.class);
    }
    
}
