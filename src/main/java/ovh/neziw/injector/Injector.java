/*
 * This file is part of "DependencyInjector", licensed under MIT License.
 *
 *  Copyright (c) 2024 neziw
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package ovh.neziw.injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Injector {

    private final Map<Class<?>, Object> bindings = new HashMap<>();

    public <T> void bind(final Class<T> type, final T instance) {
        this.bindings.put(type, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T createInstance(final Class<T> clazz) {
        try {
            Constructor<?> injectConstructor = null;
            for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                if (constructor.isAnnotationPresent(Inject.class)) {
                    injectConstructor = constructor;
                    break;
                }
            }
            if (injectConstructor == null) {
                throw new InjectException("No constructor annotated with @Inject found for class: " + clazz.getName());
            }
            final Class<?>[] parameterTypes = injectConstructor.getParameterTypes();
            final Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                final Object dependency = this.bindings.get(parameterTypes[i]);
                if (dependency == null) {
                    throw new InjectException("Missing dependency for parameter: " + parameterTypes[i].getName() + " in class: " + clazz.getName());
                }
                parameters[i] = dependency;
            }

            final T instance = (T) injectConstructor.newInstance(parameters);
            for (final Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    method.setAccessible(true);
                    method.invoke(instance);
                }
            }
            return instance;
        } catch (final Exception exception) {
            throw new InjectException("Failed to create instance of class: " + clazz.getName(), exception);
        }
    }
}