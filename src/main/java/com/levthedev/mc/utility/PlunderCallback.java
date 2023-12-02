package com.levthedev.mc.utility;

import com.levthedev.mc.dao.PlunderDAO;

@FunctionalInterface
public interface PlunderCallback {
    void onQueryFinish(PlunderDAO plunder);
}