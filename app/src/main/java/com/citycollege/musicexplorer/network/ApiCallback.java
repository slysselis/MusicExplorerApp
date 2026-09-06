package com.citycollege.musicexplorer.network;

public interface ApiCallback<T> {
    void onSuccess(T result);
    void onError(String message);
}
