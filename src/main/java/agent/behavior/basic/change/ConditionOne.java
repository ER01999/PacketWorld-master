package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;

public class ConditionOne extends BehaviorChange {
    private boolean seesSomething = false;
    @Override
        public void updateChange() {
        this.seesSomething = this.getAgentState().hasCarry();
    }
    @Override
        public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent has a packet, it will change to BehaviorTwo
        return this.seesSomething;
    }

}
