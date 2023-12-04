package com.levthedev.mc.dao;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlunderState {

    @NonNull
    private final UUID playerUuid;
    @NonNull
    private final String blockId;
    @NonNull
    private final byte[] stateData;
    @NonNull
    private final Boolean ignoreRestock;
}
