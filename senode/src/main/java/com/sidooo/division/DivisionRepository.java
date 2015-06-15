package com.sidooo.division;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("divisioinRepository")
public class DivisionRepository {
	
    @Autowired
    private SessionFactory sessionFactory;
    
    public Division getDivision(Integer id) {
        Division division = (Division)sessionFactory.getCurrentSession()
                .createQuery("from Division where id=?")
                .setParameter(0, id)
                .uniqueResult();
        return division;
    }
    
    @SuppressWarnings("unchecked")
	public List<Division> getTopList() {
    	return sessionFactory.getCurrentSession()
    			.createQuery("from Division where level=1")
    			.list();
    }
    
    @SuppressWarnings("unchecked")
	public List<Division> getChildren(Integer parentId) {
    	return sessionFactory.getCurrentSession()
    			.createQuery("from Division where parentid=?")
    			.setParameter(0, parentId)
    			.list();
    }
    
    @SuppressWarnings("unchecked")
	public List<Division> getListByLevel(Integer level) {
    	return sessionFactory.getCurrentSession()
    			.createQuery("from Division where level=?")
    			.setParameter(0, level)
    			.list();
	}

}
