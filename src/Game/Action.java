package Game;

/**
 * an enum the represents every action a player can do
 */
public enum Action {
    Idle(Constants.IDLE, true, 1),
    Walk(Constants.WALK, true, 0.2),
    Jump(Constants.JUMP, false, 0.4),
    A_(Constants.A_, false, 1),
    AAir(Constants.A_AIR, false, 1),
    Hurt(Constants.HURT, false, 1),
    Fall(Constants.FALL, false, 0.4),
    Dead_Right(Constants.DEATH_RIGHT, false, 0.0, 512, 400, Constants.DEATH_FATHER),
    Dead_Left(Constants.DEATH_LEFT, false, 0.0, 512, 400, Constants.DEATH_FATHER),
    Dead_Bottom(Constants.DEATH_BOTTOM, false, 0.0, 400, 512, Constants.DEATH_FATHER),
    Dead_Up(Constants.DEATH_UP, false, 0.0, 400, 512, Constants.DEATH_FATHER),


    PunchUP(Constants.PUNCH_UP, false, 1),
    Block(Constants.BLOCK, true, 1),
    KO(Constants.KO, false, 1);

    public final String PATH;
    public final boolean IS_ABLE_TO_REPLAY;
    public final double SPEED_MULTIPLIER;
    public final Point SCALE;
    public final int father;

    /**
     * @param path            the path to the Game.Action Game.Sprite
     * @param ableToReplay    whether the sprite needs to start over when it ends.
     * @param speedMultiplier the frameRate will be the multiplied by this
     * @param args            first two are the x and y value of the wanted scale, the third value is the father of the action
     */
    Action(String path, boolean ableToReplay, double speedMultiplier, int... args) {
        this.PATH = path;
        this.IS_ABLE_TO_REPLAY = ableToReplay;
        this.SPEED_MULTIPLIER = speedMultiplier;
        if (args.length > 0) {
            this.SCALE = new Point(args[0], args[1]);
            this.father = args[2];
        } else {
            this.SCALE = null;
            this.father = Constants.NO_FATHER;
        }
    }

}
