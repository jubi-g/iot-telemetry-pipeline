package com.itp.api_service.api.v1.validation;

import com.itp.api_service.api.v1.model.HasTimeRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, HasTimeRange> {
    private long maxHours;

    @Override
    public void initialize(ValidTimeRange constraintAnnotation) {
        this.maxHours = constraintAnnotation.maxHours();
    }

    @Override
    public boolean isValid(HasTimeRange req, ConstraintValidatorContext ctx) {
        if (req == null || req.from() == null || req.to() == null) return true;
        var from = req.from(); var to = req.to();

        boolean ok = true;
        ctx.disableDefaultConstraintViolation();
        if (!from.isBefore(to)) {
            ctx.buildConstraintViolationWithTemplate("'from' must be before 'to'")
                .addPropertyNode("from").addConstraintViolation();
            ok = false;
        }
        var hours = Duration.between(from, to).toHours();
        if (hours > maxHours) {
            ctx.buildConstraintViolationWithTemplate("time window must be ≤ " + maxHours + " hours")
                .addPropertyNode("to").addConstraintViolation();
            ok = false;
        }
        return ok;
    }
}
