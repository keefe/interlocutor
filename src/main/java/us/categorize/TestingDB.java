package us.categorize;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import us.categorize.model.Message;
import us.categorize.model.simple.PersistentMessage;

public class TestingDB {
  public static void main(String args[]) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("test-persistence");
    EntityManager em = emf.createEntityManager();
    em.getTransaction().begin();
    PersistentMessage m = new PersistentMessage();
    m.setId("artificial");
    m.setText("This would be the message text");
    em.persist(m);
    em.getTransaction().commit();
    em.close();

  }
}
