package cilabo.fuzzy.rule;

import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.consequent.Consequent;

public interface InterfaceRule {
	public InterfaceRule deepcopy();
	public void setAntecedent(Antecedent antecedent);
	public void setConsequent(Consequent consequent);
	public Antecedent getAntecedent();
	public Consequent getConsequent();

}
