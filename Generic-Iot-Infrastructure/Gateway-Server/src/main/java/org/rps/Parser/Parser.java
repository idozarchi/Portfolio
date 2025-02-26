package org.rps.Parser;

public interface Parser <T, E> {
    public T parse(E element);
}
