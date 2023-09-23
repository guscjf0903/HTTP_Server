package org.share;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    SUCCESS("HTTP/1.1 200 OK\r\n"),
    SUCCESS_NOCONTENT("HTTP/1.1 204 No Content\r\n"),
    SUCCESS_CREATED("HTTP/1.1 201 Created\r\n"),
    FAIL("HTTP/1.1 404 Not Found\r\n");

    private final String value;
}
