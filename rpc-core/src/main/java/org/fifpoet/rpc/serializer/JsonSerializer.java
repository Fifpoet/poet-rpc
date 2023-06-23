package org.fifpoet.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fifpoet.entity.RpcRequest;
import org.fifpoet.enumeration.SerializerCode;
import org.fifpoet.util.LogUtil;

import java.io.IOException;

public class JsonSerializer implements CommonSerializer {

    // provide method to transform between Java Obj and Json
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            LogUtil.ERROR().error("serialize error: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if(obj instanceof RpcRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            LogUtil.ERROR().error("deserialize error: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // RpcRequest contains Object array, which can entail deserialization can't work
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for(int i = 0; i < rpcRequest.getParamTypes().length; i ++) {
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            // judge if params can be converted to paramType
            if(!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return rpcRequest;
    }


    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }
}
