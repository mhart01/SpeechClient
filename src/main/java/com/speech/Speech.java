package com.speech;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mydaco.client.SyncMydacoClient;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import org.apache.commons.codec.binary.Base64;
import java.io.ByteArrayInputStream;

public class Speech {

    private static final SyncMydacoClient client = new SyncMydacoClient();

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("No endpoint found");
            return;
        }
        client.setDomain(args[0]);
        String endpointId = args[1];
        String fullString = args[2];
        JsonObject req = new JsonObject();
        req.addProperty("format", "mp3");
        req.addProperty("mode", "synthesize");
        req.addProperty("text", fullString);
        JsonObject response = client.call(endpointId, req);

        JsonArray valuesSpaces = response.get("valueSpaces").getAsJsonArray();
        for (JsonElement element : valuesSpaces) {
            JsonObject valueSpace = element.getAsJsonObject();
            if (valueSpace.get("metaInformation").getAsJsonObject().get("mode").getAsString().equals("anonymous")) {
                String audioString = valueSpace.get("response").getAsJsonObject().get("audio").getAsString();
                playAudioString(audioString);
            }
        }

    }

    private static void playAudioString(String audioString) throws Exception {
        byte[] audio = Base64.decodeBase64(audioString);

        AdvancedPlayer player = new AdvancedPlayer(new ByteArrayInputStream(audio),
                javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());
        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackStarted(PlaybackEvent evt) {
                System.out.println("Playback started");
            }

            @Override
            public void playbackFinished(PlaybackEvent evt) {
                System.out.println("Playback finished");
            }
        });
        player.play();
    }

}
