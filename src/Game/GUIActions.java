package Game;

public enum GUIActions {
    NOTHING(),
    SHOWED_DIALOG(),
    PLAYER_DISCONNECTED("Sorry about that", "A player you were playing with has disconnected, what would you like to do?"),
    YOU_WON("YOU DID IT!", "YOU WON THE GAME! what would you like to do next?"),
    ENEMY_WON("You were close", " just won... better luck next time. what would you like to do next?");

    public final String msg;
    public final String title;

    GUIActions(String... data) {
        if (data.length > 1) {
            this.title = data[0];
            this.msg = data[1];
        } else {
            this.title = null;
            this.msg = null;
        }
    }
}
