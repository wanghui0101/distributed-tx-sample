package com.github.distributed.tx.sample.app.producer.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.distributed.tx.sample.app.producer.domain.User;
import com.github.distributed.tx.sample.commons.dao.AbstractDao;

@Transactional(readOnly = true)
@Repository
public class UserDao extends AbstractDao<User, String> {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Transactional
	@Override
	public User save(User user) {
		if (StringUtils.isEmpty(user.getId())) {
			String update = "insert into t_user(id, name, create_at) values(?, ?, ?)";
			jdbcTemplate.update(update, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					String id = UUID.randomUUID().toString();
					user.setId(id);
					ps.setString(1, id);
					ps.setString(2, user.getName());
					ps.setTimestamp(3, user.getCreateAt());
				}
				
			});
		} else {
			String update = "update t_user t set t.name = ?, t.create_at = ? where t.id = ?";
			jdbcTemplate.update(update, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, user.getName());
					ps.setDate(2, new Date(user.getCreateAt().getTime()));
					ps.setString(3, user.getId());
				}
				
			});
		}
		
		return user;
	}

	@Override
	public User findOne(String id) {
		String select = "select id, name, create_at from t_user where id = ?";
		final User user = new User();
		jdbcTemplate.query(select, new Object[] {id}, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				user.setId(rs.getString("id"));
				user.setName(rs.getString("name"));
				user.setCreateAt(rs.getTimestamp("create_at"));
			}
			
		});
		return user;
	}

	@Transactional
	@Override
	public void delete(String id) {
		String delete = "delete from t_user where id = ?";
		jdbcTemplate.update(delete, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, id);
			}
			
		});
	}
	
}
