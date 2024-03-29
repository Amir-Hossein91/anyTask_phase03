package com.example.phase_03.validator;

import com.example.phase_03.exceptions.InvalidImageException;
import com.example.phase_03.utility.Constants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URLConnection;
import java.nio.file.Files;

public class ImageValidator implements ConstraintValidator<Image, byte[]> {

    @Override
    public void initialize(Image constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(byte[] image, ConstraintValidatorContext constraintValidatorContext) {

        InputStream inputStream = new ByteArrayInputStream(image);
        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(inputStream);
            if(!mimeType.equals("image/jpeg"))
                return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            if (image.length > 307200)
                return false;
            return true;
    }
}

