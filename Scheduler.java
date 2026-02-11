import java.util.ArrayList;
import java.util.List;

public class Scheduler {
	private List<Node> allNodes = new ArrayList<>();
	private int currentRound = 1;

	public void simulateRounds() {
		while (!allTerminated()) {
			// TODO: implement 3 round phases (prepare, send, update)
			for (Node n : allNodes) {
			}
		}
	}

	public void addNode(Node n) {
		allNodes.add(n);
	}

	private boolean allTerminated() {
		for (Node n : allNodes) {
			if (!n.terminated) {
				return false;
			}
		}
		return true;
	}
}
