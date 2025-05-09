package dao;

import model.User;

public class UserDAO extends GenericDAOImpl<User> {
    public UserDAO() {
        super(User.class);
    }

    public User findByEmail(String email) {
        try (var session = util.HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    public User findByNickname(String nickname) {
        try (var session = util.HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE nickname =:nickname",User.class)
                    .setParameter("nickname",nickname)
                    .uniqueResult();
        }
    }
}
