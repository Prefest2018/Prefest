package appiumscript.scriptexecutor;

public class PWExecutor {
    private PWPlan pwplan = null;

    public PWExecutor(PWPlan pwplan) {
        this.pwplan = pwplan;
    }

    public void execute() {
        this.pwplan.execute();
    }
}
