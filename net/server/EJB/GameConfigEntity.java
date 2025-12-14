package net.server.EJB;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.io.Serializable;

@Entity
@Table(name = "game_config")
public class GameConfigEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private int size;

    private int kingLife;
    private int queenLife;
    private int rookLife;
    private int bishopLife;
    private int knightLife;
    private int pawnLife;

    // getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getKingLife() {
        return kingLife;
    }

    public void setKingLife(int kingLife) {
        this.kingLife = kingLife;
    }

    public int getQueenLife() {
        return queenLife;
    }

    public void setQueenLife(int queenLife) {
        this.queenLife = queenLife;
    }

    public int getRookLife() {
        return rookLife;
    }

    public void setRookLife(int rookLife) {
        this.rookLife = rookLife;
    }

    public int getBishopLife() {
        return bishopLife;
    }

    public void setBishopLife(int bishopLife) {
        this.bishopLife = bishopLife;
    }

    public int getKnightLife() {
        return knightLife;
    }

    public void setKnightLife(int knightLife) {
        this.knightLife = knightLife;
    }

    public int getPawnLife() {
        return pawnLife;
    }

    public void setPawnLife(int pawnLife) {
        this.pawnLife = pawnLife;
    }

}
