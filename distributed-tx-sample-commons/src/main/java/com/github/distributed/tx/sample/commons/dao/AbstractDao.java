package com.github.distributed.tx.sample.commons.dao;

import java.io.Serializable;

public abstract class AbstractDao<T, ID extends Serializable> implements Dao<T, ID> {

	@Override
	public T save(T t) {
		return null;
	}

	@Override
	public T findOne(ID id) {
		return null;
	}

	@Override
	public void delete(ID id) {
		
	}

}
