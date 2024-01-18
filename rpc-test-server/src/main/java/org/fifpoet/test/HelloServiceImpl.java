package org.fifpoet.test;

import org.fifpoet.api.HelloParam;
import org.fifpoet.api.HelloResult;
import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.annotation.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RpcService(group = "MY_RPC")
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public HelloResult hello(HelloParam param) {
        logger.info("receive message：{}", param.getMessage());
        return new HelloResult(999, "return: " + param.getId());
    }

}
