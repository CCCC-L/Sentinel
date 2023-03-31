package com.alibaba.csp.sentinel.dashboard.client;

import com.alibaba.csp.sentinel.dashboard.config.NacosConfigUtil;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cong
 * @date 2023/3/30
 */
@Component
public class SentinelApiNacosClient {

    @Autowired
    private ConfigService configService;


    public <T> List<T> getRules(String appName, String dataIdPostfix, Class<T> tClass) throws Exception {
        String rules = configService.getConfig(appName + dataIdPostfix + NacosConfigUtil.FILE_TYPE,
                NacosConfigUtil.GROUP_ID, 3000);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }

        return JSON.parseArray(rules, tClass);
    }

    public void publish(String app, List rules, String dataIdPostFix) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }

        configService.publishConfig(app + dataIdPostFix + NacosConfigUtil.FILE_TYPE,
                NacosConfigUtil.GROUP_ID, JSON.toJSONString(rules));
    }

}
