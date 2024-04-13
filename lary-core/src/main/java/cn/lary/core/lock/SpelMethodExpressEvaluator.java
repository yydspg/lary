package cn.lary.core.lock;

import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author paul 2024/4/13
 */

public class SpelMethodExpressEvaluator implements MethodExpressEvaluator, EmbeddedValueResolverAware, BeanFactoryAware {
    private static final MapAccessor MAP_ACCESSOR = new MapAccessor();
    private final Map<String, Expression> expressionCache = new ConcurrentReferenceHashMap<>(16);
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private BeanResolver beanResolver;
    @Setter
    private StringValueResolver embeddedValueResolver;

    @Override
    public <T> T getV(Method method, Object[] args, String express, Class<T> resultType) {
        EvaluationContext context = createEvaluationContext(method, args);
        Expression exp = parseExpress(express, expressionParser);
        return exp.getValue(context, resultType);
    }
    protected EvaluationContext createEvaluationContext(Method method, Object[] args) {
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(method, method, args, parameterNameDiscoverer);
        context.setBeanResolver(beanResolver);
        context.addPropertyAccessor(MAP_ACCESSOR);
        return context;
    }
    protected Expression parseExpress(String exp, ExpressionParser parser) {
        exp = embeddedValueResolver.resolveStringValue(exp);
        Assert.notNull(exp, "Expression must not be null: " + exp);
        return expressionCache.computeIfAbsent(exp, parser::parseExpression);
    }
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        beanResolver = new BeanFactoryResolver(beanFactory);
    }
}
