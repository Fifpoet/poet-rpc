package org.fifpoet.test;

import org.fifpoet.api.HelloParam;
import org.fifpoet.api.HelloResult;
import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.annotation.RpcService;

@RpcService(impl = "second")
public class SecondImpl implements HelloService {
    @Override
    public HelloResult hello(HelloParam param) {
        return new HelloResult(2, "Second implement of HelloService!");
    }
}
