package com.eteirnum.toolkit.eval_ex;

import com.eteirnum.toolkit.EteirnumToolkit;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Level;

@UtilityClass
public class EvalExParser {
    private static final Map<String, ?> VALUE_MAP = Map.of(
            "yes", true,
            "no", false
    );

    public static @NotNull EvaluationValue parse(@NotNull String toParse) {
        Expression expression = new Expression(toParse);
        expression.withValues(VALUE_MAP);

        try {
            return expression.evaluate();
        } catch (Exception e) {
            EteirnumToolkit.instance.getLogger().log(Level.WARNING, "Failed to parse: " + toParse, e);
            return EvaluationValue.booleanValue(false);
        }
    }
}
