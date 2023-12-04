package com.levthedev.mc.utility;

import com.levthedev.mc.dao.Plunder;

@FunctionalInterface
public interface PlunderCallback {
    void onQueryFinish(Plunder plunder);
}