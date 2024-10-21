# DependencyInjector

A lightweight and easy-to-use dependency injection framework in Java, featuring constructor-based injection and automatic post-construction method invocation with `@Inject` and `@PostConstruct` annotations.

### Example Usage:
```java
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
```

```java
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
```

```java
public class FirstService {

    public void doSomething() {
        System.out.println("Sending something from FirstService");
    }
}

public class SecondService {

    public String getSecondServiceMessage() {
        return "This is the second service message!";
    }
}
```

**Output**:
```
> Example PostConstruct method called
> Sending something from FirstService
> This is the second service message!
```

### Adding dependency

**Maven:**
```xml
<repositories>
    <repository>
        <id>neziw-repo</id>
        <url>https://repo.neziw.ovh/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>ovh.neziw</groupId>
        <artifactId>DependencyInjector</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

**Gradle:**
```groovy
repositories {
    maven {
        name "neziw-repo"
        url "https://repo.neziw.ovh/releases"
    }
}

implementation "ovh.neziw:DependencyInjector:1.0.0"
```

---
Special thanks to [JetBrains](https://www.jetbrains.com/products/) company for providing development tools used to develop this project.

[<img src="https://user-images.githubusercontent.com/65517973/210912946-447a6b9a-2685-4796-9482-a44bffc727ce.png" alt="JetBrains" width="150">](https://www.jetbrains.com)