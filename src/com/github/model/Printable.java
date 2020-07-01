package com.github.model;

import java.io.IOException;

public interface Printable {
    <T> void printToPdf(T... o) throws IOException;
}
