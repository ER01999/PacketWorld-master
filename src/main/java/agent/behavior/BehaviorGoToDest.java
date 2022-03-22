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

public class BehaviorGoToDest extends Behavior {

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
        if (agentState.hasCarry()){
            moves = getBestCoordinateMoveDestination(agentState, agentAction);
            //System.out.println(c);
        } else {
            System.out.println("Eres tonto y estas yendo al destino sin paquete zoquete");
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

    //Returns the coordinates for the best move to go to a destination
    private List<Coordinate> getBestCoordinateMoveDestination(AgentState agentState, AgentAction agentAction) {
    //     var perception = agentState.getPerception();
    //     int height = perception.getHeight();
    //     int width = perception.getWidth();
    //     int offsetX = perception.getOffsetX();
    //     int offsetY = perception.getOffsetY();

    //     CellPerception destination = null;
    //     for (int i = 0; i < height; i++) {
    //         for (int j = 0; j < width; j++) {
    //             if (perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j) != null && perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j).containsDestination(agentState.getCarry().get().getColor())) {
    //                 destination = perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j);
    //             }
    //         }
    //     }
        
    //     if (destination != null) {
            
    //         if (destination.getX() == perception.getCellPerceptionOnRelPos(0, 0).getX())
    //             return new Coordinate(0, destination.getY() < perception.getCellPerceptionOnRelPos(0, 0).getY() ? -1 : 1);
    //         else if (destination.getY() == perception.getCellPerceptionOnRelPos(0, 0).getY())
    //             return new Coordinate(destination.getX() < perception.getCellPerceptionOnRelPos(0, 0).getX() ? -1 : 1, 0);
    //         else 
    //             return new Coordinate(destination.getX() < perception.getCellPerceptionOnRelPos(0, 0).getX() ? -1 : 1, destination.getY() < perception.getCellPerceptionOnRelPos(0, 0).getY() ? -1 : 1);
    //     }
        
    //     return null;       
    // }
        List<Coordinate> moves = new ArrayList<Coordinate>();
        var perception = agentState.getPerception();
        int height = perception.getHeight();
        int width = perception.getWidth();
        int offsetX = perception.getOffsetX();
        int offsetY = perception.getOffsetY();
        List<CellPerception> destCells = new ArrayList<CellPerception>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j) != null && perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j).containsDestination(agentState.getCarry().get().getColor())) {
                    destCells.add(perception.getCellPerceptionOnAbsPos(offsetX + i, offsetY + j));
                    //System.out.println(offsetX + i +" "+ offsetY + j);
                }
            }
        }
        if (destCells.size() != 0) {
            CellPerception closestCell = destCells.get(0);
            //System.out.println("Packet size > 0");
            //System.out.println("Agent Coordinates: ("+agentState.getX()+","+agentState.getY());
            //System.out.println("Perception Coordinates: ("+perception.getCellPerceptionOnRelPos(0, 0).getX()+","+perception.getCellPerceptionOnRelPos(0, 0).getY());
            for (int i = 1; i < destCells.size(); i++) {
                //System.out.println("Packet "+i+": ("+packetCells.get(i).getX()+","+packetCells.get(i).getY()+") MD: "+Perception.OptimalDistance(perception.getCellPerceptionOnRelPos(0, 0), packetCells.get(i)));
                if (Perception.OptimalDistance(perception.getCellPerceptionOnRelPos(0, 0), destCells.get(i)) < Perception.OptimalDistance(closestCell, perception.getCellPerceptionOnRelPos(0, 0))){
                    closestCell = destCells.get(i);
                    //System.out.println("Closest cell: ("+closestCell.getX()+","+closestCell.getY());
                }

            }
            //System.out.println("Closest Packet: ("+closestCell.getX()+","+closestCell.getY());

            if (closestCell.getX() == perception.getCellPerceptionOnRelPos(0, 0).getX()){
                int y = closestCell.getY() < perception.getCellPerceptionOnRelPos(0, 0).getY() ? -1 : 1;
                moves.add(new Coordinate(0, y));
                moves.add(new Coordinate(-1, y));
                moves.add(new Coordinate(1, y));
                moves.add(new Coordinate(-1, 0));
                moves.add(new Coordinate(1, 0));
                moves.add(new Coordinate(-1, -y));
                moves.add(new Coordinate(1, -y));
                moves.add(new Coordinate(0, -y));
            }
            else if (closestCell.getY() == perception.getCellPerceptionOnRelPos(0, 0).getY()) {
                int x = closestCell.getX() < perception.getCellPerceptionOnRelPos(0, 0).getX() ? -1 : 1;
                moves.add(new Coordinate(x, 0));
                moves.add(new Coordinate(x, -1));
                moves.add(new Coordinate(x, 1));
                moves.add(new Coordinate(0, -1));
                moves.add(new Coordinate(0, 1));
                moves.add(new Coordinate(-x, -1));
                moves.add(new Coordinate(-x, 1));
                moves.add(new Coordinate(-x, 0));
            }
            else {
                int x = closestCell.getX() < perception.getCellPerceptionOnRelPos(0, 0).getX() ? -1 : 1;
                int y = closestCell.getY() < perception.getCellPerceptionOnRelPos(0, 0).getY() ? -1 : 1;
                moves.add(new Coordinate(x, y));
                if (Math.abs(closestCell.getX() - perception.getCellPerceptionOnRelPos(0, 0).getX()) > Math.abs(closestCell.getY() - perception.getCellPerceptionOnRelPos(0, 0).getY())){
                    moves.add(new Coordinate(x, 0));
                    moves.add(new Coordinate(0, y));
                    moves.add(new Coordinate(x, -y));
                    moves.add(new Coordinate(-x, y));
                    moves.add(new Coordinate(0, -y));
                    moves.add(new Coordinate(-x, 0));
                } 
                else {
                    moves.add(new Coordinate(0, y));
                    moves.add(new Coordinate(x, 0));
                    moves.add(new Coordinate(-x, y));
                    moves.add(new Coordinate(x, -y));
                    moves.add(new Coordinate(-x, 0));
                    moves.add(new Coordinate(0, -y));
                }
                moves.add(new Coordinate(-x, -y));
            }
        }      
        return moves;
    }
}
