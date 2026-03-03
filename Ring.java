import java.util.*;

public class Ring {
	private List<Node> allNodes;

	private Map<Node, Message> inMessages;
	private Map<Node, Message> nextInMessages;
	private Map<Node, Message> outMessages;

	private int totalMessages;
	private boolean allTerminated;

	public Ring(List<Node> nodes) {
		this.allNodes = new ArrayList<>(nodes);
		this.totalMessages = 0;
		this.allTerminated = false;

		// setup neighbours clockwise
		for (int i = 0; i < Scheduler.NUM_NODES; i++) {
			Node nextNode = nodes.get((i + 1) % nodes.size());

			nodes.get(i).giveNeighbour(nextNode);
		}

		// setup messages
		this.inMessages = new HashMap<>();
		this.nextInMessages = new HashMap<>();
		for (Node n : nodes) {
			this.inMessages.put(n, null);
			this.nextInMessages.put(n, null);
		}
	}

	public boolean processRound(int currentRound) {
		if (this.allTerminated) {
			return true;
		}

		for (Node n : this.allNodes) {
			nextInMessages.put(n, null);
		}

		for (Node n : this.allNodes) {
			if (n.isTerminated()) {
				continue;
			}

			if (n.getWakeRound() > currentRound) {
				continue;
			}

			Message inMessage = this.inMessages.get(n);

			Message outMessage = n.processMessage(currentRound, inMessage);

			if (outMessage != null) {
				this.outMessages.put(n, outMessage);
				totalMessages++;
			}
		}

		for (Map.Entry<Node, Message> e : outMessages.entrySet()) {
			Node sender = e.getKey();
			Node receiver = sender.getNextNeighbour();
			Message outMessage = e.getValue();

			// TODO: Check if redundant or not
			if (receiver.getWakeRound() < currentRound) {
				this.nextInMessages.put(receiver, outMessage);
			}
		}

		this.inMessages = this.nextInMessages;

		this.allTerminated = true;
		for (Node n : this.allNodes) {
			if (!n.isTerminated()) {
				this.allTerminated = false;
				break;
			}
		}

		return this.allTerminated;
	}

	public boolean getAllTerminated() {
		return this.allTerminated;
	}

	public int getLeaderId() {
		for (Node n : this.allNodes) {
			if (n.getStatus().equals("leader")) {
				return n.getId();
			}
		}

		return -1;
	}

	public int getTotalMessage() {
		return this.totalMessages;
	}
}
