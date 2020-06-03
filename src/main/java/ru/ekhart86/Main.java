package ru.ekhart86;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import ru.ekhart86.config.SpringJdbcConfig;
import ru.ekhart86.dao.JdbcSingerDao;
import ru.ekhart86.entities.Singer;

import java.sql.Date;
import java.util.List;

@ComponentScan
public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringJdbcConfig.class);
        JdbcSingerDao jdbcTemplateDao = context.getBean("jdbcSingerDaoMySQL", JdbcSingerDao.class);
        Long singerNameById = 3L;
        System.out.println("Имя исполнителя с ID = " + singerNameById + " " + jdbcTemplateDao.findFirstNameById(singerNameById));
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Фамилия исполнителя с ID = " + singerNameById + " " + jdbcTemplateDao.findLastNameById(singerNameById));
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Полное имя исполнителя с ID = " + singerNameById + " " + jdbcTemplateDao.findFullNameById(singerNameById));
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Получение всех данных исполнителя по ID:");
        Long singerById = 2L;
        Singer singer = jdbcTemplateDao.findSingerById(singerById);
        System.out.println("Исполнитель с ID = " + singerById + " " + singer.toString());
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Получение всех исполнителей из таблицы:");
        List<Singer> allSingers = jdbcTemplateDao.findAll();
        allSingers.forEach(System.out::println);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Получение всех исполнителей из таблицы с определённым именем:");
        String name = "Eric";
        List<Singer> allSingersByFirstName = jdbcTemplateDao.findByFirstName(name);
        System.out.println("Исполнитель с именем " + name + " имеет фамилию - " + allSingersByFirstName.get(0).getLastName());
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Добавляем нового исполнителя David Pattinson 1986-06-5:");
        Singer newSinger = new Singer();
        newSinger.setFirstName("David");
        newSinger.setLastName("Pattinson");
        newSinger.setBirthDate(Date.valueOf("1986-06-5"));
        jdbcTemplateDao.insert(newSinger);
        System.out.println("Проверяем что исполнитель добавился:");
        allSingers = jdbcTemplateDao.findAll();
        allSingers.forEach(System.out::println);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Обновляем данные исполнителя David Pattinson: ");
        allSingersByFirstName = jdbcTemplateDao.findByFirstName("David");
        Singer david = allSingersByFirstName.get(0);
        david.setFirstName("Updated David");
        david.setLastName("Updated Pattinson");
        jdbcTemplateDao.update(david);
        System.out.println("Проверяем что исполнитель обновился:");
        allSingers = jdbcTemplateDao.findAll();
        allSingers.forEach(System.out::println);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Удаляем обновлённого исполнителя David Pattinson: ");
        jdbcTemplateDao.delete(david.getId());
        System.out.println("Проверяем что исполнитель удалился:");
        allSingers = jdbcTemplateDao.findAll();
        allSingers.forEach(System.out::println);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Получаем список исполнителей имеющих альбомы: ");
        List<Singer> singersWithAlbums = jdbcTemplateDao.findAllWithAlbums();
        System.out.println("Проверяем названия альбомов у полученных исполнителей: ");
        for (Singer singerWithAlbum : singersWithAlbums) {
            System.out.println("===========================================================");
            System.out.println("Исполнитель: " + singerWithAlbum.getFirstName() + " " + singerWithAlbum.getLastName());
            System.out.println();
            System.out.println("Альбомы: ");
            System.out.println();
            singerWithAlbum.getAlbums().forEach(e -> System.out.println(e.getTitle()));
        }
    }
}
