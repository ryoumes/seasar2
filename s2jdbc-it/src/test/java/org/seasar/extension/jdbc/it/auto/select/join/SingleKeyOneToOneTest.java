/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.extension.jdbc.it.auto.select.join;

import java.util.List;

import org.junit.runner.RunWith;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.JoinType;
import org.seasar.extension.jdbc.it.entity.Address;
import org.seasar.extension.jdbc.it.entity.Employee;
import org.seasar.framework.unit.Seasar2;

import static junit.framework.Assert.*;

/**
 * @author taedium
 * 
 */
@RunWith(Seasar2.class)
public class SingleKeyOneToOneTest {

    private JdbcManager jdbcManager;

    /**
     * 
     * @throws Exception
     */
    public void testLeftOuterJoin_fetch_fromOwnerToInverse() throws Exception {
        List<Employee> list =
            jdbcManager.from(Employee.class).leftOuterJoin("address").getResultList();
        assertEquals(14, list.size());
        for (Employee e : list) {
            assertNotNull(e.address);
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testLeftOuterJoin_fromOwnerToInverse() throws Exception {
        List<Employee> list =
            jdbcManager
                .from(Employee.class)
                .leftOuterJoin("address", false)
                .getResultList();
        assertEquals(14, list.size());
        for (Employee e : list) {
            assertNull(e.address);
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testInnerJoin_fetch_fromOwnerToInverse() throws Exception {
        List<Employee> list =
            jdbcManager
                .from(Employee.class)
                .join("address", JoinType.INNER)
                .getResultList();
        assertEquals(14, list.size());
        for (Employee e : list) {
            assertNotNull(e.address);
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testInnerJoin_fromOwnerToInverse() throws Exception {
        List<Employee> list =
            jdbcManager.from(Employee.class).join(
                "address",
                JoinType.INNER,
                false).getResultList();
        assertEquals(14, list.size());
        for (Employee e : list) {
            assertNull(e.address);
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testLeftOuterJoin_fetch_fromInverseToOwner() throws Exception {
        List<Address> list =
            jdbcManager.from(Address.class).leftOuterJoin("employee").getResultList();
        assertEquals(14, list.size());
        for (Address e : list) {
            assertNotNull(e.employee);
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testLeftOuterJoin_fromInverseToOwner() throws Exception {
        List<Address> list =
            jdbcManager
                .from(Address.class)
                .leftOuterJoin("employee", false)
                .getResultList();
        assertEquals(14, list.size());
        for (Address e : list) {
            assertNull(e.employee);
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testInnerJoin_fetch_fromInverseToOwner() throws Exception {
        List<Address> list =
            jdbcManager
                .from(Address.class)
                .join("employee", JoinType.INNER)
                .getResultList();
        assertEquals(14, list.size());
        for (Address e : list) {
            assertNotNull(e.employee);
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testInnerJoin_fromInverseToOwner() throws Exception {
        List<Address> list =
            jdbcManager.from(Address.class).join(
                "employee",
                JoinType.INNER,
                false).getResultList();
        assertEquals(14, list.size());
        for (Address e : list) {
            assertNull(e.employee);
        }
    }
}