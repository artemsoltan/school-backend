package com.school.service;

import com.school.util.ErrorModel;
import org.springframework.stereotype.Service;

@Service
public class ErrorService {
    public ErrorModel generateError(String status, String message) {
        return new ErrorModel(status, message);
    }
}