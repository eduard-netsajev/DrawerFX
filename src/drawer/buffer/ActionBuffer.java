package drawer.buffer;

import drawer.action.Action;

public interface ActionBuffer {

    void clear();

    void add(Action action);

    Action getPrevious();

    Action getNext();

    Action peekPrevious();

    Action peekNext();
}
