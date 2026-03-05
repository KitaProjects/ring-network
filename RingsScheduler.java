import java.util.*;

public class RingsScheduler {
	// number of nodes in main ring
	public final static int NUM_NODES_MAIN = 5;
	// number of interface nodes ⊆ number of nodes in main ring
	private final static int[] NUM_NODES_INTERFACE = { 5, 5, 5 };
	// number of nodes possible within subrings
	private final static int NUM_NODES_MAIN_INTERFACE = NUM_NODES_INTERFACE.length;
	// ID generation can be "ascending", "descending" or "random"
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
		generateNetwork();
	}

	private int generateWakeRound() {
		return (int) (Math.random() * MAX_WAKE_ROUND) + 1;
	}

	private int fetchId(int i) {
		if (ID_GEN_BEHAVIOUR.equals("descending")) {
			return this.availableIds.get(availableIds.size() - 1 - i);
		}

		// random is already shuffled at this point
		return this.availableIds.get(i);
	}

	// ############################
	// # NETWORK GENERATION LOGIC #
	// ############################

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

	private void generateNetwork() {
		System.out.println("[INFO] Main ring size: " + NUM_NODES_MAIN);
		System.out.println("[INFO] Sub ring sizes: " + Arrays.toString(NUM_NODES_INTERFACE));
		System.out.println("[INFO] Number of interface nodes: " + NUM_NODES_MAIN_INTERFACE);
		System.out.println("[INFO] Number of non-interface nodes: " + this.totalNonInterfaceNodes);
		System.out.println("[INIT] Generating ring-of-rings network...");

		this.allMainNodes = new ArrayList<>();

		// sub-rings are spaced out evenly because OCD
		boolean[] isInterface = new boolean[NUM_NODES_MAIN];
		if (NUM_NODES_MAIN_INTERFACE > 0) {
			int step = NUM_NODES_MAIN / NUM_NODES_MAIN_INTERFACE;
			for (int i = 0; i < NUM_NODES_MAIN_INTERFACE; i++) {
				int pos = i * step;

				if (pos < NUM_NODES_MAIN) {
					isInterface[pos] = true;
				}
			}

			if (NUM_NODES_MAIN_INTERFACE > 0 && step * (NUM_NODES_MAIN_INTERFACE - 1) >= NUM_NODES_MAIN) {
				isInterface[NUM_NODES_MAIN - 1] = true;
			}
		}

		int iId = 0;
		for (int i = 0; i < NUM_NODES_MAIN; i++) {
			Node n;
			if (isInterface[i]) {
				n = new Node(-1, generateWakeRound());
			} else {
				n = new Node(fetchId(iId), generateWakeRound());
				iId++;
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

				iSubRing++;
			}
		}

		for (int i = 0; i < NUM_NODES_MAIN; i++) {
			Node nNode = allMainNodes.get((i + 1) % NUM_NODES_MAIN);
			this.allMainNodes.get(i).giveNeighbour(nNode);
		}

		this.mainRing = new Ring(this.allMainNodes);

		System.out.println("[DONE] Ring of rings network build complete");
	}

	// ############################
	// # NETWORK SIMULATION LOGIC #
	// ############################

	private void simulateLCR() {
		System.out.println("[WORK] Starting ring-of-rings LCR simulation...");

		int currentRound = 1;
		this.totalMessages = 0;

		while (true) {
			for (Ring subRing : subRings) {
				if (!subRing.getAllTerminated()) {
					int before = subRing.getTotalMessages();
					subRing.processRound(currentRound);
					totalMessages += subRing.getTotalMessages() - before;
				}

				int leaderId = subRing.getLeaderId();
				Node iNode = subringInterfaces.get(subRing);

				if (leaderId != -1 && iNode.getId() == -1) {
					iNode.giveId(leaderId);
					iNode.giveWakeRound(currentRound);
					System.out.println("[INFO] " + iNode.getId()
							+ " learned ID from its subring");
				}

			}

			boolean allSubRingsDone = true;
			for (Ring subRing : subRings) {
				if (!subRing.getAllTerminated()) {
					allSubRingsDone = false;
					break;
				}
			}
			if (allSubRingsDone)
				break;

			if (currentRound > 1000) {
				System.out.println("[TIMEOUT] Simulation timed out after 1000 rounds");
				break;
			}
			currentRound++;
		}

		System.out.println("[INFO] All subrings terminated round: " + currentRound);

		while (!mainRing.getAllTerminated()) {
			int before = mainRing.getTotalMessages();
			mainRing.processRound(currentRound);
			totalMessages += mainRing.getTotalMessages() - before;

			if (currentRound > 2000) {
				System.out.println("[TIMEOUT] Main ring timed out after 2000 rounds");
				break;
			}
			currentRound++;
		}

		this.totalRounds = currentRound;
		this.electedLeaderId = mainRing.getLeaderId();

		System.out.println("[DONE] Ring-of-rings LCR simulation complete");
		System.out.println("\nSUMMARY");
		System.out.println("-------");
		System.out.println("[INFO] Total rounds: " + this.totalRounds);
		System.out.println("[INFO] Total messages : " + this.totalMessages);
		System.out.println("[INFO] Elected leader ID: " + this.electedLeaderId);
	}

	// ########
	// # MAIN #
	// ########

	public static void main(String[] args) {
		RingsScheduler ringsScheduler = new RingsScheduler();
		ringsScheduler.simulateLCR();
	}
}
