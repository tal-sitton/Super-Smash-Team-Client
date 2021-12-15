package Game2.Game;

/**
 * a class that represents an {@link Actor} that the user <b>can't</b> move <br>
 * currently empty, but maybe it will change in the future
 *
 * @see Actor
 */
public class Enemy extends Actor {
    private static int index = 2;

    public Enemy(Sprite mySprite, String name) {
        super(mySprite, name);
    }

}
