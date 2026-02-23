public class Node {
	// identity
	private int myID;
	private int wakeRound;
	private String status;
	private boolean terminate;

	// communication links
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
		// returns null if asleep
		if (wakeRound > round) {
			return null;
		}

		// if my ID has made it back to me I must be the leader
		if (message.content == this.myID) {
			this.status = "leader";
			this.terminate = true;
			return new Message(this.myID, true);
		}

		// if someone is elected who is not me, I must be a follower
		if (message.elected) {
			this.status = "follower";
			this.terminate = true;
			return new Message(message.content, true);
		}

		// handle when another ID is bigger than mine
		if (message.content > this.sendID) {
			this.sendID = message.content;
		}

		return new Message(this.sendID, false);
	}
}
