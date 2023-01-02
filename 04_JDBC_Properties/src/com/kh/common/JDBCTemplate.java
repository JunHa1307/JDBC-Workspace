package com.kh.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

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
		/*
		 * 기존의 방식 : JDBC Driver구문, 내가 접속할 DB의 URL정보, 접속할 계정명, 비밀번호 들을 
		 * 				자바 소스코드 내에 명시적으로 작성함 -> 정적 코딩방식(하드코딩)
		 * 문제점 	: DBMS가 변경이 되었을 경우 / 접속할 URL, 계정명, 비밀번호가 변경되었을 경우
		 * 			  => 자바 소스코드를 수정해야함.
		 * 			  수정된 내용을 반영시키고자한다면 프로그램을 재구동 해야함
		 * 			  (사용자 입장에서도 프로그램 사용중 비정상적으로 종료되었다가 다시 구동될 수 있음)
		 * 			  * 유지보수에 불편하다
		 * 해결방식	: DB관련된 정보들을 별도로 관리하는 외부파일 .properties로 만들어서 관리
		 * 			  외부파일로 key에 대한 value값을 읽어들여서 반영시킬 에정 => 동적 코딩방식
		 * 
		 */
		// 동적 코딩방식을 적용시키기 위해서 Properties 객체를 생성
		Properties prop = new Properties();
		
		// Connection 객체를 담을 그릇 생성
		Connection conn = null;
		
		
		try {
			// prop객체로부터 load메서드를 활용해서 각 키에 해당되는 value값을 얻어올 목적으로 선언
			prop.load(new FileInputStream("resources/driver.properties"));
			
			Class.forName(prop.getProperty("driver"));
			conn = DriverManager.getConnection(prop.getProperty("url"),
												prop.getProperty("username"),
												prop.getProperty("password"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
