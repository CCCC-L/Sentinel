package com.alibaba.csp.sentinel.dashboard.aop;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AbstractRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.client.SentinelApiNacosClient;
import com.alibaba.csp.sentinel.dashboard.rule.RuleType;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Cong
 * @date 2023/3/31
 */
@Aspect
@Component
public class SentinelApiClientAspect {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(new NamedThreadFactory("sentinel-dashboard-api-aspect"));

    private static final Logger LOG = LoggerFactory.getLogger(SentinelApiClientAspect.class);

    @Resource
    SentinelApiNacosClient nacosClient;

    /**
     * 读 - 限流规则
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchFlowRuleOfMachine(..))")
    public void fetchFlowRuleOfMachinePointcut() {

    }

    @Around("fetchFlowRuleOfMachinePointcut()")
    public Object fetchFlowRuleOfMachine(final ProceedingJoinPoint pjp) {
        return fetchRules(pjp, RuleType.FLOW);
    }

    /**
     * 写 - 限流规则
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setFlowRuleOfMachineAsync(..))")
    public void setFlowRuleOfMachineAsyncPointcut() {

    }

    @Around("setFlowRuleOfMachineAsyncPointcut()")
    public Object setFlowRuleOfMachineAsync(final ProceedingJoinPoint pjp) {
        return publishRulesWithCompletableFuture(pjp, RuleType.FLOW);
    }

    /**
     * 读 - 降级
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchDegradeRuleOfMachine(..))")
    public void fetchDegradeRuleOfMachinePointcut() {

    }

    @Around("fetchDegradeRuleOfMachinePointcut()")
    public Object fetchDegradeRuleOfMachine(ProceedingJoinPoint pjp) {
        return fetchRules(pjp, RuleType.DEGRADE);
    }

    /**
     * 写 - 降级
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setDegradeRuleOfMachine(..))")
    public void setDegradeRuleOfMachinePointcut() {

    }

    @Around("setDegradeRuleOfMachinePointcut()")
    public boolean setDegradeRuleOfMachine(ProceedingJoinPoint pjp) {
        return publishRules(pjp, RuleType.DEGRADE);
    }

    /**
     * 读 - 热点规则
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchParamFlowRulesOfMachine(..))")
    public void fetchParamFlowRulesOfMachinePointcut() {

    }

    @Around("fetchParamFlowRulesOfMachinePointcut()")
    public Object fetchParamFlowRulesOfMachine(final ProceedingJoinPoint pjp) {
        return fetchRulesWithCompletableFuture(pjp, RuleType.PARAM_FLOW);
    }

    /**
     * 写 - 热点规则
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setParamFlowRuleOfMachine(..))")
    public void setParamFlowRuleOfMachinePointcut() {

    }

    @Around("setParamFlowRuleOfMachinePointcut()")
    public Object setParamFlowRuleOfMachine(final ProceedingJoinPoint pjp) {
        return publishRulesWithCompletableFuture(pjp, RuleType.PARAM_FLOW);
    }

    /**
     * 读 - 系统
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchSystemRuleOfMachine(..))")
    public void fetchSystemRuleOfMachinePointcut() {

    }

    @Around("fetchSystemRuleOfMachinePointcut()")
    public Object fetchSystemRuleOfMachine(ProceedingJoinPoint pjp) {
        return fetchRules(pjp, RuleType.SYSTEM);
    }

    /**
     * 写 - 系统
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setSystemRuleOfMachine(..))")
    public void setSystemRuleOfMachinePointcut() {

    }

    @Around("setSystemRuleOfMachinePointcut()")
    public boolean setSystemRuleOfMachine(ProceedingJoinPoint pjp) {
        return publishRules(pjp, RuleType.SYSTEM);
    }

    /**
     * 读 - 授权规则
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchAuthorityRulesOfMachine(..))")
    public void fetchAuthorityRulesOfMachinePointcut() {

    }

    @Around("fetchAuthorityRulesOfMachinePointcut()")
    public Object fetchAuthorityRulesOfMachine(final ProceedingJoinPoint pjp) {
        return fetchRules(pjp, RuleType.AUTHORITY);
    }

    /**
     * 写 - 授权规则
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setAuthorityRuleOfMachine(..))")
    public void setAuthorityRuleOfMachinePointcut() {

    }

    @Around("setAuthorityRuleOfMachinePointcut()")
    public Object setAuthorityRuleOfMachine(final ProceedingJoinPoint pjp) {
        return publishRules(pjp, RuleType.AUTHORITY);
    }

    /**
     * 读 - 网关 API
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchApis(..))")
    public void fetchApisPointcut() {

    }

    @Around("fetchApisPointcut()")
    public Object fetchApis(final ProceedingJoinPoint pjp) {
        return fetchRulesWithCompletableFuture(pjp, RuleType.GW_API_GROUP);
    }

    /**
     * 写 - 网关 API
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.modifyApis(..))")
    public void modifyApisPointcut() {

    }

    @Around("modifyApisPointcut()")
    public Object modifyApisApis(final ProceedingJoinPoint pjp) {
        return publishRules(pjp, RuleType.GW_API_GROUP);
    }


    /**
     * 读 - 网关限流
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchGatewayFlowRules(..))")
    public void fetchGatewayFlowRulesPointcut() {

    }

    @Around("fetchGatewayFlowRulesPointcut()")
    public Object fetchGatewayFlowRules(final ProceedingJoinPoint pjp) {
        return fetchRulesWithCompletableFuture(pjp, RuleType.GW_FLOW);
    }

    /**
     * 写 - 网关限流
     */
    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.modifyGatewayFlowRules(..))")
    public void modifyGatewayFlowRulesPointcut() {

    }

    @Around("modifyGatewayFlowRulesPointcut()")
    public Object modifyGatewayFlowRulesApis(final ProceedingJoinPoint pjp) {
        return publishRules(pjp, RuleType.GW_FLOW);
    }


    private CompletableFuture<Object> fetchRulesWithCompletableFuture(ProceedingJoinPoint pjp, RuleType ruleType) {
        return CompletableFuture.supplyAsync(() -> fetchRules(pjp, ruleType), EXECUTOR);
    }

    private Object fetchRules(ProceedingJoinPoint pjp, RuleType ruleType) {
        String app = (String) pjp.getArgs()[0];
        String ip = (String) pjp.getArgs()[1];
        int port = (int) pjp.getArgs()[2];

        try {
            Object rules;
            switch (ruleType) {
                // 热点规则
                case PARAM_FLOW:
                    List<ParamFlowRule> paramRules = nacosClient.getRules(app, ruleType.getName(), ParamFlowRule.class);
                    rules = paramRules.stream().map(r -> ParamFlowRuleEntity.fromParamFlowRule(app, ip, port, r)).collect(Collectors.toList());
                    break;
                // 授权
                case AUTHORITY:
                    List<AuthorityRule> authorityRules = nacosClient.getRules(app, ruleType.getName(), AuthorityRule.class);
                    rules = authorityRules.stream().map(r -> AuthorityRuleEntity.fromAuthorityRule(app, ip, port, r)).collect(Collectors.toList());
                    break;
                // 网关API
                case GW_API_GROUP:
                    List<ApiDefinitionEntity> gwApiRules = nacosClient.getRules(app, ruleType.getName(), ruleType.getClazz());
                    rules = gwApiRules.stream().peek(rule -> {
                        rule.setApp(app);
                        rule.setIp(ip);
                        rule.setPort(port);
                    }).collect(Collectors.toList());
                    break;
                case GW_FLOW:
                    List<GatewayFlowRule> GatewayFlowRules = nacosClient.getRules(app, ruleType.getName(), GatewayFlowRule.class);
                    rules = GatewayFlowRules.stream().map(r -> GatewayFlowRuleEntity.fromGatewayFlowRule(app, ip, port, r)).collect(Collectors.toList());
                    break;
                default:
                    rules = nacosClient.getRules(app, ruleType.getName(), ruleType.getClazz());
            }

            return rules;
        } catch (Exception e) {
            throw new RuntimeException("fetch rules error: " + ruleType.getName(), e);
        }

    }

    private CompletableFuture<Void> publishRulesWithCompletableFuture(ProceedingJoinPoint pjp, RuleType ruleType) {
        return CompletableFuture.runAsync(() -> publishRules(pjp, ruleType), EXECUTOR);
    }

    private boolean publishRules(ProceedingJoinPoint pjp, RuleType ruleType) {
        String app = (String) pjp.getArgs()[0];
        List rules = (List) pjp.getArgs()[3];

        try {
            switch (ruleType) {
                case AUTHORITY:
                case PARAM_FLOW:
                    rules = (List) rules.stream().map(rule -> ((AbstractRuleEntity) rule).getRule()).collect(Collectors.toList());
                    break;
                case GW_API_GROUP:
                    rules = (List) rules.stream().map(rule -> ((ApiDefinitionEntity) rule).toApiDefinition()).collect(Collectors.toList());
                    break;
                case GW_FLOW:
                    rules = (List) rules.stream().map(rule -> ((GatewayFlowRuleEntity) rule).toGatewayFlowRule()).collect(Collectors.toList());
                    break;
                default:
                    break;
            }

            nacosClient.publish(app, rules, ruleType.getName());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("push rules error: " + ruleType.getName(), e);
        }
    }

}
