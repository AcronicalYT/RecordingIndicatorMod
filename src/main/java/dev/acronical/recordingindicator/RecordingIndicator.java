package dev.acronical.recordingindicator;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordingIndicator implements ModInitializer {

    public static final String MOD_ID = "recordingindicator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Recording Indicator initialised");
    }
}
