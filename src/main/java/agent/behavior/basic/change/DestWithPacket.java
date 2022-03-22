package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;

public class DestWithPacket extends BehaviorChange {
    private boolean seesDest = false;
    private boolean hasPacket = false;
    @Override
    public void updateChange() {
        this.hasPacket = this.getAgentState().hasCarry();
        if (this.hasPacket)
            this.seesDest = this.getAgentState().seesDestination(this.getAgentState().getCarry().get().getColor());
        
    }
    @Override
    public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent has a packet, it will change to BehaviorTwo
        return (this.seesDest && this.hasPacket);
    }

}
