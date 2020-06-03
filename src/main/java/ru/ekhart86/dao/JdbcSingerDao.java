package ru.ekhart86.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import ru.ekhart86.entities.Album;
import ru.ekhart86.entities.Singer;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JdbcSingerDao implements SingerDao {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Singer findSingerById(Long id) {
        String SQL = "SELECT * FROM SINGER WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(SQL, parameters, new SingerMapper());
    }

    @Override
    public List<Singer> findAll() {
        String SQL = "SELECT * FROM SINGER";
        return namedParameterJdbcTemplate.query(SQL, new SingerMapper());
    }

    @Override
    public List<Singer> findByFirstName(String firstName) {
        String SQL = "SELECT * FROM SINGER WHERE first_name = :firstName";
        SqlParameterSource parameters = new MapSqlParameterSource("firstName", firstName);
        return namedParameterJdbcTemplate.query(SQL, parameters, new SingerMapper());
    }

    @Override
    public String findFullNameById(Long id) {
        String SQL = "SELECT first_name || ' ' || last_name FROM singer WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(SQL, parameters, String.class);
    }

    @Override
    public String findLastNameById(Long id) {
        String SQL = "SELECT last_name FROM singer WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(SQL, parameters, String.class);

    }

    @Override
    public String findFirstNameById(Long id) {
        String SQL = "SELECT first_name FROM singer WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(SQL, parameters, String.class);
    }

    @Override
    public int insert(Singer singer) {
        String INSERT_QUERY = "INSERT INTO singer (first_name, last_name, birth_date) values (:first_name, :last_name, :birth_date)";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("first_name", singer.getFirstName())
                .addValue("last_name", singer.getLastName())
                .addValue("birth_date", singer.getBirthDate());
        return namedParameterJdbcTemplate.update(INSERT_QUERY, parameters);
    }

    @Override
    public void update(Singer singer) {
        String UPDATE_QUERY = "UPDATE SINGER SET first_name = :first_name, last_name = :last_name, birth_date = :birth_date WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", singer.getId())
                .addValue("first_name", singer.getFirstName())
                .addValue("last_name", singer.getLastName())
                .addValue("birth_date", singer.getBirthDate());
        int status = namedParameterJdbcTemplate.update(UPDATE_QUERY, namedParameters);
        if (status != 0) {
            System.out.println("Singer data updated for ID " + singer.getId());
        } else {
            System.out.println("No Singer found with ID " + singer.getId());
        }
    }

    @Override
    public void delete(Long singerId) {
        String DELETE_QUERY = "DELETE from SINGER WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", singerId);
        int status = namedParameterJdbcTemplate.update(DELETE_QUERY, parameters);
        if (status != 0) {
            System.out.println("Singer data deleted for ID " + singerId);
        } else {
            System.out.println("No Singer found with ID " + singerId);
        }
    }

    /**
     * @return метод возвращает список исполнителей имеющих альбомы
     */
    @Override
    public List<Singer> findAllWithAlbums() {
        String SINGER_WITH_ALBUM_QUERY = "SELECT s.id, s.first_name, s.last_name, s.birth_date" +
                ", a.id as album_id, a.title, a.release_date FROM singer AS s " +
                "JOIN album AS a ON s.id = a.singer_id";
        return namedParameterJdbcTemplate.query(SINGER_WITH_ALBUM_QUERY, new SingerWithAlbumExtractor());
    }

    private static class SingerMapper implements RowMapper<Singer> {

        @Override
        public Singer mapRow(ResultSet resultSet, int i) throws SQLException {
            Singer singer = new Singer();
            singer.setId(resultSet.getLong("id"));
            singer.setFirstName(resultSet.getString("first_name"));
            singer.setLastName(resultSet.getString("last_name"));
            singer.setBirthDate(resultSet.getDate("birth_date"));
            return singer;
        }
    }

    private static class SingerWithAlbumExtractor implements ResultSetExtractor<List<Singer>> {

        @Override
        public List<Singer> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Map<Long, Singer> map = new HashMap<>();
            Singer singer;
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                singer = map.get(id);
                if (singer == null) {
                    singer = new Singer();
                    singer.setId(id);
                    singer.setFirstName(resultSet.getString("first_name"));
                    singer.setLastName(resultSet.getString("last_name"));
                    singer.setBirthDate(resultSet.getDate("birth_date"));
                    singer.setAlbums(new ArrayList<>());
                    map.put(id, singer);
                }
                long albumId = resultSet.getLong("album_id");
                if (albumId > 0) {
                    Album album = new Album();
                    album.setId(albumId);
                    album.setSingerId(id);
                    album.setTitle(resultSet.getString("title"));
                    album.setReleaseDate(resultSet.getDate("release_date"));
                    singer.addAlbum(album);
                }
            }
            return new ArrayList<>(map.values());
        }
    }
}
