import java.util.*;

public class Scheduler {
	// constants for testing
	public static final int NUM_NODES_BASIC = 12;
	private final int RAND_VARIANCE_BASIC = 5;
	private final int MAX_WAKE_ROUNDS_BASIC = 12;

	private List<Node> allNodes;
	private ArrayList<Integer> ids;
	private ArrayList<Integer> wakeRounds;

	private int currentRound;

	public Scheduler() {
		this.allNodes = new ArrayList<>();
		this.ids = new ArrayList<>();
		this.wakeRounds = new ArrayList<>();

		start();

		this.currentRound = 1;

		simulateLCR();
	}

	// #############################################
	// #===========NODE GENERATION LOGIC===========#
	// #############################################

	// shuffled array of unique ids
	private void generateIds() {
		System.out.println("[INFO] Random ID variance is set to " + RAND_VARIANCE_BASIC);
		System.out.println("[WORK] Generating random IDs...");
		int randomInt = 0;
		for (int i = 0; i < NUM_NODES_BASIC; i++) {
			randomInt += (int) (Math.random() * RAND_VARIANCE_BASIC) + 1;
			this.ids.add(randomInt);
		}

		Collections.shuffle(this.ids);
	}

	// array of random wakeRounds
	private void generateWakeRounds() {
		System.out.println(
				"[WORK] " + MAX_WAKE_ROUNDS_BASIC + " maximum number of wake rounds being assigned...");
		int randomInt = 0;
		for (int i = 0; i < NUM_NODES_BASIC; i++) {
			randomInt = (int) (Math.random() * MAX_WAKE_ROUNDS_BASIC);
			this.wakeRounds.add(randomInt);
		}
	}

	// uses the randomly generated ids and wake rounds
	public void generateNodes() {
		System.out.println("[WORK] " + NUM_NODES_BASIC + " nodes being generated...");
		for (int i = 0; i < NUM_NODES_BASIC; i++) {
			this.allNodes.add(new Node(
					this.ids.get(i),
					this.wakeRounds.get(i)));
		}
	}

	public void giveNeighbours() {
		for (int i = 0; i < NUM_NODES_BASIC; i++) {
			if (i < NUM_NODES_BASIC - 1) {
				this.allNodes.get(i).giveNeighbour(this.allNodes.get(i + 1));
			} else {
				this.allNodes.get(i).giveNeighbour(this.allNodes.get(0));
			}
		}
	}

	// DEBUG: prints all nodes and their neighbours
	public void printRing() {
		for (Node n : this.allNodes) {
			System.out.println("Node " + n.getId() + " -> Node " + n.getNextNeighbour().getId());
		}
	}

	public void start() {
		System.out.println("[INIT] Generating ring network");

		generateIds();
		generateWakeRounds();
		generateNodes();
		giveNeighbours();

		System.out.println("[DONE] Ring network generated!");

		// printRing();
	}

	// #############################################
	// #=========MESSAGE GENERATION LOGIC==========#
	// #############################################

	public void simulateLCR() {
		System.out.println("[WORK] Simulating generated ring network...");

		// summary
		System.out.println("[DONE] Simulation finished!");
		System.out.println("\nSUMMARY");
		System.out.println("-------");
	}

	// #############################################

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
	}
}
