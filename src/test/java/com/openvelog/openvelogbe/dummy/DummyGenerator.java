package com.openvelog.openvelogbe.dummy;

import java.util.List;

public interface DummyGenerator<T> {
    T generateDummyOfThis();

    List<T> generateDummiesOfThis(long dummyCount);
}
