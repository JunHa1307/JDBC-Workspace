package com.kh.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTemplate {
	/*
	 * JDBC과정 중 반복적으로 쓰이는 구문들을 각각의 메소드로 정의해둘 곳
	 * "재사용을 목적"으로 공통 템플릿 작업 진해
	 * 
	 * 이 클래스에서의 모든 메소드들은 다 static메소드로 만들 것
	 * 
	 * 공통적인 부분 뽑아내기
	 * 1. DB와 접속된 Connection객체를 생성해서 반환시켜주는 메소드
	 */
	public static Connection getConnection() {
		
		// Connection 객체를 담을 그릇 생성
		Connection conn = null;
		
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","JDBC","JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return conn;
	}
	
	//2. 전달받은 JDBC용 객체를 반납시켜주는 메소드(Close())
	//2_1) Connection 객체를 전달받아서 반납
	
	public static void close(Connection conn) {
		
		try {
			if(conn != null && !conn.isClosed()) {
			conn.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//2_2) Statement객체를 전달받아서 반납시켜주는 메소드
	//		-> 다형성으로 인해 preparedStatement 객체를 매개변수로 받아줄 수 있다
	public static void close(Statement stmt) {
		
		try {
			if(stmt != null && !stmt.isClosed()) {
			stmt.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//2_3) ResultSet 객체를 전달받아서 반납시켜주는 메소드
	public static void close(ResultSet rset) {
		
		try {
			if(rset != null && !rset.isClosed()) {
			rset.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//3. 전달받은 Connection객체로 트랜잭션 처리를 해주는 메소드
	//3_1) commit시켜주는 메소드
	
	public static void commit(Connection conn) {
		
		try {
			if(conn !=null && !conn.isClosed())
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//3_2) rollback시켜주는 메소드
	public static void rollback(Connection conn) {
		
		try {
			if(conn !=null && !conn.isClosed())
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
