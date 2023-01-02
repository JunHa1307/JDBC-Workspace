package com.kh.model.service;

import java.sql.Connection;
import java.util.ArrayList;

import com.kh.common.JDBCTemplate;
import com.kh.model.dao.MemberDao;
import com.kh.model.vo.Member;

/*
 * Service : 기존의 DAO의 역할을 분담
 * 			 컨트롤러에서 서비스 호출(Connection 객체 생성) 후 서비스를 켜서 DAO로 넘길 것
 * 			 DAO호출 시 커넥션 객체와 기존에 넘기고자 했던 매개변수를 같이 넘겨줌
 * 			 DAO처리가 끝나면 서비스단에서 결과에 따른 트랜잭션처리도 같이해줌
 * 			 -> 서비스단을 추가함으로서 DAO에는 수수하게 SQL문을 처리하는 부분 남게 됨
 */
public class MemberService {

	public int insertMember(Member m) {
		
		// 제일 먼저 Connection 객체생성
		Connection conn = JDBCTemplate.getConnection();
	
		// Dao 호출 시 Connection객체를 매개변수로 넘겨준다
		int result = new MemberDao().insertMember(conn,m);
		
		// 결과에 따른 트랜잭션 처리
		if(result > 0) {//성공
			JDBCTemplate.commit(conn);
		}else {
			JDBCTemplate.rollback(conn);
		}
		
		//Connection객체 바납
		JDBCTemplate.close(conn);
		
		return result;
	}
	
	public ArrayList<Member> selectAll(){
		
		// 1. Connection객체 생성
		Connection conn = JDBCTemplate.getConnection();
		
		// 2. 결과값을 담을 변수
		ArrayList<Member> list = new MemberDao().selectAll(conn);
		
		// 3. Connection 객체 close
		JDBCTemplate.close(conn);
		
		return list;
	}
	
	public Member selectByUserId(String userId) {
		
		//1)
		Connection conn = JDBCTemplate.getConnection();
		
		//2)
		Member m = new MemberDao().selectByUserId(conn,userId);
		
		JDBCTemplate.close(conn);
		
		return m;
	}
	
	public ArrayList<Member> selectByUserName(String keyword){
		//1)
		Connection conn = JDBCTemplate.getConnection();
		
		//2)
		ArrayList<Member> list = new MemberDao().selectByUserName(conn, keyword);
		
		//3)
		JDBCTemplate.close(conn);
		
		return list;
	}
	
	public int updateMember(Member m) {
		//1)
		Connection conn = JDBCTemplate.getConnection();
		
		//2)
		int result = new MemberDao().updateMember(conn, m);
		
		//3)
		if(result > 0) {
			JDBCTemplate.commit(conn);
		}else {
			JDBCTemplate.rollback(conn);
		}
		
		JDBCTemplate.close(conn);
		
		return result;
	}
	
	public int deleteMember(String userId) {
		//1)
		Connection conn = JDBCTemplate.getConnection();
		
		//2)
		int result = new MemberDao().deleteMember(conn, userId);
		
		//3)
		if(result > 0) {
			JDBCTemplate.commit(conn);
		}else {
			JDBCTemplate.rollback(conn);
		}
		
		JDBCTemplate.close(conn);
		
		return result;
	}
}
