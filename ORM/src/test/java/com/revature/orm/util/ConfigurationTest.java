package com.revature.orm.util;

import com.revature.orm.model.User;
import com.revature.orm.persistence.DAO;
import com.revature.orm.persistence.DAOimpl;

import org.checkerframework.checker.units.qual.C;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import org.mockito.Mock;

import org.mockito.junit.MockitoJUnitRunner;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationTest {
    @Mock
    private Configuration cfg;
    @Mock
    private DAOimpl dao;

    @Before
    public void setUp(){
        dao = mock(DAOimpl.class);
        cfg = new Configuration("","","");
        cfg.setDao(dao);
    }

    @After
    public void tearDown(){

    }
    @Test (expected = NullPointerException.class)
    public void addAnnotatedClassButCannotGetDatabaseAccess() {
        cfg.addAnnotatedClass(User.class);

    }

    @Test
    public void getAll() {
        User user = new User(1, "f", "n", "e");
        List<Object> forReturn = new ArrayList<>();
        forReturn.add(user);
        when(dao.tableExists(anyString())).thenReturn(true);
        when(dao.getAll(User.class)).thenReturn(forReturn);
        assertTrue(user.equals(cfg.getAll(User.class).get(0)));
    }
    @Test (expected = IllegalStateException.class)
    public void getAllButTableDoesNotExist() {
        User user = new User(1, "f", "n", "e");
        List<Object> forReturn = new ArrayList<>();
        forReturn.add(user);
        when(dao.tableExists(anyString())).thenReturn(false);
        when(dao.getAll(any(Class.class))).thenReturn(forReturn);
        cfg.getAll(User.class);
    }

    @Test (expected = IllegalStateException.class)
    public void getAllButCannotAccessDatabase(){
        cfg = mock(Configuration.class);
        when(cfg.getAll(any(Class.class))).thenThrow(IllegalStateException.class);
        cfg.getAll(Integer.class);
    }

    @Test
    public void getById() {
        User user = new User(1, "f", "n", "e");
        when(dao.getById(any(Class.class), anyInt())).thenReturn((Object) user);
        assertTrue(cfg.getById(User.class, 1).equals(user));
    }

    @Test
    public void insertIntoTable() {
        ArgumentCaptor<Object> varArgs = ArgumentCaptor.forClass(Object.class);
        doNothing().when(dao).insert(any(Class.class), varArgs.capture());
        Object[] o = new Object[3];
        o[0]="f";
        o[1]="n";
        o[2]="e";
        cfg.insertIntoTable(User.class, o);
    }

    @Test
    public void update() {
        ArgumentCaptor<Object> varArgs = ArgumentCaptor.forClass(Object.class);
        doNothing().when(dao).update(any(Class.class), varArgs.capture());
        cfg.update(User.class, varArgs);
        verify(dao, atLeastOnce()).update(User.class, varArgs);
    }

    @Test
    public void deleteByID() {
        doNothing().when(dao).removeById(any(Class.class),anyInt());
        cfg.deleteByID(User.class, 2);
        verify(dao, times(1)).removeById(User.class, 2);
    }

    @Test
    public void getColumnNames() {
        List<String> forReturn = new ArrayList<>();
        forReturn.add("Test");
        when(dao.getColumnNames(User.class)).thenReturn(Optional.of(forReturn));
        cfg.getColumnNames(User.class);
        verify(dao, atLeastOnce()).getColumnNames(User.class);

    }

    @Test
    public void getColumnTypes() {
        List<String> forReturn = new ArrayList<>();
        forReturn.add("Test");
        when(dao.getAllColumnTypes(User.class)).thenReturn(Optional.of(forReturn));
        cfg.getColumnTypes(User.class);
        verify(dao, atLeastOnce()).getAllColumnTypes(User.class);
    }


    @Test
    public void getDbURL() {
        cfg = new Configuration("test", "t","t");
        assertEquals("test", cfg.getDbURL());

    }

}