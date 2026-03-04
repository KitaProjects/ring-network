public class Node {
	// identity
	private int myId;
	private int wakeRound;
	private String status;
	private boolean terminate;

	// communication links
	private Node nextNeighbour;
	private int sendId;

	public Node(int id, int wakeRound) {
		this.myId = this.sendId = id;
		this.wakeRound = wakeRound;
		this.status = "unknown";
		this.terminate = false;
	}

	public void giveId(int id) {
		this.myId = this.sendId = id;
	}

	public int getId() {
		return this.myId;
	}

	public void giveNeighbour(Node node) {
		this.nextNeighbour = node;
	}

	public Node getNextNeighbour() {
		return this.nextNeighbour;
	}

	public void giveWakeRound(int round) {
		this.wakeRound = round;
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
		if (terminate)
			return null;

		if (myId == -1)
			return null;

		if (message != null) {
			// if my ID has made it back to me I must be the leader
			if (message.content == this.myId) {
				this.status = "leader";
				this.terminate = true;
				return new Message(this.myId, true);
			}

			// if someone is elected who is not me, I must be a follower
			if (message.elected) {
				this.status = "follower";
				this.terminate = true;
				return new Message(message.content, true);
			}

			// bigger ID so send
			if (message.content > this.sendId) {
				this.sendId = message.content;
				return new Message(this.sendId, false);
			}

			return null;
		}

		// no incoming message but send ID if it's the wake round
		if (round >= this.wakeRound) {
			return new Message(this.sendId, false);
		}

		return null;
	}
}
