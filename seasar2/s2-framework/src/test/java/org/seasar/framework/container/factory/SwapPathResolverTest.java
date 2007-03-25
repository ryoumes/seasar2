/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.framework.container.factory;

import junit.framework.TestCase;

import org.seasar.framework.container.S2Container;

/**
 * @author taedium
 * 
 */
public class SwapPathResolverTest extends TestCase {

    protected void tearDown() throws Exception {
        S2ContainerFactory.destroy();
    }

    public void test() throws Exception {
        String path = getClass().getName().replace('.', '/') + ".dicon";
        S2ContainerFactory.configure(path);
        S2Container container = S2ContainerFactory.create("noExists.dicon");
        assertNotNull(container);
    }
}