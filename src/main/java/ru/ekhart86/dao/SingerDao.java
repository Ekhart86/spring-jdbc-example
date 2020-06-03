package ru.ekhart86.dao;

import ru.ekhart86.entities.Singer;

import javax.sql.DataSource;
import java.util.List;

public interface SingerDao {

    void setDataSource(DataSource dataSource);

    Singer findSingerById(Long id);

    List<Singer> findAll();

    List<Singer> findByFirstName(String firstName);

    String findFullNameById(Long id);

    String findLastNameById(Long id);

    String findFirstNameById(Long id);

    int insert(Singer singer);

    void update(Singer singer);

    void delete(Long singerId);

    List<Singer> findAllWithAlbums();

}
