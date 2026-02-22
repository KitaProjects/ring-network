public class Node {
	// Identity
	private int myID;
	private int wakeRound;
	private String status;

	// Communication Links
	private Node nextNeighbour;
	private int sendID;

	public Node(int id, int wakeRound) {
		this.myID = this.sendID = id;
		this.wakeRound = wakeRound;
		this.status = "unknown";
	}

	public void giveNeighbour(Node node) {
		this.nextNeighbour = node;
	}

	public Message processMessage(int round, Message message) {
		if (wakeRound > round) {
			return new Message(-1, "unknown");
		}

		if (message.content > this.sendID) {
			this.sendID = message.content;
		} else if (message.content == this.myID) {
			this.status = "leader";
		} else if (message.elected == "leader") {
			this.status = "follower";
		}

		return new Message(this.sendID, this.status);
	}
}
