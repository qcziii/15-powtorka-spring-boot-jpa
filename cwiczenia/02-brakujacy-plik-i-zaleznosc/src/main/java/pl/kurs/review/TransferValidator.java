package pl.kurs.review;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;
import java.util.TreeSet;

/** Validates transfer orders with Bean Validation (without Spring). */
public class TransferValidator {

    private final Validator validator;

    public TransferValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /** Names of the invalid fields, sorted. Empty set = the order is valid. */
    public Set<String> invalidFields(TransferRequest request) {
        Set<String> fields = new TreeSet<>();
        for (ConstraintViolation<TransferRequest> v : validator.validate(request)) {
            fields.add(v.getPropertyPath().toString());
        }
        return fields;
    }
}
