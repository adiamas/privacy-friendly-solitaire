package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.Gdx;

import org.secuso.privacyfriendlysolitaire.HistorianListener;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * @author: M. Fischer
 * administrates a collection of snapshots of game states to provide undo/redo functionality
 */
public class Historian implements Observer {

    /**
     * a vector of snapshots of game states
     */
    private Vector<SolitaireGame> history;

    /**
     * the index of the current game state
     */
    private int currentStateIndex;

    private HistorianListener historianListener;

    public Historian() {
        history = new Vector<SolitaireGame>();
        currentStateIndex = -1;
    }


    @Override
    public void update(Observable observable, Object o) {
        SolitaireGame game = (SolitaireGame) observable;
        if (game.getPrevAction() == null) {
            cleanUpHistory();
            history.add(game.clone());
            currentStateIndex++;
            notifyListener();
        }
    }

    /**
     * deletes all game states which indices are greater than the currentStateIndex
     */
    private void cleanUpHistory() {
        if (currentStateIndex < history.size() - 1) {
            for (int i = history.size() - 1; i > currentStateIndex; --i) {
                history.remove(i);
            }
        }
    }

    /**
     * checks if undoing is possible
     *
     * @return true if a at least one game state previous to the current exists
     */
    public boolean canUndo() {
        return currentStateIndex > 0;
    }

    /**
     * checks if redoing is possible
     *
     * @return true if at least one game state subsequent to the current exists
     */
    public boolean canRedo() {
        return currentStateIndex < (history.size() - 1);
    }

    /**
     * @return a copy of the SolitaireGame object that represents the game state previous to the current
     */
    public SolitaireGame undo() {
        currentStateIndex--;
        notifyListener();
        return history.get(currentStateIndex);
    }

    /**
     * @return a copy of the SolitaireGame object that represents the game state subsequent to the current
     */
    public SolitaireGame redo() {
        currentStateIndex++;
        notifyListener();
        return history.get(currentStateIndex);
    }

    public void registerHistorianListener(HistorianListener historianListener) {
        this.historianListener = historianListener;
    }

    private void notifyListener() {
        if (historianListener != null) {
            historianListener.possibleActions(canUndo(), canRedo());
        }
    }
}
