package com.github.distributed.tx.sample.commons.dao;

import java.io.Serializable;

public interface Dao<T, ID extends Serializable> {

	T save(T t);
	
	T findOne(ID id);
	
	void delete(ID id);
	
}
