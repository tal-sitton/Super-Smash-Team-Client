public enum Action {
    Idle(Constants.IDLE, true, 1), Walk(Constants.WALK, true, 0.3),
    Jump(Constants.JUMP, false, 0.4), A_(Constants.A_, false, 1),
    AAir(Constants.A_AIR, false, 1), Hurt(Constants.HURT, false, 1),
    Fall(Constants.FALL, false, 0.4), Punch(Constants.PUNCH, false, 1),
    PunchUP(Constants.PUNCH_UP, false, 1), Block(Constants.BLOCK, true, 1),
    KO(Constants.KO, false, 1);

    public final String PATH; //the path to the Action Sprite
    public final boolean IS_ABLE_TO_REPLAY; //whether the sprite needs to start over when it ends.
    public final double SPEED_MULTIPLIER;//the frameRate will be the slowFrameRate of the Sprite

    Action(String path, boolean ableToReplay, double speedMultiplier) {
        this.PATH = path;
        this.IS_ABLE_TO_REPLAY = ableToReplay;
        this.SPEED_MULTIPLIER = speedMultiplier;
    }

}
