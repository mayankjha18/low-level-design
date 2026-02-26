package lld.elevator;

import java.util.List;

public class ElevatorManager {

    private final List<Elevator> elevators;

    public ElevatorManager(List<Elevator> elevators) {
        this.elevators = elevators;
    }

    public void handleExternalRequest(ExternalRequest request) {
        Elevator best = findBestElevator(request);
        best.addExternalRequest(request);
    }

    private Elevator findBestElevator(ExternalRequest request) {
        Elevator best = null;
        int minCost = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            int cost = elevator.calculateCost(request);
            if (cost < minCost) {
                minCost = cost;
                best = elevator;
            }
        }
        return best;
    }



    public void handleInternalRequest(int elevatorId, InternalRequest request) {
        elevators.get(elevatorId).addInternalRequest(request);
    }

    // this would run in loop practically
    public void stepAllElevators() {
        for (Elevator elevator : elevators) {
            elevator.step();
        }
    }

    // optimization 
}
