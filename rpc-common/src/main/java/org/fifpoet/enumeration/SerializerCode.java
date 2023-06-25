package org.fifpoet.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * serializer code
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {
    DEFAULT(0),
    KRYO(0),
    JSON(1),
    PROTOBUF(2),
    ;

    private final int code;

}
