package com.example.faceauthentication.utils;

public interface FaceComparationCallback {
    void onComparationCompleted(String result);
    void onComparationFailed(String Failed);

}
