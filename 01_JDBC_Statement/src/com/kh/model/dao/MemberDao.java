package com.kh.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kh.model.vo.Member;

/*
	DAO(Data Access Object)
	Contoller을 통해서 호출
	Controller에서 요청받은 실질적인 기능을 수행함.
	DB에 직접 접근해서 SQL문을싱행하고,  수행결과 돌려받기 ->  JDBC
*/
public class MemberDao {
	/*
	 * JDBC 용 객체.
	 * - Connection : DB와의 연결정보를 담고 있는 객체(IP주소, PORT번호, 계정명, 비밀번호)
	 * - (Prepared)Statement : 해당 db 에 SQL문을 전달하고 실행한후 결과를 받아내는 객체
	 * - ResultSet : 만일 실행한 sql문이 SELECT문일 경우 조회된 결과들이 담겨있는 객체 
	 * 
	 * JDBC 처리 순서
	 * 1) JDBC DRIVER 등록 : 해당 DBMS가 제공하는 클래스 등록
	 * 2) Connection 생성 : 접속하고자 하는 db정보를 입력해서 db에 접속하면서 생성
	 * 3) Statement 생성 : Connection 객체를 이용해서 생성.
	 * 4) SQL문을 전달하면서 실행 : Statement 객체를 이용해서 SQL문실행
	 *                        > SELECT문일경우 - executeQuery()메소드를 이용하여 실행
	 *                        > 기타 DML문일경우 - executeUpdate()메소드를 이용하여 실행
	 * 5) 결과 받기
	 *                        > SELECT문일경우 -> ResultSet객체로 받기 => 6_1)
	 *                        > 기타 DML문일경우 -> INT형 변수(처리된 행의 갯수) 로 받기 => 6_2)
	 * 6_1) ResultSet(조회된 데이터들)객체에 담긴 데이터들을 하나씩 뽑아서 vo객체로 만들기(arrayList로 묶어서 관리)
	 * 6_2) 트랜잭션 처리(성공이면 Commit, Rollback)
	 * 7) 다쓴 JDBC용 객체들을 반납(close()) -> 생성된 순서의 역순으로 반납
	 * 8) 결과르를 Controller에게 반환
	 *     > select문일경우 6_1)에서 만들어진 결과값 반환
	 *     > 기타 dml문일경우 - int형 값(처리된 행의 갯수)를 반환.
	 * 
	 * * Statement특징 : 완성된 sql문을실행할 수 있는 객체.
	 * 
	 */
	
	
	
