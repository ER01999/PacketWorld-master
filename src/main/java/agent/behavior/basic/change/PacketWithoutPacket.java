package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;

public class PacketWithoutPacket extends BehaviorChange {
    private boolean hasPacket = true;
    private boolean seesPacket = false;
    @Override
    public void updateChange() {
        this.hasPacket = this.getAgentState().hasCarry();
        this.seesPacket = this.getAgentState().seesPacket();
    }
    @Override
    public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent has a packet, it will change to BehaviorTwo
        return !this.hasPacket && this.seesPacket;
    }

}
