package org.fifpoet.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Endpoint {

    private String group;
    /**
     * the implementation object
     */
    private Object stub;
    private String host;
    private int port;

}
