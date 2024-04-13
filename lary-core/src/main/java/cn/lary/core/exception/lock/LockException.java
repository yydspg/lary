package cn.lary.core.exception.lock;

/**
 * @author paul 2024/4/13
 */

public class LockException extends RuntimeException{
    public LockException() {
        super();
    }

    public LockException(String message) {

        super(message);
    }
}
