package dev.acronical.recordingindicator.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Scanner;

public class RecordingIndicatorClient implements ClientModInitializer {

    public static final String MOD_ID = "recordingindicator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier AUTH_PACKET_ID = Identifier.of("acronicalrecordingindicator", "auth");
    public static final Identifier DATA_PACKET_ID = Identifier.of("acronicalrecordingindicator", "data");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client-side Recording Indicator initialised");

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (authenticateWithServer()) {
                try {
                    sendPacketToServer();
                } catch (Exception e) {
                    LOGGER.error("Failed to send data packet to server-side plugin");
                }
            } else {
                LOGGER.error("Failed to authenticate with server-side plugin");
            }
        });
    }

    // Authenticate with the server-side spigot plugin
    public boolean authenticateWithServer() {
        LOGGER.info("Attempting to authenticate with server-side plugin");
        if (!ClientPlayNetworking.canSend(Identifier.of("acronicalrecordingindicator"))) {
            LOGGER.error("Unable to authenticate with server-side plugin: Plugin not found");
            LOGGER.info("Currently open channels: " + ClientPlayNetworking.getSendable());
            return false;
        }
        LOGGER.info("Plugin found, attempting to authenticate");
        try {
            byte[] data = "auth".getBytes();
            AuthorisationPayload payload = new AuthorisationPayload(data);
            ClientPlayNetworking.send(payload);
            LOGGER.info("Successfully authenticated with server-side plugin");
            return true;
        } catch (IllegalStateException e) {
            LOGGER.error("Failed to authenticate with server-side plugin");
            return false;
        }
    }

    // Function to connect to the server-side spigot plugin
    public void sendPacketToServer() {
        byte[] data = createDataPacket();

        if (data == null) {
            LOGGER.error("Failed to create the data packet");
            return;
        }

        RecordingIndicatorPayload payload = new RecordingIndicatorPayload(data);

        try {
            ClientPlayNetworking.send(payload);
        } catch (Exception e) {
            LOGGER.error("Failed to send data packet to server-side plugin");
        }
    }

    // Function to connect to OBS websocket displaying current status (live, recording, etc.)
    public String[] connectToOBS() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URI("http://localhost:63515").toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "RecordingIndicator");

            connection.connect();
            if (connection.getResponseCode() == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();
                connection.disconnect();

                // Parse the response to get the current status of OBS
                return response.toString().split(";;");
            } else {
                LOGGER.error("Failed to connect to OBS websocket");
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to connect to OBS websocket");
            return null;
        }
    }

    // Function to create the data packet to send to the server-side spigot plugin
    public byte[] createDataPacket() {
        String[] obsData = connectToOBS();

        if (obsData == null || obsData[0] == null || obsData[1] == null) {
            LOGGER.error("Failed to create data packet after attempting to connect to OBS websocket");
            return null;
        }

        String indicator = obsData[0];
        boolean enabled = Boolean.parseBoolean(obsData[1]);

        byte indicatorByte;
        byte enabledByte;

        if (indicator.equals("live")) indicatorByte = 0;
        else if (indicator.equals("recording")) indicatorByte = 1;
        else {
            LOGGER.error("Failed to create data packet, invalid indicator");
            return null;
        }

        if (enabled) enabledByte = 1;
        else enabledByte = 0;

        return new byte[] { indicatorByte, enabledByte };
    }
}