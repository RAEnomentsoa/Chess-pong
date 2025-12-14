package net.server.EJB;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class GameConfigServiceBean implements GameConfigServiceRemote {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(GameConfigEntity config) {
        em.persist(config);
    }

    @Override
    public GameConfigEntity loadLast() {
        return em.createQuery(
                "SELECT g FROM GameConfigEntity g ORDER BY g.id DESC",
                GameConfigEntity.class).setMaxResults(1).getSingleResult();
    }
}
