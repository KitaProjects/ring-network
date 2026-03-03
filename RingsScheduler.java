import java.util.*;

public class RingsScheduler {
	// number of nodes in main ring
	private final static int NUM_NODES_MAIN = 8;
	// number of interface nodes ⊆ number of nodes in main ring
	private final static int[] NUM_NODES_INTERFACE = { 7, 5, 9 };
	// ID generation can be "ascending", "descending" or "random"
	private final static int NUM_NODES_MAIN_INTERFACE = NUM_NODES_INTERFACE.length;
	// number of nodes possible within subrings
	private final static String ID_GEN_BEHAVIOUR = "ascending";
	// the latest a node can wake up
	private final static int MAX_WAKE_ROUND = 12;

	// info
	private int totalRounds;
	private int totalMessages;
	private int totalNonInterfaceNodes;
	private int electedLeaderId;

	// data structures
	private Ring mainRing;
	private List<Node> allMainNodes;

	private List<Ring> subRings;
	private Map<Ring, Node> subringInterfaces;

	private List<Integer> availableIds;

	public RingsScheduler() {
		generateIds();
	}

	private void generateIds() {
		this.subRings = new ArrayList<>();
		this.subringInterfaces = new HashMap<>();

		this.totalNonInterfaceNodes = NUM_NODES_MAIN - NUM_NODES_MAIN_INTERFACE;

		for (int i : NUM_NODES_INTERFACE) {
			this.totalNonInterfaceNodes += i;
		}

		this.availableIds = new ArrayList<>();
		for (int i = 1; i <= this.totalNonInterfaceNodes; i++) {
			this.availableIds.add(i);
		}

		if (ID_GEN_BEHAVIOUR.equals("random"))
			Collections.shuffle(this.availableIds);
	}

	private int generateWakeRound() {
		return (int) (Math.random() * MAX_WAKE_ROUND) + 1;
	}

	private int fetchId(int i) {
		if (ID_GEN_BEHAVIOUR.equals("descending")) {
			return this.availableIds.get(availableIds.size() - 1 - i);
		}

		// random is already sorted at this point
		return this.availableIds.get(i);
	}

	private void generateNetwork() {
		System.out.println("[INFO] Main ring size: " + NUM_NODES_MAIN);
		System.out.println("[INFO] Sub ring sizes: " + NUM_NODES_INTERFACE);
		System.out.println("[INFO] Number of interface nodes: " + NUM_NODES_MAIN_INTERFACE);
		System.out.println("[INFO] Number of non-interface nodes: " + this.totalNonInterfaceNodes);
		System.out.println("---");
		System.out.println("[INIT] Generating ring-of-rings network...");

		this.allMainNodes = new ArrayList();

		// sub-rings are spaced out evenly because OCD
		boolean[] isInterface = new boolean[NUM_NODES_MAIN];
		if (NUM_NODES_MAIN_INTERFACE > 0) {
			for (int i = 0; i < NUM_NODES_MAIN_INTERFACE; i++) {
				isInterface[i * (NUM_NODES_MAIN / NUM_NODES_MAIN_INTERFACE)] = true;
			}
		}

		int iId = 0;
		for (int i = 0; i < NUM_NODES_MAIN; i++, iId++) {
			Node n;
			if (isInterface[i]) {
				n = new Node(-1, generateWakeRound());
			} else {
				n = new Node(fetchId(iId), generateWakeRound());
			}

			this.allMainNodes.add(n);
		}

		int iSubRing = 0;
		for (int i = 0; i < NUM_NODES_MAIN; i++) {
			Node intNode = this.allMainNodes.get(i);
			if (intNode.getId() == -1) {
				int subRingSize = NUM_NODES_INTERFACE[iSubRing];

				List<Node> subRingNodes = new ArrayList<>();
				for (int j = 0; j < subRingSize; j++, iId++) {
					Node subNode = new Node(fetchId(iId), generateWakeRound());
					subRingNodes.add(subNode);
				}

				Ring subRing = new Ring(subRingNodes);
				this.subRings.add(subRing);
				this.subringInterfaces.put(subRing, intNode);

				System.out.println("[DONE] Sub-ring " + iSubRing
						+ " attatched to interface at position " + i);
			}
		}
	}
}
