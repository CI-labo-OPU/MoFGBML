package cilabo.fuzzy.rule.consequent;

import cilabo.data.DataSet;
import cilabo.fuzzy.rule.antecedent.Antecedent;

public interface ConsequentFactory {

	public Consequent learning(Antecedent antecedent);
	public DataSet getTrain();
}
