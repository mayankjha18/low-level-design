package lld.elevator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Elevator {

    private final int id;
    private int currentFloor;
    private Direction direction;

    private final List<Integer> stops;

    private static final int MAX_FLOOR = 27;
    private static final int MIN_FLOOR = 0;

    // for dynamic Scaling
    private ElevatorState state;

    // for time based optimization
    private static final int TIME_PER_FLOOR = 2;
    private static final int STOP_TIME = 5;
    private static final int DIRECTION_REVERSAL_PENALTY = 10;


    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 0;
        this.direction = Direction.IDLE;
        this.stops = new ArrayList<>();
    }

    /* ========== COST CALCULATION ========== */

    public int calculateCost(ExternalRequest request) {
        int requestFloor = request.getFloor();
        Direction requestDir = request.getDirection();

        if (direction == Direction.IDLE) {
            return Math.abs(currentFloor - requestFloor);
        }

        if (direction == requestDir) {
            if ((direction == Direction.UP && currentFloor <= requestFloor) ||
                    (direction == Direction.DOWN && currentFloor >= requestFloor)) {
                return Math.abs(currentFloor - requestFloor);
            }
        }

        return remainingDistanceToEnd()
                + Math.abs(getEndFloor() - requestFloor);
    }

    private int remainingDistanceToEnd() {
        if (direction == Direction.IDLE) return 0;

        int lastStop = getLastStopInCurrentDirection();
        return Math.abs(lastStop - currentFloor);
    }

    private int getLastStopInCurrentDirection() {
        int lastStop = currentFloor;

        for (int stop : stops) {
            if (direction == Direction.UP && stop > lastStop) {
                lastStop = stop;
            } else if (direction == Direction.DOWN && stop < lastStop) {
                lastStop = stop;
            }
        }
        return lastStop;
    }


    private int getEndFloor() {
        return direction == Direction.UP ? MAX_FLOOR : MIN_FLOOR;
    }

    /* ========== REQUEST HANDLING ========== */

    public void addExternalRequest(ExternalRequest request) {
        int floor = request.getFloor();
        if (floor == currentFloor || stops.contains(floor)) return;
        stops.add(floor);

    }

    public void addInternalRequest(InternalRequest request) {
        int floor = request.getDestinationFloor();
        if (floor == currentFloor || stops.contains(floor)) return;
        stops.add(floor);
    }

    /* ========== MOVEMENT LOGIC ========== */

    public void step() {
        if (stops.isEmpty()) {
            direction = Direction.IDLE;
            return;
        }

        sortStopsByDirection();

        int nextStop = stops.remove(0);
        currentFloor = nextStop;

        updateDirection();
    }

    private void sortStopsByDirection() {
        if (direction == Direction.UP) {
            Collections.sort(stops);
        } else if (direction == Direction.DOWN) {
            stops.sort(Collections.reverseOrder());
        } else {
            // IDLE: choose closest floor
            stops.sort((a, b) ->
                    Integer.compare(
                            Math.abs(a - currentFloor),
                            Math.abs(b - currentFloor)
                    )
            );
        }
    }

    private void updateDirection() {
        if (stops.isEmpty()) {
            direction = Direction.IDLE;
            return;
        }

        int next = stops.get(0);
        if (next > currentFloor) direction = Direction.UP;
        else if (next < currentFloor) direction = Direction.DOWN;
    }


    public int estimateTimeToServe(ExternalRequest request) {
        int time = 0;

        time += Math.abs(currentFloor - request.getFloor()) * TIME_PER_FLOOR;
        time += stops.size() * STOP_TIME;

        if (direction != Direction.IDLE && direction != request.getDirection()) {
            time += DIRECTION_REVERSAL_PENALTY;
        }

        return time;
    }

    /* ========== GETTERS ========== */

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public ElevatorState getState() {
        return state;
    }

    public void setState(ElevatorState state) {
        this.state = state;
    }
}
