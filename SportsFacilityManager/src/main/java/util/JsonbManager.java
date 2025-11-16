package util;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

/**
 * Prosta klasa narzędziowa udostępniająca
 * jedną, współdzieloną instancję konwertera JSON-B (Yasson).
 * * Używamy jednej instancji, ponieważ tworzenie jej za każdym razem
 * byłoby niewydajne.
 */
public class JsonbManager {
    // Ustawiamy ją jako publiczną, statyczną i finalną,
    // aby była łatwo dostępna z całej aplikacji.
    public static final Jsonb jsonb = JsonbBuilder.create();

    // Prywatny konstruktor, aby nikt nie mógł stworzyć instancji tej klasy
    private JsonbManager() {}
}
