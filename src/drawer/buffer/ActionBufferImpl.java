package drawer.buffer;

import drawer.action.Action;
import drawer.action.ApplicationStartAction;
import drawer.action.BlankAction;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * All actions that were undone are deleted
 * if you add any new action.
 */
public class ActionBufferImpl implements ActionBuffer {

    private ListIterator<Action> iterator;

    public ActionBufferImpl() {
        clear();
    }

    @Override
    public void clear() {
        LinkedList<Action> buffer = new LinkedList<>();
        iterator = buffer.listIterator();
    }

    @Override
    public void add(Action action) {
        cleanForwardHistory();
        iterator.add(action);
    }

    private void cleanForwardHistory() {
        while(iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    @Override
    public Action getPrevious() {
        if (iterator.hasPrevious())
            return iterator.previous();
        else
            return new ApplicationStartAction();
    }

    @Override
    public Action getNext() {
        if (iterator.hasNext())
            return iterator.next();
        else
            return new BlankAction();
    }

    @Override
    public Action peekPrevious() {
        Action action;
        if (iterator.hasPrevious()) {
            action = iterator.previous();
            iterator.next();
        }
        else
            action = new ApplicationStartAction();

        return action;
    }

    @Override
    public Action peekNext() {
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