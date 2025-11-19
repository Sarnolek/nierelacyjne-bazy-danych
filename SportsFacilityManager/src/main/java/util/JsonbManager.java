package util;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;


public class JsonbManager {

    public static final Jsonb jsonb = JsonbBuilder.create();


    private JsonbManager() {}
}
