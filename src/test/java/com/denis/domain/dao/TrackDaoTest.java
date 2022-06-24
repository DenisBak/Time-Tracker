package com.denis.domain.dao;

import com.denis.domain.Track;
import com.denis.domain.dao.track.TrackDao;
import com.denis.domain.exceptions.DAOException;
import com.denis.domain.exceptions.DomainException;
import org.junit.Test;

public class TrackDaoTest {
    @Test
    public void createTrackTest() throws DomainException, DAOException {
        TrackDao dao = TrackDao.getInstance();

        int id = new Track(1, "anon", "18:30", "20:00", "2022-06-24").getId();

        System.out.println(id);
    }
}
