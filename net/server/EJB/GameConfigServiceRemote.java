package net.server.EJB;

import jakarta.ejb.Remote;

@Remote
public interface GameConfigServiceRemote {
    void save(GameConfigEntity config);

    GameConfigEntity loadLast();
}
