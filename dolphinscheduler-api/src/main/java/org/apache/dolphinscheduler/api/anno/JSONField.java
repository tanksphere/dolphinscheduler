
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.api.anno;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface JSONField {
    int ordinal() default 0;

    String name() default "";

    String format() default "";

    boolean serialize() default true;

    boolean deserialize() default true;

//    SerializerFeature[] serialzeFeatures() default {};
//
//    Feature[] parseFeatures() default {};

    String label() default "";

    boolean jsonDirect() default false;

    Class<?> serializeUsing() default Void.class;

    Class<?> deserializeUsing() default Void.class;

    String[] alternateNames() default {};

    boolean unwrapped() default false;

    String defaultValue() default "";
}
