package com.github.neziw.app;

import ovh.neziw.injector.Injector;

public final class MyApplication {

    public static void main(final String[] args) {
        final Injector injector = new Injector();
        injector.bind(FirstService.class, new FirstService());
        injector.bind(SecondService.class, new SecondService());

        final MyClass myClass = injector.createInstance(MyClass.class);
        myClass.sendMessages();
    }
}