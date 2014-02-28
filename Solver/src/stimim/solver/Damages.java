package stimim.solver;


public class Damages {
  interface DamageCalculator {
    double calculate(Damages damages);
  }

  public static class RemovedGem {
    public int normal;
    public int enhanced;
    public int combo;
    public boolean isMultiAttack;

    public RemovedGem() {
      normal = 0;
      enhanced = 0;
      combo = 0;
      isMultiAttack = false;
    }
  }

  private static DamageCalculator CALCULATOR = DamageCalculatorBuilder.getMultiWoodDamageCalculator();

  private int combo;
  private final Damages.RemovedGem[] removedGems;

  public Damages.RemovedGem get(int x) {
    return removedGems[GemUtil.getBaseGem(x).ordinal()];
  }

  public Damages() {
    this.combo = 0;
    this.removedGems = new Damages.RemovedGem[GemUtil.N_COLOR];
    for (int i = 0; i < removedGems.length; ++i) {
      removedGems[i] = new Damages.RemovedGem();
    }
  }

  public double toDouble() {
    return CALCULATOR.calculate(this);
  }

  public static void setCalculator(DamageCalculator calculator) {
    CALCULATOR = calculator;
  }

  public int getCombo() {
    return combo;
  }

  public Damages addCombo(int amount) {
    this.combo += amount;
    return this;
  }
}