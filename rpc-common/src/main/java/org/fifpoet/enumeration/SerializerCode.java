package org.fifpoet.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * the Serializer's identity
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {

    JSON(1);

    private final int code;

}
