package com.github.distributed.tx.sample.app.producer.dao;

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

import com.github.distributed.tx.sample.app.producer.domain.MessageRecord;
import com.github.distributed.tx.sample.commons.dao.AbstractDao;

@Transactional(readOnly = true)
@Repository
public class MessageRecordDao extends AbstractDao<MessageRecord, String> {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Transactional
	@Override
	public MessageRecord save(MessageRecord messageRecord) {
		if (StringUtils.isEmpty(messageRecord.getId())) {
			String update = "insert into t_message_record(id, message_id, message_content, execute_result) values(?, ?, ?, ?)";
			jdbcTemplate.update(update, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					String id = UUID.randomUUID().toString();
					messageRecord.setId(id);
					ps.setString(1, id);
					ps.setString(2, messageRecord.getMessageId());
					ps.setString(3, messageRecord.getMessageContent());
					ps.setString(4, messageRecord.getExecuteResult());;
				}
				
			});
		} else {
			String update = "update t_message_record t set t.message_id = ?, t.message_content = ?, t.execute_result = ? where t.id = ?";
			jdbcTemplate.update(update, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, messageRecord.getMessageId());
					ps.setString(2, messageRecord.getMessageContent());
					ps.setString(3, messageRecord.getExecuteResult());
					ps.setString(4, messageRecord.getId());
				}
				
			});
		}
		
		return messageRecord;
	}

	@Override
	public MessageRecord findOne(String id) {
		String select = "select id, message_id, message_content, execute_result from t_message_record where id = ?";
		final MessageRecord messageRecord = new MessageRecord();
		jdbcTemplate.query(select, new Object[] {id}, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				messageRecord.setId(rs.getString("id"));
				messageRecord.setMessageId(rs.getString("message_id"));
				messageRecord.setMessageContent(rs.getString("message_content"));
				messageRecord.setExecuteResult(rs.getString("execute_result"));
			}
			
		});
		return messageRecord;
	}

	public MessageRecord findByMessageId(String messageId) {
		String select = "select id, message_id, message_content, execute_result from t_message_record where message_id = ?";
		final MessageRecord messageRecord = new MessageRecord();
		jdbcTemplate.query(select, new Object[] {messageId}, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				messageRecord.setId(rs.getString("id"));
				messageRecord.setMessageId(rs.getString("message_id"));
				messageRecord.setMessageContent(rs.getString("message_content"));
				messageRecord.setExecuteResult(rs.getString("execute_result"));
			}
			
		});
		return messageRecord;
	}
	
}
