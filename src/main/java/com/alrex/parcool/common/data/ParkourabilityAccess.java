package com.alrex.parcool.common.data;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;

public interface ParkourabilityAccess {
    Parkourability parcool$getParkourability();
    ReadonlyStamina parcool$getStamina();
    void parcool$setStamina(ReadonlyStamina stamina);
}
