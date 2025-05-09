package dao;

import model.Subscription;
import org.hibernate.Session;
import util.HibernateUtil;

public class SubscriptionDAO extends GenericDAOImpl<Subscription> {
    public SubscriptionDAO() {
        super(Subscription.class);
    }

    public boolean isUserSubscribed(int chatId, int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(*) FROM Subscription WHERE chat.id = :chatId AND user.id = :userId";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("chatId", chatId)
                    .setParameter("userId", userId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

}
