package stimim.solver;

import stimim.solver.Damages.DamageCalculator;
import stimim.solver.Damages.RemovedGem;

public class DamageCalculatorBuilder {

	public static class DamageCalculatorImpl implements DamageCalculator {
		private final double comboMult;
		private final double[] scale;
		private final double singleBonus;
		private final double multiBonus;
		private final double bonus;

		private DamageCalculatorImpl(double comboMult, double[] scale,
		        double singleBonus, double multiBonus, double bonus) {
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
				double value = bonus
				        * scale[i]
				        * 0.25
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

	public static DamageCalculator getDefaultDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 1, 1, 1, 1, 1, 1 },
		        1, 1, 1);
	}

	public static DamageCalculator getSingleAtkDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 1, 1, 1, 1, 1, 1 },
		        1, 1 / 6.25, 1);
	}

	public static DamageCalculator getSingleFireAtkDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 3, 0.1, 0.1, 0.1,
		        0.1, 1 }, 1, 1 / 6.25, 1);
	}

	public static DamageCalculator getSingleWoodAtkDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 0.1, 3, 0.1, 0.1,
		        0.1, 1 }, 1, 1 / 6.25, 1);
	}

	public static DamageCalculator getSingleWaterAtkDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 0.1, 0.1, 3, 0.1,
		        0.1, 1 }, 1, 1 / 6.25, 1);
	}

	public static DamageCalculator getSingleLightAtkDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 0.1, 0.1, 0.1, 3,
		        0.1, 1 }, 1, 1 / 6.25, 1);
	}

	public static DamageCalculator getSingleDarkAtkDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 0.1, 0.1, 0.1, 0.1,
		        3, 1 }, 1, 1 / 6.25, 1);
	}

	public static DamageCalculator getSingleHeartAtkDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 0.1, 0.1, 0.1, 0.1,
		        0.1, 3 }, 1, 1 / 6.25, 1);
	}

	public static DamageCalculator getMultiAtkDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 1, 1, 1, 1, 1, 1 },
		        1 / 6.25, 1, 1);
	}

	public static DamageCalculator getMultiFireDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 3, 0.1, 0.1, 0.1,
		        0.1, 1 }, 1 / 6.25, 1, 1);
	}

	public static DamageCalculator getMultiWoodDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 0.1, 3, 0.1, 0.1,
		        0.1, 1 }, 1 / 6.25, 1, 1);
	}

	public static DamageCalculator getMultiWaterDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 0.1, 0.1, 3, 0.1,
		        0.1, 1 }, 1 / 6.25, 1, 1);
	}

	public static DamageCalculator getMultiLightDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 0.1, 0.1, 0.1, 3,
		        0.1, 1 }, 1 / 6.25, 1, 1);
	}

	public static DamageCalculator getMultiDarkDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 0.1, 0.1, 0.1, 0.1,
		        3, 1 }, 1 / 6.25, 1, 1);
	}

	public static DamageCalculator getMultiHeartDamageCalculator() {
		return new DamageCalculatorImpl(0.5, new double[] { 0.1, 0.1, 0.1, 0.1,
		        0.1, 3 }, 1 / 6.25, 1, 1);
	}

	public static DamageCalculator getCustomDamageCalculator(double[] arr,
	        boolean preferSingle) {
		return new DamageCalculatorImpl(0.5, arr,
		        (preferSingle) ? 1 : 1 / 6.25, (preferSingle) ? 1 / 6.25 : 1, 1);
	}

	public static DamageCalculator getBuiltInDamageCalculator(int type) {
		switch (type & 0x1f) {
		case 0x00:
			return getSingleFireAtkDamageCalculator();
		case 0x01:
			return getSingleWoodAtkDamageCalculator();
		case 0x02:
			return getSingleWaterAtkDamageCalculator();
		case 0x03:
			return getSingleLightAtkDamageCalculator();
		case 0x04:
			return getSingleDarkAtkDamageCalculator();
		case 0x05:
			return getSingleHeartAtkDamageCalculator();
		case 0x06:
			return getSingleAtkDamageCalculator();
		case 0x10:
			return getMultiFireDamageCalculator();
		case 0x11:
			return getMultiWoodDamageCalculator();
		case 0x12:
			return getMultiWaterDamageCalculator();
		case 0x13:
			return getMultiLightDamageCalculator();
		case 0x14:
			return getMultiDarkDamageCalculator();
		case 0x15:
			return getMultiHeartDamageCalculator();
		case 0x16:
			return getMultiAtkDamageCalculator();
		default:
			return getDefaultDamageCalculator();
		}
	}
}
