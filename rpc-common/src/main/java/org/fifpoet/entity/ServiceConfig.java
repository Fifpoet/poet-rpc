package org.fifpoet.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceConfig {

    private String version;
    /**
     * the implementation object
     */
    private Object service;
    /**
     * more than one impl, designate one
     */
    private String impl;

}
