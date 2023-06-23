package org.fifpoet.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.RpcResponse;
import org.fifpoet.enumeration.SerializerCode;
import org.fifpoet.exception.SerializeException;
import org.fifpoet.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements CommonSerializer {
    // because of its concurrent unsafety, single thread hold one Kryo obj.
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)){
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            LogUtil.ERROR().error("serialization error:", e);
            throw new SerializeException("serialization error!");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
        } catch (Exception e) {
            LogUtil.ERROR().error("deserialization error:", e);
            throw new SerializeException("deserialization error!");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.KRYO.getCode();
    }
}
