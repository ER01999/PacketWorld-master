package agent.behavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
//import util.MyColor;
import environment.Perception;

public class BasicComplexity extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }

    
    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = new ArrayList<>(List.of(
            new Coordinate(1, 1), new Coordinate(-1, -1),
            new Coordinate(1, 0), new Coordinate(-1, 0),
            new Coordinate(0, 1), new Coordinate(0, -1),
            new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        // Shuffle moves randomly
        Collections.shuffle(moves);

        // Retrieve best possible move towards closest packet
        if (!agentState.hasCarry()){
            Coordinate c = getBestCoordinateMoveBox(agentState, agentAction);
            if (c != null)
                Collections.swap(moves, 0, moves.indexOf(c));
            //System.out.println(c);
        }
        // Retrieve best possible move towards closest destination
        else{
            Coordinate c = getBestCoordinateMoveDestination(agentState, agentAction);
            if (c != null)
                Collections.swap(moves, 0, moves.indexOf(c));
            //System.out.println(c);
        }
        

        // Check for viable moves
        for (var move : moves) {
            var perception = agentState.getPerception();
            int x = move.getX();
            int y = move.getY();

            // If the area is null, it is outside the bounds of the environment
            //  (when the agent is at any edge for example some moves are not possible)
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                //System.out.println("Cell is walkable");
                agentAction.step(agentState.getX() + x, agentState.getY() + y);
                //System.out.println(agentState.getName() + " " + agentState.getX() + " " + agentState.getY() + " " + agentState.getPerception().getCellPerceptionOnAbsPos(0, 0));
                return;
            } else if (perception.getCellPerceptionOnRelPos(x, y) != null && !perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                //System.out.println("Cell is not walkable");
                if (perception.getCellPerceptionOnRelPos(x, y).containsPacket() && !agentState.hasCarry()) {
                    //System.out.println("Cell contains packet");
                    agentAction.pickPacket(agentState.getX() + x, agentState.getY() + y);
                    return;
                } else if (agentState.hasCarry() && perception.getCellPerceptionOnRelPos(x, y).containsDestination(agentState.getCarry().get().getColor())) {
                    //System.out.println("Put packet");
                    //System.out.println(agentState.getCarry().get().getColor());        
                    agentAction.putPacket(agentState.getX() + x, agentState.getY() + y);
                    return;
                } 
            }
        }

        // No viable moves, skip turn
        agentAction.skip();
    }

    //Returns the coordinates for the best move to get a box
    private Coordinate getBestCoordinateMoveBox(AgentState agentState, AgentAction agentAction) {
        var perception = agentState.getPerception();
        int height = perception.getHeight();
        int width = perception.getWidth();
        int offsetX = perception.getOffsetX();
        int offsetY = perception.getOffsetY();
        List<CellPerception> packetCells = new ArrayList<CellPerception>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j) != null && perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j).containsPacket()) {
                    packetCells.add(perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j));
                    //System.out.println(offsetX + i +" "+ offsetY + j);
                }
            }
        }
        if (packetCells.size() != 0) {
            CellPerception closestCell = packetCells.get(0);
            //System.out.println("Packet size > 0");
            for (int i = 1; i < packetCells.size(); i++) {
                if (Perception.ManhattanDistance(perception.getCellPerceptionOnRelPos(0, 0), packetCells.get(i)) < Perception.ManhattanDistance(closestCell, packetCells.get(i)))
                    closestCell = packetCells.get(i);
            }
            if (closestCell.getX() == perception.getCellPerceptionOnRelPos(0, 0).getX())
                return new Coordinate(0, closestCell.getY() < perception.getCellPerceptionOnRelPos(0, 0).getY() ? -1 : 1);
            else if (closestCell.getY() == perception.getCellPerceptionOnRelPos(0, 0).getY())
                return new Coordinate(closestCell.getX() < perception.getCellPerceptionOnRelPos(0, 0).getX() ? -1 : 1, 0);
            else 
                return new Coordinate(closestCell.getX() < perception.getCellPerceptionOnRelPos(0, 0).getX() ? -1 : 1, closestCell.getY() < perception.getCellPerceptionOnRelPos(0, 0).getY() ? -1 : 1);
        }
        return null;        
    }

    //Returns the coordinates for the best move to go to a destination
    private Coordinate getBestCoordinateMoveDestination(AgentState agentState, AgentAction agentAction) {
        var perception = agentState.getPerception();
        int height = perception.getHeight();
        int width = perception.getWidth();
        int offsetX = perception.getOffsetX();
        int offsetY = perception.getOffsetY();

        CellPerception destination = null;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j) != null && perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j).containsDestination(agentState.getCarry().get().getColor())) {
                    destination = perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j);
                }
            }
        }
        
        if (destination != null) {
            
            if (destination.getX() == perception.getCellPerceptionOnRelPos(0, 0).getX())
                return new Coordinate(0, destination.getY() < perception.getCellPerceptionOnRelPos(0, 0).getY() ? -1 : 1);
            else if (destination.getY() == perception.getCellPerceptionOnRelPos(0, 0).getY())
                return new Coordinate(destination.getX() < perception.getCellPerceptionOnRelPos(0, 0).getX() ? -1 : 1, 0);
            else 
                return new Coordinate(destination.getX() < perception.getCellPerceptionOnRelPos(0, 0).getX() ? -1 : 1, destination.getY() < perception.getCellPerceptionOnRelPos(0, 0).getY() ? -1 : 1);
        }
        
        return null;       
    }
}
