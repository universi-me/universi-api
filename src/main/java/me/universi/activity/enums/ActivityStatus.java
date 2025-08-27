package me.universi.activity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Current status of this Activity.\n\n`NOT_STARTED` means the Activity will happen in the future;\n\n`STARTED` means it already started or will start today;\n\nand `ENDED` means the endDate already passed" )
public enum ActivityStatus {
    NOT_STARTED,
    STARTED,
    ENDED
}
