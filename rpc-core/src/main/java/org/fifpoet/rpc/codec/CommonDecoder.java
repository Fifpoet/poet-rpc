package org.fifpoet.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.fifpoet.util.LogUtil;
import org.slf4j.LoggerFactory;
import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.RpcResponse;
import org.fifpoet.enumeration.PackageType;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.rpc.serializer.CommonSerializer;

import java.util.List;

/**
 * codec -> coder & decoder
 * common decoder extends from netty
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 */
public class CommonDecoder extends ReplayingDecoder<Object> {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magic = in.readInt();
        if(magic != MAGIC_NUMBER) {
            LogUtil.ERROR().error(RpcErrorCode.UNKNOWN_PROTOCOL.getMessage() + ": {}", magic);
            throw new RpcException(RpcErrorCode.UNKNOWN_PROTOCOL);
        }
        // read pack type
        int packageCode = in.readInt();
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if(packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            LogUtil.ERROR().error(RpcErrorCode.UNKNOWN_PACKAGE_TYPE.getMessage() + ": {}", packageCode);
            throw new RpcException(RpcErrorCode.UNKNOWN_PACKAGE_TYPE);
        }
        // get specific serializer
        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null) {
            LogUtil.ERROR().error(RpcErrorCode.UNKNOWN_SERIALIZER.getMessage() + ": {}", serializerCode);
            throw new RpcException(RpcErrorCode.UNKNOWN_SERIALIZER);
        }
        // read bytes then convert to obj
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object obj = serializer.deserialize(bytes, packageClass);
        out.add(obj);
    }

}
