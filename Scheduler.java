import java.util.*;

public class Scheduler {
	// constants for testing
	private final int NUM_NODES = 12;
	private final int RAND_VARIANCE = 5;
	private final int MAX_WAKE_ROUNDS = 12;

	private List<Node> allNodes;
	private ArrayList<Integer> ids;
	private ArrayList<Integer> wakeRounds;

	private Map<Node, Message> inMessages;
	private Map<Node, Message> outMessages;

	private int currentRound;

	public Scheduler() {
		this.allNodes = new ArrayList<>();
		this.ids = new ArrayList<>();
		this.wakeRounds = new ArrayList<>();

		start();

		// dummy incoming messages
		this.inMessages = new HashMap<>();
		for (Node n : allNodes) {
			this.inMessages.put(n, null);
		}

		this.currentRound = 1;
	}

	// #############################################
	// #===========NODE GENERATION LOGIC===========#
	// #############################################

	// shuffled array of unique ids
	private void generateIds() {
		System.out.println("[INFO] Random ID variance is set to " + RAND_VARIANCE);
		System.out.println("[WORK] Generating random IDs");
		int randomInt = 0;
		for (int i = 0; i < NUM_NODES; i++) {
			randomInt += (int) (Math.random() * RAND_VARIANCE) + 1;
			this.ids.add(randomInt);
		}

		Collections.shuffle(this.ids);
	}

	// array of random wakeRounds
	private void generateWakeRounds() {
		System.out.println("[WORK] " + MAX_WAKE_ROUNDS + " maximum number of wake rounds being assigned...");
		int randomInt = 0;
		for (int i = 0; i < NUM_NODES; i++) {
			randomInt = (int) (Math.random() * MAX_WAKE_ROUNDS);
			this.wakeRounds.add(randomInt);
		}
	}

	// uses the randomly generated ids and wake rounds
	public void generateNodes() {
		System.out.println("[WORK] " + NUM_NODES + " nodes being generated...");
		for (int i = 0; i < NUM_NODES; i++) {
			this.allNodes.add(new Node(
					this.ids.get(i),
					this.wakeRounds.get(i)));
		}
	}

	public void giveNeighbours() {
		for (int i = 0; i < NUM_NODES; i++) {
			if (i < NUM_NODES - 1) {
				this.allNodes.get(i).giveNeighbour(this.allNodes.get(i + 1));
			} else {
				this.allNodes.get(i).giveNeighbour(this.allNodes.get(0));
			}
		}
	}

	// DEBUG: prints all nodes and their neighbours
	public void printRing() {
		for (Node n : allNodes) {
			System.out.println("Node " + n.getId() + " -> Node " + n.getNextNeighbour().getId());
		}
	}

	public void start() {
		System.out.println("[INIT] Generating ring network");

		generateIds();
		generateWakeRounds();
		generateNodes();
		giveNeighbours();

		System.out.println("[DONE] Ring network generated");

		// printRing();
	}

	// #############################################
	// #=========MESSAGE GENERATION LOGIC==========#
	// #############################################

	public void generateMessages() {
		for (int i = 0; i < NUM_NODES; i++) {
			this.allNodes.get(i).processMessage(
					this.currentRound,
					this.outMessages.get(i));
		}
	}

	public void simulateLCR() {
		int totalMessages = 0;
		boolean allTerminated = false;

		while (!allTerminated) {
			System.out.println("[INFO] Round " + this.currentRound + ":");
			this.outMessages = new HashMap<>();

			// create new out messages
			for (Node n : allNodes) {
				if (n.isTerminated())
					continue;
				if (n.getWakeRound() > this.currentRound)
					continue;

				Message inMessage = this.inMessages.get(n);
				Message outMessage = n.processMessage(
						this.currentRound,
						inMessage);

				if (outMessage != null) {
					outMessages.put(n, outMessage);
					totalMessages++;
				}
			}

			// deliver messages to neighbours
			for (Node n : allNodes) {
				Message inMessage = this.outMessages.get(n);
				Message outMessage = n.getNextNeighbour().processMessage(
						this.currentRound,
						inMessage);
			}
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
	}
}
