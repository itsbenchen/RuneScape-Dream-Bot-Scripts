import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;

@ScriptManifest(category = Category.AGILITY, name = "Agility Farming (Gnome Area)", author = "Senkaru", description = "Agility leveling in Gnome Stronghold Agility Course", version = 1.0)

public class AgilityScriptForGnomeStrongHold extends AbstractScript {

    Checkpoint checkpoint;
    int place;
    Area startingArea = new Area(2473, 3438, 2475, 3436);
    Tile startingTile = new Tile(2474, 3437);
    int sleepTimerMin = 1000; // Minimum sleep time
    int sleepTimerMax = 10000; // Maximum sleep time

    // Checkpoint Tiles (Starting)
    Tile logTile = new Tile(2474, 3435);
    Tile firstNetTile = new Tile(2473, 3425);
    Tile firstBranchTile = new Tile(2473, 3422, 1);
    Tile ropeTile = new Tile(2486, 3419, 2);
    Tile secondBranchTile = new Tile(2486, 3419, 2);
    Tile secondNetTile = new Tile(2487, 3426);
    Tile pipeTile = new Tile(2484, 3431);

    // Checkpoint Tiles To Be Reached at the end of checkpoint
    Tile logTileEnd = new Tile(2474, 3429);
    Tile firstNetTileEnd = new Tile(2473, 3423, 1);
    Tile firstBranchTileEnd = new Tile(2473, 3420, 2);
    Tile ropeTileEnd = new Tile(2483, 3420, 2);
    Tile secondBranchTileEnd = new Tile(2487, 3420);
    Tile secondNetTileEnd = new Tile(2485, 3428);
    Tile pipeTileEnd = new Tile(2484, 3437);

    
    @Override
    public void onStart() {
        log("Agility Script for Gnome Stronghold Course has started...");
        log("For any bugs, please message me on Discord");
    }

    @Override
    public int onLoop() {
        /* Do the obstacle depending on the checkpoint that the player is at */
        Player player = getLocalPlayer();
        Checkpoint checkpt = getCheckpoint();
        if (checkpt == Checkpoint.LOG) {
            GameObject log = GameObjects.closest(o -> o != null && o.getName().contentEquals("Log balance") && o.getTile().equals(logTile));
            adjustCameraToObject(log);    // Move camera to object if needed
            movePlayerToTile(player, startingTile, startingArea); // Move player to object if needed
            log.interact("Walk-across");
            sleepUntil( () -> player.getTile().equals(logTileEnd), sleepTimerMax);
        }
        else if (checkpt == Checkpoint.FIRST_NET) {
            GameObject firstNet = GameObjects.closest(o -> o != null && o.getName().contentEquals("Obstacle net") && o.getTile().equals(firstNetTile));
            firstNet.interact("Climb-over");
            sleepUntil( () -> player.getTile().equals(firstNetTileEnd), sleepTimerMax);
        }
        else if (checkpt == Checkpoint.FIRST_BRANCH) {
            GameObject firstBranch = GameObjects.closest(o -> o != null && o.getName().contentEquals("Tree branch") && o.getTile().equals(firstBranchTile));
            firstBranch.interact("Climb");
            sleepUntil( () -> player.getTile().equals(firstBranchTileEnd), sleepTimerMax);
        }
        else if (checkpt == Checkpoint.ROPE) {
            GameObject rope = GameObjects.closest(o -> o != null && o.getName().contentEquals("Balancing rope") && o.getTile().equals(ropeTile));
            rope.interact("Walk-on");
            sleepUntil( () -> player.getTile().equals(ropeTileEnd), sleepTimerMax);
        }
        else if (checkpt == Checkpoint.SECOND_BRANCH) {
            GameObject secondBranch = GameObjects.closest(o -> o != null && o.getName().contentEquals("Tree branch") && o.getTile().equals(secondBranchTile));
            secondBranch.interact("Climb-down");
            sleepUntil( () -> player.getTile().equals(secondBranchTileEnd), sleepTimerMax);
        }
        else if (checkpt == Checkpoint.SECOND_NET) {
            GameObject secondNet = GameObjects.closest(o -> o != null && o.getName().contentEquals("Obstacle net") && o.getTile().equals(secondNetTile));
            secondNet.interact("Climb-over");
            sleepUntil( () -> player.getTile().equals(secondNetTileEnd), sleepTimerMax);
        }
        else if (checkpt == Checkpoint.PIPE) {
            GameObject pipe = GameObjects.closest(o -> o != null && o.getName().contentEquals("Obstacle pipe") && o.getTile().equals(pipeTile));
            pipe.interact("Squeeze-through");
            sleepUntil( () -> player.getTile().equals(pipeTileEnd), sleepTimerMax);
        }

        return 600;
    }

    @Override
    public void onExit() {
        log("Agility Script for Gnome Stronghold Course has now ended.");
        log("Thanks for using my script!");
    }

    private enum Checkpoint { // Refers to the obstacle checkpoints in-game
        LOG, FIRST_NET, FIRST_BRANCH, ROPE, SECOND_BRANCH, SECOND_NET, PIPE
    }

    private void adjustCameraToObject(GameObject gameObject) {
        /* Adjust camera to have the game object on screen */
        if (!gameObject.isOnScreen()) { // If Camera doesn't have the object in it, then adjust camera to it
            Camera.keyboardRotateToEntity(gameObject);
        }
    }

    private void movePlayerToTile(Player player, Tile startingTile, Area startingArea) {
        /* If player isn't within the designated area, move player to that area */
        if (!startingArea.contains(player)) {
            Walking.walk(startingTile);
            sleepUntil( () -> startingArea.contains(player), sleepTimerMax);
        }
    }

    private Checkpoint getCheckpoint() {
        Player localPlayer = getLocalPlayer();
        boolean localPlayerAtStart= startingArea.contains(localPlayer);
        if (localPlayerAtStart ||localPlayer.getTile().equals(pipeTile)) { // If at start OR end of course
            return Checkpoint.LOG;
        }
        else if (localPlayer.getTile().equals(logTile)) {
            return Checkpoint.FIRST_NET;
        }
        else if (localPlayer.getTile().equals(firstNetTile)) {
            return Checkpoint.FIRST_BRANCH;
        }
        else if (localPlayer.getTile().equals(firstBranchTile)) {
            return Checkpoint.ROPE;
        }
        else if (localPlayer.getTile().equals(ropeTile)) {
            return Checkpoint.SECOND_BRANCH;
        }
        else if (localPlayer.getTile().equals(secondBranchTile)) {
            return Checkpoint.SECOND_NET;
        }
        else {  // Must be last checkpoint
            return Checkpoint.PIPE;
        }
    }
}