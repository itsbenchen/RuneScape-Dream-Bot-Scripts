import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.Category;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;

@ScriptManifest(category = Category.WOODCUTTING, name = "Draynor Village Woodcutting", author = "Senkaru", description = "Agility leveling in Gnome Stronghold Agility Course", version = 1.0)

public class DraynorVillageWoodcutting extends AbstractScript {
    Area bankingArea = new Area(3092, 3240, 3246, 0); // Area for Draynor village
    Area treeCuttingArea = new Area(3092, 3295, 3108, 3281, 0); // Area near Draynor village with Trees

    String treeName = "Tree"; // Name of tree to cut
    String logName = "Logs"; // Name of the log received from cutting tree
    int sleepTimerMin = 1000; // Minimum sleep time
    int sleepTimerMax = 10000; // Maximum sleep time

    @Override
    public void onStart() {
        log("Draynor Village Woodcutting Bot has started...");
        log("For any bugs, please message me on Discord");
    }

    @Override
    public int onLoop() {
        Player localPlayer = getLocalPlayer(); // Your character
        /* Chop Trees until inventory is full */
        if (!Inventory.isFull()) {

            if (treeCuttingArea.contains(localPlayer)) { // If your character is within the area...
                doChopping();
            } else { // Else... move your character to the tree cutting area
                Tile randomTile = treeCuttingArea.getRandomTile();
                if (Walking.walk(randomTile)) { // Walks to the specified tile
                    int sleepTime = Calculations.random(sleepTimerMin, sleepTimerMax); // Adds randomness to bot to
                                                                                       // prevent bans
                    sleep(sleepTime);
                }
            }
        } else { /* Inventory is full; we need to put all the logs in the bank */
            if (Inventory.isFull()) {
                if (bankingArea.contains(localPlayer)) { // If your character is within the banking area to enter the
                                                         // banking storage...
                    doBanking();
                } else { // Else... move your character to the banking area
                    Tile randomTile = bankingArea.getRandomTile();
                    if (Walking.walk(randomTile)) { // Walks to the specified tile
                        int sleepTime = Calculations.random(sleepTimerMin, sleepTimerMax); // Adds randomness to bot to
                                                                                           // prevent bans
                        sleep(sleepTime);
                    }
                }
            }
        }

        return 600;
    }

    @Override
    public void onExit() {
        log("Draynor Village Woodcutting Bot has now ended.");
        log("Thank you for using my script!");
    }

    private void doChopping() {
        GameObject tree = GameObjects.closest(o -> o != null && o.getName().equals(treeName));
        if (tree != null && tree.interact("Chop down")) {
            int logCount = Inventory.count(logName);
            sleepUntil(() -> Inventory.count(logName) > logCount, sleepTimerMax); // Program will sleep until the log
                                                                                  // count in inventory > previous
        }
    }

    private void doBanking() {
        NPC banker = NPCs.closest(npc -> npc != null && npc.hasAction("Bank")); // Bankers have a unique option called "Bank"; search for it
        if (banker != null && banker.interact("Bank")) {
            boolean slept = sleepUntil(() -> Bank.isOpen(), sleepTimerMax);
            if (slept) {
                if (Bank.depositAllExcept(item -> item != null && item.getName().contains("axe"))) { // Deposit all items except your axe (used to chop trees)
                    boolean slept2 = sleepUntil( () -> !Inventory.isFull(), sleepTimerMax);
                    if(slept2 && Bank.close()) {
                        sleepUntil( () -> !Bank.isOpen(), sleepTimerMax);
                    }
                }
            }
        }
    }
}