	/**
	 * 사용자가 회원 추가 요청시 입력했던 값을 가지고 Insert문을 실행하는 메소드
	 * @param m : 사용자가 입력했던 아이디부터 취미까지의 값을가지고 만든 vo객체
	 * @return : Insert문을 실행한 행의 결과값
	 */
	public int insertMember(Member m) {
		// Insert문 -> 처리된 행의 갯수 ->  트랜잭션 처리
		
		//0) 필요한 변수 셋팅
		int result = 0; // 처리된 결과(처리된 행의 갯수)를 담아줄 변수
		Connection conn = null; // 접속된 db의 연결정보를 담는 변수
		Statement stmt = null; // sql문 실행 후 결과를 받기위한 변수
		
		//+ 필요한변수 : 실행시킬 SQL문(완성된 형태의 SQL문으로 만들기) => 끝에 세미콜론 절대 붙이지말기.
		/* 
		 * INSERT INTO MEMBER
		 * VALUES (SEQ_USERNO.NEXTVAL , 'XXX','XXX','XXX','X' , XX, 'XX@XXXX','XXX' ,'XXXX', 'XXX', DEFAULT  )
		 */
		String sql = "INSERT INTO MEMBER VALUES(SEQ_USERNO.NEXTVAL ,"
				   + "'" + m.getUserId() +"'," 
				   + "'" + m.getUserPwd() +"',"
				   + "'" + m.getUserName() +"',"
				   + "'" + m.getGender() +"',"
				   +       m.getAge() + ", "
				   + "'" + m.getEmail() +"',"
				   + "'" + m.getPhone()+"',"
				   + "'" + m.getAddress() +"',"
				   + "'" + m.getHobby() +"',"
				   + "DEFAULT)";
		
		try {
			//1) JDBC 드라이버 등록.
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// 오타가 있을경우, ojdbc6.jar이 없을경우 -> ClassNotFoundException 이 발생함.
			
			//2) Connection 객체 생성 -> db와 연결시키겠다 
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC"); // 여러분은 대부분 orcl 
			
			//3) Statement 객체 생성
			stmt = conn.createStatement();
			
			// 4, 5) DB에 완성된 SQL문을 전달하면서 실행후 결과받기.
			result = stmt.executeUpdate(sql);
			
			//6_2) 트랜잭션 처리
			if(result > 0) { // 1개 이상의 행이 INSERT되었다면 => 커밋
				conn.commit();
			}else { // 실패했을경우 => 롤백
				conn.rollback();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 7) 다쓴 자원 반납해주기 -> 생성된 순서의 역순으로.
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//8)결과 반환
		return result;
	}
	
	
	/**
	 * 사용자가 회원 전체 조회 요청시 select문을 실행해주는 메소드
	 * @return
	 */
	public ArrayList<Member> selectAll(){
		// SELECT -> ResultSet => ArrayList로 반환
		
		//0) 필요한 변수들 셋팅
		// 조회된 결과를 뽑아서 담아줄 변수 => ArrayList<Member> -> 여러 회원에 대한 정보.
		ArrayList<Member> list = new ArrayList<>();// 현재 텅빈리스트.
		
		// Connection, Statement, ResultSet
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null; // SELECT문이 실행된 조회결과값들이 처음에 실질적으로 담길 객체
		
		String sql = "SELECT * FROM MEMBER";
		
		try {
			//1) JDBC 드라이버 등록.
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// 오타가 있을경우, ojdbc6.jar이 없을경우 -> ClassNotFoundException 이 발생함.
			
			//2) Connection 객체 생성 -> db와 연결시키겠다 
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","JDBC","JDBC"); // 여러분은 대부분 orcl 
			
			//3) Statement객체생성
			stmt = conn.createStatement();
			
			// 4,  5)
			rset = stmt.executeQuery(sql);
			
			// 6_1) 현재 조회결과가 담긴 ResultSet에서 한행씩 뽑아서 vo 객체에 담기.
			// rset.next() : 커서를 한줄 아래로 옮겨주고 해당행이 존재할 경우 true, 아니면 false를 반환해주는 메서드
			while(rset.next()) {
				
				// 현재 rset의 커서가 가리키고 있는 해당행의 데이터를 하나씩 뽑아서 Member객체 담기
				Member m = new Member();
				
				// rset 으로부터 어떤 컬럼에 있는 값을 뽑을건지 제시
				// 컬럼명(대소문자x), 컬럼순번
				// 권장사항 : 컬럼명으로 쓰고, 대문자로 쓰는것을 권장함.
				// rset.getInt(컬럼명 또는 순번) : int형 값을 뽑아낼때 사용
				// rset.getString(컬럼명 또는 컬럼순번) : String값을 뽑아낼때 사용
				// rset.getDate(컬럼명 또는 컬럼순번) : Date값을 뽑아 올때 사용하는 메서드.
				
				m.setUserNo(rset.getInt("USERNO"));
				m.setUserId(rset.getString("USERID"));
				m.setUserPwd(rset.getString("USERPWD"));
				m.setUserName(rset.getString("USERNAME"));
				m.setGender(rset.getString("GENDER"));
				m.setAge(rset.getInt("AGE"));
				m.setEmail(rset.getString("EMAIL"));
				m.setPhone(rset.getString("PHONE"));
				m.setAddress(rset.getString("ADDRESS"));
				m.setHobby(rset.getString("HOBBY"));
				m.setEnrollDate(rset.getDate("ENROLLDATE"));
				// 한 행에 대한 모든 칼럼의 데이터값들을
				// 각각의 필드에 담아 하나의 Member 객체에 옮겨담아주기 끝.
				
				
				list.add(m);// 리스트에해당 Member객체를담아주기
				
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			try {
				rset.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
