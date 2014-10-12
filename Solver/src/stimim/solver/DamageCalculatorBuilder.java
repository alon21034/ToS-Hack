package stimim.solver;

import stimim.solver.Damages.DamageCalculator;
import stimim.solver.Damages.RemovedGem;

public class DamageCalculatorBuilder {
  public static DamageCalculator getDefaultDamageCalculator() {
    return new DamageCalculatorImpl(0.5, new double[] {1, 1, 1, 1, 1, 1}, 1, 1.5, 1);
  }

  public static DamageCalculator getSingleAtkDamageCalculator() {
    return new DamageCalculatorImpl(0.5, new double[] {1, 1, 1, 1, 1, 1}, 1, 1/6.25, 1);
  }

  public static DamageCalculator getAllFireDamageCalculator() {
    return new DamageCalculatorImpl(0.5, new double[] {3, 0.2, 0.2, 0.2, 0.2, 1}, 1, 1.5, 1);
  }

  public static DamageCalculator getAllDarkDamageCalculator() {
    return new DamageCalculatorImpl(0.5, new double[] {0.2, 0.2, 0.2, 0.2, 3, 1}, 1, 1.5, 1);
  }

  public static DamageCalculator getAllWoodDamageCalculator() {
    return new DamageCalculatorImpl(0.5, new double[] {0.1, 3, 0.1, 0.1, 0.1, 1}, 1/2.5, 1, 1);
  }

  public static DamageCalculator getHeartDamageCalculator() {
    return new DamageCalculatorImpl(0.5, new double[] {1, 1, 1, 1, 1, 1}, 1, 1, 1);
  }

  public static class DamageCalculatorImpl implements DamageCalculator {
    private final double comboMult;
    private final double[] scale;
    private final double singleBonus;
    private final double multiBonus;
    private final double bonus;

    private DamageCalculatorImpl(double comboMult, double[] scale, double singleBonus,
        double multiBonus, double bonus) {
      this.comboMult = comboMult;
      this.scale = scale;
      this.singleBonus = singleBonus;
      this.multiBonus = multiBonus;
      this.bonus = bonus;
    }

    @Override
    public double calculate(Damages damages) {
      double score = 0;

      for (int i = 0; i < GemUtil.N_COLOR; ++i) {
        RemovedGem removed = damages.get(i);
        if (removed.combo == 0) {
          continue;
        }
        double value =
            bonus * scale[i] * 0.25
                * (removed.combo + removed.normal + removed.enhanced + removed.enhanced * 0.6);

        if (!removed.isMultiAttack) {
          value *= singleBonus;
        } else {
          value *= multiBonus;
        }
        score += value;
      }

      return score * (1 + (damages.getCombo() - 1) * comboMult);
    }
  }
}