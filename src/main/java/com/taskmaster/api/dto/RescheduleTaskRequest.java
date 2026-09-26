package com.taskmaster.api.dto;

import java.time.LocalDate;

public record RescheduleTaskRequest(
        LocalDate newDueDate
) {
}
