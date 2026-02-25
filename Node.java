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

	public int getWakeRound() {
		return this.wakeRound;
	}

	public String getStatus() {
		return this.status;
	}

	public boolean isTerminated() {
		return this.terminate;
	}

	public Message processMessage(int round, Message message) {
		if (message == null)
			return new Message(sendID, false);

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

		// bigger ID so send
		if (message.content > this.sendID) {
			this.sendID = message.content;
			return new Message(this.sendID, false);
		}

		// smaller ID so do nothing
		return null;
	}
}
