<div align="center">

# 🚀 DependencyInjector

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![Maven Central](https://img.shields.io/badge/Maven-1.0.0-green.svg)](https://repo.neziw.ovh/releases/)

</div>

> Lightweight, simple, and modern dependency injection framework for Java featuring constructor-based injection and automatic post-construction method invocation

---

## 📋 Table of Contents

- [Features](#-features)
- [Requirements](#-requirements)
- [Why Field-Based Injection is Bad?](#-why-field-based-injection-is-bad)
- [Installation](#-installation)
- [Quick Start](#-quick-start)
- [Configuration](#-configuration)
- [API Reference](#-api-reference)
- [Annotations](#-annotations)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

- 🔌 **Constructor Injection** - Simple and intuitive constructor-based dependency injection
- 🎯 **Annotation-Driven** - Clean and declarative `@Inject` and `@PostConstruct` annotations
- 🚀 **Lightweight** - Minimal dependencies, zero runtime overhead
- 🔧 **Easy Integration** - Simple API, easy to integrate into any Java project
- 🛡️ **Type-Safe** - Compile-time type safety with Java generics
- 📝 **Post-Construction** - Automatic invocation of `@PostConstruct` methods after object creation
- 🎨 **Clean Code** - Encourages clean, testable, and maintainable code architecture
- ⚡ **Fast** - Reflection-based implementation with minimal overhead

---

## 📦 Requirements

- **Java**: 17 or higher
- **Build Tool**: Maven or Gradle (for dependency management)

---

## ❌ Why Field-Based Injection is Bad?

This framework intentionally supports **only constructor-based injection** and does not provide field-based injection. This is a deliberate design decision based on best practices and software engineering principles. Here's why field-based injection is problematic and why constructor injection is the superior approach:

### 🔴 Problems with Field-Based Injection

#### 1. **Immutability and Final Fields**

Field-based injection requires non-final fields, making your objects mutable and potentially leaving them in an inconsistent state:

```java
// ❌ BAD: Field injection
public class UserService {
    @Inject  // Field must be non-final
    private DatabaseService databaseService;  // Can be null, can be changed

    public void saveUser(User user) {
        // What if databaseService is null? No way to enforce it at compile time
        this.databaseService.save(user);
    }
}
```

With constructor injection, you can use `final` fields, ensuring immutability and thread-safety:

```java

// ✅ GOOD: Constructor injection
public class UserService {

    private final DatabaseService databaseService;  // Final, immutable, thread-safe
    @Inject
    public UserService(final DatabaseService databaseService) {
        this.databaseService = databaseService;  // Guaranteed to be non-null
    }
    
    public void saveUser(User user) {
        this.databaseService.save(user);  // Always available
    }
}
```

#### 2. **Testability Issues**

Field-based injection makes unit testing significantly more difficult. You must use reflection or rely on the dependency injection framework even in tests:

```java
// ❌ BAD: Testing with field injection
public class UserServiceTest {
    @Test
    void testSaveUser() {
        UserService userService = new UserService();  // databaseService is null!
        // Must use reflection or a mock framework to inject
        // Reflection.setField(userService, "databaseService", mockDatabase);
        // This is error-prone and fragile
    }
}
```

Constructor injection makes testing straightforward and explicit:
```java
// ✅ GOOD: Testing with constructor injection
public class UserServiceTest {

    @Test
    void testSaveUser() {
        DatabaseService mockDatabase = mock(DatabaseService.class);
        UserService userService = new UserService(mockDatabase);  // Clean and explicit
        // Test implementation
    }
}

```

#### 3. **Hidden Dependencies**

Field-based injection hides dependencies. When you look at a class, you cannot immediately see what dependencies it requires without examining annotations and fields. This makes code harder to understand and maintain:

```java
// ❌ BAD: Hidden dependencies
public class OrderService {
    @Inject
    private PaymentService paymentService;  // Hidden dependency
    @Inject
    private ShippingService shippingService;  // Hidden dependency
    @Inject

    private EmailService emailService;  // Hidden dependency
    // Looking at this class, it's not immediately clear what dependencies are needed
    // Must scan all fields to understand the class dependencies
}
```

Constructor injection makes dependencies explicit and visible:

```java
// ✅ GOOD: Explicit dependencies
public class OrderService {
    private final PaymentService paymentService;
    private final ShippingService shippingService;
    private final EmailService emailService;

    @Inject
    public OrderService(
        final PaymentService paymentService,
        final ShippingService shippingService,
        final EmailService emailService
    ) {
        this.paymentService = paymentService;
        this.shippingService = shippingService;
        this.emailService = emailService;
    }
    // Dependencies are immediately visible in the constructor signature
    // Easy to understand what this class needs
}

```

#### 4. **Circular Dependency Detection**

Field-based injection can hide circular dependencies until runtime, making them harder to detect and debug. Constructor injection exposes circular dependencies immediately, forcing you to resolve them during design:

```java
// ❌ BAD: Circular dependency hidden with field injection
public class ServiceA {

    @Inject
    private ServiceB serviceB;  // Circular dependency not obvious

}

public class ServiceB {

    @Inject
    private ServiceA serviceA;  // Circular dependency not obvious

}
// This might work at runtime but creates tight coupling and design issues
```

Constructor injection makes circular dependencies impossible, encouraging better design:

```java
// ✅ GOOD: Circular dependency impossible with constructor injection
// If you try to create ServiceA, you need ServiceB
// If you try to create ServiceB, you need ServiceA
// This immediately reveals the design problem and forces you to refactor
```

#### 5. **Null Safety**

Field-based injection can leave objects in an invalid state where required dependencies are `null`. There's no compile-time guarantee that dependencies are injected. Constructor injection ensures that all required dependencies are provided before the object is created:

```java
// ❌ BAD: Null safety issues
public class UserService {

    @Inject
    private DatabaseService databaseService;  // Could be null!

    public void saveUser(User user) {
        // Runtime NullPointerException if injection failed
        this.databaseService.save(user);
    }
}
```

Constructor injection guarantees non-null dependencies:

```java

// ✅ GOOD: Null safety guaranteed
public class UserService {

    private final DatabaseService databaseService;  // Final, guaranteed non-null

    @Inject
    public UserService(final DatabaseService databaseService) {
        // Compiler and framework ensure this is never null
        this.databaseService = Objects.requireNonNull(databaseService);
    }
}
```

#### 6. **Framework Coupling**

Field-based injection tightly couples your code to the dependency injection framework. Your classes cannot be instantiated without the framework, making them harder to reuse and test. Constructor injection allows classes to be instantiated normally, with or without the framework:

```java
// ❌ BAD: Tight framework coupling
public class UserService {
    @Inject
    private DatabaseService databaseService;
    // Cannot create UserService without the DI framework
    // Must use reflection or framework-specific mechanisms
}
```

Constructor injection provides flexibility:

```java
// ✅ GOOD: Framework-agnostic
public class UserService {

    private final DatabaseService databaseService;

    @Inject  // Optional: Framework can use this
    public UserService(final DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    // Can still be created normally: new UserService(databaseService)
    // Framework is optional, not required
}

```

#### 7. **Order of Initialization**

Field-based injection makes the order of initialization unclear. Dependencies might be injected in an unpredictable order, leading to initialization issues. Constructor injection ensures a clear, predictable initialization order:

```java
// ❌ BAD: Unclear initialization order
public class ServiceA {

    @Inject
    private ServiceB serviceB;

    @PostConstruct
    void init() {
        // Is serviceB injected before this runs? Unclear
        this.serviceB.doSomething();
    }
}
```

Constructor injection provides a clear sequence: constructor → field assignment → @PostConstruct methods:

```java
// ✅ GOOD: Clear initialization order
public class ServiceA {

    private final ServiceB serviceB;

    @Inject
    public ServiceA(final ServiceB serviceB) {
        // 1. Constructor runs first
        this.serviceB = serviceB;  // 2. Fields assigned
    }

    @PostConstruct
    void init() {
        // 3. Post-construct runs last, all dependencies guaranteed available
        this.serviceB.doSomething();
    }
}
```

### ✅ Why Constructor Injection is Superior

Constructor injection provides numerous benefits that field injection cannot match:

1. **Immutability** - Enables `final` fields, ensuring objects are immutable and thread-safe
2. **Explicit Dependencies** - Dependencies are visible in the constructor signature, making code self-documenting
3. **Testability** - Easy to test without framework, just use `new MyClass(dependency)`
4. **Null Safety** - Compile-time and runtime guarantees that dependencies are never null
5. **Framework Independence** - Classes can be instantiated without the DI framework
6. **Clear Initialization** - Predictable order: constructor → fields → @PostConstruct
7. **Better Design** - Forces you to think about dependencies and prevents circular dependencies
8. **Compile-Time Safety** - Missing dependencies are caught early, not at runtime

### 🎯 Design Decision

**DependencyInjector** deliberately supports only constructor-based injection to encourage best practices and help developers write better, more maintainable code. By removing the option of field injection, we ensure that your code benefits from all the advantages listed above.

This design philosophy aligns with recommendations from the Java community, including frameworks like Spring (which recommends constructor injection as the preferred approach) and modern Java best practices.

---

## 🔧 Installation

### Maven

Add the repository and dependency to your `pom.xml`:

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

### Gradle

Add the repository and dependency to your `build.gradle` or `build.gradle.kts`:

**Kotlin DSL:**

```kotlin
repositories {
    maven {
        name = "neziw-repo"
        url = uri("https://repo.neziw.ovh/releases")
    }
}
dependencies {
    implementation("ovh.neziw:DependencyInjector:1.0.0")
}

```

**Groovy DSL:**

```groovy
repositories {
    maven {
        name "neziw-repo"
        url "https://repo.neziw.ovh/releases"
    }
}
dependencies {
    implementation "ovh.neziw:DependencyInjector:1.0.0"
}

```

---

## 🚀 Quick Start

### Basic Example

```java

import ovh.neziw.injector.Injector;
import ovh.neziw.injector.Inject;
import ovh.neziw.injector.PostConstruct;

// 1. Create an Injector instance
final Injector injector = new Injector();
// 2. Bind your dependencies
injector.bind(FirstService.class, new FirstService());
injector.bind(SecondService.class, new SecondService());
// 3. Create instances with automatic injection
final MyClass myClass = injector.createInstance(MyClass.class);
myClass.sendMessages();

```

**Service Classes:**

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

**Class with Dependencies:**

```java

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

**Output:**

```
Example PostConstruct method called
Sending something from FirstService
This is the second service message!
```

---

## ⚙️ Configuration

### Injector Setup

The `Injector` class is the central component of the framework. It manages dependency bindings and creates instances with dependency injection.

```java
final Injector injector = new Injector();
```

### Binding Dependencies

Bind dependencies before creating instances that require them:

```java

injector.bind(ServiceInterface.class, new ServiceImplementation());
injector.bind(AnotherService.class, new AnotherService());

```

### Creating Instances

Create instances with automatic dependency injection:

```java
final MyClass instance = injector.createInstance(MyClass.class);
```

---

## 🔍 API Reference

### Injector Class

#### Methods

| Method                     | Description                                   | Parameters                                                 | Returns                   |
|----------------------------|-----------------------------------------------|------------------------------------------------------------|---------------------------|
| `bind(Class<T>, T)`        | Binds a type to an instance                   | `type`: The class type<br>`instance`: The instance to bind | `void`                    |
| `createInstance(Class<T>)` | Creates an instance with dependency injection | `clazz`: The class to instantiate                          | `T`: The created instance |

#### Binding Dependencies

```java
<T> void bind(final Class<T> type, final T instance)

```

Binds a type to a specific instance. When creating instances that require this type, the bound instance will be injected.

**Example:**

```java
injector.bind(UserService.class, new UserService());

```

#### Creating Instances

```java
<T> T createInstance(final Class<T> clazz)

```

Creates an instance of the specified class with automatic dependency injection. The class must have a constructor annotated with `@Inject`.

**Example:**

```java
final MyClass instance = injector.createInstance(MyClass.class);
```

**Throws:**

- `InjectException` if no `@Inject` constructor is found
- `InjectException` if a required dependency is not bound
- `InjectException` if instantiation fails

---

## 🏷️ Annotations

### @Inject

Marks a constructor for dependency injection. Only one constructor per class should be annotated with `@Inject`.

**Target:** Constructor
**Retention:** Runtime

**Example:**

```java
@Inject
public MyClass(final ServiceA serviceA, final ServiceB serviceB) {
    this.serviceA = serviceA;
    this.serviceB = serviceB;
}

```

**Requirements:**

- Only one constructor per class can be annotated with `@Inject`
- All constructor parameters must be bound before creating instances
- Constructor must be accessible

### @PostConstruct

Marks a method to be automatically invoked after object construction and dependency injection. Multiple methods can be annotated with `@PostConstruct`.

**Target:** Method
**Retention:** Runtime

**Example:**

```java
@PostConstruct
void initialize() {
    // Initialization code here
    System.out.println("Object initialized");

}
```

**Requirements:**

- Method must be accessible (public, protected, or package-private)
- Method should not have parameters
- Method can return any type (return value is ignored)

---

## 🔧 Advanced Usage

### Custom Exception Handling

```java
try {
    final MyClass instance = injector.createInstance(MyClass.class);
} catch (final InjectException e) {
    logger.error("Failed to create instance", e);
    // Custom error handling
    throw new ApplicationException("Initialization failed", e);
}

```

### Conditional Binding

```java
final Injector injector = new Injector();
if (useProductionDatabase) {
    injector.bind(DatabaseService.class, new ProductionDatabaseService());
} else {
    injector.bind(DatabaseService.class, new DevelopmentDatabaseService());
}

```

### Factory Pattern Integration

```java

public class ServiceFactory {

    private final Injector injector;

    public ServiceFactory() {
        this.injector = new Injector();
        this.setupBindings();
    }

    private void setupBindings() {
        injector.bind(ConfigService.class, new ConfigService());
        injector.bind(DatabaseService.class, new DatabaseService());

    }

    public <T> T create(final Class<T> clazz) {
        return this.injector.createInstance(clazz);
    }
}

```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Built with Java Reflection API for dependency injection
- Inspired by modern dependency injection frameworks
- Designed for simplicity and ease of use

---

## 📞 Support

If you encounter any issues or have questions, please open an issue on the GitHub repository.

---

**Made with ❤️ by [neziw](https://github.com/neziw)**

