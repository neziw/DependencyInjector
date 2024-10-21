package com.github.neziw.app;

import ovh.neziw.injector.Inject;
import ovh.neziw.injector.PostConstruct;

public class MyClass {

    private final FirstService firstService;
    private final SecondService secondService;

    @Inject
    public MyClass(final FirstService firstService, final SecondService secondService) {
        this.firstService = firstService;
        this.secondService = secondService;
    }

    @PostConstruct
    void init() {
        System.out.println("Example PostConstruct method called");
    }

    public void sendMessages() {
        this.firstService.doSomething();
        System.out.println(this.secondService.getSecondServiceMessage());
    }
}