package com.classroom.bookyard.Helpers;

import android.net.Uri;

public interface DataStatusImage {

    void onSuccess(Uri uri);

    void onError(String e);
}
