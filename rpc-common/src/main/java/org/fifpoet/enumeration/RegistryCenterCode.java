package org.fifpoet.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * service registry type
 */
@Getter
@AllArgsConstructor
public enum RegistryCenterCode {
    DEFAULT(0),
    NACOS(0);

    private final int code;
}
