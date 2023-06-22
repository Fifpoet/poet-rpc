package org.fifpoet.api;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class HelloParam implements Serializable {
    private Integer id;
    private String message;
}
