package com.exampleJWT.demoJWT.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private String userId;
    private String name;
    private String email;
//    private String password; // Thêm thuộc tính password
}

