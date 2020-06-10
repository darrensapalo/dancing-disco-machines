package advanos.messages;

import io.reactivex.Observable;

public interface Messager<E> {

    /**
     * Performs the operation for this message.
     * @return
     */
    Observable<E> perform();
}
