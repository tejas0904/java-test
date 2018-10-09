package com.h2rd.refactoring.web;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;

import com.h2rd.refactoring.usermanagement.User;
import com.h2rd.refactoring.usermanagement.UserDao;

@Path("/users")
@Repository
public class UserResource {

	public UserDao userDao;

	@GET
	@Path("add/")
	public Response addUser(@QueryParam("name") String name, @QueryParam("email") String email,
			@QueryParam("role") List<String> roles) {

		getUserDao();

		// roles list should have at least one entry
		if (roles.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Please enter at least one role per user")
					.type(MediaType.TEXT_PLAIN).build();
		}

		// primary check if there is a unique email address
		if (userDao.isValidEmailAddress(email)) {
			User user = new User();
			user.setName(name);
			user.setEmail(email);
			user.setRoles(roles);

//	        if (userDao == null) {
//	            userDao = UserDao.getUserDao();
//	        }
			userDao.saveUser(user);
			return Response.ok().entity(user).build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Please enter another email address, User already exists with this email.")
					.type(MediaType.TEXT_PLAIN).build();
		}

	}

	// Created a helper to fetch unique instance of userDao created by spring
	// framework in application context
	private void getUserDao() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath:/application-config.xml" });
		userDao = context.getBean(UserDao.class);
	}

	@GET
	@Path("update/")
	public Response updateUser(@QueryParam("name") String name, @QueryParam("email") String email,
			@QueryParam("role") List<String> roles) {

		getUserDao();

		// roles list should have at least one entry
		if (roles.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Please enter at least one role per user")
					.type(MediaType.TEXT_PLAIN).build();
		}

		// primary check if there is a unique email address
		if (userDao.isValidEmailAddress(email)) {
			User user = new User();
			user.setName(name);
			user.setEmail(email);
			user.setRoles(roles);

			userDao.updateUser(user);
			return Response.ok().entity(user).build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Please enter another email address, User already exists with this email.")
					.type(MediaType.TEXT_PLAIN).build();
		}
	}

	@GET
	@Path("delete/")
	public Response deleteUser(@QueryParam("name") String name, @QueryParam("email") String email,
			@QueryParam("role") List<String> roles) {

//		User user = new User();
//		user.setName(name);
//		user.setEmail(email);
//		user.setRoles(roles);

		getUserDao();
		User user = userDao.findUser(name);
		if (null != user) {
			userDao.deleteUser(user);
		}

		return Response.ok().entity(user).build();
	}

	@GET
	@Path("find/")
	public Response getUsers() {

		getUserDao();
		List<User> users = userDao.getUsers();
		if (users == null) {
			users = new ArrayList<User>();
		}

		GenericEntity<List<User>> usersEntity = new GenericEntity<List<User>>(users) {
		};
		return Response.status(200).entity(usersEntity).build();
	}

	@GET
	@Path("search/")
	public Response findUser(@QueryParam("name") String name) {

//        if (userDao == null) {
//            userDao = UserDao.getUserDao();
//        }

		getUserDao();

		User user = userDao.findUser(name);
		return Response.ok().entity(user).build();
	}
}
