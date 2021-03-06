/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.framework.container.external.servlet;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;

import org.seasar.framework.container.external.RebuildableExternalContextMap;
import org.seasar.framework.util.AssertionUtil;
import org.seasar.framework.util.EnumerationIterator;

/**
 * {@link ServletContext}に {@link Map}としてアクセスするためのクラスです。
 * 
 * @author shot
 * @author higa
 */
public class ServletApplicationMap extends RebuildableExternalContextMap {

    private ServletContext context;

    /**
     * {@link ServletApplicationMap}を作成します。
     * 
     * @param context
     */
    public ServletApplicationMap(ServletContext context) {
        AssertionUtil.assertNotNull("context is null.", context);
        this.context = context;
    }

    protected Object getAttribute(String key) {
        return context.getAttribute(key);
    }

    protected Iterator getAttributeNames() {
        return new EnumerationIterator(context.getAttributeNames());
    }

    protected void removeAttribute(String key) {
        context.removeAttribute(key);
    }

    protected void setAttribute(String key, Object value) {
        context.setAttribute(key, value);
    }
}