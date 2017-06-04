package com.allie.pinterestorganizer;

import com.pinterest.android.pdk.PDKBoard;

import java.util.List;

public interface OnUserLoggedInListener {
    void onUserLoggedIn(List<PDKBoard> boardList);
}
