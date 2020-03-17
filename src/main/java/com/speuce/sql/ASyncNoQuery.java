package main.java.com.speuce.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
@Deprecated
public class ASyncNoQuery extends SQLQuery{
	String query;
	public ASyncNoQuery(String query){
		this.query = query;
	}
	@Override
	public void run() {
		Connection c = null;
		PreparedStatement stmt = null;
			try {
				//c = this.data.getConnection();
				c = this.data;
				stmt = c.prepareStatement(query);
			} catch (SQLException e){
				e.printStackTrace();
			}finally{
				close(c);
				close(stmt);
				query = null;
				data = null;
				this.cancel();
			}
		
	}
}
