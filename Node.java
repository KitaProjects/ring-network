public class Node {
	// Identity
	private int myID;
	private int wakeRound;
	private String status;

	public boolean terminated;

	// LCR Algorithm State
	private int sendID;

	// Communication Links
	private Node nextNeighbor;

	public Node(int id, int wakeRound) {
		this.myID = this.sendID = id;
		this.wakeRound = wakeRound;
		this.status = "unknown";
	}

	// FIXME: Function is temporary, must replace with actual phases of rounds
	public void processRound(int inID, int round, boolean elected) {
		if (wakeRound > round) {
			return;
		}

		if (inID > this.myID) {
			this.sendID = inID;
		} else if (inID == this.myID) {
			this.status = "leader";
		} // else discard the ID
	}
}
