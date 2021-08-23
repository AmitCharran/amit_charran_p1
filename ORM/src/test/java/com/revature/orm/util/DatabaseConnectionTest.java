package com.revature.orm.util;


import com.revature.orm.persistence.DAOimpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseConnectionTest {
    @Mock
    private ConnectionUtil c;
    @Mock
    private DAOimpl dao;
    @Mock
    private PreparedStatement ps;
    @Mock
    private ResultSet rs;

    private Test t;

    @Before
    public void setUp() throws Exception{

    }


}
