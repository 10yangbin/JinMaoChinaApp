package com.guomao.propertyservice.model.user;

public interface UserDao {

	User getCurrentUser();

	void setCurrentUser(User u);

	void updateUser(User u);
}
