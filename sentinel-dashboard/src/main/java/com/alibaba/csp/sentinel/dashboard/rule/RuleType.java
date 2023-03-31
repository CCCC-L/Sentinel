package com.alibaba.csp.sentinel.dashboard.rule;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;

/**
 * @author Cong
 * @date 2023/3/31
 */
public enum RuleType {

    /**
     * 流控规则
     */
    FLOW("-flow-rules", FlowRuleEntity.class),
    /**
     * 熔断规则
     */
    DEGRADE("-degrade-rules", DegradeRuleEntity.class),
    /**
     * 热点规则
     */
    PARAM_FLOW("-param-rules", ParamFlowRuleEntity.class),
    /**
     * 系统规则
     */
    SYSTEM("-system-rules", SystemRuleEntity.class),
    /**
     * 授权规则
     */
    AUTHORITY("-authority-rules", AuthorityRuleEntity.class),
    /**
     * 网关流控规则
     */
    GW_FLOW("-gw-flow-rules", GatewayFlowRuleEntity.class),
    /**
     * api 分组
     */
    GW_API_GROUP("-gw-api-group-rules", ApiDefinitionEntity.class);

    private final String name;

    private final Class<? extends RuleEntity> clazz;

    RuleType(String name, Class<? extends RuleEntity> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    public <T extends RuleEntity> Class<T> getClazz() {
        return (Class<T>) clazz;
    }

}
