package com.idemia.pocidemiacarabineros.services;

public interface AsynchronousTask {
    void onReceiveResults(String msn);
    void onFailure(final String msn);
}
