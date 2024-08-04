package dev.acronical.recordingindicator.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordingIndicatorClient implements ClientModInitializer {

    public static final String MOD_ID = "recordingindicator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client-side Recording Indicator initialised");
    }
}
