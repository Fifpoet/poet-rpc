package org.fifpoet.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * the Serializer's identity
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(0),
    JSON(1),
    PROTOBUF(2);

    private final int code;

}
