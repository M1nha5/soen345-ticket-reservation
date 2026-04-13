package com.harjot.ticketreservation.service;

public interface DataCallback<T> {
    void onSuccess(T data);
    void onError(String error);
}
