package drawer;

import drawer.actions.Action;
import drawer.actions.ApplicationStartAction;
import drawer.actions.BlankAction;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * All actions that were undone are deleted
 * if you add any new action.
 */
public class EditHistoryBuffer {

    private ListIterator<Action> iterator;

    public EditHistoryBuffer() {
        clear();
    }

    public void clear() {
        LinkedList<Action> buffer = new LinkedList<>();
        iterator = buffer.listIterator();
    }

    public void addAction(Action action) {
        cleanForwardHistory();
        iterator.add(action);
    }

    private void cleanForwardHistory() {
        while(iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    public Action getPreviousAction() {
        if (iterator.hasPrevious())
            return iterator.previous();
        else
            return new ApplicationStartAction();
    }


    public Action getNextAction() {
        if (iterator.hasNext())
            return iterator.next();
        else
            return new BlankAction();
    }

    public Action peekPreviousAction() {
        Action action;
        if (iterator.hasPrevious()) {
            action = iterator.previous();
            iterator.next();
        }
        else
            action = new ApplicationStartAction();

        return action;
    }

    public Action peekNextAction() {
        Action action;
        if (iterator.hasNext()) {
            action = iterator.next();
            iterator.previous();
        }
        else
            action = new BlankAction();

        return action;
    }
}