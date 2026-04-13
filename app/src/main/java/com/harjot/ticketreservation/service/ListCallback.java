package com.harjot.ticketreservation.service;

import java.util.List;

public interface ListCallback<T> {
    void onSuccess(List<T> items);
    void onError(String error);
}
