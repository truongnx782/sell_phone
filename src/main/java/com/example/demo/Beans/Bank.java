package com.example.demo.Beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bank {
    private int id;
    private String name;
    private String code;
    private String bin;
    private String shortName;
    private String logo;
    private int transferSupported;
    private int lookupSupported;
}
