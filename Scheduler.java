import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheduler {
	private final int NUM_NODES = 12;
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

		this.inMessages = new HashMap<>();
		this.outMessages = new HashMap<>();

		this.currentRound = 1;
	}

	// shuffled array of unique ids
	private void generateIds() {
		int randomInt = 0;
		for (int i = 0; i < NUM_NODES; i++) {
			randomInt += (int) (Math.random() * 5) + 1;
			this.ids.add(randomInt);
		}

		Collections.shuffle(this.ids);
	}

	// array of random wakeRounds
	private void generateWakeRounds() {
		int randomInt = 0;
		for (int i = 0; i < NUM_NODES; i++) {
			randomInt = (int) (Math.random() * MAX_WAKE_ROUNDS);
			this.wakeRounds.add(randomInt);
		}
	}

	public void generateNodes() {
		for (int i = 0; i < NUM_NODES; i++) {
			this.allNodes.add(new Node(
					this.ids.get(i),
					this.wakeRounds.get(i)));
		}
	}

	public void start() {
		System.out.println("==GENERATING RING NETWORK==");
		System.out.println(NUM_NODES + " nodes being generated...");
	}
}
