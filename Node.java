public class Node {
	// Identity
	private int myID;
	private int wakeRound;
	private String status;
	private boolean terminate;

	// Communication Links
	private Node nextNeighbour;
	private int sendID;

	public Node(int id, int wakeRound) {
		this.myID = this.sendID = id;
		this.wakeRound = wakeRound;
		this.status = "unknown";
		this.terminate = false;
	}

	public void giveNeighbour(Node node) {
		this.nextNeighbour = node;
	}

	public Node getNextNeighbour() {
		return this.nextNeighbour;
	}

	public int getId() {
		return this.myID;
	}

	public Message processMessage(int round, Message message) {
		// If my ID has made it back to me I must be the leader
		if (message.content == this.myID) {
			this.status = "leader";
			this.terminate = true;
			return new Message(this.myID, true);
			// If another ID is larger than the one I want to send send that one instead
		} else if (message.content > this.sendID) {
			this.sendID = message.content;
			// If someone has been elected and it's not me, I must be a follower
		} else if (message.elected) {
			this.terminate = true;
			this.status = "follower";
		}

		return new Message(this.sendID, message.elected);
	}
}
