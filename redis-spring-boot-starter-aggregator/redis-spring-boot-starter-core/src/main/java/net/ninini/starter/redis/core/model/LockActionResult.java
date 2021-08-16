package net.ninini.starter.redis.core.model;

public class LockActionResult<T> {

    Boolean lockState;

    T execResult;

    public LockActionResult(Boolean lockState, T execResult) {
        this.lockState = lockState;
        this.execResult = execResult;
    }
}
