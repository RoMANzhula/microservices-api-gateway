package org.romanzhula.user_service.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequest {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
