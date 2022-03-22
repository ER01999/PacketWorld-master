package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;

public class ConditionTwo extends BehaviorChange {
    private boolean hasPacket = true;
    @Override
        public void updateChange() {
        this.hasPacket = this.getAgentState().hasCarry();
    }
    @Override
        public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent has a packet, it will change to BehaviorTwo
        return this.hasPacket;
    }

}
