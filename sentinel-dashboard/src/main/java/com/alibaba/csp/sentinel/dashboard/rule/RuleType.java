package com.alibaba.csp.sentinel.dashboard.rule;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Cong
 * @date 2023/3/31
 */
public enum RuleType {

    /**
     * 流控规则
     */
    FLOW("-flow-rules", FlowRuleEntity.class, FlowRule.class),
    /**
     * 熔断规则
     */
    DEGRADE("-degrade-rules", DegradeRuleEntity.class, DegradeRule.class),
    /**
     * 热点规则
     */
    PARAM_FLOW("-param-rules", ParamFlowRuleEntity.class, ParamFlowRule.class),
    /**
     * 系统规则
     */
    SYSTEM("-system-rules", SystemRuleEntity.class, SystemRule.class),
    /**
     * 授权规则
     */
    AUTHORITY("-authority-rules", AuthorityRuleEntity.class, AuthorityRule.class),
    /**
     * 网关流控规则
     */
    GW_FLOW("-gw-flow-rules", GatewayFlowRuleEntity.class, GatewayFlowRule.class),
    /**
     * api 分组
     */
    GW_API_GROUP("-gw-api-group-rules", ApiDefinitionEntity.class, null);

    private final String name;

    private final Class<? extends RuleEntity> entityClazz;
    private final Class<?> ruleClazz;

    RuleType(String name, Class<? extends RuleEntity> entityClazz, Class<?> ruleClazz) {
        this.name = name;
        this.entityClazz = entityClazz;
        this.ruleClazz = ruleClazz;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    public <T extends RuleEntity> Class<T> getEntityClazz() {
        return (Class<T>) entityClazz;
    }

    @SuppressWarnings("unchecked")
    public <T extends RuleEntity> Class<T> getRuleClazz() {
        return (Class<T>) ruleClazz;
    }

    public Object toRuleEntity(String app, String ip, int port, List rules) {
        // 获取from..Rule 方法
        Method method = Arrays.stream(this.getEntityClazz().getMethods())
                .filter(m -> m.getName().startsWith("from") && m.getName().endsWith("Rule") && m.getReturnType() == this.getEntityClazz())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未在" + this.getEntityClazz().getName() + " 中找到from..Rule方法"));

        return rules.stream().map(r -> {
            try {
                return method.invoke(app, ip, port, r);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }


}
