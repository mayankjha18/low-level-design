package lld.elevator;

import java.util.List;

class DynamicScaler
{
    private final int TRAFFIC_THRESHOLD = 10;
    public void adjustElevatorCount(List<Elevator> elevators, int traffic) {
        int activeCount = traffic == TRAFFIC_THRESHOLD ? elevators.size() : elevators.size() / 2;

        for (int i = 0; i < elevators.size(); i++) {
            elevators.get(i).setState(
                    i < activeCount ? ElevatorState.ACTIVE : ElevatorState.POWER_SAVING
            );
        }
    }
}
