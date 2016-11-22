package com.github.distributed.tx.sample.commons.message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.distributed.tx.sample.commons.dao.AbstractDao;

@Transactional(readOnly = true)
@Repository
public class DistributedTransactionMessageDao extends AbstractDao<DistributedTransactionMessage, String> {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Transactional
	@Override
	public DistributedTransactionMessage save(DistributedTransactionMessage msg) {
		if (StringUtils.isEmpty(msg.getId())) {
			String update = "insert into t_dtm(id, message_id, app, app_interface, app_interface_method, "
					+ "exchange, routing_key, body, status, max_retry_times, "
					+ "retried_times, initialized_at, confirmed_at, acked_at, consumed_at) "
					+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			jdbcTemplate.update(update, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					String id = UUID.randomUUID().toString();
					msg.setId(id);
					ps.setString(1, msg.getId());
					ps.setString(2, msg.getMessageId());
					ps.setString(3, msg.getApp());
					ps.setString(4, msg.getAppInterfaceForConfirmation());
					ps.setString(5, msg.getAppInterfaceMethodForConfirmation());
					ps.setString(6, msg.getExchange());
					ps.setString(7, msg.getRoutingKey());
					ps.setString(8, msg.getBody());
					ps.setString(9, msg.getStatus());
					ps.setInt(10, msg.getMaxRetryTimes());
					ps.setInt(11, msg.getRetriedTimes());
					ps.setTimestamp(12, msg.getInitializedAt());
					ps.setTimestamp(13, msg.getConfirmedAt());
					ps.setTimestamp(14, msg.getAckedAt());
					ps.setTimestamp(15, msg.getConsumedAt());
				}
				
			});
		} else {
			String update = "update t_dtm t set t.message_id = ?, t.app = ?, t.app_interface = ?, t.app_interface_method = ?, "
					+ "t.exchange = ?, t.routing_key = ?, t.body = ?, t.status = ?, t.max_retry_times = ?, t.retried_times = ?, "
					+ "t.initialized_at = ?, t.confirmed_at = ?, t.acked_at = ?, t.consumed_at = ? "
					+ "where t.id = ?";
			jdbcTemplate.update(update, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, msg.getMessageId());
					ps.setString(2, msg.getApp());
					ps.setString(3, msg.getAppInterfaceForConfirmation());
					ps.setString(4, msg.getAppInterfaceMethodForConfirmation());
					ps.setString(5, msg.getExchange());
					ps.setString(6, msg.getRoutingKey());
					ps.setString(7, msg.getBody());
					ps.setString(8, msg.getStatus());
					ps.setInt(9, msg.getMaxRetryTimes());
					ps.setInt(10, msg.getRetriedTimes());
					ps.setTimestamp(11, msg.getInitializedAt());
					ps.setTimestamp(12, msg.getConfirmedAt());
					ps.setTimestamp(13, msg.getAckedAt());
					ps.setTimestamp(14, msg.getConsumedAt());
					ps.setString(15, msg.getId());
				}
				
			});
		}
		
		return msg;
	}

	@Override
	public DistributedTransactionMessage findOne(String id) {
		String select = "select id, message_id, app, app_interface, app_interface_method, "
					+ "exchange, routing_key, body, status, max_retry_times, "
					+ "retried_times, initialized_at, confirmed_at, acked_at, consumed_at "
					+ "from t_dtm where id = ?";
		final DistributedTransactionMessage msg = new DistributedTransactionMessage();
		jdbcTemplate.query(select, new Object[] {id}, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				populateDistributedTransactionMessage(rs, msg);
			}
			
		});
		return msg;
	}

	@Transactional
	@Override
	public void delete(String id) {
		String delete = "delete from t_dtm where id = ?";
		jdbcTemplate.update(delete, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, id);
			}
			
		});
	}

	public DistributedTransactionMessage findByMessageId(String messageId) {
		String select = "select id, message_id, app, app_interface, app_interface_method, "
					+ "exchange, routing_key, body, status, max_retry_times, "
					+ "retried_times, initialized_at, confirmed_at, acked_at, consumed_at "
					+ "from t_dtm where message_id = ?";
		final DistributedTransactionMessage msg = new DistributedTransactionMessage();
		jdbcTemplate.query(select, new Object[] {messageId}, new RowCallbackHandler() {
	
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				populateDistributedTransactionMessage(rs, msg);
			}
			
		});
		return msg;
	}

	public List<DistributedTransactionMessage> queryConfirmedMessages() {
		return queryMessagesByStatus(DistributedTransactionMessage.Status.CONFIRMED);
	}
	
	public List<DistributedTransactionMessage> queryAckedMessages() {
		return queryMessagesByStatus(DistributedTransactionMessage.Status.ACKED);
	}
	
	public List<DistributedTransactionMessage> queryInitializedMessages() {
		return queryMessagesByStatus(DistributedTransactionMessage.Status.INITIALIZED);
	}
	
	private List<DistributedTransactionMessage> queryMessagesByStatus(String status) {
		String select = "select id, message_id, app, app_interface, app_interface_method, "
					+ "exchange, routing_key, body, status, max_retry_times, "
					+ "retried_times, initialized_at, confirmed_at, acked_at, consumed_at "
				+ "from t_dtm where status = ? order by " + status + "_at";
		
		List<DistributedTransactionMessage> messages = new ArrayList<DistributedTransactionMessage>();
		
		jdbcTemplate.query(select, new Object[] {status}, new RowCallbackHandler() {
	
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				final DistributedTransactionMessage msg = populateDistributedTransactionMessage(rs);
				messages.add(msg);
			}
			
		});
		return messages;
	}
	
	private DistributedTransactionMessage populateDistributedTransactionMessage(ResultSet rs, DistributedTransactionMessage msg) throws SQLException {
		msg.setId(rs.getString("id"));
		msg.setMessageId(rs.getString("message_id"));
		msg.setApp(rs.getString("app"));
		msg.setAppInterfaceForConfirmation(rs.getString("app_interface"));
		msg.setAppInterfaceMethodForConfirmation(rs.getString("app_interface_method"));
		msg.setExchange(rs.getString("exchange"));
		msg.setRoutingKey(rs.getString("routing_key"));
		msg.setBody(rs.getString("body"));
		msg.setStatus(rs.getString("status"));
		msg.setMaxRetryTimes(rs.getInt("max_retry_times"));
		msg.setRetriedTimes(rs.getInt("retried_times"));
		msg.setInitializedAt(rs.getTimestamp("initialized_at"));
		msg.setConfirmedAt(rs.getTimestamp("confirmed_at"));
		msg.setAckedAt(rs.getTimestamp("acked_at"));
		msg.setConsumedAt(rs.getTimestamp("consumed_at"));
		return msg;
	}
	
	private DistributedTransactionMessage populateDistributedTransactionMessage(ResultSet rs) throws SQLException {
		return populateDistributedTransactionMessage(rs, new DistributedTransactionMessage());
	}
	
}
