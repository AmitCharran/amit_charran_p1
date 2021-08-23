package com.revature.orm.model;

import com.revature.orm.annotations.Column;
import com.revature.orm.annotations.Entity;
import com.revature.orm.annotations.Id;

import java.sql.Date;

@Entity(tableName= "test_table")
public class Test {
	
	@Id(columnName = "test_id")
	private int id;
	
	@Column(columnName = " test_field_1")
	private String testfield1;
	
	@Column(columnName = "test_field_2")
	private String testField2;

	@Column(columnName = "test_field_3")
	private int testField3;

	@Column(columnName = "test_field_4")
	private double testField4;

	@Column(columnName = "Test_field_5")
	private Date testField5;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTestfield1() {
		return testfield1;
	}

	public void setTestfield1(String testfield1) {
		this.testfield1 = testfield1;
	}

	public String getTestField2() {
		return testField2;
	}

	public void setTestField2(String testField2) {
		this.testField2 = testField2;
	}

	public int getTestField3() {
		return testField3;
	}

	public void setTestField3(int testField3) {
		this.testField3 = testField3;
	}

	public double getTestField4() {
		return testField4;
	}

	public void setTestField4(double testField4) {
		this.testField4 = testField4;
	}

	public Date getTestField5() {
		return testField5;
	}

	public void setTestField5(Date testField5) {
		this.testField5 = testField5;
	}

	public String toString(){
		return testfield1 + testField2;
	}
}
