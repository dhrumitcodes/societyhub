package com.societyhub.api.joinrequest.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateJoinRequest {

    @Size(max = 500, message = "Message must be 500 characters or fewer")
    private String message;
}